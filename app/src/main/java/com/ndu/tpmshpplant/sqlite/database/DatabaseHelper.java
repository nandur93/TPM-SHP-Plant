package com.ndu.tpmshpplant.sqlite.database;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import com.ndu.tpmshpplant.sqlite.database.model.InfoTpm;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import static com.ndu.tpmshpplant.sqlite.database.model.InfoTpm.COLUMN_AUTHOR_NAME;
import static com.ndu.tpmshpplant.sqlite.database.model.InfoTpm.COLUMN_CONTENT_ID;
import static com.ndu.tpmshpplant.sqlite.database.model.InfoTpm.COLUMN_DESCRIPTION;
import static com.ndu.tpmshpplant.sqlite.database.model.InfoTpm.COLUMN_ICON_LINK;
import static com.ndu.tpmshpplant.sqlite.database.model.InfoTpm.COLUMN_PUBLISH_DATE;
import static com.ndu.tpmshpplant.sqlite.database.model.InfoTpm.COLUMN_READ_STATUS;
import static com.ndu.tpmshpplant.sqlite.database.model.InfoTpm.COLUMN_ARTICLE_LINK;
import static com.ndu.tpmshpplant.sqlite.database.model.InfoTpm.COLUMN_TITLE;
import static com.ndu.tpmshpplant.sqlite.database.model.InfoTpm.CREATE_TABLE;
import static com.ndu.tpmshpplant.sqlite.database.model.InfoTpm.TABLE_NAME;


public class DatabaseHelper extends SQLiteOpenHelper {

    // Database Version
    private static final int DATABASE_VERSION = 1;

    // Database Name
    public static final String DATABASE_NAME = "tpm_info_db";

    public DatabaseHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Creating Tables
    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        // create assets table
        sqLiteDatabase.execSQL(CREATE_TABLE);
    }

    // Upgrading db
    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {

        // Drop older table if existed
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);

        // Create tables again
        onCreate(sqLiteDatabase);
    }

// --Commented out by Inspection START (14-Jan-21 15:26):
//    public long createAsset(String asset) {
//        // get writable database as we want to write data
//        SQLiteDatabase db = this.getWritableDatabase();
//
//        ContentValues values = new ContentValues();
//        // `id` and `timestamp` will be inserted automatically.
//        // no need to add them
//        values.put(COLUMN_ASSET_RFID, asset);
//
//        // insert row
//        long id = db.insert(TABLE_NAME, null, values);
//
//        // close db connection
//        db.close();
//
//        // return newly inserted row id
//        return id;
//    }
// --Commented out by Inspection STOP (14-Jan-21 15:26)

    public void dropTable() {
        // get readable database as we are not inserting anything
        SQLiteDatabase db = this.getReadableDatabase();
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    public InfoTpm getInfoTpm(long code) {
        // get readable database as we are not inserting anything
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_NAME,
                new String[]{/*COLUMN_ID, */COLUMN_ICON_LINK, COLUMN_ICON_LINK, COLUMN_TITLE,
                        COLUMN_DESCRIPTION, COLUMN_ARTICLE_LINK, COLUMN_AUTHOR_NAME, COLUMN_READ_STATUS, COLUMN_PUBLISH_DATE},
                /*COLUMN_ID + "=?",*/
                COLUMN_CONTENT_ID,
                new String[]{String.valueOf(code)}, null, null, null, null);

        if (cursor != null)
            cursor.moveToFirst();

        // prepare asset object
        InfoTpm infoTpm = new InfoTpm(
//                Objects.requireNonNull(cursor).getInt(cursor.getColumnIndex(COLUMN_ID)),
                Objects.requireNonNull(cursor).getString(cursor.getColumnIndex(COLUMN_CONTENT_ID)),
                cursor.getString(cursor.getColumnIndex(COLUMN_ICON_LINK)),
                cursor.getString(cursor.getColumnIndex(COLUMN_TITLE)),
                cursor.getString(cursor.getColumnIndex(COLUMN_DESCRIPTION)),
                cursor.getString(cursor.getColumnIndex(COLUMN_ARTICLE_LINK)),
                cursor.getString(cursor.getColumnIndex(COLUMN_AUTHOR_NAME)),
                cursor.getInt(cursor.getColumnIndex(COLUMN_READ_STATUS)),
                cursor.getString(cursor.getColumnIndex(COLUMN_PUBLISH_DATE)));

        // close the db connection
        cursor.close();

        return infoTpm;
    }

    public List<InfoTpm> getAllInfoTpm() {
        List<InfoTpm> infoTpms = new ArrayList<>();

        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_NAME + " ORDER BY " +
//                Asset.COLUMN_TIMESTAMP + " DESC";
                /*COLUMN_ID*/COLUMN_AUTHOR_NAME + " ASC";

        SQLiteDatabase db = this.getWritableDatabase();
        @SuppressLint("Recycle") Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                InfoTpm infoTpm = new InfoTpm();
                infoTpm.setTxtContentId(cursor.getString(cursor.getColumnIndex(COLUMN_CONTENT_ID)));
                infoTpm.setTxtIconLink(cursor.getString(cursor.getColumnIndex(COLUMN_ICON_LINK)));
                infoTpm.setTxtTitle(cursor.getString(cursor.getColumnIndex(COLUMN_TITLE)));
                infoTpm.setTxtDescription(cursor.getString(cursor.getColumnIndex(COLUMN_DESCRIPTION)));
                infoTpm.setTxtArticleLink(cursor.getString(cursor.getColumnIndex(COLUMN_ARTICLE_LINK)));
                infoTpm.setTxtAuthor(cursor.getString(cursor.getColumnIndex(COLUMN_AUTHOR_NAME)));
                infoTpm.setIntReadStatus(cursor.getInt(cursor.getColumnIndex(COLUMN_READ_STATUS)));
                infoTpm.setDtmPublishDate(cursor.getString(cursor.getColumnIndex(COLUMN_PUBLISH_DATE)));

                infoTpms.add(infoTpm);
            } while (cursor.moveToNext());
        }

        // close db connection
        db.close();

        // return assets list
        return infoTpms;
    }

    public List<InfoTpm> getAllInfoTpmByAuthorName(String authorName) {
        List<InfoTpm> infoTpms = new ArrayList<>();

        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_NAME + " WHERE " + COLUMN_AUTHOR_NAME + " LIKE '" + authorName + "' " + " ORDER BY " +
//                Asset.COLUMN_TIMESTAMP + " DESC";
                /*COLUMN_ID*/COLUMN_CONTENT_ID + " ASC";

        SQLiteDatabase db = this.getWritableDatabase();
        @SuppressLint("Recycle") Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                InfoTpm infoTpm = new InfoTpm();
                infoTpm.setTxtContentId(cursor.getString(cursor.getColumnIndex(COLUMN_CONTENT_ID)));
                infoTpm.setTxtIconLink(cursor.getString(cursor.getColumnIndex(COLUMN_ICON_LINK)));
                infoTpm.setTxtTitle(cursor.getString(cursor.getColumnIndex(COLUMN_TITLE)));
                infoTpm.setTxtDescription(cursor.getString(cursor.getColumnIndex(COLUMN_DESCRIPTION)));
                infoTpm.setTxtArticleLink(cursor.getString(cursor.getColumnIndex(COLUMN_ARTICLE_LINK)));
                infoTpm.setTxtAuthor(cursor.getString(cursor.getColumnIndex(COLUMN_AUTHOR_NAME)));
                infoTpm.setIntReadStatus(cursor.getInt(cursor.getColumnIndex(COLUMN_READ_STATUS)));
                infoTpm.setDtmPublishDate(cursor.getString(cursor.getColumnIndex(COLUMN_PUBLISH_DATE)));

                infoTpms.add(infoTpm);
            } while (cursor.moveToNext());
        }

        // close db connection
        db.close();

        // return assets list
        return infoTpms;
    }

    public int getInfoTpmCount() {
        String countQuery = "SELECT  * FROM " + TABLE_NAME;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);

        int count = cursor.getCount();
        cursor.close();

        // return count
        return count;
    }

    public int getReadCount(String readStatus) {
        String countQuery = "SELECT  * FROM " + TABLE_NAME + " WHERE " + COLUMN_READ_STATUS + " LIKE '" + readStatus + "'";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);

        int count = cursor.getCount();
        cursor.close();

        // return count
        return count;
    }

// --Commented out by Inspection START (14-Jan-21 15:26):
//    public int getAssetsCountByStatusNull(String assetLocation) {
//        String countQuery = "SELECT  * FROM " + TABLE_NAME + " WHERE " + COLUMN_ASSET_LOCATION + " LIKE '" + assetLocation + "'" + " & " + COLUMN_ASSET_STATUS + " IS NULL ";
//        SQLiteDatabase db = this.getReadableDatabase();
//        Cursor cursor = db.rawQuery(countQuery, null);
//
//        int count = cursor.getCount();
//        cursor.close();
//
//        // return count
//        return count;
//    }
// --Commented out by Inspection STOP (14-Jan-21 15:26)

// --Commented out by Inspection START (14-Jan-21 15:26):
//    public int getScannedAssetsCount() {
//        String countQuery = "SELECT  COUNT (*) FROM " + TABLE_NAME +
//                " WHERE " + COLUMN_ASSET_STATUS + " = " + "'" + scanResult + "'";
//        SQLiteDatabase db = this.getReadableDatabase();
//        Cursor cursor = db.rawQuery(countQuery, null);
//
//        int count = cursor.getCount();
//        cursor.close();
//
//        // return count
//        return count;
//    }
// --Commented out by Inspection STOP (14-Jan-21 15:26)

//    SELECT COUNT(*) FROM Asset WHERE asset_rfid IS NOT NULL and asset_status = 'Asset Ada';

    public void updateInfoTpm(InfoTpm infoTpm) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMN_READ_STATUS, infoTpm.getIntReadStatus());

        // updating row
        db.update(TABLE_NAME, values, /*COLUMN_ID*/COLUMN_CONTENT_ID + " = ?",
                /*new String[]{String.valueOf(asset.getId())});*/
                new String[]{infoTpm.getTxtContentId()});
    }

    public void deleteInfoTpm(InfoTpm infoTpm) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_NAME, /*COLUMN_ID*/COLUMN_CONTENT_ID + " = ?",
//                new String[]{String.valueOf(asset.getId())});
                new String[]{infoTpm.getTxtContentId()});
        db.close();
    }

    public long insertInfoTpm(String infoTpm) {
        // get writable database as we want to write data
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        // `id` and `timestamp` will be inserted automatically.
        // no need to add them
        values.put(COLUMN_READ_STATUS, infoTpm);

        // insert row
        long id = db.insert(TABLE_NAME, null, values);

        // close db connection
        db.close();

        // return newly inserted row id
        return id;
    }

    /*https://stackoverflow.com/questions/20415309/android-sqlite-how-to-check-if-a-record-exists*/
    /*https://stackoverflow.com/questions/20838233/sqliteexception-unrecognized-token-when-reading-from-database*/
    public boolean checkIfInfoTpmIDinDB(String infoTpmId) {
        SQLiteDatabase db = this.getReadableDatabase();
        String Query = "Select * from " + TABLE_NAME + " where " + COLUMN_CONTENT_ID + " = " + "'" + infoTpmId + "'";
        Cursor cursor = db.rawQuery(Query, null);
        if (cursor.getCount() <= 0) {
            cursor.close();
            return false;
        }
        cursor.close();
        return true;
    }

// --Commented out by Inspection START (14-Jan-21 15:26):
//    public boolean checkIsStatusUpdated(String status) {
//        SQLiteDatabase db = this.getReadableDatabase();
//        String Query = "Select * from " + TABLE_NAME + " where " + COLUMN_ASSET_STATUS + " = " + "'" + status + "'";
//        Cursor cursor = db.rawQuery(Query, null);
//        if (cursor.getCount() <= 0) {
//            cursor.close();
//            return true;
//        }
//        cursor.close();
//        return false;
//    }
// --Commented out by Inspection STOP (14-Jan-21 15:26)

    public void inputDataFromDom(HashMap<String, String> Vi) {
        SQLiteDatabase database = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(COLUMN_CONTENT_ID, Vi.get(COLUMN_CONTENT_ID));
        values.put(COLUMN_ICON_LINK, Vi.get(COLUMN_ICON_LINK));
        values.put(COLUMN_TITLE, Vi.get(COLUMN_TITLE));
        values.put(COLUMN_DESCRIPTION, Vi.get(COLUMN_DESCRIPTION));
        values.put(COLUMN_ARTICLE_LINK, Vi.get(COLUMN_ARTICLE_LINK));
        values.put(COLUMN_AUTHOR_NAME, Vi.get(COLUMN_AUTHOR_NAME));
        values.put(COLUMN_READ_STATUS, Vi.get(COLUMN_READ_STATUS));
        values.put(COLUMN_PUBLISH_DATE, Vi.get(COLUMN_PUBLISH_DATE));
//        COLUMN_CONTENT_ID
//        COLUMN_ICON_LINK
//        COLUMN_TITLE
//        COLUMN_DESCRIPTION
//        COLUMN_ARTICLE_LINK
//        COLUMN_AUTHOR_NAME
//        COLUMN_READ_STATUS
//        COLUMN_PUBLISH_DATE

        //etc
        database.insert(TABLE_NAME, null, values);
        database.close();
    }

    public void createTable() {
        // get readable database as we are not inserting anything
        SQLiteDatabase db = this.getReadableDatabase();
        onCreate(db);
    }
}
