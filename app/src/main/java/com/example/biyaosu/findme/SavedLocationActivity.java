package com.example.biyaosu.findme;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import android.support.v4.app.FragmentActivity;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.CursorAdapter;

public class SavedLocationActivity extends FragmentActivity implements LoaderManager.LoaderCallbacks<Cursor>, GetLocationRecordDialog.OnGetLocationRecordFragmentInteractionListener,
 SendSMSDialogFragment.OnSendSMSDialogFragmentInteractionListener {

    private FMDataSource fmds;
    private ListView listView;
    private TextView mainText;
    private SimpleCursorAdapter adapter;
    private CursorLoader cursorLoader;
    String classtag = SaveLocationDialog.class.getName();

    GetLocationRecordDialog getLocationDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_saved_location);
        mainText = (TextView)findViewById(R.id.mainText);
        listView = (ListView)findViewById(R.id.list);

        fmds = new FMDataSource(this);
        fmds.open();

        adapter = new SimpleCursorAdapter(getBaseContext(),
                R.layout.row_layout,
                null,
                new String[]{FMSQLiteHelper.COLUMN_ID, FMSQLiteHelper.COLUMN_NAME},
                new int[]{R.id.recordId, R.id.recordName});
        listView.setAdapter(adapter);
        getSupportLoaderManager().initLoader(0, null, this);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                TextView v_id = (TextView)view.findViewById(R.id.recordId);
                String recordId = v_id.getText().toString();
                TextView v_name = (TextView)view.findViewById(R.id.recordName);
                String locationName = v_name.getText().toString();
                if(fmds.getLocation(Integer.valueOf(recordId)) != null){
                    getLocationDialog = new GetLocationRecordDialog().newInstance(recordId, locationName, position);
                    getLocationDialog.show(getFragmentManager(), "getLocationDialog");
                }else{
                    Intent intent = getIntent();
                    finish();
                    startActivity(intent);
                }

            }
        });
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String[] projection = {FMSQLiteHelper.COLUMN_ID, FMSQLiteHelper.COLUMN_NAME};
        cursorLoader = new CursorLoader(this, DatabaseAccessUtility.CONTENT_URI, projection, null, null, null);
        return cursorLoader;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if(adapter!=null && data!=null){
            adapter.swapCursor(data);
        }else{
            Log.i(classtag, "onLoadFinished: adapter is null");
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        if(adapter!=null){
            adapter.swapCursor(null);
        }else{
            Log.i(classtag,"OnLoadFinished: adapter is null");
        }

    }

    @Override
    public void onGetLocationRecordFragmentInteraction(Uri uri) {

    }

    @Override
    public void onSendSMSDialogFragmentInteraction(Uri uri) {

    }
}
