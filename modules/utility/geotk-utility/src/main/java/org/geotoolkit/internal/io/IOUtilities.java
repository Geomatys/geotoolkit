/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2008-2009, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2009, Geomatys
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

import java.io.*;
import java.net.URI;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URISyntaxException;
import java.net.MalformedURLException;
import java.util.StringTokenizer;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import static java.lang.Character.isLetter;

import org.geotoolkit.lang.Static;
import org.geotoolkit.resources.Errors;
import org.geotoolkit.io.ContentFormatException;


/**
 * Utility methods related to I/O operations.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.07
 *
 * @since 3.00
 * @module
 */
@Static
public final class IOUtilities {
    /**
     * Do not allow instantiation of this class.
     */
    private IOUtilities() {
    }

    /**
     * If the two given files are equals, return that file. Otherwise returns the first
     * common parent found.
     *
     * @param  root The file which is the most likely to be the root.
     * @param  file The other file, which is more likely to be in a sub-directory of the root.
     * @return The root or a common parent, or {@code null} if no common parent has been found.
     */
    public static File commonParent(File root, final File file) {
        while (root != null) {
            for (File candidate=file; candidate!=null; candidate=candidate.getParentFile()) {
                if (root.equals(candidate)) {
                    return root;
                }
            }
            root = root.getParentFile();
        }
        return null;
    }

    /**
     * Converts a {@link URL} to a {@link File}. Conceptually this work is performed by a call
     * to {@link URL#toURI()} followed by a call to the {@link File(URI)} constructor. However
     * this method adds the following functionalities:
     * <p>
     * <ul>
     *   <li>Optionnaly decodes the {@code "%XX"} sequences, where {@code "XX"} is a number.</li>
     *   <li>Converts various exceptions into subclasses of {@link IOException}.</li>
     * </ul>
     *
     * @param  url The URL (may be {@code null}).
     * @param  encoding If the URL is encoded in a {@code application/x-www-form-urlencoded}
     *         MIME format, the character encoding (normally {@code "UTF-8"}. If the URL is
     *         not encoded, then {@code null}.
     * @return The file for the given URL, or {@code null} if the given URL was null.
     * @throws IOException if the URL can not be converted to a file.
     *
     * @since 3.05
     */
    public static File toFile(final URL url, final String encoding) throws IOException {
        if (url == null) {
            return null;
        }
        /*
         * Convert the URL to an URI, taking in account the encoding if any.
         *
         * Note: URL.toURI() is implemented as new URI(URL.toString()) where toString()
         * delegates to toExternalForm(), and all those methods are final. So we really
         * don't lost anything by doing those steps ourself.
         */
        String path = url.toExternalForm();
        if (encoding != null) {
            path = URLDecoder.decode(path, encoding);
        }
        URI uri;
        try {
            uri = new URI(path);
        } catch (URISyntaxException cause) {
            /*
             * Occurs only if the URL is not compliant with RFC 2396. Otherwise every URL
             * should succeed, to a failure can actually be considered as a malformed URL.
             */
            MalformedURLException e = new MalformedURLException(Errors.format(
                    Errors.Keys.ILLEGAL_ARGUMENT_$2, "URL", path));
            e.initCause(cause);
            throw e;
        }
        /*
         * We really want to call the File constructor expecting an URI argument, not the
         * constructor expecting a String argument, because the one for URI performs
         * additional platform-specific parsing.
         */
        try {
            return new File(uri);
        } catch (IllegalArgumentException cause) {
            IOException e = new FileNotFoundException(Errors.format(Errors.Keys.ILLEGAL_ARGUMENT_$2, "URL", path));
            e.initCause(cause);
            throw e;
        }
    }

    /**
     * Parses the following path as a {@link File} if possible, or a {@link URL} otherwise.
     *
     * @param  path The path to parse.
     * @return The path as a {@link File} if possible, or a {@link URL} otherwise.
     * @throws IOException If the given path is not a file and can't be parsed as a URL.
     */
    public static Object toFileOrURL(final String path) throws IOException {
        if (path.indexOf('?') < 0 && path.indexOf('#') < 0) {
            final int split = path.indexOf(':');
            /*
             * If the ':' character is found, the part before it is probably a protocol in a URL,
             * except in the particular case where there is just one letter before ':'. In such
             * case, it may be the drive letter of a Windows file.
             */
            if (split<0 || (split==1 && isLetter(path.charAt(0)) && !path.regionMatches(2, "//", 0, 2))) {
                return new File(path);
            }
        }
        final URL url = new URL(path);
        if (url.getProtocol().equalsIgnoreCase("file")) {
            return toFile(url, null);
        }
        return url;
    }

    /**
     * Returns the filename from a {@link String}, {@link File}, {@link URL} or {@link URI}.
     *
     * @param  path The path as a {@link String}, {@link File}, {@link URL} or {@link URI}.
     * @return The filename in the given path.
     *
     * @since 3.07
     */
    public static String name(final Object path) {
        if (path instanceof File) {
            return ((File) path).getName();
        }
        final String name;
        if (path instanceof URL) {
            name = ((URL) path).getPath();
        } else if (path instanceof URI) {
            name = ((URI) path).getPath();
        } else {
            name = path.toString();
        }
        return name.substring(name.lastIndexOf('/') + 1);
    }

    /**
     * Returns the filename extension from a {@link String}, {@link File}, {@link URL} or
     * {@link URI}. If no extension is found, returns an empty string.
     *
     * @param  path The path as a {@link String}, {@link File}, {@link URL} or {@link URI}.
     * @return The filename extension in the given path, or an empty string if none.
     */
    public static String extension(final Object path) {
        final String name;
        final int base;
        if (path instanceof File) {
            name = ((File) path).getName();
            base = 0;
        } else {
            if (path instanceof URL) {
                name = ((URL) path).getPath();
            } else if (path instanceof URI) {
                name = ((URI) path).getPath();
            } else {
                name = path.toString();
            }
            base = name.lastIndexOf('/');
        }
        final int i = name.lastIndexOf('.');
        return (i > base) ? name.substring(i+1).trim() : "";
    }

    /**
     * Changes the extension of the given {@link String}, {@link File}, {@link URL} or
     * {@link URI} argument. If the argument is not recognized, returns {@code null}.
     * If the result of this method is an object equals to {@code path}, then the
     * {@code path} instance is returned.
     * <p>
     * Note that this method converts {@link URI} objects to {@link URL}.
     *
     * @param  path The path as a {@link String}, {@link File}, {@link URL} or {@link URI}.
     * @param  extension The new extension, without leading dot.
     * @return The path with the new extension, or {@code null} if the given object has
     *         not been recognized (including {@code null} path).
     * @throws MalformedURLException If the given object is an {@link URI} or {@link URL},
     *         and changing the extension does not result in a valid URL.
     *
     * @since 3.07
     */
    public static Object changeExtension(final Object path, final String extension) throws MalformedURLException {
        String name;
        final int base;
        if (path instanceof File) {
            name = ((File) path).getName();
            base = 0;
        } else {
            if (path instanceof URL || path instanceof URI || path instanceof CharSequence) {
                name = path.toString();
            } else {
                return null;
            }
            base = name.lastIndexOf('/');
        }
        final StringBuffer buffer = new StringBuffer(name);
        final int i = name.lastIndexOf('.');
        if (i > base) {
            buffer.setLength(i+1);
        } else {
            buffer.append('.');
        }
        name = buffer.append(extension).toString();
        final Object result;
        if (path instanceof File) {
            result = new File(((File) path).getParent(), name);
        } else if (path instanceof URL || path instanceof URI) {
            result = new URL(name);
        } else {
            result = name;
        }
        return path.equals(result) ? path : result;
    }

    /**
     * Opens an input stream from the given {@link String}, {@link File}, {@link URL} or
     * {@link URI}.
     *
     * @param  path The file to open,
     * @return The input stream for the given file.
     * @throws IOException If an error occured while opening the given file.
     * @throws ClassCastException If the given object is not a known type.
     */
    public static InputStream open(Object path) throws IOException, ClassCastException {
        if (path instanceof CharSequence) {
            path = toFileOrURL(path.toString());
        }
        if (path instanceof File) {
            return new FileInputStream((File) path);
        }
        if (path instanceof URL) {
            return ((URL) path).openStream();
        }
        return ((URI) path).toURL().openStream();
    }

    /**
     * Opens a reader from the given {@link String}, {@link File}, {@link URL} or
     * {@link URI}. The character encoding is assumed ISO-LATIN-1.
     *
     * @param  path The file to open, as a {@link String}, {@link File}, {@link URL} or {@link URI}.
     * @return The buffered reader for the given file.
     * @throws IOException If an error occured while opening the given file.
     * @throws ClassCastException If the given object is not a known type.
     */
    public static LineNumberReader openLatin(final Object path) throws IOException, ClassCastException {
        return new LineNumberReader(new InputStreamReader(open(path), "ISO-8859-1"));
    }

    /**
     * Reads a row of a matrix and stores the values in the given buffer.
     *
     * @param  in     The input stream.
     * @param  grid   Where to store the values.
     * @param  offset Index of the first value to write in {@code grid}.
     * @param  numCol Expected number of columns.
     * @throws IOException if an error occured while reading the row.
     */
    public static void readMatrixRow(final BufferedReader in, final double[] grid,
            final int offset, final int numCol) throws IOException
    {
        final String line = in.readLine();
        if (line == null) {
            throw new EOFException(Errors.format(Errors.Keys.END_OF_DATA_FILE));
        }
        final StringTokenizer tokens = new StringTokenizer(line);
        for (int i=0; i<numCol; i++) {
            if (!tokens.hasMoreTokens()) {
                throw new ContentFormatException(Errors.format(
                        Errors.Keys.LINE_TOO_SHORT_$2, i, numCol));
            }
            final String token = tokens.nextToken();
            final double value;
            try {
                value = Double.parseDouble(token);
            } catch (NumberFormatException e) {
                throw new ContentFormatException(Errors.format(
                        Errors.Keys.UNPARSABLE_NUMBER_$1, token), e);
            }
            grid[offset + i] = value;
        }
        if (tokens.hasMoreElements()) {
            throw new ContentFormatException(Errors.format(Errors.Keys.LINE_TOO_LONG_$3,
                    numCol+tokens.countTokens(), numCol, tokens.nextToken()));
        }
    }

    /**
     * Reads a row of a matrix and stores the values in the given buffer.
     *
     * @param  in     The input stream.
     * @param  grid   Where to store the values.
     * @param  offset Index of the first value to write in {@code grid}.
     * @param  numCol Expected number of columns.
     * @throws IOException if an error occured while reading the row.
     */
    public static void readMatrixRow(final BufferedReader in, final float[] grid,
            final int offset, final int numCol) throws IOException
    {
        final String line = in.readLine();
        if (line == null) {
            throw new EOFException(Errors.format(Errors.Keys.END_OF_DATA_FILE));
        }
        final StringTokenizer tokens = new StringTokenizer(line);
        for (int i=0; i<numCol; i++) {
            if (!tokens.hasMoreTokens()) {
                throw new ContentFormatException(Errors.format(
                        Errors.Keys.LINE_TOO_SHORT_$2, i, numCol));
            }
            final String token = tokens.nextToken();
            final float value;
            try {
                value = Float.parseFloat(token);
            } catch (NumberFormatException e) {
                throw new ContentFormatException(Errors.format(
                        Errors.Keys.UNPARSABLE_NUMBER_$1, token), e);
            }
            grid[offset + i] = value;
        }
        if (tokens.hasMoreElements()) {
            throw new ContentFormatException(Errors.format(Errors.Keys.LINE_TOO_LONG_$3,
                    numCol+tokens.countTokens(), numCol, tokens.nextToken()));
        }
    }

    /**
     * Unzip the given stream to the given target directory.
     * This convenience method does not report the progress.
     *
     * @param  in The input stream to unzip. The stream will be closed.
     * @param  target The destination directory.
     * @throws IOException If an error occured while unzipping the entries.
     */
    public static void unzip(final InputStream in, final File target) throws IOException {
        final ZipInputStream def = new ZipInputStream(in);
        try {
            final byte[] buffer = new byte[4096];
            ZipEntry entry;
            while ((entry = def.getNextEntry()) != null) {
                final File file = new File(target, entry.getName());
                if (entry.isDirectory()) {
                    if (!file.isDirectory() && !file.mkdir()) {
                        throw new IOException(Errors.format(Errors.Keys.CANT_CREATE_DIRECTORY_$1, file));
                    }
                    continue;
                }
                final OutputStream out = new FileOutputStream(file);
                int n;
                while ((n = def.read(buffer)) >= 0) {
                    out.write(buffer, 0, n);
                }
                out.close();
                final long time = entry.getTime();
                if (time >= 0) {
                    file.setLastModified(time);
                }
                def.closeEntry();
            }
        } finally {
            def.close();
        }
    }
}
