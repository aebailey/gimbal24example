package tesco;

import android.app.Activity;
import android.app.ListFragment;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;

import baileyae.gimbal24example.R;

/**
 * A list fragment representing a list of Offers. This fragment
 * also supports tablet devices by allowing list items to be given an
 * 'activated' state upon selection. This helps indicate which item is
 * currently being viewed in a {@link OfferDetailFragment}.
 * <p/>
 * Activities containing this fragment MUST implement the {@link Callbacks}
 * interface.
 */
public class OfferListFragment extends ListFragment {

    /**
     * The serialization (saved instance state) Bundle key representing the
     * activated item position. Only used on tablets.
     */
    private static final String STATE_ACTIVATED_POSITION = "activated_position";

    private static String url1 = "https://secure.techfortesco.com/tescolabsapi/restservice.aspx?command=LOGIN&email=&password=&developerkey=5rW1lSTDRR4qSFkK2IYi&applicationkey=925AFA33AF0DD2EA62DB";
    private static String url2a = "https://secure.techfortesco.com/tescolabsapi/restservice.aspx?command=PRODUCTSEARCH&searchtext=";
    private String url2b = "&page=1&sessionkey=";
    private String url3;
    private String login;
    private ProgressDialog  pDialog;
    private ProgressDialog  pDialog2;
    String TAG_basep = "BaseProductId";
    String TAG_ean = "EANBarcode";
    String TAG_img = "ImagePath";
    String TAG_pname= "Name";
    String TAG_OffProm = "OfferPromotion";
    String TAG_OffVal = "OfferValidity";
    String basep;
    String ean;
    String img;
    String pname;
    String offprom;
    String offval;
    private String mysearch;
    private ArrayList productList;



    private Callbacks mCallbacks = sDummyCallbacks;
    private int mActivatedPosition = ListView.INVALID_POSITION;
    private ProductListAdapter adapter;
    public interface Callbacks {
        public void onItemSelected(ProductEvent event);
    }
    private static Callbacks sDummyCallbacks = new Callbacks() {
        @Override
        public void onItemSelected(ProductEvent event) {
        }
    };

    public OfferListFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        productList = new ArrayList<HashMap<String, String>>();
        new GetLogin().execute();
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Restore the previously serialized activated item position.
        if (savedInstanceState != null
                && savedInstanceState.containsKey(STATE_ACTIVATED_POSITION)) {
            setActivatedPosition(savedInstanceState.getInt(STATE_ACTIVATED_POSITION));
        }
        this.getListView().setSelector(R.drawable.bg_key);

    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        // Activities containing this fragment must implement its callbacks.
        if (!(activity instanceof Callbacks)) {
            throw new IllegalStateException("Activity must implement fragment's callbacks.");
        }

        mCallbacks = (Callbacks) activity;
    }

    @Override
    public void onDetach() {
        super.onDetach();

        // Reset the active callbacks interface to the dummy implementation.
        mCallbacks = sDummyCallbacks;
    }

    @Override
    public void onListItemClick(ListView listView, View view, int position, long id) {
        super.onListItemClick(listView, view, position, id);

        // Notify the active callbacks interface (the activity, if the
        // fragment is attached to one) that an item has been selected.
        setActivatedPosition(position);
        setSelection(position);
        view.setSelected(true);
        //mCallbacks.onItemSelected(String.valueOf(getSelectedItemId()));
        mCallbacks.onItemSelected((ProductEvent) listView.getItemAtPosition(position));


    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mActivatedPosition != ListView.INVALID_POSITION) {
            // Serialize and persist the activated item position.
            outState.putInt(STATE_ACTIVATED_POSITION, mActivatedPosition);
            //TODO:Define in Detail Activity Selected item
        }
    }

    /**
     * Turns on activate-on-click mode. When this mode is on, list items will be
     * given the 'activated' state when touched.
     */
    public void setActivateOnItemClick(boolean activateOnItemClick) {
        // When setting CHOICE_MODE_SINGLE, ListView will automatically
        // give items the 'activated' state when touched.
        getListView().setChoiceMode(activateOnItemClick
                ? ListView.CHOICE_MODE_SINGLE
                : ListView.CHOICE_MODE_NONE);
    }

    private void setActivatedPosition(int myposition) {
        if (myposition == ListView.INVALID_POSITION) {
            getListView().setItemChecked(mActivatedPosition, false);
        } else {
            getListView().setItemChecked(myposition, true);
        }

        mActivatedPosition = myposition;
    }

    public void set_search(String search){
        this.mysearch = search;
    }

    private class GetLogin extends AsyncTask<Void,Void,Void>{
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress dialog
            pDialog = new ProgressDialog(getActivity());
            pDialog.setMessage("Please wait...");
            pDialog.setCancelable(false);
            pDialog.show();

        }

        @Override
        protected Void doInBackground(Void... params) {
            // Creating service handler class instance
            ServiceHandler sh = new ServiceHandler();

            // Making a request to url and getting response
            String jsonStr = sh.makeServiceCall(url1, ServiceHandler.GET);
            Log.d("Response: ", "> " + jsonStr);

            if (jsonStr != null) {
                try {
                    JSONObject jsonObj = new JSONObject(jsonStr);

                    // Getting JSON Array node
                    login = jsonObj.getString("SessionKey");


                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                Log.e("ServiceHandler", "Couldn't get any data from the url");
            }


            return null;
        }
        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);

            //URL Encoder to encode user input
            try {
                mysearch = URLEncoder.encode(mysearch,"UTF-8");
                } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }


            url3 = url2a+ mysearch + url2b + login;
            Log.d("Response: ", "> " + login);
            //adapter.addEvent(new ProductEvent(42345678L,"EAN4", "http://images2.farmlanebooks.co.uk/2009A/07/twitter-blue.png",login));
            new GetOffers().execute();

        }
    }
    private class GetOffers extends AsyncTask<Void,Void,Void>{
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress dialog
            //pDialog2 = new ProgressDialog(getActivity());
            //pDialog2.setMessage("Please wait...");
           // pDialog2.setCancelable(false);
           // pDialog2.show();

        }

        @Override
        protected Void doInBackground(Void... params) {
            // Creating service handler class instance
            ServiceHandler sh = new ServiceHandler();

            // Making a request to url and getting response
            String jsonStr = sh.makeServiceCall(url3, ServiceHandler.GET);
            Log.d("Response: ", "> " + jsonStr);

            if (jsonStr != null) {
                try {
                    JSONObject jsonObj = new JSONObject(jsonStr);
                    // Getting JSON Array node
                    JSONArray products = jsonObj.getJSONArray("Products");

                    // looping through All Contacts
                    for (int i = 0; i < products.length(); i++) {
                    //for (int i = 0; i < 5; i++) {
                        // Getting JSON Array node
                        JSONObject p = products.getJSONObject(i);
                        basep = p.getString(TAG_basep);
                        ean = p.getString(TAG_ean);
                        img = p.getString(TAG_img);
                        pname = p.getString(TAG_pname);
                        offprom = p.getString(TAG_OffProm);
                        offval=p.getString(TAG_OffVal);

                        //HashMap<String, String> product = new HashMap<String, String>();
                        //product.put(TAG_basep, basep);
                        //product.put(TAG_ean, ean);
                        //product.put(TAG_img, img);
                        //product.put(TAG_pname, pname);
                        ProductEvent product = new ProductEvent(basep,ean, img,pname,offprom,offval);
                        productList.add(product);
                        //getActivity().runOnUiThread(new Runnable() {
                        //    public void run(){
                        //        adapter.addEvent(new ProductEvent(basep,ean, img,pname));
                        //    }
                        //});

                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                Log.e("ServiceHandler", "Couldn't get any data from the url");
            }


            return null;
        }
        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            // Dismiss the progress dialog
            if (pDialog.isShowing())
                pDialog.dismiss();
            adapter = new ProductListAdapter(getActivity());
            adapter.addAEvent(productList);


            setListAdapter(adapter);
        }
    }
}
