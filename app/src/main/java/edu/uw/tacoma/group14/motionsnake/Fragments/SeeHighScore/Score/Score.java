package edu.uw.tacoma.group14.motionsnake.Fragments.SeeHighScore.Score;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Model class of Score
 * Created by Justin Arnett Son Vu on 5/31/2016.
 */
public class Score implements Serializable {

    String mUserId;
    String mScore;

    public static final String USERID = "userid", SCORE = "score";

    public Score(String UserId, String Score){
        mUserId = UserId;
        mScore = Score;
    }

    public String getmUserId() {
        return mUserId;
    }

    public String getmScore() {
        return mScore;
    }

    public void setmUserId(String mUserId) {
        this.mUserId = mUserId;
    }

    public void setmScore(String mScore) {
        this.mScore = mScore;
    }

    /**
     * Parses the json string, returns an error message if unsuccessful.
     * Returns course list if success.
     * @param scoreJSON
     * @return reason or null if successful.
     */
    public static String parseScoreJSON(String scoreJSON, List<Score> scoreList) {
        String reason = null;
        if (scoreJSON != null) {
            try {
                JSONArray arr = new JSONArray(scoreJSON);

                for (int i = 0; i < arr.length(); i++) {
                    JSONObject obj = arr.getJSONObject(i);
                    Score score = new Score(obj.getString(Score.USERID),obj.getString(Score.SCORE));
                    scoreList.add(score);
                }
            } catch (JSONException e) {
                reason =  "Unable to parse data, Reason: " + e.getMessage();
            }

        }
        return reason;
    }
}
