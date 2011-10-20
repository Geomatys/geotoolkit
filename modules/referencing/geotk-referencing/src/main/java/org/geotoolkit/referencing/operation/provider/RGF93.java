/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2010-2011, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2010-2011, Geomatys
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
package org.geotoolkit.referencing.operation.provider;

import org.opengis.util.FactoryException;
import org.opengis.parameter.ParameterDescriptor;
import org.opengis.parameter.ParameterDescriptorGroup;
import org.opengis.parameter.ParameterValueGroup;
import org.opengis.referencing.ReferenceIdentifier;
import org.opengis.referencing.operation.MathTransform;
import org.opengis.referencing.operation.Transformation;

import org.geotoolkit.internal.referencing.Identifiers;
import org.geotoolkit.metadata.iso.citation.Citations;
import org.geotoolkit.parameter.Parameters;
import org.geotoolkit.parameter.DefaultParameterDescriptor;
import org.geotoolkit.referencing.NamedIdentifier;
import org.geotoolkit.referencing.operation.transform.NTv2Transform;
import org.geotoolkit.referencing.operation.MathTransformProvider;
import org.geotoolkit.resources.Errors;


/**
 * The provider for "<cite>France geocentric interpolation</cite>" (ESPG:9655).
 * The current implementation delegates to the emulation based on NTv2 method.
 *
 * @author Simon Reynard (Geomatys)
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.12
 *
 * @since 3.12
 * @module
 */
public class RGF93 extends MathTransformProvider {
    /**
     * Serial number for inter-operability with different versions.
     */
    private static final long serialVersionUID = 4049217192968903800L;

    /**
     * The operation parameter descriptor for the <cite>Geocentric translation file</cite>
     * parameter value. The default value is {@code "gr3df97a.txt"}.
     */
    public static final ParameterDescriptor<String> TRANSLATION_FILE = new DefaultParameterDescriptor<>(
            "Geocentric translation file", String.class, null, "gr3df97a.txt");

    /**
     * The parameters group.
     */
    public static final ParameterDescriptorGroup PARAMETERS = Identifiers.createDescriptorGroup(
        new ReferenceIdentifier[] {
            new NamedIdentifier(Citations.EPSG, "France geocentric interpolation"),
            new IdentifierCode (Citations.EPSG,  9655)
        }, new ParameterDescriptor<?>[] {
            TRANSLATION_FILE
        });

    /**
     * Constructs a provider.
     */
    public RGF93() {
        super(2, 2, PARAMETERS);
    }

    /**
     * Returns the operation type.
     */
    @Override
    public Class<Transformation> getOperationType() {
        return Transformation.class;
    }

    /**
     * Creates a math transform from the specified group of parameter values.
     *
     * @throws FactoryException If the grid files can not be loaded.
     */
    @Override
    protected MathTransform createMathTransform(final ParameterValueGroup values) throws FactoryException {
        final String file = Parameters.stringValue(TRANSLATION_FILE, values);
        if (!"gr3df97a.txt".equals(file)) {
            throw new FactoryException(Errors.format(Errors.Keys.CANT_READ_$1, file));
        }
        return new NTv2Transform(NTv2Transform.RGF93);
    }
}
