package com.mygdx.icompress;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;

import com.badlogic.gdx.backends.android.AndroidApplication;
import android.os.Environment;
import android.util.Log;

import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import com.mygdx.icompress.Start;

import java.io.File;

import static com.badlogic.gdx.Input.Keys.A;

public class AndroidLauncher extends AndroidApplication {
	// Storage Permissions
	private static final int REQUEST_EXTERNAL_STORAGE = 1;
	private static String[] PERMISSIONS_STORAGE = {
			Manifest.permission.READ_EXTERNAL_STORAGE,
			Manifest.permission.WRITE_EXTERNAL_STORAGE
	};

	@Override
	protected void onCreate (Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		AndroidApplicationConfiguration config = new AndroidApplicationConfiguration();
		AndroidEnvironmentInfo infoAndroid = new AndroidEnvironmentInfo();

		initialize(new Start(infoAndroid), config);
	}
}
