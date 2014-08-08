package org.geotoolkit.io;

import org.junit.After;
import org.junit.Before;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

/**
 * Initialisation for the tests over {@link org.geotoolkit.io.DirectoryWatcher} component, which is an use of {@link java.nio.file.WatchService} API
 * for recursive file-system change survey. See {@link org.geotoolkit.io.DirectWatchTest} and {@link org.geotoolkit.io.RecursiveWatchTest}
 * for the real test cases.
 *
 * @author Alexis Manin (Geomatys)
 */
public class DirectoryWatcherTest {

    protected static final String ROOT_PREFIX = "DirectoryWatcherTest";

    protected DirectoryWatcher watcher;

    protected Path rootDir;
    protected final ArrayList<PathChangedEvent> results = new ArrayList<>();

    @Before
    public void initTestDirectory() throws IOException {
        rootDir = Files.createTempDirectory(ROOT_PREFIX);
    }

    @After
    public void deleteTestDirectory() throws IOException {
        // Recursively delete all content of test root.
        Files.walkFileTree(rootDir, new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                    Files.delete(dir);

                return super.postVisitDirectory(dir, exc);
            }

            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                Files.delete(file);
                return super.visitFile(file, attrs);
            }
        });
    }

    /**
     * Create a directory (its parent must exist) and check the event propagation.
     * @param toCreate The path to create a folder from.
     * @throws IOException If a problem happened at directory creation
     * @throws InterruptedException If we cannot wait for event propagation.
     */
    public void assertDirectoryCreated(Path toCreate) throws IOException, InterruptedException {
        synchronized (results) {
            Files.createDirectory(toCreate);
            results.wait();
        }
        assertFalse("Event stack should not be empty", results.isEmpty());
        assertEquals("Event path should denote the created folder.", toCreate, results.get(0).target);
        assertEquals("Event type should be \"entry created\"", StandardWatchEventKinds.ENTRY_CREATE, results.get(0).kind);
        results.clear();
    }

    /**
     * Delete a directory (it must be empty) and check the event propagation.
     * @param toDelete The folder to remove.
     * @throws IOException If a problem happened at directory deletion
     * @throws InterruptedException If we cannot wait for event propagation.
     */
    public void assertDirectoryDeleted(Path toDelete) throws IOException, InterruptedException {
        synchronized (results) {
            Files.delete(toDelete);
            results.wait();
        }
        assertFalse("Event stack should not be empty", results.isEmpty());
        assertEquals("Event path should denote the deleted folder.", toDelete, results.get(0).target);
        assertEquals("Event type should be \"entry deleted\"", StandardWatchEventKinds.ENTRY_DELETE, results.get(0).kind);
        results.clear();
    }

    /**
     * Create a file (its parent must exist) and check the event propagation.
     * @param toCreate The path to create a file from.
     * @throws IOException If a problem happened at file creation
     * @throws InterruptedException If we cannot wait for event propagation.
     */
    public void assertFileCreated(Path toCreate) throws IOException, InterruptedException {
        synchronized (results) {
            Files.createFile(toCreate);
            results.wait();
        }
        assertFalse("Event stack should not be empty", results.isEmpty());
        assertEquals("Event path should denote the created file.", toCreate, results.get(0).target);
        assertEquals("Event type should be \"entry created\"", StandardWatchEventKinds.ENTRY_CREATE, results.get(0).kind);
        results.clear();
    }

    /**
     * Delete a file and check the event propagation.
     * @param toDelete The file to remove.
     * @throws IOException If a problem happened at file deletion
     * @throws InterruptedException If we cannot wait for event propagation.
     */
    public void assertFileDeleted(Path toDelete) throws IOException, InterruptedException {
        synchronized (results) {
            Files.delete(toDelete);
            results.wait();
        }
        assertFalse("Event stack should not be empty", results.isEmpty());
        assertEquals("Event path should denote the deleted file.", toDelete, results.get(0).target);
        assertEquals("Event type should be \"entry deleted\"", StandardWatchEventKinds.ENTRY_DELETE, results.get(0).kind);
        results.clear();
    }

    protected static class MockListener implements PathChangeListener {

        final ArrayList<PathChangedEvent> events;

        public MockListener(final ArrayList<PathChangedEvent> list) {
            events = list;
        }

        @Override
        public void pathChanged(PathChangedEvent event) {
            synchronized(events) {
                events.add(event);
                events.notify();
            }
        }
    }
}
