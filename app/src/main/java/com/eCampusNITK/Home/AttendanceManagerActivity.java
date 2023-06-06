package com.eCampusNITK.Home;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.graphics.Canvas;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.eCampusNITK.Core.RealTimeDatabaseManager;
import com.eCampusNITK.Models.MySubject;
import com.eCampusNITK.Models.Subject;
import com.eCampusNITK.R;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Objects;

public class AttendanceManagerActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    ArrayList<Subject> mainSubjectArrayList;
    SubjectsAdapter subjectsAdapter;
    TextView overAllAttendance, myTextProgress;
    ProgressBar attendanceProgressBar;
    Button addSubject;
    DatabaseReference databaseReference;
    int itemChanged = -1;
    Subject subject = new Subject();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            setStatusBarTextAndBgColor();
        }
        setContentView(R.layout.activity_attendance_manager);

        findIds();
        mainSubjectArrayList = new ArrayList<>();
        databaseReference = FirebaseDatabase.getInstance().getReference();
        databaseReference.child("Attendance").child(RealTimeDatabaseManager.getInstance().getUser().getUserID()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                mainSubjectArrayList.clear();
                for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                    Subject subject = postSnapshot.getValue(Subject.class);
                    mainSubjectArrayList.add(subject);
                }
                ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleCallback);
                itemTouchHelper.attachToRecyclerView(recyclerView);

                subjectsAdapter = new SubjectsAdapter(AttendanceManagerActivity.this, mainSubjectArrayList, simpleCallback);
                recyclerView.setLayoutManager(new LinearLayoutManager(AttendanceManagerActivity.this,
                        LinearLayoutManager.VERTICAL, false));
                recyclerView.setAdapter(subjectsAdapter);
                if(itemChanged !=-1 && mainSubjectArrayList!=null && mainSubjectArrayList.size()!=0)
                    recyclerView.scrollToPosition(itemChanged);
                updateOverAllAttendance(mainSubjectArrayList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        addSubject.setOnClickListener(v -> buildDialog());
    }

    private void findIds() {
        recyclerView = findViewById(R.id.subjects_recyler_view);
        overAllAttendance = findViewById(R.id.overallAttendance);
        myTextProgress = findViewById(R.id.myTextProgress);
        attendanceProgressBar = findViewById(R.id.progress_bar);
        addSubject = findViewById(R.id.add_subject);

    }

    @SuppressLint("SetTextI18n")
    public void updateOverAllAttendance(ArrayList<Subject> subjectArrayList) {

        int total_classes = 0;
        int attended_classes = 0;
        for (Subject subject : subjectArrayList) {
            total_classes += subject.getTotal_classes();
            attended_classes += subject.getAttended_classes();
        }
        if (total_classes == 0) {
            attendanceProgressBar.setProgress(100);
            myTextProgress.setText("100%");
            overAllAttendance.setText("100%");
        } else {
            attendanceProgressBar.setProgress((attended_classes * 100 / total_classes));
            myTextProgress.setText((attended_classes * 100 / total_classes) + "%");
            overAllAttendance.setText((attended_classes * 100 / total_classes) + "%");
        }
        mainSubjectArrayList = subjectArrayList;
    }

    private void buildDialog() {
        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.add_subject_dialog);
        dialog.setCanceledOnTouchOutside(true);
        dialog.getWindow().setBackgroundDrawableResource(R.color.transparent);

        Button add_subject_dialogue_btn, cancel_btn;
        TextInputEditText subName, total_classes, attended_classes;
        add_subject_dialogue_btn = dialog.findViewById(R.id.add_subject_dialogue_btn);
        subName = dialog.findViewById(R.id.editext_subject_name);
        total_classes = dialog.findViewById(R.id.editext_total_classes);
        attended_classes = dialog.findViewById(R.id.editext_attended_classes);
        cancel_btn = dialog.findViewById(R.id.cancel_dialogue_btn);
        add_subject_dialogue_btn.setOnClickListener(v -> {

            if ((Objects.requireNonNull(total_classes.getText()).toString().isEmpty() && !Objects.requireNonNull(attended_classes.getText()).toString().isEmpty())
                    || (!total_classes.getText().toString().isEmpty() && Objects.requireNonNull(attended_classes.getText()).toString().isEmpty())) {
                Toast.makeText(this, "Please fill attended classes as well as total classes", Toast.LENGTH_LONG).show();
            } else if ((!total_classes.getText().toString().isEmpty() && !Objects.requireNonNull(attended_classes.getText()).toString().isEmpty()) && (Integer.parseInt(total_classes.getText().toString()) < Integer.parseInt(attended_classes.getText().toString()))) {
                Toast.makeText(AttendanceManagerActivity.this, "Attended Classes cannot be more than total classes", Toast.LENGTH_SHORT).show();
            } else if (Objects.requireNonNull(subName.getText()).toString().isEmpty()) {
                Toast.makeText(AttendanceManagerActivity.this, "Please Enter Subject Name", Toast.LENGTH_SHORT).show();
            } else if (!validAttendance(total_classes, attended_classes)) {
                Toast.makeText(AttendanceManagerActivity.this, "classes cannot be negative", Toast.LENGTH_SHORT).show();
            } else {
                mainSubjectArrayList.add(getSubjectDetails(total_classes.getText().toString(), Objects.requireNonNull(attended_classes.getText()).toString(), subName.getText().toString()));
                subjectsAdapter.notifyItemInserted(mainSubjectArrayList.size() - 1);
                updateAttendanceInDataBase(mainSubjectArrayList);
            }
            dialog.dismiss();
        });
        cancel_btn.setOnClickListener(v -> dialog.dismiss());
        dialog.show();
    }

    private String getStatus(int total_classes, int attended_classes) {
        if ((attended_classes * 100) / (total_classes + 1) >= 75)
            return "On Track, You may skip the next class";
        else return "You can't miss the next class";
    }

    public void updateAttendanceInDataBase(ArrayList<Subject> subjectArrayList) {
        DatabaseReference mDatabase;
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mDatabase.child("Attendance").child(RealTimeDatabaseManager.getInstance().getUser().getUserID()).
                setValue(subjectArrayList).addOnCompleteListener(task -> {
                    if (task.isSuccessful())
                        Log.d(AttendanceManagerActivity.class.getName(), "Attendance Updated");
                }).addOnCanceledListener(() -> Toast.makeText(AttendanceManagerActivity.this, "Error", Toast.LENGTH_SHORT).show());
    }

    public Subject getSubjectDetails(String total_class, String attended_classes, String subName) {
        int total = (total_class.isEmpty()) ? 0 : Integer.parseInt(total_class);
        int attended = (attended_classes.isEmpty()) ? 0 : Integer.parseInt(attended_classes);
        String status = getStatus(total, attended);
        int percentage;
        if (total == 0) percentage = 100;
        else percentage = (attended * 100) / total;
        return new Subject(subName, total, attended, status, percentage);
    }

    public boolean validAttendance(TextView total_class, TextView attended_class) {
        int total = total_class.getText().toString().isEmpty() ? 0 : Integer.parseInt(total_class.getText().toString());
        int attended = attended_class.getText().toString().isEmpty() ? 0 : Integer.parseInt(attended_class.getText().toString());
        return total >= 0 && attended >= 0;
    }


    ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
            return false;
        }

        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
            int position = viewHolder.getAdapterPosition();

            subject = mainSubjectArrayList.get(position);
            switch (direction) {
                case ItemTouchHelper.LEFT:
                case ItemTouchHelper.RIGHT:
                    mainSubjectArrayList.remove(position);
                    updateAttendanceInDataBase(mainSubjectArrayList);
                    updateOverAllAttendance(mainSubjectArrayList);
                    break;
                default:
                    break;
            }
            subjectsAdapter.notifyItemRemoved(position);
            Snackbar.make(recyclerView, "Removed Subject", Snackbar.LENGTH_LONG)
                    .setAction("UNDO", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            mainSubjectArrayList.add(position, subject);
                            updateOverAllAttendance(mainSubjectArrayList);
                            updateAttendanceInDataBase(mainSubjectArrayList);
                            subjectsAdapter.notifyItemInserted(subjectsAdapter.getItemCount());
                            recyclerView.scrollToPosition(mainSubjectArrayList.size() - 1);
                        }
                    }).setTextColor(Color.WHITE).
                    setBackgroundTint(Color.BLACK).
                    setActionTextColor(Color.GREEN).show();
        }

        @Override
        public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
            final View foregroundView = ((SubjectsAdapter.RecyclerviewHolder) viewHolder).view_foreground;
            getDefaultUIUtil().onDraw(c, recyclerView, foregroundView, dX, dY,
                    actionState, isCurrentlyActive);
        }

        @Override
        public void clearView(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
            final View foregroundView = ((SubjectsAdapter.RecyclerviewHolder) viewHolder).view_foreground;
            getDefaultUIUtil().clearView(foregroundView);
        }
    };

    @RequiresApi(api = Build.VERSION_CODES.M)
    void setStatusBarTextAndBgColor() {
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.setStatusBarColor(getResources().getColor(R.color.lightGrey));
        window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
    }
}