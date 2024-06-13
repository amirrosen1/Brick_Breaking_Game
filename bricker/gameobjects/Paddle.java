package bricker.gameobjects;

import danogl.GameObject;
import danogl.gui.UserInputListener;
import danogl.gui.rendering.Renderable;
import danogl.util.Vector2;

import java.awt.event.KeyEvent;

/**
 * A paddle in the game.
 *
 * @author Avital Harel & Amir Rosengarten
 */
public class Paddle extends GameObject {

    private static final float MOVEMENT_SPEED = 200;
    private final UserInputListener inputListener;
    private float screenWidth;
    private int minDistFromEdge;


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
     */
    public Paddle(Vector2 topLeftCorner, Vector2 dimensions, Renderable renderable,
                  UserInputListener inputListener, Vector2 windowDimensions, int minDistFromEdge) {
        super(topLeftCorner, dimensions, renderable);
        this.inputListener = inputListener;
        this.minDistFromEdge = minDistFromEdge;
        this.screenWidth = windowDimensions.x();
    }

    /**
     * Updates the paddle's position based on the input.
     *
     * @param deltaTime The time that has passed since the last update.
     */
    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);
        Vector2 movementDir = Vector2.ZERO;
        if (inputListener.isKeyPressed(KeyEvent.VK_LEFT)) {
            movementDir = movementDir.add(Vector2.LEFT);
        }
        if (inputListener.isKeyPressed(KeyEvent.VK_RIGHT)) {
            movementDir = movementDir.add(Vector2.RIGHT);
        }
        setVelocity(movementDir.mult(MOVEMENT_SPEED));
        int paddleTopLeftCornerX = (int) getTopLeftCorner().x();
        if (paddleTopLeftCornerX < minDistFromEdge || paddleTopLeftCornerX > screenWidth - minDistFromEdge
                - getDimensions().x()) {
            transform().setTopLeftCornerX(Math.max(minDistFromEdge, Math.min(screenWidth - minDistFromEdge
                    - getDimensions().x(), paddleTopLeftCornerX)));
        }
    }
}
