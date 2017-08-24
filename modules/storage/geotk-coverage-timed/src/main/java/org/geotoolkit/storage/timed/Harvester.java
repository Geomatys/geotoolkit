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

import java.io.Closeable;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchService;
import java.util.Iterator;
import java.util.Objects;
import java.util.concurrent.DelayQueue;
import java.util.concurrent.Delayed;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.geotoolkit.nio.DirectoryWatcher;
import org.geotoolkit.nio.PathChangeListener;
import org.geotoolkit.nio.PathChangedEvent;

/**
 * Analyze and survey a given directory content, regrouping files with same name
 * but different extensions in a single {@link FileSet}, then send it to some
 * {@link Consumer} passed at built.
 *
 * Implementation notes :
 * <ol>
 * <li>At built, we scan the entire directory content and send it to the
 * parameterized consumer.</li>
 * <li>Note 2 : Directories are ignored, and no recursivity is done to
 * scan/watch folder's content.</li>
 * <li>To watch a directory's activity, we use {@link DirectoryWatcher} utility,
 * itself based on Java 7 {@link WatchService} API.</li>
 * <li>When a change is detected on a file, it is not consumed immediately. We
 * apply a delay on it, to ensure subsequent modifications won't disturb the
 * consumption process. Anytime an event is triggered, the delay on the object
 * is reset.</li>
 *</ol>
 *
 * @author Alexis Manin (Geomatys)
 */
class Harvester implements PathChangeListener, Closeable {

    private final Path source;

    private final Consumer<FileSet> target;

    private final DelayQueue<DelayedFileSet> delayQueue;
    private final ExecutorService indexThread;

    private final DirectoryWatcher watcher;
    private final TimeUnit delayUnit;
    private final long delay;

    final Path resetTrigger;

    Harvester(final Path source, final Consumer<FileSet> target, final TimeUnit delayUnit, final long delay) throws IOException {
        this(source, target, delayUnit, delay, null);
    }

    Harvester(final Path source, final Consumer<FileSet> target, final TimeUnit delayUnit, final long delay, final Path resetTrigger) throws IOException {
        this.source = source;
        this.target = target;
        this.delayUnit = delayUnit;
        this.delay = delay;
        this.resetTrigger = resetTrigger;

        delayQueue = new DelayQueue<>();

        indexThread = Executors.newSingleThreadExecutor();
        indexThread.submit(this::harvest);

        /* Before watching directory, we'll scan current content of the folder.
         * We must do it in case files have been added while harvester was shut
         * down.
         */
        try (DirectoryStream<Path> dirStream = Files.newDirectoryStream(source, Files::isRegularFile)) {
            final Iterator<Path> it = dirStream.iterator();
            try {
                while (it.hasNext()) {
                    final DelayedFileSet dfs = new DelayedFileSet(it.next(), delayUnit, delay);
                    updateFileSet(dfs);
                }
            } catch (Exception e) {
                TimedUtils.LOGGER.log(Level.WARNING, "A file cannot be harvested.", e);
            }
        }
        watcher = new DirectoryWatcher(false);
        watcher.addPathChangeListener(this);
        watcher.register(source);
        watcher.start();
    }

    @Override
    public void pathChanged(PathChangedEvent event) {
        final boolean isDeleted = StandardWatchEventKinds.ENTRY_DELETE.equals(event.kind);
        if (event.isDirectory && !isDeleted)
            return;
        final DelayedFileSet dfs = new DelayedFileSet(event.target, delayUnit, delay);
        if (StandardWatchEventKinds.ENTRY_CREATE.equals(event.kind) || StandardWatchEventKinds.ENTRY_MODIFY.equals(event.kind)) {
                updateFileSet(dfs);
        } else if (isDeleted) {
            if (resetTrigger != null && event.target.startsWith(resetTrigger)) {
                try {
                    scanDirectory();
                } catch (IOException ex) {
                    Logger.getLogger(Harvester.class.getName()).log(Level.SEVERE, null, ex);
                }
            } else {
                // TODO : Complex case : single image removal.
            }
        } else {
            TimedUtils.LOGGER.fine(() -> "Unexpected file event : "+event.kind);
        }
    }

    final void scanDirectory() throws IOException {
        try (DirectoryStream<Path> dirStream = Files.newDirectoryStream(source, Files::isRegularFile)) {
            final Iterator<Path> it = dirStream.iterator();
            try {
                while (it.hasNext()) {
                    final DelayedFileSet dfs = new DelayedFileSet(it.next(), delayUnit, delay);
                    updateFileSet(dfs);
                }
            } catch (Exception e) {
                TimedUtils.LOGGER.log(Level.WARNING, "A file cannot be harvested.", e);
            }
        }
    }

    /**
     * Search a fileset already submitted for consumption, with the same base
     * (parent directory + base name) than the one in parameter. It will be
     * replaced by input fileset, after its data (paths composing it) has been
     * transfered into input fileset.
     *
     * @param dfs The fileset to replace already submitted one with.
     */
    private void updateFileSet(final DelayedFileSet dfs) {
        Iterator<DelayedFileSet> it = delayQueue.iterator();
        while (it.hasNext()) {
            final DelayedFileSet next = it.next();
            if (next.equals(dfs)) {
                it.remove();
                next.spliterator().forEachRemaining(dfs::add);

                break;
            }
        }

        delayQueue.add(dfs);
    }

    /**
     * Consume filesets for which delay expired.
     */
    private void harvest() {
        final Thread t = Thread.currentThread();
        while (!t.isInterrupted()) {
            try {
                target.accept(delayQueue.take());
            } catch (InterruptedException ex) {
                TimedUtils.LOGGER.log(Level.FINE, "Harvester stopped", ex);
            } catch (Exception e) {
                TimedUtils.LOGGER.log(Level.WARNING, "An error occurred while harvesting data", e);
            }
        }
    }

    @Override
    public void close() throws IOException {
        try (DirectoryWatcher watcherCopy = watcher;
                Closeable threadStop = () -> indexThread.shutdownNow()) {}
    }

    private static class DelayedFileSet extends FileSet implements Delayed {

        private final long endTime;

        /**
         * {@link #hashCode() } is computed from parent directory and base name,
         * both immutable fields. In consequence, we make the hashcode an
         * immutable field to speed-up hashcode usage.
         */
        private final int hash;

        public DelayedFileSet(Path mainFile, TimeUnit delayUnit, final long delay) {
            super(mainFile);

            endTime = System.nanoTime() + delayUnit.toNanos(delay);

            hash = 7 * Objects.hashCode(parent) + baseName.hashCode();
        }

        @Override
        public long getDelay(TimeUnit unit) {
            return unit.convert(endTime - System.nanoTime(), TimeUnit.NANOSECONDS);
        }

        @Override
        public int hashCode() {
            return hash;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj)
                return true;
            if (obj == null)
                return false;
            if (getClass() != obj.getClass())
                return false;
            final FileSet other = (FileSet) obj;
            return Objects.equals(this.baseName, other.baseName)
                    && Objects.equals(this.parent, other.parent);
        }

        @Override
        public int compareTo(Delayed o) {
            if (o == null)
                return -1;
            final long diff = getDelay(TimeUnit.NANOSECONDS) - o.getDelay(TimeUnit.NANOSECONDS);
            return (int) Math.signum(diff);
        }
    }
}
