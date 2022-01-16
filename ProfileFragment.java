package com.example.vegify;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.net.wifi.SupplicantState;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.UUID;

import io.grpc.Context;

import static android.app.Activity.RESULT_OK;

public class ProfileFragment extends Fragment {



    ImageView ProfilePic;
    Uri imageuri;
    FirebaseStorage storage;
    StorageReference storageReference;

    EditText ufname,ulname,ucity,uphone;
    FirebaseAuth ufauth;
    FirebaseFirestore ufstore;
    Button uupdate;
    String Vendor_ID;

    ImageView Profile;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_profile,container,false);

       ufauth=FirebaseAuth.getInstance();
        ufstore=FirebaseFirestore.getInstance();
        ufname=view.findViewById(R.id.upfname);
        ulname=view.findViewById(R.id.uplname);
        ucity=view.findViewById(R.id.upcity);
        uphone=view.findViewById(R.id.upphone);
        uupdate=view.findViewById(R.id.Lupdate);
        updateprofile();

        ProfilePic=view.findViewById(R.id.profilepic);

        storage=FirebaseStorage.getInstance();
        storageReference=storage.getReference();

        ProfilePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                choosepicture();
            }
        });


        uupdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String fname =ufname.getText().toString().trim();
                String lname =ulname.getText().toString().trim();
                String city =ucity.getText().toString().trim();
                String phone =uphone.getText().toString().trim();
                

                if(TextUtils.isEmpty(fname)){
                    ufname.setError("First Name is Required!");
                    return;
                }
                if(TextUtils.isEmpty(lname)){
                    ulname.setError("Last Name is Required!");
                    return;
                }
                if(TextUtils.isEmpty(city)){
                    ucity.setError("City is Required!");
                    return;
                }
                if(TextUtils.isEmpty(phone)){
                    uphone.setError("Phone is Required!");
                    return;
                }


                ufstore.collection("Vendor").document(ufauth.getCurrentUser().getUid())
                        .update(

                                "fname",fname,
                                "lname",lname,
                                "city",city,
                                "Phone",phone
                        ).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {

                        Toast.makeText(getActivity(),"Updated Sucessfully", Toast.LENGTH_SHORT).show();
                        updateprofile();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getActivity(), "Error while updating", Toast.LENGTH_SHORT).show();
                    }
                });


            }
        });


        return view;
    }

    private void choosepicture() {

        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent,1);



    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 1 && resultCode == RESULT_OK && data!=null){
            imageuri=data.getData();
            ProfilePic.setImageURI(imageuri);
            uploadPicture();
        }
    }

    private void uploadPicture() {



        final String randomkey = UUID.randomUUID().toString();
        StorageReference riversRef = storageReference.child("image/" + randomkey);
        riversRef.putFile(imageuri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                      // Snackbar.make(getView().findViewById(R.id.content),"Image Uploaded",Snackbar.LENGTH_LONG).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {


                    }
                });

    }

    public void updateprofile(){
        Vendor_ID=ufauth.getCurrentUser().getUid();
        FirebaseFirestore.getInstance().collection("Vendor").document(Vendor_ID).get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {

                        ufname.setText(documentSnapshot.getString("fname"));
                        ulname.setText(documentSnapshot.getString("lname"));
                        ucity.setText(documentSnapshot.getString("city"));
                        uphone.setText(documentSnapshot.getString("Phone"));



                    }
                });



    }




}
