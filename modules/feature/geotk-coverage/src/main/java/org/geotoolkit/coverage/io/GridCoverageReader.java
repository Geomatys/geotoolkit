/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2009-2012, Open Source Geospatial Foundation (OSGeo)
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
package org.geotoolkit.coverage.io;

import java.util.List;
import java.util.concurrent.CancellationException;
import javax.imageio.ImageReader;
import org.apache.sis.coverage.SampleDimension;
import org.apache.sis.coverage.grid.GridGeometry;
import org.apache.sis.storage.DataStoreException;
import org.apache.sis.util.collection.BackingStoreException;
import org.geotoolkit.coverage.grid.GridCoverage;
import org.opengis.metadata.Metadata;
import org.opengis.util.GenericName;


/**
 * Base class of {@link GridCoverage} readers. Reading is a two steps process:
 * <p>
 * <ul>
 *   <li>The input must be set first using the {@link #setInput(Object)} method.</li>
 *   <li>The actual reading is performed by a call to the
 *       {@link #read(int, GridCoverageReadParam)} method.</li>
 * </ul>
 * <p>
 * Example:
 *
 * {@preformat java
 *     GridCoverageReader reader = ...
 *     reader.setInput(new File("MyCoverage.asc"));
 *     GridCoverage coverage = reader.read(0, null);
 * }
 *
 * {@note This class is conceptually equivalent to the <code>ImageReader</code> class provided in
 * the standard Java library. Implementations of this class are often wrappers around a Java
 * <code>ImageReader</code>, converting geodetic coordinates to pixel coordinates before to
 * delegate the reading of pixel values.}
 *
 * @author Martin Desruisseaux (IRD, Geomatys)
 * @author Johann Sorel (Geomatys)
 */
public interface GridCoverageReader {

    /**
     * Sets the input source to the given object. The input is typically a
     * {@link java.io.File} or a {@link String} object. But some other types
     * (e.g. {@link javax.imageio.stream.ImageInputStream}) may be accepted
     * as well depending on the implementation.
     *
     * {@section How streams are closed}
     * <ul>
     *   <li>If the given input is an {@linkplain java.io.InputStream input stream},
     *      {@linkplain javax.imageio.stream.ImageInputStream image input stream} or
     *      a {@linkplain java.io.Reader reader}, then it is caller responsibility to
     *      close the given stream after usage.</li>
     *  <li>If an input stream has been generated automatically by this {@code GridCoverageReader}
     *      from the given input object, then this coverage reader will close the stream when the
     *      {@link #reset()} or {@link #dispose()} method is invoked, or when a new input is set.</li>
     * </ul>
     *
     * @param  input The input (typically {@link java.io.File} or {@link String}) to be read.
     * @throws IllegalArgumentException If the input is not a valid instance for this reader.
     * @throws CoverageStoreException If the operation failed.
     *
     * @see ImageReader#setInput(Object)
     */
    void setInput(final Object input) throws DataStoreException;

    /**
     * Returns the input which was set by the last call to {@link #setInput(Object)},
     * or {@code null} if none.
     *
     * @return The current input, or {@code null} if none.
     * @throws CoverageStoreException If the operation failed.
     *
     * @see ImageReader#getInput()
     */
    Object getInput() throws DataStoreException;

    /**
     * Returns the list of coverage names available from the current input source. The length
     * of the returned list is the number of coverages found in the current input source. The
     * elements in the returned list are the names of each coverage.
     * <p>
     * The returned list may be backed by this {@code GridCoverageReader}: it should be used
     * only as long as this reader and its input source are valid. Iterating over the list
     * may be costly and the operation performed on the list may throw a
     * {@link BackingStoreException}.
     *
     * @return The names of the coverages.
     * @throws IllegalStateException If the input source has not been set.
     * @throws CoverageStoreException If an error occurs while reading the information from the input source.
     * @throws CancellationException If {@link #abort()} has been invoked in an other thread during
     *         the execution of this method.
     *
     * @see ImageReader#getNumImages(boolean)
     */
    GenericName getCoverageName() throws DataStoreException, CancellationException;

    /**
     * Returns the grid geometry for the {@link GridCoverage} to be read at the given index.
     *
     * @return The grid geometry for the {@link GridCoverage} at the specified index.
     * @throws IllegalStateException If the input source has not been set.
     * @throws IndexOutOfBoundsException If the supplied index is out of bounds.
     * @throws CoverageStoreException If an error occurs while reading the information from the input source.
     * @throws CancellationException If {@link #abort()} has been invoked in an other thread during
     *         the execution of this method.
     *
     * @see ImageReader#getWidth(int)
     * @see ImageReader#getHeight(int)
     */
    GridGeometry getGridGeometry()
            throws DataStoreException, CancellationException;

    /**
     * Returns the sample dimensions for each band of the {@link GridCoverage} to be read.
     * If sample dimensions are not known, then this method returns {@code null}.
     *
     * @return The list of sample dimensions for the {@link GridCoverage} at the specified index,
     *         or {@code null} if none. This list length is equals to the number of bands in the
     *         {@link GridCoverage}.
     * @throws IllegalStateException If the input source has not been set.
     * @throws IndexOutOfBoundsException If the supplied index is out of bounds.
     * @throws CoverageStoreException If an error occurs while reading the information from the input source.
     * @throws CancellationException If {@link #abort()} has been invoked in an other thread during
     *         the execution of this method.
     */
    List<SampleDimension> getSampleDimensions() throws DataStoreException, CancellationException;

    /**
     * Returns the ISO 19115 metadata object associated with the input source as a whole
     * and each coverages. The default implementation constructs the metadata from the
     * {@linkplain #getStreamMetadata() stream metadata} and the
     * {@linkplain #getCoverageMetadata(int) coverage metadata},
     * eventually completed by the {@link #getGridGeometry(int)}.
     * <p>
     * Since the relationship between Image I/O metadata and ISO 19115 is not always a
     * "<cite>one-to-one</cite>" relationship, this method works on a best effort basis.
     *
     * @return The ISO 19115 metadata (never {@code null}).
     * @throws CoverageStoreException If an error occurs while reading the information from the input source.
     *
     * @see <a href="../../image/io/metadata/SpatialMetadataFormat.html#default-formats">Metadata formats</a>
     *
     * @since 3.18
     */
    Metadata getMetadata() throws DataStoreException;

    /**
     * Cancels the read or write operation which is currently under progress in an other thread.
     * Invoking this method will cause a {@link CancellationException} to be thrown in the reading
     * or writing thread (not this thread), unless the operation had the time to complete.
     *
     * {@section Note for implementors}
     * Subclasses should set the {@link #abortRequested} field to {@code false} at the beginning
     * of each read or write operation, and poll the value regularly during the operation.
     *
     * @see #abortRequested
     * @see javax.imageio.ImageReader#abort()
     * @see javax.imageio.ImageWriter#abort()
     */
    void abort();

    /**
     * Reads the grid coverage.
     *
     * @param  param Optional parameters used to control the reading process, or {@code null}.
     * @return The {@link GridCoverage} at the specified index.
     * @throws IllegalStateException if the input source has not been set.
     * @throws IndexOutOfBoundsException if the supplied index is out of bounds.
     * @throws CoverageStoreException If an error occurs while reading the information from the input source.
     * @throws CancellationException If {@link #abort()} has been invoked in an other thread during
     *         the execution of this method.
     *
     * @see ImageReader#read(int)
     */
    GridCoverage read(GridCoverageReadParam param)
            throws DataStoreException, CancellationException;

    /**
     * Restores the {@code GridCoverageReader} to its initial state.
     *
     * @throws CoverageStoreException If an error occurs while restoring to the initial state.
     *
     * @see ImageReader#reset()
     */
    void reset() throws DataStoreException;

    /**
     * Allows any resources held by this reader to be released. The result of calling
     * any other method subsequent to a call to this method is undefined.
     *
     * @throws CoverageStoreException If an error occurs while disposing resources.
     *
     * @see ImageReader#dispose()
     */
    void dispose() throws DataStoreException;
}
