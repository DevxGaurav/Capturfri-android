package com.capturfri;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.util.Calendar;
import java.util.Date;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserDetails extends AppCompatActivity {

    private Manager manager;
    private CircleImageView ppic;
    private String gender="male";
    private String name="";
    private String dob="";
    private ProgressBar progressBar;
    private FloatingActionButton next;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_details);
        manager=Manager.getInstance();

        ppic=findViewById(R.id.ppic_userdetails);
        ImageView edit=findViewById(R.id.editppic_userdetails);
        final TextInputEditText name_e=findViewById(R.id.name_userdetails);
        final TextInputEditText dob_e=findViewById(R.id.dob_userdetails);
        final TextView gender_t=findViewById(R.id.gender_userdetails);
        final Switch gender_s=findViewById(R.id.switch_userdetails);
        next=findViewById(R.id.next_userdetails);
        progressBar=findViewById(R.id.progressBar_userdetails);


        gender_s.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b){
                    gender="female";
                    gender_t.setText(getString(R.string.girl));
                }else {
                    gender="male";
                    gender_t.setText(getString(R.string.boy));
                }
            }
        });


        name_e.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                name_e.setError(null);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });


        dob_e.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                dob_e.setError(null);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });



        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent pickPhoto = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(pickPhoto , 100);
            }
        });



        dob_e.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Calendar calendar = Calendar.getInstance();
                int yy = calendar.get(Calendar.YEAR);
                int mm = calendar.get(Calendar.MONTH);
                int dd = calendar.get(Calendar.DAY_OF_MONTH);
                DatePickerDialog datePicker = new DatePickerDialog(UserDetails.this, new DatePickerDialog.OnDateSetListener() {

                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        dob = String.valueOf(year) + "-" + String.valueOf(monthOfYear + 1) + "-" + String.valueOf(dayOfMonth);
                        dob_e.setText(dob);
                    }
                }, yy, mm, dd);
                datePicker.getDatePicker().setMaxDate(new Date().getTime());
                datePicker.show();
            }
        });



        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                name=name_e.getText().toString().trim();
                dob=dob_e.getText().toString().trim();
                int count=0;

                if (name.equals("")){
                    name_e.setError("Enter name");
                    count++;
                }else if (!name.matches("^[ A-Za-z]+$")){
                    name_e.setError("Enter valid name");
                    count++;
                }

                if (dob.equals("")){
                    dob_e.setError("Select dob");
                    count++;
                }

                if (count==0){
                    saveprofile();
                }
            }
        });
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100 && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri uri = data.getData();
            Bitmap bitmap = null;
            try {
                bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri);
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (bitmap!=null) {
                ppic.setImageBitmap(bitmap);
                uploadppic(uri, bitmap);
            }
        }
    }



    private void saveprofile(){
        progressBar.setVisibility(View.VISIBLE);
        next.setClickable(false);
        manager.setName(name).setGender(gender).setDob(dob).updateprofile().addOnCompleteListener(new Manager.OnCompleteListener() {
            @Override
            public void OnComplete(boolean task, String result) {
                progressBar.setVisibility(View.GONE);
                next.setClickable(true);
                if (task){
                    startActivity(new Intent(UserDetails.this,MainActivity.class));
                    finish();
                }else {
                    Snackbar.make(progressBar,result,Snackbar.LENGTH_LONG).show();
                }
            }
        });
    }

    private void uploadppic(Uri file, final Bitmap bitmap){
        progressBar.setVisibility(View.VISIBLE);
        next.setClickable(false);
        final StorageReference reference= FirebaseStorage.getInstance().getReference().child("ProfilePictures/"+ manager.getUsername());
        reference.putFile(file).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        manager.setProfilepicurl(uri.toString()).updateprofile().addOnCompleteListener(new Manager.OnCompleteListener() {
                            @Override
                            public void OnComplete(boolean task, String result) {
                                progressBar.setVisibility(View.GONE);
                                next.setClickable(true);
                                if (task){
                                    manager.saveProfileImage(bitmap);
                                    Toast.makeText(UserDetails.this, "Picture uploaded", Toast.LENGTH_SHORT).show();
                                }else {
                                    reference.delete();
                                    manager.setProfilepicurl("");
                                    Toast.makeText(UserDetails.this, "upload failed", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressBar.setVisibility(View.GONE);
                        next.setClickable(true);
                        Toast.makeText(UserDetails.this, "failed to upload picture", Toast.LENGTH_SHORT).show();
                        ppic.setImageDrawable(getDrawable(R.drawable.dppic));
                        reference.delete();
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressBar.setVisibility(View.GONE);
                next.setClickable(true);
                Toast.makeText(UserDetails.this, "failed to upload picture", Toast.LENGTH_SHORT).show();
                ppic.setImageDrawable(getDrawable(R.drawable.dppic));
            }
        });
    }
}
