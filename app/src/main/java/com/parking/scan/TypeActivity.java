package com.parking.scan;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import com.parking.scan.helper.AndroidUtils;
import com.parking.scan.webservices.NetworkUtils;
import com.parking.scan.webservices.ParkingScannerAPI;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class TypeActivity extends AppCompatActivity {

    private Button btnEntry;
    private Button btnExit;
    Context context;


    ArrayList<String> parking_names = new ArrayList<String>();
    ArrayList<String> parking_ids = new ArrayList<String>();
    Spinner dynamicSpinner;
    ArrayAdapter<String> adapter;
    public static String parking_id = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_type);
        context = this;
        findViews();

        checkCameraPermission();

        fetchparking();
    }

    private void fetchparking() {


        NetworkUtils.sendVolleyGetRequest(ParkingScannerAPI.getparking, new NetworkUtils.VolleyCallbackString() {
            @Override
            public void onSuccess(String result) {

                parking_ids.clear();
                parking_names.clear();
                try {
                    JSONObject json = new JSONObject(result);


                    if (json.getString("success").equals("1")) {


                        JSONArray feedArray = json.getJSONArray("parkings");
                        for (int i = 0; i < feedArray.length(); i++) {
                            JSONObject feedObj = (JSONObject) feedArray.get(i);


                            // add 20 radio buttons to the group


                            parking_names.add(feedObj.getString("S_name"));
                            parking_ids.add(feedObj.getString("Parking_id"));

                            adapter.notifyDataSetChanged();
                        }


                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(String error) {
                AndroidUtils.showToast(context, error);

            }
        }, context);
    }

    private void findViews() {
        btnEntry = (Button) findViewById(R.id.btn_entry);
        btnExit = (Button) findViewById(R.id.btn_exit);

        btnEntry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i = new Intent(context, MainActivity.class);
                i.putExtra("type", "entry");
                startActivity(i);

            }
        });
        btnExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i = new Intent(context, MainActivity.class);
                i.putExtra("type", "exit");
                startActivity(i);
            }
        });


        dynamicSpinner = (Spinner) findViewById(R.id.dynamic_spinner);


        adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, parking_names);

        dynamicSpinner.setAdapter(adapter);

        dynamicSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {
                Log.v("item", parking_ids.get(position));

                parking_id = parking_ids.get(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // TODO Auto-generated method stub
            }
        });
    }


    public void checkCameraPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA)) {

            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, 1);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1: {

                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                } else {

                    ActivityCompat.requestPermissions(this,
                            new String[]{Manifest.permission.CAMERA}, 1);
                }
                return;
            }

        }
    }


}
