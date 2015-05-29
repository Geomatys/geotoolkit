/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2003-2012, Open Source Geospatial Foundation (OSGeo)
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
package org.geotoolkit.test;

import java.io.*;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;


/**
 * Provides access to "{@code test-data}" directories associated with JUnit tests.  The expected
 * directory name is "{@code test-data}" to follow the javadoc "{@code doc-files}" convention of
 * ensuring that data directories don't look anything like normal java packages.
 * <p>
 * Example:
 *
 * {@preformat java
 *     class MyClass {
 *         public void example() {
 *             Image testImage = new ImageIcon(TestData.url(this, "test.png")).getImage();
 *             Reader reader = TestData.openReader(this, "script.xml");
 *             // ... do some process
 *             reader.close();
 *         }
 *     }
 * }
 *
 * Where the directory structure goes as bellow:
 * <p>
 * <ul>
 *   <li>{@code MyClass.java}<li>
 *   <li>{@code test-data/test.png}</li>
 *   <li>{@code test-data/script.xml}</li>
 * </ul>
 * <p>
 * By convention developers should locate "{@code test-data}" near the JUnit test
 * cases that uses it.
 *
 * @author James McGill (Leeds)
 * @author Martin Desruisseaux (IRD, Geomatys)
 * @author Simone Giannecchini (Geosolutions)
 * @version 3.19
 *
 * @since 2.4
 */
public final strictfp class TestData implements Runnable {
    /**
     * The test data directory.
     */
    private static final String DIRECTORY = "test-data";

    /**
     * Encoding of files and URL path.
     */
    private static final String ENCODING = "UTF-8";

    /**
     * The files to delete at shutdown time. {@link File#deleteOnExit} alone doesn't seem
     * sufficient since it will preserve any overwritten files.
     */
    private static final LinkedList<Deletable> toDelete = new LinkedList<>();

    /**
     * Registers the thread to be automatically executed at shutdown time.
     * This thread will delete all temporary files registered in {@link #toDelete}.
     */
    static {
        Runtime.getRuntime().addShutdownHook(new Thread(new TestData(), "Test data cleaner"));
    }

    /**
     * Do not allow instantiation of this class.
     */
    private TestData() {
    }

    /**
     * Locates named test-data resource for caller. <strong>Note:</strong> Consider using the
     * <code>{@link #url url}(caller, name)</code> method instead if the resource should always
     * exists.
     *
     * @param  caller Calling class or object used to locate {@code test-data}.
     * @param  name Name of the resource to find in the {@code test-data} directory,
     *         or (@code null} for the {@code test-data} directory itself.
     * @return URL or {@code null} if the named test-data could not be found.
     *
     * @see #url
     */
    public static URL getResource(final Object caller, String name) {
        if (name == null || (name=name.trim()).isEmpty()) {
            name = DIRECTORY;
        } else {
            name = DIRECTORY + '/' + name;
        }
        if (caller != null) {
            final Class<?> c = (caller instanceof Class<?>) ? (Class<?>) caller : caller.getClass();
            return c.getResource(name);
        } else {
            return Thread.currentThread().getContextClassLoader().getResource(name);
        }
    }

    /**
     * Access to <code>{@linkplain #getResource getResource}(caller, path)</code> as a non-null
     * {@link URL}. At the difference of {@code getResource}, this method throws an exception if
     * the resource is not found. This provides a more explicit explanation about the failure
     * reason than the infamous {@link NullPointerException}.
     *
     * @param  caller Calling class or object used to locate {@code test-data}.
     * @param  path Path to the resource to find in the {@code test-data} directory,
     *         or (@code null} for the {@code test-data} directory itself.
     * @return The URL to the {@code test-data} resource.
     * @throws FileNotFoundException if the resource is not found.
     *
     * @since 2.2
     */
    public static URL url(final Object caller, final String path) throws FileNotFoundException {
        final URL url = getResource(caller, path);
        if (url == null) {
            throw new FileNotFoundException("Can not locate test-data for \"" + path + '"');
        }
        return url;
    }

    /**
     * Access to <code>{@linkplain #getResource getResource}(caller, path)</code> as a non-null
     * {@link File}. It allows access the {@code test-data} directory with:
     *
     * {@preformat java
     *     TestData.file(MyClass.class, null);
     * }
     *
     * @param  caller Calling class or object used to locate {@code test-data}.
     * @param  path Path to the resource to find in the {@code test-data} directory,
     *         or (@code null} for the {@code test-data} directory itself.
     * @return The file to the {@code test-data} resource.
     * @throws FileNotFoundException if the file is not found.
     * @throws IOException if the resource can't be fetched for an other reason.
     */
    public static File file(final Object caller, final String path)
            throws FileNotFoundException, IOException
    {
        final URL url = url(caller, path);
        final File file = new File(URLDecoder.decode(url.getPath(), ENCODING));
        if (!file.exists()) {
            throw new FileNotFoundException("Can not locate test-data for \"" + file.getAbsolutePath() + '"');
        }
        return file;
    }

    /**
     * Creates a temporary file with the given name. The file will be created in the
     * {@code test-data} directory and will be deleted on exit.
     *
     * @param  caller Calling class or object used to locate {@code test-data}.
     * @param  name A base name for the temporary file.
     * @return The temporary file in the {@code test-data} directory.
     * @throws IOException if the file can't be created.
     */
    public static File temp(final Object caller, final String name) throws IOException {
        final File testData = file(caller, null);
        final int split = name.lastIndexOf('.');
        final String prefix = (split < 0) ? name  : name.substring(0,split);
        final String suffix = (split < 0) ? "tmp" : name.substring(split+1);
        final File tmp = File.createTempFile(prefix, '.' + suffix, testData);
        deleteOnExit(tmp, true);
        return tmp;
    }

    /**
     * Provides a non-null {@link InputStream} for named test data.
     * It is the caller responsibility to close this stream after usage.
     *
     * @param  caller Calling class or object used to locate {@code test-data}.
     * @param  name Filename of test data to load.
     * @return The input stream.
     * @throws FileNotFoundException if the resource is not found.
     * @throws IOException if an error occurs during an input operation.
     *
     * @since 2.2
     */
    public static InputStream openStream(final Object caller, final String name)
            throws FileNotFoundException, IOException
    {
        return new BufferedInputStream(url(caller, name).openStream());
    }

    /**
     * Provides a {@link BufferedReader} for named test data in UTF-8 encoding. The buffered
     * reader is provided as a {@link LineNumberReader} instance, which is useful for displaying
     * line numbers where error occur. It is the caller responsibility to close this reader after
     * usage.
     *
     * @param  caller The class of the object associated with named data.
     * @param  name Filename of test data to load.
     * @return The buffered reader.
     * @throws FileNotFoundException if the resource is not found.
     * @throws IOException if an error occurs during an input operation.
     *
     * @since 2.2
     */
    public static LineNumberReader openReader(final Object caller, final String name)
            throws FileNotFoundException, IOException
    {
        return new LineNumberReader(new InputStreamReader(url(caller, name).openStream(), ENCODING));
    }

    /**
     * Provides a channel for named test data. It is the caller responsibility to close this
     * chanel after usage.
     *
     * @param  caller The class of the object associated with named data.
     * @param  name Filename of test data to load.
     * @return The chanel.
     * @throws FileNotFoundException if the resource is not found.
     * @throws IOException if an error occurs during an input operation.
     *
     * @since 2.2
     */
    public static ReadableByteChannel openChannel(final Object caller, final String name)
            throws FileNotFoundException, IOException
    {
        final URL url = url(caller, name);
        final File file = new File(URLDecoder.decode(url.getPath(), ENCODING));
        if (file.exists()) {
            return new RandomAccessFile(file, "r").getChannel();
        }
        return Channels.newChannel(url.openStream());
    }

    /**
     * Reads the given resource as a text file, assuming a UTF-8 encoding.
     * The returned text uses always the Unix style of EOL.
     *
     * @param  caller The class of the object associated with named data.
     * @param  name Filename of test data to load.
     * @return The loaded test data as a text.
     * @throws IOException if an error occurs during an input operation.
     *
     * @since 3.04
     */
    public static String readText(final Object caller, final String name) throws IOException {
        return read(openReader(caller, name));
    }

    /**
     * Reads the given file as a text file, assuming a UTF-8 encoding.
     * The returned text uses always the Unix style of EOL.
     *
     * @param  file The file of test data to load.
     * @return The loaded test data as a text.
     * @throws IOException if an error occurs during an input operation.
     *
     * @since 3.07
     */
    public static String readText(final File file) throws IOException {
        return readText(file, ENCODING);
    }

    /**
     * Reads the given file as a text file, assuming a ISO-LATIN-1 encoding.
     * The returned text uses always the Unix style of EOL.
     *
     * @param  file The file of test data to load.
     * @return The loaded test data as a text.
     * @throws IOException if an error occurs during an input operation.
     *
     * @since 3.07
     */
    public static String readLatinText(final File file) throws IOException {
        return readText(file, "ISO-8859-1");
    }

    /**
     * Reads the given file as a text file, assuming the given encoding.
     * The returned text uses always the Unix style of EOL.
     */
    private static String readText(final File file, final String encoding) throws IOException {
        return read(new BufferedReader(new InputStreamReader(new FileInputStream(file), encoding)));
    }

    /**
     * Reads the given stream as a text. The returned text uses always the Unix style of EOL.
     * The given stream is closed by this method.
     */
    private static String read(final BufferedReader in) throws IOException {
        final StringBuilder buffer = new StringBuilder();
        String line;
        while ((line = in.readLine()) != null) {
            buffer.append(line).append('\n');
        }
        in.close();
        return buffer.toString();
    }

    /**
     * Reads the given file as a properties file.
     *
     * @param  file The file of test properties to load.
     * @return The loaded test data as a properties file.
     * @throws IOException if an error occurs during an input operation.
     *
     * @since 3.10
     */
    public static Properties readProperties(final File file) throws IOException {
        final Properties properties;
        try (InputStream in = new FileInputStream(file)) {
            properties = new Properties();
            properties.load(in);
        }
        return properties;
    }

    /**
     * Unzip a file in the {@code test-data} directory. The zip file content is inflated in place,
     * i.e. inflated files are written in the same {@code test-data} directory. If a file to be
     * inflated already exists in the {@code test-data} directory, then the existing file is left
     * untouched and the corresponding ZIP entry is silently skipped. This approach avoid the
     * overhead of inflating the same files many time if this {@code unzipFile} method is invoked
     * before every tests.
     * <p>
     * Inflated files will be automatically {@linkplain File#deleteOnExit deleted on exit}
     * if and only if they have been modified. Callers don't need to worry about cleanup,
     * because the files are inflated in the {@code target/.../test-data} directory, which
     * is not versionned by SVN and is cleaned by Maven on {@code mvn clean} execution.
     *
     * @param  caller The class of the object associated with named data.
     * @param  name The file name to unzip in place.
     * @throws FileNotFoundException if the specified zip file is not found.
     * @throws IOException if an error occurs during an input or output operation.
     *
     * @since 2.2
     */
    public static void unzipFile(final Object caller, final String name)
            throws FileNotFoundException, IOException
    {
        final File file    = file(caller, name);
        final File parent  = file.getParentFile().getAbsoluteFile();
        try (ZipFile zipFile = new ZipFile(file)) {
            final byte[]  buffer  = new byte[4096];
            final Enumeration<? extends ZipEntry> entries = zipFile.entries();
            while (entries.hasMoreElements()) {
                final ZipEntry entry = entries.nextElement();
                if (entry.isDirectory()) {
                    continue;
                }
                final File path = new File(parent, entry.getName());
                if (path.exists()) {
                    continue;
                }
                final File directory = path.getParentFile();
                if (directory != null && !directory.exists()) {
                    directory.mkdirs();
                }
                // Copy the file. Note: no need for a BufferedOutputStream,
                // since we are already using a buffer of type byte[4096].
                try (InputStream in = zipFile.getInputStream(entry);
                     OutputStream out = new FileOutputStream(path))
                {
                    int len;
                    while ((len = in.read(buffer)) >= 0) {
                        out.write(buffer, 0, len);
                    }
                }
                // Call 'deleteOnExit' only after after we closed the file,
                // because this method will save the modification time.
                deleteOnExit(path, false);
            }
        }
    }

    /**
     * Deletes the given file or directory. If the given argument denotes a directory,
     * then the directory content is deleted as well.
     *
     * @param file The file or directory to delete.
     * @return {@code true} on success.
     *
     * @since 3.03
     */
    public static boolean deleteRecursively(final File file) {
        if (file.isDirectory()) {
            for (final File child : file.listFiles()) {
                if (!deleteRecursively(child)) {
                    return false;
                }
            }
        }
        return file.delete();
    }

    /**
     * Requests that the file or directory denoted by the specified pathname be deleted
     * when the virtual machine terminates. This method can optionally delete the file
     * only if it has been modified, thus giving a chance for test suites to copy their
     * resources only once.
     *
     * @param file The file to delete.
     * @param force If {@code true}, delete the file in all cases. If {@code false},
     *        delete the file if and only if it has been modified.
     *
     * @since 2.4
     */
    public static void deleteOnExit(final File file, final boolean force) {
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
