/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2005-2012, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2007-2012, Geomatys
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
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.awt.Dimension;
import java.awt.geom.Rectangle2D;
import java.awt.geom.AffineTransform;
import javax.measure.converter.ConversionException;

import org.opengis.util.FactoryException;
import org.opengis.coverage.grid.GridEnvelope;
import org.opengis.referencing.cs.*;
import org.opengis.referencing.crs.*;
import org.opengis.referencing.datum.PixelInCell;
import org.opengis.referencing.operation.Matrix;
import org.opengis.referencing.operation.MathTransform;
import org.opengis.referencing.operation.MathTransform1D;
import org.opengis.referencing.operation.MathTransform2D;
import org.opengis.referencing.operation.MathTransformFactory;
import org.opengis.referencing.operation.OperationNotFoundException;

import org.apache.sis.util.ArraysExt;
import org.geotoolkit.util.Utilities;
import org.geotoolkit.util.logging.Logging;
import org.geotoolkit.coverage.grid.GeneralGridEnvelope;
import org.geotoolkit.coverage.grid.GeneralGridGeometry;
import org.geotoolkit.internal.referencing.AxisDirections;
import org.geotoolkit.internal.sql.table.SpatialDatabase;
import org.geotoolkit.referencing.crs.DefaultCompoundCRS;
import org.geotoolkit.referencing.crs.DefaultTemporalCRS;
import org.geotoolkit.referencing.crs.DefaultGeographicCRS;
import org.geotoolkit.referencing.operation.matrix.Matrices;
import org.geotoolkit.referencing.operation.matrix.XAffineTransform;
import org.geotoolkit.referencing.cs.AbstractCS;
import org.geotoolkit.referencing.cs.AxisRangeType;
import org.geotoolkit.referencing.CRS;
import org.geotoolkit.referencing.IdentifiedObjects;
import org.geotoolkit.referencing.cs.DefaultEllipsoidalCS;
import org.geotoolkit.resources.Errors;


/**
 * The horizontal, vertical and temporal components of a {@link GridGeometryEntry} CRS.
 * The CRS are built from the SRID declared in the {@code "GridGeometries"} table, linked
 * to the values declared in the PostGIS {@code "spatial_ref_sys"} table.
 *
 * @author Martin Desruisseaux (IRD, Geomatys)
 * @version 3.20
 *
 * @since 3.10 (derived from Seagis)
 * @module
 */
final class SpatialRefSysEntry {
    /**
     * Small tolerance factor for comparisons of floating point values.
     * An angular value of 1E-8° is approximatively 1 millimetre on Earth.
     */
    static final double EPS = 1E-8;

    /**
     * The horizontal and vertical SRID declared in the database.
     */
    final int horizontalSRID, verticalSRID;

    /**
     * The horizontal CRS. This is created by {@link #createSpatioTemporalCRS}.
     *
     * @see #horizontalSRID
     */
    private SingleCRS horizontalCRS;

    /**
     * The vertical CRS, or {@code null} if none.
     * This is created by {@link #createSpatioTemporalCRS}.
     *
     * @see #verticalSRID
     */
    private VerticalCRS verticalCRS;

    /**
     * The temporal CRS, or {@code null} if none.
     */
    final DefaultTemporalCRS temporalCRS;

    /**
     * The {@link #spatioTemporalCRS} without the temporal component.
     * This is created by {@link #createSpatioTemporalCRS}.
     *
     * @see #getSpatioTemporalCRS(boolean)
     */
    private CoordinateReferenceSystem spatialCRS;

    /**
     * The coordinate reference system made of the combination of all the above.
     * This is created by {@link #createSpatioTemporalCRS}.
     *
     * @see #getSpatioTemporalCRS(boolean)
     */
    private CoordinateReferenceSystem spatioTemporalCRS;

    /**
     * The database horizontal CRS. This is a copy of the {@link SpatialDatabase#horizontalCRS}
     * reference and is initialized by {@link #createSpatioTemporalCRS}.
     *
     * @see #getDatabaseCRS()
     */
    private SingleCRS databaseCRS;

    /**
     * Whatever default grid range computation should be performed on transforms relative to pixel
     * center or relative to pixel corner. This is a copy of the {@link SpatialDatabase#pixelInCell}
     * reference and is initialized by {@link #createSpatioTemporalCRS}.
     */
    private PixelInCell pixelInCell;

    /**
     * The transform to the database horizontal CRS. The database uses a single CRS for
     * indexing the whole {@code GridGeometries} table, while individual records may use
     * different CRS.
     * <p>
     * This is created by {@link #createSpatioTemporalCRS}.
     *
     * @see #toDatabaseHorizontalCRS()
     */
    private MathTransform2D toDatabaseHorizontalCRS;

    /**
     * The transform to the database vertical CRS.
     * This is created by {@link #createSpatioTemporalCRS}.
     *
     * @see #toDatabaseVerticalCRS()
     */
    private MathTransform1D toDatabaseVerticalCRS;

    /**
     * The coordinate system of the {@link #horizontalCRS} using a positive range of longitude
     * values ([0…360]°) instead than the default [-180 … 180]° range. This field is non-null
     * only if the horizontal CRS is geographic, not projected.
     *
     * @since 3.20
     */
    private CoordinateSystem shiftedCS;

    /**
     * Constructs a new entry for the given SRID.
     *
     * @param horizontalSRID The SRID of the horizontal CRS, or {@code 0} if none.
     * @param verticalSRID   The SRID of the vertical CRS, or {@code 0} if none.
     * @param temporalCRS    The temporal CRS, or {@code null} if none.
     */
    SpatialRefSysEntry(final int horizontalSRID, final int verticalSRID, final DefaultTemporalCRS temporalCRS) {
        this.horizontalSRID = horizontalSRID;
        this.verticalSRID   = verticalSRID;
        this.temporalCRS    = temporalCRS;
    }

    /**
     * If all CRS are initialized, returns 0. Otherwise returns the code of the first
     * uninitialized CRS: 1 for horizontal, 2 for vertical, 3 for temporal or 4 for the
     * 4D CRS. This method is used only for checking error conditions.
     */
    final int uninitialized() {
        if (horizontalCRS     == null && horizontalSRID != 0) return 1;
        if (verticalCRS       == null && verticalSRID   != 0) return 2;
        if (temporalCRS       == null)                        return 3;
        if (spatioTemporalCRS == null)                        return 4;
        return 0;
    }

    /**
     * Creates the horizontal and vertical CRS from the factory bundled in the given table.
     * The CRS are fetched only after the {@code SpatialRefSysEntry} creation (instead than
     * at creation time) in order to have a chance to search for an existing entry in the
     * cache before to create the CRS.
     *
     * @param  database The database.
     * @throws FactoryException if an error occurred while creating the CRS.
     */
    final void createSpatioTemporalCRS(final SpatialDatabase database) throws FactoryException {
        assert uninitialized() != 0 : this;
        databaseCRS = database.horizontalCRS;
        pixelInCell = database.pixelInCell;
        /*
         * Get the CRS components (Horizontal, Vertical, Temporal) from the PostGIS SRID,
         * except the temporal component which was explicitly given at construction time.
         */
        final CRSAuthorityFactory factory = database.getCRSAuthorityFactory();
        if (horizontalSRID != 0) {
            final CoordinateReferenceSystem crs =
                    factory.createCoordinateReferenceSystem(String.valueOf(horizontalSRID));
            try {
                horizontalCRS = (SingleCRS) crs;
            } catch (ClassCastException e) {
                throw new FactoryException(Errors.format(
                        Errors.Keys.ILLEGAL_CLASS_2, crs.getClass(), SingleCRS.class), e);
            }
        }
        if (verticalSRID != 0) {
            verticalCRS = factory.createVerticalCRS(String.valueOf(verticalSRID));
        }
        /*
         * Assemble the components together in a spatio-temporal Compound CRS.
         */
        int count = 0;
        SingleCRS[] elements = new SingleCRS[3];
        if (horizontalCRS != null) elements[count++] = horizontalCRS;
        if (verticalCRS   != null) elements[count++] = verticalCRS;
        if (temporalCRS   != null) elements[count++] = temporalCRS;
        switch (count) {
            case 0: {
                throw new FactoryException(Errors.format(Errors.Keys.UNSPECIFIED_CRS));
            }
            case 1: {
                spatioTemporalCRS = elements[0];
                if (spatioTemporalCRS != temporalCRS) {
                    spatialCRS = spatioTemporalCRS;
                }
                break;
            }
            default: {
                final SingleCRS headCRS = elements[0];
                elements = ArraysExt.resize(elements, count);
                Map<String,?> properties = IdentifiedObjects.getProperties(headCRS);
                if (verticalCRS != null) {
                    String name = headCRS.getName().getCode();
                    name = name + " + " + verticalCRS.getName().getCode();
                    final Map<String,Object> copy = new HashMap<>(properties);
                    copy.put(CoordinateReferenceSystem.NAME_KEY, name);
                    properties = copy;
                }
                final CRSFactory crsFactory = database.getCRSFactory();
                spatioTemporalCRS = crsFactory.createCompoundCRS(properties, elements);
                if (temporalCRS == null) {
                    spatialCRS = spatioTemporalCRS;
                } else {
                    if (--count == 1) {
                        spatialCRS = elements[0];
                    } else {
                        elements = ArraysExt.resize(elements, count);
                        spatialCRS = crsFactory.createCompoundCRS(properties, elements);
                    }
                }
            }
        }
        assert CRS.getHorizontalCRS(spatioTemporalCRS) == CRS.getHorizontalCRS(spatialCRS);
        assert CRS.getVerticalCRS  (spatioTemporalCRS) == CRS.getVerticalCRS  (spatialCRS);
        /*
         * Get the MathTransforms from coverage CRS to database CRS.
         */
        SingleCRS sourceCRS = horizontalCRS;
        SingleCRS targetCRS = database.horizontalCRS;
        if (sourceCRS != null && targetCRS != null) {
            toDatabaseHorizontalCRS = (MathTransform2D) CRS.findMathTransform(sourceCRS, targetCRS, true);
        }
        sourceCRS = verticalCRS;
        targetCRS = database.verticalCRS;
        if (sourceCRS != null && targetCRS != null) {
            MathTransform tr;
            try {
                tr = CRS.findMathTransform(sourceCRS, targetCRS, true);
            } catch (OperationNotFoundException e) {
                final Matrix matrix;
                try {
                    matrix = AbstractCS.swapAndScaleAxis(sourceCRS.getCoordinateSystem(),
                                                         targetCRS.getCoordinateSystem());
                } catch (ConversionException | IllegalArgumentException ignore) {
                    throw e;
                }
                tr = database.getMathTransformFactory().createAffineTransform(matrix);
                /*
                 * Be lenient with vertical transformations,  because many of them are not yet
                 * implemented (e.g. "mean sea level" to "ellipsoidal"). Log a warning without
                 * stack trace in order to not scare the user too much.  Use GridGeometryTable
                 * as the source class since SpatialRefSysEntry is too low level.
                 */
                Logging.log(GridGeometryTable.class, "createEntry",
                        new LogRecord(Level.WARNING, e.getLocalizedMessage()));
            }
            toDatabaseVerticalCRS = (MathTransform1D) tr;
        }
        /*
         * At this point, all CRS have been initialized. Now find the longitude dimension
         * and instantiate a coordinate system using a [0…360]° range of longitude values.
         */
        if (horizontalCRS instanceof GeographicCRS) {
            final EllipsoidalCS cs = ((GeographicCRS) horizontalCRS).getCoordinateSystem();
            final int i = AxisDirections.indexOf(cs, AxisDirection.EAST);
            if (i >= 0) {
                final DefaultEllipsoidalCS geotk = DefaultEllipsoidalCS.castOrCopy(cs);
                shiftedCS = geotk.shiftAxisRange(AxisRangeType.POSITIVE_LONGITUDE);
                if (shiftedCS == geotk) {
                    shiftedCS = null;
                }
            }
        }
    }

    /**
     * Returns the coordinate reference system, which may be up to 4-dimensional.
     * The {@link #createSpatioTemporalCRS} method must have been invoked before this method.
     *
     * @param includeTime {@code true} if the CRS should include the time component,
     *        or {@code false} for a spatial-only CRS.
     * @param needsLongitudeShift {@code true} if the grid geometry needs longitude
     *        values in the [0…360]° range instead than the default [-180 … 180]° range.
     */
    public CoordinateReferenceSystem getSpatioTemporalCRS(final boolean includeTime,
            final boolean needsLongitudeShift)
    {
        assert uninitialized() == 0 : this;
        CoordinateReferenceSystem crs = includeTime ? spatioTemporalCRS : spatialCRS;
        if (needsLongitudeShift) {
            if (crs instanceof GeographicCRS) {
                final DefaultGeographicCRS geotk = DefaultGeographicCRS.castOrCopy((GeographicCRS) crs);
                crs = geotk.shiftAxisRange(AxisRangeType.POSITIVE_LONGITUDE);
                if (!includeTime) spatialCRS = geotk;
                else       spatioTemporalCRS = geotk;
            } else if (crs instanceof CompoundCRS) {
                final DefaultCompoundCRS geotk = DefaultCompoundCRS.castOrCopy((CompoundCRS) crs);
                crs = geotk.shiftAxisRange(AxisRangeType.POSITIVE_LONGITUDE);
                if (!includeTime) spatialCRS = geotk;
                else       spatioTemporalCRS = geotk;
            }
        }
        return crs;
    }

    /**
     * Returns the database horizontal CRS used in PostGIS geometry columns.
     */
    final SingleCRS getDatabaseCRS() {
        assert uninitialized() == 0 : this;
        return databaseCRS;
    }

    /**
     * Returns the transform from this entry horizontal CRS to the database horizontal CRS,
     * or {@code null} if none.
     */
    final MathTransform2D toDatabaseHorizontalCRS() {
        assert uninitialized() == 0 : this;
        return toDatabaseHorizontalCRS;
    }

    /**
     * Returns the transform from this entry vertical CRS to the database vertical CRS,
     * or {@code null} if none.
     */
    final MathTransform1D toDatabaseVerticalCRS() {
        assert uninitialized() == 0 : this;
        return toDatabaseVerticalCRS;
    }

    /**
     * Returns whatever default grid range computation should be performed on transforms
     * relative to pixel center or relative to pixel corner.
     */
    final PixelInCell getPixelInCell() {
        assert uninitialized() == 0 : this;
        return pixelInCell;
    }

    /**
     * Returns {@code true} if the given grid geometry seems to use a longitude values in
     * the [0…360]° range instead than the default [-180 … 180]° range.
     *
     * @since 3.20
     */
    final boolean needsLongitudeShift(final Dimension size, final AffineTransform gridToCRS) {
        assert uninitialized() == 0 : this;
        final CoordinateSystem shiftedCS = this.shiftedCS; // Protect from changes.
        if (shiftedCS != null) {
            Rectangle2D bounds = new Rectangle2D.Double(0, 0, size.width, size.height);
            bounds = XAffineTransform.transform(gridToCRS, bounds, bounds);
            final CoordinateSystem standardCS = horizontalCRS.getCoordinateSystem();
            for (int i=0; i<=1; i++) {
                final CoordinateSystemAxis standardAxis = standardCS.getAxis(i);
                final CoordinateSystemAxis shiftedAxis  = shiftedCS .getAxis(i);
                if (standardAxis != shiftedAxis) {
                    final double min, max;
                    switch (i) {
                        case 0: min = bounds.getMinX(); max = bounds.getMaxX(); break;
                        case 1: min = bounds.getMinY(); max = bounds.getMaxY(); break;
                        default: throw new AssertionError(i);
                    }
                    if (min+EPS >= shiftedAxis .getMinimumValue() &&  // Always >= 0 for shifted axis.
                        max+EPS >= standardAxis.getMaximumValue() &&
                        max-EPS <= shiftedAxis .getMaximumValue())
                    {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /**
     * Returns a grid geometry for the given horizontal size and transform, and the given vertical
     * ordinate values. The given factory is used for creating the <cite>grid to CRS</cite>
     * transform. Dimensions are handled as below:
     * <p>
     * <ul>
     *   <li>The horizontal dimension is setup using the {@code size} and {@code gridToCRS} parameters.</li>
     *   <li>The coefficients for the vertical axis assume that the vertical ordinates are evenly
     *       spaced. This is not always true; a special processing will be performed later by
     *       {@link GridCoverageEntry}.</li>
     *   <li>The time dimension, if any, is left to the identity transform.</li>
     * </ul>
     * <p>
     * The {@link #createSpatioTemporalCRS} method must have been invoked before this method.
     *
     * @param includeTime {@code true} if the CRS should include the time component,
     *        or {@code false} for a spatial-only CRS.
     * @param needsLongitudeShift {@code true} if the grid geometry needs longitude
     *        values in the [0…360]° range instead than the default [-180 … 180]° range.
     */
    final GeneralGridGeometry createGridGeometry(final Dimension size, final AffineTransform gridToCRS,
            final double[] altitudes, final MathTransformFactory mtFactory, final boolean includeTime,
            final boolean needsLongitudeShift) throws FactoryException
    {
        assert uninitialized() == 0 : this;
        final CoordinateReferenceSystem crs = getSpatioTemporalCRS(includeTime, needsLongitudeShift);
        final int dim = crs.getCoordinateSystem().getDimension();
        final int[] lower = new int[dim];
        final int[] upper = new int[dim];
        final Matrix matrix = Matrices.create(dim + 1);
        int verticalDim = 0;
        if (horizontalCRS != null) {
            copy(gridToCRS, matrix);
            verticalDim = horizontalCRS.getCoordinateSystem().getDimension();
        }
        if (verticalCRS != null && altitudes != null) {
            int n = altitudes.length;
            if (n != 0) {
                upper[verticalDim] = n;
                final double offset = altitudes[0];
                final double scale = (--n == 0) ? 0 : (altitudes[n] - offset) / n;
                matrix.setElement(verticalDim, verticalDim, scale); // May be negative.
                matrix.setElement(verticalDim, dim, offset);
            }
        }
        upper[0] = size.width;
        upper[1] = size.height;
        final GridEnvelope gridExtent = new GeneralGridEnvelope(lower, upper, false);
        return new GeneralGridGeometry(gridExtent, pixelInCell, mtFactory.createAffineTransform(matrix), crs);
    }

    /**
     * Copies the affine transform coefficients into the two first dimensions of the affine
     * transform represented by the target matrix.
     */
    static void copy(final AffineTransform source, final Matrix target) {
        final int dim = target.getNumCol() - 1;
        target.setElement(0, 0,   source.getScaleX());
        target.setElement(1, 1,   source.getScaleY());
        target.setElement(0, 1,   source.getShearX());
        target.setElement(1, 0,   source.getShearY());
        target.setElement(0, dim, source.getTranslateX());
        target.setElement(1, dim, source.getTranslateY());
    }

    /**
     * Returns the dimension for the <var>z</var> axis, or {@code -1} if none.
     * The {@code #createSpatialCRS} method must have been invoked once before
     * this method is invoked.
     */
    final int zDimension() {
        assert uninitialized() == 0 : this;
        return (verticalCRS == null) ? -1 : (horizontalCRS == null) ? 0 :
                horizontalCRS.getCoordinateSystem().getDimension();
    }

    /**
     * Returns a hash code value for this entry.  The value must be determined only from the
     * arguments given at construction time, i.e. it must be unchanged by call to any method
     * in this class.
     */
    @Override
    public int hashCode() {
        // 100003 is a prime number assumed large enough for avoiding overlapping between SRID.
        return Utilities.hash(temporalCRS, horizontalSRID + 100003*verticalSRID);
    }

    /**
     * Compares this entry with the specified object for equality. The comparison must include
     * only the arguments given at construction time, which are final. The other arguments
     * (computed only when first needed) must not be compared.
     */
    @Override
    public boolean equals(final Object object) {
        if (object instanceof SpatialRefSysEntry) {
            final SpatialRefSysEntry that = (SpatialRefSysEntry) object;
            return this.horizontalSRID == that.horizontalSRID &&
                   this.verticalSRID   == that.verticalSRID   &&
                   Objects.equals(this.temporalCRS, that.temporalCRS);
        }
        return false;
    }

    /**
     * Returns a string representation for debugging purpose.
     */
    @Override
    public String toString() {
        return getClass().getSimpleName() + "[h=" + horizontalSRID + ", v=" + verticalSRID + ']';
    }
}
