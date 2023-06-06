package com.eCampusNITK.Home;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.eCampusNITK.Core.RealTimeDatabaseManager;
import com.eCampusNITK.Models.ChatMessage;
import com.eCampusNITK.Models.ChatPerson;
import com.eCampusNITK.Models.User;
import com.eCampusNITK.R;
import com.google.android.material.imageview.ShapeableImageView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class ChatMessageAdapter extends RecyclerView.Adapter<ChatMessageAdapter.RecyclerviewHolder> {
    private final ArrayList<ChatMessage> chatMessageArrayList;
    Context context;
    User user;
    Drawable sent, delivered, seen;





    @SuppressLint("UseCompatLoadingForDrawables")
    public ChatMessageAdapter(Context context, ArrayList<ChatMessage> chatMessageArrayList) {
        this.context                 = context;
        this.chatMessageArrayList    = chatMessageArrayList;
        user                         = RealTimeDatabaseManager.getInstance().getUser();
        sent                         = context.getResources().getDrawable(R.drawable.message_sent);
        delivered                    = context.getResources().getDrawable(R.drawable.message_delivered);
        seen                         = context.getResources().getDrawable(R.drawable.message_seen);

    }


    @NonNull
    @Override
    public RecyclerviewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View mRootView = LayoutInflater.from(parent.getContext()).inflate(R.layout.message_item, parent, false);
        return new RecyclerviewHolder(mRootView);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull RecyclerviewHolder holder, int position) {
        ChatMessage chatMessage = chatMessageArrayList.get(position);
        ChatMessage previousMessage = new ChatMessage();
        if(position!=0) previousMessage = chatMessageArrayList.get(position-1);
        if(chatMessage.getSenderID().equals(user.getUserID()) || chatMessage.getTypeofMessage()==0)
        {
            //user is the sender of this message or this is first message in the list(when connection request got accepted)
            if(chatMessage.getTypeofMessage()==0)
            {
                //This is the first text of the chat
                holder.leftChatView.setVisibility(View.GONE);
                holder.rightChatView.setVisibility(View.GONE);
                holder.noMessageTv.setText("Connection Added "+convertTimeStampTODay(chatMessage.getTimeOfMessage()));
                holder.noMessageTv.setVisibility(View.VISIBLE);
            }else
            {
                holder.leftChatView.setVisibility(View.GONE);
                holder.rightChatView.setVisibility(View.VISIBLE);
                holder.noMessageTv.setVisibility(View.GONE);
                if(convertTimeStampTODay(chatMessage.getTimeOfMessage()).equals(convertTimeStampTODay(previousMessage.getTimeOfMessage()))){
                    holder.noMessageTv.setVisibility(View.GONE);
                }else {
                    //The current message is on different day from the previous message
                    holder.noMessageTv.setText(convertTimeStampTODay(chatMessage.getTimeOfMessage()));
                    holder.noMessageTv.setVisibility(View.VISIBLE);
                }

                if(chatMessage.getTypeofMessage()==1)
                {
                    //This message is a text message sent by the current user
                    holder.rightChatImageLayout.setVisibility(View.GONE);
                    holder.rightChatMessageLayout.setVisibility(View.VISIBLE);
                    holder.rightMessageTv.setText(chatMessage.getActualMessage());
                    holder.rightMessageTime.setText(getTime(Long.parseLong(chatMessage.getTimeOfMessage())));

                } else if (chatMessage.getTypeofMessage()==2) {
                    //This message is a Pdf sent by current user
                    //Work to be done for this

                }else {
                    //This message is an image sent by the current user
                }

            }
        }else {
            //user is the receiver of this image
            holder.leftChatView.setVisibility(View.VISIBLE);
            holder.rightChatView.setVisibility(View.GONE);
            if(!convertTimeStampTODay(chatMessage.getTimeOfMessage()).equals(convertTimeStampTODay(previousMessage.getTimeOfMessage()))){
                //The current message is on different day from the previous message
                holder.noMessageTv.setText(convertTimeStampTODay(chatMessage.getTimeOfMessage()));
                holder.noMessageTv.setVisibility(View.VISIBLE);
            }

            if(chatMessage.getTypeofMessage()==1)
            {
                //This message is a text message sent by the current user
                holder.leftMessageTv.setText(chatMessage.getActualMessage());
                holder.leftMessageTime.setText(getTime(Long.parseLong(chatMessage.getTimeOfMessage())));

            } else if(chatMessage.getTypeofMessage()==2) {
                //This message is a Pdf sent by current user
                //Will work on it

            }else {
                //This message is an image sent by the current user
            }

        }

    }

    @Override
    public int getItemCount() {
        return chatMessageArrayList.size();
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    public static final class RecyclerviewHolder extends RecyclerView.ViewHolder {

        TextView     leftMessageTv,leftMessageTime,rightMessageTv, rightMessageTime, noMessageTv;
        TextView     leftChatImageTime, rightChatImageTime;
        LinearLayout leftChatView, rightChatView, leftChatMessageLayout, rightChatMessageLayout;
        LinearLayout leftChatImageLayout, rightChatImageLayout;
        ImageView    leftChatImageView, rightChatImageView;



        public RecyclerviewHolder(@NonNull View itemView) {
            super(itemView);
            leftMessageTv                      =   itemView.findViewById(R.id.left_chat_message_text_view);
            leftMessageTime                    =   itemView.findViewById(R.id.left_chat_message_time);
            rightMessageTv                     =   itemView.findViewById(R.id.right_chat_message_text_view);
            rightMessageTime                   =   itemView.findViewById(R.id.right_chat_message_time);
            noMessageTv                        =   itemView.findViewById(R.id.no_message_text);
            leftChatImageTime                  =   itemView.findViewById(R.id.left_chat_image_time);
            rightChatImageTime                 =   itemView.findViewById(R.id.right_chat_image_time);
            leftChatView                       =   itemView.findViewById(R.id.left_chat_view);
            rightChatView                      =   itemView.findViewById(R.id.right_chat_view);
            leftChatMessageLayout              =   itemView.findViewById(R.id.left_chat_message_layout);
            rightChatMessageLayout             =   itemView.findViewById(R.id.right_chat_text_layout);
            leftChatImageLayout                =   itemView.findViewById(R.id.left_chat_image_layout);
            rightChatImageLayout               =   itemView.findViewById(R.id.right_chat_image_layout);
            leftChatImageView                  =   itemView.findViewById(R.id.left_chat_imageview);
            rightChatImageView                 =   itemView.findViewById(R.id.right_chat_imageview);
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

}
