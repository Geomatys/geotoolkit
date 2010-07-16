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
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.stream.XMLStreamException;
import org.geotoolkit.data.kml.model.AbstractFeature;
import org.geotoolkit.data.kml.model.Coordinates;
import org.geotoolkit.data.kml.model.Icon;
import org.geotoolkit.data.kml.model.Kml;
import org.geotoolkit.data.kml.model.KmlException;
import org.geotoolkit.data.kml.model.Link;
import org.geotoolkit.data.kml.model.PhotoOverlay;
import org.geotoolkit.data.kml.model.Point;
import org.geotoolkit.data.kml.model.ViewVolume;
import org.geotoolkit.data.kml.xml.KmlReader;
import org.geotoolkit.data.kml.xml.KmlWriter;
import org.geotoolkit.xml.DomCompare;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.xml.sax.SAXException;
import static org.junit.Assert.*;

/**
 *
 * @author Samuel Andrés
 */
public class PhotoOverlayTest {

    private static final double DELTA = 0.000000000001;
    private static final String pathToTestFile = "src/test/resources/org/geotoolkit/data/kml/photoOverlay.kml";

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
    public void photoOverlayReadTest() throws IOException, XMLStreamException {

        final KmlReader reader = new KmlReader();
        reader.setInput(new File(pathToTestFile));
        final Kml kmlObjects = reader.read();
        reader.dispose();

        final AbstractFeature feature = kmlObjects.getAbstractFeature();
        assertTrue(feature instanceof PhotoOverlay);
        PhotoOverlay photoOverlay = (PhotoOverlay) feature;
        assertEquals("A simple non-pyramidal photo",photoOverlay.getName());
        assertEquals("High above the ocean", photoOverlay.getDescription());

        final Icon icon = photoOverlay.getIcon();
        assertEquals("small-photo.jpg", icon.getHref());

        final ViewVolume viewVolume = photoOverlay.getViewVolume();
        assertEquals(-60, viewVolume.getLeftFov(), DELTA);
        assertEquals(60, viewVolume.getRightFov(), DELTA);
        assertEquals(-45, viewVolume.getBottomFov(), DELTA);
        assertEquals(45, viewVolume.getTopFov(), DELTA);
        assertEquals(1000, viewVolume.getNear(), DELTA);

        final Point point = photoOverlay.getPoint();
        final Coordinates coordinates = point.getCoordinateSequence();
        assertEquals(1,coordinates.size());
        assertEquals(1, coordinates.getCoordinate(0).x, DELTA);
        assertEquals(1, coordinates.getCoordinate(0).y, DELTA);


    }

    @Test
    public void photoOverlayWriteTest() throws KmlException, IOException, XMLStreamException, ParserConfigurationException, SAXException{
        final KmlFactory kmlFactory = new DefaultKmlFactory();

        final double longitude = 1;
        final double latitude = 1;
        final Coordinate coordinate = kmlFactory.createCoordinate(longitude, latitude);
        final Coordinates coordinates = kmlFactory.createCoordinates(Arrays.asList(coordinate));
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

        final PhotoOverlay photoOverlay = kmlFactory.createPhotoOverlay();
        photoOverlay.setName("A simple non-pyramidal photo");
        photoOverlay.setDescription("High above the ocean");
        photoOverlay.setIcon(icon);
        photoOverlay.setViewVolume(viewVolume);
        photoOverlay.setPoint(point);
       
        final Kml kml = kmlFactory.createKml(null, photoOverlay, null, null);

        File temp = File.createTempFile("testPhotoOverlay",".kml");
        temp.deleteOnExit();

        KmlWriter writer = new KmlWriter();
        writer.setOutput(temp);
        writer.write(kml);
        writer.dispose();

        DomCompare.compare(
                 new File(pathToTestFile), temp);

    }
}