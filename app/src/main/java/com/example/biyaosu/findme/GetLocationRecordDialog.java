package com.example.biyaosu.findme;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
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
import android.widget.Toast;
import java.util.Timer;
import java.util.TimerTask;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link com.example.biyaosu.findme.GetLocationRecordDialog.OnGetLocationRecordFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link GetLocationRecordDialog#newInstance} factory method to
 * create an instance of this fragment.
 */
public class GetLocationRecordDialog extends DialogFragment implements SendSMSDialogFragment.OnSendSMSDialogFragmentInteractionListener {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_RECORDID = "id";
    private static final String ARG_RECORDNAME = "name";
    private static final String ARG_ITEMID = "itemId";

    // TODO: Rename and change types of parameters
    private String recordId;
    private String recordName;
    private int itemId;
    private EditText savedLocationNameText;
    private Button enableEditBtn;
    private Button cancelEditBtn;
    private Button saveEditBtn;
    private Button deleteRecordBtn;
    private Button sendLocationBtn;
    private Button sendBySmsBtn;
    private Button dismissButton;
    Context context;
    private FMDataSource fmds;
    private SavedLocation selectedLocation;
    private SendSMSDialogFragment sendSMSDialog;

    String classtag = GetLocationRecordDialog.class.getName();

    private OnGetLocationRecordFragmentInteractionListener mListener;

    public static GetLocationRecordDialog newInstance(String param1, String param2, int param3) {
        GetLocationRecordDialog fragment = new GetLocationRecordDialog();
        Bundle args = new Bundle();
        args.putString(ARG_RECORDID, param1);
        args.putString(ARG_RECORDNAME, param2);
        args.putInt(ARG_ITEMID, param3);
        fragment.setArguments(args);
        return fragment;
    }

    public GetLocationRecordDialog() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            recordId = getArguments().getString(ARG_RECORDID);
            recordName = getArguments().getString(ARG_RECORDNAME);
            itemId = getArguments().getInt(ARG_ITEMID);
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
        sendBySmsBtn = (Button)v.findViewById(R.id.sendSavedLocationBySmsButton);
        dismissButton = (Button)v.findViewById(R.id.dismissSavedLocationButton);
        final View btnContainer = (View)v.findViewById(R.id.buttonContainer);
        savedLocationNameText.setText(recordName);

        //enable EditText
        enableEditBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                savedLocationNameText.setEnabled(true);
                btnContainer.setVisibility(View.VISIBLE);
            }
        });

        //cancel edit
        cancelEditBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                savedLocationNameText.setEnabled(false);
                btnContainer.setVisibility(View.GONE);
            }
        });

        //save new name
        saveEditBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String newName = savedLocationNameText.getText().toString();
                if(selectedLocation == null){
                    selectedLocation = fmds.getLocation(Integer.valueOf(recordId));
                }
                selectedLocation.setName(newName);
                int rows = fmds.updateLocation(selectedLocation);

                if(rows>0){
                    Toast.makeText(context, "New Name Saved", Toast.LENGTH_LONG).show();
                    new Timer().schedule(new TimerTask() {
                        @Override
                        public void run() {
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    savedLocationNameText.setEnabled(false);
                                    btnContainer.setVisibility(View.GONE);
                                }
                            });

                        }
                    }, 1500);
                }

            }
        });

        //delete record
        deleteRecordBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(context)
                        .setMessage("Delete this location?")
                        .setNegativeButton("No", null)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                int rows = fmds.deleteLocation(Integer.valueOf(recordId));
                                if (rows > 0) {
                                    getDialog().dismiss();
                                }
                            }
                        })
                        .show();

            }
        });

        //send this location
        sendLocationBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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

        //send by sms
        sendBySmsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(selectedLocation == null){
                    selectedLocation = fmds.getLocation(Integer.valueOf(recordId));
                }
                String lat = String.valueOf(selectedLocation.getLatitude());
                String lng = String.valueOf(selectedLocation.getLongitude());
                sendSMSDialog = new SendSMSDialogFragment().newInstance(lat, lng);
                sendSMSDialog.show(getFragmentManager(), "send sms");
            }
        });

        //dismiss dialog
        dismissButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getDialog().dismiss();
            }
        });

        getDialog().setTitle("Saved Location");
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

    @Override
    public void onSendSMSDialogFragmentInteraction(Uri uri) {

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
