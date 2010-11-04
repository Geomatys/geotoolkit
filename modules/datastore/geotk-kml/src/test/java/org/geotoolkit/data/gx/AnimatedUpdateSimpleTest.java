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
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.stream.XMLStreamException;

import org.geotoolkit.data.gx.model.AbstractTourPrimitive;
import org.geotoolkit.data.gx.model.AnimatedUpdate;
import org.geotoolkit.data.gx.model.GxModelConstants;
import org.geotoolkit.data.gx.model.PlayList;
import org.geotoolkit.data.gx.xml.GxConstants;
import org.geotoolkit.data.gx.xml.GxReader;
import org.geotoolkit.data.gx.xml.GxWriter;
import org.geotoolkit.data.kml.DefaultKmlFactory;
import org.geotoolkit.data.kml.KmlFactory;
import org.geotoolkit.data.kml.model.Change;
import org.geotoolkit.data.kml.model.IconStyle;
import org.geotoolkit.data.kml.model.IdAttributes;
import org.geotoolkit.data.kml.model.Kml;
import org.geotoolkit.data.kml.model.KmlException;
import org.geotoolkit.data.kml.model.KmlModelConstants;
import org.geotoolkit.data.kml.model.Update;
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
 * @module pending
 */
public class AnimatedUpdateSimpleTest {

    private static final double DELTA = 0.000000000001;
    private static final String pathToTestFile = "src/test/resources/org/geotoolkit/data/gx/animatedUpdate-simple.kml";
    private static final FeatureFactory FF = FactoryFinder.getFeatureFactory(
            new Hints(Hints.FEATURE_FACTORY, LenientFeatureFactory.class));

    public AnimatedUpdateSimpleTest() {
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
    public void animatedUpdateReadSimpleTest()
            throws IOException, XMLStreamException, URISyntaxException, KmlException {

        Iterator i;

        final KmlReader reader = new KmlReader();
        final GxReader gxReader = new GxReader(reader);
        reader.setInput(new File(pathToTestFile));
        reader.addExtensionReader(gxReader);
        final Kml kmlObjects = reader.read();
        reader.dispose();

        final Feature document = kmlObjects.getAbstractFeature();
        assertTrue(document.getType().equals(KmlModelConstants.TYPE_DOCUMENT));
        assertEquals("gx:AnimatedUpdate example", document.getProperty(KmlModelConstants.ATT_NAME.getName()).getValue());
        assertTrue((Boolean) document.getProperty(KmlModelConstants.ATT_OPEN.getName()).getValue());
        assertEquals(1, document.getProperties(KmlModelConstants.ATT_EXTENSIONS.getName()).size());

        assertTrue(document.getProperty(KmlModelConstants.ATT_DOCUMENT_FEATURES.getName()).getValue() instanceof Feature);
        final Feature tour = (Feature) document.getProperty(KmlModelConstants.ATT_DOCUMENT_FEATURES.getName()).getValue();
        assertTrue(tour.getType().equals(GxModelConstants.TYPE_TOUR));

        assertEquals("Play me!", tour.getProperty(KmlModelConstants.ATT_NAME.getName()).getValue());
        assertEquals(1,tour.getProperties(GxModelConstants.ATT_TOUR_PLAY_LIST.getName()).size());

        i = tour.getProperties(GxModelConstants.ATT_TOUR_PLAY_LIST.getName()).iterator();

        if(i.hasNext()){
            final Object object = ((Property) i.next()).getValue();
            assertTrue(object instanceof PlayList);
            final PlayList playList = (PlayList) object;
            assertEquals(1, playList.getTourPrimitives().size());
            assertTrue(playList.getTourPrimitives().get(0) instanceof AnimatedUpdate);
            final AnimatedUpdate animatedUpdate = (AnimatedUpdate) playList.getTourPrimitives().get(0);
            assertEquals(6.5, animatedUpdate.getDuration(), DELTA);
            assertTrue(animatedUpdate.getUpdate() instanceof Update);
            final Update update = animatedUpdate.getUpdate();
            assertEquals(new URI("http://moncoco.com"), update.getTargetHref());
            assertEquals(1, update.getUpdates().size());
            assertTrue(update.getUpdates().get(0) instanceof Change);
            final Change change = (Change) update.getUpdates().get(0);
            assertEquals(1, change.getObjects().size());
            assertTrue(change.getObjects().get(0) instanceof IconStyle);
            final IconStyle iconStyle = (IconStyle) change.getObjects().get(0);
            assertEquals("iconstyle1", iconStyle.getIdAttributes().getId());
            assertEquals(10.0, iconStyle.getScale(), DELTA);
        }
    }

    @Test
    public void animatedUpdateWriteSimpleTest() throws KmlException, IOException, XMLStreamException, ParserConfigurationException, SAXException, URISyntaxException {
        final GxFactory gxFactory = DefaultGxFactory.getInstance();
        final KmlFactory kmlFactory = DefaultKmlFactory.getInstance();

        final IdAttributes styleIdAttAtributes = kmlFactory.createIdAttributes("iconstyle1", null);

        final IconStyle iconStyle = kmlFactory.createIconStyle();
        iconStyle.setIdAttributes(styleIdAttAtributes);
        iconStyle.setScale(10.0);

        final Change change = kmlFactory.createChange();
        change.setObjects(Arrays.asList((Object) iconStyle));

        final Update update = kmlFactory.createUpdate();
        update.setTargetHref(new URI("http://moncoco.com"));
        update.setUpdates(Arrays.asList((Object) change));

        final AnimatedUpdate animatedUpdate = gxFactory.createAnimatedUpdate();
        animatedUpdate.setDuration(6.5);
        animatedUpdate.setUpdate(update);

        final PlayList playList = gxFactory.createPlayList();
        playList.setTourPrimitives(Arrays.asList((AbstractTourPrimitive) animatedUpdate));

        final Feature tour = gxFactory.createTour();
        Collection<Property> tourProperties = tour.getProperties();
        tourProperties.add(FF.createAttribute("Play me!", KmlModelConstants.ATT_NAME, null));
        tourProperties.add(FF.createAttribute(playList, GxModelConstants.ATT_TOUR_PLAY_LIST, null));

        final Feature document = kmlFactory.createDocument();
        Collection<Property> documentProperties = document.getProperties();
        documentProperties.add(FF.createAttribute("gx:AnimatedUpdate example", KmlModelConstants.ATT_NAME, null));
        document.getProperty(KmlModelConstants.ATT_OPEN.getName()).setValue(Boolean.TRUE);
        documentProperties.add(FF.createAttribute(tour, KmlModelConstants.ATT_DOCUMENT_FEATURES, null));

        final Kml kml = kmlFactory.createKml(null, document, null, null);
        kml.addExtensionUri(GxConstants.URI_GX, "gx");

        final File temp = File.createTempFile("testAnimatedUpdate", ".kml");
        temp.deleteOnExit();

        final KmlWriter writer = new KmlWriter();
        final GxWriter gxWriter = new GxWriter(writer);
        writer.setOutput(temp);
        writer.addExtensionWriter(GxConstants.URI_GX, gxWriter);
        writer.write(kml);
        writer.dispose();

        DomCompare.compare(
                new File(pathToTestFile), temp);
    }

}