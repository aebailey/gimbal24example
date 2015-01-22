package tesco;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ListView;

import baileyae.gimbal24example.MapsActivity;
import baileyae.gimbal24example.R;


/**
 * An activity representing a list of Offers. This activity
 * has different presentations for handset and tablet-size devices. On
 * handsets, the activity presents a list of items, which when touched,
 * lead to a {@link OfferDetailActivity} representing
 * item details. On tablets, the activity presents the list of items and
 * item details side-by-side using two vertical panes.
 * <p/>
 * The activity makes heavy use of fragments. The list of items is a
 * {@link OfferListFragment} and the item details
 * (if present) is a {@link OfferDetailFragment}.
 * <p/>
 * This activity also implements the required
 * {@link OfferListFragment.Callbacks} interface
 * to listen for item selections.
 */
public class OfferListActivity extends Activity
        implements OfferListFragment.Callbacks {

    /**
     * Whether or not the activity is in two-pane mode, i.e. running on a tablet
     * device.
     */
    private boolean mTwoPane;
    private String mysearch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_offer_list);

        mysearch = getIntent().getStringExtra("SEARCH");
        ((OfferListFragment)getFragmentManager().findFragmentById(R.id.offer_list)).set_search(mysearch);

        if (findViewById(R.id.offer_detail_container) != null) {
            // The detail container view will be present only in the
            // large-screen layouts (res/values-large and
            // res/values-sw600dp). If this view is present, then the
            // activity should be in two-pane mode.
            mTwoPane = true;

            // In two-pane mode, list items should be given the
            // 'activated' state when touched.
            ((OfferListFragment) getFragmentManager()
                    .findFragmentById(R.id.offer_list))
                    .setActivateOnItemClick(true);
        }

        // TODO: If exposing deep links into your app, handle intents here.
    }

    /**
     * Callback method from {@link OfferListFragment.Callbacks}
     * indicating that the item with the given ID was selected.
     */
    @Override
    public void onItemSelected(ProductEvent event) {
        if (mTwoPane) {
            // In two-pane mode, show the detail view in this activity by
            // adding or replacing the detail fragment using a
            // fragment transaction.
            Bundle arguments = new Bundle();
            arguments.putParcelable("event", event);

            OfferDetailFragment fragment = new OfferDetailFragment();
            fragment.setArguments(arguments);
            getFragmentManager().beginTransaction()
                    .replace(R.id.offer_detail_container, fragment)
                    .commit();

        } else {
            // In single-pane mode, simply start the detail activity
            // for the selected item ID.
            Intent detailIntent = new Intent(this, OfferDetailActivity.class);
            detailIntent.putExtra("event", event);
            startActivity(detailIntent);
        }
    }
}
