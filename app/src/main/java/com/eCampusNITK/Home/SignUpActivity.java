package com.eCampusNITK.Home;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.eCampusNITK.Models.User;
import com.eCampusNITK.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SignUpActivity extends AppCompatActivity implements View.OnClickListener {
    private EditText username,email,phone,password,confirmPassword,course;
    private Button signUpBtn;
    private TextView openLoginScreen;
    private FirebaseAuth mAuth;
    private CheckBox accept_terms_and_conditions;
    private Spinner spinnerCourses;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            setStatusBarTextAndBgColor();
        }
        setContentView(R.layout.activity_sign_up);
        mAuth = FirebaseAuth.getInstance();
        findIds();
        setListeners();
        setCourseAdapter();
    }

    //Set listeners on all views
    void setListeners()
    {
        openLoginScreen.setOnClickListener(this);
        signUpBtn.setOnClickListener(this);
    }

    void setCourseAdapter()
    {
        ArrayAdapter<CharSequence>adapter=ArrayAdapter.createFromResource(this, R.array.Courses, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_item);
        spinnerCourses.setAdapter(adapter);
        adapter.setDropDownViewResource(R.layout.courses_dropdown);
        spinnerCourses.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Object item = parent.getItemAtPosition(position);
                course.setText(item.toString());
            }
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    //Check if any input field is empty or not
    public boolean empty(){
        return (username.getText().toString().isEmpty() || email.getText().toString().isEmpty()
        || phone.getText().toString().isEmpty() || password.getText().toString().isEmpty()
        || confirmPassword.getText().toString().isEmpty() || course.getText().toString().isEmpty());
    }

    //match the field password and confirm password
    public boolean matchPassword(){
        return password.getText().toString().equals(confirmPassword.getText().toString());
    }

    //Find all ids from resource layout files
    public void findIds(){
        username                    = findViewById(R.id.sign_up_user_name);
        email                       = findViewById(R.id.sign_up_user_email);
        phone                       = findViewById(R.id.sign_up_user_phone);
        password                    = findViewById(R.id.sign_up_user_password);
        confirmPassword             = findViewById(R.id.sign_up_confirm_password);
        signUpBtn                   = findViewById(R.id.sign_up_btn);
        course                      = findViewById(R.id.sign_up_course);
        openLoginScreen             = findViewById(R.id.openLogin);
        accept_terms_and_conditions = findViewById(R.id.accept_all_terms_checkbox);
        spinnerCourses              = findViewById(R.id.spinner_courses);
    }

    //Perform User Sign Up
    public void userSignUp(){
        ProgressDialog pd = new ProgressDialog(this);
        pd.setMessage("loading");
        pd.setCanceledOnTouchOutside(false);
        pd.show();

        mAuth.createUserWithEmailAndPassword(email.getText().toString(), password.getText().toString())
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // Sign in success, update UI with the signed-in user's information
                        FirebaseUser user = mAuth.getCurrentUser();
                        assert user != null;
                        user.sendEmailVerification().addOnCompleteListener(task1 -> {
                            if (task1.isSuccessful()) {
                                uploadUserDetails(user.getUid());
                                pd.dismiss();
                            }else {
                                pd.dismiss();
                                Toast.makeText(SignUpActivity.this, "User Registered successfully, But Email Could not be verified"+ task1.getException(),
                                        Toast.LENGTH_LONG).show();
                            }

                        });
                    } else {
                        pd.dismiss();
                        // If sign in fails, display a message to the user.
                        Toast.makeText(SignUpActivity.this, "Authentication failed. "+task.getException(),
                                Toast.LENGTH_LONG).show();
                    }
                });
    }

    //Upload User Details to fireBase realtime Database
    public void uploadUserDetails(String UID)
    {
        User user = new User();
        final ProgressDialog pd=new ProgressDialog(SignUpActivity.this);
        pd.setMessage("Please wait..");
        pd.setCanceledOnTouchOutside(false);
        pd.show();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().
                getReference("User");
        user.setName(username.getText().toString());
        user.setEmail(email.getText().toString());
        user.setPhoneNumber(phone.getText().toString());
        user.setUserID(UID);
        user.setProfilePicture("");
        user.setCourse(course.getText().toString());
        databaseReference.child(UID).setValue(user).addOnCompleteListener(task -> {
            pd.dismiss();
            Toast.makeText(SignUpActivity.this, "Sign Up Complete", Toast.LENGTH_SHORT).show();
            Intent i = new Intent(getApplicationContext(),LoginActivity.class);
            startActivity(i);
            finish();
        }).addOnCanceledListener(() -> {
            Toast.makeText(this, "Unable to create User in Database ", Toast.LENGTH_SHORT).show();
            pd.dismiss();
        });
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.openLogin:
                //Launch the login Activity
                Intent i = new Intent(getApplicationContext(),LoginActivity.class);
                startActivity(i);
                finish();
                overridePendingTransition(R.anim.animate_left_to_right,R.anim.animate_right_to_left);
                break;
            case R.id.sign_up_btn:
                //Perform User Sign Up
                if(!empty())
                {
                    if(matchPassword())
                    {
                        if(accept_terms_and_conditions.isChecked())
                        {
                            userSignUp();
                        }else {
                            Toast.makeText(this, "Please accept our Terms and Conditions", Toast.LENGTH_SHORT).show();
                        }
                    }
                    else
                        Toast.makeText(SignUpActivity.this, "Password Do not match", Toast.LENGTH_SHORT).show();
                }else {
                    Toast.makeText(SignUpActivity.this, "Please fill all the fields ", Toast.LENGTH_SHORT).show();
                }
                break;
            default:
                break;
        }

    }


    @RequiresApi(api = Build.VERSION_CODES.M)
    void setStatusBarTextAndBgColor(){
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.setStatusBarColor(this.getResources().getColor(R.color.black));
        View decorView = window.getDecorView();
        decorView.setSystemUiVisibility(decorView.getSystemUiVisibility() & ~View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR); //set status text  light
    }
}