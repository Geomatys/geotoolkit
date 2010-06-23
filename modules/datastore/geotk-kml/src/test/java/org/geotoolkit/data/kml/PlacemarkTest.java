package org.geotoolkit.data.kml;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.stream.XMLStreamException;
import org.geotoolkit.data.kml.model.AbstractFeature;
import org.geotoolkit.data.kml.model.AbstractGeometry;
import org.geotoolkit.data.kml.model.AbstractView;
import org.geotoolkit.data.kml.model.Coordinate;
import org.geotoolkit.data.kml.model.Coordinates;
import org.geotoolkit.data.kml.model.Document;
import org.geotoolkit.data.kml.model.Kml;
import org.geotoolkit.data.kml.model.KmlException;
import org.geotoolkit.data.kml.model.LookAt;
import org.geotoolkit.data.kml.model.NetworkLink;
import org.geotoolkit.data.kml.model.Placemark;
import org.geotoolkit.data.kml.model.Point;
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
public class PlacemarkTest {

    private static final double DELTA = 0.000000000001;
    private static final String pathToTestFile = "src/test/resources/org/geotoolkit/data/kml/placemark.kml";

    public PlacemarkTest() {
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
    public void placemarkReadTest() throws IOException, XMLStreamException {

        final KmlReader reader = new KmlReader();
        reader.setInput(new File(pathToTestFile));
        final Kml kmlObjects = reader.read();
        reader.dispose();

        final AbstractFeature feature = kmlObjects.getAbstractFeature();
        assertTrue(feature instanceof Placemark);
        Placemark placemark = (Placemark) feature;
        assertEquals("Google Earth - New Placemark",placemark.getName());
        assertEquals("Some Descriptive text.",placemark.getDescription());

        final AbstractView view = placemark.getView();
        assertTrue(view instanceof LookAt);
        LookAt lookAt = (LookAt) view;
        assertEquals(-90.86879847669974, lookAt.getLongitude(), DELTA);
        assertEquals(48.25330383601299, lookAt.getLatitude(), DELTA);
        assertEquals(2.7, lookAt.getHeading(), DELTA);
        assertEquals(8.3, lookAt.getTilt(), DELTA);
        assertEquals(440.8, lookAt.getRange(), DELTA);

        final AbstractGeometry geometry = placemark.getAbstractGeometry();
        assertTrue(geometry instanceof Point);
        Point point = (Point) geometry;
        final Coordinates coordinates = point.getCoordinates();
        assertEquals(1, coordinates.getCoordinates().size());
        Coordinate coordinate = coordinates.getCoordinate(0);
        assertEquals(-90.86948943473118, coordinate.getGeodeticLongitude(), DELTA);
        assertEquals(48.25450093195546, coordinate.getGeodeticLatitude(), DELTA);
        assertEquals(0, coordinate.getAltitude(), DELTA);
    }

    @Test
    public void placemarkWriteTest() throws KmlException, IOException, XMLStreamException, ParserConfigurationException, SAXException {
        final KmlFactory kmlFactory = new DefaultKmlFactory();

        final Coordinate coordinate = kmlFactory.createCoordinate(-90.86948943473118, 48.25450093195546, 0);
        final Coordinates coordinates = kmlFactory.createCoordinates(Arrays.asList(coordinate));
        final Point point = kmlFactory.createPoint();
        point.setCoordinates(coordinates);

        final LookAt lookAt = kmlFactory.createLookAt();
        lookAt.setLongitude(-90.86879847669974);
        lookAt.setLatitude(48.25330383601299);
        lookAt.setHeading(2.7);
        lookAt.setTilt(8.3);
        lookAt.setRange(440.8);

        final Placemark placemark = kmlFactory.createPlacemark();
        placemark.setName("Google Earth - New Placemark");
        placemark.setDescription("Some Descriptive text.");
        placemark.setView(lookAt);
        placemark.setAbstractGeometry(point);

        final Kml kml = kmlFactory.createKml(null, placemark, null, null);

        File temp = File.createTempFile("testPlacemark",".kml");
        temp.deleteOnExit();

        KmlWriter writer = new KmlWriter();
        writer.setOutput(temp);
        writer.write(kml);
        writer.dispose();

        DomCompare.compare(
                 new File(pathToTestFile), temp);
    }

}