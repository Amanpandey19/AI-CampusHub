package com.eCampusNITK.Home;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Toast;

import com.eCampusNITK.Core.RealTimeDatabaseManager;
import com.eCampusNITK.Models.User;
import com.eCampusNITK.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


@SuppressLint("CustomSplashScreen")
public class SplashActivity extends AppCompatActivity {

    CardView splashLogo;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            setStatusBarTextAndBgColor();
        }
        setContentView(R.layout.activity_splash);
        splashLogo  = findViewById(R.id.splash_logo_card);
        Animation animation= AnimationUtils.loadAnimation(SplashActivity.this,R.anim.animate_card_enter);
        splashLogo.setAnimation(animation);

        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if(firebaseUser!=null){
            if(firebaseUser.isEmailVerified()) {
                RealTimeDatabaseManager.getInstance().downloadCurrentUserDetails(SplashActivity.this, firebaseUser.getUid(), data -> RealTimeDatabaseManager.getInstance().getAllUsers((User) data, userArrayList -> RealTimeDatabaseManager.getInstance().downloadConnections((User) data, data1 -> {
                    Intent i = new Intent(SplashActivity.this,WelcomeActivity.class);
                    startActivity(i);
                    finish();
                })));

                RealTimeDatabaseManager.getInstance().downloadAllSubjects(SplashActivity.this, data -> {
                    // All Subjects Downloaded from DataBase;
                });

            }
            else {
                Toast.makeText(SplashActivity.this, "Email id is not verified", Toast.LENGTH_SHORT).show();
                Intent i = new Intent(SplashActivity.this,LoginActivity.class);
                startActivity(i);
                finish();
            }
        }else
        {
            Intent i = new Intent(SplashActivity.this,LoginActivity.class);
            startActivity(i);
            finish();
            //Toast.makeText(LoginActivity.this, "firebase user is null", Toast.LENGTH_SHORT).show();
        }
    }



    @RequiresApi(api = Build.VERSION_CODES.M)
    void setStatusBarTextAndBgColor(){
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.setStatusBarColor(this.getResources().getColor(R.color.lightGrey));
        window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR); //set status text  light
    }

}