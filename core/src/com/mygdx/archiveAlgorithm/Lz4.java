package com.mygdx.archiveAlgorithm;

import com.badlogic.gdx.utils.TimeUtils;

import net.jpountz.lz4.LZ4BlockOutputStream;
import net.jpountz.lz4.LZ4Compressor;
import net.jpountz.lz4.LZ4Factory;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by Marius
 */

public class Lz4 {
    private static int decompressedLength;
    private static LZ4Factory factory = LZ4Factory.fastestInstance();
    private static LZ4Compressor compressor = factory.fastCompressor();
    static final int BUFFER = 11 * 2048;

    public static byte[] compress(byte[] src, int srcLen) {
        decompressedLength = srcLen;
        System.out.println("Decompressed length = " + decompressedLength);
        int maxCompressedLength = compressor.maxCompressedLength(decompressedLength);
        System.out.println("maxCompressedLength = " + maxCompressedLength);
        byte[] compressed = new byte[maxCompressedLength];
        int compressLen = compressor.compress(src, 0, decompressedLength, compressed, 0, maxCompressedLength);
        byte[] finalCompressedArray = Arrays.copyOf(compressed, compressLen);
        return finalCompressedArray;
    }

    //lz4 doesn't support to pack multiple files in one archive so I will first tar them
    /*public static void archiveFile(File f, String archivesFolderPath, String archieveName) {

        try {
            long startTime = TimeUtils.millis();
            archieveName += ".lz4";
            System.out.println("Adding: " + f.getName() + " file to archive " + archieveName );

            FileOutputStream fout = new FileOutputStream(archivesFolderPath + "/" + archieveName);

            FileInputStream fi = new FileInputStream(f);
            BufferedInputStream origin = new BufferedInputStream(fi, BUFFER);

                int readBytes;
                byte[] data = new byte[BUFFER];
                byte[] compressedData;

                while ( (readBytes = origin.read(data, 0, data.length)) > 0){
                    compressedData = compress(data, readBytes);
                    System.out.println("Length of compressed data = " + compressedData.length);
                    System.out.println();
                    fout.write(compressedData);
                }



            fout.close();
            origin.close();
            fi.close();

            long endTime = TimeUtils.millis();
            double timeElapsed = (endTime - startTime) / 1000.0;
            DecimalFormat df = new DecimalFormat();
            df.setMaximumFractionDigits(2);

            System.out.println("The archive " + archieveName + " was created successfully in " + df.format(timeElapsed) + " using JPountz impl. of lz4!");


        } catch (Exception e) {

        }
    }*/

    public static void archiveFile(File f, String archivesFolderPath, String archieveName) {

        try {
            long startTime = TimeUtils.millis();
            archieveName += ".lz4";

            System.out.println("Adding: " + f.getName() + " file to archive " + archieveName );

            FileOutputStream fout = new FileOutputStream(archivesFolderPath + "/" + archieveName);

            LZ4BlockOutputStream out = new LZ4BlockOutputStream(fout, BUFFER);
            FileInputStream in = new FileInputStream(f);


                    byte[] data = new byte[BUFFER];
                    int len;

                    while ((len = in.read(data, 0 , data.length)) > 0) {
                        out.write(data, 0, len);
                    }


            in.close();
            out.close();
            fout.close();


            long endTime = TimeUtils.millis();
            double timeElapsed = (endTime - startTime) / 1000.0;
            DecimalFormat df = new DecimalFormat();
            df.setMaximumFractionDigits(2);

            System.out.println("The archive " + archieveName + " was created successfully in " + df.format(timeElapsed) + " seconds!");



        } catch (Exception e) {

        }

    }


}
