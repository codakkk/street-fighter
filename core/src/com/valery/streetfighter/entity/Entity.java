package com.valery.streetfighter.entity;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.valery.streetfighter.*;
import com.valery.streetfighter.animation.*;
import com.valery.streetfighter.screens.GameScreen;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Ciro on 17/11/2016.
 */
public class Entity {

    public static final int FLOOR_LIMIT = 16;

    private Vector2 position;

    protected float forceY;

    protected float velocityX;
    protected float velocityY;

    protected int xKnockback;

    protected int width, height;

    public static final BoundingBox standingBoundingBox = new BoundingBox(40, 70);
    public static final BoundingBox crouchingBoundingBox = new BoundingBox(40, 40);

    public static final BoundingBox standingPunchBoundingBox = new BoundingBox(standingBoundingBox.getWidth(), standingBoundingBox.getHeight() - 15, 15, 15);
    public static final BoundingBox crouchingPunchBoundingBox = new BoundingBox(crouchingBoundingBox.getWidth(), crouchingBoundingBox.getHeight()/2 + 5, 15, 15);

    public static final BoundingBox standingKickBoundingBox = new BoundingBox(crouchingBoundingBox.getWidth() + 12, crouchingBoundingBox.getHeight()+20, 18, 18);
    public static final BoundingBox crouchingKickBoundingBox = new BoundingBox(crouchingBoundingBox.getWidth(), 0, 15, 15);

    public static final BoundingBox inairPunchBoundingBox = new BoundingBox(crouchingBoundingBox.getWidth()+5, 20, 8, 8);

    public BoundingBox currentBoundingBox;

    public BoundingBox hitBoundingBox;

    public enum State {
        STANDING_IDLE, STANDING_LEFT_PUNCH, STANDING_RIGHT_PUNCH, STANDING_KICK, STANDING_SHIELD, STANDING_STUN,
        WALKING,
        IN_AIR, IN_AIR_KICK, IN_AIR_PUNCH,
        CROUCHING, CROUCHING_PUNCH, CROUCHING_KICK, CROUCHING_SHIELD,
        BODY_HIT, FACE_HIT, CROUCH_HIT,
        FORWARD_JUMP, BACKWARD_JUMP,
        VICTORY, DEATH
    }
    private State state = State.STANDING_IDLE;
    private State oldState = State.STANDING_IDLE;

    public enum Facing {
        LEFT, RIGHT
    }
    private Facing facing = Facing.RIGHT;

    public AnimationHandler animationHandler;

    public GameInputProcessor input;

    public int count = 0;

    public int invulnerableTime = 0;

    public int health = 100;

    public int shieldCount;
    private int stunTick;

    public static int ID = 0;

    public final int id ;

    public TextureRegion smallThumbnail, bigThumbnail;

    public Entity(){
        this.id = ID;
        this.position = new Vector2();
        this.animationHandler = new AnimationHandler (this);
        //loadAnimations();
        this.width = 38;
        this.height = 80;
        this.currentBoundingBox = new BoundingBox(standingBoundingBox);
        this.hitBoundingBox = new BoundingBox(0, 0);
        this.hitBoundingBox.setActive(false);
        this.position.y = Entity.FLOOR_LIMIT;
        input = new GameInputProcessor(id, true);
        this.setState(State.STANDING_IDLE);
        ID++;
    }

    public void reset(){
        this.currentBoundingBox = new BoundingBox(standingBoundingBox);
        this.hitBoundingBox = new BoundingBox(0, 0);
        this.hitBoundingBox.setActive(false);
        this.position.y = Entity.FLOOR_LIMIT;
        shieldCount = 0;
        stunTick = 0;
        health = 100;
        invulnerableTime = 0;
        count = 0;
        forceY = 0;
        velocityX = 0;
        velocityY = 0;
        xKnockback = 0;
        //Sets start position
        if(id == 0){
            setPosition(0, Entity.FLOOR_LIMIT);
        } else if(id == 1){
            setPosition(GameScreen.WORLD_WIDTH - 40, Entity.FLOOR_LIMIT);
        }
        this.setState(State.STANDING_IDLE);
    }

    public void handleStates(){
        switch(getState()){
            case STANDING_IDLE:
            {
                if(input.crouch.isPressed){
                    setState(State.CROUCHING);
                }
                if(input.punch.isClicked){
                    punch();
                }
                if(input.jump.isClicked){
                    jump();
                }
                if(input.kick.isClicked){
                    kick();
                }
                if(input.shield.isPressed){
                    shield();
                }
                break;
            }
            case WALKING:
            {
                if(input.punch.isClicked){
                    punch();
                }
                if(input.kick.isClicked){
                    kick();
                }
                if(input.jump.isClicked){
                    jump();
                }
            }
            case IN_AIR:
            {
                if(input.punch.isClicked){
                    punch();
                }
                if(input.kick.isClicked){
                    kick();
                }
                break;
            }
        }
    }

    public void updateGravity(float delta){
        boolean isGrounded = this.isGrounded();
        if(this.forceY > 0 ){
            this.forceY--;
            velocityY += 32 * delta;
            //if(velocityY > 5)velocityY = 5;
            System.out.println(velocityY);
        }

        if(!isGrounded)this.velocityY -= 16 * delta;
        if(isInAirAction()){
            if(canAct())setState(State.IN_AIR);
        }
        if(this.velocityY != 0)this.moveY(velocityY);
        if(this.position.y < Entity.FLOOR_LIMIT)this.position.y = Entity.FLOOR_LIMIT;
        if(this.isInAir()){
            if(isGrounded()){
                this.setState(State.STANDING_IDLE);
                this.velocityY = 0;
            }
        }

    }

    public void update(float delta){
        this.animationHandler.update(delta);
        if(input != null)input.update(delta);
        if(getState() == State.VICTORY || getState() == State.DEATH){

            return;
        }


        if(canAct() && isGrounded())setState(State.STANDING_IDLE);

        this.velocityX = 0;
        if(input.left.isPressed){
            this.velocityX = -1;
        }
        else if(input.right.isPressed){
            this.velocityX = 1;
        }

        if(xKnockback != 0){
            if(xKnockback < 0){
                xKnockback++;
                velocityX = -1;
            }
            else if(xKnockback > 0){
                xKnockback--;
                velocityX = 1;
            }
        }

        if(velocityX != 0 ){
            if(getFacing() == Facing.RIGHT){
                if(velocityX > 0)this.moveX(velocityX * 4f * 32 * delta);
                else if(velocityX < 0)this.moveX(velocityX * 2.2f * 32 * delta);
            }else if(getFacing() == Facing.LEFT){
                if(velocityX < 0)this.moveX(velocityX * 4f * 32 * delta);
                else if(velocityX > 0)this.moveX(velocityX * 2.2f * 32 * delta);
            }
        }

        handleStates();

        //HIT
        if(!canAct()){
            if(hitBoundingBox.isActive()){
                for(int i = 0; i < GameScreen.players.size(); ++i) {
                    Entity p = GameScreen.players.get(i);
                    if(p == this) continue;
                    if(p.invulnerableTime > 0)continue;
                    if(hitBoundingBox.overlaps(p.currentBoundingBox)){
                        hitBoundingBox.setActive(false);
                        if(p.getState() == this.getState()){
                            continue;
                        }
                        p.hit(this);
                        break;
                    }
                }
            }
        }
        //UPDATE FACING
        for(int i = 0; i < GameScreen.players.size(); ++i){
            Entity player = GameScreen.players.get(i);
            if(player == this)continue;
            if(player.getPosition().x > this.getPosition().x)this.facing = Facing.RIGHT;
            else this.facing = Facing.LEFT;
        }
        if(invulnerableTime > 0)invulnerableTime--;
        if(stunTick > 0){
            stunTick--;
            if(stunTick <= 0){
                setState(State.STANDING_IDLE);
            }
        }

        this.currentBoundingBox.setPosition(this.position.x, this.position.y);
        updateBoundingBox();
        //
    }

    public void render(SpriteBatch spriteBatch){
        updateAnimation();
        TextureRegion frame = this.animationHandler.getCurrentFrame();

        if (frame != null) {
            int width = frame.getRegionWidth();
            int height = frame.getRegionHeight();
            if(getFacing() == Facing.RIGHT)spriteBatch.draw(frame, this.getPosition().x, this.getPosition().y, width, height);
            else spriteBatch.draw(frame, this.getPosition().x + this.getWidth(), this.getPosition().y, -width, height);
        }
    }

    public void renderDebug(ShapeRenderer sr){

        sr.begin(ShapeRenderer.ShapeType.Line);
        sr.rect(currentBoundingBox.getX(), currentBoundingBox.getY(), currentBoundingBox.getWidth(), currentBoundingBox.getHeight());
        if(hitBoundingBox.isActive())sr.rect(hitBoundingBox.getX(), hitBoundingBox.getY(), hitBoundingBox.getWidth(), hitBoundingBox.getHeight());

        sr.end();
    }

    public boolean canAct(){
        return count <= 0;// && !isStunned();
    }

    public boolean isStunned(){
        return this.getState() == State.STANDING_STUN && stunTick > 0;
    }

    /**
     *
     * @return true if the entity is in one of the standing state.
     */
    public boolean isStanding(){
        return getState() == State.STANDING_IDLE || getState() == State.STANDING_SHIELD || getState() == State.STANDING_STUN || getState() == State.STANDING_KICK || getState() == State.STANDING_LEFT_PUNCH || getState() == State.WALKING;
    }

    public boolean isPunching(){
        return getState() == State.STANDING_LEFT_PUNCH || getState() == State.STANDING_RIGHT_PUNCH;
    }

    public boolean isCrouching(){
        return getState() == State.CROUCHING || getState() == State.CROUCHING_SHIELD || getState() == State.CROUCHING_KICK || getState() == State.CROUCHING_PUNCH || getState() == State.CROUCH_HIT;
    }

    public boolean isInAir(){
        return getState() == State.IN_AIR || getState() == State.IN_AIR_KICK || getState() == State.IN_AIR_PUNCH;
    }


    /**
     *
     * @return returns if the entity is doing an action in air (like IN_AIR_KICK, IN_AIR_PUNCH)
     */
    public boolean isInAirAction(){
        return getState() == State.IN_AIR_KICK || getState() == State.IN_AIR_PUNCH;
    }

    ////////////////////////////////////////////

    public void punch(){
        //if(!canAct())return;
        if(getState() == State.STANDING_LEFT_PUNCH || getState() == State.CROUCHING_PUNCH)return;
        if(isStanding()){
            if(getOldState() == State.STANDING_LEFT_PUNCH)setState(State.STANDING_RIGHT_PUNCH);
            else setState(State.STANDING_LEFT_PUNCH);
        }
        else if(isCrouching())setState(State.CROUCHING_PUNCH);
        else if(isInAir())setState(State.IN_AIR_PUNCH);
    }

    public void kick(){
        //if(!canAct())return;
        if(getState() == State.STANDING_KICK || getState() == State.CROUCHING_KICK)return;
        if(getState() == State.STANDING_IDLE || getState() == State.WALKING)setState(State.STANDING_KICK);
        else if(getState() == State.CROUCHING)setState(State.CROUCHING_KICK);
        else if(getState() == State.IN_AIR)setState(State.IN_AIR_KICK);
    }

    public void jump(){
        if(this.forceY > 0)return;
        if(isGrounded()){
            this.forceY = 6;
            this.velocityY = 0;
            setState(State.IN_AIR);
        }
    }

    public void crouch(){
        setState(State.CROUCHING);
    }

    public void shield(){
        if(!canAct())return;
        if(getState() == State.CROUCHING){
            setState(State.CROUCHING_SHIELD);
        } else if(getState() == State.STANDING_IDLE){
            setState(State.STANDING_SHIELD);
        }
    }

    public void stun(int timeInTicks){
        if(timeInTicks > 0){
            this.stunTick = timeInTicks;
            setState(State.STANDING_STUN);
        }
    }

    public void hit(Entity e){
        if(this.getState() == State.STANDING_SHIELD || this.getState() == State.CROUCHING_SHIELD){
            if(this.getState() == State.STANDING_SHIELD){

            }
            shieldCount++;
            if(shieldCount > 3){
                stun(25 * 2);
                shieldCount = 0;
            }
        }else {
            if(getState() == State.CROUCHING){
                setState(State.CROUCH_HIT);
            }else {
                State state = e.getState();
                switch (state) {
                    case CROUCHING:
                    case CROUCHING_PUNCH:
                    case CROUCHING_KICK:
                    case CROUCH_HIT: {
                        this.setState(State.BODY_HIT);
                        break;
                    }
                    case STANDING_LEFT_PUNCH:
                    case STANDING_KICK:
                    case IN_AIR_KICK:
                    case IN_AIR_PUNCH: {
                        this.setState(State.FACE_HIT);
                        break;
                    }
                    default: {
                        this.setState(State.BODY_HIT);
                        break;
                    }
                }
            }
            this.health -= 2 + MathUtils.random(10);
            if(getFacing() == Facing.LEFT)xKnockback = 6;
            else if(getFacing() == Facing.RIGHT)xKnockback = -6;
        }
        this.invulnerableTime = 10;
        updateAnimation();
    }

    ////////////////////////////////////////////


    public int getWidth(){
        return this.width;
    }

    public int getHeight(){
        return this.height;
    }

    public void setState(State state){
        if(this.state != state){
            this.oldState = this.state;
        }
        this.state = state;
        count = 0;
    }

    protected void updateBoundingBox(){
        BoundingBox shouldBB = standingBoundingBox;
        BoundingBox hitBB = standingPunchBoundingBox;
        switch(getState()) {
            case STANDING_IDLE:
            case STANDING_KICK:
            case STANDING_LEFT_PUNCH:
            case BODY_HIT:
            case FACE_HIT:
            case IN_AIR:
            case WALKING:
            {
                shouldBB = standingBoundingBox;
                if(getState() == State.STANDING_LEFT_PUNCH)hitBB = standingPunchBoundingBox;
                else if(getState() == State.STANDING_KICK)hitBB = standingKickBoundingBox;
                break;
            }
            case CROUCHING:
            case CROUCHING_KICK:
            case CROUCHING_PUNCH:
            case CROUCH_HIT:
            {
                shouldBB = crouchingBoundingBox;
                if(getState() == State.CROUCHING_PUNCH)hitBB = crouchingPunchBoundingBox;
                else if(getState() == State.CROUCHING_KICK)hitBB = crouchingKickBoundingBox;
                break;
            }
            case IN_AIR_KICK:
            case IN_AIR_PUNCH:
            {
                hitBB = inairPunchBoundingBox;
                break;
            }
            default:
            {
                shouldBB = standingBoundingBox;
                hitBB = standingPunchBoundingBox;
                break;
            }
        }
        if(!this.currentBoundingBox.compareSize(shouldBB))this.currentBoundingBox.apply(shouldBB, false);
        if(hitBB != null){
            if(getFacing() == Facing.RIGHT) {
                this.hitBoundingBox.setPosition(this.position.x + hitBB.getX(), this.position.y + hitBB.getY());
                this.hitBoundingBox.apply(hitBB, false);
            }
            else if(getFacing() == Facing.LEFT){
                this.hitBoundingBox.setPosition(this.position.x -this.hitBoundingBox.getWidth(), this.position.y + hitBB.getY());
                this.hitBoundingBox.setWidth(-this.hitBoundingBox.getWidth());
                this.hitBoundingBox.apply(hitBB, false);
            }
                //
        }
    }

    protected void updateAnimation(){
        this.animationHandler.setAnimation(getState());
    }

    public State getState(){
        return this.state;
    }

    public State getOldState(){
        return this.oldState;
    }

    public Facing getFacing(){
        return this.facing;
    }

    public void moveY(float y){
        if(this.position.y < FLOOR_LIMIT)this.position.y = FLOOR_LIMIT;
        else if(this.position.y > Gdx.graphics.getHeight()-this.getHeight())this.position.y = Gdx.graphics.getHeight()-this.getHeight();
        for(int i = 0; i < GameScreen.players.size(); ++i) {
            Entity enemy = GameScreen.players.get(i);
            if(enemy == this)continue;
            if(y < 0){
                //if(isOnHead(enemy) || (this.position.y <= 0))y = 0;
                if(isGrounded())y = 0;
                if(isOnHead(enemy))this.setPosition(this.getPosition().x, enemy.getPosition().y + enemy.getHeight() + 1);
            }
        }
        this.position.add(0, y);
    }

    public boolean moveX(float x){
        if(x == 0){
        	setState(State.STANDING_IDLE);
        	return false;
        }
        if(x != 0){
            if(isGrounded() && canAct())setState(State.WALKING);
            //else setState(State.STANDING_IDLE);
            //if(x > 0)facing = Facing.RIGHT;
            //else if(x < 0)facing = Facing.LEFT;
        }
        if(this.position.x < 0)this.position.x = 0;
        else if(this.position.x > GameScreen.WORLD_WIDTH-this.getWidth())this.position.x = GameScreen.WORLD_WIDTH-this.getWidth();
        for(int i = 0; i < GameScreen.players.size(); ++i) {
            Entity player = GameScreen.players.get(i);
            if(player == this)continue;
            if(player.currentBoundingBox.overlaps(this.currentBoundingBox.getX()+x, this.currentBoundingBox.getY(), this.currentBoundingBox.getWidth(), this.currentBoundingBox.getHeight())){
                x = 0;
            }
        }
        this.position.add(x, 0);
        return true;
    }


    public Vector2 getPosition(){
        return this.position;
    }

    public void setPosition(float x, float y){
        this.position.x = x;
        this.position.y = y;
    }

    public boolean isOnHead(Entity entity){
        if (this.currentBoundingBox.getX() + this.currentBoundingBox.getWidth() > entity.currentBoundingBox.getX() && this.currentBoundingBox.getX()  < entity.currentBoundingBox.getX() + entity.currentBoundingBox.getHeight()) {
            if (this.currentBoundingBox.getY() > this.currentBoundingBox.getY()+entity.currentBoundingBox.getHeight() && this.currentBoundingBox.getY() < entity.currentBoundingBox.getY() + entity.currentBoundingBox.getHeight() + 12) {
                return true;
            }
        }
        return false;
    }

    public boolean isGrounded(){
        boolean t = false;
        if(this.getPosition().y <= FLOOR_LIMIT)t = true;
        if(!t){
            for(int i = 0; i < GameScreen.players.size(); ++i){
                t = this.isOnHead(GameScreen.players.get(i));
                if(t)break;
            }
        }
        return t;
    }
}
