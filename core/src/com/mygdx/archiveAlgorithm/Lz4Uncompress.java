package com.mygdx.archiveAlgorithm;

import org.apache.commons.compress.compressors.lz4.FramedLZ4CompressorInputStream;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;


import static com.badlogic.gdx.Input.Keys.F;

/**
 * Created by Marius on 06.06.2017.
 */

public class Lz4Uncompress {
    private static final int BUFFER_LENGTH = 2048;

    public static void main(String[] args){
        File f = new File(args[0]);


        try {
            FileInputStream fileInputStream = new FileInputStream(f.getAbsolutePath());
            BufferedInputStream bufferedInputStream = new BufferedInputStream(fileInputStream);
            FramedLZ4CompressorInputStream lz4CompressorInputStream = new FramedLZ4CompressorInputStream(bufferedInputStream);

            FileOutputStream fout = new FileOutputStream("unpackedLz4File");

            int readBytes = 0;
            final byte[] buffer = new byte[BUFFER_LENGTH];

            while ( (readBytes = lz4CompressorInputStream.read(buffer)) != -1){
                fout.write(buffer, 0 , readBytes);
            }

            fout.close();
            lz4CompressorInputStream.close();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
