package baileyae.gimbal24example;

import android.app.FragmentTransaction;
import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
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
        // Passing harcoded values for latitude & longitude. Please change as per your need. This is just used to drop a Marker on the Map
        //latitude = 26.78;
        //longitude = 72.56;

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
        transaction.add(R.id.map_holder, mapFragment,"gmap").commit();
        mapFragment.getMapAsync(this);

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

    @Override
    public void onMapReady(GoogleMap mymMap) {
        mMap = mymMap;
        mMap.setMyLocationEnabled(true);
        //mylatlng = new LatLng(mLastLocation.getLatitude(),mLastLocation.getLongitude());
        //mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(sydney, 13));
    }
}
