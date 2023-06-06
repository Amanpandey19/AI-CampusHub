package com.eCampusNITK.Home;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.eCampusNITK.Models.Class_Subject;
import com.eCampusNITK.Models.Links;
import com.eCampusNITK.R;

import org.w3c.dom.Text;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class LinksAdapter extends RecyclerView.Adapter<LinksAdapter.RecyclerviewHolder> {
    private ArrayList<Links> linksArrayList;
    Context context;


    public LinksAdapter(Context context, ArrayList<Links> linksArrayList) {
        this.context                   = context;
        this.linksArrayList            = linksArrayList;
    }


    @NonNull
    @Override
    public RecyclerviewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View mRootView = LayoutInflater.from(parent.getContext()).inflate(R.layout.links_items, parent, false);
        //Animation animation= AnimationUtils.loadAnimation(context,R.anim.animate_slide_left_enter);
        //mRootView.setAnimation(animation);
        return new RecyclerviewHolder(mRootView);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull RecyclerviewHolder holder, int position) {
        Links thisLink = linksArrayList.get(position);
        holder.linkDesc.setText(thisLink.getDescription());
        holder.linkUrl.setText(thisLink.getLink());
        holder.author.setText("Uploaded by -"+thisLink.getAuthor());
        holder.postTime.setText(convertSimpleDayFormat(Long.parseLong(thisLink.getTimeOfPosting())));
    }

    public ArrayList<Links> getLinksArrayList()
    {
        return linksArrayList;
    }

    @Override
    public int getItemCount() {
        return linksArrayList.size();
    }


    public static final class RecyclerviewHolder extends RecyclerView.ViewHolder {
        TextView  linkDesc;
        TextView  linkUrl;
        TextView  author;
        TextView  postTime;

        public RecyclerviewHolder(@NonNull View itemView) {
            super(itemView);
            linkDesc  = itemView.findViewById(R.id.Link_description);
            linkUrl   = itemView.findViewById(R.id.Link_url);
            author    = itemView.findViewById(R.id.Link_author);
            postTime  = itemView.findViewById(R.id.Link_uploadtime);

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
