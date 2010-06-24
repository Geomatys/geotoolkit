package org.geotoolkit.data.kml;

import java.awt.Color;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.stream.XMLStreamException;
import org.geotoolkit.data.kml.model.AbstractFeature;
import org.geotoolkit.data.kml.model.AbstractStyleSelector;
import org.geotoolkit.data.kml.model.BasicLink;
import org.geotoolkit.data.kml.model.ColorMode;
import org.geotoolkit.data.kml.model.Document;
import org.geotoolkit.data.kml.model.IconStyle;
import org.geotoolkit.data.kml.model.IdAttributes;
import org.geotoolkit.data.kml.model.Kml;
import org.geotoolkit.data.kml.model.KmlException;
import org.geotoolkit.data.kml.model.LabelStyle;
import org.geotoolkit.data.kml.model.LineStyle;
import org.geotoolkit.data.kml.model.Placemark;
import org.geotoolkit.data.kml.model.PolyStyle;
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
public class StyleTest {

    private static final double DELTA = 0.000000000001;
    private static final String pathToTestFile = "src/test/resources/org/geotoolkit/data/kml/style.kml";

    public StyleTest() {
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
    public void styleReadTest() throws IOException, XMLStreamException {

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
        assertEquals("myDefaultStyles", style.getIdAttributes().getId());

        IconStyle iconStyle = style.getIconStyle();
        assertEquals(new Color(255, 0, 255, 161), iconStyle.getColor());
        assertEquals(1.399999976158142, iconStyle.getScale(), DELTA);
        BasicLink icon = iconStyle.getIcon();
        assertEquals("http://myserver.com/icon.jpg", icon.getHref());

        LabelStyle labelStyle = style.getLabelStyle();
        assertEquals(new Color(255, 170, 255, 127), labelStyle.getColor());
        assertEquals(1.5, labelStyle.getScale(), DELTA);

        LineStyle lineStyle = style.getLineStyle();
        assertEquals(new Color(255, 0, 0, 255), lineStyle.getColor());
        assertEquals(15, lineStyle.getWidth(), DELTA);

        PolyStyle polyStyle = style.getPolyStyle();
        assertEquals(new Color(170, 170, 127, 127), polyStyle.getColor());
        assertEquals(ColorMode.RANDOM, polyStyle.getColorMode());

        List<AbstractFeature> abstractFeatures = document.getAbstractFeatures();
        assertEquals(2, abstractFeatures.size());

        assertTrue(abstractFeatures.get(0) instanceof Placemark);
        Placemark placemark0 = (Placemark) abstractFeatures.get(0);
        assertEquals("Google Earth - New Polygon", placemark0.getName());
        assertEquals("Here is some descriptive text", placemark0.getDescription());
        assertEquals("#myDefaultStyles", placemark0.getStyleUrl());

        assertTrue(abstractFeatures.get(1) instanceof Placemark);
        Placemark placemark1 = (Placemark) abstractFeatures.get(1);
        assertEquals("Google Earth - New Path", placemark1.getName());
        assertEquals("#myDefaultStyles", placemark1.getStyleUrl());

    }

    @Test
    public void styleWriteTest() throws KmlException, IOException, XMLStreamException, ParserConfigurationException, SAXException {
        final KmlFactory kmlFactory = new DefaultKmlFactory();

        Placemark placemark0 = kmlFactory.createPlacemark();
        placemark0.setName("Google Earth - New Polygon");
        placemark0.setDescription("Here is some descriptive text");
        placemark0.setStyleUrl("#myDefaultStyles");

        Placemark placemark1 = kmlFactory.createPlacemark();
        placemark1.setName("Google Earth - New Path");
        placemark1.setStyleUrl("#myDefaultStyles");

        IconStyle iconStyle = kmlFactory.createIconStyle();
        BasicLink icon = kmlFactory.createBasicLink();
        icon.setHref("http://myserver.com/icon.jpg");
        iconStyle.setIcon(icon);
        iconStyle.setColor(new Color(255, 0, 255, 161));
        iconStyle.setScale(1.399999976158142);

        LabelStyle labelStyle = kmlFactory.createLabelStyle();
        labelStyle.setColor(new Color(255, 170, 255, 127));
        labelStyle.setScale(1.5);

        LineStyle lineStyle = kmlFactory.createLineStyle();
        lineStyle.setColor(new Color(255, 0, 0, 255));
        lineStyle.setWidth(15);

        PolyStyle polyStyle = kmlFactory.createPolyStyle();
        polyStyle.setColor(new Color(170, 170, 127, 127));
        polyStyle.setColorMode(ColorMode.RANDOM);

        IdAttributes idAttributes = kmlFactory.createIdAttributes("myDefaultStyles", null);

        Style style = kmlFactory.createStyle();
        style.setIdAttributes(idAttributes);
        style.setIconStyle(iconStyle);
        style.setLabelStyle(labelStyle);
        style.setLineStyle(lineStyle);
        style.setPolyStyle(polyStyle);

        Document document = kmlFactory.createDocument();
        document.setStyleSelectors(Arrays.asList((AbstractStyleSelector) style));
        document.setAbstractFeatures(Arrays.asList((AbstractFeature) placemark0, (AbstractFeature) placemark1));


        final Kml kml = kmlFactory.createKml(null, document, null, null);

        File temp = File.createTempFile("testStyle", ".kml");
        temp.deleteOnExit();

        KmlWriter writer = new KmlWriter();
        writer.setOutput(temp);
        writer.write(kml);
        writer.dispose();

        DomCompare.compare(
                new File(pathToTestFile), temp);

    }
}