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
package org.geotoolkit.nio;

import org.apache.sis.util.ArgumentChecks;
import org.apache.sis.util.Exceptions;
import org.apache.sis.util.logging.Logging;
import org.geotoolkit.io.ContentFormatException;
import org.geotoolkit.lang.Static;
import org.geotoolkit.resources.Errors;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.Closeable;
import java.io.Console;
import java.io.EOFException;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Writer;
import java.net.*;
import java.nio.charset.Charset;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.spi.FileSystemProvider;
import java.util.Collections;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import static java.nio.file.Files.*;
import static java.nio.file.StandardOpenOption.*;


/**
 * Utility methods related to I/O operations.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @author Johann Sorel (Geomatys)
 * @author Quentin Boileau (Geomatys)
 * @version 4.1
 *
 * @since 3.00
 * @module
 */
public final class IOUtilities extends Static {

    /**
     * Logger
     */
    private static final Logger LOGGER = Logging.getLogger("org.geotoolkit.io");

    /**
     * The default buffer size for copy operations.
     */
    private static final int BUFFER_SIZE = 8192;

    /**
     * Default charset with UTF-8
     */
    public static final Charset UTF8_CHARSET = Charset.forName("UTF-8");

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
     * Keep all {@link Path} to delete on JVM shutdown event on a concurrent Set.
     */
    private static final Set<Path> DELETE_ON_EXIT_PATHS = Collections.newSetFromMap(new ConcurrentHashMap<Path, Boolean>());
    static {
        Runtime.getRuntime().addShutdownHook(new Thread() {
            public void run() {
                for (Path path : DELETE_ON_EXIT_PATHS) {
                    if (Files.exists(path)) {
                        try {
                            if (Files.isDirectory(path)) {
                                IOUtilities.deleteRecursively(path);
                            } else {
                                Files.deleteIfExists(path);
                            }
                        } catch (IOException e) {
                            LOGGER.log(Level.WARNING, "Unable to delete on exit file : " + path.toString());
                        }
                    }
                }
            }
        });
    }

    /**
     * Do not allow instantiation of this class.
     */
    private IOUtilities() {
    }

    /**
     * Register {@link Path} for a delete on JM shutdown event.
     * Under the hood, this method register path to be delete using {@link Runtime#addShutdownHook(Thread)}.
     *
     * @param path path to delete
     */
    public static void deleteOnExit(Path path) {
        DELETE_ON_EXIT_PATHS.add(path);
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
     * If the two given Path are equals, return that Path. Otherwise returns the first
     * common parent found.
     *
     * @param  root The Path which is the most likely to be the root.
     * @param  file The other Path, which is more likely to be in a sub-directory of the root.
     * @return The root or a common parent, or {@code null} if no common parent has been found.
     */
    public static Path commonParent(Path root, final Path file) {
        while (root != null) {
            root = root.normalize();
            if (file != null) {
                final Path nfile = file.normalize();
                for (Path candidate = nfile; candidate != null; candidate = candidate.getParent()) {
                    if (root.equals(candidate)) {
                        return root;
                    }
                }
                root = root.getParent();
            }
        }
        return null;
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
     * @deprecated prefer using {@link #tryToPath(Object)} method to deal with Path instead of File
     */
    public static Object tryToFile(Object path) throws IOException {
        if (path instanceof File) {
            return (File) path;
        } else if (path instanceof CharSequence) {
            return org.apache.sis.internal.storage.IOUtilities.toFileOrURL(path.toString(), null);
        } else if (path instanceof URL) {
            final URL url = (URL) path;
            if (url.getProtocol().equalsIgnoreCase("file")) {
                return org.apache.sis.internal.storage.IOUtilities.toFile(url, null);
            }
        } else if (path instanceof URI) {
            final URI uri = (URI) path;
            final String scheme = uri.getScheme();
            if (scheme != null && scheme.equalsIgnoreCase("file")) try {
                return new File(uri);
            } catch (IllegalArgumentException cause) {
                /*
                 * Typically because the URI contains a fragment (for example a query part)
                 * that can not be represented as a File. We consider that as an error
                 * because the scheme pretended that we had a file URI.
                 */
                final IOException e = new MalformedURLException(concatenate(
                        Errors.format(Errors.Keys.IllegalArgument_2, "URI", path), cause));
                e.initCause(cause);
                throw e;
            }
        } else if (path instanceof Path) {
            return ((Path) path).toFile();
        }
        return path;
    }

    /**
     * Tries to convert the given object to a {@link Path} object if possible, or returns
     * the path unchanged otherwise. Conversion attempts are performed for objects of class
     * {@link CharSequence}, {@link URL}, {@link URI} or {@link File}.
     * <p>
     * If a conversion from a {@link URL} object was necessary, then the URL is assumed
     * to <strong>not</strong> be encoded.
     *
     * @param  path The path to convert to a {@link Path} if possible.
     * @return The path as a {@link Path} if this conversion was possible. A Conversion to Path can fail
     * if input Object is not supported or converted Path use a FileSystem not supported by default.
     *
     * @since 3.20 (derived from 3.07)
     */
    public static Object tryToPath(Object path) {
        try {
            return toPath(path);
        } catch (IllegalArgumentException | IOException ex) {
            // input candidate can't be converted into Path
            return path;
        }
    }

    /**
     * Tries to convert the given candidate into a {@link Path} object if possible, or throw IllegalArgumentException
     * if candidate can't be converted. Conversion attempts are performed for paths of class
     * {@link CharSequence}, {@link URL}, {@link URI} or {@link File}.
     * <p>
     * If a conversion from a {@link URL} object was necessary, then the URL is assumed
     * to <strong>not</strong> be encoded.
     *
     * @param  candidate The candidate to convert to a {@link Path} if possible.
     * @return The candidate as a {@link Path} if this conversion was possible.
     * @throws IOException If an error occurred while converting the candidate to a Path. For example a not supported
     * FileSystem
     * @throws IllegalArgumentException input object can't be converted into {@link Path}
     *
     */
    public static Path toPath(Object candidate) throws IOException, IllegalArgumentException {
        if (candidate instanceof Path) {
            return (Path) candidate;
        }
        if (candidate instanceof CharSequence) {
            return Paths.get(candidate.toString());
        } else if (candidate instanceof URL) {
            final URL url = (URL) candidate;
            return org.apache.sis.internal.storage.IOUtilities.toPath(url, null);
        } else if (candidate instanceof File) {
            return ((File) candidate).toPath();
        } else if (candidate instanceof URI) {
            final URI uri = (URI) candidate;
            try {
                if (uri.getScheme() == null) {
                    //scheme null, consider as Path on default FileSystem
                    return Paths.get(uri.toString());
                }
                return Paths.get(uri);
            } catch (IllegalArgumentException | FileSystemNotFoundException cause) {
                final String message = Exceptions.formatChainedMessages(null,
                        org.apache.sis.util.resources.Errors.format(org.apache.sis.util.resources.Errors.Keys.IllegalArgumentValue_2, "URI", uri), cause);
                /*
                 * If the exception is IllegalArgumentException, then the URI scheme has been recognized
                 * but the URI syntax is illegal for that file system. So we can consider that the URL is
                 * malformed in regard to the rules of that particular file system.
                 */
                final IOException e;
                if (cause instanceof IllegalArgumentException) {
                    e = new MalformedURLException(message);
                    e.initCause(cause);
                } else {
                    e = new IOException(message, cause);
                }
                throw e;
            }
        }
        throw new IllegalArgumentException("Can't convert "+candidate.getClass()+" into a Path."+
        "Supported candidate type are CharSequence, URL, URI, File and Path");
    }

    /**
     * Returns {@code true} if the method in this class can process the given object as a path.
     * Note: {@link String} input is not considered compatible as {@link Path}.
     *
     * @param  path The object to test, or {@code null}.
     * @return {@code true} If the given object is non-null and can be processed like a path.
     *
     * @since 3.08
     */
    public static boolean canProcessAsPath(final Object path) {
        return (
                //(path instanceof CharSequence) ||
                (path instanceof File) ||
                (path instanceof URL) ||
                (path instanceof URI) ||
                (path instanceof Path));
    }

    /**
     * Test if an object can be process as Path (see {@link #canProcessAsPath(Object)}) and if input object
     * target a known FileSystem using his scheme and {@link FileSystemProvider#installedProviders()}.
     *
     * @param path candidate object
     * @return {@code true} if input object is compatible with {@link Path} API AND
     * if FileSystem is known. {@code false} otherwise.
     */
    public static boolean isFileSystemSupported(final Object path) {
        if (!canProcessAsPath(path)) {
            return false;
        }

        URI uri = null;
        if (path instanceof URL) {
            try {
                uri = org.apache.sis.internal.storage.IOUtilities.toURI((URL) path, "UTF-8");
            } catch (IOException e) {
                //unable to create URI from URL
                return false;
            }
        } else if (path instanceof URI) {
            uri = (URI) path;
        } else {
            return true;
        }

        String scheme = uri.getScheme();
        if (scheme == null) {
            //scheme null, consider as Path on default FileSystem
           return true;
        } else {
            // loop over FS providers and test path creation
            for (FileSystemProvider provider: FileSystemProvider.installedProviders()) {
                if (provider.getScheme().equalsIgnoreCase(scheme)) {
                    try {
                        provider.getPath(uri);
                        return true;
                    } catch (IllegalArgumentException fse) {
                        // path construction failed
                    }
                }
            }
            //no matching FS provider
            return false;
        }
    }

    /**
     * Extract file name without extension from path object (Usually instance of {@link String},
     * {@link File}, {@link URL}, {@link URI} or {@link Path}).
     *
     * @param path candidate path object
     * @return path file name without extension or {@code null} if input object can't be processed as {@link Path}.
     */
    public static String filenameWithoutExtension(Object path) {
        final boolean isPath = canProcessAsPath(path);
        if (!isPath) {
            return null;
        }

        try {
            return filenameWithoutExtension(toPath(path));
        } catch (IOException e) {
            //silent exception
            return null;
        }
    }

    /**
     * Extract file name without extension from a {@link Path}.
     * If path file name doesn't have an extension, full file name String
     * will be returned.
     *
     * @param path path to process
     * @return file name without extension.
     * @throws NullPointerException if input path is {@code null}.
     */
    public static String filenameWithoutExtension(Path path) {
        ArgumentChecks.ensureNonNull("path", path);
        final String fileName = path.getFileName().toString();
        final int dot = fileName.lastIndexOf('.');
        if (dot > 0) {
            return fileName.substring(0, dot);
        }
        return fileName;
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
    public static Object changeExtension(final Object path, final String extension) throws MalformedURLException, IOException {
        final boolean isPath = canProcessAsPath(path);
        if (!isPath) {
            //special case of CharSequence and String
            if (path instanceof CharSequence) {
                String pathStr = (String) path;
                int dotIdx = pathStr.lastIndexOf('.');
                if (dotIdx > 0) {
                    pathStr = pathStr.substring(0, dotIdx);
                }
                return pathStr + "." + extension;
            }
            return null;
        }

        Path realPath = toPath(path);
        final Path outPath = changeExtension(realPath, extension);

        if (path instanceof Path) {
            return outPath;
        } else if (path instanceof String) {
            return outPath.toString();
        } else if (path instanceof File) {
            return outPath.toFile();
        } else if (path instanceof URL || path instanceof URI) {
            return outPath.toUri().toURL();
        }
        return null;
    }

    /**
     * Changes the extension of the given {@link Path} argument.
     *
     * @param  path The path as {@link Path}.
     * @param  extension The new extension, without leading dot.
     * @return The path with the new extension.
     */
    public static Path changeExtension(final Path path, final String extension) {
        final String previousExt = org.apache.sis.internal.storage.IOUtilities.extension(path);
        if ((previousExt == null && extension == null) || (previousExt != null && previousExt.equals(extension))) {
            return path;
        }
        final String siblingName = filenameWithoutExtension(path) +'.'+ extension;
        return path.resolveSibling(siblingName);
    }

    /**
     * Opens an input stream from the given {@link String}, {@link File}, {@link URL}, {@link URI},
     * {@link Path} or {@link InputStream}. The stream will not be buffered, and is not required to support the mark or
     * reset methods.
     * <p>
     * It is the caller responsibility to close the given stream. This method does not accept
     * pre-existing streams because they would usually require a different handling by the
     * caller (e.g. in many case, the caller will not want to close such pre-existing streams).
     *
     * @param  resource The file to open,
     * @return The input stream for the given resource.
     * @throws IOException If an error occurred while opening the given file or input resource is not supported.
     */
    public static InputStream open(Object resource) throws IOException {
        return open(resource, READ);
    }

    /**
     * Opens an input stream from the given {@link String}, {@link File}, {@link URL}, {@link URI},
     * {@link Path} or {@link InputStream}. The stream will not be buffered, and is not required to support the mark or
     * reset methods.
     * <p>
     * It is the caller responsibility to close the given stream. This method does not accept
     * pre-existing streams because they would usually require a different handling by the
     * caller (e.g. in many case, the caller will not want to close such pre-existing streams).
     *
     * @param  resource The file to open,
     * @param options open options
     * @return The input stream for the given resource.
     * @throws IOException If an error occurred while opening the given file or input resource is not supported.
     */
    public static InputStream open(Object resource, OpenOption... options) throws IOException {
        ArgumentChecks.ensureNonNull("resource", resource);
        if (resource instanceof InputStream) {
            return (InputStream) resource;
        }

        if (!canProcessAsPath(resource)) {
            throw new IOException("Can not handle input type : " + resource.getClass());
        }

        try {
            Path realPath = toPath(resource);
            return newInputStream(realPath, options);
        } catch (IOException e) {
             /*
                An IOException is also thrown if toPath catch a FileSystemNotFoundException (http case)
                Try with URL.
                Typical case when resource input is an URL not supported as FileSystem
                Path conversion will fail
             */
            URL url = null;
            if (resource instanceof URL) {
                url = (URL) resource;
            }

            if (resource instanceof URI) {
                URI uri = (URI) resource;
                try {
                    url = uri.toURL();
                } catch (MalformedURLException e2) {
                    e.addSuppressed(e2);
                }
            }

            if (url != null) {
                return url.openStream();
            }
            throw e;
        }
    }

    /**
     * Opens a reader from the given {@link String}, {@link File}, {@link URL}, {@link URI} ,
     * {@link Path} or {@link InputStream}. The character encoding is assumed ISO-LATIN-1.
     *
     * @param  path The file to open, as a {@link String}, {@link File}, {@link URL} or {@link URI}.
     * @return The buffered reader for the given file.
     * @throws IOException If an error occurred while opening the given file.
     * @throws ClassCastException If the given object is not a known type.
     */
    public static LineNumberReader openLatin(final Object path) throws IOException {
        return new LineNumberReader(new InputStreamReader(open(path), "ISO-8859-1"));
    }

    /**
     * Opens an output stream from the given {@link String}, {@link File}, {@link URL},
     * {@link URI}, {@link Path} or {@link OutputStream}.
     *
     * @param  resource The resource to open,
     * @return The output stream for the given file.
     * @throws IOException If an error occurred while opening the given file or input resource is not supported.
     *
     * @since 3.07
     */
    public static OutputStream openWrite(Object resource) throws IOException {
        return openWrite(resource, CREATE, WRITE);
    }

    /**
     * Opens an output stream from the given {@link String}, {@link File}, {@link URL},
     * {@link URI}, {@link Path} or {@link OutputStream}.
     *
     * @param  resource The resource to open,
     * @param options open options
     * @return The output stream for the given file.
     * @throws IOException If an error occurred while opening the given file or input resource is not supported.
     */
    public static OutputStream openWrite(Object resource, OpenOption... options) throws IOException {
        ArgumentChecks.ensureNonNull("resource", resource);

        if (resource instanceof OutputStream) {
            return (OutputStream) resource;
        }

        if (!canProcessAsPath(resource)) {
            throw new IOException("Can not handle input type : " + resource.getClass());
        }

        try {
            Path realPath = toPath(resource);
            return newOutputStream(realPath, options);
        } catch (IOException e) {
            /*
                Try with URL.
                Typical case when resource input is an URL not supported as FileSystem
                Path conversion will fail
             */
            URL url = null;
            if (resource instanceof URL) {
                url = (URL) resource;
            }

            if (resource instanceof URI) {
                URI uri = (URI) resource;
                try {
                    url = uri.toURL();
                } catch (MalformedURLException e2) {
                    e.addSuppressed(e2);
                }
            }

            if (url != null) {
                URLConnection connection = url.openConnection();
                connection.setDoOutput(true);
                return connection.getOutputStream();
            }
            throw e;
        }
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
            throw new EOFException(Errors.format(Errors.Keys.EndOfDataFile));
        }
        final StringTokenizer tokens = new StringTokenizer(line);
        for (int i=0; i<numCol; i++) {
            if (!tokens.hasMoreTokens()) {
                throw new ContentFormatException(Errors.format(
                        Errors.Keys.LineTooShort_2, i, numCol));
            }
            final String token = tokens.nextToken();
            final double value;
            try {
                value = Double.parseDouble(token);
            } catch (NumberFormatException e) {
                throw new ContentFormatException(concatenate(
                        Errors.format(Errors.Keys.UnparsableNumber_1, token), e), e);
            }
            grid[offset + i] = value;
        }
        if (tokens.hasMoreElements()) {
            throw new ContentFormatException(Errors.format(Errors.Keys.LineTooLong_3,
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
            throw new EOFException(Errors.format(Errors.Keys.EndOfDataFile));
        }
        final StringTokenizer tokens = new StringTokenizer(line);
        for (int i=0; i<numCol; i++) {
            if (!tokens.hasMoreTokens()) {
                throw new ContentFormatException(Errors.format(
                        Errors.Keys.LineTooShort_2, i, numCol));
            }
            final String token = tokens.nextToken();
            final float value;
            try {
                value = Float.parseFloat(token);
            } catch (NumberFormatException e) {
                throw new ContentFormatException(concatenate(
                        Errors.format(Errors.Keys.UnparsableNumber_1, token), e), e);
            }
            grid[offset + i] = value;
        }
        if (tokens.hasMoreElements()) {
            throw new ContentFormatException(Errors.format(Errors.Keys.LineTooLong_3,
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


    /**
     * Copy content behind source Path into target Path.
     * If source Path is a directory, all his content will be copied recursively.
     *
     * @param sourcePath source to copy, can be a file or a folder
     * @param targetPath target where source will be copied
     * @param copyOption optional copy option
     * @throws IOException
     */
    public static void copy(Path sourcePath, Path targetPath, CopyOption... copyOption) throws IOException {
        ArgumentChecks.ensureNonNull("sourcePath", sourcePath);
        ArgumentChecks.ensureNonNull("targetPath", targetPath);

        if (isDirectory(sourcePath)) {
            Files.walkFileTree(sourcePath, new CopyFileVisitor(targetPath, copyOption));
        } else {
            Files.copy(sourcePath, targetPath, copyOption);
        }
    }

    /**
     * This method delete recursively a file or a folder.
     *
     * @param root The File or directory to delete.
     */
    public static void deleteRecursively(final Path root) throws IOException {
        ArgumentChecks.ensureNonNull("root", root);

        if (Files.exists(root)) {
            if (Files.isRegularFile(root)) {
                Files.deleteIfExists(root);
            } else {
                Files.walkFileTree(root, new SimpleFileVisitor<Path>() {
                    @Override
                    public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                        Files.deleteIfExists(file);
                        return FileVisitResult.CONTINUE;
                    }

                    @Override
                    public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                        Files.deleteIfExists(dir);
                        return FileVisitResult.CONTINUE;
                    }
                });
            }
        }
    }

    /**
     * This method delete recursively a file or directory behind a Path without raising an exception if delete failed.
     * If an exception occurs, message will be logged as debug.
     *
     * @param path file or folder to delete
     * @return delete status. {@code true} if delete succeed, {@code false} otherwise
     */
    public static boolean deleteSilently(Path path) {
        try {
            deleteRecursively(path);
            return true;
        } catch (IOException e) {
            LOGGER.log(Level.FINER, e.getLocalizedMessage(), e);
            return false;
        }
    }

    /**
     * Append the specified text at the end of the File.
     *
     * @param text The text to append to the file.
     * @param filePath The url file.
     *
     * @throws IOException if the file does not exist or cannot be read.
     */
    public static void appendToFile(final String text, final Path filePath) throws IOException {
        try (BufferedWriter output = Files.newBufferedWriter(filePath, UTF8_CHARSET, APPEND)) {
            output.newLine();
            output.write(text);
            output.flush();
        }
    }

    /**
     * Empty a file.
     *
     * @param filePath The url file.
     *
     * @throws IOException if the file does not exist or cannot be read.
     */
    public static void emptyFile(final Path filePath) throws IOException {
        if (Files.exists(filePath)) {
            Files.delete(filePath);
        }
        Files.createFile(filePath);
    }

    /**
     * Read the contents of a file into String using UTF-8 encoding.
     *
     * @param filePath the file path
     * @return The file contents as string
     * @throws IOException if the file does not exist or cannot be read.
     */
    public static String toString(final Path filePath) throws IOException {
        return toString(filePath, UTF8_CHARSET);
    }

    /**
     * Read the contents of a file into String using specified encoding.
     *
     * @param filePath the file path
     * @param encoding encoding of file
     * @return The file contents as string
     * @throws IOException if the file does not exist or cannot be read.
     */
    public static String toString(final Path filePath, final Charset encoding) throws IOException {
        final List<String> lines = Files.readAllLines(filePath, encoding);

        final StringBuilder sb = new StringBuilder();
        for (String line : lines) {
            sb.append(line).append('\n');
        }
        return sb.toString();
    }

    /**
     * Read the contents of a stream into String using UTF-8 encoding and close the Stream.
     *
     * @param stream input steam
     * @return The file contents as string
     * @throws IOException if the file does not exist or cannot be read.
     */
    public static String toString(final InputStream stream) throws IOException {
        return toString(stream, UTF8_CHARSET);
    }

    /**
     * Read the contents of a stream into String with specified encoding and close the Stream.
     *
     * @param stream input steam
     * @return The file contents as string
     * @throws IOException if the file does not exist or cannot be read.
     */
    public static String toString(final InputStream stream, final Charset encoding) throws IOException {

        final StringBuilder sb  = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new InputStreamReader(stream, encoding))) {
            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line).append('\n');
            }
        } finally {
            stream.close();
        }
        return sb.toString();
    }

    /**
     * Write the contents of a string into path using UTF-8 encoding.
     *
     * @param content string to write
     * @param outputPath the Path to write into.
     * @throws IOException if the file does not exist or cannot be read.
     */
    public static void writeString(final String content, final Path outputPath) throws IOException {
       writeString(content, outputPath, UTF8_CHARSET);
    }

    /**
     * Write the contents of a string into path.
     *
     * @param content string to write
     * @param outputPath the Path to write into.
     * @param encoding encoding of output file.
     * @throws IOException if the file does not exist or cannot be read.
     */
    public static void writeString(final String content, final Path outputPath, final Charset encoding) throws IOException {
        ArgumentChecks.ensureNonNull("content", content);
        ArgumentChecks.ensureNonNull("outputPath", outputPath);
        try (BufferedWriter bw = Files.newBufferedWriter(outputPath, encoding, CREATE, WRITE, TRUNCATE_EXISTING)) {
            bw.write(content);
        }
    }

    /**
     * Write the content of an {@link InputStream} into a Path
     * .
     * @param stream InputStream to write
     * @param outputPath the Path to write into.
     * @throws IOException
     */
    public static void writeStream (final InputStream stream, final Path outputPath) throws IOException {
        ArgumentChecks.ensureNonNull("stream", stream);
        ArgumentChecks.ensureNonNull("outputPath", outputPath);
        try (OutputStream outputStream = Files.newOutputStream(outputPath, CREATE, WRITE, TRUNCATE_EXISTING)) {
            copy(stream, outputStream);
        }
    }

    /**
     * Load the properties from a properties file.
     * If the file does not exist it will be created and an empty Properties object will be return.
     *
     * @param path to a properties file.
     * @return a Properties Object.
     */
    public static Properties getPropertiesFromFile(final Path path) throws IOException {
        ArgumentChecks.ensureNonNull("path", path);
        final Properties prop = new Properties();
        try (InputStream in = Files.newInputStream(path)) {
            prop.load(in);
        }
        return prop;
    }

    /**
     * Store an Properties object "prop" into the specified File.
     * If file behind input path already exist, content will be truncated first.
     *
     * @param prop A properties Object.
     * @param path    A file.
     * @param comment    Comment in output property file. Can be {@code null}.
     * @throws IOException
     */
    public static void storeProperties(final Properties prop, final Path path, String comment) throws IOException {
        ArgumentChecks.ensureNonNull("prop", prop);
        ArgumentChecks.ensureNonNull("path", path);

        //replace content
        try (OutputStream out = Files.newOutputStream(path, CREATE, WRITE, TRUNCATE_EXISTING)) {
            prop.store(out, (comment != null ? comment : ""));
        }
    }

    /**
     * Searches in the Context ClassLoader for the named file and returns it.
     *
     * @param resource resource path. As package or path format (with '.' or '/' separators)
     * @return A file path if it exist or null otherwise.
     */
    public static Path getResourceAsPath(String resource) throws URISyntaxException {

        //change package based location to path based
        final String extension = org.apache.sis.internal.storage.IOUtilities.extension(resource);
        int lastDotIdx = resource.lastIndexOf('.');
        if (lastDotIdx > 0) {
            resource = resource.substring(0, lastDotIdx);
        }

        resource = resource.replace('.', '/') ;

        if (!extension.isEmpty()) {
            resource +="." + extension;
        }

        final URL systemResource = ClassLoader.getSystemResource(resource);
        if (systemResource != null) {
            return  Paths.get(systemResource.toURI());
        }
        return null;
    }

//    /**
//     * Find if a resource is "local" (meaning NIO API compatible)
//     * or distant (accessed with URL API).
//     *
//     * @param uri
//     * @return true if NIO compatible, false otherwise
//     */
//    public static boolean isNIO(URI uri) {
//        try {
//            if (uri.getScheme() == null) {
//                //scheme null, consider as Path on default FileSystem
//                Paths.get(uri.toString());
//            } else {
//                Paths.get(uri);
//            }
//            return true;
//        } catch (FileSystemNotFoundException fse) {
//            // No FileSystemProvider defined for uri scheme
//            return false;
//        }
//    }
//
//    /**
//     * Find if a resource is "local" (meaning NIO API compatible)
//     * or distant (accessed with URL API).
//     *
//     * @param url
//     * @return true if NIO compatible, false otherwise
//     */
//    public static boolean isNIO(URL url) {
//        try {
//            if (url.toExternalForm().startsWith("file:")) {
//                //scheme null, consider as Path on default FileSystem
//                Paths.get(url.toString());
//            } else {
//                Paths.get(url);
//            }
//            return true;
//        } catch (FileSystemNotFoundException fse) {
//            // No FileSystemProvider defined for url scheme
//            return false;
//        }
//    }

}
