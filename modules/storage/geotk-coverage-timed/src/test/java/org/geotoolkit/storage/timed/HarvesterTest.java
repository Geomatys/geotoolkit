/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2017, Geomatys
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
package org.geotoolkit.storage.timed;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import org.junit.Assert;
import org.junit.Assume;
import org.junit.Test;

/**
 *
 * @author Alexis Manin (Geomatys)
 */
public class HarvesterTest extends DirectoryBasedTest {

    @Test
    public void harvestEmptyDir() throws IOException, InterruptedException {
        Assume.assumeTrue(System.getProperty("os.name").toLowerCase().contains("linux"));
        final long delay = 20;
        final TimeUnit delayUnit = TimeUnit.MILLISECONDS;

        final List<Path> witness = new ArrayList<>();
        final List<Path> harvested = new ArrayList<>();
        try (final Harvester harvester = new Harvester(dir, set -> set.spliterator().forEachRemaining(harvested::add), delayUnit, delay)) {

            // Define a set of paths which must be harvested.
            final Path first = Files.createTempFile(dir, "first", ".tmp");
            final Path second = Files.createTempFile(dir, "second", ".tmp");
            final Path first2 = Files.createTempFile(dir, "first", ".tmp2");

            // Define a path outside of queried directory to ensure it will be ignored
            final Path toIgnore = Files.createTempFile("ignored", ".tmp");

            // Now, we'll ensure that sub-directories are ignored.
            final Path subDir = Files.createTempDirectory(dir, "sub");
            final Path subFile = Files.createTempFile(subDir, "sub", ".tmp");

            // Data is consumed after a delay, so we wait before checking.
            synchronized (this) {
                wait(delay * 4);
            }

            Assert.assertTrue("Missing file", harvested.contains(first));
            Assert.assertTrue("Missing file", harvested.contains(second));
            Assert.assertTrue("Missing file", harvested.contains(first2));

            Assert.assertFalse("File should be ignored", harvested.contains(toIgnore));
            Assert.assertFalse("File should be ignored", harvested.contains(subDir));
            Assert.assertFalse("File should be ignored", harvested.contains(subFile));
        }
    }
}
