/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2002-2011, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2009-2011, Geomatys
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

import java.awt.geom.AffineTransform;
import javax.measure.unit.NonSI;

import org.opengis.parameter.ParameterValueGroup;
import org.opengis.parameter.ParameterDescriptor;
import org.opengis.parameter.ParameterDescriptorGroup;
import org.opengis.parameter.ParameterNotFoundException;
import org.opengis.referencing.operation.Conversion;
import org.opengis.referencing.operation.MathTransform;
import org.opengis.referencing.ReferenceIdentifier;

import org.geotoolkit.lang.Immutable;
import org.geotoolkit.metadata.iso.citation.Citations;
import org.geotoolkit.referencing.NamedIdentifier;
import org.geotoolkit.referencing.operation.MathTransformProvider;
import org.geotoolkit.referencing.operation.transform.ProjectiveTransform;

import static org.geotoolkit.parameter.Parameters.*;
import static org.geotoolkit.internal.referencing.Identifiers.createDescriptor;
import static org.geotoolkit.internal.referencing.Identifiers.createDescriptorGroup;


/**
 * The provider for "<cite>Longitude rotation</cite>" (EPSG:9601).
 *
 * @author Martin Desruisseaux (IRD)
 * @version 3.00
 *
 * @see ProjectiveTransform
 *
 * @since 2.0
 * @module
 */
@Immutable
public class LongitudeRotation extends MathTransformProvider {
    /**
     * Serial number for inter-operability with different versions.
     */
    private static final long serialVersionUID = -2104496465933824935L;

    /**
     * The operation parameter descriptor for the "<cite>longitude offset</cite>" parameter value.
     */
    public static final ParameterDescriptor<Double> OFFSET = createDescriptor(
            new NamedIdentifier[] {
                new NamedIdentifier(Citations.EPSG, "Longitude offset")
            },
            Double.NaN, -180, +180, NonSI.DEGREE_ANGLE);

    /**
     * The parameters group.
     */
    public static final ParameterDescriptorGroup PARAMETERS = createDescriptorGroup(
            new ReferenceIdentifier[] {
                new NamedIdentifier(Citations.EPSG, "Longitude rotation"),
                new IdentifierCode (Citations.EPSG,  9601)
            }, new ParameterDescriptor<?>[] {
                OFFSET
            });

    /**
     * Constructs a provider with default parameters.
     */
    public LongitudeRotation() {
        super(2, 2, PARAMETERS);
    }

    /**
     * Returns the operation type.
     */
    @Override
    public Class<Conversion> getOperationType() {
        return Conversion.class;
    }

    /**
     * Creates a transform from the specified group of parameter values.
     *
     * @param  values The group of parameter values.
     * @return The created math transform.
     * @throws ParameterNotFoundException if a required parameter was not found.
     */
    @Override
    protected MathTransform createMathTransform(final ParameterValueGroup values)
            throws ParameterNotFoundException
    {
        final double offset = doubleValue(OFFSET, values);
        return ProjectiveTransform.create(AffineTransform.getTranslateInstance(offset, 0));
    }
}
