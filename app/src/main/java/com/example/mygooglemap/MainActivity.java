package com.example.mygooglemap;

import android.Manifest;
import android.app.Dialog;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.os.Build;
import android.os.Bundle;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import android.os.PersistableBundle;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback {

    private static final String TAG = "MapDebug";
    private boolean mLocationPermissionGranted;
    private  static final int PERMISSION_REQUEST_CODE=9001;
    private  static final int PLAY_SERVICE_ERROR_CODE=9002;
    private GoogleMap mgoogleMap;

    private EditText msearcheaddress;
    private ImageButton mbtnlocate;

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

        msearcheaddress=findViewById(R.id.edit_address);
        mbtnlocate=findViewById(R.id.imageButton);
       // mbtnlocate.setOnClickListener(this::geoLocate);



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

                 double bottomboundary=23.048843;
                  double leftboundary= 91.360974;
                  double topboundary=23.051153;
                  double rightboundary=91.363174;

                    LatLngBounds home_bounds=new LatLngBounds(
                            new LatLng(bottomboundary,leftboundary),
                            new LatLng(topboundary,rightboundary));

                  mgoogleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(home_bounds.getCenter(),3));
                    showmarker(home_bounds.getCenter());
                    mgoogleMap.setLatLngBoundsForCameraTarget(home_bounds);
                }
            }
        });

        initGoogleMap();
        //isServicesOK();
        //mapView=findViewById(R.id.mapView);
        //mapView.onCreate(savedInstanceState);
        //mapView.getMapAsync(this);

        SupportMapFragment supportMapFragment= (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map_fragment_id);
        supportMapFragment.getMapAsync(this);


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

       MarkerOptions markerOptions=new MarkerOptions().title("Bristy,I love You").position(new LatLng(0.0,0.0));
        mgoogleMap.addMarker(markerOptions);
        gotolocation(feni_latitude,feni_logitude);

        mgoogleMap.getUiSettings().setZoomControlsEnabled(true);
        mgoogleMap.getUiSettings().setMapToolbarEnabled(true);

    }

    private void gotolocation(double lat,double lng){
        LatLng latLng=new LatLng(lat,lng);
        CameraUpdate cameraUpdate= CameraUpdateFactory.newLatLngZoom(latLng,18);
        mgoogleMap.moveCamera(cameraUpdate);

    }

   private void showmarker(LatLng latLng){
        MarkerOptions markerOptions=new MarkerOptions();
        markerOptions.position(latLng);
        mgoogleMap.addMarker(markerOptions);
    }

        private void initGoogleMap(){
             if(isServicesOK()){
                 if(checkpermission()){
                     Toast.makeText(this, "Map is Ready", Toast.LENGTH_SHORT).show();
                 }
                 else{
                     requestLocationPermission();
                 }
             }
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
        }

        return super.onOptionsItemSelected(item);
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
