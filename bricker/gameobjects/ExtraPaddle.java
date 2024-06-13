package bricker.gameobjects;

import danogl.GameObject;
import danogl.collisions.Collision;
import danogl.collisions.GameObjectCollection;
import danogl.gui.UserInputListener;
import danogl.gui.rendering.Renderable;
import danogl.util.Counter;
import danogl.util.Vector2;

/**
 * An Extra paddle in the game.
 *
 * @author Avital Harel & Amir Rosengarten
 */
public class ExtraPaddle extends Paddle {

    private final Counter collisionCounter;
    private final GameObjectCollection gameObjects;

    /**
     * Construct a new GameObject instance.
     *
     * @param topLeftCorner    Position of the object, in window coordinates (pixels).
     *                         Note that (0,0) is the top-left corner of the window.
     * @param dimensions       Width and height in window coordinates.
     * @param renderable       The renderable representing the object. Can be null, in which case
     *                         the GameObject will not be rendered.
     * @param inputListener    The input listener to use for this GameObject.
     * @param windowDimensions The dimensions of the window in which this GameObject is rendered.
     * @param minDistFromEdge  The minimum distance from the edge of the window that this GameObject
     *                         should be rendered at.
     * @param gameObjects      The collection of GameObjects in the game.
     */
    public ExtraPaddle(Vector2 topLeftCorner, Vector2 dimensions, Renderable renderable,
                       UserInputListener inputListener, Vector2 windowDimensions,
                       int minDistFromEdge, GameObjectCollection gameObjects) {
        super(topLeftCorner, dimensions, renderable, inputListener, windowDimensions, minDistFromEdge);
        this.collisionCounter = new Counter();
        this.gameObjects = gameObjects;
    }

    /**
     * Called when a collision occurs with another GameObject.
     *
     * @param other     The GameObject with which a collision occurred.
     * @param collision Information regarding this collision.
     */
    @Override
    public void onCollisionEnter(GameObject other, Collision collision) {
        super.onCollisionEnter(other, collision);
        collisionCounter.increment();
        if (collisionCounter.value() == 4) {
            gameObjects.removeGameObject(this);
        }
    }
}
