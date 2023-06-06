package com.eCampusNITK.Home;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.eCampusNITK.Core.RealTimeDatabaseManager;
import com.eCampusNITK.Models.Posts;
import com.eCampusNITK.Models.User;
import com.eCampusNITK.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;

import java.util.ArrayList;
import java.util.UUID;

public class AddPostActivity extends AppCompatActivity {

    ImageView post_imageView;
    Button    selectImage;
    Button    uploadPost;
    Uri       imageUri = null;
    TextInputEditText caption;
    User     user;
    StorageReference storageReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStatusBarTextAndBgColor();
        setContentView(R.layout.activity_add_post);
        post_imageView = findViewById(R.id.user_post_image);
        selectImage    = findViewById(R.id.select_post_imaeg_btn);
        uploadPost     = findViewById(R.id.upload_post_btn);
        caption        = findViewById(R.id.editext_caption);
        user           = RealTimeDatabaseManager.getInstance().getUser();
        storageReference = FirebaseStorage.getInstance().getReference();

        selectImage.setOnClickListener(v -> onChooseFile());

        uploadPost.setOnClickListener(v -> {
            if(caption.getText()!=null && !caption.getText().toString().trim().isEmpty() && imageUri!=null)
            {
                uploadPostInDataBase();
            }
        });
    }

    public void onChooseFile()
    {
        CropImage.activity().start(AddPostActivity.this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE)
        {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if(resultCode == RESULT_OK)
            {
                if(result!=null) imageUri = result.getUri();
                post_imageView.setImageURI(imageUri);
            }else if(resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception e = result.getError();
                Toast.makeText(this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void uploadPostInDataBase()
    {
        if(imageUri != null)
        {
            final ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setTitle("Uploading...");
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.show();
            StorageReference ref       = storageReference.child(user.getUserID()+"/"+"post/"+ UUID.randomUUID());
            DatabaseReference db       = FirebaseDatabase.getInstance().getReference().child("AllPosts");

            ref.putFile(imageUri)
                    .addOnSuccessListener(taskSnapshot -> ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            String key = db.push().getKey();

                            Posts posts = new Posts();
                            String currTime  = String.valueOf(System.currentTimeMillis());
                            posts.setExpanded(false);
                            posts.setComments(new ArrayList<>());
                            posts.setPost_date(currTime);
                            posts.setPostID(key);
                            posts.setLikes(new ArrayList<>());
                            posts.setProfile_pic(user.getProfilePicture());
                            posts.setPostUrl(uri.toString());
                            posts.setName(user.getName());
                            posts.setUserPostID(user.getUserID());
                            if(caption.getText()!=null) posts.setCaption(caption.getText().toString());
                            if(key!=null) {
                                db.child(key).setValue(posts);
                                progressDialog.dismiss();
                                Toast.makeText(AddPostActivity.this, "Post Uploaded", Toast.LENGTH_SHORT).show();
                                finish();
                            }
                        }
                    }))
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            Toast.makeText(AddPostActivity.this, "Failed "+e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(@NonNull UploadTask.TaskSnapshot taskSnapshot) {
                            double progress = (100.0*taskSnapshot.getBytesTransferred()/taskSnapshot
                                    .getTotalByteCount());
                            progressDialog.setMessage("Uploaded "+(int)progress+"%");
                        }
                    });
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