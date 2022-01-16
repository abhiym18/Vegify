package com.example.vegify;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    private Button loginbtn;
    private TextView mDisplayDate;
    private DatePickerDialog.OnDateSetListener monDateSetListener;
    EditText mfname,mlname,mcity,mphone,memail,mpassword,mretypepassword;
    RadioGroup mradiogroup;
    RadioButton mradiobutton;
    TextView mdob;
    Button mregisterbtn;
    FirebaseAuth fauth;
    ProgressBar progressBar;
    String VendorID;
    String CustomerID;
    FirebaseFirestore fstore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getSupportActionBar().hide();

        getWindow().setBackgroundDrawableResource(R.drawable.vegibackgroungedited1);
        setContentView(R.layout.activity_main);
        mfname=findViewById(R.id.inputfirstname);
        mlname=findViewById(R.id.inputlastname);
        mcity=findViewById(R.id.inputcity);

        mdob=findViewById(R.id.tvdate);
        mradiogroup=findViewById(R.id.radiogrp);

        mphone=findViewById(R.id.inputphone);
        memail=findViewById(R.id.inputmail);
        mpassword=findViewById(R.id.linputpassword);
        mregisterbtn=findViewById(R.id.lloginbtn);

        fauth=FirebaseAuth.getInstance();
        fstore=FirebaseFirestore.getInstance();
        progressBar=findViewById(R.id.progbar);




        loginbtn = (Button)findViewById(R.id.LoginButton);
        loginbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openLoginActivity();
            }
        });

        if(fauth.getCurrentUser() != null){
            startActivity(new Intent(getApplicationContext(),HomeActivity.class));
            finish();
        }
        mregisterbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int rid=mradiogroup.getCheckedRadioButtonId();
                mradiobutton=findViewById(rid);
                String email=memail.getText().toString().trim();
                String password=mpassword.getText().toString().trim();
                String fname=mfname.getText().toString();
                String lname=mlname.getText().toString();
                String city=mcity.getText().toString();
                String dob=mdob.getText().toString();
                String type=mradiobutton.getText().toString();
                String phone=mphone.getText().toString();

                if(TextUtils.isEmpty(email)){
                    memail.setError("E-Mail is Required.");
                    return;
                }
                if(TextUtils.isEmpty(password)){
                    mpassword.setError("Password is Required.");
                    return;
                }
               if(password.length() < 5){
                   mpassword.setError("Password must be of minimum 6 characters.");
                   return;
               }

               progressBar.setVisibility(View.VISIBLE);


               fauth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                   @Override
                   public void onComplete(@NonNull Task<AuthResult> task) {
                       if(task.isSuccessful()){
                           Toast.makeText(MainActivity.this, "User Created Successfully.", Toast.LENGTH_SHORT).show();
                           if(type.equals("Vendor")) {
                               VendorID = fauth.getCurrentUser().getUid();
                               DocumentReference documentReference = fstore.collection("Vendor").document(VendorID);
                               Map<String, Object> Vendor = new HashMap<>();
                               Vendor.put("fname", fname);
                               Vendor.put("lname", lname);
                               Vendor.put("city", city);
                               Vendor.put("DOB", dob);
                               Vendor.put("Type", type);
                               Vendor.put("Phone", phone);
                               Vendor.put("email", email);
                               documentReference.set(Vendor).addOnSuccessListener(new OnSuccessListener<Void>() {
                                   @Override
                                   public void onSuccess(Void aVoid) {

                                   }
                               });

                               startActivity(new Intent(getApplicationContext(), Vendor_home.class));
                           }

                           else{


                               CustomerID = fauth.getCurrentUser().getUid();
                               DocumentReference documentReference1 = fstore.collection("Customer").document(CustomerID);
                               Map<String, Object> Customer = new HashMap<>();
                               Customer.put("fname", fname);
                               Customer.put("lname", lname);
                               Customer.put("city", city);
                               Customer.put("DOB", dob);
                               Customer.put("Type", type);
                               Customer.put("Phone", phone);
                               Customer.put("email", email);
                               documentReference1.set(Customer).addOnSuccessListener(new OnSuccessListener<Void>() {
                                   @Override
                                   public void onSuccess(Void aVoid) {

                                   }
                               });
                               startActivity(new Intent(getApplicationContext(), HomeActivity.class));


                           }
                       }
                       else{
                           Toast.makeText(MainActivity.this, "Error! " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                       }
                   }
               });
            }
        });

        mDisplayDate = (TextView) findViewById(R.id.tvdate);
        mDisplayDate.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Calendar cal = Calendar.getInstance();
                int year = cal.get(Calendar.YEAR);
                int month = cal.get(Calendar.MONTH);
                int day = cal.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog dialog = new DatePickerDialog(
                        MainActivity.this,
                        android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                        monDateSetListener,
                        year, month, day);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.show();

            }
        });
        monDateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int day) {
                month = month + 1;
                Log.d(TAG, "onDateSet: mm/dd/yyy: " + month + "/" + year);
                String date = day + "/" + month + "/" + year;
                mDisplayDate.setText(date);
            }
        };



    }
    public void openLoginActivity(){
        Intent intent = new Intent(this,LoginActivity.class);
        startActivity(intent);
    }

}