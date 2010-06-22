package org.geotoolkit.data.kml;

import org.geotoolkit.data.kml.xml.KmlReader;
import org.geotoolkit.data.kml.xml.KmlWriter;
import java.io.File;
import java.io.IOException;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.stream.XMLStreamException;
import org.geotoolkit.data.kml.model.AbstractFeature;
import org.geotoolkit.data.kml.model.AbstractView;
import org.geotoolkit.data.kml.model.Folder;
import org.geotoolkit.data.kml.model.Kml;
import org.geotoolkit.data.kml.model.KmlException;
import org.geotoolkit.data.kml.model.LookAt;
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
 * @author samuel
 */
public class LookAtTest {

    private static final double DELTA = 0.000000000001;
    private static final String pathToTestFile = "src/test/resources/org/geotoolkit/data/kml/lookAt.kml";

    public LookAtTest() {
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
     public void lookAtReadTest() throws IOException, XMLStreamException {

         final KmlReader reader = new KmlReader();
         reader.setInput(new File(pathToTestFile));
         final Kml kmlObjects = reader.read();
         reader.dispose();

         final AbstractFeature feature = kmlObjects.getAbstractFeature();
         System.out.println(feature);
         assertTrue(feature instanceof Folder);
         final AbstractView view = ((Folder)feature).getView();
         assertTrue(view instanceof LookAt);

         final LookAt lookAt = (LookAt) view;
         assertEquals(-122.0839597145766, lookAt.getLongitude(), DELTA);
         assertEquals(37.42222904525232, lookAt.getLatitude(), DELTA);
         assertEquals(1000.34, lookAt.getAltitude(), DELTA);
         assertEquals(-148.4122922628044, lookAt.getHeading(), DELTA);
         assertEquals(40.5575073395506, lookAt.getTilt(), DELTA);
         assertEquals(500.6566641072245, lookAt.getRange(), DELTA);
         
     }

     @Test
     public void lookAtWriteTest() throws KmlException, IOException, XMLStreamException, ParserConfigurationException, SAXException{
        final KmlFactory kmlFactory = new KmlFactoryDefault();

        double longitude = -122.0839597145766;
        double latitude = 37.42222904525232;
        double altitude = 1000.34;
        double heading = -148.4122922628044;
        double tilt = 40.5575073395506;
        double range = 500.6566641072245;
         
        final LookAt lookAt = kmlFactory.createLookAt();
        lookAt.setLongitude(longitude);
        lookAt.setLatitude(latitude);
        lookAt.setAltitude(altitude);
        lookAt.setHeading(heading);
        lookAt.setTilt(tilt);
        lookAt.setRange(range);
        final Folder folder = kmlFactory.createFolder();
        folder.setView(lookAt);
        final Kml kml = kmlFactory.createKml(null, folder, null, null);

        File temp = File.createTempFile("testLookAt",".kml");
        temp.deleteOnExit();

        KmlWriter writer = new KmlWriter();
        writer.setOutput(temp);
        writer.write(kml);
        writer.dispose();

        DomCompare.compare(
                 new File(pathToTestFile), temp);

     }


}