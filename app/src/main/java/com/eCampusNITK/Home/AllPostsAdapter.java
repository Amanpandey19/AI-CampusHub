package com.eCampusNITK.Home;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Filter;
import android.widget.Filterable;
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
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class AllPostsAdapter extends RecyclerView.Adapter<AllPostsAdapter.RecyclerviewHolder> implements Filterable {
    private final ArrayList<Posts> postsArrayList;
    private final ArrayList<Posts> backupArrayList;

    private final User user;
    private final HomeFragment homeFragment;
    Context context;


    public AllPostsAdapter(Context context, ArrayList<Posts> postsArrayList, HomeFragment fragment) {
        this.context           = context;
        this.postsArrayList    = postsArrayList;
        this.homeFragment      = fragment;
        backupArrayList        = postsArrayList;
        user                   = RealTimeDatabaseManager.getInstance().getUser();
    }


    @NonNull
    @Override
    public RecyclerviewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View mRootView = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_posts_item, parent, false);
        return new RecyclerviewHolder(mRootView);
    }

    @SuppressLint({"SetTextI18n", "NotifyDataSetChanged"})
    @Override
    public void onBindViewHolder(@NonNull RecyclerviewHolder holder, int position) {
        Posts post = postsArrayList.get(holder.getAdapterPosition());

        if(post.getUserPostID().equals(user.getUserID())) holder.name.setText("You");
        else holder.name.setText(post.getName());
        holder.days.setText(convertSimpleDayFormat(Long.parseLong(post.getPost_date())));
        holder.caption.setText(post.getCaption());
        if(!post.getPostUrl().equals("") && holder.post.getDrawable()==null) {
            Picasso.get()
                .load(post.getPostUrl()).fit().centerCrop()
                .into(holder.post);

        }
        if(!post.getProfile_pic().equals("")) Picasso.get()
                .load(post.getProfile_pic().trim()).fit().centerCrop()
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
            homeFragment.pos = holder.getAdapterPosition();
            notifyItemChanged(holder.getAdapterPosition());
        });

        holder.comments.setOnClickListener(v -> {
            post.setExpanded(!post.isExpanded());
            homeFragment.pos = holder.getAdapterPosition();
            notifyItemChanged(holder.getAdapterPosition());
        });

        holder.postComment.setOnClickListener(v -> {
            //upload comment on a post
            if(homeFragment!=null && holder.userComment.getText()!=null && !holder.userComment.getText().toString().trim().isEmpty())
            {
                String currTime  = String.valueOf(System.currentTimeMillis());
                homeFragment.uploadComment(post.getPostID(),post,new Comments(user.getName(),holder.userComment.getText().toString().trim(),currTime));
                notifyItemChanged(holder.getAdapterPosition());
                homeFragment.pos = holder.getAdapterPosition();
            }
        });

        holder.likeThisPost.setOnClickListener(v -> {
            if(homeFragment!=null)
            {
                homeFragment.likePost(post.getPostID(),post);
                notifyItemChanged(holder.getAdapterPosition());
                homeFragment.pos = holder.getAdapterPosition();
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

    @Override
    public Filter getFilter() {
        return filter;
    }

    Filter filter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            ArrayList<Posts> filteredList = new ArrayList<>();
            if(constraint.toString().isEmpty())
                filteredList.addAll(backupArrayList);
            else {
                for(Posts posts : backupArrayList)
                {
                    if(posts.getCaption().toLowerCase().contains(constraint.toString().toLowerCase()))
                        filteredList.add(posts);
                }
            }
            FilterResults filterResults = new FilterResults();
            filterResults.values = filteredList;
            return filterResults;
        }

        @SuppressLint("NotifyDataSetChanged")
        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            postsArrayList.clear();
            postsArrayList.addAll((ArrayList<Posts>)results.values);
            notifyDataSetChanged();
        }
    };
}
