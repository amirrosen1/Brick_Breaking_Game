package bricker.main;

import bricker.brick_strategies.*;
import bricker.gameobjects.*;
import danogl.GameManager;
import danogl.GameObject;
import danogl.collisions.GameObjectCollection;
import danogl.collisions.Layer;
import danogl.components.CoordinateSpace;
import danogl.gui.*;
import danogl.gui.rendering.Renderable;
import danogl.util.Counter;
import danogl.util.Vector2;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.util.Random;

/**
 * The main class for the Bricker game.
 *
 * @author Avital Harel & Amir Rosengarten
 */
public class BrickerGameManager extends GameManager {

    Random rand = new Random();
    private static final String WIN_MESSAGE = "You win :) ";
    private static final String LOSE_MESSAGE = "You lost :( ";
    private static final String PLAY_AGAIN_MESSAGE = "want to play again?";
    private static final String TAG_ORIGINAL_BALL = "Original Ball";
    private static final String HEART_IMAGE_PATH = "assets/heart.png";
    private static final String BRICK_IMAGE_PATH = "assets/brick.png";
    private static final String BACKGROUND_IMAGE_PATH = "assets/DARK_BG2_small.jpeg";
    private static final String BALL_IMAGE_PATH = "assets/ball.png";
    private static final String BALL_COLLISION_SOUND_PATH = "assets/blop_cut_silenced.wav";
    private static final String PADDLE_IMAGE_PATH = "assets/paddle.png";
    private static final String TAG_ORIGINAL_PADDLE = "Original Paddle";
    private static final String WINDOW_TITLE = "Bricker";
    private static final int DEFAULT_WINDOW_WIDTH = 700;
    private static final int DEFAULT_WINDOW_HEIGHT = 500;
    private final int BALL_SPEED = 200;
    private final int BALL_RADIUS = 20;
    private final int PUCK_BALL_SPEED = 200;
    private final float PUCK_BALL_RADIUS = (float) (BALL_RADIUS * 0.75);
    private final int PADDLE_WIDTH = 100;
    private final int PADDEL_HEIGHT = 15;
    private final int EXTRA_PADDLE_HEIGHT = 15;
    private final int EXTRA_PADDLE_WIDTH = 100;
    private final int BRICK_HEIGHT = 15;
    private final int SPACE_BETWEEN_BRICKS = 1;
    private final int SPACE_BETWEEN_BRICKS_AND_WALL = 50;
    private final int TEXT_SIZE_BOX = 15;
    private final int NUM_OF_LIVES = 3;
    private int HEART_DIMENSIONS = 15;
    private final float HEARTS_TOP_LEFT_CORNER = 5;
    int BORDER_WIDTH = 15;
    private final int MIN_DISTANCE_FROM_EDGE = 10;
    private final int DEFAULT_BRICKS_PER_ROW = 8;
    private final int DEFAULT_BRICK_ROWS = 7;
    private int FRAME_RATE = 60;
    private Ball ball;
    private Vector2 windowDimensions;
    private WindowController windowController;
    private Counter brickCounter;
    private Counter livesCounter;
    private Counter strikesCounter;
    private UserInputListener inputListener;
    private ImageReader imageReader;
    private SoundReader soundReader;
    private BrickStrategiesFactory brickStrategiesFactory;

    /**
     * Creates a new full-screen window with the specified title.
     * The window's size will be the main screen's resolution.
     */
    public BrickerGameManager(String windowTitle, Vector2 windowDimensions) {
        super(windowTitle, windowDimensions);
    }

    /**
     * The method will be called once when a GameGUIComponent is created, and again after every invocation of
     * windowController.resetGame().
     *
     * @param imageReader      Contains a single method: readImage, which reads an image from disk.
     *                         See its documentation for help.
     * @param soundReader      Contains a single method: readSound, which reads a wav file from
     *                         disk. See its documentation for help.
     * @param inputListener    Contains a single method: isKeyPressed, which returns whether
     *                         a given key is currently pressed by the user or not. See its
     *                         documentation.
     * @param windowController Contains an array of helpful, self explanatory methods
     *                         concerning the window.
     */
    @Override
    public void initializeGame(ImageReader imageReader, SoundReader soundReader,
                               UserInputListener inputListener, WindowController windowController) {
        super.initializeGame(imageReader, soundReader, inputListener, windowController);
        windowController.setTargetFramerate(FRAME_RATE);
        this.windowController = windowController;
        this.windowDimensions = windowController.getWindowDimensions();
        this.livesCounter = new Counter(NUM_OF_LIVES);
        this.strikesCounter = new Counter();
        this.inputListener = inputListener;
        this.imageReader = imageReader;
        this.soundReader = soundReader;
        createWalls();
        createBackground(imageReader);
        createBall(imageReader, soundReader);
        createPaddle(imageReader, inputListener);
        createBricks(imageReader, windowDimensions);
        createGraphicLifeCounter(imageReader);
        createNumericLifeCounter();
    }

    /**
     * The method will be called once per frame, and should contain the game's logic.
     *
     * @param deltaTime The time in seconds that has passed since the last call to update.
     */
    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);
        checkGameObjectsOutsideWindow();
        checkBallCollisionsAndResetCamera();
        checkIfGameEnded();
    }

    /**
     * The method checks if the game objects are outside the window and removes them if they are.
     */
    private void checkGameObjectsOutsideWindow() {
        for (GameObject obj : gameObjects().objectsInLayer(Layer.DEFAULT)) {
            double objectHeight = obj.getCenter().y();
            if (objectHeight > windowDimensions.y() && !(obj.getTag().equals("Original Ball"))) {
                gameObjects().removeGameObject(obj);
            }
        }
    }

    /**
     * The method checks if the ball has collided with the paddle 4 times and resets the camera if it has.
     */
    private void checkBallCollisionsAndResetCamera() {
        if ((ball.getCollisionCounter() == 5)
                && camera() != null) {
            setCamera(null);
        }
    }

    /**
     * The method checks if the game has ended and prompts the user to play again or close the window.
     */
    private void checkIfGameEnded() {
        double ballHeight = this.ball.getCenter().y();
        String prompt = "";
        if (this.brickCounter.value() == 0 || this.inputListener.isKeyPressed(KeyEvent.VK_W)) {
            prompt = WIN_MESSAGE;
        }
        if (ballHeight > this.windowDimensions.y()) {
            this.strikesCounter.increment();
            this.livesCounter.decrement();
            initBallMovementDirection();
        }
        if (this.livesCounter.value() == 0) {
            prompt = LOSE_MESSAGE;
        }
        if (!prompt.isEmpty()) {
            prompt += PLAY_AGAIN_MESSAGE;
            if (windowController.openYesNoDialog(prompt)) {
                windowController.resetGame();
            } else {
                windowController.closeWindow();
            }
        }
    }

    /**
     * The method creates a numeric life counter.
     */
    private void createNumericLifeCounter() {
        float yPosForNumericLifeCounter =
                windowDimensions.y() - PADDEL_HEIGHT - BORDER_WIDTH;
        float topLeftCornerX = HEARTS_TOP_LEFT_CORNER + 1;
        Vector2 topLeftCorner = new Vector2(topLeftCornerX, yPosForNumericLifeCounter);
        Vector2 dimensions = new Vector2(TEXT_SIZE_BOX, TEXT_SIZE_BOX);
        GameObject numericLifeCounter = new NumericLifeCounter(this.livesCounter, topLeftCorner, dimensions,
                gameObjects());
        gameObjects().addGameObject(numericLifeCounter, Layer.UI);
    }

    /**
     * The method creates a graphic life counter.
     *
     * @param imageReader Contains a single method: readImage, which reads an image from disk.
     *                    See its documentation for help.
     */
    private void createGraphicLifeCounter(ImageReader imageReader) {
        Renderable heartImage = imageReader.readImage(HEART_IMAGE_PATH, true);
        float xPosForHearts = (int) (HEART_DIMENSIONS + HEARTS_TOP_LEFT_CORNER);
        float yPosForHearts = windowDimensions.y() - PADDEL_HEIGHT - BORDER_WIDTH;
        Vector2 topLeftCorner = new Vector2(xPosForHearts, yPosForHearts);
        GameObject graphicLifeCounter = new GraphicLifeCounter(topLeftCorner,
                new Vector2(HEART_DIMENSIONS, HEART_DIMENSIONS), this.livesCounter, heartImage, gameObjects(),
                NUM_OF_LIVES);
        gameObjects().addGameObject(graphicLifeCounter, Layer.UI);
    }

    /**
     * The method creates the bricks.
     *
     * @param imageReader      Contains a single method: readImage, which reads an image from disk.
     * @param windowDimensions The window's dimensions.
     */
    private void createBricks(ImageReader imageReader, Vector2 windowDimensions) {
        this.brickCounter = new Counter();
        this.brickStrategiesFactory = new BrickStrategiesFactory(this);
        Renderable brickImage = imageReader.readImage(BRICK_IMAGE_PATH, false);
        float topOffset = 15;
        float totalSpaceBetweenBricks = (DEFAULT_BRICKS_PER_ROW - 1) * SPACE_BETWEEN_BRICKS;
        float brickWidth =
                (windowDimensions.x() - 2 * SPACE_BETWEEN_BRICKS_AND_WALL - totalSpaceBetweenBricks) /
                        DEFAULT_BRICKS_PER_ROW;
        for (int row = 0; row < DEFAULT_BRICK_ROWS; row++) {
            for (int col = 0; col < DEFAULT_BRICKS_PER_ROW; col++) {
                brickCounter.increment();
                CollisionStrategy collisionStrategy = brickStrategiesFactory.selectStrategyForBrick();
                Vector2 brickPosition = new Vector2(SPACE_BETWEEN_BRICKS_AND_WALL + col *
                        (brickWidth + SPACE_BETWEEN_BRICKS),
                        topOffset + row * (BRICK_HEIGHT + SPACE_BETWEEN_BRICKS));
                Brick brick = new Brick(brickPosition, new Vector2(brickWidth, BRICK_HEIGHT),
                        brickImage, collisionStrategy);
                gameObjects().addGameObject(brick, Layer.STATIC_OBJECTS);
            }
        }
    }

    /**
     * The method creates the background.
     *
     * @param imageReader Contains a single method: readImage, which reads an image from disk.
     */
    private void createBackground(ImageReader imageReader) {
        Renderable backgroundImage = imageReader.readImage(BACKGROUND_IMAGE_PATH,
                false);
        GameObject background = new GameObject(Vector2.ZERO, windowDimensions, backgroundImage);
        background.setCenter(windowDimensions.mult((float) 0.5));
        background.setCoordinateSpace(CoordinateSpace.CAMERA_COORDINATES);
        gameObjects().addGameObject(background, Layer.BACKGROUND);
    }

    /**
     * The method creates the walls.
     */
    private void createWalls() {
        GameObject leftWall = new GameObject(new Vector2(-10, 0), new Vector2(BORDER_WIDTH,
                windowDimensions.y()), null);
        gameObjects().addGameObject(leftWall);
        GameObject rightWall = new GameObject(new Vector2(windowDimensions.x() - 10, 0),
                new Vector2(BORDER_WIDTH, windowDimensions.y()), null);
        gameObjects().addGameObject(rightWall);
        GameObject topWall = new GameObject(Vector2.ZERO,
                new Vector2(windowDimensions.x(), BORDER_WIDTH), null);
        gameObjects().addGameObject(topWall);
    }

    /**
     * The method creates the ball.
     *
     * @param imageReader Contains a single method: readImage, which reads an image from disk.
     * @param soundReader Contains a single method: readSound, which reads a wav file from disk.
     */
    private void createBall(ImageReader imageReader, SoundReader soundReader) {
        Renderable ballImage = imageReader.readImage(BALL_IMAGE_PATH, true);
        Sound collisionSound = soundReader.readSound(BALL_COLLISION_SOUND_PATH);
        this.ball = new Ball(Vector2.ZERO, new Vector2(BALL_RADIUS, BALL_RADIUS), ballImage, collisionSound);
        this.ball.setTag(TAG_ORIGINAL_BALL);
        initBallMovementDirection();
    }

    /**
     * The method initializes the ball's movement direction.
     */
    private void initBallMovementDirection() {
        float ballVelX = BALL_SPEED;
        float ballVelY = BALL_SPEED;
        if (rand.nextBoolean()) {
            ballVelX = -ballVelX;
        }
        if (rand.nextBoolean()) {
            ballVelY = -ballVelY;
        }
        ball.setVelocity(new Vector2(ballVelX, ballVelY));
        ball.setCenter(windowDimensions.mult((float) 0.5));
        gameObjects().addGameObject(ball);
    }

    /**
     * The method creates the paddle.
     *
     * @param imageReader   Contains a single method: readImage, which reads an image from disk.
     * @param inputListener Contains a single method: isKeyPressed, which returns whether a given key is
     *                     currently
     *                      pressed by the user or not.
     */
    private void createPaddle(ImageReader imageReader, UserInputListener inputListener) {
        Renderable paddleImage = imageReader.readImage(PADDLE_IMAGE_PATH, true);
        GameObject paddle = new Paddle(Vector2.ZERO, new Vector2(PADDLE_WIDTH, PADDEL_HEIGHT),
                paddleImage, inputListener, windowDimensions, 10);
        paddle.setCenter(new Vector2(windowDimensions.x() / 2,
                (int) (windowDimensions.y() - BORDER_WIDTH - PADDEL_HEIGHT / 2 - 20)));
        paddle.setTag(TAG_ORIGINAL_PADDLE);
        gameObjects().addGameObject(paddle);
    }

    /**
     * The method returns the extra paddle height.
     *
     * @return The extra paddle height.
     */
    public int getExtraPaddleHeight() {
        return EXTRA_PADDLE_HEIGHT;
    }

    /**
     * The method returns the extra paddle width.
     *
     * @return The extra paddle width.
     */
    public int getExtraPaddleWidth() {
        return EXTRA_PADDLE_WIDTH;
    }

    /**
     * The method returns the space between bricks.
     *
     * @return The space between bricks.
     */
    public int getMinDistanceFromEdge() {
        return MIN_DISTANCE_FROM_EDGE;
    }

    /**
     * The method returns the space between bricks.
     *
     * @return The space between bricks.
     */
    public UserInputListener getInputListener() {
        return inputListener;
    }

    /**
     * The method returns the ball.
     *
     * @return The ball.
     */
    public Ball getBall() {
        return ball;
    }

    /**
     * The method returns the window controller.
     *
     * @return The window controller.
     */
    public WindowController getWindowController() {
        return windowController;
    }

    /**
     * The method returns the brick counter.
     *
     * @return The brick counter.
     */
    public Counter getBrickCounter() {
        return brickCounter;
    }

    /**
     * The method returns the game objects.
     *
     * @return The game objects.
     */
    public GameObjectCollection getGameObjects() {
        return gameObjects();
    }

    /**
     * The method returns the heart dimensions.
     *
     * @return The heart dimensions.
     */
    public int getHeartDimensions() {
        return HEART_DIMENSIONS;
    }

    /**
     * The method returns the space between bricks.
     *
     * @return The space between bricks.
     */
    public float getHeartsTopLeftCorner() {
        return HEARTS_TOP_LEFT_CORNER;
    }

    /**
     * The method returns the lives counter.
     *
     * @return The lives counter.
     */
    public Counter getLivesCounter() {
        return livesCounter;
    }

    /**
     * The method returns the strikes counter.
     *
     * @return The strikes counter.
     */
    public Counter getStrikesCounter() {
        return strikesCounter;
    }

    /**
     * The method returns the puck ball radius.
     *
     * @return The puck ball radius.
     */
    public float getPuckBallRadius() {
        return PUCK_BALL_RADIUS;
    }

    /**
     * The method returns the puck ball speed.
     *
     * @return The puck ball speed.
     */
    public int getPuckBallSpeed() {
        return PUCK_BALL_SPEED;
    }

    /**
     * The method returns the image reader.
     *
     * @return The image reader.
     */
    public ImageReader getImageReader() {
        return imageReader;
    }

    /**
     * The method returns the sound reader.
     *
     * @return The sound reader.
     */
    public SoundReader getSoundReader() {
        return soundReader;
    }

    /**
     * The main method.
     *
     * @param args The command line arguments.
     */
    public static void main(String[] args) {
        new BrickerGameManager(WINDOW_TITLE, new Vector2(DEFAULT_WINDOW_WIDTH, DEFAULT_WINDOW_HEIGHT)).run();
    }
}

