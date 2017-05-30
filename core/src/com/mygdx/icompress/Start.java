package com.mygdx.icompress;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;

import java.io.File;

public class Start extends Game {
	public static final int SPLASHSCREEN_TIMER = 300;
	public static int deviceWidth;
	public static int deviceHeight;
	public static int heightDistanceUnit;
	private AndroidEnvironment infoAndroid;
	private final String archivesFolderPath;
	private final String unarchivedFilesFolderPath;

	public Start(AndroidEnvironment infoAndroid){
		this.infoAndroid = infoAndroid;

		archivesFolderPath = infoAndroid.getExternalStoragePath() + "/iCompress Archives";
		unarchivedFilesFolderPath = infoAndroid.getExternalStoragePath() + "/iCompress Unarchived Files";
	}


	@Override
	public void create () {
		deviceWidth= Gdx.graphics.getWidth();
		deviceHeight = Gdx.graphics.getHeight();
		heightDistanceUnit = deviceHeight / 18;

		createAppFolders();

		setScreen(new LobbyScreen(this));

	}

	private void createAppFolders() {
		File archivesDirectory = new File(archivesFolderPath);
		File unarchivedFilesDirectory =  new File(unarchivedFilesFolderPath);

		if (!archivesDirectory.exists()){
			archivesDirectory.mkdir();
		}

		if (!unarchivedFilesDirectory.exists()){
			unarchivedFilesDirectory.mkdir();
		}


	}

	public String getExternalStoragePath(){
		return infoAndroid.getExternalStoragePath();
	}

	@Override
	public void render () {
		super.render();
	}
	
	@Override
	public void dispose () {

	}

	public String getArchivesFolderPath() {
		return archivesFolderPath;
	}

	public String getUnarchivedFilesFolderPath() {
		return unarchivedFilesFolderPath;
	}
}
