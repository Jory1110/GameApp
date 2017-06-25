package edu.ggc.jory.gameapp;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.TransitionDrawable;
import android.os.AsyncTask;
import android.os.Vibrator;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

import edu.ggc.jory.gameapp.utils.HighScoreHelper;
import edu.ggc.jory.gameapp.utils.SimpleAlertDialog;
import edu.ggc.jory.gameapp.utils.SoundHelper;

public class MainActivity extends AppCompatActivity implements Balloon.BalloonListener{

    private static final int MIN_ANIMATION_DELAY = 500;
    private static final int MAX_ANIMATION_DELAY = 1500;
    private static final int MIN_ANIMATION_DURATION = 1000;
    private static final int MAX_ANIMATION_DURATION = 8000;
    private static final int NUMBER_OF_PINS = 3;
    private static final int BALLOONS_PER_LEVEL = 10;

    private TransitionDrawable crossfader;



    private ViewGroup mcontentView;
    private int[] mBalloonColors = new int[3];
    private int mNextColor, mScreenWidth, mScreenHeight, mPinsUsed;



    private int mLevel;
    private int mScore;

    TextView mScoreDisplay;
    TextView mLevelDisplay;

    private List<ImageView> mPinImage = new ArrayList<ImageView>();
    private List<Balloon> mBalloons = new ArrayList<Balloon>();

    private Button mGoButton;

    private boolean mPlaying;
    private boolean mGameStopped = true;
    private int mBalloonsPopped;

    private SoundHelper mSoundHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mBalloonColors[0] = Color.argb(255,0,112,74);
        mBalloonColors[1] = Color.argb(255,165,172,176);
        //mBalloonColors[2] = Color.argb(255,0,0,255);

        Drawable backgrounds[] = new Drawable[2];
        Resources res = getResources();
        backgrounds[1] = res.getDrawable(R.drawable.modern_background);
        backgrounds[0] = res.getDrawable(R.drawable.pausescreen_modern_background);



        crossfader = new TransitionDrawable(backgrounds);


        getWindow().setBackgroundDrawable(crossfader);

        mcontentView = (ViewGroup) findViewById(R.id.activity_main);
        setToFullScreen();

        ViewTreeObserver viewTreeObserver = mcontentView.getViewTreeObserver();

        if(viewTreeObserver.isAlive()) {
            viewTreeObserver.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    mcontentView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    mScreenWidth = mcontentView.getWidth();
                    mScreenHeight = mcontentView.getHeight();
                }
            });
        }

        mcontentView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setToFullScreen();
            }
        });

        mScoreDisplay = (TextView) findViewById(R.id.score_display);
        mLevelDisplay = (TextView) findViewById(R.id.level_display);
        mGoButton = (Button) findViewById(R.id.go_button);
        mPinImage.add((ImageView) findViewById(R.id.pushpin3));
        mPinImage.add((ImageView) findViewById(R.id.pushpin4));
        mPinImage.add((ImageView) findViewById(R.id.pushpin5));
        upDateDisplay();

        mSoundHelper = new SoundHelper(this);
        mSoundHelper.prepareMusicPlayer(this);

    }

    private void startGame() {
        setToFullScreen();
        mScore = 0;
        mLevel = 0;
        mPinsUsed = 0;
        for (ImageView pin: mPinImage) {
            pin.setImageResource(R.drawable.pin);
        }
        mGameStopped = false;
        startLevel();
        mSoundHelper.playMusic();
    }

    private void setToFullScreen() {
        ViewGroup rootLayout = (ViewGroup) findViewById(R.id.activity_main);
        rootLayout.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
    }

    @Override
    protected void onResume() {
        super.onResume();
        setToFullScreen();
        if(mPlaying && mSoundHelper != null) {
            mSoundHelper.playMusic();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(mSoundHelper != null) {
            mSoundHelper.pauseMusic();
        }
    }

    private void startLevel() {
        mLevel++;
        upDateDisplay();
        crossfader.startTransition(3000);
        BalloonLauncher launcher = new BalloonLauncher();
        launcher.execute(mLevel);
        mPlaying = true;
        mBalloonsPopped = 0;
        mGoButton.setText("Stop Game");
    }

    private void finishLevel() {
        Toast.makeText(this, "You Finished level " + mLevel, Toast.LENGTH_SHORT).show();
        crossfader.reverseTransition(3000);
        mPlaying =  false;
        mGoButton.setText("Start Level " + (mLevel + 1));
    }

    public void goButtonClickHandler(View view) {
        if(mPlaying) {
            gameOver(false);
        } else if (mGameStopped) {
            startGame();
        }else {
            startLevel();
        }
    }

    @Override
    public void popBalloon(Balloon balloon, boolean userTouch) {
        mBalloonsPopped++;
        if(balloon.getColor() == Color.argb(255,218,165,32)) {
            mSoundHelper.playExplosion();
            Log.i("OMNI", "Explosion!");
        }
        else {
            mSoundHelper.playSound();
        }
        Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        // Vibrate for 500 milliseconds
        v.vibrate(500);
        mcontentView.removeView(balloon);
        mBalloons.remove(balloon);
        if (userTouch) {
            mScore++;
            
        } else {
            mPinsUsed ++;
            if(mPinsUsed <= mPinImage.size()) {
                mPinImage.get(mPinsUsed -1).setImageResource(R.drawable.pin_off);
            }
            if(mPinsUsed == NUMBER_OF_PINS) {
                gameOver(true);
            }
            else {
                Toast.makeText(this, "Missed that one!", Toast.LENGTH_SHORT).show();
            }
        }
        upDateDisplay();

        if(mBalloonsPopped == BALLOONS_PER_LEVEL) {
            finishLevel();
        }
    }

    private void gameOver(boolean allPinsUsed) {
        Toast.makeText(this, "Created by Katherine and Jory. Credit to David Gassner and Lynda.com for providing the start of this project", Toast.LENGTH_SHORT).show();
        mSoundHelper.pauseMusic();
        crossfader.reverseTransition(3000);

        for (Balloon balloon:
             mBalloons) {
            mcontentView.removeView(balloon);
            balloon.setPopped(true);

        }
        mBalloons.clear();
        mPlaying = false;
        mGameStopped = true;
        mGoButton.setText("Start Game");

        if(allPinsUsed) {
            if(HighScoreHelper.isTopScore(this, mScore)) {
                HighScoreHelper.setTopScore(this, mScore);
                SimpleAlertDialog dialog = SimpleAlertDialog.newInstance("New high schore", "Your new high score is " + mScore);
                dialog.show(getSupportFragmentManager(),null);
            }
        }
    }

    private void upDateDisplay() {
        mScoreDisplay.setText(String.valueOf(mScore));
        mLevelDisplay.setText(String.valueOf(mLevel));
    }

    private class BalloonLauncher extends AsyncTask<Integer, Integer, Void> {

        @Override
        protected Void doInBackground(Integer... params) {


            if (params.length != 1) {
                throw new AssertionError(
                        "Expected 1 param for current level");
            }

            int level = params[0];
            int maxDelay = Math.max(MIN_ANIMATION_DELAY,
                    (MAX_ANIMATION_DELAY - ((level - 1) * 500)));
            int minDelay = maxDelay / 2;

            int balloonsLaunched = 0;
            while (balloonsLaunched < BALLOONS_PER_LEVEL && mPlaying) {

//              Get a random horizontal position for the next balloon
                Random random = new Random(new Date().getTime());
                int xPosition = random.nextInt(mScreenWidth - 200);
                publishProgress(xPosition);
                balloonsLaunched++;

//              Wait a random number of milliseconds before looping
                int delay = random.nextInt(minDelay) + minDelay;
                try {
                    Thread.sleep(delay);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            return null;

        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            int xPosition = values[0];
            launchBalloon(xPosition);
        }

    }

    private void launchBalloon(int x) {

        Random ran = new Random();


        Balloon balloon;
        if (ran.nextInt(10) != 0) {
        balloon = new Balloon(this, mBalloonColors[mNextColor], randomGaussianLong(110,160,ran));
        } else {
            balloon = new Balloon(this, Color.argb(255,218,165,32), randomGaussianLong(110,160,ran));
        }

        mBalloons.add(balloon);

        if (mNextColor + 1 == mBalloonColors.length) {
            mNextColor = 0;
        } else {
            mNextColor++;
        }

//      Set balloon vertical position and dimensions, add to container
        balloon.setX(x);
        balloon.setY(mScreenHeight + balloon.getHeight());
        mcontentView.addView(balloon);

//      Let 'er fly
        int duration = Math.max(MIN_ANIMATION_DURATION, MAX_ANIMATION_DURATION - (mLevel * 1000));
        balloon.releaseBalloon(mScreenHeight, duration);



    }

    public static int  randomGaussianLong(int min, int  max, Random rng)
    {
        int  v = (int)((max + min) * 0.5 + rng.nextGaussian() * (max - min) * 0.5);
        return v < min ? min : (v > max ? max : v);
    }
}
