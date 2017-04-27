package edu.uw.tacoma.group14.motionsnake;

import android.app.Activity;
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
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.Spinner;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.net.HttpURLConnection;
import java.net.URL;

import edu.uw.tacoma.group14.motionsnake.Fragments.MainFragment;
import edu.uw.tacoma.group14.motionsnake.Fragments.Setting.SettingDB;

/**
 * Main activity of the program.
 * Will automatically start the @Link MainFragment fragment to start the program.
 * Created by Justin Arnett Son Vu on 5/31/2016.
 */
public class MainActivity extends AppCompatActivity{

    //Build the getscore PHP URL to get score
    private String buildGetScoreURL() {
        StringBuilder sb = new StringBuilder(GetScore_URL);
        try {
            SharedPreferences sharedPreferences =
                    getSharedPreferences(getString(R.string.LOGIN_PREFS), Context.MODE_PRIVATE);
            String userId = sharedPreferences.getString(getString(R.string.USERNAME),"");
            sb.append("userid=");
            sb.append(userId);
        }
        catch(Exception e) {
            Toast.makeText(getApplicationContext(), "Something wrong with the url" + e.getMessage(), Toast.LENGTH_LONG)
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
                int score = Integer.parseInt(result.trim());
                Toast.makeText(getApplicationContext(), "Your high score is: " + score, Toast.LENGTH_LONG).show();
            } catch(NumberFormatException e){
                Toast.makeText(getApplicationContext(), "There was an error when getting your score. Please try again!",
                        Toast.LENGTH_LONG) .show();
                return;
            }
        }
    }


    private final static String GetScore_URL
            = "http://cssgate.insttech.washington.edu/~_450btm14/getscore.php?";

    private Menu theMenu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        MainFragment mainFragment = new MainFragment();
        final FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        getSupportFragmentManager().beginTransaction()
                .add(R.id.main_activity, mainFragment,"Main_Fragment")
                .commit();
        ft.detach(mainFragment);
        ft.attach(mainFragment);
        ft.commit();

    }

    @Override
    public void onResume(){
        super.onResume();
    }

    /**
     * Override the original method @Link onCreateOptionsMenu.
     * Show or hide the menu base on the login status of player.
     * @param menu the menu to be created.
     * @return true or false for creating menu. True if successfully, false otherwise.
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        theMenu = menu;
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        SharedPreferences sharedPreferences =
                getSharedPreferences(getString(R.string.LOGIN_PREFS), Context.MODE_PRIVATE);

        if (!sharedPreferences.getBoolean(getString(R.string.LOGGEDIN),false))
        {
            menu.getItem(0).setVisible(false);
            menu.getItem(1).setVisible(false);

        } else {
            menu.getItem(0).setVisible(true);
            menu.getItem(0).setTitle(sharedPreferences.getString(getString(R.string.USERNAME),""));
            menu.getItem(1).setVisible(true);
        }
        return true;
    }

    //Handle menu item click.
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        //Sign out occurs when click LogOut.
        if (id == R.id.action_logout) {
            SharedPreferences sharedPreferences =
                    getSharedPreferences(getString(R.string.LOGIN_PREFS), Context.MODE_PRIVATE);
            sharedPreferences.edit().putBoolean(getString(R.string.LOGGEDIN), false)
                    .commit();
            sharedPreferences.edit().putString(getString(R.string.USERNAME), null)
                    .commit();
            Intent i = new Intent(this,MainActivity.class);
            startActivity(i);
            finish();
            return true;

        } else if(id==R.id.username_display){
            String url = buildGetScoreURL();
            ConnectivityManager connMgr = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
            if(networkInfo != null && networkInfo.isConnected()){
                new GetScoreTask().execute(url);
            } else {
                Toast.makeText(getApplicationContext(),
                        "No network connection available. Please try again.",
                        Toast.LENGTH_LONG) .show();
            }
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * This method used to save the setting to the SQLite database.
     * @param v the view when call this method.
     */
    public void Save(View v){
        Spinner angle = (Spinner)findViewById(R.id.tilt_angle);
        Spinner color = (Spinner)findViewById(R.id.color_text);
        CheckBox cb = (CheckBox)findViewById(R.id.check_debug);
        SettingDB mSettingDB = new SettingDB(this);
        mSettingDB.deleteSetting();
        mSettingDB.insertSetting(angle.getSelectedItem().toString(),color.getSelectedItem().toString(),cb.isChecked());
        mSettingDB.closeDB();
        Toast.makeText(this,"Setting saved!",Toast.LENGTH_SHORT).show();
    }

    /**
     * Override the original method @Link onPrepareOptionsMenu.
     * Show or hide the menu base on the login status of player.
     * @param menu the menu to be created.
     * @return true or false for repairing menu. True if successfully, false otherwise.
     */
    @Override
    public boolean onPrepareOptionsMenu(Menu menu){
        super.onPrepareOptionsMenu(menu);
        SharedPreferences sharedPreferences =
                getSharedPreferences(getString(R.string.LOGIN_PREFS), Context.MODE_PRIVATE);

        if (!sharedPreferences.getBoolean(getString(R.string.LOGGEDIN),false))
        {
            menu.getItem(0).setVisible(false);
            menu.getItem(1).setVisible(false);

        } else {
            menu.getItem(0).setVisible(true);
            menu.getItem(0).setTitle(sharedPreferences.getString(getString(R.string.USERNAME),""));
            menu.getItem(1).setVisible(true);
        }
        return true;
    }
}
