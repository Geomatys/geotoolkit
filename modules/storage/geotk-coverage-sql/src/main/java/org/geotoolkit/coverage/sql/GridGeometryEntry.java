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

import org.opengis.geometry.Envelope;
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
import org.apache.sis.geometry.GeneralEnvelope;
import org.apache.sis.internal.metadata.AxisDirections;
import org.apache.sis.referencing.CRS;
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
 * <p>This implementation allows direct accesses to the fields for convenience and efficiency,
 * but those fields should never be modified. We allow this unsafe practice because this class
 * is not public.</p>
 *
 * @author Martin Desruisseaux (IRD, Geomatys)
 * @author Sam Hiatt
 */
final class GridGeometryEntry {
    /**
     * Number of dimensions of the "grid to CRS" part to be represented as an affine transform
     * in the "GridGeometries" table.
     */
    static final int AFFINE_DIMENSION = 2;

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
     * Creates an entry from the given grid geometry. This constructor does not clone
     * the object given in argument. Consequently, those object shall not be modified
     * after {@code GridGeometryEntry} construction.
     *
     * @param width         number of cells in the first dimension.
     * @param height        number of cells in the second dimension.
     * @param affine        the grid to CRS affine transform for the two first dimensions.
     * @param approximate   whether the "grid to CRS" transform is only an approximation of non-linear transform.
     * @param crs           the coordinate reference system for the two first dimensions.
     * @param axes          additional dimensions, or {@code null} if none.
     * @param extraDimName  a label for the additional dimensions, or {@code null} if none.
     */
    GridGeometryEntry(final long width, final long height, final AffineTransform2D affine,
                      final boolean approximate, CoordinateReferenceSystem crs,
                      final AdditionalAxisEntry[] axes, final String extraDimName,
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
        int dim = AFFINE_DIMENSION;
        if (axes != null) {
            dim += axes.length;
        }
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
                final AdditionalAxisEntry axis = axes[i];
                names  [AFFINE_DIMENSION + i] = axis.type();
                upper  [AFFINE_DIMENSION + i] = axis.count;
                minimum[AFFINE_DIMENSION + i] = axis.standardMin;
                maximum[AFFINE_DIMENSION + i] = axis.standardMax;
                components[            1 + i] = axis.crs;
                MathTransform tr = axis.gridToCRS;
                tr = database.mtFactory.createPassThroughTransform(AFFINE_DIMENSION + i, tr, 0);
                gridToCRS = database.mtFactory.createConcatenatedTransform(gridToCRS, tr);
            }
            crs = database.crsFactory.createCompoundCRS(properties(crs, extraDimName), components);
        }
        if (ProductCoverage.HACK) {
            java.util.Arrays.fill(upper, 2, upper.length, 1);
        }
        spatialGeometry      = new GridGeometry(new GridExtent(names, null, upper, false), PixelInCell.CELL_CORNER, gridToCRS, crs);
        standardEnvelope     = new ImmutableEnvelope(minimum, maximum, null);
        spatioTemporalCRS    = database.crsFactory.createCompoundCRS(properties(crs, "time"), crs, database.temporalCRS);
        spatioTemporalExtent = spatialGeometry.getExtent().append(DimensionNameType.TIME, 0, 0, true);
        this.approximate     = approximate;
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
     * Returns the grid geometry for the given time range.
     */
    final GridGeometry getGridGeometry(final Instant startTime, final Instant endTime) throws TransformException {
        if (startTime == null || endTime == null) {
            return spatialGeometry;
        }
        MathTransform gridToCRS = spatialGeometry.getGridToCRS(PixelInCell.CELL_CORNER);
        final double tMin = TEMPORAL_CRS.toValue(startTime);
        final double tMax = TEMPORAL_CRS.toValue(endTime);
        gridToCRS = MathTransforms.compound(gridToCRS, MathTransforms.linear(tMax - tMin, tMin));
        return new GridGeometry(spatioTemporalExtent, PixelInCell.CELL_CORNER, gridToCRS, spatioTemporalCRS);
    }

    /**
     * Returns the grid geometry for the given enumerated dates.
     *
     * @param  gridExtent  number of cell values in the temporal dimension.
     * @param  timestamps  the conversion from [0 … n] grid cells to timestamp in the {@link #TEMPORAL_CRS} axis.
     */
    final GridGeometry getGridGeometry(final int gridExtent, final MathTransform timestamps) throws TransformException {
        MathTransform gridToCRS = spatialGeometry.getGridToCRS(PixelInCell.CELL_CORNER);
        gridToCRS = MathTransforms.compound(gridToCRS, timestamps);
        GridExtent extent = spatialGeometry.getExtent().append(DimensionNameType.TIME, 0, gridExtent, false);
        return new GridGeometry(extent, PixelInCell.CELL_CORNER, gridToCRS, spatioTemporalCRS);
    }

    /**
     * Returns the indices of pixel to read for the given region of interest.
     * The region of interest can be specified in any CRS.
     * If the given envelope has a time component, it must be the last dimension.
     *
     * @param  aoi  the region of interest.
     * @return indices of pixels to read.
     */
    final GridExtent extent(Envelope aoi) throws TransformException {
        final CoordinateReferenceSystem crs = aoi.getCoordinateReferenceSystem();
        if (crs != null) {
            final CoordinateSystem cs = crs.getCoordinateSystem();
            final int last = cs.getDimension() - 1;
            if (AxisDirections.isTemporal(cs.getAxis(last).getDirection())) {
                final GeneralEnvelope env = GeneralEnvelope.castOrCopy(aoi).subEnvelope(0, last);
                env.setCoordinateReferenceSystem(CRS.getComponentAt(crs, 0, last));
                aoi = env;
            }
        }
        return spatialGeometry.getExtent(aoi);
    }
}
