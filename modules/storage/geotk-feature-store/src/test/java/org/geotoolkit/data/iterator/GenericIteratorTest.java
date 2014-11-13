/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2010, Open Source Geospatial Foundation (OSGeo)
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

package org.geotoolkit.data.iterator;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.Point;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;
import junit.framework.TestCase;
import org.apache.sis.storage.DataStoreException;
import org.geotoolkit.data.FeatureStoreUtilities;
import org.geotoolkit.data.FeatureCollection;
import org.geotoolkit.data.FeatureIterator;
import org.geotoolkit.data.FeatureReader;
import org.geotoolkit.data.FeatureWriter;
import org.geotoolkit.data.memory.GenericCachedFeatureIterator;
import org.geotoolkit.data.memory.GenericEmptyFeatureIterator;
import org.geotoolkit.data.memory.GenericFilterFeatureIterator;
import org.geotoolkit.data.memory.GenericMaxFeatureIterator;
import org.geotoolkit.data.memory.GenericModifyFeatureIterator;
import org.geotoolkit.data.memory.GenericReprojectFeatureIterator;
import org.geotoolkit.data.memory.GenericRetypeFeatureIterator;
import org.geotoolkit.data.memory.GenericSortByFeatureIterator;
import org.geotoolkit.data.memory.GenericStartIndexFeatureIterator;
import org.geotoolkit.data.memory.GenericTransformFeatureIterator;
import org.geotoolkit.data.memory.GenericWrapFeatureIterator;
import org.geotoolkit.data.query.Query;
import org.geotoolkit.data.query.QueryBuilder;
import org.geotoolkit.factory.FactoryFinder;
import org.geotoolkit.factory.Hints;
import org.geotoolkit.factory.HintsPending;
import org.geotoolkit.feature.type.DefaultName;
import org.geotoolkit.feature.FeatureTypeBuilder;
import org.geotoolkit.feature.FeatureUtilities;
import org.geotoolkit.feature.SchemaException;
import org.geotoolkit.geometry.jts.JTS;
import org.geotoolkit.geometry.jts.transform.GeometryScaleTransformer;
import org.geotoolkit.geometry.jts.transform.GeometryTransformer;
import org.geotoolkit.referencing.CRS;
import org.apache.sis.referencing.CommonCRS;
import org.junit.Test;
import org.geotoolkit.feature.Feature;
import org.geotoolkit.feature.FeatureFactory;
import org.geotoolkit.feature.Property;
import org.geotoolkit.feature.simple.SimpleFeature;
import org.geotoolkit.feature.simple.SimpleFeatureType;
import org.geotoolkit.feature.type.AttributeDescriptor;
import org.geotoolkit.feature.type.FeatureType;
import org.geotoolkit.feature.type.Name;
import org.geotoolkit.feature.type.PropertyDescriptor;
import org.opengis.filter.Filter;
import org.opengis.filter.FilterFactory;
import org.opengis.filter.sort.SortBy;
import org.opengis.filter.sort.SortOrder;
import org.opengis.referencing.NoSuchAuthorityCodeException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.util.FactoryException;

/**
 * Tests of the different iterators.
 *
 * @author Johann Sorel (Geomatys)
 */
public class GenericIteratorTest extends TestCase{

    protected static final FeatureFactory AF = FeatureFactory.LENIENT;

    private static final double DELTA = 0.000001d;
    private static final FilterFactory FF = FactoryFinder.getFilterFactory(null);
    private static final GeometryFactory GF = new GeometryFactory();

    private final FeatureCollection<SimpleFeature> collection;
    private final FeatureCollection<Feature> collectionComplex;
    private final Name name;
    private final SimpleFeatureType originalType;
    private final SimpleFeatureType reducedType;
    private final SimpleFeatureType reprojectedType;
    private final String id1;
    private final String id2;
    private final String id3;
    private final String cid1;
    private final String cid2;

    private final SimpleFeature sf1;
    private final SimpleFeature sf2;
    private final SimpleFeature sf3;

    public GenericIteratorTest() throws NoSuchAuthorityCodeException, FactoryException{
        final FeatureTypeBuilder builder = new FeatureTypeBuilder();
        name = new DefaultName("http://test.com", "TestSchema");
        builder.reset();
        builder.setName(name);
        builder.add("att_geom", Point.class, CommonCRS.WGS84.normalizedGeographic());
        builder.add("att_string", String.class);
        builder.add("att_double", Double.class);
        originalType = builder.buildSimpleFeatureType();

        //build a reduced type for retype iterator
        builder.reset();
        builder.setName(name);
        builder.add("att_double", Double.class);
        reducedType = builder.buildSimpleFeatureType();

        //build a reprojected type for reproject iterator
        builder.reset();
        builder.setName(name);
        builder.add("att_geom", Point.class, CRS.decode("EPSG:4326"));
        builder.add("att_string", String.class);
        builder.add("att_double", Double.class);
        reprojectedType = builder.buildSimpleFeatureType();


        collection = FeatureStoreUtilities.collection("id", originalType);

        sf1 = FeatureUtilities.defaultFeature(originalType, "");
        sf1.setAttribute("att_geom", GF.createPoint(new Coordinate(3, 0)));
        sf1.setAttribute("att_string", "bbb");
        sf1.setAttribute("att_double", 3d);
        collection.add(sf1);
        id1 = name.getLocalPart()+"."+0;

        sf2 = FeatureUtilities.defaultFeature(originalType, "");
        sf2.setAttribute("att_geom", GF.createPoint(new Coordinate(1, 0)));
        sf2.setAttribute("att_string", "ccc");
        sf2.setAttribute("att_double", 1d);
        collection.add(sf2);
        id2 = name.getLocalPart()+"."+1;

        sf3 = FeatureUtilities.defaultFeature(originalType, "");
        sf3.setAttribute("att_geom", GF.createPoint(new Coordinate(2, 0)));
        sf3.setAttribute("att_string", "aaa");
        sf3.setAttribute("att_double", 2d);
        collection.add(sf3);
        id3 = name.getLocalPart()+"."+2;

        builder.reset();
        builder.setName(name);
        builder.add("att_string", String.class,0,1,false,null);
        builder.add("att_double", Double.class,0,1,false,null);
        FeatureType ct = builder.buildFeatureType();

        collectionComplex = FeatureStoreUtilities.collection("cid", ct);

        cid1 = "complex-1";
        cid2 = "complex-2";
        Collection<Property> props = new ArrayList<Property>();
        props.add(AF.createAttribute("aaaa", (AttributeDescriptor) ct.getDescriptor("att_string"), null));
        props.add(AF.createAttribute(12, (AttributeDescriptor) ct.getDescriptor("att_double"), null));
        collectionComplex.add(AF.createFeature(props, ct, cid1));

        props = new ArrayList<Property>();
        props.add(AF.createAttribute("bbbb", (AttributeDescriptor) ct.getDescriptor("att_string"), null));
        props.add(AF.createAttribute(7, (AttributeDescriptor) ct.getDescriptor("att_double"), null));
        collectionComplex.add(AF.createFeature(props, ct, cid2));

    }

    private void testIterationOnNext(final FeatureIterator ite, final int size){

        //check that there is no iteration on hasnext()
        for(int i=0; i<size+10; i++){
            if(!ite.hasNext()){
                fail("hasNext() has changed to false, suspicious iteration on hasNext call.");
            }
        }

        //check iteration on next, whithout calling hasNext()
        Feature last = ite.next();
        for(int i=1;i<size;i++){
            Feature current = ite.next();
            assertFalse(last.equals(current));
            last = current;
        }

    }

    @Test
    public void testEmptyIterator(){
        final FeatureIterator iterator = GenericEmptyFeatureIterator.createIterator();

        assertFalse(iterator.hasNext());

        try{
            iterator.next();
            fail("Next on empty iterator should have raised a no such element exception.");
        }catch(NoSuchElementException ex){
            //ok
        }

        try{
            iterator.remove();
            fail("Remove should have raise an error.");
        }catch(Exception ex){
            //ok
        }

        iterator.close();
    }

    @Test
    public void testEmptyReader(){
        final FeatureReader iterator = GenericEmptyFeatureIterator.createReader(collection.getFeatureType());

        assertEquals(iterator.getFeatureType(), collection.getFeatureType());
        assertFalse(iterator.hasNext());

        try{
            iterator.next();
            fail("Next on empty iterator should have raised a no such element exception.");
        }catch(NoSuchElementException ex){
            //ok
        }

        try{
            iterator.remove();
            fail("Remove should have raise an error.");
        }catch(Exception ex){
            //ok
        }

        iterator.close();
    }

    @Test
    public void testEmptyWriter(){
        final FeatureWriter iterator = GenericEmptyFeatureIterator.createWriter(collection.getFeatureType());

        assertEquals(iterator.getFeatureType(), collection.getFeatureType());
        assertFalse(iterator.hasNext());

        try{
            iterator.next();
            fail("Next on empty iterator should have raised a no such element exception.");
        }catch(NoSuchElementException ex){
            //ok
        }

        try{
            iterator.remove();
            fail("Remove should have raise an error.");
        }catch(Exception ex){
            //ok
        }

        iterator.close();
    }

    @Test
    public void testCacheIterator(){
        FeatureIterator ite = GenericCachedFeatureIterator.wrap(collection.iterator(), 1);
        assertEquals(3, FeatureStoreUtilities.calculateCount(ite));

        ite = GenericCachedFeatureIterator.wrap(collection.iterator(), 1);

        int mask = 0;
        Feature f;
        while(ite.hasNext()){
            f = ite.next();
            final String id = f.getIdentifier().getID();
                        
            if(id1.equals(id)){
                mask |= 1<<0;
            }else if(id2.equals(id)){
                mask |= 1<<1;
            }else if(id3.equals(id)){
                mask |= 1<<2;
            }
        }

        if(mask!=7){
            fail("missing features in iterations");
        }
        
    }

    @Test
    public void testFilterIterator(){
        FeatureIterator ite = GenericFilterFeatureIterator.wrap(collection.iterator(), Filter.INCLUDE);
        assertEquals(3, FeatureStoreUtilities.calculateCount(ite));

        ite = GenericFilterFeatureIterator.wrap(collection.iterator(), Filter.EXCLUDE);
        assertEquals(0, FeatureStoreUtilities.calculateCount(ite));

        ite = GenericFilterFeatureIterator.wrap(collection.iterator(), FF.equals(FF.literal("aaa"), FF.property("att_string")));
        assertEquals(1, FeatureStoreUtilities.calculateCount(ite));
        ite = GenericFilterFeatureIterator.wrap(collection.iterator(), FF.equals(FF.literal("aaa"), FF.property("att_string")));

        assertEquals(ite.next().getIdentifier().getID(),id3);
        try{
            ite.next();
            fail("Should have raise a no such element exception.");
        }catch(NoSuchElementException ex){
            //ok
        }

        //check has next do not iterate
        ite = GenericFilterFeatureIterator.wrap(collection.iterator(), Filter.INCLUDE);
        testIterationOnNext(ite, 3);

        //check sub iterator is properly closed
        CheckCloseFeatureIterator checkIte = new CheckCloseFeatureIterator(collection.iterator());
        assertFalse(checkIte.isClosed());
        ite = GenericFilterFeatureIterator.wrap(checkIte, Filter.INCLUDE);
        while(ite.hasNext()) ite.next();
        ite.close();
        assertTrue(checkIte.isClosed());
    }

    @Test
    public void testMaxIterator(){
        FeatureIterator ite = GenericMaxFeatureIterator.wrap(collection.iterator(), 10);
        assertEquals(3, FeatureStoreUtilities.calculateCount(ite));

        ite = GenericMaxFeatureIterator.wrap(collection.iterator(), 2);
        assertEquals(2, FeatureStoreUtilities.calculateCount(ite));

        ite = GenericMaxFeatureIterator.wrap(collection.iterator(), 1);
        assertEquals(ite.next().getIdentifier().getID(),id1);
        try{
            ite.next();
            fail("Should have raise a no such element exception.");
        }catch(NoSuchElementException ex){
            //ok
        }

        //check has next do not iterate
        ite = GenericMaxFeatureIterator.wrap(collection.iterator(), 10);
        testIterationOnNext(ite, 3);

        //check sub iterator is properly closed
        CheckCloseFeatureIterator checkIte = new CheckCloseFeatureIterator(collection.iterator());
        assertFalse(checkIte.isClosed());
        ite = GenericMaxFeatureIterator.wrap(checkIte, 10);
        while(ite.hasNext()) ite.next();
        ite.close();
        assertTrue(checkIte.isClosed());
    }

    @Test
    public void testModifyIterator(){
        Filter filter = Filter.INCLUDE;
        Map<PropertyDescriptor,Object> values = new HashMap<PropertyDescriptor, Object>();
        values.put(originalType.getDescriptor("att_string"), "toto");

        FeatureIterator ite = GenericModifyFeatureIterator.wrap(collection.iterator(), filter, values);
        assertEquals(3, FeatureStoreUtilities.calculateCount(ite));

        ite = GenericModifyFeatureIterator.wrap(collection.iterator(), filter, values);
        while(ite.hasNext()){
            assertTrue(ite.next().getProperty("att_string").getValue().equals("toto"));
        }


        filter = FF.equals(FF.literal("aaa"), FF.property("att_string"));
        ite = GenericModifyFeatureIterator.wrap(collection.iterator(), filter, values);
        assertEquals(3, FeatureStoreUtilities.calculateCount(ite));

        ite = GenericModifyFeatureIterator.wrap(collection.iterator(), filter, values);
        while(ite.hasNext()){
            Feature f = ite.next();
            if(f.getIdentifier().getID().equals(id3)){
                assertTrue(f.getProperty("att_string").getValue().equals("toto"));
            }else{
                assertFalse(f.getProperty("att_string").getValue().equals("toto"));
            }
        }


        ite = GenericModifyFeatureIterator.wrap(collection.iterator(), filter, values);
        ite.next();
        ite.next();
        ite.next();
        try{
            ite.next();
            fail("Should have raise a no such element exception.");
        }catch(NoSuchElementException ex){
            //ok
        }

        //check has next do not iterate
        ite = GenericModifyFeatureIterator.wrap(collection.iterator(), filter, values);
        testIterationOnNext(ite, 3);

        //check sub iterator is properly closed
        CheckCloseFeatureIterator checkIte = new CheckCloseFeatureIterator(collection.iterator());
        assertFalse(checkIte.isClosed());
        ite = GenericModifyFeatureIterator.wrap(checkIte, filter, values);
        while(ite.hasNext()) ite.next();
        ite.close();
        assertTrue(checkIte.isClosed());

    }

    @Test
    public void testReprojectFeatureIterator() throws DataStoreException, FactoryException, SchemaException{
        QueryBuilder qb = new QueryBuilder();
        qb.setTypeName(originalType.getName());
        Query query = qb.buildQuery();
        FeatureReader reader = collection.getSession().getFeatureStore().getFeatureReader(query);

        final CoordinateReferenceSystem targetCRS = CRS.decode("EPSG:4326");

        FeatureReader retyped = GenericReprojectFeatureIterator.wrap(reader, targetCRS, new Hints());
        assertEquals(reprojectedType,retyped.getFeatureType());

        int mask = 0;
        Feature f;
        while(retyped.hasNext()){
            f = retyped.next();
            final String id = f.getIdentifier().getID();
            
            assertEquals(3, f.getProperties().size());
            assertEquals(targetCRS,JTS.findCoordinateReferenceSystem((Geometry)f.getProperty("att_geom").getValue()));
            
            if(id1.equals(id)){
                mask |= 1<<0;
                assertEquals(GF.createPoint(new Coordinate(0, 3)).toString(), f.getProperty("att_geom").getValue().toString());
            }else if(id2.equals(id)){
                mask |= 1<<1;
                assertEquals(GF.createPoint(new Coordinate(0, 1)).toString(), f.getProperty("att_geom").getValue().toString());
            }else if(id3.equals(id)){
                mask |= 1<<2;
                assertEquals(GF.createPoint(new Coordinate(0, 2)).toString(), f.getProperty("att_geom").getValue().toString());
            }
        }

        if(mask!=7){
            fail("missing features in iterations");
        }
        

        //check has next do not iterate
        reader = collection.getSession().getFeatureStore().getFeatureReader(query);
        retyped = GenericReprojectFeatureIterator.wrap(reader, CRS.decode("EPSG:4326"), new Hints());
        testIterationOnNext(retyped, 3);

        //check sub iterator is properly closed
        reader = collection.getSession().getFeatureStore().getFeatureReader(query);
        CheckCloseFeatureIterator checkIte = new CheckCloseFeatureIterator(reader);
        assertFalse(checkIte.isClosed());
        retyped = GenericReprojectFeatureIterator.wrap(checkIte, CRS.decode("EPSG:4326"), new Hints());
        while(retyped.hasNext()) retyped.next();
        retyped.close();
        assertTrue(checkIte.isClosed());
    }

    @Test
    public void testTransformFeatureIterator() throws DataStoreException{
        final FeatureTypeBuilder builder = new FeatureTypeBuilder();
        final DefaultName name = new DefaultName("http://test.com", "TestSchema");
        builder.reset();
        builder.setName(name);
        builder.add("att_geom", LineString.class, CommonCRS.WGS84.normalizedGeographic());
        final SimpleFeatureType type = builder.buildSimpleFeatureType();

        final LineString geom = GF.createLineString(
                new Coordinate[]{
                    new Coordinate(0, 0),
                    new Coordinate(15, 12), //dx 15 , dy 12
                    new Coordinate(8, 28), //dx 7 , dy 16
                    new Coordinate(9, 31), //dx 1 , dy 3
                    new Coordinate(-5, 11), //dx 14 , dy 20
                    new Coordinate(-1, 9) //dx 4 , dy 2
                });

        final FeatureCollection collection = FeatureStoreUtilities.collection("id", type);
        SimpleFeature sf = FeatureUtilities.defaultFeature(type, "");
        sf.setAttribute("att_geom", geom);
        collection.add(sf);

        //get the reader -------------------------------------------------------
        QueryBuilder qb = new QueryBuilder();
        qb.setTypeName(originalType.getName());
        Query query = qb.buildQuery();
        FeatureReader reader = collection.getSession().getFeatureStore().getFeatureReader(query);

        //create the decimate reader -------------------------------------------
        final Hints hints = new Hints();
        hints.put(HintsPending.FEATURE_DETACHED, Boolean.TRUE);

        GeometryTransformer decim = new GeometryScaleTransformer(10, 10);
        FeatureReader retyped = GenericTransformFeatureIterator.wrap(reader,decim, hints);

        assertTrue(retyped.hasNext());

        LineString decimated = (LineString) retyped.next().getDefaultGeometryProperty().getValue();

        assertFalse(retyped.hasNext());
        retyped.close();

        assertEquals(4, decimated.getNumPoints());
        assertEquals(geom.getGeometryN(0).getCoordinate(), decimated.getGeometryN(0).getCoordinate());
        assertEquals(geom.getGeometryN(1).getCoordinate(), decimated.getGeometryN(1).getCoordinate());
        assertEquals(geom.getGeometryN(2).getCoordinate(), decimated.getGeometryN(2).getCoordinate());
        assertEquals(geom.getGeometryN(4).getCoordinate(), decimated.getGeometryN(3).getCoordinate());


        //check the original geometry has not been modified
        reader = collection.getSession().getFeatureStore().getFeatureReader(query);
        assertTrue(reader.hasNext());

        LineString notDecimated = (LineString) reader.next().getDefaultGeometryProperty().getValue();
        assertEquals(6, notDecimated.getNumPoints());
        assertFalse(reader.hasNext());
        reader.close();



        // same test but with reuse hint ---------------------------------------
        reader = collection.getSession().getFeatureStore().getFeatureReader(query);
        hints.put(HintsPending.FEATURE_DETACHED, Boolean.FALSE);

        decim = new GeometryScaleTransformer(10, 10);
        retyped = GenericTransformFeatureIterator.wrap(reader,decim, hints);

        assertTrue(retyped.hasNext());

        decimated = (LineString) retyped.next().getDefaultGeometryProperty().getValue();

        assertFalse(retyped.hasNext());
        retyped.close();

        assertEquals(4, decimated.getNumPoints());
        assertEquals(geom.getGeometryN(0).getCoordinate(), decimated.getGeometryN(0).getCoordinate());
        assertEquals(geom.getGeometryN(1).getCoordinate(), decimated.getGeometryN(1).getCoordinate());
        assertEquals(geom.getGeometryN(2).getCoordinate(), decimated.getGeometryN(2).getCoordinate());
        assertEquals(geom.getGeometryN(4).getCoordinate(), decimated.getGeometryN(3).getCoordinate());


        //check the original geometry has not been modified
        reader = collection.getSession().getFeatureStore().getFeatureReader(query);
        assertTrue(reader.hasNext());

        notDecimated = (LineString) reader.next().getDefaultGeometryProperty().getValue();
        assertEquals(6, notDecimated.getNumPoints());
        assertFalse(reader.hasNext());
        reader.close();
    }

    @Test
    public void testRetypeFeatureIterator() throws DataStoreException{
        QueryBuilder qb = new QueryBuilder();
        qb.setTypeName(originalType.getName());
        Query query = qb.buildQuery();
        FeatureReader reader = collection.getSession().getFeatureStore().getFeatureReader(query);

        FeatureReader retyped = GenericRetypeFeatureIterator.wrap(reader,reducedType,null);
        assertEquals(reducedType,retyped.getFeatureType());

        int mask = 0;
        Feature f;
        while(retyped.hasNext()){
            f = retyped.next();
            final String id = f.getIdentifier().getID();
            
            assertEquals(1, f.getProperties().size());
            
            if(id1.equals(id)){
                mask |= 1<<0;
                assertEquals(3d, (Double)f.getProperty("att_double").getValue(), DELTA);
            }else if(id2.equals(id)){
                mask |= 1<<1;
                assertEquals(1d, (Double)f.getProperty("att_double").getValue(), DELTA);
            }else if(id3.equals(id)){
                mask |= 1<<2;
                assertEquals(2d, (Double)f.getProperty("att_double").getValue(), DELTA);
            }
        }

        if(mask!=7){
            fail("missing features in iterations");
        }
        
        //check has next do not iterate
        reader = collection.getSession().getFeatureStore().getFeatureReader(query);
        retyped = GenericRetypeFeatureIterator.wrap(reader,reducedType,null);
        testIterationOnNext(retyped, 3);

        //check sub iterator is properly closed
        reader = collection.getSession().getFeatureStore().getFeatureReader(query);
        CheckCloseFeatureIterator checkIte = new CheckCloseFeatureIterator(reader);
        assertFalse(checkIte.isClosed());
        retyped = GenericRetypeFeatureIterator.wrap(checkIte,reducedType,null);
        while(retyped.hasNext()) retyped.next();
        retyped.close();
        assertTrue(checkIte.isClosed());

    }

    @Test
    public void testSortByIterator(){
        SortBy[] sorts = new SortBy[]{
            FF.sort("att_string", SortOrder.ASCENDING)
        };

        FeatureIterator ite = GenericSortByFeatureIterator.wrap(collection.iterator(), sorts);
        assertEquals(3, FeatureStoreUtilities.calculateCount(ite));


        ite = GenericSortByFeatureIterator.wrap(collection.iterator(), sorts);
        assertEquals(id3,ite.next().getIdentifier().getID());
        assertEquals(id1,ite.next().getIdentifier().getID());
        assertEquals(id2,ite.next().getIdentifier().getID());

        try{
            ite.next();
            fail("Should have raise a no such element exception.");
        }catch(NoSuchElementException ex){
            //ok
        }

        //check has next do not iterate
        ite = GenericSortByFeatureIterator.wrap(collection.iterator(), sorts);
        testIterationOnNext(ite, 3);

        //check sub iterator is properly closed
        CheckCloseFeatureIterator checkIte = new CheckCloseFeatureIterator(collection.iterator());
        assertFalse(checkIte.isClosed());
        ite = GenericSortByFeatureIterator.wrap(checkIte, sorts);
        while(ite.hasNext()) ite.next();
        ite.close();
        assertTrue(checkIte.isClosed());
    }

    @Test
    public void testSortByIteratorOnComplex(){

        //test string sort -----------------------------------------------------
        SortBy[] sorts = new SortBy[]{
            FF.sort("att_string", SortOrder.DESCENDING)
        };

        FeatureIterator ite = GenericSortByFeatureIterator.wrap(collectionComplex.iterator(), sorts);
        assertEquals(2, FeatureStoreUtilities.calculateCount(ite));

        ite = GenericSortByFeatureIterator.wrap(collectionComplex.iterator(), sorts);
        assertEquals(ite.next().getIdentifier().getID(),cid2);
        assertEquals(ite.next().getIdentifier().getID(),cid1);

        try{
            ite.next();
            fail("Should have raise a no such element exception.");
        }catch(NoSuchElementException ex){
            //ok
        }

        //test string sort -----------------------------------------------------
        sorts = new SortBy[]{
            FF.sort("att_double", SortOrder.DESCENDING)
        };

        ite = GenericSortByFeatureIterator.wrap(collectionComplex.iterator(), sorts);
        assertEquals(2, FeatureStoreUtilities.calculateCount(ite));

        ite = GenericSortByFeatureIterator.wrap(collectionComplex.iterator(), sorts);
        assertEquals(ite.next().getIdentifier().getID(),cid1);
        assertEquals(ite.next().getIdentifier().getID(),cid2);

        try{
            ite.next();
            fail("Should have raise a no such element exception.");
        }catch(NoSuchElementException ex){
            //ok
        }

        //test double sort -----------------------------------------------------
        sorts = new SortBy[]{
            FF.sort("att_double", SortOrder.ASCENDING)
        };

        ite = GenericSortByFeatureIterator.wrap(collectionComplex.iterator(), sorts);
        assertEquals(2, FeatureStoreUtilities.calculateCount(ite));

        ite = GenericSortByFeatureIterator.wrap(collectionComplex.iterator(), sorts);
        assertEquals(ite.next().getIdentifier().getID(),cid2);
        assertEquals(ite.next().getIdentifier().getID(),cid1);

        try{
            ite.next();
            fail("Should have raise a no such element exception.");
        }catch(NoSuchElementException ex){
            //ok
        }


        //check has next do not iterate
        ite = GenericSortByFeatureIterator.wrap(collectionComplex.iterator(), sorts);
        testIterationOnNext(ite, 2);

        //check sub iterator is properly closed
        CheckCloseFeatureIterator checkIte = new CheckCloseFeatureIterator(collection.iterator());
        assertFalse(checkIte.isClosed());
        ite = GenericSortByFeatureIterator.wrap(checkIte, sorts);
        while(ite.hasNext()) ite.next();
        ite.close();
        assertTrue(checkIte.isClosed());
    }

    @Test
    public void testStartIndexIterator(){
        FeatureIterator ite = GenericStartIndexFeatureIterator.wrap(collection.iterator(), 0);
        assertEquals(3, FeatureStoreUtilities.calculateCount(ite));

        ite = GenericStartIndexFeatureIterator.wrap(collection.iterator(), 1);
        assertEquals(2, FeatureStoreUtilities.calculateCount(ite));

        ite = GenericStartIndexFeatureIterator.wrap(collection.iterator(), 2);
        assertTrue(ite.next() != null);
        try{
            ite.next();
            fail("Should have raise a no such element exception.");
        }catch(NoSuchElementException ex){
            //ok
        }

        //check has next do not iterate
        ite = GenericStartIndexFeatureIterator.wrap(collection.iterator(), 1);
        testIterationOnNext(ite, 2);

        //check sub iterator is properly closed
        CheckCloseFeatureIterator checkIte = new CheckCloseFeatureIterator(collection.iterator());
        assertFalse(checkIte.isClosed());
        ite = GenericStartIndexFeatureIterator.wrap(checkIte, 1);
        while(ite.hasNext()) ite.next();
        ite.close();
        assertTrue(checkIte.isClosed());
    }

    @Test
    public void testWrapIterator(){

        //check has next do not iterate
        FeatureIterator ite = GenericWrapFeatureIterator.wrapToIterator(collection.iterator());
        testIterationOnNext(ite, 3);

        //check sub iterator is properly closed
        CheckCloseFeatureIterator checkIte = new CheckCloseFeatureIterator(collection.iterator());
        assertFalse(checkIte.isClosed());
        ite = GenericWrapFeatureIterator.wrapToIterator(checkIte);
        while(ite.hasNext()) ite.next();
        ite.close();
        assertTrue(checkIte.isClosed());
    }

    @Test
    public void testWrapReader(){
        //check has next do not iterate
        FeatureReader reader = GenericWrapFeatureIterator.wrapToReader(collection.iterator(),collection.getFeatureType());
        testIterationOnNext(reader, 3);

        //check sub iterator is properly closed
        CheckCloseFeatureIterator checkIte = new CheckCloseFeatureIterator(collection.iterator());
        assertFalse(checkIte.isClosed());
        reader = GenericWrapFeatureIterator.wrapToReader(checkIte,collection.getFeatureType());
        while(reader.hasNext()) reader.next();
        reader.close();
        assertTrue(checkIte.isClosed());
    }


}
