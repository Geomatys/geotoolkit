/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2002-2012, Open Source Geospatial Foundation (OSGeo)
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
package org.geotoolkit.referencing.operation.provider;

import javax.measure.unit.Unit;
import net.jcip.annotations.Immutable;

import org.opengis.parameter.ParameterValueGroup;
import org.opengis.parameter.ParameterDescriptor;
import org.opengis.parameter.ParameterDescriptorGroup;
import org.opengis.parameter.ParameterNotFoundException;
import org.opengis.referencing.operation.Conversion;
import org.opengis.referencing.operation.MathTransform1D;

import org.geotoolkit.parameter.DefaultParameterDescriptor;
import org.geotoolkit.referencing.NamedIdentifier;
import org.geotoolkit.referencing.operation.MathTransformProvider;
import org.geotoolkit.referencing.operation.transform.ExponentialTransform1D;
import org.geotoolkit.metadata.iso.citation.Citations;
import org.geotoolkit.resources.Vocabulary;

import static org.geotoolkit.parameter.Parameters.*;
import static org.geotoolkit.internal.referencing.Identifiers.createDescriptorGroup;


/**
 * The provider for {@link ExponentialTransform1D}.
 *
 * @author Martin Desruisseaux (IRD, Geomatys)
 * @version 3.20
 *
 * @see ExponentialTransform1D
 *
 * @since 2.0
 * @module
 */
@Immutable
public class Exponential extends MathTransformProvider {
    /**
     * Serial number for inter-operability with different versions.
     */
    private static final long serialVersionUID = -5838840021166379987L;

    /**
     * The operation parameter descriptor for the {@linkplain ExponentialTransform1D#base base}
     * parameter value. Valid values range from 0 to infinity. The default value is 10.
     */
    public static final ParameterDescriptor<Double> BASE = Logarithmic.BASE;

    /**
     * The operation parameter descriptor for the {@linkplain ExponentialTransform1D#scale scale}
     * parameter value. Valid values range is unrestricted. The default value is 1.
     */
    public static final ParameterDescriptor<Double> SCALE = DefaultParameterDescriptor.create(
            "scale", 1, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY, Unit.ONE);

    /**
     * The parameters group.
     */
    public static final ParameterDescriptorGroup PARAMETERS = createDescriptorGroup(
            new NamedIdentifier[] {
                new NamedIdentifier(Citations.GEOTOOLKIT, Vocabulary.formatInternational(Vocabulary.Keys.EXPONENTIAL))
            }, null, new ParameterDescriptor<?>[] {
                BASE, SCALE
            });

    /**
     * Creates a provider for logarithmic transforms.
     */
    public Exponential() {
        super(1, 1, PARAMETERS);
    }

    /**
     * Returns the operation type.
     */
    @Override
    public Class<Conversion> getOperationType() {
        return Conversion.class;
    }

    /**
     * Creates a logarithmic transform from the specified group of parameter values.
     *
     * @param  values The group of parameter values.
     * @return The created math transform.
     * @throws ParameterNotFoundException if a required parameter was not found.
     */
    @Override
    protected MathTransform1D createMathTransform(final ParameterValueGroup values)
            throws ParameterNotFoundException
    {
        return ExponentialTransform1D.create(doubleValue(BASE,  values),
                                             doubleValue(SCALE, values));
    }
}
