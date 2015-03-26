package android.iiitb.org.usermod;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.List;

public class MainUserActivity extends ActionBarActivity
        implements NavigationDrawerFragmentUser.NavigationDrawerCallbacks {

    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */
    private NavigationDrawerFragmentUser mNavigationDrawerFragment;

    /**
     * Used to store the last screen title. For use in {@link #restoreActionBar()}.
     */
    private CharSequence mTitle;

    private GoogleMap mMap; // Might be null if Google Play services APK is not available.
    private DrawerLayout drawerLayout;
    //static final LatLng iiitb = new LatLng(12.844846, 77.663231);
    //
    //GoogleApiClient mGoogleApiClient;
    public static final String TAG = MainUserActivity.class.getSimpleName();
    private final static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;
    private LocationRequest mLocationRequest;
    private MarkerOptions options;
    Marker marker= null;
    //private static final String TAG = "BroadcastTest";
    private Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Parse.enableLocalDatastore(this);
        Parse.initialize(this, "XdzcMtL72Ho3GmVBbCaEY7pzdg8cGXF1EkyTbdUw", "mpsFnnEuQURv0KzH4dPy0xtV8vN8gZdRTSzCDoix");

        setContentView(R.layout.activity_main_user);
        setUpMapIfNeeded();

        mNavigationDrawerFragment = (NavigationDrawerFragmentUser)
                getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);
        mTitle = getTitle();

        // Set up the drawer.
        mNavigationDrawerFragment.setUp(
                R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout));

        intent = new Intent(this, DriverLocation.class);
    }

    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        //setUpMap();
        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.mapuser)).getMap();
            // Check if we were successful in obtaining the map.
            if (mMap != null) {
                setUpMap();
            }
        }
    }

    private void setUpMap() {

        Log.d("MyApp", "In setUpMap");


        //get the current location from cloud here .
        ParseQuery<ParseObject> query = ParseQuery.getQuery("vehiclelocation");
        List<ParseObject> vehicleloc;
        ParseObject loc = new ParseObject("vehiclelocation");
        double latitude,longitude;
        query.whereEqualTo("vehicleid",1);
        try {
            vehicleloc = query.find();
            loc = vehicleloc.get(0);
            latitude= loc.getDouble("latitude");
            longitude = loc.getDouble("longitude");
            LatLng latLng = new LatLng(latitude, longitude);
            marker = mMap.addMarker(new MarkerOptions().position(latLng).title("MyLocation"));
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
            mMap.animateCamera(CameraUpdateFactory.zoomTo(11), 2000, null);


        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        startService(intent);
        registerReceiver(broadcastreceiver, new IntentFilter(DriverLocation.BROADCAST_ACTION));
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(broadcastreceiver);
        stopService(intent);
    }

    private BroadcastReceiver broadcastreceiver  = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            Bundle loc = intent.getBundleExtra("LOC");
            double latitude = loc.getDouble("LATITUDE");
            double longitude = loc.getDouble("LONGITUDE");
            Log.d("Myapp",String.valueOf(latitude));
            Toast.makeText(MainUserActivity.this,
                    "Triggered by Service!\n"
                            + "Data passed: Latitude : " + String.valueOf(latitude) + " Longitude : " + String.valueOf(longitude),
                    Toast.LENGTH_LONG).show();

            handleNewLocation(latitude,longitude);
        }
    };

    private void handleNewLocation(double currentLatitude,double currentLongitude) {


        //storeLocationInCloud(currentLatitude,currentLongitude);

        LatLng latLng = new LatLng(currentLatitude, currentLongitude);


        options = new MarkerOptions().position(latLng).title("I am here!");
        if(marker!=null){
            marker.remove();
        }
        marker = mMap.addMarker(options);
    }

    @Override
    public void onNavigationDrawerItemSelected(int position) {
        // update the main content by replacing fragments
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.mapuser, PlaceholderFragment.newInstance(position + 1))
                .commit();
    }

    public void onSectionAttached(int number) {
        switch (number) {
            case 1:
                mTitle = getString(R.string.title_section1);
                break;
            case 2:
                mTitle = getString(R.string.title_section2);
                break;
            case 3:
                mTitle = getString(R.string.title_section3);
                break;
            case 4:
                mTitle = getString(R.string.title_section4);
                break;
            case 5:
                mTitle = getString(R.string.title_section5);
                break;
        }
    }

    public void restoreActionBar() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(mTitle);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!mNavigationDrawerFragment.isDrawerOpen()) {
            // Only show items in the action bar relevant to this screen
            // if the drawer is not showing. Otherwise, let the drawer
            // decide what to show in the action bar.
            getMenuInflater().inflate(R.menu.main_user, menu);
            restoreActionBar();
            return true;
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_main_user, container, false);
            return rootView;
        }

        @Override
        public void onAttach(Activity activity) {
            super.onAttach(activity);
            ((MainUserActivity) activity).onSectionAttached(
                    getArguments().getInt(ARG_SECTION_NUMBER));
        }
    }

}
