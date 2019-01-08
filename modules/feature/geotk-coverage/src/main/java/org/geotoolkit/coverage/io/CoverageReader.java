/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2014, Geomatys
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
import org.geotoolkit.coverage.Coverage;
import org.apache.sis.coverage.SampleDimension;
import org.opengis.util.GenericName;

/**
 * Generalized version of the GridCoverageReader for possible none grid and
 * multi-dimensional coverages.
 *
 * @author Johann Sorel (Geomatys)
 */
public interface CoverageReader {

    /**
     * Returns the list of coverage names available from the current input source.
     *
     * @return The name of the coverage.
     * @throws IllegalStateException If the input source has not been set.
     * @throws CoverageStoreException If an error occurs while reading the information from the input source.
     * @throws CancellationException If {@link #abort()} has been invoked in an other thread during
     *         the execution of this method.
     */
    GenericName getCoverageName()
            throws CoverageStoreException, CancellationException;

    /**
     * Returns the sample dimensions for each band of the {@link Coverage} to be read.
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
    List<SampleDimension> getSampleDimensions()
            throws CoverageStoreException, CancellationException;

    /**
     * Reads the coverage.
     *
     * @param  param Optional parameters used to control the reading process, or {@code null}.
     * @return The {@link Coverage} at the specified index.
     * @throws IllegalStateException if the input source has not been set.
     * @throws IndexOutOfBoundsException if the supplied index is out of bounds.
     * @throws CoverageStoreException If an error occurs while reading the information from the input source.
     * @throws CancellationException If {@link #abort()} has been invoked in an other thread during
     *         the execution of this method.
     */
    Coverage read(GridCoverageReadParam param)
            throws CoverageStoreException, CancellationException;

    /**
     * Restores the {@code CoverageReader} to its initial state.
     *
     * @throws CoverageStoreException If an error occurs while restoring to the initial state.
     */
    void reset() throws CoverageStoreException;

    /**
     * Allows any resources held by this reader to be released. The result of calling
     * any other method subsequent to a call to this method is undefined.
     *
     * @throws CoverageStoreException If an error occurs while disposing resources.
     */
    void dispose() throws CoverageStoreException;

}
