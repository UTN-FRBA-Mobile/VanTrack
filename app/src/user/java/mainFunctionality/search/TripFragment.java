package mainFunctionality.search;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.google.firebase.messaging.FirebaseMessaging;

import mainFunctionality.reservations.MyReservationsFragment;
import mainFunctionality.viewsModels.TripsReservationsViewModel;
import mainFunctionality.viewsModels.TripsViewModel;
import utn.proy2k18.vantrack.R;
import utn.proy2k18.vantrack.mainFunctionality.search.Trip;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link TripFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 */
public class TripFragment extends Fragment {

    private static final String ARG_PARAM1 = "tripPosition";
    private static final String ARG_PARAM2 = "returnDate";

    private int position;
    private String returnDate;

    private TripsViewModel tripsModel;
    private TripsReservationsViewModel reservationsModel;
    private Trip trip;

    private OnFragmentInteractionListener mListener;


    public TripFragment() {
        // Required empty public constructor
    }

    public static TripFragment newInstance(int tripPosition, String returnDate) {
        TripFragment tripFragment = new TripFragment();

        Bundle args = new Bundle();
        args.putInt(ARG_PARAM1, tripPosition);
        args.putString(ARG_PARAM2, returnDate);
        tripFragment.setArguments(args);

        return tripFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        tripsModel = ViewModelProviders.of(getActivity()).get(TripsViewModel.class);
        reservationsModel = ViewModelProviders.of(getActivity()).get(TripsReservationsViewModel.class);
        tripsModel.init();
        position = getArguments().getInt(ARG_PARAM1);
        returnDate = getArguments().getString(ARG_PARAM2);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_trip, container, false);

        TextView origin = view.findViewById(R.id.trip_fragment_origin);
        TextView destination = view.findViewById(R.id.trip_fragment_destination);
        TextView company = view.findViewById(R.id.trip_fragment_company);
        TextView date = view.findViewById(R.id.trip_fragment_date);
        TextView time = view.findViewById(R.id.trip_fragment_time);
        TextView price = view.findViewById(R.id.trip_price);

        final Button btnBookTrip = view.findViewById(R.id.btn_book_trip);
        final Button btnBookTripSearchReturn = view.findViewById(R.id.btn_book_trip_search_return);

        final FrameLayout trip_booking_options = view.findViewById(R.id.trip_booking_options);
        trip_booking_options.setVisibility(View.VISIBLE);

        if (returnDate.equals(getResources().getString(R.string.no_return_date))) {
            btnBookTrip.setVisibility(View.VISIBLE);
            btnBookTripSearchReturn.setVisibility(View.GONE);
        } else {
            btnBookTrip.setVisibility(View.GONE);
            btnBookTripSearchReturn.setVisibility(View.VISIBLE);
        }

        trip = tripsModel.getFilteredTripAtPosition(position);

        origin.setText(trip.getOrigin());
        destination.setText(trip.getDestination());
        company.setText(trip.getCompanyName());
        date.setText(trip.getCalendarDate());
        time.setText(trip.getStrTime());
        price.setText(String.valueOf(trip.getPrice()));

        btnBookTrip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

                if (reservationsModel.isTripBooked(trip)) {
                    builder.setMessage("Ya posee una reserva para este viaje.")
                        .setPositiveButton("Aceptar", null);

                } else {
                    builder.setMessage("Desea reservar el Viaje?")
                        .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int position1) {
                                bookTrip(trip);
                                setFragment(new MyReservationsFragment());
                            }
                        })
                        .setNegativeButton("Cancelar", null);
                }

                AlertDialog alert = builder.create();
                alert.show();
            }
        });

        btnBookTripSearchReturn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

                if (reservationsModel.isTripBooked(trip)) {
                    builder.setMessage("Ya posee una reserva para este viaje.")
                        .setPositiveButton("Aceptar", null);

                } else {
                    builder.setMessage("Desea reservar el Viaje?")
                        .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int position1) {
                                bookTrip(trip);

                                SearchResultsFragment searchResultsFragment =
                                        SearchResultsFragment.newInstance(trip.getDestination(),
                                                trip.getOrigin(), returnDate, getResources().getString(R.string.no_return_date));
                                setFragment(searchResultsFragment);
                            }
                        })
                        .setNegativeButton("Cancelar", null);
                }

                AlertDialog alert = builder.create();
                alert.show();
            }
        });

        return view;
    }

    private void bookTrip(Trip trip) {
        reservationsModel.addReservationForTrip(trip);
        subscribeToTripTopic();
    }

    private void subscribeToTripTopic() {
        // topic string should be the trip unique id declared in DB
        String topic = "trips__" + trip.get_id();
        FirebaseMessaging.getInstance().subscribeToTopic(topic);
    }

    private void setFragment(Fragment fragment) {
        FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.fragment_container, fragment);
        ft.commit();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
