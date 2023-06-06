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
import android.widget.Toast;

import com.eCampusNITK.Core.RealTimeDatabaseManager;
import com.eCampusNITK.Models.Links;
import com.eCampusNITK.Models.MySubject;
import com.eCampusNITK.Models.Notes;
import com.eCampusNITK.Models.Syllabus;
import com.eCampusNITK.Models.User;
import com.eCampusNITK.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;

public class UploadNotesActivity extends AppCompatActivity implements View.OnClickListener {

    TextInputEditText topic_name, topic_desc;
    Button selectPdf, UploadPdf;
    final static int PICK_PDF_CODE = 2342;
    String subName="";
    int posOfSubjectInArrayList= -1;
    Uri imageUri = null;
    User user;
    DatabaseReference databaseReference, subjectsRef;
    StorageReference  mStorageReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            setStatusBarTextAndBgColor();
        }
        setContentView(R.layout.activity_upload_notes);
        user = RealTimeDatabaseManager.getInstance().getUser();
        findIds();
        //attaching listeners to views
        selectPdf.setOnClickListener(this);
        UploadPdf.setOnClickListener(this);

        subName                 = getIntent().getExtras().getString("subName");
        posOfSubjectInArrayList = getIntent().getExtras().getInt("pos");


        mStorageReference  = FirebaseStorage.getInstance().getReference();
        databaseReference  = FirebaseDatabase.getInstance().getReference("StudyMaterial").child("AllSubjects").child(""+posOfSubjectInArrayList);
        subjectsRef        = FirebaseDatabase.getInstance().getReference("StudyMaterial").child("AllSubjects").child(""+posOfSubjectInArrayList);

    }
    void findIds()
    {
        topic_desc = findViewById(R.id.editext_topic_description);
        topic_name = findViewById(R.id.editext_topic_name);
        selectPdf  = findViewById(R.id.select_notes_btn);
        UploadPdf  = findViewById(R.id.upload_notes_btn);
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.select_notes_btn:
                getPDF();
                break;
            case R.id.upload_notes_btn:
                uploadFile(imageUri);
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
                imageUri = data.getData();
                //pdfText.setText(DocumentFile.fromSingleUri(UploadNotesActivity.this, imageUri).getName());
            }else{
                Toast.makeText(this, "No file chosen", Toast.LENGTH_SHORT).show();
            }
        }
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

    private void uploadFile(Uri data) {
        if(null!=data)
        {
            ProgressDialog pd = new ProgressDialog(this);
            pd.setMessage("loading");
            pd.setCanceledOnTouchOutside(false);
            pd.show();

            StorageReference sRef = mStorageReference.child("Notes" + System.currentTimeMillis() + ".pdf");
            sRef.putFile(data)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            sRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    String currTime  = String.valueOf(System.currentTimeMillis());
                                    Notes  notes    = new Notes(topic_name.getText().toString(),uri.toString(),
                                            topic_desc.getText().toString(),user.getName(), currTime);
                                    subjectsRef.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<DataSnapshot> task) {
                                            if(task.getResult().exists())
                                            {
                                                DataSnapshot snapshot = task.getResult();
                                                MySubject thisSubject = snapshot.getValue(MySubject.class);
                                                if(thisSubject!=null)
                                                {
                                                    ArrayList<Notes> newNotesArrayList = new ArrayList<>();
                                                    if(thisSubject.getNotes()!=null) newNotesArrayList.addAll(thisSubject.getNotes());
                                                    newNotesArrayList.add(notes);
                                                    thisSubject.setNotes(newNotesArrayList);
                                                    subjectsRef.setValue(thisSubject);
                                                    Toast.makeText(UploadNotesActivity.this, "Notes Added Successfully", Toast.LENGTH_SHORT).show();
                                                    imageUri = null;
                                                    topic_name.setText("");
                                                    topic_desc.setText("");
                                                }
                                            }
                                            pd.dismiss();
                                        }
                                    });

                                }
                            });

                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            Toast.makeText(getApplicationContext(), exception.getMessage(), Toast.LENGTH_LONG).show();
                            pd.dismiss();
                        }
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