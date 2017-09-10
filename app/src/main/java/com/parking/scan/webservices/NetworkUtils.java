/*
* AnyPlace: A free and open Indoor Navigation Service with superb accuracy!
*
* Anyplace is a first-of-a-kind indoor information service offering GPS-less
* localization, navigation and search inside buildings using ordinary smartphones.
*
* Author(s): Lambros Petrou, Timotheos Constambeys
* 
* Supervisor: Demetrios Zeinalipour-Yazti
*
* URL: http://anyplace.cs.ucy.ac.cy
* Contact: anyplace@cs.ucy.ac.cy
*
* Copyright (c) 2015, Data Management Systems Lab (DMSL), University of Cyprus.
* All rights reserved.
*
* Permission is hereby granted, free of charge, to any person obtaining a copy of
* this software and associated documentation files (the "Software"), to deal in the
* Software without restriction, including without limitation the rights to use, copy,
* modify, merge, publish, distribute, sublicense, and/or sell copies of the Software,
* and to permit persons to whom the Software is furnished to do so, subject to the
* following conditions:
*
* The above copyright notice and this permission notice shall be included in all
* copies or substantial portions of the Software.
*
* THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS
* OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
* FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
* AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
* LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
* FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER
* DEALINGS IN THE SOFTWARE.
*
*/

package com.parking.scan.webservices;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.parking.scan.helper.AndroidUtils;

import java.util.HashMap;
import java.util.Map;


/**
 * Network helpers for usual actions.
 *
 * @author Lambros Petrou
 */
public class NetworkUtils {


    public interface VolleyCallbackString {
        void onSuccess(String result);

        void onError(String error);
    }

    public interface VolleyCallbackImage {
        void onSuccess(Bitmap result);

        void onError(String error);
    }


    public static void sendVolleyGetRequest(final String url, final VolleyCallbackString callback, final Context context) {


        if (!AndroidUtils.isOnline(context)) {

            return;
        }
        // Instantiate the RequestQueue.


        RequestQueue queue = Volley.newRequestQueue(context);

        queue.getCache().clear();
        queue.getCache().remove(url);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url + AndroidUtils.generateRandom(),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        callback.onSuccess(response);

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                callback.onError(error.toString());

            }
        });
        // Add the request to the RequestQueue.
        //stringRequest.setShouldCache(false);
        queue.add(stringRequest);

    }

    public static void sendVolleyPostRequest(final String url, final VolleyCallbackString callback, final Context context, final HashMap<String, String> params) {


        // Instantiate the RequestQueue.
        if (!AndroidUtils.isOnline(context)) {

            return;
        }

        Log.d("VOLLEY PAYMEBNT", "SENDING REQUEST");
        RequestQueue queue = Volley.newRequestQueue(context);
        queue.getCache().clear();
        StringRequest postRequest = new StringRequest(Request.Method.POST, url + AndroidUtils.generateRandom(),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // response
                        callback.onSuccess(response);


                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // error
                        callback.onError(error.toString());

                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() {

                return params;
            }
        };
//        postRequest.setShouldCache(false);


        postRequest.setRetryPolicy(new DefaultRetryPolicy(
                0,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(postRequest);
    }


}
