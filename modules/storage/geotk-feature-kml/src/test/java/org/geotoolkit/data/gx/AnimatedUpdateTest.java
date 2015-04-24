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
import com.vividsolutions.jts.geom.CoordinateSequence;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.Iterator;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.stream.XMLStreamException;

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
import org.geotoolkit.data.kml.model.IconStyle;
import org.geotoolkit.data.kml.model.IdAttributes;
import org.geotoolkit.data.kml.model.Kml;
import org.geotoolkit.data.kml.model.KmlException;
import org.geotoolkit.data.kml.model.KmlModelConstants;
import org.geotoolkit.data.kml.model.Point;
import org.geotoolkit.data.kml.model.Style;
import org.geotoolkit.data.kml.model.Update;
import org.geotoolkit.data.kml.xml.KmlConstants;
import org.geotoolkit.data.kml.xml.KmlReader;
import org.geotoolkit.data.kml.xml.KmlWriter;
import org.geotoolkit.xml.DomCompare;

import org.junit.Test;

import org.opengis.feature.Feature;

import org.xml.sax.SAXException;
import static org.junit.Assert.*;

/**
 *
 * @author Samuel Andrés
 * @module pending
 */
public class AnimatedUpdateTest extends org.geotoolkit.test.TestBase {

    private static final double DELTA = 0.000000000001;
    private static final String pathToTestFile = "src/test/resources/org/geotoolkit/data/gx/animatedUpdate.kml";

    @Test
    public void animatedUpdateReadTest() throws IOException, XMLStreamException, URISyntaxException, KmlException {
        final Feature document;
        {
            final KmlReader reader = new KmlReader();
            final GxReader gxReader = new GxReader(reader);
            reader.setInput(new File(pathToTestFile));
            reader.addExtensionReader(gxReader);
            final Kml kmlObjects = reader.read();
            reader.dispose();
            document = kmlObjects.getAbstractFeature();
        }
        assertEquals(KmlModelConstants.TYPE_DOCUMENT, document.getType());
        assertEquals("gx:AnimatedUpdate example", document.getPropertyValue(KmlConstants.TAG_NAME));
        assertEquals(Boolean.FALSE, document.getPropertyValue(KmlConstants.TAG_OPEN));

        Iterator<?> i = ((Iterable<?>) document.getPropertyValue(KmlConstants.TAG_STYLE_SELECTOR)).iterator();
        assertTrue("Expected at least one element.", i.hasNext());
        {
            final Style style = (Style) i.next();
            assertEquals("pushpin", style.getIdAttributes().getId());
            assertEquals("mystyle", style.getIconStyle().getIdAttributes().getId());
            assertEquals("http://maps.google.com/mapfiles/kml/pushpin/ylw-pushpin.png",style.getIconStyle().getIcon().getHref());
            assertEquals(2.0, style.getIconStyle().getScale(), DELTA);
        }
        assertFalse("Expected exactly one element.", i.hasNext());

        i = ((Iterable<?>) document.getPropertyValue(KmlConstants.TAG_FEATURES)).iterator();
        assertTrue("Expected at least one element.", i.hasNext());
        {
            final Feature placemark = (Feature) i.next();
            assertEquals(placemark.getType(), KmlModelConstants.TYPE_PLACEMARK);
            assertEquals("mountainpin1", ((IdAttributes) placemark.getPropertyValue(KmlConstants.ATT_ID)).getId());
            assertEquals("Pin on a mountaintop", placemark.getPropertyValue(KmlConstants.TAG_NAME));
            assertEquals(new URI("#pushpin"), placemark.getPropertyValue(KmlConstants.TAG_STYLE_URL));
            Point point = (Point) placemark.getPropertyValue(KmlConstants.TAG_GEOMETRY);
            CoordinateSequence coordinates = point.getCoordinateSequence();
            assertEquals(1, coordinates.size());
            Coordinate coordinate = coordinates.getCoordinate(0);
            assertEquals(170.1435558771009, coordinate.x, DELTA);
            assertEquals(-43.60505741890396, coordinate.y, DELTA);
            assertEquals(0, coordinate.z, DELTA);
        }

        assertTrue("Expected at least 2 elements.", i.hasNext());
        final Feature tour = (Feature) i.next();
        assertEquals(GxModelConstants.TYPE_TOUR, tour.getType());
        assertEquals("Play me!", tour.getPropertyValue(KmlConstants.TAG_NAME));
        assertFalse("Expected exactly 2 elements.", i.hasNext());

        i = ((Iterable<?>) tour.getPropertyValue(KmlConstants.ATT_PLAYLIST)).iterator();
        assertTrue("Expected at least one element.", i.hasNext());
        {
            final PlayList playList = (PlayList) i.next();
            assertEquals(3, playList.getTourPrimitives().size());

            final FlyTo flyTo = (FlyTo) playList.getTourPrimitives().get(0);
            assertEquals(3, flyTo.getDuration(), DELTA);
            assertEquals(EnumFlyToMode.SMOOTH, flyTo.getFlyToMode());

            final Camera camera = (Camera) flyTo.getView();
            assertEquals(170.157, camera.getLongitude(), DELTA);
            assertEquals(-43.671, camera.getLatitude(), DELTA);
            assertEquals(9700, camera.getAltitude(), DELTA);
            assertEquals(-6.333, camera.getHeading(), DELTA);
            assertEquals(33.5, camera.getTilt(), DELTA);

            final AnimatedUpdate animatedUpdate = (AnimatedUpdate) playList.getTourPrimitives().get(1);
            assertEquals(5, animatedUpdate.getDuration(), DELTA);
            final Update update = animatedUpdate.getUpdate();
            assertEquals(new URI("http://moncoco.com"), update.getTargetHref());
            assertEquals(1, update.getUpdates().size());
            final Change change = (Change) update.getUpdates().get(0);
            assertEquals(1, change.getObjects().size());
            final IconStyle iconStyle = (IconStyle) change.getObjects().get(0);
            assertEquals("mystyle", iconStyle.getIdAttributes().getTargetId());
            assertEquals(10.0, iconStyle.getScale(), DELTA);

            final Wait wait = (Wait) playList.getTourPrimitives().get(2);
            assertEquals(5, wait.getDuration(), DELTA);
        }
        assertFalse("Expected exactly one element.", i.hasNext());
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
        playList.setTourPrimitives(Arrays.asList(flyTo, animatedUpdate, wait));

        final Feature tour = gxFactory.createTour();
        tour.setPropertyValue(KmlConstants.TAG_NAME, "Play me!");
        tour.setPropertyValue(KmlConstants.ATT_PLAYLIST, playList);

        final Coordinate coordinate = kmlFactory.createCoordinate("170.1435558771009,-43.60505741890396,0.0");
        final CoordinateSequence coordinates = kmlFactory.createCoordinates(Arrays.asList(coordinate));
        final Point point = kmlFactory.createPoint(coordinates);
        final Feature placemark = kmlFactory.createPlacemark();
        IdAttributes placemarkIdAttributes = kmlFactory.createIdAttributes("mountainpin1", null);
        placemark.setPropertyValue(KmlConstants.ATT_ID, placemarkIdAttributes);
        placemark.setPropertyValue(KmlConstants.TAG_NAME, "Pin on a mountaintop");
        placemark.setPropertyValue(KmlConstants.TAG_STYLE_URL, new URI("#pushpin"));
        placemark.setPropertyValue(KmlConstants.TAG_GEOMETRY, point);

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
        document.setPropertyValue(KmlConstants.TAG_NAME, "gx:AnimatedUpdate example");
        document.setPropertyValue(KmlConstants.TAG_STYLE_SELECTOR, style);
        document.setPropertyValue(KmlConstants.TAG_FEATURES, Arrays.asList(placemark,tour));
        document.setPropertyValue(KmlConstants.TAG_OPEN, Boolean.FALSE);

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

        DomCompare.compare(new File(pathToTestFile), temp);
    }
}
