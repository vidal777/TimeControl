package com.example.timecontrol;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.shashank.sony.fancytoastlib.FancyToast;

import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private FirebaseAuth mAuth;
    private Button btnLogin, btnSign,btnLog;
    private RadioGroup rdGroup;
    private RadioButton rdUser,rdAdmin;
    private EditText edtEmail, edtPass,edtName,edtCode;
    private static final String TAG = "GoogleActivity";
    private static final int RC_SIGN_IN = 9001;

    private GoogleSignInClient mGoogleSignInClient;

    private String type_user;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        edtEmail = findViewById(R.id.edtEmail);
        edtPass = findViewById(R.id.edtPass);
        edtName = findViewById(R.id.edtName);
        edtCode=findViewById(R.id.edtCode);

        rdAdmin=findViewById(R.id.rdAdmin);
        rdUser=findViewById(R.id.rdUser);
        rdGroup=findViewById(R.id.rdGroup);

        btnSign = findViewById(R.id.btnSign);
        btnLog=findViewById(R.id.btnLog2);

        findViewById(R.id.signInButton).setOnClickListener(this);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        // [END config_signin]

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        mAuth = FirebaseAuth.getInstance();

        btnSign.setOnClickListener(this);
        btnLog.setOnClickListener(this);
        findViewById(R.id.signInButton).setOnClickListener(this);

        rdGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId==R.id.rdAdmin){
                    type_user="Admin";
                    edtCode.setVisibility(View.VISIBLE);
                }else{
                    type_user="User";
                    edtCode.setVisibility(View.INVISIBLE);
                }

            }
        });

    }

    private void signUp(String email, final String password,final String name){
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("TAG", "createUser:success");
                            FancyToast.makeText(MainActivity.this, "Authentication succes.",
                                    FancyToast.LENGTH_SHORT,FancyToast.SUCCESS,true).show();

                            FirebaseUser user =  mAuth.getCurrentUser();
                            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("usuarios").child(task.getResult()
                                    .getUser().getUid());
                            databaseReference.child("email").setValue(task.getResult().getUser().getEmail());
                            databaseReference.child("name").setValue(name);
                            databaseReference.child("date").setValue(datetime());
                            databaseReference.child("type_user").setValue(type_user);
                            transitionToMediaActivity();

                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w("TAG", "createUserWithEmail:failure", task.getException());
                            FancyToast.makeText(MainActivity.this, "Authentication failed.",
                                    FancyToast.LENGTH_SHORT,FancyToast.ERROR,true).show();
                        }

                    }
                });
    }



    private void signOut(){
        FirebaseAuth.getInstance().signOut();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnSign:
                signUp(edtEmail.getText().toString(), edtPass.getText().toString(),edtName.getText().toString());
                break;
            case R.id.signInButton:
                if (type_user!=null){
                    signIn();
                }else {
                    FancyToast.makeText(MainActivity.this, "Need type user.",
                            FancyToast.LENGTH_SHORT,FancyToast.INFO,true).show();
                }
                break;
            case R.id.btnLog2:
                Intent intent=new Intent(MainActivity.this,LoginActivity.class);
                startActivity(intent);
                break;
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        //updateUI(currentUser);
        if(currentUser != null){
            //Transition next activity
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                Log.w(TAG, "Google sign in failed", e);
            }
        }
    }
    // [END onactivityresult]

    // [START auth_with_google]
    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.d(TAG, "firebaseAuthWithGoogle:" + acct.getId());
        // [START_EXCLUDE silent]
        //showProgressBar();
        // [END_EXCLUDE]

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");
                            FancyToast.makeText(MainActivity.this, "LogIn succes.",
                                    FancyToast.LENGTH_SHORT,FancyToast.SUCCESS,true).show();


                            FirebaseUser user =  mAuth.getCurrentUser();
                            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("usuarios").child(task.getResult()
                                    .getUser().getUid());
                            databaseReference.child("email").setValue(task.getResult().getUser().getEmail());
                            databaseReference.child("name").setValue(task.getResult().getUser().getDisplayName());
                            databaseReference.child("date").setValue(datetime());
                            databaseReference.child("type_user").setValue(type_user);

                            transitionToMediaActivity();

                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            //Snackbar.make(findViewById(R.id.main_layout), "Authentication Failed.", Snackbar.LENGTH_SHORT).show();
                            //updateUI(null);
                        }

                    }
                });
    }
    // [END auth_with_google]

    private String datetime(){
        SimpleDateFormat ISO_8601_FORMAT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:sss'Z'");

        return ISO_8601_FORMAT.format(new Date());

    }

    // [START signin]
    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    private void transitionToMediaActivity(){
        Intent intent=new Intent(MainActivity.this,MainMedia.class);
        startActivity(intent);
        finish();
    }




}