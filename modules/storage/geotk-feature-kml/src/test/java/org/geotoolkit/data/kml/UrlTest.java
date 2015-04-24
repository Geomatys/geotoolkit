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

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.stream.XMLStreamException;

import org.geotoolkit.data.kml.model.Kml;
import org.geotoolkit.data.kml.model.KmlException;
import org.geotoolkit.data.kml.model.KmlModelConstants;
import org.geotoolkit.data.kml.model.Link;
import org.geotoolkit.data.kml.model.RefreshMode;
import org.geotoolkit.data.kml.model.Url;
import org.geotoolkit.data.kml.model.ViewRefreshMode;
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
 * @module pending
 */
public class UrlTest extends org.geotoolkit.test.TestBase {

    private static final double DELTA = 0.000000000001;
    private static final String pathToTestFile = "src/test/resources/org/geotoolkit/data/kml/url.kml";

    @Test
    public void urlReadTest() throws IOException, XMLStreamException, KmlException, URISyntaxException {
        final KmlReader reader = new KmlReader();
        reader.setInput(new File(pathToTestFile));
        final Kml kmlObjects = reader.read();
        reader.dispose();

        final Feature networkLink = kmlObjects.getAbstractFeature();
        assertEquals(KmlModelConstants.TYPE_NETWORK_LINK, networkLink.getType());

        assertEquals("NE US Radar", networkLink.getPropertyValue(KmlConstants.TAG_NAME));
        assertEquals(Boolean.TRUE, networkLink.getPropertyValue(KmlConstants.TAG_FLY_TO_VIEW));

        final Url url = (Url) networkLink.getPropertyValue(KmlConstants.TAG_LINK);
        assertEquals("http://www.example.com/geotiff/NE/MergedReflectivityQComposite.kml", url.getHref());
        assertEquals(RefreshMode.ON_INTERVAL, url.getRefreshMode());
        assertEquals(30, url.getRefreshInterval(), DELTA);
        assertEquals(ViewRefreshMode.ON_STOP, url.getViewRefreshMode());
        assertEquals(7, url.getViewRefreshTime(), DELTA);
        String text = "BBOX=[bboxWest],[bboxSouth],[bboxEast],[bboxNorth];CAMERA=\\\n"
                + "      [lookatLon],[lookatLat],[lookatRange],[lookatTilt],[lookatHeading];VIEW=\\\n"
                + "      [horizFov],[vertFov],[horizPixels],[vertPixels],[terrainEnabled]";
        assertEquals(text, url.getViewFormat());
    }

    @Test
    public void urlWriteTest() throws KmlException, IOException, XMLStreamException, ParserConfigurationException, SAXException {
        final KmlFactory kmlFactory = DefaultKmlFactory.getInstance();

        final Feature networkLink = kmlFactory.createNetworkLink();
        networkLink.setPropertyValue(KmlConstants.TAG_NAME, "NE US Radar");
        networkLink.setPropertyValue(KmlConstants.TAG_FLY_TO_VIEW, Boolean.TRUE);

        final Link link = kmlFactory.createLink();
        link.setHref("http://www.example.com/geotiff/NE/MergedReflectivityQComposite.kml");
        link.setRefreshMode(RefreshMode.ON_INTERVAL);
        link.setRefreshInterval(30);
        link.setViewRefreshMode(ViewRefreshMode.ON_STOP);
        link.setViewRefreshTime(7);
        final String text = "BBOX=[bboxWest],[bboxSouth],[bboxEast],[bboxNorth];CAMERA=\\\n"
                + "      [lookatLon],[lookatLat],[lookatRange],[lookatTilt],[lookatHeading];VIEW=\\\n"
                + "      [horizFov],[vertFov],[horizPixels],[vertPixels],[terrainEnabled]";
        link.setViewFormat(text);

        final Url url = kmlFactory.createUrl(link);
        networkLink.setPropertyValue(KmlConstants.TAG_LINK, url);

        final Kml kml = kmlFactory.createKml(null, networkLink, null, null);

        final File temp = File.createTempFile("testUrl", ".kml");
        temp.deleteOnExit();

        final KmlWriter writer = new KmlWriter();
        writer.setOutput(temp);
        writer.write(kml);
        writer.dispose();

        DomCompare.compare(new File(pathToTestFile), temp);
    }
}
