package org.geotoolkit.data.kml;

import java.awt.Color;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.stream.XMLStreamException;
import org.geotoolkit.data.kml.model.AbstractFeature;
import org.geotoolkit.data.kml.model.AbstractStyleSelector;
import org.geotoolkit.data.kml.model.Coordinate;
import org.geotoolkit.data.kml.model.Coordinates;
import org.geotoolkit.data.kml.model.Document;
import org.geotoolkit.data.kml.model.Folder;
import org.geotoolkit.data.kml.model.IdAttributes;
import org.geotoolkit.data.kml.model.Kml;
import org.geotoolkit.data.kml.model.KmlException;
import org.geotoolkit.data.kml.model.ListItem;
import org.geotoolkit.data.kml.model.ListStyle;
import org.geotoolkit.data.kml.model.Placemark;
import org.geotoolkit.data.kml.model.Point;
import org.geotoolkit.data.kml.model.Style;
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
public class ListStyleTest {

    private static final double DELTA = 0.000000000001;
    private static final String pathToTestFile = "src/test/resources/org/geotoolkit/data/kml/listStyle.kml";

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
    public void listStyleReadTest() throws IOException, XMLStreamException {

        final KmlReader reader = new KmlReader();
        reader.setInput(new File(pathToTestFile));
        final Kml kmlObjects = reader.read();
        reader.dispose();

        final AbstractFeature feature = kmlObjects.getAbstractFeature();
        assertTrue(feature instanceof Document);
        final Document document = (Document) feature;
        assertEquals("ListStyle.kml",document.getName());
        assertTrue(document.getOpen());

        assertEquals(3, document.getStyleSelectors().size());

        assertTrue(document.getStyleSelectors().get(0) instanceof Style);
        final Style style0 = (Style) document.getStyleSelectors().get(0);
        assertEquals(new Color(153, 102, 51, 255), style0.getListStyle().getBgColor());
        assertEquals("bgColorExample",style0.getIdAttributes().getId());

        assertTrue(document.getStyleSelectors().get(1) instanceof Style);
        final Style style1 = (Style) document.getStyleSelectors().get(1);
        assertEquals(ListItem.CHECK_HIDE_CHILDREN, style1.getListStyle().getListItem());
        assertEquals("checkHideChildrenExample",style1.getIdAttributes().getId());

        assertTrue(document.getStyleSelectors().get(2) instanceof Style);
        final Style style2 = (Style) document.getStyleSelectors().get(2);
        assertEquals(ListItem.RADIO_FOLDER, style2.getListStyle().getListItem());
        assertEquals("radioFolderExample", style2.getIdAttributes().getId());

        assertEquals(1, document.getAbstractFeatures().size());
        assertTrue(document.getAbstractFeatures().get(0) instanceof Folder);
        final Folder folder = (Folder) document.getAbstractFeatures().get(0);

        assertEquals("ListStyle Examples", folder.getName());
        assertTrue(folder.getOpen());

        assertEquals(3, folder.getAbstractFeatures().size());

        assertTrue(folder.getAbstractFeatures().get(0) instanceof Folder);
        final Folder folder0 = (Folder) folder.getAbstractFeatures().get(0);
        assertEquals("bgColor example", folder0.getName());
        assertTrue(folder0.getOpen());
        assertEquals("#bgColorExample", folder0.getStyleUrl());

            assertEquals(3, folder0.getAbstractFeatures().size());

            assertTrue(folder0.getAbstractFeatures().get(0) instanceof Placemark);
            final Placemark placemark00 = (Placemark) folder0.getAbstractFeatures().get(0);
            assertEquals("pl1", placemark00.getName());
            assertTrue(placemark00.getAbstractGeometry() instanceof Point);
            final Coordinates coordinates00 = ((Point) placemark00.getAbstractGeometry()).getCoordinates();
            assertEquals(1, coordinates00.getCoordinates().size());
            assertEquals(-122.362815, coordinates00.getCoordinate(0).getGeodeticLongitude(), DELTA);
            assertEquals(37.822931, coordinates00.getCoordinate(0).getGeodeticLatitude(), DELTA);
            assertEquals(0, coordinates00.getCoordinate(0).getAltitude(), DELTA);
           
            assertTrue(folder0.getAbstractFeatures().get(1) instanceof Placemark);
            final Placemark placemark01 = (Placemark) folder0.getAbstractFeatures().get(1);
            assertEquals("pl2", placemark01.getName());
            assertTrue(placemark01.getAbstractGeometry() instanceof Point);
            final Coordinates coordinates01 = ((Point) placemark01.getAbstractGeometry()).getCoordinates();
            assertEquals(1, coordinates01.getCoordinates().size());
            assertEquals(-122.362825, coordinates01.getCoordinate(0).getGeodeticLongitude(), DELTA);
            assertEquals(37.822931, coordinates01.getCoordinate(0).getGeodeticLatitude(), DELTA);
            assertEquals(0, coordinates01.getCoordinate(0).getAltitude(), DELTA);

            assertTrue(folder0.getAbstractFeatures().get(2) instanceof Placemark);
            final Placemark placemark02 = (Placemark) folder0.getAbstractFeatures().get(2);
            assertEquals("pl3", placemark02.getName());
            assertTrue(placemark02.getAbstractGeometry() instanceof Point);
            final Coordinates coordinates02 = ((Point) placemark02.getAbstractGeometry()).getCoordinates();
            assertEquals(1, coordinates02.getCoordinates().size());
            assertEquals(-122.362835, coordinates02.getCoordinate(0).getGeodeticLongitude(), DELTA);
            assertEquals(37.822931, coordinates02.getCoordinate(0).getGeodeticLatitude(), DELTA);
            assertEquals(0, coordinates02.getCoordinate(0).getAltitude(), DELTA);

        assertTrue(folder.getAbstractFeatures().get(1) instanceof Folder);
        final Folder folder1 = (Folder) folder.getAbstractFeatures().get(1);
        assertEquals("checkHideChildren example", folder1.getName());
        assertTrue(folder1.getOpen());
        assertEquals("#checkHideChildrenExample", folder1.getStyleUrl());

        assertEquals(3, folder1.getAbstractFeatures().size());

            assertTrue(folder1.getAbstractFeatures().get(0) instanceof Placemark);
            final Placemark placemark10 = (Placemark) folder1.getAbstractFeatures().get(0);
            assertEquals("pl4", placemark10.getName());
            assertTrue(placemark10.getAbstractGeometry() instanceof Point);
            final Coordinates coordinates10 = ((Point) placemark10.getAbstractGeometry()).getCoordinates();
            assertEquals(1, coordinates10.getCoordinates().size());
            assertEquals(-122.362845, coordinates10.getCoordinate(0).getGeodeticLongitude(), DELTA);
            assertEquals(37.822941, coordinates10.getCoordinate(0).getGeodeticLatitude(), DELTA);
            assertEquals(0, coordinates10.getCoordinate(0).getAltitude(), DELTA);

            assertTrue(folder1.getAbstractFeatures().get(1) instanceof Placemark);
            final Placemark placemark11 = (Placemark) folder1.getAbstractFeatures().get(1);
            assertEquals("pl5", placemark11.getName());
            assertTrue(placemark11.getAbstractGeometry() instanceof Point);
            final Coordinates coordinates11 = ((Point) placemark11.getAbstractGeometry()).getCoordinates();
            assertEquals(1, coordinates11.getCoordinates().size());
            assertEquals(-122.362855, coordinates11.getCoordinate(0).getGeodeticLongitude(), DELTA);
            assertEquals(37.822941, coordinates11.getCoordinate(0).getGeodeticLatitude(), DELTA);
            assertEquals(0, coordinates11.getCoordinate(0).getAltitude(), DELTA);

            assertTrue(folder1.getAbstractFeatures().get(2) instanceof Placemark);
            final Placemark placemark12 = (Placemark) folder1.getAbstractFeatures().get(2);
            assertEquals("pl6", placemark12.getName());
            assertTrue(placemark12.getAbstractGeometry() instanceof Point);
            final Coordinates coordinates12 = ((Point) placemark12.getAbstractGeometry()).getCoordinates();
            assertEquals(1, coordinates12.getCoordinates().size());
            assertEquals(-122.362865, coordinates12.getCoordinate(0).getGeodeticLongitude(), DELTA);
            assertEquals(37.822941, coordinates12.getCoordinate(0).getGeodeticLatitude(), DELTA);
            assertEquals(0, coordinates12.getCoordinate(0).getAltitude(), DELTA);

        assertTrue(folder.getAbstractFeatures().get(2) instanceof Folder);
        final Folder folder2 = (Folder) folder.getAbstractFeatures().get(2);
        assertEquals("radioFolder example", folder2.getName());
        assertTrue(folder2.getOpen());
        assertEquals("#radioFolderExample", folder2.getStyleUrl());

        assertEquals(3, folder2.getAbstractFeatures().size());

            assertTrue(folder2.getAbstractFeatures().get(0) instanceof Placemark);
            final Placemark placemark20 = (Placemark) folder2.getAbstractFeatures().get(0);
            assertEquals("pl7", placemark20.getName());
            assertTrue(placemark20.getAbstractGeometry() instanceof Point);
            final Coordinates coordinates20 = ((Point) placemark20.getAbstractGeometry()).getCoordinates();
            assertEquals(1, coordinates20.getCoordinates().size());
            assertEquals(-122.362875, coordinates20.getCoordinate(0).getGeodeticLongitude(), DELTA);
            assertEquals(37.822951, coordinates20.getCoordinate(0).getGeodeticLatitude(), DELTA);
            assertEquals(0, coordinates20.getCoordinate(0).getAltitude(), DELTA);

            assertTrue(folder2.getAbstractFeatures().get(1) instanceof Placemark);
            final Placemark placemark21 = (Placemark) folder2.getAbstractFeatures().get(1);
            assertEquals("pl8", placemark21.getName());
            assertTrue(placemark21.getAbstractGeometry() instanceof Point);
            final Coordinates coordinates21 = ((Point) placemark21.getAbstractGeometry()).getCoordinates();
            assertEquals(1, coordinates21.getCoordinates().size());
            assertEquals(-122.362885, coordinates21.getCoordinate(0).getGeodeticLongitude(), DELTA);
            assertEquals(37.822951, coordinates21.getCoordinate(0).getGeodeticLatitude(), DELTA);
            assertEquals(0, coordinates21.getCoordinate(0).getAltitude(), DELTA);

            assertTrue(folder2.getAbstractFeatures().get(2) instanceof Placemark);
            final Placemark placemark22 = (Placemark) folder2.getAbstractFeatures().get(2);
            assertEquals("pl9", placemark22.getName());
            assertTrue(placemark22.getAbstractGeometry() instanceof Point);
            final Coordinates coordinates22 = ((Point) placemark22.getAbstractGeometry()).getCoordinates();
            assertEquals(1, coordinates22.getCoordinates().size());
            assertEquals(-122.362895, coordinates22.getCoordinate(0).getGeodeticLongitude(), DELTA);
            assertEquals(37.822951, coordinates22.getCoordinate(0).getGeodeticLatitude(), DELTA);
            assertEquals(0, coordinates22.getCoordinate(0).getAltitude(), DELTA);

    }

    @Test
    public void listStyleWriteTest() throws KmlException, IOException, XMLStreamException, ParserConfigurationException, SAXException {
        final KmlFactory kmlFactory = new DefaultKmlFactory();

        final Coordinate coordinate000 = kmlFactory.createCoordinate(-122.362815, 37.822931, 0);
        final Coordinate coordinate010 = kmlFactory.createCoordinate(-122.362825, 37.822931, 0);
        final Coordinate coordinate020 = kmlFactory.createCoordinate(-122.362835, 37.822931, 0);
            final Coordinate coordinate100 = kmlFactory.createCoordinate(-122.362845, 37.822941, 0);
            final Coordinate coordinate110 = kmlFactory.createCoordinate(-122.362855, 37.822941, 0);
            final Coordinate coordinate120 = kmlFactory.createCoordinate(-122.362865, 37.822941, 0);
        final Coordinate coordinate200 = kmlFactory.createCoordinate(-122.362875, 37.822951, 0);
        final Coordinate coordinate210 = kmlFactory.createCoordinate(-122.362885, 37.822951, 0);
        final Coordinate coordinate220 = kmlFactory.createCoordinate(-122.362895, 37.822951, 0);

        final Coordinates coordinates00 = kmlFactory.createCoordinates(Arrays.asList(coordinate000));
        final Coordinates coordinates01 = kmlFactory.createCoordinates(Arrays.asList(coordinate010));
        final Coordinates coordinates02 = kmlFactory.createCoordinates(Arrays.asList(coordinate020));
            final Coordinates coordinates10 = kmlFactory.createCoordinates(Arrays.asList(coordinate100));
            final Coordinates coordinates11 = kmlFactory.createCoordinates(Arrays.asList(coordinate110));
            final Coordinates coordinates12 = kmlFactory.createCoordinates(Arrays.asList(coordinate120));
        final Coordinates coordinates20 = kmlFactory.createCoordinates(Arrays.asList(coordinate200));
        final Coordinates coordinates21 = kmlFactory.createCoordinates(Arrays.asList(coordinate210));
        final Coordinates coordinates22 = kmlFactory.createCoordinates(Arrays.asList(coordinate220));

        final Point point00 = kmlFactory.createPoint();
        point00.setCoordinates(coordinates00);
            final Point point01 = kmlFactory.createPoint();
            point01.setCoordinates(coordinates01);
        final Point point02 = kmlFactory.createPoint();
        point02.setCoordinates(coordinates02);
            final Point point10 = kmlFactory.createPoint();
            point10.setCoordinates(coordinates10);
        final Point point11 = kmlFactory.createPoint();
        point11.setCoordinates(coordinates11);
            final Point point12 = kmlFactory.createPoint();
            point12.setCoordinates(coordinates12);
        final Point point20 = kmlFactory.createPoint();
        point20.setCoordinates(coordinates20);
            final Point point21 = kmlFactory.createPoint();
            point21.setCoordinates(coordinates21);
        final Point point22 = kmlFactory.createPoint();
        point22.setCoordinates(coordinates22);

        final Placemark placemark00 = kmlFactory.createPlacemark();
        placemark00.setName("pl1");
        placemark00.setAbstractGeometry(point00);
            final Placemark placemark01 = kmlFactory.createPlacemark();
            placemark01.setName("pl2");
            placemark01.setAbstractGeometry(point01);
        final Placemark placemark02 = kmlFactory.createPlacemark();
        placemark02.setName("pl3");
        placemark02.setAbstractGeometry(point02);
            final Placemark placemark10 = kmlFactory.createPlacemark();
            placemark10.setName("pl4");
            placemark10.setAbstractGeometry(point10);
        final Placemark placemark11 = kmlFactory.createPlacemark();
        placemark11.setName("pl5");
        placemark11.setAbstractGeometry(point11);
            final Placemark placemark12 = kmlFactory.createPlacemark();
            placemark12.setName("pl6");
            placemark12.setAbstractGeometry(point12);
        final Placemark placemark20 = kmlFactory.createPlacemark();
        placemark20.setName("pl7");
        placemark20.setAbstractGeometry(point20);
            final Placemark placemark21 = kmlFactory.createPlacemark();
            placemark21.setName("pl8");
            placemark21.setAbstractGeometry(point21);
        final Placemark placemark22 = kmlFactory.createPlacemark();
        placemark22.setName("pl9");
        placemark22.setAbstractGeometry(point22);

        final Folder folder0 = kmlFactory.createFolder();
        folder0.setName("bgColor example");
        folder0.setOpen(true);
        folder0.setStyleUrl("#bgColorExample");
        folder0.setAbstractFeatures(Arrays.asList(
                (AbstractFeature) placemark00,
                (AbstractFeature) placemark01,
                (AbstractFeature) placemark02));
        final Folder folder1 = kmlFactory.createFolder();
        folder1.setName("checkHideChildren example");
        folder1.setOpen(true);
        folder1.setStyleUrl("#checkHideChildrenExample");
        folder1.setAbstractFeatures(Arrays.asList(
                (AbstractFeature) placemark10,
                (AbstractFeature) placemark11,
                (AbstractFeature) placemark12));
        final Folder folder2 = kmlFactory.createFolder();
        folder2.setName("radioFolder example");
        folder2.setOpen(true);
        folder2.setStyleUrl("#radioFolderExample");
        folder2.setAbstractFeatures(Arrays.asList(
                (AbstractFeature) placemark20,
                (AbstractFeature) placemark21,
                (AbstractFeature) placemark22));

        final Folder folder = kmlFactory.createFolder();
        folder.setName("ListStyle Examples");
        folder.setOpen(true);
        folder.setAbstractFeatures(Arrays.asList(
                (AbstractFeature) folder0,
                (AbstractFeature) folder1,
                (AbstractFeature) folder2));

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

        final Document document = kmlFactory.createDocument();
        document.setName("ListStyle.kml");
        document.setOpen(true);
        document.setStyleSelectors(Arrays.asList(
                (AbstractStyleSelector) style1,
                (AbstractStyleSelector) style2,
                (AbstractStyleSelector) style3));
        document.setAbstractFeatures(Arrays.asList((AbstractFeature) folder));

        final Kml kml = kmlFactory.createKml(null, document, null, null);

        File temp = File.createTempFile("testListStyle", ".kml");
        temp.deleteOnExit();

        KmlWriter writer = new KmlWriter();
        writer.setOutput(temp);
        writer.write(kml);
        writer.dispose();

        DomCompare.compare(
                new File(pathToTestFile), temp);

    }
}