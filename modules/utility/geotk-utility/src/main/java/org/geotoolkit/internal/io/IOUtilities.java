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
 * @version 3.00
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
     * Parses the following path as a {@link File} if possible, or a {@link URL} otherwise.
     *
     * @param path The path to parse.
     * @return The path as a {@link File} if possible, or a {@link URL} otherwise.
     * @throws MalformedURLException If the given path is not a file and can't be parsed as a URL.
     */
    public static Object toFileOrURL(final String path) throws MalformedURLException {
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
            return new File(url.getFile());
        }
        return url;
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
        final int i = name.lastIndexOf('.') + 1;
        return (i > base) ? name.substring(i).trim() : "";
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
