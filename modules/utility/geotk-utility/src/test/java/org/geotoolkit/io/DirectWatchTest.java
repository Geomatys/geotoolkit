package org.geotoolkit.io;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardWatchEventKinds;

import static org.apache.sis.test.Assert.assertFalse;
import static org.apache.sis.test.Assert.assertTrue;
import static org.junit.Assert.assertEquals;

/**
 * Test the {@link org.geotoolkit.io.DirectoryWatcher} component in NON-recursive mode.
 *
 * As we are managing events, we cannot know when our test listener will receive information. Found solution is to
 * synchronize our listener and our test over the {@linkplain org.geotoolkit.io.DirectoryWatcherTest#results} list.
 *
 * @author Alexis Manin (Geomatys)
 */
public class DirectWatchTest extends DirectoryWatcherTest {

    @Before
    public void initWatcher() throws IOException {
        watcher = new DirectoryWatcher(false);
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
     * A simple test in which we'll try to create / delete a DIRECTORY into watched root, and ensure events are
     * propagated.
     */
    @Test(timeout=5000)
    public void workWithDirectories() throws IOException, InterruptedException {
        Path childDir = rootDir.resolve("0");
        assertDirectoryCreated(childDir);
        assertDirectoryDeleted(childDir);
    }

    /**
     * A simple test in which we'll try to create / delete a FILE into watched root, and ensure events are
     * propagated.
     */
    @Test(timeout=5000)
    public void workWithFiles() throws IOException, InterruptedException {
        Path childFile = rootDir.resolve("0.tmp");
        assertFileCreated(childFile);
        assertFileDeleted(childFile);
    }

    /**
     * A test to ensure our watcher do not survey the complete file tree, but only direct children of our root.
     */
    @Test(timeout=5000)
    public void testNonRecursivity() throws IOException, InterruptedException {
        Path childDir = rootDir.resolve("directChild");
        assertDirectoryCreated(childDir);

        // Test we don't check sub-directories
        Path subChild = childDir.resolve("0");
        Files.createDirectory(subChild);
        Thread.sleep(500);
        assertTrue("We are in non-recursive mode. sub-directory's children should not be seen.", results.isEmpty());

        Files.delete(subChild);
        Thread.sleep(500);
        assertTrue("We are in non-recursive mode. sub-directory's children should not be seen.", results.isEmpty());

        // Test we don't see files in child directories.
        Files.createFile(subChild);
        Thread.sleep(500);
        assertTrue("We are in non-recursive mode. sub-directory's children should not be seen.", results.isEmpty());

        Files.delete(subChild);
        Thread.sleep(500);
        assertTrue("We are in non-recursive mode. sub-directory's children should not be seen.", results.isEmpty());
    }

    /**
     * Add a root directory to the watcher component, and check events are well propagated. Then we'll unregister it,
     * and check events are no longer caught.
     *
     * We also check there's no recursive watching for this new root.
     *
     * @throws IOException
     */
    @Test(timeout=5000)
    public void multipleRootTest() throws IOException, InterruptedException {
        final Path newRoot = Files.createTempDirectory("DirectoryWatcherTest2");
        watcher.register(newRoot);

        Path childDir = newRoot.resolve("0");
        assertDirectoryCreated(childDir);

        // Test we don't check sub-directories
        Path subDir = childDir.resolve("0");
        Files.createDirectory(subDir);
        Thread.sleep(500);
        assertTrue("We are in non-recursive mode. sub-directory's children should not be seen.", results.isEmpty());

        // Try to remove a root
        watcher.unregister(newRoot);

        Files.delete(subDir);
        Thread.sleep(500);
        assertTrue("We are in non-recursive mode, on a removed root. sub-directory's changes should not be seen.", results.isEmpty());

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
    @Test(timeout=5000)
    public void directoryFilterTest() throws IOException, InterruptedException {
        watcher.setDirectoryFilter(FileSystems.getDefault().getPathMatcher("regex:.*"+ROOT_PREFIX+".*"));
        Path childDir = rootDir.resolve(ROOT_PREFIX);
        assertDirectoryCreated(childDir);

        watcher.setDirectoryFilter(FileSystems.getDefault().getPathMatcher("glob:omitting"));
        Files.delete(childDir);
        Thread.sleep(1000);
        assertTrue("Deletion event should not be propagated cause of the directory filter.", results.isEmpty());

        // Remove the directory filter
        watcher.setDirectoryFilter(null);
        assertDirectoryCreated(childDir);
        assertDirectoryDeleted(childDir);
    }

    /**
     * Add a filter on file names. We check :
     * - Changed files which don't match the filter should not be seen.
     * - Changed files matching filter should be seen.
     * - Remove filter makes all changed files visible.
     * @throws Exception
     */
    @Test(timeout=5000)
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

        child = rootDir.resolve("file.tif");
        assertFileCreated(child);
        assertFileDeleted(child);

        // Remove the file filter
        watcher.setFileFilter(null);
        child = rootDir.resolve("1");
        assertFileCreated(child);
        assertFileDeleted(child);
    }
}
