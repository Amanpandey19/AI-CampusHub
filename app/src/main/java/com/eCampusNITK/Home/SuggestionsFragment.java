package com.eCampusNITK.Home;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.eCampusNITK.Core.FcmNotificationsSender;
import com.eCampusNITK.Core.RealTimeDatabaseManager;
import com.eCampusNITK.Models.Connections;
import com.eCampusNITK.Models.DeviceToken;
import com.eCampusNITK.Models.MySubject;
import com.eCampusNITK.Models.Suggestions;
import com.eCampusNITK.Models.User;
import com.eCampusNITK.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


public class SuggestionsFragment extends Fragment {

    ArrayList<Suggestions> suggestionsArrayList;
    MySuggestionsAdapter mySuggestionsAdapter;
    RecyclerView suggestionsRecyclerView;
    User user;
    TextView viewRequestTv;
    ArrayList<Connections> connectionsArrayList = new ArrayList<>();
    String fcmKey = "";


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v =  inflater.inflate(R.layout.fragment_suggestions, container, false);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            setStatusBarTextAndBgColor();
        }
        return v;
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        suggestionsRecyclerView = view.findViewById(R.id.suggestions_recycler_view);
        viewRequestTv           = view.findViewById(R.id.view_request_tv);
        suggestionsArrayList    = new ArrayList<>();
        user                    = RealTimeDatabaseManager.getInstance().getUser();

        suggestionsArrayList.clear();
        viewRequestTv.setOnClickListener(v -> {
            Intent i = new Intent(getContext(),ViewRequestActivity.class);
            startActivity(i);
            requireActivity().overridePendingTransition(R.anim.animate_left_to_right,R.anim.animate_right_to_left);
        });
        setUpRecyclerView();

    }

    private void setUpRecyclerView() {
        FirebaseDatabase database             = FirebaseDatabase.getInstance();
        connectionsArrayList = RealTimeDatabaseManager.getInstance().getConnectionsArrayList();
        DatabaseReference databaseReference   = database.getReference("User");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists())
                {
                    suggestionsArrayList.clear();
                    for (DataSnapshot postSnapshot: snapshot.getChildren()) {
                        if(postSnapshot.exists()){
                            User otherUser = postSnapshot.getValue(User.class);
                            if(otherUser!=null && !otherUser.getUserID().equals(user.getUserID()) && !isAlreadyAConnection(connectionsArrayList, otherUser.getUserID()))
                            {
                                @SuppressLint("UseCompatLoadingForDrawables") Suggestions suggestion = new Suggestions(otherUser.getProfilePicture(),
                                        otherUser.getName() ,
                                        otherUser.getCourse()+" NIT KKR",
                                        otherUser.getUserID());
                                suggestionsArrayList.add(suggestion);
                            }
                        }
                    }
                    DatabaseReference dbReference   = database.getReference("UserData").child(user.getUserID()).child("SentRequest");
                    dbReference.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if(snapshot.exists()) {
                                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                    if (dataSnapshot.exists()) {
                                        Suggestions sentRequestUser = dataSnapshot.getValue(Suggestions.class);
                                        if (sentRequestUser != null && getIndexOf(sentRequestUser.getUser_ID()) != -1)
                                            suggestionsArrayList.remove(getIndexOf(sentRequestUser.getUser_ID()));
                                    }
                                }
                            }

                                DatabaseReference dbRef   = database.getReference("UserData").child(user.getUserID()).child("Request");
                                dbRef.addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        if(snapshot.exists())
                                        {
                                            for(DataSnapshot dataSnapshot: snapshot.getChildren())
                                            {
                                                if(dataSnapshot.exists())
                                                {
                                                    Suggestions sentRequestUser = dataSnapshot.getValue(Suggestions.class);
                                                    if(sentRequestUser!=null && getIndexOf(sentRequestUser.getUser_ID())!=-1)
                                                        suggestionsArrayList.remove(getIndexOf(sentRequestUser.getUser_ID()));
                                                }
                                            }
                                        }
                                        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleCallback);
                                        itemTouchHelper.attachToRecyclerView(suggestionsRecyclerView);

                                        mySuggestionsAdapter = new MySuggestionsAdapter(getActivity() ,simpleCallback, suggestionsArrayList);
                                        suggestionsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
                                        suggestionsRecyclerView.setAdapter(mySuggestionsAdapter);

                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleCallback);
                                        itemTouchHelper.attachToRecyclerView(suggestionsRecyclerView);

                                        mySuggestionsAdapter = new MySuggestionsAdapter(getActivity() ,simpleCallback, suggestionsArrayList);
                                        suggestionsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
                                        suggestionsRecyclerView.setAdapter(mySuggestionsAdapter);

                                    }
                                });

                                //suggestionsRecyclerView.scheduleLayoutAnimation();

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleCallback);
                            itemTouchHelper.attachToRecyclerView(suggestionsRecyclerView);

                            mySuggestionsAdapter = new MySuggestionsAdapter(getActivity() ,simpleCallback, suggestionsArrayList);
                            suggestionsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
                            suggestionsRecyclerView.setAdapter(mySuggestionsAdapter);

                        }
                    });

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getActivity(), "Could Not Connect To Database, Please Check your Internet Connection", Toast.LENGTH_SHORT).show();
            }
        });
    }

    ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
            return false;
        }

        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
            int position = viewHolder.getAdapterPosition();
            switch (direction)
            {
                case ItemTouchHelper.LEFT:
                    sendRequest(suggestionsArrayList.get(position));
                    Toast.makeText(requireActivity(), "Request Sent", Toast.LENGTH_SHORT).show();
                    break;
                case ItemTouchHelper.RIGHT:
                    removeSuggestions(suggestionsArrayList.get(position));
                    Toast.makeText(requireActivity(), "Removed from suggestions", Toast.LENGTH_SHORT).show();
                    break;
                default:
                    break;
            }
            suggestionsArrayList.remove(position);
            mySuggestionsAdapter.notifyItemRemoved(position);
        }
    };

    public void sendRequest(Suggestions receiver)
    {
        Suggestions sender = new Suggestions(user.getProfilePicture(), user.getName(), user.getCourse(), user.getUserID());
        FirebaseDatabase database             = FirebaseDatabase.getInstance();
        DatabaseReference receiverReference   = database.getReference("UserData").child(receiver.getUser_ID()).child("Request");
        DatabaseReference senderReference     = database.getReference("UserData").child(sender.getUser_ID()).child("SentRequest");
        String key = sender.getUser_ID();
        if(key!=null) receiverReference.child(key).setValue(sender);
        senderReference.child(receiver.getUser_ID()).setValue(receiver);
        sendNotification(receiver.getUser_ID());
    }

    public void removeSuggestions(Suggestions suggestion)
    {
        /*FirebaseDatabase database             = FirebaseDatabase.getInstance();
        DatabaseReference databaseReference   = database.getReference("UserData").child(user.getUserID()).child("Suggestion");
        String key = suggestion.getUser_ID();
        if(key!=null) databaseReference.child(key).setValue(suggestion);*/
    }

    public int getIndexOf(final String userId)
    {
        for(int i=0; i< suggestionsArrayList.size(); i++)
        {
            if(suggestionsArrayList.get(i).getUser_ID().equals(userId))
                return i;
        }
        return -1;
    }

    public boolean isAlreadyAConnection(final List<Connections> connection, final String userId){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            return connection.stream().anyMatch(o -> o.getConnection_id().equals(userId));
        }
        return false;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    void setStatusBarTextAndBgColor(){
        if(getActivity()!=null)
        {
            Window window = getActivity().getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(getActivity().getResources().getColor(R.color.lightGrey));
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);//  set status text dark
        }

    }

    private void sendNotification(String RequestReceiverUserId) {
        DatabaseReference db = FirebaseDatabase.getInstance().getReference();
        db.child("Tokens").child(RequestReceiverUserId).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
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
                                    "New Request ",""+user.getName()+" has sent you a connection Request. Click here to see",
                                    getContext(),getActivity(),fcmKey);
                            fcmNotificationsSender.SendNotifications();
                        }catch (Exception e)
                        {
                            Toast.makeText(getContext(), ""+e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                        }

                    }
                }
            }
        });

    }

}