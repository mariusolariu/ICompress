package com.mygdx.icompress;

import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

/**
 * Created by Marius on 31.05.2017.
 * I've created this class so that I can set the width and height of my dialog widgets
 */

public class MyDialog extends Dialog {
    private float dialogWidth;
    private float dialogHeight;

    public MyDialog(String title, Skin skin, float dialogWidth, float dialogHeight){
        super(title, skin);
        this.dialogWidth = dialogWidth;
        this.dialogHeight = dialogHeight;
    }

    @Override
    public float getPrefWidth() {
        return dialogWidth;
    }

    @Override
    public float getPrefHeight() {
        return dialogHeight;
    }
}
