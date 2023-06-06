package com.eCampusNITK.Home;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.eCampusNITK.Models.Links;
import com.eCampusNITK.Models.Notes;
import com.eCampusNITK.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class NotesAdapter extends RecyclerView.Adapter<NotesAdapter.RecyclerviewHolder> {
    private ArrayList<Notes> notesArrayList;
    Context context;


    public NotesAdapter(Context context, ArrayList<Notes> notesArrayList) {
        this.context                   = context;
        this.notesArrayList            = notesArrayList;
    }


    @NonNull
    @Override
    public RecyclerviewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View mRootView = LayoutInflater.from(parent.getContext()).inflate(R.layout.notes_items, parent, false);
        //Animation animation= AnimationUtils.loadAnimation(context,R.anim.animate_slide_left_enter);
        //mRootView.setAnimation(animation);
        return new RecyclerviewHolder(mRootView);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull RecyclerviewHolder holder, int position) {
        Notes notes = notesArrayList.get(position);
        holder.notesName.setText(notes.getNotesName());
        holder.notesDesc.setText(notes.getDescription());
        //holder.notesUrl.setText(notes.getUrl());
        holder.notesUrl.setOnClickListener(v -> {
            Intent intent = new Intent(context, PdfViewActivity.class);
            Bundle bundle = new Bundle();
            bundle.putString("url", notes.getUrl());
            intent.putExtras(bundle);
            context.startActivity(intent);
        });
        holder.author.setText("Uploaded by "+notes.getAuthor());
        holder.postTime.setText(convertSimpleDayFormat(Long.parseLong(notes.getPostedDate())));
    }

    @Override
    public int getItemCount() {
        return notesArrayList.size();
    }


    public static final class RecyclerviewHolder extends RecyclerView.ViewHolder {
        TextView  notesDesc;
        TextView  notesName;
        TextView  notesUrl;
        TextView  author;
        TextView  postTime;

        public RecyclerviewHolder(@NonNull View itemView) {
            super(itemView);
            notesName  = itemView.findViewById(R.id.notes_name);
            notesDesc  = itemView.findViewById(R.id.notes_description);
            notesUrl   = itemView.findViewById(R.id.notes_url);
            author    = itemView.findViewById(R.id.notes_author);
            postTime  = itemView.findViewById(R.id.notes_uploadtime);
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
