package org.geotoolkit.data.kml;

import com.vividsolutions.jts.geom.Coordinate;
import java.net.URISyntaxException;
import org.geotoolkit.data.kml.xml.KmlReader;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.Arrays;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.stream.XMLStreamException;
import org.geotoolkit.data.kml.model.AbstractContainer;
import org.geotoolkit.data.kml.model.AbstractFeature;
import org.geotoolkit.data.kml.model.Coordinates;
import org.geotoolkit.data.kml.model.Create;
import org.geotoolkit.data.kml.model.Document;
import org.geotoolkit.data.kml.model.Kml;
import org.geotoolkit.data.kml.model.KmlException;
import org.geotoolkit.data.kml.model.NetworkLinkControl;
import org.geotoolkit.data.kml.model.Placemark;
import org.geotoolkit.data.kml.model.Point;
import org.geotoolkit.data.kml.model.Update;
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
public class CreateTest {

    private static final double DELTA = 0.000000000001;
    private static final String pathToTestFile = "src/test/resources/org/geotoolkit/data/kml/create.kml";

    public CreateTest() {
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
    public void createReadTest() throws IOException, XMLStreamException {

        final KmlReader reader = new KmlReader();
        reader.setInput(new File(pathToTestFile));
        final Kml kmlObjects = reader.read();
        reader.dispose();

        final NetworkLinkControl networkLinkControl = kmlObjects.getNetworkLinkControl();
        final Update update = networkLinkControl.getUpdate();
        final URI targetHref = update.getTargetHref();
        assertEquals("http://myserver.com/Point.kml", targetHref.toString());

        assertEquals(1, update.getCreates().size());
        Create create = update.getCreates().get(0);

        assertEquals(1, create.getContainers().size());
        assertTrue(create.getContainers().get(0) instanceof Document);
        final Document document = (Document) create.getContainers().get(0);
        assertEquals("region24", document.getIdAttributes().getTargetId());

        assertEquals(1, document.getAbstractFeatures().size());
        assertTrue(document.getAbstractFeatures().get(0) instanceof Placemark);
        final Placemark placemark = (Placemark) document.getAbstractFeatures().get(0);
        assertTrue(placemark.getAbstractGeometry() instanceof Point);
        assertEquals("placemark891",placemark.getIdAttributes().getId());

        final Point point = (Point) placemark.getAbstractGeometry();
        final Coordinates coordinates = point.getCoordinateSequence();
        assertEquals(1, coordinates.size());

        final Coordinate coordinate = coordinates.getCoordinate(0);
        assertEquals(-95.48, coordinate.x, DELTA);
        assertEquals(40.43, coordinate.y, DELTA);
        assertEquals(0, coordinate.z, DELTA);

    }

    @Test
    public void createWriteTest() throws KmlException, IOException, XMLStreamException, ParserConfigurationException, SAXException, URISyntaxException {
        final KmlFactory kmlFactory = new DefaultKmlFactory();

        Coordinate coordinate = kmlFactory.createCoordinate(-95.48, 40.43, 0);
        Coordinates coordinates = kmlFactory.createCoordinates(Arrays.asList(coordinate));

        Point point = kmlFactory.createPoint(coordinates);

        Placemark placemark = kmlFactory.createPlacemark();
        placemark.setIdAttributes(kmlFactory.createIdAttributes("placemark891", null));
        placemark.setAbstractGeometry(point);

        Document document = kmlFactory.createDocument();
        document.setAbstractFeatures(Arrays.asList((AbstractFeature) placemark));
        document.setIdAttributes(kmlFactory.createIdAttributes(null, "region24"));

        Create create = kmlFactory.createCreate();
        create.setContainers(Arrays.asList((AbstractContainer) document));

        URI targetHref = new URI("http://myserver.com/Point.kml");

        Update update = kmlFactory.createUpdate();
        update.setCreates(Arrays.asList(create));
        update.setTargetHref(targetHref);

        NetworkLinkControl networkLinkControl = kmlFactory.createNetworkLinkControl();
        networkLinkControl.setUpdate(update);


        final Kml kml = kmlFactory.createKml(networkLinkControl, null, null, null);

        File temp = File.createTempFile("testCreate", ".kml");
        temp.deleteOnExit();

        KmlWriter writer = new KmlWriter();
        writer.setOutput(temp);
        writer.write(kml);
        writer.dispose();

        DomCompare.compare(
                new File(pathToTestFile), temp);

    }
}
