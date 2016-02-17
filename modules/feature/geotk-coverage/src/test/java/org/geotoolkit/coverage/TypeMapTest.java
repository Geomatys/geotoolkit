/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2010-2012, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2010-2012, Geomatys
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
package org.geotoolkit.coverage;

import org.geotoolkit.math.NumberSet;

import org.junit.*;
import static org.junit.Assert.*;
import static org.opengis.coverage.SampleDimensionType.*;


/**
 * Tests the {@link TypeMap} implementation.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.10
 *
 * @since 3.10
 */
public final strictfp class TypeMapTest extends org.geotoolkit.test.TestBase {
    /**
     * Tests {@link TypeMap#getSampleDimensionType}
     */
    @Test
    public void testGetSampleDimensionType() {
        assertEquals(UNSIGNED_1BIT,   TypeMap.getSampleDimensionType(NumberSet.NATURAL,  1));
        assertEquals(SIGNED_8BITS,    TypeMap.getSampleDimensionType(NumberSet.INTEGER,  1));
        assertEquals(UNSIGNED_16BITS, TypeMap.getSampleDimensionType(NumberSet.NATURAL, 12));
        assertEquals(SIGNED_16BITS,   TypeMap.getSampleDimensionType(NumberSet.INTEGER, 12));
        assertEquals(REAL_32BITS,     TypeMap.getSampleDimensionType(NumberSet.REAL,    12));
    }
}
