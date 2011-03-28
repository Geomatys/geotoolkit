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

import org.geotoolkit.feature.FeatureUtilities;
import java.net.URISyntaxException;
import org.geotoolkit.data.kml.xml.KmlReader;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
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
 * @module pending
 */
public class DataRW2Test {

    private static final double DELTA = 0.000000000001;
    private static final String pathToTestFile = "src/test/resources/org/geotoolkit/data/kml/dataRW2.kml";
    private static final FeatureFactory FF = FactoryFinder.getFeatureFactory(
            new Hints(Hints.FEATURE_FACTORY, LenientFeatureFactory.class));

    public DataRW2Test() {
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

        final DataReader dataReader = new DataReader();
        final KmlReader reader = new KmlReader();
        reader.setInput(new File(pathToTestFile));
        reader.addDataReader(dataReader);
        final Kml kmlObjects = reader.read();
        reader.dispose();

        final Feature document = kmlObjects.getAbstractFeature();
        assertTrue(document.getType().equals(KmlModelConstants.TYPE_DOCUMENT));

        assertEquals("Document.kml", document.getProperty(KmlModelConstants.ATT_NAME.getName()).getValue());
        assertTrue((Boolean) document.getProperty(KmlModelConstants.ATT_OPEN.getName()).getValue());

        assertEquals(2, document.getProperties(KmlModelConstants.ATT_DOCUMENT_FEATURES.getName()).size());

        Iterator i = document.getProperties(KmlModelConstants.ATT_DOCUMENT_FEATURES.getName()).iterator();

        if(i.hasNext()){
            Object object = i.next();

            final Feature placemark0 = (Feature) object;
            assertTrue(placemark0.getProperty(KmlModelConstants.ATT_EXTENDED_DATA.getName()).getValue() instanceof ExtendedData);
            ExtendedData extendedData = (ExtendedData) placemark0.getProperty(KmlModelConstants.ATT_EXTENDED_DATA.getName()).getValue();
            assertEquals(EMPTY_LIST, extendedData.getDatas());
            assertEquals(EMPTY_LIST, extendedData.getSchemaData());
            assertEquals(1, extendedData.getAnyOtherElements().size());

            List<String> racine1 =  (List<String>) extendedData.getAnyOtherElements().get(0);
            assertEquals(2, racine1.size());
            assertEquals("Je suis un element.",racine1.get(0));
            assertEquals("J'en suis un autre.",racine1.get(1));

        }

        if(i.hasNext()){
            Object object = i.next();

            final Feature placemark1 = (Feature) object;
            assertTrue(placemark1.getProperty(KmlModelConstants.ATT_EXTENDED_DATA.getName()).getValue() instanceof Metadata);
            Metadata metadata = (Metadata) placemark1.getProperty(KmlModelConstants.ATT_EXTENDED_DATA.getName()).getValue();
            assertEquals(1, metadata.getContent().size());

            List<String> racine2 =  (List<String>) metadata.getContent().get(0);
            assertEquals(2, racine2.size());
            assertEquals("Et moi aussi.",racine2.get(0));
            assertEquals("Sans commentaire.",racine2.get(1));
        }
    }

    @Test
    public void metadataWriteTest() throws KmlException, IOException, XMLStreamException, ParserConfigurationException, SAXException, URISyntaxException {
        final KmlFactory kmlFactory = DefaultKmlFactory.getInstance();

        final Feature placemark0 = kmlFactory.createPlacemark();
        final ExtendedData extendedData = kmlFactory.createExtendedData();
        final List<String> racine0 = new ArrayList<String>();
        racine0.add("Je suis un element.");
        racine0.add("J'en suis un autre.");
        extendedData.setAnyOtherElements(Arrays.asList((Object) racine0));
        placemark0.getProperties().add(FF.createAttribute(extendedData, KmlModelConstants.ATT_EXTENDED_DATA, null));

        final Feature placemark1 = kmlFactory.createPlacemark();
        final Metadata metadata = kmlFactory.createMetadata();
        final List<String> racine1 = new ArrayList<String>();
        racine1.add("Et moi aussi.");
        racine1.add("Sans commentaire.");
        metadata.setContent(Arrays.asList((Object) racine1));
        placemark1.getProperties().add(FF.createAttribute(metadata, KmlModelConstants.ATT_EXTENDED_DATA, null));

        final Feature document = kmlFactory.createDocument();
        final Collection<Property> documentProperties = document.getProperties();
        documentProperties.add(FeatureUtilities.wrapProperty(placemark0, KmlModelConstants.ATT_DOCUMENT_FEATURES));
        documentProperties.add(FeatureUtilities.wrapProperty(placemark1, KmlModelConstants.ATT_DOCUMENT_FEATURES));
        document.getProperty(KmlModelConstants.ATT_OPEN.getName()).setValue(Boolean.TRUE);
        documentProperties.add(FF.createAttribute("Document.kml", KmlModelConstants.ATT_NAME, null));

        final Kml kml = kmlFactory.createKml(null, document, null, null);
        kml.addExtensionUri("http://www.sandres.com", "sam");

        final File temp = File.createTempFile("testMetadata", ".kml");
        temp.deleteOnExit();

        final DataWriter dataWriter = new DataWriter();
        final KmlWriter writer = new KmlWriter();
        writer.setOutput(temp);
        writer.addDataWriter("http://www.sandres.com", dataWriter);
        writer.write(kml);
        writer.dispose();

        DomCompare.compare(
                new File(pathToTestFile), temp);

    }
}
