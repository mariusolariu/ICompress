package com.mygdx.icompress;

/**
 * Created by root on 29.05.2017.
 */


//used to interface with Android platform code
public interface AndroidEnvironment {

    //all the methods are public abstract by default
    //all the fields are public static final (only constants in interfaces)
    public String getExternalStoragePath();

}
