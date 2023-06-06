package com.eCampusNITK.Home;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import com.eCampusNITK.Core.RealTimeDatabaseManager;
import com.eCampusNITK.Models.Comments;
import com.eCampusNITK.Models.Posts;
import com.eCampusNITK.Models.Subject;
import com.eCampusNITK.Models.User;
import com.eCampusNITK.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileFragment extends Fragment {

    RecyclerView           connectionsRecyclerView;
    MyConnectionAdapter    myConnectionAdapter;
    RecyclerView           recyclerView;
    NestedScrollView       nestedScrollView;
    MyPostsAdapter         allPostsAdapter;
    User                   user;
    int                    pos = -1;

    TextView               goodMorningTv,overallAttendance;
    ArrayList<Subject>     mainSubjectArrayList = new ArrayList<>();
    CircleImageView        edit_user;
    TextView userName, go_to_attendance_activity, go_to_connections_activity;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView =  inflater.inflate(R.layout.fragment_profile, container, false);
        Animation animation= AnimationUtils.loadAnimation(requireContext(),R.anim.animate_slide_left_enter);
        rootView.setAnimation(animation);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            setStatusBarTextAndBgColor();
        }
        return rootView;
    }

    @SuppressLint({"UseCompatLoadingForDrawables", "SetTextI18n"})
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        findIds(view);
        setGoodMorningText();

        setUserDetails();

        edit_user.setOnClickListener(v -> {
            Intent i = new Intent(getContext(),EditProfileActivity.class);
            startActivity(i);
            requireActivity().overridePendingTransition(R.anim.animate_left_to_right,R.anim.animate_right_to_left);
        });

        go_to_attendance_activity.setOnClickListener(v -> {
            Intent i = new Intent(getContext(),AttendanceManagerActivity.class);
            startActivity(i);
            requireActivity().overridePendingTransition(R.anim.animate_left_to_right,R.anim.animate_right_to_left);
        });

        go_to_connections_activity.setOnClickListener(v -> {
            Intent i = new Intent(getContext(),ViewConnectionsActivity.class);
            startActivity(i);
            requireActivity().overridePendingTransition(R.anim.animate_left_to_right,R.anim.animate_right_to_left);
        });

        nestedScrollView.setOnScrollChangeListener((NestedScrollView.OnScrollChangeListener) (v, scrollX, scrollY, oldScrollX, oldScrollY) -> {
            if (scrollY > oldScrollY) {
                Log.i("Profile Fragment", "Scroll DOWN");
                if (getActivity() instanceof HomeActivity) {
                    // get Main Activity
                    HomeActivity activity = (HomeActivity) getActivity();

                    // Do what you want to do with bottom Navigation
                    activity.chatBtn.hide();
                    activity.bottomAppBar.performHide();
                }
            }
            if (scrollY < oldScrollY) {
                Log.i("Profile Fragment", "Scroll UP");
                if (getActivity() instanceof HomeActivity) {
                    // get Main Activity
                    HomeActivity activity = (HomeActivity) getActivity();

                    // Do what you want to do with bottom Navigation
                    activity.chatBtn.show();
                    activity.bottomAppBar.setFabCradleMargin(35.0f);
                    activity.bottomAppBar.setCradleVerticalOffset(20.0f);
                    activity.bottomAppBar.performShow();
                }
            }

            if (scrollY == 0) {
                Log.i("Profile Fragment", "Top Scroll");
            }

            if (scrollY == ( v.getMeasuredHeight() - v.getChildAt(0).getMeasuredHeight() )) {
                Log.i("Profile Fragment", "Scroll Down");
            }
        });
    }


    private void findIds(View view)
    {
        connectionsRecyclerView    = view.findViewById(R.id.my_connections);
        userName                   = view.findViewById(R.id.userName);
        recyclerView               = view.findViewById(R.id.posts_recyclerView);
        go_to_attendance_activity  = view.findViewById(R.id.go_to_attendance);
        go_to_connections_activity = view.findViewById(R.id.view_all_connections);
        edit_user                  = view.findViewById(R.id.edit_user);
        nestedScrollView           = view.findViewById(R.id.nested_scroll_view);
        goodMorningTv              = view.findViewById(R.id.good_morning_tv);
        overallAttendance          = view.findViewById(R.id.overallAttendance);

    }
    public void setUserDetails()
    {
        final ProgressDialog pd=new ProgressDialog(getActivity());
        pd.setMessage("Please wait..");
        pd.setCanceledOnTouchOutside(false);
        pd.show();
        user=RealTimeDatabaseManager.getInstance().getUser();
        userName.setText(user.getName());
        if(!user.getProfilePicture().equals("")) Picasso.get()
                .load(user.getProfilePicture()).networkPolicy(NetworkPolicy.OFFLINE).fit()
                .into(edit_user);

        //Set Connections
        RealTimeDatabaseManager.getInstance().downloadConnections(user, data -> {
            myConnectionAdapter = new MyConnectionAdapter(getActivity(), data, ProfileFragment.this);
            connectionsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
            connectionsRecyclerView.setAdapter(myConnectionAdapter);
        });

        //Set My Posts
        RealTimeDatabaseManager.getInstance().downloadAllMyPosts(user, getActivity(), postsArrayList -> {
            Collections.reverse(postsArrayList);

            allPostsAdapter = new MyPostsAdapter(getActivity(), postsArrayList, ProfileFragment.this);
            recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
            recyclerView.setAdapter(allPostsAdapter);
            if(pos==-1 && postsArrayList.size()!=0)
            {
                recyclerView.scrollToPosition(postsArrayList.size()-1);
            }else {
                recyclerView.scrollToPosition(pos);
            }
            pd.dismiss();
        });

        //Set My attendance
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        databaseReference.child("Attendance").child(RealTimeDatabaseManager.getInstance().getUser().getUserID()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    mainSubjectArrayList.clear();
                    for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                        Subject subject = postSnapshot.getValue(Subject.class);
                        mainSubjectArrayList.add(subject);
                    }
                    updateOverAllAttendance(mainSubjectArrayList);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @SuppressLint("SetTextI18n")
    public void updateOverAllAttendance(ArrayList<Subject> subjectArrayList) {

        int total_classes = 0;
        int attended_classes = 0;
        for (Subject subject : subjectArrayList) {
            total_classes += subject.getTotal_classes();
            attended_classes += subject.getAttended_classes();
        }
        if (total_classes == 0) {
            overallAttendance.setText("100%");
        } else {
            overallAttendance.setText((attended_classes * 100 / total_classes) + "%");
        }
    }

    public void likePost(String postID, Posts posts)
    {

        DatabaseReference db       = FirebaseDatabase.getInstance().getReference().child("AllPosts").child(postID);
        ArrayList<String> likes;
        likes = posts.getLikes();
        if(likes==null)
        {
            likes = new ArrayList<>();
            likes.add(user.getUserID());
        }else {
            if(likes.contains(user.getUserID()))
            {
                likes.remove(user.getUserID());
            }else {
                likes.add(user.getUserID());
            }
        }
        posts.setLikes(likes);
        posts.setExpanded(false);
        db.setValue(posts);

    }

    public void uploadComment(String postID, Posts posts, Comments comment)
    {
        DatabaseReference db       = FirebaseDatabase.getInstance().getReference().child("AllPosts").child(postID);
        ArrayList<Comments> commentsArrayList;
        commentsArrayList = posts.getComments();
        if(commentsArrayList==null)
        {
            commentsArrayList = new ArrayList<>();
        }
        commentsArrayList.add(comment);
        posts.setComments(commentsArrayList);
        posts.setExpanded(false);
        db.setValue(posts);
    }

    @SuppressLint("SetTextI18n")
    private void setGoodMorningText() {
        //Good morning text
        int hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
        if(hour<12) goodMorningTv.setText("Good Morning");
        else if (hour<17) goodMorningTv.setText("Good AfterNoon");
        else if (hour<20) goodMorningTv.setText("Good Evening");
        else goodMorningTv.setText("Good Night");
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

}