/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2010-2012, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2010-2012, Geomatys
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
package org.geotoolkit.gui.swing.coverage;

import java.io.IOException;
import javax.imageio.ImageReader;
import javax.imageio.IIOException;
import java.util.concurrent.atomic.AtomicReference;

import org.opengis.coverage.grid.GridCoverage;

import org.geotoolkit.coverage.grid.GridCoverage2D;
import org.geotoolkit.coverage.grid.ViewType;
import org.geotoolkit.coverage.io.GridCoverageReader;
import org.geotoolkit.coverage.io.GridCoverageReadParam;
import org.geotoolkit.coverage.io.ImageReaderAdapter;
import org.geotoolkit.coverage.io.CoverageStoreException;
import org.geotoolkit.coverage.sql.GridCoverageReference;
import org.geotoolkit.gui.swing.image.ImageFileProperties;


/**
 * A panel showing the properties of a coverage file. This widget is similar to
 * {@link ImageFileProperties}, except that it uses a {@link GridCoverageReader}
 * for loading the image instead than an {@link javax.imageio.ImageReader}.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.14
 *
 * @since 3.14
 * @module
 */
@SuppressWarnings("serial")
public class CoverageFileProperties extends ImageFileProperties {
    /**
     * The image reader used in the last call to {@link #setImageInput(Object)}, or
     * {@code null} if none. Note that this field is typically read and written from
     * a background thread.
     */
    private final AtomicReference<Adapter> imageReader = new AtomicReference<>();

    /**
     * The adapter used for wrapping {@link GridCoverageReader} as an {@link ImageReader}.
     *
     * @author Martin Desruisseaux (Geomatys)
     * @version 3.14
     *
     * @since 3.14
     * @module
     */
    private static final class Adapter extends ImageReaderAdapter {
        /**
         * Creates a new adapter for the given reader.
         */
        Adapter(final GridCoverageReader reader) {
            super(reader);
        }

        /**
         * Returns the wrapped reader.
         */
        GridCoverageReader getReader() {
            return reader;
        }

        /**
         * Returns an adapter for the given reader. This method returns {@code this}
         * if this adapter is still applicable, or a new adapter otherwise.
         */
        Adapter setReader(final GridCoverageReader reader) {
            return (this.reader == reader) ? this : new Adapter(reader);
        }

        /**
         * Reads the coverage at the given index and returns its rendered view. The use
         * of {@link ViewType#RENDERED} allows better rendering in the overview pane.
         */
        @Override
        protected GridCoverage read(final int index, final GridCoverageReadParam param) throws IOException {
            GridCoverage coverage = super.read(index, param);
            if (coverage instanceof GridCoverage2D) {
                coverage = ((GridCoverage2D) coverage).view(ViewType.RENDERED);
            }
            return coverage;
        }
    }

    /**
     * Creates a new instance of {@code ImageFileProperties} with no image. One of
     * {@link #setImage(File) setImage(...)} methods must be invoked in order to set
     * the properties source.
     */
    public CoverageFileProperties() {
    }

    /**
     * Sets the specified coverage reference as the source of metadata and thumbnails.
     * This method accepts the following kind of inputs:
     * <p>
     * <ul>
     *   <li>{@link GridCoverageReference}</li>
     *   <li>{@link GridCoverageReader} with its input set (see <cite>Resources management</cite> below).</li>
     *   <li>{@link ImageReader} with its input set (see <cite>Resources management</cite> below).</li>
     *   <li>Paths as {@link java.io.File}, {@link java.net.URL}, <i>etc.</i></li>
     * </ul>
     *
     * {@section Resources management}
     * If an {@code GridCoverageReader} or {@code ImageReader} is given to this method, and if the
     * reader uses an {@linkplain javax.imageio.stream.ImageInputStream Image Input Stream} or other
     * kind of {@linkplain java.io.Closeable closeable} input, then note that this method does
     * <strong>not</strong> close such input. It is caller responsibility to close the reader
     * input after this method call.
     */
    @Override
    public void setImageInput(Object input) throws IOException {
        if (input instanceof GridCoverageReference) {
            /*
             * Get the GridCoverageReader. The existing instance will be recycled if
             * possible, provided that it is not an instance provided by the user.
             */
            Adapter adapter = imageReader.getAndSet(null);
            try {
                GridCoverageReader reader = (adapter != null) ? adapter.getReader() : null;
                try {
                    reader = ((GridCoverageReference) input).getCoverageReader(reader);
                } catch (CoverageStoreException e) {
                    final Throwable cause = e.getCause();
                    if (cause instanceof IOException) {
                        throw (IOException) cause;
                    }
                    throw new IIOException(e.getLocalizedMessage(), e);
                }
                adapter = (adapter != null) ? adapter.setReader(reader) : new Adapter(reader);
                adapter.setInput(input);
                super.setImageInput(adapter);
                adapter.setInput(null); // Close the input stream.
            } finally {
                imageReader.compareAndSet(null, adapter);
            }
        } else {
            if (input instanceof GridCoverageReader) {
                input = new Adapter((GridCoverageReader) input);
            }
            super.setImageInput(input);
        }
    }
}
