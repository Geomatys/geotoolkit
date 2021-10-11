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

import java.net.URISyntaxException;
import org.geotoolkit.data.kml.xml.KmlReader;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.Arrays;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.stream.XMLStreamException;

import org.geotoolkit.data.kml.model.Kml;
import org.geotoolkit.data.kml.model.KmlException;
import org.geotoolkit.data.kml.model.KmlModelConstants;
import org.geotoolkit.data.kml.model.NetworkLinkControl;
import org.geotoolkit.data.kml.model.Update;
import org.geotoolkit.data.kml.xml.KmlConstants;
import org.geotoolkit.data.kml.xml.KmlWriter;
import org.geotoolkit.xml.DomCompare;

import org.junit.Test;

import org.opengis.feature.Feature;
import static org.junit.Assert.*;
import org.xml.sax.SAXException;

/**
 *
 * @author Samuel Andrés
 * @module
 */
public class ReplaceTest extends org.geotoolkit.test.TestBase {

    private static final String pathToTestFile = "src/test/resources/org/geotoolkit/data/kml/replace.kml";

    @Test
    public void replaceReadTest() throws IOException, XMLStreamException, KmlException, URISyntaxException {
        final KmlReader reader = new KmlReader();
        reader.setInput(new File(pathToTestFile));
        final Kml kmlObjects = reader.read();
        reader.dispose();

        final NetworkLinkControl networkLinkControl = kmlObjects.getNetworkLinkControl();
        final Update update = networkLinkControl.getUpdate();
        final URI targetHref = update.getTargetHref();
        assertEquals("http://chezmoi.com/tests.kml", targetHref.toString());

        assertEquals(2, update.getUpdates().size());
        Feature placemark = (Feature) update.getUpdates().get(0);
        assertEquals(KmlModelConstants.TYPE_PLACEMARK, placemark.getType());
        assertEquals("Replace placemark", placemark.getPropertyValue(KmlConstants.TAG_NAME));

        Feature groundOverlay = (Feature) update.getUpdates().get(1);
        assertEquals(KmlModelConstants.TYPE_GROUND_OVERLAY, groundOverlay.getType());
        assertEquals("Replace overlay", groundOverlay.getPropertyValue(KmlConstants.TAG_NAME));
    }

    @Test
    public void replaceWriteTest() throws KmlException, IOException, XMLStreamException, ParserConfigurationException,
            SAXException, URISyntaxException
    {
        final KmlFactory kmlFactory = DefaultKmlFactory.getInstance();

        final Feature placemark = kmlFactory.createPlacemark();
        placemark.setPropertyValue(KmlConstants.TAG_NAME, "Replace placemark");

        final Feature groundOverlay = kmlFactory.createGroundOverlay();
        groundOverlay.setPropertyValue(KmlConstants.TAG_NAME, "Replace overlay");

        final URI targetHref = new URI("http://chezmoi.com/tests.kml");

        final Update update = kmlFactory.createUpdate();
        update.setTargetHref(targetHref);
        update.setUpdates(Arrays.<Object>asList(placemark, groundOverlay));

        final NetworkLinkControl networkLinkControl = kmlFactory.createNetworkLinkControl();
        networkLinkControl.setUpdate(update);


        final Kml kml = kmlFactory.createKml(networkLinkControl, null, null, null);
        kml.setVersion(KmlConstants.URI_KML_2_1);

        final File temp = File.createTempFile("testReplace", ".kml");
        temp.deleteOnExit();

        final KmlWriter writer = new KmlWriter();
        writer.setOutput(temp);
        writer.write(kml);
        writer.dispose();

        DomCompare.compare(new File(pathToTestFile), temp);
    }
}
