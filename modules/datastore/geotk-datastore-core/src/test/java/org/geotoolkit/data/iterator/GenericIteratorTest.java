/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2009, Open Source Geospatial Foundation (OSGeo)
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

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;
import junit.framework.TestCase;
import org.geotoolkit.data.DataUtilities;
import org.geotoolkit.data.DefaultFeatureCollection;
import org.geotoolkit.data.FeatureCollection;
import org.geotoolkit.data.FeatureIterator;
import org.geotoolkit.data.FeatureReader;
import org.geotoolkit.data.FeatureWriter;
import org.geotoolkit.data.memory.GenericEmptyFeatureIterator;
import org.geotoolkit.data.memory.GenericFilterFeatureIterator;
import org.geotoolkit.data.memory.GenericMaxFeatureIterator;
import org.geotoolkit.data.memory.GenericModifyFeatureIterator;
import org.geotoolkit.data.memory.GenericRetypeFeatureIterator;
import org.geotoolkit.data.memory.GenericSortByFeatureIterator;
import org.geotoolkit.data.memory.GenericStartIndexFeatureIterator;
import org.geotoolkit.factory.FactoryFinder;
import org.geotoolkit.feature.DefaultName;
import org.geotoolkit.feature.simple.SimpleFeatureBuilder;
import org.geotoolkit.feature.simple.SimpleFeatureTypeBuilder;
import org.junit.Test;
import org.opengis.feature.Feature;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.feature.type.Name;
import org.opengis.feature.type.PropertyDescriptor;
import org.opengis.filter.Filter;
import org.opengis.filter.FilterFactory;
import org.opengis.filter.sort.SortBy;
import org.opengis.filter.sort.SortOrder;

/**
 * Tests of the different iterators.
 *
 * @author Johann Sorel (Geomatys)
 */
public class GenericIteratorTest extends TestCase{

    private static final FilterFactory FF = FactoryFinder.getFilterFactory(null);

    private final FeatureCollection<SimpleFeature> collection;
    private final Name name;
    private final SimpleFeatureType type;

    public GenericIteratorTest(){
        final SimpleFeatureTypeBuilder builder = new SimpleFeatureTypeBuilder();
        name = new DefaultName("http://test.com", "TestSchema1");
        builder.reset();
        builder.setName(name);
        builder.add("att_string", String.class);
        builder.add("att_double", Double.class);
        type = builder.buildFeatureType();

        collection = new DefaultFeatureCollection<SimpleFeature>("id", type, SimpleFeature.class);

        SimpleFeature sf = SimpleFeatureBuilder.template(type, "id1");
        sf.setAttribute("att_string", "bbb");
        sf.setAttribute("att_double", 3d);
        collection.add(sf);

        sf = SimpleFeatureBuilder.template(type, "id2");
        sf.setAttribute("att_string", "ccc");
        sf.setAttribute("att_double", 1d);
        collection.add(sf);

        sf = SimpleFeatureBuilder.template(type, "id3");
        sf.setAttribute("att_string", "aaa");
        sf.setAttribute("att_double", 2d);
        collection.add(sf);
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

        assertEquals(ite.next().getIdentifier().getID(),"id3");
        try{
            ite.next();
            fail("Should have raise a no such element exception.");
        }catch(NoSuchElementException ex){
            //ok
        }

        ite = GenericFilterFeatureIterator.wrap(collection.iterator(), Filter.INCLUDE);
        testIterationOnNext(ite, 3);
    }

    @Test
    public void testMaxIterator(){
        FeatureIterator ite = GenericMaxFeatureIterator.wrap(collection.iterator(), 10);
        assertEquals(3, DataUtilities.calculateCount(ite));

        ite = GenericMaxFeatureIterator.wrap(collection.iterator(), 2);
        assertEquals(2, DataUtilities.calculateCount(ite));

        ite = GenericMaxFeatureIterator.wrap(collection.iterator(), 1);
        assertEquals(ite.next().getIdentifier().getID(),"id1");
        try{
            ite.next();
            fail("Should have raise a no such element exception.");
        }catch(NoSuchElementException ex){
            //ok
        }

        ite = GenericMaxFeatureIterator.wrap(collection.iterator(), 10);
        testIterationOnNext(ite, 3);
    }

    @Test
    public void testModifyIterator(){
        Filter filter = Filter.INCLUDE;
        Map<PropertyDescriptor,Object> values = new HashMap<PropertyDescriptor, Object>();
        values.put(type.getDescriptor("att_string"), "toto");

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
            if(f.getIdentifier().getID().equals("id3")){
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

        ite = GenericModifyFeatureIterator.wrap(collection.iterator(), filter, values);
        testIterationOnNext(ite, 3);
    }

    @Test
    public void testSortByIterator(){
        SortBy[] sorts = new SortBy[]{
            FF.sort("att_string", SortOrder.ASCENDING)
        };

        FeatureIterator ite = GenericSortByFeatureIterator.wrap(collection.iterator(), sorts);
        assertEquals(3, DataUtilities.calculateCount(ite));


        ite = GenericSortByFeatureIterator.wrap(collection.iterator(), sorts);
        assertEquals(ite.next().getIdentifier().getID(),"id3");
        assertEquals(ite.next().getIdentifier().getID(),"id1");
        assertEquals(ite.next().getIdentifier().getID(),"id2");

        try{
            ite.next();
            fail("Should have raise a no such element exception.");
        }catch(NoSuchElementException ex){
            //ok
        }

        ite = GenericSortByFeatureIterator.wrap(collection.iterator(), sorts);
        testIterationOnNext(ite, 3);
    }

    @Test
    public void testStartIndexIterator(){
        FeatureIterator ite = GenericStartIndexFeatureIterator.wrap(collection.iterator(), 0);
        assertEquals(3, DataUtilities.calculateCount(ite));

        ite = GenericStartIndexFeatureIterator.wrap(collection.iterator(), 1);
        assertEquals(2, DataUtilities.calculateCount(ite));

        ite = GenericStartIndexFeatureIterator.wrap(collection.iterator(), 2);
        assertEquals(ite.next().getIdentifier().getID(),"id3");
        try{
            ite.next();
            fail("Should have raise a no such element exception.");
        }catch(NoSuchElementException ex){
            //ok
        }

        ite = GenericStartIndexFeatureIterator.wrap(collection.iterator(), 1);
        testIterationOnNext(ite, 2);
    }

}
