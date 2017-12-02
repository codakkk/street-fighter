package com.valery.streetfighter;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.valery.streetfighter.entity.Entity;
import com.valery.streetfighter.screens.GameScreen;

/**
 * Created by Ciro on 24/11/2016.
 */
public class HUDRenderer {

    TextureRegion healthBarRegion;

    public HUDRenderer(){
        Texture texture = new Texture("ryu/ryu.png");
        healthBarRegion = new TextureRegion(texture, 1008, 0, 16, 4);
    }

    public void render(SpriteBatch batch){
        Entity player1 = GameScreen.players.get(0);
        Entity player2 = GameScreen.players.get(1);
        float healthBar = player1.health / 100f;
        batch.draw(player1.smallThumbnail, 4, GameScreen.WORLD_HEIGHT - 32 - 14);
        batch.draw(player2.smallThumbnail, 16 + 21 + 4 + 200 + 40 + 200 + 4, GameScreen.WORLD_HEIGHT - 32 - 14);
        
        batch.setColor(Color.WHITE);
        batch.draw(healthBarRegion, 8 + 21 + 4 - 1, GameScreen.WORLD_HEIGHT - 32 - 9, 202, 14);
        batch.setColor(Color.RED);
        if(player1.health > 0)batch.draw(healthBarRegion, 8 + 21 + 4, GameScreen.WORLD_HEIGHT - 32 - 8 , healthBar * 200, 12);

        //P2
        player2 = GameScreen.players.get(1);
        healthBar = player2.health / 100f;
        batch.setColor(Color.WHITE);
        batch.draw(healthBarRegion, 16 + 21 + 4 + 200 + 40 - 5, GameScreen.WORLD_HEIGHT - 32 - 9, 202, 14);
        batch.setColor(Color.RED);
        if(player2.health > 0)batch.draw(healthBarRegion, 16 + 21 + 4 + 200 + 40 - 4, GameScreen.WORLD_HEIGHT - 32 - 8, healthBar * 200, 12);
        batch.setColor(Color.WHITE);
    }
}
