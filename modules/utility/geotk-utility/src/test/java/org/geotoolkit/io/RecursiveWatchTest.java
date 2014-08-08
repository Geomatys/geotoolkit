package org.geotoolkit.io;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.UUID;

import static org.apache.sis.test.Assert.assertFalse;
import static org.apache.sis.test.Assert.assertTrue;
import static org.junit.Assert.assertEquals;

/**
 * Date: 08/08/14
 * Time: 10:12
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
     * Add a filter on directory names. 4 things must be checked here :
     * - Behavior for directories matching filter should not change.
     * - Roots which do not match the filter should not be watched.
     * - Changed sub-directories must be seen only if they match the filter.
     * - When removing the filter, the two last rules above must be cancelled.
     * @throws IOException
     */
    @Test(timeout=15000)
    public void directoryFilterTest() throws IOException, InterruptedException {
        watcher.setDirectoryFilter(FileSystems.getDefault().getPathMatcher("regex:.*"+ROOT_PREFIX+".*"));
        Path childDir = rootDir.resolve(ROOT_PREFIX);
        assertDirectoryCreated(childDir);

        // Test recursive creation
        Path previousDir = childDir;
        Path newFile;
        for (int i = 0 ; i < 10 ; i++) {
            // filter should not be applied on files, so we must see it.
            newFile = previousDir.resolve(UUID.randomUUID().toString()+".tmp");
            assertFileCreated(newFile);

            previousDir = previousDir.resolve(ROOT_PREFIX);
            assertDirectoryCreated(previousDir);
        }

        // All created folders match input pattern, we should be aware of each deletion.
        assertDeleteFileTree(childDir);

        // We should not see any of the following creation/deletion, as the filter has been set to match no given name.
        watcher.setDirectoryFilter(FileSystems.getDefault().getPathMatcher("glob:omitting"));
        Files.createDirectory(childDir);
        Thread.sleep(500);
        assertTrue("Creation event should not be propagated because of the directory filter.", results.isEmpty());

        previousDir = childDir;
        for (int i = 0 ; i < 2 ; i++) {
            // We won't see files as their parent directories do not match the filter.
            newFile = previousDir.resolve(UUID.randomUUID().toString()+".tmp");
            Files.createFile(newFile);
            Thread.sleep(500);
            assertTrue("Creation event should not be propagated because of the directory filter.", results.isEmpty());

            previousDir = previousDir.resolve(ROOT_PREFIX);
            Files.createDirectory(previousDir);
            Thread.sleep(500);
            assertTrue("Deletion event should not be propagated because of the directory filter.", results.isEmpty());
        }

        // Remove the directory filter. We should see all modification over the watched file-tree.
        watcher.setDirectoryFilter(null);
        assertDeleteFileTree(childDir);
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

        child = rootDir.resolve("0");
        assertDirectoryCreated(child);


        // Test recursive creation
        Path previousDir = child;
        Path matchingFile, ignoredFile;
        for (int i = 0 ; i < 10 ; i++) {
            // filter should not be applied on files, so we must see it.
            matchingFile = previousDir.resolve(UUID.randomUUID().toString()+".tif");
            assertFileCreated(matchingFile);

            ignoredFile = previousDir.resolve(UUID.randomUUID().toString()+".tmp");
            Files.createFile(ignoredFile);
            Thread.sleep(500);
            assertTrue("File deletion event should not be propagated cause of the file filter.", results.isEmpty());

            previousDir = previousDir.resolve(ROOT_PREFIX);
            assertDirectoryCreated(previousDir);
        }

        // Remove the file filter. All deleted file/folder should be seen by our listener.
        watcher.setFileFilter(null);
        assertDeleteFileTree(child);

    }

    /**
     * Delete a directory and all its content. For each deleted file / folder, we check event propagation.
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
