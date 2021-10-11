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
import java.util.ArrayList;
import java.util.List;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.stream.XMLStreamException;

import org.geotoolkit.data.kml.model.KmlException;
import org.geotoolkit.xml.DomCompare;

import org.junit.Test;
import org.xml.sax.SAXException;

/**
 *
 * @author Samuel Andrés
 * @module
 */
public class DataRWTest extends org.geotoolkit.test.TestBase {

    private static final String pathToTestFile = "src/test/resources/org/geotoolkit/data/kml/dataRW.xml";

    @Test
    public void cameraReadTest() throws IOException, XMLStreamException {
        final DataReader reader = new DataReader();
        reader.setInput(new File(pathToTestFile));
        final List<String> racine = reader.read();
        reader.dispose();
        for (String element : racine) {
//          System.out.println(element);
        }
    }

    @Test
    public void cameraWriteTest() throws KmlException, IOException, XMLStreamException, ParserConfigurationException, SAXException {
        final List<String> racine = new ArrayList<>();
        racine.add("Je suis un element.");
        racine.add("J'en suis un autre.");

        File temp = File.createTempFile("testDataRW", ".xml");
        temp.deleteOnExit();

        DataWriter writer = new DataWriter();
        writer.setOutput(temp);
        writer.write(racine);
        writer.dispose();

        DomCompare.compare(new File(pathToTestFile), temp);
    }
}
