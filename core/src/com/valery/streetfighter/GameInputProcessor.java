package com.valery.streetfighter;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.controllers.*;
import com.valery.streetfighter.screens.GameScreen;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Ciro on 20/11/2016.
 */
public class GameInputProcessor implements InputProcessor {

    private static final float BUFFER_RESET_TIME = 1f;

    protected List<Key> keys = new ArrayList<Key>();
    public Key left = new Key("Left");
    public Key right = new Key("Right");
    public Key punch = new Key("Punch");
    public Key kick = new Key("Kick");
    public Key test = new Key("Left");
    public Key crouch = new Key("Crouch");
    public Key jump = new Key("Jump");
    public Key shield = new Key("Shield");

    public Key commands = new Key("Commands");

    private int controllerID;

    public static int ID = 0;

    private int id = 0;

    float resetTimer;

    public List<InputState> keyBuffer;

    public GameInputProcessor(int player, boolean controller){
        this.keyBuffer = new ArrayList<InputState>();
        commands.addKey(Input.Keys.F1);
        if(player == 0){
            left.addKey(Input.Keys.A);
            right.addKey(Input.Keys.D);
            punch.addKey(Input.Keys.F);
            kick.addKey(Input.Keys.CONTROL_LEFT);
            test.addKey(Input.Keys.T);
            crouch.addKey(Input.Keys.S);
            jump.addKey(Input.Keys.SPACE);
            shield.addKey(Input.Keys.SHIFT_LEFT);
        } else if(player == 1){
            left.addKey(Input.Keys.LEFT);
            right.addKey(Input.Keys.RIGHT);
            punch.addKey(Input.Keys.CONTROL_RIGHT);
            kick.addKey(Input.Keys.NUMPAD_0);
            test.addKey(Input.Keys.T);
            crouch.addKey(Input.Keys.DOWN);
            jump.addKey(Input.Keys.UP);
            shield.addKey(Input.Keys.SHIFT_RIGHT);
        }
        if(controller){
            controllerID = ID;
            this.id = ID;
            ID++;
            int size = Controllers.getControllers().size;
            if(size != 0 && controllerID >= 0 && controllerID < size) {
                if (Controllers.getControllers().get(controllerID) != null) {
                    Controllers.getControllers().get(controllerID).addListener(new ControllerAdapter() {
                        @Override
                        public boolean buttonDown(Controller controller, int buttonIndex) {
                            //0: x
                            //1: o
                            //2: q
                            //3: t
                            if(buttonIndex == 0){
                                jump.toggle(true);
                            }
                            if(buttonIndex == 1){
                                kick.toggle(true);
                            }
                            if(buttonIndex == 2){
                                punch.toggle(true);
                            }
                            return super.buttonDown(controller, buttonIndex);
                        }

                        @Override
                        public boolean buttonUp(Controller controller, int buttonIndex) {
                            if(buttonIndex == 0){
                                jump.toggle(false);
                            }
                            if(buttonIndex == 1){
                                kick.toggle(false);
                            }
                            if(buttonIndex == 2){
                                punch.toggle(false);
                            }
                            return super.buttonUp(controller, buttonIndex);
                        }

                        @Override
                        public boolean axisMoved(Controller controller, int axisIndex, float value) {
                            return super.axisMoved(controller, axisIndex, value);
                        }

                        @Override
                        public boolean povMoved(Controller controller, int povIndex, PovDirection value) {
                            right.toggle(false);
                            left.toggle(false);
                            crouch.toggle(false);
                            if (value == PovDirection.east) {
                                right.toggle(true);
                            }else if (value == PovDirection.west) {
                                left.toggle(true);
                            }
                            if(value == PovDirection.south){
                                crouch.toggle(true);
                            }
                            return super.povMoved(controller, povIndex, value);
                        }

                    });
                }
            }
        }
    }

    public void update(float delta){
        if(GameScreen.players != null) {
            resetTimer += delta;
            if (resetTimer >= GameInputProcessor.BUFFER_RESET_TIME) {
                resetTimer -= GameInputProcessor.BUFFER_RESET_TIME;
                keyBuffer.clear();
            }
        }
        for(int i = 0; i < keys.size(); ++i){
            keys.get(i).update();
        }
    }

    @Override
    public boolean keyDown(int keycode) {
        for(int i = 0; i < keys.size(); ++i){
            keys.get(i).keyDown(keycode);
        }
        return false;
    }

    @Override
    public boolean keyUp(int keycode) {
        for(int i = 0; i < keys.size(); ++i){
            keys.get(i).keyUp(keycode);
        }
        return false;
    }

    @Override
    public boolean keyTyped(char character) {
        return false;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        return false;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        return false;
    }

    @Override
    public boolean scrolled(int amount) {
        return false;
    }


    private void pushState(final Key key, final int state){
        InputState inputState = new InputState();
        inputState.key = key;
        inputState.state = state;
        //keyBuffer.add(inputState);
        this.resetTimer = 0;
    }

    public class Key {

        private final String name;

        private List<Integer> inputs;

        private int presses, absorbs;

        public boolean isPressed = false, isClicked = false;

        public Key(String name, Integer... k){
            this(name);
            this.isPressed = false;
            this.isClicked = false;
        }

        public Key(String name){
            this.name = name;
            this.inputs = new ArrayList<Integer>();
            keys.add(this);
        }

        public void addKey(int key){
            if(this.inputs.contains(key))return;
            this.inputs.add(key);
        }

        public void release(){
            toggle(false);
        }

        public void toggle(boolean down){
            if(isPressed != down){
                isPressed = down;
            }
            if(isPressed){
                presses++;
            }
        }

        public void update(){
            if (absorbs < presses)
            {
                absorbs++;
                this.isClicked = true;
                //
                pushState(this, InputState.STATE_CLICKED);
                //
            }
            else
            {
                this.isClicked = false;
            }
        }

        public void keyDown(int k){
            for(int i = 0; i < inputs.size(); ++i){
                if(inputs.get(i) == k){
                    //pushState(this, InputState.STATE_PRESSED);
                    toggle(true);
                }
            }
        }

        public void keyUp(int k){
            for(int i = 0; i < inputs.size(); ++i){
                if(inputs.get(i) == k){
                    pushState(this, InputState.STATE_RELEASED);
                    toggle(false);
                }
            }
        }

        public String getName(){
            return this.name;
        }
    }

    public class InputState{
        public static final int STATE_RELEASED = 0;
        public static final int STATE_PRESSED = 1;

        public static final int STATE_CLICKED = 2;

        public int state;
        public Key key;

    }
}
