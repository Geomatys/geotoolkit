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

import com.vividsolutions.jts.geom.Coordinate;
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
import org.geotoolkit.data.gx.model.EnumFlyToMode;
import org.geotoolkit.data.gx.model.FlyTo;
import org.geotoolkit.data.gx.model.GxModelConstants;
import org.geotoolkit.data.gx.model.PlayList;
import org.geotoolkit.data.gx.model.Wait;
import org.geotoolkit.data.gx.xml.GxConstants;
import org.geotoolkit.data.gx.xml.GxReader;
import org.geotoolkit.data.gx.xml.GxWriter;
import org.geotoolkit.data.kml.DefaultKmlFactory;
import org.geotoolkit.data.kml.KmlFactory;
import org.geotoolkit.data.kml.model.BasicLink;
import org.geotoolkit.data.kml.model.Camera;
import org.geotoolkit.data.kml.model.Change;
import org.geotoolkit.data.kml.model.Coordinates;
import org.geotoolkit.data.kml.model.Extensions;
import org.geotoolkit.data.kml.model.IconStyle;
import org.geotoolkit.data.kml.model.IdAttributes;
import org.geotoolkit.data.kml.model.Kml;
import org.geotoolkit.data.kml.model.KmlException;
import org.geotoolkit.data.kml.model.KmlModelConstants;
import org.geotoolkit.data.kml.model.Point;
import org.geotoolkit.data.kml.model.Style;
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
 */
public class AnimatedUpdateTest {

    private static final double DELTA = 0.000000000001;
    private static final String pathToTestFile = "src/test/resources/org/geotoolkit/data/gx/animatedUpdate.kml";
    private static final FeatureFactory FF = FactoryFinder.getFeatureFactory(
            new Hints(Hints.FEATURE_FACTORY, LenientFeatureFactory.class));

    public AnimatedUpdateTest() {
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
    public void animatedUpdateReadTest()
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
        assertFalse((Boolean) document.getProperty(KmlModelConstants.ATT_OPEN.getName()).getValue());

        assertEquals(1, document.getProperties(KmlModelConstants.ATT_STYLE_SELECTOR.getName()).size());

        i = document.getProperties(KmlModelConstants.ATT_STYLE_SELECTOR.getName()).iterator();

        if(i.hasNext()){
            Object object = ((Property) i.next()).getValue();
            assertTrue(object instanceof Style);
            final Style style = (Style) object;
            assertEquals("pushpin", style.getIdAttributes().getId());

            assertEquals("mystyle", style.getIconStyle().getIdAttributes().getId());
            assertEquals("http://maps.google.com/mapfiles/kml/pushpin/ylw-pushpin.png",style.getIconStyle().getIcon().getHref());
            assertEquals(2.0, style.getIconStyle().getScale(), DELTA);
        }

        assertEquals(1, document.getProperties(KmlModelConstants.ATT_DOCUMENT_FEATURES.getName()).size());

        i = document.getProperties(KmlModelConstants.ATT_DOCUMENT_FEATURES.getName()).iterator();

        if(i.hasNext()){
            Object object  = ((Property) i.next()).getValue();
            assertTrue(object instanceof Feature);
            Feature placemark = (Feature) object;
            assertEquals(placemark.getType(), KmlModelConstants.TYPE_PLACEMARK);
            assertEquals("mountainpin1", ((IdAttributes) placemark.getProperty(KmlModelConstants.ATT_ID_ATTRIBUTES.getName()).getValue()).getId());
            assertEquals("Pin on a mountaintop", placemark.getProperty(KmlModelConstants.ATT_NAME.getName()).getValue());
            assertEquals(new URI("#pushpin"), placemark.getProperty(KmlModelConstants.ATT_STYLE_URL.getName()).getValue());

            assertTrue(placemark.getProperty(KmlModelConstants.ATT_PLACEMARK_GEOMETRY.getName()).getValue() instanceof Point);

            Point point = (Point) placemark.getProperty(KmlModelConstants.ATT_PLACEMARK_GEOMETRY.getName()).getValue();
            Coordinates coordinates = point.getCoordinateSequence();
            assertEquals(1, coordinates.size());
            Coordinate coordinate = coordinates.getCoordinate(0);
            assertEquals(170.1435558771009, coordinate.x, DELTA);
            assertEquals(-43.60505741890396, coordinate.y, DELTA);
            assertEquals(0, coordinate.z, DELTA);




        }

        assertEquals(1, document.getProperties(KmlModelConstants.ATT_EXTENSIONS.getName()).size());

        final Extensions extensions = (Extensions) document.getProperty(KmlModelConstants.ATT_EXTENSIONS.getName()).getValue();

        assertEquals(1, extensions.complexes(Extensions.Names.DOCUMENT).size());
        assertTrue(extensions.complexes(Extensions.Names.DOCUMENT).get(0) instanceof Feature);
        final Feature tour = (Feature) extensions.complexes(Extensions.Names.DOCUMENT).get(0);
        assertTrue(tour.getType().equals(GxModelConstants.TYPE_TOUR));

        assertEquals("Play me!", tour.getProperty(KmlModelConstants.ATT_NAME.getName()).getValue());
        assertEquals(1,tour.getProperties(GxModelConstants.ATT_TOUR_PLAY_LIST.getName()).size());

        i = tour.getProperties(GxModelConstants.ATT_TOUR_PLAY_LIST.getName()).iterator();

        if(i.hasNext()){
            final Object object = ((Property) i.next()).getValue();
            assertTrue(object instanceof PlayList);
            final PlayList playList = (PlayList) object;
            assertEquals(3, playList.getTourPrimitives().size());

            assertTrue(playList.getTourPrimitives().get(0) instanceof FlyTo);
            final FlyTo flyTo = (FlyTo) playList.getTourPrimitives().get(0);
            assertEquals(3, flyTo.getDuration(), DELTA);
            assertEquals(EnumFlyToMode.SMOOTH, flyTo.getFlyToMode());

            System.out.println(flyTo.getView().getClass());
            assertTrue(flyTo.getView() instanceof Camera);
            final Camera camera = (Camera) flyTo.getView();
            assertEquals(170.157, camera.getLongitude(), DELTA);
            assertEquals(-43.671, camera.getLatitude(), DELTA);
            assertEquals(9700, camera.getAltitude(), DELTA);
            assertEquals(-6.333, camera.getHeading(), DELTA);
            assertEquals(33.5, camera.getTilt(), DELTA);


            assertTrue(playList.getTourPrimitives().get(1) instanceof AnimatedUpdate);
            final AnimatedUpdate animatedUpdate = (AnimatedUpdate) playList.getTourPrimitives().get(1);
            assertEquals(5, animatedUpdate.getDuration(), DELTA);
            assertTrue(animatedUpdate.getUpdate() instanceof Update);
            final Update update = animatedUpdate.getUpdate();
            assertEquals(new URI("http://moncoco.com"), update.getTargetHref());
            assertEquals(1, update.getUpdates().size());
            assertTrue(update.getUpdates().get(0) instanceof Change);
            final Change change = (Change) update.getUpdates().get(0);
            assertEquals(1, change.getObjects().size());
            assertTrue(change.getObjects().get(0) instanceof IconStyle);
            final IconStyle iconStyle = (IconStyle) change.getObjects().get(0);
            assertEquals("mystyle", iconStyle.getIdAttributes().getTargetId());
            assertEquals(10.0, iconStyle.getScale(), DELTA);

            assertTrue(playList.getTourPrimitives().get(2) instanceof Wait);
            final Wait wait = (Wait) playList.getTourPrimitives().get(2);
            assertEquals(5, wait.getDuration(), DELTA);
        }
    }

    @Test
    public void animatedUpdateWriteTest() throws KmlException, IOException, XMLStreamException, ParserConfigurationException, SAXException, URISyntaxException {
        final GxFactory gxFactory = DefaultGxFactory.getInstance();
        final KmlFactory kmlFactory = DefaultKmlFactory.getInstance();

        final Camera camera = kmlFactory.createCamera();
        camera.setLongitude(170.157);
        camera.setLatitude(-43.671);
        camera.setAltitude(9700);
        camera.setHeading(-6.333);
        camera.setTilt(33.5);

        final FlyTo flyTo = gxFactory.createFlyTo();
        flyTo.setDuration(3);
        flyTo.setFlyToMode(EnumFlyToMode.SMOOTH);
        flyTo.setView(camera);

        final IdAttributes style1IdAttAtributes = kmlFactory.createIdAttributes(null,"mystyle");

        final IconStyle iconStyle1 = kmlFactory.createIconStyle();
        iconStyle1.setIdAttributes(style1IdAttAtributes);
        iconStyle1.setScale(10.0);

        final Change change = kmlFactory.createChange();
        change.setObjects(Arrays.asList((Object) iconStyle1));

        final Update update = kmlFactory.createUpdate();
        update.setTargetHref(new URI("http://moncoco.com"));
        update.setUpdates(Arrays.asList((Object) change));

        final AnimatedUpdate animatedUpdate = gxFactory.createAnimatedUpdate();
        animatedUpdate.setDuration(5);
        animatedUpdate.setUpdate(update);

        final Wait wait = gxFactory.createWait();
        wait.setDuration(5);

        final PlayList playList = gxFactory.createPlayList();
        playList.setTourPrimitives(Arrays.asList((AbstractTourPrimitive) flyTo, animatedUpdate, wait));

        final Feature tour = gxFactory.createTour();
        Collection<Property> tourProperties = tour.getProperties();
        tourProperties.add(FF.createAttribute("Play me!", KmlModelConstants.ATT_NAME, null));
        tourProperties.add(FF.createAttribute(playList, GxModelConstants.ATT_TOUR_PLAY_LIST, null));

        final Coordinate coordinate = kmlFactory.createCoordinate("170.1435558771009,-43.60505741890396,0.0");
        final Coordinates coordinates = kmlFactory.createCoordinates(Arrays.asList(coordinate));
        final Point point = kmlFactory.createPoint(coordinates);
        final Feature placemark = kmlFactory.createPlacemark();
        Collection<Property> placemarkProperties = placemark.getProperties();
        IdAttributes placemarkIdAttributes = kmlFactory.createIdAttributes("mountainpin1", null);
        placemarkProperties.add(FF.createAttribute(placemarkIdAttributes, KmlModelConstants.ATT_ID_ATTRIBUTES, null));
        placemarkProperties.add(FF.createAttribute("Pin on a mountaintop", KmlModelConstants.ATT_NAME, null));
        placemarkProperties.add(FF.createAttribute(new URI("#pushpin"), KmlModelConstants.ATT_STYLE_URL, null));
        placemarkProperties.add(FF.createAttribute(point, KmlModelConstants.ATT_PLACEMARK_GEOMETRY, null));

        final BasicLink icon = kmlFactory.createBasicLink();
        icon.setHref("http://maps.google.com/mapfiles/kml/pushpin/ylw-pushpin.png");

        final IconStyle iconStyle2 = kmlFactory.createIconStyle();
        final IdAttributes style2IdAttributes = kmlFactory.createIdAttributes("mystyle", null);
        iconStyle2.setScale(2);
        iconStyle2.setIcon(icon);
        iconStyle2.setIdAttributes(style2IdAttributes);

        final Style style = kmlFactory.createStyle();
        style.setIconStyle(iconStyle2);
        final IdAttributes styleIdAttributes = kmlFactory.createIdAttributes("pushpin", null);
        style.setIdAttributes(styleIdAttributes);


        final Feature document = kmlFactory.createDocument();
        Collection<Property> documentProperties = document.getProperties();
        documentProperties.add(FF.createAttribute("gx:AnimatedUpdate example", KmlModelConstants.ATT_NAME, null));
        documentProperties.add(FF.createAttribute(style, KmlModelConstants.ATT_STYLE_SELECTOR, null));
        documentProperties.add(FF.createAttribute(placemark, KmlModelConstants.ATT_DOCUMENT_FEATURES, null));
        document.getProperty(KmlModelConstants.ATT_OPEN.getName()).setValue(Boolean.FALSE);
        ((Extensions) document.getProperty(KmlModelConstants.ATT_EXTENSIONS.getName()).getValue()).
                complexes(Extensions.Names.DOCUMENT).add(tour);

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