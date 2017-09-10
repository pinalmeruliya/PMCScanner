package com.parking.scan;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.PointF;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.dlazaro66.qrcodereaderview.QRCodeReaderView;
import com.parking.scan.helper.AndroidUtils;
import com.parking.scan.webservices.NetworkUtils;
import com.parking.scan.webservices.ParkingScannerAPI;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class MainActivity extends AppCompatActivity implements QRCodeReaderView.OnQRCodeReadListener {


    private TextView resultTextView;
    private QRCodeReaderView mydecoderview;
    Context context;
    String result = "";
    String type = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        context = this;
        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        if (extras != null) {
            type = extras.getString("type", "");
        }

        mydecoderview = (QRCodeReaderView) findViewById(R.id.qrdecoderview);
        mydecoderview.setOnQRCodeReadListener(this);

        // Use this function to enable/disable decoding
        mydecoderview.setQRDecodingEnabled(true);

        // Use this function to change the autofocus interval (default is 5 secs)
        mydecoderview.setAutofocusInterval(2000L);

        // Use this function to enable/disable Torch
        mydecoderview.setTorchEnabled(true);

        // Use this function to set front camera preview
        mydecoderview.setFrontCamera();

        // Use this function to set back camera preview
        mydecoderview.setBackCamera();
    }

    @Override
    public void onQRCodeRead(String text, PointF[] points) {

        if (!text.equals(result)) {


            Log.d("DETAILS", text);

            try {
                JSONObject json = new JSONObject(text);


                final String parking_id ="";
                final String booking_id = json.getString("booking_id");
                final String qrcode_random = json.getString("qrcode_random");



                if(type.equals("entry")) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setMessage("Enter Vehicle Type: ")
                            .setCancelable(false)
                            .setPositiveButton("Two Wheeler", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {

                                    sendRequest(parking_id, booking_id, "T", qrcode_random);
                                    dialog.cancel();
                                }
                            })
                            .setNegativeButton("Four Wheeler", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    sendRequest(parking_id, booking_id, "F", qrcode_random);
                                    dialog.cancel();
                                }
                            });
                    AlertDialog alert = builder.create();
                    alert.show();
                }
                else
                {
                    sendRequest(parking_id, booking_id, "F", qrcode_random);
                }


            } catch (JSONException e) {
                e.printStackTrace();
            }


            result = text;
        }
    }

    ProgressDialog progressDialog;

    private void sendRequest(String parking_id, String booking_id, String vehicle_type, String qrcode_random) {

        String url = "";
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Processing...");
        progressDialog.setCancelable(false);
        progressDialog.show();
        if (type.equals("entry")) {
            url = ParkingScannerAPI.entry;
        } else {
            url = ParkingScannerAPI.exit;
        }
        HashMap<String, String> params = new HashMap<>();
        params.put("parking_id", TypeActivity.parking_id);
        params.put("booking_id", booking_id);
        params.put("vehicle_type", vehicle_type);
        params.put("qrcode_random", qrcode_random);

        NetworkUtils.sendVolleyPostRequest(url, new NetworkUtils.VolleyCallbackString() {
            @Override
            public void onSuccess(String result) {
                if (progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }

                try {
                    JSONObject json = new JSONObject(result);
                    if (json.getString("success").equals("1")) {
                        AndroidUtils.showAlertDialog(context, json.getString("msg"));
                    } else if (json.getString("success").equals("0")) {
                        AndroidUtils.showAlertDialog(context, json.getString("error"));
                        result="";
                    }
                } catch (JSONException e) {

                    result = "";
                    e.printStackTrace();
                }


            }

            @Override
            public void onError(String error) {

                if (progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
                result = "";
                AndroidUtils.showAlertDialog(context, "Something went wrong !! Please try again.");
            }
        }, this, params);


    }


    @Override
    protected void onResume() {
        super.onResume();
        mydecoderview.startCamera();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mydecoderview.stopCamera();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.reload:  // it is going to refer the search id name in main.xml

                //add your code here
                result = "";

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
