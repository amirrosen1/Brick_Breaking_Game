package bricker.brick_strategies;

import danogl.GameObject;
import danogl.collisions.GameObjectCollection;
import danogl.collisions.Layer;
import danogl.gui.rendering.Camera;
import danogl.GameManager;
import bricker.gameobjects.Ball;
import danogl.util.Counter;
import danogl.util.Vector2;
import danogl.gui.WindowController;

/**
 * A strategy for changing the camera when a collision occurs with a brick.
 * This class is responsible for changing the camera when a collision occurs with a brick.
 * It is responsible for removing the brick from the game and updating the brick counter.
 *
 * @author Avital Harel & Amir Rosengarten
 */
public class ChangingCameraCollisionStrategy implements CollisionStrategy {

    private static final String TAG_ORIGINAL_BALL = "Original Ball";
    private final GameManager gameManager;
    private final WindowController windowController;
    private final GameObjectCollection gameObjects;
    private final Ball ball;
    private boolean wasBrickHit = false;
    private boolean isRequiredToCheckCollision;
    private final Counter brickCounter;

    /**
     * Construct a new ChangingCameraCollisionStrategy instance.
     *
     * @param gameObject The collection of GameObjects in the game.
     * @param gameManager The game manager.
     * @param windowController The window controller.
     * @param ball The ball in the game.
     * @param brickCounter The counter representing the number of bricks in the game.
     * @param isRequiredToCheckCollision True if the collision should be checked, false otherwise.
     */
    public ChangingCameraCollisionStrategy(GameObjectCollection gameObject,GameManager gameManager,
                                       WindowController windowController,Ball ball,
                                       Counter brickCounter, boolean isRequiredToCheckCollision){
        this.gameManager = gameManager;
        this.windowController = windowController;
        this.gameObjects = gameObject;
        this.ball = ball;
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
        if (other.getTag().equals(TAG_ORIGINAL_BALL) &&  gameManager.camera() == null){
            ball.resetCollisionCounter();
            gameManager.setCamera(
                    new Camera(other, Vector2.ZERO,
                            windowController.getWindowDimensions().mult(1.2f),
                            windowController.getWindowDimensions()));
        }
    }
}
