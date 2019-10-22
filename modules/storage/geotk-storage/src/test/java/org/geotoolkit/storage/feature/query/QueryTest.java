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

import org.geotoolkit.storage.feature.query.Query;
import org.geotoolkit.storage.feature.query.QueryBuilder;
import java.util.Collections;
import org.apache.sis.feature.builder.AttributeRole;
import org.apache.sis.feature.builder.FeatureTypeBuilder;
import org.apache.sis.internal.storage.query.SimpleQuery;
import org.apache.sis.internal.system.DefaultFactories;
import org.apache.sis.referencing.CommonCRS;
import org.apache.sis.storage.DataStoreException;
import org.apache.sis.storage.FeatureSet;
import org.geotoolkit.storage.memory.InMemoryFeatureSet;
import org.geotoolkit.util.NamesExt;
import org.junit.Assert;
import static org.junit.Assert.*;
import org.junit.Test;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.opengis.feature.Feature;
import org.opengis.feature.FeatureType;
import org.opengis.filter.Filter;
import org.opengis.filter.FilterFactory;
import org.opengis.filter.sort.SortBy;
import org.opengis.filter.sort.SortOrder;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.util.GenericName;

/**
 * Test query builder.
 *
 * @author Johann Sorel (Geomatys)
 */
public class QueryTest {

    private static final FilterFactory FF = DefaultFactories.forBuildin(FilterFactory.class);
    private static final GeometryFactory GF = new GeometryFactory();
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
        assertEquals(query.getLimit(), -1);
        assertArrayEquals(query.getPropertyNames(), null);
        assertArrayEquals(query.getSortBy(), new SortBy[0]);
        assertEquals(query.getOffset(), 0);

        //only ids--------------------------------------------------------------
        query = QueryBuilder.fids(name.toString());
        assertEquals(query.getTypeName(), name.toString());
        assertEquals(query.getCoordinateSystemReproject(), null);
        assertEquals(query.getResolution(), null);
        assertEquals(query.getFilter(), Filter.INCLUDE);
        assertEquals(query.getLimit(), -1);
        assertNotNull(query.getPropertyNames()); //must be an empty array, not null
        assertTrue(query.getPropertyNames().length == 1); //must have only one value
        assertArrayEquals(query.getSortBy(), new SortBy[0]);
        assertEquals(query.getOffset(), 0);

        //only filter-----------------------------------------------------------
        query = QueryBuilder.filtered(name.toString(), Filter.EXCLUDE);
        assertEquals(query.getTypeName(), name.toString());
        assertEquals(query.getCoordinateSystemReproject(), null);
        assertEquals(query.getResolution(), null);
        assertEquals(query.getFilter(), Filter.EXCLUDE);
        assertEquals(query.getLimit(), -1);
        assertArrayEquals(query.getPropertyNames(), null);
        assertArrayEquals(query.getSortBy(), new SortBy[0]);
        assertEquals(query.getOffset(), 0);

        //only sort by----------------------------------------------------------
        query = QueryBuilder.sorted(name.toString(), new SortBy[]{FF.sort("att1", SortOrder.DESCENDING)});
        assertEquals(query.getTypeName(), name.toString());
        assertEquals(query.getCoordinateSystemReproject(), null);
        assertEquals(query.getResolution(), null);
        assertEquals(query.getFilter(), Filter.INCLUDE);
        assertEquals(query.getLimit(), -1);
        assertArrayEquals(query.getPropertyNames(), null);
        assertNotNull(query.getSortBy());
        assertTrue(query.getSortBy().length == 1);
        assertEquals(query.getSortBy()[0], FF.sort("att1", SortOrder.DESCENDING));
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
        qb.setLimit(10);
        qb.setProperties(new String[]{"att1","att2"});
        qb.setSortBy(new SortBy[]{FF.sort("att1", SortOrder.DESCENDING)});
        qb.setOffset(5);
        query = qb.buildQuery();

        assertEquals(query.getTypeName(), name.toString());
        assertEquals(query.getCoordinateSystemReproject(), CommonCRS.WGS84.normalizedGeographic());
        assertEquals(query.getResolution()[0], 45d,DELTA);
        assertEquals(query.getResolution()[1], 31d,DELTA);
        assertEquals(query.getFilter(), Filter.EXCLUDE);
        assertEquals(query.getLimit(), 10l);
        assertEquals(query.getPropertyNames()[0], "att1");
        assertEquals(query.getPropertyNames()[1], "att2");
        assertEquals(query.getSortBy()[0], FF.sort("att1", SortOrder.DESCENDING));
        assertEquals(query.getOffset(), 5);

        query2 = query;

        //test reset------------------------------------------------------------
        qb.reset();
        qb.setTypeName(name);
        query = qb.buildQuery();

        assertEquals(query.getTypeName(), name.toString());
        assertEquals(query.getCoordinateSystemReproject(), null);
        assertEquals(query.getResolution(), null);
        assertEquals(query.getFilter(), Filter.INCLUDE);
        assertEquals(query.getLimit(), -1);
        assertArrayEquals(query.getPropertyNames(), null);
        assertArrayEquals(query.getSortBy(), new SortBy[0]);
        assertEquals(query.getOffset(), 0);

        //test copy-------------------------------------------------------------
        qb.copy(query2);
        query = qb.buildQuery();

        assertEquals(query.getTypeName(), name.toString());
        assertEquals(query.getCoordinateSystemReproject(), CommonCRS.WGS84.normalizedGeographic());
        assertEquals(query.getResolution()[0], 45d, DELTA);
        assertEquals(query.getResolution()[1], 31d, DELTA);
        assertEquals(query.getFilter(), Filter.EXCLUDE);
        assertEquals(query.getLimit(), 10l);
        assertEquals(query.getPropertyNames()[0], "att1");
        assertEquals(query.getPropertyNames()[1], "att2");
        assertEquals(query.getSortBy()[0], FF.sort("att1", SortOrder.DESCENDING));
        assertEquals(query.getOffset(), 5);

        //test constructor with query-------------------------------------------
        qb = new QueryBuilder(query2);
        query = qb.buildQuery();

        assertEquals(query.getTypeName(), name.toString());
        assertEquals(query.getCoordinateSystemReproject(), CommonCRS.WGS84.normalizedGeographic());
        assertEquals(query.getResolution()[0], 45d, DELTA);
        assertEquals(query.getResolution()[1], 31d, DELTA);
        assertEquals(query.getFilter(), Filter.EXCLUDE);
        assertEquals(query.getLimit(), 10l);
        assertEquals(query.getPropertyNames()[0], "att1");
        assertEquals(query.getPropertyNames()[1], "att2");
        assertEquals(query.getSortBy()[0], FF.sort("att1", SortOrder.DESCENDING));
        assertEquals(query.getOffset(), 5);

        //test constructor with name--------------------------------------------
        qb = new QueryBuilder(name.toString());
        query = qb.buildQuery();

        assertEquals(query.getTypeName(), name.toString());
        assertEquals(query.getCoordinateSystemReproject(), null);
        assertEquals(query.getResolution(), null);
        assertEquals(query.getFilter(), Filter.INCLUDE);
        assertEquals(query.getLimit(), -1);
        assertArrayEquals(query.getPropertyNames(), null);
        assertArrayEquals(query.getSortBy(), new SortBy[0]);
        assertEquals(query.getOffset(), 0);

    }

    @Test
    public void reprojectTest() throws DataStoreException {

        CoordinateReferenceSystem inCrs = CommonCRS.WGS84.normalizedGeographic();
        CoordinateReferenceSystem outCrs = CommonCRS.WGS84.geographic();

        final FeatureTypeBuilder ftb = new FeatureTypeBuilder();
        ftb.setName("test");
        ftb.addAttribute(Point.class).setName("geom").setCRS(inCrs).addRole(AttributeRole.DEFAULT_GEOMETRY);
        final FeatureType type = ftb.build();

        final Point geometry = GF.createPoint(new Coordinate(10, 30));
        geometry.setUserData(inCrs);
        final Feature feature = type.newInstance();
        feature.setPropertyValue("geom", geometry);

        FeatureSet fs = new InMemoryFeatureSet(type, Collections.singleton(feature));

        final SimpleQuery query = QueryBuilder.reproject(type, outCrs);
        final FeatureSet rfs = fs.subset(query);
        final Feature rfeature = rfs.features(false).findFirst().get();

        Point geom1 = (Point) rfeature.getPropertyValue("sis:geometry");
        Point geom2 = (Point) rfeature.getPropertyValue("geom");

        assertEquals(outCrs, geom1.getUserData());
        assertEquals(outCrs, geom2.getUserData());
        Assert.assertEquals(30.0, geom1.getX(), 0.0);
        Assert.assertEquals(10.0, geom1.getY(), 0.0);
        Assert.assertEquals(30.0, geom2.getX(), 0.0);
        Assert.assertEquals(10.0, geom2.getY(), 0.0);

    }

}
