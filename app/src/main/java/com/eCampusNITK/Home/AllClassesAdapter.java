package com.eCampusNITK.Home;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.alexzh.circleimageview.CircleImageView;
import com.eCampusNITK.Models.Class_Subject;
import com.eCampusNITK.Models.Posts;
import com.eCampusNITK.Models.Subject;
import com.eCampusNITK.R;

import java.util.ArrayList;

public class AllClassesAdapter extends RecyclerView.Adapter<AllClassesAdapter.RecyclerviewHolder> {
    private ArrayList<Class_Subject> class_subjectArrayList;
    Context context;


    public AllClassesAdapter(Context context, ArrayList<Class_Subject> class_subjectArrayList) {
        this.context           = context;
        this.class_subjectArrayList    = class_subjectArrayList;
    }


    @NonNull
    @Override
    public RecyclerviewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View mRootView = LayoutInflater.from(parent.getContext()).inflate(R.layout.my_classes_items, parent, false);
        Animation animation= AnimationUtils.loadAnimation(context,R.anim.animate_slide_left_enter);
        mRootView.setAnimation(animation);
        return new RecyclerviewHolder(mRootView);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull RecyclerviewHolder holder, int position) {
        holder.name.setText(class_subjectArrayList.get(position).getSubject_name());
        holder.isChecked.setChecked(class_subjectArrayList.get(position).getSelected());
        holder.isChecked.setOnClickListener(v -> {
            holder.isChecked.setChecked(!class_subjectArrayList.get(position).getSelected());
            class_subjectArrayList.get(position).setSelected(!class_subjectArrayList.get(position).getSelected());

        });
    }

    public ArrayList<Class_Subject> getClass_subjectArrayList()
    {
        return class_subjectArrayList;
    }

    @Override
    public int getItemCount() {
        return class_subjectArrayList.size();
    }

    public ArrayList<Class_Subject> getSelectedSubjectArrayList() {
        ArrayList<Class_Subject> selectedSubjects = new ArrayList<>();
        for(Class_Subject class_subject: class_subjectArrayList)
        {
            if(class_subject.getSelected()) selectedSubjects.add(class_subject);
        }
        return selectedSubjects;
    }

    public static final class RecyclerviewHolder extends RecyclerView.ViewHolder {
        TextView  name;
        CheckBox  isChecked;

        public RecyclerviewHolder(@NonNull View itemView) {
            super(itemView);
            name                     =   itemView.findViewById(R.id.class_subject_name);
            isChecked                =   itemView.findViewById(R.id.class_subject_checkbox);
        }

    }
}
