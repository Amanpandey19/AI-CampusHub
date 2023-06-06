package com.eCampusNITK.Home;

import static android.os.Environment.DIRECTORY_DOWNLOADS;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DownloadManager;
import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ProgressBar;

import com.eCampusNITK.R;
import com.github.barteksc.pdfviewer.PDFView;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

public class PdfViewActivity extends AppCompatActivity {

    PDFView pdfView;
    String url;
    ProgressBar progressBar;
    ExtendedFloatingActionButton downloadPdf;
    DownloadManager manager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStatusBarTextAndBgColor();
        setContentView(R.layout.activity_pdf_view);
        pdfView = findViewById(R.id.pdfView);
        progressBar = findViewById(R.id.progress_bar_pdfView);
        downloadPdf = findViewById(R.id.download_pdf_floating_btn);
        progressBar.setIndeterminate(true);
        url = getIntent().getExtras().getString("url");
        new RetrivePDFStream().execute();


        downloadPdf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                downloadFile(PdfViewActivity.this,""+System.currentTimeMillis(),
                        ".pdf",DIRECTORY_DOWNLOADS,url);
            }
        });
    }

    class RetrivePDFStream extends AsyncTask<Void, Void, Void> {

        protected Void doInBackground(Void... voids) {
            try {
                InputStream input = new URL(url).openStream();
                pdfView.fromStream(input).load();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressBar.setVisibility(View.VISIBLE);
            downloadPdf.setVisibility(View.GONE);
        }

        @Override
        protected void onPostExecute(Void unused) {
            super.onPostExecute(unused);
            progressBar.setVisibility(View.GONE);
            downloadPdf.setVisibility(View.VISIBLE);
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

    public void downloadFile(Context context, String fileName, String fileExtension, String destinationDirectory, String url) {

        DownloadManager downloadmanager = (DownloadManager) context.
                getSystemService(Context.DOWNLOAD_SERVICE);
        Uri uri = Uri.parse(url);
        DownloadManager.Request request = new DownloadManager.Request(uri);

        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        request.setDestinationInExternalFilesDir(context, destinationDirectory, fileName + fileExtension);

        downloadmanager.enqueue(request);
    }
}