package com.valery.streetfighter.screens;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
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
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.valery.streetfighter.GameInputProcessor;
import com.valery.streetfighter.HUDRenderer;
import com.valery.streetfighter.SimGame;
import com.valery.streetfighter.Stage;
import com.valery.streetfighter.entity.Entity;

/**
 * Created by Ciro on 17/11/2016.
 */
public class GameScreen extends IScreen {

    public static final int WORLD_WIDTH = 512;
    public static final int WORLD_HEIGHT = 216;

    SpriteBatch spriteBatch;
    ShapeRenderer shapeRenderer;
    OrthographicCamera camera;

    public static List<Entity> players;

    Stage stage;

    FrameBuffer frameBuffer;

    HUDRenderer renderer;

    BitmapFont arcadeClassicFont;

    public static int gameTick;

    public static boolean DEBUG = false;

    public static float GAME_SPEED = 1f;

    private boolean isFinished = false;

    int loadingTimeInTick;
    
    public GameScreen(SimGame sim, SpriteBatch spriteBatch, Entity p1, Entity p2){
        super(sim);
        this.spriteBatch = spriteBatch;
        shapeRenderer = new ShapeRenderer();
        this.camera = new OrthographicCamera();
        this.camera.setToOrtho(false, WORLD_WIDTH, WORLD_HEIGHT);

        InputMultiplexer multiplexer = new InputMultiplexer();

        stage = new Stage();

        frameBuffer = new FrameBuffer(Pixmap.Format.RGBA8888, WORLD_WIDTH, WORLD_HEIGHT, false);
        frameBuffer.getColorBufferTexture().setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);

        renderer = new HUDRenderer();
        players = new ArrayList<Entity>();

        players.add(p1);
        players.add(p2);

        p2.setPosition(WORLD_WIDTH - 40, Entity.FLOOR_LIMIT);

        multiplexer.addProcessor(p1.input);
        multiplexer.addProcessor(p2.input);

        Gdx.input.setInputProcessor(multiplexer);

        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("arcadeclassic.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size = 18;
        arcadeClassicFont = generator.generateFont(parameter);
        generator.dispose();
        loadingTimeInTick = 0;
        //prepareFramePieces();
    }

    @Override
    public void show() {
    }

    private void reset(){
        for(Entity e : players) {
            e.reset();
        }
        gameTick = 0;
        isFinished = false;
        loadingTimeInTick = 0;
    }

    public int winnerID = -1;

    @Override
    public void update(float delta){
        if(GAME_SPEED < 1f){
            GAME_SPEED += 0.05f;
            GAME_SPEED = 1f;
        }
        delta *= GAME_SPEED;
        /*loadingTimeInTick ++;
        if(loadingTimeInTick < 30){
        	return;
        }*/
        GameScreen.gameTick++;
        //This should go in handleInput().
        if(isFinished){
        	if(players.get(0).input.jump.isClicked || players.get(1).input.jump.isClicked){
                reset();
                return;
            }
        	if(players.get(0).input.kick.isClicked || players.get(1).input.kick.isClicked){
                sim.setScreen(sim.characterScreen);
                return;
            }
        }
        Entity player1 = GameScreen.players.get(0);
        player1.updateGravity(delta);
        player1.update(delta);
        //
        Entity player2 = GameScreen.players.get(1);
        player2.updateGravity(delta);
        player2.update(delta);
        //
        if(player1.health <= 0){
            winnerID = player2.id;
            player2.setState(Entity.State.VICTORY);
            player1.setState(Entity.State.DEATH);
            isFinished = true;
        }

        if(player2.health <= 0){
            winnerID = player1.id;
            player1.setState(Entity.State.VICTORY);
            player2.setState(Entity.State.DEATH);
            isFinished = true;
        }
        
        if(stage != null)stage.update(delta);
        
        if(Gdx.input.isKeyJustPressed(Keys.F1)){
        	GameScreen.DEBUG = !GameScreen.DEBUG;
        }
    }

    BitmapFont font = new BitmapFont();

    @Override
    public void handleInput(){

    }



    @Override
    public void render(float delta) {
        this.camera.update();
        frameBuffer.begin();
        Gdx.gl.glViewport(0, 0, frameBuffer.getWidth(), frameBuffer.getHeight());
        Gdx.gl.glClearColor(1, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        spriteBatch.setProjectionMatrix(camera.combined);
        spriteBatch.begin();
        stage.render(spriteBatch);
        for (int i = 0; i < players.size(); ++i) {
            Entity p = players.get(i);
            p.render(spriteBatch);
        }

        List<GameInputProcessor.InputState> inputStateList = players.get(0).input.keyBuffer;
        if (inputStateList.size() > 0) {
            for (int i = 0; i < inputStateList.size(); ++i) {
                GameInputProcessor.InputState state = players.get(0).input.keyBuffer.get(i);
                String stateName = (state.state == GameInputProcessor.InputState.STATE_PRESSED) ? "pressed" : "released";
                if (state.state == GameInputProcessor.InputState.STATE_CLICKED) stateName = "clicked";
                if (state != null) font.draw(spriteBatch, "" + state.key.getName() + "_" + stateName, 16, 16 + i * 16);
            }
        }
        if (DEBUG) {
            font.draw(spriteBatch, "Old State: " + players.get(0).getOldState().name(), 0, 16);
            font.draw(spriteBatch, "Current State: " + players.get(0).getState().name(), 0, 32);
            font.draw(spriteBatch, "Current Anim: " + players.get(0).animationHandler.getCurrentAnimationName(), 0, 48);
            //font.draw(spriteBatch, "Shield Count: " + players.get(0).shieldCount, 0, 48 + 16);
            //font.draw(spriteBatch, "Can Act: " + players.get(0).isInAirAction(), 0, 48 + 32);

            font.draw(spriteBatch, "Old State: " + players.get(1).getOldState().name(), 300, 16);
            font.draw(spriteBatch, "Current State: " + players.get(1).getState().name(), 300, 32);
            font.draw(spriteBatch, "Current Anim: " + players.get(1).animationHandler.getCurrentAnimationName(), 300, 48);
            //font.draw(spriteBatch, "Shield Count: " + players.get(1).shieldCount, 300, 48 + 16);
        }
        int seconds = 60 - (gameTick / 40);
        seconds %= 60;
        //arcadeClassicFont.draw(spriteBatch, ""+ seconds, 16 + 21 + 4 + 200 + 8, GameScreen.WORLD_HEIGHT - 16);

        renderer.render(spriteBatch);
        //This should be in HUDRenderer.
        //isFinished = true;
        if (isFinished){
        	//this.drawFrame(spriteBatch, GameScreen.WORLD_WIDTH / 2 - 180, GameScreen.WORLD_HEIGHT - 40 - 18 - 60 + 4*8, 47, 6);
            String string = "Premi  SPAZIO  per  riavviare  la  partita!";
            arcadeClassicFont.draw(spriteBatch, string, GameScreen.WORLD_WIDTH / 2 - (string.length() * 8) / 2, GameScreen.WORLD_HEIGHT - 48);
            string = "Premi    CTRL   DESTRO   per   tornare   alla";
            arcadeClassicFont.draw(spriteBatch, string, GameScreen.WORLD_WIDTH / 2 - (string.length() * 7) / 2 - 15, GameScreen.WORLD_HEIGHT - 40 - 18 - 2);
            string = "selezione   del   personaggio!";
            arcadeClassicFont.draw(spriteBatch, string, GameScreen.WORLD_WIDTH / 2 - 172, GameScreen.WORLD_HEIGHT - 40 - 18 - 12 - 2);
        }
        spriteBatch.end();

        shapeRenderer.setProjectionMatrix(camera.combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);


        shapeRenderer.end();
        if(DEBUG){
	        for(int i = 0; i < players.size(); ++i){
	            Entity p = players.get(i);
	            p.renderDebug(shapeRenderer);
	        }
        }
        frameBuffer.end();

        Texture texture = frameBuffer.getColorBufferTexture();
        TextureRegion region = new TextureRegion(texture);
        region.flip(false, true);

        spriteBatch.begin();
        spriteBatch.draw(region, 0, 0, this.camera.viewportWidth, this.camera.viewportHeight);
        spriteBatch.end();
        /*shapeRenderer.setProjectionMatrix(camera.combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        for(Entity entity : entities) {
            shapeRenderer.setColor(Color.YELLOW);

        }
        shapeRenderer.end();*/
    }

    @Override
    public void resize(int width, int height) {
        /*this.camera = new OrthographicCamera();
        this.camera.setToOrtho(false, 512, 216);*/
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
    
    //TextureRegion downRight, upRight, leftDown, leftUp, left, right, down, up, fill;
    
    /*private void prepareFramePieces(){
        Texture theFrame = new Texture("frame.png");
        downRight = new TextureRegion(theFrame, 0, 0, 8, 8);
        upRight = new TextureRegion(theFrame, 0, 24, 8, 8);
        leftDown = new TextureRegion(theFrame, 24, 0, 8, 8);
        leftUp = new TextureRegion(theFrame, 24, 24, 8, 8);
        left = new TextureRegion(theFrame, 0, 8, 8, 8);
        right = new TextureRegion(theFrame, 24, 8, 8, 8);
        down = new TextureRegion(theFrame, 8, 24, 8, 8);
        up = new TextureRegion(theFrame, 8, 0, 8, 8);
        fill = new TextureRegion(theFrame, 8, 8, 8, 8);
    }
    
    private void drawFrame(SpriteBatch batch, int posX, int posY, int nWidthAsBlock, int nHeightAsBlock){
		for(int y = 0; y <= nHeightAsBlock; ++y){
			for(int x = 0; x <= nWidthAsBlock; ++x){
				int tX = posX + x * 8;
				int tY = posY + y * 8;
				if(x == 0 && y == 0){
					batch.draw(upRight, tX, tY);
				}else if(y == nHeightAsBlock && x == nWidthAsBlock){
					batch.draw(leftDown, tX, tY);
				}else if(y == 0 && x == nWidthAsBlock){
					batch.draw(leftUp, tX, tY);
				}else if(x == 0 && y == nHeightAsBlock){
					batch.draw(downRight, tX, tY);
				}else if(x == 0){
					batch.draw(left, tX, tY);
				}else if(x == nWidthAsBlock){
					batch.draw(right, tX, tY);
				}else if(y == 0){
					batch.draw(down, tX, tY);
				}else if(y == nHeightAsBlock){
					batch.draw(up, tX, tY);
				}else{
					batch.draw(fill, tX, tY);
				}
			}	
    	}
		//Inner Text
		int startX = posX + 1 * 8;
		int startY = posY + nHeightAsBlock * 8;
		int yOffset = 0;
		//String test = "Test   inner   text    aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa";
		String test = "Test inner aaaaaaaaaaaaaaaaaaaaaaaaaaaaa";
		int len = test.length();
		int endLen = 0;
		if(len > nWidthAsBlock - 2*2){
			List<String> list = getSubStrings(test, nWidthAsBlock - 5);
			//System.out.println(list.size());
			for(String s : list){
				this.arcadeClassicFont.draw(batch, s, startX, startY - yOffset);
				yOffset += 12;
			}
		}else{
			this.arcadeClassicFont.draw(batch, test, startX, startY - yOffset);
		}
		
    }
    
    private List<String> getSubStrings(String string, int maxChars){
    	List<String> mine = new ArrayList<String>();
    	int len = string.length();
    	int last = 0;
    	if(len > maxChars){
    		int pieces = (int)Math.floor(len / maxChars);
    		for(int i = 0; i < pieces ; ++i){
    			if(len - last > maxChars){
    				mine.add(string.substring(last, (i)*maxChars));
    				last = i*maxChars;
    			}
    		}
    	}else mine.add(string);
    	return mine;
    }*/
}
