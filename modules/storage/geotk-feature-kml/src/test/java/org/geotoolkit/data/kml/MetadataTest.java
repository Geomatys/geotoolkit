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

import org.geotoolkit.data.kml.model.ExtendedData;
import org.geotoolkit.data.kml.model.Kml;
import org.geotoolkit.data.kml.model.KmlException;
import org.geotoolkit.data.kml.model.KmlModelConstants;
import org.geotoolkit.data.kml.model.Metadata;
import org.geotoolkit.data.kml.xml.KmlWriter;
import org.geotoolkit.xml.DomCompare;

import org.junit.Test;

import org.opengis.feature.Feature;
import org.geotoolkit.data.kml.xml.KmlConstants;
import static org.junit.Assert.*;
import org.xml.sax.SAXException;
import static java.util.Collections.*;
import java.util.List;

/**
 *
 * @author Samuel Andrés
 * @module pending
 */
public class MetadataTest extends org.geotoolkit.test.TestBase {

    private static final String pathToTestFile = "src/test/resources/org/geotoolkit/data/kml/metadata.kml";

    @Test
    public void metadataReadTest() throws IOException, XMLStreamException, KmlException, URISyntaxException {
        final KmlReader reader = new KmlReader();
        reader.setInput(new File(pathToTestFile));
        final Kml kmlObjects = reader.read();
        reader.dispose();

        final Feature document = kmlObjects.getAbstractFeature();
        assertEquals(KmlModelConstants.TYPE_DOCUMENT, document.getType());

        assertEquals("Document.kml", document.getPropertyValue(KmlConstants.TAG_NAME));
        assertEquals(Boolean.TRUE, document.getPropertyValue(KmlConstants.TAG_OPEN));

        Iterator<?> i = ((Iterable<?>) document.getPropertyValue(KmlConstants.TAG_FEATURES)).iterator();
        assertTrue("Expected at least one element.", i.hasNext());
        final Feature placemark0 = (Feature) i.next();
        ExtendedData extendedData = (ExtendedData) ((List)placemark0.getPropertyValue(KmlConstants.TAG_EXTENDED_DATA)).get(0);
        assertEquals(EMPTY_LIST, extendedData.getDatas());
        assertEquals(EMPTY_LIST, extendedData.getSchemaData());
        assertEquals(EMPTY_LIST, extendedData.getAnyOtherElements());

        assertTrue("Expected at least 2 elements.", i.hasNext());
        final Feature placemark1 = (Feature) i.next();
        assertEquals(EMPTY_LIST, ((Metadata) ((List)placemark1.getPropertyValue(KmlConstants.TAG_EXTENDED_DATA)).get(0)).getContent());
        assertFalse("Expected exactly 2 elements.", i.hasNext());
    }

    @Test
    public void metadataWriteTest() throws KmlException, IOException, XMLStreamException, ParserConfigurationException, SAXException, URISyntaxException {
        final KmlFactory kmlFactory = DefaultKmlFactory.getInstance();

        final Feature placemark0 = kmlFactory.createPlacemark();
        final ExtendedData extendedData = kmlFactory.createExtendedData();
        placemark0.setPropertyValue(KmlConstants.TAG_EXTENDED_DATA, extendedData);

        final Feature placemark1 = kmlFactory.createPlacemark();
        final Metadata metadata = kmlFactory.createMetadata();
        placemark1.setPropertyValue(KmlConstants.TAG_EXTENDED_DATA, metadata);

        final Feature document = kmlFactory.createDocument();
        document.setPropertyValue(KmlConstants.TAG_FEATURES, Arrays.asList(placemark0,placemark1));
        document.setPropertyValue(KmlConstants.TAG_OPEN, Boolean.TRUE);
        document.setPropertyValue(KmlConstants.TAG_NAME, "Document.kml");

        final Kml kml = kmlFactory.createKml(null, document, null, null);

        final File temp = File.createTempFile("testMetadata", ".kml");
        temp.deleteOnExit();

        final KmlWriter writer = new KmlWriter();
        writer.setOutput(temp);
        writer.write(kml);
        writer.dispose();

        DomCompare.compare(new File(pathToTestFile), temp);
    }
}
