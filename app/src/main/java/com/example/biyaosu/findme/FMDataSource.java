package com.example.biyaosu.findme;

/**
 * Created by biyaosu on 5/19/15.
 */

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;

public class FMDataSource {

    private SQLiteDatabase db;
    private FMSQLiteHelper dbHelper;
    String classtag = FMDataSource.class.getName();

    public FMDataSource (Context context){
        dbHelper = new FMSQLiteHelper(context);
    }

    public void open() throws SQLException {
        db = dbHelper.getWritableDatabase();
    }

    public void close(){
        dbHelper.close();
    }

    //sorted by updated time
    public ArrayList<SavedLocation> listAllLocations(){
        Log.i(classtag, "listAllLocations");
        ArrayList<SavedLocation> locations = new ArrayList<SavedLocation>();

        Cursor cursor = this.getAllRecords();
        SavedLocation location = null;
        if(cursor.moveToFirst()){
            do{
                location = new SavedLocation();
                location.setId(cursor.getInt(0)); //id
                location.setName(cursor.getString(1)); //name
                location.setLatitude(cursor.getDouble(2)); //latitude
                location.setLongitude(cursor.getDouble(3)); //longitude
                location.setUpdated(cursor.getLong(4)); //updated
                location.setTop(cursor.getInt(5)); //top
                locations.add(location);
                Log.i(classtag, "id: "+cursor.getInt(0));
                Log.i(classtag, "name: "+cursor.getString(1));
            }while(cursor.moveToNext());
        }
        return locations;
    }

    public Cursor getAllRecords(){
        String query = "SELECT * FROM " + FMSQLiteHelper.TABLE_NAME;
        Cursor cursor = db.rawQuery(query, null);
        return cursor;
    }

    public Cursor getRecordsForList(){
        String query = "SELECT * FROM " + FMSQLiteHelper.TABLE_NAME;
        Log.i(classtag, "query: "+query);
        Cursor cursor = db.rawQuery(query, null);
        return cursor;
    }

    public long saveLocation(SavedLocation location){
        Log.i(classtag, "saveLocation");
        ContentValues values = new ContentValues();
        values.put(FMSQLiteHelper.COLUMN_NAME, location.getName());
        values.put(FMSQLiteHelper.COLUMN_LATITUDE, location.getLatitude());
        values.put(FMSQLiteHelper.COLUMN_LONGITUDE, location.getLongitude());
        values.put(FMSQLiteHelper.COLUMN_UPDATED, location.getUpdated());
        values.put(FMSQLiteHelper.COLUMN_TOP, location.getTop());

        long insertId = db.insert(FMSQLiteHelper.TABLE_NAME, null, values);
        return insertId;
    }

    //by id
    public SavedLocation getLocation(int id){
        Log.i(classtag, "getLocation");
        Cursor cursor = db.query(
                FMSQLiteHelper.TABLE_NAME, // a. table
                FMSQLiteHelper.COLUMNS, // b. column names
                FMSQLiteHelper.COLUMN_ID + " =?", // c. selections
                new String[]{String.valueOf(id)}, // d. selections args
                null, // e. group by
                null, // f. having
                null, // g. order by
                null // h. limit
        );
        SavedLocation location = null;
        if(cursor.moveToFirst()){
            do{
                location = new SavedLocation();
                location.setId(cursor.getInt(0)); //id
                location.setName(cursor.getString(1)); //name
                location.setLatitude(cursor.getDouble(2)); //latitude
                location.setLongitude(cursor.getDouble(3)); //longitude
                location.setUpdated(cursor.getLong(4)); //updated
                location.setTop(cursor.getInt(5)); //top
            }while(cursor.moveToNext());
        }
        return location;
    }

    public int updateLocation(SavedLocation location){
        Log.i(classtag, "updateLocation");
        ContentValues values = new ContentValues();
        values.put(FMSQLiteHelper.COLUMN_NAME, location.getName());
        values.put(FMSQLiteHelper.COLUMN_LATITUDE, location.getLatitude());
        values.put(FMSQLiteHelper.COLUMN_LONGITUDE, location.getLongitude());
        values.put(FMSQLiteHelper.COLUMN_UPDATED, location.getUpdated());
        values.put(FMSQLiteHelper.COLUMN_TOP, location.getTop());

        //return the number of rows affected
        return db.update(FMSQLiteHelper.TABLE_NAME, values, FMSQLiteHelper.COLUMN_ID + " = ?", new String[]{String.valueOf(location.getId())});
    }

    public int deleteLocation(int id){
        Log.i(classtag, "deleteLocation");
        //return row affected or 0
        return db.delete(FMSQLiteHelper.TABLE_NAME, FMSQLiteHelper.COLUMN_ID + " = ?", new String[]{String.valueOf(id)});
    }

    public int pushToTop(SavedLocation location){
        Log.i(classtag, "pushToTop");
        if(location.getTop() == 1){
            return 1;
        }else{
            ContentValues values = new ContentValues();
            values.put(FMSQLiteHelper.COLUMN_NAME, location.getName());
            values.put(FMSQLiteHelper.COLUMN_LATITUDE, location.getLatitude());
            values.put(FMSQLiteHelper.COLUMN_LONGITUDE, location.getLongitude());
            values.put(FMSQLiteHelper.COLUMN_UPDATED, location.getUpdated());
            values.put(FMSQLiteHelper.COLUMN_TOP, 1);

            //return the number of rows affected
            return db.update(FMSQLiteHelper.TABLE_NAME, values, FMSQLiteHelper.COLUMN_ID + " = ?", new String[]{String.valueOf(location.getId())});
        }
    }

    public int removeFromTop(SavedLocation location){
        Log.i(classtag, "removeFromTop");
        if(location.getTop() == 0){
            return 1;
        }else{
            ContentValues values = new ContentValues();
            values.put(FMSQLiteHelper.COLUMN_NAME, location.getName());
            values.put(FMSQLiteHelper.COLUMN_LATITUDE, location.getLatitude());
            values.put(FMSQLiteHelper.COLUMN_LONGITUDE, location.getLongitude());
            values.put(FMSQLiteHelper.COLUMN_UPDATED, location.getUpdated());
            values.put(FMSQLiteHelper.COLUMN_TOP, 0);

            //return the number of rows affected
            return db.update(FMSQLiteHelper.TABLE_NAME, values, FMSQLiteHelper.COLUMN_ID + " = ?", new String[]{String.valueOf(location.getId())});
        }
    }
}
