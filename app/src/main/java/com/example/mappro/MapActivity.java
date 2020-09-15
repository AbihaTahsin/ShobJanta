package com.example.mappro;

import android.Manifest;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQuery;
import com.firebase.geofire.GeoQueryDataEventListener;
import com.firebase.geofire.LocationCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingClient;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MapActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleMap.OnMapLongClickListener, GeoQueryDataEventListener {

    private static final String TAG = "MapActivity";


    private static final String FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String COURSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1234;
    private int BACKGROUND_LOCATION_ACCESS_REQUEST_CODE = 10002;
    private static final float DEFAULT_ZOOM = 15f;
    PlacesClient placesClient;

    //widgets
    private EditText mSearchText;
    private ImageView mGps;

    //vars
    private Boolean mLocationPermissionsGranted = false ;
    private GoogleMap mMap;
    private GeofencingClient geofencingClient;
    private float GEOFENCE_RADIUS = 200;
    private GeofenceHelper geofenceHelper;
    private FusedLocationProviderClient mFusedLocationProviderClient;

    private String GEOFENCE_ID = "SOME_GEOFENCE_ID";
    private DatabaseReference myLocationRef;
    private GeoFire geoFire;
    private List<LatLng>eventArea;
    TextView mapDays,mapName;
    Button btnSet;
    Marker userLocationMarker;

    private LocationRequest locationRequest;

    @Override
    public void onMapReady(GoogleMap googleMap) {
        Toast.makeText(this,"Map is ready!",Toast.LENGTH_SHORT).show();
        Log.d(TAG,"OnMapReady: Map is ready");
        mMap = googleMap;

        if(mLocationPermissionsGranted){
            getDeviceLocation();

            if(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)!= PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(this,Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED){
                return;
            }
            mMap.setMyLocationEnabled(true);
            mMap.getUiSettings().setMyLocationButtonEnabled(false);
            mMap.getUiSettings().setZoomControlsEnabled(true);
            mMap.getUiSettings().setCompassEnabled(true);

            init();
            initArea();
            mMap.setOnMapLongClickListener(this);

            for(LatLng latLng : eventArea){
                addCircle(latLng, GEOFENCE_RADIUS);

                GeoQuery geoQuery = geoFire.queryAtLocation(new GeoLocation(latLng.latitude,latLng.longitude),0.5f);
                geoQuery.addGeoQueryDataEventListener(MapActivity.this);
            }

        }

    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        mSearchText = (EditText)findViewById(R.id.input_search);
        mGps = (ImageView)findViewById(R.id.ic_gps);

        String apiKey = "AIzaSyDbuFwHhNypEBwpBfaH_cuLxzOWWBh_Blg";
        if(!Places.isInitialized()){
            Places.initialize(getApplicationContext(),apiKey);
        }
        placesClient = Places.createClient(this);

        mapDays = findViewById(R.id.days);
        mapName = findViewById(R.id.name);

        btnSet = (Button) findViewById(R.id.okay);


        final AutocompleteSupportFragment autocompleteSupportFragment =
                (AutocompleteSupportFragment) getSupportFragmentManager().findFragmentById(R.id.autocomplete_fragment);

        autocompleteSupportFragment.setPlaceFields(Arrays.asList(Place.Field.ID,Place.Field.LAT_LNG,Place.Field.NAME));
        autocompleteSupportFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(@NonNull Place place) {
                final LatLng latLng = place.getLatLng();

                Log.i(TAG,"OnPlaceSelected: "+latLng.latitude+"\n"+latLng.longitude);
            }

            @Override
            public void onError(@NonNull Status status) {

            }
        });
        NotificationHelper notificationHelper = new NotificationHelper(this);
        getLocationPermission();
    }

    private void init(){
        Log.d(TAG,"init: initializing");

        mSearchText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textview, int actionId, KeyEvent keyEvent) {
                if(actionId == EditorInfo.IME_ACTION_SEARCH
                    || actionId == EditorInfo.IME_ACTION_DONE
                    || keyEvent.getAction() == keyEvent.ACTION_DOWN
                    || keyEvent.getAction() == keyEvent.KEYCODE_ENTER){

                    //execution of our method for searching
                    geoLocate();
                }
                return false;
            }
        });
        mGps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG,"onClick: Clicked GPS icon");
                getDeviceLocation();
            }
        });

        hideSoftKeyboard();
    }

    private void geoLocate(){
        Log.d(TAG,"geoLocate: geoLocating");

        String searchString = mSearchText.getText().toString();

        Geocoder geocoder = new Geocoder(MapActivity.this);
        List<Address> addressList = new ArrayList<>();
        try {
            addressList = geocoder.getFromLocationName(searchString,1);
        }catch (IOException e){
            Log.e(TAG,"geoLocate: IOException: " + e.getMessage() );
        }
        if(addressList.size() > 0){
            Address address = addressList.get(0);

            Log.d(TAG,"geoLocate: Found a location" + address.toString() );
            //Toast.makeText(this,address.toString(),Toast.LENGTH_SHORT).show();

            moveCamera(new LatLng(address.getLatitude(), address.getLongitude()), DEFAULT_ZOOM, address.getAddressLine(0) );
        }
    }

    private void getDeviceLocation(){
        Log.d(TAG,"getDeviceLocation: getting device's current location");

        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        try {
            if(mLocationPermissionsGranted){
                Task location = mFusedLocationProviderClient.getLastLocation();
                location.addOnCompleteListener(new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        if(task.isSuccessful()){
                            Log.d(TAG,"onComplete: Found Location");
                            Location currentLocation = (Location) task.getResult();

                            setUserLocationMarker(currentLocation);
                            moveCamera(new LatLng(currentLocation.getLatitude(),currentLocation.getLongitude()),DEFAULT_ZOOM,"My Location");
                        }else{
                            Log.d(TAG,"onComplete: Current Location is NULL");
                            Toast.makeText(MapActivity.this,"Unable To Get Current Location",Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }

        }catch(SecurityException e){
            Log.e(TAG,"getDeviceLocation: Security Exception: "+e.getMessage());
        }
    }

    private void setUserLocationMarker(Location location){

        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());

        if (userLocationMarker == null) {
            //Create a new marker
            MarkerOptions markerOptions = new MarkerOptions();
            markerOptions.position(latLng);

            userLocationMarker = mMap.addMarker(markerOptions);
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 17));
        } else  {
            //use the previously created marker
            userLocationMarker.setPosition(latLng);
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 17));
        }


    }

    private void moveCamera(LatLng latlng,float zoom, String title){
        Log.d(TAG,"moveCamera: Moving the camera to Lat: "+latlng.latitude+",Lng: "+latlng.longitude);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latlng,zoom));

        if(!title.equals("My Location")){
            MarkerOptions options = new MarkerOptions()
                    .position(latlng)
                    .title(title);
            //mMap.addMarker(options);
        }

        geoFire.setLocation(title, new GeoLocation(latlng.latitude, latlng.longitude), new GeoFire.CompletionListener() {
            @Override
            public void onComplete(String key, DatabaseError error) {

            }
        });
        hideSoftKeyboard();
    }

    private void initMap(){
        Log.d(TAG,"initMap: initializing map");
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(MapActivity.this);

        geofencingClient = LocationServices.getGeofencingClient(this);
        geofenceHelper =  new GeofenceHelper(this);

        initArea();
        settingGeoFire();
        locationRequest = LocationRequest.create();
        locationRequest.setInterval(500);
        locationRequest.setFastestInterval(500);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    /*LocationCallback locationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            //super.onLocationResult(locationResult);
            Log.d(TAG, "onLocationResult: " + locationResult.getLastLocation());
            if (mMap != null) {
                setUserLocationMarker(locationResult.getLastLocation());
            }
        }
    };

    private void startLocationUpdates() {
        mFusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper());
    }

    private void stopLocationUpdates() {
        mFusedLocationProviderClient.removeLocationUpdates(locationCallback);
    }*/

    private void initArea() {
        eventArea = new ArrayList<>();
        eventArea.add(new LatLng(37.422,-122.044));
        eventArea.add(new LatLng(37.422,-122.144));
        eventArea.add(new LatLng(37.422,-122.244));

    }

    private void settingGeoFire() {
        myLocationRef = FirebaseDatabase.getInstance().getReference("MyLocation");
        geoFire = new GeoFire(myLocationRef);
    }

    private void getLocationPermission(){
        Log.d(TAG,"getLocationPermission: Getting Location Permissions");
        String[] permissions = {Manifest.permission.ACCESS_COARSE_LOCATION,Manifest.permission.ACCESS_FINE_LOCATION};

        if(ContextCompat.checkSelfPermission(this.getApplicationContext(),FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            if(ContextCompat.checkSelfPermission(this.getApplicationContext(),COURSE_LOCATION) == PackageManager.PERMISSION_GRANTED){
                mLocationPermissionsGranted = true;
                initMap();
            }else{
                ActivityCompat.requestPermissions(this,permissions,LOCATION_PERMISSION_REQUEST_CODE);
            }
        }else{
            ActivityCompat.requestPermissions(this,permissions,LOCATION_PERMISSION_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        Log.d(TAG,"OnRequestPermissionResult: called");
        mLocationPermissionsGranted = false;

        switch(requestCode){
            case LOCATION_PERMISSION_REQUEST_CODE:{
                if(grantResults.length > 0){
                    for(int i=0;i<grantResults.length;i++){
                        if(grantResults[i] != PackageManager.PERMISSION_GRANTED){
                            mLocationPermissionsGranted = false;
                            Log.d(TAG,"OnRequestPermissionResult: Permission Failed");
                            return;
                        }
                    }
                    mLocationPermissionsGranted = true;
                    Log.d(TAG,"OnRequestPermissionResult: Permission Granted");
                    initMap();
                }
            }
        }
    }

    private void hideSoftKeyboard(){
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }

    @Override
    public void onMapLongClick(LatLng latLng) {

        if (Build.VERSION.SDK_INT >= 29) {
            //We need background permission
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_BACKGROUND_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                handleMapLongClick(latLng);
            } else {
                if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_BACKGROUND_LOCATION)) {
                    //We show a dialog and ask for permission
                    ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.ACCESS_BACKGROUND_LOCATION}, BACKGROUND_LOCATION_ACCESS_REQUEST_CODE);
                } else {
                    ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.ACCESS_BACKGROUND_LOCATION}, BACKGROUND_LOCATION_ACCESS_REQUEST_CODE);
                }
            }

        } else {
            handleMapLongClick(latLng);
        }

    }

    private void handleMapLongClick(final LatLng latLng) {
        //mMap.clear();
        mapName.setVisibility(View.VISIBLE);
        mapDays.setVisibility(View.VISIBLE);
        btnSet.setVisibility(View.VISIBLE);

        final String geoID = mapName.getText().toString();

        btnSet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addMarker(latLng);
                addCircle(latLng, GEOFENCE_RADIUS);
                addGeofence(latLng, GEOFENCE_RADIUS,geoID);

                mapName.setVisibility(View.GONE);
                mapDays.setVisibility(View.GONE);
                btnSet.setVisibility(View.GONE);
            }
        });

    }

    private void addGeofence(LatLng latLng, float radius, String ID) {

        Geofence geofence = geofenceHelper.getGeofence(ID, latLng, radius, Geofence.GEOFENCE_TRANSITION_ENTER
                | Geofence.GEOFENCE_TRANSITION_DWELL
                | Geofence.GEOFENCE_TRANSITION_EXIT);
        GeofencingRequest geofencingRequest = geofenceHelper.getGeofencingRequest(geofence);
        PendingIntent pendingIntent = geofenceHelper.getPendingIntent();

        geofencingClient.addGeofences(geofencingRequest, pendingIntent)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "onSuccess: Geofence Added...");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        String errorMessage = geofenceHelper.getErrorString(e);
                        Log.d(TAG, "onFailure: " + errorMessage);
                    }
                });
    }

    private void addMarker(LatLng latLng){
        MarkerOptions markerOptions = new MarkerOptions().position(latLng);
        mMap.addMarker(markerOptions);
    }

    private void addCircle(LatLng latLng, float radius){
        CircleOptions circleOptions = new CircleOptions();
        circleOptions.center(latLng);
        circleOptions.radius(radius);
        circleOptions.strokeColor(Color.argb(255, 255, 0,0));
        circleOptions.fillColor(Color.argb(64, 255, 0,0));
        circleOptions.strokeWidth(2);
        mMap.addCircle(circleOptions);
    }

    @Override
    public void onDataEntered(DataSnapshot dataSnapshot, GeoLocation location) {
        Toast.makeText(MapActivity.this, "u MAP", Toast.LENGTH_SHORT).show();
        NotificationHelper notificationHelper = new NotificationHelper(this);
        notificationHelper.sendHighPriorityNotification("GEOFENCE_TRANSITION_ENTER", "", MapActivity.class);
    }

    @Override
    public void onDataExited(DataSnapshot dataSnapshot) {
        Toast.makeText(MapActivity.this, "u MAP", Toast.LENGTH_SHORT).show();
        NotificationHelper notificationHelper = new NotificationHelper(this);
        notificationHelper.sendHighPriorityNotification("GEOFENCE_TRANSITION_EXIT", "", MapActivity.class);
    }

    @Override
    public void onDataMoved(DataSnapshot dataSnapshot, GeoLocation location) {
        Toast.makeText(MapActivity.this, "u MAP", Toast.LENGTH_SHORT).show();
        NotificationHelper notificationHelper = new NotificationHelper(this);
    }

    @Override
    public void onDataChanged(DataSnapshot dataSnapshot, GeoLocation location) {

    }

    @Override
    public void onGeoQueryReady() {

    }

    @Override
    public void onGeoQueryError(DatabaseError error) {
    }
}
