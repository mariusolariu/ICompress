package com.mygdx.icompress;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Event;

/**
 * Created by Marius on 30.05.2017.
 */

public class LoadingGifActor extends Actor {
    private Animation animation;
    private float frameCounter;
    private float posX;
    private float posY;
    private float gifWidth;
    private float gifHeigth;

    public LoadingGifActor(float posX, float posY, float gifWidth, float gifHeigth){

        FileHandle gifFileHandle = Gdx.files.internal("ajax_red_256.gif");
        byte[] dataBytes = gifFileHandle.readBytes();
        animation = GifDecoderOptimized.loadGIFAnimation(Animation.PlayMode.LOOP, dataBytes);

        this.posX = posX;
        this.posY = posY;
        this.gifWidth = gifWidth;
        this.gifHeigth = gifHeigth;
    }



    @Override
    public void draw(Batch batch, float parentAlpha) {
        super.draw(batch, parentAlpha);
        batch.end(); //if u don't call this  it will throw an error (strange!)

        batch.begin();
        batch.draw((TextureRegion)animation.getKeyFrame(frameCounter,true), posX, posY, gifWidth, gifHeigth);


    }

    @Override
    public void act(float delta) {
        super.act(delta);
        frameCounter += delta;
    }
}
