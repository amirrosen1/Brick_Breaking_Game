package bricker.brick_strategies;

import danogl.GameObject;
import danogl.collisions.GameObjectCollection;
import danogl.collisions.Layer;
import danogl.util.Counter;

/**
 * This class is a part of the strategy pattern. It is used to implement a strategy that
 * requires two different behaviors to be executed when a collision occurs.
 *
 * @author Avital Harel & Amir Rosengarten
 */
public class DoubleBehaviorCollisionStrategy implements CollisionStrategy{

    CollisionStrategy collisionStrategy1;
    CollisionStrategy collisionStrategy2;
    private final GameObjectCollection gameObjects;
    private final Counter brickCounter;
    private boolean wasBrickHit = false;
    private boolean isRequiredToCheckCollision;

    /**
     * Constructor for the strategy.
     * @param strategy1 the first strategy
     * @param strategy2 the second strategy
     * @param brickCounter the counter for the bricks
     * @param isRequiredToCheckCollision whether the collision should be checked
     * @param gameObjects the game objects
     */
    public DoubleBehaviorCollisionStrategy(CollisionStrategy strategy1,
                                           CollisionStrategy strategy2, Counter brickCounter,
                                           boolean isRequiredToCheckCollision,
                                           GameObjectCollection gameObjects) {
        this.collisionStrategy1 = strategy1;
        this.collisionStrategy2 = strategy2;
        this.gameObjects = gameObjects;
        this.brickCounter = brickCounter;
        this.isRequiredToCheckCollision = isRequiredToCheckCollision;
    }

    /**
     * This method is called when a collision occurs.
     * @param current the current game object (brick)
     * @param other the other game object
     */
    @Override
    public void onCollision(GameObject current, GameObject other) {
        if(!wasBrickHit && isRequiredToCheckCollision){
            gameObjects.removeGameObject(current, Layer.STATIC_OBJECTS);
            brickCounter.decrement();
            wasBrickHit = true;
            collisionStrategy1.onCollision(current, other);
            collisionStrategy2.onCollision(current, other);
        }
    }
}
