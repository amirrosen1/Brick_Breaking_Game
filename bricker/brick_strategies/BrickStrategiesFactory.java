package bricker.brick_strategies;

import bricker.main.BrickerGameManager;
import bricker.gameobjects.*;
import danogl.collisions.GameObjectCollection;
import danogl.gui.*;
import danogl.gui.rendering.Renderable;
import danogl.util.Counter;
import danogl.util.Vector2;
import java.util.Random;

/**
 * Factory class to create different strategies for bricks.
 * @author Amir Rosengarten and Avital Harel
 */
public class BrickStrategiesFactory {

    private static final String HEART_IMAGE_PATH = "assets/heart.png";
    private static final String BALL_COLLISION_SOUND_PATH = "assets/blop_cut_silenced.wav";
    private static final String PADDLE_IMAGE_PATH = "assets/paddle.png";
    private static final String MOCK_BALL_IMAGE_PATH = "assets/mockBall.png";
    private final BrickerGameManager brickerGameManager;
    private WindowController windowController;
    private Ball ball;
    private final Counter brickCounter;
    private Random rand;
    private GameObjectCollection gameObjects;
    private ImageReader imageReader;
    private SoundReader soundReader;

    /**
     * Constructor for the factory.
     * @param brickerGameManager the game manager
     */
    public BrickStrategiesFactory(BrickerGameManager brickerGameManager){
        this.rand = new Random();
        this.brickerGameManager = brickerGameManager;
        this.windowController = brickerGameManager.getWindowController();
        this.ball = brickerGameManager.getBall();
        this.brickCounter = brickerGameManager.getBrickCounter();
        this.gameObjects = brickerGameManager.getGameObjects();
        this.imageReader = brickerGameManager.getImageReader();
        this.soundReader = brickerGameManager.getSoundReader();
    }

    /**
     * Selects a collision strategy for a brick.
     * @return the strategy
     */
    public CollisionStrategy selectStrategyForBrick() {
        CollisionStrategy strategy;
        float chance = rand.nextFloat();

        if (chance < 0.5) { // Normal behavior with 1/2 probability
            strategy = new BasicCollisionStrategy(gameObjects, brickCounter,true);

        } else { // Special behaviors including double behavior
            strategy = selectSpecialStrategy(false, true,true);
        }

        return strategy;
    }


    /**
     * Selects a special collision strategy for a brick.
     * @param includeDoubleBehavior whether to include double behavior
     * @param isRequiredToCheckCollision whether this strategy is responsible for checking collision
     * @param allowSecondDoubleBehavior whether to allow another double behavior
     * @return the strategy
     */
    private CollisionStrategy selectSpecialStrategy(boolean includeDoubleBehavior,
                                                    boolean isRequiredToCheckCollision,
                                                    boolean allowSecondDoubleBehavior) {
        float chance = rand.nextFloat();
        // If includeDoubleBehavior is true, we have 5 options, else 4
        int numberOfOptions = includeDoubleBehavior ? 5 : 4;

        float probabilityPerOption = 1.0f / numberOfOptions;

        if (chance < probabilityPerOption) {
            return createExtraBallsCollisionStrategy(isRequiredToCheckCollision);
        } else if (chance < probabilityPerOption * 2) {
            return createExtraPaddleCollisionStrategy(isRequiredToCheckCollision);
        } else if (chance < probabilityPerOption * 3) {
            return createChangingCameraCollisionStrategy(isRequiredToCheckCollision);
        } else if (includeDoubleBehavior && chance < probabilityPerOption * 4) {
            // Return a new double behavior decorator with two strategies,
            // now allowing another double behavior conditionally
            if(allowSecondDoubleBehavior){
                return new DoubleBehaviorCollisionStrategy(selectSpecialStrategy(true,
                        isRequiredToCheckCollision,  false),
                        selectSpecialStrategy(false, isRequiredToCheckCollision, false),
                        brickCounter, isRequiredToCheckCollision, gameObjects);
            } else {
                // If doubleBehaviorCounter is not greater than 0, revert to excluding double behavior to
                // prevent infinite recursion
                return new DoubleBehaviorCollisionStrategy(selectSpecialStrategy(false,
                        isRequiredToCheckCollision, false),
                        selectSpecialStrategy(true, isRequiredToCheckCollision,
                                false),
                        brickCounter, isRequiredToCheckCollision, gameObjects);
            }
        } else {
            // AddLifeStrategy or the last option if double behavior is not included
            return createAddLifeCollisionStrategy(isRequiredToCheckCollision);
        }
    }



    /**
     * Creates a strategy for adding a life.
     * @param isRequiredToCheckCollision whether this strategy is responsible for checking collision
     * @return the collision strategy
     */
    private CollisionStrategy createAddLifeCollisionStrategy(boolean isRequiredToCheckCollision) {
        Renderable heartImage = imageReader.readImage(HEART_IMAGE_PATH, true);
        float heartTopLeftCorner = brickerGameManager.getHeartsTopLeftCorner();
        float heartDimensions = brickerGameManager.getHeartDimensions();
        Counter livesCounter = brickerGameManager.getLivesCounter();
        Counter strikesCounter = brickerGameManager.getStrikesCounter();
        Vector2 widgetTopLeftCorner = new Vector2(heartTopLeftCorner, heartTopLeftCorner);
        Vector2 widgetDimensions = new Vector2(heartDimensions,heartDimensions);
        CollisionStrategy collisionStrategy = new AddLifeCollisionStrategy(gameObjects,
                brickCounter, widgetTopLeftCorner, widgetDimensions, livesCounter,
                heartImage,strikesCounter,isRequiredToCheckCollision);
        return collisionStrategy;
    }

    /**
     * Creates a strategy for extra balls.
     * @param isRequiredToCheckCollision whether this strategy is responsible for checking collision
     * @return the collision strategy
     */
    private CollisionStrategy createExtraBallsCollisionStrategy(boolean isRequiredToCheckCollision) {
        Renderable ballImage = imageReader.readImage(MOCK_BALL_IMAGE_PATH, true);
        Sound collisionSound = soundReader.readSound(BALL_COLLISION_SOUND_PATH);
        float puckRadius = brickerGameManager.getPuckBallRadius();
        int puckSpeed = brickerGameManager.getPuckBallSpeed();
        Vector2 dimensions = new Vector2(puckRadius , puckRadius);
        CollisionStrategy collisionStrategy = new ExtraBallsCollisionStrategy(gameObjects,
                Vector2.ZERO,dimensions, ballImage,collisionSound,puckSpeed,brickCounter,
                isRequiredToCheckCollision);
        return collisionStrategy;
    }

    /**
     * Creates a strategy for an extra paddle.
     * @param isRequiredToCheckCollision whether this strategy is responsible for checking collision
     * @return the collision strategy
     */
    private CollisionStrategy createExtraPaddleCollisionStrategy(boolean isRequiredToCheckCollision) {
        Renderable paddleImage = imageReader.readImage(PADDLE_IMAGE_PATH, true);
        int paddleWidth = brickerGameManager.getExtraPaddleWidth();
        int paddleHeight = brickerGameManager.getExtraPaddleHeight();
        int minDistanceFromEdge = brickerGameManager.getMinDistanceFromEdge();
        UserInputListener inputListener = brickerGameManager.getInputListener();
        Vector2 windowDimensions = windowController.getWindowDimensions();
        CollisionStrategy collisionStrategy = new ExtraPaddleCollisionStrategy(Vector2.ZERO,
                new Vector2(paddleWidth, paddleHeight), paddleImage, inputListener,
                windowDimensions,minDistanceFromEdge, gameObjects,brickCounter,isRequiredToCheckCollision);
        return collisionStrategy;
    }

    /**
     * Creates a strategy for changing the camera.
     * @param isRequiredToCheckCollision whether this strategy is responsible for checking collision
     * @return the collision strategy
     */
    private CollisionStrategy createChangingCameraCollisionStrategy(boolean isRequiredToCheckCollision) {
        CollisionStrategy collisionStrategy = new ChangingCameraCollisionStrategy(gameObjects,
                brickerGameManager, windowController, ball, brickCounter,isRequiredToCheckCollision);
        return collisionStrategy;
    }
}