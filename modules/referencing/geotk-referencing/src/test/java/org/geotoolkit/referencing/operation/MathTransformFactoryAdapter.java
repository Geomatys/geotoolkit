/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2011, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2011, Geomatys
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
package org.geotoolkit.referencing.operation;

import java.util.Set;
import java.util.Collections;
import org.opengis.util.FactoryException;
import org.opengis.util.NoSuchIdentifierException;
import org.opengis.metadata.citation.Citation;
import org.opengis.parameter.ParameterValueGroup;
import org.opengis.referencing.cs.CoordinateSystem;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.Matrix;
import org.opengis.referencing.operation.OperationMethod;
import org.opengis.referencing.operation.SingleOperation;
import org.opengis.referencing.operation.MathTransform;
import org.opengis.referencing.operation.MathTransformFactory;
import org.geotoolkit.metadata.iso.citation.Citations;


/**
 * Skeleton for {@link MathTransformFactory} custom implementations.
 * Implementors need to override at least one method.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.19
 *
 * @since 3.19
 */
public strictfp class MathTransformFactoryAdapter implements MathTransformFactory {
    /**
     * The message for all exception.
     */
    private static final String MESSAGE = "Undefined by the test suite.";

    /**
     * For subclasses constructor only.
     */
    protected MathTransformFactoryAdapter() {
    }

    /** Returns the Geotk citation. */
    @Override
    public Citation getVendor() {
        return Citations.GEOTOOLKIT;
    }

    /** Default implementation returns an empty set. */
    @Override
    public Set<OperationMethod> getAvailableMethods(Class<? extends SingleOperation> type) {
        return Collections.emptySet();
    }

    /** Default implementation unconditionally returns {@code null}. */
    @Override
    public OperationMethod getLastMethodUsed() {
        return null;
    }

    /** Default implementation throws an exception. */
    @Override
    public ParameterValueGroup getDefaultParameters(String method) throws NoSuchIdentifierException {
        throw new NoSuchIdentifierException(MESSAGE, method);
    }

    /** Default implementation throws an exception. */
    @Override
    public MathTransform createBaseToDerived(CoordinateReferenceSystem baseCRS, ParameterValueGroup parameters, CoordinateSystem derivedCS) throws FactoryException {
        throw new FactoryException(MESSAGE);
    }

    /** Default implementation throws an exception. */
    @Override
    public MathTransform createParameterizedTransform(ParameterValueGroup parameters) throws FactoryException {
        throw new FactoryException(MESSAGE);
    }

    /** Default implementation throws an exception. */
    @Override
    public MathTransform createAffineTransform(Matrix matrix) throws FactoryException {
        throw new FactoryException(MESSAGE);
    }

    /** Default implementation throws an exception. */
    @Override
    public MathTransform createConcatenatedTransform(MathTransform transform1, MathTransform transform2) throws FactoryException {
        throw new FactoryException(MESSAGE);
    }

    /** Default implementation throws an exception. */
    @Override
    public MathTransform createPassThroughTransform(int firstAffectedOrdinate, MathTransform subTransform, int numTrailingOrdinates) throws FactoryException {
        throw new FactoryException(MESSAGE);
    }

    /** Default implementation throws an exception. */
    @Override
    public MathTransform createFromXML(String xml) throws FactoryException {
        throw new FactoryException(MESSAGE);
    }

    /** Default implementation throws an exception. */
    @Override
    public MathTransform createFromWKT(String wkt) throws FactoryException {
        throw new FactoryException(MESSAGE);
    }
}
