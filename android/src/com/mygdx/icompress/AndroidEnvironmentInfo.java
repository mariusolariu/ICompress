package com.mygdx.icompress;

import android.os.Environment;
import android.util.Log;

/**
 * Created by root on 29.05.2017.
 */

public class AndroidEnvironmentInfo implements AndroidEnvironment {
    private final String externalStoragePath;

    public AndroidEnvironmentInfo(){
        externalStoragePath = Environment.getExternalStorageDirectory().getAbsolutePath();
        Log.d("directory" , externalStoragePath); //returns the primary one
    }

    @Override
    public String getExternalStoragePath() {

        return externalStoragePath;
    }
}
