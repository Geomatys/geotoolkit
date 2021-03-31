/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2012, Geomatys
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
package org.geotoolkit.filter.visitor;

import java.util.Arrays;
import java.util.List;
import org.opengis.filter.Filter;
import org.junit.Test;

import static org.junit.Assert.*;
import static org.geotoolkit.filter.FilterTestConstants.*;
import org.opengis.filter.LogicalOperator;

/**
 * Test simplifying filter visitor
 *
 * @author Johann Sorel (Geomatys)
 */
public class SimplifyFilterVisitorTest extends org.geotoolkit.test.TestBase {

    @Test
    public void testIdRegroup(){
        final Filter id1 = FF.resourceId("123");
        final Filter id2 = FF.or(Arrays.asList(FF.resourceId("456"), FF.resourceId("789")));
        final Filter id3 = FF.resourceId("789");
        final Filter or = FF.or(Arrays.<Filter<? super Object>>asList(id1, id2, id3));

        SimplifyingFilterVisitor visitor = SimplifyingFilterVisitor.INSTANCE;
        final Filter res = (Filter) visitor.visit(or);

        assertTrue(res instanceof LogicalOperator);

        final List ids = ((LogicalOperator) res).getOperands();
        assertEquals(3, ids.size());

        assertTrue(ids.contains(id1));
        assertTrue(ids.contains(id3));
    }
}
