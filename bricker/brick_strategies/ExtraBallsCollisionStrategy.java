package bricker.brick_strategies;

import bricker.gameobjects.Puck;
import danogl.GameObject;
import danogl.collisions.GameObjectCollection;
import danogl.collisions.Layer;
import danogl.gui.Sound;
import danogl.gui.rendering.Renderable;
import danogl.util.Counter;
import danogl.util.Vector2;
import java.util.Random;

/**
 * A strategy for adding extra balls to the game when a collision occurs with a brick.
 * This class is responsible for adding extra balls to the game when a collision occurs with a brick.
 * It is responsible for removing the brick from the game and updating the brick counter.
 *
 * @author Avital Harel & Amir Rosengarten
 */
public class ExtraBallsCollisionStrategy implements CollisionStrategy{

    private final GameObjectCollection gameObjects;
    private final Random rand = new Random();
    private final float BALL_SPEED;
    private final Vector2 topLeftCorner;
    private final Vector2 dimensions;
    private final Renderable renderable;
    private final Sound collisionSound;
    private boolean wasBrickHit = false;
    private boolean isRequiredToCheckCollision;
    private final Counter brickCounter;

    /**
     * Construct a new ExtraBallsCollisionStrategy instance.
     *
     * @param gameObjects The collection of GameObjects in the game.
     * @param topLeftCorner Position of the object, in window coordinates (pixels).
     *                      Note that (0,0) is the top-left corner of the window.
     * @param dimensions Width and height in window coordinates.
     * @param renderable The renderable representing the object. Can be null, in which case
     *                   the GameObject will not be rendered.
     * @param collisionSound The sound to play when a collision occurs.
     * @param ballSpeed The speed of the ball.
     * @param brickCounter The counter representing the number of bricks in the game.
     * @param isRequiredToCheckCollision True if the collision should be checked, false otherwise.
     */
public ExtraBallsCollisionStrategy(GameObjectCollection gameObjects, Vector2 topLeftCorner,
                                   Vector2 dimensions, Renderable renderable,
                                   Sound collisionSound, float ballSpeed, Counter brickCounter,
                                   boolean isRequiredToCheckCollision) {
    this.gameObjects = gameObjects;
    this.BALL_SPEED = ballSpeed;
    this.topLeftCorner = topLeftCorner;
    this.dimensions = dimensions;
    this.renderable = renderable;
    this.collisionSound = collisionSound;
    this.brickCounter = brickCounter;
    this.isRequiredToCheckCollision = isRequiredToCheckCollision;

}

    /**
     * Initialize the direction of the ball.
     * @return The direction of the ball.
     */
    private Vector2 initBallDirection(){
        double angle = rand.nextDouble() * Math.PI;
        return new Vector2((float) (Math.cos(angle)) * BALL_SPEED,
                (float) (Math.sin(angle)) * BALL_SPEED);
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
            wasBrickHit = true;
            brickCounter.decrement();
        }
        Vector2 position = current.getCenter();
        Puck puck1 = new Puck(topLeftCorner, dimensions, renderable, collisionSound);
        Puck puck2 = new Puck(topLeftCorner, dimensions, renderable, collisionSound);
        puck1.setCenter(position);
        puck2.setCenter(position);
        puck1.setVelocity(initBallDirection());
        puck2.setVelocity(initBallDirection());
        gameObjects.addGameObject(puck1);
        gameObjects.addGameObject(puck2);
    }
}
