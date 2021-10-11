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
import java.util.Arrays;
import java.util.List;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.stream.XMLStreamException;

import org.geotoolkit.data.kml.model.Data;
import org.geotoolkit.data.kml.model.ExtendedData;
import org.geotoolkit.data.kml.model.Kml;
import org.geotoolkit.data.kml.model.KmlException;
import org.geotoolkit.data.kml.model.KmlModelConstants;
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
public class DataTest extends org.geotoolkit.test.TestBase {

    private static final String pathToTestFile = "src/test/resources/org/geotoolkit/data/kml/data.kml";

    @Test
    public void dataReadTest() throws IOException, XMLStreamException, KmlException, URISyntaxException {
        final KmlReader reader = new KmlReader();
        reader.setInput(new File(pathToTestFile));
        final Kml kmlObjects = reader.read();
        reader.dispose();

        final Feature placemark = kmlObjects.getAbstractFeature();
        assertEquals(KmlModelConstants.TYPE_PLACEMARK, placemark.getType());
        assertEquals("Club house", placemark.getPropertyValue(KmlConstants.TAG_NAME));

        final ExtendedData extendedData = (ExtendedData) ((List)placemark.getPropertyValue(KmlConstants.TAG_EXTENDED_DATA)).get(0);
        assertEquals(3, extendedData.getDatas().size());

        final Data data0 = extendedData.getDatas().get(0);
        final Data data1 = extendedData.getDatas().get(1);
        final Data data2 = extendedData.getDatas().get(2);

        assertEquals("holeNumber", data0.getName());
        assertEquals("1", data0.getValue());
        assertEquals("holeYardage", data1.getName());
        assertEquals("234", data1.getValue());
        assertEquals("holePar", data2.getName());
        assertEquals("4", data2.getValue());
    }

    @Test
    public void dataWriteTest() throws KmlException, IOException, XMLStreamException, ParserConfigurationException, SAXException, URISyntaxException {
        final KmlFactory kmlFactory = DefaultKmlFactory.getInstance();

        final Feature placemark = kmlFactory.createPlacemark();

        final ExtendedData extendedData = kmlFactory.createExtendedData();

        final Data data0 = kmlFactory.createData();
        data0.setName("holeNumber");
        data0.setValue("1");

        final Data data1 = kmlFactory.createData();
        data1.setName("holeYardage");
        data1.setValue("234");

        final Data data2 = kmlFactory.createData();
        data2.setName("holePar");
        data2.setValue("4");

        extendedData.setDatas(Arrays.asList(data0, data1, data2));

        placemark.setPropertyValue(KmlConstants.TAG_NAME, "Club house");
        placemark.setPropertyValue(KmlConstants.TAG_EXTENDED_DATA, extendedData);

        final Kml kml = kmlFactory.createKml(null, placemark, null, null);

        final File temp = File.createTempFile("testData", ".kml");
        temp.deleteOnExit();

        final KmlWriter writer = new KmlWriter();
        writer.setOutput(temp);
        writer.write(kml);
        writer.dispose();

        DomCompare.compare(new File(pathToTestFile), temp);
    }
}
