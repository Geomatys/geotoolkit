package org.geotoolkit.data.kml;

import java.net.URISyntaxException;
import org.geotoolkit.data.kml.xml.KmlReader;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.stream.XMLStreamException;
import org.geotoolkit.data.kml.model.AbstractFeature;
import org.geotoolkit.data.kml.model.Data;
import org.geotoolkit.data.kml.model.ExtendedData;
import org.geotoolkit.data.kml.model.Kml;
import org.geotoolkit.data.kml.model.KmlException;
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
public class DataTest {

    private static final double DELTA = 0.000000000001;
    private static final String pathToTestFile = "src/test/resources/org/geotoolkit/data/kml/data.kml";

    public DataTest() {
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
    public void dataReadTest() throws IOException, XMLStreamException {

        final KmlReader reader = new KmlReader();
        reader.setInput(new File(pathToTestFile));
        final Kml kmlObjects = reader.read();
        reader.dispose();

        final AbstractFeature feature = kmlObjects.getAbstractFeature();
        assertTrue(feature instanceof Placemark);
        Placemark placemark = (Placemark) feature;
        assertEquals("Club house", placemark.getName());

        final Object  dataContainer = placemark.getExtendedData();
        assertTrue(dataContainer instanceof ExtendedData);
        final ExtendedData extendedData = (ExtendedData) placemark.getExtendedData();
        assertEquals(3, extendedData.getDatas().size());

        final Data data0 = extendedData.getDatas().get(0);
        final Data data1 = extendedData.getDatas().get(1);
        final Data data2 = extendedData.getDatas().get(2);

        assertEquals("holeNumber", data0.getName());
        assertEquals("1", data0.getValue());
        assertEquals("holeYardage", data1.getName());
        assertEquals("234", data1.getValue());
        assertEquals("holePar", data2.getName());
        assertEquals("4", data2.getValue());

    }

    @Test
    public void dataWriteTest() throws KmlException, IOException, XMLStreamException, ParserConfigurationException, SAXException, URISyntaxException {
        final KmlFactory kmlFactory = new DefaultKmlFactory();

        Placemark placemark = kmlFactory.createPlacemark();

        ExtendedData extendedData = kmlFactory.createExtendedData();

        Data data0 = kmlFactory.createData();
        data0.setName("holeNumber");
        data0.setValue("1");

        Data data1 = kmlFactory.createData();
        data1.setName("holeYardage");
        data1.setValue("234");

        Data data2 = kmlFactory.createData();
        data2.setName("holePar");
        data2.setValue("4");

        extendedData.setDatas(Arrays.asList(data0, data1, data2));

        placemark.setName("Club house");
        placemark.setExtendedData(extendedData);

        final Kml kml = kmlFactory.createKml(null, placemark, null, null);

        File temp = File.createTempFile("testData", ".kml");
        temp.deleteOnExit();

        KmlWriter writer = new KmlWriter();
        writer.setOutput(temp);
        writer.write(kml);
        writer.dispose();

        DomCompare.compare(
                new File(pathToTestFile), temp);

    }
}
