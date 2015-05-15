/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2009-2012, Open Source Geospatial Foundation (OSGeo)
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
package org.geotoolkit.referencing.factory;

import java.util.Iterator;
import javax.imageio.spi.ServiceRegistry;
import org.opengis.referencing.datum.DatumFactory;
import org.geotoolkit.factory.Hints;
import org.geotoolkit.factory.FactoryFinder;

import org.junit.*;
import static org.junit.Assert.*;


/**
 * Tests the {@link FactoryFinder} class.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.03
 *
 * @since 3.03
 */
public final strictfp class FactoryFinderTest {
    /**
     * Tests the search of factory using default hints. We will search {@link DatumFactory}
     * for testing purpose because there is at least two implementations of it in Geotk and
     * their ordering matter.
     */
    @Test
    public void testDefault() {
        final Hints hints = null;
        final Iterator<DatumFactory> it = FactoryFinder.getDatumFactories(hints).iterator();
        assertTrue ("hasNext", it.hasNext()); assertTrue("next", it.next() instanceof DatumAliases);
        assertTrue ("hasNext", it.hasNext()); assertTrue("next", it.next() instanceof ReferencingObjectFactory);
        assertFalse("hasNext", it.hasNext());
    }

    /**
     * Tests the application of filters. We ask for {@link ReferencingFactory} subclasses
     * only.
     * <p>
     * Note that we can't use {@link Hints#DATUM_FACTORY} since {@code ReferencingFactory}
     * does not implement {@link DatumFactory}, thus is an illegal value for that key.
     */
    @Test
    public void testFilter() {
        final Hints hints = new Hints(FactoryFinder.FILTER_KEY, new ServiceRegistry.Filter() {
            @Override public boolean filter(final Object provider) {
                return provider instanceof ReferencingFactory;
            }
        });
        final Iterator<DatumFactory> it = FactoryFinder.getDatumFactories(hints).iterator();
        assertTrue ("hasNext", it.hasNext()); assertTrue("next", it.next() instanceof DatumAliases);
        assertFalse("hasNext", it.hasNext());
    }
}
