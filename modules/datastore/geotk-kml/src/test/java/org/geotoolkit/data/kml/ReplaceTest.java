package org.geotoolkit.data.kml;

import java.net.URISyntaxException;
import org.geotoolkit.data.kml.xml.KmlReader;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.Arrays;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.stream.XMLStreamException;
import org.geotoolkit.data.kml.model.GroundOverlay;
import org.geotoolkit.data.kml.model.Kml;
import org.geotoolkit.data.kml.model.KmlException;
import org.geotoolkit.data.kml.model.NetworkLinkControl;
import org.geotoolkit.data.kml.model.Placemark;
import org.geotoolkit.data.kml.model.Update;
import org.geotoolkit.data.kml.xml.KmlModelConstants;
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
public class ReplaceTest {

    private static final double DELTA = 0.000000000001;
    private static final String pathToTestFile = "src/test/resources/org/geotoolkit/data/kml/replace.kml";

    public ReplaceTest() {
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
    public void replaceReadTest() throws IOException, XMLStreamException {

        final KmlReader reader = new KmlReader();
        reader.setInput(new File(pathToTestFile));
        final Kml kmlObjects = reader.read();
        reader.dispose();

        final NetworkLinkControl networkLinkControl = kmlObjects.getNetworkLinkControl();
        final Update update = networkLinkControl.getUpdate();
        final URI targetHref = update.getTargetHref();
        assertEquals("http://chezmoi.com/tests.kml", targetHref.toString());

        assertEquals(2, update.getUpdates().size());
        assertTrue(update.getUpdates().get(0) instanceof Placemark);
        assertEquals("Replace placemark",((Placemark)update.getUpdates().get(0)).getName());

        assertTrue(update.getUpdates().get(1) instanceof GroundOverlay);
        assertEquals("Replace overlay",((GroundOverlay)update.getUpdates().get(1)).getName());

    }

    @Test
    public void replaceWriteTest() 
            throws KmlException, IOException,
            XMLStreamException, ParserConfigurationException,
            SAXException, URISyntaxException {

        final KmlFactory kmlFactory = new DefaultKmlFactory();

        final Placemark placemark = kmlFactory.createPlacemark();
        placemark.setName("Replace placemark");

        final GroundOverlay groundOverlay = kmlFactory.createGroundOverlay();
        groundOverlay.setName("Replace overlay");

        URI targetHref = new URI("http://chezmoi.com/tests.kml");

        Update update = kmlFactory.createUpdate();
        update.setTargetHref(targetHref);
        update.setUpdates(Arrays.asList((Object) placemark, (Object) groundOverlay));

        NetworkLinkControl networkLinkControl = kmlFactory.createNetworkLinkControl();
        networkLinkControl.setUpdate(update);


        final Kml kml = kmlFactory.createKml(networkLinkControl, null, null, null);
        kml.setVersion(KmlModelConstants.URI_KML_2_1);

        File temp = File.createTempFile("testReplace", ".kml");
        temp.deleteOnExit();

        KmlWriter writer = new KmlWriter();
        writer.setOutput(temp);
        writer.write(kml);
        writer.dispose();
        
        DomCompare.compare(
                new File(pathToTestFile), temp);

    }
}
