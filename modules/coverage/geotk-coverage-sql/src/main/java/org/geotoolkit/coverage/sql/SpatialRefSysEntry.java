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

import java.util.Map;
import java.util.HashMap;
import java.sql.SQLException;
import java.awt.Dimension;
import java.awt.geom.AffineTransform;

import org.opengis.coverage.grid.GridEnvelope;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.crs.CRSFactory;
import org.opengis.referencing.crs.SingleCRS;
import org.opengis.referencing.crs.VerticalCRS;
import org.opengis.referencing.crs.TemporalCRS;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.Matrix;
import org.opengis.referencing.operation.MathTransform2D;
import org.opengis.referencing.operation.MathTransformFactory;

import org.geotoolkit.util.Utilities;
import org.geotoolkit.referencing.CRS;
import org.geotoolkit.util.XArrays;
import org.geotoolkit.coverage.grid.GeneralGridEnvelope;
import org.geotoolkit.coverage.grid.GeneralGridGeometry;
import org.geotoolkit.referencing.AbstractIdentifiedObject;
import org.geotoolkit.referencing.operation.matrix.MatrixFactory;
import org.geotoolkit.resources.Errors;


/**
 * The horizontal, vertical and temporal components of a {@link GridGeometryEntry} CRS.
 * The CRS are built from the SRID declared in the {@code "GridGeometries"} table, linked
 * to the values declared in the PostGIS {@code "spatial_ref_sys"} table.
 *
 * @author Martin Desruisseaux (IRD, Geomatys)
 * @version 3.10
 *
 * @since 3.10 (derived from Seagis)
 * @module
 */
final class SpatialRefSysEntry {
    /**
     * The horizontal and vertical SRID declared in the database.
     */
    final int horizontalSRID, verticalSRID;

    /**
     * The horizontal CRS. This is created by {@link #createSpatialCRS}.
     *
     * @see #horizontalSRID
     */
    private SingleCRS horizontalCRS;

    /**
     * The vertical CRS, or {@code null} if none. This is created by {@link #createSpatialCRS}.
     *
     * @see #verticalSRID
     */
    private VerticalCRS verticalCRS;

    /**
     * The temporal CRS, or {@code null} if none.
     */
    private final TemporalCRS temporalCRS;

    /**
     * The {@link #spatioTemporalCRS} without the temporal component.
     * This is created by {@link #createSpatioTemporalCRS}.
     */
    private CoordinateReferenceSystem spatialCRS;

    /**
     * The coordinate reference system made of the combinaison of all the above.
     * This is created by {@link #createSpatioTemporalCRS}.
     */
    private CoordinateReferenceSystem spatioTemporalCRS;

    /**
     * The transform to the database horizontal CRS created when first needed. The database uses
     * a single CRS for indexing the whole {@code GridGeometries} table, while individual records
     * may use different CRS.
     *
     * @see #toDatabaseHorizontalCRS()
     */
    private transient MathTransform2D toDatabaseHorizontalCRS;

    /**
     * Constructs a new entry for the given SRID.
     *
     * @param horizontalSRID The SRID of the horizontal CRS, or {@code 0} if none.
     * @param verticalSRID   The SRID of the vertical CRS, or {@code 0} if none.
     * @param temporalCRS    The temporal CRS, or {@code null} if none.
     */
    SpatialRefSysEntry(final int horizontalSRID, final int verticalSRID, final TemporalCRS temporalCRS) {
        this.horizontalSRID = horizontalSRID;
        this.verticalSRID   = verticalSRID;
        this.temporalCRS    = temporalCRS;
    }

    /**
     * Creates the horizontal and vertical CRS from the factory bundled in the given table.
     *
     * @param  table The table to use for creating a CRS from a SRID.
     * @throws SQLException if an error occured while reading the database.
     * @throws FactoryException if an error occured while creating the CRS.
     * @throws ClassCastException if the CRS is not of expected type.
     */
//    final void createSpatialCRS(final GridGeometryTable table,
//            final boolean hasHorizontal, final boolean hasVertical)
//            throws SQLException, FactoryException, ClassCastException
//    {
//        if (hasHorizontal && horizontalSRID != 0) {
//            horizontalCRS = (SingleCRS) table.getSpatialReferenceSystem(horizontalSRID);
//        }
//        if (hasVertical && verticalSRID != 0) {
//            verticalCRS = (VerticalCRS) table.getSpatialReferenceSystem(verticalSRID);
//        }
//    }

    /**
     * Creates the compound CRS from the single CRS created by {@link #createSpatialCRS}.
     * The result is stored in {@link #spatioTemporalCRS}.
     *
     * @param  factory The factory to use for creating the compound CRS.
     * @throws FactoryException if an error occured while creating the CRS.
     */
    final void createSpatioTemporalCRS(final CRSFactory factory) throws FactoryException {
        int count = 0;
        SingleCRS[] elements = new SingleCRS[3];
        if (horizontalCRS != null) elements[count++] = horizontalCRS;
        if (verticalCRS   != null) elements[count++] = verticalCRS;
        if (temporalCRS   != null) elements[count++] = temporalCRS;
        if (count == 0) {
            throw new FactoryException(Errors.format(Errors.Keys.UNSPECIFIED_CRS));
        }
        spatioTemporalCRS = elements[0];
        if (count == 1) {
            if (spatioTemporalCRS != temporalCRS) {
                spatialCRS = spatioTemporalCRS;
            }
            return;
        }
        elements = XArrays.resize(elements, count);
        Map<String,?> properties = AbstractIdentifiedObject.getProperties(spatioTemporalCRS);
        if (verticalCRS != null) {
            String name = spatioTemporalCRS.getName().getCode();
            name = name + " + " + verticalCRS.getName().getCode();
            final Map<String,Object> copy = new HashMap<String,Object>(properties);
            copy.put(CoordinateReferenceSystem.NAME_KEY, name);
            properties = copy;
        }
        spatioTemporalCRS = factory.createCompoundCRS(properties, elements);
        if (temporalCRS == null) {
            spatialCRS = spatioTemporalCRS;
        } else {
            if (--count == 1) {
                spatialCRS = elements[0];
            } else {
                elements = XArrays.resize(elements, count);
                spatialCRS = factory.createCompoundCRS(properties, elements);
            }
        }
        assert CRS.getHorizontalCRS(spatioTemporalCRS) == CRS.getHorizontalCRS(spatialCRS);
        assert CRS.getVerticalCRS  (spatioTemporalCRS) == CRS.getVerticalCRS  (spatialCRS);
    }

    /**
     * Returns the coordinate reference system, which may be up to 4-dimensional.
     * The {@link #createSpatioTemporalCRS} method must have been invoked before this method.
     *
     * @param includeTime {@code true} if the CRS should include the time component,
     *        or {@code false} for a spatial-only CRS.
     */
    final CoordinateReferenceSystem getCoordinateReferenceSystem(final boolean includeTime) {
        return includeTime ? spatioTemporalCRS : spatialCRS;
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
     */
    final GeneralGridGeometry getGridGeometry(final Dimension size, final AffineTransform gridToCRS,
            final double[] altitudes, final MathTransformFactory mtFactory) throws FactoryException
    {
        final int dim = spatioTemporalCRS.getCoordinateSystem().getDimension();
        final int[] lower = new int[dim];
        final int[] upper = new int[dim];
        final Matrix matrix = MatrixFactory.create(dim + 1);
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
        final GridEnvelope gridRange = new GeneralGridEnvelope(lower, upper, false);
        return new GeneralGridGeometry(gridRange, SQLCoverageReader.PIXEL_IN_CELL,
                mtFactory.createAffineTransform(matrix), spatioTemporalCRS);
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
     * Returns the transform from this entry horizontal CRS to the database horizontal CRS.
     * The {@code #createSpatialCRS} method must have been invoked before this method.
     */
    final MathTransform2D toDatabaseHorizontalCRS() throws FactoryException {
        // No need to synhronize - this is not a big deal if the transform is searched twice.
        MathTransform2D tr = toDatabaseHorizontalCRS;
        if (tr == null) {
            tr = (MathTransform2D) CRS.findMathTransform(horizontalCRS, SQLCoverageReader.HORIZONTAL_CRS, true);
            toDatabaseHorizontalCRS = tr;
        }
        return tr;
    }

    /**
     * Returns the dimension for the <var>z</var> axis, or {@code -1} if none.
     * The {@code #createSpatialCRS} method must have been invoked once before
     * this method is invoked.
     */
    final int zDimension() {
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
        int code = horizontalSRID + 100003*verticalSRID;
        if (temporalCRS != null) {
            code ^= temporalCRS.hashCode();
        }
        return code;
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
                   Utilities.equals(this.temporalCRS, that.temporalCRS);
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
