package com.valery.streetfighter.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.valery.streetfighter.GameInputProcessor;
import com.valery.streetfighter.SimGame;
import com.valery.streetfighter.entity.Entity;
import com.valery.streetfighter.entity.EntityBuilder;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Ciro on 25/11/2016.
 */
public class CharacterScreen extends IScreen {

    private static final int WIDTH = 256; //256
    private static final int HEIGHT = 212; //193

    private SpriteBatch spriteBatch;

    private BitmapFont font;
    private BitmapFont fontx2;

    private OrthographicCamera camera;

    TextureRegion p1_choose, p2_choose;
    Texture logo;

    private int player1_choose, player2_choose;

    private boolean player1_selected, player2_selected;

    List<CharacterInfo> characters;

    Entity player1, player2;

    boolean isInCommandsMenu;

    public CharacterScreen(SimGame sim, SpriteBatch batch){
        super(sim);
        this.spriteBatch = batch;
        this.camera = new OrthographicCamera();
        this.camera.setToOrtho(false, CharacterScreen.WIDTH, CharacterScreen.HEIGHT);

        characters = new ArrayList<CharacterInfo>();
        characters.add(new CharacterInfo("ryu/ryu.json", "ryu/ryu_thumb_small.png", "ryu/ryu_thumb_big.png"));
        characters.add(new CharacterInfo("ken/ken.json", "ken/ken_thumb_small.png", "ken/ken_thumb_big.png"));

        player1_choose = 0;
        player2_choose = 0;
        player1_selected = false;
        player2_selected = false;
        Texture texture = new Texture("character_select.png");
        p1_choose = new TextureRegion(texture, 280, 124, 21, 36);
        p2_choose = new TextureRegion(texture, 280, 163, 21, 35);
        logo = new Texture("logo.png");

        player1 = new Entity();
        player2 = new Entity();

        InputMultiplexer plexer = new InputMultiplexer();
        plexer.addProcessor(player1.input);
        plexer.addProcessor(player2.input);
        Gdx.input.setInputProcessor(plexer);

        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("tfont.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size = 12;
        font = generator.generateFont(parameter);
        generator.dispose();

        generator = new FreeTypeFontGenerator(Gdx.files.internal("tfont.ttf"));
        parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size = 14;
        fontx2 = generator.generateFont(parameter);
        generator.dispose();


        this.isInCommandsMenu = false;
    }

    @Override
    public void show() {
        frameBuffer = new FrameBuffer(Pixmap.Format.RGBA8888, WIDTH, HEIGHT, false);
        frameBuffer.getColorBufferTexture().setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
    }

    private void reset(){
        player1_selected = player2_selected = false;
        player1_choose = player2_choose = 0;
        isInCommandsMenu = false;
    }
    
    @Override
    public void update(float delta) {
        player1.input.update(delta);
        player2.input.update(delta);
        if(!isInCommandsMenu){
            if(player1.input.jump.isClicked || player2.input.jump.isClicked){
                player1_selected = player2_selected = true;
            }
            /*ORRENDO E ASSOLUTAMENTE DA RISCRIVERE*/
            if(player1_selected == true && player2_selected == true){
                Entity.ID = 0;
                GameInputProcessor.ID = 0;
                EntityBuilder builder = new EntityBuilder(null);
                player1 = builder.createEntityFromJson(characters.get(player1_choose).jsonName);
                player2 = builder.createEntityFromJson(characters.get(player2_choose).jsonName);
                this.sim.setScreen(new GameScreen(sim, spriteBatch, player1, player2));
                this.reset();
                return;
            }
            if(player1.input.left.isClicked){
                player1_choose--;
            }
            else if(player1.input.right.isClicked){
                player1_choose++;
            }
            if(player2.input.left.isClicked){
                player2_choose--;
            }
            else if(player2.input.right.isClicked){
                player2_choose++;
            }
            if(player1_choose < 0)player1_choose = characters.size()-1;
            if(player2_choose < 0)player2_choose = characters.size()-1;
            if(player1_choose > characters.size()-1)player1_choose = 0;
            if(player2_choose > characters.size()-1)player2_choose = 0;
        }
        if (player1.input.commands.isClicked) {
            isInCommandsMenu = !isInCommandsMenu;
        }
    }

    FrameBuffer frameBuffer;

    @Override
    public void render(float delta) {
        this.camera.update();
        frameBuffer.begin();
        Gdx.gl.glClearColor(0, 0, 0.5f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        Gdx.gl.glViewport(0, 0, frameBuffer.getWidth(), frameBuffer.getHeight());

        spriteBatch.setProjectionMatrix(camera.combined);
        spriteBatch.begin();
        if(isInCommandsMenu){
            int xOffset = 0;
            int yOffset = 4;
            //P1
            fontx2.draw(spriteBatch, "  Giocatore  #1:", xOffset, HEIGHT - 8);//16
            font.draw(spriteBatch, "A:  Muovi a sinistra", xOffset, HEIGHT - 20 - yOffset);//16
            font.draw(spriteBatch, "D:  Muovi a destra", xOffset, HEIGHT - 28 - yOffset);//16
            font.draw(spriteBatch, "S:  Accovacciati", xOffset, HEIGHT - 36 - yOffset);//16
            font.draw(spriteBatch, "F:  Pugno", xOffset, HEIGHT - 44 - yOffset);//16
            font.draw(spriteBatch, "CTRL: Calcio", xOffset, HEIGHT - 52 - yOffset);//16
            font.draw(spriteBatch, "Spazio: Salta", xOffset, HEIGHT - 60 - yOffset);//16

            //P2
            xOffset = 0;
            yOffset = 6;
            fontx2.draw(spriteBatch, "  Giocatore  #2:", xOffset, HEIGHT - 82);//16
            font.draw(spriteBatch, "Freccia Sx: Muovi a sinistra", xOffset, HEIGHT - 90 - yOffset);//16
            font.draw(spriteBatch, "Freccia Dx: Muovi a destra", xOffset, HEIGHT - 98 - yOffset);//16
            font.draw(spriteBatch, "Freccia Giu': Accovacciati", xOffset, HEIGHT - 106 - yOffset);//16
            font.draw(spriteBatch, "CTRL Dx: Pugno", xOffset, HEIGHT - 114 - yOffset);//16
            font.draw(spriteBatch, "NUM_0: Calcio", xOffset, HEIGHT - 122 - yOffset);//16
            font.draw(spriteBatch, "Freccia Su': Salta", xOffset, HEIGHT - 130 - yOffset);//16

            yOffset = 15;

            font.draw(spriteBatch, "Inoltre, è possibile effettuare delle ", xOffset, HEIGHT - 140 - yOffset);
            font.draw(spriteBatch, "combo mentre si è in volo.", xOffset, HEIGHT - 148 - yOffset);
            fontx2.draw(spriteBatch, "Esempi: ", xOffset, HEIGHT - 164 - yOffset);
            font.draw(spriteBatch, "Pungo in volo: Salto + Pugno", xOffset, HEIGHT - 178 - yOffset);
            font.draw(spriteBatch, "Calcio in volo: Salto + Calcio", xOffset, HEIGHT - 188 - yOffset);
        }
        else if(!isInCommandsMenu) {
            int startX = CharacterScreen.WIDTH / 2 - characters.get(0).thumbnail.getWidth();
            int startY = 24;
            for (int i = 0; i < characters.size(); ++i) {
                CharacterInfo info = characters.get(i);
                spriteBatch.draw(info.thumbnail, startX + (info.thumbnail.getWidth() * i), startY);
                if (player1_choose == i) {
                    spriteBatch.draw(p1_choose, startX + (info.thumbnail.getWidth() * i), startY);
                    spriteBatch.draw(info.thumbnail_big, startX - info.thumbnail_big.getWidth() - 20, startY - 3);
                }
                if (player2_choose == i) {
                    spriteBatch.draw(p2_choose, startX + (info.thumbnail.getWidth() * i), startY - 3);
                    spriteBatch.draw(info.thumbnail_big, startX + (info.thumbnail.getWidth() * characters.size()) + 20, startY - 3);
                }
            }
            spriteBatch.draw(logo, CharacterScreen.WIDTH / 2 - logo.getWidth() / 4, CharacterScreen.HEIGHT / 3 + logo.getHeight() / 4 + 12, logo.getWidth() / 2, logo.getHeight() / 2);
            String string = "Created  by  Ciro  Carandente   4I";
            font.draw(spriteBatch, string, CharacterScreen.WIDTH - (string.length() * 14) / 2, CharacterScreen.HEIGHT - 12);//16
            string = "I.S.I.S  Guido  Tassinari";
            font.draw(spriteBatch, string, CharacterScreen.WIDTH - (string.length() * 16) / 2, CharacterScreen.HEIGHT - 4);
            string = "Premi F1 per la lista dei comandi";
            font.draw(spriteBatch, string, CharacterScreen.WIDTH - (string.length() * 14) / 2, 12);//CharacterScreen.HEIGHT/3 + logo.getHeight()/4 + 16
        }
        spriteBatch.end();
        frameBuffer.end();
        Texture texture = frameBuffer.getColorBufferTexture();
        TextureRegion region = new TextureRegion(texture);
        region.flip(false, true);
        spriteBatch.begin();
        spriteBatch.draw(region, 0, 0, this.camera.viewportWidth, this.camera.viewportHeight);
        spriteBatch.end();
    }

    @Override
    public void handleInput() {
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

    private class CharacterInfo {
        String jsonName;
        Texture thumbnail;
        Texture thumbnail_big;

        public CharacterInfo(String jsonName, String thumbnail, String thumbnail_big){
            this.jsonName = jsonName;
            this.thumbnail = new Texture(thumbnail);
            this.thumbnail_big = new Texture(thumbnail_big);
        }
    }
}
