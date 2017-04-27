/*
 * Motion Snake Game
 *
 * Author: Justin Arnett
 * Version: May 5, 2016
 */


package edu.uw.tacoma.group14.motionsnake.Fragments.SnakeEngine;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import edu.uw.tacoma.group14.motionsnake.Fragments.Dialog.LogInDiag;
import edu.uw.tacoma.group14.motionsnake.Fragments.Setting.SettingDB;
import edu.uw.tacoma.group14.motionsnake.MainActivity;
import edu.uw.tacoma.group14.motionsnake.R;

/**
 * The activity that handles the device running the game.
 */
public class SnakeGame extends FragmentActivity implements LogInDiag.OnFragmentInteractionListener{

    private String buildLoginURL(String thisUserid,String thisPwd) {

        StringBuilder sb = new StringBuilder(Login_URL);

            sb.append("userid=");
            sb.append(thisUserid);

            sb.append("&pwd=");
            sb.append(thisPwd);

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
                        mSharedPreferences = getSharedPreferences(getString(R.string.LOGIN_PREFS)
                                , Context.MODE_PRIVATE);

                    mSharedPreferences
                            .edit()
                            .putBoolean(getString(R.string.LOGGEDIN), true)
                            .commit();

                    mSharedPreferences
                            .edit()
                            .putString(getString(R.string.USERNAME),userid)
                            .commit();
                    updateScore();
                    Intent i = getBaseContext().getPackageManager()
                            .getLaunchIntentForPackage( getBaseContext().getPackageName() );
                    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    finish();
                    startActivity(i);

                } else {
                    String url1 = buildRegisterURL(userid,pwd);
                        new RegisterTask().execute(url1);
                    //return;
                }

            } catch(NumberFormatException e){
                return;
            }
            invalidateOptionsMenu();
        }
    }



    //Build a Register PHP URL
    private String buildRegisterURL(String thisUserid,String thisPwd) {

        StringBuilder sb = new StringBuilder(Register_URL);

            sb.append("userid=");
            sb.append(thisUserid);


            sb.append("&pwd=");
            sb.append(thisPwd);

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
                Toast.makeText(getApplicationContext(),"Successfully Registered!",Toast.LENGTH_SHORT).show();
                if(mSharedPreferences == null)
                    mSharedPreferences = getSharedPreferences(getString(R.string.LOGIN_PREFS)
                            , Context.MODE_PRIVATE);
                mSharedPreferences
                        .edit()
                        .putBoolean(getString(R.string.LOGGEDIN), true)
                        .commit();

                mSharedPreferences
                        .edit()
                        .putString(getString(R.string.USERNAME),userid)
                        .commit();
                pureUpdateScore();
                Intent i = getBaseContext().getPackageManager()
                        .getLaunchIntentForPackage( getBaseContext().getPackageName() );
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                finish();
                startActivity(i);

            } else {
                Toast.makeText(getApplicationContext(), "Unable to Register user. '" +result+"'",
                        Toast.LENGTH_LONG) .show();
                return;
            }
            invalidateOptionsMenu();
        }
    }



    /*
     * The game engine that updates the game's entities
     * and the paint them to the view. Also hold's the
     * game's logic and input controls.
     */
    GameManager GM;
    protected int score;
    protected String userid;
    private final static String Login_URL
            = "http://cssgate.insttech.washington.edu/~_450btm14/login.php?";
    private final static String Register_URL
            = "http://cssgate.insttech.washington.edu/~_450btm14/addUser.php?";
    private SharedPreferences mSharedPreferences;
    private String pwd;

    /**
     * Creates the activity, and initializes the game.
     *
     * @param savedInstanceState The instance State.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        SettingDB settingDB = new SettingDB(this);
        String[] strings = settingDB.getSetting().split(",");
        int angle = Integer.parseInt(strings[0]);
        String color = strings[1];
        boolean debug = (Integer.parseInt(strings[2]) == 1)? true : false;
        GM = new GameManager(this,angle,color,debug);
        Bundle b = getIntent().getExtras();
        userid = b.getString("userid");
        setContentView(GM);
    }


    /**
     * When the app is resumed while during this activity.
     * Resumes operation of the game manager.
     */
    @Override
    protected void onResume() {
        super.onResume();
        GM.resume();
    }

    /**
     * When the app is paused during the activity, operation
     * of the game manager will be paused.
     */
    @Override
    protected void onPause() {
        super.onPause();
        GM.pause();
    }

    protected void pureUpdateScore(){
        UpdateScore us = new UpdateScore(score, userid, this);
        us.UploadScore();
    }

    protected void updateScore(){
        if(userid.length() > 0) {
            UpdateScore us = new UpdateScore(score, userid, this);
            us.UploadScore();
            Intent i = new Intent(this, MainActivity.class);
            startActivity(i);
        } else {
            LogInDiag logFrag = new LogInDiag();
            logFrag.show(getSupportFragmentManager(),"LogInorRegister");
        }
    }

    public void doLogInDialog(String thisuserid,String thispwd){
        //First, do LogIn

        //buildURL(thisuserid,thispwd);
        String url = buildLoginURL(thisuserid,thispwd);
        ConnectivityManager connMgr = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if(networkInfo != null && networkInfo.isConnected()){
            userid = thisuserid;
            pwd = thispwd;
            new LoginTask().execute(url);
        } else {
            Toast.makeText(this,
                    "No network connection available. Please try again.",
                    Toast.LENGTH_LONG) .show();
        }

        //doLogin();
        mSharedPreferences = getSharedPreferences(getString(R.string.LOGIN_PREFS)
                , Context.MODE_PRIVATE);
        String id =  mSharedPreferences.getString(getString(R.string.USERNAME),"");
        boolean r = mSharedPreferences.getBoolean(getString(R.string.LOGGEDIN),false);



    }

    public void doCancel(){
        Intent i = getBaseContext().getPackageManager()
                .getLaunchIntentForPackage( getBaseContext().getPackageName() );
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        finish();
        startActivity(i);
    }
}
