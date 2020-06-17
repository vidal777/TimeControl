package apps.ejemplo.TimeControl;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.LinearLayout;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import apps.ejemplo.TimeControl.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.shashank.sony.fancytoastlib.FancyToast;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

public class SignActivity extends AppCompatActivity implements View.OnClickListener {

    private FirebaseAuth mAuth;
    private Button btnSign,btnLog,btnRegisterCompany;
    private TextInputEditText edtEmail, edtPass,edtName,edtCode;
    private static final String TAG = "GoogleActivity";
    private LinearLayout linearLayout;



    public static SharedPreferences prefe;

    public static SharedPreferences.Editor editore;

    String name,email,code,password,type_user;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign);


        prefe = getApplicationContext().getSharedPreferences("User", 0); //Use save state button
        editore = prefe.edit();

        btnRegisterCompany=findViewById(R.id.btnRegisterCompany);
        edtEmail = findViewById(R.id.edtEmail);
        edtPass = findViewById(R.id.edtPass);
        edtName = findViewById(R.id.edtName);
        edtCode=findViewById(R.id.edtCode);
        btnSign = findViewById(R.id.btnSign);
        btnLog=findViewById(R.id.btnLog2);
        linearLayout=findViewById(R.id.linearLayout);



        mAuth = FirebaseAuth.getInstance();

        btnSign.setOnClickListener(this);
        btnLog.setOnClickListener(this);
        btnRegisterCompany.setOnClickListener(this);
        linearLayout.setOnClickListener(this);

        linearLayout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
                return true;
            }
        });


    }


    private void signUp(){  //Sign Up Normal Mode
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information

                            FirebaseUser user = mAuth.getCurrentUser();

                            //Set a display name to know who is in session.
                            UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                    .setDisplayName(name)
                                    .build();

                            user.updateProfile(profileUpdates)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                Log.d(TAG, "User profile updated.");
                                            }
                                        }
                                    });


                            //Save data to Firebase
                            HashMap<Object,String> hashMap= new HashMap<>();

                            hashMap.put("email",task.getResult().getUser().getEmail());
                            hashMap.put("name",name);
                            hashMap.put("date",datetime());
                            hashMap.put("type_user",type_user);
                            hashMap.put("image","");

                            editore.putString("User",type_user);
                            editore.commit();

                            FirebaseDatabase database=FirebaseDatabase.getInstance();

                            DatabaseReference reference=database.getReference("usuarios");

                            reference.child(user.getUid()).setValue(hashMap);

                            //Save data to table users mysql
                            API api = new API(SignActivity.this);
                            api.register_user(task.getResult()
                                    .getUser().getUid(),name,task.getResult().getUser().getEmail(),code);

                            FancyToast.makeText(SignActivity.this, "Authentication succes.",
                                    FancyToast.LENGTH_SHORT,FancyToast.SUCCESS,true).show();

                            transitionToMediaActivity();

                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w("TAG", "createUserWithEmail:failure", task.getException());
                            FancyToast.makeText(SignActivity.this, "Authentication failed.",
                                    FancyToast.LENGTH_SHORT,FancyToast.ERROR,true).show();
                        }

                    }
                });

        FirebaseUser user = mAuth.getCurrentUser();

    }

    private void checkCode() {
        RequestQueue queue = Volley.newRequestQueue(this);
        String URL= "http://timecontrol.ddns.net/android_app/check_code.php?code=" + code ;
        StringRequest stringRequest = new StringRequest(Request.Method.GET, URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.i("RESPONSE",response);
                        response=response.trim();
                        String[] arrOfStr = response.split("/", 2);

                        if(response.equals("No Exists")){
                            FancyToast.makeText(getApplicationContext(), "Code no exists",
                                    FancyToast.LENGTH_SHORT, FancyToast.ERROR, true).show();
                        }else{
                            type_user=arrOfStr[0];
                            code= arrOfStr[1];
                            signUp();
                        }



                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        FancyToast.makeText(getApplicationContext(), "Error on transaction",
                                FancyToast.LENGTH_SHORT, FancyToast.ERROR, true).show();
                    }
                });
        queue.add(stringRequest);
    }

    private Boolean checkGaps(){
        boolean mcheck=true;
        email= edtEmail.getText().toString().trim();
        password= edtPass.getText().toString().trim();
        name= edtName.getText().toString().trim();
        code= edtCode.getText().toString().trim();
        if(name.length()<1){
            edtName.setError("Name can't be blank");
            edtName.setFocusable(true);
            mcheck=false;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            edtEmail.setError("Invalid Email");
            edtEmail.setFocusable(true);
            mcheck=false;
        }if(password.length()<6){
            edtPass.setError("Password length at least 6 characters");
            edtPass.setFocusable(true);
            mcheck=false;

        }
        if (code.length()<13) {
            edtCode.setError("Incorrect code");
            edtCode.setFocusable(true);
            mcheck=false;
        }
        return mcheck;
    }



    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnSign:
                if (checkGaps()){
                    checkCode();
                }
                break;
            case R.id.btnLog2:
                Intent intent=new Intent(SignActivity.this,LoginActivity.class);
                startActivity(intent);
                break;
            case R.id.btnRegisterCompany:
                Intent mainActivity=new Intent(this,MainActivity.class);
                startActivity(mainActivity);
                //finish();
                break;
            //case R.id.linearLayout:

        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        finish();

    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        //updateUI(currentUser);
        if(currentUser != null){
            transitionToMediaActivity();
        }
    }


    private String datetime(){
        SimpleDateFormat ISO_8601_FORMAT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:sss'Z'");
        return ISO_8601_FORMAT.format(new Date());
    }



    private void transitionToMediaActivity(){
        Intent intent=new Intent(SignActivity.this,MainMedia.class);
        startActivity(intent);
        finish();
    }
}