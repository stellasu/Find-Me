package com.example.biyaosu.findme;

import android.app.Activity;
import android.app.DialogFragment;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link HelpDialog.OnHelpDialogFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link HelpDialog#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HelpDialog extends DialogFragment {

    private OnHelpDialogFragmentInteractionListener mListener;
    private Button closeHelpBtn;

    public static HelpDialog newInstance() {
        HelpDialog fragment = new HelpDialog();
        return fragment;
    }

    public HelpDialog() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_help_dialog, container, false);
        closeHelpBtn = (Button)v.findViewById(R.id.closeHelpButton);
        closeHelpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getDialog().dismiss();
            }
        });
        getDialog().setTitle("Help");

        return v;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnHelpDialogFragmentInteractionListener) activity;
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
    public interface OnHelpDialogFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onHelpDialogFragmentInteraction(Uri uri);
    }

}
