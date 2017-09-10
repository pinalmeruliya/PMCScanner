package com.parking.scan.helper;

import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AlertDialog;
import android.widget.Toast;

import java.util.Random;

/**
 * Created by harshit on 06/02/17.
 */

public class AndroidUtils {


    public static void showToast(Context context, String msg) {
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
    }

    public static boolean isOnline(Context context) {
        ConnectivityManager connMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            return true;
        } else {

            AndroidUtils.showToast(context, "Internet is not available.");
            return false;
        }
    }

    public static String generateRandom() {
        Random random = new Random();
        return "/" + random.nextInt((999999999 - 100000000) + 1) + 100000000;
    }

    public static void showAlertDialog(final Context context, String title) {
        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);

        alertDialog.setTitle("Alert");
        alertDialog.setMessage(title);

        alertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        alertDialog.show();

    }


}
