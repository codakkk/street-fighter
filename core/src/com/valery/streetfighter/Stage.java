package com.valery.streetfighter;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.valery.streetfighter.screens.GameScreen;

/**
 * Created by Ciro on 24/11/2016.
 */
public class Stage {

    TextureRegion[] ceil;
    TextureRegion floor;
    TextureRegion background;
    TextureRegion sky;
    
    List<AnimatedBackground> backgrounds;
    
    public Stage(){
        Texture texture = new Texture("stage.png");
        floor = new TextureRegion(texture, 0, 160, 512, 56);
        background = new TextureRegion(texture, 48, 44, 416, 117);
        sky = new TextureRegion(texture, 108, 503, 404, 49);
        ceil = new TextureRegion[2];
        ceil[0] = new TextureRegion(texture, 32, 0, 448, 16);
        ceil[1] = new TextureRegion(texture, 48, 16, 416, 16);
        
        backgrounds = new ArrayList<AnimatedBackground>();
        
        AnimatedBackground b = new AnimatedBackground(369, 56 - 16, 1f/2f, 
        		new TextureRegion(texture, 193, 432, 30, 62), new TextureRegion(texture, 229, 432, 32, 62));
        backgrounds.add(b);
        
        b = new AnimatedBackground(369 - 10 - 39, 56 - 16, 1f/2f, 
        		new TextureRegion(texture, 275, 435, 39, 53), new TextureRegion(texture, 319, 432, 39, 56));
        backgrounds.add(b);
        
        b = new AnimatedBackground(369 - 10 - 39 - 4 - 20, 56 - 16, 1f/2f, 
        		new TextureRegion(texture, 296, 334, 20, 58));
        backgrounds.add(b);
        
        b = new AnimatedBackground(369 - 10 - 39 - 4 - 20 - 70 - 6, 56 - 16 + 25, 1f/2f, 
        		new TextureRegion(texture, 221, 233, 70, 134));
        backgrounds.add(b);
        
        b = new AnimatedBackground(
        		369 - 10 - 39 - 4 - 20 - 70 - 6 - 14 - 30, 
        		56 - 16, 
        		1f/2f, 
        		new TextureRegion(texture, 374, 432, 30, 63), new TextureRegion(texture, 409, 432, 30, 63));
        backgrounds.add(b);
        
        b = new AnimatedBackground(
        		369 - 10 - 39 - 4 - 20 - 70 - 6 - 14 - 30 - 24 - 9, 
        		56 - 16, 
        		1f/2f, 
        		new TextureRegion(texture, 453, 432, 24, 56), new TextureRegion(texture, 481, 432, 24, 56));
        backgrounds.add(b);
        
        b = new AnimatedBackground(
        		369 - 10 - 39 - 4 - 20 - 70 - 6 - 14 - 30 - 24 - 9 - 40, 
        		56 - 16, 
        		1f/2f, 
        		new TextureRegion(texture, 104, 336, 40, 56));
        backgrounds.add(b);
    }

    public void update(float delta){
        for(AnimatedBackground b : backgrounds){
        	b.update(delta);
        }
    }
    
    public void render(SpriteBatch spriteBatch){
        if(!spriteBatch.isDrawing())return;
        spriteBatch.draw(sky, 0, GameScreen.WORLD_HEIGHT - 52, 512, 52);
        spriteBatch.draw(background, 0, 50, 512, 216f/1.5f);
        spriteBatch.draw(floor, 0, 0);
        spriteBatch.draw(ceil[0], 0, GameScreen.WORLD_HEIGHT - 16, 512, 16);
        spriteBatch.draw(ceil[1], 0, GameScreen.WORLD_HEIGHT - 32, 512, 16);
        System.out.println(backgrounds.size());
        for(AnimatedBackground b : backgrounds){
        	b.render(spriteBatch);
        }
    }
}
