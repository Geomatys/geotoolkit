/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2006-2009, Open Source Geospatial Foundation (OSGeo)
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
package org.geotoolkit.image.io;

import java.net.URLDecoder;
import java.net.URL;
import java.net.URI;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.FileOutputStream;
import java.io.FileNotFoundException;
import javax.imageio.spi.ImageReaderSpi;

import org.geotoolkit.resources.Errors;
import org.geotoolkit.internal.io.TemporaryFile;


/**
 * Base class for image readers that require {@link File} input source. If the input source
 * is of other kind, then the content will be copied to a temporary file. This class is used
 * for image formats backed by some external API (typically C/C++ libraries) working only with
 * files.
 *
 * @author Antoine Hnawia (IRD)
 * @author Martin Desruisseaux (IRD)
 * @version 3.03
 *
 * @since 2.4
 * @module
 *
 * @deprecated Old implementation, temporarily keeped around until {@code NetcdfImageReader} has
 *      been ported.
 */
@Deprecated
public abstract class GeoFileImageReader extends GeoStreamImageReader {
    /**
     * The file to read. This is the same reference than {@link #input} if the later was
     * already a {@link File} object, or a temporary file otherwise.
     */
    private File inputFile;

    /**
     * {@code true} if {@link #inputFile} is a temporary file.
     */
    private boolean isTemporary;

    /**
     * Constructs a new image reader.
     *
     * @param provider The {@link ImageReaderSpi} that is invoking this constructor,
     *        or {@code null} if none.
     */
    public GeoFileImageReader(final ImageReaderSpi provider) {
        super(provider);
    }

    /**
     * Returns the encoding used for {@linkplain URL} {@linkplain #input input}s.
     * The default implementation returns {@code "UTF-8"} in all cases. Subclasses
     * should override this method if {@link #getInputFile} should converts {@link URL}
     * to {@link File} objects using a different encoding.
     *
     * @return The encoding used for URL inputs.
     */
    public String getURLEncoding() {
        return "UTF-8";
    }

    /**
     * Ensures that the specified file can be read.
     *
     * @throws FileNotFoundException if the file is not found or can not be read.
     */
    private void ensureFileExists(final File file) throws FileNotFoundException {
        if (!file.isFile() || !file.canRead()) {
            throw new FileNotFoundException(getErrorResources().getString(
                    Errors.Keys.FILE_DOES_NOT_EXIST_$1, file));
        }
    }

    /**
     * Returns the {@linkplain #input input} as a file. If the input is not a file,
     * then its content is copied to a temporary file and the temporary file is returned.
     *
     * @return The {@linkplain #input input} as a file.
     * @throws FileNotFoundException if the file is not found or can not be read.
     * @throws IOException if a copy was necessary but failed.
     */
    protected File getInputFile() throws FileNotFoundException, IOException {
        if (inputFile != null) {
            ensureFileExists(inputFile);
            return inputFile;
        }
        if (input instanceof String) {
            inputFile = new File((String) input);
            ensureFileExists(inputFile);
            return inputFile;
        }
        if (input instanceof File) {
            inputFile = (File) input;
            ensureFileExists(inputFile);
            return inputFile;
        }
        if (input instanceof URI) {
            final URI sourceURI = (URI) input;
            if (sourceURI.getScheme().equalsIgnoreCase("file")) {
                inputFile = new File(sourceURI.getPath());
                ensureFileExists(inputFile);
                return inputFile;
            }
        }
        if (input instanceof URL) {
            final URL sourceURL = (URL) input;
            if (sourceURL.getProtocol().equalsIgnoreCase("file")) {
                inputFile = new File(URLDecoder.decode(sourceURL.getPath(), getURLEncoding()));
                ensureFileExists(inputFile);
                return inputFile;
            }
        }
        /*
         * Can not convert the input directly to a file. Asks the input stream
         * before to create the temporary file in case an exception is thrown.
         */
        final InputStream in = getInputStream();
        /*
         * Creates a temporary file using the first declared image suffix
         * (e.g. "png"), or "tmp" if there is no suffix declared.
         */
        String suffix = "tmp";
        if (originatingProvider != null) {
            final String[] suffixes = originatingProvider.getFileSuffixes();
            if (suffixes != null && suffixes.length != 0) {
                // We assume that the first file suffix is the
                // most representative of this file format.
                suffix = suffixes[0];
            }
        }
        inputFile = TemporaryFile.createTempFile("Image", suffix, null);
        isTemporary = true;
        /*
         * Copy the content of the specified input stream to the temporary file.
         * Note that there is no need to use instance of BufferedInputStream or
         * BufferedOutputStream since we already use a 8 kb buffer.
         */
        final OutputStream out = new FileOutputStream(inputFile);
        final byte[] buffer = new byte[8192];
        int length;
        while ((length=in.read(buffer)) >= 0) {
            out.write(buffer, 0, length);
        }
        in.close();
        out.close();
        return inputFile;
    }

    /**
     * Returns {@code true} if the file given by {@link #getInputFile} is a temporary file.
     *
     * @return {@code true} if the input file is a temporary one.
     */
    protected boolean isTemporaryFile() {
        return isTemporary;
    }

    /**
     * Returns {@code true} since image readers backed by {@link File}
     * object usually supports random access efficiently.
     *
     * @throws IOException If an error occured while fetching the information.
     */
    @Override
    public boolean isRandomAccessEasy(final int imageIndex) throws IOException {
        return true;
    }

    /**
     * Deletes the temporary file, if any.
     *
     * @throws IOException If an error occured while disposing resources.
     */
    @Override
    protected void close() throws IOException {
        if (inputFile != null) {
            if (isTemporary) {
                TemporaryFile.delete(inputFile);
            }
            inputFile = null;
        }
        isTemporary = false;
        super.close();
    }
}
