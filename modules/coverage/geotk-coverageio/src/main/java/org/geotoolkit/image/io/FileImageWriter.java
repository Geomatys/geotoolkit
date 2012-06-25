/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2011-2012, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2011-2012, Geomatys
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
import java.io.FileInputStream;
import javax.imageio.spi.ImageWriterSpi;

import org.geotoolkit.internal.io.IOUtilities;
import org.geotoolkit.internal.io.TemporaryFile;
import org.geotoolkit.internal.image.io.Formats;
import org.geotoolkit.resources.Errors;


/**
 * Base class for image writers that require {@link File} output destination. This class is used with
 * image formats backed by some external API (typically C/C++ libraries) working only with files.
 * <p>
 * The output type can be any of the types documented in the
 * {@linkplain org.geotoolkit.image.io.StreamImageWriter.Spi provider} javadoc. The {@link File}
 * object can be obtained by a call to {@link #getOutputFile()}, which handles the various output
 * types as below:
 * <p>
 * <ul>
 *   <li>{@link File} outputs are returned as-is.</li>
 *   <li>{@link String} outputs are converted to {@code File} objects by a call to the
 *       {@link File#File(String)} constructor.</li>
 *   <li>{@link URL} and {@link URI} inputs are converted to {@code File} objects by a call to the
 *       {@link File#File(URI)} constructor only if the protocol is {@code "file"}. In the particular
 *       case of {@code URL}s, the encoding is specified by {@link #getURLEncoding()}.</li>
 *   <li>For all other cases, a temporary file is returned. When the {@link #close()} method is
 *       invoked, the content of the temporary file is copied to the output stream and the file
 *       is deleted.</li>
 * </ul>
 *
 * @author Johann Sorel (Geomatys)
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.20
 *
 * @since 3.20
 * @module
 */
public abstract class FileImageWriter extends StreamImageWriter {
    /**
     * The file to write. This is the same reference than {@link #output} if the later was
     * already a {@link File} object. Otherwise this is a {@code File} derived from the
     * output URL, URI or Path if possible, or a temporary file otherwise.
     */
    private File outputFile;

    /**
     * {@code true} if {@link #outputFile} is a temporary file.
     */
    private boolean isTemporary;

    /**
     * Constructs a new image writer.
     *
     * @param provider The {@link ImageWriterSpi} that is constructing this object, or {@code null}.
     */
    protected FileImageWriter(final Spi provider) {
        super(provider);
    }

    /**
     * Returns the encoding used for {@linkplain URL} {@linkplain #output output}s.
     * The default implementation returns {@code "UTF-8"} in all cases. Subclasses
     * can override this method if {@link #getOutputFile()} should convert {@link URL}
     * to {@link File} objects using a different encoding.
     *
     * @return The encoding used for URL outputs.
     */
    public String getURLEncoding() {
        return "UTF-8";
    }

    /**
     * Returns the {@linkplain #output output} as a file. If the output is not a file,
     * then a temporary file is returned. The content of the file will be send to the
     * original output when {@linkplain #close() closing} this image writer.
     *
     * @return The {@linkplain #output output} as a file.
     * @throws IOException If the output can not be converted to a file, or a failure
     *         occurred while creating a temporary file.
     */
    protected File getOutputFile() throws IOException {
        if (outputFile != null) {
            return outputFile;
        }
        final Object output = this.output;
        if (output == null) {
            throw new IllegalStateException(getErrorResources().getString(Errors.Keys.NO_IMAGE_OUTPUT));
        }
        if (output instanceof String) {
            outputFile = new File((String) output);
            return outputFile;
        }
        if (output instanceof File) {
            outputFile = (File) output;
            return outputFile;
        }
        if (output instanceof URI) {
            final URI targetURI = (URI) output;
            if (targetURI.getScheme().equalsIgnoreCase("file")) {
                outputFile = new File(targetURI);
                return outputFile;
            }
        }
        if (output instanceof URL) {
            final URL targetURL = (URL) output;
            if (targetURL.getProtocol().equalsIgnoreCase("file")) {
                outputFile = IOUtilities.toFile(targetURL, getURLEncoding());
                return outputFile;
            }
        }
        /*
         * Can not convert the output directly to a file. Creates a temporary file using
         * the first declared image suffix (e.g. "png"), or "tmp" if there is no declared
         * suffix. The "FIW" prefix stands for "FileImageWriter".
         */
        outputFile = TemporaryFile.createTempFile("FIW", Formats.getFileSuffix(originatingProvider), null);
        isTemporary = true;
        return outputFile;
    }

    /**
     * Returns {@code true} if the file given by {@link #getOutputFile()} is a temporary file.
     *
     * @return {@code true} if the output file is a temporary one.
     */
    protected boolean isTemporaryFile() {
        return isTemporary;
    }

    /**
     * Flushes the image content to the output stream, closes the stream and deletes the
     * temporary file (if any). More specifically, this method performs the following steps:
     * <p>
     * <ol>
     *   <li>If the content was written to a temporary file, copy that content to the original
     *       {@linkplain #getOutputStream() output stream}.</li>
     *   <li>Deletes the temporary file (if any).</li>
     *   <li>Closes the output stream {@linkplain StreamImageWriter#close() as documented in
     *       the super-class}</li>
     * </ol>
     * <p>
     * This method is invoked automatically by {@link #setOutput(Object)}, {@link #reset()},
     * {@link #dispose()} or {@link #finalize()} methods and doesn't need to be invoked explicitly.
     * It has protected access only in order to allow overriding by subclasses.
     *
     * @throws IOException If an error occurred while disposing resources.
     */
    @Override
    protected void close() throws IOException {
        final File file = outputFile;
        outputFile = null;
        if (isTemporary) try {
            isTemporary = false;
            final OutputStream out = getOutputStream();
            final InputStream in = new FileInputStream(file);
            try {
                IOUtilities.copy(in, out);
            } finally {
                in.close();
            }
            out.flush();
            // Do not close the 'out' stream. Let the super.close() method decides what
            // it needs to close (it depends if the stream was specified by the user or
            // created under the hood by the writer).
        } finally {
            // Delete the temporary file before to close the stream in order to make sure that
            // it is deleted even if super.close() failed. In extreme cases, this may also free
            // some disk space needed by super.close() for completing its work.
            if (!TemporaryFile.delete(file)) {
                file.deleteOnExit();
            }
            super.close();
        } else {
            super.close();
        }
    }
}
