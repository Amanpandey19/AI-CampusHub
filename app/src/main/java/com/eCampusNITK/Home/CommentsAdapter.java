package com.eCampusNITK.Home;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.eCampusNITK.Models.Comments;
import com.eCampusNITK.R;

import org.w3c.dom.Text;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class CommentsAdapter extends RecyclerView.Adapter<CommentsAdapter.NestedViewHolder> {

    private ArrayList<Comments> comments;

    public CommentsAdapter(ArrayList<Comments> comments){

        if(comments!=null) this.comments = comments;
        else this.comments = new ArrayList<>();
    }
    @NonNull
    @Override
    public NestedViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.comments_item , parent , false);
        return new NestedViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NestedViewHolder holder, int position) {
        holder.comment.setText(comments.get(position).getPersonComment());
        holder.person.setText(comments.get(position).getPersonName());
        holder.uploadTime.setText(convertSimpleDayFormat(Long.parseLong(comments.get(position).getDay())));
    }

    @Override
    public int getItemCount() {
        return comments.size();
    }

    public class NestedViewHolder extends RecyclerView.ViewHolder{
        private TextView comment;
        private TextView person;
        private TextView uploadTime;
        public NestedViewHolder(@NonNull View itemView) {
            super(itemView);
            comment     = itemView.findViewById(R.id.commenting_person_comment);
            person      = itemView.findViewById(R.id.commenting_person_name);
            uploadTime  = itemView.findViewById(R.id.commenting_time);

        }
    }

    private static Calendar clearTimes(Calendar c) {
        c.set(Calendar.HOUR_OF_DAY,0);
        c.set(Calendar.MINUTE,0);
        c.set(Calendar.SECOND,0);
        c.set(Calendar.MILLISECOND,0);
        return c;
    }

    public static String convertSimpleDayFormat(long val) {
        Calendar today=Calendar.getInstance();
        today=clearTimes(today);

        Calendar yesterday=Calendar.getInstance();
        yesterday.add(Calendar.DAY_OF_YEAR,-1);
        yesterday=clearTimes(yesterday);

        Calendar last7days=Calendar.getInstance();
        last7days.add(Calendar.DAY_OF_YEAR,-7);
        last7days=clearTimes(last7days);

        Calendar last30days=Calendar.getInstance();
        last30days.add(Calendar.DAY_OF_YEAR,-30);
        last30days=clearTimes(last30days);


        if(val >today.getTimeInMillis())
        {
            SimpleDateFormat formatter = new SimpleDateFormat("hh:mm aa");
            String dateString = formatter.format(new Date(val));
            return dateString;
        }
        else if(val>yesterday.getTimeInMillis())
        {
            return "yesterday";
        }
        else if(val>last7days.getTimeInMillis())
        {
            return "last 7 days";
        }
        else if(val>last30days.getTimeInMillis())
        {
            return "last 30 days";
        }
        else
        {
            return "more than 30 days";
        }
    }
}