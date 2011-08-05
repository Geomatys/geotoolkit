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
package org.geotoolkit.test;

import java.io.IOException;
import java.util.Properties;
import org.opengis.util.Factory;
import org.opengis.referencing.AuthorityFactory;
import org.opengis.referencing.datum.DatumFactory;
import org.opengis.referencing.operation.CoordinateOperationFactory;


/**
 * Provides Geotk-specific information for GeoAPI test suite. Those information are provided
 * to the test suite in all Geotk module. It filters the factory in order to focus the tests
 * on only one class of interest.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.19
 *
 * @since 3.19
 */
public final class ImplementationDetails implements org.opengis.test.ImplementationDetails {
    /**
     * Returns {@code false} if the given factory is not a factory that we want to test,
     * and {@code true} in all other cases.
     */
    @Override
    public <T extends Factory> boolean filter(final Class<T> category, final T factory) {
        final String name = factory.getClass().getName();
        if (name.startsWith("org.geotools.") && name.endsWith("GeotoolsFactory")) {
            // The referencing module provides a dummy GeoTools factory for testing
            // the cohabitation of GeoTools and Geotk libraries. We don't want this
            // dummy implementation to be tested by GeoAPI.
            return false;
        }
        if (name.startsWith("org.geotoolkit.")) {
            if (DatumFactory.class.isAssignableFrom(category)) {
                if (!name.endsWith("DatumAliases")) {
                    return false;
                }
            }
            if (CoordinateOperationFactory.class.isAssignableFrom(category)) {
                if (!name.endsWith("CachingCoordinateOperationFactory")) {
                    return false;
                }
            }
            if (AuthorityFactory.class.isAssignableFrom(category)) {
                if (!name.endsWith("ThreadedEpsgFactory")) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * Unconditionally returns {@code null}, since we do not disable any test.
     */
    @Override
    public Properties configuration(final Factory... factories) throws IOException {
        return null;
    }
}
