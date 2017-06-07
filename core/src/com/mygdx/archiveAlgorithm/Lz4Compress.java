package com.mygdx.archiveAlgorithm;

import org.apache.commons.compress.compressors.lz4.FramedLZ4CompressorOutputStream;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;

/**
 * Created by Marius on 06.06.2017.
 */

public class Lz4Compress {
   private static final int BUFFER = 2048;

    public static void archiveFiles(ArrayList<File> selectedEntries, String archivesFolderPath, String archieveName) {

        try {
            FileOutputStream fileOutputStream = new FileOutputStream(archivesFolderPath + "/" + archieveName + "_" );
            BufferedOutputStream outputStream = new BufferedOutputStream(fileOutputStream);
            FramedLZ4CompressorOutputStream lzOut = new FramedLZ4CompressorOutputStream(outputStream);

            BufferedInputStream origin = null;

            final byte[] buffer = new byte[BUFFER];
            int n ;

            for (File f : selectedEntries) {

                int length = f.getName().length();
                String fileName = f.getName().substring(0, length - 4);
                fileName += ".lz4";

                System.out.println("Adding " + fileName);

                FileInputStream fileInputStream = new FileInputStream(f.getAbsolutePath());
                origin = new BufferedInputStream(fileInputStream, BUFFER);

                    while ((n = origin.read(buffer, 0, BUFFER)) != -1) {
                        lzOut.write(buffer, 0, n);
                    }

                //lzOut.flush();
                origin.close();

            }

            System.out.println("here");
            lzOut.close();

        } catch (FileNotFoundException e) {
            e.printStackTrace();

        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    public static void main(String[] args) {


       /* File directory = new File(args[0]);
        ArrayList<File> files = new ArrayList<File>();

        for (File f : directory.listFiles()){
            files.add(f);
        }

        archiveFiles(files, "/mnt/92AEC018AEBFF339/proiecte/ProiecteAndroid/ICompress/", "testSize.lz4" );*/

        /*try {
            File directory = new File(args[0]);
            File outputDirectory = new File("output");
            outputDirectory.mkdir();
            //File inputFile = new File(args[0]);

            final byte[] buffer = new byte[BUFFER];
            int n = 0;

            for (File f : directory.listFiles()) {
                int length = f.getName().length();
                String archiveName = f.getName().substring(0, length - 4);
                archiveName += ".lz4";

                FileOutputStream fileOutputStream = new FileOutputStream("/mnt/92AEC018AEBFF339/proiecte/ProiecteAndroid/ICompress/output/" + archiveName);
                BufferedOutputStream outputStream = new BufferedOutputStream(fileOutputStream);
                FramedLZ4CompressorOutputStream lzOut = new FramedLZ4CompressorOutputStream(outputStream);

                InputStream fileInputStream = new FileInputStream(f.getAbsolutePath());

                while ((n = fileInputStream.read(buffer)) != -1) {
                    lzOut.write(buffer, 0, n);
                }

                fileInputStream.close();

                lzOut.close();

            }


        } catch (FileNotFoundException e) {
            e.printStackTrace();

        } catch (IOException e) {
            e.printStackTrace();
        }*/
    }
}
