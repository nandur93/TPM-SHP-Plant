package com.ndu.tpmshpplant;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;

import com.github.javiersantos.appupdater.AppUpdater;
import com.github.javiersantos.appupdater.enums.Display;
import com.github.javiersantos.appupdater.enums.UpdateFrom;
import com.ndu.showinstalledappdetail.SystemPackageDetail;

import static com.ndu.sendfeedback.SendFeedback.sendFeedback;
import static com.ndu.tpmshpplant.MainActivity.versCode;
import static com.ndu.tpmshpplant.MainActivity.versName;

public class SettingsActivity extends AppCompatActivity {

    private static final String TAG = SettingsActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.action_settings);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeButtonEnabled(true);
        }
        toolbar.setNavigationOnClickListener(view -> finish());
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.settings, new SettingsFragment())
                .commit();
    }

    public static class SettingsFragment extends PreferenceFragmentCompat implements SharedPreferences.OnSharedPreferenceChangeListener {
        public static final String KEY_EXPORT_FILE_DIRECTORY = "file_directory";
        public static final String SOURCE_FILE = "source_file";
        public SharedPreferences sharedPrefs;
        public SharedPreferences.Editor editor;

        @SuppressLint("CommitPrefEdits")
        @SuppressWarnings("ConstantConditions")
        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            setPreferencesFromResource(R.xml.root_preferences, rootKey);

            //Preference prefAbout = findPreference("about");
            //https://stackoverflow.com/a/61885766/7772358
            Preference prefVersion = findPreference("current_version");
            Preference prefCheckUpdate = findPreference("check_update");
            Preference prefSendFeedback = findPreference("feedback");
            ListPreference sourceFile = findPreference(SOURCE_FILE);

            //update value
            sharedPrefs = PreferenceManager.getDefaultSharedPreferences(getContext());
            editor = sharedPrefs.edit();

            if (sourceFile != null) {
                /*https://stackoverflow.com/questions/37707061/getting-the-value-of-an-item-in-the-array*/
                String[] sourceArray = getResources().getStringArray(R.array.sourceVersion);
                String src1 = sourceArray[0];
                String src2 = sourceArray[1];
                sourceFile.setValue(sharedPrefs.getString(SOURCE_FILE, "1"));
                sourceFile.setOnPreferenceChangeListener((preference, newValue) -> {
                    editor.putString(SOURCE_FILE, newValue.toString());
                    editor.apply();
                    return true;
                });
                String summaryTitle;
                if (sharedPrefs.getString(SOURCE_FILE, "1").equals("1")) {
                    summaryTitle = src1;
                } else {
                    summaryTitle = src2;
                }
                sourceFile.setSummary(summaryTitle);
                Log.d(TAG, "onCreatePreferences: summaryTitle " + summaryTitle);
            }

            //TODO: delai dalam refresh karena value hanya merubah trigger saja belum merubah path

            //getVersionName
            if (prefVersion != null) {
                String version = getResources().getString(R.string.version_title);
                String build = getResources().getString(R.string.build_title);
                prefVersion.setSummary(version + " " + versName + " " + build + " " + versCode);
                prefVersion.setOnPreferenceClickListener(preference -> {
                    SystemPackageDetail.showInstalledAppDetails(getContext(), requireActivity().getPackageName());
                    return true;
                });
            }

            if (prefCheckUpdate != null) {
                // String update_xml = getResources().getString(R.string.update_xml_resource);
                prefCheckUpdate.setOnPreferenceClickListener(preference -> {
                    new AppUpdater(requireContext())
                            .showEvery(250)
                            //.setUpdateFrom(UpdateFrom.GITHUB)
                            //.setGitHubUserAndRepo("javiersantos", "AppUpdater")
                            .setUpdateFrom(UpdateFrom.GOOGLE_PLAY)
                            //.setUpdateXML(update_xml)
                            .setDisplay(Display.DIALOG)
                            .setButtonDoNotShowAgain(null)
                            .showAppUpdated(true)
                            .start();
                    return true;
                });
            }

            if (prefSendFeedback != null) {
                prefSendFeedback.setOnPreferenceClickListener(preference -> {
                    sendFeedback(requireContext(),
                            requireContext().getResources().getString(R.string.pattern_mailbody),
                            requireContext().getString(R.string.feedback_body),
                            requireContext().getString(R.string.app_name),
                            requireContext().getString(R.string.choose_email_client),
                            requireContext().getResources().getString(R.string.nav_header_subtitle),
                            versName, versCode);
                    return true;
                });
            }

        }

        @Override
        public void onResume() {
            super.onResume();
            getPreferenceScreen().getSharedPreferences()
                    .registerOnSharedPreferenceChangeListener(this);
        }

        @Override
        public void onPause() {
            super.onPause();
            getPreferenceScreen().getSharedPreferences()
                    .unregisterOnSharedPreferenceChangeListener(this);
        }

        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {
            if (s.equals(KEY_EXPORT_FILE_DIRECTORY)) {
                Log.d(TAG, "onSharedPreferenceChanged: " + sharedPreferences.toString());
                //https://stackoverflow.com/questions/8003098/how-do-you-refresh-preferenceactivity-to-show-changes-in-the-settings
            }
            requireActivity().finish();
            requireActivity().startActivity(requireActivity().getIntent());
            Log.d(TAG, "onCreatePreferences: " + sharedPreferences.getString(s, "Directory Default"));
        }
    }
}