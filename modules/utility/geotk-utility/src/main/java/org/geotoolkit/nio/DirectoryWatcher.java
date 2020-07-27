package org.geotoolkit.nio;

import org.apache.sis.util.ArgumentChecks;
import org.apache.sis.util.logging.Logging;

import javax.swing.event.EventListenerList;
import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.HashSet;
import java.util.logging.Level;
import java.util.logging.Logger;

import static java.nio.file.StandardWatchEventKinds.*;

/**
 * Watch for file-system modification for a given folder. There's a recursive mode to allow watch of all sub-directories
 * as well. If the recursive mode is activated, when a new directory is created in the watched one, it will be registered
 * for watching, as all its sub-directories.
 * <p/>
 * Watched folders are registered for the standard event types defined by {@link java.nio.file.StandardWatchEventKinds}.
 * <p/>
 *
 * Use of the class :
 *
 * - Build a new watcher, with a boolean argument to specify if we want recursive survey (true) or not (false) :
 * final DirectoryWatcher watcher = new DirectoryWatcher(true) {..your implementation...};
 * <p/>
 * - Give it the separate folders to work on :
 * final Path toWatch1, toWatch2;
 * watcher.register(toWatch1, toWatch2);
 * <p/>
 * - Your watcher is ready to go, call the {@linkplain #start()} method to launch the survey.
 * /!\ It launches an asynchronous survey in a separate thread.
 * <p/>
 * - To stop|pause the watching, you just have to call {@linkplain #stop()} method.
 * <p/>
 * - Finally, when you're done with survey, please close your watcher using {@linkplain #close()}.
 * <p/>
 *
 * N.B : Thread-safe implementation.
 *
 * @author Alexis Manin (Geomatys)
 */
public class DirectoryWatcher implements Closeable {

    private static final Logger LOGGER = Logging.getLogger("org.geotoolkit.io");

    private final WatchService service;
    private final Thread watchThread;

    /**
     * True if we listen changes of all the file tree, or only for the direct children of the registered folders.
     */
    public final boolean recursive;

    protected final HashSet<Path> roots = new HashSet<>();
    protected final HashSet<Path> unregistered = new HashSet<>();

    /**
     * Filter used to determine which files must be ignored and which must be used.
     */
    protected DirectoryStream.Filter<Path> fileFilter = null;
    private final Object fileFilterLock = new Object();

    protected final EventListenerList listeners = new EventListenerList();

    private RegistrationErrorHandler registrationErrorHandler = logThenContinue();

    /**
     * Prepare watcher for NON-recursive survey.
     *
     * @throws IOException
     */
    public DirectoryWatcher() throws IOException {
        this(false);
    }

    /**
     * Initialize watching service.
     *
     * @param recursive True if we want to watch over the entire file tree, false if we want to be notified only for
     *                  direct childrens of root folder(s).
     * @throws IOException If an error occurred while building underlying {@link java.nio.file.WatchService}.
     */
    public DirectoryWatcher(final boolean recursive) throws IOException {
        service = FileSystems.getDefault().newWatchService();
        this.recursive = recursive;

        watchThread = new Thread(new Runnable() {
            @Override
            public void run() {
                watch();
            }
        });
        watchThread.setDaemon(true);
    }

    public void setRegistrationErrorHandler(RegistrationErrorHandler handler) {
        if (handler == null) registrationErrorHandler = logThenContinue();
        else registrationErrorHandler = handler;
    }

    /**
     * Provide a filter to specify if a file must processed or ignored at change.
     *
     * @param filter A PathMatcher whose {@link java.nio.file.PathMatcher#matches(java.nio.file.Path)} method return
     *               true if we must process the path in parameter. If null, all changed files are processed.
     */
    public void setFileFilter(final DirectoryStream.Filter<Path> filter) {
        synchronized (fileFilterLock) {
            fileFilter = filter;
        }
    }

    /**
     * @return the filter used to know if we must process or not a file. Null if no filtering applied.
     */
    public DirectoryStream.Filter<Path> getFileFilter() {
        synchronized (fileFilterLock) {
            return fileFilter;
        }
    }

    /**
     * Register a directory to the WatchService. If it does not exists, we'll try to create it.
     *
     * In case this method throws an error, the state of the watcher is undetermined, because it may have succeed an
     * unknown number of directories before failure.
     *
     * @param paths A list of folders to watch. If one does not exists, we will try to create it.
     * @throws java.io.IOException                      If an input folder does not exists and cannot be created, or if an error occurred
     *                                                  trying to register it or one of its children.
     * @throws java.nio.file.FileAlreadyExistsException If an input path denotes a file and not a folder.
     * @throws SecurityException                        If an input directory cannot be watched due to a lack of permission.
     */
    public synchronized void register(Path... paths) throws IOException, SecurityException {
        for (Path toWatch : paths) {
            if (!roots.contains(toWatch)) {
                try {
                    if (!Files.isDirectory(toWatch)) {
                        Files.createDirectories(toWatch);
                    }
                } catch (Exception e) {
                    registrationErrorHandler.handle(new RegistrationError(toWatch, true, e));
                }

                registerRoot(toWatch);
                roots.add(toWatch);
            }
            // In case someone asked for its removal before.
            unregistered.remove(toWatch);
        }
    }

    public synchronized void unregister(Path... paths) {
        for (Path dir : paths) {
            unregistered.add(dir);
            // in case it's a previously registered folder which is queried for removal, and not one of its children.
            roots.remove(dir);
        }
    }

    /**
     * Register a directory along with all its sub-directories to the WatchService.
     */
    private final void registerDir(final Path dir) throws IOException, SecurityException {
        dir.register(service, ENTRY_CREATE, ENTRY_DELETE, ENTRY_MODIFY);

        if (recursive) {

            Files.walkFileTree(dir, new SimpleFileVisitor<Path>() {

                @Override
                public FileVisitResult preVisitDirectory(Path subdir, BasicFileAttributes attrs)
                        throws IOException {
                    subdir.register(service, ENTRY_CREATE, ENTRY_DELETE, ENTRY_MODIFY);
                    return FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult visitFileFailed(Path file, IOException exc) throws IOException {
                    LOGGER.log(Level.FINE, "Cannot register a newly created directory", exc);
                    return FileVisitResult.SKIP_SUBTREE;
                }
            });
        }
    }

    /**
     * Try to register directories directly specified by user. It uses a specific error handling mecanism that allow for
     * a better control of failure. For more information, see {@link RegistrationErrorHandler}, {@link #rethrow()},
     * {@link #logThenContinue()}.
     */
    private final void registerRoot(final Path root) throws IOException, SecurityException {
        try {
            root.register(service, ENTRY_CREATE, ENTRY_DELETE, ENTRY_MODIFY);
        } catch (Exception e) {
            registrationErrorHandler.handle(new RegistrationError(root, true, e));
        }

        if (recursive) {
            Files.walkFileTree(root, new SimpleFileVisitor<Path>() {

                @Override
                public FileVisitResult preVisitDirectory(Path subdir, BasicFileAttributes attrs)
                        throws IOException {
                    try {
                        subdir.register(service, ENTRY_CREATE, ENTRY_DELETE, ENTRY_MODIFY);
                    } catch (Exception e) {
                        registrationErrorHandler.handle(new RegistrationError(subdir, false, e));
                    }
                    return FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult visitFileFailed(Path file, IOException exc) throws IOException {
                    registrationErrorHandler.handle(new RegistrationError(file, false, exc));
                    // If we reach there, it means we're not in fail-fast mode, and must just ignore this file
                    return FileVisitResult.SKIP_SUBTREE;
                }
            });
        }
    }

    /**
     * Start to survey the registered folders (see {@linkplain #register(java.nio.file.Path...)}
     */
    private void watch() {
        final Thread currentThread = Thread.currentThread();

        while (!currentThread.isInterrupted()) {
            final WatchKey key;
            try {
                key = service.take();
            } catch (ClosedWatchServiceException e) {
                LOGGER.log(Level.INFO, "Folder watcher has been closed !");
                break;
            } catch (InterruptedException e) {
                LOGGER.log(Level.INFO, "Folder watcher has been interrupted !");
                currentThread.interrupt();
                break;
            }

            // Do the check here, because we don't know how many time we have waited for an event, nor what happened.
            if (currentThread.isInterrupted()) {
                break;
            }

            // If could happen if we've cancelled if and it was already waiting for new events. We just have to ignore it.
            if (!key.isValid()) {
                continue;
            }

            // TODO : Change if an implementation of watchable other than path appears in the jvm.
            final Path watchedPath = (Path) key.watchable();

            try {
                // Check if we must stop following the current folder.
                final Path dereferenced = getUnregisteredParent(watchedPath);
                if (dereferenced != null) {
                    key.cancel();
                    // If current event path represents the exact path to remove, not just a children, we can forget about it.
                    if (dereferenced.equals(watchedPath)) {
                        synchronized (unregistered) {
                            unregistered.remove(dereferenced);
                        }
                    }
                    continue;
                }

                for (WatchEvent event : key.pollEvents()) {

                    WatchEvent.Kind kind = event.kind();
                    // Watch service received too many events from the system and cannot handle them.
                    if (kind == OVERFLOW) {
                        LOGGER.log(Level.INFO, "Too many changes happened to the watched directory. Unable to catch them all.");
                        continue;
                    }

                    try {
                        Path target;
                        final Object context = event.context();
                        if (context instanceof Path) {
                            target = (Path) context;
                        } else if (context instanceof File) {
                            target = ((File) context).toPath();
                        } else {
                            // Create an exception to be able to retrieve code fragment from message, which will allow to quickly add new cases if needed.
                            final IllegalArgumentException e = new IllegalArgumentException("Watch event is of unknown type (need Path or File).");
                            LOGGER.log(Level.INFO, "Watch event skipped.", e);
                            continue;
                        }

                        target = watchedPath.resolve(target);

                        final boolean isDirectory = Files.isDirectory(target);
                        // Add the new directory to the watched ones if we're on recursive mode.
                        if (isDirectory && recursive && kind == ENTRY_CREATE) {
                            try {
                                registerDir(target);
                            } catch (IOException e) {
                                LOGGER.log(Level.WARNING, "Newly created folder cannot be watched : " + target, e);
                            }
                        }

                        final boolean matchFileFilter;
                        synchronized (fileFilterLock) {
                            matchFileFilter = (fileFilter == null || fileFilter.accept(target));
                        }
                        if (matchFileFilter) {
                            firePathChanged(target, kind, isDirectory, event.count());
                        }

                    } catch (Exception e) {
                        // We don't want the entire mechanism to be destroyed for a simple error on a file.
                        LOGGER.log(Level.WARNING, "An error occurred while processing " + event.context() + " for the event " + kind, e);
                    }
                }

            } finally {
            /* Finished work with key, reset it. If we cannot, it means the bound directory is not watched anymore, we can
               remove it from our list of surveyed folders.
             */
                if (!key.reset()) {
                    synchronized (roots) {
                        roots.remove(watchedPath);
                    }
                }
            }
        }
    }

    protected Path getUnregisteredParent(final Path target) {
        synchronized (unregistered) {
            for (Path path : unregistered) {
                if (target.startsWith(path) || path.equals(target)) {
                    return path;
                }
            }
        }
        return null;
    }

    public void start() {
        watchThread.start();
    }

    public void stop() {
        watchThread.interrupt();
    }

    @Override
    public synchronized void close() throws IOException {
        stop();
        synchronized (roots) {
            roots.clear();
        }
        service.close();
    }

    public void addPathChangeListener(final PathChangeListener listener) {
        ArgumentChecks.ensureNonNull("Event listener", listener);
        listeners.add(PathChangeListener.class, listener);
    }

    public void removePathChangeListener(final PathChangeListener toForget) {
        ArgumentChecks.ensureNonNull("Event listener", toForget);
        listeners.remove(PathChangeListener.class, toForget);
    }

    /**
     * Each time the watch service will catch an event on a file/folder, it will use this method. It allows user defining
     * its own processes over file changes.
     *
     * N.B : If an exception is thrown by a listener, it is stored and thrown back after all listeners have been executed.
     * If another listener throw an exception, it is stored via {@link java.lang.Exception#addSuppressed(Throwable)}.
     *
     * @param target      The file which have been triggered for changes.
     * @param kind        The kind of change which applied on the file. Most likely one of the {@link java.nio.file.StandardWatchEventKinds}.
     * @param isDirectory A boolean which is set to true if the input path is a directory, false otherwise.
     * @param count       Number of times the same event occurred.
     * @throws java.lang.Exception if a listener throw an exception.
     */
    protected void firePathChanged(Path target, WatchEvent.Kind kind, boolean isDirectory, int count) throws Exception {
        final PathChangedEvent evt = new PathChangedEvent(this, target, kind, isDirectory, count);
        Exception traced = null;
        for (PathChangeListener l : listeners.getListeners(PathChangeListener.class)) {
            try {
                l.pathChanged(evt);
            } catch (Exception e) {
                if (traced == null) {
                    traced = e;
                } else {
                    traced.addSuppressed(e);
                }
            }
        }
        if (traced != null) {
            throw traced;
        }
    }

    /**
     * Skip registration of failed path, but continue other directories integration after.
     *
     * This log the error as a warning for root directories (directly specified by user) and as a debug log for
     * others (subtrees in recursive registration).
     */
    public static RegistrationErrorHandler logThenContinue() {
        return event -> LOGGER.log(
                event.isRoot ? Level.WARNING : Level.FINE,
                event.error,
                () -> String.format("Cannot register %sdirectory: %s", event.isRoot ? "root " : "sub-", event.target)
        );
    }

    /**
     * Fail-first strategy: rethrow {@link RegistrationError#error occurred error}. Consequence is that any further registering
     * process is cancelled at this point, and the watcher will propagate error to the caller of {@link DirectoryWatcher#register(Path...)}.
     */
    public static RegistrationErrorHandler rethrow() {
        return DirectoryWatcher::rethrowError;
    }

    private static void rethrowError(RegistrationError info) throws IOException, RuntimeException {
        final Exception error = info.error;
        if (error instanceof IOException) throw (IOException) error;
        else if (error instanceof RuntimeException) throw (RuntimeException) error;
        else throw new RuntimeException("Cannot register directory", error);
    }

    public final class RegistrationError {

        final Path target;
        final boolean isRoot;
        final Exception error;

        private RegistrationError(Path target, boolean isRoot, Exception error) {
            this.target = target;
            this.isRoot = isRoot;
            this.error = error;
        }
    }

    /**
     * Behavior to adopt when an error occurs while registering a directory. Tipically, propagating the exception will
     * stop the entire registration process, and returning successfully means that we should ignore the directory and
     * continue processing.
     */
    @FunctionalInterface
    public interface RegistrationErrorHandler {
        void handle(RegistrationError errorInfo) throws IOException;
    }
}
