package com.mygdx.icompress;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.CheckBox;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.mygdx.archiveAlgorithm.UnZip;
import com.mygdx.archiveAlgorithm.Zip;

import java.io.File;
import java.text.DecimalFormat;
import java.util.ArrayList;




/**
 * Created by root on 24.05.2017.
 */

public class LobbyScreen implements Screen {
    //app instance variable
    private Start app;

    //scene2d.ui fields
    private Stage stage;
    private Table rootTable;
    private Skin skin;
    private ScrollPane scrollPane;
    private Table scrollPaneTable;
    private TextField archiveNameT_Field;
    private CheckBox selectAllCheckBox;
    private Label currentRootFolderPathLabel;
    private Label.LabelStyle labelStyle;
    private Dialog archiveNameDialog;
    private Dialog packingFileDialog;
    private Dialog unpackingFileDialog;

    //resources
    private BitmapFont gilsansFont;
    private TextureRegion mp3T_Region;
    private TextureRegion mp4T_Region;
    private TextureRegion randomFileT_Region;
    private TextureRegion folderT_Region;
    private TextureRegion imageIconT_Region;
    private TextureRegion archiveT_Region;
    private TextureRegionDrawable backgroundDrawable;


    //archive objects
    private Zip zip;
    private UnZip unzip;

    //app logic fields
    private SelectBoxStates selectBox_State;
    private TableFileEntry[] tableFileEntries;
    private String rootFolderPath;
    private FileHandle rootFolder;
    private ArrayList<File> selectedFiles;


    public LobbyScreen(Start app) {
        this.app = app;
    }

    @Override
    public void show() {
        stage = new Stage(new ScreenViewport());
        rootTable = new Table();
        scrollPaneTable = new Table();
        skin = new Skin(Gdx.files.internal("uiskin.json"));
        scrollPane = new ScrollPane(scrollPaneTable, skin);
        selectBox_State = SelectBoxStates.CHECK;


        zip = new Zip();
        unzip = new UnZip();
        selectedFiles = new ArrayList<File>();


        gilsansFont = new BitmapFont(Gdx.files.internal("gillsans72.fnt"));
        gilsansFont.getData().setScale(0.6f);

        labelStyle = new Label.LabelStyle(gilsansFont, Color.BLACK);

        rootFolderPath = app.getExternalStoragePath();
        rootFolder = Gdx.files.absolute(rootFolderPath);

        backgroundDrawable = new TextureRegionDrawable(new TextureRegion(new Texture("bg_iCompress.png")));

        createIconResources();
        initializeRootTable();
        initializeDialogs();
        initializeTopTable();
        initializeScrollPanel();
        addScrollablePaneContent();


        stage.addActor(rootTable);

        Gdx.input.setInputProcessor(stage);
    }


    private void initializeRootTable() {
        rootTable.setFillParent(true);

        rootTable.setBackground(backgroundDrawable);

    }

    private void initializeTopTable() {
        Table dummyTabel = new Table();
        dummyTabel.setBackground(backgroundDrawable);

        Texture archiveTexture = new Texture("archive.png");
        TextureRegion archiveTextureR = new TextureRegion(archiveTexture);
        Image archiveIcon = new Image(archiveTextureR);

        Texture unarchiveTexture = new Texture("unarchive.png");
        TextureRegion unarchiveTextureR = new TextureRegion(unarchiveTexture);
        Image unarchiveIcon = new Image(unarchiveTextureR);

        Texture networkTexture = new Texture("network.png");
        TextureRegion networkT_Region = new TextureRegion(networkTexture);
        Image networkIcon = new Image(networkT_Region);

        Texture deleteTexture = new Texture("delete.png");
        TextureRegion deleteT_Region = new TextureRegion(deleteTexture);
        Image deleteIcon = new Image(new TextureRegion(deleteT_Region));

            dummyTabel.add(networkIcon);
            dummyTabel.add(archiveIcon).right();
            dummyTabel.add(unarchiveIcon).left();
            dummyTabel.add(deleteIcon);

            rootTable.add(dummyTabel).height(2 * Start.heightDistanceUnit).padBottom(Start.heightDistanceUnit);
            rootTable.row();

        addListenersArchiveIcons(networkIcon, archiveIcon, unarchiveIcon, deleteIcon);

        createUpFolderBar();
    }

    private void addListenersArchiveIcons(Image networkIcon,  Image archiveIcon, Image unarchiveIcon, Image deleteIcon) {

        //// TODO: 31.05.2017 add listener for the networkIcon

        archiveIcon.addListener(new ClickListener() {

            @Override
            public void clicked(InputEvent event, float x, float y) {
                archiveNameDialog.show(stage);
            }
        });

        unarchiveIcon.addListener(new ClickListener() {

            @Override
            public void clicked(InputEvent event, float x, float y) {
                //clears the previous stored files in the ArrayList
                selectedFiles.clear();

                getSelectedFiles();

                removeNonArchiveFiles();

                displayUnpackingDialogUnpackArchive();

            }
        });

        deleteIcon.addListener(new ClickListener(){

            @Override
            public void clicked(InputEvent event, float x, float y){

                //clears the previous stored files in the ArrayList
                selectedFiles.clear();

                getSelectedFiles();

                for (File f : selectedFiles){
                    f.delete();
                }

                //update the content of the scrollable pane

                //discard the previous content
                tableFileEntries = null;
                scrollPaneTable.reset();
                addScrollablePaneContent();

            }
        });

    }


    private void removeNonArchiveFiles() {
        //remove all files that are not ".zip"
        ArrayList<Integer> nonArchivesFileIndices = new ArrayList<Integer>();

        for (int i = 0; i < selectedFiles.size(); i++) {

            //check if the file is zip or not
            File f = selectedFiles.get(i);
            String fileName = f.getName();
            int length = fileName.length();
            String fileExtension = (length > 3) ? fileName.substring(length - 4, length) : "";

            //can't be a zip file
            if (length <= 3) continue;

            if (!(".zip".equals(fileExtension))) {
                nonArchivesFileIndices.add(i);
            }
        }

        for (Integer j : nonArchivesFileIndices) {
            selectedFiles.remove(j);
        }
    }

    private void initializeDialogs() {
        float dialogWidth = 2 * Start.deviceWidth / 3;
        float dialogHeight = 4 * Start.heightDistanceUnit;

        archiveNameDialog = new MyDialog("Choose archive name !", skin, dialogWidth, dialogHeight);
        packingFileDialog = new MyDialog("Packing...", skin, dialogWidth, dialogHeight);
        unpackingFileDialog = new MyDialog("Unpacking...", skin, dialogWidth, dialogHeight);
        //archiveNameDialog.debug();

        // centering the gif in the middle of the dialog
        float gifWidth = Start.deviceWidth / 2;
        float gifHeight = dialogHeight - packingFileDialog.getTitleLabel().getWidth();
        float xPosition = dialogWidth / 2 - gifWidth / 2;
        float yPosition = (dialogHeight - gifHeight) / 3;
        LoadingGifActor gifActorPacking = new LoadingGifActor(xPosition, yPosition, gifWidth, gifHeight);
        LoadingGifActor gifActorUnpacking = new LoadingGifActor(xPosition, yPosition, gifWidth, gifHeight);

        packingFileDialog.add(gifActorPacking);
        unpackingFileDialog.add(gifActorUnpacking);


        final TextField.TextFieldStyle textFieldStyle = skin.get(TextField.TextFieldStyle.class);
        textFieldStyle.font.getData().setScale(2f);
        archiveNameT_Field = new TextField("", textFieldStyle);
        TextButton okButton = new TextButton("Ok", skin);

        archiveNameDialog.text("Please type an archive name \n and hit <<ok>> button !").row();
        archiveNameDialog.getButtonTable().add(archiveNameT_Field).width(2 * dialogWidth / 3).padBottom(Start.heightDistanceUnit/4).row();
        archiveNameDialog.getButtonTable().add(okButton).width(dialogWidth / 3).padBottom(Start.heightDistanceUnit/4);

        okButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {

                //clears the previous files stored (the contents of the previously created archive)
                selectedFiles.clear();

                getSelectedFiles();

                Gdx.input.setOnscreenKeyboardVisible(false);

                archiveNameDialog.hide();

                try {
                    displayLoadingDialogPackArchive();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }
        });

    }

    private void displayLoadingDialogPackArchive() throws InterruptedException {
        final String archiveName = archiveNameT_Field.getText() + ".zip";

        packingFileDialog.show(stage);

            new Thread(new Runnable() {
                @Override
                public void run() {
                    zip.archiveFiles(selectedFiles, app.getArchivesFolderPath(), archiveName);

                    //that's how you communicate with the rendering thread using libGDX (in order to send some results)
                    Gdx.app.postRunnable(new Runnable() {
                        @Override
                        public void run() {
                            packingFileDialog.hide();
                            //clear the last name typed from the textbox
                            archiveNameT_Field.setText("");

                        }
                    });
                }
            }).start();

    }


    private void  displayUnpackingDialogUnpackArchive(){

        unpackingFileDialog.show(stage);

        new Thread(new Runnable() {
            @Override
            public void run() {
                unzip.unarchiveFiles(selectedFiles, app.getUnarchivedFilesFolderPath());

                //that's how you communicate with the rendering thread using libGDX (in order to send some results)
                Gdx.app.postRunnable(new Runnable() {
                    @Override
                    public void run() {
                        unpackingFileDialog.hide();
                    }
                });
            }
        }).start();

    }


    private void getSelectedFiles() {

        for (TableFileEntry f : tableFileEntries) {
            if (f.getCheckBox().isChecked()) {

                selectedFiles.add(f.getFileHandle().file());
            }
        }

    }

    private void createUpFolderBar() {
        Table dummyTable = new Table();
        dummyTable.setBackground(backgroundDrawable);

        Texture upFolderTexture = new Texture("upfolder.png");
        TextureRegion upFolderT_Region = new TextureRegion(upFolderTexture);
        Image upFolderIcon = new Image(upFolderT_Region);

        //some white spaces introduced because of a problem with proper displaying in the table (@noob)
        Label descriptionOfUpFolderLabel = new Label("Up one level                        " , labelStyle);

        selectAllCheckBox = new CheckBox(" ", skin);


            dummyTable.add(upFolderIcon);
            dummyTable.add(descriptionOfUpFolderLabel).left();
            dummyTable.add(selectAllCheckBox).expand().fill();

            //dummyTable.debug();


            rootTable.add(dummyTable);
            rootTable.row();

        addListenersUpFolderSelectBox(upFolderIcon);

    }

    private void addListenersUpFolderSelectBox(Image upFolderIcon) {

        upFolderIcon.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (rootFolder.parent() != null) {
                    // Gdx.app.log("root Folder", rootFolder.parent().path());
                    rootFolder = rootFolder.parent();
                    rootFolderPath = rootFolder.path();


                    //discard the previous content
                    tableFileEntries = null;

                    currentRootFolderPathLabel.setText(rootFolderPath);

                    scrollPaneTable.reset();
                    addScrollablePaneContent();
                } else {
                    // do nothing - the user can't go any higher level than the current one
                }
            }
        });

        selectAllCheckBox.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {

                if (selectBox_State == SelectBoxStates.CHECK) {  //check all the select boxes from each file entry

                    for (TableFileEntry tfe : tableFileEntries) {
                        tfe.getCheckBox().setChecked(true);
                    }

                    selectBox_State = SelectBoxStates.UNCHECK;
                } else {
                    for (TableFileEntry tfe : tableFileEntries) {
                        tfe.getCheckBox().setChecked(false);
                    }

                    selectBox_State = SelectBoxStates.CHECK;
                }
            }
        });
    }


    private void initializeScrollPanel() {
        scrollPaneTable.setBackground(backgroundDrawable);

        scrollPane.setFadeScrollBars(true);
        scrollPane.setScrollingDisabled(true, false);

        rootTable.add(scrollPane).width(Start.deviceWidth).height(13 * Start.heightDistanceUnit);
        rootTable.row();

        currentRootFolderPathLabel = new Label(rootFolderPath, labelStyle);

        rootTable.add(currentRootFolderPathLabel).padTop(Start.heightDistanceUnit / 4);
    }


    public void addScrollablePaneContent() {
        final FileHandle[] files = rootFolder.list();
        tableFileEntries = new TableFileEntry[files.length];


        Label currentLabel;
        Image currentImage;
        String currentFileName;
        CheckBox simpleCheckBox;
        Double fileSize;
        DecimalFormat df = new DecimalFormat();
        df.setMaximumFractionDigits(2);


        for (int i = 0; i < files.length; i++) {
            currentFileName = files[i].name();
            fileSize = (double)files[i].length() / (1024 * 1024); // in MB


            // each filename will have a length of 24 chars, either of it's own or filled with spaces
            if (currentFileName.length() > 18) {
                currentFileName = currentFileName.substring(0, 18);
            }
            currentFileName = String.format("%-18s", currentFileName);

            if (!files[i].isDirectory()) currentFileName += " / " + df.format(fileSize) + " MB";

            simpleCheckBox = new CheckBox(" ", skin);
            currentLabel = new Label(currentFileName, labelStyle);
            currentImage = new Image(getTextureRegion(files[i]));

            tableFileEntries[i] = new TableFileEntry(files[i], currentLabel, currentImage, simpleCheckBox, i);
            //if the file is a directory then we add an listener on the directory icon that directs the user inside the directory
            if (files[i].isDirectory()) {
                tableFileEntries[i].addListenerToDirectoryImg(this);
            }

            //add the data of each file to the scrollable Pane one row at a time
            scrollPaneTable.add(currentImage);
            scrollPaneTable.add(tableFileEntries[i].getLabel()).center().left();//.expandX().fill();
            scrollPaneTable.add(tableFileEntries[i].getCheckBox()).expandX().fill().bottom().left();

            scrollPaneTable.row().top();

        }

        // scrollPaneTable.debug();

        selectAllCheckBox.setChecked(false);
    }


    private TextureRegion getTextureRegion(FileHandle file) {
        char option;
        String fileName = "";
        int lengthOfFileName = 0;

        if (file.isDirectory()) {
            option = 'd';
        } else { // select the last letter of the files's name
            fileName = file.name();
            lengthOfFileName = fileName.length();
            option = fileName.charAt(lengthOfFileName - 1);
        }

        switch (option) {
            case 'd':
                //directory
                return folderT_Region;

            case '3':
                //mp3 file
                return mp3T_Region;

            case '4':
                //mp4 file
                return mp4T_Region;

            case 'g':
                //jpg file

                //some files end in 'g' but they are not "jpg" => I will do an extra check
                if (".jpg".equals(fileName.substring(lengthOfFileName - 4, lengthOfFileName))) {
                    return imageIconT_Region;
                }

            case 'p':
                //zip file

                if (".zip".equals(fileName.substring(lengthOfFileName - 4, lengthOfFileName))){
                    return archiveT_Region;
                }

            default:
                //a file that doesn't belong to any of the categories from above
                return randomFileT_Region;

        }

    }

    private void createIconResources() {
        Texture mp3Texture = new Texture("mp3.png");
        mp3T_Region = new TextureRegion(mp3Texture);


        Texture mp4Texture = new Texture("mp4.png");
        mp4T_Region = new TextureRegion(mp4Texture);


        Texture randomFileTexture = new Texture("file.png");
        randomFileT_Region = new TextureRegion(randomFileTexture);


        Texture folderTexture = new Texture("folder.png");
        folderT_Region = new TextureRegion(folderTexture);


        Texture imageIconTexture = new Texture("img.png");
        imageIconT_Region = new TextureRegion(imageIconTexture);

        Texture archiveTexture = new Texture("zipIcon.png");
        archiveT_Region = new TextureRegion(archiveTexture);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        stage.act(delta);
        stage.draw();

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
        stage.dispose();
    }


    public TableFileEntry[] getTableFileEntries() {
        return tableFileEntries;
    }

    public void setTableFileEntries(TableFileEntry[] tableFileEntries) {
        this.tableFileEntries = tableFileEntries;
    }

    public String getRootFolderPath() {
        return rootFolderPath;
    }

    public Label getCurrentRootFolderPathLabel() {
        return currentRootFolderPathLabel;
    }

    public void setCurrentRootFolderPathLabel(Label currentRootFolderPathLabel) {
        this.currentRootFolderPathLabel = currentRootFolderPathLabel;
    }

    public void setRootFolderPath(String rootFolderPath) {
        this.rootFolderPath = rootFolderPath;
    }

    public FileHandle getRootFolder() {
        return rootFolder;
    }

    public void setRootFolder(FileHandle rootFolder) {
        this.rootFolder = rootFolder;
    }

    public Table getScrollPaneTable() {
        return scrollPaneTable;
    }

    public void setScrollPaneTable(Table scrollPaneTable) {
        this.scrollPaneTable = scrollPaneTable;
    }



}
