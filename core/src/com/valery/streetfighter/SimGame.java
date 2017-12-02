package com.valery.streetfighter;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.valery.streetfighter.screens.CharacterScreen;
import com.valery.streetfighter.screens.GameScreen;
import com.valery.streetfighter.screens.IScreen;

public class SimGame extends Game {

	SpriteBatch batch;
	float time = System.currentTimeMillis();

	public CharacterScreen characterScreen;
	
	@Override
	public void create () {
		batch = new SpriteBatch();
		time = System.currentTimeMillis();
		characterScreen = new CharacterScreen(this, batch);
		setScreen(characterScreen);
	}

	private static final float TICK_DURATION = 1f/30f;

	float mRemainder;
	int ticks = 0;

	float accDt = 0;
	@Override
	public void render () {
		IScreen screen = ( (IScreen)getScreen());

		float dt = Gdx.graphics.getDeltaTime();
		accDt += dt;
		dt += mRemainder;

		int nticks = (int)(dt / TICK_DURATION);
		mRemainder = dt - nticks * TICK_DURATION;
		ticks += nticks;
		while(nticks >= 1){
			screen.update(dt);
			nticks--;
		}

		if(accDt >= 1f){
			accDt -= 1f;
			System.out.println("ticksPerFrame: "+ ticks);
			ticks = 0;
		}
		super.render();
	}
	
	@Override
	public void dispose () {
		batch.dispose();
	}
}
