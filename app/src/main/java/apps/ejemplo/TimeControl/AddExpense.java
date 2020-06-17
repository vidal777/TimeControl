package apps.ejemplo.TimeControl;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.DatePickerDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;

import apps.ejemplo.TimeControl.R;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.shashank.sony.fancytoastlib.FancyToast;


import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.UUID;

import static com.google.firebase.storage.FirebaseStorage.getInstance;

public class AddExpense extends AppCompatActivity implements View.OnClickListener{

    FirebaseAuth firebaseAuth;
    FirebaseUser user;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;

    StorageReference storageReference;
    //path where images of user profile and cover will be stored
    String storagePath= "Expenses/";

    ImageButton btnBack2,btnDone;
    ImageView imageButton;
    EditText edtValue,edtComments;

    Spinner spinner_expense;
    ArrayAdapter<String> adapterspinner_expense;

    Button btnDate;

    int day,month,year;

    private static final int CAMERA_REQUEST_CODE=100;
    private static final int STORAGE_REQUEST_CODE=200;
    private static final int IMAGE_PICK_GALLERY_CODE=300;
    private static final int IMAGE_PICK_CAMERA_CODE=400;


    String[] cameraPermissions;
    String[] storagePermissions;

    String concept;

    Uri image_uri;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_expense);

        btnBack2=findViewById(R.id.btnBack3);
        btnDone=findViewById(R.id.btnDone);

        btnDate=findViewById(R.id.btnDate);
        imageButton=findViewById(R.id.imageButton);
        edtComments=findViewById(R.id.edtComments);
        edtValue=findViewById(R.id.edtValue);

        btnBack2.setOnClickListener(AddExpense.this);
        btnDone.setOnClickListener(AddExpense.this);
        edtValue.setOnClickListener(AddExpense.this);
        btnDate.setOnClickListener(AddExpense.this);
        imageButton.setOnClickListener(AddExpense.this);

        //init firebase
        firebaseAuth=FirebaseAuth.getInstance();
        user=firebaseAuth.getCurrentUser();
        firebaseDatabase=FirebaseDatabase.getInstance();
        databaseReference=firebaseDatabase.getReference("usuarios");
        storageReference=getInstance().getReference(); //firebase storage reference

        cameraPermissions= new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
        storagePermissions= new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};

        btnDate.setText(getCurrentDate());

        spinner_expense = findViewById(R.id.spinner_expense);

        final String[] opcions = {"Dietes", "Transport", "Varis"};

        adapterspinner_expense = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, opcions);

        spinner_expense.setAdapter(adapterspinner_expense);

        spinner_expense.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                concept=opcions[position];
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
            }

        });


    }

    public String getCurrentDate()
    {
        Calendar c = Calendar.getInstance();
        year=c.get(Calendar.YEAR);
        month=c.get(Calendar.MONTH);
        day=c.get(Calendar.DAY_OF_MONTH);

        SimpleDateFormat df = new SimpleDateFormat("EEE, d MMM yyyy");

        return df.format(c.getTime());
    }

    @Override
    public void onClick(View v) {
        String uniqueID = "";
        switch (v.getId()){
            case R.id.imageButton:
                if (!checkCameraPermission()){
                    requestCameraPermission();
                }else{
                    pickFromCamera();
                }
                break;
            case R.id.btnBack3:
                finish();
                break;
            case R.id.btnDone:
                String value= edtValue.getText().toString().trim();
                if(value.length()>0){
                    String data_entrada = year + "-" + (month+1) + "-" + day;
                    int price=Integer.parseInt(edtValue.getText().toString());
                    String comments=edtComments.getText().toString();
                    if(image_uri!=null){
                        uniqueID = UUID.randomUUID().toString();
                        uploadExpense(image_uri,uniqueID);

                    }
                    API api = new API(this);
                    api.set_expense(user.getUid(),data_entrada,price,comments,concept,uniqueID);
                    finish();

                }else{
                    edtValue.setError("Name can't be blank");
                    edtValue.setFocusable(true);
                }

                break;
            case R.id.btnDate:
                DatePickerDialog datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int Year, int monthOfYear, int dayOfMonth) {
                        btnDate.setText(dayOfMonth + "/" + (monthOfYear + 1) + "/" + year);
                        day = dayOfMonth;
                        month = monthOfYear + 1;
                        year = Year;
                    }
                }, year,month,day);
                datePickerDialog.show();

                break;
        }
    }


    private boolean checkCameraPermission(){
        return ContextCompat.checkSelfPermission(this,Manifest.permission.CAMERA) == (PackageManager.PERMISSION_GRANTED);
    }

    private void requestCameraPermission(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(cameraPermissions,CAMERA_REQUEST_CODE);
        }
    }

    private void uploadExpense(Uri uri,String id) {

        String filePathAndName= storagePath + "" + id;
        StorageReference storageReference2nd= storageReference.child(filePathAndName);

        storageReference2nd.putFile(uri);

    }



    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if ( resultCode == RESULT_OK){
            if ( requestCode == IMAGE_PICK_GALLERY_CODE){
                //image is picked from gallery, get uri of image
                image_uri= data.getData();
                //uploadProfileCoverPhoto(image_uri);

            }
            if(requestCode== IMAGE_PICK_CAMERA_CODE){
                Glide.with(getApplicationContext()).load(image_uri).into(imageButton);
                imageButton.setBackgroundColor(Color.parseColor("#FFFFFF"));
               // uploadProfileCoverPhoto(image_uri);

            }

        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
                if (grantResults.length>0){
                    boolean cameraAccepted= grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    boolean writeStorageAccepted= grantResults[1] == PackageManager.PERMISSION_GRANTED;
                    if (cameraAccepted && writeStorageAccepted){ //permission enable
                        pickFromCamera();
                    }else{
                        FancyToast.makeText(this, "Please enable camera & storage permissions",
                                FancyToast.LENGTH_SHORT,FancyToast.INFO,true).show();
                    }
                }
            }

    private void pickFromCamera(){
        //Intent of picking image from device camera
        ContentValues values=new ContentValues();
        values.put(MediaStore.Images.Media.TITLE, "Temp Pic");
        values.put(MediaStore.Images.Media.DESCRIPTION, "Temp Description");
        //put image uri
        image_uri=AddExpense.this.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,values);

        //intent to start camera
        Intent cameraIntent= new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, image_uri);
        startActivityForResult(cameraIntent, IMAGE_PICK_CAMERA_CODE);


    }


}
