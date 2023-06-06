package com.eCampusNITK.Home;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.eCampusNITK.Core.RealTimeDatabaseManager;
import com.eCampusNITK.Models.Connections;
import com.eCampusNITK.Models.User;
import com.eCampusNITK.R;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class ViewConnectionsActivity extends AppCompatActivity {


    RecyclerView connectionsRecyclerView;
    ArrayList<Connections> connectionsArrayList;
    ViewConnectionsAdapter    myConnectionAdapter;
    User user;

    @SuppressLint("UseCompatLoadingForDrawables")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            setStatusBarTextAndBgColor();
        }
        setContentView(R.layout.activity_view_connections);

        connectionsRecyclerView = findViewById(R.id.my_connections_recycler_view);
        connectionsArrayList    = new ArrayList<>();
        user                    = RealTimeDatabaseManager.getInstance().getUser();
        setUpRecyclerView();
    }

    private void setUpRecyclerView() {
        final ProgressDialog pd=new ProgressDialog(ViewConnectionsActivity.this);
        pd.setMessage("Please wait..");
        pd.setCanceledOnTouchOutside(false);
        pd.show();
        RealTimeDatabaseManager.getInstance().downloadConnections(user, data -> {
            try{
                myConnectionAdapter = new ViewConnectionsAdapter(ViewConnectionsActivity.this, data);
                connectionsRecyclerView.setLayoutManager(new LinearLayoutManager(ViewConnectionsActivity.this, LinearLayoutManager.VERTICAL, false));
                connectionsRecyclerView.setAdapter(myConnectionAdapter);
                pd.dismiss();

            }catch (Exception e)
            {
                pd.dismiss();
                Log.d(ViewConnectionsActivity.this.getClass().getName(), "Error in connections array list adapter ");
            }
        });
    }

    @SuppressLint("NotifyDataSetChanged")
    public void removeConnection(String connectionId)
    {
        FirebaseDatabase database                    = FirebaseDatabase.getInstance();
        DatabaseReference userReference              = database.getReference("UserData").child(user.getUserID()).child("Connections");
        DatabaseReference connectionReference        = database.getReference("UserData").child(connectionId).child("Connections");
        DatabaseReference chatRef1 = FirebaseDatabase.getInstance().getReference("Chats").child(user.getUserID()).child(connectionId);
        DatabaseReference chatRef2 = FirebaseDatabase.getInstance().getReference("Chats").child(connectionId).child(user.getUserID());

        try {
            chatRef1.setValue(null);
            chatRef2.setValue(null);
            userReference.child(connectionId).setValue(null);
            connectionReference.child(user.getUserID()).setValue(null);
            setUpRecyclerView();

        }catch (Exception e)
        {
            Log.d(ViewConnectionsActivity.this.getClass().getName(),"Error in removing connections");
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    void setStatusBarTextAndBgColor(){
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);//  set status text dark
        window.setStatusBarColor(ContextCompat.getColor(this,R.color.grey_theme));// set status background white
    }
}