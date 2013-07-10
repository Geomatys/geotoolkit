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
package org.geotoolkit.geometry;

import java.util.Arrays;
import java.util.Objects;
import java.io.Serializable;
import java.awt.geom.Rectangle2D;

import org.opengis.referencing.cs.CoordinateSystemAxis;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.geometry.Envelope;
import org.opengis.geometry.DirectPosition;
import org.opengis.geometry.MismatchedDimensionException;
import org.opengis.metadata.extent.GeographicBoundingBox;
import org.apache.sis.geometry.GeneralDirectPosition;
import org.geotoolkit.resources.Errors;
import org.apache.sis.util.ArraysExt;
import org.geotoolkit.referencing.crs.DefaultGeographicCRS;

import static org.apache.sis.util.ArgumentChecks.*;
import static org.apache.sis.math.MathFunctions.isNegative;
import static org.geotoolkit.internal.InternalUtilities.isPoleToPole;


/**
 * Base class of envelopes backed by an array.
 * See {@link GeneralEnvelope} javadoc for more information.
 *
 * @author Martin Desruisseaux (IRD, Geomatys)
 * @version 3.20
 *
 * @since 3.2 (derived from 1.2)
 * @module
 */
class ArrayEnvelope extends AbstractEnvelope implements Serializable {
    /**
     * Serial number for inter-operability with different versions.
     */
    private static final long serialVersionUID = 7284917239693486738L;

    /**
     * Minimum and maximum ordinate values. The first half contains minimum ordinates, while the
     * last half contains maximum ordinates. This layout is convenient for the creation of lower
     * and upper corner direct positions.
     */
    final double[] ordinates;

    /**
     * The coordinate reference system, or {@code null}.
     */
    CoordinateReferenceSystem crs;

    /**
     * Constructs an empty envelope of the specified dimension. All ordinates
     * are initialized to 0 and the coordinate reference system is undefined.
     *
     * @param dimension The envelope dimension.
     */
    public ArrayEnvelope(final int dimension) {
        ordinates = new double[dimension << 1];
    }

    /**
     * Constructs one-dimensional envelope defined by a range of values.
     *
     * @param min The lower value.
     * @param max The upper value.
     */
    public ArrayEnvelope(final double min, final double max) {
        ordinates = new double[] {min, max};
    }

    /**
     * Builds a two-dimensional envelope with the specified bounds.
     *
     * @param xmin The lower value for the first ordinate.
     * @param xmax The upper value for the first ordinate.
     * @param ymin The lower value for the second ordinate.
     * @param ymax The upper value for the second ordinate.
     */
    public ArrayEnvelope(final double xmin, final double xmax, final double ymin, final double ymax) {
        ordinates = new double[] {
            xmin, ymin, xmax, ymax
        };
    }

    /**
     * Constructs a envelope defined by two positions.
     *
     * @param  minDP Lower ordinate values.
     * @param  maxDP Upper ordinate values.
     * @throws MismatchedDimensionException if the two positions don't have the same dimension.
     */
    public ArrayEnvelope(final double[] minDP, final double[] maxDP) {
        ensureNonNull("minDP", minDP);
        ensureNonNull("maxDP", maxDP);
        ensureSameDimension(minDP.length, maxDP.length);
        ordinates = new double[minDP.length + maxDP.length];
        System.arraycopy(minDP, 0, ordinates, 0,            minDP.length);
        System.arraycopy(maxDP, 0, ordinates, minDP.length, maxDP.length);
    }

    /**
     * Constructs a new envelope with the same data than the specified envelope.
     *
     * @param envelope The envelope to copy.
     */
    public ArrayEnvelope(final Envelope envelope) {
        ensureNonNull("envelope", envelope);
        if (envelope instanceof ArrayEnvelope) {
            final ArrayEnvelope e = (ArrayEnvelope) envelope;
            ordinates = e.ordinates.clone();
            crs = e.crs;
        } else {
            crs = envelope.getCoordinateReferenceSystem();
            final int dimension = envelope.getDimension();
            ordinates = new double[2*dimension];
            for (int i=0; i<dimension; i++) {
                ordinates[i]           = envelope.getMinimum(i);
                ordinates[i+dimension] = envelope.getMaximum(i);
            }
        }
    }

    /**
     * Constructs a new envelope with the same data than the specified
     * geographic bounding box. The coordinate reference system is set
     * to {@linkplain DefaultGeographicCRS#WGS84 WGS84}.
     *
     * @param box The bounding box to copy.
     */
    public ArrayEnvelope(final GeographicBoundingBox box) {
        ensureNonNull("box", box);
        ordinates = new double[] {
            box.getWestBoundLongitude(),
            box.getSouthBoundLatitude(),
            box.getEastBoundLongitude(),
            box.getNorthBoundLatitude()
        };
        if (Boolean.FALSE.equals(box.getInclusion())) {
            swap(0);
            if (!isPoleToPole(ordinates[1], ordinates[3])) {
                swap(1);
            }
        }
        crs = DefaultGeographicCRS.WGS84;
    }

    /**
     * Constructs two-dimensional envelope defined by a {@link Rectangle2D}.
     * The coordinate reference system is initially undefined.
     *
     * @param rect The rectangle to copy.
     */
    public ArrayEnvelope(final Rectangle2D rect) {
        ensureNonNull("rect", rect);
        ordinates = new double[] {
            rect.getMinX(), rect.getMinY(),
            rect.getMaxX(), rect.getMaxY()
        };
    }

    /**
     * Constructs a new envelope initialized to the values parsed from the given string in
     * <cite>Well Known Text</cite> (WKT) format. The given string is typically a {@code BOX}
     * element like below:
     *
     * {@preformat wkt
     *     BOX(-180 -90, 180 90)
     * }
     *
     * However this constructor is lenient to other geometry types like {@code POLYGON}.
     * See the javadoc of the {@link GeneralEnvelope#GeneralEnvelope(String) GeneralEnvelope}
     * constructor for more information.
     *
     * @param  wkt The {@code BOX}, {@code POLYGON} or other kind of element to parse.
     * @throws NumberFormatException If a number can not be parsed.
     * @throws IllegalArgumentException If the parenthesis are not balanced.
     */
    public ArrayEnvelope(final String wkt) throws NumberFormatException, IllegalArgumentException {
        ensureNonNull("wkt", wkt);
        int levelParenth = 0; // Number of opening parenthesis: (
        int levelBracket = 0; // Number of opening brackets: [
        int dimLimit     = 4; // The length of minimum and maximum arrays.
        int maxDimension = 0; // The number of valid entries in the minimum and maximum arrays.
        final int length = wkt.length();
        double[] minimum = new double[dimLimit];
        double[] maximum = new double[dimLimit];
        int dimension = 0;
scan:   for (int i=0; i<length; i++) {
            char c = wkt.charAt(i);
            if (Character.isJavaIdentifierStart(c)) {
                do if (++i >= length) break scan;
                while (Character.isJavaIdentifierPart(c = wkt.charAt(i)));
            }
            if (Character.isWhitespace(c)) {
                continue;
            }
            switch (c) {
                case ',':                                      dimension=0; continue;
                case '(':     ++levelParenth;                  dimension=0; continue;
                case '[':     ++levelBracket;                  dimension=0; continue;
                case ')': if (--levelParenth<0) fail(wkt,'('); dimension=0; continue;
                case ']': if (--levelBracket<0) fail(wkt,'['); dimension=0; continue;
            }
            /*
             * At this point we have skipped the leading keyword (BOX, POLYGON, etc.),
             * the spaces and the parenthesis if any. We should be at the beginning of
             * a number. Search the first separator character (which determine the end
             * of the number) and parse the number.
             */
            final int start = i;
            boolean flush = false;
scanNumber: while (++i < length) {
                c = wkt.charAt(i);
                if (Character.isWhitespace(c)) {
                    break;
                }
                switch (c) {
                    case ',':                                      flush=true; break scanNumber;
                    case ')': if (--levelParenth<0) fail(wkt,'('); flush=true; break scanNumber;
                    case ']': if (--levelBracket<0) fail(wkt,'['); flush=true; break scanNumber;
                }
            }
            final double value = Double.parseDouble(wkt.substring(start, i));
            /*
             * Adjust the minimum and maximum value using the number that we parsed,
             * increasing the arrays size if necessary. Remember the maximum number
             * of dimensions we have found so far.
             */
            if (dimension == maxDimension) {
                if (dimension == dimLimit) {
                    dimLimit *= 2;
                    minimum = Arrays.copyOf(minimum, dimLimit);
                    maximum = Arrays.copyOf(maximum, dimLimit);
                }
                minimum[dimension] = maximum[dimension] = value;
                maxDimension = ++dimension;
            } else {
                if (value < minimum[dimension]) minimum[dimension] = value;
                if (value > maximum[dimension]) maximum[dimension] = value;
                dimension++;
            }
            if (flush) {
                dimension = 0;
            }
        }
        if (levelParenth != 0) fail(wkt, ')');
        if (levelBracket != 0) fail(wkt, ']');
        ordinates = ArraysExt.resize(minimum, maxDimension << 1);
        System.arraycopy(maximum, 0, ordinates, maxDimension, maxDimension);
    }

    /**
     * Throws an exception for unmatched parenthesis during WKT parsing.
     */
    private static void fail(final String wkt, char missing) {
        throw new IllegalArgumentException(Errors.format(
                Errors.Keys.NON_EQUILIBRATED_PARENTHESIS_2, wkt, missing));
    }

    /**
     * Makes sure the specified dimensions are identical.
     */
    static void ensureSameDimension(final int dim1, final int dim2) throws MismatchedDimensionException {
        if (dim1 != dim2) {
            throw new MismatchedDimensionException(Errors.format(
                    Errors.Keys.MISMATCHED_DIMENSION_2, dim1, dim2));
        }
    }

    /**
     * Swaps two ordinate values.
     */
    private void swap(final int i) {
        final int m = i + (ordinates.length >>> 1);
        final double t = ordinates[i];
        ordinates[i] = ordinates[m];
        ordinates[m] = t;
    }

    /**
     * Returns the length of coordinate sequence (the number of entries) in this envelope.
     * This information is available even when the {@linkplain #getCoordinateReferenceSystem
     * coordinate reference system} is unknown.
     *
     * @return The dimensionality of this envelope.
     */
    @Override
    public int getDimension() {
        return ordinates.length >>> 1;
    }

    /**
     * Returns the envelope coordinate reference system, or {@code null} if unknown.
     * If non-null, it shall be the same as {@linkplain #getLowerCorner() lower corner}
     * and {@linkplain #getUpperCorner() upper corner} CRS.
     *
     * @return The envelope CRS, or {@code null} if unknown.
     */
    @Override
    public CoordinateReferenceSystem getCoordinateReferenceSystem() {
        assert crs == null || crs.getCoordinateSystem().getDimension() == getDimension();
        return crs;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public DirectPosition getLowerCorner() {
        final int dim = ordinates.length >>> 1;
        final GeneralDirectPosition position = new GeneralDirectPosition(dim);
        System.arraycopy(ordinates, 0, position.ordinates, 0, dim);
        position.setCoordinateReferenceSystem(crs);
        return position;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public DirectPosition getUpperCorner() {
        final int dim = ordinates.length >>> 1;
        final GeneralDirectPosition position = new GeneralDirectPosition(dim);
        System.arraycopy(ordinates, dim, position.ordinates, 0, dim);
        position.setCoordinateReferenceSystem(crs);
        return position;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public double getLower(final int dimension) throws IndexOutOfBoundsException {
        ensureValidIndex(ordinates.length >>> 1, dimension);
        return ordinates[dimension];
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public double getUpper(final int dimension) throws IndexOutOfBoundsException {
        final int dim = ordinates.length >>> 1;
        ensureValidIndex(dim, dimension);
        return ordinates[dimension + dim];
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public double getMinimum(final int dimension) throws IndexOutOfBoundsException {
        final int dim = ordinates.length >>> 1;
        ensureValidIndex(dim, dimension);
        double lower = ordinates[dimension];
        if (isNegative(ordinates[dimension + dim] - lower)) { // Special handling for -0.0
            final CoordinateSystemAxis axis = getAxis(crs, dimension);
            lower = (axis != null) ? axis.getMinimumValue() : Double.NEGATIVE_INFINITY;
        }
        return lower;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public double getMaximum(final int dimension) throws IndexOutOfBoundsException {
        final int dim = ordinates.length >>> 1;
        ensureValidIndex(dim, dimension);
        double upper = ordinates[dimension + dim];
        if (isNegative(upper - ordinates[dimension])) { // Special handling for -0.0
            final CoordinateSystemAxis axis = getAxis(crs, dimension);
            upper = (axis != null) ? axis.getMaximumValue() : Double.POSITIVE_INFINITY;
        }
        return upper;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public double getMedian(final int dimension) throws IndexOutOfBoundsException {
        ensureValidIndex(ordinates.length >>> 1, dimension);
        final double minimum = ordinates[dimension];
        final double maximum = ordinates[dimension + (ordinates.length >>> 1)];
        double median = 0.5 * (minimum + maximum);
        if (isNegative(maximum - minimum)) { // Special handling for -0.0
            median = fixMedian(getAxis(crs, dimension), median);
        }
        return median;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public double getSpan(final int dimension) throws IndexOutOfBoundsException {
        ensureValidIndex(ordinates.length >>> 1, dimension);
        double span = ordinates[dimension + (ordinates.length >>> 1)] - ordinates[dimension];
        if (isNegative(span)) { // Special handling for -0.0
            span = fixSpan(getAxis(crs, dimension), span);
        }
        return span;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isEmpty() {
        final int dimension = ordinates.length >>> 1;
        if (dimension == 0) {
            return true;
        }
        for (int i=0; i<dimension; i++) {
            final double span = ordinates[i+dimension] - ordinates[i];
            if (!(span > 0)) { // Use '!' in order to catch NaN
                if (!(isNegative(span) && isWrapAround(crs, i))) {
                    return true;
                }
            }
        }
        assert !isAllNaN() : this;
        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isAllNaN() {
        for (int i=0; i<ordinates.length; i++) {
            if (!Double.isNaN(ordinates[i])) {
                return false;
            }
        }
        assert isEmpty() : this;
        return true;
    }

    /**
     * Returns a hash value for this envelope.
     */
    @Override
    public int hashCode() {
        int code = Arrays.hashCode(ordinates);
        if (crs != null) {
            code += crs.hashCode();
        }
        assert code == super.hashCode();
        return code;
    }

    /**
     * Compares the specified object with this envelope for equality.
     */
    @Override
    public boolean equals(final Object object) {
        if (object != null && object.getClass() == getClass()) {
            final ArrayEnvelope that = (ArrayEnvelope) object;
            return Arrays.equals(this.ordinates, that.ordinates) &&
                  Objects.equals(this.crs, that.crs);
        }
        return false;
    }
}
