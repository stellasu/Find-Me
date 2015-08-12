package com.example.biyaosu.findme;

/**
 * Created by biyaosu on 5/19/15.
 */

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.Cursor;
import android.util.Log;

public class FMSQLiteHelper extends SQLiteOpenHelper{

    String classtag = FMSQLiteHelper.class.getName();
    private static final int DB_VERSION = 1;
    private static final String DB_NAME = "FMDB";
    protected static final String TABLE_NAME = "SavedLocations";
    protected static final String COLUMN_ID = "_id";
    protected static final String COLUMN_NAME = "name";
    protected static final String COLUMN_LATITUDE = "latitude";
    protected static final String COLUMN_LONGITUDE = "longitude";
    protected static final String COLUMN_UPDATED = "updated";
    protected static final String COLUMN_TOP = "top";
    protected static final String[] COLUMNS = {COLUMN_ID, COLUMN_NAME, COLUMN_LATITUDE, COLUMN_LONGITUDE, COLUMN_UPDATED, COLUMN_TOP};

    private static final String CREATE_TABLE = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + " ( " +
            COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            COLUMN_NAME + " TEXT, " +
            COLUMN_LATITUDE + " DOUBLE, " +
            COLUMN_LONGITUDE + " DOUBLE, " +
            COLUMN_UPDATED + " LONG, " +
            COLUMN_TOP + " INTEGER )";
    private static final String DELETE_TABLE = "DROP TABLE IF EXISTS " + TABLE_NAME;

    public FMSQLiteHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(DELETE_TABLE);
        this.onCreate(db);
    }

}
