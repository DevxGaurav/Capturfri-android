    package com.capturfri;

import android.content.Intent;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class SignIn extends AppCompatActivity {

    private Manager manager;
    private ProgressBar progressBar_login;
    private ProgressBar progressBar_signup;
    private boolean doubleBackToExitPressedOnce = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
        getWindow().setStatusBarColor(ContextCompat.getColor(SignIn.this,R.color.colorAccent));
        manager=Manager.getInstance();

        ConstraintLayout loginsheet=findViewById(R.id.login_sheet_signin);
        ConstraintLayout signupsheet=findViewById(R.id.signup_sheet_signin);
        final BottomSheetBehavior login_behavior=BottomSheetBehavior.from(loginsheet);
        final BottomSheetBehavior signup_behavior=BottomSheetBehavior.from(signupsheet);
        ConstraintLayout login_toolbar=findViewById(R.id.toolbar_login_signin);
        final ImageView back_login=findViewById(R.id.back_login_signin);
        final ImageView back_signup=findViewById(R.id.back_signup_signin);

        back_login.setVisibility(View.GONE);
        back_signup.setVisibility(View.GONE);

        final TextInputEditText username_login=findViewById(R.id.username_login_signin);
        final TextInputEditText password_login=findViewById(R.id.password_login_signin);
        password_login.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);
        password_login.setTransformationMethod(PasswordTransformationMethod.getInstance());
        final TextInputLayout p_login=findViewById(R.id.t2_login_signin);

        final TextInputEditText username_signup=findViewById(R.id.username_signup_signin);
        final TextInputEditText password_signup=findViewById(R.id.password_signup_signin);
        password_signup.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);
        password_signup.setTransformationMethod(PasswordTransformationMethod.getInstance());
        final TextInputLayout p_signup=findViewById(R.id.t2_signup_signin);

        FloatingActionButton login_fab=findViewById(R.id.loginfab_login_signin);
        FloatingActionButton signup_fab=findViewById(R.id.signupfab_signup_signin);

        TextView fp=findViewById(R.id.fp_login_signin);
        TextView signup_txt=findViewById(R.id.signup_login_signin);

        progressBar_login=findViewById(R.id.progressBar_login_signin);
        progressBar_signup=findViewById(R.id.progressBar_signup_signin);


        login_behavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View view, int i) {
                if (i==BottomSheetBehavior.STATE_EXPANDED){
                    back_login.setVisibility(View.VISIBLE);
                }else if (i==BottomSheetBehavior.STATE_DRAGGING){
                    back_login.setVisibility(View.GONE);
                    username_login.setError(null);
                    password_login.set  Error(null);

                }
            }

            @Override
            public void onSlide(@NonNull View view, float v) {

            }
        });


        signup_behavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View view, int i) {
                if (i==BottomSheetBehavior.STATE_EXPANDED){
                    back_signup.setVisibility(View.VISIBLE);
                }else if (i==BottomSheetBehavior.STATE_DRAGGING){
                    back_signup.setVisibility(View.GONE);
                    username_signup.setError(null);
                    password_signup.setError(null);
                }
            }

            @Override
            public void onSlide(@NonNull View view, float v) {

            }
        });


        login_toolbar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                login_behavior.setState(BottomSheetBehavior.STATE_EXPANDED);
            }
        });


        back_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                login_behavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                username_login.setError(null);
                password_login.setError(null);
                back_login.setVisibility(View.GONE);
            }
        });

        back_signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signup_behavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                username_signup.setError(null);
                password_signup.setError(null);
                back_signup.setVisibility(View.GONE);
            }
        });


        fp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //forgot password
            }
        });

        signup_txt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                username_login.setError(null);
                password_login.setError(null);
                signup_behavior.setState(BottomSheetBehavior.STATE_EXPANDED);
            }
        });

        username_login.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                username_login.setError(null);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        password_login.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                password_login.setError(null);
                p_login.setPasswordVisibilityToggleEnabled(true);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });


        username_signup.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                username_signup.setError(null);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });


        password_signup.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                password_signup.setError(null);
                p_signup.setPasswordVisibilityToggleEnabled(true);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });


        login_fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String username=username_login.getText().toString().trim().toLowerCase();
                String password=password_login.getText().toString().trim();
                int count=0;


                if (username.equals("")){
                    username_login.setError("Enter username");
                    count++;
                }

                if (password.equals("")){
                    p_login.setPasswordVisibilityToggleEnabled(false);
                    password_login.setError("Enter Password");
                    count++;
                }

                if (count==0){
                    //login
                    login(username,password);
                }
            }
        });



        signup_fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String username=username_signup.getText().toString().trim().toLowerCase();
                String password=password_signup.getText().toString().trim();
                int count=0;

                if (username.equals("")){
                    username_signup.setError("Enter username");
                    count++;
                }else if (!username.matches("^[a-z0-9_]{3,15}$")){
                    username_signup.setError("Username should only contain alphabets, numbers or underscore(_) and should have a length between 3 to 15");
                    count++;
                }else if (!(username.charAt(0)>='a' && username.charAt(0)<='z')){
                    username_signup.setError("username should only start with a alphabet");
                    count++;
                }


                if (password.equals("")){
                    p_signup.setPasswordVisibilityToggleEnabled(false);
                    password_signup.setError("Enter password");
                    count++;
                }else if (password.length()<6){
                    p_signup.setPasswordVisibilityToggleEnabled(false);
                    password_signup.setError("Password must have atleast 6 characters");
                    count++;
                }


                if (count==0){
                    //signup
                    signup(username,password);
                }
            }
        });
    }


    private void signup(String username, String password){
        progressBar_signup.setVisibility(View.VISIBLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        manager.signup(username,password).addOnCompleteListener(new Manager.OnCompleteListener() {
            @Override
            public void OnComplete(boolean task, String result) {
                progressBar_signup.setVisibility(View.GONE);
                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                if (task){
                    startActivity(new Intent(SignIn.this,UserDetails.class));
                    finish();
                }else {
                    Snackbar.make(progressBar_signup,result,Snackbar.LENGTH_LONG).show();
                }
            }
        });
    }

        
    private void login(String username, String password){
        progressBar_login.setVisibility(View.VISIBLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        manager.login(username,password).addOnCompleteListener(new Manager.OnCompleteListener() {
            @Override
            public void OnComplete(boolean task, String result) {
                progressBar_login.setVisibility(View.GONE);
                    
                if (task){
                    if (manager.getName().equals("")){
                        startActivity(new Intent(SignIn.this,UserDetails.class));
                        finish();
                    }else {
                        startActivity(new Intent(SignIn.this, MainActivity.class));
                        finish();
                    }
                }else {
                    Snackbar.make(progressBar_login,result,Snackbar.LENGTH_LONG).show();
                }
            }
        });
    }


    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_HOME);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        } else {

            this.doubleBackToExitPressedOnce = true;
            Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show();

            new Handler().postDelayed(new Runnable() {

                @Override
                public void run() {
                    doubleBackToExitPressedOnce = false;
                }
            }, 2000);
        }
    }
}
