package apps.ejemplo.TimeControl;


import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import apps.ejemplo.TimeControl.R;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.shashank.sony.fancytoastlib.FancyToast;
import com.squareup.picasso.Picasso;

import java.util.HashMap;

import static android.app.Activity.RESULT_OK;
import static com.google.firebase.storage.FirebaseStorage.getInstance;


/**
 * A simple {@link Fragment} subclass.
 */
public class SettingsTab extends Fragment {

    //firebase
    private FirebaseAuth firebaseAuth;
    FirebaseUser user;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;

    //storage
    private StorageReference storageReference;
    //path where images of user profile and cover will be stored
    private String storagePath= "Users_Profile_Cover_Imgs/";

    private ImageView avatarIv;
    private TextView nameTv,emailTv,userTv;

    private ProgressDialog pd;

    private static final int CAMERA_REQUEST_CODE=100;
    private static final int STORAGE_REQUEST_CODE=200;
    private static final int IMAGE_PICK_GALLERY_CODE=300;
    private static final int IMAGE_PICK_CAMERA_CODE=400;

    private String[] cameraPermissions;
    private String[] storagePermissions;

    //uri of picked image
    private Uri image_uri;

    //for checking profile or cover photo
    private String profileorCoverPhoto;

    private Button btnChangePassw;


    public SettingsTab() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_settings_tab, container, false);


        //init firebase
        firebaseAuth=FirebaseAuth.getInstance();
        user=firebaseAuth.getCurrentUser();
        firebaseDatabase=FirebaseDatabase.getInstance();
        databaseReference=firebaseDatabase.getReference("usuarios");
        storageReference=getInstance().getReference(); //firebase storage reference

        //init arrays of permissions
        cameraPermissions= new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
        storagePermissions= new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};


        //init views
        avatarIv=view.findViewById(R.id.avatarIv);
        nameTv=view.findViewById(R.id.nameTv);
        emailTv=view.findViewById(R.id.emailTv);
        userTv=view.findViewById(R.id.userTv);
        btnChangePassw=view.findViewById(R.id.btnChangePassw);

        //init progress dialog
        pd= new ProgressDialog(getActivity());



        Query query=databaseReference.orderByChild("email").equalTo(user.getEmail());
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()){
                    String name= "" + ds.child("name").getValue();
                    String email= "" + ds.child("email").getValue();
                    String user= "" + ds.child("type_user").getValue();
                    String image= "" + ds.child("image").getValue();

                    nameTv.setText(name);
                    emailTv.setText(email);
                    userTv.setText(user);

                    try{
                       Picasso.get().load(image).into(avatarIv);
                    }catch (Exception e){
                        //Picasso.get().load(R.drawable.ic_add_photo).into(avatarIv);
                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        //fab button

        avatarIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showImagePicDialog();
            }
        });

        btnChangePassw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                firebaseAuth.sendPasswordResetEmail(user.getEmail())
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(getActivity(), "Check email to change your password!", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(getActivity(), "Fail to send change password email!", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        });

        return view;
    }


    private boolean checkStoragePermission(){
        return ContextCompat.checkSelfPermission(getActivity(),Manifest.permission.WRITE_EXTERNAL_STORAGE) == (PackageManager.PERMISSION_GRANTED);
    }

    private void requestStoragePermission(){
        requestPermissions(storagePermissions,STORAGE_REQUEST_CODE);
    }

    private boolean checkCameraPermission(){
        return ContextCompat.checkSelfPermission(getActivity(),Manifest.permission.CAMERA) == (PackageManager.PERMISSION_GRANTED);
    }

    private void requestCameraPermission(){
        requestPermissions(cameraPermissions,CAMERA_REQUEST_CODE);
    }




    private void showImagePicDialog(){
        String[] options = {"Camera", "Gallery"};

        AlertDialog.Builder builder=new AlertDialog.Builder(getActivity());
        builder.setTitle("Pick Image From");
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                profileorCoverPhoto= "image";
                if (which == 0){  //Camera clicked
                    if (!checkCameraPermission()){
                        requestCameraPermission();
                    }else{
                        pickFromCamera();
                    }

                }else if ( which == 1){
                    if(!checkStoragePermission()){
                        requestStoragePermission();
                    }else{
                        pickFromGallery();
                    }


                }
            }
        });
        builder.create().show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case CAMERA_REQUEST_CODE:{
                if (grantResults.length>0){
                    boolean cameraAccepted= grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    boolean writeStorageAccepted= grantResults[1] == PackageManager.PERMISSION_GRANTED;
                    if (cameraAccepted && writeStorageAccepted){ //permission enable
                        pickFromCamera();
                    }else{
                        FancyToast.makeText(getContext(), "Please enable camera & storage permissions",
                                FancyToast.LENGTH_SHORT,FancyToast.INFO,true).show();
                    }
                }
            }
            break;
            case STORAGE_REQUEST_CODE:{
                if (grantResults.length>0){
                    boolean writeStorageAccepted= grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    if (writeStorageAccepted){ //permission enable
                        pickFromGallery();
                    }else{
                        FancyToast.makeText(getContext(), "Please enable storage permissions",
                                FancyToast.LENGTH_SHORT,FancyToast.INFO,true).show();
                    }
                }

            }
            break;
        }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if ( resultCode == RESULT_OK){
            if ( requestCode == IMAGE_PICK_GALLERY_CODE){
                //image is picked from gallery, get uri of image
                image_uri= data.getData();
                uploadProfileCoverPhoto(image_uri);

            }
            if(requestCode== IMAGE_PICK_CAMERA_CODE){
                uploadProfileCoverPhoto(image_uri);

            }

        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void uploadProfileCoverPhoto(Uri uri) {

        String filePathAndName= storagePath + "" + profileorCoverPhoto + "_"+ user.getUid();
        StorageReference storageReference2nd= storageReference.child(filePathAndName);

        storageReference2nd.putFile(uri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                        while (!uriTask.isSuccessful());
                        Uri dowloadUri= uriTask.getResult();

                        //check if image is uploaded or not
                        if (uriTask.isSuccessful()){
                            //image uploaded
                            HashMap<String,Object> results= new HashMap<>();
                            results.put(profileorCoverPhoto, dowloadUri.toString());

                            databaseReference.child(user.getUid()).updateChildren(results)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            pd.dismiss();
                                            FancyToast.makeText(getContext(), "Image Updated...",
                                                    FancyToast.LENGTH_SHORT,FancyToast.SUCCESS,true).show();
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            pd.dismiss();
                                            FancyToast.makeText(getContext(), "Error Updating Image...",
                                                    FancyToast.LENGTH_SHORT,FancyToast.ERROR,true).show();
                                        }
                                    });

                        }else{
                            //error
                            pd.dismiss();
                            FancyToast.makeText(getContext(), "Some error occured",
                                    FancyToast.LENGTH_SHORT,FancyToast.INFO,true).show();

                        }

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        pd.dismiss();
                        FancyToast.makeText(getActivity(), e.getMessage(),
                                FancyToast.LENGTH_SHORT,FancyToast.INFO,true).show();
                    }
                });

    }

    private void pickFromCamera(){
        //Intent of picking image from device camera
        ContentValues values=new ContentValues();
        values.put(MediaStore.Images.Media.TITLE, "Temp Pic");
        values.put(MediaStore.Images.Media.DESCRIPTION, "Temp Description");
        //put image uri
        image_uri=getActivity().getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,values);

        //intent to start camera
        Intent cameraIntent= new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, image_uri);
        startActivityForResult(cameraIntent, IMAGE_PICK_CAMERA_CODE);


    }

    private void pickFromGallery(){
        //pick from gallery

        Intent galleryIntent= new Intent(Intent.ACTION_PICK);
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent,IMAGE_PICK_GALLERY_CODE);

    }
}
