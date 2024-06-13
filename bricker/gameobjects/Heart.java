package bricker.gameobjects;

import danogl.GameObject;
import danogl.collisions.Collision;
import danogl.collisions.GameObjectCollection;
import danogl.gui.rendering.Renderable;
import danogl.util.Counter;
import danogl.util.Vector2;

/**
 * A heart in the game.
 *
 * @author Avital Harel & Amir Rosengarten
 */
public class Heart extends GameObject {

    private Counter livesCounter;
    private Counter strikesCounter;
    private GameObjectCollection gameObjects;

    /**
     * Construct a new GameObject instance.
     *
     * @param heartTopLeftCorner Position of the object, in window coordinates (pixels).
     *                           Note that (0,0) is the top-left corner of the window.
     * @param heartDimensions    Width and height in window coordinates.
     * @param heartRenderable    The renderable representing the object. Can be null, in which case
     *                           the GameObject will not be rendered.
     * @param livesCounter       The counter representing the number of lives.
     * @param gameObjects        The collection of GameObjects in the game.
     */
    public Heart(Vector2 heartTopLeftCorner,
                 Vector2 heartDimensions,
                 Renderable heartRenderable, Counter livesCounter, GameObjectCollection gameObjects
            , Counter strikesCounter) {
        super(heartTopLeftCorner, heartDimensions, heartRenderable);
        this.livesCounter = livesCounter;
        this.gameObjects = gameObjects;
        this.strikesCounter = strikesCounter;
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
        if (strikesCounter.value() <= 4 && livesCounter.value() < 4) {
            livesCounter.increment();
            gameObjects.removeGameObject(this);
        }
    }

    /**
     * Called when a collision ends with another GameObject.
     *
     * @param other The other GameObject.
     * @return True if the GameObject should continue to collide with the other GameObject, false otherwise.
     */
    @Override
    public boolean shouldCollideWith(GameObject other) {

        return other.getTag().equals("Original Paddle");
    }
}
