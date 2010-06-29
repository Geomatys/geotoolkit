package org.geotoolkit.data.kml;

import org.geotoolkit.data.kml.xml.KmlReader;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.stream.XMLStreamException;
import org.geotoolkit.data.kml.model.AbstractFeature;
import org.geotoolkit.data.kml.model.AltitudeMode;
import org.geotoolkit.data.kml.model.Coordinate;
import org.geotoolkit.data.kml.model.Coordinates;
import org.geotoolkit.data.kml.model.Document;
import org.geotoolkit.data.kml.model.Kml;
import org.geotoolkit.data.kml.model.KmlException;
import org.geotoolkit.data.kml.model.LineString;
import org.geotoolkit.data.kml.model.LookAt;
import org.geotoolkit.data.kml.model.Placemark;
import org.geotoolkit.data.kml.xml.KmlWriter;
import org.geotoolkit.xml.DomCompare;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import org.xml.sax.SAXException;


/**
 *
 * @author Samuel Andrés
 */
public class LineStringTest {

    private static final double DELTA = 0.000000000001;
    private static final String pathToTestFile = "src/test/resources/org/geotoolkit/data/kml/lineString.kml";

    public LineStringTest() {
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
    public void lineStringReadTest() throws IOException, XMLStreamException {

        final KmlReader reader = new KmlReader();
        reader.setInput(new File(pathToTestFile));
        final Kml kmlObjects = reader.read();
        reader.dispose();

        final AbstractFeature feature = kmlObjects.getAbstractFeature();
        assertTrue(feature instanceof Document);

        final Document document = (Document) feature;
        assertEquals("LineString.kml", document.getName());
        assertTrue(document.getOpen());

        assertTrue(document.getView() instanceof LookAt);
        final LookAt lookAt = (LookAt) document.getView();

        assertEquals(-122.36415, lookAt.getLongitude(), DELTA);
        assertEquals(37.824553, lookAt.getLatitude(), DELTA);
        assertEquals(1, lookAt.getAltitude(), DELTA);
        assertEquals(2, lookAt.getHeading(), DELTA);
        assertEquals(50, lookAt.getTilt(), DELTA);
        assertEquals(150, lookAt.getRange(), DELTA);


        assertEquals(2, document.getAbstractFeatures().size());
        assertTrue(document.getAbstractFeatures().get(0) instanceof Placemark);

        final Placemark placemark0 = (Placemark) document.getAbstractFeatures().get(0);
        final Placemark placemark1 = (Placemark) document.getAbstractFeatures().get(1);

        assertEquals("unextruded", placemark0.getName());
        assertTrue(placemark0.getAbstractGeometry() instanceof LineString);
        final LineString lineString0 = (LineString) placemark0.getAbstractGeometry();
        assertTrue(lineString0.getExtrude());
        assertTrue(lineString0.getTessellate());

        final Coordinates coordinates0 = lineString0.getCoordinates();
        assertEquals(2, coordinates0.getCoordinates().size());

        final Coordinate coordinate00 = coordinates0.getCoordinate(0);
        assertEquals(-122.364383, coordinate00.getGeodeticLongitude(), DELTA);
        assertEquals(37.824664, coordinate00.getGeodeticLatitude(), DELTA);
        assertEquals(0, coordinate00.getAltitude(), DELTA);

        final Coordinate coordinate01 = coordinates0.getCoordinate(1);
        assertEquals(-122.364152, coordinate01.getGeodeticLongitude(), DELTA);
        assertEquals(37.824322, coordinate01.getGeodeticLatitude(), DELTA);
        assertEquals(0, coordinate01.getAltitude(), DELTA);

        assertEquals("extruded", placemark1.getName());
        assertTrue(placemark1.getAbstractGeometry() instanceof LineString);
        final LineString lineString1 = (LineString) placemark1.getAbstractGeometry();
        assertTrue(lineString1.getExtrude());
        assertTrue(lineString1.getTessellate());
        assertEquals(AltitudeMode.RELATIVE_TO_GROUND, lineString1.getAltitudeMode());

        final Coordinates coordinates1 = lineString1.getCoordinates();
        assertEquals(2, coordinates1.getCoordinates().size());

        final Coordinate coordinate10 = coordinates1.getCoordinate(0);
        assertEquals(-122.364167, coordinate10.getGeodeticLongitude(), DELTA);
        assertEquals(37.824787, coordinate10.getGeodeticLatitude(), DELTA);
        assertEquals(50, coordinate10.getAltitude(), DELTA);

        final Coordinate coordinate11 = coordinates1.getCoordinate(1);
        assertEquals(-122.363917, coordinate11.getGeodeticLongitude(), DELTA);
        assertEquals(37.824423, coordinate11.getGeodeticLatitude(), DELTA);
        assertEquals(50, coordinate11.getAltitude(), DELTA);

    }

    @Test
    public void lineStringWriteTest() throws KmlException, IOException, XMLStreamException, ParserConfigurationException, SAXException {
        final KmlFactory kmlFactory = new DefaultKmlFactory();

        final Coordinate coordinate00 = kmlFactory.createCoordinate(-122.364383,37.824664,0.0);
        final Coordinate coordinate01 = kmlFactory.createCoordinate(-122.364152,37.824322,0.0);
        final Coordinate coordinate10 = kmlFactory.createCoordinate(-122.364167,37.824787,50.0);
        final Coordinate coordinate11 = kmlFactory.createCoordinate(-122.363917,37.824423,50.0);

        final Coordinates coordinates0 = kmlFactory.createCoordinates(Arrays.asList(coordinate00, coordinate01));
        final Coordinates coordinates1 = kmlFactory.createCoordinates(Arrays.asList(coordinate10, coordinate11));

        final LineString lineString0 = kmlFactory.createLineString();
        lineString0.setCoordinates(coordinates0);
        lineString0.setExtrude(true);
        lineString0.setTessellate(true);

        final LineString lineString1 = kmlFactory.createLineString();
        lineString1.setCoordinates(coordinates1);
        lineString1.setExtrude(true);
        lineString1.setTessellate(true);
        lineString1.setAltitudeMode(AltitudeMode.RELATIVE_TO_GROUND);

        final Placemark placemark0 = kmlFactory.createPlacemark();
        placemark0.setName("unextruded");
        placemark0.setAbstractGeometry(lineString0);

        final Placemark placemark1 = kmlFactory.createPlacemark();
        placemark1.setName("extruded");
        placemark1.setAbstractGeometry(lineString1);

        final LookAt lookAt = kmlFactory.createLookAt();
        lookAt.setLongitude(-122.36415);
        lookAt.setLatitude(37.824553);
        lookAt.setAltitude(1);
        lookAt.setHeading(2);
        lookAt.setTilt(50);
        lookAt.setRange(150);

        Document document = kmlFactory.createDocument();
        document.setName("LineString.kml");
        document.setOpen(true);
        document.setView(lookAt);
        document.setAbstractFeatures(Arrays.asList((AbstractFeature) placemark0, (AbstractFeature) placemark1));


        final Kml kml = kmlFactory.createKml(null, document, null, null);

        File temp = File.createTempFile("testLineString", ".kml");
        temp.deleteOnExit();

        KmlWriter writer = new KmlWriter();
        writer.setOutput(temp);
        writer.write(kml);
        writer.dispose();

        DomCompare.compare(
                new File(pathToTestFile), temp);

    }
}
