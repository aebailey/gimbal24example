package baileyae.gimbal24example;

import android.app.FragmentTransaction;
import android.content.Context;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;


public class GoogleMapFragment extends Fragment implements OnMapReadyCallback{


    private static final String TAG = "Google Maps Activity";
    private static View view;
    public GoogleMap mMap;


    private static Double latitude, longitude;
    MapFragment mapFragment;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.googlemapfragmentlayout, container, false);


        setUpMapIfNeeded(); // For setting up the MapFragment

        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        setUpMapIfNeeded();
    }

    @Override
    public void onResume() {
        super.onResume();
        setUpMapIfNeeded();

    }

    public void onPause() {
        super.onPause();

        if (mapFragment != null) {
            FragmentTransaction fragmentTransaction = getChildFragmentManager().beginTransaction();
            fragmentTransaction.remove(mapFragment);
            fragmentTransaction.commit();
        }
    }




    public void setUpMapIfNeeded( ) {
        // Do a null check to confirm that we have not already instantiated the map.
        MapFragment mapFragment = new MapFragment();
        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        if(getChildFragmentManager().findFragmentByTag("gmap")==null){
            transaction.add(R.id.map_holder, mapFragment,"gmap").commit();
        }else{
            transaction.replace(R.id.map_holder, mapFragment,"gmap").commit();
        }
        mapFragment.getMapAsync(this);

    }


    //protected void startLocationUpdates() {
    //    LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest,  this);
    //}

    @Override
    public void onMapReady(GoogleMap mymMap) {
        mMap = mymMap;
        mMap.setMyLocationEnabled(true);
        MyApp appState = ((MyApp)getActivity().getApplicationContext());
        Location mLastLocation = appState.getmLastLocation();
        LatLng mylatlng;
        SharedPreferences sharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);
        if (mLastLocation == null) {
            double latitude = Double.longBitsToDouble(sharedPref.getLong("Latitude", 0));
            double longitude = Double.longBitsToDouble(sharedPref.getLong("Longitude", 0));
            mylatlng = new LatLng(latitude,longitude);
        }else{
            mylatlng = new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude());
        }
        float zoom = sharedPref.getFloat("zoom",13);

        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mylatlng, zoom));
    }

    public void onDestroy(){
        super.onDestroy();
        CameraPosition mMyCam = mMap.getCameraPosition();
        double longitude = mMyCam.target.longitude;
        double latitude = mMyCam.target.latitude;
        float zoom = mMyCam.zoom;
        SharedPreferences settings = getActivity().getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();
        editor.putLong("Latitude", Double.doubleToLongBits(latitude));
        editor.putLong("Longitude", Double.doubleToLongBits(longitude));
        editor.putFloat("zoom", zoom);
        editor.commit();

    }
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater menuInflater)  {

        menuInflater.inflate(R.menu.menu_display_maps, menu);
        super.onCreateOptionsMenu(menu, menuInflater);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //int id = item.getItemId();
        switch (item.getItemId()) {

            case R.id.map_normal:
                mMap.setMapType((GoogleMap.MAP_TYPE_NORMAL));
                return true;
            case R.id.map_satellite:
                mMap.setMapType((GoogleMap.MAP_TYPE_SATELLITE));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

}
