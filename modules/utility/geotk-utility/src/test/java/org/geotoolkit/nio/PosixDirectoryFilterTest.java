package org.geotoolkit.nio;

import org.junit.Test;

import java.io.IOException;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * @author Quentin Boileau (Geomatys)
 */
public class PosixDirectoryFilterTest {

    @Test
    public void testFilter() throws IOException {

        Path rootDir = Files.createTempDirectory("root");
        try {
            fillDirectory(rootDir);

            try (DirectoryStream<Path> stream = Files.newDirectoryStream(rootDir, new PosixDirectoryFilter("*.png"))) {
                for (Path path : stream) {
                    assertEquals("image.png", path.getFileName().toString());
                }
            }

            List<String> foundFileName = new ArrayList<>();
            try (DirectoryStream<Path> stream = Files.newDirectoryStream(rootDir, new PosixDirectoryFilter("image.*"))) {
                for (Path path : stream) {
                    foundFileName.add(path.getFileName().toString());
                }
            }
            assertEquals(3, foundFileName.size());
            assertTrue(foundFileName.contains("image.jpg"));
            assertTrue(foundFileName.contains("image.PNG"));
            assertTrue(foundFileName.contains("image.png"));
        } finally {
            IOUtilities.deleteRecursively(rootDir);
        }
    }

    @Test
    public void testFilterCaseUnsensitive() throws IOException {

        Path rootDir = Files.createTempDirectory("root");
        try {
            fillDirectory(rootDir);

            List<String> foundFileName = new ArrayList<>();
            try (DirectoryStream<Path> stream = Files.newDirectoryStream(rootDir, new PosixDirectoryFilter("*.png", true))) {
                for (Path path : stream) {
                    foundFileName.add(path.getFileName().toString());
                }
            }
            assertEquals(2, foundFileName.size());
            assertTrue(foundFileName.contains("image.PNG"));
            assertTrue(foundFileName.contains("image.png"));

            foundFileName.clear();
            try (DirectoryStream<Path> stream = Files.newDirectoryStream(rootDir, new PosixDirectoryFilter("md.*", true))) {
                for (Path path : stream) {
                    foundFileName.add(path.getFileName().toString());
                }
            }
            assertEquals(2, foundFileName.size());
            assertTrue(foundFileName.contains("md.txt"));
            assertTrue(foundFileName.contains("MD.xml"));
        } finally {
            IOUtilities.deleteRecursively(rootDir);
        }
    }

    private void fillDirectory(Path rootDir) throws IOException {
        Files.createFile(rootDir.resolve("image.png"));
        Files.createFile(rootDir.resolve("image.PNG"));
        Files.createFile(rootDir.resolve("image.jpg"));
        Files.createFile(rootDir.resolve("md.txt"));
        Files.createFile(rootDir.resolve("MD.xml"));
    }
}
