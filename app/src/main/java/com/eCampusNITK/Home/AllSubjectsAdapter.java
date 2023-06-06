package com.eCampusNITK.Home;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.eCampusNITK.Models.Class_Subject;
import com.eCampusNITK.Models.MySubject;
import com.eCampusNITK.R;

import java.util.ArrayList;

public class AllSubjectsAdapter extends RecyclerView.Adapter<AllSubjectsAdapter.RecyclerviewHolder> {
    private ArrayList<MySubject> mySubjectArrayList;
    Context  context;
    Activity activity;


    public AllSubjectsAdapter(Context context, Activity activity, ArrayList<MySubject> mySubjectArrayList) {
        this.context               = context;
        this.activity              = activity;
        this.mySubjectArrayList    = mySubjectArrayList;
    }


    @NonNull
    @Override
    public RecyclerviewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View mRootView = LayoutInflater.from(parent.getContext()).inflate(R.layout.my_class_item, parent, false);
        Animation animation= AnimationUtils.loadAnimation(context,R.anim.animate_slide_left_enter);
        mRootView.setAnimation(animation);
        return new RecyclerviewHolder(mRootView);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull RecyclerviewHolder holder, @SuppressLint("RecyclerView") int position) {
        holder.subjectName.setText(mySubjectArrayList.get(position).getSubjectName());
        if(null!=mySubjectArrayList.get(position).getSubjectLinks()) holder.subjectLinks.setText(mySubjectArrayList.get(position).getSubjectLinks().size()+" Links Available");
        else holder.subjectLinks.setText("0 Links Available");
        if(null!=mySubjectArrayList.get(position).getNotes()) holder.subjectNotes.setText(mySubjectArrayList.get(position).getNotes().size()+" Notes Available");
        else holder.subjectNotes.setText("0 Notes Available");

        holder.cardView.setOnClickListener(v -> {
            Intent intent = new Intent(activity, ViewSubjectDetailsActivity.class);
            Bundle bundle = new Bundle();
            bundle.putString("subName", mySubjectArrayList.get(position).getSubjectName());
            intent.putExtras(bundle);
            context.startActivity(intent);
        });
    }

    public ArrayList<MySubject> getSubject_arrayList()
    {
        return mySubjectArrayList;
    }


    @Override
    public int getItemCount() {
        return mySubjectArrayList.size();
    }

    public static final class RecyclerviewHolder extends RecyclerView.ViewHolder {
        TextView  subjectName;
        TextView  subjectLinks;
        TextView  subjectNotes;
        CardView cardView;

        public RecyclerviewHolder(@NonNull View itemView) {
            super(itemView);
            cardView                        =   itemView.findViewById(R.id.my_subject_cardview);
            subjectName                     =   itemView.findViewById(R.id.class_name);
            subjectLinks                    =   itemView.findViewById(R.id.class_links_number);
            subjectNotes                    =   itemView.findViewById(R.id.class_notes_number);
        }

    }
}
