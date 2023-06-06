package com.eCampusNITK.Core;

/*
This is a Singleton class. Object will be instantiated once and will be reused again and again till the app is running.
 */

import android.annotation.SuppressLint;
import android.app.Activity;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.eCampusNITK.Home.AttendanceManagerActivity;
import com.eCampusNITK.Home.SubjectsAdapter;
import com.eCampusNITK.Home.ViewConnectionsActivity;
import com.eCampusNITK.Home.ViewConnectionsAdapter;
import com.eCampusNITK.Models.Connections;
import com.eCampusNITK.Models.MySubject;
import com.eCampusNITK.Models.Posts;
import com.eCampusNITK.Models.Subject;
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

public class RealTimeDatabaseManager {

    private static RealTimeDatabaseManager realTimeDatabaseManager;

    private User user;
    private final ArrayList<MySubject> allSubjects = new ArrayList<>();
    private final ArrayList<Connections> connectionsArrayList = new ArrayList<>();
    private final ArrayList<User>        userArrayList        = new ArrayList<>();
    private final ArrayList<Posts> postsArrayList  = new ArrayList<>();


    public static  RealTimeDatabaseManager getInstance() {
        if(null == realTimeDatabaseManager){
            realTimeDatabaseManager = new RealTimeDatabaseManager();
        }
        return realTimeDatabaseManager;
    }

    public void downloadCurrentUserDetails(Activity activity,String userId, RealtimeDatabaseCallback callback)
    {
        FirebaseDatabase database             = FirebaseDatabase.getInstance();
        DatabaseReference databaseReference   = database.getReference("User");
        DatabaseReference userRef             = databaseReference.child(userId);
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User currentUser = dataSnapshot.getValue(User.class);
                callback.onDataAvailable(currentUser);
                user = currentUser;
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(activity, ""+databaseError, Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void downloadAllSubjects(Activity activity, RealtimeDatabaseCallback callback)
    {
        FirebaseDatabase database             = FirebaseDatabase.getInstance();
        DatabaseReference databaseReference   = database.getReference("StudyMaterial").child("AllSubjects");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists())
                {
                    for (DataSnapshot postSnapshot: snapshot.getChildren()) {
                        if(postSnapshot.exists()){
                            MySubject mySubject = postSnapshot.getValue(MySubject.class);
                            allSubjects.add(mySubject);
                        }
                    }
                }
                callback.onDataAvailable(allSubjects);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(activity, "Could Not Connect To Database, Please Check your Internet Connection", Toast.LENGTH_SHORT).show();
            }
        });
    }


    public ArrayList<MySubject> getAllSubjects()
    {
        return  allSubjects;
    }

    public User getUser()
    { return user; }

    public void setUser(User newUser)
    {
        user = newUser;
    }


    public interface RealtimeDatabaseCallback {
        void onDataAvailable(Object data);
    }


    public interface RealtimeDatabaseConnectionsListCallback {
        void onDataAvailable(ArrayList<Connections> data);
    }
    public void downloadConnections(User currUser, RealtimeDatabaseConnectionsListCallback realtimeDatabaseConnectionsListCallback){
        connectionsArrayList.clear();
        FirebaseDatabase  database                = FirebaseDatabase.getInstance();
        DatabaseReference connectionRef           = database.getReference("UserData").child(currUser.getUserID()).child("Connections");
        connectionRef.get().addOnCompleteListener(task -> {
            connectionsArrayList.clear();
            ArrayList<String> userIds = new ArrayList<>();
            if(task.isSuccessful() && task.getResult().hasChildren())
            {
                for(DataSnapshot dataSnapshot : task.getResult().getChildren())
                {
                    if(dataSnapshot.exists())
                    {
                        UserID userID = dataSnapshot.getValue(UserID.class);
                        if(userID!=null) {
                            User connectionUser = new User();
                            connectionUser = getUser(userID.getUserId());
                            Connections connection = new Connections(connectionUser.getProfilePicture(), connectionUser.getName(),
                                    connectionUser.getCourse(), connectionUser.getUserID());
                            connectionsArrayList.add(connection);
                        }
                    }
                }
                realtimeDatabaseConnectionsListCallback.onDataAvailable(connectionsArrayList);

            }else
            {
                realtimeDatabaseConnectionsListCallback.onDataAvailable(connectionsArrayList);
            }
        });

    }

    public interface RealtimeDatabaseUserListCallback {
        void onDataAvailable(ArrayList<User> userArrayList);
    }
    public void getAllUsers(User CurrUser,RealtimeDatabaseUserListCallback realtimeDatabaseUserListCallback)
    {
        FirebaseDatabase  database                = FirebaseDatabase.getInstance();
        DatabaseReference allConnectionRef = database.getReference().child("User");
        allConnectionRef.get().addOnCompleteListener(task -> {
            if(task.isSuccessful())
            {
                if(task.getResult().exists())
                {
                    for(DataSnapshot dataSnapshot: task.getResult().getChildren())
                    {
                        User user1 = dataSnapshot.getValue(User.class);
                        if(user1!=null && !user1.getUserID().equals(CurrUser.getUserID()))
                            userArrayList.add(user1);
                    }
                    realtimeDatabaseUserListCallback.onDataAvailable(userArrayList);
                }
            }
        });
    }



    public interface RealtimeDatabasePostsListCallback {
        void onDataAvailable(ArrayList<Posts> postsArrayList);
    }
    public void downloadAllPosts(User currUser,Activity activity, RealtimeDatabasePostsListCallback realtimeDatabasePostsListCallback){
        postsArrayList.clear();
        FirebaseDatabase  database                = FirebaseDatabase.getInstance();
        DatabaseReference connectionRef           = database.getReference("AllPosts");
        connectionRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                postsArrayList.clear();
                if(snapshot.exists() && snapshot.hasChildren())
                {
                    for(DataSnapshot dataSnapshot : snapshot.getChildren())
                    {
                        if(dataSnapshot.exists())
                        {
                            Posts posts = dataSnapshot.getValue(Posts.class);
                            if(posts!=null) postsArrayList.add(posts);
                        }
                    }
                }
                realtimeDatabasePostsListCallback.onDataAvailable(postsArrayList);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

    }
    public void downloadAllMyPosts(User currUser,Activity activity, RealtimeDatabasePostsListCallback realtimeDatabasePostsListCallback){
        postsArrayList.clear();
        FirebaseDatabase  database                = FirebaseDatabase.getInstance();
        DatabaseReference connectionRef           = database.getReference("AllPosts");
        connectionRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                postsArrayList.clear();
                if(snapshot.exists() && snapshot.hasChildren())
                {
                    for(DataSnapshot dataSnapshot : snapshot.getChildren())
                    {
                        if(dataSnapshot.exists())
                        {
                            Posts posts = dataSnapshot.getValue(Posts.class);
                            if(posts!=null && posts.getUserPostID().equals(currUser.getUserID())) postsArrayList.add(posts);
                        }
                    }
                }
                realtimeDatabasePostsListCallback.onDataAvailable(postsArrayList);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

    }
    public ArrayList<Connections> getConnectionsArrayList()
    { return  connectionsArrayList; }

    public User getUser(String userId)
    {
        for(User u: userArrayList)
        {
            if(u.getUserID().equals(userId)) return u;
        }
        return null;
    }



}
