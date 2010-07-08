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
import org.geotoolkit.data.kml.model.AltitudeMode;
import org.geotoolkit.data.kml.model.Boundary;
import org.geotoolkit.data.kml.model.ColorMode;
import org.geotoolkit.data.kml.model.Coordinates;
import org.geotoolkit.data.kml.model.Document;
import org.geotoolkit.data.kml.model.IdAttributes;
import org.geotoolkit.data.kml.model.Kml;
import org.geotoolkit.data.kml.model.KmlException;
import org.geotoolkit.data.kml.model.LinearRing;
import org.geotoolkit.data.kml.model.Placemark;
import org.geotoolkit.data.kml.model.PolyStyle;
import org.geotoolkit.data.kml.model.Polygon;
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
public class PolyStyleTest {

    private static final double DELTA = 0.000000000001;
    private static final String pathToTestFile = "src/test/resources/org/geotoolkit/data/kml/polyStyle.kml";

    public PolyStyleTest() {
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
    public void polyStyleReadTest() throws IOException, XMLStreamException, URISyntaxException {

        final KmlReader reader = new KmlReader();
        reader.setInput(new File(pathToTestFile));
        final Kml kmlObjects = reader.read();
        reader.dispose();

        final AbstractFeature feature = kmlObjects.getAbstractFeature();
        assertTrue(feature instanceof Document);
        final Document document = ((Document) feature);
        assertEquals("PolygonStyle.kml", document.getName());
        assertTrue(document.getOpen());

        List<AbstractStyleSelector> styleSelectors = document.getStyleSelectors();
        assertEquals(1, styleSelectors.size());

        assertTrue(styleSelectors.get(0) instanceof Style);
        Style style = (Style) styleSelectors.get(0);
        assertEquals("examplePolyStyle", style.getIdAttributes().getId());
            PolyStyle polyStyle = style.getPolyStyle();
            assertEquals(new Color(204, 0, 0, 255), polyStyle.getColor());
            assertEquals(ColorMode.RANDOM, polyStyle.getColorMode());

        assertEquals(1, document.getAbstractFeatures().size());
        assertTrue(document.getAbstractFeatures().get(0) instanceof Placemark);
        Placemark placemark = (Placemark) document.getAbstractFeatures().get(0);
        assertEquals("hollow box", placemark.getName());
        assertEquals(new URI("#examplePolyStyle"), placemark.getStyleUrl());
        assertTrue(placemark.getAbstractGeometry() instanceof Polygon);
        Polygon polygon = (Polygon) placemark.getAbstractGeometry();
        assertTrue(polygon.getExtrude());
        assertEquals(AltitudeMode.RELATIVE_TO_GROUND, polygon.getAltitudeMode());

        Boundary outerBoundaryIs = polygon.getOuterBoundary();
        LinearRing linearRing = outerBoundaryIs.getLinearRing();
        Coordinates coordinates = linearRing.getCoordinateSequence();
        assertEquals(5, coordinates.size());

        Coordinate coordinate0 = coordinates.getCoordinate(0);
        assertEquals(-122.3662784465226, coordinate0.x, DELTA);
        assertEquals(37.81884427772081, coordinate0.y, DELTA);
        assertEquals(30, coordinate0.z, DELTA);

        Coordinate coordinate1 = coordinates.getCoordinate(1);
        assertEquals(-122.3652480684771, coordinate1.x, DELTA);
        assertEquals(37.81926777010555, coordinate1.y, DELTA);
        assertEquals(30, coordinate1.z, DELTA);

        Coordinate coordinate2 = coordinates.getCoordinate(2);
        assertEquals(-122.365640222455, coordinate2.x, DELTA);
        assertEquals(37.81986126286519, coordinate2.y, DELTA);
        assertEquals(30, coordinate2.z, DELTA);

        Coordinate coordinate3 = coordinates.getCoordinate(3);
        assertEquals(-122.36666937925, coordinate3.x, DELTA);
        assertEquals(37.81942987753481, coordinate3.y, DELTA);
        assertEquals(30, coordinate3.z, DELTA);

        Coordinate coordinate4 = coordinates.getCoordinate(4);
        assertEquals(-122.3662784465226, coordinate4.x, DELTA);
        assertEquals(37.81884427772081, coordinate4.y, DELTA);
        assertEquals(30, coordinate4.z, DELTA);

        List<Boundary> innerBoundariesAre = polygon.getInnerBoundaries();
        assertEquals(1, innerBoundariesAre.size());

        Boundary innerBoundaryIs0 = innerBoundariesAre.get(0);
        LinearRing linearRing0 = innerBoundaryIs0.getLinearRing();
        Coordinates coordinates0 = linearRing0.getCoordinateSequence();
        assertEquals(5, coordinates0.size());

        Coordinate coordinate00 = coordinates0.getCoordinate(0);
        assertEquals(-122.366212593918, coordinate00.x, DELTA);
        assertEquals(37.81897719083808, coordinate00.y, DELTA);
        assertEquals(30, coordinate00.z, DELTA);

        Coordinate coordinate01 = coordinates0.getCoordinate(1);
        assertEquals(-122.3654241733188, coordinate01.x, DELTA);
        assertEquals(37.81929450992014, coordinate01.y, DELTA);
        assertEquals(30, coordinate01.z, DELTA);

        Coordinate coordinate02 = coordinates0.getCoordinate(2);
        assertEquals(-122.3657048517827, coordinate02.x, DELTA);
        assertEquals(37.81973175302663, coordinate02.y, DELTA);
        assertEquals(30, coordinate02.z, DELTA);

        Coordinate coordinate03 = coordinates0.getCoordinate(3);
        assertEquals(-122.3664882465854, coordinate03.x, DELTA);
        assertEquals(37.81940249291773, coordinate03.y, DELTA);
        assertEquals(30, coordinate03.z, DELTA);

        Coordinate coordinate04 = coordinates0.getCoordinate(4);
        assertEquals(-122.366212593918, coordinate04.x, DELTA);
        assertEquals(37.81897719083808, coordinate04.y, DELTA);
        assertEquals(30, coordinate04.z, DELTA);

    }

    @Test
    public void polyStyleWriteTest() throws KmlException, IOException, XMLStreamException, ParserConfigurationException, SAXException, URISyntaxException {
        final KmlFactory kmlFactory = new DefaultKmlFactory();

        Coordinate coordinate0 = kmlFactory.createCoordinate(
                -122.3662784465226, 37.81884427772081, 30);
        Coordinate coordinate1 = kmlFactory.createCoordinate(
                -122.3652480684771, 37.81926777010555, 30);
        Coordinate coordinate2 = kmlFactory.createCoordinate(
                -122.365640222455, 37.81986126286519, 30);
        Coordinate coordinate3 = kmlFactory.createCoordinate(
                -122.36666937925, 37.81942987753481, 30);
        Coordinate coordinate4 = kmlFactory.createCoordinate(
                -122.3662784465226, 37.81884427772081, 30);
        Coordinates coordinates = kmlFactory.createCoordinates(
                Arrays.asList(coordinate0, coordinate1,
                coordinate2, coordinate3, coordinate4));
        
        Coordinate coordinate00 = kmlFactory.createCoordinate(
                -122.366212593918, 37.81897719083808, 30);
        Coordinate coordinate01 = kmlFactory.createCoordinate(
                -122.3654241733188, 37.81929450992014, 30);
        Coordinate coordinate02 = kmlFactory.createCoordinate(
                -122.3657048517827, 37.81973175302663, 30);
        Coordinate coordinate03 = kmlFactory.createCoordinate(
                -122.3664882465854, 37.81940249291773, 30);
        Coordinate coordinate04 = kmlFactory.createCoordinate(
                -122.366212593918, 37.81897719083808, 30);
        Coordinates coordinates0 = kmlFactory.createCoordinates(
                Arrays.asList(coordinate00, coordinate01,
                coordinate02, coordinate03, coordinate04));

        LinearRing linearRing = kmlFactory.createLinearRing(coordinates);

        LinearRing linearRing0 = kmlFactory.createLinearRing(coordinates0);

        Boundary outerBoundaryIs = kmlFactory.createBoundary();
        outerBoundaryIs.setLinearRing(linearRing);

        Boundary innerBoundaryIs = kmlFactory.createBoundary();
        innerBoundaryIs.setLinearRing(linearRing0);

        Polygon polygon = kmlFactory.createPolygon(outerBoundaryIs, Arrays.asList(innerBoundaryIs));
        polygon.setExtrude(true);
        polygon.setAltitudeMode(AltitudeMode.RELATIVE_TO_GROUND);

        Placemark placemark = kmlFactory.createPlacemark();
        placemark.setName("hollow box");
        placemark.setStyleUrl(new URI("#examplePolyStyle"));
        placemark.setAbstractGeometry(polygon);

        Style style = kmlFactory.createStyle();
        IdAttributes idAttributes = kmlFactory.createIdAttributes("examplePolyStyle", null);
        style.setIdAttributes(idAttributes);

        PolyStyle polyStyle = kmlFactory.createPolyStyle();
        polyStyle.setColor(new Color(204, 0, 0, 255));
        polyStyle.setColorMode(ColorMode.RANDOM);
        style.setPolyStyle(polyStyle);

        Document document = kmlFactory.createDocument();
        document.setStyleSelectors(Arrays.asList((AbstractStyleSelector) style));
        document.setAbstractFeatures(Arrays.asList((AbstractFeature) placemark));
        document.setName("PolygonStyle.kml");
        document.setOpen(true);

        final Kml kml = kmlFactory.createKml(null, document, null, null);

        File temp = File.createTempFile("testPolyStyle", ".kml");
        temp.deleteOnExit();

        KmlWriter writer = new KmlWriter();
        writer.setOutput(temp);
        writer.write(kml);
        writer.dispose();

        DomCompare.compare(
                new File(pathToTestFile), temp);

    }

}