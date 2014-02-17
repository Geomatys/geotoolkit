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

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.CoordinateSequence;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.Collection;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.stream.XMLStreamException;

import org.geotoolkit.data.kml.model.Icon;
import org.geotoolkit.data.kml.model.Kml;
import org.geotoolkit.data.kml.model.KmlException;
import org.geotoolkit.data.kml.model.KmlModelConstants;
import org.geotoolkit.data.kml.model.Link;
import org.geotoolkit.data.kml.model.Point;
import org.geotoolkit.data.kml.model.ViewVolume;
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
public class PhotoOverlayTest {

    private static final double DELTA = 0.000000000001;
    private static final String pathToTestFile = "src/test/resources/org/geotoolkit/data/kml/photoOverlay.kml";
    private static final FeatureFactory FF = FactoryFinder.getFeatureFactory(
            new Hints(Hints.FEATURE_FACTORY, LenientFeatureFactory.class));

    public PhotoOverlayTest() {
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
    public void photoOverlayReadTest() throws IOException, XMLStreamException, KmlException, URISyntaxException {

        final KmlReader reader = new KmlReader();
        reader.setInput(new File(pathToTestFile));
        final Kml kmlObjects = reader.read();
        reader.dispose();

        final Feature photoOverlay = kmlObjects.getAbstractFeature();
        assertTrue(photoOverlay.getType().equals(KmlModelConstants.TYPE_PHOTO_OVERLAY));
        assertEquals("A simple non-pyramidal photo", photoOverlay.getProperty(KmlModelConstants.ATT_NAME.getName()).getValue());
        assertEquals("High above the ocean", photoOverlay.getProperty(KmlModelConstants.ATT_DESCRIPTION.getName()).getValue());

        final Icon icon = (Icon) photoOverlay.getProperty(KmlModelConstants.ATT_OVERLAY_ICON.getName()).getValue();
        assertEquals("small-photo.jpg", icon.getHref());

        final ViewVolume viewVolume = (ViewVolume) photoOverlay.getProperty(KmlModelConstants.ATT_PHOTO_OVERLAY_VIEW_VOLUME.getName()).getValue();
        assertEquals(-60, viewVolume.getLeftFov(), DELTA);
        assertEquals(60, viewVolume.getRightFov(), DELTA);
        assertEquals(-45, viewVolume.getBottomFov(), DELTA);
        assertEquals(45, viewVolume.getTopFov(), DELTA);
        assertEquals(1000, viewVolume.getNear(), DELTA);

        final Point point = (Point) photoOverlay.getProperty(KmlModelConstants.ATT_PHOTO_OVERLAY_POINT.getName()).getValue();
        final CoordinateSequence coordinates = point.getCoordinateSequence();
        assertEquals(1, coordinates.size());
        assertEquals(1, coordinates.getCoordinate(0).x, DELTA);
        assertEquals(1, coordinates.getCoordinate(0).y, DELTA);


    }

    @Test
    public void photoOverlayWriteTest() throws KmlException, IOException, XMLStreamException, ParserConfigurationException, SAXException {
        final KmlFactory kmlFactory = DefaultKmlFactory.getInstance();

        final double longitude = 1;
        final double latitude = 1;
        final Coordinate coordinate = kmlFactory.createCoordinate(longitude, latitude);
        final CoordinateSequence coordinates = kmlFactory.createCoordinates(Arrays.asList(coordinate));
        final Point point = kmlFactory.createPoint(coordinates);

        final double leftFov = -60.0;
        final double rightFov = 60.0;
        final double bottomFov = -45.0;
        final double topFov = 45.0;
        final double near = 1000.0;
        final ViewVolume viewVolume = kmlFactory.createViewVolume(null, null, leftFov, rightFov, bottomFov, topFov, near, null, null);

        final Link link = kmlFactory.createLink();
        link.setHref("small-photo.jpg");
        final Icon icon = kmlFactory.createIcon(link);

        final Feature photoOverlay = kmlFactory.createPhotoOverlay();
        final Collection<Property> photoOverlayProperties = photoOverlay.getProperties();
        photoOverlayProperties.add(FF.createAttribute("A simple non-pyramidal photo", KmlModelConstants.ATT_NAME, null));
        photoOverlayProperties.add(FF.createAttribute("High above the ocean", KmlModelConstants.ATT_DESCRIPTION, null));
        photoOverlayProperties.add(FF.createAttribute(icon, KmlModelConstants.ATT_OVERLAY_ICON, null));
        photoOverlayProperties.add(FF.createAttribute(viewVolume, KmlModelConstants.ATT_PHOTO_OVERLAY_VIEW_VOLUME, null));
        photoOverlayProperties.add(FF.createAttribute(point, KmlModelConstants.ATT_PHOTO_OVERLAY_POINT, null));

        final Kml kml = kmlFactory.createKml(null, photoOverlay, null, null);

        final File temp = File.createTempFile("testPhotoOverlay",".kml");
        temp.deleteOnExit();

        final KmlWriter writer = new KmlWriter();
        writer.setOutput(temp);
        writer.write(kml);
        writer.dispose();

        DomCompare.compare(
                 new File(pathToTestFile), temp);
    }
}
