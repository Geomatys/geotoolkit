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

import java.awt.RenderingHints;
import org.opengis.metadata.quality.ConformanceResult;

import org.junit.*;
import static org.junit.Assert.*;
import org.apache.sis.test.DependsOn;


/**
 * Tests {@link Factory}.
 *
 * @author Martin Desruisseaux (IRD, Geomatys)
 * @version 3.03
 *
 * @since 2.3
 */
@DependsOn(HintsTest.class)
public final strictfp class FactoryTest {
    /**
     * A key for testing purpose.
     */
    private static strictfp class Key extends RenderingHints.Key {
        Key(final int n) {
            super(n);
        }

        @Override
        public boolean isCompatibleValue(Object value) {
            return true;
        }

    }

    /**
     * Tests {@link Factory#equals}.
     */
    @Test
    public void testEquals() {
        final Key key1              = new Key(1 );
        final Key key2              = new Key(2 );
        final Key key3_reference_f1 = new Key(31);
        final Key key2_reference_f3 = new Key(23);
        final Key key1_reference_f2 = new Key(12);

        final Factory f1 = new EmptyFactory();
        final Factory f2 = new EmptyFactory();
        final Factory f3 = new EmptyFactory();
        f1.hints.put(key1, "Value 1");
        f2.hints.put(key2, "Value 2");
        f3.hints.put(key3_reference_f1, f1);
        f2.hints.put(key2_reference_f3, f3);
        f1.hints.put(key1_reference_f2, f2);

        assertFalse(f1.toString().isEmpty());

        assertEquals(f1, f1);
        assertEquals(f2, f2);
        assertEquals(f3, f3);
        assertFalse ("Expected different number of hints.",             f1.equals(f2));
        assertFalse ("Expected same number of hints, differerent key.", f1.equals(f3));
        assertFalse ("Expected different number of hints.",             f2.equals(f3));

        // Tests recursivity on a f2 --> f3 --> f1 --> f2 dependency graph.
        final Factory f1b = new EmptyFactory();
        final Factory f2b = new EmptyFactory();
        final Factory f3b = new EmptyFactory();
        f1b.hints.put(key1,        "Value 1");
        f2b.hints.put(key2,        "Value 2");
        f3b.hints.put(key3_reference_f1, f1b);
        f2b.hints.put(key2_reference_f3, f3b);
        f1b.hints.put(key1_reference_f2, f2b);
        assertEquals(f2, f2b);

        f1b.hints.put(key1, "Different value");
        assertFalse(f2.equals(f2b));
    }

    /**
     * Tests the {@link Factory#availability()} method.
     */
    @Test
    public void testAvailability() {
        final Factory factory = new EmptyFactory();
        final ConformanceResult availability = factory.availability();
        assertNotNull(availability);
        assertTrue(availability.pass());
        // The following tests getExplanation() as well, but we can't easily
        // check the result since it is implementation and locale dependent.
        assertNotNull(availability.toString());
    }
}
