/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2004-2012, Open Source Geospatial Foundation (OSGeo)
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
package org.geotoolkit.coverage.grid;

import java.util.Map;
import java.util.List;
import java.util.Locale;
import java.util.Collections;
import java.util.logging.Logger;
import java.awt.geom.Point2D;
import java.text.NumberFormat;
import java.text.FieldPosition;
import javax.media.jai.PropertySource;

import org.opengis.coverage.grid.*;
import org.opengis.geometry.DirectPosition;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

import org.geotoolkit.resources.Errors;
import org.geotoolkit.util.logging.Logging;
import org.geotoolkit.coverage.AbstractCoverage;
import org.geotoolkit.geometry.DirectPosition2D;
import org.geotoolkit.util.collection.UnmodifiableArrayList;


/**
 * Base class for Geotk implementation of grid coverage.
 *
 * @author Martin Desruisseaux (IRD)
 * @version 3.00
 *
 * @since 2.1
 * @module
 */
public abstract class AbstractGridCoverage extends AbstractCoverage implements GridCoverage {
    /**
     * For compatibility during cross-version serialization.
     */
    private static final long serialVersionUID = 6476934258101450793L;

    /**
     * The logger for grid coverage operations.
     */
    public static final Logger LOGGER = Logging.getLogger(AbstractGridCoverage.class);

    /**
     * Sources grid coverage, or {@code null} if none. This information is lost during
     * serialization, in order to avoid sending a too large amount of data over the network.
     */
    private final transient List<GridCoverage> sources;

    /**
     * Constructs a grid coverage using the specified coordinate reference system. If the
     * coordinate reference system is {@code null}, then the subclasses must override
     * {@link #getDimension()}.
     *
     * @param name
     *          The grid coverage name.
     * @param crs
     *          The coordinate reference system. This specifies the coordinate system used when
     *          accessing a coverage or grid coverage with the {@code evaluate(...)} methods.
     * @param propertySource
     *          The source for this coverage, or {@code null} if none. Source may be
     *          (but is not limited to) a {@link javax.media.jai.PlanarImage} or an
     *          other {@code AbstractGridCoverage} object.
     * @param properties
     *          The set of properties for this coverage, or {@code null} if there is none.
     *          Keys are {@link String} objects ({@link javax.media.jai.util.CaselessStringKey}
     *          are accepted as well), while values may be any {@link Object}.
     */
    protected AbstractGridCoverage(final CharSequence             name,
                                   final CoordinateReferenceSystem crs,
                                   final PropertySource propertySource,
                                   final Map<?,?>           properties)
    {
        super(name, crs, propertySource, properties);
        sources = null;
    }

    /**
     * Constructs a grid coverage with sources. Arguments are the same than for the
     * {@linkplain #AbstractGridCoverage(CharSequence,CoordinateReferenceSystem,PropertySource,Map)
     * previous constructor}, with an additional {@code sources} argument.
     *
     * @param name
     *          The grid coverage name.
     * @param crs
     *          The coordinate reference system.
     * @param sources
     *          The {@linkplain #getSources sources} for a grid coverage, or {@code null} if none.
     * @param propertySource
     *          The source for properties for this coverage, or {@code null} if none.
     * @param properties
     *          Set of additional properties for this coverage, or {@code null} if there is none.
     */
    protected AbstractGridCoverage(final CharSequence             name,
                                   final CoordinateReferenceSystem crs,
                                   final GridCoverage[]        sources,
                                   final PropertySource propertySource,
                                   final Map<?,?>           properties)
    {
        super(name, crs, propertySource, properties);
        if (sources != null) {
            switch (sources.length) {
                case 0:  this.sources = null; break;
                case 1:  this.sources = Collections.singletonList(sources[0]); break;
                default: this.sources = UnmodifiableArrayList.wrap(sources.clone()); break;
            }
        } else {
            this.sources = null;
        }
    }

    /**
     * Constructs a new coverage with the same parameters than the specified coverage.
     *
     * @param name The name for this coverage, or {@code null} for the same than {@code coverage}.
     * @param coverage The source coverage.
     */
    protected AbstractGridCoverage(final CharSequence name,
                                   final GridCoverage coverage)
    {
        super(name, coverage);
        sources = Collections.singletonList(coverage);
    }

    /**
     * Returns the source data for a grid coverage. If the {@code GridCoverage} was produced from
     * an underlying dataset, the returned list is an empty list. If the {@code GridCoverage} was
     * produced using {@link org.opengis.coverage.grid.GridCoverageProcessor}, then it should
     * return the source grid coverage of the one used as input to {@code GridCoverageProcessor}.
     * In general the {@code getSources()} method is intended to return the original
     * {@code GridCoverage} on which it depends. This is intended to allow applications
     * to establish what {@code GridCoverage}s will be affected when others are updated,
     * as well as to trace back to the "raw data".
     */
    @Override
    public List<GridCoverage> getSources() {
        // Reminder: 'sources' is always null after deserialization.
        if (sources != null) {
            return sources;
        } else {
            return Collections.emptyList();
        }
    }

    /**
     * Returns {@code true} if grid data can be edited. The default
     * implementation returns {@code false}.
     */
    @Override
    public boolean isDataEditable() {
        return false;
    }

    /**
     * Returns the number of predetermined overviews for the grid.
     * The default implementation returns 0.
     */
    @Override
    public int getNumOverviews() {
        return 0;
    }

    /**
     * Returns the grid geometry for an overview. The default implementation always throws
     * an exception, since the default {@linkplain #getNumOverviews number of overviews} is 0.
     *
     * @throws IndexOutOfBoundsException if the specified index is out of bounds.
     */
    @Override
    public GridGeometry getOverviewGridGeometry(int index) throws IndexOutOfBoundsException {
        throw new IndexOutOfBoundsException(indexOutOfBounds(index));
    }

    /**
     * Returns a pre-calculated overview for a grid coverage. The default implementation always
     * throws an exception, since the default {@linkplain #getNumOverviews number of overviews}
     * is 0.
     *
     * @throws IndexOutOfBoundsException if the specified index is out of bounds.
     */
    @Override
    public GridCoverage getOverview(int index) throws IndexOutOfBoundsException {
        throw new IndexOutOfBoundsException(indexOutOfBounds(index));
    }

    /**
     * Returns information for the packing of grid coverage values.
     * The default implementation throws an {@link UnsupportedOperationException}.
     * We don't know at this time if and when this method will be implemented, since
     * the API is going to change when we will shift to ISO 19123.
     *
     * @deprecated Not yet implemented, and maybe will never be implemented.
     */
    @Override
    @Deprecated
    public GridPacking getGridPacking() {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    /**
     * Returns a block of grid coverage data for all sample dimensions.
     * The default implementation throws an {@link UnsupportedOperationException}.
     * We don't know at this time if and when this method will be implemented, since
     * the API is going to change when we will shift to ISO 19123.
     *
     * @deprecated We should use some higher level construct instead (multi-dimensional array
     *             or something similar).
     */
    @Override
    @Deprecated
    public byte[] getPackedDataBlock(final GridRange range) throws InvalidRangeException {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    /**
     * Returns a sequence of byte values for a block.
     * The default implementation throws an {@link UnsupportedOperationException}.
     * We don't know at this time if and when this method will be implemented, since
     * the API is going to change when we will shift to ISO 19123.
     *
     * @deprecated We should use some higher level construct instead (multi-dimensional array
     *             or something similar).
     */
    @Override
    @Deprecated
    public boolean[] getDataBlock(final GridRange range, boolean[] destination)
            throws InvalidRangeException
    {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    /**
     * Returns a sequence of byte values for a block.
     * The default implementation throws an {@link UnsupportedOperationException}.
     * We don't know at this time if and when this method will be implemented, since
     * the API is going to change when we will shift to ISO 19123.
     *
     * @deprecated We should use some higher level construct instead (multi-dimensional array
     *             or something similar).
     */
    @Override
    @Deprecated
    public byte[] getDataBlock(final GridRange range, byte[] destination)
            throws InvalidRangeException
    {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    /**
     * Returns a sequence of short values for a block.
     * The default implementation throws an {@link UnsupportedOperationException}.
     * We don't know at this time if and when this method will be implemented, since
     * the API is going to change when we will shift to ISO 19123.
     *
     * @deprecated We should use some higher level construct instead (multi-dimensional array
     *             or something similar).
     */
    @Override
    @Deprecated
    public short[] getDataBlock(final GridRange range, short[] destination)
            throws InvalidRangeException
    {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    /**
     * Returns a sequence of integer values for a block.
     * The default implementation throws an {@link UnsupportedOperationException}.
     * We don't know at this time if and when this method will be implemented, since
     * the API is going to change when we will shift to ISO 19123.
     *
     * @deprecated We should use some higher level construct instead (multi-dimensional array
     *             or something similar).
     */
    @Override
    @Deprecated
    public int[] getDataBlock(final GridRange range, int[] destination)
            throws InvalidRangeException
    {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    /**
     * Returns a sequence of float values for a block.
     * The default implementation throws an {@link UnsupportedOperationException}.
     * We don't know at this time if and when this method will be implemented, since
     * the API is going to change when we will shift to ISO 19123.
     *
     * @deprecated We should use some higher level construct instead (multi-dimensional array
     *             or something similar).
     */
    @Override
    @Deprecated
    public float[] getDataBlock(final GridRange range, final float[] destination)
            throws InvalidRangeException
    {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    /**
     * Returns a sequence of double values for a block.
     * The default implementation throws an {@link UnsupportedOperationException}.
     * We don't know at this time if and when this method will be implemented, since
     * the API is going to change when we will shift to ISO 19123.
     *
     * @deprecated We should use some higher level construct instead (multi-dimensional array
     *             or something similar).
     */
    @Override
    @Deprecated
    public double[] getDataBlock(final GridRange range, final double[] destination)
            throws InvalidRangeException
    {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    /**
     * Set a block of values for all sample dimensions. The default implementation always throws
     * an exception, since this grid coverage is not editable by default.
     *
     * @deprecated We should use some higher level construct instead (multi-dimensional array
     *             or something similar).
     */
    @Override
    @Deprecated
    public void setDataBlock(GridRange range, boolean[] values)
            throws InvalidRangeException, GridNotEditableException
    {
        throw new GridNotEditableException(); // TODO: provides a localized message.
    }

    /**
     * Set a block of values for all sample dimensions. The default implementation always throws
     * an exception, since this grid coverage is not editable by default.
     *
     * @deprecated We should use some higher level construct instead (multi-dimensional array
     *             or something similar).
     */
    @Override
    @Deprecated
    public void setDataBlock(GridRange range, byte[] values)
            throws InvalidRangeException, GridNotEditableException
    {
        throw new GridNotEditableException(); // TODO: provides a localized message.
    }

    /**
     * Set a block of values for all sample dimensions. The default implementation always throws
     * an exception, since this grid coverage is not editable by default.
     *
     * @deprecated We should use some higher level construct instead (multi-dimensional array
     *             or something similar).
     */
    @Override
    @Deprecated
    public void setDataBlock(GridRange range, short[] values)
            throws InvalidRangeException, GridNotEditableException
    {
        throw new GridNotEditableException(); // TODO: provides a localized message.
    }

    /**
     * Set a block of values for all sample dimensions. The default implementation always throws
     * an exception, since this grid coverage is not editable by default.
     *
     * @deprecated We should use some higher level construct instead (multi-dimensional array
     *             or something similar).
     */
    @Override
    @Deprecated
    public void setDataBlock(GridRange range, int[] values)
            throws InvalidRangeException, GridNotEditableException
    {
        throw new GridNotEditableException(); // TODO: provides a localized message.
    }

    /**
     * Set a block of values for all sample dimensions. The default implementation always throws
     * an exception, since this grid coverage is not editable by default.
     *
     * @deprecated We should use some higher level construct instead (multi-dimensional array
     *             or something similar).
     */
    @Override
    @Deprecated
    public void setDataBlock(GridRange range, float[] values)
            throws InvalidRangeException, GridNotEditableException
    {
        throw new GridNotEditableException(); // TODO: provides a localized message.
    }

    /**
     * Set a block of values for all sample dimensions. The default implementation always throws
     * an exception, since this grid coverage is not editable by default.
     *
     * @deprecated We should use some higher level construct instead (multi-dimensional array
     *             or something similar).
     */
    @Override
    @Deprecated
    public void setDataBlock(GridRange range, double[] values)
            throws InvalidRangeException, GridNotEditableException
    {
        throw new GridNotEditableException(); // TODO: provides a localized message.
    }

    /**
     * Returns a localized error message for {@link IndexOutOfBoundsException}.
     */
    private String indexOutOfBounds(final int index) {
        return Errors.getResources(getLocale()).
                getString(Errors.Keys.ILLEGAL_ARGUMENT_2, "index", index);
    }

    /**
     * Constructs an error message for a point that can not be evaluated.
     * This is used for formatting error messages.
     *
     * @param  point The coordinate point to format.
     * @param  outside {@code true} if the evaluation failed because the given point is outside
     *         the coverage, or {@code false} if it failed for an other (unknown) reason.
     * @return An error message.
     *
     * @since 2.5
     */
    protected String formatEvaluateError(final Point2D point, final boolean outside) {
        return formatEvaluateError((DirectPosition) new DirectPosition2D(point), outside);
    }

    /**
     * Constructs an error message for a position that can not be evaluated.
     * This is used for formatting error messages.
     *
     * @param  point The coordinate point to format.
     * @param  outside {@code true} if the evaluation failed because the given point is outside
     *         the coverage, or {@code false} if it failed for an other (unknown) reason.
     * @return An error message.
     *
     * @since 2.5
     */
    protected String formatEvaluateError(final DirectPosition point, final boolean outside) {
        final Locale locale = getLocale();
        return Errors.getResources(locale).getString(outside ?
                Errors.Keys.POINT_OUTSIDE_COVERAGE_1 : Errors.Keys.CANT_EVALUATE_FOR_COORDINATE_1, toString(point, locale));
    }

    /**
     * Constructs a string for the specified point.
     * This is used for formatting error messages.
     *
     * @param  point The coordinate point to format.
     * @param  locale The locale for formatting numbers.
     * @return The coordinate point as a string, without '(' or ')' characters.
     */
    static String toString(final Point2D point, final Locale locale) {
        return toString((DirectPosition) new DirectPosition2D(point), locale);
    }

    /**
     * Constructs a string for the specified point.
     * This is used for formatting error messages.
     *
     * @param  point The coordinate point to format.
     * @param  locale The locale for formatting numbers.
     * @return The coordinate point as a string, without '(' or ')' characters.
     */
    static String toString(final DirectPosition point, final Locale locale) {
        final StringBuffer buffer = new StringBuffer();
        final FieldPosition dummy = new FieldPosition(0);
        final NumberFormat format = NumberFormat.getNumberInstance(locale);
        final int       dimension = point.getDimension();
        for (int i=0; i<dimension; i++) {
            if (i != 0) {
                buffer.append(", ");
            }
            format.format(point.getOrdinate(i), buffer, dummy);
        }
        return buffer.toString();
    }
}
