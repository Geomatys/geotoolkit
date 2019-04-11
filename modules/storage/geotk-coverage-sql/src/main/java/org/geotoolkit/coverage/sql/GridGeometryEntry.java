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
import java.time.Instant;
import java.awt.Shape;
import java.awt.geom.Rectangle2D;

import org.opengis.util.FactoryException;
import org.opengis.referencing.cs.RangeMeaning;
import org.opengis.referencing.cs.CoordinateSystem;
import org.opengis.referencing.cs.CoordinateSystemAxis;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.crs.CRSFactory;
import org.opengis.referencing.datum.PixelInCell;
import org.opengis.referencing.operation.MathTransform;
import org.opengis.referencing.operation.TransformException;
import org.opengis.referencing.operation.MathTransformFactory;
import org.opengis.metadata.spatial.DimensionNameType;

import org.apache.sis.coverage.grid.GridExtent;
import org.apache.sis.coverage.grid.GridGeometry;
import org.apache.sis.coverage.grid.PixelTranslation;
import org.apache.sis.referencing.CommonCRS;
import org.apache.sis.referencing.cs.AxesConvention;
import org.apache.sis.referencing.crs.AbstractCRS;
import org.apache.sis.referencing.crs.DefaultTemporalCRS;
import org.apache.sis.referencing.IdentifiedObjects;
import org.apache.sis.referencing.operation.transform.MathTransforms;
import org.apache.sis.referencing.operation.matrix.AffineTransforms2D;
import org.apache.sis.internal.referencing.j2d.AffineTransform2D;


/**
 * Implementation of a four-dimensional grid geometry. This class assumes that the two first
 * axes are always for the horizontal component of the CRS (no matter if it is (x,y) or (y,x))
 * and that the vertical component, if any, is the third axis. The time dimension is the last
 * axis.
 *
 * @author Martin Desruisseaux (IRD, Geomatys)
 * @author Johann Sorel (Geomatys)
 * @author Sam Hiatt
 */
final class GridGeometryEntry extends Entry {
    /**
     * Number of dimensions of the "grid to CRS" part to be represented as an affine transform
     * in the "GridGeometries" table.
     */
    static final int AFFINE_DIMENSION = 2;

    /**
     * Location of (0,0) cell coordinates for the "grid to CRS" transforms handled in this package.
     */
    static final PixelInCell CELL_ORIGIN = PixelInCell.CELL_CORNER;

    /**
     * The temporal coordinate reference system created for the date.
     * Hard-coded for now, may need to be revisited in a future version.
     *
     * @see Database#temporalCRS
     */
    static final DefaultTemporalCRS TEMPORAL_CRS = DefaultTemporalCRS.castOrCopy(CommonCRS.Temporal.TRUNCATED_JULIAN.crs());

    /**
     * The immutable grid geometry without the temporal dimension. This grid geometry may be 2D, 3D or more.
     * The coordinate reference system is the one declared in the {@link GridGeometryTable} for that entry.
     * The envelope must include the vertical range if any, but not the temporal dimension.
     */
    private final GridGeometry spatialGeometry;

    /**
     * Extent of the grid {@linkplain #spatialGeometry} expanded with the {@link #spatioTemporalCRS}.
     */
    private final GridExtent spatioTemporalExtent;

    /**
     * Same coordinate reference system than the one used by {@link #spatialGeometry}, with time axis added.
     * The time axis is the same for all images in the database, namely {@link Database#temporalCRS}.
     */
    private final CoordinateReferenceSystem spatioTemporalCRS;

    /**
     * Whether the "grid to CRS" transform is only an approximation of non-linear transform.
     * In such case, it is not sufficient to rely on the {@link #spatialGeometry} field; caller may
     * need to reload the grid geometry from original file.
     */
    private final boolean approximate;

    /**
     * Time coordinates to insert on the temporal axis between grid coverage start time and end time,
     * or {@code null} if none.
     */
    private final AdditionalAxisEntry temporalAxis;

    /**
     * Creates an entry from the given grid geometry. This constructor does not clone
     * the object given in argument. Consequently, those object shall not be modified
     * after {@code GridGeometryEntry} construction.
     *
     * @param width         number of cells in the first dimension.
     * @param height        number of cells in the second dimension.
     * @param affine        the grid to CRS affine transform for the two first dimensions.
     * @param approximate   whether the "grid to CRS" transform is only an approximation of non-linear transform.
     * @param crs           the coordinate reference system for the two first dimensions.
     * @param temporalAxis  time coordinates to insert on the grid coverage temporal axis, or {@code null} if none.
     * @param otherAxes     additional dimensions, or {@code null} if none.
     * @param otherDimName  a label for the additional dimensions, or {@code null} if none.
     */
    GridGeometryEntry(final long width, final long height, final AffineTransform2D affine,
                      final boolean approximate, CoordinateReferenceSystem crs,
                      final AdditionalAxisEntry temporalAxis, final AdditionalAxisEntry[] otherAxes, final String otherDimName,
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
        /*
         * Collects the data for grid extent and the envelope, starting from the two first dimensions
         * and adding all supplemental axes.
         */
        int dim = AFFINE_DIMENSION;
        if (otherAxes != null) {
            dim += otherAxes.length;
        }
        final long[]   upper   = new long  [dim];
        final DimensionNameType[] names = new DimensionNameType[dim];
        names[0] = DimensionNameType.COLUMN; upper[0] = width;
        names[1] = DimensionNameType.ROW;    upper[1] = height;
        final CRSFactory crsFactory = database.getCRSFactory();
        MathTransform gridToCRS = affine;
        if (otherAxes != null) {
            final CoordinateReferenceSystem[] components = new CoordinateReferenceSystem[otherAxes.length + 1];
            components[0] = crs;
            final MathTransformFactory mtFactory = database.getMathTransformFactory();
            gridToCRS = mtFactory.createPassThroughTransform(0, gridToCRS, otherAxes.length);
            for (int i=0; i<otherAxes.length; i++) {
                final AdditionalAxisEntry axis = otherAxes[i];
                names[AFFINE_DIMENSION + i] = axis.type();
                upper[AFFINE_DIMENSION + i] = axis.count;
                components[          1 + i] = axis.crs;
                MathTransform tr            = axis.gridToCRS;
                tr = mtFactory.createPassThroughTransform(AFFINE_DIMENSION + i, tr, otherAxes.length - (i + 1));
                gridToCRS = mtFactory.createConcatenatedTransform(gridToCRS, tr);
            }
            crs = crsFactory.createCompoundCRS(properties(crs, otherDimName), components);
        }
        spatialGeometry      = new GridGeometry(new GridExtent(names, null, upper, false), CELL_ORIGIN, gridToCRS, crs);
        spatioTemporalCRS    = crsFactory.createCompoundCRS(properties(crs, "time"), crs, database.temporalCRS);
        GridExtent extent = spatialGeometry.getExtent();
        spatioTemporalExtent = extent.insert(extent.getDimension(), DimensionNameType.TIME, 0, (temporalAxis != null) ? temporalAxis.count : 1, false);
        this.approximate     = approximate;
        this.temporalAxis    = temporalAxis;
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
        for (int i=0; i<AFFINE_DIMENSION; i++) {
            final CoordinateSystemAxis axis = cs.getAxis(i);
            if (RangeMeaning.WRAPAROUND.equals(axis.getRangeMeaning())) {
                final double min, max;
                switch (i) {
                    case 0: min = bounds.getMinX(); max = bounds.getMaxX(); break;
                    case 1: min = bounds.getMinY(); max = bounds.getMaxY(); break;
                    default: throw new AssertionError(i);
                }
                /*
                 * We want to test if (min >= 0 && max >= axis.getMaximumValue()).
                 * But the minimal coordinate is not always exactly on zero for various reasons.
                 * For example because of rounding errors we sometime have values around -1E-14.
                 * Instead, we test which convention has the less length outside its domain.
                 */
                final double axisMin = axis.getMinimumValue();
                final double axisMax = axis.getMaximumValue();
                final double out180  = Math.max(max - axisMax, 0) + Math.max(axisMin - min, 0);
                final double out360  = Math.max(max - (axisMax - axisMin), 0) - Math.min(min, 0);
                if (out180 > out360) return true;
            }
        }
        return false;
    }

    /**
     * Returns the grid geometry for the given time range.
     * This is used for computing the grid geometry of a single {@link GridCoverageEntry}.
     *
     * @param  startTime  the grid coverage start time (inclusive), or {@code null} if none.
     * @param  endTime    the grid coverage end time (exclusive), or {@code null} if none.
     */
    final GridGeometry getGridGeometry(final Instant startTime, final Instant endTime) throws TransformException {
        if (startTime == null || endTime == null) {
            return spatialGeometry;
        }
        MathTransform gridToCRS = spatialGeometry.getGridToCRS(CELL_ORIGIN);
        final double tMin = TEMPORAL_CRS.toValue(startTime);
        final MathTransform gridToTemporal;
        if (temporalAxis == null) {
            final double tMax = TEMPORAL_CRS.toValue(endTime);
            gridToTemporal = MathTransforms.linear(tMax - tMin, tMin);
        } else {
            // Time offsets are stored in days.
            gridToTemporal = MathTransforms.concatenate(temporalAxis.gridToCRS, MathTransforms.linear(1, tMin));
        }
        gridToCRS = MathTransforms.compound(gridToCRS, gridToTemporal);
        return new GridGeometry(spatioTemporalExtent, CELL_ORIGIN, gridToCRS, spatioTemporalCRS);
    }

    /**
     * Returns the grid geometry for the given enumerated dates.
     * This method is used for computing the grid geometry of a stack comprising more than one {@link GridCoverageEntry}.
     *
     * @param  timestamps  the timestamps in units defined by {@link GridGeometryEntry#TEMPORAL_CRS}.
     */
    final GridGeometry getGridGeometry(final double[] timestamps) throws TransformException {
        MathTransform tr;
        switch (timestamps.length) {
            case 0:  return spatialGeometry;
            case 1:  tr = MathTransforms.linear(1.0, timestamps[0]); break;     // Assume a temporal resolution of 1 day.
            default: tr = MathTransforms.interpolate(null, timestamps); break;
        }
        tr = PixelTranslation.translate(tr, PixelInCell.CELL_CENTER, CELL_ORIGIN);
        MathTransform gridToCRS = spatialGeometry.getGridToCRS(CELL_ORIGIN);
        gridToCRS = MathTransforms.compound(gridToCRS, tr);
        GridExtent extent = spatialGeometry.getExtent();
        extent = extent.insert(extent.getDimension(), DimensionNameType.TIME, 0, timestamps.length, false);
        return new GridGeometry(extent, CELL_ORIGIN, gridToCRS, spatioTemporalCRS);
    }
}
