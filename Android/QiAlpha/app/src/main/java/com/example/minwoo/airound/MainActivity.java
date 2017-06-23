package com.example.minwoo.airound;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.BounceInterpolator;
import android.view.animation.Interpolator;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, OnMapReadyCallback,
        GoogleMap.OnMarkerClickListener {

    private GoogleMap mMap;

    private Marker mBrisbane;

    /**
     * Keeps track of the last selected marker (though it may no longer be selected).  This is
     * useful for refreshing the info window.
     */
    private Marker mLastSelectedMarker;

    private final List<Marker> mMarkerRainbow = new ArrayList<Marker>();
    private Location location;

    private boolean mShowPermissionDeniedDialog = false;

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;

    private UiSettings mUiSettings;

    private static final int MY_LOCATION_PERMISSION_REQUEST_CODE = 1;

    private static final int LOCATION_LAYER_PERMISSION_REQUEST_CODE = 2;

    private boolean mLocationPermissionDenied = false;

    //sample data
    //sample data + our database data
    private static final LatLng streetPark29 = new LatLng(32.713568,-117.158771);
    private static final LatLng streetPark19 = new LatLng(32.712836,-117.157357);
    private static final LatLng streetPark31 = new LatLng(32.713414,-117.158403);
    private static final LatLng streetPark3 = new LatLng(32.714932,-117.156462);
    private static final LatLng streetPark17 = new LatLng(32.712646,-117.157671);
    private static final LatLng streetPark27 = new LatLng(32.713576,-117.158046);
    private static final LatLng streetPark24 = new LatLng(32.713577,-117.157682);
    private static final LatLng streetPark2 = new LatLng(32.713699,-117.156724);
    private static final LatLng streetPark12 = new LatLng(32.713698,-117.157079);
    private static final LatLng streetPark23 = new LatLng(32.713434,-117.157503);
    private static final LatLng streetPark10 = new LatLng(32.713391,-117.159317);
    private static final LatLng streetPark16 = new LatLng(32.712525,-117.159008);
    private static final LatLng streetPark28 = new LatLng(32.713566,-117.159029);
    private static final LatLng streetPark15 = new LatLng(32.712524,-117.158753);
    private static final LatLng QUALCOMM_INSTITUTE = new LatLng(32.724780, -117.163575);

    //checkbox setting
    private boolean checkMylocation = true;
    private boolean checkBuilding = true;
    private boolean checkIndoor = false;
    private boolean checkTraffic = false;

    String address;
    String getData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        SupportMapFragment mapFragment =
                (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        address = getString(R.string.getAllZoneInfo);
    }

    public void onMapReady(GoogleMap map) {
        mMap = map;
        updateMyLocation();
        updateTraffic();
        updateIndoor();
        updateBuildings();
        addMarkersToMap();
        double a = 0.0;
        LatLng la = new LatLng(a,a);
        try {
            getData = new HttpConnectionThread(MainActivity.this).execute(address).get();
            readData();
        }catch (Exception e){
        }
    }

    private boolean checkReady() {
        if (mMap == null) {
            Toast.makeText(this, "Map not ready", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private void updateMyLocation() {
        if (!checkReady()) {
            return;
        }

        if (!checkMylocation) {
            mMap.setMyLocationEnabled(false);
            return;
        }

        // Enable the location layer. Request the location permission if needed.
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true);
        } else {
            // Uncheck the box until the layer has been enabled and request missing permission.
            PermissionUtils.requestPermission(this, LOCATION_PERMISSION_REQUEST_CODE,
                    Manifest.permission.ACCESS_FINE_LOCATION, false);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] results) {
        if (requestCode != LOCATION_PERMISSION_REQUEST_CODE) {
            return;
        }

        if (PermissionUtils.isPermissionGranted(permissions, results,
                Manifest.permission.ACCESS_FINE_LOCATION)) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            mMap.setMyLocationEnabled(true);
        } else {
            mShowPermissionDeniedDialog = true;
        }
    }

    @Override
    protected void onResumeFragments() {
        super.onResumeFragments();
        if (mShowPermissionDeniedDialog) {
            PermissionUtils.PermissionDeniedDialog
                    .newInstance(false).show(getSupportFragmentManager(), "dialog");
            mShowPermissionDeniedDialog = false;
        }
    }

    private void updateBuildings() {
        if (!checkReady()) {
            return;
        }
        mMap.setBuildingsEnabled(checkBuilding);
    }

    private void updateIndoor() {
        if (!checkReady()) {
            return;
        }
        mMap.setIndoorEnabled(checkIndoor);
    }

    private void updateTraffic() {
        if (!checkReady()) {
            return;
        }
        mMap.setTrafficEnabled(checkTraffic);
    }
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        menu.findItem(R.id.action_mylocation).setChecked(checkMylocation);
        menu.findItem(R.id.action_building).setChecked(checkBuilding);
        menu.findItem(R.id.action_indoor).setChecked(checkIndoor);
        menu.findItem(R.id.action_traffic).setChecked(checkTraffic);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if(item.isChecked()){
            item.setChecked(false);
        }else{
            item.setChecked(true);
        }
        switch (id){
            case R.id.action_mylocation:
                checkMylocation = !checkMylocation;
                break;
            case R.id.action_building:
                checkBuilding = !checkBuilding;
                break;
            case R.id.action_indoor:
                checkIndoor = !checkIndoor;
                break;
            case R.id.action_traffic:
                checkTraffic = !checkTraffic;
                break;
        }
        onMapReady(mMap);
        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            Intent it = new Intent(MainActivity.this, TestActivity.class);
            startActivity(it);
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
    public void readData(){
        try{
            if(getData != null) {
                JSONObject jsonObject = new JSONObject(getData);
                JSONArray jsonArray = jsonObject.getJSONArray("getData");
                for (int i= 0; i < jsonArray.length(); i++){
                    String location = jsonObject.getString("midCoordinates");
                    int index = location.indexOf(",");
                    double latitude = Double.parseDouble(location.substring(0,index));
                    double longitude = Double.parseDouble(location.substring((index+1)));
                }
            }
        }catch (JSONException e ){
        }
    }
    private void addMarkersToMap() {
        // Uses a colored icon.
        mBrisbane = mMap.addMarker(new MarkerOptions()
                .position(streetPark29)
                .title("streetPark29")
                .snippet("totalQty: 20, availableQty: 0, type: public, price: $")
                .icon(BitmapDescriptorFactory.defaultMarker(0)));

        mBrisbane = mMap.addMarker(new MarkerOptions()
                .position(streetPark19)
                .title("streetPark19")
                .snippet("totalQty: 20, availableQty: 0, type: public, price: FREE")
                .icon(BitmapDescriptorFactory.defaultMarker(0)));

        mBrisbane = mMap.addMarker(new MarkerOptions()
                .position(streetPark31)
                .title("streetPark31")
                .snippet("totalQty: 20, availableQty: 0, type: public, price: FREE")
                .icon(BitmapDescriptorFactory.defaultMarker(0)));

        mBrisbane = mMap.addMarker(new MarkerOptions()
                .position(streetPark3)
                .title("streetPark3")
                .snippet("totalQty: 20, availableQty: 0, type: public, price: $$")
                .icon(BitmapDescriptorFactory.defaultMarker(0)));
        mBrisbane = mMap.addMarker(new MarkerOptions()
                .position(streetPark17)
                .title("streetPark17")
                .snippet("totalQty: 20, availableQty: 0, type: public, price: $")
                .icon(BitmapDescriptorFactory.defaultMarker(0)));

        mBrisbane = mMap.addMarker(new MarkerOptions()
                .position(streetPark27)
                .title("streetPark27")
                .snippet("totalQty: 20, availableQty: 2, type: public, price: $$")
                .icon(BitmapDescriptorFactory.defaultMarker(30)));
        mBrisbane = mMap.addMarker(new MarkerOptions()
                .position(streetPark24)
                .title("streetPark24")
                .snippet("totalQty: 20, availableQty: 0, type: public, price: $$")
                .icon(BitmapDescriptorFactory.defaultMarker(0)));

        mBrisbane = mMap.addMarker(new MarkerOptions()
                .position(streetPark2)
                .title("streetPark2")
                .snippet("totalQty: 20, availableQty: 14, type: public, price: FREE")
                .icon(BitmapDescriptorFactory.defaultMarker(120)));

        mBrisbane = mMap.addMarker(new MarkerOptions()
                .position(streetPark12)
                .title("streetPark12")
                .snippet("totalQty: 20, availableQty: 0, type: public, price: $$")
                .icon(BitmapDescriptorFactory.defaultMarker(0)));
        mBrisbane = mMap.addMarker(new MarkerOptions()
                .position(streetPark23)
                .title("streetPark23")
                .snippet("totalQty: 20, availableQty: 6, type: public, price: $")
                .icon(BitmapDescriptorFactory.defaultMarker(60)));

        mBrisbane = mMap.addMarker(new MarkerOptions()
                .position(streetPark10)
                .title("streetPark10")
                .snippet("totalQty: 20, availableQty: 3, type: public, price: FREE")
                .icon(BitmapDescriptorFactory.defaultMarker(60)));


        mBrisbane = mMap.addMarker(new MarkerOptions()
                .position(streetPark16)
                .title("streetPark16")
                .snippet("totalQty: 20, availableQty: 0, type: public, price: FREE")
                .icon(BitmapDescriptorFactory.defaultMarker(0)));
        mBrisbane = mMap.addMarker(new MarkerOptions()
                .position(streetPark28)
                .title("streetPark28")
                .snippet("totalQty: 20, availableQty: 0, type: public, price: FREE")
                .icon(BitmapDescriptorFactory.defaultMarker(0)));

        mBrisbane = mMap.addMarker(new MarkerOptions()
                .position(streetPark15)
                .title("streetPark15")
                .snippet("totalQty: 20, availableQty: 14, type: public, price: $$")
                .icon(BitmapDescriptorFactory.defaultMarker(120)));
        mBrisbane = mMap.addMarker(new MarkerOptions()
                .position(QUALCOMM_INSTITUTE)
                .title("QUALCOMM_INSTITUTE")
                .snippet("totalQty: 1, availableQty: 1, type: private, price: $$$$")
                .icon(BitmapDescriptorFactory.defaultMarker(120)));

        // Creates a marker rainbow demonstrating how to create default marker icons of different
        // hues (colors).
    }
    @Override
    public boolean onMarkerClick(final Marker marker) {
        // This causes the marker at Perth to bounce into position when it is clicked.
        final Handler handler = new Handler();
        final long start = SystemClock.uptimeMillis();
        final long duration = 1500;

        final Interpolator interpolator = new BounceInterpolator();

        handler.post(new Runnable() {
            @Override
            public void run() {
                long elapsed = SystemClock.uptimeMillis() - start;
                float t = Math.max(
                        1 - interpolator.getInterpolation((float) elapsed / duration), 0);
                marker.setAnchor(0.5f, 1.0f + 2 * t);

                if (t > 0.0) {
                    // Post again 16ms later.
                    handler.postDelayed(this, 16);
                }
            }
        });


        // Markers have a z-index that is settable and gettable.
        float zIndex = marker.getZIndex() + 1.0f;
        marker.setZIndex(zIndex);
        mLastSelectedMarker = marker;
        // We return false to indicate that we have not consumed the event and that we wish
        // for the default behavior to occur (which is for the camera to move such that the
        // marker is centered and for the marker's info window to open, if it has one).
        return false;
    }
}
