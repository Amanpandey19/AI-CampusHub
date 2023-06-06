package com.eCampusNITK.Home;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import com.eCampusNITK.Core.RealTimeDatabaseManager;
import com.eCampusNITK.Models.Links;
import com.eCampusNITK.Models.MySubject;
import com.eCampusNITK.Models.User;
import com.eCampusNITK.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class UploadLinkActivity extends AppCompatActivity {

    TextInputEditText linkEditText, linkDescriptionEditText;
    Button uploadLink;
    String subName = "";
    int posOfSubjectInArrayList= -1;
    User user;
    DatabaseReference mDatabaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            setStatusBarTextAndBgColor();
        }
        setContentView(R.layout.activity_upload_link);
        findIDs();
        subName                 = getIntent().getExtras().getString("subName");
        posOfSubjectInArrayList = getIntent().getExtras().getInt("pos");
        user                    = RealTimeDatabaseManager.getInstance().getUser();
        mDatabaseReference      = FirebaseDatabase.getInstance().getReference("StudyMaterial").child("AllSubjects");
        uploadLink.setOnClickListener(v -> {
            if(!isValidInput())
            {
                Toast.makeText(UploadLinkActivity.this, "Please make sure the input is Valid", Toast.LENGTH_SHORT).show();
            }
            else
            {
                String currTime  = String.valueOf(System.currentTimeMillis());
                Links newLink    = new Links(linkEditText.getText().toString(),user.getName(), linkDescriptionEditText.getText().toString(), currTime);
                uploadLInkInDataBase(newLink);
            }
        });
    }

    void findIDs()
    {
        linkEditText             =   findViewById(R.id.editext_link);
        linkDescriptionEditText  =   findViewById(R.id.editext_link_description);
        uploadLink               =   findViewById(R.id.upload_link_btn);
    }

    boolean isValidInput()
    {
        return !linkEditText.getText().toString().trim().isEmpty() && !linkDescriptionEditText.getText().toString().trim().isEmpty();
    }

    void uploadLInkInDataBase(Links newLink)
    {
        mDatabaseReference.child(""+posOfSubjectInArrayList).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if(task.getResult().exists())
                {
                    DataSnapshot snapshot = task.getResult();
                    MySubject thisSubject = snapshot.getValue(MySubject.class);
                    if(thisSubject!=null)
                    {
                        ArrayList<Links> newLinkArrayList = new ArrayList<>();
                        if(thisSubject.getSubjectLinks()!=null) newLinkArrayList.addAll(thisSubject.getSubjectLinks());
                        newLinkArrayList.add(newLink);
                        thisSubject.setSubjectLinks(newLinkArrayList);
                        mDatabaseReference.child(""+posOfSubjectInArrayList).setValue(thisSubject);
                        Toast.makeText(UploadLinkActivity.this, "Link Added Successfully", Toast.LENGTH_SHORT).show();
                        linkEditText.setText("");
                        linkDescriptionEditText.setText("");
                    }
                }
            }
        });
    }

    void setStatusBarTextAndBgColor(){
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.setStatusBarColor(this.getResources().getColor(R.color.black));
        View decorView = window.getDecorView();
        decorView.setSystemUiVisibility(decorView.getSystemUiVisibility() & ~View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR); //set status text  light
    }
}