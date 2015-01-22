package tesco;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import baileyae.gimbal24example.R;

/**
 * A fragment representing a single Offer detail screen.
 * This fragment is either contained in a {@link OfferListActivity}
 * in two-pane mode (on tablets) or a {@link OfferDetailActivity}
 * on handsets.
 */
public class OfferDetailFragment extends Fragment {
    /**
     * The fragment argument representing the item ID that this fragment
     * represents.
     */
    public static final String ARG_ITEM_ID = "item_id";

    /**
     * The dummy content this fragment is presenting.
     */
    private ProductEvent myEvent;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public OfferDetailFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments().containsKey("event")) {
            myEvent = getArguments().getParcelable("event");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_offer_detail, container, false);

        // Show the dummy content as text in a TextView.
        if (myEvent != null) {
            ((TextView) rootView.findViewById(R.id.offer_detail)).setText(myEvent.getprodName());
            ((TextView) rootView.findViewById(R.id.offer_detail1)).setText(myEvent.getOfferPromotion());
        }

        return rootView;
    }
}
