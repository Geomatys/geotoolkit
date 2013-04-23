/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2008-2012, Open Source Geospatial Foundation (OSGeo)
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

import java.io.*;
import java.net.URI;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URISyntaxException;
import java.net.MalformedURLException;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.Files;
import java.nio.file.FileSystemNotFoundException;
import java.util.Locale;
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
 * @author Johann Sorel (Geomatys)
 * @version 3.20
 *
 * @since 3.00
 * @module
 */
public final class IOUtilities extends Static {
    /**
     * The default buffer size for copy operations.
     */
    private static final int BUFFER_SIZE = 8192;

    /**
     * The writer to the console (if possible) or standard output stream otherwise.
     * This is created when first needed.
     */
    private static Writer stdout;

    /**
     * A printer wrapping {@link #stdout}, created when first needed.
     */
    private static PrintWriter printer;

    /**
     * Do not allow instantiation of this class.
     */
    private IOUtilities() {
    }

    /**
     * Returns a writer to the standard output stream. This method returns the
     * {@linkplain Console#writer() console writer} if available, because that
     * writer uses a more appropriate encoding on some platform. In no console
     * writer is available, then a writer wrapping the {@linkplain System#out
     * standard output stream} is returned.
     *
     * @return A writer to the standard output stream.
     *
     * @since 3.17
     */
    public static synchronized Writer standardWriter() {
        if (stdout == null) {
            final Console console = System.console();
            if (console != null) {
                stdout = printer = console.writer();
            } else {
                stdout = new OutputStreamWriter(System.out);
            }
        }
        return stdout;
    }

    /**
     * Returns a printer to the standard output stream. This method returns the
     * {@linkplain Console#writer() console printer} if available, because that
     * printer uses a more appropriate encoding on some platform. In no console
     * printer is available, then a printer wrapping the {@linkplain System#out
     * standard output stream} is returned.
     *
     * @return A printer to the standard output stream.
     *
     * @see org.geotoolkit.io.NumberedLineWriter#getStandardOutput()
     *
     * @since 3.17
     */
    public static synchronized PrintWriter standardPrintWriter() {
        if (printer == null) {
            final Writer writer = standardWriter();
            if (printer == null) {
                printer = new PrintWriter(writer, true);
            }
        }
        return printer;
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
     * Encodes the characters that are not legal for the {@link URI(String)} constructor.
     * Note that in addition of unreserved characters ("{@code _-!.~'()*}") the reserved
     * characters ("{@code ?/[]@}") and the punctuation characters ("{@code ,;:$&+=}")
     * are left unchanged, so they will be processed with their special meaning by the
     * URI constructor.
     * <p>
     * The current implementations replaces only the space characters, control characters
     * and the {@code %} character. Future versions may replace more characters as needed
     * from experience.
     *
     * @param  path The path to encode.
     * @return The encoded path.
     *
     * @since 3.15
     */
    public static String encodeURI(final String path) {
        Charset encoding = null;
        StringBuilder buffer = null;
        final int length = path.length();
        for (int i=0; i<length; i++) {
            final char c = path.charAt(i);
            if (!Character.isSpaceChar(c) && !Character.isISOControl(c) && c != '%') {
                /*
                 * The character is valid, or is punction character, or is a reserved character.
                 * All those characters should be handled properly by the URI(String) constructor.
                 */
                if (buffer != null) {
                    buffer.append(c);
                }
                continue;
            }
            /*
             * The character is invalid, so we need to escape it. Note that the encoding
             * is fixed to UTF-8 as of java.net.URI specification (see its class javadoc).
             */
            if (buffer == null) {
                buffer = new StringBuilder(path);
                buffer.setLength(i);
                encoding = Charset.forName("UTF-8");
            }
            for (final byte b : String.valueOf(c).getBytes(encoding)) {
                buffer.append('%');
                final String hex = Integer.toHexString(b & 0xFF).toUpperCase(Locale.US);
                if (hex.length() < 2) {
                    buffer.append('0');
                }
                buffer.append(hex);
            }
        }
        return (buffer != null) ? buffer.toString() : path;
    }

    /**
     * Converts a {@link URL} to a {@link File}. Conceptually this work is performed by a call
     * to {@link URL#toURI()}. However this method adds the following functionalities:
     * <p>
     * <ul>
     *   <li>Optionally decodes the {@code "%XX"} sequences, where {@code "XX"} is a number.</li>
     *   <li>Converts various exceptions into subclasses of {@link IOException}.</li>
     * </ul>
     *
     * @param  url The URL (may be {@code null}).
     * @param  encoding If the URL is encoded in a {@code application/x-www-form-urlencoded}
     *         MIME format, the character encoding (normally {@code "UTF-8"}). If the URL is
     *         not encoded, then {@code null}.
     * @return The URI for the given URL, or {@code null} if the given URL was null.
     * @throws IOException if the URL can not be converted to a URI.
     *
     * @since 3.20 (derived from 3.05)
     */
    public static URI toURI(final URL url, final String encoding) throws IOException {
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
        path = encodeURI(path);
        try {
            return new URI(path);
        } catch (URISyntaxException cause) {
            /*
             * Occurs only if the URL is not compliant with RFC 2396. Otherwise every URL
             * should succeed, so a failure can actually be considered as a malformed URL.
             */
            final MalformedURLException e = new MalformedURLException(concatenate(
                    Errors.format(Errors.Keys.ILLEGAL_ARGUMENT_2, "URL", path), cause));
            e.initCause(cause);
            throw e;
        }
    }

    /**
     * Converts a {@link URL} to a {@link File}. Conceptually this work is performed by a call
     * to {@link URL#toURI()} followed by a call to the {@link File#File(URI)} constructor.
     * However this method adds the following functionalities:
     * <p>
     * <ul>
     *   <li>Optionally decodes the {@code "%XX"} sequences, where {@code "XX"} is a number.</li>
     *   <li>Converts various exceptions into subclasses of {@link IOException}.</li>
     * </ul>
     *
     * @param  url The URL (may be {@code null}).
     * @param  encoding If the URL is encoded in a {@code application/x-www-form-urlencoded}
     *         MIME format, the character encoding (normally {@code "UTF-8"}). If the URL is
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
        final URI uri = toURI(url, encoding);
        /*
         * We really want to call the File constructor expecting a URI argument,
         * not the constructor expecting a String argument, because the one for
         * the URI argument performs additional platform-specific parsing.
         */
        try {
            return new File(uri);
        } catch (IllegalArgumentException cause) {
            /*
             * Typically happen when the URI contains fragment that can not be represented
             * in a File (e.g. a Query part), so it could be considered as if the URI with
             * the fragment part can not represent an existing file.
             */
            final MalformedURLException e = new MalformedURLException(concatenate(
                    Errors.format(Errors.Keys.ILLEGAL_ARGUMENT_2, "URL", url), cause));
            e.initCause(cause);
            throw e;
        }
    }

    /**
     * Converts a {@link URL} to a {@link Path}. Conceptually this work is performed by a call
     * to {@link URL#toURI()} followed by a call to the {@link Paths#get(URI)} static method.
     * However this method adds the following functionalities:
     * <p>
     * <ul>
     *   <li>Optionally decodes the {@code "%XX"} sequences, where {@code "XX"} is a number.</li>
     *   <li>Converts various exceptions into subclasses of {@link IOException}.</li>
     * </ul>
     *
     * @param  url The URL (may be {@code null}).
     * @param  encoding If the URL is encoded in a {@code application/x-www-form-urlencoded}
     *         MIME format, the character encoding (normally {@code "UTF-8"}). If the URL is
     *         not encoded, then {@code null}.
     * @return The path for the given URL, or {@code null} if the given URL was null.
     * @throws IOException if the URL can not be converted to a path.
     *
     * @since 3.20 (derived from 3.05)
     */
    public static Path toPath(final URL url, final String encoding) throws IOException {
        if (url == null) {
            return null;
        }
        final URI uri = toURI(url, encoding);
        try {
            return Paths.get(uri);
        } catch (IllegalArgumentException | FileSystemNotFoundException cause) {
            final MalformedURLException e = new MalformedURLException(concatenate(
                    Errors.format(Errors.Keys.ILLEGAL_ARGUMENT_2, "URL", url), cause));
            e.initCause(cause);
            throw e;
        }
    }

    /**
     * Returns {@code true} if the given string seems to be an ordinary file.
     * If {@code false}, then the path is more likely to be a URL.
     *
     * @param  path The path to check.
     * @return {@code true} if the path seems to be an ordinary file path.
     *
     * @since 3.20 (derived from 3.00)
     */
    private static boolean isFile(final String path) {
        if (path.indexOf('?') < 0 && path.indexOf('#') < 0) {
            final int split = path.indexOf(':');
            /*
             * If the ':' character is found, the part before it is probably a protocol in a URL,
             * except in the particular case where there is just one letter before ':'. In such
             * case, it may be the drive letter of a Windows file.
             */
            if (split<0 || (split==1 && isLetter(path.charAt(0)) && !path.regionMatches(2, "//", 0, 2))) {
                return true;
            }
        }
        return false;
    }

    /**
     * Parses the following path as a {@link File} if possible, or a {@link URL} otherwise.
     *
     * @param  path The path to parse.
     * @return The path as a {@link File} if possible, or a {@link URL} otherwise.
     * @throws IOException If the given path is not a file and can't be parsed as a URL.
     */
    public static Object toFileOrURL(final String path) throws IOException {
        if (isFile(path)) {
            return new File(path);
        }
        final URL url = new URL(path);
        if (url.getProtocol().equalsIgnoreCase("file")) {
            return toFile(url, null);
        }
        return url;
    }

    /**
     * Parses the following path as a {@link Path} if possible, or a {@link URL} otherwise.
     *
     * @param  path The path to parse.
     * @return The path as a {@link Path} if possible, or a {@link URL} otherwise.
     * @throws IOException If the given path is not a file and can't be parsed as a URL.
     *
     * @since 3.20 (derived from 3.00)
     */
    public static Object toPathOrURL(final String path) throws IOException {
        if (isFile(path)) {
            return Paths.get(path);
        }
        final URL url = new URL(path);
        if (url.getProtocol().equalsIgnoreCase("file")) {
            return toPath(url, null);
        }
        return url;
    }

    /**
     * Tries to convert the given path to a {@link File} object if possible, or returns
     * the path unchanged otherwise. Conversion attempts are performed for paths of class
     * {@link CharSequence}, {@link URL}, {@link URI} or {@link Path}.
     * <p>
     * If a conversion from a {@link URL} object was necessary, then the URL is assumed
     * to <strong>not</strong> be encoded.
     *
     * @param  path The path to convert to a {@link File} if possible.
     * @return The path as a {@link File} if this conversion was possible.
     * @throws IOException If an error occurred while converting the path to a file.
     *
     * @since 3.07
     */
    public static Object tryToFile(Object path) throws IOException {
        if (path instanceof CharSequence) {
            path = toFileOrURL(path.toString());
        } else if (path instanceof URL) {
            final URL url = (URL) path;
            if (url.getProtocol().equalsIgnoreCase("file")) {
                path = toFile(url, null);
            }
        } else if (path instanceof URI) {
            final URI uri = (URI) path;
            final String scheme = uri.getScheme();
            if (scheme != null && scheme.equalsIgnoreCase("file")) try {
                path = new File(uri);
            } catch (IllegalArgumentException cause) {
                /*
                 * Typically because the URI contains a fragment (for example a query part)
                 * that can not be represented as a File. We consider that as an error
                 * because the scheme pretended that we had a file URI.
                 */
                final IOException e = new MalformedURLException(concatenate(
                        Errors.format(Errors.Keys.ILLEGAL_ARGUMENT_2, "URI", path), cause));
                e.initCause(cause);
                throw e;
            }
        } else if (path instanceof Path) {
            path = ((Path) path).toFile();
        }
        return path;
    }

    /**
     * Tries to convert the given path to a {@link Path} object if possible, or returns
     * the path unchanged otherwise. Conversion attempts are performed for paths of class
     * {@link CharSequence}, {@link URL}, {@link URI} or {@link File}.
     * <p>
     * If a conversion from a {@link URL} object was necessary, then the URL is assumed
     * to <strong>not</strong> be encoded.
     *
     * @param  path The path to convert to a {@link Path} if possible.
     * @return The path as a {@link Path} if this conversion was possible.
     * @throws IOException If an error occurred while converting the path to a file.
     *
     * @since 3.20 (derived from 3.07)
     */
    public static Object tryToPath(Object path) throws IOException {
        if (path instanceof CharSequence) {
            path = toPathOrURL(path.toString());
        } else if (path instanceof URL) {
            final URL url = (URL) path;
            if (url.getProtocol().equalsIgnoreCase("file")) {
                path = toPath(url, null);
            }
        } else if (path instanceof URI) {
            final URI uri = (URI) path;
            final String scheme = uri.getScheme();
            if (scheme != null && scheme.equalsIgnoreCase("file")) try {
                path = Paths.get(uri);
            } catch (IllegalArgumentException cause) {
                /*
                 * Typically because the URI contains a fragment (for example a query part)
                 * that can not be represented as a File. We consider that as an error
                 * because the scheme pretended that we had a file URI.
                 */
                final IOException e = new MalformedURLException(concatenate(
                        Errors.format(Errors.Keys.ILLEGAL_ARGUMENT_2, "URI", path), cause));
                e.initCause(cause);
                throw e;
            }
        } else if (path instanceof File) {
            path = ((File) path).toPath();
        }
        return path;
    }

    /**
     * Returns {@code true} if the method in this class can process the given object as a path.
     *
     * @param  path The object to test, or {@code null}.
     * @return {@code true} If the given object is non-null and can be processed like a path.
     *
     * @since 3.08
     */
    public static boolean canProcessAsPath(final Object path) {
        return (path instanceof CharSequence) || (path instanceof File) ||
                (path instanceof URL) || (path instanceof URI) || (path instanceof Path);
    }

    /**
     * Returns the filename from a {@link String}, {@link File}, {@link URL}, {@link URI}
     * or {@link Path}.
     *
     * @param  path The path as a {@link String}, {@link File}, {@link URL}, {@link URI} or {@link Path}.
     * @return The filename in the given path.
     *
     * @since 3.07
     */
    public static String name(final Object path) {
        if (path instanceof Path) {
            return ((Path) path).getFileName().toString();
        }
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
     * Returns the filename extension from a {@link String}, {@link File}, {@link URL},
     * {@link URI} or {@link Path}. If no extension is found, returns an empty string.
     *
     * @param  path The path as a {@link String}, {@link File}, {@link URL}, {@link URI} or {@link Path}.
     * @return The filename extension in the given path, or an empty string if none.
     */
    public static String extension(final Object path) {
        final String name;
        final int base;
        if (path instanceof Path) {
            name = ((Path) path).getFileName().toString();
            base = 0;
        } else if (path instanceof File) {
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
     * Changes the extension of the given {@link String}, {@link File}, {@link URL}, {@link URI}
     * {@link Path} argument. If the argument is not recognized, returns {@code null}.
     * If the result of this method is an object equals to {@code path}, then the
     * {@code path} instance is returned.
     * <p>
     * Note that this method converts {@link URI} objects to {@link URL}.
     *
     * @param  path The path as a {@link String}, {@link File}, {@link URL}, {@link URI} or {@link Path}.
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
        if (path instanceof Path) {
            name = ((Path) path).getFileName().toString();
            base = 0;
        } else if (path instanceof File) {
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
        if (path instanceof Path) {
            result = ((Path) path).resolveSibling(name);
        } else if (path instanceof File) {
            result = new File(((File) path).getParent(), name);
        } else if (path instanceof URL || path instanceof URI) {
            result = new URL(name);
        } else {
            result = name;
        }
        return path.equals(result) ? path : result;
    }

    /**
     * Opens an input stream from the given {@link String}, {@link File}, {@link URL}, {@link URI}
     * or {@link Path}. The stream will not be buffered, and is not required to support the mark or
     * reset methods.
     * <p>
     * It is the caller responsibility to close the given stream. This method does not accept
     * pre-existing streams because they would usually require a different handling by the
     * caller (e.g. in many case, the caller will not want to close such pre-existing streams).
     *
     * @param  path The file to open,
     * @return The input stream for the given file.
     * @throws IOException If an error occurred while opening the given file.
     * @throws ClassCastException If the given object is not a known type.
     */
    public static InputStream open(Object path) throws IOException, ClassCastException {
        if (path instanceof CharSequence) {
            path = toFileOrURL(path.toString());
        }
        if (path instanceof File) {
            return new FileInputStream((File) path);
        }
        if (path instanceof Path) {
            return Files.newInputStream((Path) path);
        }
        return ((path instanceof URI) ? ((URI) path).toURL() : ((URL) path)).openStream();
    }

    /**
     * Opens a reader from the given {@link String}, {@link File}, {@link URL}, {@link URI} or
     * {@link Path}. The character encoding is assumed ISO-LATIN-1.
     *
     * @param  path The file to open, as a {@link String}, {@link File}, {@link URL} or {@link URI}.
     * @return The buffered reader for the given file.
     * @throws IOException If an error occurred while opening the given file.
     * @throws ClassCastException If the given object is not a known type.
     */
    public static LineNumberReader openLatin(final Object path) throws IOException, ClassCastException {
        return new LineNumberReader(new InputStreamReader(open(path), "ISO-8859-1"));
    }

    /**
     * Opens an output stream from the given {@link String}, {@link File}, {@link URL},
     * {@link URI} or {@link Path}.
     *
     * @param  path The file to open,
     * @return The output stream for the given file.
     * @throws IOException If an error occurred while opening the given file.
     * @throws ClassCastException If the given object is not a known type.
     *
     * @since 3.07
     */
    public static OutputStream openWrite(Object path) throws IOException, ClassCastException {
        if (path instanceof CharSequence) {
            path = toFileOrURL(path.toString());
        }
        if (path instanceof File) {
            return new FileOutputStream((File) path);
        }
        if (path instanceof Path) {
            return Files.newOutputStream((Path) path);
        }
        return ((path instanceof URI) ? ((URI) path).toURL() : ((URL) path)).openConnection().getOutputStream();
    }

    /**
     * Closes the given stream if it is closeable, or do nothing otherwise. Closeable
     * objects are any instance of {@link Closeable}.
     *
     * @param  stream The object to close if it is closeable.
     * @throws IOException If an error occurred while closing the stream.
     *
     * @since 3.07
     */
    public static void close(final Object stream) throws IOException {
        if (stream instanceof Closeable) {
            ((Closeable) stream).close();
        }
        // On JDK6, we needed an explicit check for ImageInputStream.
        // Since JDK7, this is not needed anymore.
    }

    /**
     * Reads a row of a matrix and stores the values in the given buffer.
     *
     * @param  in     The input stream.
     * @param  grid   Where to store the values.
     * @param  offset Index of the first value to write in {@code grid}.
     * @param  numCol Expected number of columns.
     * @throws IOException if an error occurred while reading the row.
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
                        Errors.Keys.LINE_TOO_SHORT_2, i, numCol));
            }
            final String token = tokens.nextToken();
            final double value;
            try {
                value = Double.parseDouble(token);
            } catch (NumberFormatException e) {
                throw new ContentFormatException(concatenate(
                        Errors.format(Errors.Keys.UNPARSABLE_NUMBER_1, token), e), e);
            }
            grid[offset + i] = value;
        }
        if (tokens.hasMoreElements()) {
            throw new ContentFormatException(Errors.format(Errors.Keys.LINE_TOO_LONG_3,
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
     * @throws IOException if an error occurred while reading the row.
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
                        Errors.Keys.LINE_TOO_SHORT_2, i, numCol));
            }
            final String token = tokens.nextToken();
            final float value;
            try {
                value = Float.parseFloat(token);
            } catch (NumberFormatException e) {
                throw new ContentFormatException(concatenate(
                        Errors.format(Errors.Keys.UNPARSABLE_NUMBER_1, token), e), e);
            }
            grid[offset + i] = value;
        }
        if (tokens.hasMoreElements()) {
            throw new ContentFormatException(Errors.format(Errors.Keys.LINE_TOO_LONG_3,
                    numCol+tokens.countTokens(), numCol, tokens.nextToken()));
        }
    }

    /**
     * Concatenates the given message with the message of the given exception, if any.
     * This is used when an exception is catch and rethrow, in order to provide more
     * useful message.
     */
    private static String concatenate(String message, final Exception exception) {
        final String cause = exception.getLocalizedMessage();
        if (cause != null) {
            message = message + ' ' + cause;
        }
        return message;
    }

    /**
     * Unzip the given stream to the given target directory.
     * This convenience method does not report the progress.
     *
     * @param  in The input stream to unzip. <strong>The stream will be closed.</strong>
     * @param  target The destination directory.
     * @throws IOException If an error occurred while unzipping the entries.
     */
    public static void unzip(final InputStream in, final File target) throws IOException {
        try (ZipInputStream def = new ZipInputStream(in)) {
            final byte[] buffer = new byte[BUFFER_SIZE];
            ZipEntry entry;
            while ((entry = def.getNextEntry()) != null) {
                final File file = new File(target, entry.getName());
                if (entry.isDirectory()) {
                    if (!file.isDirectory() && !file.mkdir()) {
                        throw new IOException(Errors.format(Errors.Keys.CANT_CREATE_DIRECTORY_1, file));
                    }
                    continue;
                }
                try (OutputStream out = new FileOutputStream(file)) {
                    int n;
                    while ((n = def.read(buffer)) >= 0) {
                        out.write(buffer, 0, n);
                    }
                }
                final long time = entry.getTime();
                if (time >= 0) {
                    file.setLastModified(time);
                }
                def.closeEntry();
            }
        }
    }

    /**
     * Copies the content from the given input stream to the given output stream.
     * This method does not close the given streams.
     *
     * @param input  The source of bytes to copy.
     * @param output The destination where to copy.
     * @throws IOException If an error occurred while performing the copy operation.
     *
     * @since 3.20
     */
    public static void copy(final InputStream input, final OutputStream output) throws IOException {
        final byte[] buffer = new byte[BUFFER_SIZE];
        int bytesRead;
        while ((bytesRead = input.read(buffer)) >= 0) {
            output.write(buffer, 0, bytesRead);
        }
    }
}
