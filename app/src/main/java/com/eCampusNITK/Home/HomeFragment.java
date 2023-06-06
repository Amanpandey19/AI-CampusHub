package com.eCampusNITK.Home;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.Toast;

import com.eCampusNITK.Core.RealTimeDatabaseManager;
import com.eCampusNITK.Models.Comments;
import com.eCampusNITK.Models.Posts;
import com.eCampusNITK.Models.User;
import com.eCampusNITK.R;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Collections;

import androidx.appcompat.widget.Toolbar;

public class HomeFragment extends Fragment {

    RecyclerView           recyclerView;
    ArrayList<Posts>       arrayList;
    AllPostsAdapter        allPostsAdapter;
    User                   user;
    Toolbar                toolbar;
    int                    pos = -1;
    ImageView              logo;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView =  inflater.inflate(R.layout.fragment_home, container, false);
        Animation animation= AnimationUtils.loadAnimation(requireContext(),R.anim.animate_left_to_right);
        rootView.setAnimation(animation);
        setStatusBarTextAndBgColor();
        return rootView;
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recyclerView     = view.findViewById(R.id.posts_recyclerView);
        toolbar          = view.findViewById(R.id.toolbar_home);
        logo             = view.findViewById(R.id.online_campus_logo);
        toolbar.inflateMenu(R.menu.home_toolbar_menu);
        user = RealTimeDatabaseManager.getInstance().getUser();
        toolbar.setOnMenuItemClickListener(item -> {
            switch (item.getItemId())
            {
                case R.id.add_post_home:
                    Intent i = new Intent(getActivity(),AddPostActivity.class);
                    startActivity(i);
                    if(getActivity()!=null) getActivity().overridePendingTransition(R.anim.animate_left_to_right,R.anim.animate_right_to_left);
                    break;
                case R.id.search_home:
                    Toast.makeText(getContext(), "Working on it", Toast.LENGTH_SHORT).show();
                    break;
                default:
                    break;
            }
            return true;
        });

        arrayList    = new ArrayList<>();


        RealTimeDatabaseManager.getInstance().downloadAllPosts(user, getActivity(), new RealTimeDatabaseManager.RealtimeDatabasePostsListCallback() {
            @Override
            public void onDataAvailable(ArrayList<Posts> postsArrayList) {
                Collections.reverse(postsArrayList);
                allPostsAdapter = new AllPostsAdapter(getActivity(), postsArrayList, HomeFragment.this);
                recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
                recyclerView.setAdapter(allPostsAdapter);
                recyclerView.setHasFixedSize(true);
                recyclerView.addOnScrollListener(new CustomScrollListener());
                if(pos==-1 && postsArrayList.size()!=0)
                {
                    //recyclerView.scrollToPosition(postsArrayList.size()-1);
                }else {
                    recyclerView.scrollToPosition(pos);
                }

            }
        });

        if(getActivity()!=null)
        {
            DrawerLayout drawer = (DrawerLayout) getActivity().findViewById(R.id.drawerLayout);
            logo.setOnClickListener(v -> {
                if(drawer.isOpen()) drawer.closeDrawer(GravityCompat.START);
                else drawer.openDrawer(GravityCompat.START);
            });
        }
    }


    public void likePost(String postID, Posts posts)
    {

        DatabaseReference db       = FirebaseDatabase.getInstance().getReference().child("AllPosts").child(postID);
        ArrayList<String> likes    = new ArrayList<>();
        likes = posts.getLikes();
        if(likes==null)
        {
            likes = new ArrayList<>();
            likes.add(user.getUserID());
            posts.setLikes(likes);
            posts.setExpanded(false);
            db.setValue(posts);
        }else {
            if(likes.contains(user.getUserID()))
            {
                likes.remove(user.getUserID());
            }else {
                likes.add(user.getUserID());
            }
            posts.setLikes(likes);
            posts.setExpanded(false);
            db.setValue(posts);
        }

    }

    public void uploadComment(String postID, Posts posts, Comments comment)
    {
        DatabaseReference db       = FirebaseDatabase.getInstance().getReference().child("AllPosts").child(postID);
        ArrayList<Comments> commentsArrayList    = new ArrayList<>();
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

    public class CustomScrollListener extends RecyclerView.OnScrollListener {
        public CustomScrollListener() {
        }

        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
            switch (newState) {
                case RecyclerView.SCROLL_STATE_IDLE:
                    System.out.println("The RecyclerView is not scrolling");
                    break;
                case RecyclerView.SCROLL_STATE_DRAGGING:
                    System.out.println("Scrolling now");
                    break;
                case RecyclerView.SCROLL_STATE_SETTLING:
                    System.out.println("Scroll Settling");
                    break;

            }

        }

        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            if (dx > 0) {
                System.out.println("Scrolled Right");
            } else if (dx < 0) {
                System.out.println("Scrolled Left");
            } else {
                System.out.println("No Horizontal Scrolled");
            }

            if (dy > 0) {
                //System.out.println("Scrolled Downwards");
                if (getActivity() instanceof HomeActivity) {
                    // get Main Activity
                    HomeActivity activity = (HomeActivity) getActivity();

                    // Do what you want to do with bottom Navigation
                    activity.bottomAppBar.performHide();
                    activity.chatBtn.hide();
                }
            } else if (dy < 0) {
                if (getActivity() instanceof HomeActivity) {
                    // get Main Activity
                    HomeActivity activity = (HomeActivity) getActivity();

                    // Do what you want to do with bottom Navigation
                    activity.bottomAppBar.performShow();
                    activity.chatBtn.show();
                }
            } else {
                System.out.println("No Vertical Scrolled");
            }
        }
    }

    void setStatusBarTextAndBgColor(){
        if(getActivity()!=null)
        {
            Window window = getActivity().getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(this.getResources().getColor(R.color.black));
            View decorView = window.getDecorView();
            decorView.setSystemUiVisibility(decorView.getSystemUiVisibility() & ~View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR); //set status text  light
        }

    }

}