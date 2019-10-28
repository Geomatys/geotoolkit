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
package org.geotoolkit.coverage.io;

import java.util.concurrent.CancellationException;
import javax.imageio.ImageWriter;
import org.apache.sis.coverage.grid.GridCoverage;
import org.apache.sis.storage.DataStoreException;


/**
 * Base class of {@link GridCoverage} writers. Writing is a two steps process:
 * <p>
 * <ul>
 *   <li>The output must be set first using the {@link #setOutput(Object)} method.</li>
 *   <li>The actual writing is performed by a call to the
 *       {@link #write(GridCoverage, GridCoverageWriteParam)} method.</li>
 * </ul>
 * <p>
 * Example:
 *
 * {@preformat java
 *     GridCoverage coverage = ...
 *     GridCoverageWriter writer = ...
 *     writer.setOutput(new File("MyCoverage.asc"));
 *     writer.write(coverage, null);
 * }
 *
 * {@note This class is conceptually equivalent to the <code>ImageWriter</code> class provided
 * in the standard Java library. Implementations of this class are often wrappers around a Java
 * <code>ImageWriter</code>, converting geodetic coordinates to pixel coordinates before to
 * delegate the writing of pixel values.}
 *
 * @author Martin Desruisseaux (Geomatys)
 * @author Johann Sorel (Geomatys)
 * @version 3.20
 *
 * @see ImageWriter
 *
 * @since 3.14
 * @module
 */
@Deprecated
public interface GridCoverageWriter {

    /**
     * Sets the output destination to the given object. The output is typically a
     * {@link java.io.File} or a {@link String} object. But some other types
     * (e.g. {@link javax.imageio.stream.ImageOutputStream}) may be accepted
     * as well depending on the implementation.
     *
     * {@section How streams are closed}
     * <ul>
     *   <li>If the given output is an {@linkplain java.io.OutputStream output stream},
     *      {@linkplain javax.imageio.stream.ImageOutputStream image output stream} or
     *      a {@linkplain java.io.Writer writer}, then it is caller responsibility to
     *      close the given stream after usage.</li>
     *  <li>If an output stream has been generated automatically by this {@code GridCoverageWriter}
     *      from the given output object, then this coverage writer will close the stream when the
     *      {@link #reset()} or {@link #dispose()} method is invoked, or when a new output is set.</li>
     * </ul>
     *
     * @param  output The output (typically {@link java.io.File} or {@link String}) to be written.
     * @throws IllegalArgumentException If the output is not a valid instance for this writer.
     * @throws CoverageStoreException If the operation failed.
     *
     * @see ImageWriter#setOutput(Object)
     */
    void setOutput(Object output) throws CoverageStoreException;

    /**
     * Returns the output which was set by the last call to {@link #setOutput(Object)},
     * or {@code null} if none.
     *
     * @return The current output, or {@code null} if none.
     * @throws CoverageStoreException If the operation failed.
     *
     * @see ImageWriter#getOutput()
     */
    Object getOutput() throws CoverageStoreException;

    /**
     * Writes a single grid coverage.
     *
     * @param  coverage The coverage to write.
     * @param  param Optional parameters used to control the writing process, or {@code null}.
     * @throws IllegalStateException If the output destination has not been set.
     * @throws CoverageStoreException If an error occurs while writing the information to the output destination.
     * @throws CancellationException If {@link #abort()} has been invoked in an other thread during
     *         the execution of this method.
     */
    void write(GridCoverage coverage, GridCoverageWriteParam param)
            throws CoverageStoreException, CancellationException;

    /**
     * Writes one or many grid coverages. The default implementation delegates to
     * {@link #write(GridCoverage, GridCoverageWriteParam)} if the given iterable
     * contains exactly one coverage, or throws an {@link CoverageStoreException} otherwise.
     *
     * @param  coverages The coverages to write.
     * @param  param Optional parameters used to control the writing process, or {@code null}.
     * @throws IllegalStateException If the output destination has not been set.
     * @throws CoverageStoreException If the iterable contains an unsupported number of coverages,
     *         or if an error occurs while writing the information to the output destination.
     * @throws CancellationException If {@link #abort()} has been invoked in an other thread during
     *         the execution of this method.
     *
     * @since 3.20
     */
    void write(final Iterable<? extends GridCoverage> coverages, final GridCoverageWriteParam param)
            throws CoverageStoreException, CancellationException;

    /**
     * Restores the {@code GridCoverageWriter} to its initial state.
     *
     * @throws CoverageStoreException If an error occurs while restoring to the initial state.
     *
     * @see ImageWriter#reset()
     */
    void reset() throws DataStoreException;

    /**
     * Allows any resources held by this writer to be released. The result of calling
     * any other method subsequent to a call to this method is undefined.
     *
     * @throws CoverageStoreException If an error occurs while disposing resources.
     *
     * @see ImageWriter#dispose()
     */
    void dispose() throws DataStoreException;

}
