package com.ndu.tpmshpplant.sqlite;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import androidx.preference.PreferenceManager;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;

public class XmlDownloader {

    private static SharedPreferences preferences;
    private static SharedPreferences.Editor editor;

    public static void downloadFile(String url, File outputFile, Context context, String xmlPath, String defaultPath) {
        Thread thread = new Thread(() -> {
            try {
                //Your code goes here
                try {
                    URL u = new URL(url);
                    URLConnection conn = u.openConnection();
                    int contentLength = conn.getContentLength();

                    DataInputStream stream = new DataInputStream(u.openStream());

                    byte[] buffer = new byte[contentLength];
                    stream.readFully(buffer);
                    stream.close();

                    DataOutputStream fos = new DataOutputStream(new FileOutputStream(outputFile));
                    fos.write(buffer);
                    fos.flush();
                    fos.close();
                    preferences = PreferenceManager.getDefaultSharedPreferences(context);
                    editor = preferences.edit();
                    editor.putString(xmlPath, defaultPath);
                    editor.apply();
                    Log.d("TAG", "downloadFile: " + url + " " + preferences.getString(xmlPath, "null"));

                } catch (IOException e) {
                    // swallow a 404
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        thread.start();
    }
}
