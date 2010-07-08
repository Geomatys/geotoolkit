package org.geotoolkit.data.kml;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.stream.XMLStreamException;
import org.geotoolkit.data.kml.model.AbstractFeature;
import org.geotoolkit.data.kml.model.Alias;
import org.geotoolkit.data.kml.model.AltitudeMode;
import org.geotoolkit.data.kml.model.IdAttributes;
import org.geotoolkit.data.kml.model.Kml;
import org.geotoolkit.data.kml.model.KmlException;
import org.geotoolkit.data.kml.model.Link;
import org.geotoolkit.data.kml.model.Location;
import org.geotoolkit.data.kml.model.Model;
import org.geotoolkit.data.kml.model.Orientation;
import org.geotoolkit.data.kml.model.Placemark;
import org.geotoolkit.data.kml.model.RefreshMode;
import org.geotoolkit.data.kml.model.ResourceMap;
import org.geotoolkit.data.kml.model.Scale;
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
public class ModelTest {

    private static final double DELTA = 0.000000000001;
    private static final String pathToTestFile = "src/test/resources/org/geotoolkit/data/kml/model.kml";

    public ModelTest() {
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
    public void regionReadTest() throws IOException, XMLStreamException, URISyntaxException {

        final KmlReader reader = new KmlReader();
        reader.setInput(new File(pathToTestFile));
        final Kml kmlObjects = reader.read();
        reader.dispose();

        final AbstractFeature feature = kmlObjects.getAbstractFeature();
        assertTrue(feature instanceof Placemark);
        final Placemark placemark = (Placemark) feature;
        assertEquals("Colorado", placemark.getName());
        assertTrue(placemark.getAbstractGeometry() instanceof Model);

        final Model model = (Model) placemark.getAbstractGeometry();
        assertEquals("khModel543", model.getIdAttributes().getId());
        assertEquals(AltitudeMode.RELATIVE_TO_GROUND, model.getAltitudeMode());

        final Location location = model.getLocation();
        assertEquals(39.55375305703105, location.getLongitude(), DELTA);
        assertEquals(-18.9813220168456, location.getLatitude(), DELTA);
        assertEquals(1223, location.getAltitude(), DELTA);

        final Orientation orientation = model.getOrientation();
        assertEquals(45, orientation.getHeading(), DELTA);
        assertEquals(10, orientation.getTilt(), DELTA);
        assertEquals(0.5, orientation.getRoll(), DELTA);

        final Scale scale = model.getScale();
        assertEquals(4, scale.getX(), DELTA);
        assertEquals(2, scale.getY(), DELTA);
        assertEquals(3, scale.getZ(), DELTA);

        final Link link = model.getLink();
        assertEquals("house.dae", link.getHref());
        assertEquals(RefreshMode.ON_EXPIRE, link.getRefreshMode());

        final ResourceMap resourceMap = model.getRessourceMap();
        assertEquals(3, resourceMap.getAliases().size());

        final Alias alias0 = resourceMap.getAliases().get(0);
        assertEquals(new URI("../files/CU-Macky---Center-StairsnoCulling.jpg"), alias0.getTargetHref());
        assertEquals(new URI("CU-Macky---Center-StairsnoCulling.jpg"), alias0.getSourceHref());

        final Alias alias1 = resourceMap.getAliases().get(1);
        assertEquals(new URI("../files/CU-Macky-4sideturretnoCulling.jpg"), alias1.getTargetHref());
        assertEquals(new URI("CU-Macky-4sideturretnoCulling.jpg"), alias1.getSourceHref());

        final Alias alias2 = resourceMap.getAliases().get(2);
        assertEquals(new URI("../files/CU-Macky-Back-NorthnoCulling.jpg"), alias2.getTargetHref());
        assertEquals(new URI("CU-Macky-Back-NorthnoCulling.jpg"), alias2.getSourceHref());
    }

    @Test
    public void regionWriteTest() throws KmlException, IOException, XMLStreamException, ParserConfigurationException, SAXException, URISyntaxException{
        final KmlFactory kmlFactory = new DefaultKmlFactory();

        final Alias alias0 = kmlFactory.createAlias();
        alias0.setTargetHref(new URI("../files/CU-Macky---Center-StairsnoCulling.jpg"));
        alias0.setSourceHref(new URI("CU-Macky---Center-StairsnoCulling.jpg"));

        final Alias alias1 = kmlFactory.createAlias();
        alias1.setTargetHref(new URI("../files/CU-Macky-4sideturretnoCulling.jpg"));
        alias1.setSourceHref(new URI("CU-Macky-4sideturretnoCulling.jpg"));

        final Alias alias2 = kmlFactory.createAlias();
        alias2.setTargetHref(new URI("../files/CU-Macky-Back-NorthnoCulling.jpg"));
        alias2.setSourceHref(new URI("CU-Macky-Back-NorthnoCulling.jpg"));

        final ResourceMap resourceMap = kmlFactory.createResourceMap();
        resourceMap.setAliases(Arrays.asList(alias0, alias1, alias2));

        final Link link = kmlFactory.createLink();
        link.setHref("house.dae");
        link.setRefreshMode(RefreshMode.ON_EXPIRE);

        final Scale scale = kmlFactory.createScale();
        scale.setX(4);
        scale.setY(2);
        scale.setZ(3);

        final Orientation orientation = kmlFactory.createOrientation();
        orientation.setHeading(45);
        orientation.setTilt(10);
        orientation.setRoll(0.5);

        final Location location = kmlFactory.createLocation();
        location.setLongitude(39.55375305703105);
        location.setLatitude(-18.9813220168456);
        location.setAltitude(1223);

        final Model model = kmlFactory.createModel();
        final IdAttributes idAttributes = kmlFactory.createIdAttributes("khModel543", null);
        model.setIdAttributes(idAttributes);
        model.setAltitudeMode(AltitudeMode.RELATIVE_TO_GROUND);
        model.setLocation(location);
        model.setOrientation(orientation);
        model.setScale(scale);
        model.setLink(link);
        model.setRessourceMap(resourceMap);
        
        final Placemark placemark = kmlFactory.createPlacemark();
        placemark.setName("Colorado");
        placemark.setAbstractGeometry(model);


        final Kml kml = kmlFactory.createKml(null, placemark, null, null);

        File temp = File.createTempFile("testModel",".kml");
        temp.deleteOnExit();

        KmlWriter writer = new KmlWriter();
        writer.setOutput(temp);
        writer.write(kml);
        writer.dispose();

        DomCompare.compare(
                 new File(pathToTestFile), temp);

    }

}