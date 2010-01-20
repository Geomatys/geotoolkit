/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2008 - 2009, Geomatys
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation; either
 *    version 2.1 of the License, or (at your option) any later version.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */

package org.geotoolkit.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

/**
 *
 * @author Guilhem Legal (Geomatys)
 */
public class FileUtilities {

    /**
     * This method delete recursively a file or a folder.
     * 
     * @param file The File or directory to delete.
     */
    public static void deleteDirectory(File dir) {
         if (dir.exists()) {
            if (dir.isDirectory()) {
                for (File f : dir.listFiles()) {
                    if (f.isDirectory()) {
                        deleteDirectory(f);
                    } else {
                        f.delete();
                    }
                }
                dir.delete();
            } else {
                dir.delete();
            }
        }
    }

    /**
     * Append the specified text at the end of the File.
     *
     * @param text The text to append to the file.
     * @param urlFile The url file.
     *
     * @throws IOException if the file does not exist or cannot be read.
     */
    public static void appendToFile(String text, String urlFile) throws IOException {

        //true means we append a the end of the file
        final FileWriter fw         = new FileWriter(urlFile, true);
        final BufferedWriter output = new BufferedWriter(fw);

        output.write(text);
        output.newLine();
        output.flush();
        output.close();
    }

    /**
     * Empty a file.
     *
     * @param urlFile The url file.
     *
     * @throws IOException if the file does not exist or cannot be read.
     */
    public static void emptyFile(String urlFile) throws IOException {

        //false means we overwrite
        final FileWriter fw = new FileWriter(urlFile, false);
        final BufferedWriter output = new BufferedWriter(fw);

        output.write("");
        output.flush();
        output.close();
    }



    /**
     * Read the contents of a file into string.
     *
     * @param f the file name
     * @return The file contents as string
     * @throws IOException if the file does not exist or cannot be read.
     */
    public static String getStringFromFile(File f) throws IOException {

        final StringBuilder sb  = new StringBuilder();
        final BufferedReader br = new BufferedReader(new FileReader(f));
        String line;
        while ((line = br.readLine()) != null){
            sb.append(line).append('\n');
        }
        br.close();
        return sb.toString();
    }
}
