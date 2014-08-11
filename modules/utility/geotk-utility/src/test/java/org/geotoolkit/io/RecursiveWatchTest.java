package org.geotoolkit.io;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.UUID;

import static org.apache.sis.test.Assert.assertTrue;

/**
 * A test class to ensure recursive survey mechanism of {@link org.geotoolkit.io.DirectoryWatcher} is working properly.
 *
 * @author Alexis Manin (Geomatys)
 */
public class RecursiveWatchTest extends DirectoryWatcherTest {

    @Before
    public void initWatcher() throws IOException {
        watcher = new DirectoryWatcher(true);
        watcher.addPathChangeListener(new MockListener(results));
        watcher.register(rootDir);
        watcher.start();
    }

    @After
    public void closeWatcher() throws IOException {
        watcher.stop();
        watcher.close();
    }


    /**
     * A simple test in which we'll try to create / delete files and directories recursively, and ensure events are
     * propagated.
     */
    @Test(timeout=5000)
    public void testRecursivity() throws IOException, InterruptedException {
        Path childFile = rootDir.resolve("0.tmp");
        assertFileCreated(childFile);
        assertFileDeleted(childFile);

        Path childDir = rootDir.resolve("0");
        assertDirectoryCreated(childDir);

        // Test recursive creation
        Path previousDir = childDir;
        Path newFile;
        for (int i = 0 ; i < 10 ; i++) {
            newFile = previousDir.resolve(UUID.randomUUID().toString()+".tmp");
            assertFileCreated(newFile);
            previousDir = previousDir.resolve(UUID.randomUUID().toString());
            assertDirectoryCreated(previousDir);
        }

        //Test recursive removal
        assertDeleteFileTree(childDir);
    }

    /**
     * Add a root directory to the watcher component, and check events are well propagated. Then we'll unregister it,
     * and check events are no longer caught.
     *
     * @throws IOException
     */
    @Test(timeout=5000)
    public void multipleRootTest() throws IOException, InterruptedException {
        final Path newRoot = Files.createTempDirectory("DirectoryWatcherTest2");
        watcher.register(newRoot);

        Path childDir = newRoot.resolve("0");
        assertDirectoryCreated(childDir);

        // Test recursivity
        Path subDir = childDir.resolve("0");
        assertDirectoryCreated(subDir);

        // Test recursivity
        Path subFile = subDir.resolve("0.tmp");
        assertFileCreated(subFile);
        assertFileDeleted(subFile);

        // Try to remove a root
        watcher.unregister(newRoot);

        Files.delete(subDir);
        Thread.sleep(500);
        assertTrue("We are on a removed root. sub-directory's changes should not be seen.", results.isEmpty());

        Files.delete(childDir);
        Thread.sleep(500);
        assertTrue("We are on a removed root. sub-directory's changes should not be seen.", results.isEmpty());

        Files.delete(newRoot);
        Thread.sleep(500);
        assertTrue("We are on a removed root. sub-directory's changes should not be seen.", results.isEmpty());
    }

    /**
     * Add a filter on file names. We check :
     * - Changed files which don't match the filter should not be seen.
     * - Changed files matching filter should be seen.
     * - Remove filter makes all changed files visible.
     * @throws Exception
     */
    @Test(timeout=10000)
    public void fileFilterTest() throws Exception {
        // Add a file filter
        watcher.setFileFilter(FileSystems.getDefault().getPathMatcher("regex:(?i).*\\.tif"));

        Path child = rootDir.resolve("file.tmp");
        Files.createFile(child);
        Thread.sleep(500);
        assertTrue("File creation event should not be propagated cause of the file filter.", results.isEmpty());

        Files.delete(child);
        Thread.sleep(500);
        assertTrue("File deletion event should not be propagated cause of the file filter.", results.isEmpty());

        child = rootDir.resolve("testDir.tif");
        assertDirectoryCreated(child);

        // Test recursive creation
        Path previousDir = child;
        Path matchingFile, ignoredFile;
        for (int i = 0 ; i < 5 ; i++) {

            // filter should not be applied on files, so we must see it.
            matchingFile = previousDir.resolve(""+i+".tif");
            assertFileCreated(matchingFile);

            ignoredFile = previousDir.resolve(""+i+".tmp");
            Files.createFile(ignoredFile);
            Thread.sleep(500);
            assertTrue("File deletion event should not be propagated cause of the file filter.", results.isEmpty());

            // check that a directory matching filter is seen
            assertDirectoryCreated(previousDir.resolve("temp.tif"));

            // Check a directory which doesn't match filter.
            previousDir = previousDir.resolve(""+i);
            Files.createDirectory(previousDir);
            Thread.sleep(500);
            assertTrue("File deletion event should not be propagated cause of the file filter.", results.isEmpty());
        }

        // Remove the file filter. All deleted file/folder should be seen by our listener.
        watcher.setFileFilter(null);
        assertDeleteFileTree(child);
    }

    /**
     * Delete a directory and all its content. For each deleted file / folder, we check event propagation.
     *
     * @param root The folder to delete.
     * @throws IOException If input path does not represent a directory, or if an unexpected error happened while removal.
     */
    public void assertDeleteFileTree(final Path root) throws IOException {
        Files.walkFileTree(root, new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                try {
                    assertDirectoryDeleted(dir);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                return super.postVisitDirectory(dir, exc);
            }

            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                try {
                    assertFileDeleted(file);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                return super.visitFile(file, attrs);
            }
        });
    }
}
