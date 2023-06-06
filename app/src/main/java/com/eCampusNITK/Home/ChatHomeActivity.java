package com.eCampusNITK.Home;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.eCampusNITK.Core.RealTimeDatabaseManager;
import com.eCampusNITK.Models.ChatMessage;
import com.eCampusNITK.Models.ChatPerson;
import com.eCampusNITK.Models.Connections;
import com.eCampusNITK.Models.User;
import com.eCampusNITK.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;

public class ChatHomeActivity extends AppCompatActivity {

    ArrayList<ChatPerson> chatPersonArrayList;
    ChatHomeAdapter chatHomeAdapter;
    RecyclerView chatListRecyclerView;
    SearchView searchView;
    DatabaseReference dbRef;
    User              user;
    ArrayList<Connections> Connections = new ArrayList<>();



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            setStatusBarTextAndBgColor();
        }
        setContentView(R.layout.activity_chat_home);

        setTitle("Chats");
        chatListRecyclerView   = findViewById(R.id.chat_list_recyclerView);
        chatPersonArrayList    = new ArrayList<>();
        user                   = RealTimeDatabaseManager.getInstance().getUser();
        Connections            = RealTimeDatabaseManager.getInstance().getConnectionsArrayList();
        dbRef                  = FirebaseDatabase.getInstance().getReference("Chats").child(user.getUserID());
        dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if(snapshot.exists())
                {
                    String personUserId ;
                    String personImage ;
                    int    notSeenMessages ;
                    chatPersonArrayList.clear();
                    ArrayList<ChatMessage> chatMessages = new ArrayList<>();
                    for (DataSnapshot dataSnapshot: snapshot.getChildren())
                    {
                        if(dataSnapshot.exists())
                        {
                            personUserId    = dataSnapshot.getKey();
                            personImage     = getConnectionProfileImage(personUserId);
                            for(DataSnapshot data: dataSnapshot.getChildren())
                            {
                                if(data.exists())
                                {
                                    ChatMessage chatMessage = data.getValue(ChatMessage.class);
                                    if(chatMessage!=null){
                                        chatMessages.add(chatMessage);
                                    }

                                }
                            }
                            String lastText       =   chatMessages.get(chatMessages.size()-1).getActualMessage();
                            String lastTextTime   =   chatMessages.get(chatMessages.size()-1).getTimeOfMessage();
                            int    lastTextStatus =   chatMessages.get(chatMessages.size()-1).getMessageStatus();
                            String personName     =   getConnectionName(personUserId);
                            String lastPersonId   =   chatMessages.get(chatMessages.size()-1).getSenderID();
                            int    lastTextType   =   chatMessages.get(chatMessages.size()-1).getTypeofMessage();
                            notSeenMessages       =   getNotSeenMessages(chatMessages);

                            ChatPerson chatPerson =  new ChatPerson(personImage,personName,lastTextStatus,
                                    lastText,lastTextTime,""+notSeenMessages,lastPersonId,lastTextType, personUserId);
                            chatPersonArrayList.add(chatPerson);
                        }
                    }
                    Collections.sort(chatPersonArrayList, (o1, o2) -> {
                        if(o1.getLast_text_time().equals( o2.getLast_text_time()))
                            return 0;
                        return Long.parseLong(o1.getLast_text_time()) > Long.parseLong(o2.getLast_text_time()) ? -1 : 1;
                    });
                    chatHomeAdapter = new ChatHomeAdapter(ChatHomeActivity.this ,ChatHomeActivity.this,  chatPersonArrayList);
                    chatListRecyclerView.setLayoutManager(new LinearLayoutManager(ChatHomeActivity.this, LinearLayoutManager.VERTICAL, false));
                    chatListRecyclerView.setAdapter(chatHomeAdapter);
                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });



    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_menu, menu);
        if(getSupportActionBar()!=null) getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.black)));
        MenuItem menuItem = menu.findItem(R.id.search_chat_home);
        searchView = (SearchView) menuItem.getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                chatHomeAdapter.getFilter().filter(newText);
                return false;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }

    public String getConnectionProfileImage(String personId)
    {
        for (int i =0; i<Connections.size();i++)
        {
            if(Connections.get(i).getConnection_id().equals(personId)) return Connections.get(i).getUser_img();
        }
        return "";
    }

    public String getConnectionName(String personId)
    {
        for (int i =0; i<Connections.size();i++)
        {
            if(Connections.get(i).getConnection_id().equals(personId)) return Connections.get(i).getUser_name();
        }
        return "";
    }

    public int getNotSeenMessages(ArrayList<ChatMessage> chats)
    {
        int res=0;
        if(chats.size()==0 || chats.size()==1) return 0;
        for(int i=chats.size()-1; i>=0;i--)
        {
            if(chats.get(i).getMessageStatus()!=2) break;
            res++;
        }

        return res;
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