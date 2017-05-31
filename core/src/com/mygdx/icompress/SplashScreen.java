package com.mygdx.icompress;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.TimeUtils;

/**
 * Created by root on 24.05.2017.
 */

public class SplashScreen implements Screen {
    private Start app;
    private long startTime;
    private long currentTime;
    private SpriteBatch spriteBatch;
    private Texture logoTexture;

    public SplashScreen(Start app){
        this.app = app;

    }

    @Override
    public void show() {
        startTime = TimeUtils.millis();
        spriteBatch = new SpriteBatch();
        logoTexture = new Texture("iCompressLogo.png");
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        currentTime = TimeUtils.millis();

        if (currentTime - startTime <= Start.SPLASHSCREEN_TIMER){

            spriteBatch.begin();
            spriteBatch.draw(logoTexture, 0, 0, app.deviceWidth, app.deviceHeight);
            spriteBatch.end();

        }else{

            app.setScreen(new LobbyScreen(app));
            dispose();

        }


    }

    @Override
    public void resize(int width, int height) {

    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {
        spriteBatch.dispose();
        logoTexture.dispose();
    }
}
