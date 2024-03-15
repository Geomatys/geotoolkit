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

import org.opengis.filter.Filter;
import org.junit.Test;
import org.opengis.filter.ResourceId;
import org.apache.sis.feature.privy.AttributeConvention;
import static org.junit.Assert.*;
import static org.geotoolkit.filter.FilterTestConstants.*;

/**
 * Test FIDFixVisitor
 *
 * @author Johann Sorel (Geomatys)
 */
public class FIDFixVisitorTest {

    @Test
    public void testReplacement1(){
        Filter filter = FF.equal(FF.property(AttributeConvention.IDENTIFIER), FF.literal("river.1"));

        filter = (Filter) FIDFixVisitor.INSTANCE.visit(filter);

        assertNotNull(filter);
        assertTrue(filter instanceof ResourceId);

        ResourceId fid = (ResourceId) filter;

        assertEquals("river.1", fid.getIdentifier());
    }

    @Test
    public void testReplacement2(){
        Filter filter = FF.equal(FF.literal("river.1"),
                FF.property(AttributeConvention.IDENTIFIER));

        filter = (Filter) FIDFixVisitor.INSTANCE.visit(filter);

        assertNotNull(filter);
        assertTrue(filter instanceof ResourceId);

        ResourceId fid = (ResourceId) filter;

        assertEquals("river.1", fid.getIdentifier());
    }
}
