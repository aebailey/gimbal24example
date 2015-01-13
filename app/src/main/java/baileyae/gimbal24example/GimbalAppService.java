/**
 * Copyright (C) 2014 Gimbal, Inc. All rights reserved.
 *
 * This software is the confidential and proprietary information of Gimbal, Inc.
 *
 * The following sample code illustrates various aspects of the Gimbal SDK.
 *
 * The sample code herein is provided for your convenience, and has not been
 * tested or designed to work on any particular system configuration. It is
 * provided AS IS and your use of this sample code, whether as provided or
 * with any modification, is at your own risk. Neither Gimbal, Inc.
 * nor any affiliate takes any liability nor responsibility with respect
 * to the sample code, and disclaims all warranties, express and
 * implied, including without limitation warranties on merchantability,
 * fitness for a specified purpose, and against infringement.
 */
package baileyae.gimbal24example;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.gimbal.android.Communication;
import com.gimbal.android.CommunicationListener;
import com.gimbal.android.CommunicationManager;
import com.gimbal.android.Gimbal;
import com.gimbal.android.Place;
import com.gimbal.android.PlaceEventListener;
import com.gimbal.android.PlaceManager;
import com.gimbal.logging.GimbalLogConfig;
import com.gimbal.logging.GimbalLogLevel;

import java.util.Collection;
import java.util.Date;
import java.util.LinkedList;

import baileyae.gimbal24example.GimbalEvent.TYPE;

import static com.gimbal.android.Gimbal.registerForPush;


public class GimbalAppService extends Service {
    private static final int MAX_NUM_EVENTS = 100;
    private LinkedList<GimbalEvent> events;
    private PlaceEventListener placeEventListener;
    private CommunicationListener communicationListener;
    private static final String TAG = "GIMBAL APP SERVICE";

    @Override
    public void onCreate() {
        GimbalLogConfig.setLogLevel(GimbalLogLevel.DEBUG);
        GimbalLogConfig.enableFileLogging(this);
        events = new LinkedList<GimbalEvent>(GimbalDAO.getEvents(getApplicationContext()));
        Log.i(TAG,"App Service Created");
        Gimbal.setApiKey(this.getApplication(), "a5ba1b92-0be5-4ae9-bece-21b8b832546a");

        // Setup PlaceEventListener
        placeEventListener = new PlaceEventListener() {

            @Override
            public void onEntry(Place place, long timestamp) {
                addEvent(new GimbalEvent(TYPE.PLACE_ENTER, place.getName(), new Date(timestamp)));
                Log.i(TAG,"Place Entered");
                place.getAttributes();
                notify_place(place, TYPE.PLACE_ENTER);
            }

            @Override
            public void onExit(Place place, long entryTimestamp, long exitTimestamp) {
                addEvent(new GimbalEvent(TYPE.PLACE_EXIT, place.getName(), new Date(exitTimestamp)));
                Log.i(TAG,"Place Exited");
                notify_place(place, TYPE.PLACE_EXIT);
            }
        };

        PlaceManager.getInstance().addPlaceEventListener(placeEventListener);
        //placeManager.startMonitoring();

        //Log.i(TAG, String.valueOf(PlaceManager.isMonitoring()));



        // Setup Push Communication
        String gcmSenderId = "649583496832"; // <--- SET THIS STRING TO YOUR PUSH SENDER ID HERE (Google API project #) ##
        registerForPush(gcmSenderId);

        // Setup CommunicationListener
        communicationListener = new CommunicationListener() {
            @Override
            public void communicationsOnPlaceEntry(Collection<Communication> communications, Place place, long timestamp) {
                for (Communication comm : communications) {
                    addEvent(new GimbalEvent(TYPE.COMMUNICATION_ENTER, comm.getTitle(), new Date(timestamp)));
                }
            }

            @Override
            public void communicationsOnPlaceExit(Collection<Communication> communications, Place place, long entryTimestamp, long exitTimestamp) {
                for (Communication comm : communications) {
                    addEvent(new GimbalEvent(TYPE.COMMUNICATION_EXIT, comm.getTitle(), new Date(exitTimestamp)));
                }
            }

            @Override
            public void communicationFromPush(Communication communication) {
                Log.i(TAG,"Push communication received");
                addEvent(new GimbalEvent(TYPE.COMMUNICATION_PUSH, communication.getTitle(), new Date()));

            }
        };
        CommunicationManager.getInstance().addListener(communicationListener);
    }

    private void addEvent(GimbalEvent event) {
        while (events.size() >= MAX_NUM_EVENTS) {
            events.removeLast();
        }
        Log.i(TAG,"Event Added");
        events.add(0, event);
        GimbalDAO.setEvents(getApplicationContext(), events);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i(TAG,"On Start Command");
        super.onStartCommand(intent, flags, startId);
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        PlaceManager.getInstance().removePlaceEventListener(placeEventListener);
        CommunicationManager.getInstance().removeListener(communicationListener);
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public void notify_place (Place place, TYPE type){
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(iconRes(type))
                        .setContentTitle(place.getName())
                        .setContentText("You have triggered a place event");
// Creates an explicit intent for an Activity in your app
        Intent resultIntent = new Intent(this, MapsActivity.class);

// The stack builder object will contain an artificial back stack for the
// started Activity.
// This ensures that navigating backward from the Activity leads out of
// your application to the Home screen.
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
// Adds the back stack for the Intent (but not the Intent itself)
        stackBuilder.addParentStack(MapsActivity.class);
// Adds the Intent that starts the Activity to the top of the stack
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(
                        0,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );
        mBuilder.setContentIntent(resultPendingIntent);
        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
// mId allows you to update the notification later on.
        mNotificationManager.notify(0, mBuilder.build());
    }

    private int iconRes(GimbalEvent.TYPE type) {
        switch (type) {
            case PLACE_ENTER:
                return R.drawable.place_enter;
            case PLACE_EXIT:
                return R.drawable.place_exit;
            case COMMUNICATION_ENTER:
                return R.drawable.comm_enter;
            case COMMUNICATION_EXIT:
                return R.drawable.comm_exit;
            case COMMUNICATION_PUSH:
                return R.drawable.comm_enter;
            default:
                return R.drawable.place_enter;
        }
    }

}
