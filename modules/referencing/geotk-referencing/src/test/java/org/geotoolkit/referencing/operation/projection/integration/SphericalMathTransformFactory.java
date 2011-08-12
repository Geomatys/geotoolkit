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
package org.geotoolkit.referencing.operation.projection.integration;

import java.util.Set;
import org.opengis.util.FactoryException;
import org.opengis.util.NoSuchIdentifierException;
import org.opengis.parameter.ParameterValueGroup;
import org.opengis.referencing.cs.CoordinateSystem;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.Matrix;
import org.opengis.referencing.operation.MathTransform;
import org.opengis.referencing.operation.MathTransformFactory;
import org.opengis.referencing.operation.OperationMethod;
import org.opengis.referencing.operation.SingleOperation;

import org.geotoolkit.factory.FactoryFinder;
import org.geotoolkit.referencing.factory.ReferencingFactory;


/**
 * For internal usage by {@link SphericalGeoapiTest} only.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.19
 *
 * @since 3.19
 */
final class SphericalMathTransformFactory extends ReferencingFactory implements MathTransformFactory {
    /**
     * The original factory on which to delegate the operations.
     */
    private final MathTransformFactory factory;

    /**
     * Creates a new factory which will delegates all operations to the default factory,
     * except for parameterized transforms which will be forced to spherical formulas.
     */
    SphericalMathTransformFactory() {
        this.factory = FactoryFinder.getMathTransformFactory(null);
    }

    @Override
    public Set<OperationMethod> getAvailableMethods(Class<? extends SingleOperation> type) {
        return factory.getAvailableMethods(type);
    }

    @Override
    public OperationMethod getLastMethodUsed() {
        return factory.getLastMethodUsed();
    }

    @Override
    public ParameterValueGroup getDefaultParameters(String method) throws NoSuchIdentifierException {
        return factory.getDefaultParameters(method);
    }

    @Override
    public MathTransform createBaseToDerived(CoordinateReferenceSystem baseCRS, ParameterValueGroup parameters, CoordinateSystem derivedCS) throws FactoryException {
        return factory.createBaseToDerived(baseCRS, parameters, derivedCS);
    }

    @Override
    public MathTransform createParameterizedTransform(ParameterValueGroup parameters) throws FactoryException {
        parameters.parameter("semi-minor axis").setValue(parameters.parameter("semi-major axis").doubleValue());
        return factory.createParameterizedTransform(parameters);
    }

    @Override
    public MathTransform createAffineTransform(Matrix matrix) throws FactoryException {
        return factory.createAffineTransform(matrix);
    }

    @Override
    public MathTransform createConcatenatedTransform(MathTransform transform1, MathTransform transform2) throws FactoryException {
        return factory.createConcatenatedTransform(transform1, transform2);
    }

    @Override
    public MathTransform createPassThroughTransform(int firstAffectedOrdinate, MathTransform subTransform, int numTrailingOrdinates) throws FactoryException {
        return factory.createPassThroughTransform(firstAffectedOrdinate, subTransform, numTrailingOrdinates);
    }

    @Override
    public MathTransform createFromXML(String xml) throws FactoryException {
        return factory.createFromXML(xml);
    }

    @Override
    public MathTransform createFromWKT(String wkt) throws FactoryException {
        return factory.createFromWKT(wkt);
    }
}
