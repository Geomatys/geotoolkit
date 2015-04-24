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
import org.apache.sis.referencing.CommonCRS;
import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;
import org.apache.sis.storage.DataStoreException;
import org.geotoolkit.data.FeatureCollection;
import org.geotoolkit.data.FeatureIterator;
import org.geotoolkit.data.FeatureReader;
import org.geotoolkit.data.FeatureStoreUtilities;
import org.geotoolkit.data.FeatureWriter;
import org.geotoolkit.data.memory.GenericCachedFeatureIterator;
import org.geotoolkit.data.memory.GenericEmptyFeatureIterator;
import org.geotoolkit.data.memory.GenericFilterFeatureIterator;
import org.geotoolkit.data.memory.GenericMaxFeatureIterator;
import org.geotoolkit.data.memory.GenericModifyFeatureIterator;
import org.geotoolkit.data.memory.GenericSortByFeatureIterator;
import org.geotoolkit.data.memory.GenericStartIndexFeatureIterator;
import org.geotoolkit.data.memory.GenericWrapFeatureIterator;
import org.geotoolkit.data.query.Query;
import org.geotoolkit.data.query.QueryBuilder;
import org.geotoolkit.factory.FactoryFinder;
import org.geotoolkit.factory.Hints;
import org.geotoolkit.factory.HintsPending;
import org.geotoolkit.util.NamesExt;
import org.geotoolkit.geometry.jts.JTS;
import org.geotoolkit.geometry.jts.transform.GeometryScaleTransformer;
import org.geotoolkit.geometry.jts.transform.GeometryTransformer;
import org.junit.Ignore;
import org.junit.Test;
import org.opengis.filter.Filter;
import org.opengis.filter.FilterFactory;
import org.opengis.filter.sort.SortBy;
import org.opengis.filter.sort.SortOrder;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.util.FactoryException;
import org.opengis.util.GenericName;

import static junit.framework.Assert.*;
import org.apache.sis.feature.FeatureTypeExt;
import org.apache.sis.feature.ReprojectFeatureType;
import org.apache.sis.feature.TransformFeatureType;
import org.apache.sis.feature.ViewFeatureType;
import org.apache.sis.feature.builder.AttributeRole;
import org.apache.sis.feature.builder.FeatureTypeBuilder;
import org.apache.sis.referencing.CRS;
import org.opengis.feature.Feature;
import org.opengis.feature.FeatureType;
import org.opengis.referencing.NoSuchAuthorityCodeException;
import org.apache.sis.internal.feature.AttributeConvention;
import org.geotoolkit.data.memory.GenericDecoratedFeatureIterator;

/**
 * Tests of the different iterators.
 *
 * @author Johann Sorel (Geomatys)
 */
public class GenericIteratorTest extends org.geotoolkit.test.TestBase {

    private static final double DELTA = 0.000001d;
    private static final FilterFactory FF = FactoryFinder.getFilterFactory(null);
    private static final GeometryFactory GF = new GeometryFactory();
    private static final Integer COMPLEX_ID_1 = 11;
    private static final Integer COMPLEX_ID_2 = 12;
    private static final GenericName NAME = NamesExt.create("http://test.com", "TestSchema");

    private final FeatureCollection collection;
    private final FeatureCollection collectionComplex;
    private final GenericName name;
    private final FeatureType originalType;
    private final FeatureType reducedType;
    private final FeatureType reprojectedType;
    private final Integer id1;
    private final Integer id2;
    private final Integer id3;

    private final Feature sf1;
    private final Feature sf2;
    private final Feature sf3;

    public GenericIteratorTest() throws NoSuchAuthorityCodeException, FactoryException{
        FeatureTypeBuilder builder = new FeatureTypeBuilder();
        name = NamesExt.create("http://test.com", "TestSchema");
        builder = new FeatureTypeBuilder();
        builder.setName(name);
        builder.addAttribute(Integer.class).setName(AttributeConvention.IDENTIFIER_PROPERTY);
        builder.addAttribute(Point.class).setName("att_geom").setCRS(CommonCRS.WGS84.normalizedGeographic());
        builder.addAttribute(String.class).setName("att_string");
        builder.addAttribute(Double.class).setName("att_double");
        originalType = builder.build();

        //build a reduced type for retype iterator
        builder = new FeatureTypeBuilder();
        builder.setName(name);
        builder.addAttribute(Integer.class).setName(AttributeConvention.IDENTIFIER_PROPERTY);
        builder.addAttribute(Double.class).setName("att_double");
        reducedType = builder.build();

        //build a reprojected type for reproject iterator
        builder = new FeatureTypeBuilder();
        builder.setName(name);
        builder.addAttribute(Integer.class).setName(AttributeConvention.IDENTIFIER_PROPERTY);
        builder.addAttribute(Point.class).setName("att_geom").setCRS(CRS.forCode("EPSG:4326"));
        builder.addAttribute(String.class).setName("att_string");
        builder.addAttribute(Double.class).setName("att_double");
        reprojectedType = builder.build();


        collection = FeatureStoreUtilities.collection("id", originalType);

        id1 = 0;
        sf1 = originalType.newInstance();
        sf1.setPropertyValue(AttributeConvention.IDENTIFIER_PROPERTY.toString(), id1);
        sf1.setPropertyValue("att_geom", GF.createPoint(new Coordinate(3, 0)));
        sf1.setPropertyValue("att_string", "bbb");
        sf1.setPropertyValue("att_double", 3d);
        collection.add(sf1);

        id2 = 1;
        sf2 = originalType.newInstance();
        sf2.setPropertyValue(AttributeConvention.IDENTIFIER_PROPERTY.toString(), id2);
        sf2.setPropertyValue("att_geom", GF.createPoint(new Coordinate(1, 0)));
        sf2.setPropertyValue("att_string", "ccc");
        sf2.setPropertyValue("att_double", 1d);
        collection.add(sf2);

        id3 = 2;
        sf3 = originalType.newInstance();
        sf3.setPropertyValue(AttributeConvention.IDENTIFIER_PROPERTY.toString(), id3);
        sf3.setPropertyValue("att_geom", GF.createPoint(new Coordinate(2, 0)));
        sf3.setPropertyValue("att_string", "aaa");
        sf3.setPropertyValue("att_double", 2d);
        collection.add(sf3);

        builder = new FeatureTypeBuilder();
        builder.setName(name);
        builder.addAttribute(Integer.class).setName(AttributeConvention.IDENTIFIER_PROPERTY);
        builder.addAttribute(String.class).setName("att_string").setMinimumOccurs(0).setMaximumOccurs(1);
        builder.addAttribute(Double.class).setName("att_double").setMinimumOccurs(0).setMaximumOccurs(1);
        FeatureType ct = builder.build();

        collectionComplex = FeatureStoreUtilities.collection("cid", ct);

        final Feature f1 = ct.newInstance();
        f1.setPropertyValue(AttributeConvention.IDENTIFIER_PROPERTY.toString(), COMPLEX_ID_1);
        f1.setPropertyValue("att_string","aaaa");
        f1.setPropertyValue("att_double",12.0);
        collectionComplex.add(f1);

        final Feature f2 = ct.newInstance();
        f2.setPropertyValue(AttributeConvention.IDENTIFIER_PROPERTY.toString(), COMPLEX_ID_2);
        f2.setPropertyValue("att_string","bbbb");
        f2.setPropertyValue("att_double",7.0);
        collectionComplex.add(f2);

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
        FeatureCollection collection = buildSimpleFeatureCollection();
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
        FeatureCollection collection = buildSimpleFeatureCollection();
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
    @Ignore("See #GEOTK-489")
    public void testCacheIterator(){
        FeatureCollection collection = buildSimpleFeatureCollection();
        FeatureIterator ite = GenericCachedFeatureIterator.wrap(collection.iterator(), 1);
        assertEquals(3, FeatureStoreUtilities.calculateCount(ite));

        ite = GenericCachedFeatureIterator.wrap(collection.iterator(), 1);

        int mask = 0;
        Feature f;
        while(ite.hasNext()){

            f = ite.next();
            final Object id = f.getPropertyValue(AttributeConvention.IDENTIFIER_PROPERTY.toString());

            if(id1.equals(id)){
                mask |= 1<<0;
            }else if(id2.equals(id)){
                mask |= 1<<1;
            }else if(id3.equals(id)){
                mask |= 1<<2;
            }
        }
        ite.close();
        if(mask!=7){
            fail("missing features in iterations");
        }

    }

    @Test
    public void testFilterIterator(){
        FeatureCollection collection = buildSimpleFeatureCollection();
        FeatureIterator ite = GenericFilterFeatureIterator.wrap(collection.iterator(), Filter.INCLUDE);
        assertEquals(3, FeatureStoreUtilities.calculateCount(ite));

        ite = GenericFilterFeatureIterator.wrap(collection.iterator(), Filter.EXCLUDE);
        assertEquals(0, FeatureStoreUtilities.calculateCount(ite));

        ite = GenericFilterFeatureIterator.wrap(collection.iterator(), FF.equals(FF.literal("aaa"), FF.property("att_string")));
        assertEquals(1, FeatureStoreUtilities.calculateCount(ite));
        ite = GenericFilterFeatureIterator.wrap(collection.iterator(), FF.equals(FF.literal("aaa"), FF.property("att_string")));

        assertEquals(id3, ite.next().getPropertyValue(AttributeConvention.IDENTIFIER_PROPERTY.toString()));
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
        FeatureCollection collection = buildSimpleFeatureCollection();
        FeatureIterator ite = GenericMaxFeatureIterator.wrap(collection.iterator(), 10);
        assertEquals(3, FeatureStoreUtilities.calculateCount(ite));

        ite = GenericMaxFeatureIterator.wrap(collection.iterator(), 2);
        assertEquals(2, FeatureStoreUtilities.calculateCount(ite));

        ite = GenericMaxFeatureIterator.wrap(collection.iterator(), 1);
        assertEquals(id1, ite.next().getPropertyValue(AttributeConvention.IDENTIFIER_PROPERTY.toString()));
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
        FeatureCollection collection = buildSimpleFeatureCollection();
        FeatureType originalType = collection.getFeatureType();
        Filter filter = Filter.INCLUDE;
        Map<String,Object> values = new HashMap<>();
        values.put("att_string", "toto");

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
            if (f.getPropertyValue(AttributeConvention.IDENTIFIER_PROPERTY.toString()).equals(id3)) {
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
    public void testReprojectFeatureIterator() throws DataStoreException, FactoryException{
        QueryBuilder qb = new QueryBuilder();
        qb.setTypeName(collection.getFeatureType().getName());
        Query query = qb.buildQuery();
        FeatureReader reader = collection.getSession().getFeatureStore().getFeatureReader(query);

        final CoordinateReferenceSystem targetCRS = CommonCRS.WGS84.geographic();

        FeatureReader retyped = GenericDecoratedFeatureIterator.wrap(reader, new ReprojectFeatureType(reader.getFeatureType(), targetCRS), new Hints());

        int mask = 0;
        Feature f;
        while(retyped.hasNext()){
            f = retyped.next();
            final Object id = f.getPropertyValue(AttributeConvention.IDENTIFIER_PROPERTY.toString());

            assertEquals(4, f.getType().getProperties(true).size());
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
        retyped = GenericDecoratedFeatureIterator.wrap(reader, new ReprojectFeatureType(reader.getFeatureType(), CommonCRS.WGS84.geographic()), new Hints());
        testIterationOnNext(retyped, 3);

        //check sub iterator is properly closed
        reader = collection.getSession().getFeatureStore().getFeatureReader(query);
        CheckCloseFeatureIterator checkIte = new CheckCloseFeatureIterator(reader);
        assertFalse(checkIte.isClosed());
        retyped = GenericDecoratedFeatureIterator.wrap(checkIte, new ReprojectFeatureType(checkIte.getFeatureType(), CommonCRS.WGS84.geographic()), new Hints());
        while(retyped.hasNext()) retyped.next();
        retyped.close();
        assertTrue(checkIte.isClosed());
    }

    @Test
    public void testTransformFeatureIterator() throws DataStoreException{
        FeatureType originalType = buildOriginalFT();
        final FeatureTypeBuilder builder = new FeatureTypeBuilder();
        final GenericName name = NamesExt.create("http://test.com", "TestSchema");
        builder.setName(name);
        builder.addAttribute(String.class).setName(AttributeConvention.IDENTIFIER_PROPERTY);
        builder.addAttribute(LineString.class).setName("att_geom").setCRS(CommonCRS.WGS84.normalizedGeographic()).addRole(AttributeRole.DEFAULT_GEOMETRY);
        final FeatureType type = builder.build();

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
        Feature sf = type.newInstance();
        sf.setPropertyValue("att_geom", geom);
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
        final TransformFeatureType ttype = new TransformFeatureType(reader.getFeatureType(), decim);
        FeatureReader retyped = GenericDecoratedFeatureIterator.wrap(reader, ttype, hints);

        assertTrue(retyped.hasNext());

        LineString decimated = (LineString) retyped.next().getPropertyValue(AttributeConvention.GEOMETRY_PROPERTY.toString());

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

        LineString notDecimated = (LineString) reader.next().getPropertyValue(AttributeConvention.GEOMETRY_PROPERTY.toString());
        assertEquals(6, notDecimated.getNumPoints());
        assertFalse(reader.hasNext());
        reader.close();



        // same test but with reuse hint ---------------------------------------
        reader = collection.getSession().getFeatureStore().getFeatureReader(query);
        hints.put(HintsPending.FEATURE_DETACHED, Boolean.FALSE);

        decim = new GeometryScaleTransformer(10, 10);
        retyped = GenericDecoratedFeatureIterator.wrap(reader,new TransformFeatureType(reader.getFeatureType(), decim), hints);

        assertTrue(retyped.hasNext());

        decimated = (LineString) retyped.next().getPropertyValue(AttributeConvention.GEOMETRY_PROPERTY.toString());

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

        notDecimated = (LineString) reader.next().getPropertyValue(AttributeConvention.GEOMETRY_PROPERTY.toString());
        assertEquals(6, notDecimated.getNumPoints());
        assertFalse(reader.hasNext());
        reader.close();
    }

    @Test
    public void testRetypeFeatureIterator() throws DataStoreException{

        final FeatureCollection collection = buildSimpleFeatureCollection();
        final FeatureType reducedType = FeatureTypeExt.createSubType(collection.getFeatureType(), AttributeConvention.IDENTIFIER_PROPERTY.toString(),"att_double");
        final QueryBuilder qb = new QueryBuilder();
        qb.setTypeName(collection.getFeatureType().getName());
        final Query query = qb.buildQuery();
        FeatureReader reader = collection.getSession().getFeatureStore().getFeatureReader(query);

        FeatureReader retyped = GenericDecoratedFeatureIterator.wrap(reader, (ViewFeatureType) reducedType,null);
        assertEquals(reducedType,retyped.getFeatureType());

        int mask = 0;
        Feature f;
        while(retyped.hasNext()){
            f = retyped.next();
            final Object id = f.getPropertyValue(AttributeConvention.IDENTIFIER_PROPERTY.toString());

            assertEquals(2, f.getType().getProperties(true).size());

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

        assertEquals("missing features in iterations", 7, mask);

        //check has next do not iterate
        reader = collection.getSession().getFeatureStore().getFeatureReader(query);
        retyped = GenericDecoratedFeatureIterator.wrap(reader, (ViewFeatureType) reducedType,null);
        testIterationOnNext(retyped, 3);

        //check sub iterator is properly closed
        reader = collection.getSession().getFeatureStore().getFeatureReader(query);
        CheckCloseFeatureIterator checkIte = new CheckCloseFeatureIterator(reader);
        assertFalse(checkIte.isClosed());
        retyped = GenericDecoratedFeatureIterator.wrap(checkIte, (ViewFeatureType) reducedType,null);
        while(retyped.hasNext()) retyped.next();
        retyped.close();
        assertTrue(checkIte.isClosed());

    }

    @Test
    public void testSortByIterator(){
        SortBy[] sorts = new SortBy[]{
            FF.sort("att_string", SortOrder.ASCENDING)
        };

        FeatureCollection collection = buildSimpleFeatureCollection();
        FeatureIterator ite = GenericSortByFeatureIterator.wrap(collection.iterator(), sorts);
        assertEquals(3, FeatureStoreUtilities.calculateCount(ite));


        ite = GenericSortByFeatureIterator.wrap(collection.iterator(), sorts);
        assertEquals(id3, ite.next().getPropertyValue(AttributeConvention.IDENTIFIER_PROPERTY.toString()));
        assertEquals(id1, ite.next().getPropertyValue(AttributeConvention.IDENTIFIER_PROPERTY.toString()));
        assertEquals(id2, ite.next().getPropertyValue(AttributeConvention.IDENTIFIER_PROPERTY.toString()));

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

        FeatureCollection collectionComplex = buildComplexFeatureCollection();
        FeatureCollection collection = buildSimpleFeatureCollection();
        //test string sort -----------------------------------------------------
        SortBy[] sorts = new SortBy[]{
            FF.sort("att_string", SortOrder.DESCENDING)
        };

        FeatureIterator ite = GenericSortByFeatureIterator.wrap(collectionComplex.iterator(), sorts);
        assertEquals(2, FeatureStoreUtilities.calculateCount(ite));

        ite = GenericSortByFeatureIterator.wrap(collectionComplex.iterator(), sorts);
        assertEquals(COMPLEX_ID_2, ite.next().getPropertyValue(AttributeConvention.IDENTIFIER_PROPERTY.toString()));
        assertEquals(COMPLEX_ID_1, ite.next().getPropertyValue(AttributeConvention.IDENTIFIER_PROPERTY.toString()));

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
        assertEquals(COMPLEX_ID_1, ite.next().getPropertyValue(AttributeConvention.IDENTIFIER_PROPERTY.toString()));
        assertEquals(COMPLEX_ID_2, ite.next().getPropertyValue(AttributeConvention.IDENTIFIER_PROPERTY.toString()));

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
        assertEquals(COMPLEX_ID_2, ite.next().getPropertyValue(AttributeConvention.IDENTIFIER_PROPERTY.toString()));
        assertEquals(COMPLEX_ID_1, ite.next().getPropertyValue(AttributeConvention.IDENTIFIER_PROPERTY.toString()));

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
        FeatureCollection collection = buildSimpleFeatureCollection();
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

        FeatureCollection collection = buildSimpleFeatureCollection();
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
        FeatureCollection collection = buildSimpleFeatureCollection();
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

    private FeatureCollection buildComplexFeatureCollection() {
        FeatureTypeBuilder builder = new FeatureTypeBuilder();
        builder.setName(NAME);
        builder.addAttribute(Integer.class).setName("id").addRole(AttributeRole.IDENTIFIER_COMPONENT);
        builder.addAttribute(String.class).setName("att_string");
        builder.addAttribute(Double.class).setName("att_double");
        FeatureType ct = builder.build();

        FeatureCollection collectionComplex = FeatureStoreUtilities.collection("cid", ct);

        Feature f1 = ct.newInstance();
        f1.setPropertyValue("id", COMPLEX_ID_1);
        f1.setPropertyValue("att_string", "aaaa");
        f1.setPropertyValue("att_double", 12.0);
        collectionComplex.add(f1);

        Feature f2 = ct.newInstance();
        f2.setPropertyValue("id", COMPLEX_ID_2);
        f2.setPropertyValue("att_string", "bbbb");
        f2.setPropertyValue("att_double", 7.0);
        collectionComplex.add(f2);

        return collectionComplex;
    }

    private FeatureCollection buildSimpleFeatureCollection() {
        FeatureType originalType = buildOriginalFT();
        FeatureCollection collection = FeatureStoreUtilities.collection("id", originalType);

        Feature sf1 = originalType.newInstance();
        sf1.setPropertyValue("id", 0);
        sf1.setPropertyValue("att_geom", GF.createPoint(new Coordinate(3, 0)));
        sf1.setPropertyValue("att_string", "bbb");
        sf1.setPropertyValue("att_double", 3d);
        collection.add(sf1);

        Feature sf2 = originalType.newInstance();
        sf2.setPropertyValue("id", 1);
        sf2.setPropertyValue("att_geom", GF.createPoint(new Coordinate(1, 0)));
        sf2.setPropertyValue("att_string", "ccc");
        sf2.setPropertyValue("att_double", 1d);
        collection.add(sf2);

        Feature sf3 = originalType.newInstance();
        sf3.setPropertyValue("id", 2);
        sf3.setPropertyValue("att_geom", GF.createPoint(new Coordinate(2, 0)));
        sf3.setPropertyValue("att_string", "aaa");
        sf3.setPropertyValue("att_double", 2d);
        collection.add(sf3);
        return collection;
    }

    private FeatureType buildOriginalFT() {
        final FeatureTypeBuilder builder = new FeatureTypeBuilder();
        builder.setName(NAME);
        builder.addAttribute(Integer.class).setName("id").addRole(AttributeRole.IDENTIFIER_COMPONENT);
        builder.addAttribute(Point.class).setName("att_geom").setCRS(CommonCRS.WGS84.normalizedGeographic()).addRole(AttributeRole.DEFAULT_GEOMETRY);
        builder.addAttribute(String.class).setName("att_string");
        builder.addAttribute(Double.class).setName("att_double");
        return builder.build();
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
}
