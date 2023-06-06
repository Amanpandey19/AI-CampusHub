package com.eCampusNITK.Home;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.eCampusNITK.Core.RealTimeDatabaseManager;
import com.eCampusNITK.Models.User;
import com.eCampusNITK.R;
import com.google.android.material.bottomappbar.BottomAppBar;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.squareup.picasso.Picasso;
import com.zegocloud.uikit.prebuilt.call.config.ZegoNotificationConfig;
import com.zegocloud.uikit.prebuilt.call.invite.ZegoUIKitPrebuiltCallInvitationConfig;
import com.zegocloud.uikit.prebuilt.call.invite.ZegoUIKitPrebuiltCallInvitationService;

import de.hdodenhof.circleimageview.CircleImageView;


public class HomeActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {

    BottomNavigationView bottomNavigationView;
    BottomAppBar         bottomAppBar;
    FloatingActionButton chatBtn;
    User user;
    TextView userName,phoneNumber;
    CircleImageView userImage;

    @SuppressLint("NonConstantResourceId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        bottomNavigationView = findViewById(R.id.bottom_navigation);
        chatBtn              = findViewById(R.id.chat_fab);
        bottomAppBar         = findViewById(R.id.bottom_app_bar);

        bottomNavigationView.setBackground(null);
        bottomNavigationView.setOnNavigationItemSelectedListener(this);
        bottomNavigationView.setSelectedItemId(R.id.home);
        user = RealTimeDatabaseManager.getInstance().getUser();
        startService(user.getUserID());



        //Side navigation
        NavigationView navigationView = findViewById(R.id.navigation);
        userName     = navigationView.getHeaderView(0).findViewById(R.id.user_name);
        phoneNumber  = navigationView.getHeaderView(0).findViewById(R.id.user_number);
        userImage    = navigationView.getHeaderView(0).findViewById(R.id.user_image);

        userName.setText(user.getName());
        phoneNumber.setText(user.getPhoneNumber());
        if(!user.getProfilePicture().equals("")) Picasso.get()
                .load(user.getProfilePicture())
                .into(userImage);

        navigationView.setNavigationItemSelectedListener(item -> {
            DrawerLayout drawer = findViewById(R.id.drawerLayout);
            switch (item.getItemId()) {
                case R.id.option_home:
                    drawer.closeDrawer(GravityCompat.START);
                    bottomNavigationView.setSelectedItemId(R.id.home);
                    return true;

                case R.id.option_my_profile:
                    drawer.closeDrawer(GravityCompat.START);
                    bottomNavigationView.setSelectedItemId(R.id.account);
                    return true;

                case R.id.option_customer_support:
                    drawer.closeDrawer(GravityCompat.START);
                    Intent intent = new Intent(HomeActivity.this, ChatBotActivity.class);
                    startActivity(intent);
                    return true;

                case R.id.option_logout:
                    drawer.closeDrawer(GravityCompat.START);
                    FirebaseAuth.getInstance().signOut();
                    Intent i = new Intent(HomeActivity.this,LoginActivity.class);
                    startActivity(i);
                    finish();
                    return true;

                case R.id.option_privacy_policy:
                    drawer.closeDrawer(GravityCompat.START);
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.freeprivacypolicy.com/live/17f0016c-5479-4ecb-820c-3e3b9b428257"));
                    startActivity(browserIntent);
                    return true;
            }
            drawer.closeDrawer(GravityCompat.START);
            return true;
        });

        //Scan (Floating Action Button)
        chatBtn.setOnClickListener(view -> {
            Intent i = new Intent(HomeActivity.this,ChatHomeActivity.class);
            startActivity(i);
            overridePendingTransition(R.anim.animate_left_to_right,R.anim.animate_right_to_left);
        });

    }


    //Bottom Navigation
    HomeFragment homeFragment = new HomeFragment();
    ProfileFragment profileFragment = new ProfileFragment();

    SuggestionsFragment suggestionsFragment = new SuggestionsFragment();
    MyClassFragment  myClassFragment = new MyClassFragment();

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.home:
                getSupportFragmentManager().beginTransaction().replace(R.id.flFragment, homeFragment).commit();
                return true;

            case R.id.account:
                getSupportFragmentManager().beginTransaction().replace(R.id.flFragment, profileFragment).commit();
                return true;

            case R.id.classes:
                getSupportFragmentManager().beginTransaction().replace(R.id.flFragment, myClassFragment).commit();
                return true;

            case R.id.suggestion:
                getSupportFragmentManager().beginTransaction().replace(R.id.flFragment, suggestionsFragment).commit();
                return true;
        }
        return false;
    }


    public void startService(String userID)
    {
        long appID = 0;   // yourAppID
        String appSign = "";  // yourAppSign
        String userName = user.getName();   // yourUserName

        ZegoUIKitPrebuiltCallInvitationConfig callInvitationConfig = new ZegoUIKitPrebuiltCallInvitationConfig();
        callInvitationConfig.notifyWhenAppRunningInBackgroundOrQuit = true;
        ZegoNotificationConfig notificationConfig = new ZegoNotificationConfig();
        notificationConfig.sound = "zego_uikit_sound_call";
        notificationConfig.channelID = "CallInvitation";
        notificationConfig.channelName = "CallInvitation";
        ZegoUIKitPrebuiltCallInvitationService.init(getApplication(), appID, appSign, userID, userName,callInvitationConfig);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        ZegoUIKitPrebuiltCallInvitationService.unInit();
    }
}