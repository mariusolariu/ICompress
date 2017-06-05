package com.mygdx.server;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by Ronan on 05/06/2017.
 */

public class ZipObject {

    //Attribute to determine if we want to zip or unzip
    private boolean isForZip;
    private ArrayList<File> archiveFiles;
    private String destPath;
    private String archiveName;

    public ZipObject(boolean isForZip, ArrayList<File> archiveFiles, String destPath, String archiveName) {
        this.isForZip = isForZip;
        this.archiveFiles = archiveFiles;
        this.destPath = destPath;
        this.archiveName = archiveName;
    }

    public boolean isForZip() {
        return isForZip;
    }

    public ArrayList<File> getArchiveFiles() {
        return archiveFiles;
    }

    public String getDestPath() {
        return destPath;
    }

    public String getArchiveName() {
        return archiveName;
    }
}
