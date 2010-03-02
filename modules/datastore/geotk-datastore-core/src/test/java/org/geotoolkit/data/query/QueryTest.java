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

import junit.framework.TestCase;

import org.geotoolkit.data.FeatureCollection;
import org.geotoolkit.data.FeatureIterator;
import org.geotoolkit.data.FeatureWriter;
import org.geotoolkit.data.memory.MemoryDataStore;
import org.geotoolkit.data.session.Session;
import org.geotoolkit.factory.FactoryFinder;
import org.geotoolkit.feature.DefaultName;
import org.geotoolkit.feature.simple.SimpleFeatureTypeBuilder;
import org.geotoolkit.referencing.crs.DefaultGeographicCRS;

import org.junit.Test;

import org.opengis.feature.Feature;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.feature.type.Name;
import org.opengis.filter.Filter;
import org.opengis.filter.FilterFactory;
import org.opengis.filter.sort.SortBy;
import org.opengis.filter.sort.SortOrder;

/**
 * Test query builder.
 *
 * @author Johann Sorel (Geomatys)
 */
public class QueryTest extends TestCase{

    private static final FilterFactory FF = FactoryFinder.getFilterFactory(null);


    private final MemoryDataStore store = new MemoryDataStore();
    private final Name name1;
    private final Name name2;

    public QueryTest() throws Exception {
        final SimpleFeatureTypeBuilder builder = new SimpleFeatureTypeBuilder();

        //----------------------------------------------------------------------
        name1 = new DefaultName("http://type1.com", "Type1");
        builder.reset();
        builder.setName(name1);
        builder.add(new DefaultName("http://type1.com", "att1"), String.class);
        builder.add(new DefaultName("http://type1.com", "att2"), Integer.class);
        final SimpleFeatureType sft1 = builder.buildFeatureType();
        store.createSchema(name1,sft1);

        FeatureWriter fw = store.getFeatureWriterAppend(name1);
        SimpleFeature sf = (SimpleFeature) fw.next();
        sf.setAttribute("att1", "str1");
        sf.setAttribute("att2", 1);
        fw.write();
        sf = (SimpleFeature) fw.next();
        sf.setAttribute("att1", "str2");
        sf.setAttribute("att2", 2);
        fw.write();
        sf = (SimpleFeature) fw.next();
        sf.setAttribute("att1", "str3");
        sf.setAttribute("att2", 3);
        fw.write();
        sf = (SimpleFeature) fw.next();
        sf.setAttribute("att1", "str50");
        sf.setAttribute("att2", 50);
        fw.write();
        sf = (SimpleFeature) fw.next();
        sf.setAttribute("att1", "str51");
        sf.setAttribute("att2", 51);
        fw.write();

        fw.close();


        //----------------------------------------------------------------------
        name2 = new DefaultName("http://type2.com", "Type2");
        builder.reset();
        builder.setName(name2);
        builder.add(new DefaultName("http://type2.com", "att3"), Integer.class);
        builder.add(new DefaultName("http://type2.com", "att4"), Double.class);
        final SimpleFeatureType sft2 = builder.buildFeatureType();
        store.createSchema(name2,sft2);

        fw = store.getFeatureWriterAppend(name2);
        sf = (SimpleFeature) fw.next();
        sf.setAttribute("att3", 1);
        sf.setAttribute("att4", 10);
        fw.write();
        sf = (SimpleFeature) fw.next();
        sf.setAttribute("att3", 2);
        sf.setAttribute("att4", 20);
        fw.write();
        sf = (SimpleFeature) fw.next();
        sf.setAttribute("att3", 2);
        sf.setAttribute("att4", 30);
        fw.write();
        sf = (SimpleFeature) fw.next();
        sf.setAttribute("att3", 3);
        sf.setAttribute("att4", 40);
        fw.write();
        sf = (SimpleFeature) fw.next();
        sf.setAttribute("att3", 60);
        sf.setAttribute("att4", 60);
        fw.write();
        sf = (SimpleFeature) fw.next();
        sf.setAttribute("att3", 61);
        sf.setAttribute("att4", 61);
        fw.write();

        fw.close();

    }

    /**
     * test static methods from querybuilder
     */
    @Test
    public void testStaticQueryBuilder() throws Exception {
        Query query = null;
        Name name = new DefaultName("http://test.org","testLocal");

        //test null values------------------------------------------------------
        try{
            QueryBuilder.all((Name)null);
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
        assertEquals(query.getTypeName(), name);
        assertEquals(query.getCoordinateSystemReproject(), null);
        assertEquals(query.getFilter(), Filter.INCLUDE);
        assertEquals(query.getMaxFeatures(), null);
        assertEquals(query.getPropertyNames(), null);
        assertEquals(query.getSortBy(), null);
        assertEquals(query.getStartIndex(), 0);

        //only ids--------------------------------------------------------------
        query = QueryBuilder.fids(name);
        assertEquals(query.getTypeName(), name);
        assertEquals(query.getCoordinateSystemReproject(), null);
        assertEquals(query.getFilter(), Filter.INCLUDE);
        assertEquals(query.getMaxFeatures(), null);
        assertNotNull(query.getPropertyNames()); //must be an empty array, not null
        assertTrue(query.getPropertyNames().length == 0); //must be an empty array, not null
        assertEquals(query.getSortBy(), null);
        assertEquals(query.getStartIndex(), 0);

        //only filter-----------------------------------------------------------
        query = QueryBuilder.filtered(name, Filter.EXCLUDE);
        assertEquals(query.getTypeName(), name);
        assertEquals(query.getCoordinateSystemReproject(), null);
        assertEquals(query.getFilter(), Filter.EXCLUDE);
        assertEquals(query.getMaxFeatures(), null);
        assertEquals(query.getPropertyNames(), null);
        assertEquals(query.getSortBy(), null);
        assertEquals(query.getStartIndex(), 0);

        //only sort by----------------------------------------------------------
        query = QueryBuilder.sorted(name, new SortBy[]{FF.sort("att1", SortOrder.DESCENDING)});
        assertEquals(query.getTypeName(), name);
        assertEquals(query.getCoordinateSystemReproject(), null);
        assertEquals(query.getFilter(), Filter.INCLUDE);
        assertEquals(query.getMaxFeatures(), null);
        assertEquals(query.getPropertyNames(), null);
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
        Name name = new DefaultName("http://test.org","testLocal");
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
        qb.setCRS(DefaultGeographicCRS.WGS84);
        qb.setFilter(Filter.EXCLUDE);
        qb.setMaxFeatures(10);
        qb.setProperties(new String[]{"att1","att2"});
        qb.setSortBy(new SortBy[]{FF.sort("att1", SortOrder.DESCENDING)});
        qb.setStartIndex(5);
        query = qb.buildQuery();

        assertEquals(query.getTypeName(), name);
        assertEquals(query.getCoordinateSystemReproject(), DefaultGeographicCRS.WGS84);
        assertEquals(query.getFilter(), Filter.EXCLUDE);
        assertEquals(query.getMaxFeatures(), Integer.valueOf(10));
        assertEquals(query.getPropertyNames()[0], DefaultName.valueOf("att1"));
        assertEquals(query.getPropertyNames()[1], DefaultName.valueOf("att2"));
        assertEquals(query.getSortBy()[0], FF.sort("att1", SortOrder.DESCENDING));
        assertEquals(query.getStartIndex(), 5);

        query2 = query;

        //test reset------------------------------------------------------------
        qb.reset();
        qb.setTypeName(name);
        query = qb.buildQuery();

        assertEquals(query.getTypeName(), name);
        assertEquals(query.getCoordinateSystemReproject(), null);
        assertEquals(query.getFilter(), Filter.INCLUDE);
        assertEquals(query.getMaxFeatures(), null);
        assertEquals(query.getPropertyNames(), null);
        assertEquals(query.getSortBy(), null);
        assertEquals(query.getStartIndex(), 0);

        //test copy-------------------------------------------------------------
        qb.copy(query2);
        query = qb.buildQuery();

        assertEquals(query.getTypeName(), name);
        assertEquals(query.getCoordinateSystemReproject(), DefaultGeographicCRS.WGS84);
        assertEquals(query.getFilter(), Filter.EXCLUDE);
        assertEquals(query.getMaxFeatures(), Integer.valueOf(10));
        assertEquals(query.getPropertyNames()[0], DefaultName.valueOf("att1"));
        assertEquals(query.getPropertyNames()[1], DefaultName.valueOf("att2"));
        assertEquals(query.getSortBy()[0], FF.sort("att1", SortOrder.DESCENDING));
        assertEquals(query.getStartIndex(), 5);

        //test constructor with query-------------------------------------------
        qb = new QueryBuilder(query2);
        query = qb.buildQuery();

        assertEquals(query.getTypeName(), name);
        assertEquals(query.getCoordinateSystemReproject(), DefaultGeographicCRS.WGS84);
        assertEquals(query.getFilter(), Filter.EXCLUDE);
        assertEquals(query.getMaxFeatures(), Integer.valueOf(10));
        assertEquals(query.getPropertyNames()[0], DefaultName.valueOf("att1"));
        assertEquals(query.getPropertyNames()[1], DefaultName.valueOf("att2"));
        assertEquals(query.getSortBy()[0], FF.sort("att1", SortOrder.DESCENDING));
        assertEquals(query.getStartIndex(), 5);

        //test constructor with name--------------------------------------------
        qb = new QueryBuilder(name);
        query = qb.buildQuery();

        assertEquals(query.getTypeName(), name);
        assertEquals(query.getCoordinateSystemReproject(), null);
        assertEquals(query.getFilter(), Filter.INCLUDE);
        assertEquals(query.getMaxFeatures(), null);
        assertEquals(query.getPropertyNames(), null);
        assertEquals(query.getSortBy(), null);
        assertEquals(query.getStartIndex(), 0);

    }

    /**
     * Test that cross datastore queries works correctly.
     */
    @Test
    public void testInnerJoinQuery() throws Exception{
        final Session session = store.createSession(false);

        final QueryBuilder qb = new QueryBuilder();
        final Join join = new DefaultJoin(
                new DefaultSelector(session, name1, "s1"),
                new DefaultSelector(session, name2, "s2"),
                JoinType.INNER,
                FF.equals(FF.property("att2"), FF.property("att3")));
        qb.setSource(join);

        final Query query = qb.buildQuery();

        final FeatureCollection col = session.getFeatureCollection(query);

        FeatureIterator ite = col.iterator();
        Feature f = null;

        f = ite.next();
        assertEquals(f.getProperty("att1").getValue(), "str1");
        assertEquals(f.getProperty("att2").getValue(), 1);
        assertEquals(f.getProperty("att3").getValue(), 1);
        assertEquals(f.getProperty("att4").getValue(), 10d);

        f = ite.next();
        assertEquals(f.getProperty("att1").getValue(), "str2");
        assertEquals(f.getProperty("att2").getValue(), 2);
        assertEquals(f.getProperty("att3").getValue(), 2);
        assertEquals(f.getProperty("att4").getValue(), 20d);

        f = ite.next();
        assertEquals(f.getProperty("att1").getValue(), "str2");
        assertEquals(f.getProperty("att2").getValue(), 2);
        assertEquals(f.getProperty("att3").getValue(), 2);
        assertEquals(f.getProperty("att4").getValue(), 30d);

        f = ite.next();
        assertEquals(f.getProperty("att1").getValue(), "str3");
        assertEquals(f.getProperty("att2").getValue(), 3);
        assertEquals(f.getProperty("att3").getValue(), 3);
        assertEquals(f.getProperty("att4").getValue(), 40d);

        assertFalse(ite.hasNext());
    }

    /**
     * Test that cross datastore queries works correctly.
     */
    @Test
    public void testOuterLeftQuery() throws Exception{
        final Session session = store.createSession(false);

        final QueryBuilder qb = new QueryBuilder();
        final Join join = new DefaultJoin(
                new DefaultSelector(session, name1, "s1"),
                new DefaultSelector(session, name2, "s2"),
                JoinType.LEFT_OUTER,
                FF.equals(FF.property("att2"), FF.property("att3")));
        qb.setSource(join);

        final Query query = qb.buildQuery();

        final FeatureCollection col = session.getFeatureCollection(query);

        FeatureIterator ite = col.iterator();
        Feature f = null;

        f = ite.next();
        assertEquals(f.getProperty("att1").getValue(), "str1");
        assertEquals(f.getProperty("att2").getValue(), 1);
        assertEquals(f.getProperty("att3").getValue(), 1);
        assertEquals(f.getProperty("att4").getValue(), 10d);

        f = ite.next();
        assertEquals(f.getProperty("att1").getValue(), "str2");
        assertEquals(f.getProperty("att2").getValue(), 2);
        assertEquals(f.getProperty("att3").getValue(), 2);
        assertEquals(f.getProperty("att4").getValue(), 20d);

        f = ite.next();
        assertEquals(f.getProperty("att1").getValue(), "str2");
        assertEquals(f.getProperty("att2").getValue(), 2);
        assertEquals(f.getProperty("att3").getValue(), 2);
        assertEquals(f.getProperty("att4").getValue(), 30d);

        f = ite.next();
        assertEquals(f.getProperty("att1").getValue(), "str3");
        assertEquals(f.getProperty("att2").getValue(), 3);
        assertEquals(f.getProperty("att3").getValue(), 3);
        assertEquals(f.getProperty("att4").getValue(), 40d);

        f = ite.next();
        assertEquals(f.getProperty("att1").getValue(), "str50");
        assertEquals(f.getProperty("att2").getValue(), 50);
        assertEquals(f.getProperty("att3").getValue(), null);
        assertEquals(f.getProperty("att4").getValue(), null);

        f = ite.next();
        assertEquals(f.getProperty("att1").getValue(), "str51");
        assertEquals(f.getProperty("att2").getValue(), 51);
        assertEquals(f.getProperty("att3").getValue(), null);
        assertEquals(f.getProperty("att4").getValue(), null);

        assertFalse(ite.hasNext());

        System.out.println(col);
    }

}