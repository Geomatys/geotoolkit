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


package org.geotoolkit.data.query;

import org.apache.sis.internal.system.DefaultFactories;
import org.apache.sis.referencing.CommonCRS;
import org.geotoolkit.util.NamesExt;
import org.junit.Test;
import org.opengis.filter.Filter;
import org.opengis.filter.FilterFactory;
import org.opengis.filter.sort.SortBy;
import org.opengis.filter.sort.SortOrder;
import org.opengis.util.GenericName;
import static org.junit.Assert.*;

/**
 * Test query builder.
 *
 * @author Johann Sorel (Geomatys)
 */
public class QueryTest {

    private static final FilterFactory FF = DefaultFactories.forBuildin(FilterFactory.class);
    private static final double DELTA = 0.00001;

    /**
     * test static methods from querybuilder
     */
    @Test
    public void testStaticQueryBuilder() throws Exception {
        Query query = null;
        GenericName name = NamesExt.create("http://test.org", "testLocal");

        //test null values------------------------------------------------------
        try{
            QueryBuilder.all((GenericName)null);
            throw new Exception("We can not build a query without at least the type name.");
        }catch(NullPointerException ex){
            //ok
        }

        try{
            QueryBuilder.fids(null);
            throw new Exception("We can not build a query without at least the type name.");
        }catch(NullPointerException ex){
            //ok
        }

        try{
            QueryBuilder.filtered(null, Filter.EXCLUDE);
            throw new Exception("We can not build a query without at least the type name.");
        }catch(NullPointerException ex){
            //ok
        }

        try{
            QueryBuilder.sorted(null, new SortBy[]{FF.sort("att1", SortOrder.DESCENDING)});
            throw new Exception("We can not build a query without at least the type name.");
        }catch(NullPointerException ex){
            //ok
        }




        //all-------------------------------------------------------------------
        query = QueryBuilder.all(name);
        assertEquals(query.getTypeName(), name.toString());
        assertEquals(query.getCoordinateSystemReproject(), null);
        assertEquals(query.getResolution(), null);
        assertEquals(query.getFilter(), Filter.INCLUDE);
        assertEquals(query.getMaxFeatures(), null);
        assertArrayEquals(query.getPropertyNames(), null);
        assertArrayEquals(query.getSortBy(), null);
        assertEquals(query.getStartIndex(), 0);

        //only ids--------------------------------------------------------------
        query = QueryBuilder.fids(name.toString());
        assertEquals(query.getTypeName(), name.toString());
        assertEquals(query.getCoordinateSystemReproject(), null);
        assertEquals(query.getResolution(), null);
        assertEquals(query.getFilter(), Filter.INCLUDE);
        assertEquals(query.getMaxFeatures(), null);
        assertNotNull(query.getPropertyNames()); //must be an empty array, not null
        assertTrue(query.getPropertyNames().length == 1); //must have only one value
        assertArrayEquals(query.getSortBy(), null);
        assertEquals(query.getStartIndex(), 0);

        //only filter-----------------------------------------------------------
        query = QueryBuilder.filtered(name.toString(), Filter.EXCLUDE);
        assertEquals(query.getTypeName(), name.toString());
        assertEquals(query.getCoordinateSystemReproject(), null);
        assertEquals(query.getResolution(), null);
        assertEquals(query.getFilter(), Filter.EXCLUDE);
        assertEquals(query.getMaxFeatures(), null);
        assertArrayEquals(query.getPropertyNames(), null);
        assertArrayEquals(query.getSortBy(), null);
        assertEquals(query.getStartIndex(), 0);

        //only sort by----------------------------------------------------------
        query = QueryBuilder.sorted(name.toString(), new SortBy[]{FF.sort("att1", SortOrder.DESCENDING)});
        assertEquals(query.getTypeName(), name.toString());
        assertEquals(query.getCoordinateSystemReproject(), null);
        assertEquals(query.getResolution(), null);
        assertEquals(query.getFilter(), Filter.INCLUDE);
        assertEquals(query.getMaxFeatures(), null);
        assertArrayEquals(query.getPropertyNames(), null);
        assertNotNull(query.getSortBy());
        assertTrue(query.getSortBy().length == 1);
        assertEquals(query.getSortBy()[0], FF.sort("att1", SortOrder.DESCENDING));
        assertEquals(query.getStartIndex(), 0);

    }

    /**
     * test querybuilder
     */
    @Test
    public void testQueryBuilder() throws Exception {
        GenericName name = NamesExt.create("http://test.org", "testLocal");
        Query query = null;
        Query query2 = null;

        //test no parameters----------------------------------------------------
        QueryBuilder qb = new QueryBuilder();
        try{
            query = qb.buildQuery();
            throw new Exception("We can not build a query without at least the type name.");
        }catch(NullPointerException ex){
            //ok
        }

        //test all parameters---------------------------------------------------
        qb.setTypeName(name);
        qb.setCRS(CommonCRS.WGS84.normalizedGeographic());
        qb.setResolution(new double[]{45,31});
        qb.setFilter(Filter.EXCLUDE);
        qb.setMaxFeatures(10);
        qb.setProperties(new String[]{"att1","att2"});
        qb.setSortBy(new SortBy[]{FF.sort("att1", SortOrder.DESCENDING)});
        qb.setStartIndex(5);
        query = qb.buildQuery();

        assertEquals(query.getTypeName(), name.toString());
        assertEquals(query.getCoordinateSystemReproject(), CommonCRS.WGS84.normalizedGeographic());
        assertEquals(query.getResolution()[0], 45d,DELTA);
        assertEquals(query.getResolution()[1], 31d,DELTA);
        assertEquals(query.getFilter(), Filter.EXCLUDE);
        assertEquals(query.getMaxFeatures(), Integer.valueOf(10));
        assertEquals(query.getPropertyNames()[0], "att1");
        assertEquals(query.getPropertyNames()[1], "att2");
        assertEquals(query.getSortBy()[0], FF.sort("att1", SortOrder.DESCENDING));
        assertEquals(query.getStartIndex(), 5);

        query2 = query;

        //test reset------------------------------------------------------------
        qb.reset();
        qb.setTypeName(name);
        query = qb.buildQuery();

        assertEquals(query.getTypeName(), name.toString());
        assertEquals(query.getCoordinateSystemReproject(), null);
        assertEquals(query.getResolution(), null);
        assertEquals(query.getFilter(), Filter.INCLUDE);
        assertEquals(query.getMaxFeatures(), null);
        assertArrayEquals(query.getPropertyNames(), null);
        assertArrayEquals(query.getSortBy(), null);
        assertEquals(query.getStartIndex(), 0);

        //test copy-------------------------------------------------------------
        qb.copy(query2);
        query = qb.buildQuery();

        assertEquals(query.getTypeName(), name.toString());
        assertEquals(query.getCoordinateSystemReproject(), CommonCRS.WGS84.normalizedGeographic());
        assertEquals(query.getResolution()[0], 45d, DELTA);
        assertEquals(query.getResolution()[1], 31d, DELTA);
        assertEquals(query.getFilter(), Filter.EXCLUDE);
        assertEquals(query.getMaxFeatures(), Integer.valueOf(10));
        assertEquals(query.getPropertyNames()[0], "att1");
        assertEquals(query.getPropertyNames()[1], "att2");
        assertEquals(query.getSortBy()[0], FF.sort("att1", SortOrder.DESCENDING));
        assertEquals(query.getStartIndex(), 5);

        //test constructor with query-------------------------------------------
        qb = new QueryBuilder(query2);
        query = qb.buildQuery();

        assertEquals(query.getTypeName(), name.toString());
        assertEquals(query.getCoordinateSystemReproject(), CommonCRS.WGS84.normalizedGeographic());
        assertEquals(query.getResolution()[0], 45d, DELTA);
        assertEquals(query.getResolution()[1], 31d, DELTA);
        assertEquals(query.getFilter(), Filter.EXCLUDE);
        assertEquals(query.getMaxFeatures(), Integer.valueOf(10));
        assertEquals(query.getPropertyNames()[0], "att1");
        assertEquals(query.getPropertyNames()[1], "att2");
        assertEquals(query.getSortBy()[0], FF.sort("att1", SortOrder.DESCENDING));
        assertEquals(query.getStartIndex(), 5);

        //test constructor with name--------------------------------------------
        qb = new QueryBuilder(name.toString());
        query = qb.buildQuery();

        assertEquals(query.getTypeName(), name.toString());
        assertEquals(query.getCoordinateSystemReproject(), null);
        assertEquals(query.getResolution(), null);
        assertEquals(query.getFilter(), Filter.INCLUDE);
        assertEquals(query.getMaxFeatures(), null);
        assertArrayEquals(query.getPropertyNames(), null);
        assertArrayEquals(query.getSortBy(), null);
        assertEquals(query.getStartIndex(), 0);

    }

}
