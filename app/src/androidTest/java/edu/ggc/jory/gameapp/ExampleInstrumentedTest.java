package edu.ggc.jory.gameapp;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import edu.ggc.jory.gameapp.utils.HighScoreHelper;

import static org.junit.Assert.*;

/**
 * Instrumentation test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class ExampleInstrumentedTest {
    @Test
    public void useAppContext() throws Exception {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getTargetContext();

        assertEquals("edu.ggc.jory.gameapp", appContext.getPackageName());
    }

    @Test
    public void isTopScore() throws Exception {
        Context appContext = InstrumentationRegistry.getTargetContext();
        HighScoreHelper.setTopScore(appContext, 100);

        Assert.assertTrue(HighScoreHelper.isTopScore(appContext,101));
        Assert.assertFalse(HighScoreHelper.isTopScore(appContext,50));


    }

    @Test
    public void getTopScore() throws Exception {
        Context appContext = InstrumentationRegistry.getTargetContext();
        HighScoreHelper.setTopScore(appContext, 100);

        Assert.assertEquals(100,HighScoreHelper.getTopScore(appContext));
    }
}
