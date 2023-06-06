package com.eCampusNITK.Home;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.animation.Animator;
import android.annotation.SuppressLint;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.eCampusNITK.Core.RealTimeDatabaseManager;
import com.eCampusNITK.Models.ChatMessage;
import com.eCampusNITK.Models.Connections;
import com.eCampusNITK.Models.User;
import com.eCampusNITK.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;
import com.zegocloud.uikit.prebuilt.call.invite.widget.ZegoSendCallInvitationButton;
import com.zegocloud.uikit.service.defines.ZegoUIKitUser;

import java.util.ArrayList;
import java.util.Collections;

public class ChatActivity extends AppCompatActivity {

    User         user;
    String       otherPersonUserId;
    ImageButton  sendFileOptions, sendBtn, goback;
    ImageView    personImage;
    TextView     personName;
    CardView     fileSharingOptionsCard;
    EditText     message_text_et;
    RecyclerView chats_recycler_view;
    DatabaseReference databaseReference;
    DatabaseReference chatRef1,chatRef2;
    ArrayList<ChatMessage> chatMessageArrayList = new ArrayList<>();
    ChatMessageAdapter     chatMessageAdapter;
    boolean                fileSharingLayoutHidden=false;
    ZegoSendCallInvitationButton voiceCallBtn,videoCallBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStatusBarTextAndBgColor();
        setContentView(R.layout.activity_chat);
        findIds();
        setUpRecyclerView();
        chatRef1 = FirebaseDatabase.getInstance().getReference("Chats").child(user.getUserID()).child(otherPersonUserId);
        chatRef2 = FirebaseDatabase.getInstance().getReference("Chats").child(otherPersonUserId).child(user.getUserID());
        sendBtn.setOnClickListener(v -> {
            if(!message_text_et.getText().toString().trim().isEmpty())
            {
                ChatMessage chatMessage = new ChatMessage(user.getUserID(),
                        otherPersonUserId,message_text_et.getText().toString().trim(),
                        ""+System.currentTimeMillis(),1,"No File",2,"None");
                sendMessage(chatMessage);
            }
        });


        sendFileOptions.bringToFront();
        sendFileOptions.setOnClickListener(v -> {
            fileSharingLayoutHidden = !fileSharingLayoutHidden;
            if(fileSharingLayoutHidden)
            {showFileSharingCard(); }
            else { hideFileSharingCard(); }
        });

        goback.setOnClickListener(v -> finish());

        setVoiceCallBtn();
        setVideoCallBtn();
    }

    private void sendMessage(ChatMessage chatMessage) {
        String key1 = chatRef1.push().getKey();
        String key2 = chatRef2.push().getKey();
        if(key1!=null && key2!=null) {
            chatRef1.child(key1).setValue(chatMessage);
            chatRef2.child(key2).setValue(chatMessage);
            message_text_et.setText("");
        }
    }

    private void setUpRecyclerView() {
        databaseReference = FirebaseDatabase.getInstance().getReference("Chats").child(user.getUserID()).child(otherPersonUserId);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists())
                {
                    chatMessageArrayList.clear();
                    for(DataSnapshot dataSnapshot: snapshot.getChildren())
                    {
                        if(dataSnapshot.exists())
                        {
                            ChatMessage chatMessage = dataSnapshot.getValue(ChatMessage.class);
                            if(chatMessage!=null) chatMessageArrayList.add(chatMessage);
                        }

                    }
                    chatMessageAdapter = new ChatMessageAdapter(ChatActivity.this , chatMessageArrayList);
                    chats_recycler_view.setLayoutManager(new LinearLayoutManager(ChatActivity.this, LinearLayoutManager.VERTICAL, false));
                    chats_recycler_view.setAdapter(chatMessageAdapter);
                    chats_recycler_view.scrollToPosition(chatMessageArrayList.size()-1);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void findIds() {
        otherPersonUserId      = getIntent().getExtras().getString("personUserID");
        user                   = RealTimeDatabaseManager.getInstance().getUser();
        sendFileOptions        = findViewById(R.id.send_file_options);
        sendBtn                = findViewById(R.id.send_btn);
        goback                 = findViewById(R.id.go_back);
        personImage            = findViewById(R.id.chat_person_image);
        personName             = findViewById(R.id.chat_person_name);
        fileSharingOptionsCard = findViewById(R.id.file_sharing_select_cardview);
        message_text_et        = findViewById(R.id.message_edit_text);
        chats_recycler_view    = findViewById(R.id.chats_recycler_view);
        voiceCallBtn           = findViewById(R.id.voiceCallBtn);
        videoCallBtn           = findViewById(R.id.videoCallBtn);
        setPersonImageAndName();
    }

    private void setPersonImageAndName()
    {
        ArrayList<Connections> connectionsArrayList;
        connectionsArrayList = RealTimeDatabaseManager.getInstance().getConnectionsArrayList();
        for(int i=0;i<connectionsArrayList.size();i++)
        {
            if(connectionsArrayList.get(i).getConnection_id().equals(otherPersonUserId))
            {
                personName.setText(connectionsArrayList.get(i).getUser_name());
                Picasso.get().
                        load(connectionsArrayList.get(i).getUser_img()).
                        fit().
                        centerCrop().
                        networkPolicy(NetworkPolicy.OFFLINE).placeholder(R.drawable.no_user).into(personImage);
                break;
            }
        }
    }

    private void showFileSharingCard()
    {
        float radius = Math.max(fileSharingOptionsCard.getWidth(),fileSharingOptionsCard.getHeight());
        Animator animator = ViewAnimationUtils.createCircularReveal(fileSharingOptionsCard,fileSharingOptionsCard.getLeft(),
                fileSharingOptionsCard.getTop(),0,radius*3);
        animator.setDuration(1000);
        fileSharingOptionsCard.setVisibility(View.VISIBLE);
        animator.start();
    }

    private void hideFileSharingCard()
    {
        float radius = Math.max(fileSharingOptionsCard.getWidth(),fileSharingOptionsCard.getHeight());
        Animator animator = ViewAnimationUtils.createCircularReveal(fileSharingOptionsCard,fileSharingOptionsCard.getLeft(),
                fileSharingOptionsCard.getTop(),radius*3,0);
        animator.setDuration(1000);
        animator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(@NonNull Animator animation) {

            }

            @Override
            public void onAnimationEnd(@NonNull Animator animation) {
                fileSharingOptionsCard.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationCancel(@NonNull Animator animation) {

            }

            @Override
            public void onAnimationRepeat(@NonNull Animator animation) {

            }
        });
        animator.start();
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    public void setVoiceCallBtn()
    {
        try{
            voiceCallBtn.setIsVideoCall(false);
            voiceCallBtn.setBackground(getDrawable(R.drawable.baseline_phone_24));
            voiceCallBtn.setInvitees(Collections.singletonList(new ZegoUIKitUser(otherPersonUserId,personName.getText().toString().trim())));
        }catch (Exception e)
        {
            Toast.makeText(this, ""+e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    public void setVideoCallBtn()
    {
        try{
            videoCallBtn.setIsVideoCall(true);
            videoCallBtn.setBackground(getDrawable(R.drawable.baseline_video_camera_back_24));
            videoCallBtn.setInvitees(Collections.singletonList(new ZegoUIKitUser(otherPersonUserId,personName.getText().toString().trim())));
        }catch (Exception e)
        {
            Toast.makeText(this, ""+e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
        }

    }

    void setStatusBarTextAndBgColor(){
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.setStatusBarColor(this.getResources().getColor(R.color.black));
        View decorView = window.getDecorView();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            decorView.setSystemUiVisibility(decorView.getSystemUiVisibility() & ~View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR); //set status text  light
        }
    }
}