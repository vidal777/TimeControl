package com.example.timecontrol;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;



public class API{

    Context context;
    RequestQueue requestQueue;


    public API(Context context) {
        this.context = context;
        requestQueue = Volley.newRequestQueue(context);
    }

    public void register_user(final String uid,final String type,final String name,final String email){
        String URL= "http://192.168.1.92/android_app/register_user.php";


        StringRequest stringRequest= new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Toast.makeText(context, "Exit", Toast.LENGTH_SHORT).show();

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(context, "FAIL" + error.getMessage(), Toast.LENGTH_SHORT).show();

                Log.i("EROOOOOR",error.getMessage());

            }
        })
        {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> parametros = new HashMap<String, String>();
                parametros.put("data", datetime());
                parametros.put("uid", uid);
                parametros.put("type", type);
                parametros.put("name", name);
                parametros.put("email", email);
                return parametros;
            }
        };

        requestQueue.add(stringRequest);
    }

    public void get_in(final String uid,final String name){
        String URL= "http://192.168.1.92/android_app/get_in.php";


        StringRequest stringRequest= new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.e("DEBUG","EXIT");

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("DEBUG","FAIL");

            }
        })
        {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> parametros = new HashMap<String, String>();
                parametros.put("data", datetime());
                parametros.put("uid", uid);
                parametros.put("get_in", timestamp());
                parametros.put("name", name);
                //parametros.put("get_out", timestamp());

                return parametros;
            }
        };

        requestQueue.add(stringRequest);
    }

    public void get_out(final String uid,final String name){
        String URL= "http://192.168.1.92/android_app/get_out.php";


        StringRequest stringRequest= new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.e("DEBUG","EXIT");

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("DEBUG","FAIL");
            }
        })
        {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> parametros = new HashMap<String, String>();
                parametros.put("data", datetime());
                parametros.put("uid", uid);
                parametros.put("name", name);
                parametros.put("get_out", timestamp());

                return parametros;
            }
        };

        requestQueue.add(stringRequest);
    }



    private String datetime(){
        SimpleDateFormat ISO_8601_FORMAT = new SimpleDateFormat("yyyy-MM-dd");

        return ISO_8601_FORMAT.format(new Date());

    }

    private  String timestamp(){
        return String.valueOf(TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis()));
    }
}
