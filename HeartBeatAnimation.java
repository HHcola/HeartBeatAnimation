package com.qq.ac.android.view;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;

import com.qq.ac.android.library.util.LogComUtils;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by timothyhe on 2017/10/31.
 */

public class HeartBeatAnimation {

    private static final String TAG = "HeartBeatAnimation";
    private View mTarget;
    private long mDuration = 100;
    private long mDelay = 1200;
    private float mFromScale = 1.0f;
    private float mToScale = 0.8f;
    private AnimatorSet mHeartBeatAnimatorSet;
    private ObjectAnimator mHeartBeatIncreaseAnimator;
    private ObjectAnimator mHeartBeatDecreaseAnimator;
    private AnimatorEndListener mAnimatorEndListener;
    private AtomicBoolean mAnimatorEnd = new AtomicBoolean(false);
    private final int RESTART_ANIMATOR_MSG = 1000;

    public HeartBeatAnimation(View target) {
        mTarget = target;
    }

    public static HeartBeatAnimation with(View target) {
        return new HeartBeatAnimation(target);
    }

    public HeartBeatAnimation in(long duration) {
        mDuration = duration;
        return this;
    }

    public HeartBeatAnimation target(View target) {
        mTarget = target;
        return this;
    }

    public HeartBeatAnimation after(long delay) {
        mDelay = delay;
        return this;
    }

    public HeartBeatAnimation scaleFrom(float fromScale) {
        mFromScale = fromScale;
        return this;
    }

    public HeartBeatAnimation scaleTo(float toScale) {
        mToScale = toScale;
        return this;
    }

    public void cancel(AnimatorEndListener animatorEndListener) {
        LogComUtils.d(TAG, "cancel");
        if (mHeartBeatAnimatorSet != null) {
            mAnimatorEnd.compareAndSet(false, true);
            mAnimatorEndListener = animatorEndListener;
            if (mHandler.hasMessages(RESTART_ANIMATOR_MSG)) {
                mHandler.removeMessages(RESTART_ANIMATOR_MSG);
                onListenerAnimatorEnd();
            }
        }
    }

    public void end(AnimatorEndListener animatorEndListener) {
        if (mHeartBeatAnimatorSet != null) {
            mAnimatorEndListener = animatorEndListener;
            mHeartBeatAnimatorSet.end();
        }
    }

    public void start() {
        if (mHandler.hasMessages(RESTART_ANIMATOR_MSG)) {
            mHandler.removeMessages(RESTART_ANIMATOR_MSG);
        }
        if (mHeartBeatAnimatorSet == null) {
            mHeartBeatAnimatorSet = new AnimatorSet();
            PropertyValuesHolder pvhIncreaseScaleX =
                    PropertyValuesHolder.ofFloat("scaleX", mFromScale, mToScale);
            PropertyValuesHolder pvhIncreaseScaleY =
                    PropertyValuesHolder.ofFloat("scaleY", mFromScale, mToScale);
            PropertyValuesHolder pvhDecreaseScaleX =
                    PropertyValuesHolder.ofFloat("scaleX", mToScale, mFromScale);
            PropertyValuesHolder pvhDecreaseScaleY =
                    PropertyValuesHolder.ofFloat("scaleY", mToScale, mFromScale);

            mHeartBeatIncreaseAnimator = ObjectAnimator.ofPropertyValuesHolder(
                    mTarget, pvhIncreaseScaleX, pvhIncreaseScaleY
            );
            mHeartBeatIncreaseAnimator.setDuration(mDuration);
            mHeartBeatIncreaseAnimator.setInterpolator(new AccelerateInterpolator());



            mHeartBeatDecreaseAnimator = ObjectAnimator.ofPropertyValuesHolder(
                    mTarget, pvhDecreaseScaleX, pvhDecreaseScaleY
            );
            mHeartBeatDecreaseAnimator.setDuration(mDuration);
            mHeartBeatDecreaseAnimator.setInterpolator(new DecelerateInterpolator());


            mHeartBeatAnimatorSet.play(mHeartBeatIncreaseAnimator).before(mHeartBeatDecreaseAnimator);
            mHeartBeatAnimatorSet.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {
                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    LogComUtils.d(TAG, "mHeartBeatAnimatorSet  onAnimationEnd");
                    if (!mAnimatorEnd.get()) {
                        mHandler.sendEmptyMessageDelayed(RESTART_ANIMATOR_MSG, mDelay);
                    } else {
                        onListenerAnimatorEnd();
                    }
                }

                @Override
                public void onAnimationCancel(Animator animation) {
                }

                @Override
                public void onAnimationRepeat(Animator animation) {
                }
            });
        } else {
            mHeartBeatIncreaseAnimator.setTarget(mTarget);
            mHeartBeatDecreaseAnimator.setTarget(mTarget);
        }

        mHandler.sendEmptyMessageDelayed(RESTART_ANIMATOR_MSG, mDelay);
    }


    private void onListenerAnimatorEnd() {
        if (mAnimatorEndListener != null) {
            mAnimatorEndListener.onAnimatorEnd();
        }
    }

    private Handler mHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            if (!mAnimatorEnd.get()) {
                mHeartBeatAnimatorSet.start();
            } else {
                onListenerAnimatorEnd();
            }
        }
    };

    public interface AnimatorEndListener {
        void onAnimatorEnd();
    }
}
