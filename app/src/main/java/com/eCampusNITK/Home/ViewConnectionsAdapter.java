package com.eCampusNITK.Home;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.alexzh.circleimageview.CircleImageView;
import com.eCampusNITK.Models.Connections;
import com.eCampusNITK.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class ViewConnectionsAdapter extends RecyclerView.Adapter<ViewConnectionsAdapter.RecyclerviewHolder> {
    ArrayList<Connections> connectionsArrayList;
    Context context;

    public ViewConnectionsAdapter(Context context,  ArrayList<Connections> connectionsArrayList) {
        this.context              = context;
        this.connectionsArrayList = connectionsArrayList;
    }


    @NonNull
    @Override
    public RecyclerviewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View mRootView = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_connections_item, parent, false);
        Animation animation= AnimationUtils.loadAnimation(context,R.anim.animate_slide_left_enter);
        mRootView.setAnimation(animation);
        return new RecyclerviewHolder(mRootView);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull RecyclerviewHolder holder, int position) {
        holder.name.setText(connectionsArrayList.get(position).getUser_name());
        if(!connectionsArrayList.get(position).getUser_img().equals("")) Picasso.get()
                .load(connectionsArrayList.get(position).getUser_img()).fit().centerCrop()
                .into(holder.image);
        holder.course.setText(connectionsArrayList.get(position).getUser_course()+" NIT KKR");
        holder.remove.setOnClickListener(v -> {
            if (context instanceof ViewConnectionsActivity) {
                ((ViewConnectionsActivity)context).removeConnection(connectionsArrayList.get(holder.getAdapterPosition()).getConnection_id());
            }
        });

        holder.message.setOnClickListener(v -> {
            Intent intent = new Intent(context, ChatActivity.class);
            intent.putExtra("personUserID", connectionsArrayList.get(position).getConnection_id());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return connectionsArrayList.size();
    }

    public static final class RecyclerviewHolder extends RecyclerView.ViewHolder {
        CircleImageView image;
        TextView name;
        TextView course;
        TextView message;
        TextView remove;

        public RecyclerviewHolder(@NonNull View itemView) {
            super(itemView);
            image                   =   itemView.findViewById(R.id.connection_image);
            name                    =   itemView.findViewById(R.id.connection_name);
            course                  =   itemView.findViewById(R.id.connection_course);
            message                 =   itemView.findViewById(R.id.message_connection);
            remove                  =   itemView.findViewById(R.id.remove_connection);
        }
    }
}
