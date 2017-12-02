package com.valery.streetfighter;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.valery.streetfighter.animation.EAnimation;

public class AnimatedBackground {

	private float x, y;
	
	private EAnimation animation;
	
	float accTime;
	
	public AnimatedBackground(float x, float y, float animTime, TextureRegion... regions){
		animation = new EAnimation(animTime, regions);
		accTime = 0f;
		this.x = x;
		this.y = y;
	}
	
	public void update(float delta){
		animation.update(delta);
		accTime+= delta;
	}
	
	public void render(SpriteBatch batch){
		TextureRegion region = animation.getKeyFrame(accTime, true);
		batch.draw(region, x, y);
		System.out.println("Test");
	}
}
