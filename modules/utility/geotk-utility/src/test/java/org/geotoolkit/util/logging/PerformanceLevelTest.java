/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2010-2011, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2010-2011, Geomatys
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
package org.geotoolkit.util.logging;

import java.util.concurrent.TimeUnit;

import org.junit.*;
import static org.junit.Assert.*;
import static org.geotoolkit.util.logging.PerformanceLevel.*;


/**
 * Tests the {@link PerformanceLevel} class.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.16
 *
 * @since 3.16
 */
public final class PerformanceLevelTest {
    /**
     * Ensures that the duration are conform to the documentation.
     */
    @Test
    public void testGetMinDuration() {
        assertEquals(0,   PERFORMANCE.getMinDuration(TimeUnit.NANOSECONDS));
        assertEquals(100, SLOW       .getMinDuration(TimeUnit.MILLISECONDS));
        assertEquals(1,   SLOWER     .getMinDuration(TimeUnit.SECONDS));
        assertEquals(5,   SLOWEST    .getMinDuration(TimeUnit.SECONDS));
    }

    /**
     * Tests modifying the configuration.
     */
    @Test
    public void testSetMinDuration() {
        try {
            SLOW.setMinDuration(2, TimeUnit.SECONDS);
            assertEquals(0, PERFORMANCE.getMinDuration(TimeUnit.SECONDS));
            assertEquals(2, SLOW       .getMinDuration(TimeUnit.SECONDS));
            assertEquals(2, SLOWER     .getMinDuration(TimeUnit.SECONDS));
            assertEquals(5, SLOWEST    .getMinDuration(TimeUnit.SECONDS));

            SLOWEST.setMinDuration(1, TimeUnit.SECONDS);
            assertEquals(0, PERFORMANCE.getMinDuration(TimeUnit.SECONDS));
            assertEquals(1, SLOW       .getMinDuration(TimeUnit.SECONDS));
            assertEquals(1, SLOWER     .getMinDuration(TimeUnit.SECONDS));
            assertEquals(1, SLOWEST    .getMinDuration(TimeUnit.SECONDS));

            PERFORMANCE.setMinDuration(6, TimeUnit.SECONDS);
            assertEquals(0, PERFORMANCE.getMinDuration(TimeUnit.SECONDS));
            assertEquals(6, SLOW       .getMinDuration(TimeUnit.SECONDS));
            assertEquals(6, SLOWER     .getMinDuration(TimeUnit.SECONDS));
            assertEquals(6, SLOWEST    .getMinDuration(TimeUnit.SECONDS));
        } finally {
            SLOW   .setMinDuration(100, TimeUnit.MILLISECONDS);
            SLOWER .setMinDuration(1,   TimeUnit.SECONDS);
            SLOWEST.setMinDuration(5,   TimeUnit.SECONDS);
        }
    }

    /**
     * Tests the {@link PerformanceLevel#forDuration(long, TimeUnit)} method.
     */
    @Test
    public void testForDuration() {
        assertSame(SLOW,        forDuration(500, TimeUnit.MILLISECONDS));
        assertSame(SLOWER,      forDuration(2,   TimeUnit.SECONDS));
        assertSame(SLOWEST,     forDuration(6,   TimeUnit.SECONDS));
        assertSame(PERFORMANCE, forDuration(50,  TimeUnit.MILLISECONDS));
    }
}
