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

import org.geotoolkit.data.kml.model.Delete;
import org.geotoolkit.data.kml.model.IdAttributes;
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
import org.xml.sax.SAXException;

import static org.junit.Assert.*;

/**
 *
 * @author Samuel Andrés
 * @module
 */
public class DeleteTest extends org.geotoolkit.test.TestBase {

    private static final String pathToTestFile = "src/test/resources/org/geotoolkit/data/kml/delete.kml";

    @Test
    public void deleteReadTest() throws IOException, XMLStreamException, KmlException, URISyntaxException {
        final KmlReader reader = new KmlReader();
        reader.setInput(new File(pathToTestFile));
        final Kml kmlObjects = reader.read();
        reader.dispose();

        final NetworkLinkControl networkLinkControl = kmlObjects.getNetworkLinkControl();
        final Update update = networkLinkControl.getUpdate();
        final URI targetHref = update.getTargetHref();
        assertEquals("http://www.foo.com/Point.kml", targetHref.toString());

        assertEquals(1, update.getUpdates().size());
        final Delete delete = (Delete) update.getUpdates().get(0);

        assertEquals(1, delete.getFeatures().size());
        assertEquals(KmlModelConstants.TYPE_PLACEMARK, delete.getFeatures().get(0).getType());
        final Feature placemark = delete.getFeatures().get(0);
        assertEquals("pa3556", ((IdAttributes) placemark.getPropertyValue(KmlConstants.ATT_ID)).getTargetId());
    }

    @Test
    public void deleteWriteTest() throws KmlException, IOException, XMLStreamException, ParserConfigurationException, SAXException, URISyntaxException {
        final KmlFactory kmlFactory = DefaultKmlFactory.getInstance();

        final Feature placemark = kmlFactory.createPlacemark();
        final IdAttributes placemarkIdAttributes = kmlFactory.createIdAttributes(null, "pa3556");
        placemark.setPropertyValue(KmlConstants.ATT_ID, placemarkIdAttributes);

        final Delete delete = kmlFactory.createDelete();
        delete.setFeatures(Arrays.asList(placemark));

        final URI targetHref = new URI("http://www.foo.com/Point.kml");

        final Update update = kmlFactory.createUpdate();
        update.setUpdates(Arrays.asList((Object) delete));
        update.setTargetHref(targetHref);

        final NetworkLinkControl networkLinkControl = kmlFactory.createNetworkLinkControl();
        networkLinkControl.setUpdate(update);

        final Kml kml = kmlFactory.createKml(networkLinkControl, null, null, null);

        final File temp = File.createTempFile("testDelete", ".kml");
        temp.deleteOnExit();

        final KmlWriter writer = new KmlWriter();
        writer.setOutput(temp);
        writer.write(kml);
        writer.dispose();

        DomCompare.compare(new File(pathToTestFile), temp);
    }
}
