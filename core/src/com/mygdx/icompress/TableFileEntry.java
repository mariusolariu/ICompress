package com.mygdx.icompress;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.CheckBox;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

/**
 * Created by root on 25.05.2017.
 */

public class TableFileEntry {
    private CheckBox checkBox;
    private Label label;
    private FileHandle fileHandle;
    private Image directoryIconImg;
    private int id;

    public TableFileEntry(FileHandle fileHandle, Label label,Image directoryIconImg, CheckBox checkBox, int id ){
        this.fileHandle = fileHandle;
        this.label = label;
        this.id = id;
        this.checkBox = checkBox;
        this.directoryIconImg = directoryIconImg;
    }

    public void addListenerToDirectoryImg(LobbyScreen lobbyScreen){
        final LobbyScreen lobbyScreen1 = lobbyScreen; // this trick could create problems in a multhithreaded app
        //the variable needs to final because that's how Java manges closures
        // and it makes sure that the other parts of the code witch work with this variable
        // won't look like the're working with an out-of-date variable

        directoryIconImg.addListener(new ClickListener(){
            public void clicked(InputEvent event, float x, float y) {
                lobbyScreen1.setRootFolder(fileHandle);
                lobbyScreen1.setRootFolderPath(fileHandle.path());



                //discard the previous content
                lobbyScreen1.setTableFileEntries(null);

                lobbyScreen1.getCurrentRootFolderPathLabel().setText(fileHandle.path());

                lobbyScreen1.getScrollPaneTable().reset();
                lobbyScreen1.addScrollablePaneContent();

            }
        });

    }

    public CheckBox getCheckBox() {
        return checkBox;
    }

    public void setCheckBox(CheckBox checkBox) {
        this.checkBox = checkBox;
    }

    public Label getLabel() {
        return label;
    }

    public void setLabel(Label label) {
        this.label = label;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public FileHandle getFileHandle() {
        return fileHandle;
    }

    public void setFileHandle(FileHandle fileHandle) {
        this.fileHandle = fileHandle;
    }
}
