package org.geotoolkit.data.kml;

import java.io.File;
import java.io.IOException;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.stream.XMLStreamException;
import org.geotoolkit.data.model.KmlFactory;
import org.geotoolkit.data.model.KmlFactoryDefault;
import org.geotoolkit.data.model.kml.AbstractFeature;
import org.geotoolkit.data.model.kml.AbstractView;
import org.geotoolkit.data.model.kml.AltitudeMode;
import org.geotoolkit.data.model.kml.Angle180;
import org.geotoolkit.data.model.kml.Angle360;
import org.geotoolkit.data.model.kml.Angle90;
import org.geotoolkit.data.model.kml.Anglepos180;
import org.geotoolkit.data.model.kml.Camera;
import org.geotoolkit.data.model.kml.Kml;
import org.geotoolkit.data.model.kml.KmlException;
import org.geotoolkit.data.model.kml.PhotoOverlay;
import org.geotoolkit.xml.DomCompare;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import org.xml.sax.SAXException;

import static org.geotoolkit.data.model.KmlModelConstants.*;

/**
 *
 * @author samuel
 */
public class CameraTest {

    private static final double DELTA = 0.000000000001;

    public CameraTest() {
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
     public void cameraReadTest() throws IOException, XMLStreamException {

         final KmlReader reader = new KmlReader();
         reader.setInput(new File("src/test/resources/org/geotoolkit/data/kml/camera.kml"));
         final Kml kmlObjects = reader.read();
         reader.dispose();

         final AbstractFeature feature = kmlObjects.getAbstractFeature();
         assertTrue(feature instanceof PhotoOverlay);
         final AbstractView view = ((PhotoOverlay)feature).getView();
         assertTrue(view instanceof Camera);

         final Camera camera = (Camera) view;
         assertEquals(4, camera.getLongitude().getAngle(), DELTA);
         
     }

     @Test
     public void cameraWriteTest() throws KmlException, IOException, XMLStreamException, ParserConfigurationException, SAXException{
         final KmlFactory kmlFactory = new KmlFactoryDefault();

         Angle180 longitude = kmlFactory.createAngle180(4);
         Angle90 latitude = kmlFactory.createAngle90(43);
         Angle360 heading = kmlFactory.createAngle360(2);
         Anglepos180 tilt = kmlFactory.createAnglepos180(0);
         Angle180 roll = kmlFactory.createAngle180(0);
         Camera camera = kmlFactory.createCamera(null, null, null, null, longitude, latitude, 625, heading, tilt, roll, AltitudeMode.RELATIVE_TO_GROUND, null, null);

         final PhotoOverlay photoOverlay = kmlFactory.createPhotoOverlay(null, null, null, DEF_VISIBILITY,
                 DEF_OPEN, null, null, null, null, null, null, null, camera,
                 null, null, null, null, null, null, null, null, DEF_DRAW_ORDER, null, null, null,
                 DEF_ROTATION, null, null, null, null, null, null);
         final Kml kml = kmlFactory.createKml(null, photoOverlay, null, null);

         File temp = File.createTempFile("testKml",".kml");
         temp.deleteOnExit();

         KmlWriter writer = new KmlWriter();
         writer.setOutput(temp);
         writer.write(kml);
         writer.dispose();

         DomCompare.compare(
                 new File("src/test/resources/org/geotoolkit/data/kml/camera.kml"), temp);

     }


}