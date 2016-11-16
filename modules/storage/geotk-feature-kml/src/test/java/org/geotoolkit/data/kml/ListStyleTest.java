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

import java.awt.Color;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.Iterator;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.stream.XMLStreamException;

import org.geotoolkit.data.kml.model.IdAttributes;
import org.geotoolkit.data.kml.model.Kml;
import org.geotoolkit.data.kml.model.KmlException;
import org.geotoolkit.data.kml.model.KmlModelConstants;
import org.geotoolkit.data.kml.model.ListItem;
import org.geotoolkit.data.kml.model.ListStyle;
import org.geotoolkit.data.kml.model.Point;
import org.geotoolkit.data.kml.model.Style;
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
public class ListStyleTest extends org.geotoolkit.test.TestBase {

    private static final double DELTA = 0.000000000001;
    private static final String pathToTestFile = "src/test/resources/org/geotoolkit/data/kml/listStyle.kml";

    @Test
    public void listStyleReadTest() throws IOException, XMLStreamException, URISyntaxException, KmlException {

        final Feature document;
        {
            final KmlReader reader = new KmlReader();
            reader.setInput(new File(pathToTestFile));
            final Kml kmlObjects = reader.read();
            reader.dispose();

            document = kmlObjects.getAbstractFeature();
            assertEquals(KmlModelConstants.TYPE_DOCUMENT, document.getType());
            assertEquals("ListStyle.kml", document.getPropertyValue(KmlConstants.TAG_NAME));
            assertEquals(Boolean.TRUE, document.getPropertyValue(KmlConstants.TAG_OPEN));
        }

        final Style style0, style1, style2;
        {
            final Iterator<?> i = ((Iterable<?>) document.getPropertyValue(KmlConstants.TAG_STYLE_SELECTOR)).iterator();
            assertTrue("Expected at least one element.", i.hasNext());
            style0 = (Style) i.next();
            assertEquals(new Color(153, 102, 51, 255), style0.getListStyle().getBgColor());
            assertEquals("bgColorExample", style0.getIdAttributes().getId());

            assertTrue("Expected at least 2 elements.", i.hasNext());
            style1 = (Style) i.next();
            assertEquals(ListItem.CHECK_HIDE_CHILDREN, style1.getListStyle().getListItem());
            assertEquals("checkHideChildrenExample", style1.getIdAttributes().getId());

            assertTrue("Expected at least 3 elements.", i.hasNext());
            style2 = (Style) i.next();
            assertEquals(ListItem.RADIO_FOLDER, style2.getListStyle().getListItem());
            assertEquals("radioFolderExample", style2.getIdAttributes().getId());
            assertFalse("Expected exactly 3 elements.", i.hasNext());
        }

        final Feature rootFolder;
        {
            final Iterator<?> i = ((Iterable<?>) document.getPropertyValue(KmlConstants.TAG_FEATURES)).iterator();
            assertTrue("Expected at least one element.", i.hasNext());
            rootFolder = (Feature) i.next();
            assertEquals(KmlModelConstants.TYPE_FOLDER, rootFolder.getType());
            assertEquals("ListStyle Examples", rootFolder.getPropertyValue(KmlConstants.TAG_NAME));
            assertEquals(Boolean.TRUE, rootFolder.getPropertyValue(KmlConstants.TAG_OPEN));
            assertFalse("Expected exactly one element.", i.hasNext());
        }

        final Feature folder0, folder1, folder2;
        {
            final Iterator<?> i = ((Iterable<?>) rootFolder.getPropertyValue(KmlConstants.TAG_FEATURES)).iterator();
            assertTrue("Expected at least one element.", i.hasNext());
            folder0 = (Feature) i.next();
            assertEquals("bgColor example", folder0.getPropertyValue(KmlConstants.TAG_NAME));
            assertEquals(Boolean.TRUE, folder0.getPropertyValue(KmlConstants.TAG_OPEN));
            assertEquals(new URI("#bgColorExample"), folder0.getPropertyValue(KmlConstants.TAG_STYLE_URL));

            assertTrue("Expected at least 2 elements.", i.hasNext());
            folder1 = (Feature) i.next();
            assertEquals("checkHideChildren example", folder1.getPropertyValue(KmlConstants.TAG_NAME));
            assertEquals(Boolean.TRUE, folder1.getPropertyValue(KmlConstants.TAG_OPEN));
            assertEquals(new URI("#checkHideChildrenExample"), folder1.getPropertyValue(KmlConstants.TAG_STYLE_URL));

            assertTrue("Expected at least 3 elements.", i.hasNext());
            folder2 = (Feature) i.next();
            assertEquals("radioFolder example", folder2.getPropertyValue(KmlConstants.TAG_NAME));
            assertEquals(Boolean.TRUE, folder2.getPropertyValue(KmlConstants.TAG_OPEN));
            assertEquals(new URI("#radioFolderExample"), folder2.getPropertyValue(KmlConstants.TAG_STYLE_URL));
            assertFalse("Expected exactly 3 elements.", i.hasNext());
        }

        // Folder 0
        {
            final Iterator<?> i = ((Iterable<?>) folder0.getPropertyValue(KmlConstants.TAG_FEATURES)).iterator();
            assertTrue("Expected at least one element.", i.hasNext());
            Feature placemark = (Feature) i.next();
            assertEquals("pl1", placemark.getPropertyValue(KmlConstants.TAG_NAME));
            CoordinateSequence coordinates = ((Point) placemark.getPropertyValue(KmlConstants.TAG_GEOMETRY)).getCoordinateSequence();
            assertEquals(1, coordinates.size());
            assertEquals(-122.362815, coordinates.getCoordinate(0).x, DELTA);
            assertEquals(  37.822931, coordinates.getCoordinate(0).y, DELTA);
            assertEquals(   0,        coordinates.getCoordinate(0).z, DELTA);

            assertTrue("Expected at least 2 elements.", i.hasNext());
            placemark = (Feature) i.next();
            assertEquals("pl2", placemark.getPropertyValue(KmlConstants.TAG_NAME));
            coordinates = ((Point) placemark.getPropertyValue(KmlConstants.TAG_GEOMETRY)).getCoordinateSequence();
            assertEquals(   1,        coordinates.size());
            assertEquals(-122.362825, coordinates.getCoordinate(0).x, DELTA);
            assertEquals(  37.822931, coordinates.getCoordinate(0).y, DELTA);
            assertEquals(   0,        coordinates.getCoordinate(0).z, DELTA);

            assertTrue("Expected at least 3 elements.", i.hasNext());
            placemark = (Feature) i.next();
            assertEquals("pl3", placemark.getPropertyValue(KmlConstants.TAG_NAME));
            coordinates = ((Point) placemark.getPropertyValue(KmlConstants.TAG_GEOMETRY)).getCoordinateSequence();
            assertEquals(   1,        coordinates.size());
            assertEquals(-122.362835, coordinates.getCoordinate(0).x, DELTA);
            assertEquals(  37.822931, coordinates.getCoordinate(0).y, DELTA);
            assertEquals(   0,        coordinates.getCoordinate(0).z, DELTA);
            assertFalse("Expected exactly 3 elements.", i.hasNext());
        }

        // Folder 1
        {
            final Iterator<?> i = ((Iterable<?>) folder1.getPropertyValue(KmlConstants.TAG_FEATURES)).iterator();
            assertTrue("Expected at least one element.", i.hasNext());
            Feature placemark = (Feature) i.next();
            assertEquals("pl4", placemark.getPropertyValue(KmlConstants.TAG_NAME));
            CoordinateSequence coordinates = ((Point) placemark.getPropertyValue(KmlConstants.TAG_GEOMETRY)).getCoordinateSequence();
            assertEquals(   1,        coordinates.size());
            assertEquals(-122.362845, coordinates.getCoordinate(0).x, DELTA);
            assertEquals(  37.822941, coordinates.getCoordinate(0).y, DELTA);
            assertEquals(   0,        coordinates.getCoordinate(0).z, DELTA);

            assertTrue("Expected at least 2 elements.", i.hasNext());
            placemark = (Feature) i.next();
            assertEquals("pl5", placemark.getPropertyValue(KmlConstants.TAG_NAME));
            coordinates = ((Point) placemark.getPropertyValue(KmlConstants.TAG_GEOMETRY)).getCoordinateSequence();
            assertEquals(   1,        coordinates.size());
            assertEquals(-122.362855, coordinates.getCoordinate(0).x, DELTA);
            assertEquals(  37.822941, coordinates.getCoordinate(0).y, DELTA);
            assertEquals(   0,        coordinates.getCoordinate(0).z, DELTA);

            assertTrue("Expected at least 3 elements.", i.hasNext());
            placemark = (Feature) i.next();
            assertEquals("pl6", placemark.getPropertyValue(KmlConstants.TAG_NAME));
            coordinates = ((Point) placemark.getPropertyValue(KmlConstants.TAG_GEOMETRY)).getCoordinateSequence();
            assertEquals(   1,        coordinates.size());
            assertEquals(-122.362865, coordinates.getCoordinate(0).x, DELTA);
            assertEquals(  37.822941, coordinates.getCoordinate(0).y, DELTA);
            assertEquals(   0,        coordinates.getCoordinate(0).z, DELTA);
            assertFalse("Expected exactly 3 elements.", i.hasNext());
        }

        // Folder 2
        {
            Iterator<?> i = ((Iterable<?>) folder2.getPropertyValue(KmlConstants.TAG_FEATURES)).iterator();
            assertTrue("Expected at least one element.", i.hasNext());
            Feature placemark = (Feature) i.next();
            assertEquals("pl7", placemark.getPropertyValue(KmlConstants.TAG_NAME));
            CoordinateSequence coordinates = ((Point) placemark.getPropertyValue(KmlConstants.TAG_GEOMETRY)).getCoordinateSequence();
            assertEquals(1, coordinates.size());
            assertEquals(-122.362875, coordinates.getCoordinate(0).x, DELTA);
            assertEquals(37.822951, coordinates.getCoordinate(0).y, DELTA);
            assertEquals(0, coordinates.getCoordinate(0).z, DELTA);

            assertTrue("Expected at least 2 elements.", i.hasNext());
            placemark = (Feature) i.next();
            assertEquals("pl8", placemark.getPropertyValue(KmlConstants.TAG_NAME));
            coordinates = ((Point) placemark.getPropertyValue(KmlConstants.TAG_GEOMETRY)).getCoordinateSequence();
            assertEquals(   1,        coordinates.size());
            assertEquals(-122.362885, coordinates.getCoordinate(0).x, DELTA);
            assertEquals(  37.822951, coordinates.getCoordinate(0).y, DELTA);
            assertEquals(   0,        coordinates.getCoordinate(0).z, DELTA);

            assertTrue("Expected at least 3 elements.", i.hasNext());
            placemark = (Feature) i.next();
            assertEquals("pl9", placemark.getPropertyValue(KmlConstants.TAG_NAME));
            coordinates = ((Point) placemark.getPropertyValue(KmlConstants.TAG_GEOMETRY)).getCoordinateSequence();
            assertEquals(   1,        coordinates.size());
            assertEquals(-122.362895, coordinates.getCoordinate(0).x, DELTA);
            assertEquals(  37.822951, coordinates.getCoordinate(0).y, DELTA);
            assertEquals(   0,        coordinates.getCoordinate(0).z, DELTA);
            assertFalse("Expected exactly 3 elements.", i.hasNext());
        }
    }

    @Test
    public void listStyleWriteTest() throws KmlException, IOException, XMLStreamException, ParserConfigurationException, SAXException, URISyntaxException {
        final KmlFactory kmlFactory = DefaultKmlFactory.getInstance();

        final Coordinate coordinate000 = kmlFactory.createCoordinate(-122.362815, 37.822931, 0);
        final Coordinate coordinate010 = kmlFactory.createCoordinate(-122.362825, 37.822931, 0);
        final Coordinate coordinate020 = kmlFactory.createCoordinate(-122.362835, 37.822931, 0);
        final Coordinate coordinate100 = kmlFactory.createCoordinate(-122.362845, 37.822941, 0);
        final Coordinate coordinate110 = kmlFactory.createCoordinate(-122.362855, 37.822941, 0);
        final Coordinate coordinate120 = kmlFactory.createCoordinate(-122.362865, 37.822941, 0);
        final Coordinate coordinate200 = kmlFactory.createCoordinate(-122.362875, 37.822951, 0);
        final Coordinate coordinate210 = kmlFactory.createCoordinate(-122.362885, 37.822951, 0);
        final Coordinate coordinate220 = kmlFactory.createCoordinate(-122.362895, 37.822951, 0);

        final CoordinateSequence coordinates00 = kmlFactory.createCoordinates(Arrays.asList(coordinate000));
        final CoordinateSequence coordinates01 = kmlFactory.createCoordinates(Arrays.asList(coordinate010));
        final CoordinateSequence coordinates02 = kmlFactory.createCoordinates(Arrays.asList(coordinate020));
        final CoordinateSequence coordinates10 = kmlFactory.createCoordinates(Arrays.asList(coordinate100));
        final CoordinateSequence coordinates11 = kmlFactory.createCoordinates(Arrays.asList(coordinate110));
        final CoordinateSequence coordinates12 = kmlFactory.createCoordinates(Arrays.asList(coordinate120));
        final CoordinateSequence coordinates20 = kmlFactory.createCoordinates(Arrays.asList(coordinate200));
        final CoordinateSequence coordinates21 = kmlFactory.createCoordinates(Arrays.asList(coordinate210));
        final CoordinateSequence coordinates22 = kmlFactory.createCoordinates(Arrays.asList(coordinate220));

        final Point point00 = kmlFactory.createPoint(coordinates00);
        final Point point01 = kmlFactory.createPoint(coordinates01);
        final Point point02 = kmlFactory.createPoint(coordinates02);
        final Point point10 = kmlFactory.createPoint(coordinates10);
        final Point point11 = kmlFactory.createPoint(coordinates11);
        final Point point12 = kmlFactory.createPoint(coordinates12);
        final Point point20 = kmlFactory.createPoint(coordinates20);
        final Point point21 = kmlFactory.createPoint(coordinates21);
        final Point point22 = kmlFactory.createPoint(coordinates22);

        final Feature placemark00 = kmlFactory.createPlacemark();
        placemark00.setPropertyValue(KmlConstants.TAG_NAME, "pl1");
        placemark00.setPropertyValue(KmlConstants.TAG_GEOMETRY, point00);

        final Feature placemark01 = kmlFactory.createPlacemark();
        placemark01.setPropertyValue(KmlConstants.TAG_NAME, "pl2");
        placemark01.setPropertyValue(KmlConstants.TAG_GEOMETRY, point01);

        final Feature placemark02 = kmlFactory.createPlacemark();
        placemark02.setPropertyValue(KmlConstants.TAG_NAME, "pl3");
        placemark02.setPropertyValue(KmlConstants.TAG_GEOMETRY, point02);

        final Feature placemark10 = kmlFactory.createPlacemark();
        placemark10.setPropertyValue(KmlConstants.TAG_NAME, "pl4");
        placemark10.setPropertyValue(KmlConstants.TAG_GEOMETRY, point10);

        final Feature placemark11 = kmlFactory.createPlacemark();
        placemark11.setPropertyValue(KmlConstants.TAG_NAME, "pl5");
        placemark11.setPropertyValue(KmlConstants.TAG_GEOMETRY, point11);

        final Feature placemark12 = kmlFactory.createPlacemark();
        placemark12.setPropertyValue(KmlConstants.TAG_NAME, "pl6");
        placemark12.setPropertyValue(KmlConstants.TAG_GEOMETRY, point12);

        final Feature placemark20 = kmlFactory.createPlacemark();
        placemark20.setPropertyValue(KmlConstants.TAG_NAME, "pl7");
        placemark20.setPropertyValue(KmlConstants.TAG_GEOMETRY, point20);

        final Feature placemark21 = kmlFactory.createPlacemark();
        placemark21.setPropertyValue(KmlConstants.TAG_NAME, "pl8");
        placemark21.setPropertyValue(KmlConstants.TAG_GEOMETRY, point21);

        final Feature placemark22 = kmlFactory.createPlacemark();
        placemark22.setPropertyValue(KmlConstants.TAG_NAME, "pl9");
        placemark22.setPropertyValue(KmlConstants.TAG_GEOMETRY, point22);

        final Feature folder0 = kmlFactory.createFolder();
        folder0.setPropertyValue(KmlConstants.TAG_NAME, "bgColor example");
        folder0.setPropertyValue(KmlConstants.TAG_OPEN, Boolean.TRUE);
        folder0.setPropertyValue(KmlConstants.TAG_STYLE_URL, new URI("#bgColorExample"));
        folder0.setPropertyValue(KmlConstants.TAG_FEATURES, Arrays.asList(placemark00,placemark01,placemark02));

        final Feature folder1 = kmlFactory.createFolder();
        folder1.setPropertyValue(KmlConstants.TAG_NAME, "checkHideChildren example");
        folder1.setPropertyValue(KmlConstants.TAG_OPEN, Boolean.TRUE);
        folder1.setPropertyValue(KmlConstants.TAG_STYLE_URL, new URI("#checkHideChildrenExample"));
        folder1.setPropertyValue(KmlConstants.TAG_FEATURES, Arrays.asList(placemark10,placemark11,placemark12));

        final Feature folder2 = kmlFactory.createFolder();
        folder2.setPropertyValue(KmlConstants.TAG_NAME, "radioFolder example");
        folder2.setPropertyValue(KmlConstants.TAG_OPEN, Boolean.TRUE);
        folder2.setPropertyValue(KmlConstants.TAG_STYLE_URL, new URI("#radioFolderExample"));
        folder2.setPropertyValue(KmlConstants.TAG_FEATURES, Arrays.asList(placemark20,placemark21,placemark22));

        final Feature folder = kmlFactory.createFolder();
        folder.setPropertyValue(KmlConstants.TAG_NAME, "ListStyle Examples");
        folder.setPropertyValue(KmlConstants.TAG_OPEN, Boolean.TRUE);
        folder.setPropertyValue(KmlConstants.TAG_FEATURES, Arrays.asList(folder0,folder1,folder2));

        final ListStyle listStyle1 = kmlFactory.createListStyle();
        listStyle1.setBgColor(new Color(153, 102, 51, 255));
        final Style style1 = kmlFactory.createStyle();
        style1.setListStyle(listStyle1);
        final IdAttributes idAttributes1 = kmlFactory.createIdAttributes("bgColorExample", null);
        style1.setIdAttributes(idAttributes1);

        final ListStyle listStyle2 = kmlFactory.createListStyle();
        listStyle2.setListItem(ListItem.CHECK_HIDE_CHILDREN);
        final Style style2 = kmlFactory.createStyle();
        style2.setListStyle(listStyle2);
        final IdAttributes idAttributes2 = kmlFactory.createIdAttributes("checkHideChildrenExample", null);
        style2.setIdAttributes(idAttributes2);

        final ListStyle listStyle3 = kmlFactory.createListStyle();
        listStyle3.setListItem(ListItem.RADIO_FOLDER);
        final Style style3 = kmlFactory.createStyle();
        style3.setListStyle(listStyle3);
        final IdAttributes idAttributes3 = kmlFactory.createIdAttributes("radioFolderExample", null);
        style3.setIdAttributes(idAttributes3);

        final Feature document = kmlFactory.createDocument();
        document.setPropertyValue(KmlConstants.TAG_NAME, "ListStyle.kml");
        document.setPropertyValue(KmlConstants.TAG_OPEN, Boolean.TRUE);
        document.setPropertyValue(KmlConstants.TAG_FEATURES, folder);
        document.setPropertyValue(KmlConstants.TAG_STYLE_SELECTOR, Arrays.asList(style1,style2,style3));

        final Kml kml = kmlFactory.createKml(null, document, null, null);

        final File temp = File.createTempFile("testListStyle", ".kml");
        temp.deleteOnExit();

        final KmlWriter writer = new KmlWriter();
        writer.setOutput(temp);
        writer.write(kml);
        writer.dispose();

        DomCompare.compare(new File(pathToTestFile), temp);
    }
}
