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
import java.net.URISyntaxException;
import java.util.Arrays;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.stream.XMLStreamException;

import org.geotoolkit.data.gx.xml.GxConstants;
import org.geotoolkit.data.gx.xml.GxReader;
import org.geotoolkit.data.gx.xml.GxWriter;
import org.geotoolkit.data.kml.DefaultKmlFactory;
import org.geotoolkit.data.kml.KmlFactory;
import org.geotoolkit.data.kml.model.Extensions;
import org.geotoolkit.data.kml.model.Kml;
import org.geotoolkit.data.kml.model.KmlException;
import org.geotoolkit.data.kml.model.KmlModelConstants;
import org.geotoolkit.data.kml.model.Point;
import org.geotoolkit.data.kml.xml.KmlReader;
import org.geotoolkit.data.kml.xml.KmlWriter;
import org.geotoolkit.data.kml.xsd.SimpleTypeContainer;
import org.geotoolkit.xml.DomCompare;

import org.junit.Test;

import org.opengis.feature.Feature;
import org.geotoolkit.data.kml.xml.KmlConstants;
import org.xml.sax.SAXException;
import static org.junit.Assert.*;

/**
 *
 * @author Samuel Andrés
 * @module pending
 */
public class BalloonVisibilityTest extends org.geotoolkit.test.TestBase {

    private static final double DELTA = 0.000000000001;
    private static final String pathToTestFile = "src/test/resources/org/geotoolkit/data/gx/balloonVisibility.kml";

    @Test
    public void balloonVisibilityReadTest() throws IOException, XMLStreamException, URISyntaxException, KmlException {
        final KmlReader reader = new KmlReader();
        final GxReader gxReader = new GxReader(reader);
        reader.setInput(new File(pathToTestFile));
        reader.addExtensionReader(gxReader);
        final Kml kmlObjects = reader.read();
        reader.dispose();

        final Feature placemark = kmlObjects.getAbstractFeature();
        assertEquals(KmlModelConstants.TYPE_PLACEMARK, placemark.getType());
        assertEquals("Eiffel Tower", placemark.getPropertyValue(KmlConstants.TAG_NAME));
        assertEquals("\n" +
                     "        Located in Paris, France.\n"+
                     "\n" +
                     "        This description balloon opens\n"+
                     "        when the Placemark is loaded.\n" +
                     "    ", placemark.getPropertyValue(KmlConstants.TAG_DESCRIPTION));
        assertEquals(1, ((Extensions) placemark.getProperty(KmlConstants.TAG_EXTENSIONS).getValue()).simples(Extensions.Names.FEATURE).size());
        final SimpleTypeContainer balloonVisibility = ((Extensions) placemark.getProperty(KmlConstants.TAG_EXTENSIONS).getValue()).simples(Extensions.Names.FEATURE).get(0);

        assertEquals(GxConstants.URI_GX, balloonVisibility.getNamespaceUri());
        assertEquals(GxConstants.TAG_BALLOON_VISIBILITY, balloonVisibility.getTagName());
        assertEquals(Boolean.FALSE, balloonVisibility.getValue());

        final Point point = (Point) placemark.getPropertyValue(KmlConstants.TAG_GEOMETRY);

        assertEquals(1, point.getCoordinateSequence().size());
        final Coordinate coordinate = point.getCoordinateSequence().getCoordinate(0);
        assertEquals(2.294785, coordinate.x, DELTA);
        assertEquals(48.858093, coordinate.y, DELTA);
        assertEquals(0.0, coordinate.z, DELTA);

    }

    @Test
    public void balloonVisibilityWriteTest() throws KmlException, IOException, XMLStreamException, ParserConfigurationException, SAXException, URISyntaxException {
        final KmlFactory kmlFactory = DefaultKmlFactory.getInstance();

        final CoordinateSequence coordinates = kmlFactory.createCoordinates(Arrays.asList(
                kmlFactory.createCoordinate("2.294785,48.858093,0.0")));

        final Point point = kmlFactory.createPoint(coordinates);

        final Feature placemark = kmlFactory.createPlacemark();
        placemark.setPropertyValue(KmlConstants.TAG_NAME, "Eiffel Tower");
        final String description = "\n        Located in Paris, France.\n"+
                                   "\n        This description balloon opens\n" +
                                     "        when the Placemark is loaded.\n" + "    ";
        placemark.setPropertyValue(KmlConstants.TAG_DESCRIPTION, description);
        placemark.setPropertyValue(KmlConstants.TAG_GEOMETRY, point);
        ((Extensions) placemark.getProperty(KmlConstants.TAG_EXTENSIONS).getValue()).
                simples(Extensions.Names.FEATURE).add(kmlFactory.createSimpleTypeContainer(GxConstants.URI_GX, GxConstants.TAG_BALLOON_VISIBILITY, false));

        final Kml kml = kmlFactory.createKml(null, placemark, null, null);
        kml.addExtensionUri(GxConstants.URI_GX, "gx");

        final File temp = File.createTempFile("testBalloonVisibility", ".kml");
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