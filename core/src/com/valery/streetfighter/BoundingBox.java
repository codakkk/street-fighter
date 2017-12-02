package com.valery.streetfighter;

/**
 * Created by Ciro on 23/11/2016.
 */
public class BoundingBox {

    private float x, y;

    private float width, height;

    private boolean active;

    public BoundingBox(float x, float y, float width, float height){
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.active = true;
    }

    public BoundingBox(float width, float height){
        this(0, 0, width, height);
    }
    public BoundingBox(BoundingBox b){
        this(b.getX(), b.getY(), b.getWidth(), b.getHeight());
    }

    /**
     *
     * @param b
     * @param add tells if must add b's position to this position
     * @return instance chaining
     */
    public BoundingBox apply(BoundingBox b, boolean add){
        if(b == null)return this;
        if(add){
            this.x = this.x + b.getX();
            this.y = this.y + b.getY();
        }
        this.width = b.getWidth();
        this.height = b.getHeight();
        return this;
    }

    public boolean compareSize(BoundingBox b){
        if(b == null)return false;
        return b.getWidth() == this.width && b.getHeight() == this.height;
    }

    public boolean overlaps(float bx, float by, float bwidth, float bheight){
        return this.x < bx + bwidth && this.x + this.width > bx && this.y < by + bheight && this.y + this.height > by;
    }

    public boolean overlaps(BoundingBox b){
        return this.overlaps(b.getX(), b.getY(), b.getWidth(), b.getHeight());
    }

    public BoundingBox set(float x, float y, float width, float height){
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        return this;
    }

    public BoundingBox setPosition(float x, float y){
        this.x = x;
        this.y = y;
        return this;
    }

    public void setX(float x){
        this.x = x;
    }

    public float getX(){
        return this.x;
    }

    public void setY(float y){
        this.y = y;
    }

    public float getY(){
        return this.y;
    }

    public void setWidth(float width){
        this.width = width;
    }

    public float getWidth(){
        return this.width;
    }

    public void setHeight(float height){
        this.height = height;
    }

    public float getHeight(){
        return this.height;
    }

    public boolean isActive(){
        return this.active;
    }

    public void setActive(boolean active){
        this.active = active;
    }
}
