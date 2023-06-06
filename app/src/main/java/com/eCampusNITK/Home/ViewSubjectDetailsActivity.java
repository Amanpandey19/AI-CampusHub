package com.eCampusNITK.Home;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.eCampusNITK.Models.Links;
import com.eCampusNITK.Models.MySubject;
import com.eCampusNITK.Models.Notes;
import com.eCampusNITK.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class ViewSubjectDetailsActivity extends AppCompatActivity {

    TextView subName, uploadSyllabusTv, noNotesAvailableTv, noLinksAvailableTv;
    TextView syllabusUrl, noSyllabusTv, postedBy, postTime, uploadNotesTv, uploadLinksTv;
    String   sub = "";
    DatabaseReference subjectsRef;
    MySubject thisSubject;
    LinearLayout syllabusView;
    int posOfSubjectInArrayList = -1;
    RecyclerView notesRecyclerView, linksRecyclerView;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            setStatusBarTextAndBgColor();
        }
        setContentView(R.layout.activity_view_subject_details);
        findIds();
        sub                = getIntent().getExtras().getString("subName");
        subName.setText(sub);
        thisSubject        = new MySubject();
        subjectsRef        = FirebaseDatabase.getInstance().getReference("StudyMaterial").child("AllSubjects");


        uploadSyllabusTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ViewSubjectDetailsActivity.this, UploadSyllabusActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("subName", sub);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });

        uploadLinksTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ViewSubjectDetailsActivity.this, UploadLinkActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("subName", sub);
                bundle.putInt("pos",posOfSubjectInArrayList);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });

        uploadNotesTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ViewSubjectDetailsActivity.this, UploadNotesActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("subName", sub);
                bundle.putInt("pos",posOfSubjectInArrayList);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });

        syllabusUrl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ViewSubjectDetailsActivity.this, PdfViewActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("url", thisSubject.getSyllabus().getSyllabusUrl());
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });


        subjectsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists())
                {
                    int i = 0;
                    for(DataSnapshot dataSnapshot : snapshot.getChildren())
                    {
                        MySubject mySubject = dataSnapshot.getValue(MySubject.class);
                        if(mySubject!=null && mySubject.getSubjectName().equals(sub))
                        {
                            thisSubject.setSubjectName(mySubject.getSubjectName());
                            thisSubject.setSyllabus(mySubject.getSyllabus());
                            thisSubject.setSubjectLinks(mySubject.getSubjectLinks());
                            thisSubject.setNotes(mySubject.getNotes());
                            setSyllabusData(thisSubject);
                            break;
                        }
                        i++;
                    }
                    posOfSubjectInArrayList = i;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    void findIds()
    {
        subName             = findViewById(R.id.subject_name);
        uploadSyllabusTv    = findViewById(R.id.upload_Syllabus);
        syllabusUrl         = findViewById(R.id.syllabus_url);
        noSyllabusTv        = findViewById(R.id.no_syllabus_available_text);
        postedBy            = findViewById(R.id.syllabus_author);
        postTime            = findViewById(R.id.syllabus_uploadtime);
        syllabusView        = findViewById(R.id.syllabus_view);
        noLinksAvailableTv  = findViewById(R.id.no_Links_available_text);
        noNotesAvailableTv  = findViewById(R.id.no_Notes_available_text);
        uploadLinksTv       = findViewById(R.id.upload_Links);
        uploadNotesTv       = findViewById(R.id.upload_Notes);
        notesRecyclerView   = findViewById(R.id.notes_recylerview);
        linksRecyclerView   = findViewById(R.id.links_recylerview);
    }

    @SuppressLint("SetTextI18n")
    void setSyllabusData(MySubject subject)
    {
        if(subject.getSyllabus()==null)
        {
            noSyllabusTv.setVisibility(View.VISIBLE);
            syllabusView.setVisibility(View.GONE);
        }else {
            noSyllabusTv.setVisibility(View.GONE);
            syllabusView.setVisibility(View.VISIBLE);
            postedBy.setText("Uploaded by - "+subject.getSyllabus().getPostedBy());
            postTime.setText(convertSimpleDayFormat(Long.parseLong(subject.getSyllabus().getPostedDate())));
            //syllabusUrl.setText(subject.getSyllabus().getSyllabusUrl());
        }

        setNotesData(subject.getNotes());
        setLinksData(subject.getSubjectLinks());
    }

    void setNotesData(ArrayList<Notes> notesArrayList)
    {
        if(notesArrayList==null)
            noNotesAvailableTv.setVisibility(View.VISIBLE);
        else {
            noNotesAvailableTv.setVisibility(View.GONE);
            setNotesRecyclerView(notesArrayList);
        }
    }
    void setLinksData(ArrayList<Links> linksArrayList)
    {
        if(linksArrayList==null)
            noLinksAvailableTv.setVisibility(View.VISIBLE);
        else {
            noLinksAvailableTv.setVisibility(View.GONE);
            setLinksRecyclerView(linksArrayList);
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
            return "more than 30days";
        }
    }

    void setNotesRecyclerView(ArrayList<Notes> notesArrayList)
    {
        NotesAdapter notesAdapter = new NotesAdapter(this,notesArrayList);
        notesRecyclerView.setLayoutManager(new LinearLayoutManager(ViewSubjectDetailsActivity.this, LinearLayoutManager.VERTICAL, false));
        notesRecyclerView.setAdapter(notesAdapter);
    }
    void setLinksRecyclerView(ArrayList<Links> linksArrayList)
    {
        LinksAdapter linksAdapter = new LinksAdapter(this,linksArrayList);
        linksRecyclerView.setLayoutManager(new LinearLayoutManager(ViewSubjectDetailsActivity.this, LinearLayoutManager.VERTICAL, false));
        linksRecyclerView.setAdapter(linksAdapter);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    void setStatusBarTextAndBgColor(){
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);//  set status text
        window.setStatusBarColor(ContextCompat.getColor(this,R.color.grey_theme));// set status background
    }
}