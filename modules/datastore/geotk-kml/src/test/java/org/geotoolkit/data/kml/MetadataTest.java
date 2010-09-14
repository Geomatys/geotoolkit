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
import java.util.Collection;
import java.util.Iterator;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.stream.XMLStreamException;
import org.geotoolkit.data.kml.model.ExtendedData;
import org.geotoolkit.data.kml.model.Kml;
import org.geotoolkit.data.kml.model.KmlException;
import org.geotoolkit.data.kml.model.KmlModelConstants;
import org.geotoolkit.data.kml.model.Metadata;
import org.geotoolkit.data.kml.xml.KmlWriter;
import org.geotoolkit.factory.FactoryFinder;
import org.geotoolkit.factory.Hints;
import org.geotoolkit.feature.LenientFeatureFactory;
import org.geotoolkit.xml.DomCompare;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.opengis.feature.Feature;
import org.opengis.feature.FeatureFactory;
import org.opengis.feature.Property;
import static org.junit.Assert.*;
import org.xml.sax.SAXException;
import static java.util.Collections.*;

/**
 *
 * @author Samuel Andrés
 */
public class MetadataTest {

    private static final double DELTA = 0.000000000001;
    private static final String pathToTestFile = "src/test/resources/org/geotoolkit/data/kml/metadata.kml";
    private static final FeatureFactory FF = FactoryFinder.getFeatureFactory(
            new Hints(Hints.FEATURE_FACTORY, LenientFeatureFactory.class));

    public MetadataTest() {
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
    public void metadataReadTest() throws IOException, XMLStreamException, KmlException, URISyntaxException {

        final KmlReader reader = new KmlReader();
        reader.setInput(new File(pathToTestFile));
        final Kml kmlObjects = reader.read();
        reader.dispose();

        final Feature document = kmlObjects.getAbstractFeature();
        assertTrue(document.getType().equals(KmlModelConstants.TYPE_DOCUMENT));

        assertEquals("Document.kml", document.getProperty(KmlModelConstants.ATT_NAME.getName()).getValue());
        assertTrue((Boolean) document.getProperty(KmlModelConstants.ATT_OPEN.getName()).getValue());

        assertEquals(2, document.getProperties(KmlModelConstants.ATT_DOCUMENT_FEATURES.getName()).size());

        Iterator i = document.getProperties(KmlModelConstants.ATT_DOCUMENT_FEATURES.getName()).iterator();

        if(i.hasNext()){
            Object object = ((Property) i.next()).getValue();

            final Feature placemark0 = (Feature) object;
            assertTrue(placemark0.getProperty(KmlModelConstants.ATT_EXTENDED_DATA.getName()).getValue() instanceof ExtendedData);
            ExtendedData extendedData = (ExtendedData) placemark0.getProperty(KmlModelConstants.ATT_EXTENDED_DATA.getName()).getValue();
            assertEquals(EMPTY_LIST, extendedData.getDatas());
            assertEquals(EMPTY_LIST, extendedData.getSchemaData());
            assertEquals(EMPTY_LIST, extendedData.getAnyOtherElements());

        }

        if(i.hasNext()){
            Object object = ((Property) i.next()).getValue();

            final Feature placemark1 = (Feature) object;
            assertTrue(placemark1.getProperty(KmlModelConstants.ATT_EXTENDED_DATA.getName()).getValue() instanceof Metadata);
            assertEquals(EMPTY_LIST, ((Metadata) placemark1.getProperty(KmlModelConstants.ATT_EXTENDED_DATA.getName()).getValue()).getContent());

        }
    }

    @Test
    public void metadataWriteTest() throws KmlException, IOException, XMLStreamException, ParserConfigurationException, SAXException, URISyntaxException {
        final KmlFactory kmlFactory = DefaultKmlFactory.getInstance();

        final Feature placemark0 = kmlFactory.createPlacemark();
        final ExtendedData extendedData = kmlFactory.createExtendedData();
        placemark0.getProperties().add(FF.createAttribute(extendedData, KmlModelConstants.ATT_EXTENDED_DATA, null));

        final Feature placemark1 = kmlFactory.createPlacemark();
        final Metadata metadata = kmlFactory.createMetadata();
        placemark1.getProperties().add(FF.createAttribute(metadata, KmlModelConstants.ATT_EXTENDED_DATA, null));

        final Feature document = kmlFactory.createDocument();
        final Collection<Property> documentProperties = document.getProperties();
        documentProperties.add(FF.createAttribute(placemark0, KmlModelConstants.ATT_DOCUMENT_FEATURES, null));
        documentProperties.add(FF.createAttribute(placemark1, KmlModelConstants.ATT_DOCUMENT_FEATURES, null));
        document.getProperty(KmlModelConstants.ATT_OPEN.getName()).setValue(Boolean.TRUE);
        documentProperties.add(FF.createAttribute("Document.kml", KmlModelConstants.ATT_NAME, null));

        final Kml kml = kmlFactory.createKml(null, document, null, null);

        final File temp = File.createTempFile("testMetadata", ".kml");
        temp.deleteOnExit();

        final KmlWriter writer = new KmlWriter();
        writer.setOutput(temp);
        writer.write(kml);
        writer.dispose();

        DomCompare.compare(
                new File(pathToTestFile), temp);

    }
}
