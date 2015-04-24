package com.example.biyaosu.findme;

import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.net.Uri;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.UiSettings;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

public class MapsActivity extends FragmentActivity implements LocationListener, PromptDialogFragment.OnFragmentInteractionListener {

    private GoogleMap mMap; // Might be null if Google Play services APK is not available.
    private LocationManager mLocationManager;
    private Location location;
    private String provider;
    private double lat = 0;
    private double lng = 0;
    private LatLng currentLatLng = null;
    PromptDialogFragment prompt;
    String classtag = MapsActivity.class.getName();
    YelpAPI yelpAPI = new YelpAPI();
    private ArrayList<HashMap<String, String>> yelpList = new ArrayList<>();
    private float zoomLevel = 10.0f;
    private Marker currentLocationMarker = null;
    private Marker droppedPin = null;
    private UiSettings uiSettings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        Criteria locationCriteria = new Criteria();
        locationCriteria.setAccuracy(Criteria.ACCURACY_FINE);
        mLocationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        provider = mLocationManager.getBestProvider(locationCriteria, true);
        location = null;

        if(provider != null)
        {
            mLocationManager.requestLocationUpdates(provider, 0, 0, this);
            location = mLocationManager.getLastKnownLocation(provider);
        }
        if(location != null)
        {
            lat = location.getLatitude();
            lng = location.getLongitude();
        }
        Log.i(classtag, "onCreate lat: " + lat + " lng: " + lng);

        setUpMapIfNeeded();

    }

    @Override
    protected void onResume() {
        super.onResume();

        if(provider != null)
        {
            mLocationManager.requestLocationUpdates(provider, 0, 0, this);
            location = mLocationManager.getLastKnownLocation(provider);
        }
        if(location != null)
        {
            lat = location.getLatitude();
            lng = location.getLongitude();
        }
        Log.i(classtag, "onResume lat: "+lat+" lng: "+lng);

        setUpMapIfNeeded();
    }

    @Override
    protected void onPause(){
        super.onPause();
    }

    /**
     * Sets up the map if it is possible to do so (i.e., the Google Play services APK is correctly
     * installed) and the map has not already been instantiated.. This will ensure that we only ever
     * call {@link #setUpMap()} once when {@link #mMap} is not null.
     * <p/>
     * If it isn't installed {@link SupportMapFragment} (and
     * {@link com.google.android.gms.maps.MapView MapView}) will show a prompt for the user to
     * install/update the Google Play services APK on their device.
     * <p/>
     * A user can return to this FragmentActivity after following the prompt and correctly
     * installing/updating/enabling the Google Play services. Since the FragmentActivity may not
     * have been completely destroyed during this process (it is likely that it would only be
     * stopped or paused), {@link #onCreate(Bundle)} may not be called again so we should call this
     * method in {@link #onResume()} to guarantee that it will be called.
     */
    private void setUpMapIfNeeded() {
        Log.i(classtag, "setUpMapIfNeeded");
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
                    .getMap();

            // Check if we were successful in obtaining the map.
            if (mMap != null) {
                setUpMap();
            }
        }

        //add the drop pin function here
        mMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng clickedLatLng) {
                if(droppedPin != null){
                    droppedPin.remove();
                }
                droppedPin = mMap.addMarker(new MarkerOptions()
                        .title("Destination")
                        .position(clickedLatLng)
                        .snippet("Click to export this destination")
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW))
                );
            }
        });

    }

    /**
     * This is where we can add markers or lines, add listeners or move the camera. In this case, we
     * just add a marker near Africa.
     * <p/>
     * This should only be called once and when we are sure that {@link #mMap} is not null.
     */
    private void setUpMap()
    {
        Log.i(classtag, "setUpMap lat: "+lat+" lng: "+lng);
        currentLatLng = new LatLng(lat, lng);
        if(currentLocationMarker != null){
            currentLocationMarker.remove();
        }
        currentLocationMarker = mMap.addMarker(new MarkerOptions()
                .position(currentLatLng)
                .title("I'm here")
                .snippet("Click to export your location")
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE))
        );
        mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {
                LatLng markerLatlng = marker.getPosition();
                String markerLat = String.valueOf(markerLatlng.latitude);
                String markerLng = String.valueOf(markerLatlng.longitude);
                Log.i(classtag, "exported latlng: "+markerLat+" "+markerLng);
                prompt = new PromptDialogFragment().newInstance(markerLat, markerLng);

                prompt.show(getFragmentManager(), "markerLocation");
            }
        });

        if(droppedPin != null){
            droppedPin = mMap.addMarker(new MarkerOptions()
                            .title("Destination")
                            .position(droppedPin.getPosition())
                            .snippet("Click to export this destination")
                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW))
            );
        }
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, zoomLevel));
        mMap.setMyLocationEnabled(true);

        //add google map options
        uiSettings = mMap.getUiSettings();
        uiSettings.setMyLocationButtonEnabled(true);
        uiSettings.setZoomControlsEnabled(true);
        uiSettings.setMapToolbarEnabled(true);
    }


    //add yelp business markers to the map
    private void setUpMapWithYelpLocation()
    {
        Log.i(classtag, "setUpMapWithYelpLocation lat: "+lat+" lng: "+lng);
        setUpMap();

        Thread thread = new Thread(new Runnable(){
            @Override
            public void run() {
                try {
                    yelpList = yelpAPI.queryAPI(String.valueOf(lat), String.valueOf(lng));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        thread.start();
        try{
            thread.join();
        } catch (InterruptedException e){
            e.printStackTrace();
        }

        Iterator<HashMap<String, String>> it = yelpList.iterator();
        if(it.hasNext()){
            zoomLevel = 14.0f;
        }
        while(it.hasNext()){
            HashMap map = it.next();
            try{
                String businessLat = String.valueOf(map.get("businessLat"));
                String businessLng = String.valueOf(map.get("businessLng"));
                String businessName = String.valueOf(map.get("name"));
                LatLng businessLatLng = new LatLng(Double.parseDouble(businessLat), Double.parseDouble(businessLng));
                mMap.addMarker(new MarkerOptions()
                        .position(businessLatLng)
                        .title(businessName)
                        .snippet("Click to export business location"));
            }catch (Exception e){
                Log.i(classtag, "Exception: "+e.getMessage());
            }
        }
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, zoomLevel));
    }

    @Override
    public void onLocationChanged(Location location) {
        Log.i("Message: ","Location changed, " + location.getAccuracy() + " , " + location.getLatitude()+ "," + location.getLongitude());
        lat = location.getLatitude();
        lng = location.getLongitude();
        mLocationManager.removeUpdates(this);

        setUpMap();
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    public void onFragmentInteraction(Uri uri){

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch(item.getItemId()){
            case R.id.display_yelp:
                Log.i(classtag, "display yelp locations");
                setUpMapWithYelpLocation();
                return true;
            case R.id.clear_yelp:
                Log.i(classtag, "clear yelp locations");
                clearYelpMarkers();
                return true;
            case R.id.actionbar_help:
                Log.i(classtag, "help");
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    //clear all yelp markers, leave currentLocationMarker and droppedPin
    public void clearYelpMarkers()
    {
        Log.i(classtag, "clearYelpMarkers");
        mMap.clear();
        zoomLevel = 10.0f;
        setUpMap();
    }

}
