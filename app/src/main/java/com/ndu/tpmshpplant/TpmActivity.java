package com.ndu.tpmshpplant;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.single.PermissionListener;
import com.ndu.tpmshpplant.sqlite.database.DatabaseHelper;
import com.ndu.tpmshpplant.sqlite.database.model.InfoTpm;
import com.ndu.tpmshpplant.sqlite.utils.MyDividerItemDecoration;
import com.ndu.tpmshpplant.sqlite.utils.RecyclerTouchListener;
import com.ndu.tpmshpplant.sqlite.view.InfoTpmAdapter;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import ir.androidexception.filepicker.dialog.SingleFilePickerDialog;

import static com.ndu.simpletoaster.SimpleToaster.toaster;
import static com.ndu.tpmshpplant.sqlite.database.model.InfoTpm.COLUMN_AUTHOR_NAME;
import static com.ndu.tpmshpplant.sqlite.database.model.InfoTpm.COLUMN_CONTENT_ID;
import static com.ndu.tpmshpplant.sqlite.database.model.InfoTpm.COLUMN_DESCRIPTION;
import static com.ndu.tpmshpplant.sqlite.database.model.InfoTpm.COLUMN_ICON_LINK;
import static com.ndu.tpmshpplant.sqlite.database.model.InfoTpm.COLUMN_PUBLISH_DATE;
import static com.ndu.tpmshpplant.sqlite.database.model.InfoTpm.COLUMN_READ_STATUS;
import static com.ndu.tpmshpplant.sqlite.database.model.InfoTpm.COLUMN_THUMBNAIL_LINK;
import static com.ndu.tpmshpplant.sqlite.database.model.InfoTpm.COLUMN_TITLE;

public class TpmActivity extends AppCompatActivity implements SearchView.OnQueryTextListener {
    private static final String TAG = "rfid";
    private static final String XML_PATH = "xml_path";
    // --Commented out by Inspection (14-Jan-21 15:25):private static final String DEMO_MODE = "1";
    private InfoTpmAdapter infoTpmAdapter;
    private final List<InfoTpm> infoTpmList = new ArrayList<>();

    private DatabaseHelper db;
    //RFID
    private SharedPreferences.Editor editor;
    private SharedPreferences preferences;
    //    private ProgressBar spinner;

    @SuppressLint({"UseCompatLoadingForDrawables", "SetTextI18n", "SimpleDateFormat"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tpm);

        //[START Initialize]

        Toolbar toolbar = findViewById(R.id.toolbar);
        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        db = new DatabaseHelper(this);

        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        editor = preferences.edit();

        toolbar.setTitle(getResources().getString(R.string.app_name));
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeButtonEnabled(true);
        }

        /*OnClick Handling*/
        toolbar.setNavigationOnClickListener(view -> finish());

        Date c = Calendar.getInstance().getTime();
        System.out.println("Current time => " + c);

        SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy HH:mm:ss", Locale.getDefault());
        String formattedDate = df.format(c);

        /*Storage permission*/
        runDexter();

        /*Create Asset table in Asset.db*/
        try {
            db.createTable();
            Log.d(TAG, "onCreateDatabase: ");
        } catch (Exception e) {
            e.printStackTrace();
        }


        //Todo: Hidden scanned asset
        infoTpmList.addAll(db.getAllInfoTpm());
        infoTpmAdapter = new InfoTpmAdapter(infoTpmList);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new MyDividerItemDecoration(this, LinearLayoutManager.VERTICAL, 16));
        recyclerView.setAdapter(infoTpmAdapter);
        /*
          On long press on RecyclerView item, open alert dialog
          with options to choose
          Edit and Delete
         */
        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(this,
                recyclerView, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, final int position) {
                clearSharedPref();
                final InfoTpm infoTpm = infoTpmList.get(position);
                String txtContentId = infoTpm.getTxtContentId();
                String txtIconLink = infoTpm.getTxtIconLink();
                String txtTitle = infoTpm.getTxtTitle();
                String txtDescription = infoTpm.getTxtThumbnail();
                String txtThumbnail = infoTpm.getTxtThumbnail();
                String txtAuthor = infoTpm.getTxtAuthor();
                String dtmPublishDate = infoTpm.getDtmPublishDate();
                int intReadStatus = infoTpm.getIntReadStatus();

                editor.putString(COLUMN_CONTENT_ID, txtContentId);
                editor.putString(COLUMN_ICON_LINK, txtIconLink);
                editor.putString(COLUMN_TITLE, txtTitle);
                editor.putString(COLUMN_DESCRIPTION, txtDescription);
                editor.putString(COLUMN_THUMBNAIL_LINK, txtThumbnail);
                editor.putString(COLUMN_AUTHOR_NAME, txtAuthor);
                editor.putString(COLUMN_PUBLISH_DATE, dtmPublishDate);
                editor.putInt(COLUMN_READ_STATUS, intReadStatus);
                editor.apply();
            }

            @Override
            public void onLongClick(View view, int position) {
//                showActionsDialog(position);
            }
        }));
    }

    private void clearSharedPref() {
        preferences.edit().remove(COLUMN_CONTENT_ID).apply();
        preferences.edit().remove(COLUMN_ICON_LINK).apply();
        preferences.edit().remove(COLUMN_TITLE).apply();
        preferences.edit().remove(COLUMN_DESCRIPTION).apply();
        preferences.edit().remove(COLUMN_THUMBNAIL_LINK).apply();
        preferences.edit().remove(COLUMN_AUTHOR_NAME).apply();
        preferences.edit().remove(COLUMN_PUBLISH_DATE).apply();
        preferences.edit().remove(COLUMN_READ_STATUS).apply();
    }

//    private void goToDetail() {
//        Intent intent;
//        intent = new
//                Intent(TpmActivity.this, AssetDetailActivity.class);
//        startActivity(intent);
//    }

    private void runDexter() {
        Dexter.withContext(this)
                .withPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                .withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse response) {
                        toaster(TpmActivity.this, getResources().getString(R.string.msg_storage_granted), 0);
                    }

                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse response) {
                        toaster(TpmActivity.this, getResources().getString(R.string.msg_storage_denied), 0);
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(com.karumi.dexter.listener.PermissionRequest permissionRequest, PermissionToken permissionToken) {

                    }
                }).check();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.tpm_activity_menu, menu);

        final MenuItem searchItem = menu.findItem(R.id.action_search);
        final SearchView searchView = (SearchView) searchItem.getActionView();
        searchView.setOnQueryTextListener(this);
        return true;
    }

    @Override
    public boolean onQueryTextChange(String query) {
        // Here is where we are going to implement the filter logic
        infoTpmAdapter.filter(query, db);
        return false;
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        infoTpmAdapter.filter(query, db);
        return false;
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch (id) {
            case R.id.action_settings:
                goToSetting();
                return true;

            case R.id.action_refresh_data_lokal:
                openFilePicker();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void openFilePicker() {
//        Intent intentfile = new Intent(Intent.ACTION_OPEN_DOCUMENT);
//        intentfile.addCategory(Intent.CATEGORY_OPENABLE);
//        intentfile.setType("text/xml");
//        //https://stackoverflow.com/questions/35915602/selecting-a-specific-type-of-file-in-android
//        startActivityForResult(intentfile, PICKFILE_RESULT_CODE);

        if (permissionGranted()) {
            SingleFilePickerDialog singleFilePickerDialog = new SingleFilePickerDialog(this,
                    () -> toaster(TpmActivity.this, getResources().getString(R.string.msg_canceled), 0),
                    files -> {
                        toaster(TpmActivity.this, files[0].getPath(), 0);
                        Log.d(TAG, "openFilePicker: " + files[0].getPath());
                        editor.putString(XML_PATH, files[0].getPath());
                        editor.apply();
                        pullDataAsyncTask task = new pullDataAsyncTask();
                        deleteInfoTpmDatabase();
                        task.execute();
                    });
            singleFilePickerDialog.show();
        } else {
            requestPermission();
        }
    }

    private void addAllInfoTpm() {
        infoTpmList.clear();
        infoTpmList.addAll(db.getAllInfoTpm());
        infoTpmAdapter.notifyDataSetChanged();
    }

    private boolean permissionGranted() {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermission() {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
    }

    private void showAllInfoIndDB() {
        infoTpmList.clear();
        infoTpmList.addAll(db.getAllInfoTpm());
        infoTpmAdapter.notifyDataSetChanged();
    }

    private void goToSetting() {
        Intent settingsIntent = new
                Intent(TpmActivity.this, SettingsActivity.class);
        startActivity(settingsIntent);
    }

    private void deleteInfoTpmDatabase() {
        Log.d(TAG, "deleteInfoTpmDatabase: true");
        db.dropTable();
        infoTpmList.clear();
        infoTpmAdapter.notifyDataSetChanged();
    }

    /**
     * Inserting new asset in db
     * and refreshing the list
     */
    private void createAsset(String asset) {
        // inserting asset in db and getting
        // newly inserted asset id
        long id = db.insertInfoTpm(asset);

        // get the newly inserted asset from db
        InfoTpm infoTpm = db.getInfoTpm(id);

        if (infoTpm != null) {
            // adding new asset to array list at 0 position
            infoTpmList.add(0, infoTpm);

            // refreshing the list
            infoTpmAdapter.notifyDataSetChanged();
        }
    }

    /**
     * Updating asset in db and updating
     * item in the list by its position
     */
    private void updateReadStatus(int readStatus, int position) {
        InfoTpm infoTpm = infoTpmList.get(position);
        // updating asset text
        infoTpm.setIntReadStatus(readStatus);

        // updating asset in db
        db.updateInfoTpm(infoTpm);

        // refreshing the list
        infoTpmList.set(position, infoTpm);
        infoTpmAdapter.notifyItemChanged(position);
    }

    /**
     * Deleting aaset from SQLite and removing the
     * item from the list by its position
     */
    private void deleteItem(int position) {
        // deleting the asset from db
        db.deleteInfoTpm(infoTpmList.get(position));

        // removing the asset from the list
        infoTpmList.remove(position);
        infoTpmAdapter.notifyItemRemoved(position);
    }

    /*https://stackoverflow.com/a/8018905/7772358*/
    public void displayExceptionMessage(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    @SuppressWarnings("LoopConditionNotUpdatedInsideLoop")
    protected String getNodeValue(String tag, Element element) {
        NodeList nodeList = element.getElementsByTagName(tag);
        Node node = nodeList.item(0);
        if (node != null) {
            if (node.hasChildNodes()) {
                Node child = node.getFirstChild();
                while (child != null) {
                    if (child.getNodeType() == Node.TEXT_NODE) {
                        return child.getNodeValue();
                    }
                }
            }
        }
        return "";
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }

    /**
     * sub-class of AsyncTask
     */
    @SuppressLint("StaticFieldLeak")
    protected class pullDataAsyncTask extends AsyncTask<Context, Integer, String> {
        /*https://stackoverflow.com/questions/6450275/android-how-to-work-with-asynctasks-progressdialog*/
        private final ProgressDialog dialog = new ProgressDialog(TpmActivity.this);
        private int totalInfo = 0;

        // -- run intensive processes here
        // -- notice that the datatype of the first param in the class definition matches the param passed to this
        // method
        // -- and that the datatype of the last param in the class definition matches the return type of this method
        @Override
        protected String doInBackground(Context... params) {
            // -- on every iteration
            // -- runs a while loop that causes the thread to sleep for 50 milliseconds
            // -- publishes the progress - calls the onProgressUpdate handler defined below
            // -- and increments the counter variable i by one

            /*https://www.tutlane.com/tutorial/android/android-xml-parsing-using-sax-parser*/
            /*https://stackoverflow.com/questions/15967896/how-to-parse-xml-file-from-sdcard-in-android*/
            try {
                @SuppressWarnings("MismatchedQueryAndUpdateOfCollection") ArrayList<HashMap<String, String>> infoList = new ArrayList<>();
                /*Input from android asset folder*/
                //InputStream istream = getAssets().open("TpmInfo.xml");

                /*Input from mnt/sdcard*/
                String pathFile = preferences.getString(XML_PATH, "/storage/emulated/0/Tpm/TpmInfo.xml");
                //File file = new File("mnt/sdcard/Asset/AssetUpdate.xml");
                File file = new File(Objects.requireNonNull(pathFile));
                InputStream inputStreamSd = new FileInputStream(file.getPath());
                DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
                DocumentBuilder docBuilder = builderFactory.newDocumentBuilder();
                Document doc = docBuilder.parse(inputStreamSd);
                NodeList nList = doc.getElementsByTagName("info");
                HashMap<String, String> hashMapInfo;
                totalInfo = nList.getLength();

                for (int i = 0; i < nList.getLength(); i++) {
                    if (nList.item(0).getNodeType() == Node.ELEMENT_NODE) {
                        hashMapInfo = new HashMap<>();
                        Element elm = (Element) nList.item(i);
                        hashMapInfo.put(COLUMN_CONTENT_ID, getNodeValue(COLUMN_CONTENT_ID, elm));
                        hashMapInfo.put(COLUMN_ICON_LINK, getNodeValue(COLUMN_ICON_LINK, elm));
                        hashMapInfo.put(COLUMN_TITLE, getNodeValue(COLUMN_TITLE, elm));
                        hashMapInfo.put(COLUMN_DESCRIPTION, getNodeValue(COLUMN_DESCRIPTION, elm));
                        hashMapInfo.put(COLUMN_THUMBNAIL_LINK, getNodeValue(COLUMN_THUMBNAIL_LINK, elm));
                        hashMapInfo.put(COLUMN_AUTHOR_NAME, getNodeValue(COLUMN_AUTHOR_NAME, elm));
                        hashMapInfo.put(COLUMN_PUBLISH_DATE, getNodeValue(COLUMN_PUBLISH_DATE, elm));
                        hashMapInfo.put(COLUMN_READ_STATUS, getNodeValue(COLUMN_READ_STATUS, elm));
                        infoList.add(hashMapInfo);
                        //scan get position
                        if (db.checkIfInfoTpmIDinDB(hashMapInfo.put(COLUMN_CONTENT_ID, getNodeValue(COLUMN_CONTENT_ID, elm)))) {
                            Log.d(TAG, "onReceive: Exist");
                            Log.d(TAG, "loadAssetList: ");
                        } else {
                            // Inserting record
                            Log.d(TAG, "onReceive: Data No Exist" + i);
                            db.inputDataFromDom(Objects.requireNonNull(hashMapInfo));
                            publishProgress(i);
                        }
                    }
                }
            } catch (IOException | ParserConfigurationException | SAXException e) {
                e.printStackTrace();
                return "No File";
                //displayExceptionMessage(e.getMessage());
            }
            return "COMPLETE!";
        }

        // -- gets called just before thread begins
        @Override
        protected void onPreExecute() {
            Log.i(TAG, "onPreExecute()");
            super.onPreExecute();
            this.dialog.setMessage(getResources().getString(R.string.msg_importing_data_tpm));
            this.dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            this.dialog.setCancelable(false);
            this.dialog.setCanceledOnTouchOutside(false);
            this.dialog.show();
        }

        // -- called from the publish progress
        // -- notice that the datatype of the second param gets passed to this method
        @SuppressLint("SetTextI18n")
        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            try {
                double valuesDb = Double.parseDouble(String.valueOf((values[0])));
                double totalInfoDB = Double.parseDouble(String.valueOf(totalInfo));
                double percenTage = (valuesDb / totalInfoDB) * 100;
                Log.d(TAG, "onCreate: " + percenTage);
                BigDecimal bd = new BigDecimal(percenTage).setScale(2, RoundingMode.HALF_EVEN);
                bd.doubleValue();
//                this.dialog.setMessage("Importing data asset " + (values[0]) + "/" + totalAsset + " (" + bd + "%)");
                this.dialog.setMessage(getResources().getString(R.string.msg_importing_data_tpm) + ", " + getResources().getString(R.string.msg_please_wait));
                this.dialog.setMax(totalInfo);
                this.dialog.setProgress((values[0]));

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        // -- called if the cancel button is pressed
        @Override
        protected void onCancelled() {
            super.onCancelled();
            Log.i(TAG, "onCancelled()");
            this.dialog.setMessage(getResources().getString(R.string.msg_canceled));
        }

        // -- called as soon as doInBackground method completes
        // -- notice that the third param gets passed to this method
        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            Log.i(TAG, "onPostExecute(): " + result);
            if (this.dialog.isShowing()) {
                this.dialog.dismiss();
            }
            if (result.equals("COMPLETE!")) {
                this.dialog.setMessage(result);
                toaster(TpmActivity.this, getResources().getString(R.string.msg_import_complete), 0);
            } else if (result.equals("No File")) {
                toaster(TpmActivity.this, getResources().getString(R.string.msg_no_xml_in_directory), 0);
            } else {
                toaster(TpmActivity.this, getResources().getString(R.string.msg_import_failed), 0);
            }
            addAllInfoTpm();
        }
    }
}