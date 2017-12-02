package com.valery.streetfighter.animation;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;

/**
 * Created by Ciro on 24/11/2016.
 */
public class EAnimation extends Animation {

    private AnimationListener listener;

    public EAnimation (float frameDuration, Array<? extends TextureRegion> keyFrames, PlayMode playMode) {
        super(frameDuration, keyFrames, playMode);
    }

    public EAnimation (float frameDuration, TextureRegion... keyFrames) {
        super(frameDuration, keyFrames);
    }

    public EAnimation(float frameDuration, Array<? extends TextureRegion> keyFrames) {
        super(frameDuration, keyFrames);
    }

    private TextureRegion lastFrame;

    public void update(float delta){
        if(listener != null) {
            TextureRegion currentFrame = this.getKeyFrame(delta);
            if (currentFrame != lastFrame) {
                listener.onFrameEnd();
                listener.onFrameStart(this.getKeyFrameIndex(delta));
            }
            lastFrame = currentFrame;
            if (this.isAnimationFinished(delta)) {
                listener.onAnimationEnd();
            }
        }
    }

    public AnimationListener getListener(){
        return this.listener;
    }

    public void setListener(AnimationListener listener){
        this.listener = listener;
    }
}
