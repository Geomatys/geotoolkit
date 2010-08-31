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
import java.util.Arrays;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.stream.XMLStreamException;
import org.geotoolkit.xal.model.AddressDetails;
import org.geotoolkit.xal.model.AddressLines;
import org.geotoolkit.xal.model.GenericTypedGrPostal;
import org.geotoolkit.xal.model.Xal;
import org.geotoolkit.xal.xml.XalReader;
import org.geotoolkit.xal.xml.XalWriter;
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
 * @author SAmuel Andrés
 */
public class AddressLinesTest {

    private static final double DELTA = 0.000000000001;
    private static final String pathToTestFile = "src/test/resources/org/geotoolkit/xal/addressLines.xml";

    public AddressLinesTest() {
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
    public void addressLinesReadTest() throws IOException, XMLStreamException {

        final XalReader reader = new XalReader();
        reader.setInput(new File(pathToTestFile));
        final Xal xalObjects = reader.read();
        reader.dispose();

        assertEquals(1, xalObjects.getAddressDetails().size());
        final AddressDetails addressDetails0 = xalObjects.getAddressDetails().get(0);

        final AddressLines addressLines = addressDetails0.getAddressLines();
        assertEquals(2, addressLines.getAddressLines().size());

        final GenericTypedGrPostal addressLine0 = addressLines.getAddressLines().get(0);
        assertEquals("code1", addressLine0.getGrPostal().getCode());
        assertEquals("type1", addressLine0.getType());
        assertEquals("Première ligne", addressLine0.getContent());

        final GenericTypedGrPostal addressLine1 = addressLines.getAddressLines().get(1);
        assertEquals("code2", addressLine1.getGrPostal().getCode());
        assertEquals("type2", addressLine1.getType());
        assertEquals("Seconde ligne", addressLine1.getContent());

    }

    @Test
    public void addressWriteTest() throws IOException, XMLStreamException, ParserConfigurationException, SAXException{
        final XalFactory xalFactory = DefaultXalFactory.getInstance();

        final AddressLines addressLines = xalFactory.createAddressLines();

        final GenericTypedGrPostal addressLine0 = xalFactory.createGenericTypedGrPostal();
        addressLine0.setGrPostal(xalFactory.createGrPostal("code1"));
        addressLine0.setType("type1");
        addressLine0.setContent("Première ligne");

        final GenericTypedGrPostal addressLine1 = xalFactory.createGenericTypedGrPostal();
        addressLine1.setGrPostal(xalFactory.createGrPostal("code2"));
        addressLine1.setType("type2");
        addressLine1.setContent("Seconde ligne");
        addressLines.setAddressLines(Arrays.asList(addressLine0, addressLine1));

        final AddressDetails addressDetails = xalFactory.createAddressDetails();
        addressDetails.setAddressLines(addressLines);

        final Xal xal = xalFactory.createXal(Arrays.asList(addressDetails), null);

        final File temp = File.createTempFile("addressLinesTest",".xal");
        temp.deleteOnExit();

        final XalWriter writer = new XalWriter();
        writer.setOutput(temp);
        writer.write(xal);
        writer.dispose();

        DomCompare.compare(
                 new File(pathToTestFile), temp);

    }

}