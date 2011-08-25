/*
 *    GeotoolKit - An Open source Java GIS Toolkit
 *    http://geotoolkit.org
 * 
 *    (C) 2005-2008, Open Source Geospatial Foundation (OSGeo)
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
package org.geotoolkit;

// J2SE dependencies
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.LineNumberReader;
import java.io.OutputStream;
import java.net.URL;
import java.nio.channels.ReadableByteChannel;
import java.util.Iterator;
import java.util.LinkedList;
import org.geotoolkit.test.TestData;


/**
 * Provides access to the common {@code test-data} directories provided in the
 * {@code sample-data} module. This directory is shared by test suites in other
 * modules.
 * <p>
 * This file has to live in the {@code org.geotoolkit} root package in order to
 * get access to the {@code org/geotoolkit/test-data} directory. If you don't
 * need this directory, then use the {@link org.geotoolkit.test.TestData}
 * class provided in the {@code org.geotoolkit.resources} directory.
 *
 * @module pending
 * @since 2.2
 * @version $Id$
 * @author Martin Desruisseaux
 *
 * @tutorial http://www.geotoolkit.org/display/GEOT/5.8+Test+Data
 */
public final class ShapeTestData implements Runnable {
    
    /**
     * The files to delete at shutdown time. {@link File#deleteOnExit} alone doesn't seem
     * sufficient since it will preserve any overwritten files.
     */
    private static final LinkedList<Deletable> toDelete = new LinkedList<Deletable>();

    /**
     * Registers the thread to be automatically executed at shutdown time.
     * This thread will delete all temporary files registered in {@link #toDelete}.
     */
    static {
        Runtime.getRuntime().addShutdownHook(new Thread(new ShapeTestData(), "Test data cleaner"));
    }
    
    /**
     * Do not allow instantiation of this class.
     */
    private ShapeTestData() {
    }

    /**
     * Access to <code>{@linkplain #getResource getResource}(name)</code> as a non-null
     * {@link URL}. At the difference of {@code getResource}, this method throws an exception if
     * the resource is not found. This provides a more explicit explanation about the failure
     * reason than the infamous {@link NullPointerException}.
     *
     * @param  name Path to file in {@code org/geotoolkit/test-data}.
     * @return The URL to the {@code test-data} resource.
     * @throws FileNotFoundException if the resource is not found.
     */
    public static URL url(final String name) throws FileNotFoundException {
        return TestData.url(ShapeTestData.class, name);
    }

    /**
     * Access to <code>{@linkplain #getResource getResource}(name)</code> as a non-null
     * {@link File}. You can access the {@code test-data} directory with:
     *
     * <blockquote><pre>
     * TestData.file(null);
     * </pre></blockquote>
     *
     * @param  name Path to file in {@code org/geotoolkit/test-data}.
     * @return The file to the {@code test-data} resource.
     * @throws FileNotFoundException if the file is not found.
     * @throws IOException if the resource can't be fetched for an other reason.
     */
    public static File file(final String name) throws IOException {
        return TestData.file(ShapeTestData.class, name);
    }

    /**
     * Provides a non-null {@link InputStream} for named test data.
     * It is the caller responsability to close this stream after usage.
     *
     * @param  name Path to file in {@code org/geotoolkit/test-data}.
     * @return The input stream.
     * @throws FileNotFoundException if the resource is not found.
     * @throws IOException if an error occurs during an input operation.
     */
    public static InputStream openStream(final String name) throws IOException {
        return TestData.openStream(ShapeTestData.class, name);
    }

    /**
     * Provides a {@link BufferedReader} for named test data. The buffered reader is provided as
     * an {@link LineNumberReader} instance, which is useful for displaying line numbers where
     * error occur. It is the caller responsability to close this reader after usage.
     *
     * @param  name Path to file in {@code org/geotoolkit/test-data}.
     * @return The buffered reader.
     * @throws FileNotFoundException if the resource is not found.
     * @throws IOException if an error occurs during an input operation.
     */
    public static LineNumberReader openReader(final String name) throws IOException {
        return TestData.openReader(ShapeTestData.class, name);
    }

    /**
     * Provides a channel for named test data. It is the caller responsability to close this
     * chanel after usage.
     *
     * @param  name Path to file in {@code org/geotoolkit/test-data}.
     * @return The chanel.
     * @throws FileNotFoundException if the resource is not found.
     * @throws IOException if an error occurs during an input operation.
     */
    public static ReadableByteChannel openChannel(final String name) throws IOException {
        return TestData.openChannel(ShapeTestData.class, name);
    }

    /**
     * Copies the named resources from the {@code sample-data} module to the {@code test-data}
     * directory in an other module. For example if {@code TestData.copy(this, "foo.txt")} is
     * invoked inside a test suite in the {@code org.geotoolkit.foo} package, then this method
     * copies {@code org/geotoolkit/test-data/foo.txt} (from {@code sample-data} module) to
     * {@code org/geotoolkit/foo/test-data/foo.txt} (in the {@code foo} module).
     * <p>
     * This method is useful when a test case needs to access a resource through a {@link File},
     * for example because it want to open it using {@link java.io.RandomAccess}. Because the
     * resources provided in the {@code sample-data} module are available to other modules as
     * a JAR file, other modules can only access them through an {@link URL} unless they copy
     * them in their own {@code test-data} directory.
     * <p>
     * If the named file already exists in the caller {@code test-data} directory, then this
     * method does nothing. It make it safe to invoke this method many time in a test suite,
     * since this method should not copy the file more than once for a given JVM execution.
     * <p>
     * The file will be {@linkplain File#deleteOnExit deleted on exit} if and only if it has
     * been modified. Callers don't need to worry about cleanup, because the files are copied
     * in the {@code target/.../test-data} directory, which is not versionned by SVN and is
     * cleaned by Maven on {@code mvn clean} execution.
     *
     * @param  caller Calling class or object used to locate the destination {@code test-data}.
     * @param  name Path to file in {@code org/geotoolkit/test-data}.
     * @return The file to the <code>org/geotoolkit/<strong>caller-package</strong>/test-data</code>
     *         resource copy, returned for convenience.
     * @throws FileNotFoundException if the file is not found.
     * @throws IOException if the resource can't be fetched for an other reason.
     */
    public static File copy(final Object caller, final String name) throws IOException {
        final File path      = new File(name);
        final File directory = new File(TestData.file(caller, null), path.getParent());
        final File file      = new File(directory, path.getName());
        if (!file.exists()) {
            if (directory.mkdirs()) {
                deleteOnExit(directory, false);
            }
            final InputStream   in = openStream(name);
            final OutputStream out = new FileOutputStream(file);
            final byte[]    buffer = new byte[4096];
            deleteOnExit(file, false);
            int count;
            while ((count = in.read(buffer)) >= 0) {
                out.write(buffer, 0, count);
            }
            out.close();
            in.close();
        }
        return file;
    }
    
    
    
    
    
    
    
    /**
     * Requests that the file or directory denoted by the specified
     * pathname be deleted when the virtual machine terminates.
     *
     * @param file The file to delete on exit.
     */
    private static void deleteOnExit(final File file) {
        deleteOnExit(file, true);
    }

    /**
     * Requests that the file or directory denoted by the specified pathname be deleted
     * when the virtual machine terminates. This method can optionally delete the file
     * only if it has been modified, thus giving a chance for test suites to copy their
     * resources only once.
     *
     * @param file The file to delete.
     * @param force If {@code true}, delete the file in all cases. If {@code false},
     *        delete the file if and only if it has been modified. The default value
     *        if {@code true}.
     *
     * @since 2.4
     */
    protected static void deleteOnExit(final File file, final boolean force) {
        if (force) {
            file.deleteOnExit();
        }
        final Deletable entry = new Deletable(file, force);
        synchronized (toDelete) {
            if (file.isFile()) {
                toDelete.addFirst(entry);
            } else {
                toDelete.addLast(entry);
            }
        }
    }

    /**
     * A file that may be deleted on JVM shutdown.
     */
    private static final class Deletable {
        /**
         * The file to delete.
         */
        private final File file;

        /**
         * The initial timestamp. Used in order to determine if the file has been modified.
         */
        private final long timestamp;

        /**
         * Constructs an entry for a file to be deleted.
         */
        public Deletable(final File file, final boolean force) {
            this.file = file;
            timestamp = force ? Long.MIN_VALUE : file.lastModified();
        }

        /**
         * Returns {@code true} if failure to delete this file can be ignored.
         */
        public boolean canIgnore() {
            return timestamp != Long.MIN_VALUE && file.isDirectory();
        }

        /**
         * Deletes this file, if modified. Returns {@code false} only
         * if the file should be deleted but the operation failed.
         */
        public boolean delete() {
            if (!file.exists() || file.lastModified() <= timestamp) {
                return true;
            }
            return file.delete();
        }

        /**
         * Returns the filepath.
         */
        @Override
        public String toString() {
            return String.valueOf(file);
        }
    }

    /**
     * Deletes all temporary files. This method is invoked automatically at shutdown time and
     * should not be invoked directly. It is public only as an implementation side effect.
     */
    @Override
    public void run() {
        int iteration = 5; // Maximum number of iterations
        synchronized (toDelete) {
            while (!toDelete.isEmpty()) {
                if (--iteration < 0) {
                    break;
                }
                /*
                 * Before to try to delete the files, invokes the finalizers in a hope to close
                 * any input streams that the user didn't explicitly closed. Leaving streams open
                 * seems to occurs way too often in our test suite...
                 */
                System.gc();
                System.runFinalization();
                for (final Iterator<Deletable> it=toDelete.iterator(); it.hasNext();) {
                    final Deletable f = it.next();
                    try {
                        if (f.delete()) {
                            it.remove();
                            continue;
                        }
                    } catch (SecurityException e) {
                        if (iteration == 0) {
                            System.err.print(e.getClass().getCanonicalName());
                            System.err.print(": ");
                        }
                    }
                    // Can't use logging, since logger are not available anymore at shutdown time.
                    if (iteration == 0 && !f.canIgnore()) {
                        System.err.print("Can't delete ");
                        System.err.println(f);
                    }
                }
            }
        }
    }
    
    
}
