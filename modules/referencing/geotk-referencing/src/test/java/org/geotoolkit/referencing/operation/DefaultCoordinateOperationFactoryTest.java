/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2009, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2009, Geomatys
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

import org.geotoolkit.factory.FactoryFinder;
import org.geotoolkit.factory.Hints;


/**
 * Same tests than {@link CoordinateOperationFactoryTest}, but the EPSG factory disabled.
 * Consequently this class usually tests {@link DefaultCoordinateOperationFactory}, while
 * the superclass tests whetever {@link CoordinateOperationFactory} implementation is found
 * on the classpath.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.07
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
}
