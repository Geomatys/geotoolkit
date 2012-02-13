/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2012, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2012, Geomatys
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
package org.geotoolkit.test.validator;

import org.opengis.parameter.ParameterValue;
import org.opengis.parameter.ParameterValueGroup;
import org.opengis.parameter.GeneralParameterValue;
import org.opengis.parameter.GeneralParameterDescriptor;
import org.opengis.parameter.ParameterDescriptorGroup;
import org.opengis.test.ValidatorContainer;

import static org.junit.Assert.*;


/**
 * Completes the parameter validations with the addition of more restrictive Geotk conditions.
 * Geotk requires the exact same instance where GeoAPI requires only instances that are
 * {@linkplain Object#equals(Object) equal}.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.20
 *
 * @since 3.20
 */
final strictfp class ParameterValidator extends org.opengis.test.referencing.ParameterValidator {
    /**
     * Creates a new validator instance.
     *
     * @param container The set of validators to use for validating other kinds of objects
     *                  (see {@linkplain #container field javadoc}).
     */
    public ParameterValidator(final ValidatorContainer container) {
        super(container);
    }

    /**
     * Ensures that ISO 19103 or GeoAPI restrictions apply, then checks for yet more restrictive
     * Geotk conditions. This method requires the exact same instance where GeoAPI requires only
     * instances that are {@linkplain Object#equals(Object) equal}.
     *
     * @param object The object to validate, or {@code null}.
     */
    @Override
    public void validate(final ParameterDescriptorGroup object) {
        super.validate(object);
        if (object != null) {
            for (final GeneralParameterDescriptor descriptor : object.descriptors()) {
                assertSame("ParameterDescriptorGroup: descriptor(String) inconsistent with descriptors().",
                        descriptor, object.descriptor(descriptor.getName().getCode()));
            }
        }
    }

    /**
     * Ensures that ISO 19103 or GeoAPI restrictions apply, then checks for yet more restrictive
     * Geotk conditions. This method requires the exact same instance where GeoAPI requires only
     * instances that are {@linkplain Object#equals(Object) equal}.
     *
     * @param object The object to validate, or {@code null}.
     */
    @Override
    public void validate(final ParameterValueGroup object) {
        super.validate(object);
        if (object != null) {
            final ParameterDescriptorGroup descriptors = object.getDescriptor();
            for (final GeneralParameterValue value : object.values()) {
                final GeneralParameterDescriptor descriptor = value.getDescriptor();
                final String name = descriptor.getName().getCode();
                assertSame("ParameterValueGroup: descriptor(String) inconsistent with value.getDescriptor().",
                        descriptor, descriptors.descriptor(name));
                if (value instanceof ParameterValue<?>) {
                    assertSame("ParameterValueGroup: value(String) inconsistent with values().",
                            value, object.parameter(name));
                }
            }
        }
    }
}
