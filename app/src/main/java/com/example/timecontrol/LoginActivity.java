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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.shashank.sony.fancytoastlib.FancyToast;

import static com.example.timecontrol.SignActivity.editore;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText edtEmail2,edtPass2;
    private Button btnLogin,btnLog2,btnForgot;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        edtEmail2=findViewById(R.id.edtEmail2);
        edtPass2=findViewById(R.id.edtPass2);
        btnLogin=findViewById(R.id.btnLogin);
        btnLog2=findViewById(R.id.btnLog2);
        btnForgot=findViewById(R.id.btnForgot);

        btnLogin.setOnClickListener(this);
        btnLog2.setOnClickListener(this);
        btnForgot.setOnClickListener(this);


        mAuth = FirebaseAuth.getInstance();

    }

    private void get_type_user(){  //Know type_user ( Problems: need internet connection and you have to wait until ondatachange.)

        FirebaseUser user =  FirebaseAuth.getInstance().getCurrentUser();

        DatabaseReference mref = FirebaseDatabase.getInstance().getReference("usuarios").child(user.getUid().toString()).child("type_user");
        mref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String type_user = dataSnapshot.getValue(String.class);
                editore.putString("User",type_user);
                editore.commit();
                transitionToMediaActivity();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
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

                            get_type_user();

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
                startActivity(new Intent(LoginActivity.this, SignActivity.class));
                finish();
                break;
            case R.id.btnForgot:
                startActivity(new Intent(LoginActivity.this, ResetPasswordActivity.class));

        }

    }

    private void transitionToMediaActivity(){
        Intent intent=new Intent(LoginActivity.this,MainMedia.class);
        startActivity(intent);
        finish();
    }
}
