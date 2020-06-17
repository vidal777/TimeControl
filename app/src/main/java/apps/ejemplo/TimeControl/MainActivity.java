package apps.ejemplo.TimeControl;

import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import apps.ejemplo.TimeControl.R;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;
import com.shashank.sony.fancytoastlib.FancyToast;

public class MainActivity extends AppCompatActivity {

    Button btnSend;
    TextInputEditText edtName,edtNameCompany,edtCIF,edtNumberWorkers,edtEmail;
    CheckBox checkPolitic,checkConditions;
    String namecompany,email,CIF,numberWorkers;
    private LinearLayout linearLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);


        //set inits
        btnSend=findViewById(R.id.btnSend);
        edtEmail=findViewById(R.id.edtEmail);
        edtCIF=findViewById(R.id.edtCIF);
        edtNameCompany=findViewById(R.id.edtNameCompany);
        edtNumberWorkers=findViewById(R.id.edtNumberWorkers);
        checkConditions=findViewById(R.id.checkConditions);
        checkPolitic=findViewById(R.id.checkPolitic);
        linearLayout=findViewById(R.id.linearLayout);



        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkGaps()){
                    checkCompany();
                }
            }
        });

        linearLayout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
                return true;
            }
        });

    }

    private void DialogPopUp(){
        MaterialAlertDialogBuilder builderInner = new MaterialAlertDialogBuilder(this);
        builderInner.setTitle("Check email");
        builderInner.setMessage("\n" +
                "Check your email so you can register. You will see that a number of codes have been sent. First of all it corresponds to the administrator of the company. Then register using your name and provide the other codes to users.");
        builderInner.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent= new Intent(MainActivity.this,SignActivity.class);
                startActivity(intent);
                finish();
            }
        });

        androidx.appcompat.app.AlertDialog alert = builderInner.create();
        alert.setCanceledOnTouchOutside(true);
        alert.show();

    }


    private void checkCompany() {
        RequestQueue queue = Volley.newRequestQueue(this);
        String URL= "http://timecontrol.ddns.net/android_app/check_company.php?CIF=" + CIF + "&namecompany=" + namecompany ;
        StringRequest stringRequest = new StringRequest(Request.Method.GET, URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        response=response.trim();
                        if(response.equals("Exists")){
                            FancyToast.makeText(getApplicationContext(), "Company exists",
                                    FancyToast.LENGTH_SHORT, FancyToast.ERROR, true).show();
                        }else{
                            API api = new API(MainActivity.this);
                            api.set_company(namecompany,CIF,numberWorkers,email);

                            DialogPopUp();

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



    private Boolean checkGaps() {
        boolean check=true;
        email= edtEmail.getText().toString().trim();
        namecompany= edtNameCompany.getText().toString().trim();
        CIF= edtCIF.getText().toString().trim();
        numberWorkers= edtNumberWorkers.getText().toString().trim();

        if (namecompany.contains("'")){
            edtNameCompany.setError("Only supports letters and numbers");
            edtNameCompany.setFocusable(true);
            check=false;
        }
        if(namecompany.length()<1){
            edtNameCompany.setError("Name can't be blank");
            edtNameCompany.setFocusable(true);
            check=false;
        }
        if (CIF.length()<9){
            edtCIF.setError("Incorrect CIF");
            edtCIF.setFocusable(true);
            check=false;
        }
        if(numberWorkers.length()<1){
            edtNumberWorkers.setError("Name can't be blank");
            edtNumberWorkers.setFocusable(true);
            check=false;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            edtEmail.setError("Invalid Email");
            edtEmail.setFocusable(true);
            check=false;
        }
        if (!checkPolitic.isChecked()){
            checkPolitic.setError("Need Permission");
            //checkPolitic.setFocusable(true);
            check=false;
        }
        if (!checkConditions.isChecked()){
            checkConditions.setError("Need Permission");
            //checkConditions.setFocusable(true);
            check=false;
        }
        return check;

    }



}