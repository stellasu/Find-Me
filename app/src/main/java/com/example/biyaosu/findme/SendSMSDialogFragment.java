package com.example.biyaosu.findme;

import android.app.Activity;
import android.app.DialogFragment;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.telephony.SmsManager;
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
 * {@link com.example.biyaosu.findme.SendSMSDialogFragment.OnSendSMSDialogFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link SendSMSDialogFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SendSMSDialogFragment extends DialogFragment {

    private static final String ARG_LAT = "lat";
    private static final String ARG_LNG = "lng";

    private String lat;
    private String lng;
    private String phoneNumber;
    private String content;
    private EditText phoneNumberText;
    private EditText contentText;
    private Button sendSMSBtn;

    Context context;
    String classtag = SendSMSDialogFragment.class.getName();

    private OnSendSMSDialogFragmentInteractionListener mListener;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SendSMSDialogFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SendSMSDialogFragment newInstance(String param1, String param2) {
        SendSMSDialogFragment fragment = new SendSMSDialogFragment();
        Bundle args = new Bundle();
        args.putString(ARG_LAT, param1);
        args.putString(ARG_LNG, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public SendSMSDialogFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            lat = getArguments().getString(ARG_LAT);
            lng = getArguments().getString(ARG_LNG);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_send_smsdialog, container, false);
        phoneNumberText = (EditText)v.findViewById(R.id.phoneNumber);
        contentText = (EditText)v.findViewById(R.id.smsContent);
        sendSMSBtn = (Button)v.findViewById(R.id.sendSmsButton);

        content = "Take me there! "+"http://maps.google.com/maps?daddr="+lat+","+lng;
        contentText.setText(content);

        sendSMSBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                phoneNumber = String.valueOf(phoneNumberText.getText());
                SmsManager smsManager = SmsManager.getDefault();
                String textToSend = String.valueOf(contentText.getText());
                boolean flag = true;
                try{
                    smsManager.sendTextMessage(phoneNumber, null, textToSend, null, null);
                }catch(Exception e){
                    flag = false;
                    e.printStackTrace();
                }
                if(flag){
                    Toast.makeText(context, "Message Sent", Toast.LENGTH_LONG).show();
                    new Timer().schedule(new TimerTask() {
                        @Override
                        public void run() {
                            getDialog().dismiss();
                        }
                    }, 1500);
                }else{
                    Toast.makeText(context, "Message not send", Toast.LENGTH_LONG).show();
                }

            }
        });

        getDialog().setTitle("Send by SMS");
        return v;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onSendSMSDialogFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if(context == null){
            context = activity;
        }
        try {
            mListener = (OnSendSMSDialogFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
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
    public interface OnSendSMSDialogFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onSendSMSDialogFragmentInteraction(Uri uri);
    }

}
