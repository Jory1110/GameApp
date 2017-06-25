package edu.ggc.jory.gameapp;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.view.MotionEvent;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.content.Context;

import edu.ggc.jory.gameapp.utils.PixelHelper;

import static edu.ggc.jory.gameapp.R.drawable.balloon;

/**
 * Created by Jory on 4/23/17.
 */

@SuppressLint("AppCompatCustomView")
public class Balloon extends ImageView implements Animator.AnimatorListener, ValueAnimator.AnimatorUpdateListener {

    private ValueAnimator mAnimator;
    private BalloonListener mListener;
    private boolean mPopped;
    private int color;
    public Balloon(Context context, int mBalloonColor, double size) {
        super(context);
    }

    public Balloon (Context context, int color, int rawHeight) {
        super(context);

        mListener = (BalloonListener) context;
        this.color = color;
        this.setImageResource(balloon);
        this.setColorFilter(color);
        int rawWidth = rawHeight/2;

        int dpHeight = PixelHelper.pixelsToDp(rawHeight,context);
        int dpWidth = PixelHelper.pixelsToDp(rawWidth,context);


        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(dpWidth,dpHeight);
        setLayoutParams(params);
    }

    public int getColor() {
        return color;
    }

    public void releaseBalloon(int screenHeigh, int duration) {
        mAnimator = new ValueAnimator();
        mAnimator.setDuration(duration);
        mAnimator.setFloatValues(screenHeigh, 0f);
        mAnimator.setInterpolator(new LinearInterpolator());
        mAnimator.setTarget(this);
        mAnimator.addListener(this);
        mAnimator.addUpdateListener(this);
        mAnimator.start();
    }

    @Override
    public void onAnimationStart(Animator animation) {

    }

    @Override
    public void onAnimationEnd(Animator animation) {
        if(!mPopped) {
            mListener.popBalloon(this,false);
        }

    }

    @Override
    public void onAnimationCancel(Animator animation) {

    }

    @Override
    public void onAnimationRepeat(Animator animation) {

    }

    @Override
    public void onAnimationUpdate(ValueAnimator animation) {
        setY((float) animation.getAnimatedValue());


    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if(!mPopped && event.getAction() == MotionEvent.ACTION_DOWN) {
            mListener.popBalloon(this,true);
            mPopped = true;
            mAnimator.cancel();
        }
        return super.onTouchEvent(event);
    }

    public void setPopped(boolean popped) {
        mPopped = popped;
        if(popped) {
            mAnimator.cancel();
        }
    }

    public interface BalloonListener {
        void popBalloon(Balloon balloon, boolean userTouch);
    }
}
