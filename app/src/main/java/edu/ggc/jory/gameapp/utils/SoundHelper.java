package edu.ggc.jory.gameapp.utils;

import android.app.Activity;
import android.content.Context;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.view.View;

import edu.ggc.jory.gameapp.R;

/**
 * Created by Jory on 4/23/17.
 */

public class SoundHelper {

    private MediaPlayer mMusicPlayer;

    private SoundPool mSoundPool;
    private SoundPool explosion;
    private int mSoundID;
    private int explosionID;
    private boolean mLoaded;
    private float mVolume;

    public SoundHelper(Activity activity) {

        AudioManager audioManager = (AudioManager) activity.getSystemService(Context.AUDIO_SERVICE);
        float actVolume = (float) audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        float maxVolume = (float) audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        mVolume = actVolume / maxVolume;

        activity.setVolumeControlStream(AudioManager.STREAM_MUSIC);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            AudioAttributes audioAttrib = new AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_GAME)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .build();
            mSoundPool = new SoundPool.Builder().setAudioAttributes(audioAttrib).setMaxStreams(6).build();
            explosion = new SoundPool.Builder().setAudioAttributes(audioAttrib).setMaxStreams(6).build();

        } else {
            //noinspection deprecation
            mSoundPool = new SoundPool(6, AudioManager.STREAM_MUSIC, 0);
            explosion = new SoundPool(6, AudioManager.STREAM_MUSIC, 0);

        }

        mSoundPool.setOnLoadCompleteListener(new SoundPool.OnLoadCompleteListener() {
            @Override
            public void onLoadComplete(SoundPool soundPool, int sampleId, int status) {
                mLoaded = true;
            }
        });
        mSoundID = mSoundPool.load(activity, R.raw.balloon_pop, 1);
        explosionID = explosion.load(activity, R.raw.explosion, 1);
    }

    public void playSound() {
        if (mLoaded) {
            mSoundPool.play(mSoundID, mVolume, mVolume, 1, 0, 1f);
        }
    }

    public void playExplosion() {
        if (mLoaded) {
            explosion.play(explosionID, 80, 80, 1, 0, 1f);
        }
    }

    public void prepareMusicPlayer(Context context) {
        mMusicPlayer = MediaPlayer.create(context.getApplicationContext(), R.raw.piano);
        mMusicPlayer.setVolume(5f,5f);
        mMusicPlayer.setLooping(true);
    }

    public void playMusic() {
        if(mMusicPlayer !=null) {
            mMusicPlayer.start();
        }
    }

    public void pauseMusic() {
        if(mMusicPlayer != null && mMusicPlayer.isPlaying()) {
            mMusicPlayer.pause();
        }
    }
}
