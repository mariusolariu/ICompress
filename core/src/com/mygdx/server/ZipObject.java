package com.mygdx.server;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by Ronan on 05/06/2017.
 */

public class ZipObject implements Serializable{

    //Attribute to determine if we want to zip or unzip
    private boolean iWantToZip;
    private ArrayList<File> archiveFiles;
    private String archiveName;

    public ZipObject(boolean iWantToZip, ArrayList<File> archiveFiles, String archiveName) {
        this.iWantToZip = iWantToZip;
        this.archiveFiles = archiveFiles;
        this.archiveName = archiveName;
    }

    public boolean getIWantToZip() {
        return iWantToZip;
    }

    public ArrayList<File> getArchiveFiles() {
        return archiveFiles;
    }

    public String getArchiveName() {
        return archiveName;
    }
}
