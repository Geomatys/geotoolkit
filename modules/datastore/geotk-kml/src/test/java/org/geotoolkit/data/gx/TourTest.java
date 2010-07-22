/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
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
package org.geotoolkit.data.gx;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Collection;
import java.util.Iterator;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.stream.XMLStreamException;
import org.geotoolkit.data.gx.model.GxModelConstants;
import org.geotoolkit.data.gx.model.PlayList;
import org.geotoolkit.data.gx.xml.GxConstants;
import org.geotoolkit.data.gx.xml.GxReader;
import org.geotoolkit.data.gx.xml.GxWriter;
import org.geotoolkit.data.kml.DefaultKmlFactory;
import org.geotoolkit.data.kml.KmlFactory;
import org.geotoolkit.data.kml.model.Extensions;
import org.geotoolkit.data.kml.model.Kml;
import org.geotoolkit.data.kml.model.KmlException;
import org.geotoolkit.data.kml.model.KmlModelConstants;
import org.geotoolkit.data.kml.xml.KmlReader;
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
import org.xml.sax.SAXException;
import static org.junit.Assert.*;

/**
 *
 * @author Samuel Andrés
 */
public class TourTest {

    private static final double DELTA = 0.000000000001;
    private static final String pathToTestFile = "src/test/resources/org/geotoolkit/data/gx/tour.kml";
    private static final FeatureFactory FF = FactoryFinder.getFeatureFactory(
            new Hints(Hints.FEATURE_FACTORY, LenientFeatureFactory.class));

    public TourTest() {
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
    public void tourReadTest() throws IOException, XMLStreamException, URISyntaxException, KmlException {

        Iterator i;

        final KmlReader reader = new KmlReader();
        final GxReader gxReader = new GxReader();
        reader.setInput(new File(pathToTestFile));
        reader.addExtensionReader(GxConstants.URI_GX, gxReader);
        final Kml kmlObjects = reader.read();
        reader.dispose();

        final Feature document = kmlObjects.getAbstractFeature();
        assertTrue(document.getType().equals(KmlModelConstants.TYPE_DOCUMENT));
        assertEquals("gx:AnimatedUpdate example", document.getProperty(KmlModelConstants.ATT_NAME.getName()).getValue());
        assertTrue((Boolean) document.getProperty(KmlModelConstants.ATT_OPEN.getName()).getValue());
        assertEquals(1, document.getProperties(KmlModelConstants.ATT_EXTENSIONS.getName()).size());

        Extensions extensions = (Extensions) document.getProperty(KmlModelConstants.ATT_EXTENSIONS.getName()).getValue();

        assertEquals(1, extensions.complexes(Extensions.Names.DOCUMENT).size());
        assertTrue(extensions.complexes(Extensions.Names.DOCUMENT).get(0) instanceof Feature);
        Feature tour = (Feature) extensions.complexes(Extensions.Names.DOCUMENT).get(0);
        assertTrue(tour.getType().equals(GxModelConstants.TYPE_TOUR));

        assertEquals("Play me!", tour.getProperty(KmlModelConstants.ATT_NAME.getName()).getValue());
        assertEquals(1,tour.getProperties(GxModelConstants.ATT_TOUR_PLAY_LIST.getName()).size());

        i = tour.getProperties(GxModelConstants.ATT_TOUR_PLAY_LIST.getName()).iterator();
    }

    @Test
    public void tourWriteTest() throws KmlException, IOException, XMLStreamException, ParserConfigurationException, SAXException, URISyntaxException {
        final KmlFactory kmlFactory = new DefaultKmlFactory();
        final GxFactory gxFactory = new DefaultGxFactory();

        final PlayList playList = gxFactory.createPlayList();
        final Feature tour = gxFactory.createTour();
        Collection<Property> tourProperties = tour.getProperties();
        tourProperties.add(FF.createAttribute("Play me!", KmlModelConstants.ATT_NAME, null));
        tourProperties.add(FF.createAttribute(playList, GxModelConstants.ATT_TOUR_PLAY_LIST, null));


        final Feature document = kmlFactory.createDocument();
        Collection<Property> documentProperties = document.getProperties();
        documentProperties.add(FF.createAttribute("gx:AnimatedUpdate example", KmlModelConstants.ATT_NAME, null));
        document.getProperty(KmlModelConstants.ATT_OPEN.getName()).setValue(Boolean.TRUE);
        Extensions extensions = (Extensions) document.getProperty(KmlModelConstants.ATT_EXTENSIONS.getName()).getValue();
        extensions.complexes(Extensions.Names.DOCUMENT).add(tour);
        final Kml kml = kmlFactory.createKml(null, document, null, null);
        kml.addExtensionUri(GxConstants.URI_GX, "gx");

        final File temp = File.createTempFile("testTour", ".kml");
        temp.deleteOnExit();

        final KmlWriter writer = new KmlWriter();
        final GxWriter gxWriter = new GxWriter();
        writer.setOutput(temp);
        writer.addExtensionWriter(GxConstants.URI_GX, gxWriter);
        writer.write(kml);
        writer.dispose();

        DomCompare.compare(
                new File(pathToTestFile), temp);
    }
}