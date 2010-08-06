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
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.Point;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;
import junit.framework.TestCase;

import org.geotoolkit.storage.DataStoreException;
import org.geotoolkit.data.DataUtilities;
import org.geotoolkit.data.FeatureCollection;
import org.geotoolkit.data.FeatureIterator;
import org.geotoolkit.data.FeatureReader;
import org.geotoolkit.data.FeatureWriter;
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
import org.geotoolkit.feature.DefaultName;
import org.geotoolkit.feature.SchemaException;
import org.geotoolkit.feature.simple.SimpleFeatureBuilder;
import org.geotoolkit.feature.FeatureTypeBuilder;
import org.geotoolkit.feature.LenientFeatureFactory;
import org.geotoolkit.geometry.jts.transform.GeometryTransformer;
import org.geotoolkit.geometry.jts.transform.GeometryScaleTransformer;
import org.geotoolkit.referencing.CRS;
import org.geotoolkit.referencing.crs.DefaultGeographicCRS;

import org.junit.Test;

import org.opengis.feature.Feature;
import org.opengis.feature.FeatureFactory;
import org.opengis.feature.Property;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.feature.type.AttributeDescriptor;
import org.opengis.feature.type.FeatureType;
import org.opengis.feature.type.Name;
import org.opengis.feature.type.PropertyDescriptor;
import org.opengis.filter.Filter;
import org.opengis.filter.FilterFactory;
import org.opengis.filter.sort.SortBy;
import org.opengis.filter.sort.SortOrder;
import org.opengis.util.FactoryException;
import org.opengis.referencing.NoSuchAuthorityCodeException;

/**
 * Tests of the different iterators.
 *
 * @author Johann Sorel (Geomatys)
 */
public class GenericIteratorTest extends TestCase{

    protected static final FeatureFactory AF = FactoryFinder
            .getFeatureFactory(new Hints(Hints.FEATURE_FACTORY, LenientFeatureFactory.class));

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

    public GenericIteratorTest() throws NoSuchAuthorityCodeException, FactoryException{
        final FeatureTypeBuilder builder = new FeatureTypeBuilder();
        name = new DefaultName("http://test.com", "TestSchema");
        builder.reset();
        builder.setName(name);
        builder.add("att_geom", Point.class, DefaultGeographicCRS.WGS84);
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


        collection = DataUtilities.collection("id", originalType);

        SimpleFeature sf = SimpleFeatureBuilder.template(originalType, "");
        sf.setAttribute("att_geom", GF.createPoint(new Coordinate(3, 0)));
        sf.setAttribute("att_string", "bbb");
        sf.setAttribute("att_double", 3d);
        collection.add(sf);
        id1 = name.getLocalPart()+"."+0;

        sf = SimpleFeatureBuilder.template(originalType, "");
        sf.setAttribute("att_geom", GF.createPoint(new Coordinate(1, 0)));
        sf.setAttribute("att_string", "ccc");
        sf.setAttribute("att_double", 1d);
        collection.add(sf);
        id2 = name.getLocalPart()+"."+1;

        sf = SimpleFeatureBuilder.template(originalType, "");
        sf.setAttribute("att_geom", GF.createPoint(new Coordinate(2, 0)));
        sf.setAttribute("att_string", "aaa");
        sf.setAttribute("att_double", 2d);
        collection.add(sf);
        id3 = name.getLocalPart()+"."+2;

        builder.reset();
        builder.setName(name);
        builder.add("att_string", String.class,0,1,false,null);
        builder.add("att_double", Double.class,0,1,false,null);
        FeatureType ct = builder.buildFeatureType();

        collectionComplex = DataUtilities.collection("cid", ct);

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

    private void testIterationOnNext(FeatureIterator ite, int size){

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
    public void testFilterIterator(){
        FeatureIterator ite = GenericFilterFeatureIterator.wrap(collection.iterator(), Filter.INCLUDE);
        assertEquals(3, DataUtilities.calculateCount(ite));

        ite = GenericFilterFeatureIterator.wrap(collection.iterator(), Filter.EXCLUDE);
        assertEquals(0, DataUtilities.calculateCount(ite));

        ite = GenericFilterFeatureIterator.wrap(collection.iterator(), FF.equals(FF.literal("aaa"), FF.property("att_string")));
        assertEquals(1, DataUtilities.calculateCount(ite));
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
        assertEquals(3, DataUtilities.calculateCount(ite));

        ite = GenericMaxFeatureIterator.wrap(collection.iterator(), 2);
        assertEquals(2, DataUtilities.calculateCount(ite));

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
        assertEquals(3, DataUtilities.calculateCount(ite));

        ite = GenericModifyFeatureIterator.wrap(collection.iterator(), filter, values);
        while(ite.hasNext()){
            assertTrue(ite.next().getProperty("att_string").getValue().equals("toto"));
        }


        filter = FF.equals(FF.literal("aaa"), FF.property("att_string"));
        ite = GenericModifyFeatureIterator.wrap(collection.iterator(), filter, values);
        assertEquals(3, DataUtilities.calculateCount(ite));

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
        FeatureReader reader = collection.getSession().getDataStore().getFeatureReader(query);

        FeatureReader retyped = GenericReprojectFeatureIterator.wrap(reader, CRS.decode("EPSG:4326"), new Hints());
        assertEquals(reprojectedType,retyped.getFeatureType());

        Feature f;

        f = retyped.next();
        assertEquals(3, f.getProperties().size());
        assertEquals(GF.createPoint(new Coordinate(0, 3)).toString(), f.getProperty("att_geom").getValue().toString());

        f = retyped.next();
        assertEquals(3, f.getProperties().size());
        assertEquals(GF.createPoint(new Coordinate(0, 1)).toString(), f.getProperty("att_geom").getValue().toString());

        f = retyped.next();
        assertEquals(3, f.getProperties().size());
        assertEquals(GF.createPoint(new Coordinate(0, 2)).toString(), f.getProperty("att_geom").getValue().toString());


        //check has next do not iterate
        reader = collection.getSession().getDataStore().getFeatureReader(query);
        retyped = GenericReprojectFeatureIterator.wrap(reader, CRS.decode("EPSG:4326"), new Hints());
        testIterationOnNext(retyped, 3);

        //check sub iterator is properly closed
        reader = collection.getSession().getDataStore().getFeatureReader(query);
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
        builder.add("att_geom", LineString.class, DefaultGeographicCRS.WGS84);
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

        final FeatureCollection collection = DataUtilities.collection("id", type);
        SimpleFeature sf = SimpleFeatureBuilder.template(type, "");
        sf.setAttribute("att_geom", geom);
        collection.add(sf);

        //get the reader -------------------------------------------------------
        QueryBuilder qb = new QueryBuilder();
        qb.setTypeName(originalType.getName());
        Query query = qb.buildQuery();
        FeatureReader reader = collection.getSession().getDataStore().getFeatureReader(query);
        
        //create the decimate reader -------------------------------------------
        GeometryTransformer decim = new GeometryScaleTransformer(10, 10);
        FeatureReader retyped = GenericTransformFeatureIterator.wrap(reader,decim, new Hints());
        
        assertTrue(retyped.hasNext());

        final LineString decimated = (LineString) retyped.next().getDefaultGeometryProperty().getValue();

        assertFalse(retyped.hasNext());
        retyped.close();

        assertEquals(4, decimated.getNumPoints());
        assertEquals(geom.getGeometryN(0).getCoordinate(), decimated.getGeometryN(0).getCoordinate());
        assertEquals(geom.getGeometryN(1).getCoordinate(), decimated.getGeometryN(1).getCoordinate());
        assertEquals(geom.getGeometryN(2).getCoordinate(), decimated.getGeometryN(2).getCoordinate());
        assertEquals(geom.getGeometryN(4).getCoordinate(), decimated.getGeometryN(3).getCoordinate());

    }

    @Test
    public void testRetypeFeatureIterator() throws DataStoreException{
        QueryBuilder qb = new QueryBuilder();
        qb.setTypeName(originalType.getName());
        Query query = qb.buildQuery();
        FeatureReader reader = collection.getSession().getDataStore().getFeatureReader(query);

        FeatureReader retyped = GenericRetypeFeatureIterator.wrap(reader,reducedType,null);
        assertEquals(reducedType,retyped.getFeatureType());

        Feature f;

        f = retyped.next();
        assertEquals(1, f.getProperties().size());
        assertEquals(3d, (Double)f.getProperty("att_double").getValue(), DELTA);

        f = retyped.next();
        assertEquals(1, f.getProperties().size());
        assertEquals(1d, (Double)f.getProperty("att_double").getValue(), DELTA);

        f = retyped.next();
        assertEquals(1, f.getProperties().size());
        assertEquals(2d, (Double)f.getProperty("att_double").getValue(), DELTA);


        //check has next do not iterate
        reader = collection.getSession().getDataStore().getFeatureReader(query);
        retyped = GenericRetypeFeatureIterator.wrap(reader,reducedType,null);
        testIterationOnNext(retyped, 3);

        //check sub iterator is properly closed
        reader = collection.getSession().getDataStore().getFeatureReader(query);
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
        assertEquals(3, DataUtilities.calculateCount(ite));


        ite = GenericSortByFeatureIterator.wrap(collection.iterator(), sorts);
        assertEquals(ite.next().getIdentifier().getID(),id3);
        assertEquals(ite.next().getIdentifier().getID(),id1);
        assertEquals(ite.next().getIdentifier().getID(),id2);

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
        assertEquals(2, DataUtilities.calculateCount(ite));

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
        assertEquals(2, DataUtilities.calculateCount(ite));

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
        assertEquals(2, DataUtilities.calculateCount(ite));

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
        assertEquals(3, DataUtilities.calculateCount(ite));

        ite = GenericStartIndexFeatureIterator.wrap(collection.iterator(), 1);
        assertEquals(2, DataUtilities.calculateCount(ite));

        ite = GenericStartIndexFeatureIterator.wrap(collection.iterator(), 2);
        assertEquals(ite.next().getIdentifier().getID(),id3);
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
