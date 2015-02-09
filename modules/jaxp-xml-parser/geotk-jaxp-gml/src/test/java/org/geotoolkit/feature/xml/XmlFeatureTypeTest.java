/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2010-2015, Geomatys
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

import java.io.BufferedReader;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;
import javax.xml.bind.JAXBException;
import javax.xml.parsers.ParserConfigurationException;
import org.geotoolkit.feature.xml.jaxb.JAXBFeatureTypeReader;
import org.geotoolkit.feature.xml.jaxb.JAXBFeatureTypeWriter;
import org.geotoolkit.xml.DomCompare;

import org.junit.*;

import org.geotoolkit.feature.type.FeatureType;

import static org.junit.Assert.*;
import static org.geotoolkit.feature.xml.XmlTestData.*;
import org.xml.sax.SAXException;

/**
 *
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public class XmlFeatureTypeTest {

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
        assertEquals(simpleTypeFull, types.get(0));
    }

    @Test
    public void testReadSimpleFeatureType2() throws JAXBException {
        final XmlFeatureTypeReader reader = new JAXBFeatureTypeReader();
        final List<FeatureType> types = reader.read(XmlFeatureTypeTest.class
                .getResourceAsStream("/org/geotoolkit/feature/xml/SimpleType2.xsd"));

        assertEquals(1, types.size());
        assertEquals(simpleTypeFull, types.get(0));
    }

    @Test
    public void testReadMultiGeomFeatureType() throws JAXBException {
        final XmlFeatureTypeReader reader = new JAXBFeatureTypeReader();
        final List<FeatureType> types = reader.read(XmlFeatureTypeTest.class
                .getResourceAsStream("/org/geotoolkit/feature/xml/MultiGeomType.xsd"));

        assertEquals(1, types.size());
        assertEquals(multiGeomType, types.get(0));
    }

    @Test
    public void testReadWfsFeatureType() throws JAXBException {
        final XmlFeatureTypeReader reader = new JAXBFeatureTypeReader();
        final List<FeatureType> types = reader.read(XmlFeatureTypeTest.class
                .getResourceAsStream("/org/geotoolkit/feature/xml/wfs1.xsd"));

        assertEquals(1, types.size());
        //assertEquals(multiGeomType, types.get(0));
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
    public void testReadVeryComplexFeatureType() throws Exception {
        final XmlFeatureTypeReader reader = new JAXBFeatureTypeReader();
        final URL url = new URL("http://schemas.opengis.net/gml/3.1.1/base/feature.xsd");
        final List<FeatureType> types = reader.read(url);
        System.out.println("RESULT:" + types);
    }

    @Test
    public void testReadPropertyGroupFeatureType() throws JAXBException {
        final XmlFeatureTypeReader reader = new JAXBFeatureTypeReader();
        final List<FeatureType> types = reader.read(XmlFeatureTypeTest.class
                .getResourceAsStream("/org/geotoolkit/feature/xml/propertyGroupType.xsd"));

        FeatureType type = null;
        for(FeatureType ft : types){
            if(ft.getName().getLocalPart().equals("SF_SamplingFeature")){
                type = ft;
            }
        }

        assertNotNull(type);
        assertEquals(5, type.getDescriptors().size());

    }

    @Test
    public void testWriteSimpleFeatureType() throws JAXBException, IOException, ParserConfigurationException, SAXException{
        final File temp = File.createTempFile("gml", ".xml");
        temp.deleteOnExit();
        final XmlFeatureTypeWriter writer = new JAXBFeatureTypeWriter();
        writer.write(simpleTypeFull, new FileOutputStream(temp));

        DomCompare.compare(XmlFeatureTypeTest.class
                .getResourceAsStream("/org/geotoolkit/feature/xml/SimpleType.xsd"), temp);
    }

    @Test
    public void testWriteMultiGeomFeatureType() throws JAXBException, IOException, ParserConfigurationException, SAXException{
        final File temp = File.createTempFile("gml", ".xml");
        temp.deleteOnExit();
        final XmlFeatureTypeWriter writer = new JAXBFeatureTypeWriter();
        writer.write(multiGeomType, new FileOutputStream(temp));

        DomCompare.compare(XmlFeatureTypeTest.class
                .getResourceAsStream("/org/geotoolkit/feature/xml/MultiGeomType.xsd"), temp);
    }

    @Test
    public void testWriteComplexFeatureType() throws JAXBException, IOException, ParserConfigurationException, SAXException{
        final File temp = File.createTempFile("gml", ".xml");
        temp.deleteOnExit();
        final XmlFeatureTypeWriter writer = new JAXBFeatureTypeWriter();
        writer.write(complexType, new FileOutputStream(temp));

        DomCompare.compare(XmlFeatureTypeTest.class
                .getResourceAsStream("/org/geotoolkit/feature/xml/ComplexType.xsd"), temp);
    }

    
    protected static String getStringResponse(URLConnection conec) throws UnsupportedEncodingException, IOException {
        final StringWriter sw     = new StringWriter();
        final BufferedReader in   = new BufferedReader(new InputStreamReader(conec.getInputStream(), "UTF-8"));
        char [] buffer = new char[1024];
        int size;
        while ((size = in.read(buffer, 0, 1024)) > 0) {
            sw.append(new String(buffer, 0, size));
        }
        return sw.toString();
    }
}
