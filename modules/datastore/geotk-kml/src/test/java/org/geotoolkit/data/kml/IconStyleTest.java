package org.geotoolkit.data.kml;

import com.vividsolutions.jts.geom.Coordinate;
import java.awt.Color;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.List;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.stream.XMLStreamException;
import org.geotoolkit.data.kml.model.AbstractFeature;
import org.geotoolkit.data.kml.model.AbstractStyleSelector;
import org.geotoolkit.data.kml.model.BasicLink;
import org.geotoolkit.data.kml.model.ColorMode;
import org.geotoolkit.data.kml.model.Coordinates;
import org.geotoolkit.data.kml.model.Document;
import org.geotoolkit.data.kml.model.IconStyle;
import org.geotoolkit.data.kml.model.IdAttributes;
import org.geotoolkit.data.kml.model.Kml;
import org.geotoolkit.data.kml.model.KmlException;
import org.geotoolkit.data.kml.model.Placemark;
import org.geotoolkit.data.kml.model.Point;
import org.geotoolkit.data.kml.model.Style;
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
public class IconStyleTest {

    private static final double DELTA = 0.000000000001;
    private static final String pathToTestFile = "src/test/resources/org/geotoolkit/data/kml/iconStyle.kml";

    public IconStyleTest() {
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
    public void iconStyleReadTest() throws IOException, XMLStreamException, URISyntaxException {

        final KmlReader reader = new KmlReader();
        reader.setInput(new File(pathToTestFile));
        final Kml kmlObjects = reader.read();
        reader.dispose();

        final AbstractFeature feature = kmlObjects.getAbstractFeature();
        assertTrue(feature instanceof Document);
        final Document document = ((Document) feature);

        List<AbstractStyleSelector> styleSelectors = document.getStyleSelectors();
        assertEquals(1, styleSelectors.size());

        assertTrue(styleSelectors.get(0) instanceof Style);
        Style style = (Style) styleSelectors.get(0);
        assertEquals("randomColorIcon", style.getIdAttributes().getId());
            IconStyle iconStyle = style.getIconStyle();
            assertEquals(new Color(0, 255, 0, 255), iconStyle.getColor());
            assertEquals(ColorMode.RANDOM, iconStyle.getColorMode());
            assertEquals(1.1, iconStyle.getScale(), DELTA);
            BasicLink icon =  iconStyle.getIcon();
            assertEquals("http://maps.google.com/mapfiles/kml/pal3/icon21.png", icon.getHref());

        assertEquals(1, document.getAbstractFeatures().size());
        assertTrue(document.getAbstractFeatures().get(0) instanceof Placemark);
        Placemark placemark = (Placemark) document.getAbstractFeatures().get(0);
        assertEquals("IconStyle.kml", placemark.getName());
        assertEquals(new URI("#randomColorIcon"), placemark.getStyleUrl());
        assertTrue(placemark.getAbstractGeometry() instanceof Point);
        Point point = (Point) placemark.getAbstractGeometry();
        Coordinates coordinates = point.getCoordinateSequence();
        assertEquals(1, coordinates.size());
        Coordinate coordinate = coordinates.getCoordinate(0);
        assertEquals(-122.36868, coordinate.x, DELTA);
        assertEquals(37.831145, coordinate.y, DELTA);
        assertEquals(0, coordinate.z, DELTA);
    }

    @Test
    public void iconStyleWriteTest() throws KmlException, IOException, XMLStreamException, ParserConfigurationException, SAXException, URISyntaxException {
        final KmlFactory kmlFactory = new DefaultKmlFactory();

        Coordinate coordinate = kmlFactory.createCoordinate(-122.36868, 37.831145, 0);
        Coordinates coordinates = kmlFactory.createCoordinates(Arrays.asList(coordinate));
        Point point = kmlFactory.createPoint(coordinates);

        Placemark placemark = kmlFactory.createPlacemark();
        placemark.setName("IconStyle.kml");
        placemark.setStyleUrl(new URI("#randomColorIcon"));
        placemark.setAbstractGeometry(point);

        Style style = kmlFactory.createStyle();
            IconStyle iconStyle = kmlFactory.createIconStyle();
            BasicLink icon = kmlFactory.createBasicLink();
            icon.setHref("http://maps.google.com/mapfiles/kml/pal3/icon21.png");
            iconStyle.setIcon(icon);
            iconStyle.setScale(1.1);
            iconStyle.setColor(new Color(0, 255, 0, 255));
            iconStyle.setColorMode(ColorMode.RANDOM);
        style.setIconStyle(iconStyle);
        IdAttributes idAttributes = kmlFactory.createIdAttributes("randomColorIcon", null);
        style.setIdAttributes(idAttributes);

        Document document = kmlFactory.createDocument();
        document.setStyleSelectors(Arrays.asList((AbstractStyleSelector) style));
        document.setAbstractFeatures(Arrays.asList((AbstractFeature) placemark));

        final Kml kml = kmlFactory.createKml(null, document, null, null);

        File temp = File.createTempFile("testIconStyle", ".kml");
        temp.deleteOnExit();

        KmlWriter writer = new KmlWriter();
        writer.setOutput(temp);
        writer.write(kml);
        writer.dispose();

        DomCompare.compare(
                new File(pathToTestFile), temp);

    }

}