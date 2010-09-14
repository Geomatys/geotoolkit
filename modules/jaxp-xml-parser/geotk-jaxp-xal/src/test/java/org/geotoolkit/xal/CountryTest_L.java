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
import org.geotoolkit.xal.model.Country;
import org.geotoolkit.xal.model.Locality;
import org.geotoolkit.xal.model.PostBox;
import org.geotoolkit.xal.model.PostBoxNumber;
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
import static org.junit.Assert.*;

/**
 *
 * @author Samuel Andrés
 */
public class CountryTest_L {

    private static final double DELTA = 0.000000000001;
    private static final String pathToTestFile = "src/test/resources/org/geotoolkit/xal/country_L.xml";

    public CountryTest_L() {
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
    public void country_LReadTest() throws IOException, XMLStreamException, XalException {

        final XalReader reader = new XalReader();
        reader.setInput(new File(pathToTestFile));
        final Xal xalObjects = reader.read();
        reader.dispose();

        assertEquals(1, xalObjects.getAddressDetails().size());
        final AddressDetails addressDetails0 = xalObjects.getAddressDetails().get(0);

        final Country country = addressDetails0.getCountry();

        final Locality locality = country.getLocality();
        assertEquals("countryLocality", locality.getType());

        final PostBox postBox = locality.getPostBox();
        assertEquals("localityPostBox", postBox.getType());

        final PostBoxNumber postBoxNumber = postBox.getPostBoxNumber();
        assertEquals("obligatoire", postBoxNumber.getGrPostal().getCode());
        assertEquals("PBNcontent", postBoxNumber.getContent());

        

    }

    @Test
    public void country_LWriteTest() throws IOException, XMLStreamException, ParserConfigurationException, SAXException, XalException{
        final XalFactory xalFactory = DefaultXalFactory.getInstance();

        final PostBoxNumber postBoxNumber = xalFactory.createPostBoxNumber(xalFactory.createGrPostal("obligatoire"), "PBNcontent");
        
        final PostBox postBox = xalFactory.createPostBox();
        postBox.setPostBoxNumber(postBoxNumber);
        postBox.setType("localityPostBox");
        
        final Locality locality = xalFactory.createLocality();
        locality.setPostBox(postBox);
        locality.setType("countryLocality");

        final Country country = xalFactory.createCountry();
        country.setLocality(locality);


        final AddressDetails addressDetails = xalFactory.createAddressDetails();
            addressDetails.setCountry(country);

        final Xal xal = xalFactory.createXal(Arrays.asList(addressDetails), null);

        final File temp = File.createTempFile("country_L",".xml");
        temp.deleteOnExit();

        final XalWriter writer = new XalWriter();
        writer.setOutput(temp);
        writer.write(xal);
        writer.dispose();

        DomCompare.compare(
                 new File(pathToTestFile), temp);

    }

}