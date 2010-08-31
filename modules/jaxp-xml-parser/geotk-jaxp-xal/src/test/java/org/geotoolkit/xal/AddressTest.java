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
import org.geotoolkit.xal.model.AddressIdentifier;
import org.geotoolkit.xal.model.GenericTypedGrPostal;
import org.geotoolkit.xal.model.PostalServiceElements;
import org.geotoolkit.xal.model.SortingCode;
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
public class AddressTest {


    private static final double DELTA = 0.000000000001;
    private static final String pathToTestFile = "src/test/resources/org/geotoolkit/xal/address.xml";

    public AddressTest() {
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
    public void addressReadTest() throws IOException, XMLStreamException {

        final XalReader reader = new XalReader();
        reader.setInput(new File(pathToTestFile));
        final Xal xalObjects = reader.read();
        reader.dispose();

        assertEquals("v", xalObjects.getVersion());
        assertEquals(1, xalObjects.getAddressDetails().size());
        final AddressDetails addressDetails0 = xalObjects.getAddressDetails().get(0);
        assertEquals("addressType", addressDetails0.getAddressType());
        assertEquals("currentStatus", addressDetails0.getCurrentStatus());
        assertEquals("validFrom", addressDetails0.getValidFromDate());
        assertEquals("validTo", addressDetails0.getValidToDate());
        assertEquals("usage", addressDetails0.getUsage());
        assertEquals("code", addressDetails0.getGrPostal().getCode());
        assertEquals("key", addressDetails0.getAddressDetailsKey());


        final PostalServiceElements postalServiceElements = addressDetails0.getPostalServiceElements();
        assertEquals("typePostalServiceElement", postalServiceElements.getType());

        assertEquals(2, postalServiceElements.getAddressIdentifiers().size());
        final AddressIdentifier addressIdentifier0 = postalServiceElements.getAddressIdentifiers().get(0);
        assertEquals("code34", addressIdentifier0.getGrPostal().getCode());
        assertEquals("identifierType34", addressIdentifier0.getIdentifierType());
        assertEquals("type34", addressIdentifier0.getType());
        assertEquals("Hérault", addressIdentifier0.getContent());
        final AddressIdentifier addressIdentifier1 = postalServiceElements.getAddressIdentifiers().get(1);
        assertEquals("code11", addressIdentifier1.getGrPostal().getCode());
        assertEquals("identifierType11", addressIdentifier1.getIdentifierType());
        assertEquals("type11", addressIdentifier1.getType());
        assertEquals("Aude", addressIdentifier1.getContent());

        final GenericTypedGrPostal endorsementLineCode = postalServiceElements.getEndorsementLineCode();
        assertEquals("code30", endorsementLineCode.getGrPostal().getCode());
        assertEquals("type30", endorsementLineCode.getType());
        assertEquals("Gard", endorsementLineCode.getContent());

        final GenericTypedGrPostal keyLineCode = postalServiceElements.getKeyLineCode();
        assertEquals("code64", keyLineCode.getGrPostal().getCode());
        assertEquals("type64", keyLineCode.getType());
        assertEquals("Pyrénées Atlantiques", keyLineCode.getContent());

        final GenericTypedGrPostal barCode = postalServiceElements.getBarcode();
        assertEquals("code66", barCode.getGrPostal().getCode());
        assertEquals("type66", barCode.getType());
        assertEquals("Pyrénées Orientales", barCode.getContent());

        final SortingCode sortingCode = postalServiceElements.getSortingCode();
        assertEquals("codeSortingCode", sortingCode.getGrPostal().getCode());
        assertEquals("typeSortingCode", sortingCode.getType());

        final GenericTypedGrPostal addressLatitude = postalServiceElements.getAddressLatitude();
        assertEquals("codeAddressLatitude", addressLatitude.getGrPostal().getCode());
        assertEquals("typeAddressLatitude", addressLatitude.getType());
        assertEquals("Latitude", addressLatitude.getContent());

        final GenericTypedGrPostal addressLatitudeDirection = postalServiceElements.getAddressLatitudeDirection();
        assertEquals("codeLatitudeDirection", addressLatitudeDirection.getGrPostal().getCode());
        assertEquals("typeLatitudeDirection", addressLatitudeDirection.getType());
        assertEquals("LatitudeDirection", addressLatitudeDirection.getContent());

        final GenericTypedGrPostal addressLongitude = postalServiceElements.getAddressLongitude();
        assertEquals("codeLongitude", addressLongitude.getGrPostal().getCode());
        assertEquals("typeLongitude", addressLongitude.getType());
        assertEquals("Longitude", addressLongitude.getContent());

        final GenericTypedGrPostal addressLongitudeDirection = postalServiceElements.getAddressLongitudeDirection();
        assertEquals("codeLongitudeDirection", addressLongitudeDirection.getGrPostal().getCode());
        assertEquals("typeLongitudeDirection", addressLongitudeDirection.getType());
        assertEquals("LongitudeDirection", addressLongitudeDirection.getContent());

        assertEquals(2, postalServiceElements.getSupplementaryPostalServiceData().size());

        final GenericTypedGrPostal sps1 = postalServiceElements.getSupplementaryPostalServiceData().get(0);
        assertEquals("codeSPS1", sps1.getGrPostal().getCode());
        assertEquals("typeSPS1", sps1.getType());
        assertEquals("First supplementary postal service data", sps1.getContent());

        final GenericTypedGrPostal sps2 = postalServiceElements.getSupplementaryPostalServiceData().get(1);
        assertEquals("codeSPS2", sps2.getGrPostal().getCode());
        assertEquals("typeSPS2", sps2.getType());
        assertEquals("Second supplementary postal service data", sps2.getContent());

        final GenericTypedGrPostal address = addressDetails0.getAddress();
        assertEquals("addressCode", address.getGrPostal().getCode());
        assertEquals("addressType", address.getType());
        assertEquals("Une adresse", address.getContent());

    }

    @Test
    public void addressWriteTest() throws IOException, XMLStreamException, ParserConfigurationException, SAXException{
        final XalFactory xalFactory = DefaultXalFactory.getInstance();

        final PostalServiceElements postalServiceElements = xalFactory.createPostalServiceElements();

        final AddressIdentifier addressIdentifier0 = xalFactory.createAddressIdentifier();
        addressIdentifier0.setGrPostal(xalFactory.createGrPostal("code34"));
        addressIdentifier0.setIdentifierType("identifierType34");
        addressIdentifier0.setType("type34");
        addressIdentifier0.setContent("Hérault");

        final AddressIdentifier addressIdentifier1 = xalFactory.createAddressIdentifier();
        addressIdentifier1.setGrPostal(xalFactory.createGrPostal("code11"));
        addressIdentifier1.setIdentifierType("identifierType11");
        addressIdentifier1.setType("type11");
        addressIdentifier1.setContent("Aude");
        postalServiceElements.setAddressIdentifiers(Arrays.asList(addressIdentifier0, addressIdentifier1));

        final GenericTypedGrPostal endorsementLineCode = xalFactory.createGenericTypedGrPostal();
        endorsementLineCode.setGrPostal(xalFactory.createGrPostal("code30"));
        endorsementLineCode.setType("type30");
        endorsementLineCode.setContent("Gard");
        postalServiceElements.setEndorsementLineCode(endorsementLineCode);

        final GenericTypedGrPostal keyLineCode = xalFactory.createGenericTypedGrPostal();
        keyLineCode.setGrPostal(xalFactory.createGrPostal("code64"));
        keyLineCode.setType("type64");
        keyLineCode.setContent("Pyrénées Atlantiques");
        postalServiceElements.setKeyLineCode(keyLineCode);

        final GenericTypedGrPostal barCode = xalFactory.createGenericTypedGrPostal();
        barCode.setGrPostal(xalFactory.createGrPostal("code66"));
        barCode.setType("type66");
        barCode.setContent("Pyrénées Orientales");
        postalServiceElements.setBarcode(barCode);

        final SortingCode sortingCode = xalFactory.createSortingCode(
                "typeSortingCode", xalFactory.createGrPostal("codeSortingCode"));
        postalServiceElements.setSortingCode(sortingCode);

        final GenericTypedGrPostal addressLatitude = xalFactory.createGenericTypedGrPostal();
        addressLatitude.setGrPostal(xalFactory.createGrPostal("codeAddressLatitude"));
        addressLatitude.setType("typeAddressLatitude");
        addressLatitude.setContent("Latitude");
        postalServiceElements.setAddressLatitude(addressLatitude);

        final GenericTypedGrPostal addressLatitudeDirection = xalFactory.createGenericTypedGrPostal();
        addressLatitudeDirection.setGrPostal(xalFactory.createGrPostal("codeLatitudeDirection"));
        addressLatitudeDirection.setType("typeLatitudeDirection");
        addressLatitudeDirection.setContent("LatitudeDirection");
        postalServiceElements.setAddressLatitudeDirection(addressLatitudeDirection);

        final GenericTypedGrPostal addressLongitude = xalFactory.createGenericTypedGrPostal();
        addressLongitude.setGrPostal(xalFactory.createGrPostal("codeLongitude"));
        addressLongitude.setType("typeLongitude");
        addressLongitude.setContent("Longitude");
        postalServiceElements.setAddressLongitude(addressLongitude);

        final GenericTypedGrPostal addressLongitudeDirection = xalFactory.createGenericTypedGrPostal();
        addressLongitudeDirection.setGrPostal(xalFactory.createGrPostal("codeLongitudeDirection"));
        addressLongitudeDirection.setType("typeLongitudeDirection");
        addressLongitudeDirection.setContent("LongitudeDirection");
        postalServiceElements.setAddressLongitudeDirection(addressLongitudeDirection);

        final GenericTypedGrPostal sps1 = xalFactory.createGenericTypedGrPostal();
        sps1.setGrPostal(xalFactory.createGrPostal("codeSPS1"));
        sps1.setType("typeSPS1");
        sps1.setContent("First supplementary postal service data");

        final GenericTypedGrPostal sps2 = xalFactory.createGenericTypedGrPostal();
        sps2.setGrPostal(xalFactory.createGrPostal("codeSPS2"));
        sps2.setType("typeSPS2");
        sps2.setContent("Second supplementary postal service data");
        postalServiceElements.setSupplementaryPostalServiceData(Arrays.asList(sps1, sps2));

        postalServiceElements.setType("typePostalServiceElement");

        final GenericTypedGrPostal address = xalFactory.createGenericTypedGrPostal();
        address.setGrPostal(xalFactory.createGrPostal("addressCode"));
        address.setType("addressType");
        address.setContent("Une adresse");

        final AddressDetails addressDetails = xalFactory.createAddressDetails();
        addressDetails.setPostalServiceElements(postalServiceElements);
        addressDetails.setAddress(address);
        addressDetails.setAddressType("addressType");
        addressDetails.setCurrentStatus("currentStatus");
        addressDetails.setValidFromDate("validFrom");
        addressDetails.setValidToDate("validTo");
        addressDetails.setUsage("usage");
        addressDetails.setGrPostal(xalFactory.createGrPostal("code"));
        addressDetails.setAddressDetailsKey("key");


        final Xal xal = xalFactory.createXal(Arrays.asList(addressDetails), "v");

        final File temp = File.createTempFile("addressTest",".xal");
        temp.deleteOnExit();

        final XalWriter writer = new XalWriter();
        writer.setOutput(temp);
        writer.write(xal);
        writer.dispose();

        DomCompare.compare(
                 new File(pathToTestFile), temp);

    }

}