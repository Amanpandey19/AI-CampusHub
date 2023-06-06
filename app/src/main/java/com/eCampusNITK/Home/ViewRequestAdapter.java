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
import androidx.recyclerview.widget.RecyclerView;

import com.alexzh.circleimageview.CircleImageView;
import com.eCampusNITK.Models.Suggestions;
import com.eCampusNITK.R;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class ViewRequestAdapter extends RecyclerView.Adapter<ViewRequestAdapter.RecyclerviewHolder> {
    ArrayList<Suggestions> requestArrayList;
    Context context;

    public ViewRequestAdapter(Context context, ArrayList<Suggestions> requestArrayList) {
        this.context              = context;
        this.requestArrayList     = requestArrayList;
    }


    @NonNull
    @Override
    public RecyclerviewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View mRootView = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_reuests_item, parent, false);
        Animation animation= AnimationUtils.loadAnimation(context,R.anim.animate_slide_left_enter);
        mRootView.setAnimation(animation);
        return new RecyclerviewHolder(mRootView);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull RecyclerviewHolder holder, int position) {
        holder.name.setText(requestArrayList.get(position).getUser_name());
        if(requestArrayList.get(position).getUser_img()!=null && !requestArrayList.get(position).getUser_img().isEmpty())
        {
            Picasso.get().load(requestArrayList.get(position).getUser_img()).
                    fit().centerCrop().
                    into(holder.image);
        }
        holder.course.setText(requestArrayList.get(position).getUser_course()+" NIT KKR");
        holder.accept.setOnClickListener(v -> {
            if (context instanceof ViewRequestActivity) {
                ((ViewRequestActivity)context).acceptRequest(requestArrayList.get(position));
            }
        });

        holder.remove.setOnClickListener(v -> {
            if (context instanceof ViewRequestActivity) {
                ((ViewRequestActivity)context).rejectRequest(requestArrayList.get(position));
            }
        });
    }

    @Override
    public int getItemCount() {
        return requestArrayList.size();
    }

    public static final class RecyclerviewHolder extends RecyclerView.ViewHolder {
        CircleImageView image;
        TextView name;
        TextView course;
        TextView accept;
        TextView remove;

        public RecyclerviewHolder(@NonNull View itemView) {
            super(itemView);
            image                   =   itemView.findViewById(R.id.connection_image);
            name                    =   itemView.findViewById(R.id.connection_name);
            course                  =   itemView.findViewById(R.id.connection_course);
            accept                  =   itemView.findViewById(R.id.accept_connection);
            remove                  =   itemView.findViewById(R.id.remove_connection);
        }
    }
}
