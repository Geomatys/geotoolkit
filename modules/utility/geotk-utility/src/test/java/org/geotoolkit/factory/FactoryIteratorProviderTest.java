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
package org.geotoolkit.factory;

import org.junit.*;
import static org.junit.Assert.*;
import org.apache.sis.test.DependsOn;


/**
 * Tests {@link FactoryIteratorProvider} addition in {@link FactoryIteratorProviders}.
 *
 * @author Martin Desruisseaux (IRD)
 * @version 3.00
 *
 * @since 2.4
 */
@DependsOn(FactoryRegistryTest.class)
public final strictfp class FactoryIteratorProviderTest {
    /**
     * The registry to use for testing purpose.
     */
    private FactoryRegistry registry;

    /**
     * Creates a new, initially empty, factory registry.
     */
    @Before
    public void createRegistry() {
        registry = new FactoryRegistry(DummyFactory.class);
    }

    /**
     * Returns a factory from the specified registry.
     *
     * @throws FactoryNotFoundException if no factory was found.
     * @throws FactoryRegistryException if a factory can't be returned for some other reason.
     */
    private static DummyFactory getFactory(final FactoryRegistry registry,
                                           final Class<? extends DummyFactory> type)
            throws FactoryRegistryException
    {
        Hints hints = null;
        if (type != null) {
            hints = new Hints(DummyFactory.DUMMY_FACTORY, type);
        }
        return registry.getServiceProvider(DummyFactory.class,
                null, hints, DummyFactory.DUMMY_FACTORY);
    }

    /**
     * Tests the registration of {@link DummyFactory} instances from
     * {@link DummyFactoryIteratorProvider}.
     */
    @Test
    public void testRegistration() {
        /*
         * Tests the initially empty factory.
         */
        try {
            assertNotNull(getFactory(registry, null));
            fail("No factory should have been found.");
        } catch (FactoryNotFoundException e) {
            // This is the expected exception.
        }
        /*
         * Tests after the addition of a new provider.
         */
        final FactoryIteratorProvider provider1 = new DummyFactoryIteratorProvider(true);
        try {
            Factories.addFactoryIteratorProvider(provider1);
            assertNotNull(getFactory(registry, null));
            DummyFactory factory;

            factory = getFactory(registry, Example1.class);
            assertEquals(Example1.class, factory.getClass());
            factory = getFactory(registry, Example2.class);
            assertEquals(Example2.class, factory.getClass());
            try {
                assertNotNull(getFactory(registry, Example3.class));
                fail("Example #3 should not be registered.");
            } catch (FactoryNotFoundException e) {
                // This is the expected exception.
            }
            try {
                assertNotNull(getFactory(registry, Example4.class));
                fail("Example #4 should not be registered.");
            } catch (FactoryNotFoundException e) {
                // This is the expected exception.
            }
            /*
             * Add yet an other provider, and test again.
             */
            final FactoryIteratorProvider provider2 = new DummyFactoryIteratorProvider(false);
            try {
                Factories.addFactoryIteratorProvider(provider2);
                factory = getFactory(registry, Example1.class);
                assertEquals(Example1.class, factory.getClass());
                factory = getFactory(registry, Example2.class);
                assertEquals(Example2.class, factory.getClass());
                factory = getFactory(registry, Example3.class);
                assertEquals(Example3.class, factory.getClass());
                factory = getFactory(registry, Example4.class);
                assertEquals(Example4.class, factory.getClass());
            } finally {
                Factories.removeFactoryIteratorProvider(provider2);
            }
        } finally {
            Factories.removeFactoryIteratorProvider(provider1);
        }
        /*
         * Tests with a new registry, which should be empty.
         */
        createRegistry();
        try {
            assertNotNull(getFactory(registry, null));
            fail("The factory should be empty again.");
        } catch (FactoryNotFoundException e) {
            // This is the expected exception.
        }
    }
}
