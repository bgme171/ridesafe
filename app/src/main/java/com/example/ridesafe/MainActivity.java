package com.example.ridesafe;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.hardware.TriggerEventListener;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    public static final int DEFAULT_UPDATE_INTERVAL = 30000;
    public static final int FAST_UPDATE_INTERVAL = 5000;
    private static final int PERMISSIONS_FINE_LOCATION = 99;
    TextView txt_accel_x, txt_accel_y, txt_accel_z, txt_fall;

    TextView tv_lat, tv_altitude, tv_lon, tv_accuracy, tv_speed, tv_sensor, tv_updates, tv_address;
    TextView tv_counter, tv_max_x, tv_max_y, tv_max_z;
    TextView tv_timer;
    Switch sw_locationUpdates, sw_gps;

    boolean on_fall= false;

    private SensorManager mSensorManager;
    private Sensor mAccelerometer;
    private float previous_x, previous_y, previous_z = 0;

    // Location request is a config file for all settings related to FusedLocationProviderClient
    LocationRequest locationRequest;

    LocationCallback locationCallback;


    CountDownTimer timer =  new CountDownTimer(5000, 1000) {

        public void onTick(long millisUntilFinished) {
            tv_timer.setText(String.valueOf(millisUntilFinished));
        }

        public void onFinish() {
            stopLocationUpdates();
            //previous_x=0;
            //previous_y=0;
            //previous_z=0;
            //mSensorManager.registerListener(sensorEventListener, mAccelerometer, SensorManager.SENSOR_DELAY_GAME);
            on_fall=false;
        }
    };


    int counter = 0;
    float max_x, max_y, max_z =0;

    // Google's API for location services.
    FusedLocationProviderClient fusedLocationProviderClient;




    private SensorEventListener sensorEventListener = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent event) {


            float x = event.values[0];
            float y = event.values[1];
            float z = event.values[2];

            txt_accel_x.setText("X axis acceleration = "+ (int)x);
            txt_accel_y.setText("Y axis acceleration = "+ (int)y);
            txt_accel_z.setText("z axis acceleration = "+ (int)z);



            float changeX = Math.abs(x - previous_x);
            previous_x = x;
            float changeY = Math.abs(y - previous_y);
            previous_y = y;
            float changeZ = Math.abs(z - previous_z);
            previous_z = z;

            if(on_fall){
                return;
            }

            if (changeX > 20 || changeY > 20 || changeZ > 20){
                // TODO: review why unregister isn't working fine
                // it looks like old values are used
                //mSensorManager.unregisterListener(this);
                on_fall=true;

                counter++;
                txt_fall.setText("Fall detected " + counter);
                txt_fall.setTextSize(50);
                txt_fall.setTextColor(Color.RED);
                tv_counter.setText("Total count= "+counter);

                timer.start();
                startLocationUpdates();
            }

            if(changeX > max_x){
                max_x= changeX;
                tv_max_x.setText("Max delta on X= "+max_x);
            }
            if(changeY > max_y){
                max_y= changeY;
                tv_max_y.setText("Max delta on y= "+max_y);
            }
            if(changeZ > max_z){
                max_z= changeZ;
                tv_max_z.setText("Max delta on Z= "+max_z);
            }

        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {

        }
    }; // end  eventListener accel

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        txt_accel_x = findViewById(R.id.txt_accel_x);
        txt_accel_y = findViewById(R.id.txt_accel_y);
        txt_accel_z = findViewById(R.id.txt_accel_z);

        txt_fall = findViewById(R.id.txt_alarm);

        mSensorManager = (SensorManager)getSystemService(SENSOR_SERVICE);
        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);
        mSensorManager.registerListener(sensorEventListener, mAccelerometer, SensorManager.SENSOR_DELAY_GAME);


        tv_lat=findViewById(R.id.tv_lat);
        tv_lon=findViewById(R.id.tv_lon);
        tv_altitude=findViewById(R.id.tv_altitude);
        tv_accuracy=findViewById(R.id.tv_accuracy);
        tv_speed=findViewById(R.id.tv_speed);
        tv_sensor=findViewById(R.id.tv_sensor);
        tv_address=findViewById(R.id.tv_address);
        tv_updates=findViewById(R.id.tv_updates);
        sw_gps=findViewById(R.id.sw_gps);
        sw_locationUpdates=findViewById(R.id.sw_locationsupdates);

        tv_counter=findViewById(R.id.tv_counter);
        tv_max_x=findViewById(R.id.tv_max_x);
        tv_max_y=findViewById(R.id.tv_max_y);
        tv_max_z=findViewById(R.id.tv_max_z);

        tv_timer = findViewById(R.id.tv_timer);

        // get max capabilities of sensor
        // tv_sensor.setText(String.valueOf(mAccelerometer.getMinDelay()));

        locationRequest = LocationRequest.create()
                .setInterval(DEFAULT_UPDATE_INTERVAL)
                .setFastestInterval(FAST_UPDATE_INTERVAL)
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setMaxWaitTime(100);

        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(@NonNull LocationResult locationResult) {
                super.onLocationResult(locationResult);

                Location location = locationResult.getLastLocation();
                updateUIValues(location);
            }
        };

        sw_locationUpdates.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (sw_locationUpdates.isChecked()){
                    // ON
                    //startLocationUpdates();
                    mSensorManager.registerListener(sensorEventListener, mAccelerometer, SensorManager.SENSOR_DELAY_GAME);
                } else {
                    // OFF
                    stopLocationUpdates();
                }
            }
        });
        updateGPS();

    } //end onCreate

    private void stopLocationUpdates() {
        tv_updates.setText("Location OFF");
        tv_lon.setText("Not available");
        tv_lat.setText("Not available");
        tv_accuracy.setText("Not available");
        tv_altitude.setText("Not available");
        tv_speed.setText("Not available");
        fusedLocationProviderClient.removeLocationUpdates(locationCallback);

    }

    private void startLocationUpdates() {
        tv_updates.setText("Location ON");
        fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, null);
        updateGPS();
    }

    private void updateGPS(){
        // permissions
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(MainActivity.this);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            // we have permission
            fusedLocationProviderClient.getLastLocation().addOnSuccessListener(this, new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                        // we got permission. Use location
                    updateUIValues(location);
                }
            });

        }
        else{
            // we need to ask for permission
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                requestPermissions(new String[] {Manifest.permission.ACCESS_FINE_LOCATION},PERMISSIONS_FINE_LOCATION);
            }
        }
    }

    private void updateUIValues(Location location) {
        tv_lat.setText(String.valueOf("Lat: " +location.getLatitude()));
        tv_lon.setText(String.valueOf("Lon: "+location.getLongitude()));
        tv_accuracy.setText(String.valueOf("Accuracy: "+location.getAccuracy()));

        if (location.hasSpeed()){
            tv_speed.setText("Speed: "+ String.valueOf(location.getSpeed()));
        }else{
            tv_speed.setText("Speed is not available");
        }
        Geocoder geocoder= new Geocoder(MainActivity.this);
        try{
            List<Address> addresses = geocoder.getFromLocation(location.getLatitude(),location.getLongitude(),1);
            tv_address.setText("Address: "+ addresses.get(0).getAddressLine(0));
        }catch (Exception e){
            tv_address.setText("Address couldn't be get");
        }

    }

    protected void onResume() {
        super.onResume();
        mSensorManager.registerListener(sensorEventListener, mAccelerometer, SensorManager.SENSOR_DELAY_GAME);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode){
            case PERMISSIONS_FINE_LOCATION:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    updateGPS();
                }
                else{
                    Toast.makeText(this, "this app requires permission to be granted in to work properly", Toast.LENGTH_SHORT).show();
                    finish();
                }
        }
    }

    protected void onPause() {
        super.onPause();
        mSensorManager.unregisterListener(sensorEventListener);
    }
}