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

import java.net.URISyntaxException;
import org.geotoolkit.data.kml.*;
import java.awt.Color;
import java.io.File;
import java.io.IOException;
import java.util.Collection;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.stream.XMLStreamException;

import org.geotoolkit.data.gx.xml.GxConstants;
import org.geotoolkit.data.gx.xml.GxReader;
import org.geotoolkit.data.gx.xml.GxWriter;
import org.geotoolkit.data.kml.model.Extensions;
import org.geotoolkit.data.kml.model.Extensions.Names;
import org.geotoolkit.data.kml.model.Icon;
import org.geotoolkit.data.kml.model.Kml;
import org.geotoolkit.data.kml.model.KmlException;
import org.geotoolkit.data.kml.model.KmlModelConstants;
import org.geotoolkit.data.kml.model.LatLonBox;
import org.geotoolkit.data.kml.model.Link;
import org.geotoolkit.data.kml.model.RefreshMode;
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
public class IconExtTest {

    private static final double DELTA = 0.000000000001;
    private static final String pathToTestFile = "src/test/resources/org/geotoolkit/data/gx/iconExt.kml";
    private static final FeatureFactory FF = FactoryFinder.getFeatureFactory(
            new Hints(Hints.FEATURE_FACTORY, LenientFeatureFactory.class));

    public IconExtTest() {
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
    public void iconExtReadTest() throws IOException, XMLStreamException, KmlException, URISyntaxException {

        final KmlReader reader = new KmlReader();
        final GxReader gxReader = new GxReader(reader);
        reader.setInput(new File(pathToTestFile));
        reader.addExtensionReader(gxReader);
        final Kml kmlObjects = reader.read();
        reader.dispose();

        final Feature groundOverlay = kmlObjects.getAbstractFeature();
        assertTrue(groundOverlay.getType().equals(KmlModelConstants.TYPE_GROUND_OVERLAY));
        assertEquals("GroundOverlay.kml", groundOverlay.getProperty(KmlModelConstants.ATT_NAME.getName()).getValue());
        assertEquals("7fffffff",KmlUtilities.toKmlColor((Color) groundOverlay.getProperty(KmlModelConstants.ATT_OVERLAY_COLOR.getName()).getValue()));
        assertEquals(1,groundOverlay.getProperty(KmlModelConstants.ATT_OVERLAY_DRAW_ORDER.getName()).getValue());
        final Icon icon = (Icon) groundOverlay.getProperty(KmlModelConstants.ATT_OVERLAY_ICON.getName()).getValue();
            assertEquals("http://www.google.com/intl/en/images/logo.gif",icon.getHref());
            assertEquals(RefreshMode.ON_INTERVAL, icon.getRefreshMode());
            assertEquals(86400, icon.getRefreshInterval(),DELTA);
            assertEquals(0.75, icon.getViewBoundScale(),DELTA);

        final Extensions extensions = icon.extensions();
        assertEquals(4, extensions.simples(Names.BASIC_LINK).size());
        int x = (Integer) extensions.simples(Names.BASIC_LINK).get(0).getValue();
        int y = (Integer) extensions.simples(Names.BASIC_LINK).get(1).getValue();
        int w = (Integer) extensions.simples(Names.BASIC_LINK).get(2).getValue();
        int h = (Integer) extensions.simples(Names.BASIC_LINK).get(3).getValue();
        assertEquals(2, x);
        assertEquals(8, y);
        assertEquals(1000, w);
        assertEquals(87, h);

        final LatLonBox latLonBox = (LatLonBox) groundOverlay.getProperty(KmlModelConstants.ATT_GROUND_OVERLAY_LAT_LON_BOX.getName()).getValue();
            assertEquals(37.83234, latLonBox.getNorth(), DELTA);
            assertEquals(37.832122, latLonBox.getSouth(), DELTA);
            assertEquals(-122.373033, latLonBox.getEast(), DELTA);
            assertEquals(-122.373724, latLonBox.getWest(), DELTA);
            assertEquals(45, latLonBox.getRotation(), DELTA);

    }

    @Test
    public void iconExtWriteTest() throws KmlException, IOException, XMLStreamException, ParserConfigurationException, SAXException{
        final KmlFactory kmlFactory = DefaultKmlFactory.getInstance();

        final double north = 37.83234;
        final double south = 37.832122;
        final double east = -122.373033;
        final double west = -122.373724;
        final double rotation = 45;

        final LatLonBox latLonBox = kmlFactory.createLatLonBox(null, null, north, south, east, west, null, null, rotation, null, null);

        final String href = "http://www.google.com/intl/en/images/logo.gif";
        final RefreshMode refreshMode = RefreshMode.ON_INTERVAL;
        final double refreshInterval = 86400;
        final double viewBoundScale = 0.75;
        final Link link = kmlFactory.createLink();
        link.setHref(href);
        link.setRefreshMode(refreshMode);
        link.setRefreshInterval(refreshInterval);
        link.setViewBoundScale(viewBoundScale);
        link.extensions().simples(Names.BASIC_LINK).add(
                kmlFactory.createSimpleTypeContainer(GxConstants.URI_GX, GxConstants.TAG_X, 2));
        link.extensions().simples(Names.BASIC_LINK).add(
                kmlFactory.createSimpleTypeContainer(GxConstants.URI_GX, GxConstants.TAG_Y, 8));
        link.extensions().simples(Names.BASIC_LINK).add(
                kmlFactory.createSimpleTypeContainer(GxConstants.URI_GX, GxConstants.TAG_W, 1000));
        link.extensions().simples(Names.BASIC_LINK).add(
                kmlFactory.createSimpleTypeContainer(GxConstants.URI_GX, GxConstants.TAG_H, 87));

        final Icon icon = kmlFactory.createIcon(link);

        final String name = "GroundOverlay.kml";
        final Color color = new Color(255,255,255,127);
        final int drawOrder = 1;

        final Feature groundOverlay = kmlFactory.createGroundOverlay();
        final Collection<Property> groundOverlayProperties = groundOverlay.getProperties();
        groundOverlayProperties.add(FF.createAttribute(name, KmlModelConstants.ATT_NAME, null));
        groundOverlay.getProperty(KmlModelConstants.ATT_OVERLAY_COLOR.getName()).setValue(color);
        groundOverlay.getProperty(KmlModelConstants.ATT_OVERLAY_DRAW_ORDER.getName()).setValue(drawOrder);
        groundOverlayProperties.add(FF.createAttribute(icon, KmlModelConstants.ATT_OVERLAY_ICON, null));
        groundOverlayProperties.add(FF.createAttribute(latLonBox, KmlModelConstants.ATT_GROUND_OVERLAY_LAT_LON_BOX, null));

        final Kml kml = kmlFactory.createKml(null, groundOverlay, null, null);
        kml.addExtensionUri(GxConstants.URI_GX, "gx");

        final File temp = File.createTempFile("testIconExt",".kml");
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