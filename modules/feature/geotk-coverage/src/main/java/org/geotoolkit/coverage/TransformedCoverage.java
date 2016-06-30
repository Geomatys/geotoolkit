/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2005-2012, Open Source Geospatial Foundation (OSGeo)
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
package org.geotoolkit.coverage;

import javax.media.jai.PropertySource;

import org.opengis.coverage.Coverage;
import org.opengis.coverage.SampleDimension;
import org.opengis.coverage.CannotEvaluateException;
import org.opengis.coverage.PointOutsideCoverageException;
import org.opengis.geometry.Envelope;
import org.opengis.geometry.DirectPosition;
import org.opengis.util.FactoryException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.MathTransform;
import org.opengis.referencing.operation.TransformException;

import org.geotoolkit.lang.Decorator;
import org.geotoolkit.referencing.CRS;
import org.apache.sis.geometry.GeneralEnvelope;
import org.apache.sis.util.Utilities;


/**
 * A coverage wrapping an other one with a different coordinate reference system. The coordinate
 * transformation is applied on the fly every time an {@code evaluate} method is invoked. It may
 * be efficient if few points are queried, but become inefficient if a large amount of points is
 * queried. In the later case, consider reprojecting the whole grid coverage instead.
 *
 * {@section Synchronization}
 * This class is not thread safe for performance reasons. If desired, users should create one
 * instance of {@code TransformedCoverage} for each thread.
 *
 * @author Martin Desruisseaux (IRD)
 * @version 3.00
 *
 * @since 2.1
 * @module
 */
@Decorator(Coverage.class)
public class TransformedCoverage extends AbstractCoverage {
    /**
     * For cross-version compatibility.
     */
    private static final long serialVersionUID = 638094266593359879L;

    /**
     * The wrapped coverage. This is the coverage where {@code evaluate} methods in
     * this class will delegate the work, after having transformed the coordinates.
     */
    protected final Coverage coverage;

    /**
     * The transform from this coverage CRS to the CRS of the wrapped coverage.
     */
    protected final MathTransform toOriginalCRS;

    /**
     * The projected point.
     */
    private transient DirectPosition position;

    /**
     * Creates a new coverage wrapping the specified one.
     *
     * @param  name     The name for this new coverage.
     * @param  crs      The crs for this coverage.
     * @param  coverage The coverage to wraps.
     * @throws FactoryException if no transformation can be found from the coverage CRS to the
     *         specified CRS.
     */
    protected TransformedCoverage(final CharSequence name, final CoordinateReferenceSystem crs,
            final Coverage coverage) throws FactoryException
    {
        super(name, crs, (coverage instanceof PropertySource) ? ((PropertySource) coverage) : null, null);
        this.coverage = coverage;
        toOriginalCRS = CRS.findMathTransform(crs, coverage.getCoordinateReferenceSystem());
    }

    /**
     * Creates a new coverage wrapping the specified one with a different CRS.
     * If the specified coverage already uses the specified CRS (or an equivalent one),
     * it is returned unchanged.
     *
     * @param  name     The name for this new coverage.
     * @param  crs      The CRS for this coverage.
     * @param  coverage The coverage to wraps.
     * @return A coverage using the specified CRS.
     * @throws FactoryException if no transformation can be found from the coverage CRS to the
     *         specified CRS.
     */
    public static Coverage reproject(final CharSequence name, final CoordinateReferenceSystem crs,
            Coverage coverage) throws FactoryException
    {
        while (true) {
            if (Utilities.equalsIgnoreMetadata(coverage.getCoordinateReferenceSystem(), crs)) {
                return coverage;
            }
            if (coverage.getClass() == TransformedCoverage.class) {
                coverage = ((TransformedCoverage) coverage).coverage;
                continue;
            }
            break;
        }
        return new TransformedCoverage(name, crs, coverage);
    }

    /**
     * The number of sample dimensions in the coverage.
     * For grid coverages, a sample dimension is a band.
     *
     * @return The number of sample dimensions in the coverage.
     */
    @Override
    public int getNumSampleDimensions() {
        return coverage.getNumSampleDimensions();
    }

    /**
     * Retrieve sample dimension information for the coverage.
     *
     * @param  index Index for sample dimension to retrieve. Indices are numbered 0 to
     *         (<var>{@linkplain #getNumSampleDimensions n}</var>-1).
     * @return Sample dimension information for the coverage.
     * @throws IndexOutOfBoundsException if {@code index} is out of bounds.
     */
    @Override
    public SampleDimension getSampleDimension(final int index) throws IndexOutOfBoundsException {
        return coverage.getSampleDimension(index);
    }

    /**
     * Wraps the checked exception into an unchecked one.
     *
     * @todo Provides a localized message.
     */
    private CannotEvaluateException transformationFailed(final TransformException cause) {
        return new CannotEvaluateException("Transformation failed", cause);
    }

    /**
     * Returns the envelope.
     */
    @Override
    public Envelope getEnvelope() {
        final GeneralEnvelope envelope;
        try {
            envelope = CRS.transform(toOriginalCRS.inverse(), coverage.getEnvelope());
        } catch (TransformException exception) {
            throw transformationFailed(exception);
        }
        envelope.setCoordinateReferenceSystem(crs);
        return envelope;
    }

    /**
     * Returns the value vector for a given point in the coverage.
     *
     * @param  coord The coordinate point where to evaluate.
     * @throws PointOutsideCoverageException if {@code coord} is outside coverage.
     * @throws CannotEvaluateException if the computation failed for some other reason.
     */
    @Override
    public final Object evaluate(final DirectPosition coord)
            throws PointOutsideCoverageException, CannotEvaluateException
    {
        try {
            return coverage.evaluate(position = toOriginalCRS.transform(coord, position));
        } catch (TransformException exception) {
            throw transformationFailed(exception);
        }
    }

    /**
     * Returns a sequence of boolean values for a given point in the coverage.
     */
    @Override
    public final boolean[] evaluate(final DirectPosition coord, boolean[] dest)
            throws PointOutsideCoverageException, CannotEvaluateException
    {
        try {
            return coverage.evaluate(position = toOriginalCRS.transform(coord, position), dest);
        } catch (TransformException exception) {
            throw transformationFailed(exception);
        }
    }

    /**
     * Returns a sequence of byte values for a given point in the coverage.
     */
    @Override
    public final byte[] evaluate(final DirectPosition coord, byte[] dest)
            throws PointOutsideCoverageException, CannotEvaluateException
    {
        try {
            return coverage.evaluate(position = toOriginalCRS.transform(coord, position), dest);
        } catch (TransformException exception) {
            throw transformationFailed(exception);
        }
    }

    /**
     * Returns a sequence of integer values for a given point in the coverage.
     */
    @Override
    public final int[] evaluate(final DirectPosition coord, int[] dest)
            throws PointOutsideCoverageException, CannotEvaluateException
    {
        try {
            return coverage.evaluate(position = toOriginalCRS.transform(coord, position), dest);
        } catch (TransformException exception) {
            throw transformationFailed(exception);
        }
    }

    /**
     * Returns a sequence of float values for a given point in the coverage.
     */
    @Override
    public final float[] evaluate(final DirectPosition coord, float[] dest)
            throws PointOutsideCoverageException, CannotEvaluateException
    {
        try {
            return coverage.evaluate(position = toOriginalCRS.transform(coord, position), dest);
        } catch (TransformException exception) {
            throw transformationFailed(exception);
        }
    }

    /**
     * Returns a sequence of double values for a given point in the coverage.
     */
    @Override
    public final double[] evaluate(final DirectPosition coord, final double[] dest)
            throws PointOutsideCoverageException, CannotEvaluateException
    {
        try {
            return coverage.evaluate(position = toOriginalCRS.transform(coord, position), dest);
        } catch (TransformException exception) {
            throw transformationFailed(exception);
        }
    }
}
