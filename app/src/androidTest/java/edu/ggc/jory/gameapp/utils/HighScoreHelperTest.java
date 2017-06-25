package edu.ggc.jory.gameapp.utils;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;


public class HighScoreHelperTest {



    @Test
    public void isTopScore() throws Exception {
        Context appContext = InstrumentationRegistry.getTargetContext();
        HighScoreHelper.setTopScore(appContext, 100);

        Assert.assertTrue(HighScoreHelper.isTopScore(appContext,100));
        Assert.assertFalse(HighScoreHelper.isTopScore(appContext,50));


    }

    @Test
    public void getTopScore() throws Exception {
        Context appContext = InstrumentationRegistry.getTargetContext();
        HighScoreHelper.setTopScore(appContext, 100);

        Assert.assertEquals(100,HighScoreHelper.getTopScore(appContext));
    }

}