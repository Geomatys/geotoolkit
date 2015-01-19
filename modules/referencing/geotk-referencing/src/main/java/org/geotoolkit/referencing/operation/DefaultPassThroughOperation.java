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
 *
 *    This package contains documentation from OpenGIS specifications.
 *    OpenGIS consortium's work is fully acknowledged here.
 */
package org.geotoolkit.referencing.operation;

import java.util.Map;
import net.jcip.annotations.Immutable;

import org.opengis.referencing.operation.SingleOperation;
import org.opengis.referencing.operation.MathTransform;
import org.opengis.referencing.operation.MathTransformFactory;
import org.opengis.referencing.operation.PassThroughOperation;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

import org.apache.sis.io.wkt.Formatter;
import org.apache.sis.util.UnsupportedImplementationException;
import org.apache.sis.referencing.operation.DefaultOperationMethod;

import org.apache.sis.referencing.operation.transform.PassThroughTransform;
import static org.apache.sis.util.ArgumentChecks.ensureNonNull;


/**
 * A pass-through operation specifies that a subset of a coordinate tuple is subject to a specific
 * coordinate operation.
 *
 * @author Martin Desruisseaux (IRD)
 * @version 3.01
 *
 * @since 2.0
 * @module
 */
@Immutable
public class DefaultPassThroughOperation extends DefaultSingleOperation implements PassThroughOperation {
    /**
     * Serial number for inter-operability with different versions.
     */
    private static final long serialVersionUID = 4308173919747248695L;

    /**
     * The operation to apply on the subset of a coordinate tuple.
     */
    protected final SingleOperation operation;

    /**
     * Constructs a single operation from a set of properties. The properties given in argument
     * follow the same rules than for the {@link AbstractCoordinateOperation} constructor.
     * Affected ordinates will range from {@code firstAffectedOrdinate}
     * inclusive to {@code dimTarget-numTrailingOrdinates} exclusive.
     *
     * @param properties Set of properties. Should contains at least {@code "name"}.
     * @param sourceCRS The source CRS.
     * @param targetCRS The target CRS.
     * @param operation The operation to apply on the subset of a coordinate tuple.
     * @param firstAffectedOrdinate Index of the first affected ordinate.
     * @param numTrailingOrdinates Number of trailing ordinates to pass through.
     */
    public DefaultPassThroughOperation(final Map<String,?>            properties,
                                       final CoordinateReferenceSystem sourceCRS,
                                       final CoordinateReferenceSystem targetCRS,
                                       final SingleOperation           operation,
                                       final int           firstAffectedOrdinate,
                                       final int            numTrailingOrdinates)
    {
//      TODO: Uncomment if Sun fix RFE #4093999
//      ensureNonNull("operation", operation);
        this(properties, sourceCRS, targetCRS, operation,
             PassThroughTransform.create(firstAffectedOrdinate,
                                         operation.getMathTransform(),
                                         numTrailingOrdinates));
    }

    /**
     * Constructs a single operation from a set of properties and the given transform.
     * The properties given in argument follow the same rules than for the
     * {@linkplain AbstractCoordinateOperation#AbstractCoordinateOperation(Map,
     * CoordinateReferenceSystem, CoordinateReferenceSystem, MathTransform)
     * super-class constructor}.
     *
     * @param  properties Set of properties. Should contains at least {@code "name"}.
     * @param  sourceCRS The source CRS.
     * @param  targetCRS The target CRS.
     * @param  operation The operation to apply on the subset of a coordinate tuple.
     * @param  transform The {@linkplain MathTransformFactory#createPassThroughTransform
     *                   pass through transform}.
     */
    public DefaultPassThroughOperation(final Map<String,?>            properties,
                                       final CoordinateReferenceSystem sourceCRS,
                                       final CoordinateReferenceSystem targetCRS,
                                       final SingleOperation           operation,
                                       final MathTransform             transform)
    {
        super(properties, sourceCRS, targetCRS, transform, new DefaultOperationMethod(transform));
        this.operation = operation;
        ensureNonNull("operation", operation);
        ensureValidDimension(operation.getSourceCRS(), transform.getSourceDimensions());
        ensureValidDimension(operation.getTargetCRS(), transform.getTargetDimensions());
    }

    /**
     * Ensures that the dimension of the specified CRS is not greater than the specified value.
     */
    private static void ensureValidDimension(final CoordinateReferenceSystem crs, final int dim) {
        if (crs.getCoordinateSystem().getDimension() > dim) {
            throw new IllegalArgumentException(); // TODO: provides a localized message.
        }
    }

    /**
     * Returns the GeoAPI interface implemented by this class.
     * The SIS implementation returns {@code PassThroughOperation.class}.
     *
     * {@note Subclasses usually do not need to override this method since GeoAPI does not define
     *        <code>PassThroughOperation</code> sub-interface. Overriding possibility is left mostly
     *        for implementors who wish to extend GeoAPI with their own set of interfaces.}
     *
     * @return {@code PassThroughOperation.class} or a user-defined sub-interface.
     */
    @Override
    public Class<? extends PassThroughOperation> getInterface() {
        return PassThroughOperation.class;
    }

    /**
     * Returns the operation to apply on the subset of a coordinate tuple.
     *
     * @return The operation.
     */
    @Override
    public SingleOperation getOperation() {
        return operation;
    }

    /**
     * Returns the ordered sequence of positive integers defining the positions in a source
     * coordinate tuple of the coordinates affected by this pass-through operation.
     *
     * @return Indices of the modified source coordinates.
     *
     * @todo Current version works only with Geotk implementation.
     */
    @Override
    public int[] getModifiedCoordinates() {
        if (!(transform instanceof PassThroughTransform)) {
            throw new UnsupportedImplementationException(transform.getClass());
        }
        return ((PassThroughTransform) transform).getModifiedCoordinates();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String formatTo(final Formatter formatter) {
        final String name = super.formatTo(formatter);
        try {
            final int[] ordinates = getModifiedCoordinates();
            for (int i=0; i<ordinates.length; i++) {
                formatter.append(ordinates[i]);
            }
        } catch (UnsupportedOperationException exception) {
            // No indices will be formatted.
            formatter.setInvalidWKT(this, exception);
        }
        formatter.append((org.apache.sis.io.wkt.FormattableObject) operation); // TODO
        return name;
    }
}
