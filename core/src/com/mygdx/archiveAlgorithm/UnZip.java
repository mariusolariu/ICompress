package com.mygdx.archiveAlgorithm;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;


/**
 * Created by root on 27.05.2017.
 */

public class UnZip {
    private static final int BUFFER = 2048;

    public void unarchiveFiles(ArrayList<File> archiveFiles, String unarchivedFilesFolderPath) {

        try {
            for (File f : archiveFiles) {

                BufferedOutputStream dest = null;
                FileInputStream fis = new FileInputStream(f.getAbsolutePath());
                ZipInputStream zis = new ZipInputStream(new BufferedInputStream(fis));

                ZipEntry entry;

                while ((entry = zis.getNextEntry()) != null) {
                    System.out.println("Extracting: " + entry);
                    int count;

                    byte data[] = new byte[BUFFER];

                    //write the files to the disk
                    FileOutputStream fos = new FileOutputStream(unarchivedFilesFolderPath + "/" + entry.getName());
                    dest = new BufferedOutputStream(fos, BUFFER);

                    while ((count = zis.read(data, 0, BUFFER)) != -1) {
                        dest.write(data, 0, count);
                    }

                    dest.flush();
                    dest.close();

                }

                zis.close();
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}
