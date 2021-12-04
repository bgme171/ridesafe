package com.example.ridesafe;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class ContactsActivity extends AppCompatActivity {

    EditText txt_1, txt_2;
    Button b_save;
    ListView lv_contacts;
    Set<String> contact_list;
    Uri tmp_uri;

    ArrayList<String> nombres;
    ArrayList<String> uris;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacts);


        ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.READ_CONTACTS}, PackageManager.PERMISSION_GRANTED);

        txt_1 = (EditText) findViewById(R.id.txt_1);
        txt_2 = (EditText) findViewById(R.id.txt_2);

        tmp_uri = null;
        lv_contacts = (ListView) findViewById(R.id.lv_contacts);

        list_view_refresh();
        lv_contacts.setOnItemClickListener(new AdapterView.OnItemClickListener() {
               @Override
               public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                   new AlertDialog.Builder(ContactsActivity.this)
                        .setTitle("Eliminar contacto")
                        .setMessage("El contacto será eliminado de tu lista de emergencias")
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                    delete(position);
                            }
                        })
                        .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        })
                        .show();
                                               }
                                           }
        );


                b_save = findViewById(R.id.b_guardar);



    }

    public void save(View view){

        if(tmp_uri!= null){
            SharedPreferences preferences = getSharedPreferences("contacts", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = preferences.edit();

            Set<String> tmp_contactList = new HashSet<String>(preferences.getStringSet("contact_list", new HashSet<String>()));
            tmp_contactList.add(tmp_uri.toString());
            editor.putStringSet("contact_list", tmp_contactList);
            editor.commit();
            tmp_uri=null;
            list_view_refresh();
        }

    }


    public void list_view_refresh(){
        SharedPreferences preferences = getSharedPreferences("contacts", Context.MODE_PRIVATE);
        contact_list = preferences.getStringSet("contact_list", new HashSet<String>());

        nombres = new ArrayList<>();
        uris = new ArrayList<>();
        if(!contact_list.isEmpty()){
            for(String tmp : contact_list){
                String[] data = get_data_from_uri(Uri.parse(tmp));
                nombres.add(data[0]);
                uris.add(tmp);
            }
            //txt_1.setText(String.valueOf(contact_list.size()));
        }else{
            nombres.add("Sin contactos");
        }
        lv_contacts.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, nombres));
    }

    public void delete(int position){


            SharedPreferences preferences = getSharedPreferences("contacts", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = preferences.edit();
            Set<String> tmp_contactList = new HashSet<String>(preferences.getStringSet("contact_list", new HashSet<String>()));
            tmp_contactList.remove(uris.get(position));
            editor.putStringSet("contact_list", tmp_contactList);
            editor.commit();
            list_view_refresh();

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

    public void buscar(View view){
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType(ContactsContract.CommonDataKinds.Phone.CONTENT_TYPE);

        startActivityForResult(intent, 1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == 1 && resultCode == RESULT_OK){

            Uri uri = data.getData();
            String[] info = get_data_from_uri(uri);
            txt_1.setText(info[0]);
            txt_2.setText(info[1]);
            tmp_uri = uri;
        }


    }


    public String[] get_data_from_uri(Uri uri){

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
}