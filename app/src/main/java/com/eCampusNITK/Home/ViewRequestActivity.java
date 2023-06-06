package com.eCampusNITK.Home;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.eCampusNITK.Core.FcmNotificationsSender;
import com.eCampusNITK.Core.RealTimeDatabaseManager;
import com.eCampusNITK.Models.ChatMessage;
import com.eCampusNITK.Models.Connections;
import com.eCampusNITK.Models.DeviceToken;
import com.eCampusNITK.Models.Suggestions;
import com.eCampusNITK.Models.User;
import com.eCampusNITK.Models.UserID;
import com.eCampusNITK.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ViewRequestActivity extends AppCompatActivity {


    RecyclerView requestsRecyclerView;
    ArrayList<Suggestions> requestArrayList;
    ViewRequestAdapter    myRequestsAdapter;

    User user;
    String fcmKey = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            setStatusBarTextAndBgColor();
        }
        setContentView(R.layout.activity_view_request);
        requestsRecyclerView = findViewById(R.id.my_requests_recycler_view);
        requestArrayList     = new ArrayList<>();
        user                 = RealTimeDatabaseManager.getInstance().getUser();
        setUpRecyclerView();


    }

    private void setUpRecyclerView() {
        FirebaseDatabase database             = FirebaseDatabase.getInstance();
        DatabaseReference databaseReference   = database.getReference("UserData");

        databaseReference.child(user.getUserID()).child("Request").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists())
                {
                    requestArrayList.clear();
                    for(DataSnapshot dataSnapshot : snapshot.getChildren())
                    {
                        if(dataSnapshot.exists())
                        {
                            Suggestions request = dataSnapshot.getValue(Suggestions.class);
                            if(request!=null)
                            {
                                requestArrayList.add(request);
                            }
                        }
                    }
                    myRequestsAdapter = new ViewRequestAdapter(ViewRequestActivity.this, requestArrayList);
                    requestsRecyclerView.setLayoutManager(new LinearLayoutManager(ViewRequestActivity.this, LinearLayoutManager.VERTICAL, false));
                    requestsRecyclerView.setAdapter(myRequestsAdapter);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @SuppressLint("NotifyDataSetChanged")
    public void acceptRequest(Suggestions sender)
    {
        Suggestions currUser = new Suggestions(user.getProfilePicture(), user.getName(), user.getCourse() , user.getUserID());
        FirebaseDatabase database                = FirebaseDatabase.getInstance();
        DatabaseReference requestReference       = database.getReference("UserData").child(user.getUserID()).child("Request");
        DatabaseReference senderRequestReference = database.getReference("UserData").child(sender.getUser_ID()).child("SentRequest");
        DatabaseReference receiverReference      = database.getReference("UserData").child(user.getUserID()).child("Connections");
        DatabaseReference senderReference        = database.getReference("UserData").child(sender.getUser_ID()).child("Connections");
        DatabaseReference senderChatReference    = database.getReference("Chats").child(sender.getUser_ID()).child(user.getUserID());
        DatabaseReference UserChatReference      = database.getReference("Chats").child(user.getUserID()).child(sender.getUser_ID());
        requestReference.child(sender.getUser_ID()).setValue(null);
        senderRequestReference.child(user.getUserID()).setValue(null);
        receiverReference.child(sender.getUser_ID()).setValue(new UserID(sender.getUser_ID()));
        senderReference.child(currUser.getUser_ID()).setValue(new UserID(currUser.getUser_ID()));
        String currTime  = String.valueOf(System.currentTimeMillis());
        ChatMessage firstMessage = new ChatMessage(sender.getUser_ID(),user.getUserID(),
                "Tap here to chat",currTime,0,"",2,"");
        requestArrayList.remove(sender);
        String key1 = senderChatReference.push().getKey();
        String key2 = UserChatReference.push().getKey();
        if(key1!=null && key2!=null)
        {
            senderChatReference.child(key1).setValue(firstMessage);
            UserChatReference.child(key2).setValue(firstMessage);
            sendNotification(sender.getUser_ID());
        }
        myRequestsAdapter.notifyDataSetChanged();
    }

    private void sendNotification(String RequestSenderUserId) {
        DatabaseReference db = FirebaseDatabase.getInstance().getReference();
        db.child("Tokens").child(RequestSenderUserId).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if(task.isSuccessful())
                {
                    DeviceToken deviceToken = task.getResult().getValue(DeviceToken.class);
                    if(deviceToken!=null)
                    {
                        try{
                            FcmNotificationsSender fcmNotificationsSender = new
                                    FcmNotificationsSender(deviceToken.getToken(),
                                    "Request Accepted ",""+user.getName()+" has accepted your Request. CLick here to chat with your new Connection",
                                    ViewRequestActivity.this,ViewRequestActivity.this,fcmKey);
                            fcmNotificationsSender.SendNotifications();
                        }catch (Exception e)
                        {
                            Toast.makeText(ViewRequestActivity.this, ""+e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                        }

                    }
                }
            }
        });

    }

    @SuppressLint("NotifyDataSetChanged")
    public void rejectRequest(Suggestions sender)
    {
        FirebaseDatabase database                = FirebaseDatabase.getInstance();
        DatabaseReference requestReference       = database.getReference("UserData").child(user.getUserID()).child("Request");
        DatabaseReference senderReference        = database.getReference("UserData").child(sender.getUser_ID()).child("SentRequest");
        requestReference.child(sender.getUser_ID()).setValue(null);
        senderReference.child(user.getUserID()).setValue(null);
        requestArrayList.remove(sender);
        myRequestsAdapter.notifyDataSetChanged();
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    void setStatusBarTextAndBgColor(){
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);//  set status text dark
        window.setStatusBarColor(ContextCompat.getColor(this,R.color.lightGrey));// set status background white
    }
}