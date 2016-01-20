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
package org.geotoolkit.referencing.operation;

import java.util.Map;
import java.util.HashMap;
import org.opengis.parameter.ParameterDescriptorGroup;
import org.opengis.parameter.GeneralParameterValue;
import org.opengis.referencing.IdentifiedObject;
import org.opengis.referencing.operation.MathTransform;
import org.apache.sis.util.ArgumentChecks;
import org.apache.sis.referencing.operation.DefaultOperationMethod;

import static org.geotoolkit.referencing.IdentifiedObjects.EMPTY_ALIAS_ARRAY;
import static org.geotoolkit.referencing.IdentifiedObjects.EMPTY_IDENTIFIER_ARRAY;


/**
 * An {@linkplain DefaultOperationMethod operation method} capable to create a
 * {@linkplain MathTransform math transform} from set of {@linkplain GeneralParameterValue
 * parameter values}. Implementations of this class should be listed in the following file
 * (see the {@linkplain org.geotoolkit.factory factory package} for more information about
 * how to manage providers registered in such files):
 *
 * {@preformat text
 *     META-INF/services/org.geotoolkit.referencing.operation.MathTransformProvider
 * }
 *
 * The {@linkplain DefaultMathTransformFactory default math transform factory} will parse the
 * above files in all JAR files in order to get all available providers on a system. In Geotk,
 * most providers are defined in the {@linkplain org.geotoolkit.referencing.operation.provider
 * provider sub-package}.
 *
 * @author Martin Desruisseaux (IRD)
 * @version 3.01
 *
 * @since 1.2
 * @level advanced
 * @module
 *
 * @see org.geotoolkit.referencing.operation.provider
 * @see org.geotoolkit.factory
 *
 * @deprecated To be removed after the port to SIS.
 */
@Deprecated
public abstract class MathTransformProvider extends DefaultOperationMethod
        implements org.apache.sis.referencing.operation.transform.MathTransformProvider
{
    /**
     * Serial number for inter-operability with different versions.
     */
    private static final long serialVersionUID = 7530475536803158473L;

    /**
     * Constructs a math transform provider from a set of parameters. The provider
     * {@linkplain #getIdentifiers identifiers} will be the same than the parameter
     * ones.
     *
     * @param sourceDimension Number of dimensions in the source CRS of this operation method.
     * @param targetDimension Number of dimensions in the target CRS of this operation method.
     * @param parameters The set of parameters (never {@code null}).
     */
    public MathTransformProvider(final int sourceDimension,
                                 final int targetDimension,
                                 final ParameterDescriptorGroup parameters)
    {
        super(toMap(parameters), sourceDimension, targetDimension, parameters);
    }

    /**
     * Work around for RFE #4093999 in Sun's bug database
     * ("Relax constraint on placement of this()/super() call in constructors").
     */
    private static Map<String,Object> toMap(final IdentifiedObject parameters) {
        ArgumentChecks.ensureNonNull("parameters", parameters);
        final Map<String,Object> properties = new HashMap<>(4);
        properties.put(NAME_KEY,        parameters.getName());
        properties.put(IDENTIFIERS_KEY, parameters.getIdentifiers().toArray(EMPTY_IDENTIFIER_ARRAY));
        properties.put(ALIAS_KEY,       parameters.getAlias()      .toArray(EMPTY_ALIAS_ARRAY));
        return properties;
    }
}
