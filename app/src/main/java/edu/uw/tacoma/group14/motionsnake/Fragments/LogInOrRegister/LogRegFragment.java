package edu.uw.tacoma.group14.motionsnake.Fragments.LogInOrRegister;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import edu.uw.tacoma.group14.motionsnake.Fragments.MainFragment;
import edu.uw.tacoma.group14.motionsnake.R;

/**
 * Build a fragment to let user log in or register.
 * A simple {@link Fragment} subclass.
 * to handle interaction events.
 * Use the {@link LogRegFragment#newInstance} factory method to
 * create an instance of this fragment.
 * Created by Justin Arnett Son Vu on 5/31/2016.
 */
public class LogRegFragment extends Fragment {

    //Build the Login PHP URL to login user
    private String buildLoginURL(View v) {

        StringBuilder sb = new StringBuilder(Login_URL);

        try {

            String userId = mUserIdEditText.getText().toString();
            sb.append("userid=");
            sb.append(userId);


            String pwd = mPwdEditText.getText().toString();
            sb.append("&pwd=");
            sb.append(pwd);
        }
        catch(Exception e) {
            Toast.makeText(v.getContext(), "Something wrong with the url" + e.getMessage(), Toast.LENGTH_LONG)
                    .show();
        }
        return sb.toString();
    }

    //make a call to database server to authenticate user.
    private class LoginTask extends AsyncTask<String, Void, String> {

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
                int resultInInt = Integer.parseInt(result.trim());
                if(resultInInt == 1){
                    if(mSharedPreferences == null)
                        mSharedPreferences = getContext().getSharedPreferences(getString(R.string.LOGIN_PREFS)
                                , Context.MODE_PRIVATE);
                    mSharedPreferences
                            .edit()
                            .putBoolean(getString(R.string.LOGGEDIN), true)
                            .commit();
                    //save username the user entered to display on screen
                    final EditText usernameEditText = (EditText)getActivity().findViewById(R.id.username_edittext);
                    String username = usernameEditText.getText().toString();
                    mSharedPreferences
                            .edit()
                            .putString(getString(R.string.USERNAME),username)
                            .commit();
                    MainFragment mainFragment = new MainFragment();
                    getActivity().getSupportFragmentManager().beginTransaction()
                            .replace(R.id.main_activity, mainFragment)
                            .addToBackStack(null)
                            .commit();

                } else {
                    Toast.makeText(getActivity(), "User name and password are not match with our records. Please try again!",
                            Toast.LENGTH_LONG) .show();
                    return;
                }
            } catch(NumberFormatException e){
                Toast.makeText(getActivity(), "User name and password are not match with our records. Please try again!",
                        Toast.LENGTH_LONG) .show();
                return;
            }
            getActivity().invalidateOptionsMenu();
        }
    }



    //Build a Register PHP URL
    private String buildRegisterURL(View v) {

        StringBuilder sb = new StringBuilder(Register_URL);

        try {

            String userId = mUserIdEditText.getText().toString();
            sb.append("userid=");
            sb.append(userId);


            String pwd = mPwdEditText.getText().toString();
            sb.append("&pwd=");
            sb.append(pwd);
        }
        catch(Exception e) {
            Toast.makeText(v.getContext(), "Something wrong with the url" + e.getMessage(), Toast.LENGTH_LONG)
                    .show();
        }
        return sb.toString();
    }

    //make a call to database server to create new user.
    private class RegisterTask extends AsyncTask<String, Void, String> {

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
            if(result.startsWith("success")){
                Toast.makeText(getActivity(),"Successfully Registered!",Toast.LENGTH_SHORT).show();
                if(mSharedPreferences == null)
                    mSharedPreferences = getContext().getSharedPreferences(getString(R.string.LOGIN_PREFS)
                            , Context.MODE_PRIVATE);
                mSharedPreferences
                        .edit()
                        .putBoolean(getString(R.string.LOGGEDIN), true)
                        .commit();
                //save username the user entered to display on screen
                final EditText usernameEditText = (EditText)getActivity().findViewById(R.id.username_edittext);
                String username = usernameEditText.getText().toString();
                mSharedPreferences
                        .edit()
                        .putString(getString(R.string.USERNAME),username)
                        .commit();
                MainFragment mainFragment = new MainFragment();
                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.main_activity, mainFragment)
                        .addToBackStack(null)
                        .commit();

            } else {
                Toast.makeText(getActivity(), "Unable to Register user. '" +result+"'",
                        Toast.LENGTH_LONG) .show();
                return;
            }
            getActivity().invalidateOptionsMenu();
        }
    }

    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    private EditText mUserIdEditText;
    private EditText mPwdEditText;
    private final static String Login_URL
            = "http://cssgate.insttech.washington.edu/~_450btm14/login.php?";
    private final static String Register_URL
            = "http://cssgate.insttech.washington.edu/~_450btm14/addUser.php?";
    private SharedPreferences mSharedPreferences;


    public LogRegFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment LogRegFragment.
     */
    public static LogRegFragment newInstance(String param1, String param2) {
        LogRegFragment fragment = new LogRegFragment();
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
     * Override the original onCreateView method.
     * Register onClickListeners for Login and Register buttons.
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_log_reg, container, false);
        mUserIdEditText = (EditText)view.findViewById(R.id.username_edittext);
        mPwdEditText = (EditText)view.findViewById(R.id.pwd_edittext);
        Button loginButton = (Button) view.findViewById(R.id.login_button);
        loginButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                String url = buildLoginURL(view);
                ConnectivityManager connMgr = (ConnectivityManager)getActivity()
                        .getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
                if(networkInfo != null && networkInfo.isConnected()){
                    new LoginTask().execute(url);
                    getActivity().invalidateOptionsMenu();
                } else {
                    Toast.makeText(getActivity(),
                            "No network connection available. Please try again.",
                            Toast.LENGTH_LONG) .show();
                }
            }
        });

        Button registerButton = (Button) view.findViewById(R.id.register_button);
        registerButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                String url = buildRegisterURL(view);
                ConnectivityManager connMgr = (ConnectivityManager)getActivity()
                        .getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
                if(networkInfo != null && networkInfo.isConnected()){
                    new RegisterTask().execute(url);
                    getActivity().invalidateOptionsMenu();
                } else {
                    Toast.makeText(getActivity(),
                            "No network connection available. Please try again.",
                            Toast.LENGTH_LONG) .show();
                }
            }
        });
        return view;
    }
}
