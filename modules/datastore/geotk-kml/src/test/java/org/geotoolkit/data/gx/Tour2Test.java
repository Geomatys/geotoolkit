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
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.stream.XMLStreamException;
import org.geotoolkit.data.gx.model.AbstractTourPrimitive;
import org.geotoolkit.data.gx.model.EnumFlyToMode;
import org.geotoolkit.data.gx.model.EnumPlayMode;
import org.geotoolkit.data.gx.model.FlyTo;
import org.geotoolkit.data.gx.model.GxModelConstants;
import org.geotoolkit.data.gx.model.PlayList;
import org.geotoolkit.data.gx.model.SoundCue;
import org.geotoolkit.data.gx.model.TourControl;
import org.geotoolkit.data.gx.model.Wait;
import org.geotoolkit.data.gx.xml.GxConstants;
import org.geotoolkit.data.gx.xml.GxReader;
import org.geotoolkit.data.gx.xml.GxWriter;
import org.geotoolkit.data.kml.DefaultKmlFactory;
import org.geotoolkit.data.kml.KmlFactory;
import org.geotoolkit.data.kml.model.EnumAltitudeMode;
import org.geotoolkit.data.kml.model.Kml;
import org.geotoolkit.data.kml.model.KmlException;
import org.geotoolkit.data.kml.model.LookAt;
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
public class Tour2Test {

    private static final double DELTA = 0.000000000001;
    private static final String pathToTestFile = "src/test/resources/org/geotoolkit/data/gx/tour2.kml";
    private static final FeatureFactory FF = FactoryFinder.getFeatureFactory(
            new Hints(Hints.FEATURE_FACTORY, LenientFeatureFactory.class));

    public Tour2Test() {
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
    public void tour2ReadTest() throws IOException, XMLStreamException, URISyntaxException, KmlException {

        Iterator i;

        final KmlReader reader = new KmlReader();
        final GxReader gxReader = new GxReader(reader);
        reader.setInput(new File(pathToTestFile));
        reader.addExtensionReader(gxReader);
        final Kml kmlObjects = reader.read();
        reader.dispose();

        final Feature tour = kmlObjects.getAbstractFeature();
        assertTrue(tour.getType().equals(GxModelConstants.TYPE_TOUR));
        assertTrue(tour.getProperty(GxModelConstants.ATT_TOUR_PLAY_LIST.getName()).getValue() instanceof PlayList);
        final PlayList playList = (PlayList) tour.getProperty(GxModelConstants.ATT_TOUR_PLAY_LIST.getName()).getValue();
        assertEquals(4, playList.getTourPrimitives().size());

        assertTrue(playList.getTourPrimitives().get(0) instanceof FlyTo);
        final FlyTo flyTo = (FlyTo) playList.getTourPrimitives().get(0);
        assertEquals(5, flyTo.getDuration(), DELTA);
        assertEquals(EnumFlyToMode.SMOOTH, flyTo.getFlyToMode());
        assertTrue(flyTo.getView() instanceof LookAt);
        final LookAt lookAt = (LookAt) flyTo.getView();
        assertEquals(-79.387, lookAt.getLongitude(), DELTA);
        assertEquals(43.643, lookAt.getLatitude(), DELTA);
        assertEquals(10, lookAt.getAltitude(), DELTA);
        assertEquals(-172.3, lookAt.getHeading(), DELTA);
        assertEquals(10, lookAt.getTilt(), DELTA);
        assertEquals(1200, lookAt.getRange(), DELTA);
        assertEquals(EnumAltitudeMode.RELATIVE_TO_GROUND, lookAt.getAltitudeMode());

        assertTrue(playList.getTourPrimitives().get(1) instanceof TourControl);
        final TourControl tourControl = (TourControl) playList.getTourPrimitives().get(1);
        assertEquals(EnumPlayMode.PAUSE, tourControl.getPlayMode());

        assertTrue(playList.getTourPrimitives().get(2) instanceof SoundCue);
        final SoundCue soundCue = (SoundCue) playList.getTourPrimitives().get(2);
        assertEquals("http://dev.keyhole.com/codesite/cntowerfacts.mp3", soundCue.getHref());

        assertTrue(playList.getTourPrimitives().get(3) instanceof Wait);
        final Wait wait = (Wait) playList.getTourPrimitives().get(3);
        assertEquals(10, wait.getDuration(), DELTA);

    }

    @Test
    public void tour2WriteTest() throws KmlException, IOException, XMLStreamException, ParserConfigurationException, SAXException, URISyntaxException {
        final GxFactory gxFactory = DefaultGxFactory.getInstance();
        final KmlFactory kmlFactory = DefaultKmlFactory.getInstance();

        final FlyTo flyTo = gxFactory.createFlyTo();
        flyTo.setDuration(5);
        flyTo.setFlyToMode(EnumFlyToMode.SMOOTH);
        final LookAt lookAt = kmlFactory.createLookAt();
        lookAt.setLongitude(-79.387);
        lookAt.setLatitude(43.643);
        lookAt.setAltitude(10);
        lookAt.setHeading(-172.3);
        lookAt.setTilt(10);
        lookAt.setRange(1200);
        lookAt.setAltitudeMode(EnumAltitudeMode.RELATIVE_TO_GROUND);
        flyTo.setView(lookAt);

        final TourControl tourControl = gxFactory.createTourControl();
        tourControl.setPlayMode(EnumPlayMode.PAUSE);

        final SoundCue soundCue = gxFactory.createSoundCue();
        soundCue.setHref("http://dev.keyhole.com/codesite/cntowerfacts.mp3");

        final Wait wait = gxFactory.createWait();
        wait.setDuration(10);


        final PlayList playList = gxFactory.createPlayList();
        playList.setTourPrimitives(Arrays.asList((AbstractTourPrimitive) flyTo, tourControl, soundCue, wait));

        final Feature tour = gxFactory.createTour();
        Collection<Property> tourProperties = tour.getProperties();
        tourProperties.add(FF.createAttribute(playList, GxModelConstants.ATT_TOUR_PLAY_LIST, null));

        final Kml kml = kmlFactory.createKml(null, tour, null, null);
        kml.addExtensionUri(GxConstants.URI_GX, "gx");

        final File temp = File.createTempFile("testTour2", ".kml");
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