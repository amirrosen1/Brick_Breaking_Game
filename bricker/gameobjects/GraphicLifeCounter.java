package bricker.gameobjects;

import danogl.GameObject;
import danogl.collisions.GameObjectCollection;
import danogl.collisions.Layer;
import danogl.gui.rendering.Renderable;
import danogl.util.Counter;
import danogl.util.Vector2;

/**
 * A graphic representation of the life counter in the game.
 *
 * @author Avital Harel & Amir Rosengarten
 */
public class GraphicLifeCounter extends GameObject {

    private static final int DISTANCE_BETWEEN_HEARTS = 5;
    private Counter livesCounter;
    private final int numOfLives;
    private int numOfHeartsOnScreen;
    private GameObject[] hearts;
    private final GameObjectCollection gameObjects;
    private int nextHeartToRemoveFromScreenIdx;

    /**
     * Construct a new GameObject instance.
     *
     * @param widgetTopLeftCorner   Position of the object, in window coordinates (pixels).
     *                              Note that (0,0) is the top-left corner of the window.
     * @param widgetDimensions      Width and height in window coordinates.
     * @param livesCounter          The counter representing the number of lives.
     * @param widgetRenderable      The renderable representing the object. Can be null, in which case
     *                              the GameObject will not be rendered.
     * @param gameObjectsCollection The collection of GameObjects in the game.
     * @param numOfLives            The number of lives to represent.
     */
    public GraphicLifeCounter(Vector2 widgetTopLeftCorner, Vector2 widgetDimensions,
                              Counter livesCounter, Renderable widgetRenderable,
                              GameObjectCollection gameObjectsCollection, int numOfLives) {
        super(Vector2.ZERO, Vector2.ZERO, widgetRenderable);
        this.livesCounter = livesCounter;
        this.numOfLives = numOfLives;
        this.gameObjects = gameObjectsCollection;
        this.hearts = new GameObject[numOfLives + 1];
        this.nextHeartToRemoveFromScreenIdx = numOfLives - 1;
        this.numOfHeartsOnScreen = numOfLives;
        addHearts(widgetTopLeftCorner, widgetDimensions, widgetRenderable);
    }

    /**
     * Adds the hearts to the screen.
     *
     * @param heartTopLeftCorner The top left corner of the first heart.
     * @param heartDimensions    The dimensions of the hearts.
     * @param heartRenderable    The renderable representing the hearts.
     */
    private void addHearts(Vector2 heartTopLeftCorner, Vector2 heartDimensions, Renderable heartRenderable) {
        Vector2 curHeartLoc = heartTopLeftCorner;
        for (int i = 0; i < numOfLives; i++) {
            GameObject heart = new GameObject(curHeartLoc, heartDimensions, heartRenderable);
            gameObjects.addGameObject(heart, Layer.UI);
            curHeartLoc = curHeartLoc.add(new Vector2(heartDimensions.x() + DISTANCE_BETWEEN_HEARTS, 0));
            hearts[i] = heart;
        }
        hearts[numOfLives] = new GameObject(curHeartLoc, heartDimensions, heartRenderable);
    }

    /**
     * Updates the life counter.
     *
     * @param deltaTime The time that has passed since the last update.
     */
    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);
        if (this.livesCounter.value() < numOfHeartsOnScreen) {
            GameObject heartToRemove = hearts[nextHeartToRemoveFromScreenIdx];
            gameObjects.removeGameObject(heartToRemove, Layer.UI);
            numOfHeartsOnScreen--;
            nextHeartToRemoveFromScreenIdx--;
        } else if (this.livesCounter.value() > numOfHeartsOnScreen) {
            nextHeartToRemoveFromScreenIdx++;
            GameObject heartToAdd = hearts[nextHeartToRemoveFromScreenIdx];
            gameObjects.addGameObject(heartToAdd, Layer.UI);
            numOfHeartsOnScreen++;
        }
    }
}
