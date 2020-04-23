package com.example.timecontrol;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
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

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;


public class AdminTab extends Fragment implements View.OnClickListener {


    ListView listView;

    SwipeRefreshLayout swipeRefreshLayout;

    private Spinner spinner;

    String URL;

    Button btnEntrada,btnSortida;
    ImageButton btnOk,btnReset;

    TextView edtEntrada,edtSortida;

    int E_dia,E_mes,E_any,S_dia,S_mes,S_any;

    ArrayAdapter<String> adapterspinner;

    ArrayAdapter<String> adapterspinner2;

    FloatingActionButton location;

    ArrayList<HashMap<String, String>> usersList= new ArrayList<>();

    String type_user;



    public AdminTab() {
        // Required empty public constructor
    }

    private String prova(){  //Know type_user ( Problems: need internet connection and you have to wait until ondatachange.)

        FirebaseUser user =  FirebaseAuth.getInstance().getCurrentUser();

        DatabaseReference mref = FirebaseDatabase.getInstance().getReference("usuarios").child(user.getUid().toString()).child("type_user");
        mref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                type_user = dataSnapshot.getValue(String.class);
                Log.i("TYPE_USER",type_user);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        return type_user;

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_admin_tab, container, false);

        Log.i("PROVA",prova()+ "");

        URL= "http://192.168.1.92/android_app/get_data_admin.php?position=0" ;


        btnEntrada=view.findViewById(R.id.btnEntrada);
        btnSortida=view.findViewById(R.id.btnSortida);
        btnOk=view.findViewById(R.id.btnOk);
        btnReset=view.findViewById(R.id.btnReset);

        edtEntrada=view.findViewById(R.id.edtEntrada);
        edtSortida=view.findViewById(R.id.edtSortida);

        btnEntrada.setOnClickListener(AdminTab.this);
        btnSortida.setOnClickListener(AdminTab.this);
        btnOk.setOnClickListener(AdminTab.this);
        btnReset.setOnClickListener(AdminTab.this);
        location=view.findViewById(R.id.location);



        listView=view.findViewById(R.id.listView);

        spinner=view.findViewById(R.id.spinner);

        String [] opciones= {"Last Month","Last Week","Last Day"};

        adapterspinner= new ArrayAdapter<String>(getContext(),android.R.layout.simple_spinner_item,opciones);

        spinner.setAdapter(adapterspinner);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                URL= "http://192.168.1.92/android_app/get_data_admin.php?position=" + position;
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
                searchLocation();
            }
        });

        return view;
    }
    private void searchLocation(){
        final AlertDialog.Builder builder= new AlertDialog.Builder(getActivity());
        builder.setTitle("Select User and Date");
        builder.setMessage("prova")
                .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Toast.makeText(getActivity(),"You clicked Yes !!",Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("NO", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Toast.makeText(getActivity(),"You clicked No !!",Toast.LENGTH_SHORT).show();
                    }
                });
    /*
        LinearLayout linearLayout= new LinearLayout(getActivity());
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        linearLayout.setPadding(10,10,10,10);


        final DatePicker datePicker=new DatePicker(getActivity());


        linearLayout.addView(datePicker);


        builder.setView(linearLayout);

     */

        builder.create().show();
    }

    private void Call(){
        GetData(new AdminTab.VolleyCallback(){
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
                    URL= "http://192.168.1.92/android_app/get_data_admin.php?position=3&data_entrada=" + data_entrada +"&data_sortida=" + data_sortida;
                    Call();

                }
                break;
            case R.id.btnReset:
                spinner.setSelection(0);
                edtSortida.setText("");
                edtEntrada.setText("");
                URL= "http://192.168.1.92/android_app/get_data_admin.php?position=0";
                Call();

        }
    }
}
