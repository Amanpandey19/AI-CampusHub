package com.eCampusNITK.Home;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.alexzh.circleimageview.CircleImageView;
import com.eCampusNITK.Core.RealTimeDatabaseManager;
import com.eCampusNITK.Models.Comments;
import com.eCampusNITK.Models.Posts;
import com.eCampusNITK.Models.User;
import com.eCampusNITK.R;
import com.google.android.material.textfield.TextInputEditText;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class MyPostsAdapter extends RecyclerView.Adapter<MyPostsAdapter.RecyclerviewHolder> {
    private final ArrayList<Posts> postsArrayList;
    private final User user;
    private final ProfileFragment profileFragment;
    Context context;


    public MyPostsAdapter(Context context, ArrayList<Posts> postsArrayList, ProfileFragment fragment) {
        this.context           = context;
        this.postsArrayList    = postsArrayList;
        this.profileFragment      = fragment;
        user                   = RealTimeDatabaseManager.getInstance().getUser();
    }


    @NonNull
    @Override
    public RecyclerviewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View mRootView = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_posts_item, parent, false);
        return new RecyclerviewHolder(mRootView);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull RecyclerviewHolder holder, int position) {
        Posts post = postsArrayList.get(position);

        holder.name.setText("You");
        holder.days.setText(convertSimpleDayFormat(Long.parseLong(post.getPost_date())));
        holder.caption.setText(post.getCaption());
        if(!post.getPostUrl().equals("") && holder.post.getDrawable()==null) Picasso.get()
                .load(post.getPostUrl()).fit().centerCrop().networkPolicy(NetworkPolicy.OFFLINE)
                .into(holder.post);
        if(!post.getProfile_pic().equals("")) Picasso.get()
                .load(post.getProfile_pic().trim()).fit().centerCrop().networkPolicy(NetworkPolicy.OFFLINE)
                .into(holder.image);
        if(post.getLikes()!=null && post.getLikes().size()!=0)
            holder.likes.setText(""+post.getLikes().size()+" Likes");
        else
            holder.likes.setText("0 "+"Likes");
        if(post.getComments()!=null && post.getComments().size()!=0) holder.comments.setText(postsArrayList.get(position).getComments().size()+" Comments");
        else holder.comments.setText(0+" Comments");

        if(post.getLikes()!=null) holder.likeThisPost.setChecked(post.getLikes().contains(user.getUserID()));
        else holder.likeThisPost.setChecked(false);

        boolean isExpandable = post.isExpanded();
        holder.commentsLayout.setVisibility(isExpandable ? View.VISIBLE : View.GONE);


        CommentsAdapter commentsAdapter = new CommentsAdapter(post.getComments());
        holder.commentsRecyclerView.setLayoutManager(new LinearLayoutManager(holder.itemView.getContext()));
        holder.commentsRecyclerView.setHasFixedSize(true);
        holder.commentsRecyclerView.setAdapter(commentsAdapter);
        holder.openComments.setOnClickListener(v -> {
            post.setExpanded(!post.isExpanded());
            notifyItemChanged(holder.getAdapterPosition());
        });

        holder.comments.setOnClickListener(v -> {
            post.setExpanded(!post.isExpanded());

            notifyItemChanged(holder.getAdapterPosition());
        });

        holder.postComment.setOnClickListener(v -> {
            //upload comment on a post
            if(profileFragment!=null && holder.userComment.getText()!=null && !holder.userComment.getText().toString().trim().isEmpty())
            {
                String currTime  = String.valueOf(System.currentTimeMillis());
                profileFragment.uploadComment(post.getPostID(),post,new Comments(user.getName(),holder.userComment.getText().toString().trim(),currTime));
                notifyItemChanged(holder.getAdapterPosition());
                profileFragment.pos = holder.getAdapterPosition();
            }
        });

        holder.likeThisPost.setOnClickListener(v -> {
            if(profileFragment!=null)
            {
                profileFragment.likePost(post.getPostID(),post);
                notifyItemChanged(holder.getAdapterPosition());
                profileFragment.pos = holder.getAdapterPosition();
            }
        });

    }

    @Override
    public int getItemCount() {
        return postsArrayList.size();
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }
    public static final class RecyclerviewHolder extends RecyclerView.ViewHolder {
        CircleImageView image;
        TextView  name;
        TextView caption;
        TextView  days;
        ImageView post;
        TextView  likes;
        TextView  comments;
        RecyclerView commentsRecyclerView;
        TextInputEditText userComment;
        Button            postComment;
        RelativeLayout    commentsLayout;
        LinearLayout      openComments;
        CheckBox          likeThisPost;


        public RecyclerviewHolder(@NonNull View itemView) {
            super(itemView);
            image                   =   itemView.findViewById(R.id.post_user_image);
            name                    =   itemView.findViewById(R.id.user_name);
            days                    =   itemView.findViewById(R.id.post_time);
            caption                 =   itemView.findViewById(R.id.post_caption);
            post                    =   itemView.findViewById(R.id.post_image);
            likes                   =   itemView.findViewById(R.id.post_likes);
            comments                =   itemView.findViewById(R.id.post_comments);
            commentsRecyclerView    =   itemView.findViewById(R.id.comments_recycler_view);
            userComment             =   itemView.findViewById(R.id.editext_user_comment);
            openComments            =   itemView.findViewById(R.id.open_comments);
            postComment             =   itemView.findViewById(R.id.user_comment_post_btn);
            commentsLayout          =   itemView.findViewById(R.id.comments_layout);
            likeThisPost            =   itemView.findViewById(R.id.like_a_post_checkbox);
        }
    }

    private static void clearTimes(Calendar c) {
        c.set(Calendar.HOUR_OF_DAY,0);
        c.set(Calendar.MINUTE,0);
        c.set(Calendar.SECOND,0);
        c.set(Calendar.MILLISECOND,0);
    }

    public static String convertSimpleDayFormat(long val) {
        Calendar today=Calendar.getInstance();
        clearTimes(today);

        Calendar yesterday=Calendar.getInstance();
        yesterday.add(Calendar.DAY_OF_YEAR,-1);
        clearTimes(yesterday);

        Calendar last7days=Calendar.getInstance();
        last7days.add(Calendar.DAY_OF_YEAR,-7);
        clearTimes(last7days);

        Calendar last30days=Calendar.getInstance();
        last30days.add(Calendar.DAY_OF_YEAR,-30);
        clearTimes(last30days);


        if(val >today.getTimeInMillis())
        {
            @SuppressLint("SimpleDateFormat") SimpleDateFormat formatter = new SimpleDateFormat("hh:mm aa");
            return formatter.format(new Date(val));
        }
        else if(val>yesterday.getTimeInMillis())
            return "yesterday";
        else if(val>last7days.getTimeInMillis())
            return "last 7 days";
        else if(val>last30days.getTimeInMillis())
            return "last 30 days";
        else
            return "more than 30days";
    }
}
