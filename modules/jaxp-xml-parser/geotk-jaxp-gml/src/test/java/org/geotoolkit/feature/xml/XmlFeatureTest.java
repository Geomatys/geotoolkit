/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2010, Geomatys
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

package org.geotoolkit.feature.xml;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryCollection;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.LinearRing;
import com.vividsolutions.jts.geom.MultiLineString;
import com.vividsolutions.jts.geom.MultiPoint;
import com.vividsolutions.jts.geom.MultiPolygon;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.Polygon;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.bind.JAXBException;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.stream.XMLStreamException;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.geotoolkit.data.FeatureStoreUtilities;
import org.geotoolkit.data.FeatureCollection;
import org.geotoolkit.data.FeatureIterator;
import org.geotoolkit.data.query.QueryBuilder;
import org.geotoolkit.factory.FactoryFinder;
import org.geotoolkit.feature.DefaultName;
import org.geotoolkit.feature.FeatureTypeBuilder;
import org.geotoolkit.feature.simple.SimpleFeatureBuilder;
import org.geotoolkit.feature.xml.jaxb.JAXBFeatureTypeReader;
import org.geotoolkit.feature.xml.jaxb.JAXBFeatureTypeWriter;
import org.geotoolkit.feature.xml.jaxp.JAXPStreamFeatureReader;
import org.geotoolkit.feature.xml.jaxp.JAXPStreamFeatureWriter;
import org.geotoolkit.referencing.CRS;
import org.geotoolkit.storage.DataStoreException;
import org.geotoolkit.xml.DomCompare;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.type.FeatureType;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.filter.FilterFactory;
import org.opengis.filter.sort.SortOrder;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

import static org.junit.Assert.*;
import static org.geotoolkit.data.AbstractFeatureStore.*;
import org.geotoolkit.data.AbstractFeatureCollection;
import org.geotoolkit.feature.FeatureUtilities;
import org.geotoolkit.feature.xml.jaxp.ElementFeatureWriter;
import org.geotoolkit.util.FileUtilities;
import org.opengis.feature.ComplexAttribute;
import org.opengis.feature.Feature;
import org.opengis.feature.Property;
import org.opengis.feature.type.ComplexType;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

/**
 *
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public class XmlFeatureTest {

    private static final FilterFactory FF = FactoryFinder.getFilterFactory(null);

    private static SimpleFeatureType simpleTypeBasic;
    private static SimpleFeatureType simpleTypeFull;
    private static SimpleFeature simpleFeatureFull;
    private static SimpleFeature simpleFeature1;
    private static SimpleFeature simpleFeature2;
    private static SimpleFeature simpleFeature3;
    private static FeatureCollection collectionSimple;
    private static Feature complexFeature;

    private static FeatureType multiGeomType;

    private static FeatureType complexType;

    private static String EPSG_VERSION;

    @BeforeClass
    public static void setUpClass() throws Exception {


        final CoordinateReferenceSystem crs = CRS.decode("EPSG:4326");

        final FeatureTypeBuilder ftb = new FeatureTypeBuilder();
        ftb.setName(GML_NAMESPACE,"TestSimple");
        ftb.add(new DefaultName(GML_NAMESPACE,"ID"),                   Integer.class);
        ftb.add(new DefaultName(GML_NAMESPACE,"attString"),            String.class);
        ftb.add(new DefaultName(GML_NAMESPACE,"attShort"),             Short.class);
        ftb.add(new DefaultName(GML_NAMESPACE,"attInteger"),           Integer.class);
        ftb.add(new DefaultName(GML_NAMESPACE,"attLong"),              Long.class);
        ftb.add(new DefaultName(GML_NAMESPACE,"attDouble"),            Double.class);
        ftb.add(new DefaultName(GML_NAMESPACE,"attDecimal"),           BigDecimal.class);
        ftb.add(new DefaultName(GML_NAMESPACE,"attDate"),              Date.class);
        ftb.add(new DefaultName(GML_NAMESPACE,"attDateTime"),          Timestamp.class);
        ftb.add(new DefaultName(GML_NAMESPACE,"attBoolean"),           Boolean.class);
        ftb.add(new DefaultName(GML_NAMESPACE,"geomPoint"),            Point.class, crs);
        ftb.add(new DefaultName(GML_NAMESPACE,"geomMultiPoint"),       MultiPoint.class, crs);
        ftb.add(new DefaultName(GML_NAMESPACE,"geomLine"),             LineString.class, crs);
        ftb.add(new DefaultName(GML_NAMESPACE,"geomMultiLine"),        MultiLineString.class, crs);
        ftb.add(new DefaultName(GML_NAMESPACE,"geomPolygon"),          Polygon.class, crs);
        //multipolygon does not exist in gml
        ftb.add(new DefaultName(GML_NAMESPACE,"geomMultiPolygon"),     GeometryCollection.class, crs);
        ftb.add(new DefaultName(GML_NAMESPACE,"geomMultiGeometry"),    GeometryCollection.class, crs);
        ftb.add(new DefaultName(GML_NAMESPACE,"geomAnyGeometry"),      Geometry.class, crs);
        simpleTypeFull = ftb.buildSimpleFeatureType();

        ftb.reset();
        ftb.setName(GML_NAMESPACE,"TestMultiGeom");
        ftb.add(new DefaultName(GML_NAMESPACE,"ID"),                   Integer.class,           1,1,true,null);
        ftb.add(new DefaultName(GML_NAMESPACE,"attString"),            String.class,            1,1,true,null);
        ftb.add(new DefaultName(GML_NAMESPACE,"attShort"),             Short.class,             1,1,true,null);
        ftb.add(new DefaultName(GML_NAMESPACE,"attInteger"),           Integer.class,           1,1,true,null);
        ftb.add(new DefaultName(GML_NAMESPACE,"attLong"),              Long.class,              0,1,true,null);
        ftb.add(new DefaultName(GML_NAMESPACE,"attDouble"),            Double.class,            0,1,true,null);
        ftb.add(new DefaultName(GML_NAMESPACE,"attDecimal"),           BigDecimal.class,        0,1,true,null);
        ftb.add(new DefaultName(GML_NAMESPACE,"attDate"),              Date.class,              1,Integer.MAX_VALUE,true,null);
        ftb.add(new DefaultName(GML_NAMESPACE,"attDateTime"),          Timestamp.class,         1,Integer.MAX_VALUE,true,null);
        ftb.add(new DefaultName(GML_NAMESPACE,"attBoolean"),           Boolean.class,           1,Integer.MAX_VALUE,true,null);
        ftb.add(new DefaultName(GML_NAMESPACE,"geomPoint"),            Point.class,             crs,0,Integer.MAX_VALUE,true,null);
        ftb.add(new DefaultName(GML_NAMESPACE,"geomMultiPoint"),       MultiPoint.class,        crs,0,Integer.MAX_VALUE,true,null);
        ftb.add(new DefaultName(GML_NAMESPACE,"geomLine"),             LineString.class,        crs,0,Integer.MAX_VALUE,true,null);
        ftb.add(new DefaultName(GML_NAMESPACE,"geomMultiLine"),        MultiLineString.class,   crs,1,Integer.MAX_VALUE,true,null);
        ftb.add(new DefaultName(GML_NAMESPACE,"geomPolygon"),          Polygon.class,           crs,1,Integer.MAX_VALUE,true,null);
        //multipolygon does not exist in gml
        ftb.add(new DefaultName(GML_NAMESPACE,"geomMultiPolygon"),     GeometryCollection.class,crs,1,Integer.MAX_VALUE,true,null);
        ftb.add(new DefaultName(GML_NAMESPACE,"geomMultiGeometry"),    GeometryCollection.class,crs,1,Integer.MAX_VALUE,true,null);
        ftb.add(new DefaultName(GML_NAMESPACE,"geomAnyGeometry"),      Geometry.class,          crs,1,Integer.MAX_VALUE,true,null);
        multiGeomType = ftb.buildFeatureType();

        ftb.reset();
        ftb.setName(GML_NAMESPACE,"TestSimpleBasic");
        ftb.add(new DefaultName(GML_NAMESPACE,"attString"),            String.class);
        ftb.add(new DefaultName(GML_NAMESPACE,"attDouble"),            Double.class);
        simpleTypeBasic = ftb.buildSimpleFeatureType();


        ftb.reset();
        ftb.setName(GML_NAMESPACE,"Address");
        ftb.add(new DefaultName(GML_NAMESPACE,"streetName"),           String.class,            1,1,true,null);
        ftb.add(new DefaultName(GML_NAMESPACE,"streetNumber"),         String.class,            1,1,true,null);
        ftb.add(new DefaultName(GML_NAMESPACE,"city"),                 String.class,            1,1,true,null);
        ftb.add(new DefaultName(GML_NAMESPACE,"province"),             String.class,            1,1,true,null);
        ftb.add(new DefaultName(GML_NAMESPACE,"postalCode"),           String.class,            1,1,true,null);
        ftb.add(new DefaultName(GML_NAMESPACE,"country"),              String.class,            0,1,true,null);
        final ComplexType adress = ftb.buildType();

        ftb.reset();
        ftb.setName(GML_NAMESPACE,"Person");
        ftb.add(new DefaultName(GML_NAMESPACE,"insuranceNumber"),      Integer.class,           1,1,true,null);
        ftb.add(new DefaultName(GML_NAMESPACE,"lastName"),             String.class,            1,1,true,null);
        ftb.add(new DefaultName(GML_NAMESPACE,"firstName"),            String.class,            1,1,true,null);
        ftb.add(new DefaultName(GML_NAMESPACE,"age"),                  Integer.class,           1,1,true,null);
        ftb.add(new DefaultName(GML_NAMESPACE,"sex"),                  String.class,            1,1,true,null);
        //ftb.add(new DefaultName(GML_NAMESPACE,"spouse"),               Person.class,            0,1,true,null);
        ftb.add(new DefaultName(GML_NAMESPACE,"location"),             Point.class,        crs, 0,1,true,null);
        ftb.add(adress, new DefaultName(GML_NAMESPACE,"mailAddress"),  null,                    0,1,true,null);
        ftb.add(new DefaultName(GML_NAMESPACE,"phone"),                String.class,            0,Integer.MAX_VALUE,true,null);

        complexType = ftb.buildFeatureType();


        final GeometryFactory GF = new GeometryFactory();
        SimpleFeatureBuilder sfb = new SimpleFeatureBuilder(simpleTypeFull);

        final Point pt = GF.createPoint(new Coordinate(5, 10));
        final MultiPoint mpt = GF.createMultiPoint(new Coordinate[]{new Coordinate(5, 10), new Coordinate(15, 20)});
        final LineString line1 = GF.createLineString(new Coordinate[]{new Coordinate(10, 10), new Coordinate(20, 20), new Coordinate(30, 30)});
        final LineString line2 = GF.createLineString(new Coordinate[]{new Coordinate(11, 11), new Coordinate(21, 21), new Coordinate(31, 31)});
        final MultiLineString mline = GF.createMultiLineString(new LineString[]{line1,line2});
        final LinearRing ring1 = GF.createLinearRing(new Coordinate[]{new Coordinate(0, 0), new Coordinate(10, 0), new Coordinate(10, 10),new Coordinate(0, 0)});
        final Polygon poly1 = GF.createPolygon(ring1, new LinearRing[0]);
        final LinearRing ring2 = GF.createLinearRing(new Coordinate[]{new Coordinate(1, 1), new Coordinate(11, 1), new Coordinate(11, 11),new Coordinate(1, 1)});
        final Polygon poly2 = GF.createPolygon(ring2, new LinearRing[0]);
        final MultiPolygon mpoly = GF.createMultiPolygon(new Polygon[]{poly1,poly2});

        final Calendar calendar1 = Calendar.getInstance();
        calendar1.setTimeZone(TimeZone.getTimeZone("GMT+0"));
        calendar1.set(Calendar.YEAR, 2005);
        calendar1.set(Calendar.MONTH, 7);
        calendar1.set(Calendar.DAY_OF_MONTH, 21);
        calendar1.set(Calendar.HOUR_OF_DAY, 0);
        calendar1.set(Calendar.MINUTE, 0);
        calendar1.set(Calendar.SECOND, 0);
        calendar1.set(Calendar.MILLISECOND, 0);

        final Calendar calendar2 = Calendar.getInstance();
        calendar2.setTimeZone(TimeZone.getTimeZone("GMT+0"));
        calendar2.set(Calendar.YEAR, 2005);
        calendar2.set(Calendar.MONTH, 7);
        calendar2.set(Calendar.DAY_OF_MONTH, 21);
        calendar2.set(Calendar.HOUR_OF_DAY, 15);
        calendar2.set(Calendar.MINUTE, 43);
        calendar2.set(Calendar.SECOND, 12);
        calendar2.set(Calendar.MILLISECOND, 52);

        int i=0;
        sfb.reset();
        sfb.set(i++, 36);
        sfb.set(i++, "stringValue");
        sfb.set(i++, 12);
        sfb.set(i++, 24);
        sfb.set(i++, 48);
        sfb.set(i++, 96.12);
        sfb.set(i++, new BigDecimal(456789));
        sfb.set(i++, calendar1.getTime());
        sfb.set(i++, new Timestamp(calendar2.getTimeInMillis()));
        sfb.set(i++, Boolean.TRUE);
        sfb.set(i++, pt);
        sfb.set(i++, mpt);
        sfb.set(i++, line1);
        sfb.set(i++, mline);
        sfb.set(i++, poly1);
        sfb.set(i++, mpoly);
        sfb.set(i++, mpt);
        sfb.set(i++, pt);
        simpleFeatureFull = sfb.buildFeature("id-156");

        sfb = new SimpleFeatureBuilder(simpleTypeBasic);
        sfb.set(0,"some text with words.");
        sfb.set(1,56.14d);
        simpleFeature1 = sfb.buildFeature("id-89");
        sfb.set(0,"some words assembled in a text.");
        sfb.set(1,39.45d);
        simpleFeature2 = sfb.buildFeature("id-36");
        sfb.set(0,"a text composed of words.");
        sfb.set(1,12.31d);
        simpleFeature3 = sfb.buildFeature("id-412");

        collectionSimple = FeatureStoreUtilities.collection("one of a kind ID", simpleTypeBasic);
        collectionSimple.add(simpleFeature1);
        collectionSimple.add(simpleFeature2);
        collectionSimple.add(simpleFeature3);
        collectionSimple = collectionSimple.subCollection(
                    QueryBuilder.sorted(collectionSimple.getFeatureType().getName(), FF.sort("attDouble", SortOrder.ASCENDING)));
                    ((AbstractFeatureCollection)collectionSimple).setId("one of a kind ID");


        complexFeature = FeatureUtilities.defaultFeature(complexType, "id-0");
        complexFeature.getProperty("insuranceNumber").setValue(new Integer(345678345));
        complexFeature.getProperty("lastName").setValue("Smith");
        complexFeature.getProperty("firstName").setValue("John");

        //final Property age = FeatureUtilities.defaultProperty(complexType.getDescriptor("age"));
        //age.setValue(new Integer(35));
        complexFeature.getProperty("age").setValue(new Integer(35));
        complexFeature.getProperty("sex").setValue("male");

        final Property location = FeatureUtilities.defaultProperty(complexType.getDescriptor("location"));
        location.setValue(GF.createPoint(new Coordinate(10, 10)));
        complexFeature.getProperties().add(location);

        final ComplexAttribute address = (ComplexAttribute) FeatureUtilities.defaultProperty(
                complexType.getDescriptor("mailAddress"));
        address.getProperty("streetName").setValue("Main");
        address.getProperty("streetNumber").setValue("10");
        address.getProperty("city").setValue("SomeTown");
        address.getProperty("province").setValue("Ontario");
        address.getProperty("postalCode").setValue("M1R1K9");
        final Property country = FeatureUtilities.defaultProperty(adress.getDescriptor("country"));
        country.setValue("Canada");
        address.getProperties().add(country);

        complexFeature.getProperties().add(address);

        final Property phone = FeatureUtilities.defaultProperty(complexType.getDescriptor("phone"));
        phone.setValue(Arrays.asList("4161234567", "4168901234"));
        complexFeature.getProperties().add(phone);

        EPSG_VERSION = CRS.getVersion("EPSG").toString();
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    @Test
    public void testReadSimpleFeatureType() throws JAXBException {
        final XmlFeatureTypeReader reader = new JAXBFeatureTypeReader();
        final List<FeatureType> types = reader.read(XmlFeatureTest.class
                .getResourceAsStream("/org/geotoolkit/feature/xml/SimpleType.xsd"));

        assertEquals(1, types.size());
        assertEquals(simpleTypeFull, types.get(0));
    }

    @Test
    public void testReadSimpleFeatureType2() throws JAXBException {
        final XmlFeatureTypeReader reader = new JAXBFeatureTypeReader();
        final List<FeatureType> types = reader.read(XmlFeatureTest.class
                .getResourceAsStream("/org/geotoolkit/feature/xml/SimpleType2.xsd"));

        assertEquals(1, types.size());
        assertEquals(simpleTypeFull, types.get(0));
    }

    @Test
    public void testReadMultiGeomFeatureType() throws JAXBException {
        final XmlFeatureTypeReader reader = new JAXBFeatureTypeReader();
        final List<FeatureType> types = reader.read(XmlFeatureTest.class
                .getResourceAsStream("/org/geotoolkit/feature/xml/MultiGeomType.xsd"));

        assertEquals(1, types.size());
        assertEquals(multiGeomType, types.get(0));
    }

    @Test
    public void testReadWfsFeatureType() throws JAXBException {
        final XmlFeatureTypeReader reader = new JAXBFeatureTypeReader();
        final List<FeatureType> types = reader.read(XmlFeatureTest.class
                .getResourceAsStream("/org/geotoolkit/feature/xml/wfs1.xsd"));

        assertEquals(1, types.size());
        //assertEquals(multiGeomType, types.get(0));
    }

    @Test
    public void testReadComplexFeatureType() throws JAXBException {
        final XmlFeatureTypeReader reader = new JAXBFeatureTypeReader();
        final List<FeatureType> types = reader.read(XmlFeatureTest.class
                .getResourceAsStream("/org/geotoolkit/feature/xml/ComplexType.xsd"));

        assertEquals(1, types.size());
        assertEquals(complexType, types.get(0));
    }

    @Test
    public void testWriteSimpleFeatureType() throws JAXBException, IOException, ParserConfigurationException, SAXException{
        final File temp = File.createTempFile("gml", ".xml");
        temp.deleteOnExit();
        final XmlFeatureTypeWriter writer = new JAXBFeatureTypeWriter();
        writer.write(simpleTypeFull, new FileOutputStream(temp));

        DomCompare.compare(XmlFeatureTest.class
                .getResourceAsStream("/org/geotoolkit/feature/xml/SimpleType.xsd"), temp);
    }

    @Test
    public void testWriteMultiGeomFeatureType() throws JAXBException, IOException, ParserConfigurationException, SAXException{
        final File temp = File.createTempFile("gml", ".xml");
        temp.deleteOnExit();
        final XmlFeatureTypeWriter writer = new JAXBFeatureTypeWriter();
        writer.write(multiGeomType, new FileOutputStream(temp));

        DomCompare.compare(XmlFeatureTest.class
                .getResourceAsStream("/org/geotoolkit/feature/xml/MultiGeomType.xsd"), temp);
    }

    @Test
    public void testWriteComplexFeatureType() throws JAXBException, IOException, ParserConfigurationException, SAXException{
        final File temp = File.createTempFile("gml", ".xml");
        temp.deleteOnExit();
        final XmlFeatureTypeWriter writer = new JAXBFeatureTypeWriter();
        writer.write(complexType, new FileOutputStream(temp));

        DomCompare.compare(XmlFeatureTest.class
                .getResourceAsStream("/org/geotoolkit/feature/xml/ComplexType.xsd"), temp);
    }

    @Test
    public void testReadSimpleFeature() throws JAXBException, IOException, XMLStreamException{
        final XmlFeatureReader reader = new JAXPStreamFeatureReader(simpleTypeFull);
        Object obj = reader.read(XmlFeatureTest.class
                .getResourceAsStream("/org/geotoolkit/feature/xml/SimpleFeature.xml"));
        reader.dispose();

        assertTrue(obj instanceof SimpleFeature);

        SimpleFeature result = (SimpleFeature) obj;
        assertEquals(simpleFeatureFull, result);

        final XmlFeatureReader readerGml = new JAXPStreamFeatureReader(simpleTypeFull);
        readerGml.getProperties().put(JAXPStreamFeatureReader.BINDING_PACKAGE, "GML");
        obj = readerGml.read(XmlFeatureTest.class.getResourceAsStream("/org/geotoolkit/feature/xml/SimpleFeature.xml"));
        readerGml.dispose();

        assertTrue(obj instanceof SimpleFeature);

        result = (SimpleFeature) obj;
        assertEquals(simpleFeatureFull, result);
    }

    @Test
    public void testReadSimpleFeatureOldEnc() throws JAXBException, IOException, XMLStreamException{

        final XmlFeatureReader readerGml = new JAXPStreamFeatureReader(simpleTypeFull);
        readerGml.getProperties().put(JAXPStreamFeatureReader.BINDING_PACKAGE, "GML");
        Object obj = readerGml.read(XmlFeatureTest.class.getResourceAsStream("/org/geotoolkit/feature/xml/SimpleFeatureOldEnc.xml"));

        assertTrue(obj instanceof SimpleFeature);

        SimpleFeature result = (SimpleFeature) obj;
        assertEquals(simpleFeatureFull, result);

        obj = readerGml.read(XmlFeatureTest.class
                .getResourceAsStream("/org/geotoolkit/feature/xml/SimpleFeatureOldEnc2.xml"));

        assertTrue(obj instanceof SimpleFeature);

        result = (SimpleFeature) obj;
        assertEquals(simpleFeatureFull, result);

        // adding lineString encoding
        obj = readerGml.read(XmlFeatureTest.class
                .getResourceAsStream("/org/geotoolkit/feature/xml/SimpleFeatureOldEnc3.xml"));
        readerGml.dispose();

        assertTrue(obj instanceof SimpleFeature);

        result = (SimpleFeature) obj;
        assertEquals(simpleFeatureFull, result);

        /*
         * Not working with JTSWrapper binding mode for JAXP Feature Writer
         *
         * working for Polygon
         * working for LineString
         * not for point
         */

        final XmlFeatureReader reader = new JAXPStreamFeatureReader(simpleTypeFull);
        obj = reader.read(XmlFeatureTest.class
                .getResourceAsStream("/org/geotoolkit/feature/xml/SimpleFeatureOldEnc.xml"));

        assertTrue(obj instanceof SimpleFeature);

        result = (SimpleFeature) obj;
        assertEquals(simpleFeatureFull, result);

        // adding lineString encoding
        obj = reader.read(XmlFeatureTest.class
                .getResourceAsStream("/org/geotoolkit/feature/xml/SimpleFeatureOldEnc2.xml"));

        assertTrue(obj instanceof SimpleFeature);

        result = (SimpleFeature) obj;
        assertEquals(simpleFeatureFull, result);

        // adding lineString encoding
        obj = reader.read(XmlFeatureTest.class
                .getResourceAsStream("/org/geotoolkit/feature/xml/SimpleFeatureOldEnc3.xml"));
        reader.dispose();

        assertTrue(obj instanceof SimpleFeature);

        result = (SimpleFeature) obj;
        assertFalse(simpleFeatureFull.equals(result));
    }

    @Test
    public void testWriteSimpleFeature() throws JAXBException, IOException, XMLStreamException,
            DataStoreException, ParserConfigurationException, SAXException{
        final File temp = File.createTempFile("gml", ".xml");
        temp.deleteOnExit();
        final XmlFeatureWriter writer = new JAXPStreamFeatureWriter();
        writer.write(simpleFeatureFull, temp);
        writer.dispose();

        String expResult = FileUtilities.getStringFromStream(XmlFeatureTest.class.getResourceAsStream("/org/geotoolkit/feature/xml/SimpleFeature.xml"));
        expResult = expResult.replace("EPSG_VERSION", EPSG_VERSION);
        DomCompare.compare(expResult, temp);
    }

    @Test
    public void testWriteSimpleFeatureElement() throws JAXBException, IOException, XMLStreamException,
            DataStoreException, ParserConfigurationException, SAXException, TransformerConfigurationException, TransformerException{
        final File temp = File.createTempFile("gml", ".xml");
        temp.deleteOnExit();
        final ElementFeatureWriter writer = new ElementFeatureWriter();
        Element result = writer.write(simpleFeatureFull, false);

        Source source = new DOMSource(result.getOwnerDocument());

        // Prepare the output file
        Result resultxml = new StreamResult(temp);

        // Write the DOM document to the file
        Transformer xformer = TransformerFactory.newInstance().newTransformer();
        xformer.transform(source, resultxml);


        String expResult = FileUtilities.getStringFromStream(XmlFeatureTest.class.getResourceAsStream("/org/geotoolkit/feature/xml/SimpleFeature.xml"));
        expResult = expResult.replace("EPSG_VERSION", EPSG_VERSION);
        DomCompare.compare(expResult, temp);
    }

    @Test
    public void testReadSimpleCollection() throws JAXBException, IOException, XMLStreamException{
        final XmlFeatureReader reader = new JAXPStreamFeatureReader(simpleTypeBasic);
        final Object obj = reader.read(XmlFeatureTest.class
                .getResourceAsStream("/org/geotoolkit/feature/xml/CollectionSimple.xml"));
        reader.dispose();

        assertTrue(obj instanceof FeatureCollection);

        FeatureCollection result = (FeatureCollection) obj;
        try {
            String id = result.getID();
            result = result.subCollection(QueryBuilder.sorted(
                    result.getFeatureType().getName(), FF.sort("attDouble", SortOrder.ASCENDING)));
            ((AbstractFeatureCollection)result).setId(id);
        } catch (DataStoreException ex) {
            Logger.getLogger(XmlFeatureTest.class.getName()).log(Level.SEVERE, null, ex);
        }

        FeatureIterator resultIte = result.iterator();
        FeatureIterator expectedIte = collectionSimple.iterator();

        assertEquals(collectionSimple.size(), result.size());
        assertEquals(collectionSimple.getID(), result.getID());

        assertEquals(resultIte.next(), expectedIte.next());
        assertEquals(resultIte.next(), expectedIte.next());
        assertEquals(resultIte.next(), expectedIte.next());
        resultIte.close();
        expectedIte.close();
    }

    @Test
    public void testReadSimpleCollectionEmbeddedFT() throws JAXBException, IOException, XMLStreamException{
        JAXPStreamFeatureReader reader = new JAXPStreamFeatureReader();
        reader.getProperties().put(JAXPStreamFeatureReader.READ_EMBEDDED_FEATURE_TYPE, true);

        Object obj = reader.read(XmlFeatureTest.class
                .getResourceAsStream("/org/geotoolkit/feature/xml/featureCollectionEmbedFT.xml"));
        reader.dispose();

        assertTrue(obj instanceof FeatureCollection);

        reader = new JAXPStreamFeatureReader();
        reader.getProperties().put(JAXPStreamFeatureReader.READ_EMBEDDED_FEATURE_TYPE, true);

        obj = reader.read(XmlFeatureTest.class
                .getResourceAsStream("/org/geotoolkit/feature/xml/featureCollectionEmbedFT2.xml"));
        reader.dispose();

        assertTrue(obj instanceof FeatureCollection);

        obj = reader.read(XmlFeatureTest.class
                .getResourceAsStream("/org/geotoolkit/feature/xml/featureCollectionEmbedFT3.xml"));
        reader.dispose();

        assertTrue(obj instanceof FeatureCollection);
    }


    @Test
    public void testWriteSimpleCollection() throws JAXBException, IOException, XMLStreamException,
            DataStoreException, ParserConfigurationException, SAXException{
        final File temp = File.createTempFile("gml", ".xml");
        temp.deleteOnExit();
        final XmlFeatureWriter writer = new JAXPStreamFeatureWriter();
        writer.write(collectionSimple, temp);
        writer.dispose();

        DomCompare.compare(XmlFeatureTest.class
                .getResourceAsStream("/org/geotoolkit/feature/xml/CollectionSimple.xml"), temp);
    }

    @Test
    public void testWriteSimplCollectionElement() throws JAXBException, IOException, XMLStreamException,
            DataStoreException, ParserConfigurationException, SAXException, TransformerConfigurationException, TransformerException{
        final File temp = File.createTempFile("gml", ".xml");
        temp.deleteOnExit();
        final ElementFeatureWriter writer = new ElementFeatureWriter();
        Element result = writer.write(collectionSimple, false);

        Source source = new DOMSource(result.getOwnerDocument());

        // Prepare the output file
        Result resultxml = new StreamResult(temp);

        // Write the DOM document to the file
        Transformer xformer = TransformerFactory.newInstance().newTransformer();
        xformer.transform(source, resultxml);


        String expResult = FileUtilities.getStringFromStream(XmlFeatureTest.class.getResourceAsStream("/org/geotoolkit/feature/xml/CollectionSimple.xml"));
        expResult = expResult.replace("EPSG_VERSION", EPSG_VERSION);
        DomCompare.compare(expResult, temp);
    }

    @Test
    public void testWriteComplexFeature() throws JAXBException, IOException, XMLStreamException,
            DataStoreException, ParserConfigurationException, SAXException{
        final File temp = File.createTempFile("gml", ".xml");
        temp.deleteOnExit();
        final XmlFeatureWriter writer = new JAXPStreamFeatureWriter();
        final StringWriter sw = new StringWriter();
        writer.write(complexFeature, temp);
        writer.dispose();

        String expResult = FileUtilities.getStringFromStream(XmlFeatureTest.class.getResourceAsStream("/org/geotoolkit/feature/xml/ComplexFeature.xml"));
        expResult = expResult.replace("EPSG_VERSION", EPSG_VERSION);
        DomCompare.compare(expResult, temp);
    }

    @Test
    public void testReadComplexFeature() throws JAXBException, IOException, XMLStreamException{
        final XmlFeatureReader reader = new JAXPStreamFeatureReader(complexType);
        Object obj = reader.read(XmlFeatureTest.class
                .getResourceAsStream("/org/geotoolkit/feature/xml/ComplexFeature.xml"));
        reader.dispose();

        assertTrue(obj instanceof Feature);

        Feature result = (Feature) obj;

        assertEquals(complexFeature, result);

        final XmlFeatureReader readerGml = new JAXPStreamFeatureReader(complexType);
        readerGml.getProperties().put(JAXPStreamFeatureReader.BINDING_PACKAGE, "GML");
        obj = readerGml.read(XmlFeatureTest.class.getResourceAsStream("/org/geotoolkit/feature/xml/ComplexFeature.xml"));
        readerGml.dispose();

        assertTrue(obj instanceof Feature);

        result =  (Feature) obj;
        assertEquals(complexFeature, result);
    }
}
