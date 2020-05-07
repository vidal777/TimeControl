package com.example.timecontrol;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.shashank.sony.fancytoastlib.FancyToast;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity {

    Button btnSend;
    TextInputEditText edtName,edtNameCompany,edtCIF,edtNumberWorkers,edtEmail;
    CheckBox checkPolitic,checkConditions;

    String name,namecompany,email,CIF,numberWorkers;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);

        //set inits
        btnSend=findViewById(R.id.btnSend);
        edtName=findViewById(R.id.edtName);
        edtEmail=findViewById(R.id.edtEmail);
        edtCIF=findViewById(R.id.edtCIF);
        edtNameCompany=findViewById(R.id.edtNameCompany);
        edtNumberWorkers=findViewById(R.id.edtNumberWorkers);
        checkConditions=findViewById(R.id.checkConditions);
        checkPolitic=findViewById(R.id.checkPolitic);






        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkGaps()){
                        API api = new API(MainActivity.this);
                        api.set_company(name,namecompany,CIF,numberWorkers,email);
                }
            }
        });



    }

    private Boolean checkGaps() {
        Boolean check=true;
        email= edtEmail.getText().toString().trim();
        namecompany= edtNameCompany.getText().toString().trim();
        name= edtName.getText().toString().trim();
        CIF= edtCIF.getText().toString().trim();
        numberWorkers= edtNumberWorkers.getText().toString().trim();

        if(name.length()<1){
            edtName.setError("Name can't be blank");
            edtName.setFocusable(true);
            check=false;
        }else if(namecompany.length()<1){
            edtNameCompany.setError("Name can't be blank");
            edtNameCompany.setFocusable(true);
            check=false;
        }else if (CIF.length()<9){
            edtCIF.setError("Incorrect CIF");
            edtCIF.setFocusable(true);
            check=false;
        }else if(numberWorkers.length()<1){
            edtNumberWorkers.setError("Name can't be blank");
            edtNumberWorkers.setFocusable(true);
            check=false;
        }
        else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            edtEmail.setError("Invalid Email");
            edtEmail.setFocusable(true);
            check=false;
        }else if (!checkPolitic.isChecked()){
            checkPolitic.setError("Need Permission");
            //checkPolitic.setFocusable(true);
            check=false;
        }else if (!checkConditions.isChecked()){
            checkConditions.setError("Need Permission");
            //checkConditions.setFocusable(true);
            check=false;
        }
        return check;

    }



}