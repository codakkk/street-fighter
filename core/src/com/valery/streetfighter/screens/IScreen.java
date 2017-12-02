package com.valery.streetfighter.screens;

import com.badlogic.gdx.Screen;
import com.badlogic.gdx.assets.AssetManager;
import com.valery.streetfighter.SimGame;

/**
 * Created by Ciro on 19/11/2016.
 */
public class IScreen implements Screen {

    protected SimGame sim;

    protected AssetManager assetManager;

    public IScreen(SimGame sim){
        this.sim = sim;
    }

    public void injectDependencies(){

    }

    @Override
    public void show() {

    }

    public void update(float delta){

    }

    @Override
    public void render(float delta) {

    }

    public void handleInput(){

    }

    @Override
    public void resize(int width, int height) {

    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {

    }

    public AssetManager getAssetManager(){
        return this.assetManager;
    }
}
