package edu.uw.tacoma.group14.motionsnake.Fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.net.HttpURLConnection;
import java.net.URL;

import edu.uw.tacoma.group14.motionsnake.Fragments.InstructionFragment.InstructionFragment;
import edu.uw.tacoma.group14.motionsnake.Fragments.LogInOrRegister.LogRegFragment;
import edu.uw.tacoma.group14.motionsnake.Fragments.SeeHighScore.ScoreFragment;
import edu.uw.tacoma.group14.motionsnake.Fragments.Setting.SettingFragment;
import edu.uw.tacoma.group14.motionsnake.Fragments.SnakeEngine.SnakeGame;
import edu.uw.tacoma.group14.motionsnake.R;

/**
 * This fragment create and handle a view of Main activity.
 * Contains all components in the program.
 * A simple {@link Fragment} subclass.
 * Use the {@link MainFragment#newInstance} factory method to
 * create an instance of this fragment.
 * Created by Justin Arnett Son Vu on 5/31/2016.
 */
public class MainFragment extends Fragment {

    //Build the getscore PHP URL to get score
    private String buildGetScoreURL() {

        StringBuilder sb = new StringBuilder(GetScore_URL);

        try {
            SharedPreferences sharedPreferences =
                    getActivity().getSharedPreferences(getString(R.string.LOGIN_PREFS), Context.MODE_PRIVATE);
            String userId = sharedPreferences.getString(getString(R.string.USERNAME),"");
            sb.append("userid=");
            sb.append(userId);
        }
        catch(Exception e) {
            Toast.makeText(getActivity().getApplicationContext(), "Something wrong with the url" + e.getMessage(), Toast.LENGTH_LONG)
                    .show();
        }
        return sb.toString();
    }

    //make a call to database server to authenticate user.
    private class GetScoreTask extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... urls) {
            String response = "";
            HttpURLConnection urlConnection = null;
            for (String url : urls) {
                try {
                    URL urlObject = new URL(url);
                    urlConnection = (HttpURLConnection) urlObject.openConnection();

                    InputStream content = urlConnection.getInputStream();

                    BufferedReader buffer = new BufferedReader(new InputStreamReader(content));
                    String s = "";
                    while ((s = buffer.readLine()) != null) {
                        response += s;
                    }

                } catch (Exception e) {
                    response = "Unable to Login, Reason: "
                            + e.getMessage();
                } finally {
                    if (urlConnection != null)
                        urlConnection.disconnect();
                }
            }
            return response;
        }


        /**
         * It checks to see if there was a problem with the URL(Network) which is when an
         * exception is caught. It tries to call the parse Method and checks to see if it was successful.
         * If not, it displays the exception.
         *
         * @param result
         */
        @Override
        protected void onPostExecute(String result) {

            try{
                int thisscore = Integer.parseInt(result.trim());
                score = thisscore;

            } catch(NumberFormatException e){
                Toast.makeText(getActivity().getApplicationContext(), "There was an error when getting your score. Please try again!",
                        Toast.LENGTH_LONG) .show();
                return;
            }
        }
    }

    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    //private OnFragmentInteractionListener mListener;
    private final static String GetScore_URL
            = "http://cssgate.insttech.washington.edu/~_450btm14/getscore.php?";
    protected int score = -1;


    public MainFragment() {
        // Required empty public constructor
        super();
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment MainFragment.
     */
    public static MainFragment newInstance(String param1, String param2) {
        MainFragment fragment = new MainFragment();
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
        String url = buildGetScoreURL();
        GetScoreTask st = new GetScoreTask();
        st.execute(url);
    }

    /**
     * Override the original @Link onCreateView.
     * Add onClick listener to the buttons.
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_main, container, false);
        Button seeHighScoreButton = (Button) view.findViewById(R.id.see_high_score_button);
        seeHighScoreButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                ScoreFragment scoreFragment = new ScoreFragment();
            getActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.main_activity, scoreFragment,"Score_Fragment")
                .addToBackStack(null)
                .commit();
            }
        });

        Button instructionButton = (Button) view.findViewById(R.id.instruction_button);
        instructionButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                InstructionFragment instructionFragment = new InstructionFragment();
                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.main_activity, instructionFragment)
                        .addToBackStack(null)
                        .commit();
            }
        });

        Button LogRegButton = (Button) view.findViewById(R.id.Login_or_register_button);
        LogRegButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                LogRegFragment logRegFragment = new LogRegFragment();
                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.main_activity, logRegFragment)
                        .addToBackStack(null)
                        .commit();
            }
        });

        Button playButton = (Button) view.findViewById(R.id.playgame_button);
        playButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
             Intent i = new Intent(getContext(), SnakeGame.class);

                SharedPreferences sharedPreferences =
                        getActivity().getSharedPreferences(getString(R.string.LOGIN_PREFS), Context.MODE_PRIVATE);
                String userId = sharedPreferences.getString(getString(R.string.USERNAME),"");
                i.putExtra("userid",userId);
                startActivity(i);

            }
        });

        Button settingButton = (Button) view.findViewById(R.id.setting_button);
        settingButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                SettingFragment fragment = new SettingFragment();
                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.main_activity, fragment)
                        .addToBackStack(null)
                        .commit();
            }
        });

        Button share = (Button)view.findViewById(R.id.share);
        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("plain/text");
                intent.putExtra(Intent.EXTRA_EMAIL, new String[] {  });
                intent.putExtra(Intent.EXTRA_SUBJECT, "Motion snake high score!");

                intent.putExtra(Intent.EXTRA_TEXT, "Hello. I got "+score+" score for the Motion Snake game!");
                Log.d("SONSON",""+score);
                startActivity(Intent.createChooser(intent, ""));
            }
        });
        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    //Show or hide button base on login status.
    @Override
    public void onResume(){
        super.onResume();
        SharedPreferences sharedPreferences =
                getActivity().getSharedPreferences(getString(R.string.LOGIN_PREFS), Context.MODE_PRIVATE);
        if (sharedPreferences.getBoolean(getString(R.string.LOGGEDIN),false))
        {
            Button logIn = (Button)getActivity().findViewById(R.id.Login_or_register_button);
            logIn.setVisibility(View.GONE);
            Button sharing = (Button) getActivity().findViewById(R.id.share);
            sharing.setVisibility(View.VISIBLE);
        } else {
            Button logIn = (Button)getActivity().findViewById(R.id.Login_or_register_button);
            Button sharing = (Button) getActivity().findViewById(R.id.share);
            if(logIn != null) {
                logIn.setVisibility(View.VISIBLE);
                sharing.setVisibility(View.GONE);
            }
        }
    }

    //Show or hide button base on login status.
    @Override
    public void onStart(){
        super.onStart();
        WebView wv = (WebView)getActivity().findViewById(R.id.gif_view);
        wv.loadUrl("file:///android_asset/giphy.gif");
        wv.getSettings().setLoadWithOverviewMode(true);
        wv.getSettings().setUseWideViewPort(true);

        SharedPreferences sharedPreferences =
                getActivity().getSharedPreferences(getString(R.string.LOGIN_PREFS), Context.MODE_PRIVATE);
        if (sharedPreferences.getBoolean(getString(R.string.LOGGEDIN),false))
        {
            Button logIn = (Button)getActivity().findViewById(R.id.Login_or_register_button);
            logIn.setVisibility(View.GONE);
            Button sharing = (Button) getActivity().findViewById(R.id.share);
            sharing.setVisibility(View.VISIBLE);
        } else {
            Button logIn = (Button)getActivity().findViewById(R.id.Login_or_register_button);
            Button sharing = (Button) getActivity().findViewById(R.id.share);
            if(logIn != null) {
                logIn.setVisibility(View.VISIBLE);
                sharing.setVisibility(View.GONE);
            }
        }
    }
}
