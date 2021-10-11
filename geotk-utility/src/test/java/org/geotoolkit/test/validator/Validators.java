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

import org.opengis.test.ValidatorContainer;


/**
 * Specializes the GeoAPI validators with the addition of more restrictive tests.
 * Note that the validators are installed on a system-wide basis.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.20
 *
 * @since 3.20
 */
public final strictfp class Validators extends org.opengis.test.Validators {
    /**
     * Do not allow instantiation of this class.
     */
    private Validators() {
    }

    /**
     * Installs the Geotk validators.
     */
    static {
        install(DEFAULT);
    }

    /**
     * Installs the customized validators in the given container.
     *
     * @param container Where to install the validators.
     */
    private static void install(final ValidatorContainer container) {
        container.naming    = new NameValidator     (container);
        container.parameter = new ParameterValidator(container);
    }
}
