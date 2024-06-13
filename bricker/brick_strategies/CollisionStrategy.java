package bricker.brick_strategies;

import danogl.GameObject;

/**
 * A strategy for handling a collision with a brick.
 * This class is responsible for handling a collision with a brick.
 * It is responsible for removing the brick from the game and updating the brick counter.
 *
 * @author Avital Harel & Amir Rosengarten
 */
public interface CollisionStrategy {

    /**
     * Called when a collision occurs with another GameObject.
     * @param current The GameObject with which a collision occurred.
     * @param other The other GameObject.
     */
    void onCollision(GameObject current, GameObject other);
}
