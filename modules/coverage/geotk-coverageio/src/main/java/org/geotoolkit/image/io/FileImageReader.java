/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2006-2012, Open Source Geospatial Foundation (OSGeo)
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
package org.geotoolkit.image.io;

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
import org.geotoolkit.internal.io.IOUtilities;
import org.geotoolkit.internal.io.TemporaryFile;


/**
 * Base class for image readers that require {@link File} input source. This class is used with
 * image formats backed by some external API (typically C/C++ libraries) working only with files.
 * <p>
 * The input type can be any of the types documented in the
 * {@linkplain org.geotoolkit.image.io.StreamImageReader.Spi provider} javadoc. The {@link File}
 * object can be obtained by a call to {@link #getInputFile()}, which handles the various input
 * types as below:
 * <p>
 * <ul>
 *   <li>{@link File} inputs are returned as-is.</li>
 *   <li>{@link String} inputs are converted to {@code File} objects by a call to the
 *       {@link File#File(String)} constructor.</li>
 *   <li>{@link URL} and {@link URI} inputs are converted to {@code File} objects by a call to the
 *       {@link File#File(URI)} constructor only if the protocol is {@code "file"}. In the particular
 *       case of {@code URL}s, the encoding is specified by {@link #getURLEncoding()}.</li>
 *   <li>For all other cases, the input content is copied to a temporary file and the corresponding
 *       {@code File} object is returned. The temporary file is deleted by the {@link #close()}
 *       method.</li>
 * </ul>
 *
 * @author Antoine Hnawia (IRD)
 * @author Martin Desruisseaux (IRD, Geomatys)
 * @version 3.20
 *
 * @since 2.4
 * @module
 */
public abstract class FileImageReader extends StreamImageReader {
    /**
     * The file to read. This is the same reference than {@link #input} if the later was
     * already a {@link File} object. Otherwise this is a {@code File} derived from the
     * input URL, URI or Path if possible, or a temporary file otherwise.
     */
    private File inputFile;

    /**
     * {@code true} if {@link #inputFile} is a temporary file.
     */
    private boolean isTemporary;

    /**
     * Constructs a new image reader.
     *
     * @param provider The {@link ImageReaderSpi} that is constructing this object, or {@code null}.
     */
    protected FileImageReader(final Spi provider) {
        super(provider);
    }

    /**
     * Returns the encoding used for {@linkplain URL} {@linkplain #input input}s.
     * The default implementation returns {@code "UTF-8"} in all cases. Subclasses
     * can override this method if {@link #getInputFile()} should convert {@link URL}
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
                inputFile = new File(sourceURI);
                ensureFileExists(inputFile);
                return inputFile;
            }
        }
        if (input instanceof URL) {
            final URL sourceURL = (URL) input;
            if (sourceURL.getProtocol().equalsIgnoreCase("file")) {
                inputFile = IOUtilities.toFile(sourceURL, getURLEncoding());
                ensureFileExists(inputFile);
                return inputFile;
            }
        }
        /*
         * Can not convert the input directly to a file. Asks the input stream
         * before to create the temporary file in case an exception is thrown.
         * Then creates a temporary file using the first declared image suffix
         * (e.g. "png"), or "tmp" if there is no declared suffix. The "FIR"
         * prefix stands for "FileImageReader".
         */
        final InputStream in = getInputStream();
        inputFile = TemporaryFile.createTempFile("FIR", XImageIO.getFileSuffix(originatingProvider), null);
        isTemporary = true;
        final OutputStream out = new FileOutputStream(inputFile);
        try {
            IOUtilities.copy(in, out);
        } finally {
            out.close();
        }
        /*
         * Do not close the input stream, because it may be a stream explicitly specified by the user.
         * The stream will be closed by the 'setInput', 'reset', 'close' or 'dispose' methods.
         */
        return inputFile;
    }

    /**
     * Returns {@code true} if the file given by {@link #getInputFile()} is a temporary file.
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
     * @throws IOException If an error occurred while fetching the information.
     */
    @Override
    public boolean isRandomAccessEasy(final int imageIndex) throws IOException {
        return true;
    }

    /**
     * Closes the stream {@linkplain StreamImageReader#close() as documented in the super-class},
     * then deletes the temporary file (if any). This method is invoked automatically by
     * {@link #setInput(Object, boolean, boolean) setInput(...)}, {@link #reset() reset()},
     * {@link #dispose() dispose()} or {@link #finalize()} methods and doesn't need to be
     * invoked explicitly.
     *
     * @throws IOException If an error occurred while disposing resources.
     */
    @Override
    protected void close() throws IOException {
        try {
            super.close(); // Must close the stream before to delete the file.
        } finally {
            final File file = inputFile;
            if (file != null) {
                inputFile = null;
                if (isTemporary) {
                    if (!TemporaryFile.delete(file)) {
                        file.deleteOnExit();
                    }
                }
            }
            isTemporary = false;
        }
    }
}
