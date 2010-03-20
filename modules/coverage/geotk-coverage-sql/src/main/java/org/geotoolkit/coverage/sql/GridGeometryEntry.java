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

import java.util.Arrays;
import java.awt.Shape;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RectangularShape;
import java.awt.geom.AffineTransform;
import static java.lang.Math.abs;

import org.opengis.coverage.grid.GridEnvelope;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.TransformException;
import org.opengis.referencing.operation.MathTransformFactory;
import org.opengis.referencing.operation.MathTransform1D;
import org.opengis.metadata.extent.GeographicBoundingBox;

import org.geotoolkit.util.Utilities;
import org.geotoolkit.internal.sql.table.Entry;
import org.geotoolkit.coverage.grid.GeneralGridGeometry;
import org.geotoolkit.metadata.iso.extent.DefaultGeographicBoundingBox;
import org.geotoolkit.referencing.crs.DefaultTemporalCRS;
import org.geotoolkit.referencing.operation.transform.AffineTransform2D;
import org.geotoolkit.referencing.operation.matrix.MatrixFactory;
import org.geotoolkit.referencing.operation.matrix.XMatrix;


/**
 * Implementation of a four-dimensional grid geometry. This class assumes that the two first
 * axis are always for the horizontal component of the CRS (no matter if it is (x,y) or (y,x))
 * and that the vertical component, if any, is the third axis. The time dimension is the last
 * axis.
 * <p>
 * This implementation allows direct access to the field for convenience and efficiency, but
 * those fields should never be modified. We allow this unsafe practice because this class
 * is not public.
 *
 * @author Martin Desruisseaux (IRD, Geomatys)
 * @author Sam Hiatt
 * @version 3.10
 *
 * @since 3.10 (derived from Seagis)
 * @module
 */
final class GridGeometryEntry extends Entry {
    /**
     * For cross-version compatibility.
     */
    private static final long serialVersionUID = -3529884841649813534L;

    /**
     * The spatial reference systems. Typically many grid geometries will share the same
     * instance of {@link SpatialRefSysEntry}.
     */
    private final SpatialRefSysEntry srsEntry;

    /**
     * The immutable grid geometry, which may be 2D, 3D or 4D. The coordinate reference system is
     * the one declared in the {@link GridCoverageTable} for that entry. The envelope must include
     * the vertical range if any. If there is a temporal dimension, then the temporal extent must be
     * presents as well but may be invalid (the exact value will be set on an coverage-by-coverage
     * basis).
     */
    final GeneralGridGeometry geometry;

    /**
     * The "grid to CRS" affine transform for the horizontal part. The vertical
     * transform is not included because the {@link #verticalOrdinates} may not
     * be regular.
     */
    final AffineTransform2D gridToCRS;

    /**
     * A shape describing the coverage outline in the database CRS (usually WGS 84). This is the
     * value computed by Geotk, not the PostGIS object declared in the database, in order to make
     * sure that coordinate transformations are applied using the same algorithm, i.e. the Geotk
     * algortihm instead than the Proj4 algorithms used by PostGIS. This is necessary in case the
     * {@code "spatial_ref_sys"} table content is inconsistent with the EPSG database used by Geotk.
     *
     * @see #getHorizontalEnvelope()
     */
    private final Shape standardEnvelope;

    /**
     * The minimal and maximal <var>z</var> values in the vertical CRS of the database,
     * or {@link Double#NaN} if none.
     */
    final double standardMinZ, standardMaxZ;

    /**
     * The vertical ordinates in the vertical CRS of the coverage, or {@code null}.
     */
    private final double[] verticalOrdinates;

    /**
     * Creates an entry from the given grid geometry.
     *
     * @param identifier        The identifier of this grid geometry.
     * @param gridToCRS         The grid to CRS affine transform.
     * @param verticalOrdinates The vertical ordinate values, or {@code null} if none.
     */
    GridGeometryEntry(final Comparable<?>      identifier,
                      final Dimension          size,
                      final SpatialRefSysEntry srsEntry,
                      final AffineTransform2D  gridToCRS,
                      final double[] verticalOrdinates,
                      final MathTransformFactory mtFactory)
            throws FactoryException, TransformException
    {
        super(identifier, null);
        this.srsEntry          = srsEntry;
        this.gridToCRS         = gridToCRS;
        this.verticalOrdinates = verticalOrdinates;
        if (verticalOrdinates != null) {
            if (verticalOrdinates.length > Short.MAX_VALUE - 1) {
                throw new IllegalArgumentException(); // See 'indexOfNearestAltitude' for this limitation.
            }
        }
        geometry = srsEntry.createGridGeometry(size, gridToCRS, verticalOrdinates, mtFactory);
        standardEnvelope = srsEntry.toDatabaseHorizontalCRS().createTransformedShape(getHorizontalEnvelope());
        double min = Double.NaN, max = Double.NaN;
        if (verticalOrdinates != null && verticalOrdinates.length != 0) {
            min = verticalOrdinates[0];
            max = verticalOrdinates[verticalOrdinates.length - 1];
            final MathTransform1D tr = srsEntry.toDatabaseVerticalCRS();
            if (tr != null) {
                min = tr.transform(min);
                max = tr.transform(max);
                if (max < min) {
                    final double t = max;
                    max = min;
                    min = max;
                }
            }
        }
        standardMinZ = min;
        standardMaxZ = max;
    }

    /**
     * Returns the SRID of the horizontal component of the CRS.
     * This is a primary key in the {@code "spatial_ref_sys"} table.
     */
    public int getHorizontalSRID() {
        return srsEntry.horizontalSRID;
    }

    /**
     * Returns the SRID of the vertical component of the CRS.
     */
    public int getVerticalSRID() {
        return srsEntry.verticalSRID;
    }

    /**
     * Returns the temporal component of the CRS.
     */
    public DefaultTemporalCRS getTemporalCRS() {
        return srsEntry.temporalCRS;
    }

    /**
     * Returns the coordinate reference system. May be up to 4-dimensional.
     *
     * @param includeTime {@code true} if the CRS should include the time component,
     *        or {@code false} for a spatial-only CRS.
     */
    public CoordinateReferenceSystem getSpatioTemporalCRS(final boolean includeTime) {
        final CoordinateReferenceSystem crs = srsEntry.getSpatioTemporalCRS(includeTime);
        assert !includeTime || crs.equals(geometry.getCoordinateReferenceSystem()) : crs;
        return crs;
    }

    /**
     * Returns a matrix for the <cite>grid to CRS</cite> affine transform.  The coefficients for
     * the horizontal and vertical (if any) dimensions are initialized. But the coefficients for
     * the temporal dimension (if any) must be initialized by the caller. The temporal dimension
     * is assumed the last one.
     *
     * @param clip        The source region to be read in pixel coordinates.
     * @param subsampling The subsampling which is going to be applies during the reading process.
     * @param dimension   The number of dimensions for the source and target CRS.
     * @param zIndice     The 1-based indice of the <var>z</var> value, or 0 if none.
     */
    final XMatrix getGridToCRS(final Rectangle clip, final Dimension subsampling,
                               final int dimension, int zIndice)
    {
        final XMatrix matrix = MatrixFactory.create(dimension + 1);
        AffineTransform tr = gridToCRS;
        if (clip.x != 0 || clip.y != 0 || subsampling.width != 1 || subsampling.height != 1) {
            tr = new AffineTransform(tr);
            tr.translate(clip.x, clip.y);
            tr.scale(subsampling.width, subsampling.height);
        }
        SpatialRefSysEntry.copy(tr, matrix);
        if (verticalOrdinates != null) {
            final int imax = verticalOrdinates.length - 1;
            if (--zIndice > imax) {
                zIndice = imax;
            }
            if (zIndice >= 0) {
                final int zDimension = srsEntry.zDimension();
                if (zDimension >= 0) {
                    final double z = verticalOrdinates[zIndice];
                    final double before = (zIndice != 0)    ? z - verticalOrdinates[zIndice - 1] : 0;
                    final double after  = (zIndice != imax) ? verticalOrdinates[zIndice + 1] - z : 0;
                    double interval = (before != 0 && abs(before) <= abs(after)) ? before : after;
                    matrix.setElement(zDimension, zDimension, interval);
                    matrix.setElement(zDimension, dimension, z - 0.5*interval);
                }
            }
        }
        return matrix;
    }

    /**
     * Returns {@code true} if the geographic bounding box described by this entry is empty.
     */
    public boolean isEmpty() {
        RectangularShape bounds;
        if (standardEnvelope instanceof RectangularShape) {
            bounds = (RectangularShape) standardEnvelope;
        } else {
            bounds = standardEnvelope.getBounds2D();
        }
        return bounds.isEmpty();
    }

    /**
     * Convenience method returning the two first dimension of the grid range.
     */
    public Dimension getSize() {
        final GridEnvelope gridRange = geometry.getGridRange();
        return new Dimension(gridRange.getSpan(0), gridRange.getSpan(1));
    }

    /**
     * Convenience method returning the two first dimension of the grid range.
     */
    public Rectangle getBounds() {
        final GridEnvelope gridRange = geometry.getGridRange();
        return new Rectangle(gridRange.getLow (0), gridRange.getLow (1),
                             gridRange.getSpan(0), gridRange.getSpan(1));
    }

    /**
     * Returns the geographic bounding box. This method transforms the {@link #standardEnvelope}
     * rather than the coverage envelope, because the "standard" envelope is typically already
     * in WGS 84.
     *
     * @throws TransformException If the envelope can not be converted to WGS84.
     */
    public GeographicBoundingBox getGeographicBoundingBox() throws TransformException {
        return new DefaultGeographicBoundingBox(standardEnvelope.getBounds2D(), srsEntry.getDatabaseCRS());
    }

    /**
     * Returns the coverage shape in coverage CRS (not database CRS). The returned shape is likely
     * (but not garanteed) to be an instance of {@link Rectangle2D}. It can be freely modified.
     */
    public Shape getHorizontalEnvelope() {
        final GridEnvelope gridRange = geometry.getGridRange();
        Shape shape = new Rectangle2D.Double(
                gridRange.getLow (0), gridRange.getLow (1),
                gridRange.getSpan(0), gridRange.getSpan(1));
        shape = AffineTransform2D.transform(gridToCRS, shape, true);
        return shape;
    }

    /**
     * Returns the vertical ordinate values, or {@code null} if none. If non-null,
     * then the array length must be equals to the {@code gridRange.getLength(2)}.
     */
    public double[] getVerticalOrdinates() {
        if (verticalOrdinates != null) {
            assert geometry.getGridRange().getSpan(2) == verticalOrdinates.length : geometry;
            return verticalOrdinates.clone();
        }
        return null;
    }

    /**
     * Returns the 1-based index of the closest altitude. If this entry contains no altitude,
     * or if the specified <var>z</var> is not a finite number, then this method returns 0.
     *
     * @param z The value to search for.
     * @return  The 1-based altitude index, or {@code 0} if none.
     */
    final short indexOfNearestAltitude(final double z) {
        short index = 0;
        if (!Double.isNaN(z) && !Double.isInfinite(z)) {
            double delta = Double.POSITIVE_INFINITY;
            if (verticalOrdinates != null) {
                for (int i=0; i<verticalOrdinates.length; i++) {
                    final double d = abs(verticalOrdinates[i] - z);
                    if (d < delta) {
                        delta = d;
                        index = (short) (i + 1); // Array length has been checked at construction time.
                    }
                }
            }
        }
        return index;
    }

    /**
     * Returns {@code true} if the specified entry has the same envelope than this entry,
     * regardless the grid size.
     */
    final boolean sameEnvelope(final GridGeometryEntry that) {
        return Utilities.equals(this.geometry.getEnvelope(), that.geometry.getEnvelope()) &&
                  Arrays.equals(this.verticalOrdinates,      that.verticalOrdinates);
    }

    /**
     * Compares this grid geometry with the specified object for equality.
     */
    @Override
    public boolean equals(final Object object) {
        if (object == this) {
            return true;
        }
        if (super.equals(object)) {
            final GridGeometryEntry that = (GridGeometryEntry) object;
            return Utilities.equals(this.srsEntry,          that.srsEntry) &&
                   Utilities.equals(this.geometry,          that.geometry) &&
                      Arrays.equals(this.verticalOrdinates, that.verticalOrdinates);
        }
        return false;
    }
}
