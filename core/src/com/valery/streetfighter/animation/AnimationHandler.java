package com.valery.streetfighter.animation;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.valery.streetfighter.entity.Entity;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Ciro on 17/11/2016.
 */
public class AnimationHandler {

    private Map<Entity.State, EAnimation> animations;

    private float deltaTime;

    private EAnimation currentAnimation;

    String currentAnimationName = "";

    Entity entity;

    public AnimationHandler(Entity entity){
        this.entity = entity;
        this.animations = new HashMap<Entity.State, EAnimation>();
        this.currentAnimation = null;
        this.deltaTime = 0f;
    }

    public void update(float delta){
        if(currentAnimation != null){
            this.deltaTime += delta;
            currentAnimation.update(this.deltaTime);
        }
    }

    /*public TextureRegion getOldFrame(){
        int idx = currentAnimation.getKeyFrameIndex(deltaTime);
        int endIdx = idx;
        if(idx > 0)endIdx = idx-1;
        return this.currentAnimation.getKeyFrames()[endIdx];
    }*/

    public TextureRegion getCurrentFrame(){
        if(this.currentAnimation == null)return null;
        return this.currentAnimation.getKeyFrame(deltaTime);
    }

    public void setAnimation(Entity.State state){
        if(this.animations.containsKey(state)){
            if(this.animations.get(state) == currentAnimation)return;
            if(this.currentAnimation != null && !this.isCurrentAnimationFinished() && this.currentAnimation.getListener() != null)this.currentAnimation.getListener().onAnimationEnd();
            this.currentAnimation = animations.get(state);
            this.deltaTime = 0f;
            this.currentAnimationName = state.name();
            if(this.currentAnimation.getListener() != null)this.currentAnimation.getListener().onAnimationStart();
        }
    }

    public EAnimation getAnimation(Entity.State state){
        if(this.animations.containsKey(state)){
            return this.animations.get(state);
        }
        return null;
    }

    public EAnimation getCurrentAnimation(){
        return this.currentAnimation;
    }

    public String getCurrentAnimationName(){
        return currentAnimationName;
    }

    public boolean isCurrentAnimationFinished(){
        if(this.currentAnimation == null)return false;
        return this.currentAnimation.isAnimationFinished(deltaTime);
    }

    public void removeAnimation(String name){
        if(this.animations.containsKey(name)) {
            this.animations.remove(name);
        }
    }

    public void addAnimation(Entity.State state, EAnimation animation){
        if(!animations.containsKey(state)){
            animations.put(state, animation);
        }
    }
}
