package com.example.ridesafe;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
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

//    public static final int DEFAULT_UPDATE_INTERVAL = 1000;
//    public static final int FAST_UPDATE_INTERVAL = 500;
    private static final int PERMISSIONS_FINE_LOCATION = 99;
    public static int ACTION_MANAGE_OVERLAY_PERMISSION_REQUEST_CODE= 2323;
//    TextView txt_accel_x, txt_accel_y, txt_accel_z, txt_fall;
//
//    TextView tv_lat, tv_altitude, tv_lon, tv_accuracy, tv_speed, tv_sensor, tv_updates, tv_address;
//    TextView tv_counter, tv_max_x, tv_max_y, tv_max_z;
//    TextView tv_timer;
//    Switch sw_locationUpdates, sw_gps;

    Button b_contacts, b_start, b_stop, b_permission;

//    Location previous_location;
//    float distance= 999.99f;
//
//    boolean on_fall= false;
//    boolean first_location = true;
//
//    private SensorManager mSensorManager;
//    private Sensor mAccelerometer;
//    private float previous_x, previous_y, previous_z = 0;
//
//    // Location request is a config file for all settings related to FusedLocationProviderClient
//    LocationRequest locationRequest;
//
//    LocationCallback locationCallback;
//
//
//    CountDownTimer timer =  new CountDownTimer(10000, 1000) {
//
//        public void onTick(long millisUntilFinished) {
//            tv_timer.setText(String.valueOf(millisUntilFinished));
//        }
//
//        public void onFinish() {
//            stopLocationUpdates();
//            //previous_x=0;
//            //previous_y=0;
//            //previous_z=0;
//            //mSensorManager.registerListener(sensorEventListener, mAccelerometer, SensorManager.SENSOR_DELAY_GAME);
//            on_fall=false;
//        }
//    };
//
//
//    int counter = 0;
//    float max_x, max_y, max_z =0;
//
    // Google's API for location services.
//    FusedLocationProviderClient fusedLocationProviderClient;




//    private SensorEventListener sensorEventListener = new SensorEventListener() {
//        @Override
//        public void onSensorChanged(SensorEvent event) {
//
//
//            float x = event.values[0];
//            float y = event.values[1];
//            float z = event.values[2];
//
//            txt_accel_x.setText("X axis acceleration = "+ (int)x);
//            txt_accel_y.setText("Y axis acceleration = "+ (int)y);
//            txt_accel_z.setText("z axis acceleration = "+ (int)z);
//
//
//
//            float changeX = Math.abs(x - previous_x);
//            previous_x = x;
//            float changeY = Math.abs(y - previous_y);
//            previous_y = y;
//            float changeZ = Math.abs(z - previous_z);
//            previous_z = z;
//
//            if(on_fall){
//                return;
//            }
//
//            if (changeX > 80 || changeY > 80 || changeZ > 80){
//                // TODO: review why unregister isn't working fine
//                // it looks like old values are used
//                //mSensorManager.unregisterListener(this);
//                on_fall=true;
//                timer.start();
//                startLocationUpdates();
//            }
//
//            if(changeX > max_x){
//                max_x= changeX;
//                tv_max_x.setText("Max delta on X= "+max_x);
//            }
//            if(changeY > max_y){
//                max_y= changeY;
//                tv_max_y.setText("Max delta on y= "+max_y);
//            }
//            if(changeZ > max_z){
//                max_z= changeZ;
//                tv_max_z.setText("Max delta on Z= "+max_z);
//            }
//
//        }
//
//        @Override
//        public void onAccuracyChanged(Sensor sensor, int accuracy) {
//
//        }
//    }; // end  eventListener accel

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.SEND_SMS}, 2);
        }

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q && !Settings.canDrawOverlays(MainActivity.this)){
            RequestPermission();
        }


//        txt_accel_x = findViewById(R.id.txt_accel_x);
//        txt_accel_y = findViewById(R.id.txt_accel_y);
//        txt_accel_z = findViewById(R.id.txt_accel_z);
//
//        txt_fall = findViewById(R.id.txt_alarm);
//
//        mSensorManager = (SensorManager)getSystemService(SENSOR_SERVICE);
//        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);
//        mSensorManager.registerListener(sensorEventListener, mAccelerometer, SensorManager.SENSOR_DELAY_GAME);
//
//
//        tv_lat=findViewById(R.id.tv_lat);
//        tv_lon=findViewById(R.id.tv_lon);
//        tv_altitude=findViewById(R.id.tv_altitude);
//        tv_accuracy=findViewById(R.id.tv_accuracy);
//        tv_speed=findViewById(R.id.tv_speed);
//        tv_sensor=findViewById(R.id.tv_sensor);
//        tv_address=findViewById(R.id.tv_address);
//        tv_updates=findViewById(R.id.tv_updates);
//        sw_gps=findViewById(R.id.sw_gps);
//        sw_locationUpdates=findViewById(R.id.sw_locationsupdates);
//
//        tv_counter=findViewById(R.id.tv_counter);
//        tv_max_x=findViewById(R.id.tv_max_x);
//        tv_max_y=findViewById(R.id.tv_max_y);
//        tv_max_z=findViewById(R.id.tv_max_z);
//
//        tv_timer = findViewById(R.id.tv_timer);

        b_contacts = findViewById(R.id.b_contacts);
        b_start = findViewById(R.id.b_start);
        b_stop = findViewById(R.id.b_stop);
        b_stop  = findViewById(R.id.b_permission);


        b_contacts.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, ContactsActivity.class);
                //intent.putExtra("last_location", location);
                startActivity(intent);
            }
        });


//        locationRequest = LocationRequest.create()
//                .setInterval(DEFAULT_UPDATE_INTERVAL)
//                .setFastestInterval(FAST_UPDATE_INTERVAL)
//                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
//                .setMaxWaitTime(100);
//
//        locationCallback = new LocationCallback() {
//            @Override
//            public void onLocationResult(@NonNull LocationResult locationResult) {
//                super.onLocationResult(locationResult);
//
//                Location location = locationResult.getLastLocation();
//                updateUIValues(location);
//            }
//        };

        gps_register();
        createNotificationChannel();

    } //end onCreate

//    private void stopLocationUpdates() {
//        tv_updates.setText("Location OFF");
//        tv_lon.setText("Not available");
//        tv_lat.setText("Not available");
//        tv_accuracy.setText("Not available");
//        tv_altitude.setText("Not available");
//        tv_speed.setText("Not available");
//        fusedLocationProviderClient.removeLocationUpdates(locationCallback);
//
//    }
//
//    private void startLocationUpdates() {
//        tv_updates.setText("Location ON");
//        //fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(MainActivity.this);
//        fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, null);
//    }

    private void gps_register(){
        // permissions
//        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(MainActivity.this);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
//            // we have permission
//            fusedLocationProviderClient.getLastLocation().addOnSuccessListener(this, new OnSuccessListener<Location>() {
//                @Override
//                public void onSuccess(Location location) {
//                        // we got permission. Use location
//                }
//            });

        }
        else{
            // we need to ask for permission
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                requestPermissions(new String[] {Manifest.permission.ACCESS_FINE_LOCATION},PERMISSIONS_FINE_LOCATION);
            }
        }
    }

//    private void updateUIValues(Location location) {
//
//        tv_lat.setText(String.valueOf("Lat: " +location.getLatitude()));
//        tv_lon.setText(String.valueOf("Lon: "+location.getLongitude()));
//        //tv_accuracy.setText(String.valueOf("Accuracy: "+location.getAccuracy()));
//
//
//        if(first_location){
//            previous_location=location;
//            first_location=false;
//            tv_accuracy.setText("We returned");
//            return;
//        }else{
//            tv_accuracy.setText("We get distance");
//            distance = location.distanceTo(previous_location);
//        }
//
//
//        tv_speed.setText("Distance: " + String.valueOf(distance));
//        if(distance < location.getAccuracy()){
//            //tv_altitude.setText("We are on a fall event");
//            //counter++;
//            //txt_fall.setText("Fall detected " + counter);
//            //txt_fall.setTextSize(50);
//            //txt_fall.setTextColor(Color.RED);
//            //tv_counter.setText("Total count= "+counter);
//            stopLocationUpdates();
//            timer.cancel();
//            Intent intent = new Intent(MainActivity.this, AlarmActivity.class);
//            intent.putExtra("last_location", location);
//            startActivity(intent);
//        }
//
//
//        Geocoder geocoder= new Geocoder(MainActivity.this);
//        try{
//            List<Address> addresses = geocoder.getFromLocation(location.getLatitude(),location.getLongitude(),1);
//            tv_address.setText("Address: "+ addresses.get(0).getAddressLine(0));
//        }catch (Exception e){
//            tv_address.setText("Address couldn't be get");
//        }
//
//    }

    protected void onResume() {
        super.onResume();
       // on_fall=false;
       // first_location=true;
        //mSensorManager.registerListener(sensorEventListener, mAccelerometer, SensorManager.SENSOR_DELAY_GAME);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode){
            case PERMISSIONS_FINE_LOCATION:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    //updateGPS();
                }
                else{
                    Toast.makeText(this, "this app requires permission to be granted in to work properly", Toast.LENGTH_SHORT).show();
                    finish();
                }
        }
    }

    protected void onPause() {
        super.onPause();
       //mSensorManager.unregisterListener(sensorEventListener);
    }

    public void start_service(View view){
        Intent myService = new Intent(MainActivity.this, AccelService.class);
        MainActivity.this.startService(myService);
    }

    public void stop_service(View view){
        Intent myService = new Intent(MainActivity.this, AccelService.class);
        MainActivity.this.stopService(myService);
    }


    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "canal_notificaiones";
            String description = "descripcion";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel("256", name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    // request over

    private void RequestPermission() {
        // Check if Android M or higher

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // Show alert dialog to the user saying a separate permission is needed
            // Launch the settings activity if the user prefers
            Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                    Uri.parse("package:" + MainActivity.this.getPackageName()));
            startActivityForResult(intent, ACTION_MANAGE_OVERLAY_PERMISSION_REQUEST_CODE);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == ACTION_MANAGE_OVERLAY_PERMISSION_REQUEST_CODE) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (!Settings.canDrawOverlays(MainActivity.this)) {
                    //
                }
                else
                {
                    // Permission Granted-System will work
                }

            }
        }
    }



}