package com.example.ridesafe;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    TextView txt_accel_x, txt_accel_y, txt_accel_z, txt_fall;

    private SensorManager mSensorManager;
    private Sensor mAccelerometer;
    private float previous_x, previous_y, previous_z;



    private SensorEventListener sensorEventListener = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent event) {
            float x = event.values[0];
            float y = event.values[1];
            float z = event.values[2];

            txt_accel_x.setText("X axis acceleration = "+ (int)x);
            txt_accel_y.setText("Y axis acceleration = "+ (int)y);
            txt_accel_z.setText("z axis acceleration = "+ (int)z);

            double changeX = Math.abs(x - previous_x);
            previous_x = x;
            double changeY = Math.abs(x - previous_x);
            previous_x = x;
            double changeZ = Math.abs(x - previous_x);
            previous_x = x;

            if (changeX > 15.5 | changeY > 15.5 | changeZ > 15.5){
                txt_fall.setText("Fall detected");
                txt_fall.setTextSize(50);
                txt_fall.setTextColor(Color.RED);

            }

        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        txt_accel_x = findViewById(R.id.txt_accel_x);
        txt_accel_y = findViewById(R.id.txt_accel_y);
        txt_accel_z = findViewById(R.id.txt_accel_z);

        txt_fall = findViewById(R.id.txt_alarm);

        mSensorManager = (SensorManager)getSystemService(SENSOR_SERVICE);
        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
    }

    protected void onResume() {
        super.onResume();
        mSensorManager.registerListener(sensorEventListener, mAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);
    }

    protected void onPause() {
        super.onPause();
        mSensorManager.unregisterListener(sensorEventListener);
    }
}