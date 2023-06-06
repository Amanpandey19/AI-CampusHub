package com.eCampusNITK.Home;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.ColorStateList;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;
import com.eCampusNITK.Models.Subject;
import com.eCampusNITK.R;

import java.util.ArrayList;

public class SubjectsAdapter extends RecyclerView.Adapter<SubjectsAdapter.RecyclerviewHolder> {
    ArrayList<Subject> subjectArrayList;
    Context context;

    ItemTouchHelper.SimpleCallback simpleCallback;

    public SubjectsAdapter(Context context, ArrayList<Subject> subjectArrayList,
                           ItemTouchHelper.SimpleCallback simpleCallback) {
        this.context              = context;
        this.subjectArrayList     = subjectArrayList;
        this.simpleCallback       = simpleCallback;

    }


    @NonNull
    @Override
    public RecyclerviewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View mRootView = LayoutInflater.from(parent.getContext()).inflate(R.layout.add_subject_item, parent, false);
        return new RecyclerviewHolder(mRootView);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull RecyclerviewHolder holder, @SuppressLint("RecyclerView") int position) {
        holder.subject_name.setText(subjectArrayList.get(position).getSubject_name());
        holder.attendance_out_of.setText(subjectArrayList.get(position).getAttended_classes()+"/"+subjectArrayList.get(position).getTotal_classes());
        holder.subject_status.setText("Status : "+subjectArrayList.get(position).getStatus());
        holder.attendance_percentage.setText(subjectArrayList.get(position).getPercentage()+"%");
        holder.progressBar.setProgress(subjectArrayList.get(position).getPercentage());
        if(subjectArrayList.get(position).getPercentage()<75) holder.progressBar.setProgressTintList(ColorStateList.valueOf(context.getColor(R.color.red)));
        else holder.progressBar.setProgressTintList(ColorStateList.valueOf(context.getColor(R.color.green)));
        holder.class_attended.setOnClickListener(v -> {

            subjectArrayList.get(position).setAttended_classes(subjectArrayList.get(position).getAttended_classes()+1);
            subjectArrayList.get(position).setTotal_classes(subjectArrayList.get(position).getTotal_classes()+1);

            holder.attendance_out_of.setText(subjectArrayList.get(position).getAttended_classes()+"/"+subjectArrayList.get(position).getTotal_classes());
            holder.attendance_percentage.setText((subjectArrayList.get(position).getAttended_classes()*100/subjectArrayList.get(position).getTotal_classes())+"%");

            subjectArrayList.get(position).setPercentage((subjectArrayList.get(position).getAttended_classes()*100/subjectArrayList.get(position).getTotal_classes()));
            holder.progressBar.setProgress(subjectArrayList.get(position).getPercentage());
            if((subjectArrayList.get(position).getAttended_classes()*100/(subjectArrayList.get(position).getTotal_classes()))<75)
            {
                subjectArrayList.get(position).setStatus("You can't miss the next class");
                holder.subject_status.setText("Status : "+subjectArrayList.get(position).getStatus());
            }
            else if((subjectArrayList.get(position).getAttended_classes()*100/(subjectArrayList.get(position).getTotal_classes()+1))<75)
            {
                subjectArrayList.get(position).setStatus("On Track, You can't miss the next class");
                holder.subject_status.setText("Status : "+subjectArrayList.get(position).getStatus());
            }else
            {
                subjectArrayList.get(position).setStatus("On Track, You can miss the next class");
                holder.subject_status.setText("Status : "+subjectArrayList.get(position).getStatus());
            }

            if(subjectArrayList.get(position).getPercentage()<75) holder.progressBar.setProgressTintList(ColorStateList.valueOf(context.getColor(R.color.red)));
            else holder.progressBar.setProgressTintList(ColorStateList.valueOf(context.getColor(R.color.green)));

            if( context instanceof AttendanceManagerActivity)
            {
                ((AttendanceManagerActivity) context).updateOverAllAttendance(subjectArrayList);
                ((AttendanceManagerActivity) context).updateAttendanceInDataBase(subjectArrayList);
                ((AttendanceManagerActivity) context).itemChanged = holder.getAdapterPosition();
            }
            notifyItemChanged(position);
        });


        holder.class_not_attended.setOnClickListener(v -> {

            subjectArrayList.get(position).setTotal_classes(subjectArrayList.get(position).getTotal_classes()+1);

            holder.attendance_out_of.setText(subjectArrayList.get(position).getAttended_classes()+"/"+subjectArrayList.get(position).getTotal_classes());
            holder.attendance_percentage.setText((subjectArrayList.get(position).getAttended_classes()*100/subjectArrayList.get(position).getTotal_classes())+"%");

            subjectArrayList.get(position).setPercentage((subjectArrayList.get(position).getAttended_classes()*100/subjectArrayList.get(position).getTotal_classes()));
            holder.progressBar.setProgress(subjectArrayList.get(position).getPercentage());
            if((subjectArrayList.get(position).getAttended_classes()*100/(subjectArrayList.get(position).getTotal_classes()))<75)
            {
                subjectArrayList.get(position).setStatus("You can't miss the next class");
                holder.subject_status.setText("Status : "+subjectArrayList.get(position).getStatus());
            }
            else if((subjectArrayList.get(position).getAttended_classes()*100/(subjectArrayList.get(position).getTotal_classes()+1))<75)
            {
                subjectArrayList.get(position).setStatus("On Track, You can't miss the next class");
                holder.subject_status.setText("Status : "+subjectArrayList.get(position).getStatus());
            }else
            {
                subjectArrayList.get(position).setStatus("On Track, You can miss the next class");
                holder.subject_status.setText("Status : "+subjectArrayList.get(position).getStatus());
            }

            if(subjectArrayList.get(position).getPercentage()<75) holder.progressBar.setProgressTintList(ColorStateList.valueOf(context.getColor(R.color.red)));
            else holder.progressBar.setProgressTintList(ColorStateList.valueOf(context.getColor(R.color.green)));


            if( context instanceof AttendanceManagerActivity)
            {
                ((AttendanceManagerActivity) context).updateOverAllAttendance(subjectArrayList);
                ((AttendanceManagerActivity) context).updateAttendanceInDataBase(subjectArrayList);
                ((AttendanceManagerActivity) context).itemChanged = holder.getAdapterPosition();
            }

            notifyItemChanged(position);
        });

    }

    @Override
    public int getItemCount() {
        return subjectArrayList.size();
    }

    public static final class RecyclerviewHolder extends RecyclerView.ViewHolder {
        ProgressBar progressBar;
        TextView subject_name;
        TextView attendance_out_of;
        TextView subject_status;
        TextView attendance_percentage;
        LinearLayout view_foreground;
        RelativeLayout view_background;
        ImageView class_attended, class_not_attended;

        public RecyclerviewHolder(@NonNull View itemView) {
            super(itemView);
            progressBar                        =   itemView.findViewById(R.id.progress_bar);
            subject_name                       =   itemView.findViewById(R.id.subject_name);
            attendance_out_of                  =   itemView.findViewById(R.id.attendance_out_of);
            subject_status                     =   itemView.findViewById(R.id.subject_status);
            attendance_percentage              =   itemView.findViewById(R.id.subject_attendance_percentage);
            class_attended                     =   itemView.findViewById(R.id.class_attended);
            class_not_attended                 =   itemView.findViewById(R.id.classes_not_attended);
            view_background                    =   itemView.findViewById(R.id.view_background);
            view_foreground                    =   itemView.findViewById(R.id.view_foreground);


        }


    }

}
