package com.example.timecontrol;


import android.os.AsyncTask;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;


/**
 * A simple {@link Fragment} subclass.
 */
public class UsersTab extends Fragment {

    ListView listView;


    public UsersTab() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_users_tab, container, false);

        listView=view.findViewById(R.id.listView);

        return view;
    }

    public void onResume(){
        super.onResume();

        GetData(new VolleyCallback(){
            @Override
            public void onSuccess(String result){
                JSON(result);
            }
        });
    }

    private void JSON(String result){

        ArrayList<HashMap<String, String>> usersList= new ArrayList<>();
        try {
            JSONObject jsonObj = new JSONObject(result);
            JSONArray users=jsonObj.getJSONArray("users");

            //traversing through all the object
            for (int i = 0; i < users.length(); i++) {
                JSONObject c = users.getJSONObject(i);
                String name=c.getString("name");
                String hour=c.getString("hores");
                String minuts=c.getString("minuts");

                //getting product object from json array
                HashMap<String, String> user = new HashMap<>();
                user.put("Hores",hour);
                user.put("Name",name);
                user.put("Minuts",minuts);

                usersList.add(user);

                Log.i("USSSER",usersList.toString());
                ListAdapter adapter=new SimpleAdapter(getContext(),usersList,R.layout.list_item,new String[]{"Name","Hores","Minuts"},new int[]{R.id.Name,R.id.Hores,R.id.Minuts});
               // ArrayAdapter<HashMap<String, String>> adapter = new ArrayAdapter<HashMap<String, String>>(getContext(),R.layout.fragment_users_tab,usersList);
                listView.setAdapter(adapter);

            }

        }catch (JSONException e){
            e.printStackTrace();
        }

    }




    public interface VolleyCallback {
        void onSuccess(String string);
    }

    private void GetData(final VolleyCallback callBack){
        String URL= "http://192.168.1.56/android_app/get_data.php";
        final String names[];

        StringRequest stringRequest = new StringRequest(Request.Method.GET, URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        callBack.onSuccess(response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                });

        Volley.newRequestQueue(getContext()).add(stringRequest);

    }

}
