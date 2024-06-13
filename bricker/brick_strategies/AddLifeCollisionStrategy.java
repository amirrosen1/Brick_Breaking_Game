package bricker.brick_strategies;

import bricker.gameobjects.Heart;
import danogl.GameObject;
import danogl.collisions.GameObjectCollection;
import danogl.collisions.Layer;
import danogl.gui.rendering.Renderable;
import danogl.util.Counter;
import danogl.util.Vector2;

/**
 * A strategy for adding a life to the game when a collision occurs with a brick.
 *
 * @author Avital Harel & Amir Rosengarten
 */
public class AddLifeCollisionStrategy implements CollisionStrategy{

    private static final float INITIAL_SPEED_X = 0;
    private static final float INITIAL_SPEED_Y = 100;
    private final GameObjectCollection gameObjects;
    private final Counter livesCounter;
    private final Vector2 widgetTopLeftCorner;
    private final Vector2 widgetDimensions;
    private final Renderable widgetRenderable;
    private final Counter strikesCounter;
    private boolean isRequiredToCheckCollision;
    private boolean wasBrickHit = false;
    private Counter brickCounter;

    /**
     * Construct a new AddLifeCollisionStrategy instance.
     *
     * @param gameObjects The collection of GameObjects in the game.
     * @param brickCounter The counter representing the number of bricks in the game.
     * @param widgetTopLeftCorner Position of the object, in window coordinates (pixels).
     *                           Note that (0,0) is the top-left corner of the window.
     * @param widgetDimensions Width and height in window coordinates.
     * @param livesCounter The counter representing the number of lives.
     * @param widgetRenderable The renderable representing the object. Can be null, in which case
     *                         the GameObject will not be rendered.
     * @param strikesCounter The counter representing the number of strikes.
     * @param isRequiredToCheckCollision True if the collision should be checked, false otherwise.
     */
    public AddLifeCollisionStrategy(GameObjectCollection gameObjects, Counter brickCounter,
                                    Vector2 widgetTopLeftCorner, Vector2 widgetDimensions,
                                    Counter livesCounter, Renderable widgetRenderable,
                                    Counter strikesCounter, boolean isRequiredToCheckCollision) {
        this.gameObjects = gameObjects;
        this.livesCounter = livesCounter;
        this.widgetTopLeftCorner = widgetTopLeftCorner;
        this.widgetDimensions = widgetDimensions;
        this.widgetRenderable = widgetRenderable;
        this.strikesCounter = strikesCounter;
        this.isRequiredToCheckCollision = isRequiredToCheckCollision;
        this.brickCounter = brickCounter;
    }

    /**
     * Called when a collision occurs with another GameObject.
     * @param current The GameObject with which a collision occurred.
     * @param other The other GameObject.
     */
    @Override
    public void onCollision(GameObject current, GameObject other) {
        if(!wasBrickHit && isRequiredToCheckCollision){
            gameObjects.removeGameObject(current, Layer.STATIC_OBJECTS);
            wasBrickHit = true;
            brickCounter.decrement();
        }
        Vector2 position = current.getCenter();
        Heart heart = new Heart(widgetTopLeftCorner, widgetDimensions, widgetRenderable,
                livesCounter,gameObjects,strikesCounter);
        heart.setCenter(position);
        heart.setVelocity(new Vector2(INITIAL_SPEED_X, INITIAL_SPEED_Y));
        gameObjects.addGameObject(heart);
    }
}
