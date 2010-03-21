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
import org.opengis.metadata.extent.GeographicBoundingBox;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

import org.geotoolkit.image.io.IIOListeners;
import org.geotoolkit.coverage.CoverageStack;
import org.geotoolkit.coverage.GridSampleDimension;
import org.geotoolkit.coverage.grid.GridCoverage2D;
import org.geotoolkit.coverage.grid.GridGeometry2D;
import org.geotoolkit.util.NumberRange;
import org.geotoolkit.util.DateRange;


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
     *
     * @return The coverage name, suitable for use in a graphical user interface.
     */
    @Override
    String getName();

    /**
     * Returns the path to the image file as an object of the given type. The current
     * implementation supports only the {@link java.io.File}, {@link java.net.URL} and
     * {@link java.net.URI} types.
     * <p>
     * In the particular case of input of type {@link java.io.File}, the returned path
     * is expected to be {@link java.io.File#isAbsolute() absolute}. If the file is not
     * absolute, then the file is probably not accessible on the local machine (i.e. the
     * path is relative to a distant server and can nott be represented as a {@code File}
     * object). In such case, consider using the {@link java.net.URI} type instead.
     *
     * @param  <T>  The compile-time type of the {@code type} argument.
     * @param  type The desired input type: {@link java.io.File}, {@link java.net.URL} or
     *         {@link java.net.URI}.
     * @return The input as an object of the given type.
     * @throws IOException If the input can not be represented as an object of the given type.
     */
    <T> T getFile(final Class<T> type) throws IOException;

    /**
     * Returns the image format. The returned string should be one of the names recognized
     * by the Java image I/O framework. For example, the returned string shall be understood
     * by {@link javax.imageio.ImageIO#getImageReadersByFormatName(String)}.
     *
     * @return The Java Image I/O format name.
     */
    String getImageFormat();

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
     * This method is equivalent to the following call:
     *
     * {@preformat java
     *     return getGridGeometry().getEnvelope();
     * }
     *
     * @return The coverage spatio-temporal envelope.
     */
    @Override
    Envelope getEnvelope();

    /**
     * Returns the range of values in the third dimension, which may be vertical or temporal.
     * This method returns the range in units of the database vertical or temporal CRS, which
     * may not be the same than the vertical or temporal CRS of the coverage. This is done that
     * way in order to allow sorting coverages by elevation or by time no matter how the coverage
     * represents those quantities.
     * <p>
     * If elevation or time in units of the coverage CRS is desired, use the
     * {@link #getEnvelope()} method instead.
     */
    @Override
    NumberRange<?> getZRange();

    /**
     * Returns the temporal part of the {@linkplain #getEnvelope coverage envelope}.
     * Invoking this method is equivalent to extracting the temporal component of the
     * envelope and transforming the coordinates if needed.
     *
     * @return The temporal component of the envelope, or {@code null} if none.
     */
    DateRange getTimeRange();

    /**
     * Returns the coverage grid geometry.
     *
     * @return The coverage grid geometry.
     */
    @Override
    GridGeometry2D getGridGeometry();

    /**
     * Returns the coverage sample dimensions, or {@code null} if unknown.
     * <p>
     * This method returns always the <cite>geophysics</cite> version of sample dimensions
     * (<code>{@linkplain GridSampleDimension#geophysics geophysics}(true)</code>), which is
     * consistent with the coverage returned by {@link #getCoverage getCoverage(...)}.
     *
     * @return The sample dimensions, or {@code null}.
     */
    @Override
    GridSampleDimension[] getSampleDimensions();

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
     */
    @Override
    GridCoverage2D getCoverage(IIOListeners listeners) throws IOException, CancellationException;

    /**
     * Abort the image reading. This method can be invoked from any thread. If {@link #getCoverage
     * getCoverage(...)} was in progress at the time this method is invoked, then it will stop and
     * throw {@link CancellationException}.
     */
    void abort();
}
