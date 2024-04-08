/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2005-2008, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2009, Geomatys
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
package org.geotoolkit.geometry.jts;

import java.awt.geom.Rectangle2D;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Envelope;
import java.util.Objects;
import org.opengis.geometry.DirectPosition;
import org.opengis.geometry.MismatchedDimensionException;
import org.opengis.util.FactoryException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.CoordinateOperation;
import org.opengis.referencing.operation.CoordinateOperationFactory;
import org.opengis.referencing.operation.MathTransform;
import org.opengis.referencing.operation.TransformException;
import org.apache.sis.geometry.DirectPosition2D;
import org.apache.sis.geometry.GeneralEnvelope;
import org.geotoolkit.referencing.CRS;
import org.apache.sis.util.Classes;
import org.geotoolkit.resources.Errors;
import org.apache.sis.geometry.Envelopes;

/**
 * A JTS envelope associated with a
 * {@linkplain CoordinateReferenceSystem coordinate reference system}. In
 * addition, this JTS envelope also implements the Types
 * {@linkplain org.opengis.geometry.Envelope envelope} interface
 * for interoperability with Types.
 *
 * @module
 * @since 2.2
 * @version $Id$
 * @author Jody Garnett
 * @author Martin Desruisseaux
 * @author Simone Giannecchini
 * @author Johann Sorel
 *
 * @see org.apache.sis.geometry.Envelope2D
 * @see org.apache.sis.geometry.GeneralEnvelope
 * @see org.opengis.metadata.extent.GeographicBoundingBox
 */
public class JTSEnvelope2D extends Envelope implements org.opengis.geometry.Envelope {

    /** A ReferencedEnvelope containing "everything" */
    public static final JTSEnvelope2D EVERYTHING = new JTSEnvelope2D(
            Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY,
            Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY, null) {

        private static final long serialVersionUID = -3188702602373537164L;

        @Override
        public boolean contains(Coordinate p) {
            return true;
        }

        @Override
        public boolean contains(DirectPosition pos) {
            return true;
        }

        @Override
        public boolean contains(double x, double y) {
            return true;
        }

        @Override
        public boolean contains(Envelope other) {
            return true;
        }

        @Override
        public boolean isEmpty() {
            return false;
        }

        @Override
        public boolean isNull() {
            return true;
        }
    };
    /**
     * Serial number for compatibility with different versions.
     */
    private static final long serialVersionUID = -3188702602373537163L;
    /**
     * The coordinate reference system, or {@code null}.
     */
    private CoordinateReferenceSystem crs;

    /**
     * Creates a null envelope with a null coordinate reference system.
     */
    public JTSEnvelope2D() {
        this((CoordinateReferenceSystem) null);
    }

    /**
     * Creates a null envelope with the specified coordinate reference system.
     *
     * @param crs The coordinate reference system.
     * @throws MismatchedDimensionException if the CRS dimension is not valid.
     */
    public JTSEnvelope2D(final CoordinateReferenceSystem crs)
            throws MismatchedDimensionException {
        this.crs = crs;
        checkCoordinateReferenceSystemDimension();
    }

    /**
     * Creates an envelope for a region defined by maximum and minimum values.
     *
     * @param x1  The first x-value.
     * @param x2  The second x-value.
     * @param y1  The first y-value.
     * @param y2  The second y-value.
     * @param crs The coordinate reference system.
     *
     * @throws MismatchedDimensionException if the CRS dimension is not valid.
     */
    public JTSEnvelope2D(final double x1, final double x2, final double y1, final double y2,
            final CoordinateReferenceSystem crs) throws MismatchedDimensionException {
        super(x1, x2, y1, y2);
        this.crs = crs;
        checkCoordinateReferenceSystemDimension();
    }

    /**
     * Creates an envelope for a Java2D rectangle.
     *
     * @param rectangle The rectangle.
     * @param crs The coordinate reference system.
     *
     * @throws MismatchedDimensionException if the CRS dimension is not valid.
     *
     * @since 2.4
     */
    public JTSEnvelope2D(final Rectangle2D rectangle, final CoordinateReferenceSystem crs)
            throws MismatchedDimensionException {
        this(rectangle.getMinX(), rectangle.getMaxX(), rectangle.getMinY(), rectangle.getMaxY(), crs);
    }

    /**
     * Creates a new envelope from an existing envelope.
     *
     * @param envelope The envelope to initialize from
     * @throws MismatchedDimensionException if the CRS dimension is not valid.
     *
     * @since 2.3
     */
    public JTSEnvelope2D(final JTSEnvelope2D envelope)
            throws MismatchedDimensionException {
        super(envelope);
        crs = envelope.getCoordinateReferenceSystem();
        checkCoordinateReferenceSystemDimension();
    }

    /**
     * Creates a new envelope from an existing OGC envelope.
     *
     * @param envelope The envelope to initialize from.
     * @throws MismatchedDimensionException if the CRS dimension is not valid.
     *
     * @since 2.4
     */
    public JTSEnvelope2D(final org.opengis.geometry.Envelope envelope)
            throws MismatchedDimensionException {
        super(envelope.getMinimum(0), envelope.getMaximum(0), envelope.getMinimum(1),
                envelope.getMaximum(1));
        this.crs = envelope.getCoordinateReferenceSystem();
        checkCoordinateReferenceSystemDimension();
    }

    /**
     * Creates a new envelope from an existing JTS envelope.
     *
     * @param envelope The envelope to initialize from.
     * @param crs The coordinate reference system.
     * @throws MismatchedDimensionException if the CRS dimension is not valid.
     */
    public JTSEnvelope2D(final Envelope envelope, final CoordinateReferenceSystem crs)
            throws MismatchedDimensionException {
        super(envelope);
        this.crs = crs;
        checkCoordinateReferenceSystemDimension();
    }

    /**
     * Convenience method for checking coordinate reference system validity.
     *
     * @throws IllegalArgumentException if the CRS dimension is not valid.
     */
    private void checkCoordinateReferenceSystemDimension()
            throws MismatchedDimensionException {
        if (crs != null) {
            final int expected = getDimension();
            final int dimension = crs.getCoordinateSystem().getDimension();
            if (dimension != expected) {
                throw new MismatchedDimensionException(Errors.format(
                        Errors.Keys.MismatchedDimension_3, crs.getName().getCode(),
                        dimension, expected));
            }
        }
    }

    /**
     * Returns the coordinate reference system associated with this envelope.
     */
    @Override
    public CoordinateReferenceSystem getCoordinateReferenceSystem() {
        return crs;
    }

    /**
     * Returns the number of dimensions.
     */
    @Override
    public int getDimension() {
        return 2;
    }

    /**
     * Returns the minimal ordinate along the specified dimension.
     */
    @Override
    public double getMinimum(final int dimension) {
        switch (dimension) {
            case 0:  return getMinX();
            case 1:  return getMinY();
            default: throw new IndexOutOfBoundsException(String.valueOf(dimension));
        }
    }

    /**
     * Returns the maximal ordinate along the specified dimension.
     */
    @Override
    public double getMaximum(final int dimension) {
        switch (dimension) {
            case 0:  return getMaxX();
            case 1:  return getMaxY();
            default: throw new IndexOutOfBoundsException(String.valueOf(dimension));
        }
    }

    /**
     * Returns the center ordinate along the specified dimension.
     */
    @Override
    public double getMedian(final int dimension) {
        switch (dimension) {
            case 0:  return 0.5 * (getMinX() + getMaxX());
            case 1:  return 0.5 * (getMinY() + getMaxY());
            default: throw new IndexOutOfBoundsException(String.valueOf(dimension));
        }
    }

    /**
     * Returns the envelope length along the specified dimension. This length is
     * equals to the maximum ordinate minus the minimal ordinate.
     */
    @Override
    public double getSpan(final int dimension) {
        switch (dimension) {
            case 0:  return getWidth();
            case 1:  return getHeight();
            default: throw new IndexOutOfBoundsException(String.valueOf(dimension));
        }
    }

    /**
     * A coordinate position consisting of all the minimal coordinates for each
     * dimension for all points within the {@code Envelope}.
     */
    @Override
    public DirectPosition getLowerCorner() {
        return new DirectPosition2D(crs, getMinX(), getMinY());
    }

    /**
     * A coordinate position consisting of all the maximal coordinates for each
     * dimension for all points within the {@code Envelope}.
     */
    @Override
    public DirectPosition getUpperCorner() {
        return new DirectPosition2D(crs, getMaxX(), getMaxY());
    }

    /**
     * Returns {@code true} if lengths along all dimension are zero.
     *
     * @since 2.4
     */
    public boolean isEmpty() {
        return super.isNull();
    }

    /**
     * Returns {@code true} if the provided location is contained by this bounding box.
     *
     * @since 2.4
     */
    public boolean contains(final DirectPosition pos) {
        return super.contains(pos.getCoordinate(0), pos.getCoordinate(1));
    }

    /**
     * Include the provided coordinates, expanding as necessary.
     *
     * @since 2.4
     */
    public void include(final double x, final double y) {
        super.expandToInclude(x, y);
    }

    /**
     * Transforms the referenced envelope to the specified coordinate reference system.
     * <p>
     * This method can handle the case where the envelope contains the North or South pole,
     * or when it cross the &plusmn;180ï¿½ longitude.
     *
     * @param targetCRS The target coordinate reference system.
     * @return The transformed envelope.
     * @throws FactoryException if the math transform can't be determined.
     * @throws TransformException if at least one coordinate can't be transformed.
     */
    public JTSEnvelope2D transform(final CoordinateReferenceSystem targetCRS)
            throws TransformException, FactoryException
    {
        return transform(targetCRS, 5);
    }

    /**
     * Transforms the referenced envelope to the specified coordinate reference system
     * using the specified amount of points.
     * <p>
     * This method can handle the case where the envelope contains the North or South pole,
     * or when it cross the &plusmn;180ï¿½ longitude.
     *
     * @param targetCRS The target coordinate reference system.
     * @param numPointsForTransformation The number of points to use for sampling the envelope.
     * @return The transformed envelope.
     * @throws FactoryException if the math transform can't be determined.
     * @throws TransformException if at least one coordinate can't be transformed.
     */
    public JTSEnvelope2D transform(final CoordinateReferenceSystem targetCRS, final int numPointsForTransformation)
            throws TransformException, FactoryException
    {
        if (crs == null) {
            if (isEmpty()) {
                // We don't have a CRS yet because we are still empty, being empty is
                // something we can represent in the targetCRS
                return new JTSEnvelope2D(targetCRS);
            } else {
                // really this is a the code that created this ReferencedEnvelope
                throw new NullPointerException("Unable to transform referenced envelope, crs has not yet been provided.");
            }
        }

        /*
         * Gets a first estimation using an algorithm capable to take singularity in account
         * (North pole, South pole, 180ï¿½ longitude). We will expand this initial box later.
         */
        CoordinateOperationFactory coordinateOperationFactory = CRS.getCoordinateOperationFactory();

        final CoordinateOperation operation = coordinateOperationFactory.createOperation(crs, targetCRS);
        final GeneralEnvelope transformed = Envelopes.transform(operation, this);
        transformed.setCoordinateReferenceSystem(targetCRS);

        /*
         * Now expands the box using the usual utility methods.
         */
        final JTSEnvelope2D target = new JTSEnvelope2D(transformed);
        final MathTransform transform = operation.getMathTransform();
        JTS.transform(this, target, transform, numPointsForTransformation);

        return target;
    }

    /**
     * Returns a hash value for this envelope. This value need not remain
     * consistent between different implementations of the same class.
     */
    @Override
    public int hashCode() {
        int code = super.hashCode() ^ (int) serialVersionUID;
        if (crs != null) {
            code ^= crs.hashCode();
        }
        return code;
    }

    /**
     * Compares the specified object with this envelope for equality.
     */
    @Override
    public boolean equals(final Object object) {
        if (super.equals(object)) {
            final CoordinateReferenceSystem otherCRS = (object instanceof JTSEnvelope2D)
                    ? ((JTSEnvelope2D) object).crs : null;

            return Objects.equals(crs, otherCRS);
        }
        return false;
    }

    /**
     * Returns a string representation of this envelope. The default implementation
     * is okay for occasional formatting (for example for debugging purpose).
     */
    @Override
    public String toString() {
        final StringBuilder buffer = new StringBuilder(Classes.getShortClassName(this)).append('[');
        final int dimension = getDimension();

        for (int i = 0; i < dimension; i++) {
            if (i != 0) {
                buffer.append(", ");
            }

            buffer.append(getMinimum(i)).append(" : ").append(getMaximum(i));
        }

        return buffer.append(']').toString();
    }

    /**
     * Utility method to ensure that an Envelope if a JTSEnvelope2D.
     * <p>
     * This method first checks if <tt>e</tt> is an instanceof {@link JTSEnvelope2D},
     * if it is, itself is returned. If not <code>new JTSEnvelope2D(e,null)</code>
     * is returned.
     * </p>
     * <p>
     * If e is null, null is returned.
     * </p>
     * @param e The envelope.  Can be null.
     * @return A JTSEnvelope2D using the specified envelope, or null if the envelope was null.
     */
    public static JTSEnvelope2D reference(final Envelope e) {
        if (e == null) {
            return null;
        } else {
            if (e instanceof JTSEnvelope2D) {
                return (JTSEnvelope2D) e;
            }

            return new JTSEnvelope2D(e, null);
        }
    }
}
