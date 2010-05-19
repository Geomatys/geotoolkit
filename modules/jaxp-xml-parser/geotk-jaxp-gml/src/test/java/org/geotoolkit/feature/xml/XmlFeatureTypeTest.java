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

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryCollection;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.MultiLineString;
import com.vividsolutions.jts.geom.MultiPoint;
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

import org.geotoolkit.feature.DefaultName;
import org.geotoolkit.feature.FeatureTypeBuilder;
import org.geotoolkit.feature.xml.jaxb.JAXBFeatureTypeReader;
import org.geotoolkit.feature.xml.jaxb.JAXBFeatureTypeWriter;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import org.opengis.feature.type.FeatureType;
import org.opengis.feature.simple.SimpleFeatureType;

import static org.junit.Assert.*;
import static org.geotoolkit.data.AbstractDataStore.*;
import org.xml.sax.SAXException;

/**
 *
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public class XmlFeatureTypeTest {

    private final SimpleFeatureType simpleType;
    private final FeatureType complexType;

    public XmlFeatureTypeTest() {
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
        ftb.add(new DefaultName(GML_NAMESPACE,"geomPoint"),            Point.class);
        ftb.add(new DefaultName(GML_NAMESPACE,"geomMultiPoint"),       MultiPoint.class);
        ftb.add(new DefaultName(GML_NAMESPACE,"geomLine"),             LineString.class);
        ftb.add(new DefaultName(GML_NAMESPACE,"geomMultiLine"),        MultiLineString.class);
        ftb.add(new DefaultName(GML_NAMESPACE,"geomPolygon"),          Polygon.class);
        ftb.add(new DefaultName(GML_NAMESPACE,"geomMultiPolygon"),     GeometryCollection.class); //multipolygon does not exist in gml
        ftb.add(new DefaultName(GML_NAMESPACE,"geomMultiGeometry"),    GeometryCollection.class);
        ftb.add(new DefaultName(GML_NAMESPACE,"geomAnyGeometry"),      Geometry.class);
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
        ftb.add(new DefaultName(GML_NAMESPACE,"geomPoint"),            Point.class,             0,Integer.MAX_VALUE,true,null);
        ftb.add(new DefaultName(GML_NAMESPACE,"geomMultiPoint"),       MultiPoint.class,        0,Integer.MAX_VALUE,true,null);
        ftb.add(new DefaultName(GML_NAMESPACE,"geomLine"),             LineString.class,        0,Integer.MAX_VALUE,true,null);
        ftb.add(new DefaultName(GML_NAMESPACE,"geomMultiLine"),        MultiLineString.class,   1,Integer.MAX_VALUE,true,null);
        ftb.add(new DefaultName(GML_NAMESPACE,"geomPolygon"),          Polygon.class,           1,Integer.MAX_VALUE,true,null);
        //multipolygon does not exist in gml
        ftb.add(new DefaultName(GML_NAMESPACE,"geomMultiPolygon"),     GeometryCollection.class,1,Integer.MAX_VALUE,true,null);
        ftb.add(new DefaultName(GML_NAMESPACE,"geomMultiGeometry"),    GeometryCollection.class,1,Integer.MAX_VALUE,true,null);
        ftb.add(new DefaultName(GML_NAMESPACE,"geomAnyGeometry"),      Geometry.class,          1,Integer.MAX_VALUE,true,null);
        complexType = ftb.buildFeatureType();
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
        final List<FeatureType> types = reader.read(XmlFeatureTypeTest.class
                .getResourceAsStream("/org/geotoolkit/feature/xml/SimpleType.xsd"));

        assertEquals(1, types.size());
        assertEquals(simpleType, types.get(0));
    }

    @Test
    public void testReadComplexFeatureType() throws JAXBException {
        final XmlFeatureTypeReader reader = new JAXBFeatureTypeReader();
        final List<FeatureType> types = reader.read(XmlFeatureTypeTest.class
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

        DomCompare.compare(XmlFeatureTypeTest.class
                .getResourceAsStream("/org/geotoolkit/feature/xml/SimpleType.xsd"), temp);
    }

}