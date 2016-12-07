/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2009-2016, Geomatys
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation;
 *    version 2.1 of the License.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */
package org.geotoolkit.image.io.stream;

import org.geotoolkit.nio.IOUtilities;
import org.junit.Assert;
import org.junit.Assume;
import org.junit.Test;

import javax.imageio.stream.ImageOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.management.ManagementFactory;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Test Path implementation of ImageStream.
 *
 * @author Quentin Boileau (Geomatys)
 */
public class PathImageStreamTest {

    private boolean isUnix() {
        String os = System.getProperty("os.name").toLowerCase();
        if(os.contains("nix") || os.contains("nux") || os.indexOf("aix") > 0) {
            //check lsof tool exist
            try{
                Runtime.getRuntime().exec("lsof -h");
                return true;
            }catch(IOException ex){
                return false;
            }
        }
        return false;
    }

    /**
     * This test try to find unclosed stream created by PathImageOutputStreamSpi and PathImageInputStreamSpi
     * causing TooManyFileOpen errors.
     */
    @Test
    public void testUnclosedStream() throws Exception {
        Assume.assumeTrue(isUnix());

        String name1 = ManagementFactory.getRuntimeMXBean().getName();
        final String pid = name1.substring(0, name1.indexOf("@"));

        String imageName = "myImage.png";
        Path tempDirectory = Files.createTempDirectory("PathImageStreamTest");
        Path imagePath = tempDirectory.resolve(imageName);

        try {
            //test ImageOutputStream
            PathImageOutputStreamSpi spi = new PathImageOutputStreamSpi();
            Assert.assertFalse(isOpened(pid, imageName));
            try (ImageOutputStream imageOut = spi.createOutputStreamInstance(imagePath, true, null)) {
                //should be opened
                Assert.assertTrue(isOpened(pid, imageName));
            }
            Assert.assertFalse(isOpened(pid, imageName));

            Thread.sleep(1000);

            //test ImageInputStream
            Assert.assertFalse(isOpened(pid, imageName));
            try (PathImageInputStream imagein = new PathImageInputStream(imagePath)) {
                //should be opened
                Assert.assertTrue(isOpened(pid, imageName));
            }
            Assert.assertFalse(isOpened(pid, imageName));
        } finally {
            IOUtilities.deleteSilently(tempDirectory);
        }
    }

    /**
     * Execute lsof command with test runner pid and search if fileReference is opened.
     *
     * @param pid
     * @param fileReference
     * @return true if opened, false otherwise
     */
    private boolean isOpened(String pid, String fileReference) throws Exception {

        String command = "lsof -p " + pid;
        Process p = Runtime.getRuntime().exec(command);
        p.waitFor();

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()))) {
            String line = "";
            while ((line = reader.readLine()) != null) {
                if (line.contains(fileReference)) {
                    return true;
                }
            }
        }
        return false;
    }


}
