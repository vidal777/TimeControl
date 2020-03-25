package com.example.timecontrol;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.shashank.sony.fancytoastlib.FancyToast;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText edtEmail2,edtPass2;
    private Button btnLogin,btnLog2;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        edtEmail2=findViewById(R.id.edtEmail2);
        edtPass2=findViewById(R.id.edtPass2);
        btnLogin=findViewById(R.id.btnLogin);
        btnLog2=findViewById(R.id.btnLog2);

        btnLogin.setOnClickListener(this);
        btnLog2.setOnClickListener(this);


        mAuth = FirebaseAuth.getInstance();

    }

    private void LogIn(String email,String password){
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("TAG", "signInWithEmail:success");
                            FancyToast.makeText(LoginActivity.this, "LogIn succes.",
                                    FancyToast.LENGTH_SHORT,FancyToast.SUCCESS,true).show();
                            FirebaseUser user = mAuth.getCurrentUser();


                            transitionToMediaActivity();

                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w("TAG", "signInWithEmail:failure", task.getException());
                            FancyToast.makeText(LoginActivity.this, "Authentication failed.",
                                    FancyToast.LENGTH_SHORT,FancyToast.ERROR,true).show();
                            //updateUI(null);
                        }

                        // ...
                    }
                });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btnLogin:
                LogIn(edtEmail2.getText().toString(), edtPass2.getText().toString());
                break;
            case R.id.btnLog2:
                Intent intent=new Intent(LoginActivity.this,MainActivity.class);
                startActivity(intent);
                finish();
                break;
        }

    }

    private void transitionToMediaActivity(){
        Intent intent=new Intent(LoginActivity.this,MainMedia.class);
        startActivity(intent);
        finish();
    }
}
