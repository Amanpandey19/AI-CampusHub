package com.eCampusNITK.Home;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import com.eCampusNITK.Core.RealTimeDatabaseManager;
import com.eCampusNITK.Models.Class_Subject;
import com.eCampusNITK.Models.MySubject;
import com.eCampusNITK.Models.SubjectList;
import com.eCampusNITK.Models.Syllabus;
import com.eCampusNITK.Models.User;
import com.eCampusNITK.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;


public class AddSubjectActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    AllClassesAdapter allClassesAdapter;
    ArrayList<Class_Subject> class_subjectArrayList = new ArrayList<>();
    ArrayList<MySubject> subjectArrayListInDataBase = new ArrayList<>();
    Button saveChanges;
    User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            setStatusBarTextAndBgColor();
        }
        setContentView(R.layout.activity_add_subject);

        recyclerView = findViewById(R.id.class_subject_recyler_view);
        saveChanges  = findViewById(R.id.save_changes_btn);
        user         = RealTimeDatabaseManager.getInstance().getUser();


        setSubjectList(class_subjectArrayList);


        saveChanges.setOnClickListener(v -> uploadSelectedSubjects(allClassesAdapter.getSelectedSubjectArrayList()));

    }
    void setSubjectList(ArrayList<Class_Subject> class_subjectArrayList)
    {
        ProgressDialog pd = new ProgressDialog(AddSubjectActivity.this);
        pd.setMessage("loading");
        pd.show();
        SubjectList subjectList = new SubjectList();
        class_subjectArrayList.addAll(subjectList.getSubjectList());

        setSubjectListInDataBase();
        ArrayList<Class_Subject> selectedSubjectsInDataBase = new ArrayList<>();

        FirebaseDatabase database             = FirebaseDatabase.getInstance();
        DatabaseReference databaseReference   = database.getReference("StudyMaterial").child("SelectedSubjects").child(user.getUserID());
        databaseReference.get().addOnCompleteListener(task -> {
            if(task.isSuccessful())
            {
                if(task.getResult()!=null)
                {
                    DataSnapshot snapshot = task.getResult();
                    for (DataSnapshot postSnapshot: snapshot.getChildren()) {
                        if(postSnapshot.exists()){
                            Class_Subject class_subject = postSnapshot.getValue(Class_Subject.class);
                            selectedSubjectsInDataBase.add(class_subject);
                        }
                    }
                    for(Class_Subject class_subject : class_subjectArrayList)
                    {
                        if(containsInDataBase(selectedSubjectsInDataBase, class_subject.getSubject_name())) class_subject.setSelected(true);
                    }
                    allClassesAdapter = new AllClassesAdapter(AddSubjectActivity.this , class_subjectArrayList);
                    recyclerView.setLayoutManager(new LinearLayoutManager(AddSubjectActivity.this, LinearLayoutManager.VERTICAL, false));
                    recyclerView.setAdapter(allClassesAdapter);

                }
                pd.dismiss();
            }
        });
    }

    void  setSubjectListInDataBase()
    {
        subjectArrayListInDataBase = new ArrayList<>();
        if(null!=RealTimeDatabaseManager.getInstance().getAllSubjects())
        {
            subjectArrayListInDataBase.addAll(RealTimeDatabaseManager.getInstance().getAllSubjects());
        }
        int previousSize = subjectArrayListInDataBase.size();
        for(Class_Subject subject : class_subjectArrayList)
        {
            if(!containsThisSubject(subjectArrayListInDataBase, subject.getSubject_name()))
            {
                MySubject newSubject = new MySubject(subject.getSubject_name(), new ArrayList<>(), new ArrayList<>(), new Syllabus());
                subjectArrayListInDataBase.add(newSubject);
            }
        }
        int currSize = subjectArrayListInDataBase.size();
        if(currSize!=previousSize)
        {
            uploadSubjectsInDataBase(subjectArrayListInDataBase);
        }
    }

    public boolean containsThisSubject(final List<MySubject> list, final String name){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            return list.stream().anyMatch(o -> o.getSubjectName().equals(name));
        }
        return false;
    }

    public boolean containsInDataBase(final List<Class_Subject> list, final String name){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            return list.stream().anyMatch(o -> o.getSubject_name().equals(name));
        }
        return false;
    }

    public void uploadSubjectsInDataBase(ArrayList<MySubject> subjectArrayList)
    {
        DatabaseReference mDatabase;
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mDatabase.child("StudyMaterial").child("AllSubjects").
                setValue(subjectArrayList).addOnCompleteListener(task -> {
                    if(task.isSuccessful())
                    {
                        Log.d(AttendanceManagerActivity.class.getName(),"Subject List Updated in DataBase");
                    }

                }).addOnCanceledListener(() ->  Toast.makeText(AddSubjectActivity.this, "Error", Toast.LENGTH_SHORT).show());

    }
    public void uploadSelectedSubjects(ArrayList<Class_Subject> selectedSubjects)
    {
        DatabaseReference mDatabase;
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mDatabase.child("StudyMaterial").child("SelectedSubjects").child(user.getUserID()).
                setValue(selectedSubjects).addOnCompleteListener(task -> {
                    if(task.isSuccessful()){
                        Log.d(AttendanceManagerActivity.class.getName(),"Subject List Updated in DataBase");
                        finish();
                    }
                }).addOnCanceledListener(() -> Toast.makeText(AddSubjectActivity.this, "Error", Toast.LENGTH_SHORT).show());

    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    void setStatusBarTextAndBgColor(){
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);//  set status text dark
        window.setStatusBarColor(ContextCompat.getColor(this,R.color.bg_theme));// set status background white
    }
}