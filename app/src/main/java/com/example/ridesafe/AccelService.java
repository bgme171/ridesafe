package com.example.ridesafe;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.os.CountDownTimer;
import android.os.IBinder;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;

public class AccelService extends Service {

    public static final int DEFAULT_UPDATE_INTERVAL = 1000;
    public static final int FAST_UPDATE_INTERVAL = 0;

    Location previous_location;
    float distance;

    boolean on_fall;
    boolean first_location;

    private SensorManager mSensorManager;
    private Sensor mAccelerometer;
    private float previous_x, previous_y, previous_z;

    // Location request is a config file for all settings related to FusedLocationProviderClient
    LocationRequest locationRequest;

    LocationCallback locationCallback;

    // Google's API for location services.
    FusedLocationProviderClient fusedLocationProviderClient;


    CountDownTimer timer = new CountDownTimer(10000, 1000) {

        public void onTick(long millisUntilFinished) {

        }

        public void onFinish() {
            stopLocationUpdates();
            on_fall = false;
            distance = 9999f;
            first_location = true;
        }
    };

    private SensorEventListener sensorEventListener = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent event) {

            float x = event.values[0];
            float y = event.values[1];
            float z = event.values[2];

            float changeX = Math.abs(x - previous_x);
            previous_x = x;
            float changeY = Math.abs(y - previous_y);
            previous_y = y;
            float changeZ = Math.abs(z - previous_z);
            previous_z = z;

            if (on_fall) {
                return;
            }

            if (changeX > 20 || changeY > 20 || changeZ > 20) {
                // FIXME: review why unregister isn't working fine
                // it looks like old values are used
                //mSensorManager.unregisterListener(this);
                on_fall = true;
                timer.start();
                startLocationUpdates();
            }

        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {

        }
    }; //end  SensorEventListener


    public void onCreate() {
        super.onCreate();

        on_fall = false;
        first_location = true;
        distance = 9999f;
        previous_x = 0;
        previous_y = 0;
        previous_z = 0;


        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);


        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(AccelService.this);

        locationRequest = LocationRequest.create()
                .setInterval(DEFAULT_UPDATE_INTERVAL)
                .setFastestInterval(FAST_UPDATE_INTERVAL)
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(@NonNull LocationResult locationResult) {
                super.onLocationResult(locationResult);

                Location location = locationResult.getLastLocation();
                updateUIValues(location);
            }
        };

    } //end onCreate

    private void stopLocationUpdates() {
        fusedLocationProviderClient.removeLocationUpdates(locationCallback);
    } //end stopLocationUpdates

    private void startLocationUpdates() {
        fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, null);
    } //end startLocationUpdates

    private void updateUIValues(Location location) {

        if(first_location){
            previous_location=location;
            first_location=false;

            return;
        }else{
            distance = location.distanceTo(previous_location);
        }

        if(distance < location.getAccuracy()){
            stopLocationUpdates();
            timer.cancel();

            Intent intent = new Intent(AccelService.this, AlarmActivity.class);
            intent.putExtra("last_location", location);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);

            stopForeground(true);
            stopSelf();
        }

    } //end updateUIValues

    public int onStartCommand(Intent intent, int flags, int startId){
        Intent main_activity = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, main_activity, 0);

        Notification notification = new NotificationCompat.Builder(AccelService.this, "256")
                .setSmallIcon(R.drawable.common_google_signin_btn_icon_dark)
                .setContentTitle("RideSafe está activo")
                .setContentIntent(pendingIntent)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT).build();

        startForeground(1,notification);

        mSensorManager.registerListener(sensorEventListener, mAccelerometer, SensorManager.SENSOR_DELAY_GAME);
        return START_STICKY;
    } //end onStartCommand

    public void onDestroy(){
        super.onDestroy();
        timer.cancel();
        mSensorManager.unregisterListener(sensorEventListener);
        stopLocationUpdates();
        stopForeground(true);
        stopSelf();
    } //end onDestroy

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
