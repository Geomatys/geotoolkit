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

import java.util.List;
import org.opengis.util.LocalName;
import org.opengis.util.ScopedName;
import org.opengis.test.ValidatorContainer;

import static org.junit.Assert.*;


/**
 * Completes the name validations with the addition of more restrictive Geotk conditions.
 * Geotk requires the exact same instance where GeoAPI requires only instances that are
 * {@linkplain Object#equals(Object) equal}.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.20
 *
 * @since 3.20
 */
final strictfp class NameValidator extends org.opengis.test.util.NameValidator {
    /**
     * Creates a new validator instance.
     *
     * @param container The set of validators to use for validating other kinds of objects
     *                  (see {@linkplain #container field javadoc}).
     */
    public NameValidator(final ValidatorContainer container) {
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
    public void validate(final ScopedName object) {
        super.validate(object);
        if (object != null) {
            final List<? extends LocalName> parsedNames = object.getParsedNames();
            assertSame("ScopedName: head.scope shall be same than scope.", object.scope(), object.head().scope());
            assertSame("GenericName: head() shall be the first element in getParsedNames() list.", parsedNames.get(0), object.head());
            assertSame("GenericName: tip() shall be the last element in getParsedNames() list.",   parsedNames.get(parsedNames.size()-1), object.tip());
            assertSame("ScopedName: tip() and tail.tip() shall be the same.",   object.tip(),  object.tail().tip());
            assertSame("ScopedName: head() and path.head() shall be the same.", object.head(), object.path().head());
        }
    }
}
