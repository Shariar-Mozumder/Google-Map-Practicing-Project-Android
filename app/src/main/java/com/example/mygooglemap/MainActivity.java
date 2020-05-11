package com.example.mygooglemap;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.os.HandlerThread;
import android.os.Looper;
import android.os.PersistableBundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback {

    private static final String TAG = "MapDebug";
    private static final int GPS_REQUEST_CODE = 9003;
    private boolean mLocationPermissionGranted;
    private  static final int PERMISSION_REQUEST_CODE=9001;
    private  static final int PLAY_SERVICE_ERROR_CODE=9002;
    private GoogleMap mgoogleMap;

    private TextView cordinateText;

    private EditText msearcheaddress;
    private ImageButton mbtnlocate;
    private Button batch_button;
    private FusedLocationProviderClient mProviderClient;
    private LocationCallback mLocationCallback;
    HandlerThread mhandlerThread;

    private final double feni_latitude=23.047462;
    private final double feni_logitude=91.365476;
    private final double bata_lat=23.739329;
    private final double bata_lng=90.388606;
  // private MapView mapView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        cordinateText=findViewById(R.id.coordinate_TV_id);
        msearcheaddress=findViewById(R.id.edit_address);
        mbtnlocate=findViewById(R.id.imageButton);
        batch_button=findViewById(R.id.batch_button);
        batch_button.setOnClickListener(this::batchLocationButtonClicked);
       mbtnlocate.setOnClickListener(this::geoLocate);

       mProviderClient=new FusedLocationProviderClient(this);
       mLocationCallback= new LocationCallback(){

           @Override
           public void onLocationResult(LocationResult locationResult) {

               if(locationResult==null){
                   return;
               }
               Location location=locationResult.getLastLocation();

             /*  runOnUiThread(new Runnable() {
                   @Override
                   public void run() {*/
                       cordinateText.setText(location.getLatitude()+" "+location.getLongitude());

                       gotolocation(location.getLatitude(),location.getLongitude());
                       showmarker(location.getLatitude(),location.getLongitude());

                    /*   Log.d(TAG, "run: inside runonUitread method"+Thread.currentThread().getName());
                   }
               });*/



               Toast.makeText(MainActivity.this, location.getLatitude()+" "+location.getLongitude(), Toast.LENGTH_SHORT).show();
               Log.d(TAG, "onLocationResult: "+location.getLatitude()+" "+location.getLongitude());

           }
       };





        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mgoogleMap!=null){
                    mgoogleMap.animateCamera(CameraUpdateFactory.zoomTo(3.0f));
                    LatLng latLng= new LatLng(bata_lat,bata_lng);

                    CameraUpdate cameraUpdate=CameraUpdateFactory.newLatLngZoom(latLng,18);
                    mgoogleMap.animateCamera(cameraUpdate, 5000, new GoogleMap.CancelableCallback() {
                        @Override
                        public void onFinish() {
                            Toast.makeText(MainActivity.this, "Animation is finished", Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onCancel() {
                            Toast.makeText(MainActivity.this, "Animation Cancelled", Toast.LENGTH_SHORT).show();
                        }
                    });

                /* double bottomboundary=23.048843;
                  double leftboundary= 91.360974;
                  double topboundary=23.051153;
                  double rightboundary=91.363174;

                    LatLngBounds home_bounds=new LatLngBounds(
                            new LatLng(bottomboundary,leftboundary),
                            new LatLng(topboundary,rightboundary));

                  mgoogleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(home_bounds.getCenter(),3));
                    //showmarker(home_bounds.getCenter());
                    mgoogleMap.setLatLngBoundsForCameraTarget(home_bounds);*/
                }
            }
        });

        initGoogleMap();
        //isServicesOK();
        //mapView=findViewById(R.id.mapView);
        //mapView.onCreate(savedInstanceState);
        //mapView.getMapAsync(this);



        }

    private void batchLocationButtonClicked(View view) {
        Intent intent=new Intent(MainActivity.this,BatchLocationActivity.class);
        startActivity(intent);
    }

    private void geoLocate(View view){
        hideSoftKeyboard(view);
        String locationName=msearcheaddress.getText().toString();
            Geocoder geocoder=new Geocoder(this, Locale.getDefault());
            try {
                List<Address> addressList= geocoder.getFromLocationName(locationName,1);
                if(addressList.size()>0){
                    Address address=addressList.get(0);
                    gotolocation(address.getLatitude(),address.getLongitude());
                    mgoogleMap.addMarker(new MarkerOptions().position(
                             new LatLng(address.getLatitude(),address.getLongitude())));
                    Toast.makeText(this, address.getLocality(), Toast.LENGTH_SHORT).show();
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    private void hideSoftKeyboard(View view) {
        InputMethodManager imn=(InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        imn.hideSoftInputFromWindow(view.getWindowToken(),0);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        Log.d(TAG, "onMapReady: Map is showing");
        mgoogleMap=googleMap;

      // MarkerOptions markerOptions=new MarkerOptions().title("Bristy,I love You").position(new LatLng(0.0,0.0));
        //mgoogleMap.addMarker(markerOptions);
        gotolocation(feni_latitude,feni_logitude);

      //  mgoogleMap.setMyLocationEnabled(true);
        //mgoogleMap.getUiSettings().setZoomControlsEnabled(true);
      // mgoogleMap.getUiSettings().setMapToolbarEnabled(true);
      // mgoogleMap.getUiSettings().setMyLocationButtonEnabled(true);

    }

    private void gotolocation(double lat,double lng){
        LatLng latLng=new LatLng(lat,lng);
        CameraUpdate cameraUpdate= CameraUpdateFactory.newLatLngZoom(latLng,15);
        mgoogleMap.moveCamera(cameraUpdate);

    }

   private void showmarker(double lat,double lng){
        MarkerOptions markerOptions=new MarkerOptions();
        markerOptions.position(new LatLng(lat,lng));
        mgoogleMap.addMarker(markerOptions);
    }

        private void initGoogleMap(){
             if(isServicesOK()){
                 if(isGPSEnabled()){
                     if(checkpermission()){
                         Toast.makeText(this, "Map is Ready", Toast.LENGTH_SHORT).show();
                         SupportMapFragment supportMapFragment= (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map_fragment_id);
                         supportMapFragment.getMapAsync(this);

                     }
                     else{
                         requestLocationPermission();
                     }
                 }

             }
        }

        private boolean isGPSEnabled(){

            LocationManager locationManager= (LocationManager) getSystemService(LOCATION_SERVICE);
            boolean providerenabled=locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

            if(providerenabled){
                return true;
            }
            else{
                AlertDialog alertDialog=new AlertDialog.Builder(this)
                        .setTitle("GPS Permission")
                        .setMessage("GPS Permission is required")
                        .setPositiveButton("Yes",(dialog, which) -> {
                            Intent intent=new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                            startActivityForResult(intent,GPS_REQUEST_CODE);
                        })
                        .setCancelable(false)
                        .show();
            }


            return false;
        }

    private boolean checkpermission() {
        return ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION)==
                PackageManager.PERMISSION_GRANTED;
    }

    private boolean isServicesOK(){
        GoogleApiAvailability googleApiAvailability=GoogleApiAvailability.getInstance();
        int result=googleApiAvailability.isGooglePlayServicesAvailable(this);

        if(result== ConnectionResult.SUCCESS){
            return true;
        }
        else if(googleApiAvailability.isUserResolvableError(result)){
            Dialog dialog=googleApiAvailability.getErrorDialog(this,result,PLAY_SERVICE_ERROR_CODE);
            dialog.show();
        }
        else{
            Toast.makeText(this, "Play Services are required by this application", Toast.LENGTH_SHORT).show();
        }
        return false;
    }
    private void requestLocationPermission() {

        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED){
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION},PERMISSION_REQUEST_CODE);
            }
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        switch (id){
            case R.id.no_map:{
                mgoogleMap.setMapType(GoogleMap.MAP_TYPE_NONE);
                break;
            }
            case R.id.normal_map:{
                mgoogleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                break;
            }
            case R.id.satellite_map:{
               mgoogleMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
               break;
            }
            case R.id.terrain_map:{
                mgoogleMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
                break;
            }
            case R.id.hybrid_map:{
                mgoogleMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
                break;
            }
            case R.id.current_location:{
               // getCurrentLocation();
                getLocationUpdates();
                break;
            }
        }

        return super.onOptionsItemSelected(item);
    }

    private void getCurrentLocation() {
        mProviderClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
            @Override
            public void onComplete(@NonNull Task<Location> task) {
                if(task.isSuccessful()){
                    Location location=task.getResult();
                    gotolocation(location.getLatitude(),location.getLongitude());
                    mgoogleMap.addMarker(new MarkerOptions().position(
                            new LatLng(location.getLatitude(),location.getLongitude())));
                }
                else{
                    Toast.makeText(MainActivity.this, "Error on my location", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(requestCode==PERMISSION_REQUEST_CODE&&grantResults[0]==PackageManager.PERMISSION_GRANTED){
            mLocationPermissionGranted=true;
            Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show();
        }
        else{
            Toast.makeText(this, "Permission Not granted", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        LocationManager locationManager= (LocationManager) getSystemService(LOCATION_SERVICE);
        boolean providerenabled=locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

        if(requestCode==GPS_REQUEST_CODE){
            if(providerenabled){
                Toast.makeText(this, "GPS is Enabled", Toast.LENGTH_SHORT).show();
            }
            else{
                Toast.makeText(this, "GPS is not Enabled, Unable to Show user Location", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void getLocationUpdates(){
        LocationRequest locationRequest=LocationRequest.create();

        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(5000);
        locationRequest.setFastestInterval(1000);

        if(ActivityCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION)
        !=PackageManager.PERMISSION_GRANTED){
            return;
        }


        mhandlerThread = new HandlerThread("LocationCallbackThread");
        mhandlerThread.start();

        mProviderClient.requestLocationUpdates(locationRequest,mLocationCallback,mhandlerThread.getLooper());


    }

    @Override
    protected void onPause() {
        super.onPause();

        if(mLocationCallback!=null){
            mProviderClient.removeLocationUpdates(mLocationCallback);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(mhandlerThread!=null){
            mhandlerThread.quit();
        }

    }

    /* @Override
    protected void onStart() {
        super.onStart();
        mapView.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();b
        mapView.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mapView.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);
        mapView.onSaveInstanceState(outState);
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }*/
}
