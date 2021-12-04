package com.example.ridesafe;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

public class ContactsActivity extends AppCompatActivity {

    EditText txt_1, txt_2;
    Button b_save;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacts);


        ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.READ_CONTACTS}, PackageManager.PERMISSION_GRANTED);

        txt_1 = (EditText) findViewById(R.id.txt_1);
        txt_2 = (EditText) findViewById(R.id.txt_2);

        SharedPreferences preferences = getSharedPreferences("contacts", Context.MODE_PRIVATE);
        //txt_1.setText(preferences.getString("uri1", ""));
        //txt_2.setText(preferences.getString("uri2", ""));

        b_save = findViewById(R.id.b_guardar);


        b_save.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                save();
            }
        });

    }

    public void save(){
        SharedPreferences preferences = getSharedPreferences("contacts", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("uri1", txt_1.getText().toString());
        editor.putString("uri2", txt_2.getText().toString());
        editor.commit();
        finish();
    }

    public void search(View view){

        try {
            Cursor cursor = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                     new String[]{ContactsContract.CommonDataKinds.Phone.NUMBER,
                                  ContactsContract.CommonDataKinds.Phone.TYPE},
                    "DISPLAY_NAME = '" + txt_1.getText().toString() + "'", null, null);

            cursor.moveToFirst();
            txt_2.setText(cursor.getString(0));

        }catch (Exception e){
            e.printStackTrace();
        }

    }

}