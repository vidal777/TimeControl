package com.example.timecontrol;


import android.Manifest;
import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;

import androidx.core.app.ActivityCompat;
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
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.shashank.sony.fancytoastlib.FancyToast;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import static android.content.Context.LOCATION_SERVICE;


/**
 * A simple {@link Fragment} subclass.
 */
public class ProfileTab extends Fragment implements View.OnClickListener {
    private FirebaseAuth mAuth;

    public static Button btnFitxar;

    private SharedPreferences pref;

    private SharedPreferences.Editor editor;

    String address;


    private FusedLocationProviderClient fusedLocationClient;

    public ProfileTab() {
        // Required empty public constructor
    }


    @Override
    public void onStart() {
        if (pref.getBoolean("Valor",false)) {
            btnFitxar.setText("Acabar");
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
            btnFitxar.setText("Acabar");
            //btnFitxar.setBackgroundColor(R.color.green);
            btnFitxar.setBackgroundResource(R.drawable.custom_button_red);
        }else{
            btnFitxar.setBackgroundResource(R.drawable.custom_button);
        }


        fusedLocationClient = LocationServices.getFusedLocationProviderClient(getContext());

        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION,Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.INTERNET}
                        ,10);
            }
        }


        return view;
    }

    private void location(final String valor,final FirebaseUser user ){

        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(getActivity(), new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        // Got last known location. In some rare situations this can be null.
                        if (location != null) {
                            Log.i(location.getLongitude() + "" ,location.getLatitude()+" ");

                            try{
                                Geocoder geo = new Geocoder(getActivity(), Locale.getDefault());
                                List<Address> addresses = geo.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                                if (addresses.isEmpty()) {
                                }
                                else {
                                    if (addresses.size() > 0) {
                                        address=addresses.get(0).getAddressLine(0);
                                        if (valor=="FITXAR"){
                                            API api = new API(getContext());
                                            api.get_in(user.getUid(),user.getDisplayName(),address);
                                        }else{
                                            API api = new API(getContext());
                                            api.get_out(user.getUid(),user.getDisplayName(),address);
                                        }

                                    }
                                }
                            }
                            catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }
                });
    }


    private String datetime(){
        SimpleDateFormat ISO_8601_FORMAT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:sss'Z'");

        return ISO_8601_FORMAT.format(new Date());

    }

    private String time(){
        SimpleDateFormat ISO_8601_FORMAT = new SimpleDateFormat("HH:mm:ss");

        return ISO_8601_FORMAT.format(new Date());

    }

    private boolean isNetworkAvailable() {  //Check if there is connection
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }


    @Override
    public void onClick(View v) {

        Boolean prova=isNetworkAvailable();

        Log.i("PROVA",prova.toString());

        if (btnFitxar.getText().toString()=="FITXAR"){

            FirebaseUser user =  mAuth.getCurrentUser();

            btnFitxar.setText("Acabar");
            btnFitxar.setBackgroundResource(R.drawable.custom_button_red);

            FancyToast.makeText(getContext(), "GET IN " + time(),
                    FancyToast.LENGTH_SHORT,FancyToast.INFO,true).show();

            editor.putBoolean("Valor",true);

            editor.putString("key_name", "NOFITXAR"); // Storing string

            editor.commit(); // commit changes

            location("FITXAR",user);

        }else{

            FirebaseUser user =  mAuth.getCurrentUser();

            btnFitxar.setText("FITXAR");
            btnFitxar.setBackgroundResource(R.drawable.custom_button);

            FancyToast.makeText(getContext(), "GET OUT " + time(),
                    FancyToast.LENGTH_SHORT,FancyToast.INFO,true).show();

            editor.putBoolean("Valor",false);

            editor.putString("key_name", "FITXAR"); // Storing string

            editor.commit(); // commit changes

            location("NOFITXAR",user);

        }

    }
}
