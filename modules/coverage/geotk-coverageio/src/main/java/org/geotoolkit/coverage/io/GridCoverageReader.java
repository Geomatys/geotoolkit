/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2009-2011, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2009-2011, Geomatys
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

import java.util.Arrays;
import java.util.Map;
import java.util.List;
import java.util.concurrent.CancellationException;
import javax.imageio.ImageReader;

import org.opengis.coverage.grid.GridCoverage;

import org.geotoolkit.coverage.GridSampleDimension;
import org.geotoolkit.coverage.grid.GeneralGridGeometry;
import org.geotoolkit.image.io.metadata.SpatialMetadata;
import org.geotoolkit.util.collection.BackingStoreException;
import org.geotoolkit.util.MeasurementRange;
import org.geotoolkit.resources.Vocabulary;
import org.geotoolkit.internal.io.IOUtilities;


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
 * @version 3.14
 *
 * @see ImageReader
 *
 * @since 3.09 (derived from 2.4)
 * @module
 */
public abstract class GridCoverageReader extends GridCoverageStore {
    /**
     * The input (typically a {@link java.io.File}, {@link java.net.URL} or {@link String}),
     * or {@code null} if input is not set.
     */
    Object input;

    /**
     * Creates a new instance.
     */
    protected GridCoverageReader() {
        ignoreGridTransforms = true;
    }

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
     * @throws IllegalArgumentException if input is not a valid instance for this reader.
     * @throws CoverageStoreException if the operation failed.
     *
     * @see ImageReader#setInput(Object)
     */
    public void setInput(final Object input) throws CoverageStoreException {
        this.input = input;
        abortRequested = false;
    }

    /**
     * Returns the input which was set by the last call to {@link #setInput(Object)},
     * or {@code null} if none.
     *
     * @return The current input, or {@code null} if none.
     * @throws CoverageStoreException if the operation failed.
     *
     * @see ImageReader#getInput()
     */
    public Object getInput() throws CoverageStoreException {
        return input;
    }

    /**
     * Returns the name of the {@linkplain #input}, or "<cite>Untitled</cite>" if
     * the input is not a recognized type. This is used for formatting messages only.
     */
    final String getInputName() {
        final Object input = this.input;
        if (IOUtilities.canProcessAsPath(input)) {
            return IOUtilities.name(input);
        } else {
            return Vocabulary.getResources(locale).getString(Vocabulary.Keys.UNTITLED);
        }
    }

    /**
     * Returns the list of coverage names available from the current input source. The length
     * of the returned list is the number of coverages found in the current input source. The
     * elements in the returned list are the names of each coverage.
     * <p>
     * The returned list may be backed by this {@code GridCoverageReader}: it should be used
     * only as long as this reader and its input source are valids. Iterating over the list
     * may be costly and the operation performed on the list may throw a
     * {@link BackingStoreException}.
     *
     * @return The names of the coverages.
     * @throws IllegalStateException If the input source has not been set.
     * @throws CoverageStoreException If an error occurs reading the information from the input source.
     * @throws CancellationException If {@link #abort()} has been invoked in an other thread during
     *         the execution of this method.
     *
     * @see ImageReader#getNumImages(boolean)
     */
    public abstract List<String> getCoverageNames()
            throws CoverageStoreException, CancellationException;

    /**
     * Returns the grid geometry for the {@link GridCoverage} to be read at the given index.
     *
     * @param  index The index of the coverage to be queried.
     * @return The grid geometry for the {@link GridCoverage} at the specified index.
     * @throws IllegalStateException if the input source has not been set.
     * @throws IndexOutOfBoundsException if the supplied index is out of bounds.
     * @throws CoverageStoreException if an error occurs reading the information from the input source.
     * @throws CancellationException If {@link #abort()} has been invoked in an other thread during
     *         the execution of this method.
     *
     * @see ImageReader#getWidth(int)
     * @see ImageReader#getHeight(int)
     */
    public abstract GeneralGridGeometry getGridGeometry(int index)
            throws CoverageStoreException, CancellationException;

    /**
     * Returns the sample dimensions for each band of the {@link GridCoverage} to be read.
     * If sample dimensions are not known, then this method returns {@code null}.
     *
     * @param  index The index of the coverage to be queried.
     * @return The list of sample dimensions for the {@link GridCoverage} at the specified index,
     *         or {@code null} if none. This list length is equals to the number of bands in the
     *         {@link GridCoverage}.
     * @throws IllegalStateException if the input source has not been set.
     * @throws IndexOutOfBoundsException if the supplied index is out of bounds.
     * @throws CoverageStoreException if an error occurs reading the information from the input source.
     * @throws CancellationException If {@link #abort()} has been invoked in an other thread during
     *         the execution of this method.
     */
    public abstract List<GridSampleDimension> getSampleDimensions(int index)
            throws CoverageStoreException, CancellationException;

    /**
     * Returns the ranges of valid sample values for each band in this format.
     * The ranges are always expressed in <cite>geophysics</cite> units.
     * <p>
     * The default implementation computes the ranges from the information returned
     * by {@link #getSampleDimensions(int)}, if any.
     *
     * @param  index The index of the coverage to be queried.
     * @return The ranges of values for each band, or {@code null} if none.
     * @throws CoverageStoreException if an error occurs reading the information from the input source.
     * @throws CancellationException If {@link #abort()} has been invoked in an other thread during
     *         the execution of this method.
     *
     * @since 3.10
     */
    public List<MeasurementRange<?>> getSampleValueRanges(final int index)
            throws CoverageStoreException, CancellationException
    {
        final List<GridSampleDimension> sampleDimensions = getSampleDimensions(index);
        if (sampleDimensions == null) {
            return null;
        }
        @SuppressWarnings({"unchecked","rawtypes"})  // Generic array creation.
        final MeasurementRange<?>[] ranges = new MeasurementRange[sampleDimensions.size()];
        for (int i=0; i<ranges.length; i++) {
            GridSampleDimension sd = sampleDimensions.get(i);
            if (sd != null) {
                sd = sd.geophysics(true);
                ranges[i] = MeasurementRange.createBestFit(
                        sd.getMinimumValue(), true, sd.getMaximumValue(), true, sd.getUnits());
            }
        }
        return Arrays.asList(ranges);
    }

    /**
     * Returns the metadata associated with the input source as a whole, or {@code null} if none.
     * The default implementation returns {@code null} in every cases.
     *
     * @return The metadata associated with the input source as a whole, or {@code null}.
     * @throws CoverageStoreException if an error occurs reading the information from the input source.
     *
     * @see ImageReader#getStreamMetadata()
     *
     * @since 3.14
     */
    public SpatialMetadata getStreamMetadata() throws CoverageStoreException {
        return null;
    }

    /**
     * Returns the metadata associated with the given coverage, or {@code null} if none.
     * The default implementation returns {@code null} in every cases.
     *
     * @param  index The index of the coverage to be queried.
     * @return The metadata associated with the given coverage, or {@code null}.
     * @throws CoverageStoreException if an error occurs reading the information from the input source.
     *
     * @see ImageReader#getImageMetadata(int)
     *
     * @since 3.14
     */
    public SpatialMetadata getCoverageMetadata(final int index) throws CoverageStoreException {
        return null;
    }

    /**
     * Returns an optional map of properties associated with the coverage at the given index, or
     * {@code null} if none. The properties are implementation-specific; they are available to
     * subclasses for any use. The {@code GridCoverageReader} class will simply gives those
     * properties to the {@link javax.media.jai.PropertySource} object to be created by the
     * {@link #read read} method, without any processing.
     * <p>
     * The default implementation returns {@code null} in every cases.
     *
     * @param  index The index of the coverage to be queried.
     * @return The properties, or {@code null} if none.
     * @throws CoverageStoreException if an error occurs reading the information from the input source.
     * @throws CancellationException If {@link #abort()} has been invoked in an other thread during
     *         the execution of this method.
     */
    public Map<?,?> getProperties(int index) throws CoverageStoreException, CancellationException {
        return null;
    }

    /**
     * Reads the grid coverage.
     *
     * @param  index The index of the coverage to be queried.
     * @param  param Optional parameters used to control the reading process, or {@code null}.
     * @return The {@link GridCoverage} at the specified index.
     * @throws IllegalStateException if the input source has not been set.
     * @throws IndexOutOfBoundsException if the supplied index is out of bounds.
     * @throws CoverageStoreException if an error occurs reading the information from the input source.
     * @throws CancellationException If {@link #abort()} has been invoked in an other thread during
     *         the execution of this method.
     *
     * @see ImageReader#read(int)
     */
    public abstract GridCoverage read(int index, GridCoverageReadParam param)
            throws CoverageStoreException, CancellationException;

    /**
     * Restores the {@code GridCoverageReader} to its initial state.
     *
     * @throws CoverageStoreException if an error occurs while restoring to the initial state.
     *
     * @see ImageReader#reset()
     */
    @Override
    public void reset() throws CoverageStoreException {
        input = null;
        super.reset();
    }

    /**
     * Allows any resources held by this reader to be released. The result of calling
     * any other method subsequent to a call to this method is undefined.
     *
     * @throws CoverageStoreException if an error occurs while disposing resources.
     *
     * @see ImageReader#dispose()
     */
    @Override
    public void dispose() throws CoverageStoreException {
        input = null;
        super.dispose();
    }
}
