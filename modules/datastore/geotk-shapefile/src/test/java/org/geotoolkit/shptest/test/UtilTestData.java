/*
 *    GeoTools - The Open Source Java GIS Toolkit
 *    http://geotools.org
 *
 *    (C) 2003-2008, Open Source Geospatial Foundation (OSGeo)
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
package org.geotoolkit.shptest.test;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;


/**
 * Provides access to {@code test-data} directories associated with JUnit tests.
 * <p>
 * We have chosen "{@code test-data}" to follow the javadoc "{@code doc-files}" convention
 * of ensuring that data directories don't look anything like normal java packages.
 * <p>
 * Example:
 * <pre>
 * class MyClass {
 *     public void example() {
 *         Image testImage = new ImageIcon(TestData.url(this, "test.png")).getImage();
 *         Reader reader = TestData.openReader(this, "script.xml");
 *         // ... do some process
 *         reader.close();
 *     }
 * }
 * </pre>
 * Where the directory structure goes as bellow:
 * <ul>
 *   <li>{@code MyClass.java}<li>
 *   <li>{@code test-data/test.png}</li>
 *   <li>{@code test-data/script.xml}</li>
 * </ul>
 * <p>
 * By convention you should try and locate {@code test-data} near the JUnit test
 * cases that uses it. If you need an access to shared test data, import the
 * {@link org.geotools.TestData} class from the {@code sample-module} instead
 * of this one.
 *
 * @since 2.4
 * @source $URL$
 * @version $Id$
 * @author James McGill
 * @author Simone Giannecchiin
 * @author Martin Desruisseaux
 *
 * @tutorial http://www.geotools.org/display/GEOT/5.8+Test+Data
 */
public class UtilTestData extends org.geotoolkit.test.TestData {
    /**
     * The {@linkplain System#getProperty(String) system property} key for more extensive test
     * suite. The value for this key is returned by the {@link #isExtensiveTest} method. Some
     * test suites will perform more extensive test coverage if this property is set to
     * {@code true}. The value for this property is typically defined on the command line as a
     * <code>-D{@value}=true</code> option at Java or Maven starting time.
     */
    public static final String EXTENSIVE_TEST_KEY = "org.geotools.test.extensive";

    /**
     * The {@linkplain System#getProperty(String) system property} key for interactive tests.
     * The value for this key is returned by the {@link #isInteractiveTest} method. Some
     * test suites will show windows with maps and other artifacts related to testing
     * if this property is set to {@code true}.
     * The value for this property is typically defined on the command line as a
     * <code>-D{@value}=true</code> option at Java or Maven starting time.
     */
    public static final String INTERACTIVE_TEST_KEY = "org.geotools.test.interactive";

    /**
     * {@code true} if JAI media lib is available.
     */
    private static final boolean mediaLibAvailable;
    static {
        Class mediaLibImage = null;
        try {
            mediaLibImage = Class.forName("com.sun.medialib.mlib.Image");
        } catch (ClassNotFoundException e) {
        }
        mediaLibAvailable = (mediaLibImage != null);
    }

    /**
     * Do not allow instantiation of this class, except for extending it.
     */
    protected UtilTestData() {
    }

    /**
     * Get a property as a boolean value. If the property can't be
     * fetch for security reason, then default to {@code false}.
     */
    private static boolean getBoolean(final String name) {
        try {
            return Boolean.getBoolean(name);
        } catch (SecurityException exception) {
            // Note: we use Java Logger instead of Geotools Logging because this module
            // do not depends on the module that defines Logging. This class is used for
            // test purpose only anyway, so it should not be an issue.
            Logger.getLogger("org.geotools").warning(exception.getLocalizedMessage());
            return false;
        }
    }

    /**
     * Returns {@code true} if the running Java virtual machine is 1.5. This is the lowest
     * Java version currently supported by Geotools. This version will increase in future
     * Geotools version.
     * <p>
     * This method was used for some broken JUnit tests that were know to run on JSE 1.4 but
     * not on JSE 1.6 for example.
     *
     * @return {@code true} if we are running on the target Java platform.
     */
    public static boolean isBaseJavaPlatform() {
        return System.getProperty("java.version").startsWith("1.5");
    }

    /**
     * Returns {@code true} if JAI MediaLib acceleration is available.
     * <p>
     * This method is used to disable some checks in unit tests that fail when JAI is
     * run in pure java mode.
     *
     * @return {@code true} if JAI medialib are available.
     */
    public static boolean isMediaLibAvailable() {
        return mediaLibAvailable;
    }

    /**
     * Returns {@code true} if {@value #EXTENSIVE_TEST_KEY} system property is set to
     * {@code true}. Test suites should check this value before to perform lengthly tests.
     *
     * @return {@code true} if extensive tests are enabled.
     */
    public static boolean isExtensiveTest() {
        return getBoolean(EXTENSIVE_TEST_KEY);
    }

    /**
     * Returns {@code true} if {@value #INTERACTIVE_TEST_KEY} system property is set to {@code true}.
     * Test suites should check this value before showing any kind of graphical window to the user.
     *
     * @return {@code true} if interactive tests are enabled.
     */
    public static boolean isInteractiveTest() {
        return getBoolean(INTERACTIVE_TEST_KEY);
    }

    /**
     * Provides a {@link java.io.BufferedReader} for named test data.
     * It is the caller responsability to close this reader after usage.
     *
     * @param  caller The class of the object associated with named data.
     * @param  name of test data to load.
     * @return The reader, or {@code null} if the named test data are not found.
     * @throws IOException if an error occurs during an input operation.
     *
     * @deprecated Use {@link #openReader} instead. The {@code openReader} method throws an
     *  exception if the resource is not found, instead of returning null. This make debugging
     *  easier, since it replaces infamous {@link NullPointerException} by a more explicit error
     *  message during tests. Furthermore, the {@code openReader} name make it more obvious that
     *  the stream is not closed automatically and is also consistent with other method names in
     *  this class.
     */
    @Deprecated
    public static BufferedReader getReader(final Object caller, final String name)
            throws IOException
    {
        final URL url = getResource(caller, name);
        if (url == null) {
            return null; // echo handling of getResource( ... )
        }
        return new BufferedReader(new InputStreamReader(url.openStream()));
    }

    /**
     * Requests that the file or directory denoted by the specified
     * pathname be deleted when the virtual machine terminates.
     *
     * @param file The file to delete on exit.
     */
    protected static void deleteOnExit(final File file) {
        deleteOnExit(file, true);
    }
}
