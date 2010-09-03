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
import org.geotoolkit.xal.model.AdministrativeArea;
import org.geotoolkit.xal.model.Country;
import org.geotoolkit.xal.model.Locality;
import org.geotoolkit.xal.model.PostBox;
import org.geotoolkit.xal.model.PostBoxNumber;
import org.geotoolkit.xal.model.PostOffice;
import org.geotoolkit.xal.model.PostalRoute;
import org.geotoolkit.xal.model.PostalRouteNumber;
import org.geotoolkit.xal.model.SubAdministrativeArea;
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
 * @author Samuel Andrés
 */
public class AdministrativeAreaTest {

    private static final double DELTA = 0.000000000001;
    private static final String pathToTestFile = "src/test/resources/org/geotoolkit/xal/administrativeArea.xml";

    public AdministrativeAreaTest() {
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
    public void administrativeAreaReadTest() throws IOException, XMLStreamException {

        final XalReader reader = new XalReader();
        reader.setInput(new File(pathToTestFile));
        final Xal xalObjects = reader.read();
        reader.dispose();

        assertEquals(1, xalObjects.getAddressDetails().size());
        final AddressDetails addressDetails0 = xalObjects.getAddressDetails().get(0);

        final AdministrativeArea administrativeArea = addressDetails0.getAdministrativeArea();

        final SubAdministrativeArea subAdministrativeArea = administrativeArea.getSubAdministrativeArea();

        final Locality locality1 = subAdministrativeArea.getLocality();
        assertEquals("SAALocality", locality1.getType());

        final PostOffice postOffice = locality1.getPostOffice();
        assertEquals("postOfficeType", postOffice.getType());
        assertEquals("postOfficeIndicator", postOffice.getIndicator());

        final Locality locality2 = administrativeArea.getLocality();
        assertEquals("AALocality", locality2.getType());

        final PostalRouteNumber postalRouteNumber = locality2.getPostalRoute().getPostalRouteNumber();
        assertEquals("postalRouteCode", postalRouteNumber.getGrPostal().getCode());
        assertEquals("postal route number", postalRouteNumber.getContent());

    }

    @Test
    public void administrativeAreaWriteTest() throws IOException, XMLStreamException, ParserConfigurationException, SAXException{
        final XalFactory xalFactory = DefaultXalFactory.getInstance();

        final PostalRouteNumber postalRouteNumber = xalFactory.createPostalRouteNumber(xalFactory.createGrPostal("postalRouteCode"), "postal route number");
        final PostalRoute postalRoute = xalFactory.createPostalRoute();
        postalRoute.setPostalRouteNumber(postalRouteNumber);

        final Locality localityAA = xalFactory.createLocality();
        localityAA.setType("AALocality");
        localityAA.setPostalRoute(postalRoute);


        final PostOffice postOffice = xalFactory.createPostOffice();
        postOffice.setType("postOfficeType");
        postOffice.setIndicator("postOfficeIndicator");

        final Locality localitySAA = xalFactory.createLocality();
        localitySAA.setPostOffice(postOffice);
        localitySAA.setType("SAALocality");

        final SubAdministrativeArea subAdministrativeArea = xalFactory.createSubAdministrativeArea();
        subAdministrativeArea.setLocality(localitySAA);

        final AdministrativeArea administrativeArea = xalFactory.createAdministrativeArea();
        administrativeArea.setSubAdministrativeArea(subAdministrativeArea);
        administrativeArea.setLocality(localityAA);


        final AddressDetails addressDetails = xalFactory.createAddressDetails();
            addressDetails.setAdministrativeArea(administrativeArea);

        final Xal xal = xalFactory.createXal(Arrays.asList(addressDetails), null);

        final File temp = File.createTempFile("administrativeArea",".xml");
        temp.deleteOnExit();

        final XalWriter writer = new XalWriter();
        writer.setOutput(temp);
        writer.write(xal);
        writer.dispose();

        DomCompare.compare(
                 new File(pathToTestFile), temp);

    }

}