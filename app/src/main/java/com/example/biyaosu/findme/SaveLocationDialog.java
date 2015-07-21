package com.example.biyaosu.findme;

import android.app.Activity;
import android.app.DialogFragment;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.util.Log;
import android.content.Context;
import android.widget.Toast;

import java.util.Timer;
import java.util.TimerTask;

public class SaveLocationDialog extends DialogFragment {

    private static final String ARG_LAT = "lat";
    private static final String ARG_LNG = "lng";

    private String lat;
    private String lng;
    private String name;
    private long datetime;
    private Button saveBtn;
    private Button cancelBtn;
    private EditText editText;

    String classtag = SaveLocationDialog.class.getName();
    Context context;

    private FMDataSource fmds;
    private OnSaveLocationFragmentInteractionListener mListener;

    public static SaveLocationDialog newInstance(String lat, String lng){
        SaveLocationDialog fragment = new SaveLocationDialog();
        Bundle args = new Bundle();
        args.putString(ARG_LAT, lat);
        args.putString(ARG_LNG, lng);
        fragment.setArguments(args);
        return fragment;
    }

    public SaveLocationDialog(){

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(getArguments() != null){
            lat = getArguments().getString(ARG_LAT);
            lng = getArguments().getString(ARG_LNG);
            Log.i(classtag, "parameters: "+lat+". "+lng);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState){
        View v = inflater.inflate(R.layout.activity_save_location_dialog, container, false);
        saveBtn = (Button)v.findViewById(R.id.saveButton);
        cancelBtn = (Button)v.findViewById(R.id.cancelSaveLocationButton);
        editText = (EditText)v.findViewById(R.id.locationName);

        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(classtag, "saveBtn clicked");
                name = editText.getText().toString();
                datetime = System.currentTimeMillis();
                //dismiss keyboard after inputting
                InputMethodManager imm = (InputMethodManager)context.getSystemService(context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromInputMethod(editText.getWindowToken(), 0);

                SavedLocation location = new SavedLocation();
                location.setTop(0);
                location.setUpdated(System.currentTimeMillis());
                location.setLongitude(Double.valueOf(lng));
                location.setLatitude(Double.valueOf(lat));
                location.setName(name);
                location.setUpdated(datetime);
                long id = fmds.saveLocation(location);
                Log.i(classtag, "insertId: "+id);

                if(id>0){
                    Toast.makeText(context, "Location Saved", Toast.LENGTH_LONG).show();
                }
                new Timer().schedule(new TimerTask() {
                    @Override
                    public void run() {
                        getDialog().dismiss();
                    }
                }, 1500);
            }
        });

        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getDialog().dismiss();
            }
        });
        getDialog().setTitle("Save This Location");
        return v;
    }

    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onSaveLocationFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if(context == null){
            context = activity;
        }
        try {
            mListener = (OnSaveLocationFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnSaveLocationFragmentInteractionListener");
        }
        fmds = new FMDataSource(activity);
        fmds.open();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnSaveLocationFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onSaveLocationFragmentInteraction(Uri uri);
    }

}
