/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2005-2012, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2007-2018, Geomatys
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

import javax.imageio.IIOException;
import java.io.IOException;
import java.nio.file.Path;
import java.sql.SQLException;
import java.util.List;
import java.util.Date;

import org.opengis.geometry.Envelope;
import org.apache.sis.measure.NumberRange;
import org.apache.sis.storage.DataStoreException;
import org.apache.sis.referencing.crs.DefaultTemporalCRS;

import org.geotoolkit.image.palette.IIOListeners;
import org.geotoolkit.coverage.CoverageStack;
import org.geotoolkit.coverage.GridSampleDimension;
import org.geotoolkit.coverage.grid.GridCoverage2D;
import org.geotoolkit.coverage.grid.GeneralGridGeometry;
import org.geotoolkit.resources.Errors;

import static java.lang.Double.NaN;


/**
 * Reference to a {@link GridCoverage2D}. This object holds some metadata about the coverage time range,
 * envelope, <cite>etc.</cite>) without the need to open the image file, since the metadata are extracted
 * from the database.
 *
 * <p>{@code GridCoverageReference} instances are immutable and thread-safe.</p>
 *
 * @author Martin Desruisseaux (IRD, Geomatys)
 * @author Sam Hiatt
 */
public final class GridCoverageReference implements CoverageStack.Element {
    /**
     * The series in which the {@code GridCoverageReference} is defined.
     */
    private final SeriesTable.Entry series;

    /**
     * The grid coverage filename, not including the extension.
     */
    private final String filename;

    /**
     * The 1-based index of the image to read.
     */
    final short imageIndex;

    /**
     * Image start time, inclusive.
     */
    private final long startTime;

    /**
     * Image end time, exclusive.
     */
    private final long endTime;

    /**
     * The spatial and vertical extents of the grid coverage, together with the <cite>grid to CRS</cite> transform.
     */
    private final GridGeometryEntry grid;

    /**
     * Creates an entry containing coverage information (but not yet the coverage itself).
     *
     * @param  startTime  the coverage start time, or {@code null} if none.
     * @param  endTime    the coverage end time, or {@code null} if none.
     */
    GridCoverageReference(final SeriesTable.Entry series, final String filename, final short imageIndex,
            final Date startTime, final Date endTime, final GridGeometryEntry grid) throws SQLException
    {
        this.series     = series;
        this.filename   = filename;
        this.imageIndex = imageIndex;
        this.startTime  = (startTime != null) ? startTime.getTime() : Long.MIN_VALUE;
        this.endTime    = (  endTime != null) ?   endTime.getTime() : Long.MAX_VALUE;
        this.grid       = grid;
    }

    /**
     * Returns a name for the coverage, for use in graphical user interfaces.
     *
     * @return the coverage name, suitable for use in a graphical user interface.
     */
    @Override
    public String getName() {
        final StringBuilder buffer = new StringBuilder(40);
        buffer.append(series.product).append(':').append(filename);
        if (imageIndex != 0) {
            buffer.append(':').append(imageIndex);
        }
        return buffer.toString();
    }

    /**
     * Returns the name of the driver to use for reading data.
     */
    final String getFormat() {
        return series.format.rasterFormat;
    }

    /**
     * Returns the path to the image file. The returned path should be absolute.
     *
     * @return path to the file.
     */
    final Path getPath() {
        return series.path(filename);
    }

    /**
     * Returns the grid geometry.
     *
     * @return the grid geometry.
     */
    @Override
    public GeneralGridGeometry getGridGeometry() {
        return grid.getGridGeometry(startTime != Long.MIN_VALUE ? new Date(startTime) : null,
                                      endTime != Long.MAX_VALUE ? new Date(  endTime) : null);
    }

    /**
     * Returns the spatio-temporal envelope of the coverage. The CRS of the returned envelope
     * is the {@linkplain #getCoordinateReferenceSystem(boolean) spatio-temporal CRS} of this
     * entry, which may vary on a coverage-by-coverage basis.
     *
     * @return the coverage spatio-temporal envelope.
     */
    @Override
    public Envelope getEnvelope() {
        return getGridGeometry().getEnvelope();
    }

    @Override
    public Number getZCenter() throws IOException {
        final NumberRange<?> range = getZRange();
        if (range != null) {
            final Number lower = range.getMinValue();
            final Number upper = range.getMaxValue();
            if (lower != null) {
                if (upper != null) {
                    return 0.5 * (lower.doubleValue() + upper.doubleValue());
                } else {
                    return lower.doubleValue();
                }
            } else if (upper != null) {
                return upper.doubleValue();
            } else {
                return NaN;
            }
        } else {
            return NaN;
        }
    }

    /**
     * Returns the range of values in the third dimension, which may be vertical or temporal.
     * The main purpose of this method is to allow sorting of entries, not to get the elevation.
     * If the coverage is not restricted to a particular range along the third dimension,
     * then this method returns a range with infinite bounds.
     *
     * {@section Unit of measurement}
     * This method returns the range in units of the database vertical or temporal CRS, which
     * may not be the same than the vertical or temporal CRS of the coverage. This is done that
     * way in order to allow sorting coverages by elevation or by time no matter how the coverage
     * represents those quantities. If elevation or time in units of the coverage CRS is desired,
     * then use the {@link #getEnvelope()} method instead.
     *
     * @return the range of values in the third dimension, in units of the database CRS.
     */
    @Override
    public NumberRange<Double> getZRange() {
        double min, max;
        final Envelope envelope = grid.standardEnvelope;
        if (envelope.getDimension() >= 3) {
            min = envelope.getMinimum(2);
            max = envelope.getMaximum(2);
        } else {
            min = Double.NEGATIVE_INFINITY;
            max = Double.POSITIVE_INFINITY;
            final DefaultTemporalCRS temporalCRS = GridGeometryEntry.TEMPORAL_CRS;
            if (startTime != Long.MIN_VALUE) min = temporalCRS.toValue(new Date(startTime));
            if (  endTime != Long.MAX_VALUE) max = temporalCRS.toValue(new Date(  endTime));
        }
        return NumberRange.create(min, true, max, false);
    }

    /**
     * Returns the coverage sample dimensions, or {@code null} if unknown.
     * This method returns always the <cite>geophysics</cite> version of sample dimensions
     * (<code>{@linkplain GridSampleDimension#geophysics geophysics}(true)</code>), which is
     * consistent with the coverage returned by {@link #getCoverage getCoverage(...)}.
     *
     * @return the sample dimensions, or {@code null} if unknown.
     */
    @Override
    public GridSampleDimension[] getSampleDimensions() {
        final List<GridSampleDimension> sd = series.format.sampleDimensions;
        if (sd == null) {
            return null;
        }
        final GridSampleDimension[] bands = sd.toArray(new GridSampleDimension[sd.size()]);
        for (int i=0; i<bands.length; i++) {
            bands[i] = bands[i].geophysics(true);
        }
        return bands;
    }

    /**
     * Loads the data if needed and returns the coverage.
     */
    @Override
    public GridCoverage2D getCoverage(final IIOListeners listeners) throws IOException {
        try {
            return IO.read(getFormat(), getPath());
        } catch (DataStoreException e) {
            final Throwable cause = e.getCause();
            if (cause instanceof IOException) {
                throw (IOException) cause;
            }
            throw new IIOException(Errors.format(Errors.Keys.CantReadFile_1, getName()), e);
        }
    }
}
