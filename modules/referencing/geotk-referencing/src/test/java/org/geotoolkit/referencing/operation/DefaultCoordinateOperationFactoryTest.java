/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2009-2010, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2009-2010, Geomatys
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

import javax.imageio.spi.ServiceRegistry;

import org.opengis.referencing.operation.CoordinateOperationFactory;
import org.opengis.referencing.operation.OperationNotFoundException;

import org.geotoolkit.factory.FactoryFinder;
import org.geotoolkit.factory.Hints;

import org.junit.Test;


/**
 * Same tests than {@link CoordinateOperationFactoryTest}, but with the EPSG factory disabled.
 * Consequently this class tests {@link DefaultCoordinateOperationFactory}, while the superclass
 * tests whatever {@link CoordinateOperationFactory} implementation is found on the classpath.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.16
 *
 * @since 3.07
 */
public final class DefaultCoordinateOperationFactoryTest extends CoordinateOperationFactoryTest {
    /**
     * Creates a new test suite.
     */
    public DefaultCoordinateOperationFactoryTest() {
        super(new Hints(FactoryFinder.FILTER_KEY, new ServiceRegistry.Filter() {
            @Override public boolean filter(final Object provider) {
                if (provider instanceof CoordinateOperationFactory) {
                    return isUsingDefaultFactory((CoordinateOperationFactory) provider);
                }
                return true;
            }
        }));
    }

    /**
     * Override a test which is not expected to work without EPSG database.
     */
    @Test
    @Override
    public void testProjected2D_withMeridianShift() throws Exception {
        try {
            super.testProjected4D_to2D_withMeridianShift();
            fail("Should not allow the transform without Bursa-Wolf parameters.");
        } catch (OperationNotFoundException e) {
            // This is the expected exception.
        }
    }
}
