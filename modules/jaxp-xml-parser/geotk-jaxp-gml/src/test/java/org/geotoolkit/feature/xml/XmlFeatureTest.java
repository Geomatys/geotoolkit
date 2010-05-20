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
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import javax.xml.bind.JAXBException;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.stream.XMLStreamException;

import org.geotoolkit.feature.DefaultName;
import org.geotoolkit.feature.FeatureTypeBuilder;
import org.geotoolkit.feature.simple.SimpleFeatureBuilder;
import org.geotoolkit.feature.xml.jaxb.JAXBFeatureTypeReader;
import org.geotoolkit.feature.xml.jaxb.JAXBFeatureTypeWriter;
import org.geotoolkit.feature.xml.jaxp.JAXPStreamFeatureReader;
import org.geotoolkit.feature.xml.jaxp.JAXPStreamFeatureWriter;
import org.geotoolkit.referencing.CRS;
import org.geotoolkit.storage.DataStoreException;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.type.FeatureType;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.NoSuchAuthorityCodeException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

import static org.junit.Assert.*;
import static org.geotoolkit.data.AbstractDataStore.*;
import org.xml.sax.SAXException;

/**
 *
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public class XmlFeatureTest {

    private final SimpleFeatureType simpleType;
    private final SimpleFeature simpleFeature1;

    private final FeatureType complexType;

    public XmlFeatureTest() throws NoSuchAuthorityCodeException, FactoryException {
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
        simpleType = ftb.buildSimpleFeatureType();

        ftb.reset();
        ftb.setName(GML_NAMESPACE,"TestComplex");
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
        complexType = ftb.buildFeatureType();


        final GeometryFactory GF = new GeometryFactory();
        final SimpleFeatureBuilder sfb = new SimpleFeatureBuilder(simpleType);

        final Point pt = GF.createPoint(new Coordinate(45, 56));
        final MultiPoint mpt = GF.createMultiPoint(new Coordinate[]{new Coordinate(12, 31), new Coordinate(89, 78)});
        final LineString line1 = GF.createLineString(new Coordinate[]{new Coordinate(12, 31), new Coordinate(88, 77), new Coordinate(10, 38)});
        final LineString line2 = GF.createLineString(new Coordinate[]{new Coordinate(13, 32), new Coordinate(89, 78), new Coordinate(11, 39)});
        final MultiLineString mline = GF.createMultiLineString(new LineString[]{line1,line2});
        final LinearRing ring1 = GF.createLinearRing(new Coordinate[]{new Coordinate(11, 30), new Coordinate(88, 77), new Coordinate(40, 35),new Coordinate(11, 30)});
        final Polygon poly1 = GF.createPolygon(ring1, new LinearRing[0]);
        final LinearRing ring2 = GF.createLinearRing(new Coordinate[]{new Coordinate(12, 31), new Coordinate(89, 78), new Coordinate(41, 36),new Coordinate(12, 31)});
        final Polygon poly2 = GF.createPolygon(ring2, new LinearRing[0]);
        final MultiPolygon mpoly = GF.createMultiPolygon(new Polygon[]{poly1,poly2});

        int i=0;
        sfb.reset();
        sfb.set(i++, 36);
        sfb.set(i++, "stringValue");
        sfb.set(i++, 12);
        sfb.set(i++, 24);
        sfb.set(i++, 48);
        sfb.set(i++, 96.12);
        sfb.set(i++, new BigDecimal(456789));
        sfb.set(i++, new Date(3600000*23));
        sfb.set(i++, new Timestamp(3600000*23));
        sfb.set(i++, Boolean.TRUE);
        sfb.set(i++, pt);
        sfb.set(i++, mpt);
        sfb.set(i++, line1);
        sfb.set(i++, mline);
        sfb.set(i++, poly1);
        sfb.set(i++, mpoly);
        sfb.set(i++, mpt);
        sfb.set(i++, pt);
        simpleFeature1 = sfb.buildFeature("id-156");

    }

    @BeforeClass
    public static void setUpClass() throws Exception {
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
        assertEquals(simpleType, types.get(0));
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
        writer.write(simpleType, new FileOutputStream(temp));

        DomCompare.compare(XmlFeatureTest.class
                .getResourceAsStream("/org/geotoolkit/feature/xml/SimpleType.xsd"), temp);
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
        final XmlFeatureReader reader = new JAXPStreamFeatureReader(simpleType);
        final Object obj = reader.read(XmlFeatureTest.class
                .getResourceAsStream("/org/geotoolkit/feature/xml/SimpleFeature.xml"));
        reader.dispose();

        assertTrue(obj instanceof SimpleFeature);

        SimpleFeature result = (SimpleFeature) obj;
        assertEquals(simpleFeature1.toString()+"\n"+result.toString()+"\n",simpleFeature1, result);
    }

    @Test
    public void testWriteSimpleFeature() throws JAXBException, IOException, XMLStreamException,
            DataStoreException, ParserConfigurationException, SAXException{
        final File temp = File.createTempFile("gml", ".xml");
        //temp.deleteOnExit();
        final XmlFeatureWriter writer = new JAXPStreamFeatureWriter();
        writer.write(simpleFeature1, temp);
        writer.dispose();

        DomCompare.compare(XmlFeatureTest.class
                .getResourceAsStream("/org/geotoolkit/feature/xml/SimpleFeature.xml"), temp);
    }

}