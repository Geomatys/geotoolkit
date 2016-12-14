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


package org.geotoolkit.data.memory;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.Point;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import junit.framework.TestCase;
import org.apache.sis.feature.FeatureExt;
import org.apache.sis.feature.builder.AttributeRole;
import org.apache.sis.feature.builder.FeatureTypeBuilder;

import org.geotoolkit.data.FeatureCollection;
import org.geotoolkit.data.FeatureReader;
import org.geotoolkit.data.FeatureWriter;
import org.geotoolkit.data.query.Query;
import org.geotoolkit.data.query.QueryBuilder;
import org.geotoolkit.data.session.Session;
import org.geotoolkit.factory.FactoryFinder;
import org.geotoolkit.util.NamesExt;
import org.geotoolkit.filter.sort.DefaultSortBy;
import org.apache.sis.referencing.CRS;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.opengis.feature.Feature;
import org.opengis.feature.FeatureType;
import org.opengis.util.GenericName;
import org.opengis.filter.Filter;
import org.opengis.filter.FilterFactory;
import org.opengis.filter.sort.SortBy;
import org.opengis.filter.sort.SortOrder;
import org.apache.sis.internal.feature.AttributeConvention;
import org.apache.sis.referencing.CommonCRS;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public class MemoryDatastoreTest extends TestCase{

    private static final FilterFactory FF = FactoryFinder.getFilterFactory(null);

    public MemoryDatastoreTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @Test
    public void testCreateDataStore() throws Exception {
        MemoryFeatureStore store = new MemoryFeatureStore();
    }

    @Test
    public void testSchemas() throws Exception {
        FeatureTypeBuilder builder = new FeatureTypeBuilder();
        final MemoryFeatureStore store = new MemoryFeatureStore();
        Set<GenericName> names;

        names = store.getNames();
        assertEquals(0,names.size());

        //test creation of one schema ------------------------------------------
        GenericName name = NamesExt.create("http://test.com", "TestSchema1");
        builder.setName(name);
        builder.addAttribute(String.class).setName("att1");
        final FeatureType type1 = builder.build();

        store.createFeatureType(type1);

        names = store.getNames();
        assertEquals(1,names.size());
        GenericName n = names.iterator().next();

        assertEquals(n.tip().toString(), "TestSchema1");
        assertEquals(NamesExt.getNamespace(n), "http://test.com");

        FeatureType t = store.getFeatureType(n.toString());
        assertEquals(t, type1);

        try{
            store.getFeatureType(NamesExt.create("http://not", "exist").toString());
            throw new Exception("Asking for a schema that doesnt exist should have raised an error");
        }catch(Exception ex){
            //ok
        }

        //test update schema ---------------------------------------------------
        builder = new FeatureTypeBuilder();
        builder.setName("http://test.com", "TestSchema1");
        builder.addAttribute(String.class).setName("att1");
        builder.addAttribute(Double.class).setName("att2");
        FeatureType type2 = builder.build();

        store.updateFeatureType(type2);

        names = store.getNames();
        assertEquals(1,names.size());
        n = names.iterator().next();

        assertEquals(n.tip().toString(), "TestSchema1");
        assertEquals(NamesExt.getNamespace(n), "http://test.com");

        t = store.getFeatureType(n.toString());
        assertEquals(t, type2);


        //test delete schema ---------------------------------------------------

        names = store.getNames();
        assertEquals(1,names.size());

        store.deleteFeatureType(name.toString());

        names = store.getNames();
        assertEquals(0,names.size());

        try{
            store.deleteFeatureType(NamesExt.create("http://not", "exist").toString());
            throw new Exception("Deleting a schema that doesnt exist should have raised an error");
        }catch(Exception ex){
            //ok
        }

    }

    @Test
    public void testFeatures() throws Exception {
        final FeatureTypeBuilder builder = new FeatureTypeBuilder();
        final MemoryFeatureStore store = new MemoryFeatureStore();

        //create the schema
        GenericName name = NamesExt.create("http://test.com", "TestSchema1");
        builder.setName(name);
        builder.addAttribute(String.class).setName(AttributeConvention.IDENTIFIER_PROPERTY);
        builder.addAttribute(String.class).setName("att1");
        final FeatureType type = builder.build();
        store.createFeatureType(type);


        //test reader with no features in datastore
        FeatureReader reader = store.getFeatureReader(QueryBuilder.all(name));
        int count = 0;
        try{
            while(reader.hasNext()){
                reader.next();
                count++;
            }
        }finally{
            reader.close();
        }
        assertEquals(count, 0);
        assertEquals(store.getCount(QueryBuilder.all(name)), 0);



        //test a non existant type
        try{
            FeatureReader reader2 = store.getFeatureReader(QueryBuilder.all(NamesExt.create("http://not", "exist")));
            throw new Exception("Deleting a schema that doesnt exist should have raised an error");
        }catch(Exception ex){
            //ok
        }

        //create a few features
        FeatureWriter writer = store.getFeatureWriter(QueryBuilder.filtered(name.toString(),Filter.EXCLUDE));
        try{
            for(int i=0;i<10;i++){
                Feature f = writer.next();
                f.setPropertyValue("att1", "hop"+i);
                writer.write();
            }
        }finally{
            writer.close();
        }

        //check that we really have 10 features now
        reader = store.getFeatureReader(QueryBuilder.sorted(name.toString(),new SortBy[]{new DefaultSortBy(FF.property("att1"), SortOrder.ASCENDING)}));
        count = 0;
        try{
            while(reader.hasNext()){
                Feature f = reader.next();
                assertEquals(f.getPropertyValue("att1"),"hop"+count);
                count++;
            }
        }finally{
            reader.close();
        }
        assertEquals(count, 10);
        assertEquals(store.getCount(QueryBuilder.all(name)), 10);

        //check updating features
        writer = store.getFeatureWriter(QueryBuilder.all(name.toString()));
        count = 0;
        try{
            while(writer.hasNext()){
                Feature f = writer.next();
                f.setPropertyValue("att1", "hop"+count*count);
                writer.write();
                count++;
            }
        }finally{
            writer.close();
        }

        //check that all 10 features where updated
        reader = store.getFeatureReader(QueryBuilder.all(name));
        count = 0;
        try{
            while(reader.hasNext()){
                Feature f = reader.next();
                assertEquals(f.getPropertyValue("att1"),"hop"+(count*count));
                count++;
            }
        }finally{
            reader.close();
        }
        assertEquals(10,count);
        assertEquals(store.getCount(QueryBuilder.all(name)), 10);

        //check deleting features
        writer = store.getFeatureWriter(QueryBuilder.all(name));
        try{
            while(writer.hasNext()){
                Feature f = writer.next();
                writer.remove();
            }
        }finally{
            writer.close();
        }

        reader = store.getFeatureReader(QueryBuilder.all(name));
        count = 0;
        try{
            while(reader.hasNext()){
                reader.next();
                count++;
            }
        }finally{
            reader.close();
        }
        assertEquals(0,count);
        assertEquals(store.getCount(QueryBuilder.all(name)), 0);
    }

    @Test
    public void testQuerySupport() throws Exception {
        final FeatureTypeBuilder builder = new FeatureTypeBuilder();
        final MemoryFeatureStore store = new MemoryFeatureStore();

        //create the schema
        final GenericName name = NamesExt.create("http://test.com", "TestSchema1");
        builder.setName(name);
        builder.addAttribute(String.class).setName(AttributeConvention.IDENTIFIER_PROPERTY);
        builder.addAttribute(String.class).setName("string");
        builder.addAttribute(Double.class).setName("double");
        builder.addAttribute(Date.class).setName("date");
        final FeatureType type = builder.build();
        store.createFeatureType(type);
        final QueryBuilder qb = new QueryBuilder(name.toString());

        //create a few features
        FeatureWriter writer = store.getFeatureWriter(QueryBuilder.filtered(name.toString(),Filter.EXCLUDE));
        try{
            Feature f = writer.next();
            f.setPropertyValue("string", "hop3");
            f.setPropertyValue("double", 3d);
            f.setPropertyValue("date", new Date(1000L));
            writer.write();

            f = writer.next();
            f.setPropertyValue("string", "hop1");
            f.setPropertyValue("double", 1d);
            f.setPropertyValue("date", new Date(100000L));
            writer.write();

            f = writer.next();
            f.setPropertyValue("string", "hop2");
            f.setPropertyValue("double", 2d);
            f.setPropertyValue("date", new Date(10000L));
            writer.write();

        }finally{
            writer.close();
        }

        //quick count check
        FeatureReader reader = store.getFeatureReader(QueryBuilder.all(name));
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
        assertEquals(store.getCount(QueryBuilder.all(name)), 3);


        //ASCENDING ORDER ------------------------------------------------------

        //test sort by on string
        reader = store.getFeatureReader(QueryBuilder.sorted(name.toString(), new SortBy[]{FF.sort("string", SortOrder.ASCENDING)}));
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
        }finally{
            reader.close();
        }

        //test sort by on double
        reader = store.getFeatureReader(QueryBuilder.sorted(name.toString(), new SortBy[]{FF.sort("double", SortOrder.ASCENDING)}));
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
            assertEquals(sf.getPropertyValue("double"),3d);
        }finally{
            reader.close();
        }

        //test sort by on date
        reader = store.getFeatureReader(QueryBuilder.sorted(name.toString(), new SortBy[]{FF.sort("date", SortOrder.ASCENDING)}));
        try{
            Feature sf;
            reader.hasNext();
            sf = reader.next();
            assertEquals(sf.getPropertyValue("date"),new Date(1000L));
            reader.hasNext();
            sf = reader.next();
            assertEquals(sf.getPropertyValue("date"),new Date(10000L));
            reader.hasNext();
            sf = reader.next();
            assertEquals(sf.getPropertyValue("date"),new Date(100000L));
        }finally{
            reader.close();
        }

        //DESCENDING ORDER ------------------------------------------------------

        //test sort by on string
        reader = store.getFeatureReader(QueryBuilder.sorted(name.toString(), new SortBy[]{FF.sort("string", SortOrder.DESCENDING)}));
        try{
            Feature sf;
            reader.hasNext();
            sf = reader.next();
            assertEquals(sf.getPropertyValue("string"),"hop3");
            reader.hasNext();
            sf = reader.next();
            assertEquals(sf.getPropertyValue("string"),"hop2");
            reader.hasNext();
            sf = reader.next();
            assertEquals(sf.getPropertyValue("string"),"hop1");
        }finally{
            reader.close();
        }

        //test sort by on double
        reader = store.getFeatureReader(QueryBuilder.sorted(name.toString(), new SortBy[]{FF.sort("double", SortOrder.DESCENDING)}));
        try{
            Feature sf;
            reader.hasNext();
            sf = reader.next();
            assertEquals(sf.getPropertyValue("double"),3d);
            reader.hasNext();
            sf = reader.next();
            assertEquals(sf.getPropertyValue("double"),2d);
            reader.hasNext();
            sf = reader.next();
            assertEquals(sf.getPropertyValue("double"),1d);
        }finally{
            reader.close();
        }

        //test sort by on date
        reader = store.getFeatureReader(QueryBuilder.sorted(name.toString(), new SortBy[]{FF.sort("date", SortOrder.DESCENDING)}));
        try{
            Feature sf;
            reader.hasNext();
            sf = reader.next();
            assertEquals(sf.getPropertyValue("date"),new Date(100000L));
            reader.hasNext();
            sf = reader.next();
            assertEquals(sf.getPropertyValue("date"),new Date(10000L));
            reader.hasNext();
            sf = reader.next();
            assertEquals(sf.getPropertyValue("date"),new Date(1000L));

            assertFalse(reader.hasNext());
        }finally{
            reader.close();
        }


        //TEST FILTER ----------------------------------------------------------
        //test on date
        Filter filter = FF.equals(FF.property("date"), FF.literal(new Date(10000L)));
        Query query = QueryBuilder.filtered(name.toString(),filter);
        assertEquals(store.getCount(query),1);

        reader = store.getFeatureReader(query);
        try{
            Feature sf;
            reader.hasNext();
            sf = reader.next();
            assertEquals(sf.getPropertyValue("date"),new Date(10000L));
            assertFalse(reader.hasNext());
        }finally{
            reader.close();
        }

        //test on double
        filter = FF.equals(FF.property("double"), FF.literal(2d));
        query = QueryBuilder.filtered(name.toString(),filter);
        assertEquals(store.getCount(query),1);

        reader = store.getFeatureReader(query);
        try{
            Feature sf;
            reader.hasNext();
            sf = reader.next();
            assertEquals(sf.getPropertyValue("double"),2d);
            assertFalse(reader.hasNext());
        }finally{
            reader.close();
        }

        //test on string
        filter = FF.equals(FF.property("string"), FF.literal("hop1"));
        query = QueryBuilder.filtered(name.toString(),filter);
        assertEquals(store.getCount(query),1);

        reader = store.getFeatureReader(query);
        try{
            Feature sf;
            reader.hasNext();
            sf = reader.next();
            assertEquals(sf.getPropertyValue("string"),"hop1");
            assertFalse(reader.hasNext());
        }finally{
            reader.close();
        }

        //TEST ATTRIBUTS LIMITATION --------------------------------------------
        qb.reset();
        qb.setTypeName(name);
        qb.setProperties(new String[]{"string","date"});
        query = qb.buildQuery();

        assertEquals(store.getCount(query),3);

        reader = store.getFeatureReader(query);
        assertEquals(2,reader.getFeatureType().getProperties(true).size());
        assertNotNull(reader.getFeatureType().getProperty("string"));
        assertNotNull(reader.getFeatureType().getProperty("date"));

        try{
            while(reader.hasNext()){
                Feature f = reader.next();
                assertTrue( f.getPropertyValue("string") instanceof String );
                assertTrue( f.getPropertyValue("date") instanceof Date );
            }
        }finally{
            reader.close();
        }

        //TEST start index -----------------------------------------------------
        qb.reset();
        qb.setTypeName(name);
        qb.setStartIndex(1);
        qb.setSortBy(new SortBy[]{FF.sort("date", SortOrder.DESCENDING)});
        query = qb.buildQuery();

        assertEquals(2,store.getCount(query));

        reader = store.getFeatureReader(query);
        assertEquals(reader.getFeatureType().getProperties(true).size(),4);

        try{
            Feature sf;
            reader.hasNext();
            sf = reader.next();
            assertEquals(sf.getPropertyValue("date"),new Date(10000L));
            reader.hasNext();
            sf = reader.next();
            assertEquals(sf.getPropertyValue("date"),new Date(1000L));

            assertFalse(reader.hasNext());
        }finally{
            reader.close();
        }

        //TEST max features ----------------------------------------------------
        qb.reset();
        qb.setTypeName(name);
        qb.setMaxFeatures(1);
        qb.setSortBy(new SortBy[]{FF.sort("date", SortOrder.DESCENDING)});
        query = qb.buildQuery();

        assertEquals(1,store.getCount(query));

        reader = store.getFeatureReader(query);

        try{
            Feature sf;
            reader.hasNext();
            sf = reader.next();
            assertEquals(sf.getPropertyValue("date"),new Date(100000L));

            assertFalse(reader.hasNext());
        }finally{
            reader.close();
        }

    }

    @Test
    public void testQueryCRSReprojectSupport() throws Exception {
        final FeatureTypeBuilder builder = new FeatureTypeBuilder();
        final MemoryFeatureStore store = new MemoryFeatureStore();
        final GeometryFactory gf = new GeometryFactory();

        //create the schema
        final GenericName name = NamesExt.create("http://test.com", "TestSchema1");
        builder.setName(name);
        builder.addAttribute(String.class).setName(AttributeConvention.IDENTIFIER_PROPERTY);
        builder.addAttribute(Point.class).setName("geometry").setCRS(CRS.forCode("EPSG:27582")).addRole(AttributeRole.DEFAULT_GEOMETRY);
        builder.addAttribute(String.class).setName("string");
        final FeatureType type = builder.build();
        store.createFeatureType(type);
        final QueryBuilder qb = new QueryBuilder(name.toString());

        //create a few features
        FeatureWriter writer = store.getFeatureWriter(QueryBuilder.filtered(name.toString(),Filter.EXCLUDE));
        try{
            Feature f = writer.next();
            f.setPropertyValue("geometry", gf.createPoint(new Coordinate(10, 10)));
            f.setPropertyValue("string", "hop1");
            writer.write();

        }finally{
            writer.close();
        }

        //quick count check
        FeatureReader reader = store.getFeatureReader(QueryBuilder.all(name));
        int count = 0;
        try{
            while(reader.hasNext()){
                reader.next();
                count++;
            }
        }finally{
            reader.close();
        }
        assertEquals(count, 1);
        assertEquals(store.getCount(QueryBuilder.all(name)), 1);

        //try simple read-------------------------------------------------------
        //check geometry has not been modified
        qb.reset();
        qb.setTypeName(name);
        Query query = qb.buildQuery();

        assertEquals(1,store.getCount(query));

        reader = store.getFeatureReader(query);

        try{
            Feature sf;
            reader.hasNext();
            sf = reader.next();
            assertEquals( ((Point)sf.getPropertyValue("geometry")).getX(), gf.createPoint(new Coordinate(10, 10)).getX() );
            assertEquals( ((Point)sf.getPropertyValue("geometry")).getY(), gf.createPoint(new Coordinate(10, 10)).getY() );

            assertFalse(reader.hasNext());
        }finally{
            reader.close();
        }

        //test reprojection-------------------------------------------------------
        qb.reset();
        qb.setTypeName(name);
        qb.setCRS(CommonCRS.WGS84.geographic());
        query = qb.buildQuery();

        assertEquals(1,store.getCount(query));

        reader = store.getFeatureReader(query);

        try{
            Feature sf;
            reader.hasNext();
            sf = reader.next();
            assertNotSame( ((Point)sf.getPropertyValue("geometry")).getX(), gf.createPoint(new Coordinate(10, 10)).getX() );
            assertNotSame( ((Point)sf.getPropertyValue("geometry")).getY(), gf.createPoint(new Coordinate(10, 10)).getY() );

            assertFalse(reader.hasNext());
        }finally{
            reader.close();
        }

     }

    @Test
    public void testPreserveId() throws Exception{
        final FeatureTypeBuilder builder = new FeatureTypeBuilder();
        final MemoryFeatureStore store = new MemoryFeatureStore();
        Set<GenericName> names;

        names = store.getNames();
        assertEquals(0,names.size());

        //test creation of one schema ------------------------------------------
        GenericName name = NamesExt.create("http://test.com", "TestSchema1");
        builder.setName(name);
        builder.addAttribute(String.class).setName(AttributeConvention.IDENTIFIER_PROPERTY);
        builder.addAttribute(String.class).setName("att1");
        final FeatureType type1 = builder.build();

        store.createFeatureType(type1);

        names = store.getNames();
        assertEquals(1,names.size());
        GenericName n = names.iterator().next();

        assertEquals(n.tip().toString(), "TestSchema1");
        assertEquals(NamesExt.getNamespace(n), "http://test.com");


        //try to insert features -----------------------------------------------
        Collection<Feature> features = new ArrayList<>();
        Feature sfb = type1.newInstance();
        sfb.setPropertyValue(AttributeConvention.IDENTIFIER_PROPERTY.toString(),"myId1");
        sfb.setPropertyValue("att1", "hophop1");
        features.add(sfb);
        sfb = type1.newInstance();
        sfb.setPropertyValue(AttributeConvention.IDENTIFIER_PROPERTY.toString(),"myId2");
        sfb.setPropertyValue("att1", "hophop2");
        features.add(sfb);

        store.addFeatures(name.toString(), features);

        FeatureReader reader = store.getFeatureReader(QueryBuilder.all(name));
        Feature f = reader.next();
        assertEquals("myId1", FeatureExt.getId(f).getID());
        f = reader.next();
        assertEquals("myId2", FeatureExt.getId(f).getID());

        assertFalse(reader.hasNext());

    }

    @Test
    public void testCollectionAttributs() throws Exception{
        final FeatureTypeBuilder builder = new FeatureTypeBuilder();
        final MemoryFeatureStore store = new MemoryFeatureStore();
        Set<GenericName> names;

        names = store.getNames();
        assertEquals(0,names.size());

        //test creation of one schema ------------------------------------------
        GenericName name = NamesExt.create("http://test.com", "TestSchema1");
        builder.setName(name);
        builder.addAttribute(String.class).setName(AttributeConvention.IDENTIFIER_PROPERTY);
        builder.addAttribute(List.class).setName("ListAtt");
        builder.addAttribute(Map.class).setName("MapAtt");
        builder.addAttribute(Set.class).setName("SetAtt");
        final FeatureType type1 = builder.build();

        store.createFeatureType(type1);

        names = store.getNames();
        assertEquals(1,names.size());
        GenericName n = names.iterator().next();

        assertEquals(n.tip().toString(), "TestSchema1");
        assertEquals(NamesExt.getNamespace(n), "http://test.com");

        //try to insert features -----------------------------------------------
        Collection<Feature> features = new ArrayList<>();
        final Feature f1 = type1.newInstance();
        f1.setPropertyValue(AttributeConvention.IDENTIFIER_PROPERTY.toString(), "myId1");
        f1.setPropertyValue("ListAtt", Collections.singletonList("aListValue"));
        f1.setPropertyValue("MapAtt", Collections.singletonMap("aMapKey", "aMapValue"));
        f1.setPropertyValue("SetAtt", Collections.singleton("aSetValue"));
        features.add(f1);

        final Feature f2 = type1.newInstance();
        f2.setPropertyValue(AttributeConvention.IDENTIFIER_PROPERTY.toString(), "myId2");
        f2.setPropertyValue("ListAtt", Collections.singletonList("aListValue2"));
        f2.setPropertyValue("MapAtt", Collections.singletonMap("aMapKey2", "aMapValue2"));
        f2.setPropertyValue("SetAtt", Collections.singleton("aSetValue2"));
        features.add(f2);

        store.addFeatures(name.toString(), features);

        FeatureReader reader = store.getFeatureReader(QueryBuilder.all(name));

        int nb = 0;
        while(reader.hasNext()){
            nb++;
            final Feature f = reader.next();
            final String fid = FeatureExt.getId(f).getID();
            if("myId1".equals(fid)){
                assertTrue(f.getProperty("ListAtt").getValue() instanceof List);
                List lst = (List) f.getProperty("ListAtt").getValue();
                assertEquals("aListValue",lst.get(0));

                assertTrue(f.getProperty("MapAtt").getValue() instanceof Map);
                Map map = (Map) f.getProperty("MapAtt").getValue();
                assertEquals("aMapKey",map.keySet().iterator().next());
                assertEquals("aMapValue",map.values().iterator().next());

                assertTrue(f.getProperty("SetAtt").getValue() instanceof Set);
                Set set = (Set) f.getProperty("SetAtt").getValue();
                assertEquals("aSetValue",set.iterator().next());
            }else if("myId2".equals(fid)){
                assertTrue(f.getProperty("ListAtt").getValue() instanceof List);
                List lst = (List) f.getProperty("ListAtt").getValue();
                assertEquals("aListValue2",lst.get(0));

                assertTrue(f.getProperty("MapAtt").getValue() instanceof Map);
                Map map = (Map) f.getProperty("MapAtt").getValue();
                assertEquals("aMapKey2",map.keySet().iterator().next());
                assertEquals("aMapValue2",map.values().iterator().next());

                assertTrue(f.getProperty("SetAtt").getValue() instanceof Set);
                Set set = (Set) f.getProperty("SetAtt").getValue();
                assertEquals("aSetValue2",set.iterator().next());
            }else{
                fail("Unexpected feature with id : "+fid);
            }

        }

        assertEquals(2, nb);

    }

    @Test
    public void testNoIteratorUnclosed() throws Exception{
        final FeatureTypeBuilder builder = new FeatureTypeBuilder();
        final MemoryFeatureStore store = new MemoryFeatureStore();
        Set<GenericName> names;

        names = store.getNames();
        assertEquals(0,names.size());

        //test creation of one schema ------------------------------------------
        GenericName name = NamesExt.create("http://test.com", "TestSchema1");
        builder.setName(name);
        builder.addAttribute(List.class).setName("ListAtt");
        builder.addAttribute(Map.class).setName("MapAtt");
        builder.addAttribute(Set.class).setName("SetAtt");
        final FeatureType type1 = builder.build();

        store.createFeatureType(type1);

        store.isWritable(name.toString());

        final Session session = store.createSession(true);
        final FeatureCollection col = session.getFeatureCollection(QueryBuilder.all(name));

        col.isWritable();

    }

}
