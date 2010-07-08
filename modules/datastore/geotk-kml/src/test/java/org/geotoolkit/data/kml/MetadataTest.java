package org.geotoolkit.data.kml;

import java.net.URISyntaxException;
import org.geotoolkit.data.kml.xml.KmlReader;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.stream.XMLStreamException;
import org.geotoolkit.data.kml.model.AbstractFeature;
import org.geotoolkit.data.kml.model.Document;
import org.geotoolkit.data.kml.model.ExtendedData;
import org.geotoolkit.data.kml.model.Kml;
import org.geotoolkit.data.kml.model.KmlException;
import org.geotoolkit.data.kml.model.Metadata;
import org.geotoolkit.data.kml.model.Placemark;
import org.geotoolkit.data.kml.model.Snippet;
import org.geotoolkit.data.kml.xml.KmlWriter;
import org.geotoolkit.data.utilities.DefaultCdata;
import org.geotoolkit.xml.DomCompare;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import org.xml.sax.SAXException;
import static java.util.Collections.*;

/**
 *
 * @author Samuel Andrés
 */
public class MetadataTest {

    private static final double DELTA = 0.000000000001;
    private static final String pathToTestFile = "src/test/resources/org/geotoolkit/data/kml/metadata.kml";

    public MetadataTest() {
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
    public void metadataReadTest() throws IOException, XMLStreamException {

        final KmlReader reader = new KmlReader();
        reader.setInput(new File(pathToTestFile));
        final Kml kmlObjects = reader.read();
        reader.dispose();

        final AbstractFeature feature = kmlObjects.getAbstractFeature();
        assertTrue(feature instanceof Document);

        final Document document = (Document) feature;
        assertEquals("Document.kml", document.getName());
        assertTrue(document.getOpen());
        assertEquals(2,document.getAbstractFeatures().size());

        assertTrue(document.getAbstractFeatures().get(0) instanceof Placemark);
        final Placemark placemark0 = (Placemark) document.getAbstractFeatures().get(0);
        assertTrue(placemark0.getExtendedData() instanceof ExtendedData);
        assertEquals(EMPTY_LIST, ((ExtendedData) placemark0.getExtendedData()).getDatas());
        assertEquals(EMPTY_LIST, ((ExtendedData) placemark0.getExtendedData()).getSchemaData());
        assertEquals(EMPTY_LIST, ((ExtendedData) placemark0.getExtendedData()).getAnyOtherElements());

        assertTrue(document.getAbstractFeatures().get(1) instanceof Placemark);
        final Placemark placemark1 = (Placemark) document.getAbstractFeatures().get(1);
        assertTrue(placemark1.getExtendedData() instanceof Metadata);
        assertEquals(EMPTY_LIST, ((Metadata) placemark1.getExtendedData()).getContent());

    }

    @Test
    public void metadataWriteTest() throws KmlException, IOException, XMLStreamException, ParserConfigurationException, SAXException, URISyntaxException {
        final KmlFactory kmlFactory = new DefaultKmlFactory();

        final Placemark placemark0 = kmlFactory.createPlacemark();
        final ExtendedData extendedData = kmlFactory.createExtendedData();
        placemark0.setExtendedData(extendedData);

        final Placemark placemark1 = kmlFactory.createPlacemark();
        final Metadata metadata = kmlFactory.createMetadata();
        placemark1.setExtendedData(metadata);

        final Document document = kmlFactory.createDocument();
        document.setAbstractFeatures(Arrays.asList((AbstractFeature) placemark0,
                (AbstractFeature) placemark1));
        document.setOpen(true);
        document.setName("Document.kml");

        final Kml kml = kmlFactory.createKml(null, document, null, null);

        File temp = File.createTempFile("testMetadata", ".kml");
        temp.deleteOnExit();

        KmlWriter writer = new KmlWriter();
        writer.setOutput(temp);
        writer.write(kml);
        writer.dispose();

        DomCompare.compare(
                new File(pathToTestFile), temp);

    }
}
