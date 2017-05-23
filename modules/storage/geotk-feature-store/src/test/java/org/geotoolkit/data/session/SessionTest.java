/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2009-2015, Geomatys
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


package org.geotoolkit.data.session;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.Point;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import org.geotoolkit.feature.FeatureExt;
import org.apache.sis.feature.builder.FeatureTypeBuilder;
import org.apache.sis.storage.DataStoreException;

import org.geotoolkit.data.FeatureIterator;
import org.geotoolkit.data.FeatureReader;
import org.geotoolkit.data.FeatureWriter;
import org.geotoolkit.data.memory.MemoryFeatureStore;
import org.geotoolkit.data.query.Query;
import org.geotoolkit.data.query.QueryBuilder;
import org.geotoolkit.factory.FactoryFinder;
import org.geotoolkit.util.NamesExt;
import org.geotoolkit.geometry.DefaultBoundingBox;
import org.apache.sis.referencing.CommonCRS;

import org.junit.Before;
import org.junit.Test;

import org.opengis.util.GenericName;
import org.opengis.filter.identity.FeatureId;
import org.opengis.filter.sort.SortBy;
import org.opengis.filter.sort.SortOrder;
import org.opengis.filter.FilterFactory2;
import org.opengis.referencing.NoSuchAuthorityCodeException;
import org.opengis.util.FactoryException;

import static org.junit.Assert.*;
import org.opengis.feature.Feature;
import org.opengis.feature.FeatureType;
import org.apache.sis.internal.feature.AttributeConvention;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public class SessionTest extends org.geotoolkit.test.TestBase {

    private static final double TOLERANCE = 1e-7;

    private static final GeometryFactory GF = new GeometryFactory();

    private static final FilterFactory2 FF = (FilterFactory2) FactoryFinder.getFilterFactory(null);

    private MemoryFeatureStore store = new MemoryFeatureStore();


    public SessionTest() {
    }

    @Before
    public void setUp() throws Exception {
        store = new MemoryFeatureStore();
        final FeatureTypeBuilder builder = new FeatureTypeBuilder();

        //create the schema
        final GenericName name = NamesExt.create("http://test.com", "TestSchema1");
        builder.setName(name);
        builder.addAttribute(String.class).setName(AttributeConvention.IDENTIFIER_PROPERTY);
        builder.addAttribute(Point.class).setName("geom").setCRS(CommonCRS.WGS84.normalizedGeographic());
        builder.addAttribute(String.class).setName("string");
        builder.addAttribute(Double.class).setName("double");
        builder.addAttribute(Date.class).setName("date");
        final FeatureType type = builder.build();
        store.createFeatureType(type);

        //create a few features
        FeatureWriter writer = store.getFeatureWriter(QueryBuilder.all(name.toString()));
        try{
            Feature f = writer.next();
            f.setPropertyValue("geom", GF.createPoint(new Coordinate(3, 30)));
            f.setPropertyValue("string", "hop3");
            f.setPropertyValue("double", 3d);
            f.setPropertyValue("date", new Date(1000L));
            writer.write();

            f = writer.next();
            f.setPropertyValue("geom", GF.createPoint(new Coordinate(1, 10)));
            f.setPropertyValue("string", "hop1");
            f.setPropertyValue("double", 1d);
            f.setPropertyValue("date", new Date(100000L));
            writer.write();

            f = writer.next();
            f.setPropertyValue("geom", GF.createPoint(new Coordinate(2, 20)));
            f.setPropertyValue("string", "hop2");
            f.setPropertyValue("double", 2d);
            f.setPropertyValue("date", new Date(10000L));
            writer.write();

        }finally{
            writer.close();
        }

        //quick count check
        FeatureReader reader = store.getFeatureReader(QueryBuilder.all(name.toString()));
        int count = 0;
        try{
            while(reader.hasNext()){
                reader.next();
                count++;
            }
        }finally{
            reader.close();
        }
        assertEquals(count, 3);
        assertEquals(store.getCount(QueryBuilder.all(name.toString())), 3);
    }

    @Test
    public void testSessionReader() throws Exception {
        final GenericName name = store.getNames().iterator().next();
        final QueryBuilder qb = new QueryBuilder();
        Query query;

        //create an asynchrone session
        final Session session = store.createSession(true);


        //test simple reader with no modification ------------------------------
        qb.setTypeName(name);
        query = qb.buildQuery();

        assertEquals(store.getCount(query),3);
        assertEquals(session.getCount(query),3);
        assertFalse(session.hasPendingChanges());

        //test an iterator------------------------------------------------------
        FeatureIterator reader = session.getFeatureIterator(QueryBuilder.sorted(name.toString(), new SortBy[]{FF.sort("string", SortOrder.ASCENDING)}));
        try{
            Feature sf;
            reader.hasNext();
            sf = reader.next();
            assertEquals(sf.getPropertyValue("string"),"hop1");
            reader.hasNext();
            sf = reader.next();
            assertEquals(sf.getPropertyValue("string"),"hop2");
            reader.hasNext();
            sf = reader.next();
            assertEquals(sf.getPropertyValue("string"),"hop3");

            assertFalse(reader.hasNext());
        }finally{
            reader.close();
        }

        //----------------------------------------------------------------------
        //test adding new features----------------------------------------------
        //----------------------------------------------------------------------
        final Feature sfb = store.getFeatureType(name.toString()).newInstance();
        sfb.setPropertyValue(AttributeConvention.IDENTIFIER_PROPERTY.toString(), "temporary");
        sfb.setPropertyValue("string", "hop4");
        sfb.setPropertyValue("double", 2.5d);
        sfb.setPropertyValue("date", new Date(100L));

        session.addFeatures(name.toString(), Collections.singletonList(sfb));

        //check that he new feature is available in the session but not in the datastore
        qb.reset();
        qb.setTypeName(name);
        query = qb.buildQuery();

        assertEquals(store.getCount(query),3);
        assertEquals(session.getCount(query),4);
        assertTrue(session.hasPendingChanges());

        reader = session.getFeatureIterator(QueryBuilder.filtered(name.toString(), FF.equals(FF.literal("hop4"), FF.property("string"))));
        try{
            Feature sf;
            reader.hasNext();
            sf = reader.next();
            assertEquals(sf.getPropertyValue("string"),"hop4");
            assertEquals(sf.getPropertyValue("double"),2.5d);
            assertEquals(sf.getPropertyValue("date"),new Date(100L));

            assertFalse(reader.hasNext());
        }finally{
            reader.close();
        }

        //check that the sorting order has been preserve within the session

        reader = session.getFeatureIterator(QueryBuilder.sorted(name.toString(),new SortBy[]{FF.sort("double", SortOrder.ASCENDING)}));
        try{
            Feature sf;
            reader.hasNext();
            sf = reader.next();
            assertEquals(sf.getPropertyValue("double"),1d);

            reader.hasNext();
            sf = reader.next();
            assertEquals(sf.getPropertyValue("double"),2d);

            reader.hasNext();
            sf = reader.next();
            assertEquals(sf.getPropertyValue("double"),2.5d);

            reader.hasNext();
            sf = reader.next();
            assertEquals(sf.getPropertyValue("double"),3d);

            assertFalse(reader.hasNext());
        }finally{
            reader.close();
        }


        //check that the new feature is available in the session and in the datastore
        session.commit();

        assertEquals(store.getCount(query),4);
        assertEquals(session.getCount(query),4);
        assertFalse(session.hasPendingChanges());

        //make a more deep test to find our feature
        reader = session.getFeatureIterator(QueryBuilder.filtered(name.toString(), FF.equals(FF.literal("hop4"), FF.property("string"))));
        try{
            Feature sf;
            reader.hasNext();
            sf = reader.next();
            assertEquals(sf.getPropertyValue("string"),"hop4");
            assertEquals(sf.getPropertyValue("double"),2.5d);
            assertEquals(sf.getPropertyValue("date"),new Date(100L));

            assertFalse(reader.hasNext());
        }finally{
            reader.close();
        }

        reader = store.getFeatureReader(QueryBuilder.filtered(name.toString(), FF.equals(FF.literal("hop4"), FF.property("string"))));
        try{
            Feature sf;
            reader.hasNext();
            sf = reader.next();
            assertEquals(sf.getPropertyValue("string"),"hop4");
            assertEquals(sf.getPropertyValue("double"),2.5d);
            assertEquals(sf.getPropertyValue("date"),new Date(100L));

            assertFalse(reader.hasNext());
        }finally{
            reader.close();
        }

    }

    @Test
    public void testSessionRemoveDelta() throws DataStoreException{
        final GenericName name = store.getNames().iterator().next();
        final QueryBuilder qb = new QueryBuilder();
        Query query;

        //create an asynchrone session
        final Session session = store.createSession(true);

        //----------------------------------------------------------------------
        //test removing feature-------------------------------------------------
        //----------------------------------------------------------------------

        //check that the feature exist
        FeatureIterator reader = session.getFeatureIterator(QueryBuilder.filtered(name.toString(), FF.equals(FF.literal("hop3"), FF.property("string"))));
        try{
            Feature sf;
            reader.hasNext();
            sf = reader.next();
            assertEquals(sf.getPropertyValue("string"),"hop3");
            assertEquals(sf.getPropertyValue("double"),3d);
            assertEquals(sf.getPropertyValue("date"),new Date(1000L));

            assertFalse(reader.hasNext());
        }finally{
            reader.close();
        }

        assertFalse(session.hasPendingChanges());

        //remove the feature
        session.removeFeatures(name.toString(), FF.equals(FF.literal("hop3"), FF.property("string")));

        //check that the feature is removed in the session but not in the datastore
        qb.reset();
        qb.setTypeName(name);
        query = qb.buildQuery();

        assertEquals(store.getCount(query),3);
        assertEquals(session.getCount(query),2);
        assertTrue(session.hasPendingChanges());

        qb.reset();
        qb.setTypeName(name);
        qb.setFilter(FF.equals(FF.literal("hop3"), FF.property("string")));
        query = qb.buildQuery();

        assertEquals(1,store.getCount(query));
        assertEquals(0,session.getCount(query));

        //check that the new feature is removed in the session and in the datastore
        session.commit();

        assertEquals(0,store.getCount(query));
        assertEquals(0,session.getCount(query));
        assertFalse(session.hasPendingChanges());

        //sanity check, count everything
        qb.reset();
        qb.setTypeName(name);
        query = qb.buildQuery();

        assertEquals(2,store.getCount(query));
        assertEquals(2,session.getCount(query));
        assertFalse(session.hasPendingChanges());
    }

    @Test
    public void testSessionModifyDelta() throws DataStoreException, NoSuchAuthorityCodeException, FactoryException{
        final GenericName name = store.getNames().iterator().next();
        final QueryBuilder qb = new QueryBuilder();
        Query query;

        //create an asynchrone session
        final Session session = store.createSession(true);

        //----------------------------------------------------------------------
        //test modifying feature------------------------------------------------
        //----------------------------------------------------------------------
        Point newPt = GF.createPoint(new Coordinate(5, 50));
        Map<String,Object> values = new HashMap<>();
        values.put("double", 15d);
        values.put("geom", newPt);

        session.updateFeatures(name.toString(), FF.equals(FF.property("double"), FF.literal(2d)), values);

        //check we have a modification -----------------------------------------
        qb.reset();
        qb.setTypeName(name);
        query = qb.buildQuery();

        assertEquals(store.getCount(query),3);
        assertEquals(session.getCount(query),3);
        assertTrue(session.hasPendingChanges());

        //check the geometry is changed
        FeatureIterator ite = session.getFeatureIterator(query);
        boolean found = false;
        while(ite.hasNext()){
            Feature f = ite.next();
            if(f.getProperty("double").getValue().equals(15d)){
                found = true;
                assertTrue(newPt.getCoordinate().equals2D( ((Point)f.getPropertyValue("geom")).getCoordinate() ));
            }
        }
        ite.close();
        if(!found){
            fail("modified feature not found.");
        }

        //check the query modified feature is correctly reprojected ------------
        qb.reset();
        qb.setCRS(CommonCRS.WGS84.geographic());
        qb.setTypeName(name);
        query = qb.buildQuery();

        assertEquals(store.getCount(query),3);
        assertEquals(session.getCount(query),3);
        assertTrue(session.hasPendingChanges());

        //check the geometry is changed
        ite = session.getFeatureIterator(query);
        found = false;
        while(ite.hasNext()){
            Feature f = ite.next();
            if(f.getProperty("double").getValue().equals(15d)){
                found = true;
                Point pt = (Point)f.getPropertyValue("geom");
                assertEquals(50d, pt.getCoordinate().x, TOLERANCE);
                assertEquals(5d, pt.getCoordinate().y, TOLERANCE);
            }
        }
        ite.close();
        if(!found){
            fail("modified feature not found.");
        }

        //make a second change on the same feature -----------------------------
        newPt = GF.createPoint(new Coordinate(9, 90));
        values = new HashMap<>();
        values.put("geom", newPt);
        session.updateFeatures(name.toString(), FF.equals(FF.property("double"), FF.literal(15d)), values);

        qb.reset();
        qb.setCRS(CommonCRS.WGS84.geographic());
        qb.setTypeName(name);
        query = qb.buildQuery();

        //check the geometry is changed
        ite = session.getFeatureIterator(query);
        found = false;
        while(ite.hasNext()){
            Feature f = ite.next();
            if(f.getProperty("double").getValue().equals(15d)){
                found = true;
                Point pt = (Point)f.getPropertyValue("geom");
                assertEquals(90d, pt.getCoordinate().x, TOLERANCE);
                assertEquals(9d, pt.getCoordinate().y, TOLERANCE);
            }
        }
        ite.close();
        if(!found){
            fail("modified feature not found.");
        }






        session.rollback();
        assertFalse(session.hasPendingChanges());


        //check that two modification doesnt conflict eachother
        qb.reset();
        qb.setTypeName(name);
        query = qb.buildQuery();
        String desc = "double";

        ite = session.getFeatureIterator(query);
        FeatureId id1 = FeatureExt.getId(ite.next());
        FeatureId id2 = FeatureExt.getId(ite.next());
        ite.close();

        session.updateFeatures(name.toString(), FF.id(Collections.singleton(id1)), Collections.singletonMap(desc, 50d));
        session.updateFeatures(name.toString(), FF.id(Collections.singleton(id2)), Collections.singletonMap(desc, 100d));

        qb.reset();
        qb.setTypeName(name);
        qb.setFilter(FF.id(Collections.singleton(id1)));
        query = qb.buildQuery();

        ite = session.getFeatureIterator(query);
        int count = 0;
        while(ite.hasNext()){
            Feature f = ite.next();
            assertEquals(id1,FeatureExt.getId(f));
            assertEquals(50d, f.getPropertyValue("double"));
            count++;
        }
        ite.close();

        assertEquals(1, count);


        qb.reset();
        qb.setTypeName(name);
        qb.setFilter(FF.id(Collections.singleton(id2)));
        query = qb.buildQuery();

        ite = session.getFeatureIterator(query);
        count = 0;
        while(ite.hasNext()){
            Feature f = ite.next();
            assertEquals(id2,FeatureExt.getId(f));
            assertEquals(100d, f.getPropertyValue("double"));
            count++;
        }
        ite.close();

        assertEquals(1, count);


        //todo must handle count and envelope before testing more

        //check the modification is visible only in the session
//        qb.reset();
//        qb.setTypeName(name);
//        qb.setFilter(FF.equals(FF.property("double"), FF.literal(15d)));
//        query = qb.buildQuery();
//
//        assertEquals(store.getCount(query),0);
//        assertEquals(session.getCount(query),1);
//        assertTrue(session.hasPendingChanges());

        //check the modification is visible only in the session
//        qb.reset();
//        qb.setTypeName(name);
//        qb.setFilter(FF.equals(FF.property("double"), FF.literal(2d)));
//        query = qb.buildQuery();
//
//        assertEquals(store.getCount(query),1);
//        assertEquals(session.getCount(query),0);
//        assertTrue(session.hasPendingChanges());



    }

    @Test
    public void testSessionModifyDeltaFilter() throws DataStoreException, NoSuchAuthorityCodeException, FactoryException{
        final GenericName name = store.getNames().iterator().next();
        final QueryBuilder qb = new QueryBuilder();
        Query query;

        //create an asynchrone session
        final Session session = store.createSession(true);
        final Point newPt = GF.createPoint(new Coordinate(50, 1));
        final Map<String,Object> values = new HashMap<>();
        values.put("geom", newPt);

        session.updateFeatures(name.toString(), FF.equals(FF.property("double"), FF.literal(2d)), values);

        //check we have a modification in data crs -----------------------------
        DefaultBoundingBox bbox = new DefaultBoundingBox(CommonCRS.WGS84.normalizedGeographic());
        bbox.setRange(0, 49, 51);
        bbox.setRange(1, 0, 2);
        qb.reset();
        qb.setTypeName(name);
        qb.setFilter(FF.bbox(FF.property("geom"), bbox));
        query = qb.buildQuery();

        assertEquals(0,store.getCount(query));
        assertEquals(1,session.getCount(query));
        assertTrue(session.hasPendingChanges());

        //check the geometry is changed
        FeatureIterator ite = session.getFeatureIterator(query);
        boolean found = false;
        while(ite.hasNext()){
            Feature f = ite.next();
            if(f.getProperty("double").getValue().equals(2d)){
                found = true;
                final Point pt = (Point)f.getPropertyValue("geom");
                //we expect axes the same way
                assertEquals(newPt.getCoordinate().x, pt.getCoordinate().x,TOLERANCE);
                assertEquals(newPt.getCoordinate().y, pt.getCoordinate().y,TOLERANCE);
            }
        }
        ite.close();
        if(!found){
            fail("modified feature not found.");
        }


        //check we have a modification in another crs --------------------------
        bbox = new DefaultBoundingBox(CommonCRS.WGS84.geographic());
        bbox.setRange(1, 49, 51);
        bbox.setRange(0, 0, 2);
        qb.reset();
        qb.setTypeName(name);
        qb.setFilter(FF.bbox(FF.property("geom"), bbox));
        query = qb.buildQuery();

        //check the geometry is changed
        ite = session.getFeatureIterator(query);
        found = false;
        while(ite.hasNext()){
            Feature f = ite.next();
            if(f.getProperty("double").getValue().equals(2d)){
                found = true;
                final Point pt = (Point)f.getPropertyValue("geom");
                //we expect axes the same way
                assertEquals(newPt.getCoordinate().x, pt.getCoordinate().x, TOLERANCE);
                assertEquals(newPt.getCoordinate().y, pt.getCoordinate().y, TOLERANCE);
            }
        }
        ite.close();
        if(!found){
            fail("modified feature not found.");
        }

        session.commit();

        //check valid once in the datastore-------------------------------------
        ite = session.getFeatureIterator(query);
        found = false;
        while(ite.hasNext()){
            Feature f = ite.next();
            if(f.getProperty("double").getValue().equals(2d)){
                found = true;
                final Point pt = (Point)f.getPropertyValue("geom");
                //we expect axes the same way
                assertEquals(newPt.getCoordinate().x, pt.getCoordinate().x, TOLERANCE);
                assertEquals(newPt.getCoordinate().y, pt.getCoordinate().y, TOLERANCE);
            }
        }
        ite.close();
        if(!found){
            fail("modified feature not found.");
        }

    }

}
