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
import java.util.Collection;
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
public class ListStyleTest {

    private static final double DELTA = 0.000000000001;
    private static final String pathToTestFile = "src/test/resources/org/geotoolkit/data/kml/listStyle.kml";
    private static final FeatureFactory FF = FactoryFinder.getFeatureFactory(
            new Hints(Hints.FEATURE_FACTORY, LenientFeatureFactory.class));

    public ListStyleTest() {
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
    public void listStyleReadTest() throws IOException, XMLStreamException, URISyntaxException, KmlException {

        final KmlReader reader = new KmlReader();
        reader.setInput(new File(pathToTestFile));
        final Kml kmlObjects = reader.read();
        reader.dispose();

        final Feature document = kmlObjects.getAbstractFeature();
        assertTrue(document.getType().equals(KmlModelConstants.TYPE_DOCUMENT));
        assertEquals("ListStyle.kml", document.getProperty(KmlModelConstants.ATT_NAME.getName()).getValue());
        assertTrue((Boolean) document.getProperty(KmlModelConstants.ATT_OPEN.getName()).getValue());

        assertEquals(3, document.getProperties(KmlModelConstants.ATT_STYLE_SELECTOR.getName()).size());

        Iterator i = document.getProperties(KmlModelConstants.ATT_STYLE_SELECTOR.getName()).iterator();

        if(i.hasNext()){
            Object object = ((Property) i.next()).getValue();
            assertTrue(object instanceof Style);
            final Style style0 = (Style) object;
            assertEquals(new Color(153, 102, 51, 255), style0.getListStyle().getBgColor());
            assertEquals("bgColorExample", style0.getIdAttributes().getId());
        }

        if(i.hasNext()){
            Object object = ((Property) i.next()).getValue();
            assertTrue(object instanceof Style);
            final Style style1 = (Style) object;
            assertEquals(ListItem.CHECK_HIDE_CHILDREN, style1.getListStyle().getListItem());
            assertEquals("checkHideChildrenExample", style1.getIdAttributes().getId());
        }

        if(i.hasNext()){
            Object object = ((Property) i.next()).getValue();
            assertTrue(object instanceof Style);
            final Style style2 = (Style) object;
            assertEquals(ListItem.RADIO_FOLDER, style2.getListStyle().getListItem());
            assertEquals("radioFolderExample", style2.getIdAttributes().getId());
        }

        assertEquals(1, document.getProperties(KmlModelConstants.ATT_DOCUMENT_FEATURES.getName()).size());

        i = document.getProperties(KmlModelConstants.ATT_DOCUMENT_FEATURES.getName()).iterator();

        if(i.hasNext()){
            Object object = ((Property) i.next()).getValue();
            assertTrue(object instanceof Feature);
            Feature folder = (Feature) object;
            assertTrue(folder.getType().equals(KmlModelConstants.TYPE_FOLDER));

            assertEquals("ListStyle Examples", folder.getProperty(KmlModelConstants.ATT_NAME.getName()).getValue());
            assertTrue((Boolean) folder.getProperty(KmlModelConstants.ATT_OPEN.getName()).getValue());

            assertEquals(3, folder.getProperties(KmlModelConstants.ATT_FOLDER_FEATURES.getName()).size());

            Iterator j = folder.getProperties(KmlModelConstants.ATT_FOLDER_FEATURES.getName()).iterator();

            if (j.hasNext()){
                final Object obj = ((Property) j.next()).getValue();
                final Feature folder0 = (Feature) obj;
                assertEquals("bgColor example", folder0.getProperty(KmlModelConstants.ATT_NAME.getName()).getValue());
                assertTrue((Boolean) folder0.getProperty(KmlModelConstants.ATT_OPEN.getName()).getValue());
                assertEquals(new URI("#bgColorExample"), folder0.getProperty(KmlModelConstants.ATT_STYLE_URL.getName()).getValue());

                assertEquals(3, folder0.getProperties(KmlModelConstants.ATT_FOLDER_FEATURES.getName()).size());

                Iterator k = folder0.getProperties(KmlModelConstants.ATT_FOLDER_FEATURES.getName()).iterator();

                if(k.hasNext()){
                    final Object o = ((Property) k.next()).getValue();
                    assertTrue(o instanceof Feature);
                    final Feature placemark00 = (Feature) o;

                    assertEquals("pl1", placemark00.getProperty(KmlModelConstants.ATT_NAME.getName()).getValue());
                    assertTrue(placemark00.getProperty(KmlModelConstants.ATT_PLACEMARK_GEOMETRY.getName()).getValue() instanceof Point);
                    final CoordinateSequence coordinates00 = ((Point) placemark00.getProperty(KmlModelConstants.ATT_PLACEMARK_GEOMETRY.getName()).getValue()).getCoordinateSequence();
                    assertEquals(1, coordinates00.size());
                    assertEquals(-122.362815, coordinates00.getCoordinate(0).x, DELTA);
                    assertEquals(37.822931, coordinates00.getCoordinate(0).y, DELTA);
                    assertEquals(0, coordinates00.getCoordinate(0).z, DELTA);
                }

                if(k.hasNext()){
                    final Object o = ((Property) k.next()).getValue();
                    assertTrue(o instanceof Feature);
                    final Feature placemark01 = (Feature) o;

                    assertEquals("pl2", placemark01.getProperty(KmlModelConstants.ATT_NAME.getName()).getValue());
                    assertTrue(placemark01.getProperty(KmlModelConstants.ATT_PLACEMARK_GEOMETRY.getName()).getValue() instanceof Point);
                    final CoordinateSequence coordinates01 = ((Point) placemark01.getProperty(KmlModelConstants.ATT_PLACEMARK_GEOMETRY.getName()).getValue()).getCoordinateSequence();
                    assertEquals(1, coordinates01.size());
                    assertEquals(-122.362825, coordinates01.getCoordinate(0).x, DELTA);
                    assertEquals(37.822931, coordinates01.getCoordinate(0).y, DELTA);
                    assertEquals(0, coordinates01.getCoordinate(0).z, DELTA);
                }

                if(k.hasNext()){
                    final Object o = ((Property) k.next()).getValue();
                    assertTrue(o instanceof Feature);
                    final Feature placemark02 = (Feature) o;

                    assertEquals("pl3", placemark02.getProperty(KmlModelConstants.ATT_NAME.getName()).getValue());
                    assertTrue(placemark02.getProperty(KmlModelConstants.ATT_PLACEMARK_GEOMETRY.getName()).getValue() instanceof Point);
                    final CoordinateSequence coordinates02 = ((Point) placemark02.getProperty(KmlModelConstants.ATT_PLACEMARK_GEOMETRY.getName()).getValue()).getCoordinateSequence();
                    assertEquals(1, coordinates02.size());
                    assertEquals(-122.362835, coordinates02.getCoordinate(0).x, DELTA);
                    assertEquals(37.822931, coordinates02.getCoordinate(0).y, DELTA);
                    assertEquals(0, coordinates02.getCoordinate(0).z, DELTA);
                }
            }

            if (j.hasNext()){
                final Object obj = ((Property) j.next()).getValue();
                final Feature folder1 = (Feature) obj;

                assertEquals("checkHideChildren example", folder1.getProperty(KmlModelConstants.ATT_NAME.getName()).getValue());
                assertTrue((Boolean) folder1.getProperty(KmlModelConstants.ATT_OPEN.getName()).getValue());
                assertEquals(new URI("#checkHideChildrenExample"), folder1.getProperty(KmlModelConstants.ATT_STYLE_URL.getName()).getValue());

                assertEquals(3, folder1.getProperties(KmlModelConstants.ATT_FOLDER_FEATURES.getName()).size());

                Iterator k = folder1.getProperties(KmlModelConstants.ATT_FOLDER_FEATURES.getName()).iterator();

                if(k.hasNext()){
                    final Object o = ((Property) k.next()).getValue();
                    assertTrue(o instanceof Feature);
                    final Feature placemark10 = (Feature) o;

                    assertEquals("pl4", placemark10.getProperty(KmlModelConstants.ATT_NAME.getName()).getValue());
                    assertTrue(placemark10.getProperty(KmlModelConstants.ATT_PLACEMARK_GEOMETRY.getName()).getValue() instanceof Point);
                    final CoordinateSequence coordinates10 = ((Point) placemark10.getProperty(KmlModelConstants.ATT_PLACEMARK_GEOMETRY.getName()).getValue()).getCoordinateSequence();
                    assertEquals(1, coordinates10.size());
                    assertEquals(-122.362845, coordinates10.getCoordinate(0).x, DELTA);
                    assertEquals(37.822941, coordinates10.getCoordinate(0).y, DELTA);
                    assertEquals(0, coordinates10.getCoordinate(0).z, DELTA);

                }

                if(k.hasNext()){
                    final Object o = ((Property) k.next()).getValue();
                    assertTrue(o instanceof Feature);
                    final Feature placemark11 = (Feature) o;

                    assertEquals("pl5", placemark11.getProperty(KmlModelConstants.ATT_NAME.getName()).getValue());
                    assertTrue(placemark11.getProperty(KmlModelConstants.ATT_PLACEMARK_GEOMETRY.getName()).getValue() instanceof Point);
                    final CoordinateSequence coordinates11 = ((Point) placemark11.getProperty(KmlModelConstants.ATT_PLACEMARK_GEOMETRY.getName()).getValue()).getCoordinateSequence();
                    assertEquals(1, coordinates11.size());
                    assertEquals(-122.362855, coordinates11.getCoordinate(0).x, DELTA);
                    assertEquals(37.822941, coordinates11.getCoordinate(0).y, DELTA);
                    assertEquals(0, coordinates11.getCoordinate(0).z, DELTA);

                }

                if(k.hasNext()){
                    final Object o = ((Property) k.next()).getValue();
                    assertTrue(o instanceof Feature);
                    final Feature placemark12 = (Feature) o;

                    assertEquals("pl6", placemark12.getProperty(KmlModelConstants.ATT_NAME.getName()).getValue());
                    assertTrue(placemark12.getProperty(KmlModelConstants.ATT_PLACEMARK_GEOMETRY.getName()).getValue() instanceof Point);
                    final CoordinateSequence coordinates12 = ((Point) placemark12.getProperty(KmlModelConstants.ATT_PLACEMARK_GEOMETRY.getName()).getValue()).getCoordinateSequence();
                    assertEquals(1, coordinates12.size());
                    assertEquals(-122.362865, coordinates12.getCoordinate(0).x, DELTA);
                    assertEquals(37.822941, coordinates12.getCoordinate(0).y, DELTA);
                    assertEquals(0, coordinates12.getCoordinate(0).z, DELTA);

                }
            }

            if (j.hasNext()){
                final Object obj = ((Property) j.next()).getValue();
                final Feature folder2 = (Feature) obj;

                assertEquals("radioFolder example", folder2.getProperty(KmlModelConstants.ATT_NAME.getName()).getValue());
                assertTrue((Boolean) folder2.getProperty(KmlModelConstants.ATT_OPEN.getName()).getValue());
                assertEquals(new URI("#radioFolderExample"), folder2.getProperty(KmlModelConstants.ATT_STYLE_URL.getName()).getValue());

                assertEquals(3, folder2.getProperties(KmlModelConstants.ATT_FOLDER_FEATURES.getName()).size());

                Iterator k = folder2.getProperties(KmlModelConstants.ATT_FOLDER_FEATURES.getName()).iterator();

                if(k.hasNext()){
                    final Object o = ((Property) k.next()).getValue();
                    assertTrue(o instanceof Feature);
                    final Feature placemark20 = (Feature) o;

                    assertEquals("pl7", placemark20.getProperty(KmlModelConstants.ATT_NAME.getName()).getValue());
                    assertTrue(placemark20.getProperty(KmlModelConstants.ATT_PLACEMARK_GEOMETRY.getName()).getValue() instanceof Point);
                    final CoordinateSequence coordinates20 = ((Point) placemark20.getProperty(KmlModelConstants.ATT_PLACEMARK_GEOMETRY.getName()).getValue()).getCoordinateSequence();
                    assertEquals(1, coordinates20.size());
                    assertEquals(-122.362875, coordinates20.getCoordinate(0).x, DELTA);
                    assertEquals(37.822951, coordinates20.getCoordinate(0).y, DELTA);
                    assertEquals(0, coordinates20.getCoordinate(0).z, DELTA);
                }

                if(k.hasNext()){
                    final Object o = ((Property) k.next()).getValue();
                    assertTrue(o instanceof Feature);
                    final Feature placemark21 = (Feature) o;

                    assertEquals("pl8", placemark21.getProperty(KmlModelConstants.ATT_NAME.getName()).getValue());
                    assertTrue(placemark21.getProperty(KmlModelConstants.ATT_PLACEMARK_GEOMETRY.getName()).getValue() instanceof Point);
                    final CoordinateSequence coordinates21 = ((Point) placemark21.getProperty(KmlModelConstants.ATT_PLACEMARK_GEOMETRY.getName()).getValue()).getCoordinateSequence();
                    assertEquals(1, coordinates21.size());
                    assertEquals(-122.362885, coordinates21.getCoordinate(0).x, DELTA);
                    assertEquals(37.822951, coordinates21.getCoordinate(0).y, DELTA);
                    assertEquals(0, coordinates21.getCoordinate(0).z, DELTA);

                }

                if(k.hasNext()){
                    final Object o = ((Property) k.next()).getValue();
                    assertTrue(o instanceof Feature);
                    final Feature placemark22 = (Feature) o;

                    assertEquals("pl9", placemark22.getProperty(KmlModelConstants.ATT_NAME.getName()).getValue());
                    assertTrue(placemark22.getProperty(KmlModelConstants.ATT_PLACEMARK_GEOMETRY.getName()).getValue() instanceof Point);
                    final CoordinateSequence coordinates22 = ((Point) placemark22.getProperty(KmlModelConstants.ATT_PLACEMARK_GEOMETRY.getName()).getValue()).getCoordinateSequence();
                    assertEquals(1, coordinates22.size());
                    assertEquals(-122.362895, coordinates22.getCoordinate(0).x, DELTA);
                    assertEquals(37.822951, coordinates22.getCoordinate(0).y, DELTA);
                    assertEquals(0, coordinates22.getCoordinate(0).z, DELTA);

                }
            }
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
        final Collection<Property> placemark00Properties = placemark00.getProperties();
        placemark00Properties.add(FF.createAttribute("pl1", KmlModelConstants.ATT_NAME, null));
        placemark00Properties.add(FF.createAttribute(point00, KmlModelConstants.ATT_PLACEMARK_GEOMETRY, null));

            final Feature placemark01 = kmlFactory.createPlacemark();
            final Collection<Property> placemark01Properties = placemark01.getProperties();
            placemark01Properties.add(FF.createAttribute("pl2", KmlModelConstants.ATT_NAME, null));
            placemark01Properties.add(FF.createAttribute(point01, KmlModelConstants.ATT_PLACEMARK_GEOMETRY, null));

        final Feature placemark02 = kmlFactory.createPlacemark();
        final Collection<Property> placemark02Properties = placemark02.getProperties();
        placemark02Properties.add(FF.createAttribute("pl3", KmlModelConstants.ATT_NAME, null));
        placemark02Properties.add(FF.createAttribute(point02, KmlModelConstants.ATT_PLACEMARK_GEOMETRY, null));

            final Feature placemark10 = kmlFactory.createPlacemark();
            final Collection<Property> placemark10Properties = placemark10.getProperties();
            placemark10Properties.add(FF.createAttribute("pl4", KmlModelConstants.ATT_NAME, null));
            placemark10Properties.add(FF.createAttribute(point10, KmlModelConstants.ATT_PLACEMARK_GEOMETRY, null));

        final Feature placemark11 = kmlFactory.createPlacemark();
        final Collection<Property> placemark11Properties = placemark11.getProperties();
        placemark11Properties.add(FF.createAttribute("pl5", KmlModelConstants.ATT_NAME, null));
        placemark11Properties.add(FF.createAttribute(point11, KmlModelConstants.ATT_PLACEMARK_GEOMETRY, null));

            final Feature placemark12 = kmlFactory.createPlacemark();
            final Collection<Property> placemark12Properties = placemark12.getProperties();
            placemark12Properties.add(FF.createAttribute("pl6", KmlModelConstants.ATT_NAME, null));
            placemark12Properties.add(FF.createAttribute(point12, KmlModelConstants.ATT_PLACEMARK_GEOMETRY, null));

        final Feature placemark20 = kmlFactory.createPlacemark();
        final Collection<Property> placemark20Properties = placemark20.getProperties();
        placemark20Properties.add(FF.createAttribute("pl7", KmlModelConstants.ATT_NAME, null));
        placemark20Properties.add(FF.createAttribute(point20, KmlModelConstants.ATT_PLACEMARK_GEOMETRY, null));

            final Feature placemark21 = kmlFactory.createPlacemark();
            final Collection<Property> placemark21Properties = placemark21.getProperties();
            placemark21Properties.add(FF.createAttribute("pl8", KmlModelConstants.ATT_NAME, null));
            placemark21Properties.add(FF.createAttribute(point21, KmlModelConstants.ATT_PLACEMARK_GEOMETRY, null));

        final Feature placemark22 = kmlFactory.createPlacemark();
        final Collection<Property> placemark22Properties = placemark22.getProperties();
        placemark22Properties.add(FF.createAttribute("pl9", KmlModelConstants.ATT_NAME, null));
        placemark22Properties.add(FF.createAttribute(point22, KmlModelConstants.ATT_PLACEMARK_GEOMETRY, null));

        final Feature folder0 = kmlFactory.createFolder();
        final Collection<Property> folder0Properties = folder0.getProperties();
        folder0Properties.add(FF.createAttribute("bgColor example", KmlModelConstants.ATT_NAME, null));
        folder0.getProperty(KmlModelConstants.ATT_OPEN.getName()).setValue(Boolean.TRUE);
        folder0Properties.add(FF.createAttribute(new URI("#bgColorExample"), KmlModelConstants.ATT_STYLE_URL, null));
        folder0Properties.add(FF.createAttribute(placemark00, KmlModelConstants.ATT_FOLDER_FEATURES, null));
        folder0Properties.add(FF.createAttribute(placemark01, KmlModelConstants.ATT_FOLDER_FEATURES, null));
        folder0Properties.add(FF.createAttribute(placemark02, KmlModelConstants.ATT_FOLDER_FEATURES, null));

        final Feature folder1 = kmlFactory.createFolder();
        final Collection<Property> folder1Properties = folder1.getProperties();      
        folder1Properties.add(FF.createAttribute("checkHideChildren example", KmlModelConstants.ATT_NAME, null));
        folder1.getProperty(KmlModelConstants.ATT_OPEN.getName()).setValue(Boolean.TRUE);
        folder1Properties.add(FF.createAttribute(new URI("#checkHideChildrenExample"), KmlModelConstants.ATT_STYLE_URL, null));
        folder1Properties.add(FF.createAttribute(placemark10, KmlModelConstants.ATT_FOLDER_FEATURES, null));
        folder1Properties.add(FF.createAttribute(placemark11, KmlModelConstants.ATT_FOLDER_FEATURES, null));
        folder1Properties.add(FF.createAttribute(placemark12, KmlModelConstants.ATT_FOLDER_FEATURES, null));

        final Feature folder2 = kmlFactory.createFolder();
        final Collection<Property> folder2Properties = folder2.getProperties();
        folder2Properties.add(FF.createAttribute("radioFolder example", KmlModelConstants.ATT_NAME, null));
        folder2.getProperty(KmlModelConstants.ATT_OPEN.getName()).setValue(Boolean.TRUE);
        folder2Properties.add(FF.createAttribute(new URI("#radioFolderExample"), KmlModelConstants.ATT_STYLE_URL, null));
        folder2Properties.add(FF.createAttribute(placemark20, KmlModelConstants.ATT_FOLDER_FEATURES, null));
        folder2Properties.add(FF.createAttribute(placemark21, KmlModelConstants.ATT_FOLDER_FEATURES, null));
        folder2Properties.add(FF.createAttribute(placemark22, KmlModelConstants.ATT_FOLDER_FEATURES, null));

        final Feature folder = kmlFactory.createFolder();
        final Collection<Property> folderProperties = folder.getProperties();
        folderProperties.add(FF.createAttribute("ListStyle Examples", KmlModelConstants.ATT_NAME, null));
        folder.getProperty(KmlModelConstants.ATT_OPEN.getName()).setValue(Boolean.TRUE);
        folderProperties.add(FF.createAttribute(folder0, KmlModelConstants.ATT_FOLDER_FEATURES, null));
        folderProperties.add(FF.createAttribute(folder1, KmlModelConstants.ATT_FOLDER_FEATURES, null));
        folderProperties.add(FF.createAttribute(folder2, KmlModelConstants.ATT_FOLDER_FEATURES, null));

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
        final Collection<Property> documentProperties = document.getProperties();
        documentProperties.add(FF.createAttribute("ListStyle.kml", KmlModelConstants.ATT_NAME, null));
        document.getProperty(KmlModelConstants.ATT_OPEN.getName()).setValue(Boolean.TRUE);
        documentProperties.add(FF.createAttribute(folder, KmlModelConstants.ATT_DOCUMENT_FEATURES, null));
        documentProperties.add(FF.createAttribute(style1, KmlModelConstants.ATT_STYLE_SELECTOR, null));
        documentProperties.add(FF.createAttribute(style2, KmlModelConstants.ATT_STYLE_SELECTOR, null));
        documentProperties.add(FF.createAttribute(style3, KmlModelConstants.ATT_STYLE_SELECTOR, null));

        final Kml kml = kmlFactory.createKml(null, document, null, null);

        final File temp = File.createTempFile("testListStyle", ".kml");
        temp.deleteOnExit();

        final KmlWriter writer = new KmlWriter();
        writer.setOutput(temp);
        writer.write(kml);
        writer.dispose();

        DomCompare.compare(
                new File(pathToTestFile), temp);
    }
}
