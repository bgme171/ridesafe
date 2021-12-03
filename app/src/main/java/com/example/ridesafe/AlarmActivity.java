package com.example.ridesafe;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

public class AlarmActivity extends AppCompatActivity {

    TextView tv_alarm_timer, tv_location;
    Button b_cancel;

    CountDownTimer alarm_timer =  new CountDownTimer(10000, 1000) {


        public void onTick(long millisUntilFinished) {
            tv_alarm_timer.setText(String.valueOf((int)millisUntilFinished/1000));
        }

        public void onFinish() {
        }
    };





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm);

        tv_alarm_timer = findViewById(R.id.tv_alarm_timer);
        tv_alarm_timer.setTextSize(50);
        tv_alarm_timer.setTextColor(Color.RED);

        tv_location = findViewById(R.id.tv_location);
        b_cancel=findViewById(R.id.b_cancel);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            Location location = (Location) extras.get("last_location");
            //The key argument here must match that used in the other activity
            tv_location.setText(String.valueOf("Lat: " + location.getLatitude()));
        }


        alarm_timer.start();

        b_cancel.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                finish();
            }
        });



    }
}