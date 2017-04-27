package edu.uw.tacoma.group14.motionsnake;

import android.test.ActivityInstrumentationTestCase2;

import com.robotium.solo.Solo;

import edu.uw.tacoma.group14.motionsnake.Fragments.Setting.SettingFragment;

/**
 * This class test the creation and function of the Setting Fragment.
 * Created by Justin Arnett Son Vu on 5/31/2016.
 */
public class SettingFragmentTest extends ActivityInstrumentationTestCase2<MainActivity> {
    private Solo solo;

    public SettingFragmentTest(){
        super(MainActivity.class);
    }

    @Override
    public void setUp() throws Exception{
        super.setUp();
        solo = new Solo(getInstrumentation(),getActivity());
        solo.clickOnButton("Setting");
    }

    @Override
    public void tearDown() throws Exception{
        solo.finishOpenedActivities();
    }

    public void testSettingFragmentLoad(){
        boolean fragmentLoaded = solo.searchText("Tilt angle:");
        assertTrue("Setting fragment loaded",fragmentLoaded);
    }

    public void testSaveWork(){
        solo.clickOnButton("SAVE");
        boolean saveWorked = solo.searchText("Setting saved!");
        assertTrue("Save works",saveWorked);
    }
}
