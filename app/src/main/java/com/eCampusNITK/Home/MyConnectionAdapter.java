package com.eCampusNITK.Home;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.alexzh.circleimageview.CircleImageView;
import com.eCampusNITK.Models.Connections;
import com.eCampusNITK.R;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.util.ArrayList;

public class MyConnectionAdapter extends RecyclerView.Adapter<MyConnectionAdapter.RecyclerviewHolder> {
    private ArrayList<Connections> connectionsArrayList = new ArrayList<>();
    Context context;
    ProfileFragment profileFragment;


    public MyConnectionAdapter(Context context, ArrayList<Connections> connectionsArrayList, ProfileFragment profileFragment) {
        this.context              = context;
        this.connectionsArrayList = connectionsArrayList;
        this.profileFragment      = profileFragment;
    }


    @NonNull
    @Override
    public RecyclerviewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View mRootView = LayoutInflater.from(parent.getContext()).inflate(R.layout.connections_item, parent, false);
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

    }

    @Override
    public int getItemCount() {
        return connectionsArrayList.size();
    }

    public static final class RecyclerviewHolder extends RecyclerView.ViewHolder {
        CircleImageView image;
        TextView name;
        TextView course;
        TextView remove;

        public RecyclerviewHolder(@NonNull View itemView) {
            super(itemView);
            image                   =   itemView.findViewById(R.id.connection_image);
            name                    =   itemView.findViewById(R.id.connection_name);
            course                  =   itemView.findViewById(R.id.connection_course);
        }
    }
}
