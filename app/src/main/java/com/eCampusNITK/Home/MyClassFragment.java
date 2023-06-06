package com.eCampusNITK.Home;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.eCampusNITK.Core.RealTimeDatabaseManager;
import com.eCampusNITK.Models.Class_Subject;
import com.eCampusNITK.Models.MySubject;
import com.eCampusNITK.Models.TimeTable;
import com.eCampusNITK.Models.User;
import com.eCampusNITK.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Random;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


public class MyClassFragment extends Fragment {

    ArrayList<MySubject> mySubjectArrayList     = new ArrayList<>();
    ArrayList<Class_Subject> selectedArrayList  = new ArrayList<>();
    RecyclerView mySubjectsRecyclerView;
    AllSubjectsAdapter allSubjectsAdapter;
    CardView askDoubt;
    User user;
    Button addSubjects;
    CardView about_SubjectCard;
    TextView responseTv, uploadTimeTable,did_you_know_tv;
    LinearLayout timeTableView;
    String       timeTableUrl="";
    TextView  timeTimeUrlTv,timeTablePostingTime,authorOfTimeTable,noTimeTableTv;
    public static final MediaType JSON
            = MediaType.get("application/json; charset=utf-8");
    OkHttpClient client = new OkHttpClient();



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v =  inflater.inflate(R.layout.fragment_my_class, container, false);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            setStatusBarTextAndBgColor();
        }
        return v;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        findIdAndSetUser(view);
        setRecyclerView();
        setTimeTableLayout();


        addSubjects.setOnClickListener(v -> {
            Intent i = new Intent(getContext(),AddSubjectActivity.class);
            startActivity(i);
            requireActivity().overridePendingTransition(R.anim.animate_left_to_right,R.anim.animate_right_to_left);
        });

        askDoubt.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), ChatBotActivity.class);
            startActivity(intent);
        });

        uploadTimeTable.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), UploadTimeTableActivity.class);
            startActivity(intent);
        });

        timeTimeUrlTv.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), PdfViewActivity.class);
            Bundle bundle = new Bundle();
            bundle.putString("url", timeTableUrl);
            intent.putExtras(bundle);
            startActivity(intent);
        });

    }

    private void setTimeTableLayout() {
        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference();
        dbRef.child("Courses").child(user.getCourse()).addValueEventListener(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists())
                {
                    noTimeTableTv.setVisibility(View.GONE);
                    timeTableView.setVisibility(View.VISIBLE);
                    TimeTable timeTable = snapshot.getValue(TimeTable.class);
                    if(timeTable!=null) {
                        authorOfTimeTable.setText("Uploaded by - "+timeTable.getAuthor());
                        timeTableUrl = timeTable.getTimeTableUrl();
                        timeTablePostingTime.setText(convertSimpleDayFormat(Long.parseLong(timeTable.getPostingTime())));
                    }
                }else {
                    noTimeTableTv.setVisibility(View.VISIBLE);
                    timeTableView.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void findIdAndSetUser(View view) {
        mySubjectsRecyclerView = view.findViewById(R.id.class_recycler_view);
        addSubjects            = view.findViewById(R.id.add_subjects_btn);
        askDoubt               = view.findViewById(R.id.ask_doubt_layout);
        about_SubjectCard      = view.findViewById(R.id.about_a_subject);
        responseTv             = view.findViewById(R.id.response_tv);
        did_you_know_tv        = view.findViewById(R.id.did_you_know_tv);
        uploadTimeTable        = view.findViewById(R.id.upload_timeTable);

        timeTableView               = view.findViewById(R.id.time_table_view);
        timeTimeUrlTv               = view.findViewById(R.id.timeTable_url);
        timeTablePostingTime        = view.findViewById(R.id.time_table_uploadtime);
        authorOfTimeTable           = view.findViewById(R.id.time_table_author);
        noTimeTableTv               = view.findViewById(R.id.no_time_table_available_text);

        user                   = RealTimeDatabaseManager.getInstance().getUser();
    }

    void setRecyclerView()
    {
        mySubjectArrayList.clear();
        ProgressDialog pd = new ProgressDialog(requireActivity());
        pd.setMessage("loading");
        pd.show();

        FirebaseDatabase database             =   FirebaseDatabase.getInstance();
        DatabaseReference databaseReference   =   database.getReference("StudyMaterial").child("SelectedSubjects").child(user.getUserID());
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                    if(snapshot.exists())
                    {
                        selectedArrayList.clear();
                        for (DataSnapshot postSnapshot: snapshot.getChildren()) {
                            if(postSnapshot.exists()) {
                                Class_Subject class_subject = postSnapshot.getValue(Class_Subject.class);
                                selectedArrayList.add(class_subject);
                            }
                        }

                        DatabaseReference mDatabase;
                        mDatabase = FirebaseDatabase.getInstance().getReference();
                        mDatabase.child("StudyMaterial").child("AllSubjects").addValueEventListener(new ValueEventListener() {
                            @SuppressLint("SetTextI18n")
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if(snapshot.exists())
                                {
                                    mySubjectArrayList.clear();
                                    for (DataSnapshot postSnapshot: snapshot.getChildren()) {
                                        if(postSnapshot.exists()){
                                            MySubject mySubject = postSnapshot.getValue(MySubject.class);
                                            if (mySubject != null && containsThisSubject(selectedArrayList, mySubject.getSubjectName()))
                                                mySubjectArrayList.add(mySubject);
                                        }
                                    }
                                    if(mySubjectArrayList!=null && mySubjectArrayList.size()!=0)
                                    {
                                        Random ran = new Random();
                                        int x = ran.nextInt(mySubjectArrayList.size());
                                        String subname = mySubjectArrayList.get(x).getSubjectName();
                                        did_you_know_tv.setText("Here are a few resources to Study "+subname);
                                        callAPI("Provide few youtube channel links to study "+subname+ "?");

                                    }else {
                                        about_SubjectCard.setVisibility(View.GONE);
                                    }
                                    allSubjectsAdapter = new AllSubjectsAdapter(getContext() , getActivity(), mySubjectArrayList);
                                    mySubjectsRecyclerView.setLayoutManager(new GridLayoutManager(getContext(),2));
                                    mySubjectsRecyclerView.setAdapter(allSubjectsAdapter);
                                    pd.dismiss();
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                Toast.makeText(requireContext(), ""+error.getMessage(), Toast.LENGTH_SHORT).show();
                                pd.dismiss();
                            }
                        });
                    }else {
                        Toast.makeText(requireContext(), "Add Subjects to your class", Toast.LENGTH_SHORT).show();
                        pd.dismiss();
                    }
                }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(requireContext(), "Cancelled", Toast.LENGTH_SHORT).show();
                pd.dismiss();
            }
        });

    }

    public boolean containsThisSubject(final List<Class_Subject> list, final String name){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            return list.stream().anyMatch(o -> o.getSubject_name().equals(name));
        }
        return false;
    }

    void addResponse(String response)
    {
        Animation animation= AnimationUtils.loadAnimation(getContext(),R.anim.animate_slide_left_enter);
        about_SubjectCard.setAnimation(animation);
        about_SubjectCard.setVisibility(View.VISIBLE);
        responseTv.setText(response);
    }

    void callAPI(String question) {
        //okhttp

        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("model", "text-davinci-003");
            jsonBody.put("prompt", question);
            jsonBody.put("max_tokens", 4000);
            jsonBody.put("temperature", 0);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        RequestBody body = RequestBody.create(jsonBody.toString(), JSON);
        Request request = new Request.Builder()
                .url("https://api.openai.com/v1/completions")
                .header("Authorization", "Bearer ")
                .post(body)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                about_SubjectCard.setVisibility(View.GONE);
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (response.isSuccessful()) {
                    JSONObject jsonObject;
                    try {
                        jsonObject = new JSONObject(Objects.requireNonNull(response.body()).string());
                        JSONArray jsonArray = jsonObject.getJSONArray("choices");
                        String result = jsonArray.getJSONObject(0).getString("text");

                        if(getActivity()!=null) getActivity().runOnUiThread(() -> addResponse(result.trim()));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
                else {
                    about_SubjectCard.setVisibility(View.GONE);
                }
            }
        });

    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    void setStatusBarTextAndBgColor(){
        if(getActivity()!=null)
        {
            Window window = getActivity().getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(getActivity().getResources().getColor(R.color.lightGrey));
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);//  set status text dark
        }

    }


    private static void clearTimes(Calendar c) {
        c.set(Calendar.HOUR_OF_DAY,0);
        c.set(Calendar.MINUTE,0);
        c.set(Calendar.SECOND,0);
        c.set(Calendar.MILLISECOND,0);
    }

    public static String convertSimpleDayFormat(long val) {
        Calendar today=Calendar.getInstance();
        clearTimes(today);

        Calendar yesterday=Calendar.getInstance();
        yesterday.add(Calendar.DAY_OF_YEAR,-1);
        clearTimes(yesterday);

        Calendar last7days=Calendar.getInstance();
        last7days.add(Calendar.DAY_OF_YEAR,-7);
        clearTimes(last7days);

        Calendar last30days=Calendar.getInstance();
        last30days.add(Calendar.DAY_OF_YEAR,-30);
        clearTimes(last30days);


        if(val >today.getTimeInMillis())
        {
            @SuppressLint("SimpleDateFormat") SimpleDateFormat formatter = new SimpleDateFormat("hh:mm aa");
            return formatter.format(new Date(val));
        }
        else if(val>yesterday.getTimeInMillis())
            return "yesterday";
        else if(val>last7days.getTimeInMillis())
            return "last 7 days";
        else if(val>last30days.getTimeInMillis())
            return "last 30 days";
        else
            return "more than 30days";
    }
}