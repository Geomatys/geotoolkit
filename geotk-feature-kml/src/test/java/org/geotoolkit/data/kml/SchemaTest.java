/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2010, Geomatys
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation; either
 *    version 2.1 of the License, or (at your option) any later version.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */
package org.geotoolkit.data.kml;

import java.net.URISyntaxException;
import org.geotoolkit.data.kml.xml.KmlReader;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Iterator;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.stream.XMLStreamException;

import org.geotoolkit.data.kml.model.Kml;
import org.geotoolkit.data.kml.model.KmlException;
import org.geotoolkit.data.kml.model.KmlModelConstants;
import org.geotoolkit.data.kml.model.Schema;
import org.geotoolkit.data.kml.model.SimpleField;
import org.geotoolkit.data.kml.xml.KmlWriter;
import org.geotoolkit.data.kml.xsd.DefaultCdata;
import org.geotoolkit.data.kml.xml.KmlConstants;
import org.geotoolkit.xml.DomCompare;

import org.junit.Test;

import org.opengis.feature.Feature;
import org.xml.sax.SAXException;

import static org.junit.Assert.*;

/**
 *
 * @author Samuel Andrés
 * @module
 */
public class SchemaTest extends org.geotoolkit.test.TestBase {

    private static final String pathToTestFile = "src/test/resources/org/geotoolkit/data/kml/schema.kml";

    @Test
    public void schemaReadTest() throws IOException, XMLStreamException, KmlException, URISyntaxException {
        final KmlReader reader = new KmlReader();
        reader.setInput(new File(pathToTestFile));
        final Kml kmlObjects = reader.read();
        reader.dispose();

        final Feature document = kmlObjects.getAbstractFeature();
        assertEquals(KmlModelConstants.TYPE_DOCUMENT, document.getType());

        Iterator<?> i = ((Iterable<?>) document.getPropertyValue(KmlConstants.TAG_SCHEMA)).iterator();
        assertTrue("Expected at least one element.", i.hasNext());
        Schema schema = (Schema) i.next();
        assertEquals("TrailHeadType", schema.getName());
        assertEquals("TrailHeadTypeId", schema.getId());

        assertEquals(3, schema.getSimpleFields().size());

        SimpleField simpleField0 = schema.getSimpleFields().get(0);
        assertEquals("string", simpleField0.getType());
        assertEquals("TrailHeadName", simpleField0.getName());
        assertEquals(new DefaultCdata("<b>Trail Head Name</b>"), simpleField0.getDisplayName());
        SimpleField simpleField1 = schema.getSimpleFields().get(1);
        assertEquals("double", simpleField1.getType());
        assertEquals("TrailLength", simpleField1.getName());
        assertEquals(new DefaultCdata("<i>The length in miles</i>"), simpleField1.getDisplayName());
        SimpleField simpleField2 = schema.getSimpleFields().get(2);
        assertEquals("int", simpleField2.getType());
        assertEquals("ElevationGain", simpleField2.getName());
        assertEquals(new DefaultCdata("<i>change in altitude</i>"), simpleField2.getDisplayName());

        assertFalse("Expected exactly one element.", i.hasNext());
    }

    @Test
    public void schemaWriteTest() throws KmlException, IOException, XMLStreamException, ParserConfigurationException, SAXException, URISyntaxException {
        final KmlFactory kmlFactory = DefaultKmlFactory.getInstance();

        final SimpleField simpleField0 = kmlFactory.createSimpleField();
        simpleField0.setDisplayName(new DefaultCdata("<b>Trail Head Name</b>"));
        simpleField0.setType("string");
        simpleField0.setName("TrailHeadName");

        final SimpleField simpleField1 = kmlFactory.createSimpleField();
        simpleField1.setDisplayName(new DefaultCdata("<i>The length in miles</i>"));
        simpleField1.setType("double");
        simpleField1.setName("TrailLength");

        final SimpleField simpleField2 = kmlFactory.createSimpleField();
        simpleField2.setDisplayName(new DefaultCdata("<i>change in altitude</i>"));
        simpleField2.setType("int");
        simpleField2.setName("ElevationGain");

        final Schema schema = kmlFactory.createSchema(
                Arrays.asList(simpleField0, simpleField1, simpleField2),
                "TrailHeadType", "TrailHeadTypeId", null);

        final Feature document = kmlFactory.createDocument();
        document.setPropertyValue(KmlConstants.TAG_SCHEMA, schema);

        final Kml kml = kmlFactory.createKml(null, document, null, null);

        final File temp = File.createTempFile("testSchema", ".kml");
        temp.deleteOnExit();

        final KmlWriter writer = new KmlWriter();
        writer.setOutput(temp);
        writer.write(kml);
        writer.dispose();

        DomCompare.compare(new File(pathToTestFile), temp);
    }
}
