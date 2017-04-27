package edu.uw.tacoma.group14.motionsnake.Fragments.InstructionFragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import edu.uw.tacoma.group14.motionsnake.R;

/**
 * Fragment to display instruction of the game.
 * Use the {@link InstructionFragment#newInstance} factory method to
 * create an instance of this fragment.
 * Created by Justin Arnett Son Vu on 5/31/2016.
 */
public class InstructionFragment extends Fragment {
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;


    public InstructionFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment InstructionFragment.
     */
    public static InstructionFragment newInstance(String param1, String param2) {
        InstructionFragment fragment = new InstructionFragment();
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
        return inflater.inflate(R.layout.fragment_instruction, container, false);
    }

    /**
     * Set the text for TextView component in the Fragment.
     */
    @Override
    public void onStart(){
        super.onStart();
        TextView txv = (TextView)getActivity().findViewById(R.id.instruction_view);
        txv.setText("This game requires a device and will not respond if ran with an emulator. " +
                "Tilt your device to control the snake, and guide it to eat chickens for points. " +
                "If you guide the snake to collide with itself, you will lose and get a game over. ");
        txv.setTextSize(28);
    }
}
