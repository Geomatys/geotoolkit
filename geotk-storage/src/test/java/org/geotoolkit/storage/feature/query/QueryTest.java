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

import java.util.Collections;
import org.apache.sis.feature.builder.AttributeRole;
import org.apache.sis.feature.builder.FeatureTypeBuilder;
import org.apache.sis.referencing.CommonCRS;
import org.apache.sis.storage.DataStoreException;
import org.apache.sis.storage.FeatureQuery;
import org.apache.sis.storage.FeatureSet;
import org.geotoolkit.filter.FilterUtilities;
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
import org.opengis.filter.SortOrder;
import org.opengis.filter.SortProperty;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
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
        try{
            new Query((GenericName) null);
            throw new Exception("We can not build a query without at least the type name.");
        }catch(NullPointerException ex){
            //ok
        }

        try{
            Query.filtered(null, Filter.exclude());
            throw new Exception("We can not build a query without at least the type name.");
        }catch(NullPointerException ex){
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

        //only filter-----------------------------------------------------------
        query = Query.filtered(name.toString(), Filter.exclude());
        assertEquals(query.getTypeName(), name.toString());
        assertEquals(query.getSelection(), Filter.exclude());
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

        final FeatureQuery query = Query.reproject(type, outCrs);
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
