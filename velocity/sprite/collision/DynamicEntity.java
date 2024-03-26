package velocity.sprite.collision;

import java.util.ArrayList;

import velocity.sprite.Sprite;

public interface DynamicEntity {
    public static final int DIR_UP = 0;
    public static final int DIR_DOWN = 1;
    public static final int DIR_LEFT = 2;
    public static final int DIR_RIGHT = 3;

    /**
     * Implementers require moveDir field but cannot be put here for functionality reasons.
     * public boolean[] moveDir = new boolean[4];

     * Just allows detecting collisions and trigger hits.
     * Also enables physics.
     */
    public void simCollide(ArrayList<Sprite> others);
}
