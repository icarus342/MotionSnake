package edu.uw.tacoma.group14.motionsnake.Fragments.Dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import edu.uw.tacoma.group14.motionsnake.R;

/**
 * LogInDiag is a dialog fragment. It is
 * used to to allow user to login or register
 * after they finish the game if they haven't
 * logged in before.
 * Created by Justin Arnett Son Vu on 5/31/2016.
 */
public class LogInDiag extends DialogFragment {
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public LogInDiag() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment LogInDiag.
     */
    public static LogInDiag newInstance(String param1, String param2) {
        LogInDiag fragment = new LogInDiag();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_log_in_diag, container, false);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface help this dialog communicate with
     * the calling activity. The calling activity must implements
     * doLogInDialog(String,String) and doCancel() methods.
     */
    public interface OnFragmentInteractionListener {
        public void doLogInDialog(String userid,String pwd);
        public void doCancel();
    }

    /**
     * Built the dialog along with all the necessary components.
     * Register the callback method to the button components.
     * @param savedInstanceState
     * @return Dialog to allow user enter their log in information.
     */

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();

        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        builder.setView(inflater.inflate(R.layout.fragment_log_in_diag, null))
                // Add action buttons
                .setPositiveButton("Sign In or Register", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        // sign in the user ...
                        Dialog f = (Dialog) dialog;
                        final EditText useridEdit =(EditText)f.findViewById(R.id.userid_dialog);
                        final EditText pwdEdit =(EditText) f.findViewById(R.id.pwd_dialog);
                        mListener.doLogInDialog(useridEdit.getText().toString(),pwdEdit.getText().toString());
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dismiss();
                        mListener.doCancel();

                    }
                });
        return builder.create();
    }

}
