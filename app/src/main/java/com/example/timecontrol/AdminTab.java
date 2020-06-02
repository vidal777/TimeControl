package com.example.timecontrol;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.icu.util.ULocale;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.shashank.sony.fancytoastlib.FancyToast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;


public class AdminTab extends Fragment implements View.OnClickListener {

    private FirebaseAuth firebaseAuth;
    private FirebaseUser user;
    private String uidAdmin;


    private ListView listView;


    private Spinner spinner;

    private String URL;

    private Button btnEntrada,btnSortida;
    private ImageButton btnOk,btnReset;


    private int E_dia,E_mes,E_any,S_dia,S_mes,S_any;

    private ArrayAdapter<String> adapterspinner;


    private ArrayList<HashMap<String, String>> usersList= new ArrayList<>();


    private ArrayList<HashMap<String, String>> userList = new ArrayList<>();

    private int Position=0;
    private int day,month,year;

    private String data_entrada,data_sortida;


    public AdminTab() {
        // Required empty public constructor
    }




    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_admin_tab, container, false);

        firebaseAuth=FirebaseAuth.getInstance();
        user=firebaseAuth.getCurrentUser();
        uidAdmin=user.getUid();


        URL= "http://timecontrol.ddns.net/android_app/get_data_admin.php?position=0&uidAdmin=" + uidAdmin ;


        btnEntrada=view.findViewById(R.id.btnEntrada);
        btnSortida=view.findViewById(R.id.btnSortida);
        btnOk=view.findViewById(R.id.btnOk);
        btnReset=view.findViewById(R.id.btnReset);


        btnEntrada.setOnClickListener(AdminTab.this);
        btnSortida.setOnClickListener(AdminTab.this);
        btnOk.setOnClickListener(AdminTab.this);
        btnReset.setOnClickListener(AdminTab.this);



        listView=view.findViewById(R.id.listView);

        spinner=view.findViewById(R.id.spinner);

        String [] opciones= {"Last Month","Last Week","Last Day"};

        adapterspinner= new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, opciones);

        spinner.setAdapter(adapterspinner);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                Position=position;
                URL= "http://timecontrol.ddns.net/android_app/get_data_admin.php?position=" + position + "&uidAdmin=" + uidAdmin;
                Call();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
            }

        });


        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Get the selected item text from ListView
                HashMap<String, String> user;
                user=usersList.get(position);
                Collection<String> values = user.values();
                ArrayList<String> listOfValues = new ArrayList<>(values);
                String uid=listOfValues.get(0);


                //searchLocation();
                if (Position==3){
                    URL = "http://timecontrol.ddns.net/android_app/get_data_user.php?position=3&data_entrada=" + data_entrada + "&data_sortida=" + data_sortida + "&uid=" + uid;

                }else{

                    URL = "http://timecontrol.ddns.net/android_app/get_data_user.php?position=" + Position + "&uid=" + uid; //Falla peruqe tenim que passar la data
                }
                Call_user();
            }
        });

        return view;
    }
    private void Create_view(){
        AlertDialog.Builder builderSingle = new AlertDialog.Builder(getActivity());
        builderSingle.setTitle("Select One Name:");

        ListAdapter adapter = new SimpleAdapter(getContext(), userList, R.layout.list_item_user, new String[]{"get_in", "get_out"}, new int[]{R.id.get_in, R.id.get_out});

        builderSingle.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        builderSingle.setAdapter(adapter, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                HashMap<String, String> user;
                user=userList.get(which);
                Collection<String> values = user.values();
                ArrayList<String> listOfValues = new ArrayList<>(values);
                final String address_in=listOfValues.get(1);
                final String address_out=listOfValues.get(2);
                //String strName = arrayAdapter.getItem(which);
                AlertDialog.Builder builderInner = new AlertDialog.Builder(getActivity());
                builderInner.setTitle("Your Selected Item is");
                builderInner.setPositiveButton("Address Out", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog,int which) {
                        searchLocation(address_out);
                    }
                });
                builderInner.setNegativeButton("Address In", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog,int which) {
                        searchLocation(address_in);
                    }
                });
                AlertDialog alert = builderInner.create();
                alert.setCanceledOnTouchOutside(true);
                alert.show();
            }
        });
        AlertDialog alert = builderSingle.create();
        alert.setCanceledOnTouchOutside(true);
        alert.show();
    }

    private LatLng getLocationFromAddress(Context context, String strAddress) {

        Geocoder coder = new Geocoder(context);
        List<Address> address;
        LatLng p1 = null;

        try {
            // May throw an IOException
            address = coder.getFromLocationName(strAddress, 5);
            if (address == null) {
                return null;
            }

            Address location = address.get(0);
            p1 = new LatLng(location.getLatitude(), location.getLongitude() );

        } catch (IOException ex) {

            ex.printStackTrace();
        }

        return p1;
    }

    private void searchLocation(String address) {

        LatLng latLng=getLocationFromAddress(getActivity(),address);

        Intent intent;
        intent = new Intent(getActivity(),MapsActivity.class);
        intent.putExtra("LatLng",latLng);
        startActivity(intent);

    }

    private void Call(){
        GetData(new AdminTab.VolleyCallback(){
            @Override
            public void onSuccess(String result){
                JSON(result);
            }
        });
    }

    private void Call_user(){
        GetData(new AdminTab.VolleyCallback(){
            @Override
            public void onSuccess(String result){
                JSON_user(result);
            }
        });
    }



    private void JSON_user(String result) {
        Log.i("RESULTS",result);
        userList.clear();
        try {
            JSONObject jsonObj = new JSONObject(result);
            JSONArray users = jsonObj.getJSONArray("users");

            if (users.toString().equals("[]")) {
                FancyToast.makeText(getContext(), "NO DATA ON THIS DATE",
                        FancyToast.LENGTH_SHORT, FancyToast.INFO, true).show();
                //listView.setAdapter(null);

            } else {

                //traversing through all the object
                for (int i = 0; i < users.length(); i++) {
                    JSONObject c = users.getJSONObject(i);
                    String get_in = c.getString("get_in");
                    String get_out = c.getString("get_out");
                    String address_in = c.getString("address_in");
                    String address_out = c.getString("address_out");

                    //getting product object from json array
                    HashMap<String, String> user = new HashMap<>();

                    user.put("get_in", get_in);
                    user.put("get_out", get_out);
                    user.put("address_in",address_in);
                    user.put("address_out",address_out);

                    userList.add(user);

                }
                    Create_view();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    public void onResume(){
        super.onResume();
    }

    private void JSON(String result){
        usersList.clear();
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
                    String uid=c.getString("uid");
                    String name=c.getString("name");
                    String hour=c.getString("hores");
                    String minuts=c.getString("minuts");

                    //getting product object from json array
                    HashMap<String, String> user = new HashMap<>();
                    user.put("Uid",uid);
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

    private void GetData(final AdminTab.VolleyCallback callBack){
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

        final Calendar c = Calendar.getInstance();
        year=c.get(Calendar.YEAR);
        month=c.get(Calendar.MONTH);
        day=c.get(Calendar.DAY_OF_MONTH);

        switch (v.getId()){
            case R.id.btnEntrada:
                DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(), new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        btnEntrada.setText(dayOfMonth+"/"+(monthOfYear+1)+"/"+year);
                        E_dia=dayOfMonth;
                        E_mes=monthOfYear+1;
                        E_any=year;
                    }
                },year,month,day);
                datePickerDialog.show();

                break;
            case R.id.btnSortida:
                DatePickerDialog datePickerDialog2 = new DatePickerDialog(getContext(), new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        btnSortida.setText(dayOfMonth+"/"+(monthOfYear+1)+"/"+year);
                        S_dia=dayOfMonth;
                        S_mes=monthOfYear+1;
                        S_any=year;
                    }
                },year,month,day);
                datePickerDialog2.show();

                break;
            case R.id.btnOk:
                if (btnEntrada.getText().toString().matches("") && btnSortida.getText().toString().matches("")){
                    FancyToast.makeText(getContext(), "Necessita insertar data",
                            FancyToast.LENGTH_SHORT,FancyToast.ERROR,true).show();

                }else{
                    Position=3;
                    data_entrada=E_any+"-"+E_mes+"-"+E_dia;
                    data_sortida=S_any+"-"+S_mes+"-"+S_dia;
                    URL= "http://timecontrol.ddns.net/android_app/get_data_admin.php?position=3&data_entrada=" + data_entrada +"&data_sortida=" + data_sortida + "&uidAdmin=" + uidAdmin;
                    Call();

                }
                break;
            case R.id.btnReset:
                spinner.setSelection(0);
                btnSortida.setText("");
                btnEntrada.setText("");
                URL= "http://timecontrol.ddns.net/android_app/get_data_admin.php?position=0&uidAdmin=" + uidAdmin ;
                Call();

        }
    }
}
