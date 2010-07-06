package org.geotoolkit.data.kml;

import com.vividsolutions.jts.geom.Coordinate;
import org.geotoolkit.data.kml.xml.KmlReader;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.stream.XMLStreamException;
import org.geotoolkit.data.kml.model.AbstractFeature;
import org.geotoolkit.data.kml.model.AltitudeMode;
import org.geotoolkit.data.kml.model.Boundary;
import org.geotoolkit.data.kml.model.Coordinates;
import org.geotoolkit.data.kml.model.Document;
import org.geotoolkit.data.kml.model.Kml;
import org.geotoolkit.data.kml.model.KmlException;
import org.geotoolkit.data.kml.model.LinearRing;
import org.geotoolkit.data.kml.model.Placemark;
import org.geotoolkit.data.kml.model.Polygon;
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
public class PolygonTest {

    private static final double DELTA = 0.000000000001;
    private static final String pathToTestFile = "src/test/resources/org/geotoolkit/data/kml/polygon.kml";

    public PolygonTest() {
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
    public void polygonReadTest() throws IOException, XMLStreamException {

        final KmlReader reader = new KmlReader();
        reader.setInput(new File(pathToTestFile));
        final Kml kmlObjects = reader.read();
        reader.dispose();

        final AbstractFeature feature = kmlObjects.getAbstractFeature();
        assertTrue(feature instanceof Document);
        final Document document = (Document) feature;
        assertEquals("Polygon.kml", document.getName());
        assertTrue(document.getOpen());
        assertEquals(1,document.getAbstractFeatures().size());
        assertTrue(document.getAbstractFeatures().get(0) instanceof Placemark);
        final Placemark placemark = (Placemark) document.getAbstractFeatures().get(0);
        assertEquals("hollow box", placemark.getName());
        assertTrue(placemark.getAbstractGeometry() instanceof Polygon);
        final Polygon polygon = (Polygon) placemark.getAbstractGeometry();
        assertTrue(polygon.getExtrude());
        assertEquals(AltitudeMode.RELATIVE_TO_GROUND, polygon.getAltitudeMode());

        final Boundary outerBoundaryIs = polygon.getOuterBoundary();
        final LinearRing linearRing1 = outerBoundaryIs.getLinearRing();
        final Coordinates coordinates1 = linearRing1.getCoordinateSequence();
        assertEquals(5, coordinates1.size());

        final Coordinate coordinate10 = coordinates1.getCoordinate(0);
        assertEquals(-122.366278, coordinate10.x, DELTA);
        assertEquals(37.818844, coordinate10.y, DELTA);
        assertEquals(30, coordinate10.z, DELTA);

        final Coordinate coordinate11 = coordinates1.getCoordinate(1);
        assertEquals(-122.365248, coordinate11.x, DELTA);
        assertEquals(37.819267, coordinate11.y, DELTA);
        assertEquals(30, coordinate11.z, DELTA);

        final Coordinate coordinate12 = coordinates1.getCoordinate(2);
        assertEquals(-122.365640, coordinate12.x, DELTA);
        assertEquals(37.819861, coordinate12.y, DELTA);
        assertEquals(30, coordinate12.z, DELTA);

        final Coordinate coordinate13 = coordinates1.getCoordinate(3);
        assertEquals(-122.366669, coordinate13.x, DELTA);
        assertEquals(37.819429, coordinate13.y, DELTA);
        assertEquals(30, coordinate13.z, DELTA);

        final Coordinate coordinate14 = coordinates1.getCoordinate(4);
        assertEquals(-122.366278, coordinate14.x, DELTA);
        assertEquals(37.818844, coordinate14.y, DELTA);
        assertEquals(30, coordinate14.z, DELTA);

        assertEquals(1, polygon.getInnerBoundaries().size());
        final Boundary innerBoundaryIs = polygon.getInnerBoundaries().get(0);
        final LinearRing linearRing2 = innerBoundaryIs.getLinearRing();
        final Coordinates coordinates2 = linearRing2.getCoordinateSequence();
        assertEquals(5, coordinates2.size());

        final Coordinate coordinate20 = coordinates2.getCoordinate(0);
        assertEquals(-122.366212, coordinate20.x, DELTA);
        assertEquals(37.818977, coordinate20.y, DELTA);
        assertEquals(30, coordinate20.z, DELTA);

        final Coordinate coordinate21 = coordinates2.getCoordinate(1);
        assertEquals(-122.365424, coordinate21.x, DELTA);
        assertEquals(37.819294, coordinate21.y, DELTA);
        assertEquals(30, coordinate21.z, DELTA);

        final Coordinate coordinate22 = coordinates2.getCoordinate(2);
        assertEquals(-122.365704, coordinate22.x, DELTA);
        assertEquals(37.819731, coordinate22.y, DELTA);
        assertEquals(30, coordinate22.z, DELTA);

        final Coordinate coordinate23 = coordinates2.getCoordinate(3);
        assertEquals(-122.366488, coordinate23.x, DELTA);
        assertEquals(37.819402, coordinate23.y, DELTA);
        assertEquals(30, coordinate23.z, DELTA);

        final Coordinate coordinate24 = coordinates2.getCoordinate(4);
        assertEquals(-122.366212, coordinate24.x, DELTA);
        assertEquals(37.818977, coordinate24.y, DELTA);
        assertEquals(30, coordinate24.z, DELTA);


    }

    @Test
    public void polygonWriteTest() throws KmlException, IOException, XMLStreamException, ParserConfigurationException, SAXException {
        final KmlFactory kmlFactory = new DefaultKmlFactory();

        final Coordinate coordinate10 = kmlFactory.createCoordinate("-122.366278,37.818844,30.0");
        final Coordinate coordinate11 = kmlFactory.createCoordinate("-122.365248,37.819267,30.0");
        final Coordinate coordinate12 = kmlFactory.createCoordinate("-122.365640,37.819861,30.0");
        final Coordinate coordinate13 = kmlFactory.createCoordinate("-122.366669,37.819429,30.0");
        final Coordinate coordinate14 = kmlFactory.createCoordinate("-122.366278,37.818844,30.0");

        final Coordinate coordinate20 = kmlFactory.createCoordinate("-122.366212,37.818977,30.0");
        final Coordinate coordinate21 = kmlFactory.createCoordinate("-122.365424,37.819294,30.0");
        final Coordinate coordinate22 = kmlFactory.createCoordinate("-122.365704,37.819731,30.0");
        final Coordinate coordinate23 = kmlFactory.createCoordinate("-122.366488,37.819402,30.0");
        final Coordinate coordinate24 = kmlFactory.createCoordinate("-122.366212,37.818977,30.0");

        final Coordinates coordinates1 = kmlFactory.createCoordinates(
                Arrays.asList(coordinate10, coordinate11, coordinate12, coordinate13, coordinate14));
        
        final Coordinates coordinates2 = kmlFactory.createCoordinates(
                Arrays.asList(coordinate20, coordinate21, coordinate22, coordinate23, coordinate24));
        
        final LinearRing linearRing1 = kmlFactory.createLinearRing(coordinates1);
        
        final  LinearRing linearRing2 = kmlFactory.createLinearRing(coordinates2);
        
        final Boundary outerBoundaryIs = kmlFactory.createBoundary();
        outerBoundaryIs.setLinearRing(linearRing1);

        final Boundary innerBoundaryIs = kmlFactory.createBoundary();
        innerBoundaryIs.setLinearRing(linearRing2);

        final Polygon polygon = kmlFactory.createPolygon(outerBoundaryIs, Arrays.asList(innerBoundaryIs));
        polygon.setExtrude(true);
        polygon.setAltitudeMode(AltitudeMode.RELATIVE_TO_GROUND);

        final Placemark placemark = kmlFactory.createPlacemark();
        placemark.setName("hollow box");
        placemark.setAbstractGeometry(polygon);

        final Document document = kmlFactory.createDocument();
        document.setName("Polygon.kml");
        document.setOpen(true);
        document.setAbstractFeatures(Arrays.asList((AbstractFeature) placemark));


        final Kml kml = kmlFactory.createKml(null, document, null, null);

        File temp = File.createTempFile("testPolygon", ".kml");
        temp.deleteOnExit();

        KmlWriter writer = new KmlWriter();
        writer.setOutput(temp);
        writer.write(kml);
        writer.dispose();

        DomCompare.compare(
                new File(pathToTestFile), temp);

    }
}
