package edu.uw.tacoma.group14.motionsnake.Fragments.SnakeEngine;

import android.app.Fragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
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
 * Created by sonvu on 5/31/2016.
 */
public class UpdateScore {

    //make a call to database server to create new user.
    private class UploadScoreTask extends AsyncTask<String, Void, String> {

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
                    response = "Unable to save score, Reason: "
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
            if (result.startsWith("success")) {
                Toast.makeText(mContext,"Thank you for playing game. Your socre was saved!",Toast.LENGTH_SHORT);
            }
        }
    }

    private final static String AddScore_URL
            = "http://cssgate.insttech.washington.edu/~_450btm14/addScore.php?";
    private SharedPreferences mSharedPreferences;
    //private int score;
    private StringBuilder URL = new StringBuilder(AddScore_URL);
    private int score;
    private String userId;
    private Context mContext;

    public UpdateScore(int thisScore,String thisUserid, Context theContext){
        score = thisScore;
        userId = thisUserid;
        mContext = theContext;
    }

//    @Override
//    public void onCreate(Bundle savedInstanceState){
//        super.onCreate(savedInstanceState);
//    }

    public void UploadScore(){
        //Log.d("SONSON",""+R.string.LOGIN_PREFS);

        URL.append("userid=");
        URL.append(userId);
        URL.append("&score=");
        URL.append(score);
        Log.d("SONSON",""+userId);
        ConnectivityManager connMgr = (ConnectivityManager)mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if(networkInfo != null && networkInfo.isConnected()){
            new UploadScoreTask().execute(URL.toString());
        } else {
            Toast.makeText(mContext.getApplicationContext(),
                    "No network connection available. Please try again.",
                    Toast.LENGTH_LONG) .show();
        }
    }
}
