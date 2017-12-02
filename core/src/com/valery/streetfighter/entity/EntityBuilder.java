package com.valery.streetfighter.entity;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.google.gson.Gson;
import com.valery.streetfighter.animation.AnimationAdapter;
import com.valery.streetfighter.animation.EAnimation;
import com.valery.streetfighter.screens.GameScreen;

import java.util.List;

/**
 * Created by Ciro on 25/11/2016.
 */
public class EntityBuilder {

    GameScreen game;

    public EntityBuilder(GameScreen game){
        this.game = game;
    }

    public Entity createEntityFromJson(String file){
        SEntity sentity = createSkeletonEntityFromJson(file);
        final Entity entity = new Entity();
        Texture texture = new Texture(sentity.texture);
        for(int i = 0; i < sentity.animations.size(); ++i) {
            final SAnimation sanim = sentity.animations.get(i);
            //BRUTTISSIMODC
            TextureRegion[] regions = new TextureRegion[sanim.frames.size()];
            for (int j = 0; j < sanim.frames.size(); ++j) {
                SFrame frame = sanim.frames.get(j);
                regions[j] = new TextureRegion(texture, frame.x, frame.y, frame.width, frame.height);
            }
            Animation.PlayMode mode = Animation.PlayMode.NORMAL;
            if (sanim.playType == 0) mode = Animation.PlayMode.NORMAL;
            else if (sanim.playType == 1) mode = Animation.PlayMode.LOOP;
            AnimationAdapter listener = new AnimationAdapter() {
                @Override
                public void onAnimationStart() {
                    entity.count = sanim.frames.size() ;
                    if (sanim.action) entity.hitBoundingBox.setActive(true);
                }

                @Override
                public void onFrameEnd() {
                    entity.count--;
                }

                @Override
                public void onAnimationEnd() {
                    entity.count = 0;
                    if (sanim.action) entity.hitBoundingBox.setActive(false);
                    //setState(State.STANDING_IDLE);
                }
            };
            EAnimation animation = new EAnimation(sanim.duration, regions);
            if (listener != null) animation.setListener(listener);
            animation.setPlayMode(mode);
            Entity.State state = Entity.State.valueOf(sanim.name.toUpperCase());
            if (state != null) entity.animationHandler.addAnimation(state, animation);
        }
        entity.smallThumbnail = new TextureRegion(new Texture(sentity.thumbnail));
        entity.bigThumbnail = new TextureRegion(new Texture(sentity.thumbnail));
        return entity;
    }

    public SEntity createSkeletonEntityFromJson(String file){
        String string = Gdx.files.internal(file).readString().trim();
        System.out.println(string);
        Gson gson = new Gson();
        SEntity sentity = gson.fromJson(string, SEntity.class);//gson.fromJson;
        System.out.println("Name: " + sentity.name + " Texture: " + sentity.texture);
        for(int i = 0; i < sentity.animations.size(); ++i){
            SAnimation animation = sentity.animations.get(i);
            System.out.println("Name: " + animation.name + "texture: " + " - duration: " + animation.duration);
            for(int j = 0; j < animation.frames.size(); ++j){
                SFrame frame = animation.frames.get(j);
                System.out.print("Frame " + j + " - ");
                System.out.print("x: " + frame.x);
                System.out.print(" - y: " + frame.y);
                System.out.print(" - w: " + frame.width);
                System.out.print(" - h: " + frame.height + "\n");
            }
        }
        return sentity;
    }

    private class SEntity {
        String name;
        String texture;
        String thumbnail;
        String thumbnail_big;
        private List<SAnimation> animations;
    }

    private class SAnimation {
        String name;
        float duration;
        int playType;
        boolean action = false;
        List<SFrame> frames;
    }

    //USED FOR DESERIALIZING
    public class SFrame {
        int x, y, width, height;
    }
}
