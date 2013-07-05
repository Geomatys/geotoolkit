/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2009-2012, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2009-2012, Geomatys
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
package org.geotoolkit.internal.io;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.HashMap;
import java.util.logging.Level;
import java.lang.ref.PhantomReference;

import org.apache.sis.util.Disposable;
import org.apache.sis.util.logging.Logging;
import org.geotoolkit.resources.Loggings;
import org.geotoolkit.internal.ReferenceQueueConsumer;


/**
 * Manages temporary files created by the Geotk library. This class provides a {@link #createTempFile}
 * method which similar to the one provided in the standard {@link File} class, except that the file
 * may be deleted earlier. More specifically if the {@link File} object has been garbage-collected,
 * then the corresponding file in the file system is deleted immediately instead than waiting for
 * the JVM shutdown.
 *
 * {@note This class extends <code>PhantomReference</code> for implementation convenience.
 *        Callers should ignore that detail.}
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.16
 *
 * @since 3.03
 * @module
 */
public final class TemporaryFile extends PhantomReference<File> implements Disposable {
    /**
     * The temporary files not yet deleted. Keys are the string returned by {@link File#getPath()}.
     */
    private static final Map<String,TemporaryFile> REFERENCES = new HashMap<>();

    /**
     * Registers a shutdown hook which will delete every files not yet deleted.
     */
    static {
        try {
            Class.forName("org.geotoolkit.factory.ShutdownHook", true, TemporaryFile.class.getClassLoader());
        } catch (Exception e) {
            Logging.unexpectedException(TemporaryFile.class, "<init>", e);
        }
    }

    /**
     * The path to the file to delete. We do not keep directly a reference to {@link File}
     * since we want to allow the garbage collector to collect the file. Note that Sun
     * implementation of {@link File#deleteOnExit()} in JDK 6 also retains the path instead
     * than the {@code File}.
     */
    private final String path;

    /**
     * Creates a new reference to a temporary file.
     */
    private TemporaryFile(final File file) {
        super(file, ReferenceQueueConsumer.DEFAULT.queue);
        path = file.getPath();
    }

    /**
     * Creates a new temporary file and register it immediately for deletion on JVM shutdown.
     *
     * @param  prefix    The prefix string to be used in generating the file's name.
     * @param  suffix    The suffix string to be used in generating the file's name
     *                   (usually with a leading dot), or {@code null} for {@code ".tmp"}.
     * @param  directory The directory in which the file is to be created, or {@code null}.
     * @return The temporary file.
     * @throws IOException If the file can not be created.
     */
    public static File createTempFile(String prefix, String suffix, File directory) throws IOException {
        final File file = File.createTempFile(prefix, suffix, directory);
        final TemporaryFile ref = new TemporaryFile(file);
        synchronized (REFERENCES) {
            if (REFERENCES.put(ref.path, ref) != null) {
                // Should never happen since File.createTempFile ensures unique filename.
                // Not that there is a slight risks that this failure happens if the user
                // invoked File.delete() instead than TemporaryFile.delete(file).
                throw new AssertionError(ref);
            }
        }
        return file;
    }

    /**
     * Deletes the given temporary file. This method should be invoked instead of
     * {@link File#delete()} in order to unregister the given file.
     *
     * @param file The file to delete.
     * @return {@code true} if the file has been deleted.
     */
    public static boolean delete(final File file) {
        synchronized (REFERENCES) {
            final TemporaryFile ref = REFERENCES.remove(file.getPath());
            if (ref != null) {
                ref.clear();
            }
        }
        return file.delete(); // Must be after the removal from the list.
    }

    /**
     * Deletes the current file. This is invoked by {@link #dispose()}
     * when a {@link File} object has been garbage-collected.
     *
     * @return {@code true} if the file has been successfully deleted.
     */
    private boolean delete() {
        synchronized (REFERENCES) {
            if (REFERENCES.remove(path) == this) {
                return new File(path).delete();
            }
        }
        return false; // Already deleted by the shutdown hook.
    }

    /**
     * Deletes every files, no matter if they have been garbage-collected or not.
     * This method should be invoking during shutdown only.
     *
     * @return {@code true} if at least one file has been successfully deleted.
     */
    public static boolean deleteAll() {
        boolean deleted = false;
        final Map<String,TemporaryFile> references = REFERENCES;
        if (references != null) { // Safety check against weird behavior at shutdown time.
            synchronized (references) {
                for (final TemporaryFile ref : references.values()) {
                    deleted |= new File(ref.path).delete();
                    ref.clear();
                }
                references.clear();
            }
        }
        return deleted;
    }

    /**
     * Deletes this file. This method is invoked automatically when
     * a {@link File} object has been garbage-collected.
     */
    @Override
    public void dispose() {
        if (delete()) {
            /*
             * Logs the message at the WARNING level because execution of this code
             * means that the application failed to delete itself the temporary file.
             */
            Logging.log(TemporaryFile.class, "delete",
                    Loggings.format(Level.WARNING, Loggings.Keys.TEMPORARY_FILE_GC_1, this));
        }
    }

    /**
     * Returns the file path.
     *
     * @return The file path.
     */
    @Override
    public String toString() {
        return path;
    }
}
