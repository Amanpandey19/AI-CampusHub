package com.eCampusNITK.Home;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.eCampusNITK.Core.RealTimeDatabaseManager;
import com.eCampusNITK.Models.DeviceToken;
import com.eCampusNITK.Models.User;
import com.eCampusNITK.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessaging;



public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText email,password;
    private FirebaseAuth mAuth;
    private TextView openSignUp, forgotPassword;
    private Button loginBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            setStatusBarTextAndBgColor();
        }
        setContentView(R.layout.activity_login);
        mAuth = FirebaseAuth.getInstance();
        findIds();
        setListeners();
    }

    void setListeners()
    {
        loginBtn.setOnClickListener(this);
        openSignUp.setOnClickListener(this);
        forgotPassword.setOnClickListener(this);
    }

    public boolean isvalid()
    {
        String emailAddress = email.getText().toString().trim();
        if (password.getText().toString().length() < 6) {
            password.setError("password minimum contain 6 character");
            password.requestFocus();
            return false;
        }
        if (password.getText().toString().equals("")) {
            password.setError("please enter password");
            password.requestFocus();
            return false;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(emailAddress).matches()) {
            email.setError("please enter valid email address");
            email.requestFocus();
            return false;
        }
        if (email.getText().toString().equals("")) {
            email.setError("please enter email address");
            email.requestFocus();
            return false;
        }
        return true;
    }

    public void findIds()
    {
        email          = findViewById(R.id.login_email);
        password       = findViewById(R.id.login_password);
        loginBtn       = findViewById(R.id.login_btn);
        openSignUp     = findViewById(R.id.openSignUp);
        forgotPassword = findViewById(R.id.forgotPassword);
    }

    public void login()
    {
        mAuth.signInWithEmailAndPassword(email.getText().toString().trim(), password.getText().toString().trim())
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // Sign in success, update UI with the signed-in user's information
                        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                        if(user!=null && user.isEmailVerified()){
                            FirebaseMessaging.getInstance().getToken().addOnCompleteListener(task1 -> {
                                if(task1.isSuccessful())
                                {
                                    DatabaseReference db = FirebaseDatabase.getInstance().getReference("Tokens");
                                    DeviceToken deviceToken = new DeviceToken();
                                    deviceToken.setToken(task1.getResult());
                                    db.child(user.getUid()).setValue(deviceToken);
                                    afterLogin();
                                }
                            });
                        }else {
                            ableLoginBtn();
                            FirebaseAuth.getInstance().signOut();
                            Toast.makeText(LoginActivity.this, "Your Email is not verified yet, please check your email.", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        ableLoginBtn();
                        // If sign in fails, display a message to the user.
                        Toast.makeText(LoginActivity.this, "You have entered wrong password",
                                Toast.LENGTH_SHORT).show();

                    }
                }).addOnCanceledListener(() -> {
                    ableLoginBtn();
                    Toast.makeText(LoginActivity.this, "Authentication Cancelled.", Toast.LENGTH_SHORT).show();
                });
    }


    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View v)
    {
        switch (v.getId()) {
            case R.id.openSignUp:
                //Launch the Sign up Activity
                Intent i = new Intent(getApplicationContext(),SignUpActivity.class);
                startActivity(i);
                finish();
                overridePendingTransition(R.anim.animate_left_to_right,R.anim.animate_right_to_left);
                break;
            case R.id.login_btn:
                //Perform User Login
                if(isvalid()){
                    disableLoginBtn();
                    login();
                }
                break;

            case R.id.forgotPassword:
                forgetpass();
                default:
                    break;
        }
    }

    private void forgetpass()
    {
        if(email.getText().toString().isEmpty())
        {
            Toast.makeText(this, "Enter Email to send link to reset password", Toast.LENGTH_SHORT).show();
        }else {
            mAuth.sendPasswordResetEmail(email.getText().toString()).addOnCompleteListener(task -> {
                if(task.isSuccessful())
                {
                    Toast.makeText(LoginActivity.this, "Password Link sent to this email ID", Toast.LENGTH_LONG).show();
                }else {
                    Toast.makeText(LoginActivity.this, "Not able to send link", Toast.LENGTH_LONG).show();
                }
            });
        }
    }

    private void afterLogin()
    {
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if(firebaseUser!=null){
            if(firebaseUser.isEmailVerified()) {
                RealTimeDatabaseManager.getInstance().downloadCurrentUserDetails(LoginActivity.this, firebaseUser.getUid(), data -> RealTimeDatabaseManager.getInstance().getAllUsers((User) data, userArrayList -> RealTimeDatabaseManager.getInstance().downloadConnections((User) data, data1 -> {
                    Intent i = new Intent(LoginActivity.this,WelcomeActivity.class);
                    startActivity(i);
                    finish();
                    overridePendingTransition(R.anim.animate_left_to_right,R.anim.animate_right_to_left);
                })));

                RealTimeDatabaseManager.getInstance().downloadAllSubjects(LoginActivity.this, data -> {
                    // All Subjects Downloaded from DataBase;
                });

            }
            else {
                ableLoginBtn();
                Toast.makeText(LoginActivity.this, "Email id is not verified", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private void disableLoginBtn()
    {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            loginBtn.setBackgroundTintList(getColorStateList(R.color.light_black));
            loginBtn.setText("Please wait ...");
            loginBtn.setClickable(false);
            loginBtn.setFocusable(false);
        }
    }

    @SuppressLint("SetTextI18n")
    private void ableLoginBtn()
    {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            loginBtn.setBackgroundTintList(getColorStateList(R.color.black));
            loginBtn.setText("Login");
            loginBtn.setClickable(true);
            loginBtn.setFocusable(true);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    void setStatusBarTextAndBgColor(){
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.setStatusBarColor(this.getResources().getColor(R.color.black));
        View decorView = window.getDecorView();
        decorView.setSystemUiVisibility(decorView.getSystemUiVisibility() & ~View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR); //set status text  light
    }
}