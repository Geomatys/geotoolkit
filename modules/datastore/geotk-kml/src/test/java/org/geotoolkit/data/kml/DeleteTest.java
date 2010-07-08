package org.geotoolkit.data.kml;

import java.net.URISyntaxException;
import org.geotoolkit.data.kml.xml.KmlReader;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.Arrays;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.stream.XMLStreamException;
import org.geotoolkit.data.kml.model.AbstractFeature;
import org.geotoolkit.data.kml.model.Delete;
import org.geotoolkit.data.kml.model.Kml;
import org.geotoolkit.data.kml.model.KmlException;
import org.geotoolkit.data.kml.model.NetworkLinkControl;
import org.geotoolkit.data.kml.model.Placemark;
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
public class DeleteTest {

    private static final double DELTA = 0.000000000001;
    private static final String pathToTestFile = "src/test/resources/org/geotoolkit/data/kml/delete.kml";

    public DeleteTest() {
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
    public void deleteReadTest() throws IOException, XMLStreamException {

        final KmlReader reader = new KmlReader();
        reader.setInput(new File(pathToTestFile));
        final Kml kmlObjects = reader.read();
        reader.dispose();

        final NetworkLinkControl networkLinkControl = kmlObjects.getNetworkLinkControl();
        final Update update = networkLinkControl.getUpdate();
        final URI targetHref = update.getTargetHref();
        assertEquals("http://www.foo.com/Point.kml", targetHref.toString());

        assertEquals(1, update.getUpdates().size());
        assertTrue(update.getUpdates().get(0) instanceof Delete);
        final Delete delete = (Delete) update.getUpdates().get(0);

        assertEquals(1, delete.getFeatures().size());
        assertTrue(delete.getFeatures().get(0) instanceof Placemark);
        final Placemark placemark = (Placemark) delete.getFeatures().get(0);
        assertEquals("pa3556", placemark.getIdAttributes().getTargetId());

    }

    @Test
    public void deleteWriteTest() throws KmlException, IOException, XMLStreamException, ParserConfigurationException, SAXException, URISyntaxException {
        final KmlFactory kmlFactory = new DefaultKmlFactory();

        final Placemark placemark = kmlFactory.createPlacemark();
        placemark.setIdAttributes(kmlFactory.createIdAttributes(null, "pa3556"));

        final Delete delete = kmlFactory.createDelete();
        delete.setFeatures(Arrays.asList((AbstractFeature) placemark));

        final URI targetHref = new URI("http://www.foo.com/Point.kml");

        final Update update = kmlFactory.createUpdate();
        update.setUpdates(Arrays.asList((Object) delete));
        update.setTargetHref(targetHref);

        final NetworkLinkControl networkLinkControl = kmlFactory.createNetworkLinkControl();
        networkLinkControl.setUpdate(update);


        final Kml kml = kmlFactory.createKml(networkLinkControl, null, null, null);

        final File temp = File.createTempFile("testDelete", ".kml");
        temp.deleteOnExit();

        KmlWriter writer = new KmlWriter();
        writer.setOutput(temp);
        writer.write(kml);
        writer.dispose();

        DomCompare.compare(
                new File(pathToTestFile), temp);

    }
}
