package johnson.dillan.brewme;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.icu.text.DecimalFormat;
import android.icu.text.MeasureFormat;
import android.icu.util.Measure;
import android.icu.util.MeasureUnit;
import android.icu.util.ULocale;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class BreweryListFragment extends Fragment implements AdapterView.OnItemSelectedListener {

    private static final String TAG = "BreweryListFragment";
    private static final String[] LOCATION_PERMISSIONS = new String[]{
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
    };
    private static final int REQUEST_LOCATION_PERMISSIONS = 0;

    private RecyclerView mBreweryRecyclerView;
    private BreweryAdapter mAdapter;
    private List<BreweryItem> mBreweries = new ArrayList<>();
    private GoogleApiClient mClient;

    private double mUserLongitude = 0;
    private double mUserLatitude = 0;
    private String mSelectedState = "alabama";


    public static BreweryListFragment newInstance() {
        return new BreweryListFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        setRetainInstance(true);
        mClient = new GoogleApiClient.Builder(getActivity()).addApi(LocationServices.API).addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
            @Override
            public void onConnected(Bundle bundle) {
                // not sure what to put here, or whether or not it is even needed.
                if ( hasLocationPermission() ) {
                    findLocation();
                } else {
                    requestPermissions(LOCATION_PERMISSIONS, REQUEST_LOCATION_PERMISSIONS);
                }
            }

            @Override
            public void onConnectionSuspended(int i) {

            }
        }).build();
        new FetchItemsTask().execute();
    } // end of onCreate()

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_brewery_list, container, false);

        mBreweryRecyclerView = (RecyclerView) v.findViewById(R.id.breweries_recycler_view);
        mBreweryRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        setupAdapter();
        updateUI();
        return v;
    }

    private void setupAdapter() {
        if (isAdded()) {
            mBreweryRecyclerView.setAdapter(new BreweryAdapter(mBreweries));
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater){
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_state_picker, menu);

        MenuItem item = menu.findItem(R.id.pick_state);
        Spinner spinner = (Spinner) item.getActionView();
        spinner.findViewById(R.id.state_spinner);
        spinner.setOnItemSelectedListener(this);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource( getActivity(), R.array.states_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource( android.R.layout.simple_spinner_dropdown_item );

        spinner.setAdapter(adapter);
    }

    public void onItemSelected(AdapterView<?> parent, View view,
                               int pos, long id) {
        // An item was selected. You can retrieve the selected item using
         String state = parent.getItemAtPosition(pos).toString();
         state = state.toLowerCase();
         state = state.replace(" ", "_");
         mSelectedState = state;
//         Log.d("The selected State: ", mSelectedState );
        new FetchItemsTask().execute();
    }

    public void onNothingSelected(AdapterView<?> parent) {
        // Another interface callback
    }


    private class FetchItemsTask extends AsyncTask<Void, Void, BreweryLab> {
        @Override
        protected BreweryLab doInBackground(Void... params) {
            return new BreweryFetcher().fetchItems( mSelectedState );
        } // end of doInBackground()

        @Override
        protected void onPostExecute(BreweryLab breweryLab) {
            mBreweries = breweryLab.getBreweries();
            setupAdapter();
        }

    }// end of fetchitemstask

    private class BreweryHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private TextView mNameTextView;
        private TextView mLocationTextView;
        private TextView mDistanceTextView;
        private BreweryItem mBrewery;

        public BreweryHolder(LayoutInflater inflater, ViewGroup parent) {
            super(inflater.inflate(R.layout.list_item_brewery, parent, false));
            itemView.setOnClickListener(this);
            mNameTextView = (TextView) itemView.findViewById(R.id.brewery_name);
            mLocationTextView = (TextView) itemView.findViewById(R.id.brewery_city_state);
            mDistanceTextView = (TextView) itemView.findViewById(R.id.brewery_distance);

        }

        public void bind(BreweryItem b) {
            mBrewery = b;
            mNameTextView.setText(b.getName());
            String l = b.getCity() + ", " + b.getState();
            mLocationTextView.setText(l);

            Location user = new Location("user");
            user.setLatitude(mUserLatitude);
            user.setLongitude(mUserLongitude);
            Location dest = new Location("dest");
            dest.setLatitude(b.getLatitude());
            dest.setLongitude(b.getLongitude());


            String distanceTo = b.metersToMiles( user.distanceTo(dest) );
            distanceTo += " mi";
            mDistanceTextView.setText( distanceTo );


        }

        @Override
        public void onClick(View v) {
            // implement action for clicking a brewery item.
            Intent intent = BreweryActivity.newIntent( getActivity(), mBrewery.getId() );
            startActivity( intent );
        }

    }

    private class BreweryAdapter extends RecyclerView.Adapter<BreweryHolder> {

        private List<BreweryItem> mBreweries;

        public BreweryAdapter(List<BreweryItem> breweries) {
            mBreweries = breweries;
        }

        @NonNull
        @Override
        public BreweryHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
            return new BreweryHolder(layoutInflater, parent);
        }

        @Override
        public void onBindViewHolder(@NonNull BreweryHolder breweryHolder, int position) {
            BreweryItem brewery = mBreweries.get(position);
            breweryHolder.bind(brewery);
            setFadeAnimation( breweryHolder.itemView );
        }

        @Override
        public int getItemCount() {
            return mBreweries.size();
        }

        public void setBreweries( List<BreweryItem> breweries ){
            mBreweries = breweries;
        }

    } // end of Resort Adapter Class

    private void setFadeAnimation(View view) {
        AlphaAnimation anim = new AlphaAnimation(0.0f, 1.0f);
        anim.setDuration(700);
        view.startAnimation(anim);
    }
    private boolean hasLocationPermission() {
        int result = ContextCompat.checkSelfPermission(getActivity(), LOCATION_PERMISSIONS[0]);
        return result == PackageManager.PERMISSION_GRANTED;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case REQUEST_LOCATION_PERMISSIONS:
                if (hasLocationPermission()) {
                    findLocation();
                }
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    private void findLocation() {
        LocationRequest request = LocationRequest.create();
        request.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        request.setNumUpdates(1);
        request.setInterval(0);
        if (ActivityCompat.checkSelfPermission( getActivity(), Manifest.permission.ACCESS_FINE_LOCATION ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(mClient, request, new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                mUserLongitude = location.getLongitude();
                mUserLatitude = location.getLatitude();
                Log.d("Brewery", "Got location: " + location);
            }
        });
    } // end of findLocation()

    private void updateUI(){
        BreweryLab crimeLab = BreweryLab.get();
        List<BreweryItem> breweries = crimeLab.getBreweries();

        if ( mAdapter == null ){
            mAdapter = new BreweryAdapter(breweries);
            mBreweryRecyclerView.setAdapter(new BreweryAdapter(mBreweries));
        }
        else {
            mAdapter.setBreweries( breweries );
            mAdapter.notifyDataSetChanged();
        }

    }


    @Override
    public void onStart(){
        super.onStart();
        mClient.connect();
    }

    @Override
    public void onResume(){
        super.onResume();
        updateUI();
    }

    @Override
    public void onStop() {
        super.onStop();
        mClient.disconnect();
    }


} // end of brewerylistfragment
