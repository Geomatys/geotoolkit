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

import java.util.Arrays;
import java.util.Objects;
import java.awt.Shape;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.geom.Point2D;
import java.awt.geom.Dimension2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RectangularShape;
import static java.lang.Math.abs;

import org.opengis.util.FactoryException;
import org.opengis.geometry.Envelope;
import org.opengis.coverage.grid.GridEnvelope;
import org.opengis.referencing.datum.PixelInCell;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.TransformException;
import org.opengis.referencing.operation.MathTransformFactory;
import org.opengis.referencing.operation.MathTransform1D;
import org.opengis.referencing.operation.MathTransform2D;
import org.opengis.metadata.extent.GeographicBoundingBox;

import org.geotoolkit.internal.sql.table.DefaultEntry;
import org.geotoolkit.geometry.Envelopes;
import org.apache.sis.geometry.Envelope2D;
import org.apache.sis.geometry.AbstractEnvelope;
import org.geotoolkit.display.shape.DoubleDimension2D;
import org.geotoolkit.coverage.grid.GeneralGridGeometry;
import org.apache.sis.metadata.iso.extent.DefaultGeographicBoundingBox;
import org.geotoolkit.referencing.crs.DefaultTemporalCRS;
import org.apache.sis.internal.referencing.j2d.AffineTransform2D;
import org.geotoolkit.referencing.operation.matrix.Matrices;
import org.geotoolkit.referencing.operation.matrix.XAffineTransform;
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
 * @version 3.20
 *
 * @since 3.10 (derived from Seagis)
 * @module
 */
final class GridGeometryEntry extends DefaultEntry {
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
     * the one declared in the {@link GridGeometryTable} for that entry. The envelope must include
     * the vertical range if any. If there is a temporal dimension, then the temporal extent must be
     * present as well but may be invalid (the exact value will be set on an coverage-by-coverage
     * basis).
     */
    final GeneralGridGeometry geometry;

    /**
     * Same as {@link #geometry}, but without temporal component.
     *
     * @since 3.15
     */
    final GeneralGridGeometry spatialGeometry;

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
     * algorithm instead than the Proj4 algorithms used by PostGIS. This is necessary in case the
     * {@code "spatial_ref_sys"} table content is inconsistent with the EPSG database used by Geotk.
     *
     * @see #getHorizontalEnvelope()
     */
    final Shape standardEnvelope;

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
     * {@code true} if the {@link #verticalOrdinates} array elements are sorted in
     * increasing order.
     */
    private final boolean verticalOrdinatesSorted;

    /**
     * {@code true} if the grid geometry needs longitude values in the [0…360]° range
     * instead than the default [-180 … 180]° range.
     *
     * @since 3.20
     */
    private final boolean needsLongitudeShift;

    /**
     * Creates an entry from the given grid geometry. This constructor does not clone
     * the object given in argument. Consequently, those object shall not be modified
     * after {@code GridGeometryEntry} construction.
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
        /*
         * Inspect the vertical ordinates, in search for extremums values
         * and whatever the array is sorted or not.
         */
        double min = Double.POSITIVE_INFINITY;
        double max = Double.NEGATIVE_INFINITY;
        boolean isSorted = true;
        if (verticalOrdinates != null) {
            if (verticalOrdinates.length > Short.MAX_VALUE - 1) {
                throw new IllegalArgumentException(); // See 'indexOfNearestAltitude' for this limitation.
            }
            double previous = Double.NEGATIVE_INFINITY;
            for (int i=0; i<verticalOrdinates.length; i++) {
                final double z = verticalOrdinates[i];
                if (z < min) min = z;
                if (z > max) max = z;
                isSorted &= (z > previous);
                previous = z;
            }
            // Transform the (min, max) in "standard" units of the database.
            final MathTransform1D tr = srsEntry.toDatabaseVerticalCRS();
            if (tr != null) {
                min = tr.transform(min);
                max = tr.transform(max);
                if (max < min) {
                    final double t = max;
                    max = min;
                    min = t;
                }
            }
        }
        if (!(min < max)) {
            min = max = Double.NaN;
        }
        standardMinZ = min;
        standardMaxZ = max;
        verticalOrdinatesSorted = isSorted;
        /*
         * Create the geometry and the envelope.
         */
        needsLongitudeShift = srsEntry.needsLongitudeShift(size, gridToCRS);
        geometry = srsEntry.createGridGeometry(size, gridToCRS, verticalOrdinates, mtFactory, true, needsLongitudeShift);
        if (srsEntry.temporalCRS != null) {
            spatialGeometry = srsEntry.createGridGeometry(size, gridToCRS, verticalOrdinates, mtFactory, false, needsLongitudeShift);
        } else {
            spatialGeometry = geometry;
        }
        standardEnvelope = srsEntry.toDatabaseHorizontalCRS().createTransformedShape(getHorizontalEnvelope());
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
     * This CRS is used by {@link GridCoverageEntry#getGridGeometry()} in order
     * to build a {@link GeneralGridGeometry} specific to each coverage entry.
     *
     * @param includeTime {@code true} if the CRS should include the time component,
     *        or {@code false} for a spatial-only CRS.
     */
    public CoordinateReferenceSystem getSpatioTemporalCRS(final boolean includeTime) {
        final CoordinateReferenceSystem crs = srsEntry.getSpatioTemporalCRS(includeTime, needsLongitudeShift);
        assert !includeTime || crs.equals(geometry.getCoordinateReferenceSystem()) : crs;
        return crs;
    }

    /**
     * Returns whatever default grid range computation should be performed on transforms
     * relative to pixel center or relative to pixel corner.
     */
    public PixelInCell getPixelInCell() {
        return srsEntry.getPixelInCell();
    }

    /**
     * Returns a matrix for the <cite>grid to CRS</cite> affine transform.  The coefficients for
     * the horizontal and vertical (if any) dimensions are initialized. But the coefficients for
     * the temporal dimension (if any) must be initialized by the caller. The temporal dimension
     * is assumed the last one.
     *
     * @param dimension  The number of dimensions for the source and target CRS.
     * @param zIndex     The 1-based index of the <var>z</var> value, or 0 if none.
     */
    final XMatrix getGridToCRS(final int dimension, int zIndex) {
        final XMatrix matrix = Matrices.create(dimension + 1);
        SpatialRefSysEntry.copy(gridToCRS, matrix);
        if (verticalOrdinates != null) {
            final int imax = verticalOrdinates.length - 1;
            if (imax >= 0) {
                final int zDimension = srsEntry.zDimension();
                if (zDimension >= 0) {
                    if (--zIndex > imax) {
                        zIndex = imax;
                    }
                    final double scale, offset;
                    if (zIndex >= 0) {
                        final double z = verticalOrdinates[zIndex];
                        final double before = (zIndex != 0)    ? z - verticalOrdinates[zIndex - 1] : 0;
                        final double after  = (zIndex != imax) ? verticalOrdinates[zIndex + 1] - z : 0;
                        scale = (before != 0 && abs(before) <= abs(after)) ? before : after;
                        offset = z - 0.5 * scale;
                    } else {
                        offset = verticalOrdinates[0];
                        scale  = verticalOrdinates[imax] - offset;
                    }
                    matrix.setElement(zDimension, zDimension, scale);
                    matrix.setElement(zDimension, dimension, offset);
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
     * Convenience method returning the two first dimensions of the grid extent.
     */
    public Dimension getImageSize() {
        final GridEnvelope extent = spatialGeometry.getExtent();
        return new Dimension(extent.getSpan(0), extent.getSpan(1));
    }

    /**
     * Convenience method returning the two first dimensions of the grid extent.
     */
    public Rectangle getImageBounds() {
        final GridEnvelope extent = spatialGeometry.getExtent();
        return new Rectangle(extent.getLow (0), extent.getLow (1),
                             extent.getSpan(0), extent.getSpan(1));
    }

    /**
     * Returns the horizontal resolution, in units of the database CRS (often WGS84).
     * This method computes the resolution by projecting a pixel at the image center.
     *
     * @return The horizontal resolution, or {@code null} if it can not be computed.
     * @throws TransformException If the resolution can not be converted to the database CRS.
     */
    public Dimension2D getStandardResolution() throws TransformException {
        final double[] resolution = spatialGeometry.getResolution();
        if (resolution != null) {
            final MathTransform2D toDatabaseHorizontalCRS = srsEntry.toDatabaseHorizontalCRS();
            if (toDatabaseHorizontalCRS != null) {
                final Point2D center = getHorizontalCenter();
                final Rectangle2D.Double pixel = new Rectangle2D.Double(
                        center.getX(), center.getY(), resolution[0], resolution[1]);
                pixel.x -= 0.5 * pixel.width;
                pixel.y -= 0.5 * pixel.height;
                Envelopes.transform(toDatabaseHorizontalCRS, pixel, pixel);
                return new DoubleDimension2D(pixel.width, pixel.height);
            }
        }
        return null;
    }

    /**
     * Returns the geographic bounding box. This method transforms the {@link #standardEnvelope}
     * rather than the coverage envelope, because the "standard" envelope is typically already
     * in WGS 84.
     *
     * @throws TransformException If the envelope can not be converted to WGS84.
     */
    public GeographicBoundingBox getGeographicBoundingBox() throws TransformException {
        final DefaultGeographicBoundingBox bbox = new DefaultGeographicBoundingBox();
        bbox.setBounds((Envelope) new Envelope2D(srsEntry.getDatabaseCRS(), standardEnvelope.getBounds2D()));
        return bbox;
    }

    /**
     * Returns the coverage shape in coverage CRS (not database CRS). The returned shape is likely
     * (but not guaranteed) to be an instance of {@link Rectangle2D}. It can be freely modified.
     */
    private Shape getHorizontalEnvelope() {
        final GridEnvelope extent = spatialGeometry.getExtent();
        Shape shape = new Rectangle2D.Double(
                extent.getLow (0), extent.getLow (1),
                extent.getSpan(0), extent.getSpan(1));
        shape = XAffineTransform.transform(gridToCRS, shape, true);
        return shape;
    }

    /**
     * Returns the coordinates (in coverage CRS) at the center of the image.
     */
    private Point2D getHorizontalCenter() {
        final GridEnvelope extent = spatialGeometry.getExtent();
        Point2D center = new Point2D.Double(
                extent.getLow(0) + 0.5*extent.getSpan(0),
                extent.getLow(1) + 0.5*extent.getSpan(1));
        return gridToCRS.transform(center, center);
    }

    /**
     * Returns the vertical ordinate values, or {@code null} if none. If non-null,
     * then the array length must be equals to the {@code extent.getLength(2)}.
     */
    public double[] getVerticalOrdinates() {
        if (verticalOrdinates != null) {
            assert geometry.getExtent().getSpan(2) == verticalOrdinates.length : geometry;
            return verticalOrdinates.clone();
        }
        return null;
    }

    /**
     * Returns the 1-based index of the closest altitude. If this entry contains no altitude,
     * or if the specified <var>z</var> is not a finite number, then this method returns 0.
     *
     * @param z The value to search for, or {@code NaN} if none.
     * @return  The 1-based altitude index, or {@code 0} if none.
     */
    final short indexOfNearestAltitude(final double z) {
        int index = 0;
        if (!Double.isNaN(z) && !Double.isInfinite(z)) {
            double delta = Double.POSITIVE_INFINITY;
            if (verticalOrdinates != null) {
                if (verticalOrdinatesSorted) {
                    index = Arrays.binarySearch(verticalOrdinates, z);
                    if (index >= 0) {
                        index++; // Make the index 1-based.
                    } else {
                        index = ~index;
                        if (index != verticalOrdinates.length && (index == 0 ||
                                verticalOrdinates[index] - z < z - verticalOrdinates[index-1]))
                        {
                            index++; // Upper value is closer to z than the lower value.
                        }
                        // At this point, the index is 1-based.
                    }
                } else {
                    for (int i=0; i<verticalOrdinates.length; i++) {
                        final double d = abs(verticalOrdinates[i] - z);
                        if (d < delta) {
                            delta = d;
                            index = i + 1;
                        }
                    }
                }
            }
        }
        return (short) index; // Array length has been checked at construction time.
    }

    /**
     * Returns {@code true} if the specified entry has the same envelope than this entry,
     * regardless the grid size.
     */
    final boolean sameEnvelope(final GridGeometryEntry that) {
        if (Arrays.equals(verticalOrdinates, that.verticalOrdinates)) {
            final AbstractEnvelope e1 = (AbstractEnvelope) this.geometry.getEnvelope();
            final AbstractEnvelope e2 = (AbstractEnvelope) that.geometry.getEnvelope();
            return e1.equals(e2, SpatialRefSysEntry.EPS, true);
        }
        return false;
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
            return Objects.equals(this.srsEntry,          that.srsEntry) &&
                   Objects.equals(this.geometry,          that.geometry) &&
                    Arrays.equals(this.verticalOrdinates, that.verticalOrdinates);
        }
        return false;
    }
}
