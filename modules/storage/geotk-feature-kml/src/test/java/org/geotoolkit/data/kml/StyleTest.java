/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2010, Geomatys
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation; either
 *    version 2.1 of the License, or (at your option) any later version.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */
package org.geotoolkit.data.kml;

import org.geotoolkit.feature.FeatureUtilities;
import java.awt.Color;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collection;
import java.util.Iterator;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.stream.XMLStreamException;

import org.geotoolkit.data.kml.model.BasicLink;
import org.geotoolkit.data.kml.model.ColorMode;
import org.geotoolkit.data.kml.model.IconStyle;
import org.geotoolkit.data.kml.model.IdAttributes;
import org.geotoolkit.data.kml.model.Kml;
import org.geotoolkit.data.kml.model.KmlException;
import org.geotoolkit.data.kml.model.KmlModelConstants;
import org.geotoolkit.data.kml.model.LabelStyle;
import org.geotoolkit.data.kml.model.LineStyle;
import org.geotoolkit.data.kml.model.PolyStyle;
import org.geotoolkit.data.kml.model.Style;
import org.geotoolkit.data.kml.xml.KmlReader;
import org.geotoolkit.data.kml.xml.KmlWriter;
import org.geotoolkit.factory.FactoryFinder;
import org.geotoolkit.factory.Hints;
import org.geotoolkit.feature.LenientFeatureFactory;
import org.geotoolkit.xml.DomCompare;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import org.opengis.feature.Feature;
import org.opengis.feature.FeatureFactory;
import org.opengis.feature.Property;
import org.xml.sax.SAXException;
import static org.junit.Assert.*;

/**
 *
 * @author Samuel Andrés
 * @module pending
 */
public class StyleTest {

    private static final double DELTA = 0.000000000001;
    private static final String pathToTestFile = "src/test/resources/org/geotoolkit/data/kml/style.kml";
    private static final FeatureFactory FF = FactoryFinder.getFeatureFactory(
            new Hints(Hints.FEATURE_FACTORY, LenientFeatureFactory.class));

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
    public void styleReadTest() throws IOException, XMLStreamException, URISyntaxException, KmlException {

        final KmlReader reader = new KmlReader();
        reader.setInput(new File(pathToTestFile));
        final Kml kmlObjects = reader.read();
        reader.dispose();

        final Feature document = kmlObjects.getAbstractFeature();
        assertTrue(document.getType().equals(KmlModelConstants.TYPE_DOCUMENT));

        assertEquals(1, document.getProperties(KmlModelConstants.ATT_STYLE_SELECTOR.getName()).size());

        Iterator i = document.getProperties(KmlModelConstants.ATT_STYLE_SELECTOR.getName()).iterator();

        if (i.hasNext()) {
            Object object = ((Property) i.next()).getValue();

            assertTrue(object instanceof Style);
            Style style = (Style) object;
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
        }

        assertEquals(2, document.getProperties(KmlModelConstants.ATT_DOCUMENT_FEATURES.getName()).size());

        i = document.getProperties(KmlModelConstants.ATT_DOCUMENT_FEATURES.getName()).iterator();

        if (i.hasNext()) {
            Object object = i.next();
            assertTrue(object instanceof Feature);
            Feature placemark0 = (Feature) object;
            assertEquals("Google Earth - New Polygon", placemark0.getProperty(KmlModelConstants.ATT_NAME.getName()).getValue());
            assertEquals("Here is some descriptive text", placemark0.getProperty(KmlModelConstants.ATT_DESCRIPTION.getName()).getValue());
            assertEquals(new URI("#myDefaultStyles"), placemark0.getProperty(KmlModelConstants.ATT_STYLE_URL.getName()).getValue());
        }

        if (i.hasNext()) {
            Object object = i.next();
            assertTrue(object instanceof Feature);
            Feature placemark1 = (Feature) object;
            assertEquals("Google Earth - New Path", placemark1.getProperty(KmlModelConstants.ATT_NAME.getName()).getValue());
            assertEquals(new URI("#myDefaultStyles"), placemark1.getProperty(KmlModelConstants.ATT_STYLE_URL.getName()).getValue());
        }
    }

    @Test
    public void styleWriteTest() throws KmlException, IOException, XMLStreamException, ParserConfigurationException, SAXException, URISyntaxException {
        final KmlFactory kmlFactory = DefaultKmlFactory.getInstance();

        final Feature placemark0 = kmlFactory.createPlacemark();
        final Collection<Property> placemark0Properties = placemark0.getProperties();
        placemark0Properties.add(FF.createAttribute("Google Earth - New Polygon", KmlModelConstants.ATT_NAME, null));
        placemark0Properties.add(FF.createAttribute("Here is some descriptive text", KmlModelConstants.ATT_DESCRIPTION, null));
        placemark0Properties.add(FF.createAttribute(new URI("#myDefaultStyles"), KmlModelConstants.ATT_STYLE_URL, null));

        final Feature placemark1 = kmlFactory.createPlacemark();
        final Collection<Property> placemark1Properties = placemark1.getProperties();
        placemark1Properties.add(FF.createAttribute("Google Earth - New Path", KmlModelConstants.ATT_NAME, null));
        placemark1Properties.add(FF.createAttribute(new URI("#myDefaultStyles"), KmlModelConstants.ATT_STYLE_URL, null));


        final IconStyle iconStyle = kmlFactory.createIconStyle();
        final BasicLink icon = kmlFactory.createBasicLink();
        icon.setHref("http://myserver.com/icon.jpg");
        iconStyle.setIcon(icon);
        iconStyle.setColor(new Color(255, 0, 255, 161));
        iconStyle.setScale(1.399999976158142);

        final LabelStyle labelStyle = kmlFactory.createLabelStyle();
        labelStyle.setColor(new Color(255, 170, 255, 127));
        labelStyle.setScale(1.5);

        final LineStyle lineStyle = kmlFactory.createLineStyle();
        lineStyle.setColor(new Color(255, 0, 0, 255));
        lineStyle.setWidth(15);

        final PolyStyle polyStyle = kmlFactory.createPolyStyle();
        polyStyle.setColor(new Color(170, 170, 127, 127));
        polyStyle.setColorMode(ColorMode.RANDOM);

        final IdAttributes idAttributes = kmlFactory.createIdAttributes("myDefaultStyles", null);

        final Style style = kmlFactory.createStyle();
        style.setIdAttributes(idAttributes);
        style.setIconStyle(iconStyle);
        style.setLabelStyle(labelStyle);
        style.setLineStyle(lineStyle);
        style.setPolyStyle(polyStyle);

        final Feature document = kmlFactory.createDocument();
        final Collection<Property> documentProperties = document.getProperties();
        documentProperties.add(FF.createAttribute(style, KmlModelConstants.ATT_STYLE_SELECTOR, null));
        documentProperties.add(FeatureUtilities.wrapProperty(placemark0, KmlModelConstants.ATT_DOCUMENT_FEATURES));
        documentProperties.add(FeatureUtilities.wrapProperty(placemark1, KmlModelConstants.ATT_DOCUMENT_FEATURES));


        final Kml kml = kmlFactory.createKml(null, document, null, null);

        final File temp = File.createTempFile("testStyle", ".kml");
        temp.deleteOnExit();

        final KmlWriter writer = new KmlWriter();
        writer.setOutput(temp);
        writer.write(kml);
        writer.dispose();

        DomCompare.compare(
                new File(pathToTestFile), temp);

    }
}
