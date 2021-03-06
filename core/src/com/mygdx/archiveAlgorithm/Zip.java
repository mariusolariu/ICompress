package com.mygdx.archiveAlgorithm;

import com.badlogic.gdx.Gdx;

import java.io.*;
import java.util.ArrayList;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * Created by root on 28.05.2017.
 */

public class Zip {
    static final int BUFFER = 2048;

    public static void archiveFiles(ArrayList<File> selectedEntries, String archivesFolderPath, String archieveName){

        try {
            BufferedInputStream origin = null;
            //creates the archive in the parent directory of the files
            FileOutputStream dest = new FileOutputStream(archivesFolderPath + "/" + archieveName);
            ZipOutputStream out = new ZipOutputStream(new BufferedOutputStream(dest));

            //files can be added to a zip compressed(DEFLATED) or uncompressed(STORED)
            out.setMethod(ZipOutputStream.DEFLATED);

            byte data[] = new byte[2048];
           // selectedEntries.size();

            for (File f : selectedEntries){
                Gdx.app.log("Adding ",  f.getName());

                FileInputStream fi = new FileInputStream(f.getAbsolutePath());
                origin = new BufferedInputStream(fi, BUFFER);
                ZipEntry entry = new ZipEntry(f.getName());

                //ZipEntry

                //before writing the data to the output stream you first have to put the zip object
                out.putNextEntry(entry);

                int count;

                while ((count = origin.read(data, 0, BUFFER)) != -1){
                    out.write(data, 0, count);
                }

                origin.close();
            }

            out.close();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }



}