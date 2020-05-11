package com.example.mygooglemap;

import androidx.appcompat.app.AppCompatActivity;

import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;

import java.util.List;

public class BatchLocationActivity extends AppCompatActivity {

    public static final String TAG="My Tag";

    private Button mBtnLocationRequest;
    private TextView mOutputText;
    private FusedLocationProviderClient mlocationClient;
    private LocationCallback mlocationCallback;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_batch_location);

        mBtnLocationRequest=findViewById(R.id.btn_location_request);
        mOutputText=findViewById(R.id.tv_output);

        mlocationClient= LocationServices.getFusedLocationProviderClient(this);
        mlocationCallback=new LocationCallback(){
            @Override
            public void onLocationResult(LocationResult locationResult) {
                super.onLocationResult(locationResult);

                if(locationResult==null){
                    Log.d(TAG, "onLocationResult: Location error");
                    return;
                }
                List<Location> locations=locationResult.getLocations();

                LocationResultHelper helper=new LocationResultHelper(BatchLocationActivity.this,locations);
               // helper.showNotification();

                Toast.makeText(BatchLocationActivity.this,"Location Received" , Toast.LENGTH_SHORT).show();

                mOutputText.setText(helper.getLocationResultText());
            }
        };


        mBtnLocationRequest.setOnClickListener(this::requestBatchLocationUpdates);
    }

    private void requestBatchLocationUpdates(View view) {

        LocationRequest locationRequest=LocationRequest.create();
        locationRequest.setInterval(5000);
        locationRequest.setFastestInterval(1000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        locationRequest.setMaxWaitTime(15*1000);

        mlocationClient.requestLocationUpdates(locationRequest,mlocationCallback,null);
    }
}
