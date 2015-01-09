package baileyae.gimbal24example;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.AsyncTask;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.List;
import java.util.Locale;


public class DisplayLocationActivity extends FragmentActivity {
    private TextView mAddress;
    public ProgressDialog pd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.display_location);
        mAddress = (TextView) findViewById(R.id.address);
        TextView mLatLong = (TextView) findViewById(R.id.latlong);


        pd = ProgressDialog.show(this, "Working..", "Getting Location", true,
                false);
        Location mylocation = getIntent().getParcelableExtra("LOCATION_DATA");
        mLatLong.setText(mylocation.getLatitude() + " , " + mylocation.getLongitude());
        //Toast toast = Toast.makeText(this,"LOCATION IS" + mylocation ,Toast.LENGTH_LONG);
        if(mylocation != null) {
            (new GetAddressTask(this)).execute(mylocation);
        }else {
            pd.dismiss();
            Toast toast = Toast.makeText(this,"LOCATION IS NOT FOUND",Toast.LENGTH_LONG);
            toast.show();
        }

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_display_location, menu);
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


    public class GetAddressTask extends
            AsyncTask<Location, Void, String> {
        Context mContext;
        public GetAddressTask(Context context) {
            super();
            mContext = context;
        }

        /**
         * Get a Geocoder instance, get the latitude and longitude
         * look up the address, and return it
         *
         * @params params One or more Location objects
         * @return A string containing the address of the current
         * location, or an empty string if no address can be found,
         * or an error message
         */
        @Override
        protected String doInBackground(Location... params) {
            Geocoder geocoder =
                    new Geocoder(mContext, Locale.getDefault());
            // Get the current location from the input parameter list
            Location loc = params[0];
            // Create a list to contain the result address
            List<Address> addresses = null;
            try {
                /*
                 * Return 1 address.
                 */
                addresses = geocoder.getFromLocation(loc.getLatitude(),
                        loc.getLongitude(), 1);
            } catch (IOException e1) {
                Log.e("LocationSampleActivity",
                        "IO Exception in getFromLocation()");
                e1.printStackTrace();
                return ("IO Exception trying to get address");
            } catch (IllegalArgumentException e2) {
                // Error message to post in the log
                String errorString = "Illegal arguments " +
                        Double.toString(loc.getLatitude()) +
                        " , " +
                        Double.toString(loc.getLongitude()) +
                        " passed to address service";
                Log.e("LocationSampleActivity", errorString);
                e2.printStackTrace();
                return errorString;
            }
            // If the reverse geocode returned an address
            if (addresses != null && addresses.size() > 0) {
                // Get the first address
                Address address = addresses.get(0);
                /*
                 * Format the first line of address (if available),
                 * city, and country name.
                 */
                String addressText;
                if (address.getMaxAddressLineIndex() > 0) {
                    addressText = "";
                    for (int i = 0; i < address.getMaxAddressLineIndex()+1; i++) {
                        addressText = addressText + address.getAddressLine(i) + System.getProperty("line.separator");
                    }
                    return addressText;
                } else {
                    return "No address found";
                }
            }else {
                return "No address found";
            }
        }
        /**
         * A method that's called once doInBackground() completes. Turn
         * off the indeterminate activity indicator and set
         * the text of the UI element that shows the address. If the
         * lookup failed, display the error message.
         */
        @Override
        protected void onPostExecute(String address) {
            // Set activity indicator visibility to "gone"
            pd.dismiss();
            // Display the results of the lookup.
            mAddress.setText(address);
        }
    }

 }

