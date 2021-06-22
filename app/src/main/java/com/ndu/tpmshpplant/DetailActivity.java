package com.ndu.tpmshpplant;

import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.Menu;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.DialogFragment;

import com.artifex.mupdf.viewer.DocumentActivity;

import java.io.File;

public class DetailActivity extends AppCompatActivity {

    private static final String FRAGMENT_INFO = "info";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        Toolbar toolbar = findViewById(R.id.toolbarDetail);
        toolbar.setTitle(getResources().getString(R.string.app_name));
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeButtonEnabled(true);
        }
//Disable fragment for a moment
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new PdfRendererBasicFragment())
                    .commitNow();
        }

        /*OnClick Handling*/
        toolbar.setNavigationOnClickListener(view -> finish());
//        if (getSupportActionBar() != null) {
//            getSupportActionBar().hide();
//        }

        startMuPDFActivityWithExampleFile();

    }

    //https://www.mupdf.com/docs/android-sdk.html
    public void startMuPDFActivity(Uri documentUri) {
        Intent intent = new Intent(this, DocumentActivity.class);
        intent.setAction(Intent.ACTION_VIEW);
        intent.setData(documentUri);
        startActivity(intent);
    }

    public void startMuPDFActivityWithExampleFile() {
        File dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
        File file = new File("/data/data/com.ndu.tpmshpplant/cache/Chapter 1,2,3 & 12 (Rev).pdf");
        Uri uri = Uri.fromFile(file);
        startMuPDFActivity(uri);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_info:
                new InfoDialogFragment().show(getSupportFragmentManager(), FRAGMENT_INFO);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public static class InfoDialogFragment extends DialogFragment {

        @NonNull
        @Override
        public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
            return new AlertDialog.Builder(requireContext())
                    .setMessage(R.string.intro_message)
                    .setPositiveButton(android.R.string.ok, null)
                    .show();
        }

    }
}