package baileyae.gimbal24example;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.FragmentManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentSender;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.ActionBarActivity;
import android.transition.Scene;
import android.transition.Transition;
import android.transition.TransitionInflater;
import android.transition.TransitionManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.gimbal.android.Gimbal;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;


public class MapsActivity extends ActionBarActivity
        implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {


    public GoogleApiClient mGoogleApiClient;
    private Location mLastLocation;
    private LatLng mylatlng;
    private static final String TAG = "Maps Activity";

    private Boolean mRequestingLocationUpdates;
    private GimbalEventReceiver gimbalEventReceiver;
    private GimbalEventListAdapter adapter;
    public static  FragmentManager fragmentManager;

    // Request code to use when launching the resolution activity
    private static final int REQUEST_RESOLVE_ERROR = 1001;
    // Unique tag for the error dialog fragment
    private static final String DIALOG_ERROR = "dialog_error";
    // Bool to track whether the app is already resolving an error
    private boolean mResolvingError = false;
    private static final String STATE_RESOLVING_ERROR = "resolving_error";
    private ViewGroup container;
    private Transition transitionMgr;
    private Scene scene1;
    private Scene scene2;
    private ListView listView;
    private Context main_context;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        main_context = this;
        //Log.i(TAG, "On Create Started");

        setContentView(R.layout.activity_maps);

        container =(ViewGroup) findViewById(R.id.main_container);





        mRequestingLocationUpdates = Boolean.TRUE;
        updateValuesFromBundle(savedInstanceState);
        Intent intent = new Intent(this, GimbalAppService.class);
        //Log.i(TAG, "Intent Created");
        startService(intent);
        //Log.i(TAG, "Service Started");
        fragmentManager = getFragmentManager();
        adapter = new GimbalEventListAdapter(this);
        listView = (ListView) findViewById(R.id.listview);
        createListView(listView);




        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();

    }

    public void goToNextScene(View view){
        Scene tmp = scene2;
        scene2 = scene1;
        scene1 = tmp;
        TransitionManager.go(scene1);
    }

    public void createListView(final ListView clistView){

        clistView.setAdapter(adapter);
        clistView.setOnItemClickListener(new android.widget.AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,int position, long id) {
                Intent intent = new Intent(main_context,event_details.class);

                intent.putExtra("item_clicked", position);
                startActivity(intent);
            }
        });
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_RESOLVE_ERROR) {
            mResolvingError = false;
            if (resultCode == RESULT_OK) {
                // Make sure the app is not already connected or attempting to connect
                if (!mGoogleApiClient.isConnecting() &&
                        !mGoogleApiClient.isConnected()) {
                    mGoogleApiClient.connect();
                }
            }
        }
    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {
        if (mResolvingError) {
            // Already attempting to resolve an error.
            return;
        } else if (result.hasResolution()) {
            try {
                mResolvingError = true;
                result.startResolutionForResult(this, REQUEST_RESOLVE_ERROR);
            } catch (IntentSender.SendIntentException e) {
                // There was an error with the resolution intent. Try again.
                mGoogleApiClient.connect();
            }
        } else {
            // Show dialog using GooglePlayServicesUtil.getErrorDialog()
            showErrorDialog(result.getErrorCode());
            mResolvingError = true;
        }

    }

    private void showErrorDialog(int errorCode) {
        // Create a fragment for the error dialog
        ErrorDialogFragment dialogFragment = new ErrorDialogFragment();
        // Pass the error that should be displayed
        Bundle args = new Bundle();
        args.putInt(DIALOG_ERROR, errorCode);
        dialogFragment.setArguments(args);
        dialogFragment.show(getSupportFragmentManager(), "errordialog");
    }

    /* Called from ErrorDialogFragment when the dialog is dismissed. */
    public void onDialogDismissed() {
        mResolvingError = false;
    }


    @Override
    public void onConnectionSuspended(int i) {

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
        createLocationRequest();
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
    }



    @Override
    public void onLocationChanged(Location location) {

            mylatlng = new LatLng(location.getLatitude(),location.getLongitude());
            mLastLocation = location;
            MyApp appState = ((MyApp)getApplicationContext());
            appState.setState(mLastLocation);
            //mLastUpdateTime = DateFormat.getTimeInstance().format(new Date());
            updateUI();
        }
    private void updateUI() {
        mylatlng = new LatLng(mLastLocation.getLatitude(),mLastLocation.getLongitude());
        Log.i(TAG, "Update Ui Location: "+mylatlng.toString());
        //GoogleMapFragment.mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mylatlng, 13));
    }

    protected void stopLocationUpdates() {
        LocationServices.FusedLocationApi.removeLocationUpdates(
                mGoogleApiClient, this);
    }

    public void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putBoolean("REQUESTING_LOCATION_UPDATES_KEY",
                mRequestingLocationUpdates);
        savedInstanceState.putParcelable("LOCATION_KEY", mLastLocation);
        savedInstanceState.putBoolean(STATE_RESOLVING_ERROR, mResolvingError);
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
        //int id = item.getItemId();
        switch (item.getItemId()) {
            case R.id.get_location:
                startLocationUpdates();
                if(mLastLocation != null){
                    Intent intent = new Intent(this, DisplayLocationActivity.class);

                    intent.putExtra("LOCATION_DATA", mLastLocation);
                    startActivity(intent);
                }else {
                    Toast toast = Toast.makeText(this, "Location not found", Toast.LENGTH_SHORT);
                    Log.w(TAG, "Location not found");
                    toast.show();
                }
                return true;
            case R.id.action_settings:
                startActivity(new Intent(this, SettingsActivity.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);

        }
    }


}
