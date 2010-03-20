/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2005-2010, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2007-2010, Geomatys
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
package org.geotoolkit.coverage.sql;

import java.net.URI;
import java.io.File;
import java.io.IOException;
import java.util.concurrent.CancellationException;

import org.opengis.geometry.Envelope;
import org.opengis.coverage.SampleDimension;
import org.opengis.coverage.grid.GridGeometry;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

import org.geotoolkit.image.io.IIOListeners;
import org.geotoolkit.coverage.CoverageStack;
import org.geotoolkit.coverage.GridSampleDimension;
import org.geotoolkit.coverage.grid.GridCoverage2D;
import org.geotoolkit.util.NumberRange;
import org.geotoolkit.util.DateRange;
import org.opengis.metadata.extent.GeographicBoundingBox;


/**
 * Reference to a {@link GridCoverage2D}. This object holds some metadata about the coverage
 * ({@linkplain #getTimeRange time range}, {@linkplain #getGeographicBoundingBox geographic
 * bounding box}, <cite>etc.</cite>) without the need to load the coverage itself. Coverage
 * loading will occurs only when {@link #getCoverage(IIOListeners)} is invoked for the first
 * time.
 * <p>
 * {@code GridCoverageReference} instances are immutable and thread-safe.
 *
 * @author Martin Desruisseaux (IRD, Geomatys)
 * @version 3.10
 *
 * @since 3.10 (derived from Seagis)
 * @module
 */
public interface GridCoverageReference extends CoverageStack.Element {
    /**
     * Returns a name for the coverage, for use in graphical user interfaces.
     */
    @Override
    String getName();

    /**
     * Returns the path to the image file, or {@code null} if the file is not accessible
     * on the local machine. In the later case, {@link #getURI} should be used instead.
     *
     * @return The path to the image file, or {@code null} if the image is not accessible
     *         through a {@link File} object.
     */
    File getFile();

    /**
     * Returns the URI to the image data, or {@code null} if none. The data may or may not
     * be a file hosted on the local machine.
     *
     * @return The path to the image file, or {@code null} if the image is not accessible
     *         through a {@link URI} object.
     */
    URI getURI();

    /**
     * Returns the native Coordinate Reference System of the coverage.
     * The returned CRS may be up to 4-dimensional.
     *
     * @param includeTime {@code true} if the returned CRS should include the time component,
     *        or {@code false} for a spatial-only CRS.
     *
     * @return The native CRS of the coverage.
     */
    CoordinateReferenceSystem getSpatioTemporalCRS(boolean includeTime);

    /**
     * Returns the geographic bounding box of the {@linkplain #getEnvelope coverage envelope}.
     * Invoking this method is equivalent to extracting the horizontal component of the envelope
     * and transform the coordinates if needed.
     * <p>
     * This method may return {@code null} if the geographic bounding box can not be computed.
     *
     * @return The geographic component of the envelope, or {@code null} if none.
     */
    GeographicBoundingBox getGeographicBoundingBox();

    /**
     * Returns the spatio-temporal envelope of the coverage.
     *
     * @return The coverage spatio-temporal envelope.
     */
    @Override
    Envelope getEnvelope();

    /**
     * Returns the range of values in the third dimension. This is typically the lowest and
     * highest coverage elevation in metres if the coverage envelope has a vertical dimension,
     * or the start time and end time otherwise.
     */
    @Override
    NumberRange<?> getZRange();

    /**
     * Returns the temporal part of the {@linkplain #getEnvelope coverage envelope}.
     * Invoking this method is equivalent to extracting the temporal component of the
     * envelope and transform the coordinates if needed.
     *
     * @return The temporal component of the envelope, or {@code null} if none.
     */
    DateRange getTimeRange();

    /**
     * Returns the coverage grid geometry. <strong>This information may be only approximative</strong>,
     * especially in the case of mosaic image because the reading process may use a different
     * subsampling than the requested one for performance reason.
     *
     * @return The coverage grid geometry.
     */
    @Override
    GridGeometry getGridGeometry();

    /**
     * Returns the coverage sample dimensions. This method returns always the
     * <cite>geophysics</cite> version of sample dimensions
     * (<code>{@linkplain GridSampleDimension#geophysics geophysics}(true)</code>), which is
     * consistent with the coverage returned by {@link #load load(...)}.
     */
    @Override
    SampleDimension[] getSampleDimensions();

    /**
     * Loads the data if needed and returns the coverage. This method returns always the geophysics
     * version of data (<code>{@linkplain GridCoverage2D#view view}(ViewType.GEOPHYSICS)</code>).
     * <p>
     * If the coverage has already been read previously and has not yet been reclaimed by the
     * garbage collector, then the existing coverage is returned immediately.
     *
     * @param  listeners Objects to inform about progress, or {@code null} if none.
     * @return The coverage.
     * @throws IOException if an error occured while reading the image.
     * @throws CancellationException if {@link #abort()} has been invoked during the reading process.
     *
     * @todo Should probable thrown an exception instead of returning null when the reading is aborted.
     */
    @Override
    GridCoverage2D getCoverage(IIOListeners listeners) throws IOException, CancellationException;

    /**
     * Abort the image reading. This method can be invoked from any thread. If {@link #load
     * load(...)} was in progress at the time this method is invoked, then it will stop and
     * throw {@link CancellationException}.
     */
    void abort();
}
