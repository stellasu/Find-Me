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

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapsActivity extends FragmentActivity implements LocationListener, PromptDialogFragment.OnFragmentInteractionListener {

    private GoogleMap mMap; // Might be null if Google Play services APK is not available.
    private LocationManager mLocationManager;
    private Location location;
    String provider;
    double lat = 0;
    double lng = 0;
    PromptDialogFragment prompt;
    String classtag = MapsActivity.class.getName();

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
            //lat = (int) (location.getLatitude() * 1E6);
            //lng = (int) (location.getLongitude() * 1E6);
            lat = location.getLatitude();
            lng = location.getLongitude();
        }
        Log.i(classtag, "onCreate lat: "+lat+" lng: "+lng);

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
    }

    /**
     * This is where we can add markers or lines, add listeners or move the camera. In this case, we
     * just add a marker near Africa.
     * <p/>
     * This should only be called once and when we are sure that {@link #mMap} is not null.
     */
    private void setUpMap() {
        Log.i(classtag, "setUpMap lat: "+lat+" lng: "+lng);
        LatLng latlng = new LatLng(lat, lng);
        mMap.addMarker(new MarkerOptions().position(latlng).title("I'm here").snippet("Click to export your location"));
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
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latlng, 4.0f));
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

}
