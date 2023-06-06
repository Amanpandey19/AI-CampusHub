package com.eCampusNITK.Home;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.documentfile.provider.DocumentFile;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.eCampusNITK.Core.RealTimeDatabaseManager;
import com.eCampusNITK.Models.MySubject;
import com.eCampusNITK.Models.Syllabus;
import com.eCampusNITK.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;


public class UploadSyllabusActivity extends AppCompatActivity implements View.OnClickListener {

    TextView pdfText;
    Button  selectPdf, uploadPdf;
    Uri imageuri = null;
    //this is the pic pdf code used in file chooser
    final static int PICK_PDF_CODE = 2342;

    //the firebase objects for storage and database
    StorageReference mStorageReference;
    DatabaseReference mDatabaseReference;
    DatabaseReference subjectsRef;
    String subName;
    String userName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            setStatusBarTextAndBgColor();
        }
        setContentView(R.layout.activity_upload_syllabus);
        findIds();

        //getting firebase objects
        mStorageReference  = FirebaseStorage.getInstance().getReference();
        mDatabaseReference = FirebaseDatabase.getInstance().getReference("StudyMaterial").child("AllSubjects");
        subjectsRef        = FirebaseDatabase.getInstance().getReference("StudyMaterial").child("AllSubjects");

        subName = getIntent().getExtras().getString("subName");
        userName = RealTimeDatabaseManager.getInstance().getUser().getName();

        //attaching listeners to views
        selectPdf.setOnClickListener(this);
        uploadPdf.setOnClickListener(this);
    }
    void findIds()
    {
        pdfText   = findViewById(R.id.pdfText);
        selectPdf = findViewById(R.id.select_syllabus_btn);
        uploadPdf = findViewById(R.id.upload_syllabus_btn);
    }

    private void getPDF() {
        //for greater than lolipop versions we need the permissions asked on runtime
        //so if the permission is not available user will go to the screen to allow storage permission
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                    Uri.parse("package:" + getPackageName()));
            startActivity(intent);
            return;
        }

        //creating an intent for file chooser
        Intent intent = new Intent();
        intent.setType("application/pdf");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_PDF_CODE);
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.select_syllabus_btn:
                getPDF();
                break;
            case R.id.upload_syllabus_btn:
                uploadFile(imageuri);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //when the user choses the file
        if (requestCode == PICK_PDF_CODE && resultCode == RESULT_OK && data != null && data.getData() != null) {
            //if a file is selected
            if (data.getData() != null) {
                //uploading the file
                imageuri = data.getData();
                pdfText.setText(DocumentFile.fromSingleUri(UploadSyllabusActivity.this, imageuri).getName());
            }else{
                Toast.makeText(this, "No file chosen", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void uploadFile(Uri data) {
        if(null!=data)
        {
            ProgressDialog pd = new ProgressDialog(this);
            pd.setMessage("loading");
            pd.show();

            StorageReference sRef = mStorageReference.child("Syllabus" + subName + ".pdf");
            sRef.putFile(data)
                    .addOnSuccessListener(taskSnapshot -> sRef.getDownloadUrl().addOnSuccessListener(uri -> {
                        String currTime  = String.valueOf(System.currentTimeMillis());
                        Syllabus syllabus = new Syllabus(uri.toString(),userName, currTime);
                        subjectsRef.get().addOnCompleteListener(task -> {
                            int i=0;
                            for(DataSnapshot snapshot : task.getResult().getChildren())
                            {
                                MySubject mySubject = snapshot.getValue(MySubject.class);
                                if(mySubject != null && mySubject.getSubjectName().equals(subName))
                                {
                                    mySubject.setSyllabus(syllabus);
                                    mDatabaseReference.child(""+i).setValue(mySubject);
                                    Toast.makeText(UploadSyllabusActivity.this, "Syllabus Uploaded Successfully", Toast.LENGTH_SHORT).show();
                                    imageuri = null;
                                    break;
                                }
                                i++;
                            }
                            pd.dismiss();
                        });

                    }))
                    .addOnFailureListener(exception -> {
                        Toast.makeText(getApplicationContext(), exception.getMessage(), Toast.LENGTH_LONG).show();
                        pd.dismiss();
                    });

        }
        else {

            Toast.makeText(this, "No data to upload", Toast.LENGTH_SHORT).show();
        }

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