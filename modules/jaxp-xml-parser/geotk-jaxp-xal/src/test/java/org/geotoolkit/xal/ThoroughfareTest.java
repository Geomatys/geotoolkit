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
package org.geotoolkit.xal;

import java.io.File;
import java.io.IOException;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.stream.XMLStreamException;
import org.geotoolkit.xal.model.Xal;
import org.geotoolkit.xal.model.XalException;
import org.geotoolkit.xal.xml.XalReader;
import org.geotoolkit.xal.xml.XalWriter;
import org.geotoolkit.xml.DomCompare;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.xml.sax.SAXException;

/**
 *
 * @author Samuel Andrés
 */
public class ThoroughfareTest {

    private static final String pathToTestFile = "src/test/resources/org/geotoolkit/xal/thoroughfare.xml";

    public ThoroughfareTest() {
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
    public void thoroughfareTest()
                throws IOException, XMLStreamException, ParserConfigurationException, SAXException, XalException {

        final XalReader reader = new XalReader();
        reader.setInput(new File(pathToTestFile));
        final Xal xal = reader.read();
        reader.dispose();


        final File temp = File.createTempFile("thoroughfare",".xml");
        temp.deleteOnExit();

        final XalWriter writer = new XalWriter();
        writer.setOutput(temp);
        writer.write(xal);
        writer.dispose();

        DomCompare.compare(
                 new File(pathToTestFile), temp);

    }


}