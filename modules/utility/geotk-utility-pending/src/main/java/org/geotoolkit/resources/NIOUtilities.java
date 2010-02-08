/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
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
package org.geotoolkit.resources;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.ByteBuffer;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.logging.Level;
import org.geotoolkit.util.logging.Logging;


/**
 * Utility class for managing memory mapped buffers.
 *
 * @module pending
 * @since 2.0
 * @version $Id$
 * @author Andrea Aimes
 */
public final class NIOUtilities {
    /**
     * {@code true} if a warning has already been logged.
     */
    private static boolean warned = false;

    /**
     * Do not allows instantiation of this class.
     */
    private NIOUtilities() {
    }

    /**
     * Takes a URL and converts it to a File. The attempts to deal with
     * Windows UNC format specific problems, specifically files located
     * on network shares and different drives.
     *
     * If the URL.getAuthority() returns null or is empty, then only the
     * url's path property is used to construct the file. Otherwise, the
     * authority is prefixed before the path.
     *
     * It is assumed that url.getProtocol returns "file".
     *
     * Authority is the drive or network share the file is located on.
     * Such as "C:", "E:", "\\fooServer"
     *
     * @param url a URL object that uses protocol "file"
     * @return a File that corresponds to the URL's location
     */
    public static File urlToFile(final URL url) {
        String string = url.toExternalForm();

        try {
            string = URLDecoder.decode(string, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            // Shouldn't happen
        }

        final String path3;
        final String simplePrefix = "file:/";
        final String standardPrefix = simplePrefix + "/";

        if (string.startsWith(standardPrefix)) {
            path3 = string.substring(standardPrefix.length());
        } else if (string.startsWith(simplePrefix)) {
            path3 = string.substring(simplePrefix.length() - 1);
        } else {
            final String auth = url.getAuthority();
            final String path2 = url.getPath().replace("%20", " ");
            if (auth != null && !auth.equals("")) {
                path3 = "//" + auth + path2;
            } else {
                path3 = path2;
    }
        }

        return new File(path3);
    }

    /**
     * Really closes a {@code MappedByteBuffer} without the need to wait for garbage
     * collection. Any problems with closing a buffer on Windows (the problem child in this
     * case) will be logged as {@code SEVERE} to the logger of the package name. To
     * force logging of errors, set the System property "org.geotoolkit.io.debugBuffer" to "true".
     *
     * @param  buffer The buffer to close.
     * @return true if the operation was successful, false otherwise.
     *
     * @see java.nio.MappedByteBuffer
     */
    public static boolean clean(final ByteBuffer buffer) {
        if (buffer == null || ! buffer.isDirect() ) {
            return false;
        }
        Boolean b = AccessController.doPrivileged(new PrivilegedAction<Boolean>() {
            public Boolean run() {
                Boolean success = Boolean.FALSE;
                try {
                    Method getCleanerMethod = buffer.getClass().getMethod("cleaner", (Class[])null);
                    getCleanerMethod.setAccessible(true);
                    Object cleaner = getCleanerMethod.invoke(buffer,  (Object[])null);
                    Method clean = cleaner.getClass().getMethod("clean", (Class[])null);
                    clean.invoke(cleaner, (Object[])null);
                    success = Boolean.TRUE;
                } catch (Exception e) {
                    // This really is a show stopper on windows
                    if (isLoggable()) {
                        log(e, buffer);
                    }
                }
                return success;
            }
        });
        return b.booleanValue();
    }

    /**
     * Checks if a warning message should be logged.
     */
    private static synchronized boolean isLoggable() {
        try {
            return !warned && (
                    Boolean.getBoolean("org.geotoolkit.io.debugBuffer") ||
                    System.getProperty("os.name").indexOf("Windows") >= 0 );
        } catch (SecurityException exception) {
            // The utilities may be running in an Applet, in which case we
            // can't read properties. Assumes we are not in debugging mode.
            return false;
        }
    }

    /**
     * Logs a warning message.
     */
    private static synchronized void log(final Exception e, final ByteBuffer buffer) {
        warned = true;
        String message = "Error attempting to close a mapped byte buffer : " + buffer.getClass().getName()
                       + "\n JVM : " + System.getProperty("java.version")
                       + ' '         + System.getProperty("java.vendor");
        Logging.getLogger("org.geotoolkit.io").log(Level.SEVERE, message, e);
    }

    /**
     * Delete a directory and all its contents, both files and directories.
     * <p>
     * Note, if this is passed a file rather than a directory, the method will
     * not delete anything and return {@code false}.
     * </p>
     *
     * @param directory A {@code File} object, expected to reference a
     *                    directory.
     * @return {@code true} if, and only if, the directory was successfully
     *           deleted, and {@code false} otherwise.
     */
    public static boolean deleteDirectory(final File directory) throws IOException{
        if (directory == null)
            return false;
        if (!directory.exists())
            return false;

        if (directory.isDirectory()) {
            for (File f : directory.listFiles()) {
                if (f.isDirectory()) {
                    deleteDirectory(f);
                } else {
                    final boolean deleted = f.delete();
                    if (!deleted) {
                        throw new IOException("unable to delete the file:" + f.getName());
                    }
                }
            }
        }
        return directory.delete();

    }

}
