/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2009-2010, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2009-2010, Geomatys
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
import javax.imageio.ImageReader;

import org.opengis.util.InternationalString;
import org.opengis.coverage.grid.GridCoverage;

import org.geotoolkit.coverage.GridSampleDimension;
import org.geotoolkit.coverage.grid.GeneralGridGeometry;
import org.geotoolkit.util.collection.BackingStoreException;


/**
 * Base class of {@link GridCoverage} readers. Reading is a two steps process:
 * The input file must be set first, then the actual reading is performed with
 * a call to the {@link #read} method. Example:
 *
 * {@preformat java
 *     GridCoverageReader reader = ...
 *     reader.setInput(new File("MyCoverage.dat"));
 *     GridCoverage coverage = reader.read(0);
 * }
 *
 * This class is conceptually equivalent to the {@link ImageReader} class in the standard
 * Java library. Actually implementations of this class are often a wrapper around a Java
 * {@code ImageReader}, converting geodetic coordinates to pixel coordinates before to
 * delegate the reading of pixel values.
 *
 * @author Martin Desruisseaux (IRD, Geomatys)
 * @version 3.09
 *
 * @see ImageReader
 *
 * @since 3.09 (derived from 2.4)
 * @module
 */
public abstract class GridCoverageReader {
    /**
     * Creates a new instance.
     */
    protected GridCoverageReader() {
    }

    /**
     * Sets the input source to the given object. The input is typically a
     * {@link java.io.File} or a {@link String} object. But some other types
     * (e.g. {@link javax.imageio.stream.ImageInputStream}) may be accepted
     * as well depending on the implementation.
     *
     * @param  input The input (typically {@link java.io.File} or {@link String}) to be read.
     * @throws IllegalArgumentException if input is not a valid instance for this reader.
     * @throws CoverageStoreException if the operation failed.
     *
     * @see ImageReader#setInput(Object)
     */
    public abstract void setInput(Object input) throws CoverageStoreException;

    /**
     * Returns the list of coverage names available from the current input source. The length
     * of the returned list is the number of coverages found in the current input source. The
     * elements in the returned list are the names of each coverage, which may be {@code null}
     * for unamed coverages.
     * <p>
     * The list returned may be backed by this {@code GridCoverageReader}: it should be used
     * only as long as this reader and its input source are valids, iterating over the list
     * may be costly and the operation performed on the list may throw a
     * {@link BackingStoreException}.
     *
     * @return The names of the coverages.
     * @throws IllegalStateException If the input source has not been set.
     * @throws CoverageStoreException If an error occurs reading the information from the input source.
     *
     * @see ImageReader#getNumImages(boolean)
     */
    public abstract List<InternationalString> getCoverageNames() throws CoverageStoreException;

    /**
     * Returns the grid geometry for the {@link GridCoverage} to be read at the given index.
     *
     * @param  index The index of the coverage to be queried.
     * @return The grid geometry for the {@link GridCoverage} at the specified index.
     * @throws IllegalStateException if the input source has not been set.
     * @throws IndexOutOfBoundsException if the supplied index is out of bounds.
     * @throws CoverageStoreException if an error occurs reading the width information from the input source.
     *
     * @see ImageReader#getWidth(int)
     * @see ImageReader#getHeight(int)
     */
    public abstract GeneralGridGeometry getGridGeometry(int index) throws CoverageStoreException;

    /**
     * Returns the sample dimensions for each band of the {@link GridCoverage} to be read.
     * If sample dimensions are not known, then this method returns {@code null}.
     *
     * @param  index The index of the coverage to be queried.
     * @return The list of sample dimensions for the {@link GridCoverage} at the specified index.
     *         This list length is equals to the number of bands in the {@link GridCoverage}.
     * @throws IllegalStateException if the input source has not been set.
     * @throws IndexOutOfBoundsException if the supplied index is out of bounds.
     * @throws CoverageStoreException if an error occurs reading the width information from the input source.
     */
    public abstract List<GridSampleDimension> getSampleDimensions(int index) throws CoverageStoreException;

    /**
     * Reads the grid coverage.
     *
     * @param  index The index of the image to be queried.
     * @param  param Optional parameters used to control the reading process, or {@code null}.
     * @return The {@link GridCoverage} at the specified index.
     * @throws IllegalStateException if the input source has not been set.
     * @throws IndexOutOfBoundsException if the supplied index is out of bounds.
     * @throws CoverageStoreException if an error occurs reading the width information from the input source.
     *
     * @see ImageReader#read(int)
     */
    public abstract GridCoverage read(int index, GridCoverageReadParam param) throws CoverageStoreException;

    /**
     * Restores the {@code GridCoverageReader} to its initial state.
     *
     * @throws CoverageStoreException if an error occurs while disposing resources.
     */
    public abstract void reset() throws CoverageStoreException;
}
