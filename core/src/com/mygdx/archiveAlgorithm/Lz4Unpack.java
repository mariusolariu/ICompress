package com.mygdx.archiveAlgorithm;

import com.badlogic.gdx.utils.TimeUtils;

import net.jpountz.lz4.LZ4BlockInputStream;
import net.jpountz.lz4.LZ4Factory;
import net.jpountz.lz4.LZ4SafeDecompressor;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DecimalFormat;

/**
 * Created by Marius
 */

public class Lz4Unpack {
    private static final int BUFFER = 2048;
    private static LZ4Factory factory = LZ4Factory.fastestInstance();
    private static LZ4SafeDecompressor decompressor = factory.safeDecompressor();

    public static byte[] decompress(byte[] finalCompressedArray, int decompressedLength) {
        byte[] restored = new byte[decompressedLength];
        //decompressor.decompress(finalCompressedArray, 0,)
        restored = decompressor.decompress(finalCompressedArray, decompressedLength);
        return restored;
    }

  /*  public static void unpackFile(File targetFile, String unarchivedFilesFolderPath) {

        try {
            long startTime = TimeUtils.millis();
            String targetFileName = targetFile.getName();

            System.out.println("Unpacking " + targetFileName + "...");

            if (targetFileName.contains(".lz4")) {
                int length = targetFileName.length();
                targetFileName = targetFileName.substring(0, length - 4);
            }

            FileOutputStream fout = new FileOutputStream(unarchivedFilesFolderPath + "/" + targetFileName);
            BufferedOutputStream dest = new BufferedOutputStream(fout, BUFFER);

            FileInputStream fis = new FileInputStream(targetFile);
            BufferedInputStream origin = new BufferedInputStream(fis, BUFFER);

            int readBytes;
            byte[] data = new byte[BUFFER];
            byte[] unpackedData;

            while ((readBytes = origin.read(data, 0, data.length)) > 0) {
                unpackedData = decompress(data, readBytes);
                dest.write(unpackedData);
            }


            origin.close();
            fis.close();

            dest.flush();
            dest.close();

            fout.close();

            long endTime = TimeUtils.millis();
            double timeElapsed = (endTime - startTime) / 1000.0;
            DecimalFormat df = new DecimalFormat();
            df.setMaximumFractionDigits(2);

            System.out.println(targetFileName + ".lz4 was unpacked successfully in " + df.format(timeElapsed) + " seconds!");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
*/

    public static void unpackFile(File targetFile, String unarchivedFilesFolderPath) {

        try {
            long startTime = TimeUtils.millis();
            String targetFileName = targetFile.getName();

            System.out.println("Unpacking " + targetFileName + "...");

            //remove the lz4 extension for the unpacked file that's going to be created
            if (targetFileName.contains(".lz4")) {
                int length = targetFileName.length();
                targetFileName = targetFileName.substring(0, length - 4);
            }

            FileOutputStream fout = new FileOutputStream(unarchivedFilesFolderPath + "/" + targetFileName);
            BufferedOutputStream dest = new BufferedOutputStream(fout, BUFFER);

            FileInputStream fis = new FileInputStream(targetFile);



                    LZ4BlockInputStream lz4In = new LZ4BlockInputStream(fis);

                    byte[] data = new byte[BUFFER];
                    int len;

                    while ((len = lz4In.read(data, 0, data.length)) > 0) {
                        dest.write(data, 0, len);
                    }

            lz4In.close();

            fis.close();

            dest.flush();
            dest.close();
            fout.close();

            long endTime = TimeUtils.millis();
            double timeElapsed = (endTime - startTime) / 1000.0;
            DecimalFormat df = new DecimalFormat();
            df.setMaximumFractionDigits(2);

            System.out.println(targetFileName + ".lz4 was unpacked successfully in " + df.format(timeElapsed) + " seconds!");



        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }
}

