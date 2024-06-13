package bricker.brick_strategies;

import danogl.GameObject;
import danogl.collisions.GameObjectCollection;
import danogl.collisions.Layer;
import danogl.util.Counter;

/**
 * A strategy for handling a collision with a brick.
 * This class is responsible for handling a collision with a brick.
 * It is responsible for removing the brick from the game and updating the brick counter.
 *
 * @author Avital Harel & Amir Rosengarten
 */
public class BasicCollisionStrategy implements CollisionStrategy{

    private final GameObjectCollection gameObjects;
    private final Counter brickCounter;
    private boolean wasBrickHit = false;
    private boolean isRequiredToCheckCollision;

    /**
     * Construct a new BasicCollisionStrategy instance.
     *
     * @param gameObjects The collection of GameObjects in the game.
     * @param brickCounter The counter representing the number of bricks in the game.
     * @param isRequiredToCheckCollision True if the collision should be checked, false otherwise.
     */
    public BasicCollisionStrategy(GameObjectCollection gameObjects,Counter brickCounter,
                                  boolean isRequiredToCheckCollision) {
        this.gameObjects = gameObjects;
        this.brickCounter = brickCounter;
        this.isRequiredToCheckCollision = isRequiredToCheckCollision;
    }

    /**
     * Called when a collision occurs with another GameObject.
     * @param current The GameObject with which a collision occurred.
     * @param other The other GameObject.
     */
    @Override
    public void onCollision(GameObject current, GameObject other) {
        gameObjects.removeGameObject(current, Layer.STATIC_OBJECTS);
        if(!wasBrickHit && isRequiredToCheckCollision){
            brickCounter.decrement();
            wasBrickHit = true;
        }
    }
}
