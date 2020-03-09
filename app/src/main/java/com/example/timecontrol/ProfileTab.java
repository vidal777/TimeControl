package com.example.timecontrol;


import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Date;


/**
 * A simple {@link Fragment} subclass.
 */
public class ProfileTab extends Fragment implements View.OnClickListener {
    private FirebaseAuth mAuth;

        private Button btnFitxar;

    public ProfileTab() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view=inflater.inflate(R.layout.fragment_profile_tab, container, false);

        btnFitxar= view.findViewById(R.id.btnFitxar);
        mAuth = FirebaseAuth.getInstance();

        btnFitxar.setOnClickListener(ProfileTab.this);

        return view;
    }

    private String datetime(){
        SimpleDateFormat ISO_8601_FORMAT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:sss'Z'");

        return ISO_8601_FORMAT.format(new Date());

    }

    @SuppressLint("ResourceAsColor")
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

        }else{
            FirebaseUser user =  mAuth.getCurrentUser();
            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("horarios").child(user
                    .getUid());
            databaseReference.child("datetime_exit " + datetime()).setValue(datetime());

            btnFitxar.setText("FITXAR");
            btnFitxar.setBackgroundResource(R.drawable.custom_button);
        }

    }
}
