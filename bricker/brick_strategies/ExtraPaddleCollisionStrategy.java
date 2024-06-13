package bricker.brick_strategies;

import bricker.gameobjects.ExtraPaddle;
import danogl.GameObject;
import danogl.collisions.GameObjectCollection;
import danogl.collisions.Layer;
import danogl.gui.UserInputListener;
import danogl.gui.rendering.Renderable;
import danogl.util.Counter;
import danogl.util.Vector2;

/**
 * A strategy for adding an extra paddle to the game when a collision occurs with a brick.
 * This class is responsible for adding an extra paddle to the game when a collision occurs with a brick.
 * It is responsible for removing the brick from the game and updating the brick counter.
 *
 * @author Avital Harel & Amir Rosengarten
 */
public class ExtraPaddleCollisionStrategy implements CollisionStrategy {

    private static final String TAG_EXTRA_PADDLE = "Extra Paddle";
    private final GameObjectCollection gameObjects;
    private Vector2 paddleInitLocation;
    private Vector2 paddleDimensions;
    private Renderable paddleRenderable;
    private UserInputListener paddleInputListener;
    private Vector2 windowDimensions;
    private int minDistFromEdge;
    private boolean wasBrickHit = false;
    private boolean isRequiredToCheckCollision;
    private final Counter brickCounter;

    /**
     * Construct a new ExtraPaddleCollisionStrategy instance.
     *
     * @param topLeftCorner Position of the object, in window coordinates (pixels).
     *                      Note that (0,0) is the top-left corner of the window.
     * @param dimensions Width and height in window coordinates.
     * @param renderable The renderable representing the object. Can be null, in which case
     *                   the GameObject will not be rendered.
     * @param inputListener The input listener for the paddle.
     * @param windowDimensions The dimensions of the window.
     * @param minDistFromEdge The minimum distance from the edge of the window.
     * @param gameObjects The collection of GameObjects in the game.
     * @param brickCounter The counter representing the number of bricks in the game.
     * @param isRequiredToCheckCollision True if the collision should be checked, false otherwise.
     */
    public ExtraPaddleCollisionStrategy(Vector2 topLeftCorner, Vector2 dimensions, Renderable renderable,
                                        UserInputListener inputListener, Vector2 windowDimensions,
                                        int minDistFromEdge, GameObjectCollection gameObjects,
                                        Counter brickCounter, boolean isRequiredToCheckCollision) {
        this.gameObjects = gameObjects;
        this.paddleInitLocation = topLeftCorner;
        this.paddleDimensions = dimensions;
        this.paddleRenderable = renderable;
        this.paddleInputListener = inputListener;
        this.windowDimensions = windowDimensions;
        this.minDistFromEdge = minDistFromEdge;
        this.isRequiredToCheckCollision = isRequiredToCheckCollision;
        this.brickCounter = brickCounter;
    }

    /**
     * Check if an extra paddle is in the game.
     * @return True if an extra paddle is in the game, false otherwise.
     */
    private boolean checkIfExtraPaddleInGame() {
        for (GameObject obj : gameObjects.objectsInLayer(Layer.DEFAULT)) {
            if (obj.getTag().equals(TAG_EXTRA_PADDLE)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Called when a collision occurs with another GameObject.
     * @param current The GameObject with which a collision occurred.
     * @param other The other GameObject.
     */
    @Override
    public void onCollision(GameObject current, GameObject other) {
        if (!wasBrickHit && isRequiredToCheckCollision) {
            gameObjects.removeGameObject(current, Layer.STATIC_OBJECTS);
            brickCounter.decrement();
            wasBrickHit = true;
        }
        if (!checkIfExtraPaddleInGame()) {
            GameObject extraPaddle = new ExtraPaddle(paddleInitLocation, paddleDimensions,
                    paddleRenderable, paddleInputListener, windowDimensions,
                    minDistFromEdge, gameObjects);
            Vector2 paddleInitLocation = new Vector2(windowDimensions.x() / 2,
                    windowDimensions.y() / 2);
            extraPaddle.setCenter(paddleInitLocation);
            extraPaddle.setTag(TAG_EXTRA_PADDLE);
            gameObjects.addGameObject(extraPaddle);
        }
    }
}
