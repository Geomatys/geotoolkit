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
import org.opengis.filter.Id;
import org.opengis.filter.PropertyIsEqualTo;
import org.opengis.filter.expression.Expression;
import static org.geotoolkit.test.Assert.*;
import static org.geotoolkit.filter.FilterTestConstants.*;

/**
 * Test FIDFixVisitor
 * 
 * @author Johann Sorel (Geomatys)
 */
public class FIDFixVisitorTest {
    
    @Test
    public void testReplacement1(){
        
        Filter filter = FF.equals(FF.property("@id"),FF.literal("river.1"));
        
        FIDFixVisitor visitor = new FIDFixVisitor();
        filter = (Filter) filter.accept(visitor, null);
        
        assertNotNull(filter);
        assertTrue(filter instanceof Id);
        
        Id fid = (Id) filter;
        
        assertEquals(1, fid.getIdentifiers().size());
        assertEquals("river.1", fid.getIdentifiers().iterator().next().getID());
        
    }
    
    @Test
    public void testReplacement2(){
        
        Filter filter = FF.equals(FF.literal("river.1"),FF.property("@id"));
        
        FIDFixVisitor visitor = new FIDFixVisitor();
        filter = (Filter) filter.accept(visitor, null);
        
        assertNotNull(filter);
        assertTrue(filter instanceof Id);
        
        Id fid = (Id) filter;
        
        assertEquals(1, fid.getIdentifiers().size());
        assertEquals("river.1", fid.getIdentifiers().iterator().next().getID());
        
    }
    
}
