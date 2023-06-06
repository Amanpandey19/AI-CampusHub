package com.eCampusNITK.Home;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.eCampusNITK.Core.RealTimeDatabaseManager;
import com.eCampusNITK.Models.ChatPerson;
import com.eCampusNITK.Models.User;
import com.eCampusNITK.R;
import com.google.android.material.imageview.ShapeableImageView;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class ChatHomeAdapter extends RecyclerView.Adapter<ChatHomeAdapter.RecyclerviewHolder> implements Filterable {
    private final ArrayList<ChatPerson> chatPersonArrayList;
    private final ArrayList<ChatPerson> backupArrayList;
    Context context;
    Activity activity;
    User    user;
    Drawable sent,delivered,seen;

    @SuppressLint("UseCompatLoadingForDrawables")
    public ChatHomeAdapter(Context context, Activity activity, ArrayList<ChatPerson> chatPersonArrayList) {
        this.context                = context;
        this.chatPersonArrayList    = chatPersonArrayList;
        user                        = RealTimeDatabaseManager.getInstance().getUser();
        backupArrayList             = new ArrayList<>(chatPersonArrayList);
        sent                         = context.getResources().getDrawable(R.drawable.message_sent);
        delivered                    = context.getResources().getDrawable(R.drawable.message_delivered);
        seen                         = context.getResources().getDrawable(R.drawable.message_seen);
        this.activity                = activity;

    }


    @NonNull
    @Override
    public RecyclerviewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View mRootView = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_person_item, parent, false);
        return new RecyclerviewHolder(mRootView);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull RecyclerviewHolder holder, int position) {
        ChatPerson chatPerson = chatPersonArrayList.get(position);
        holder.last_text.setText(chatPerson.getLast_text());
        holder.person_name.setText(chatPerson.getPerson_name());
        if(isToday(chatPerson.getLast_text_time()))
        {
            holder.text_time.setText(getTime(Long.parseLong(chatPerson.getLast_text_time())));
        }else {
            holder.text_time.setText(convertTimeStampTODay(chatPerson.getLast_text_time()));
        }
        if(chatPerson.getPerson_image()!=null) Picasso.get().load(chatPerson.getPerson_image())
                .fit()
                .centerCrop()
                .into(holder.personImage);

        holder.chatPersonCard.setOnClickListener(v -> {
            Intent intent = new Intent(context, ChatActivity.class);
            intent.putExtra("personUserID", chatPerson.getPerson_userId());
            context.startActivity(intent);
            activity.overridePendingTransition(R.anim.animate_left_to_right,R.anim.animate_right_to_left);
        });

    }

    @Override
    public int getItemCount() {
        return chatPersonArrayList.size();
    }

    public static final class RecyclerviewHolder extends RecyclerView.ViewHolder {

        ShapeableImageView personImage;
        TextView last_text, text_time, person_name;
        CardView  chatPersonCard;

        public RecyclerviewHolder(@NonNull View itemView) {
            super(itemView);
            personImage                      =   itemView.findViewById(R.id.chat_person_image);
            last_text                        =   itemView.findViewById(R.id.chat_person_last_messsage);
            text_time                        =   itemView.findViewById(R.id.last_chat_message_time);
            person_name                      =   itemView.findViewById(R.id.chat_person_name);
            chatPersonCard                   =   itemView.findViewById(R.id.chat_person_card);
        }
    }

    public String convertTimeStampTODay(String timeStamp)
    {
        @SuppressLint("SimpleDateFormat") SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy");
        return simpleDateFormat.format(new Date(Long.parseLong(timeStamp)));
    }

    public String getTime(Long timeStamp)
    {
        @SuppressLint("SimpleDateFormat") SimpleDateFormat formatter = new SimpleDateFormat("hh:mm aa");
        return formatter.format(new Date(timeStamp));
    }

    public boolean isToday(String timestamp)
    {
        @SuppressLint("SimpleDateFormat") SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
        return formatter.format(new Date(Long.parseLong(timestamp))).equals(formatter.format(new Date(System.currentTimeMillis())));
    }

    @Override
    public Filter getFilter() {
        return filter;
    }

    Filter filter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            ArrayList<ChatPerson> filteredList = new ArrayList<>();
            if(constraint.toString().isEmpty())
                filteredList.addAll(backupArrayList);
            else {
                for(ChatPerson person : backupArrayList)
                {
                    if(person.getPerson_name().toLowerCase().contains(constraint.toString().toLowerCase()))
                        filteredList.add(person);
                }
            }
            FilterResults filterResults = new FilterResults();
            filterResults.values = filteredList;
            return filterResults;
        }

        @SuppressLint("NotifyDataSetChanged")
        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            chatPersonArrayList.clear();
            chatPersonArrayList.addAll((ArrayList<ChatPerson>)results.values);
            notifyDataSetChanged();
        }
    };
}
