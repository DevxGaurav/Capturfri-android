package com.capturfri;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

public class Launch extends AppCompatActivity {

    private Manager manager;
    private ImageView logo;
    private String[] PERMISSIONS = {android.Manifest.permission.ACCESS_COARSE_LOCATION, android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.READ_EXTERNAL_STORAGE, android.Manifest.permission.WRITE_EXTERNAL_STORAGE};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launch);
        manager=Manager.getInstance().init(getApplicationContext());
        logo=findViewById(R.id.logo_launch);
        getpermission();
    }



    private void getpermission(){
        if(!hasPermissions()){
            ActivityCompat.requestPermissions(this, PERMISSIONS, 1);
        }else{
            if (manager.getUsername().equals("") || manager.getPassword().equals("")){
                startActivity(new Intent(Launch.this,SignIn.class));
                finish();
            }else {
                login();
            }
        }
    }


    private boolean hasPermissions() {
        for (int i=0;i<PERMISSIONS.length;i++) {
            if (ActivityCompat.checkSelfPermission(this, PERMISSIONS[i]) != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if(requestCode==1){
            if(hasPermissions()){
                if (manager.getUsername().equals("") || manager.getPassword().equals("")){
                    startActivity(new Intent(Launch.this,SignIn.class));
                    finish();
                }else {
                    login();
                }
            }else{
                Snackbar.make(logo,"Capturfri require the permissions to run", Snackbar.LENGTH_INDEFINITE).setAction("Grant permissions", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        getpermission();
                    }
                }).show();
            }
        }
    }


    private void login(){
        manager.login(manager.getUsername(),manager.getPassword()).addOnCompleteListener(new Manager.OnCompleteListener() {
            @Override
            public void OnComplete(boolean task, String result) {
                if (task){
                    if (manager.getName().equals("")){
                        startActivity(new Intent(Launch.this,UserDetails.class));
                        finish();
                    }else {
                        startActivity(new Intent(Launch.this,MainActivity.class));
                        finish();
                    }
                }else {
                    if (result.toLowerCase().equals("connection error")){
                        startActivity(new Intent(Launch.this,MainActivity.class));
                        finish();
                    }else {
                        startActivity(new Intent(Launch.this,SignIn.class));
                        finish();
                    }
                }
            }
        });
    }
}
