package velocity.util;

import velocity.Rect;

/**
 * Represents a transformation in Velocity.
 */
public class Transform implements Cloneable {
    /**
     * Current object location.
     */
    public Rect location;

    /**
     * Current object rotation.
     */
    public float rotation;

    /**
     * Current object scale.
     */
    public Point scale;

    /**
     * Z layer of the current object.
     */
    public int sortOrder;
    
    /**
     * Create a new transformation representation for a sprite.
     * 
     * @param location Current location.
     * @param rotation Current rotation.
     * @param scale Current scale.
     * @param sortOrder Z sorting layer.
     */
    public Transform(Rect location, float rotation, Point scale, int sortOrder) {
        this.location = location;
        this.rotation = rotation;
        this.scale = scale;
        this.sortOrder = sortOrder;
    }

    /**
     * Create a new transformation representation for a sprite.
     * 
     * @param location Current location (as a point).
     * @param rotation Current rotation.
     * @param scale Current scale.
     * @param sortOrder Z sorting layer.
     */
    public Transform(Point location, float rotation, Point scale, int sortOrder) {
        this.location = new Rect(location, new Point(0, 0));
        this.rotation = rotation;
        this.scale = new Point(scale);
        this.sortOrder = sortOrder;
    } 

    /**
     * Create a new transformation representation for a sprite.
     * Defaults to 0 degrees rotation, 1x scale, and Z layer 0.
     * 
     * @param location Sprite rect.
     */
    public Transform(Rect location) {
        this(location, 0f, new Point(1, 1), 0);
    }

    /**
     * Create a new transformation representation for a sprite.
     * Defaults to 0 degrees rotation, 1x scale, and Z layer 0.
     * 
     * @param location Sprite center position.
     */
    public Transform(Point location) {
        this(new Rect(location, new Point(0, 0)), 0f, new Point(1, 1), 0);
    }

    /**
     * Identity transformation..
     * Defaults to Point.zero 0 degrees rotation, 1x scale, and Z layer 0.
     * 
     */
    public Transform() {
        this(Point.zero);
    }

    /**
     * Create a new transformation representation for a sprite.
     * Defaults to 1x scale and Z layer 0.
     * 
     * @param location Sprite rect.
     * @param rotation Current rotation.
     */
    public Transform(Rect location, float rotation) {
        this(location, rotation, new Point(1, 1), 0);
    }

    /**
     * Create a new transformation representation for a sprite.
     * Defaults to 0 degrees rotation and Z layer 0.
     * 
     * @param location Sprite rect.
     * @param scale Sprite scaling.
     */
    public Transform(Rect location, Point scale) {
        this(location, 0f, scale, 0);
    }

    /**
     * Create a new transformation representation for a sprite.
     * Defaults to 0 degrees rotation and 1x scale.
     * 
     * @param location Sprite rect.
     * @param sortOrder Sprite rendering order.
     */
    public Transform(Rect location, int sortOrder) {
        this(location, 0f, new Point(1, 1), sortOrder);
    }

    /**
     * Create a new transformation representation for a sprite.
     * Defaults to Z layer 0.
     * 
     * @param location Sprite rect.
     * @param rotation Sprite Z rotation.
     * @param scale Sprite scaling.
     */
    public Transform(Rect location, float rotation, Point scale) {
        this(location, rotation, scale, 0);
    }

    /**
     * Update the width and height of the internal rect.
     * 
     * @param bounds The new width and height.
     */
    public void updateRect(Point bounds) {
        this.location.setWH(bounds);
    }

    /**
     * Set the center position of this transform.
     * 
     * @param position New center position.
     */
    public void setPosition(Point position) {
        this.location.setPos(position);
    }

    /**
     * Get this transform's center location.
     * 
     * @return Current center location.
     */
    public Point getPosition() {
        return this.location.getPos();
    }

    /**
     * Translate this transform.
     * 
     * @param delta Distance to move.
     */
    public void translate(Point delta) {
        this.location.translate(delta);
    }

    /**
     * Rotate this transform.
     * 
     * @param delta Angle to rotate.
     */
    public void rotate(float delta) {
        this.rotation += delta;
    }

    /**
     * Rotate this transform.
     * 
     * @param angle Angle to rotate.
     */
    public void setRotation(float angle) {
        this.rotation = angle;
    }

    /**
     * Set this transform's scale.
     * 
     * @param scale New absolute scale.
     */
    public void setScale(Point scale) {
        this.scale = scale;
    }

    /**
     * Set the sorting layer of this transform.
     * 
     * @param zOrder sorting layer.
     */
    public void setZOrder(int zOrder) {
        this.sortOrder = zOrder;
    }

    /**
     * Create an independent copy of this transform.
     */
    @Override
    public Transform clone() {
        return new Transform(location.copy(), rotation, new Point(scale), sortOrder);
    }
}
