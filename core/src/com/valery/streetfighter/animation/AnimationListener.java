package com.valery.streetfighter.animation;

/**
 * Created by Ciro on 24/11/2016.
 */
public interface AnimationListener {

    public void onAnimationStart();

    public void onFrameStart(int frameid);
    public void onFrameEnd();

    public void onAnimationEnd();
}
