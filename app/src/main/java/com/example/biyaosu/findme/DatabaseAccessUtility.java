package com.example.biyaosu.findme;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;

/**
 * Created by biyaosu on 6/25/15.
 */
public class DatabaseAccessUtility extends ContentProvider {

    public static String authority="com.example.biyaosu.findme.DatabaseAccessUtility";
    public static Uri CONTENT_URI = Uri.parse("content://" + authority  + "/FMDB");
    private FMDataSource fmds;

    @Override
    public boolean onCreate() {
        fmds = new FMDataSource(getContext());
        fmds.open();
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        Cursor cursor = fmds.getRecordsForList();
        return cursor;
    }

    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        return null;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        return 0;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        return 0;
    }
}
