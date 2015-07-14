package com.example.biyaosu.findme;

import android.app.Activity;
import android.app.DialogFragment;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link com.example.biyaosu.findme.GetLocationRecordDialog.OnGetLocationRecordFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link GetLocationRecordDialog#newInstance} factory method to
 * create an instance of this fragment.
 */
public class GetLocationRecordDialog extends DialogFragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_RECORDID = "id";
    private static final String ARG_RECORDNAME = "name";

    // TODO: Rename and change types of parameters
    private String recordId;
    private String recordName;
    private EditText savedLocationNameText;
    private Button enableEditBtn;
    private Button cancelEditBtn;
    private Button saveEditBtn;
    private Button deleteRecordBtn;
    private Button sendLocationBtn;
    Context context;
    private FMDataSource fmds;
    private SavedLocation selectedLocation;

    String classtag = GetLocationRecordDialog.class.getName();

    private OnGetLocationRecordFragmentInteractionListener mListener;

    public static GetLocationRecordDialog newInstance(String param1, String param2) {
        GetLocationRecordDialog fragment = new GetLocationRecordDialog();
        Bundle args = new Bundle();
        args.putString(ARG_RECORDID, param1);
        args.putString(ARG_RECORDNAME, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public GetLocationRecordDialog() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.i(classtag, "onCreate");
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            recordId = getArguments().getString(ARG_RECORDID);
            recordName = getArguments().getString(ARG_RECORDNAME);
        }
        selectedLocation = fmds.getLocation(Integer.valueOf(recordId));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_get_location_record_dialog, container, false);
        savedLocationNameText = (EditText)v.findViewById(R.id.savedLocationName);
        enableEditBtn = (Button)v.findViewById(R.id.enableEditButton);
        saveEditBtn = (Button)v.findViewById(R.id.saveEditNameButton);
        cancelEditBtn = (Button)v.findViewById(R.id.cancelEditNameButton);
        deleteRecordBtn = (Button)v.findViewById(R.id.deleteRecordButton);
        sendLocationBtn = (Button)v.findViewById(R.id.sendSavedLocationButton);
        final View btnContainer = (View)v.findViewById(R.id.buttonContainer);
        savedLocationNameText.setText(recordName);

        //enable EditText
        enableEditBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(classtag, "enableEditBtn clicked");
                savedLocationNameText.setEnabled(true);
                btnContainer.setVisibility(View.VISIBLE);
            }
        });

        //cancel edit
        cancelEditBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(classtag, "cancelEditBtn clicked");
                savedLocationNameText.setEnabled(false);
                btnContainer.setVisibility(View.GONE);
            }
        });

        //save new name
        saveEditBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(classtag, "saveEditBtn clicked");
                String newName = savedLocationNameText.getText().toString();
                if(selectedLocation == null){
                    selectedLocation = fmds.getLocation(Integer.valueOf(recordId));
                }
                selectedLocation.setName(newName);
                fmds.updateLocation(selectedLocation);
            }
        });

        //delete record
        deleteRecordBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(classtag, "deleteRecordBtn clicked");
                fmds.deleteLocation(Integer.valueOf(recordId));
            }
        });

        //send this location
        sendLocationBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(classtag, "sendLocationBtn clicked");
                if(selectedLocation == null){
                    selectedLocation = fmds.getLocation(Integer.valueOf(recordId));
                }
                String lat = String.valueOf(selectedLocation.getLatitude());
                String lng = String.valueOf(selectedLocation.getLongitude());
                Intent i = new Intent(Intent.ACTION_SEND);
                i.setType("message/rfc822");
                i.putExtra(Intent.EXTRA_SUBJECT, "Find me here!");
                String body = "<a href='http://maps.google.com/maps?daddr="+lat+","+lng+"'>Take me there!</a>";
                i.putExtra(Intent.EXTRA_TEXT, Html.fromHtml(body));
                try {
                    startActivity(Intent.createChooser(i, "Send mail..."));
                } catch (android.content.ActivityNotFoundException ex) {
                    Log.i(classtag, "no email client installed");
                }
            }
        });

        return v;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onGetLocationRecordFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Activity activity) {
        Log.i(classtag, "onAttach");
        super.onAttach(activity);
        if(context == null){
            context = activity;
        }
        try {
            mListener = (OnGetLocationRecordFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
        fmds = new FMDataSource(context);
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
    public interface OnGetLocationRecordFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onGetLocationRecordFragmentInteraction(Uri uri);
    }

}
