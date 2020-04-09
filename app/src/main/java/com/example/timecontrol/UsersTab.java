package com.example.timecontrol;


import android.app.DatePickerDialog;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.shashank.sony.fancytoastlib.FancyToast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;


/**
 * A simple {@link Fragment} subclass.
 */
public class UsersTab extends Fragment implements View.OnClickListener {


    ListView listView;

    SwipeRefreshLayout swipeRefreshLayout;

    private Spinner spinner;

    String URL;

    Button btnEntrada,btnSortida,btnOk,btnReset;

    TextView edtEntrada,edtSortida;

    int E_dia,E_mes,E_any,S_dia,S_mes,S_any;



    public UsersTab() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_users_tab, container, false);

        URL= "http://192.168.1.92/android_app/get_data.php?position=0" ;


        btnEntrada=view.findViewById(R.id.btnEntrada);
        btnSortida=view.findViewById(R.id.btnSortida);
        btnOk=view.findViewById(R.id.btnOk);
        btnReset=view.findViewById(R.id.btnReset);

        edtEntrada=view.findViewById(R.id.edtEntrada);
        edtSortida=view.findViewById(R.id.edtSortida);

        btnEntrada.setOnClickListener(UsersTab.this);
        btnSortida.setOnClickListener(UsersTab.this);
        btnOk.setOnClickListener(UsersTab.this);
        btnReset.setOnClickListener(UsersTab.this);



        listView=view.findViewById(R.id.listView);

        spinner=view.findViewById(R.id.spinner);

        String [] opciones= {"Last Month","Last Week","Last Day"};

        ArrayAdapter<String> adapterspinner= new ArrayAdapter<String>(getContext(),android.R.layout.simple_spinner_item,opciones);

        spinner.setAdapter(adapterspinner);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                URL= "http://192.168.1.92/android_app/get_data.php?position=" + position;
                Call();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
            }

        });

        return view;
    }

    private void Call(){
        GetData(new VolleyCallback(){
            @Override
            public void onSuccess(String result){
                JSON(result);
            }
        });
    }

    public void onResume(){
        super.onResume();
        Call();
    }

    private void JSON(String result){

        ArrayList<HashMap<String, String>> usersList= new ArrayList<>();
        ListAdapter adapter=new SimpleAdapter(getContext(),usersList,R.layout.list_item,new String[]{"Name","Hores","Minuts"},new int[]{R.id.Name,R.id.Hores,R.id.Minuts});
        try {
            JSONObject jsonObj = new JSONObject(result);
            JSONArray users=jsonObj.getJSONArray("users");

            if (users.toString().equals("[]")){
                FancyToast.makeText(getContext(), "NO DATA ON THIS DATE",
                        FancyToast.LENGTH_SHORT,FancyToast.INFO,true).show();
                listView.setAdapter(null);

            }else{

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


                    listView.setAdapter(adapter);

                }
            }



        }catch (JSONException e){
            e.printStackTrace();
        }

    }


    public interface VolleyCallback {

        void onSuccess(String string);
    }
    private void GetData(final VolleyCallback callBack){
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


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btnEntrada:
                final Calendar c= Calendar.getInstance();
                DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(), new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        edtEntrada.setText(dayOfMonth+"/"+(monthOfYear+1)+"/"+year);
                        E_dia=dayOfMonth;
                        E_mes=monthOfYear+1;
                        E_any=year;
                    }
                },E_dia,E_mes,E_any);
                datePickerDialog.show();

                break;
            case R.id.btnSortida:
                final Calendar d= Calendar.getInstance();

                DatePickerDialog datePickerDialog2 = new DatePickerDialog(getContext(), new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        edtSortida.setText(dayOfMonth+"/"+(monthOfYear+1)+"/"+year);
                        S_dia=dayOfMonth;
                        S_mes=monthOfYear+1;
                        S_any=year;
                    }
                },S_dia,S_mes,S_any);
                datePickerDialog2.show();

                break;
            case R.id.btnOk:
                if (edtEntrada.getText().toString().matches("") && edtSortida.getText().toString().matches("")){
                    FancyToast.makeText(getContext(), "Necessita insertar data",
                            FancyToast.LENGTH_SHORT,FancyToast.ERROR,true).show();

                }else{
                    String data_entrada=E_any+"-"+E_mes+"-"+E_dia;
                    String data_sortida=S_any+"-"+S_mes+"-"+S_dia;
                    URL= "http://192.168.1.92/android_app/get_data.php?position=3&data_entrada=" + data_entrada +"&data_sortida=" + data_sortida;
                    Call();

                }
                break;
        }
    }
}
