/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2002-2008, Open Source Geospatial Foundation (OSGeo)
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
package org.geotoolkit.filter.binarylogic;

import java.util.ArrayList;
import java.util.List;
import org.junit.Test;

import org.opengis.filter.Filter;

import static org.junit.Assert.*;
import static org.apache.sis.test.Assertions.assertSerializedEquals;
import static org.geotoolkit.filter.FilterTestConstants.*;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public class BinaryLogicTest extends org.geotoolkit.test.TestBase {
    @Test
    public void testAnd() {
        Filter filter1 = FF.equal(FF.property("testInteger"), FF.literal(101));
        Filter filter2 = FF.equal(FF.property("testGeometry"), FF.literal(RIGHT_GEOMETRY));
        Filter filter3 = FF.equal(FF.property("testString"), FF.literal("test string data"));

        List<Filter<Object>> filters = new ArrayList<>();
        filters.add(filter1);
        filters.add(filter2);
        filters.add(filter3);

        Filter and = FF.and(filters);
        assertTrue( and.test(CANDIDATE_1) );

        Filter filter4 = FF.equal(FF.property("testGeometry"), FF.literal(WRONG_GEOMETRY));
        filters.add(filter4);
        and = FF.and(filters);
        assertFalse( and.test(CANDIDATE_1) );

        filters.clear();
        filters.add(filter1);
        filters.add(filter3);
        and = FF.and(filters);
        assertSerializedEquals(and); //test serialize
    }

    @Test
    public void testOr() {
        Filter filter1 = FF.equal(FF.property("testInteger"), FF.literal(101));
        Filter filter2 = FF.equal(FF.property("testGeometry"), FF.literal(RIGHT_GEOMETRY));
        Filter filter3 = FF.equal(FF.property("testString"), FF.literal("test string data"));

        List<Filter<Object>> filters = new ArrayList<>();
        filters.add(filter1);
        filters.add(filter2);
        filters.add(filter3);

        Filter or = FF.or(filters);
        assertTrue( or.test(CANDIDATE_1) );

        Filter filter4 = FF.equal(FF.property("testGeometry"), FF.literal(WRONG_GEOMETRY));
        Filter filter5 = FF.equal(FF.property("testInteger"), FF.literal(312.23));
        filters.add(filter4);
        filters.add(filter5);
        or = FF.or(filters);
        assertTrue( or.test(CANDIDATE_1) );

        filters.clear();
        filters.add(filter4);
        filters.add(filter5);
        or = FF.or(filters);
        assertFalse( or.test(CANDIDATE_1) );

        filters.clear();
        filters.add(filter1);
        filters.add(filter3);
        or = FF.or(filters);
        assertSerializedEquals(or); //test serialize
    }
}
