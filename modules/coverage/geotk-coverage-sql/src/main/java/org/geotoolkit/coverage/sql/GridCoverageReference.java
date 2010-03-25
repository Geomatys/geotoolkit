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
import java.awt.geom.Rectangle2D;

import org.opengis.geometry.Envelope;
import org.opengis.metadata.extent.GeographicBoundingBox;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

import org.geotoolkit.image.io.IIOListeners;
import org.geotoolkit.coverage.CoverageStack;
import org.geotoolkit.coverage.GridSampleDimension;
import org.geotoolkit.coverage.grid.GridCoverage2D;
import org.geotoolkit.coverage.grid.GridGeometry2D;
import org.geotoolkit.coverage.io.CoverageStoreException;
import org.geotoolkit.coverage.io.GridCoverageReadParam;
import org.geotoolkit.coverage.io.GridCoverageReader;
import org.geotoolkit.util.NumberRange;
import org.geotoolkit.util.DateRange;


/**
 * Reference to a {@link GridCoverage2D}. This object holds some metadata about the coverage
 * ({@linkplain #getTimeRange time range}, {@linkplain #getGeographicBoundingBox geographic
 * bounding box}, <cite>etc.</cite>) without the need to open the image file - the metadata
 * are extracted from the database.
 * <p>
 * The actual loading of pixel values occurs when {@link #getCoverage(IIOListeners)} is invoked
 * for the first time. Alternatively, users can invoke {@link #getReader()} in order to get more
 * control on the reading process.
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
     * In the particular case of input of the {@link java.io.File} type, the returned path
     * is expected to be {@linkplain java.io.File#isAbsolute() absolute}. If the file is not
     * absolute, then the file is probably not accessible on the local machine (i.e. the
     * path is relative to a distant server and can not be represented as a {@code File}
     * object). In such case, consider using the {@code URI} type instead.
     *
     * @param  <T>  The compile-time type of the {@code type} argument.
     * @param  type The desired input type: {@link java.io.File}, {@link java.net.URL} or
     *         {@link java.net.URI}.
     * @return The input as an object of the given type.
     * @throws IOException If the input can not be represented as an object of the given type.
     */
    <T> T getFile(Class<T> type) throws IOException;

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
     * This method return {@code null} if the geographic bounding box can not be computed.
     *
     * @return The geographic component of the envelope, or {@code null} if unknown.
     */
    GeographicBoundingBox getGeographicBoundingBox();

    /**
     * Returns the spatio-temporal envelope of the coverage. The CRS of the returned envelope
     * is the {@linkplain #getSpatioTemporalCRS(boolean) spatio-temporal CRS} of this entry,
     * which may vary on a coverage-by-coverage basis. If an envelope is some unified CRS is
     * desired, consider using {@link #getXYRange()},  {@link #getZRange()} and
     * {@link #getTimeRange()} instead.
     * <p>
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
     * Returns the range of values in the two first dimensions, which are horizontal.
     * This method returns the range in units of the database horizontal CRS, which may
     * not be the same than the horizontal CRS of the coverage.
     * <p>
     * If the range of values in units of the coverage CRS is desired, then use the
     * {@link #getEnvelope()} method instead.
     *
     * @return The range of values in the two first dimensions, in units of the database CRS.
     */
    Rectangle2D getXYRange();

    /**
     * Returns the range of values in the third dimension, which may be vertical or temporal.
     * This method returns the range in units of the database vertical or temporal CRS, which
     * may not be the same than the vertical or temporal CRS of the coverage. This is done that
     * way in order to allow sorting coverages by elevation or by time no matter how the coverage
     * represents those quantities.
     * <p>
     * If elevation or time in units of the coverage CRS is desired, then use the
     * {@link #getEnvelope()} method instead.
     *
     * @return The range of values in the third dimension, in units of the database CRS.
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
     * This method returns always the <cite>geophysics</cite> version of sample dimensions
     * (<code>{@linkplain GridSampleDimension#geophysics geophysics}(true)</code>), which is
     * consistent with the coverage returned by {@link #getCoverage getCoverage(...)}.
     *
     * @return The sample dimensions, or {@code null} if unknown.
     */
    @Override
    GridSampleDimension[] getSampleDimensions();

    /**
     * Returns a pre-configured reader which can be used for loading the pixel values.
     * The {@linkplain GridCoverageReader#getInput()} of the returned reader is the
     * {@linkplain #getFile(Class)} declared in this entry, and can not be changed.
     * <p>
     * This method gives to the user more control on the reading process. For example, it allows
     * to {@linkplain GridCoverageReader#abort() abort} the reading process of that reader only,
     * as opposed to the {@link #abort()} method defined in this interface. However if such control
     * is not needed, consider using the {@link #getCoverage getCoverage} or {@link #read read}
     * method instead since they may share resources (e.g. the underlying
     * {@link javax.imageio.ImageReader}) more effectively.
     * <p>
     * It is the caller responsability to invoke {@link GridCoverageReader#dispose()}
     * when the reader is no longer needed.
     *
     * @return A pre-configured reader for loading the pixel values.
     * @throws CoverageStoreException If an error occured while creating the reader.
     */
    GridCoverageReader getReader() throws CoverageStoreException;

    /**
     * Reads the data if needed and returns the coverage. This method is equivalent to invoking
     * the {@link #read read} method, except that default parameters are used, typically reading
     * the full coverage. If the coverage has already been read previously and has not yet been
     * reclaimed by the garbage collector, then the existing coverage may be returned immediately.
     * <p>
     * This method returns always the {@linkplain org.geotoolkit.coverage.grid.ViewType#GEOPHYSICS
     * geophysics} version of data, like the pseudo-code below:
     *
     * {@preformat java
     *     return GridCoverage2D.view(ViewType#GEOPHYSICS);
     * }
     *
     * @param  listeners Objects to inform about progress, or {@code null} if none.
     * @return The coverage.
     *
     * @throws IOException if an error occured while reading the image.
     * @throws CancellationException if {@link #abort()} has been invoked during the reading process.
     */
    @Override
    GridCoverage2D getCoverage(IIOListeners listeners) throws IOException, CancellationException;

    /**
     * Reads the data and returns the coverage. At the difference of
     * {@link #getCoverage(IIOListeners)}, this method doesn't cache the returned coverage.
     * <p>
     * This method returns always the {@linkplain org.geotoolkit.coverage.grid.ViewType#GEOPHYSICS
     * geophysics} version of data, like the pseudo-code below:
     *
     * {@preformat java
     *     return GridCoverage2D.view(ViewType#GEOPHYSICS);
     * }
     *
     * @param  param Optional parameters used to control the reading process, or {@code null}.
     * @param  listeners Objects to inform about progress, or {@code null} if none.
     * @return The coverage.
     *
     * @throws CoverageStoreException if an error occured while reading the image.
     * @throws CancellationException if {@link #abort()} has been invoked during the reading process.
     */
    GridCoverage2D read(GridCoverageReadParam param, IIOListeners listeners)
            throws CoverageStoreException, CancellationException;

    /**
     * Aborts the image reading. This method can be invoked from any thread. If {@link #read
     * read(...)} was in progress at the time this method is invoked, then it will stop and
     * throw {@link CancellationException}.
     * <p>
     * Note that if more than one read was in progress concurrently, all of them will be
     * aborted by the call to this method.
     */
    void abort();
}
