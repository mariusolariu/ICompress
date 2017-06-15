package com.mygdx.archiveAlgorithm;

import com.badlogic.gdx.utils.TimeUtils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * Created by root on 28.05.2017.
 *
 */

public class ZipNativeLibrary {
    static final int BUFFER = 2048;

    public static void archiveFiles(ArrayList<File> selectedEntries, String archivesFolderPath, String archieveName){

        try {
            long startTime = TimeUtils.millis();
            archieveName += ".zip";

            BufferedInputStream origin = null;
            //creates the archive in the parent directory of the files
            FileOutputStream dest = new FileOutputStream(archivesFolderPath + "/" + archieveName);
            ZipOutputStream out = new ZipOutputStream(new BufferedOutputStream(dest));

            //files can be added to a zip compressed(DEFLATED) or uncompressed(STORED)
            out.setMethod(ZipOutputStream.DEFLATED);

            byte data[] = new byte[BUFFER];
           // selectedEntries.size();

            for (File f : selectedEntries){
                System.out.println("Adding: <<" + f.getName() + ">> file to archive " + archieveName );

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

            long endTime = TimeUtils.millis();
            double timeElapsed = (endTime - startTime) / 1000.0;
            DecimalFormat df = new DecimalFormat();
            df.setMaximumFractionDigits(2);

            System.out.println("The archive <<" + archieveName + ">> was created successfully in " + df.format(timeElapsed) + " seconds!");

            out.close();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }



}