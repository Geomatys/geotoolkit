/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
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
package org.geotoolkit.storage.feature.query;

import org.geotoolkit.filter.FilterUtilities;
import org.geotoolkit.util.NamesExt;
import static org.junit.Assert.*;
import org.junit.Test;
import org.locationtech.jts.geom.GeometryFactory;
import org.opengis.filter.Filter;
import org.opengis.filter.FilterFactory;
import org.opengis.filter.SortOrder;
import org.opengis.filter.SortProperty;
import org.opengis.util.GenericName;

/**
 * Test query builder.
 *
 * @author Johann Sorel (Geomatys)
 */
public class QueryTest {

    private static final FilterFactory FF = FilterUtilities.FF;
    private static final GeometryFactory GF = org.geotoolkit.geometry.jts.JTS.getFactory();
    private static final double DELTA = 0.00001;

    /**
     * test static methods from querybuilder
     */
    @Test
    public void testStaticQueryBuilder() throws Exception {
        Query query = null;
        GenericName name = NamesExt.create("http://test.org", "testLocal");

        //test null values------------------------------------------------------
        try {
            new Query((GenericName) null);
            fail("We can not build a query without at least the type name.");
        } catch(NullPointerException ex) {
            //ok
        }

        //all-------------------------------------------------------------------
        query = new Query(name);
        assertEquals(query.getTypeName(), name.toString());
        assertEquals(query.getSelection(), Filter.include());
        assertFalse(query.getLimit().isPresent());
        assertArrayEquals(query.getPropertyNames(), null);
        assertArrayEquals(QueryUtilities.getSortProperties(query.getSortBy()), new SortProperty[0]);
        assertEquals(query.getOffset(), 0);
    }

    /**
     * test querybuilder
     */
    @Test
    public void testQueryBuilder() throws Exception {
        GenericName name = NamesExt.create("http://test.org", "testLocal");
        Query query = null;
        Query query2 = null;

        Query qb = new Query();

        //test all parameters---------------------------------------------------
        qb.setTypeName(name);
        qb.setSelection(Filter.exclude());
        qb.setLimit(10);
        qb.setProperties(new String[]{"att1","att2"});
        qb.setSortBy(new SortProperty[]{FF.sort(FF.property("att1"), SortOrder.DESCENDING)});
        qb.setOffset(5);
        query = qb;

        assertEquals(query.getTypeName(), name.toString());
        assertEquals(query.getSelection(), Filter.exclude());
        assertEquals(query.getLimit().getAsLong(), 10l);
        assertEquals(query.getPropertyNames()[0], "att1");
        assertEquals(query.getPropertyNames()[1], "att2");
        assertEquals(QueryUtilities.getSortProperties(query.getSortBy())[0], FF.sort(FF.property("att1"), SortOrder.DESCENDING));
        assertEquals(query.getOffset(), 5);

        query2 = query;
    }

}
