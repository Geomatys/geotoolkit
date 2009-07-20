/*
 *    GeoTools - The Open Source Java GIS Toolkit
 *    http://geotools.org
 * 
 *    (C) 2002-2008, Open Source Geospatial Foundation (OSGeo)
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
package org.geotoolkit.data;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.geotoolkit.feature.SchemaException;
import org.geotoolkit.feature.simple.SimpleFeatureBuilder;
import org.geotoolkit.filter.IllegalFilterException;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.geotoolkit.referencing.CRS;
import org.geotoolkit.referencing.crs.DefaultGeographicCRS;
import org.opengis.filter.And;
import org.opengis.filter.BinaryLogicOperator;
import org.opengis.filter.Filter;
import org.opengis.filter.FilterFactory;
import org.opengis.filter.Id;
import org.opengis.filter.PropertyIsBetween;
import org.opengis.filter.PropertyIsEqualTo;
import org.opengis.filter.PropertyIsLike;
import org.opengis.filter.PropertyIsNull;
import org.opengis.filter.expression.Expression;
import org.opengis.filter.expression.Function;
import org.opengis.filter.expression.PropertyName;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

import com.vividsolutions.jts.geom.Geometry;
import org.geotoolkit.data.DataUtilities;
import org.geotoolkit.data.DefaultQuery;
import org.geotoolkit.data.FeatureReader;
import org.geotoolkit.data.FeatureSource;
import org.geotoolkit.data.query.Query;
import org.geotoolkit.factory.FactoryFinder;
import org.geotoolkit.feature.FeatureTypeUtilities;
import org.geotoolkit.feature.collection.FeatureCollection;
import org.opengis.feature.IllegalAttributeException;


/**
 * Tests cases for DataUtilities.
 *
 * @author Jody Garnett, Refractions Research
 * @source $URL$
 */
public class DataUtilitiesTest extends DataTestCase {
    /**
     * Constructor for DataUtilitiesTest.
     *
     * @param arg0
     */
    public DataUtilitiesTest(String arg0) {
        super(arg0);
    }

    public void testUrlToFile() throws Exception {
        handleFile(System.getProperty("user.home"));
        handleFile(System.getProperty("user.dir"));

        String os = System.getProperty("os.name");

        String c = "C:\\";
        String cOne = "C:\\one";
        String cOneTwo = "C:\\one\\two";
        String cOneTwoThreeSpace = "C:\\one\\two\\and three";
        String d = "D:\\";
        String slashOne = "/one";
        String one = "one";
        String slashoneTwoThree = "/one/two/and three";

        if (os.toUpperCase().contains("WINDOWS")) {
            handleFile(c);
            handleFile(cOne);
            handleFile(cOneTwo);
            handleFile(cOneTwoThreeSpace);
            handleFile(d);
            //TODO
//            if (TestData.isExtensiveTest())
//                handleFile("\\\\host\\share\\file");
        } else {
            handleFile(slashOne);

            handleFile(one);
            handleFile(slashoneTwoThree);
        }
        
        String prefix = "file://";
        assertURL(one, prefix+one);
        assertURL(slashOne, prefix+slashOne);
        assertURL(replaceSlashes(c), prefix+replaceSlashes(c));
        assertURL(replaceSlashes(cOne), prefix+replaceSlashes(cOne));
        assertURL(replaceSlashes(cOneTwo), prefix+replaceSlashes(cOneTwo));
        assertURL(replaceSlashes(cOneTwoThreeSpace), prefix+replaceSlashes(cOneTwoThreeSpace));
        
        
    }
    private String replaceSlashes(String string) {
        return string.replaceAll("\\\\", "/");
    }

    private void assertURL(String expectedFilePath, String urlString) throws MalformedURLException {
        URL url = new URL(urlString);
        
        File file = DataUtilities.urlToFile(url);

        String os = System.getProperty("os.name");
        if (os.toUpperCase().contains("WINDOWS")) {
            assertEquals( expectedFilePath.replaceAll("/", "\\\\"), file.getPath());
        }else {
            if( expectedFilePath.endsWith("/") ){
                expectedFilePath = expectedFilePath.substring(0,expectedFilePath.length()-1);
            }
            assertEquals( expectedFilePath, file.getPath());
        }
        
    }

    public void handleFile( String path ) throws Exception {
        File file = new File( path );
        URI uri = file.toURI();
        URL url = file.toURL();
        URL url2 = file.toURI().toURL();
        
        assertEquals( "jdk contract", file.getAbsoluteFile(), new File( uri ));

        File toFile = DataUtilities.urlToFile( url );        
        assertEquals( path+":url", file.getAbsoluteFile(), toFile);

        File toFile2 = DataUtilities.urlToFile( url2 );        
        assertEquals( path+":url2", file.getAbsoluteFile(), toFile2 );
    }
    /**
     * Test for {@link DataUtilities#attributeNames(FeatureType)}
     */
    public void testAttributeNamesFeatureType() {
        String[] names;

        names = DataUtilities.attributeNames(roadType);
        assertEquals(3, names.length);
        assertEquals("id", names[0]);
        assertEquals("geom", names[1]);
        assertEquals("name", names[2]);

        names = DataUtilities.attributeNames(subRoadType);
        assertEquals(2, names.length);
        assertEquals("id", names[0]);
        assertEquals("geom", names[1]);
    }

    /*
     * Test for String[] attributeNames(Filter)
     */
    public void testAttributeNamesFilter() throws IllegalFilterException {
        FilterFactory factory = FactoryFinder.getFilterFactory(null);
        String[] names;

        Filter filter = null;

        // check null
        names = DataUtilities.attributeNames(filter,null);
        assertEquals(names.length, 0);

        Id fidFilter = factory.id(Collections.singleton(factory.featureId("fid")));

        // check fidFilter         
        names = DataUtilities.attributeNames(fidFilter,null);
        assertEquals(0, names.length);

        PropertyName id = factory.property("id");
        PropertyName name = factory.property("name");
        PropertyName geom = factory.property("geom");

        // check nullFilter
        PropertyIsNull nullFilter = factory.isNull(id);
        names = DataUtilities.attributeNames(nullFilter,null);
        assertEquals(1, names.length);
        assertEquals("id", names[0]);

        PropertyIsEqualTo equal = factory.equals(name, id);
        names = DataUtilities.attributeNames(equal,null);
        assertEquals(2, names.length);
        List list = Arrays.asList(names);
        assertTrue(list.contains("name"));
        assertTrue(list.contains("id"));
 
        Function fnCall = factory.function("max", new Expression[] { id, name });
        System.out.println(fnCall);

        PropertyIsLike fn = factory.like(fnCall, "does-not-matter");
        names = DataUtilities.attributeNames(fn,null);
        list = Arrays.asList(names);
        assertTrue(list.contains("name"));
        assertTrue(list.contains("id"));

        PropertyIsBetween between = factory.between(name, id, geom);
        names = DataUtilities.attributeNames(between,null);
        assertEquals(3, names.length);
        list = Arrays.asList(names);
        assertTrue(list.contains("name"));
        assertTrue(list.contains("id"));
        assertTrue(list.contains("geom"));
        
        // check logic filter
        PropertyIsNull geomNull = factory.isNull(geom);
        names = DataUtilities.attributeNames(factory.and(geomNull, equal),null);
        assertEquals(3, names.length);
        list = Arrays.asList(names);
        assertTrue(list.contains("name"));
        assertTrue(list.contains("id"));
        assertTrue(list.contains("geom"));
        
        // check not filter
        names = DataUtilities.attributeNames(factory.not(geomNull),null);
        assertEquals(1, names.length);
        assertEquals("geom", names[0]);
        
        //check with namespace qualified name
        PropertyIsEqualTo equalToWithPrefix = factory.equals(factory.property("gml:name"),id);
        names = DataUtilities.attributeNames(equalToWithPrefix,roadType);
        assertEquals(2, names.length);
        list = Arrays.asList(names);
        assertTrue(list.contains("name"));
        assertTrue(list.contains("id"));     
    }

    /**
     * Test for {@link DataUtilities#attributeNames(Filter, FeatureType)} 
     */
    public void testAttributeNamesFilterFeatureType() {
        FilterFactory factory = FactoryFinder.getFilterFactory(null);
        Filter filter = factory.equals(factory.property("id"), factory.add(
                factory.property("geom"), factory.property("gml:name")));

        String[] names;

        names = DataUtilities.attributeNames(filter, roadType);
        assertEquals(3, names.length);
        List namesList = Arrays.asList(names);
        assertTrue(namesList.contains("id"));
        assertTrue(namesList.contains("geom"));
        assertTrue(namesList.contains("name"));
    }

    /**
     * Test for {@link DataUtilities#attributeNames(Expression, FeatureType)} 
     */
    public void testAttributeExpressionFilterFeatureType() {
        FilterFactory factory = FactoryFinder.getFilterFactory(null);
        Expression expression = factory.add(factory.property("geom"), 
                factory.property("gml:name"));

        String[] names;

        names = DataUtilities.attributeNames(expression, roadType);
        assertEquals(2, names.length);
        List namesList = Arrays.asList(names);
        assertTrue(namesList.contains("geom"));
        assertTrue(namesList.contains("name"));
    }

    public void testCompare() throws SchemaException {
        assertEquals(0, DataUtilities.compare(null, null));
        assertEquals(-1, DataUtilities.compare(roadType, null));
        assertEquals(-1, DataUtilities.compare(null, roadType));
        assertEquals(-1, DataUtilities.compare(riverType, roadType));
        assertEquals(-1, DataUtilities.compare(roadType, riverType));
        assertEquals(0, DataUtilities.compare(roadType, roadType));
        assertEquals(1, DataUtilities.compare(subRoadType, roadType));

        // different order
        SimpleFeatureType road2 = FeatureTypeUtilities.createType("namespace.road",
                "geom:LineString,name:String,id:0");
        assertEquals(1, DataUtilities.compare(road2, roadType));

        // different namespace        
        SimpleFeatureType road3 = FeatureTypeUtilities.createType("test.road",
                "id:0,geom:LineString,name:String");
        assertEquals(0, DataUtilities.compare(road3, roadType));
    }

    public void testIsMatch() {
    }

    public void testReType() throws Exception {
        SimpleFeature rd1 = roadFeatures[0];
        assertEquals(rd1, rd1);

        SimpleFeature rdDuplicate = SimpleFeatureBuilder.copy(rd1);

        assertEquals(rd1, rdDuplicate);
        assertNotSame(rd1, rdDuplicate);

        SimpleFeature rd2 = DataUtilities.reType(roadType, rd1);

        assertEquals(rd1, rd2);
        assertNotSame(rd1, rd2);

        SimpleFeature rd3 = DataUtilities.reType(subRoadType, rd1);

        assertFalse(rd1.equals(rd3));
        assertEquals(2, rd3.getAttributeCount());
        assertEquals(rd1.getID(), rd3.getID());
        assertEquals(rd1.getAttribute("id"), rd3.getAttribute("id"));
        assertEquals((Geometry) rd1.getAttribute("geom"),
            (Geometry) rd3.getAttribute("geom"));
        assertNotNull(rd3.getDefaultGeometry());

        SimpleFeature rv1 = riverFeatures[0];
        assertEquals(rv1, rv1);

        SimpleFeature rvDuplicate = SimpleFeatureBuilder.copy(rv1);

        assertEquals(rv1, rvDuplicate);
        assertNotSame(rv1, rvDuplicate);

        SimpleFeature rv2 = DataUtilities.reType(riverType, rv1);

        assertEquals(rv1, rv2);
        assertNotSame(rv1, rv2);

        SimpleFeature rv3 = DataUtilities.reType(subRiverType, rv1);

        assertFalse(rv1.equals(rv3));
        assertEquals(2, rv3.getAttributeCount());
        assertEquals(rv1.getID(), rv3.getID());
        assertEquals(rv1.getAttribute("name"), rv3.getAttribute("name"));
        assertEquals(rv1.getAttribute("flow"), rv3.getAttribute("flow"));
        assertNull(rv3.getDefaultGeometry());
    }

    /*
     * Test for Feature template(FeatureType)
     */
    public void testTemplateFeatureType() throws IllegalAttributeException {
        SimpleFeature feature = DataUtilities.template(roadType);
        assertNotNull(feature);
        assertEquals(roadType.getAttributeCount(), feature.getAttributeCount());
    }

    /*
     * Test for Feature template(FeatureType, String)
     */
    public void testTemplateFeatureTypeString()
        throws IllegalAttributeException {
        SimpleFeature feature = DataUtilities.template(roadType, "Foo");
        assertNotNull(feature);
        assertEquals(roadType.getAttributeCount(), feature.getAttributeCount());
        assertEquals("Foo", feature.getID());
        assertNull(feature.getAttribute("name"));
        assertNull(feature.getAttribute("id"));
        assertNull(feature.getAttribute("geom"));
    }

    public void testDefaultValues() throws IllegalAttributeException {
        Object[] values = DataUtilities.defaultValues(roadType);
        assertNotNull(values);
        assertEquals(values.length, roadType.getAttributeCount());
    }

    public void testDefaultValue() throws IllegalAttributeException {
        assertNull(DataUtilities.defaultValue(roadType.getDescriptor("name")));
        assertNull(DataUtilities.defaultValue(roadType.getDescriptor("id")));
        assertNull(DataUtilities.defaultValue(roadType.getDescriptor("geom")));
    }

    

    public void testCollection() {
        FeatureCollection<SimpleFeatureType, SimpleFeature> collection = DataUtilities.collection( roadFeatures );
        assertEquals( roadFeatures.length,  collection.size() );                
    }

    public void testReaderFeatureArray() throws Exception {
         FeatureReader<SimpleFeatureType, SimpleFeature> reader = DataUtilities.reader( roadFeatures );
        assertEquals( roadFeatures.length,  count( reader ) );
    }
    public void testReaderCollection() throws Exception {
        FeatureCollection<SimpleFeatureType, SimpleFeature> collection = DataUtilities.collection( roadFeatures );
        assertEquals( roadFeatures.length,  collection.size() );
                
         FeatureReader<SimpleFeatureType, SimpleFeature> reader = DataUtilities.reader( collection );
        assertEquals( roadFeatures.length,  count( reader ) );
    }    
    public void testCreateSubType() throws Exception {
    	SimpleFeatureType before =
    		FeatureTypeUtilities.createType("cities","the_geom:Point:srid=4326,name:String");
    	SimpleFeatureType after = FeatureTypeUtilities.createSubType(before, new String[]{"the_geom"} );
    	assertEquals( 1, after.getAttributeCount() );
    	
    	before = FeatureTypeUtilities.createType("cities","the_geom:Point:srid=4326,name:String,population:Integer");
        URI here = new URI("http://localhost/");
        after = FeatureTypeUtilities.createSubType(before, new String[]{"the_geom"},DefaultGeographicCRS.WGS84, "now", here);
        assertEquals( here.toString(), after.getName().getNamespaceURI());
        assertEquals( "now", after.getName().getLocalPart());
        assertEquals( DefaultGeographicCRS.WGS84, after.getCoordinateReferenceSystem() );
        assertEquals( 1, after.getAttributeCount() );
        assertEquals( "the_geom", after.getDescriptor(0).getLocalName() );
        assertNotNull( after.getGeometryDescriptor() );
    }

    public void testSource() throws Exception {
        FeatureSource<SimpleFeatureType, SimpleFeature> s = DataUtilities.source( roadFeatures );
        assertEquals( -1, s.getCount( Query.ALL ) );
        assertEquals( 3, s.getFeatures().size() );
        assertEquals( 3, s.getFeatures( Query.ALL ).size() );
        assertEquals( 3, s.getFeatures( Filter.INCLUDE ).size() );
        assertEquals( 0, s.getFeatures( Filter.EXCLUDE ).size() );
        assertEquals( 1, s.getFeatures( rd1Filter ).size() );
        assertEquals( 2, s.getFeatures( rd12Filter ).size() );                             
    }

    /**
     * tests the policy of DataUtilities.mixQueries
     * @throws Exception
     */
    public void testMixQueries() throws Exception
	{
    	DefaultQuery firstQuery;
    	DefaultQuery secondQuery;

    	firstQuery = new DefaultQuery("typeName", Filter.EXCLUDE, 100, new String[]{"att1", "att2", "att3"}, "handle");    	
    	secondQuery = new DefaultQuery("typeName", Filter.EXCLUDE, 20, new String[]{"att1", "att2", "att4"}, "handle2");
    	secondQuery.setStartIndex( 4 );
    	
    	Query mixed = DataUtilities.mixQueries(firstQuery, secondQuery, "newhandle");
    	
    	//the passed handle
    	assertEquals("newhandle", mixed.getHandle());
    	//the lower of both
    	assertEquals(20, mixed.getMaxFeatures());
    	//att1, 2, 3 and 4
    	assertEquals(4, mixed.getPropertyNames().length);
    	assertEquals(4, (int) mixed.getStartIndex() );
    	
    	//now use some filters
    	Filter filter1 = null;
    	Filter filter2 = null;
    	FilterFactory ffac = FactoryFinder.getFilterFactory(null);

    	String typeSpec = "geom:Point,att1:String,att2:String,att3:String,att4:String";
    	SimpleFeatureType testType = FeatureTypeUtilities.createType("testType", typeSpec);
    	//System.err.println("created test type: " + testType);
    	
    	filter1 = ffac.equals(ffac.property("att1"), ffac.literal("val1"));
    	filter2 = ffac.equals(ffac.property("att2"), ffac.literal("val2"));

    	firstQuery = new DefaultQuery("typeName", filter1, 100, null, "handle");
    	secondQuery = new DefaultQuery("typeName", filter2, 20, new String[]{"att1", "att2", "att4"}, "handle2");
    	
    	mixed = DataUtilities.mixQueries(firstQuery, secondQuery, "newhandle");
    	
    	//the passed handle
    	assertEquals("newhandle", mixed.getHandle());
    	//the lower of both
    	assertEquals(20, mixed.getMaxFeatures());
    	//att1, 2 and 4
    	assertEquals(3, mixed.getPropertyNames().length);
    	
    	Filter mixedFilter = mixed.getFilter();
    	assertNotNull(mixedFilter);
    	assertTrue(mixedFilter instanceof BinaryLogicOperator);
    	BinaryLogicOperator f = (BinaryLogicOperator) mixedFilter;
    	
    	assertTrue(f instanceof And);
    	for(Iterator fit = f.getChildren().iterator(); fit.hasNext(); )
    	{
    		Filter subFilter = (Filter)fit.next();
    		assertTrue(filter1.equals(subFilter) || filter2.equals(subFilter));
    	}
	}
    
    public void testSpecNoCRS() throws Exception {
        String spec = "id:String,polygonProperty:Polygon";
        SimpleFeatureType ft = FeatureTypeUtilities.createType("testType", spec);
        String spec2 = FeatureTypeUtilities.spec(ft);
        //System.out.println("BEFORE:"+spec);
        //System.out.println(" AFTER:"+spec2);        
        assertEquals(spec, spec2);
    }
    
    public void testSpecCRS() throws Exception {
        String spec = "id:String,polygonProperty:Polygon:srid=32615";
        SimpleFeatureType ft = FeatureTypeUtilities.createType("testType", spec);
        String spec2 = FeatureTypeUtilities.spec(ft);
        //System.out.println("BEFORE:"+spec);
        //System.out.println(" AFTER:"+spec2);        
        assertEquals(spec, spec2);
    }
    
    public void testSpecNotIdentifiable() throws Exception {
        String spec = "id:String,polygonProperty:Polygon:srid=32615";
        SimpleFeatureType ft = FeatureTypeUtilities.createType("testType", spec);
        CoordinateReferenceSystem crsNoId = CRS.parseWKT("PROJCS[\"Geoscience Australia Standard National Scale Lambert Projection\",GEOGCS[\"WGS 84\",DATUM[\"WGS_1984\",SPHEROID[\"WGS_1978\",6378135,298.26],TOWGS84[0,0,0]],PRIMEM[\"Greenwich\",0],UNIT[\"Decimal_Degree\",0.0174532925199433]],PROJECTION[\"Lambert_Conformal_Conic_2SP\"],PARAMETER[\"central_meridian\",134.0],PARAMETER[\"latitude_of_origin\",0.0],PARAMETER[\"standard_parallel_1\",-18.0],PARAMETER[\"standard_parallel_2\",-36.0],UNIT[\"Meter\",1]]");
        SimpleFeatureType transformedFt = FeatureTypeUtilities.transform(ft, crsNoId);

        // since we cannot go back to a code with do a best effort encoding
        String expected = "id:String,polygonProperty:Polygon";
        String spec2 = FeatureTypeUtilities.spec(transformedFt);
        //System.out.println("  BEFORE:"+spec);
        //System.out.println("EXPECTED:"+expected);
        //System.out.println("  AFTER:"+spec2);        
        assertEquals(expected, spec2);
    }

    public static void main(String[] args) {
        junit.textui.TestRunner.run(DataUtilitiesTest.class);
    }
    
}
