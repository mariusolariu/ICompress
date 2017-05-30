package com.mygdx.icompress;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.ui.ProgressBar;
import com.badlogic.gdx.scenes.scene2d.ui.ProgressBar.ProgressBarStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.TimeUtils;

public class LoadingScreen implements Screen {
    private Start mGame;
    private BitmapFont bf_loadProgress;
    private long progress = 0;
    private long startTime = 0;
    private ShapeRenderer mShapeRenderer;
    private OrthographicCamera camera;
    private final int screenWidth = 800, screenHeight = 480;
    private SpriteBatch spriteBatch;

    public LoadingScreen(Game game) {
        mGame = (Start) game;
        bf_loadProgress = new BitmapFont();
        mShapeRenderer = new ShapeRenderer();
        startTime = TimeUtils.nanoTime();
        spriteBatch = new SpriteBatch();
        initCamera();
    }

    private void initCamera() {
        camera = new OrthographicCamera();
        camera.setToOrtho(false, screenWidth, screenHeight);
        camera.update();

    }

    @Override
    public void show() {
        // TODO Auto-generated method stub

    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0.2f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        showLoadProgress();
    }

    /**
     * Show progress that updates after every half second "0.5 sec"
     */
    private void showLoadProgress() {

        long currentTimeStamp = TimeUtils.nanoTime();
        if (currentTimeStamp - startTime > TimeUtils.millisToNanos(500)) {
            startTime = currentTimeStamp;
            progress = progress + 10;
        }
        // Width of progress bar on screen relevant to Screen width
        float progressBarWidth = (screenWidth / 100) * progress;

        spriteBatch.setProjectionMatrix(camera.combined);
        spriteBatch.begin();
        bf_loadProgress.draw(spriteBatch, "Loading " + progress + " / " + 100, 10, 40);
        spriteBatch.end();

        mShapeRenderer.setProjectionMatrix(camera.combined);
        mShapeRenderer.begin(ShapeType.Filled);
        mShapeRenderer.setColor(Color.YELLOW);
        mShapeRenderer.rect(0, 10, progressBarWidth, 10);
        mShapeRenderer.end();
        if (progress == 100)
            moveToMenuScreen();

    }

    /**
     * Move to menu screen after progress reaches 100%
     */
    private void moveToMenuScreen() {
        mGame.setScreen(new LobbyScreen(mGame));
        dispose();
    }

    @Override
    public void resize(int width, int height) {
        // TODO Auto-generated method stub

    }

    @Override
    public void pause() {
        // TODO Auto-generated method stub

    }

    @Override
    public void resume() {
        // TODO Auto-generated method stub

    }

    @Override
    public void hide() {
        // TODO Auto-generated method stub

    }

    @Override
    public void dispose() {
        bf_loadProgress.dispose();
        mShapeRenderer.dispose();
    }

}