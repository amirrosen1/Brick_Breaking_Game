package bricker.gameobjects;

import danogl.GameObject;
import danogl.collisions.GameObjectCollection;
import danogl.collisions.Layer;
import danogl.gui.rendering.TextRenderable;
import danogl.util.Counter;
import danogl.util.Vector2;

import java.awt.*;

/**
 * A numeric representation of the life counter in the game.
 * <p>
 * This class is responsible for updating the numeric representation of the life counter in the game.
 * It is responsible for updating the color of the numeric representation according to the number of lives.
 *
 * @author Avital Harel & Amir Rosengarten
 */
public class NumericLifeCounter extends GameObject {

    private Counter livesCounter;
    private final GameObjectCollection gameObjects;
    private GameObject numericLifeCounter;
    private Vector2 topLeftCorner;
    private Vector2 dimensions;

    /**
     * Construct a new GameObject instance.
     *
     * @param livesCounter         The counter representing the number of lives.
     * @param topLeftCorner        Position of the object, in window coordinates (pixels).
     *                             Note that (0,0) is the top-left corner of the window.
     * @param dimensions           Width and height in window coordinates.
     * @param gameObjectCollection The collection of GameObjects in the game.
     */
    public NumericLifeCounter(Counter livesCounter, Vector2 topLeftCorner, Vector2 dimensions,
                              GameObjectCollection gameObjectCollection) {
        super(topLeftCorner, dimensions, null);
        this.livesCounter = livesCounter;
        this.gameObjects = gameObjectCollection;
        this.topLeftCorner = topLeftCorner;
        this.dimensions = dimensions;
        createNumericCounter(Color.green);
    }

    /**
     * Creates the numeric representation of the life counter.
     *
     * @param color The color of the numeric representation.
     */
    private void createNumericCounter(Color color) {
        TextRenderable textRenderable = new TextRenderable(String.format("%d", livesCounter.value()));
        textRenderable.setColor(color);
        this.numericLifeCounter = new GameObject(topLeftCorner, dimensions, textRenderable);
        gameObjects.addGameObject(numericLifeCounter, Layer.UI);
    }

    /**
     * Updates the numeric representation of the life counter.
     *
     * @param deltaTime The time that has passed since the last update.
     */
    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);
        if (livesCounter.value() == 3 || livesCounter.value() == 4) {
            gameObjects.removeGameObject(this.numericLifeCounter, Layer.UI);
            createNumericCounter(Color.green);
        } else if (livesCounter.value() == 2) {
            gameObjects.removeGameObject(this.numericLifeCounter, Layer.UI);
            createNumericCounter(Color.yellow);
        } else if (livesCounter.value() == 1) {
            gameObjects.removeGameObject(this.numericLifeCounter, Layer.UI);
            createNumericCounter(Color.red);
        }
    }
}