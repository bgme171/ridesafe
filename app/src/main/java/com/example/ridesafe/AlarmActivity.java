package com.example.ridesafe;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.provider.ContactsContract;
import android.telephony.SmsManager;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class AlarmActivity extends AppCompatActivity {

    TextView tv_alarm_timer, tv_location;
    Button b_cancel;
    Location location;
    MediaPlayer mediaPlayer;

    CountDownTimer alarm_timer =  new CountDownTimer(30000, 1000) {

        public void onTick(long millisUntilFinished) {
            tv_alarm_timer.setText(String.valueOf((int)millisUntilFinished/1000));
        }

        public void onFinish() {
            send_sms();
            if(mediaPlayer.isPlaying()){
                mediaPlayer.stop();
            }
            mediaPlayer.release();
            mediaPlayer=null;
        }
    };


    public void send_sms(){
        SmsManager smsManager = SmsManager.getDefault();
        ArrayList<String> numbers = get_numbers();

        String msg = "Hola, he sufrido un accidente mientrás conducía motocicleta\nEsta es mi ubicación: "
                      + get_adrress()
                      + "\n"
                      + "Link:"
                      + "\n"
                      + "http://maps.google.com/maps?daddr=" + location.getLatitude() + "," + location.getLongitude()
                      +  "\n"
                      +"(Enviado automáticamente por RideSafe)";


        for (String number: numbers){
            Toast.makeText(AlarmActivity.this, number , Toast.LENGTH_LONG).show();
            ArrayList<String> parts = smsManager.divideMessage(msg);
            smsManager.sendMultipartTextMessage(number.replace("+",""), null, parts, null, null);
        }

    }


    public String get_adrress(){
        Geocoder geocoder= new Geocoder(AlarmActivity.this);
        try{
            List<Address> addresses = geocoder.getFromLocation(location.getLatitude(),location.getLongitude(),1);
            return addresses.get(0).getAddressLine(0);
        }catch (Exception e){
            return "NA";
        }

    }

    public ArrayList<String> get_numbers(){
        SharedPreferences preferences = getSharedPreferences("contacts", Context.MODE_PRIVATE);
        Set<String> contact_list = preferences.getStringSet("contact_list", new HashSet<String>());

        ArrayList<String> numbers = new ArrayList<String>();



        for(String tmp : contact_list){
            String[] data = get_data_from_uri(Uri.parse(tmp));
            numbers.add(data[1]);

        }

        return numbers;
    }


    private String[] get_data_from_uri(Uri uri){

        Cursor cursor = getContentResolver().query(uri,null, null, null, null);

        if(cursor != null && cursor.moveToFirst()){
            int name_idx = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME);
            int number_idx = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);

            String nombre = cursor.getString(name_idx);
            String numero = cursor.getString(number_idx);
            return new String[] {nombre, numero};

        }
        return null;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm);

        // Needed to start activity even when the phone is blocked
        final Window win= getWindow();
        win.addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
        win.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);

        // Needed to start activity on FullScreen
        View decorView = win.getDecorView();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            decorView.setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        }


        mediaPlayer = MediaPlayer.create(this, R.raw.sci_alarm);
        mediaPlayer.setLooping(true);
        mediaPlayer.setVolume(100,100);
        mediaPlayer.start();

        tv_alarm_timer = findViewById(R.id.tv_alarm_timer);
        tv_alarm_timer.setTextSize(50);
        tv_alarm_timer.setTextColor(Color.RED);

        tv_location = findViewById(R.id.tv_location);
        b_cancel=findViewById(R.id.b_cancel);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            location = (Location) extras.get("last_location");
            //The key argument here must match that used in the other activity
            tv_location.setText(String.valueOf("Lat: " + location.getLatitude()));
        }


        alarm_timer.start();

        b_cancel.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if(mediaPlayer.isPlaying()){
                    mediaPlayer.stop();
                }
                mediaPlayer.release();
                mediaPlayer=null;
                finish();

            }
        });



    }
}