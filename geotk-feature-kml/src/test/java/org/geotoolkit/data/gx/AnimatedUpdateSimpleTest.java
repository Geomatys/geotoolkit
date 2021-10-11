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
import org.geotoolkit.xml.DomCompare;

import org.junit.Test;

import org.opengis.feature.Feature;

import org.geotoolkit.data.kml.xml.KmlConstants;
import org.xml.sax.SAXException;
import static org.junit.Assert.*;

/**
 *
 * @author Samuel Andrés
 * @module
 */
public class AnimatedUpdateSimpleTest extends org.geotoolkit.test.TestBase {

    private static final double DELTA = 0.000000000001;
    private static final String pathToTestFile = "src/test/resources/org/geotoolkit/data/gx/animatedUpdate-simple.kml";

    @Test
    public void animatedUpdateReadSimpleTest() throws IOException, XMLStreamException, URISyntaxException, KmlException {
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
        assertEquals(Boolean.TRUE, document.getPropertyValue(KmlConstants.TAG_OPEN));

        final Feature tour = (Feature) document.getProperty(KmlConstants.TAG_FEATURES).getValue();
        assertEquals(GxModelConstants.TYPE_TOUR, tour.getType());
        assertEquals("Play me!", tour.getPropertyValue(KmlConstants.TAG_NAME));
        Iterator<?> i = ((Iterable<?>) tour.getPropertyValue(KmlConstants.ATT_PLAYLIST)).iterator();
        assertTrue("Expected at least one element.", i.hasNext());

        final PlayList playList = (PlayList) i.next();
        assertEquals(1, playList.getTourPrimitives().size());
        final AnimatedUpdate animatedUpdate = (AnimatedUpdate) playList.getTourPrimitives().get(0);
        assertEquals(6.5, animatedUpdate.getDuration(), DELTA);
        final Update update = animatedUpdate.getUpdate();
        assertEquals(new URI("http://moncoco.com"), update.getTargetHref());
        assertEquals(1, update.getUpdates().size());
        final Change change = (Change) update.getUpdates().get(0);
        assertEquals(1, change.getObjects().size());
        final IconStyle iconStyle = (IconStyle) change.getObjects().get(0);
        assertEquals("iconstyle1", iconStyle.getIdAttributes().getId());
        assertEquals(10.0, iconStyle.getScale(), DELTA);

        assertFalse("Expected exactly one element.", i.hasNext());
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
        tour.setPropertyValue(KmlConstants.TAG_NAME, "Play me!");
        tour.setPropertyValue(KmlConstants.ATT_PLAYLIST, playList);

        final Feature document = kmlFactory.createDocument();
        document.setPropertyValue(KmlConstants.TAG_NAME, "gx:AnimatedUpdate example");
        document.setPropertyValue(KmlConstants.TAG_OPEN, Boolean.TRUE);
        document.setPropertyValue(KmlConstants.TAG_FEATURES, tour);

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
