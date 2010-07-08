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
import org.geotoolkit.data.kml.model.Coordinates;
import org.geotoolkit.data.kml.model.Document;
import org.geotoolkit.data.kml.model.IconStyle;
import org.geotoolkit.data.kml.model.IdAttributes;
import org.geotoolkit.data.kml.model.Kml;
import org.geotoolkit.data.kml.model.KmlException;
import org.geotoolkit.data.kml.model.LabelStyle;
import org.geotoolkit.data.kml.model.Pair;
import org.geotoolkit.data.kml.model.Placemark;
import org.geotoolkit.data.kml.model.Point;
import org.geotoolkit.data.kml.model.Style;
import org.geotoolkit.data.kml.model.StyleMap;
import org.geotoolkit.data.kml.model.StyleState;
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
public class StyleMapTest {

    private static final double DELTA = 0.000000000001;
    private static final String pathToTestFile = "src/test/resources/org/geotoolkit/data/kml/styleMap.kml";

    public StyleMapTest() {
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
    public void styleMapReadTest() throws IOException, XMLStreamException, URISyntaxException {

        final KmlReader reader = new KmlReader();
        reader.setInput(new File(pathToTestFile));
        final Kml kmlObjects = reader.read();
        reader.dispose();

        final AbstractFeature feature = kmlObjects.getAbstractFeature();
        assertTrue(feature instanceof Document);
        final Document document = ((Document) feature);
        assertEquals("StyleMap.kml", document.getName());
        assertTrue(document.getOpen());

        List<AbstractStyleSelector> styleSelectors = document.getStyleSelectors();
        assertEquals(3, styleSelectors.size());

        assertTrue(styleSelectors.get(0) instanceof Style);
        Style style0 = (Style) styleSelectors.get(0);
        assertEquals("normalState", style0.getIdAttributes().getId());
            IconStyle iconStyle0 = style0.getIconStyle();
            BasicLink icon0 =  iconStyle0.getIcon();
            assertEquals("http://maps.google.com/mapfiles/kml/pal3/icon55.png", icon0.getHref());
        LabelStyle labelStyle0 = style0.getLabelStyle();

        assertTrue(styleSelectors.get(1) instanceof Style);
        Style style1 = (Style) styleSelectors.get(1);
        assertEquals("highlightState", style1.getIdAttributes().getId());
            IconStyle iconStyle1 = style1.getIconStyle();
            assertEquals(1.1, iconStyle1.getScale(), DELTA);
            BasicLink icon1 =  iconStyle1.getIcon();
            assertEquals("http://maps.google.com/mapfiles/kml/pal3/icon60.png", icon1.getHref());
        LabelStyle labelStyle1 = style1.getLabelStyle();
        assertEquals(new Color(192, 0, 0, 255), labelStyle1.getColor());
        assertEquals(1.1, labelStyle1.getScale(), DELTA);

        assertTrue(styleSelectors.get(2) instanceof StyleMap);
        StyleMap style2 = (StyleMap) styleSelectors.get(2);
        assertEquals("styleMapExample", style2.getIdAttributes().getId());
        assertEquals(2, style2.getPairs().size());
            Pair pair0 = style2.getPairs().get(0);
            assertEquals(new URI("#normalState"), pair0.getStyleUrl());
        Pair pair1 = style2.getPairs().get(1);
        assertEquals(StyleState.HIGHLIGHT, pair1.getKey());
        assertEquals(new URI("#highlightState"), pair1.getStyleUrl());

        assertEquals(1, document.getAbstractFeatures().size());
        assertTrue(document.getAbstractFeatures().get(0) instanceof Placemark);
        Placemark placemark = (Placemark) document.getAbstractFeatures().get(0);
        assertEquals("StyleMap example", placemark.getName());
        assertEquals(new URI("#styleMapExample"), placemark.getStyleUrl());
        assertTrue(placemark.getAbstractGeometry() instanceof Point);
        Point point = (Point) placemark.getAbstractGeometry();
        Coordinates coordinates = point.getCoordinateSequence();
        assertEquals(1, coordinates.size());
        Coordinate coordinate = coordinates.getCoordinate(0);
        assertEquals(-122.368987, coordinate.x, DELTA);
        assertEquals(37.817634, coordinate.y, DELTA);
        assertEquals(0, coordinate.z, DELTA);
    }

    @Test
    public void styleMapWriteTest() throws KmlException, IOException, XMLStreamException, ParserConfigurationException, SAXException, URISyntaxException {
        final KmlFactory kmlFactory = new DefaultKmlFactory();

        Coordinate coordinate = kmlFactory.createCoordinate(-122.368987, 37.817634, 0);
        Coordinates coordinates = kmlFactory.createCoordinates(Arrays.asList(coordinate));
        Point point = kmlFactory.createPoint(coordinates);

        Placemark placemark = kmlFactory.createPlacemark();
        placemark.setName("StyleMap example");
        placemark.setStyleUrl(new URI("#styleMapExample"));
        placemark.setAbstractGeometry(point);

        Style style0 = kmlFactory.createStyle();
            IconStyle iconStyle0 = kmlFactory.createIconStyle();
            BasicLink icon0 = kmlFactory.createBasicLink();
            icon0.setHref("http://maps.google.com/mapfiles/kml/pal3/icon55.png");
            iconStyle0.setIcon(icon0);

            LabelStyle labelStyle0 = kmlFactory.createLabelStyle();
        style0.setIconStyle(iconStyle0);
        style0.setLabelStyle(labelStyle0);
        IdAttributes idAttributes0 = kmlFactory.createIdAttributes("normalState", null);
        style0.setIdAttributes(idAttributes0);

        Style style1 = kmlFactory.createStyle();
            IconStyle iconStyle1 = kmlFactory.createIconStyle();
            iconStyle1.setScale(1.1);
            BasicLink icon1 = kmlFactory.createBasicLink();
            icon1.setHref("http://maps.google.com/mapfiles/kml/pal3/icon60.png");
            iconStyle1.setIcon(icon1);

            LabelStyle labelStyle1 = kmlFactory.createLabelStyle();
            labelStyle1.setColor(new Color(192, 0, 0, 255));
            labelStyle1.setScale(1.1);
        style1.setIconStyle(iconStyle1);
        style1.setLabelStyle(labelStyle1);
        IdAttributes idAttributes1 = kmlFactory.createIdAttributes("highlightState", null);
        style1.setIdAttributes(idAttributes1);

        StyleMap styleMap = kmlFactory.createStyleMap();
            Pair pair0 = kmlFactory.createPair();
            pair0.setStyleUrl(new URI("#normalState"));

            Pair pair1 = kmlFactory.createPair();
            pair1.setKey(StyleState.HIGHLIGHT);
            pair1.setStyleUrl(new URI("#highlightState"));
        styleMap.setPairs(Arrays.asList(pair0, pair1));
        IdAttributes idAttributes2 = kmlFactory.createIdAttributes("styleMapExample", null);
        styleMap.setIdAttributes(idAttributes2);

        Document document = kmlFactory.createDocument();
        document.setStyleSelectors(Arrays.asList(
                (AbstractStyleSelector) style0,
                (AbstractStyleSelector) style1,
                (AbstractStyleSelector) styleMap));
        document.setAbstractFeatures(Arrays.asList((AbstractFeature) placemark));
        document.setName("StyleMap.kml");
        document.setOpen(true);

        final Kml kml = kmlFactory.createKml(null, document, null, null);

        File temp = File.createTempFile("testStyleMap", ".kml");
        temp.deleteOnExit();

        KmlWriter writer = new KmlWriter();
        writer.setOutput(temp);
        writer.write(kml);
        writer.dispose();

        DomCompare.compare(
                new File(pathToTestFile), temp);

    }

}