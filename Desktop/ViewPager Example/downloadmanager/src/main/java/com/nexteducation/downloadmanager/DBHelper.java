package com.nexteducation.downloadmanager;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.google.gson.Gson;

import java.util.ArrayList;

/**
 * Created by suresh on 2/11/2016.
 */
public class DBHelper extends SQLiteOpenHelper
{
    private final static String DATABASE_NAME = "download";
    private final static int DATABASE_VERSION = 1;
    private final static String TABLE_NAME = "downloadrequestqueue";
    private final static String KEY_ID = "_id";
    private final static String KEY_CHAPTER = "chapter";
    private final static String KEY_STATUS = "status";
    private final static String CREATE_DRQ = " CREATE TABLE " + TABLE_NAME + " ( " + KEY_ID + " TEXT PRIMARY KEY," + KEY_CHAPTER + " TEXT, " + KEY_STATUS + " TEXT " + ")";
    private static DBHelper _instance;

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }


    public static DBHelper getInstance(Context ctx) {
        /**
         * use the application context as suggested by CommonsWare.
         * this will ensure that you dont accidentally leak an Activitys
         * context (see this article for more information:
         * http://android-developers.blogspot.nl/2009/01/avoiding-memory-leaks.html)
         */
        if (_instance == null) {
            _instance = new DBHelper(ctx.getApplicationContext());
            _instance.getWritableDatabase();
        }
        return _instance;
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_DRQ);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    public void insertDataIntoDatabase(String id, String json, String statusOfDownload) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put(KEY_ID, id);
        values.put(KEY_CHAPTER, json);
        values.put(KEY_STATUS, statusOfDownload);

        long value = db.insert(TABLE_NAME, null, values);
        Log.v("DownloadPlugin", "===" + "insert" + value);
    }

    public String getDataFromDatabase() {
        ArrayList<DownloadRequest> downloadRequests = new ArrayList<DownloadRequest>();
        String chapterData = "";
        String requestqueueData = "";
        String selectQuery = "SELECT  * FROM " + TABLE_NAME;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (c.moveToFirst()) {
            do {

                chapterData = (c.getString(c.getColumnIndex(KEY_CHAPTER)));
                DownloadRequest downloadRequest = new Gson().fromJson(chapterData, DownloadRequest.class);
                downloadRequests.add(downloadRequest);
            } while (c.moveToNext());
        }
        c.close();
        requestqueueData = new Gson().toJson(downloadRequests);
        return requestqueueData;
    }

    public boolean isNodeIdExist(String nodeId) {
        String selectQuery = "select * from downloadrequestqueue where _id=" + nodeId;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);
        if (c != null && c.getCount() > 0) {
            return true;
        }
        return false;
    }

    public ArrayList<DownloadRequest> getDownloadedArraylist() {
        ArrayList<DownloadRequest> downloadRequests = new ArrayList<DownloadRequest>();
        String selectQuery = "SELECT  * FROM " + TABLE_NAME;
        String chapterData = "";
        String status = "";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (c.moveToFirst()) {
            do {

                chapterData = c.getString(c.getColumnIndex(KEY_CHAPTER));
                DownloadRequest downloadRequest = new Gson().fromJson(chapterData, DownloadRequest.class);
                status = c.getString(c.getColumnIndex(KEY_STATUS));
                downloadRequest.setNodeStatus(status);
                downloadRequests.add(downloadRequest);
            } while (c.moveToNext());
        }

        return downloadRequests;
    }

    public void updateDB(ArrayList<DownloadRequest> downloadRequestArrayList, String status) {
        SQLiteDatabase db = this.getReadableDatabase();
        for (DownloadRequest downloadRequest : downloadRequestArrayList) {
            String json = new Gson().toJson(downloadRequest);
            ContentValues values = new ContentValues();
            values.put(KEY_CHAPTER, json);
            values.put(KEY_STATUS, status);
            db.update(TABLE_NAME,
                    values,
                    KEY_ID + " = ? ",
                    new String[]{downloadRequest.getNodeId()});
        }

    }

    public void updateDownloadDRQ(String nodeId, String json, String statusOfDownload) {
        SQLiteDatabase db = this.getReadableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_CHAPTER, json);
        values.put(KEY_STATUS, statusOfDownload);
        int value = db.update(TABLE_NAME,
                values,
                KEY_ID + " = ? ",
                new String[]{nodeId});
        Log.v("DownloadPlugin", "===" + "update" + value);
    }

    public void deleteFromDatabase(String nodeId) {
        SQLiteDatabase db = this.getReadableDatabase();
        int value = db.delete(TABLE_NAME, KEY_ID + " = ? ",
                new String[]{nodeId});
        Log.v("DownloadPlugin", "===" + "delete" + value);
    }

    public void deleteAllDataFromDatabase() {
        SQLiteDatabase db = this.getReadableDatabase();
        db.delete(TABLE_NAME, null, null);
    }


    public DownloadRequest getDownloadRequestFromNodeId(String nodeId) {
        SQLiteDatabase db = this.getReadableDatabase();
        DownloadRequest downloadRequest = null;
        Cursor cursor = db.query(TABLE_NAME, new String[]{KEY_CHAPTER}, KEY_ID + "=?", new String[]{nodeId}, null, null, null, null);
        if (cursor != null && cursor.getCount() > 0 && cursor.moveToFirst()) {
            do {
                String chapterData = cursor.getString(cursor.getColumnIndex(KEY_CHAPTER));
                downloadRequest = new Gson().fromJson(chapterData, DownloadRequest.class);

            } while (cursor.moveToNext());

        }
        cursor.close();
        return downloadRequest;
    }
}
