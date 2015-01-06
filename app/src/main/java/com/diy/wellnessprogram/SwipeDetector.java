package com.diy.wellnessprogram;

import android.nfc.Tag;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import java.util.logging.Logger;

public class SwipeDetector implements View.OnTouchListener {

    private String TAG = "PGGURU";

    public static enum Action {
        LR, // Left to Right
        RL, // Right to Left
        TB, // Top to bottom
        BT, // Bottom to Top
        None // when no action was detected
    }

    private static final String logTag = "SwipeDetector";
    private static final int MIN_DISTANCE_X= 50;
    private static final int MIN_DISTANCE_Y= 20;
    private float downX, downY, upX, upY;
    private Action mSwipeDetected = Action.None;

    public boolean swipeDetected() {
        return mSwipeDetected != Action.None;
    }

    public Action getAction() {
        return mSwipeDetected;
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN: {
                downX = event.getX();
                downY = event.getY();
                mSwipeDetected = Action.None;
                //return false; // allow other events like Click to be processed
                return false;
            }
            //case MotionEvent.ACTION_MOVE: {
            case MotionEvent.ACTION_MOVE: {
                upX = event.getX();
                upY = event.getY();
                float deltaX = downX - upX;
                float deltaY = downY - upY;
                if(Math.abs(deltaX) > Math.abs(deltaY)) {
                    if (Math.abs(deltaX) > MIN_DISTANCE_X) {
                        if (deltaX < 0) {
                            Log.i(TAG, "Swipe Left to Right");
                            mSwipeDetected = Action.LR;
                            return false;
                        }
                        if (deltaX > 0) {
                            Log.i(TAG, "Swipe Right to Left");
                            mSwipeDetected = Action.RL;
                            return false;
                        }

                    } /*else {
                        return false;
                    }
                    */
                } else {
                    if (Math.abs(deltaY) > MIN_DISTANCE_Y) {
                        if (deltaY < 0) {
                            Log.i(TAG, "Swipe Top to Bottom: "+deltaY);
                            mSwipeDetected = Action.TB;
                            return true;
                        }
                        if (deltaY > 0) {
                            Log.i(TAG, "Swipe Bottom to Top");
                            mSwipeDetected = Action.BT;
                            return true;
                        }

                    } /*else {
                        return false;
                    }
                    */
                }
                return true;
            }
        }
        return false;
    }
}