package com.eCampusNITK.Home;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.alexzh.circleimageview.CircleImageView;
import com.eCampusNITK.Models.Suggestions;
import com.eCampusNITK.R;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class MySuggestionsAdapter extends RecyclerView.Adapter<MySuggestionsAdapter.RecyclerviewHolder> {
    ArrayList<Suggestions> suggestionsArrayList;
    Context context;
    ItemTouchHelper.SimpleCallback simpleCallback;

    public MySuggestionsAdapter(Context context, ItemTouchHelper.SimpleCallback simpleCallback, ArrayList<Suggestions> suggestionsArrayList) {
        this.context              = context;
        this.suggestionsArrayList = suggestionsArrayList;
        this.simpleCallback       = simpleCallback;
    }

    @NonNull
    @Override
    public RecyclerviewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View mRootView = LayoutInflater.from(parent.getContext()).inflate(R.layout.suggestions_item, parent, false);
        Animation animation= AnimationUtils.loadAnimation(context,R.anim.animate_slide_left_enter);
        mRootView.setAnimation(animation);
        return new RecyclerviewHolder(mRootView);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull RecyclerviewHolder holder, int position) {
        holder.name.setText(suggestionsArrayList.get(position).getUser_name());
        if(!suggestionsArrayList.get(position).getUser_img().equals("")) Picasso.get()
                .load(suggestionsArrayList.get(position).getUser_img()).
                fit().centerCrop()
                .into(holder.image);

        holder.course.setText(suggestionsArrayList.get(position).getUser_course());
        holder.add.setOnClickListener(v -> simpleCallback.onSwiped(holder, ItemTouchHelper.LEFT));
        holder.remove.setOnClickListener(v -> simpleCallback.onSwiped(holder, ItemTouchHelper.RIGHT));

    }

    @Override
    public int getItemCount() {
        return suggestionsArrayList.size();
    }

    public static final class RecyclerviewHolder extends RecyclerView.ViewHolder {
        CircleImageView image;
        TextView name;
        TextView course;
        TextView add;
        TextView remove;

        public RecyclerviewHolder(@NonNull View itemView) {
            super(itemView);
            image                   =   itemView.findViewById(R.id.connection_image);
            name                    =   itemView.findViewById(R.id.connection_name);
            course                  =   itemView.findViewById(R.id.connection_course);
            add                     =   itemView.findViewById(R.id.add_suggestion);
            remove                  =   itemView.findViewById(R.id.remove_suggestion);
        }
    }
}
