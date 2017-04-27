package edu.uw.tacoma.group14.motionsnake;

import junit.framework.TestCase;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;

import edu.uw.tacoma.group14.motionsnake.Fragments.SeeHighScore.Score.Score;

/**
 * Created by Justin Arnett Son Vu on 5/31/2016.
 */
public class ScoreTest extends TestCase {

    private Score mScore;

    @Test
    public void testConstructor(){
        Score score = new Score("player","1600");
        assertNotNull(score);
    }

    @Test
    public void testParseCourseJSON() {
        String scoreJSON = "[{\"userid\":\"player\",\"score\":\"1600\"}]";
        String message =  Score.parseScoreJSON(scoreJSON
                , new ArrayList<Score>());
        assertTrue("JSON With Valid String", message == null);
    }

    @Before
    public void setUp(){
        mScore = new Score("player","1600");
    }

    @Test
    public void testGetUserId(){
        assertEquals("player",mScore.getmUserId());
    }


    @Test
    public void testGetScore(){
        assertEquals("1600",mScore.getmScore());
    }

    @Test
    public void testSetUserId(){
        mScore.setmUserId("Son");
        assertEquals("Son",mScore.getmUserId());
    }

    @Test
    public void testSetScore(){
        mScore.setmScore("1000");
        assertEquals("1000",mScore.getmScore());
    }


}
