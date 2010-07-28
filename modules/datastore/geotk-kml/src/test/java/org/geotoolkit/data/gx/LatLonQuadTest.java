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
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.stream.XMLStreamException;
import org.geotoolkit.data.gx.model.LatLonQuad;
import org.geotoolkit.data.gx.xml.GxConstants;
import org.geotoolkit.data.gx.xml.GxReader;
import org.geotoolkit.data.gx.xml.GxWriter;
import org.geotoolkit.data.kml.DefaultKmlFactory;
import org.geotoolkit.data.kml.KmlFactory;
import org.geotoolkit.data.kml.model.Coordinates;
import org.geotoolkit.data.kml.model.Extensions;
import org.geotoolkit.data.kml.model.Icon;
import org.geotoolkit.data.kml.model.Kml;
import org.geotoolkit.data.kml.model.KmlException;
import org.geotoolkit.data.kml.model.KmlModelConstants;
import org.geotoolkit.data.kml.model.Link;
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
public class LatLonQuadTest {

    private static final double DELTA = 0.000000000001;
    private static final String pathToTestFile = "src/test/resources/org/geotoolkit/data/gx/latLonQuad.kml";
    private static final FeatureFactory FF = FactoryFinder.getFeatureFactory(
            new Hints(Hints.FEATURE_FACTORY, LenientFeatureFactory.class));

    public LatLonQuadTest() {
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
    public void latLonQuadReadTest()
            throws IOException, XMLStreamException, URISyntaxException, KmlException {

        Iterator i;

        final KmlReader reader = new KmlReader();
        final GxReader gxReader = new GxReader(reader);
        reader.setInput(new File(pathToTestFile));
        reader.addExtensionReader(gxReader);
        final Kml kmlObjects = reader.read();
        reader.dispose();

        final Feature groundOverlay = kmlObjects.getAbstractFeature();
        assertTrue(groundOverlay.getType().equals(KmlModelConstants.TYPE_GROUND_OVERLAY));
        assertEquals("gx:LatLonQuad Example", groundOverlay.getProperty(KmlModelConstants.ATT_NAME.getName()).getValue());

        assertTrue(groundOverlay.getProperty(KmlModelConstants.ATT_OVERLAY_ICON.getName()).getValue() instanceof Icon);
        final Icon icon = (Icon) groundOverlay.getProperty(KmlModelConstants.ATT_OVERLAY_ICON.getName()).getValue();
        assertEquals("http://code.google.com/apis/kml/documentation/Images/rectangle.gif", icon.getHref());
        assertEquals(0.75, icon.getViewBoundScale(), DELTA);

        final Extensions extensions = (Extensions) groundOverlay.getProperty(KmlModelConstants.ATT_EXTENSIONS.getName()).getValue();

        assertEquals(1, extensions.complexes(Extensions.Names.GROUND_OVERLAY).size());
        assertTrue(extensions.complexes(Extensions.Names.GROUND_OVERLAY).get(0) instanceof LatLonQuad);
        final LatLonQuad latLonQuad = (LatLonQuad) extensions.complexes(Extensions.Names.GROUND_OVERLAY).get(0);
        assertTrue(latLonQuad.getCoordinates() instanceof Coordinates);

        final Coordinates coordinates = latLonQuad.getCoordinates();

        assertEquals(4, coordinates.size());

        final Coordinate coordinate0 = coordinates.getCoordinate(0);
        assertEquals(81.601884,coordinate0.x, DELTA);
        assertEquals(44.160723,coordinate0.y, DELTA);
        assertEquals(Double.NaN,coordinate0.z, DELTA);

        final Coordinate coordinate1 = coordinates.getCoordinate(1);
        assertEquals(83.529902,coordinate1.x, DELTA);
        assertEquals(43.665148,coordinate1.y, DELTA);
        assertEquals(Double.NaN,coordinate1.z, DELTA);

        final Coordinate coordinate2 = coordinates.getCoordinate(2);
        assertEquals(82.947737,coordinate2.x, DELTA);
        assertEquals(44.248831,coordinate2.y, DELTA);
        assertEquals(Double.NaN,coordinate2.z, DELTA);

        final Coordinate coordinate3 = coordinates.getCoordinate(3);
        assertEquals(81.509322,coordinate3.x, DELTA);
        assertEquals(44.321015,coordinate3.y, DELTA);
        assertEquals(Double.NaN,coordinate3.z, DELTA);

    }

    @Test
    public void latLonQuadWriteTest() throws KmlException, IOException, XMLStreamException, ParserConfigurationException, SAXException, URISyntaxException {
        final GxFactory gxFactory = DefaultGxFactory.getInstance();
        final KmlFactory kmlFactory = DefaultKmlFactory.getInstance();

        final Coordinate coordinate0 = kmlFactory.createCoordinate("81.601884,44.160723");
        final Coordinate coordinate1 = kmlFactory.createCoordinate("83.529902,43.665148");
        final Coordinate coordinate2 = kmlFactory.createCoordinate("82.947737,44.248831");
        final Coordinate coordinate3 = kmlFactory.createCoordinate("81.509322,44.321015");

        final  Coordinates coordinates = kmlFactory.createCoordinates(
                Arrays.asList(coordinate0, coordinate1, coordinate2, coordinate3));

        final LatLonQuad latLonQuad = gxFactory.createLatLonQuad();
        latLonQuad.setCoordinates(coordinates);

        final Link link = kmlFactory.createLink();
        link.setHref("http://code.google.com/apis/kml/documentation/Images/rectangle.gif");
        link.setViewBoundScale(0.75);
        final Icon icon = kmlFactory.createIcon(link);

        final Feature groundOverlay = kmlFactory.createGroundOverlay();
        Collection<Property> groundOverlayProperties = groundOverlay.getProperties();
        groundOverlayProperties.add(FF.createAttribute("gx:LatLonQuad Example",KmlModelConstants.ATT_NAME ,null));
        groundOverlayProperties.add(FF.createAttribute(icon, KmlModelConstants.ATT_OVERLAY_ICON, null));
        ((Extensions) groundOverlay.getProperty(KmlModelConstants.ATT_EXTENSIONS.getName()).getValue()).
                complexes(Extensions.Names.GROUND_OVERLAY).add(latLonQuad);

        final Kml kml = kmlFactory.createKml(null, groundOverlay, null, null);
        kml.addExtensionUri(GxConstants.URI_GX, "gx");

        final File temp = File.createTempFile("testLatLonQuad", ".kml");
        //temp.deleteOnExit();

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