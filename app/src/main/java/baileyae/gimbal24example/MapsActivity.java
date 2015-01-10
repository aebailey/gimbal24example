package baileyae.gimbal24example;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.FragmentManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;


import com.gimbal.android.Gimbal;
import com.gimbal.android.PlaceEventListener;
import com.gimbal.android.PlaceManager;
import com.gimbal.logging.GimbalLogConfig;
import com.gimbal.logging.GimbalLogLevel;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;


public class MapsActivity extends ActionBarActivity
        implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {

    /*
 * Define a request code to send to Google Play services
 * This code is returned in Activity.onActivityResult
 */
    private final static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;
    private GoogleMap mMap; // Might be null if Google Play services APK is not available.
    private GoogleApiClient mGoogleApiClient;
    private Location mLastLocation;
    private LatLng mylatlng;
    private static final String TAG = "Maps Activity";

    private Boolean mRequestingLocationUpdates;
    private GimbalEventReceiver gimbalEventReceiver;
    private GimbalEventListAdapter adapter;
    public static android.support.v4.app.FragmentManager fragmentManager;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(TAG, "On Create Started");
        setContentView(R.layout.activity_maps);
        fragmentManager = getSupportFragmentManager();
        GimbalLogConfig.setLogLevel(GimbalLogLevel.DEBUG);
        GimbalLogConfig.enableFileLogging(this);
        mRequestingLocationUpdates = Boolean.TRUE;
        updateValuesFromBundle(savedInstanceState);
        Intent intent = new Intent(this, GimbalAppService.class);
        Log.i(TAG, "Intent Created");
        startService(intent);
        Log.i(TAG, "Service Started");

        adapter = new GimbalEventListAdapter(this);

        ListView listView = (ListView) findViewById(R.id.listview);
        listView.setAdapter(adapter);

        GoogleMapActivity.setUpMapIfNeeded();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();

    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.i(TAG, "OnStart");
        //if (!mResolvingError) {
            mGoogleApiClient.connect();
        //}
        gimbalEventReceiver = new GimbalEventReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(GimbalDAO.GIMBAL_NEW_EVENT_ACTION);
        registerReceiver(gimbalEventReceiver, intentFilter);

        Log.i(TAG, "OnStart Commands Registered");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.i(TAG, "On Stop");
        mGoogleApiClient.disconnect();
        unregisterReceiver(gimbalEventReceiver);

    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.i(TAG, "On Pause");
        stopLocationUpdates();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i(TAG, "On Resume");
        // Make sure that GPS is enabled on the device
        LocationManager mlocManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        boolean enabled = mlocManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

        if(!enabled) {
            showDialogGPS();
        }
        if (mGoogleApiClient.isConnected() && !mRequestingLocationUpdates) {
            Log.i(TAG, "Starting Location Updates");
            startLocationUpdates();
        }
        adapter.setEvents(GimbalDAO.getEvents(getApplicationContext()));
    }




    /** Adding for Google Play Services Check
     *
     *
     */
    // Define a DialogFragment that displays the error dialog
    public static class ErrorDialogFragment extends DialogFragment {
        // Global field to contain the error dialog
        private Dialog mDialog;

        // Default constructor. Sets the dialog field to null
        public ErrorDialogFragment() {
            super();
            mDialog = null;
        }

        // Set the dialog to display
        public void setDialog(Dialog dialog) {
            mDialog = dialog;
        }

        // Return a Dialog to the DialogFragment.
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            return mDialog;
        }

    }
    /*
     * Handle results returned to the FragmentActivity
     * by Google Play services
     */
    @Override
    protected void onActivityResult(
            int requestCode, int resultCode, Intent data) {
        // Decide what to do based on the original request code
        switch (requestCode) {

            case CONNECTION_FAILURE_RESOLUTION_REQUEST :
            /*
             * If the result code is Activity.RESULT_OK, try
             * to connect again
             */
                switch (resultCode) {

                    case Activity.RESULT_OK :
                    /*
                     * Try the request again
                     */

                        break;
                }

        }

    }

    private boolean servicesConnected() {
        // Check that Google Play services is available
        int resultCode =  GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        // If Google Play services is available
        if (ConnectionResult.SUCCESS == resultCode) {
            // In debug mode, log the status
            Log.d("Geofence Detection",
                    "Google Play services is available.");
            // Continue
            return true;
            // Google Play services was not available for some reason
        } else {

            // Get the error dialog from Google Play services
            Dialog errorDialog = GooglePlayServicesUtil.getErrorDialog(
                    resultCode,
                    this,
                    CONNECTION_FAILURE_RESOLUTION_REQUEST);

            // If Google Play services can provide an error dialog
            if (errorDialog != null) {
                // Create a new DialogFragment for the error dialog
                ErrorDialogFragment errorFragment =
                        new ErrorDialogFragment();
                // Set the dialog in the DialogFragment
                errorFragment.setDialog(errorDialog);
                // Show the error dialog in the DialogFragment
                errorFragment.show(getSupportFragmentManager(),"Geofence Detection");
            }
            return false;
        }

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }
    @Override
    public void onConnectionSuspended(int i) {

    }






    /**
     * Adding code for each button
     */
    public void sendNormal(View view) {
        // Do something in response to button
        mMap.setMapType((GoogleMap.MAP_TYPE_NORMAL));
    }

    public void sendSatellite(View view) {
        // Do something in response to button
        mMap.setMapType((GoogleMap.MAP_TYPE_SATELLITE));
    }

    public void getLocation(View view) {
        // Do something in response to button
        startLocationUpdates();
        if(mLastLocation != null){
        Intent intent = new Intent(this, DisplayLocationActivity.class);

        intent.putExtra("LOCATION_DATA", mLastLocation);
        startActivity(intent);}else{
            Toast toast = Toast.makeText(this,"Location not found" ,Toast.LENGTH_SHORT);
            Log.w(TAG, "Location not found");
        }
    }

    //Once connected to Play APi, get location
    @Override
    public void onConnected(Bundle connectionHint) {
        Context context = getApplicationContext();
        // Make sure that GPS is enabled on the device
        LocationManager mlocManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        boolean enabled = mlocManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        Log.i(TAG, "Checked GPS");
        if(!enabled) {
            showDialogGPS();
        }

        /**
         * Show a dialog to the user requesting that GPS be enabled
         */


        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                mGoogleApiClient);
        //Toast toast = Toast.makeText(context,"LOCATION IS" + mLastLocation ,Toast.LENGTH_SHORT);
        if (mLastLocation != null) {

            mylatlng = new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude());
            //mMap.addMarker(new MarkerOptions().position(mylatlng).title("Marker"));
            updateUI();
        }
        //toast.show();
        if (mRequestingLocationUpdates) {
            startLocationUpdates();
        }
        if (GimbalDAO.showOptIn(getApplicationContext())) {
            startActivity(new Intent(this, OptInActivity.class));
        }
    }

    private void showDialogGPS() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(false);
        builder.setTitle("Enable GPS");
        builder.setMessage("Please enable GPS");
        builder.setInverseBackgroundForced(true);
        builder.setPositiveButton("Enable", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                startActivity(
                        new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
            }
        });
        builder.setNegativeButton("Ignore", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        AlertDialog alert = builder.create();
        alert.show();
    }
    //Update location settings - 5 seconds
    public LocationRequest mLocationRequest = new LocationRequest();
    protected void createLocationRequest() {

        mLocationRequest.setInterval(10000);
        mLocationRequest.setFastestInterval(5000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }
    protected void startLocationUpdates() {
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest,  this);
    }



    @Override
    public void onLocationChanged(Location location) {

            mylatlng = new LatLng(location.getLatitude(),location.getLongitude());
            mLastLocation = location;
            //mLastUpdateTime = DateFormat.getTimeInstance().format(new Date());
            updateUI();
        }
    private void updateUI() {
        mylatlng = new LatLng(mLastLocation.getLatitude(),mLastLocation.getLongitude());
        Log.i(TAG, "Update Ui Location: "+mylatlng.toString());
        GoogleMapActivity.mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mylatlng, 13));
    }

    protected void stopLocationUpdates() {
        LocationServices.FusedLocationApi.removeLocationUpdates(
                mGoogleApiClient, this);
    }

    public void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putBoolean("REQUESTING_LOCATION_UPDATES_KEY",
                mRequestingLocationUpdates);
        savedInstanceState.putParcelable("LOCATION_KEY", mLastLocation);

        super.onSaveInstanceState(savedInstanceState);
    }

    private void updateValuesFromBundle(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            // Update the value of mRequestingLocationUpdates from the Bundle, and
            // make sure that the Start Updates and Stop Updates buttons are
            // correctly enabled or disabled.
            if (savedInstanceState.keySet().contains("REQUESTING_LOCATION_UPDATES_KEY")) {
                mRequestingLocationUpdates = savedInstanceState.getBoolean(
                        "REQUESTING_LOCATION_UPDATES_KEY");
               // setButtonsEnabledState();
            }

            // Update the value of mCurrentLocation from the Bundle and update the
            // UI to show the correct latitude and longitude.
            if (savedInstanceState.keySet().contains("LOCATION_KEY")) {
                // Since LOCATION_KEY was found in the Bundle, we can be sure that
                // mCurrentLocationis not null.
                mLastLocation = savedInstanceState.getParcelable("LOCATION_KEY");
                Log.i(TAG, "Last Location: "+mLastLocation.getLatitude()+" , " + mLastLocation.getLongitude());
            }

            //updateUI();
        }
    }

    // --------------------
    // EVENT RECEIVER
    // --------------------

    class GimbalEventReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            adapter.setEvents(GimbalDAO.getEvents(getApplicationContext()));
            Log.i(TAG, "Gimbal Adapter event Set");
        }
    }

    private void registerForPush(String gcmSenderId)
    {
        if (gcmSenderId != null) {
            Gimbal.registerForPush(gcmSenderId);
        }
    }
    // --------------------
    // SETTINGS MENU
    // --------------------



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_display_location, menu);
        super.onCreateOptionsMenu(menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            startActivity(new Intent(this, SettingsActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
