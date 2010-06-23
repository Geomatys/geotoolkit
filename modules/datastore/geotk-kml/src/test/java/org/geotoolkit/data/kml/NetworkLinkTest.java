package org.geotoolkit.data.kml;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.stream.XMLStreamException;
import org.geotoolkit.data.kml.model.AbstractFeature;
import org.geotoolkit.data.kml.model.Document;
import org.geotoolkit.data.kml.model.Kml;
import org.geotoolkit.data.kml.model.KmlException;
import org.geotoolkit.data.kml.model.NetworkLink;
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
public class NetworkLinkTest {

    private static final double DELTA = 0.000000000001;
    private static final String pathToTestFile = "src/test/resources/org/geotoolkit/data/kml/networkLink.kml";

    public NetworkLinkTest() {
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
    public void networkLinkReadTest() throws IOException, XMLStreamException {

        final KmlReader reader = new KmlReader();
        reader.setInput(new File(pathToTestFile));
        final Kml kmlObjects = reader.read();
        reader.dispose();

        final AbstractFeature feature = kmlObjects.getAbstractFeature();
        assertTrue(feature instanceof Document);
        Document document = (Document) feature;
        assertFalse(document.getVisibility());

        final List<AbstractFeature> features = document.getAbstractFeatures();
        assertEquals(1,features.size());
        assertTrue(features.get(0) instanceof NetworkLink);
        NetworkLink networkLink = (NetworkLink) features.get(0);
        assertEquals("NE US Radar",networkLink.getName());
        assertTrue(networkLink.getRefreshVisibility());
        assertTrue(networkLink.getFlyToView());

    }

    @Test
    public void networkLinkWriteTest() throws KmlException, IOException, XMLStreamException, ParserConfigurationException, SAXException {
        final KmlFactory kmlFactory = new DefaultKmlFactory();

        final NetworkLink networkLink = kmlFactory.createNetworkLink();
        networkLink.setName("NE US Radar");
        networkLink.setRefreshVisibility(true);
        networkLink.setFlyToView(true);

        final Document document = kmlFactory.createDocument();
        document.setVisibility(false);
        document.setAbstractFeatures(Arrays.asList((AbstractFeature) networkLink));

        final Kml kml = kmlFactory.createKml(null, document, null, null);

        File temp = File.createTempFile("testNetworkLink",".kml");
        temp.deleteOnExit();

        KmlWriter writer = new KmlWriter();
        writer.setOutput(temp);
        writer.write(kml);
        writer.dispose();

        DomCompare.compare(
                 new File(pathToTestFile), temp);
    }

}