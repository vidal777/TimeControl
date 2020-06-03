package apps.ejemplo.TimeControl;


import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;

import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import apps.ejemplo.TimeControl.R;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.shashank.sony.fancytoastlib.FancyToast;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;


/**
 * A simple {@link Fragment} subclass.
 */
public class ProfileTab extends Fragment implements View.OnClickListener {
    private FirebaseAuth mAuth;

    static Button btnFitxar;

    private SharedPreferences pref;

    private SharedPreferences.Editor editor;

    private String address;




    private FusedLocationProviderClient fusedLocationClient;

    public ProfileTab() {
        // Required empty public constructor
    }


    @Override
    public void onStart() {
        if (pref.getBoolean("Valor",false)) {
            btnFitxar.setText("ACABAR");
            btnFitxar.setBackgroundResource(R.drawable.custom_button_red);
        }
        super.onStart();
    }



    @SuppressLint("ResourceAsColor")
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
            btnFitxar.setText("ACABAR");
            btnFitxar.setBackgroundResource(R.drawable.custom_button_red);
        }else{
            btnFitxar.setBackgroundResource(R.drawable.custom_button);
        }


        fusedLocationClient = LocationServices.getFusedLocationProviderClient(getActivity());

        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION,Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.INTERNET}
                        ,10);
            }
        }


        return view;
    }

    public interface ServerCallback{
        void onSucces(boolean result);
    }

    private void updateLocation(final String valor,Location location,FirebaseUser user){
        try{
            Geocoder geo = new Geocoder(getActivity(), Locale.getDefault());
            List<Address> addresses = geo.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
            if (addresses.isEmpty()) {
            }
            else {
                if (addresses.size() > 0) {
                    address=addresses.get(0).getAddressLine(0);
                    if (valor.equals("FITXAR")){
                        API api = new API(getContext());
                        //api.get_in(user.getUid(),user.getDisplayName(),address);
                        api.get_in(user.getUid(),address,new ServerCallback(){
                            @Override
                            public void onSucces(boolean result) {
                                if (result){
                                    Start();
                                }else{
                                    FancyToast.makeText(getContext(), "ERROR",
                                            FancyToast.LENGTH_SHORT, FancyToast.ERROR, true).show();
                                }
                            }
                        });
                    }else{
                        API api = new API(getContext());
                        //api.get_in(user.getUid(),user.getDisplayName(),address);
                        api.get_out(user.getUid(),address,new ServerCallback(){
                            @Override
                            public void onSucces(boolean result) {
                                if (result){
                                    Finish();
                                }else{
                                    FancyToast.makeText(getContext(), "ERROR",
                                            FancyToast.LENGTH_SHORT, FancyToast.ERROR, true).show();
                                }
                            }
                        });
                    }

                }
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }

    }


    public interface CallbackLocation{
        void OnSucces(Location location);
    }



    private void getLocation(final CallbackLocation callbackLocation ){
        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(getActivity(), new OnSuccessListener<Location>(){
                    @Override
                    public void onSuccess(Location location) {
                        callbackLocation.OnSucces(location);
                        // Got last known location. In some rare situations this can be null.
                        if (location != null) {
                            // Logic to handle location object
                        }
                    }
                });
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

    private void Start(){

        btnFitxar.setText("ACABAR");
        btnFitxar.setBackgroundResource(R.drawable.custom_button_red);

        FancyToast.makeText(getContext(), "GET IN " + time(),
                FancyToast.LENGTH_SHORT, FancyToast.INFO, true).show();

        editor.putBoolean("Valor", true);

        editor.commit(); // commit changes
    }

    private void Finish(){
        btnFitxar.setText("FITXAR");
        btnFitxar.setBackgroundResource(R.drawable.custom_button);

        FancyToast.makeText(getContext(), "GET OUT " + time(),
                FancyToast.LENGTH_SHORT, FancyToast.INFO, true).show();

        editor.putBoolean("Valor", false);


        editor.commit(); // commit changes
    }


    @Override
    public void onClick(View v) {


        switch (v.getId()) {
            case R.id.btnFitxar:
                if (btnFitxar.getText().toString().equals("FITXAR")) {

                    final FirebaseUser user = mAuth.getCurrentUser();

                    getLocation(new CallbackLocation() {
                        @Override
                        public void OnSucces(Location location) {
                            if (isNetworkAvailable()){
                                Log.i("NETWORK AVAIBLE","NETWORK AVAIBLE");
                                updateLocation("FITXAR",location,user);
                            }else{
                                Log.i("NO NETWORK","NO NETWORK");
                            }

                        }
                    });

                } else {

                    final FirebaseUser user = mAuth.getCurrentUser();

                    getLocation(new CallbackLocation() {
                        @Override
                        public void OnSucces(Location location) {
                            if (isNetworkAvailable()){
                                Log.i("NETWORK AVAIBLE","NETWORK AVAIBLE");
                                updateLocation("NOFITXAR",location,user);
                            }else{
                                Log.i("NO NETWORK","NO NETWORK");
                            }

                        }
                    });


                }
                break;
        }
    }
}
