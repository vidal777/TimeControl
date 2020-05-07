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
import com.shashank.sony.fancytoastlib.FancyToast;

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
        String URL= "http://192.168.1.71/android_app/register_user.php";


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
                parametros.put("type", type);
                parametros.put("name", name);
                parametros.put("email", email);
                return parametros;
            }
        };

        requestQueue.add(stringRequest);
    }

    public void get_in(final String uid, final String name, final String address){
        Log.i("ADDRESS", address + " ");
        String URL= "http://192.168.1.71/android_app/get_in.php";


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
                parametros.put("address_in",address);
                return parametros;
            }
        };

        requestQueue.add(stringRequest);
    }

    public void get_out(final String uid,final String name,final String address){
        String URL= "http://192.168.1.71/android_app/get_out.php";


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
                parametros.put("address_out",address);

                return parametros;
            }
        };

        requestQueue.add(stringRequest);
    }

    public void change_data(final String uid,final String name){
        String URL= "http://192.168.1.71/android_app/change_data.php";


        StringRequest stringRequest= new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                FancyToast.makeText(context, "Updated",
                        FancyToast.LENGTH_SHORT,FancyToast.SUCCESS,true).show();

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                FancyToast.makeText(context, "Fail Updated",
                        FancyToast.LENGTH_SHORT,FancyToast.ERROR,true).show();
            }
        })
        {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> parametros = new HashMap<String, String>();
                parametros.put("uid", uid);
                parametros.put("name", name);
                return parametros;
            }
        };

        requestQueue.add(stringRequest);

    }

    public void set_expense(final String uid,final String name,final String data,final int price,final String comments,final String concept,final String id){
        String URL= "http://192.168.1.71/android_app/set_expense.php";


        StringRequest stringRequest= new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                FancyToast.makeText(context, "Expense Updated",
                        FancyToast.LENGTH_SHORT,FancyToast.SUCCESS,true).show();

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                FancyToast.makeText(context, "Fail Update Expense",
                        FancyToast.LENGTH_SHORT,FancyToast.ERROR,true).show();
            }
        })
        {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> parametros = new HashMap<String, String>();
                parametros.put("uid", uid);
                parametros.put("name", name);
                parametros.put("data", data);
                parametros.put("price", String.valueOf(price));
                parametros.put("comments", comments);
                parametros.put("concept", concept);
                parametros.put("idPhoto",id);

                return parametros;
            }
        };

        requestQueue.add(stringRequest);

    }

    public void set_company(final String name,final String nameCompany,final String CIF,final String numberWorkers,final String email){
        String URL= "http://192.168.1.71/android_app/set_company.php";


        StringRequest stringRequest= new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                FancyToast.makeText(context, "Company Updated",
                        FancyToast.LENGTH_SHORT,FancyToast.SUCCESS,true).show();

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                FancyToast.makeText(context, "Fail Update Company",
                        FancyToast.LENGTH_SHORT,FancyToast.ERROR,true).show();
            }
        })
        {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> parametros = new HashMap<String, String>();
                parametros.put("name", name);
                parametros.put("nameCompany", nameCompany);
                parametros.put("CIF", CIF);
                parametros.put("numberWorkers", numberWorkers);
                parametros.put("email", email);

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
