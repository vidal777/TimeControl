package com.example.timecontrol;


import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.wifi.WifiManager;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.shashank.sony.fancytoastlib.FancyToast;

import java.text.SimpleDateFormat;
import java.util.Date;

import static android.content.Context.LOCATION_SERVICE;


/**
 * A simple {@link Fragment} subclass.
 */
public class ProfileTab extends Fragment implements View.OnClickListener {
    private FirebaseAuth mAuth;

    public static Button btnFitxar;

    private SharedPreferences pref;

    private SharedPreferences.Editor editor;

    private LocationManager locationManager;

    public ProfileTab() {
        // Required empty public constructor
    }


    @Override
    public void onStart() {
        Log.e("DEBUG", "onStart");
        if (pref.getBoolean("Valor",false)) {
            btnFitxar.setText("Acabar Jornada");
            btnFitxar.setBackgroundResource(R.drawable.custom_button_red);
        }
        super.onStart();
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view=inflater.inflate(R.layout.fragment_profile_tab, container, false);

        btnFitxar= view.findViewById(R.id.btnFitxar);
        mAuth = FirebaseAuth.getInstance();

        btnFitxar.setOnClickListener(ProfileTab.this);
        btnFitxar.setText("FITXAR");


        pref = getActivity().getSharedPreferences("MyPref", 0); //Use save state button
         editor = pref.edit();


        if (pref.getBoolean("Valor",false)) {
            btnFitxar.setText("Acabar Jornada");
            //btnFitxar.setBackgroundColor(R.color.green);
            btnFitxar.setBackgroundResource(R.drawable.custom_button_red);
        }

        locationManager=(LocationManager) getActivity().getSystemService(LOCATION_SERVICE);



        return view;
    }


    private String datetime(){
        SimpleDateFormat ISO_8601_FORMAT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:sss'Z'");

        return ISO_8601_FORMAT.format(new Date());

    }

    private String time(){
        SimpleDateFormat ISO_8601_FORMAT = new SimpleDateFormat("HH:mm:ss");

        return ISO_8601_FORMAT.format(new Date());

    }

    @Override
    public void onClick(View v) {

        if (btnFitxar.getText().toString()=="FITXAR"){
            FirebaseUser user =  mAuth.getCurrentUser();
            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("horarios").child(user
                    .getUid());
            databaseReference.child("datetime_entrance " + datetime()).setValue(datetime());

            btnFitxar.setText("Acabar Jornada");
            //btnFitxar.setBackgroundColor(R.color.green);
            btnFitxar.setBackgroundResource(R.drawable.custom_button_red);

            FancyToast.makeText(getContext(), "GET IN " + time(),
                    FancyToast.LENGTH_SHORT,FancyToast.INFO,true).show();

            API api = new API(getContext());
            api.get_in(user.getUid(),user.getDisplayName());

            editor.putBoolean("Valor",true);

            editor.putString("key_name", "NOFITXAR"); // Storing string

            editor.commit(); // commit changes

        }else{

            editor.putBoolean("Valor",false);

            editor.putString("key_name", "FITXAR"); // Storing string

            editor.commit(); // commit changes


            FirebaseUser user =  mAuth.getCurrentUser();
            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("horarios").child(user
                    .getUid());
            databaseReference.child("datetime_exit " + datetime()).setValue(datetime());

            btnFitxar.setText("FITXAR");
            btnFitxar.setBackgroundResource(R.drawable.custom_button);

            FancyToast.makeText(getContext(), "GET OUT " + time(),
                    FancyToast.LENGTH_SHORT,FancyToast.INFO,true).show();

            API api = new API(getContext());
            api.get_out(user.getUid(),user.getDisplayName());
        }

    }
}
