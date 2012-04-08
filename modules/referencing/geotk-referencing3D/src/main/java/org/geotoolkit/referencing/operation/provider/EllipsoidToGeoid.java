/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2006-2012, Open Source Geospatial Foundation (OSGeo)
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

import java.util.Collections;
import net.jcip.annotations.Immutable;

import org.opengis.util.FactoryException;
import org.opengis.parameter.ParameterValueGroup;
import org.opengis.parameter.ParameterDescriptor;
import org.opengis.parameter.ParameterDescriptorGroup;
import org.opengis.parameter.ParameterNotFoundException;
import org.opengis.referencing.operation.MathTransform;
import org.opengis.referencing.operation.Transformation;

import org.geotoolkit.referencing.NamedIdentifier;
import org.geotoolkit.referencing.datum.DefaultGeodeticDatum;
import org.geotoolkit.referencing.operation.MathTransformProvider;
import org.geotoolkit.parameter.DefaultParameterDescriptor;
import org.geotoolkit.metadata.iso.citation.Citations;
import org.geotoolkit.resources.Vocabulary;
import org.geotoolkit.resources.Errors;

import static org.geotoolkit.parameter.Parameters.*;
import static org.geotoolkit.internal.referencing.Identifiers.createDescriptorGroup;
import static org.geotoolkit.referencing.operation.transform.EarthGravitationalModel.*;


/**
 * The provider for "<cite>Ellipsoid to Geoid</cite>" vertical transformation.
 * This transformation uses a Earth Gravitational Model.
 *
 * @author Martin Desruisseaux (IRD, Geomatys)
 * @version 3.20
 *
 * @since 2.3
 * @module
 */
@Immutable
public class EllipsoidToGeoid extends MathTransformProvider {
    /**
     * Serial number for inter-operability with different versions.
     */
    private static final long serialVersionUID = 914369333205211248L;

    /**
     * The operation parameter descriptor for the datum.
     * Valid values are {@code "WGS84"} and {@code "WGS72"}.
     *
     * @deprecated Invoke <code>{@linkplain #PARAMETERS}.{@linkplain ParameterDescriptorGroup#descriptor(String)
     * descriptor(String)}</code> instead.
     */
    public static final ParameterDescriptor<String> DATUM = new DefaultParameterDescriptor<>(
            Collections.singletonMap(NAME_KEY, new NamedIdentifier(Citations.GEOTOOLKIT,
                    Vocabulary.formatInternational(Vocabulary.Keys.DATUM))),
            String.class, new String[] {"WGS84", "WGS72"}, "WGS84", null, null, null, true);

    /**
     * The operation parameter descriptor for the maximum degree and order. The default value is
     * {@value org.geotoolkit.referencing.operation.transform.EarthGravitationalModel#DEFAULT_ORDER}.
     *
     * @deprecated Invoke <code>{@linkplain #PARAMETERS}.{@linkplain ParameterDescriptorGroup#descriptor(String)
     * descriptor(String)}</code> instead.
     */
    public static final ParameterDescriptor<Integer> ORDER = DefaultParameterDescriptor.create(
            Collections.singletonMap(NAME_KEY, new NamedIdentifier(Citations.GEOTOOLKIT,
                    Vocabulary.formatInternational(Vocabulary.Keys.ORDER))),
            DEFAULT_ORDER, 2, 180, false);

    /**
     * The parameters group.
     */
    public static final ParameterDescriptorGroup PARAMETERS = createDescriptorGroup(new NamedIdentifier[] {
            new NamedIdentifier(Citations.GEOTOOLKIT, "Ellipsoid_To_Geoid")
        }, null, new ParameterDescriptor<?>[] {
            DATUM, ORDER
        });

    /**
     * Constructs a provider.
     */
    public EllipsoidToGeoid() {
        super(3, 3, PARAMETERS);
    }

    /**
     * Returns the operation type.
     */
    @Override
    public Class<? extends Transformation> getOperationType() {
        return Transformation.class;
    }

    /**
     * Creates a math transform from the specified group of parameter values.
     *
     * @param  values The group of parameter values.
     * @return The created math transform.
     * @throws ParameterNotFoundException if a required parameter was not found.
     * @throws FactoryException if this method failed to load the coefficient file.
     */
    @Override
    protected MathTransform createMathTransform(final ParameterValueGroup values)
            throws ParameterNotFoundException, FactoryException
    {
        final DefaultGeodeticDatum datum;
        final String name = stringValue(DATUM, values);
        if ("WGS84".equalsIgnoreCase(name)) {
            datum = DefaultGeodeticDatum.WGS84;
        } else if ("WGS72".equalsIgnoreCase(name)) {
            datum = DefaultGeodeticDatum.WGS72;
        } else {
            throw new IllegalArgumentException(Errors.format(Errors.Keys.UNSUPPORTED_DATUM_$1, name));
        }
        final Integer order = integerValue(ORDER, values);
        int nmax = (order != null) ? order : DEFAULT_ORDER;
        return create(datum, nmax);
    }
}
