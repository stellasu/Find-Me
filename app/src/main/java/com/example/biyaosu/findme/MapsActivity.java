package com.example.biyaosu.findme;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.net.Uri;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.os.Environment;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.GoogleMap.InfoWindowAdapter;

import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.net.URL;
import java.net.HttpURLConnection;
import java.io.InputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;

public class MapsActivity extends FragmentActivity implements LocationListener, PromptDialogFragment.OnFragmentInteractionListener,
        SaveLocationDialog.OnSaveLocationFragmentInteractionListener, HelpDialog.OnHelpDialogFragmentInteractionListener, SendSMSDialogFragment.OnSendSMSDialogFragmentInteractionListener {

    private GoogleMap mMap; // Might be null if Google Play services APK is not available.
    private LocationManager mLocationManager;
    private Location location;
    private String provider;
    private double lat = 0;
    private double lng = 0;
    private LatLng currentLatLng = null;
    PromptDialogFragment prompt;
    YelpAPI yelpAPI = new YelpAPI();
    private ArrayList<HashMap<String, String>> yelpList = new ArrayList<>();
    private boolean hasYelpData = false;
    private float zoomLevel = 12.0f;
    private Marker currentLocationMarker = null;
    private boolean hasDroppedPin = false;
    private Marker droppedPin = null;
    private double droppedLat = 0;
    private double droppedLng = 0;
    private UiSettings uiSettings;
    private HashMap<Marker, HashMap<String, String>> markerMap = new HashMap<>();
    HelpDialog helpDialog;
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
            lat = location.getLatitude();
            lng = location.getLongitude();
        }
        Log.i(classtag, "onCreate lat: " + lat + " lng: " + lng);

    }

    @Override
    protected void onResume()
    {
        Log.i(classtag, "onResume");
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
    protected void onPause()
    {
        super.onPause();
    }

    @Override
    protected void onSaveInstanceState(Bundle savedInstanceState)
    {
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putDouble("lat", lat);
        savedInstanceState.putDouble("lng", lng);
        savedInstanceState.putDouble("droppedLat", droppedLat);
        savedInstanceState.putDouble("droppedLng", droppedLng);
        savedInstanceState.putBoolean("hasDroppedPin", hasDroppedPin);
        savedInstanceState.putBoolean("hasYelpData", hasYelpData);
        savedInstanceState.putFloat("zoomLevel", zoomLevel);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState)
    {
        super.onRestoreInstanceState(savedInstanceState);
        lat = savedInstanceState.getDouble("lat");
        lng = savedInstanceState.getDouble("lng");
        droppedLat = savedInstanceState.getDouble("droppedLat");
        droppedLng = savedInstanceState.getDouble("droppedLng");
        hasDroppedPin = savedInstanceState.getBoolean("hasDroppedPin");
        hasYelpData = savedInstanceState.getBoolean("hasYelpData");
        zoomLevel = savedInstanceState.getFloat("zoomLevel");
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
    private void setUpMap()
    {
        Log.i(classtag, "setUpMap lat: "+lat+" lng: "+lng);
        mMap.clear();
        currentLatLng = new LatLng(lat, lng);
        if(currentLocationMarker != null){
            currentLocationMarker.remove();
        }
        currentLocationMarker = mMap.addMarker(new MarkerOptions()
                        .title("You are here")
                        .position(currentLatLng)
                        .snippet("Click to export your location")
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE))
        );

        mMap.setInfoWindowAdapter(new CustomizedInfoWindow());

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

        if(hasDroppedPin){
            Log.i(classtag, "hasDroppedPin true");
            if(droppedPin != null){
                Log.i(classtag, "droppedPin not null");
                droppedPin = mMap.addMarker(new MarkerOptions()
                                .title("Destination")
                                .position(droppedPin.getPosition())
                                .snippet("Click to export this destination")
                                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW))
                );
            }else{
                droppedPin = mMap.addMarker(new MarkerOptions()
                                .title("Destination")
                                .position(new LatLng(droppedLat, droppedLng))
                                .snippet("Click to export this destination")
                                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW))
                );
            }
        }

        //add the drop pin function here
        mMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng clickedLatLng) {
                Log.i(classtag, "setOnMapLongClickListener");
                hasDroppedPin = true;
                if(droppedPin != null){
                    Log.i(classtag, "droppedPin not null");
                    droppedPin.remove();
                }
                droppedPin = mMap.addMarker(new MarkerOptions()
                                .title("Destination")
                                .position(clickedLatLng)
                                .snippet("Click to export this destination")
                                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW))
                );
                droppedLat = clickedLatLng.latitude;
                droppedLng = clickedLatLng.longitude;
            }
        });

        if(hasYelpData && yelpList.isEmpty()){
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
                Marker yelpMarker = mMap.addMarker(new MarkerOptions()
                        .position(businessLatLng));
                markerMap.put(yelpMarker, map);
            }catch (Exception e){
                Log.i(classtag, "Exception: "+e.getMessage());
            }
        }

        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, zoomLevel));
        mMap.setMyLocationEnabled(true);

        //add google map options
        uiSettings = mMap.getUiSettings();
        uiSettings.setMyLocationButtonEnabled(true);
        uiSettings.setZoomControlsEnabled(true);
    }


    //add yelp business markers to the map
    private void setUpMapWithYelpLocation()
    {
        Log.i(classtag, "setUpMapWithYelpLocation lat: "+lat+" lng: "+lng);
        setUpMap();
        if(hasYelpData && yelpList.isEmpty()){
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
                Marker yelpMarker = mMap.addMarker(new MarkerOptions()
                        .position(businessLatLng));
                markerMap.put(yelpMarker, map);
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

    public void onSaveLocationFragmentInteraction(Uri uri){

    }

    public void onHelpDialogFragmentInteraction(Uri uri) {

    }

    @Override
    public void onSendSMSDialogFragmentInteraction(Uri uri) {

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
                hasYelpData = true;
                setUpMapWithYelpLocation();
                return true;
            case R.id.clear_yelp:
                Log.i(classtag, "clear yelp locations");
                hasYelpData = false;
                clearYelpMarkers();
                return true;
            case R.id.removeDropped:
                Log.i(classtag, "remove dropped pin");
                removeDroppedPin();
                return true;
            case R.id.savedLocations:
                Log.i(classtag, "see saved locations");
                FMDataSource fmds = new FMDataSource(getApplicationContext());
                fmds.open();
                fmds.listAllLocations();
                seeSavedLocations();
                return true;
            case R.id.actionbar_help:
                Log.i(classtag, "help");
                showHelpDialog();
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
        markerMap.clear();
        yelpList.clear();
        zoomLevel = 12.0f;
        setUpMap();
    }

    //remove dropped pin
    public void removeDroppedPin()
    {
        Log.i(classtag, "removeDroppedPin");
        hasDroppedPin = false;
        if(droppedPin != null){
            Log.i(classtag, "droppedPin not null");
            droppedPin.remove();
            droppedPin = null;
        }
    }

    public class CustomizedInfoWindow implements InfoWindowAdapter
    {
        public CustomizedInfoWindow () {}

        @Override
        public View getInfoWindow (Marker marker)
        {
            return null;
        }

        @Override
        public View getInfoContents (Marker marker)
        {
            if(markerMap.containsKey(marker)){
                HashMap<String, String> map = markerMap.get(marker);
                String business_name = map.get("name");
                String business_snippet = map.get("snippet");
                String business_introduction = map.get("businessCategories");

                View infowindowView = getLayoutInflater().inflate(
                        R.layout.infowindow, null);
                TextView businessName = ((TextView)infowindowView
                        .findViewById(R.id.businessName));
                businessName.setText(business_name);
                TextView businessIntroduction = ((TextView) infowindowView
                        .findViewById(R.id.introduction));
                businessIntroduction.setText(business_introduction);
                ImageView businessSnippet = ((ImageView)infowindowView
                        .findViewById(R.id.snippet));
                new DownloadImageTask(businessSnippet).execute(business_snippet);

                return infowindowView;
            }else{
                return null;
            }

        }
    }

    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap>
    {
        ImageView bmImage;

        public DownloadImageTask(ImageView bmImage)
        {
            this.bmImage = bmImage;
        }

        protected Bitmap doInBackground(String... urls)
        {
            Bitmap bMap = null;
            String url = urls[0];
            bMap = getBitmapFromURL(url);
            if(bMap == null){
                Log.i(classtag, "no bMap");
            }else{
                Log.i(classtag, "has bMap");
            }
            return bMap;
        }

        protected void onPostExecute(Bitmap result)
        {
            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
            result.compress(Bitmap.CompressFormat.JPEG, 40, bytes);
            bmImage.setImageBitmap(result);
            saveImageToSD(result);
        }
    }

    public Bitmap getBitmapFromURL(String src)
    {
        try {
            Log.i(classtag, "image url: "+src);
            URL url = new URL(src);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap myBitmap = BitmapFactory.decodeStream(input);
            return myBitmap;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private void saveImageToSD(Bitmap bmp)
    {
        Log.i(classtag, "saveImageToSD");
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = Environment.getExternalStorageDirectory()
                + File.separator + System.currentTimeMillis() + "downloaded.jpg";
        Log.i(classtag, "path: "+path);
        File file = new File(path);
        try {
            file.createNewFile();
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            FileOutputStream fos = new FileOutputStream(file);
            fos.write(bytes.toByteArray());
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void seeSavedLocations()
    {
        Intent i = new Intent(this, SavedLocationActivity.class);
        startActivity(i);
    }

    public void showHelpDialog()
    {
        if(helpDialog == null){
            helpDialog = new HelpDialog().newInstance();
        }
        helpDialog.show(getFragmentManager(), "help dialog");
    }


}
