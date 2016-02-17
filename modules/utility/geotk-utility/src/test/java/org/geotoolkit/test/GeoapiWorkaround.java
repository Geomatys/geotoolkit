/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2013, Geomatys
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
package org.geotoolkit.test;

import org.opengis.metadata.extent.GeographicBoundingBox;
import org.opengis.test.Validators;
import org.opengis.test.metadata.ExtentValidator;

/**
 * Temporarily disable the check for GeographicBoundingBox until we can upgrade to next GeoAPI version.
 */
public final class GeoapiWorkaround extends ExtentValidator {
    private static final GeoapiWorkaround INSTANCE = new GeoapiWorkaround();

    private GeoapiWorkaround() {
        super(Validators.DEFAULT);
    }

    /**
     * Temporarily disable the check for GeographicBoundingBox until we can upgrade to next GeoAPI version.
     */
    @Override
    public void validate(GeographicBoundingBox object) {
    }

    /**
     * Installs the workaround.
     */
    public static void install() {
        Validators.DEFAULT.extent = INSTANCE;
    }
}
