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

import java.util.Map;
import java.util.HashMap;
import java.util.Date;
import java.awt.Shape;
import java.awt.geom.Rectangle2D;

import org.opengis.util.FactoryException;
import org.opengis.referencing.cs.RangeMeaning;
import org.opengis.referencing.cs.CoordinateSystem;
import org.opengis.referencing.cs.CoordinateSystemAxis;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.datum.PixelInCell;
import org.opengis.referencing.operation.MathTransform;
import org.opengis.referencing.operation.MathTransform2D;
import org.opengis.referencing.operation.TransformException;
import org.opengis.metadata.spatial.DimensionNameType;

import org.apache.sis.geometry.ImmutableEnvelope;
import org.apache.sis.coverage.grid.GridExtent;
import org.apache.sis.coverage.grid.GridGeometry;
import org.apache.sis.referencing.CRS;
import org.apache.sis.referencing.CommonCRS;
import org.apache.sis.referencing.cs.AxesConvention;
import org.apache.sis.referencing.crs.AbstractCRS;
import org.apache.sis.referencing.crs.DefaultTemporalCRS;
import org.apache.sis.referencing.IdentifiedObjects;
import org.apache.sis.referencing.operation.transform.MathTransforms;
import org.apache.sis.referencing.operation.matrix.AffineTransforms2D;
import org.apache.sis.internal.referencing.j2d.AffineTransform2D;

import org.geotoolkit.coverage.grid.GeneralGridEnvelope;
import org.geotoolkit.coverage.grid.GeneralGridGeometry;


/**
 * Implementation of a four-dimensional grid geometry. This class assumes that the two first
 * axes are always for the horizontal component of the CRS (no matter if it is (x,y) or (y,x))
 * and that the vertical component, if any, is the third axis. The time dimension is the last
 * axis.
 *
 * <p>This implementation allows direct accesses to the fields for convenience and efficiency,
 * but those fields should never be modified. We allow this unsafe practice because this class
 * is not public.</p>
 *
 * @author Martin Desruisseaux (IRD, Geomatys)
 * @author Sam Hiatt
 */
final class GridGeometryEntry {
    /**
     * Number of dimensions in the {@code "GridGeometries"} table, before taking in account
     * the additional axes.
     */
    private static final int DIMENSIONS = 2;

    /**
     * The temporal coordinate reference system created for the date.
     * Hard-coded for now, may need to be revisited in a future version.
     *
     * @see Database#temporalCRS
     */
    static final DefaultTemporalCRS TEMPORAL_CRS = DefaultTemporalCRS.castOrCopy(CommonCRS.Temporal.TRUNCATED_JULIAN.crs());

    /**
     * A shape describing the coverage outline in the database CRS (usually WGS 84). This is the value computed by Geotk,
     * not the PostGIS object declared in the database, in order to make sure that coordinate transformations are applied
     * using the same algorithm, i.e. the Geotk algorithm instead than the Proj4 algorithms used by PostGIS.
     */
    final ImmutableEnvelope standardEnvelope;

    /**
     * The immutable grid geometry without the temporal dimension. This grid geometry may be 2D, 3D or more.
     * The coordinate reference system is the one declared in the {@link GridGeometryTable} for that entry.
     * The envelope must include the vertical range if any, but not the temporal dimension.
     */
    private final GridGeometry geometry;

    /**
     * Same coordinate reference system than the one used by {@link #geometry}, with time axis added.
     */
    private final CoordinateReferenceSystem spatioTemporalCRS;

    /**
     * Creates an entry from the given grid geometry. This constructor does not clone
     * the object given in argument. Consequently, those object shall not be modified
     * after {@code GridGeometryEntry} construction.
     *
     * @param width         number of cells in the first dimension.
     * @param height        number of cells in the second dimension.
     * @param affine        the grid to CRS affine transform for the two first dimensions.
     * @param crs           the coordinate reference system for the two first dimensions.
     * @param axes          additional dimensions, or {@code null} if none.
     * @param extraDimName  a label for the additional dimensions, or {@code null} if none.
     */
    GridGeometryEntry(final long width, final long height,
                      final AffineTransform2D affine, CoordinateReferenceSystem crs,
                      final AdditionalAxisTable.Entry[] axes, final String extraDimName,
                      final Database database) throws FactoryException, TransformException, IllegalRecordException
    {
        /*
         * First get the extent of the two first dimensions in the raster CRS (not the CRS of the "extent" column).
         * We need that for deciding whether the longitude values should be in [0…360]° range instead of [-180 … 180]°.
         */
        Shape shape = new Rectangle2D.Double(0, 0, width, height);
        shape = AffineTransforms2D.transform(affine, shape, true);
        if (needsLongitudeShift(shape.getBounds2D(), crs.getCoordinateSystem())) {
            crs = AbstractCRS.castOrCopy(crs).forConvention(AxesConvention.POSITIVE_RANGE);
        }
        shape = ((MathTransform2D) CRS.findOperation(crs, database.extentCRS, null).getMathTransform()).createTransformedShape(shape);
        final Rectangle2D bounds = shape.getBounds2D();
        if (bounds.isEmpty()) {
            throw new IllegalRecordException("Empty grid extent.");
        }
        /*
         * Collects the data for grid extent and the envelope, starting from the two first dimensions
         * and adding all supplemental axes.
         */
        int dim = DIMENSIONS;
        if (axes != null) {
            dim += axes.length;
        }
        final long[]   lower   = new long  [dim];
        final long[]   upper   = new long  [dim];
        final double[] minimum = new double[dim];
        final double[] maximum = new double[dim];
        final DimensionNameType[] names = new DimensionNameType[dim];
        names[0] = DimensionNameType.COLUMN; upper[0] = width;  minimum[0] = bounds.getMinX(); maximum[0] = bounds.getMaxX();
        names[1] = DimensionNameType.ROW;    upper[1] = height; minimum[1] = bounds.getMinY(); maximum[1] = bounds.getMaxY();
        MathTransform gridToCRS = affine;
        if (axes != null) {
            final CoordinateReferenceSystem[] components = new CoordinateReferenceSystem[axes.length + 1];
            components[0] = crs;
            gridToCRS = database.mtFactory.createPassThroughTransform(0, gridToCRS, axes.length);
            for (int i=0; i<axes.length; i++) {
                final AdditionalAxisTable.Entry axis = axes[i];
                names  [DIMENSIONS + i] = axis.type();
                upper  [DIMENSIONS + i] = axis.count;
                minimum[DIMENSIONS + i] = axis.standardMin;
                maximum[DIMENSIONS + i] = axis.standardMax;
                components[      1 + i] = axis.crs;
                MathTransform tr = axis.gridToCRS;
                tr = database.mtFactory.createPassThroughTransform(DIMENSIONS + i, tr, dim - (i+1));
                gridToCRS = database.mtFactory.createConcatenatedTransform(gridToCRS, tr);
            }
            crs = database.crsFactory.createCompoundCRS(properties(crs, extraDimName), components);
        }
        geometry = new GridGeometry(new GridExtent(names, lower, upper, false), PixelInCell.CELL_CORNER, gridToCRS, crs);
        standardEnvelope = new ImmutableEnvelope(minimum, maximum, null);
        spatioTemporalCRS = database.crsFactory.createCompoundCRS(properties(crs, "time"), crs, database.temporalCRS);
    }

    /**
     * Creates a default name for a new compound CRS adding one (rarely more) dimension to an existing CRS.
     */
    private static Map<String,Object> properties(final CoordinateReferenceSystem crs, final String extraDimName) {
        final Map<String,Object> properties = new HashMap<>(IdentifiedObjects.getProperties(crs, CoordinateReferenceSystem.IDENTIFIERS_KEY));
        properties.put(CoordinateReferenceSystem.NAME_KEY, IdentifiedObjects.getIdentifierOrName(crs) + " + " + extraDimName);
        return properties;
    }

    /**
     * Returns {@code true} if the given bounds seem to require longitude values in
     * the [0…360]° range instead than the default [-180 … 180]° range.
     */
    private static boolean needsLongitudeShift(final Rectangle2D bounds, final CoordinateSystem cs) {
        for (int i=0; i<=1; i++) {
            final CoordinateSystemAxis axis = cs.getAxis(i);
            if (RangeMeaning.WRAPAROUND.equals(axis.getRangeMeaning())) {
                final double min, max;
                switch (i) {
                    case 0: min = bounds.getMinX(); max = bounds.getMaxX(); break;
                    case 1: min = bounds.getMinY(); max = bounds.getMaxY(); break;
                    default: throw new AssertionError(i);
                }
                if (min >= 0 && max >= axis.getMaximumValue()) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Returns the Geotk implementation of grid geometry for the given time range.
     *
     * @todo the GeneralGridEnvelope created in this method could be created once-for-all in the constructor.
     *       Waiting for the replacement of Geotk classes by Apache SIS classes before doing so.
     */
    final GeneralGridGeometry getGridGeometry(final Date startTime, final Date endTime) {
        final boolean hasTime = (startTime != null && endTime != null);
        final GridExtent extent = geometry.getExtent();
        final int dimension = extent.getDimension();
        final int[] lower = new int[dimension + (hasTime ? 1 : 0)];
        final int[] upper = new int[lower.length];
        for (int i=0; i<dimension; i++) {
            lower[i] = Math.toIntExact(extent.getLow (i));
            upper[i] = Math.toIntExact(extent.getHigh(i));
        }
        MathTransform gridToCRS = geometry.getGridToCRS(PixelInCell.CELL_CORNER);
        final CoordinateReferenceSystem crs;
        if (hasTime) {
            final double tMin = TEMPORAL_CRS.toValue(startTime);
            final double tMax = TEMPORAL_CRS.toValue(endTime);
            gridToCRS = MathTransforms.compound(gridToCRS, MathTransforms.linear(tMax - tMin, tMin));
            crs = spatioTemporalCRS;
        } else {
            crs = geometry.getCoordinateReferenceSystem();
        }
        return new GeneralGridGeometry(new GeneralGridEnvelope(lower, upper, true), PixelInCell.CELL_CORNER, gridToCRS, crs);
    }
}
