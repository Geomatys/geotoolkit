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

import java.awt.Color;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
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
import org.geotoolkit.xml.DomCompare;

import org.junit.Test;

import org.opengis.feature.Feature;
import org.geotoolkit.data.kml.xml.KmlConstants;
import org.xml.sax.SAXException;
import static org.junit.Assert.*;

/**
 *
 * @author Samuel Andrés
 * @module
 */
public class StyleTest extends org.geotoolkit.test.TestBase {

    private static final double DELTA = 0.000000000001;
    private static final String pathToTestFile = "src/test/resources/org/geotoolkit/data/kml/style.kml";

    @Test
    public void styleReadTest() throws IOException, XMLStreamException, URISyntaxException, KmlException {
        final Feature document;
        {
            final KmlReader reader = new KmlReader();
            reader.setInput(new File(pathToTestFile));
            final Kml kmlObjects = reader.read();
            reader.dispose();
            document = kmlObjects.getAbstractFeature();
        }
        assertEquals(KmlModelConstants.TYPE_DOCUMENT, document.getType());

        Iterator<?> i = ((Iterable<?>) document.getPropertyValue(KmlConstants.TAG_STYLE_SELECTOR)).iterator();
        assertTrue("Expected at least one element.", i.hasNext());
        {
            Style style = (Style) i.next();
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
        assertFalse("Expected exactly one element.", i.hasNext());

        i = ((Iterable<?>) document.getPropertyValue(KmlConstants.TAG_FEATURES)).iterator();
        assertTrue("Expected at least one element.", i.hasNext());
        {
            Feature placemark = (Feature) i.next();
            assertEquals("Google Earth - New Polygon",    placemark.getPropertyValue(KmlConstants.TAG_NAME));
            assertEquals("Here is some descriptive text", placemark.getPropertyValue(KmlConstants.TAG_DESCRIPTION));
            assertEquals(new URI("#myDefaultStyles"),     placemark.getPropertyValue(KmlConstants.TAG_STYLE_URL));
        }

        assertTrue("Expected at least 2 elements.", i.hasNext());
        {
            Feature placemark = (Feature) i.next();
            assertEquals("Google Earth - New Path",     placemark.getPropertyValue(KmlConstants.TAG_NAME));
            assertEquals(new URI("#myDefaultStyles"),   placemark.getPropertyValue(KmlConstants.TAG_STYLE_URL));
            assertFalse("Expected exactly 2 elements.", i.hasNext());
        }
    }

    @Test
    public void styleWriteTest() throws KmlException, IOException, XMLStreamException, ParserConfigurationException, SAXException, URISyntaxException {
        final KmlFactory kmlFactory = DefaultKmlFactory.getInstance();

        final Feature placemark0 = kmlFactory.createPlacemark();
        placemark0.setPropertyValue(KmlConstants.TAG_NAME, "Google Earth - New Polygon");
        placemark0.setPropertyValue(KmlConstants.TAG_DESCRIPTION, "Here is some descriptive text");
        placemark0.setPropertyValue(KmlConstants.TAG_STYLE_URL, new URI("#myDefaultStyles"));

        final Feature placemark1 = kmlFactory.createPlacemark();
        placemark1.setPropertyValue(KmlConstants.TAG_NAME, "Google Earth - New Path");
        placemark1.setPropertyValue(KmlConstants.TAG_STYLE_URL, new URI("#myDefaultStyles"));

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
        document.setPropertyValue(KmlConstants.TAG_STYLE_SELECTOR, style);
        document.setPropertyValue(KmlConstants.TAG_FEATURES, Arrays.asList(placemark0,placemark1));

        final Kml kml = kmlFactory.createKml(null, document, null, null);

        final File temp = File.createTempFile("testStyle", ".kml");
        temp.deleteOnExit();

        final KmlWriter writer = new KmlWriter();
        writer.setOutput(temp);
        writer.write(kml);
        writer.dispose();

        DomCompare.compare(new File(pathToTestFile), temp);
    }
}
