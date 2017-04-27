package edu.uw.tacoma.group14.motionsnake.Fragments.Setting;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.Spinner;

import edu.uw.tacoma.group14.motionsnake.R;

/**
 * This class show fragment that contains setting for the game.
 * This fragment also let user to adjust setting and save it.
 * Will interact with SQLite class and @Link SettingDB to get, save or delete record.
 * A simple {@link Fragment} subclass.
 * to handle interaction events.
 * Use the {@link SettingFragment#newInstance} factory method to
 * create an instance of this fragment.
 * Created by Justin Arnett Son Vu on 5/31/2016.
 */
public class SettingFragment extends Fragment {
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    private SettingDB mSettingDB;

    //private OnFragmentInteractionListener mListener;

    public SettingFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SettingFragment.
     */
    public static SettingFragment newInstance(String param1, String param2) {
        SettingFragment fragment = new SettingFragment();
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

    /**
     * Override the original @Link onCreateView method.
     * Add array of data to the spinner.
     * Save data to the database when hit save button.
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_setting, container, false);
        Spinner angleSpin = (Spinner)v.findViewById(R.id.tilt_angle);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getContext(),R.array.angle,android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_item);
        angleSpin.setAdapter(adapter);

        Spinner colorSpin = (Spinner)v.findViewById(R.id.color_text);
        ArrayAdapter<CharSequence> adapter1 = ArrayAdapter.createFromResource(getContext(),R.array.color_array,android.R.layout.simple_spinner_item);
        adapter1.setDropDownViewResource(android.R.layout.simple_spinner_item);
        colorSpin.setAdapter(adapter1);

        CheckBox cb = (CheckBox)v.findViewById(R.id.check_debug);

        mSettingDB = new SettingDB(getActivity());
        String[] mSettingss = mSettingDB.getSetting().split(",");
        String angle;
        String color;
        boolean debug;
        if(mSettingss.length > 0){
            angle = mSettingss[0];
            color = mSettingss[1];
            debug = (Integer.parseInt(mSettingss[2]) == 1)? true : false;
        } else {
            angle = "0";
            color = "red";
            debug = false;
        }
        cb.setChecked(debug);
        if(color.equals("red")){
            colorSpin.setSelection(0);
        } else if(color.equals("yellow")){
            colorSpin.setSelection(1);
        } else if (color.equals("green")){
            colorSpin.setSelection(2);
        }
        int theAngle = Integer.parseInt(angle);
        angleSpin.setSelection(theAngle);
        mSettingDB.deleteSetting();
        mSettingDB.insertSetting(angle,color,debug);
        mSettingDB.closeDB();
        return v;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

}
