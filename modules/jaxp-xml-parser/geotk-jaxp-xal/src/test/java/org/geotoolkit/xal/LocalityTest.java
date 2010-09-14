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
import org.geotoolkit.xal.model.AfterBeforeEnum;
import org.geotoolkit.xal.model.BuildingName;
import org.geotoolkit.xal.model.Department;
import org.geotoolkit.xal.model.DependentLocality;
import org.geotoolkit.xal.model.DependentLocalityNumber;
import org.geotoolkit.xal.model.GenericTypedGrPostal;
import org.geotoolkit.xal.model.LargeMailUser;
import org.geotoolkit.xal.model.LargeMailUserIdentifier;
import org.geotoolkit.xal.model.LargeMailUserName;
import org.geotoolkit.xal.model.Locality;
import org.geotoolkit.xal.model.PostBox;
import org.geotoolkit.xal.model.PostBoxNumber;
import org.geotoolkit.xal.model.PostOffice;
import org.geotoolkit.xal.model.PostalCode;
import org.geotoolkit.xal.model.PostalRoute;
import org.geotoolkit.xal.model.PostalRouteNumber;
import org.geotoolkit.xal.model.Premise;
import org.geotoolkit.xal.model.Thoroughfare;
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
public class LocalityTest {

    private static final double DELTA = 0.000000000001;
    private static final String pathToTestFile = "src/test/resources/org/geotoolkit/xal/locality.xml";

    public LocalityTest() {
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
    public void localityReadTest() throws IOException, XMLStreamException, XalException {

        final XalReader reader = new XalReader();
        reader.setInput(new File(pathToTestFile));
        final Xal xalObjects = reader.read();
        reader.dispose();

        assertEquals(1, xalObjects.getAddressDetails().size());
        final AddressDetails addressDetails0 = xalObjects.getAddressDetails().get(0);

        final Locality locality = addressDetails0.getLocality();
        assertEquals("localityType", locality.getType());
        assertEquals("localityUsageType", locality.getUsageType());
        assertEquals("localityIndicator", locality.getIndicator());
        assertEquals(2, locality.getAddressLines().size());
        assertEquals("locality address 1", locality.getAddressLines().get(0).getContent());
        assertEquals("locality address 2", locality.getAddressLines().get(1).getContent());
        
        assertEquals(1, locality.getLocalityNames().size());
        final GenericTypedGrPostal localityName = locality.getLocalityNames().get(0);
        assertEquals("localityNameType", localityName.getType());
        assertEquals("localityNameCode", localityName.getGrPostal().getCode());
        assertEquals("locality name", localityName.getContent());

        final LargeMailUser largeMailUser = locality.getLargeMailUser();
        assertEquals("largeMailUserType", largeMailUser.getType());
        assertEquals(1, largeMailUser.getAddressLines().size());
        assertEquals("lmu line 1", largeMailUser.getAddressLines().get(0).getContent());

        assertEquals(1, largeMailUser.getLargeMailUserNames().size());
        final LargeMailUserName largeMailUserName = largeMailUser.getLargeMailUserNames().get(0);
        assertEquals("lmunType", largeMailUserName.getType());
        assertEquals("lmunCode", largeMailUserName.getCode());
        assertEquals("large mail user name", largeMailUserName.getContent());

        final LargeMailUserIdentifier largeMailUserIdentifier = largeMailUser.getLargeMailUserIdentifier();
        assertEquals("lmuiType", largeMailUserIdentifier.getType());
        assertEquals("lmuiCode", largeMailUserIdentifier.getGrPostal().getCode());
        assertEquals("lmuiIndicator", largeMailUserIdentifier.getIndicator());
        assertEquals("lmu identifier", largeMailUserIdentifier.getContent());

        assertEquals(1, largeMailUser.getBuildingNames().size());
        final BuildingName buildingName = largeMailUser.getBuildingNames().get(0);
        assertEquals("BNType", buildingName.getType());
        assertEquals("BNCode", buildingName.getGrPostal().getCode());
        assertEquals(AfterBeforeEnum.BEFORE, buildingName.getTypeOccurrence());
        assertEquals("Building name content", buildingName.getContent());

        assertEquals("largeMailUserDepartment", largeMailUser.getDepartment().getType());

        final PostBox postBox = largeMailUser.getPostBox();
        assertEquals("largeMailUserPostBox", postBox.getType());
        final PostBoxNumber postBoxNumber = postBox.getPostBoxNumber();
        assertEquals("lmu", postBoxNumber.getGrPostal().getCode());
        assertEquals("post box number", postBoxNumber.getContent());

        assertEquals("largeMailUserThoroughfare", largeMailUser.getThoroughfare().getType());
        assertEquals("largeMailUserPostalCode", largeMailUser.getPostalCode().getType());

        assertEquals("localityThoroughfare", locality.getThoroughfare().getType());
        assertEquals("localityPremise", locality.getPremise().getType());

        final DependentLocality dependentLocality1 = locality.getDependentLocality();
        assertEquals("dlType", dependentLocality1.getType());
        assertEquals("dlUsageType", dependentLocality1.getUsageType());
        assertEquals("dlConnector", dependentLocality1.getConnector());
        assertEquals("dlIndicator", dependentLocality1.getIndicator());
        assertEquals(1, dependentLocality1.getAddressLines().size());
        assertEquals("dependent locality address", dependentLocality1.getAddressLines().get(0).getContent());
        assertEquals(1, dependentLocality1.getDependentLocalityNames().size());
        GenericTypedGrPostal dependentLocalityName = dependentLocality1.getDependentLocalityNames().get(0);
        assertEquals("dependent locality name", dependentLocalityName.getContent());
        assertEquals("dlnType", dependentLocalityName.getType());
        assertEquals("dlnCode", dependentLocalityName.getGrPostal().getCode());

        final PostBox dependentLocalityPostBox = dependentLocality1.getPostBox();
        assertEquals("dlPostBox", dependentLocalityPostBox.getType());
        final PostBoxNumber dependentLocalityPostBoxNumber = dependentLocalityPostBox.getPostBoxNumber();
        assertEquals("dl", dependentLocalityPostBoxNumber.getGrPostal().getCode());
        assertEquals("postBoxNumber", dependentLocalityPostBoxNumber.getContent());
        assertEquals("dlThoroughfare", dependentLocality1.getThoroughfare().getType());
        assertEquals("DLPremise", dependentLocality1.getPremise().getType());

        final DependentLocality dependentLocality2 = dependentLocality1.getDependentLocality();
        assertEquals("subDL", dependentLocality2.getType());
        assertEquals("subDL-largeMailUser", dependentLocality2.getLargeMailUser().getType());
        final DependentLocality dependentLocality3 = dependentLocality2.getDependentLocality();
        assertEquals("subsubDL", dependentLocality3.getType());
        assertEquals("subsubDL-postOffice", dependentLocality3.getPostOffice().getType());
        final DependentLocality dependentLocality4 = dependentLocality3.getDependentLocality();
        assertEquals("subsubsubDL", dependentLocality4.getType());
        PostalRoute postalRoute = dependentLocality4.getPostalRoute();
        assertEquals("subsubsubDL-postalRoute", postalRoute.getType());
        final PostalRouteNumber postalRouteNumber = postalRoute.getPostalRouteNumber();
        assertEquals("prn", postalRouteNumber.getGrPostal().getCode());
        assertEquals("postal route number", postalRouteNumber.getContent());

        assertEquals("dlPostalCode", dependentLocality1.getPostalCode().getType());
        assertEquals("localityPostalCode", locality.getPostalCode().getType());





    }

    @Test
    public void localityWriteTest() throws IOException, XMLStreamException, ParserConfigurationException, SAXException, XalException{
        final XalFactory xalFactory = DefaultXalFactory.getInstance();

        final GenericTypedGrPostal localityAddressLine1 = xalFactory.createGenericTypedGrPostal(null, null, "locality address 1");
        final GenericTypedGrPostal localityAddressLine2 = xalFactory.createGenericTypedGrPostal(null, null, "locality address 2");
        final GenericTypedGrPostal localityName = xalFactory.createGenericTypedGrPostal("localityNameType", xalFactory.createGrPostal("localityNameCode"), "locality name");

        final GenericTypedGrPostal largeMailUserAddresLine = xalFactory.createGenericTypedGrPostal(null, null, "lmu line 1");
        final LargeMailUserName largeMailUserName = xalFactory.createLargeMailUserName();
        largeMailUserName.setType("lmunType");
        largeMailUserName.setCode("lmunCode");
        largeMailUserName.setContent("large mail user name");

        final LargeMailUserIdentifier largeMailUserIdentifier = xalFactory.createLargeMailUserIdentifier();
        largeMailUserIdentifier.setType("lmuiType");
        largeMailUserIdentifier.setIndicator("lmuiIndicator");
        largeMailUserIdentifier.setGrPostal(xalFactory.createGrPostal("lmuiCode"));
        largeMailUserIdentifier.setContent("lmu identifier");
        
        final BuildingName buildingName = xalFactory.createBuildingName();
        buildingName.setType("BNType");
        buildingName.setTypeOccurrence(AfterBeforeEnum.BEFORE);
        buildingName.setGrPostal(xalFactory.createGrPostal("BNCode"));
        buildingName.setContent("Building name content");

        final Department department = xalFactory.createDepartment();
        department.setType("largeMailUserDepartment");

        final PostBoxNumber postBoxNumber = xalFactory.createPostBoxNumber(xalFactory.createGrPostal("lmu"), "post box number");
        final PostBox postBox = xalFactory.createPostBox();
        postBox.setPostBoxNumber(postBoxNumber);
        postBox.setType("largeMailUserPostBox");

        final Thoroughfare largeMailUserThoroughfare = xalFactory.createThoroughfare();
        largeMailUserThoroughfare.setType("largeMailUserThoroughfare");

        final PostalCode largeMailUserPostalCode = xalFactory.createPostalCode();
        largeMailUserPostalCode.setType("largeMailUserPostalCode");

        final LargeMailUser largeMailUser = xalFactory.createLargeMailUser();
        largeMailUser.setType("largeMailUserType");
        largeMailUser.setAddressLines(Arrays.asList(largeMailUserAddresLine));
        largeMailUser.setLargeMailUserNames(Arrays.asList(largeMailUserName));
        largeMailUser.setLargeMailUserIdentifier(largeMailUserIdentifier);
        largeMailUser.setBuildingNames(Arrays.asList(buildingName));
        largeMailUser.setDepartment(department);
        largeMailUser.setPostBox(postBox);
        largeMailUser.setThoroughfare(largeMailUserThoroughfare);
        largeMailUser.setPostalCode(largeMailUserPostalCode);

        final Thoroughfare localityThoroughfare = xalFactory.createThoroughfare();
        localityThoroughfare.setType("localityThoroughfare");

        final Premise localityPremise = xalFactory.createPremise();
        localityPremise.setType("localityPremise");

        final PostalRouteNumber dlPostalRouteNumber = xalFactory.createPostalRouteNumber(xalFactory.createGrPostal("prn"), "postal route number");
        final PostalRoute dlPostalRoute = xalFactory.createPostalRoute();
        dlPostalRoute.setPostalRouteNumber(dlPostalRouteNumber);
        dlPostalRoute.setType("subsubsubDL-postalRoute");

        final DependentLocality subSubSubDl = xalFactory.createDependentLocality();
        subSubSubDl.setType("subsubsubDL");
        subSubSubDl.setPostalRoute(dlPostalRoute);

        final PostOffice dlPostOffice = xalFactory.createPostOffice();
        dlPostOffice.setType("subsubDL-postOffice");

        final DependentLocality subSubDl = xalFactory.createDependentLocality();
        subSubDl.setType("subsubDL");
        subSubDl.setPostOffice(dlPostOffice);
        subSubDl.setDependentLocality(subSubSubDl);

        final LargeMailUser dlLargeMailUser = xalFactory.createLargeMailUser();
        dlLargeMailUser.setType("subDL-largeMailUser");

        final DependentLocality subDl = xalFactory.createDependentLocality();
        subDl.setType("subDL");
        subDl.setLargeMailUser(dlLargeMailUser);
        subDl.setDependentLocality(subSubDl);


        final PostalCode dlPostalCode = xalFactory.createPostalCode();
        dlPostalCode.setType("dlPostalCode");


        final GenericTypedGrPostal dlAddressLine = xalFactory.createGenericTypedGrPostal(null, null, "dependent locality address");
        final GenericTypedGrPostal dlName = xalFactory.createGenericTypedGrPostal("dlnType", xalFactory.createGrPostal("dlnCode"), "dependent locality name");
        final DependentLocalityNumber dlNumber = xalFactory.createDependentLocalityNumber(AfterBeforeEnum.AFTER, xalFactory.createGrPostal("code"), "dependent locality number");
        final PostBox postBox1 = xalFactory.createPostBox();
        postBox1.setType("dlPostBox");
        postBox1.setPostBoxNumber(xalFactory.createPostBoxNumber(xalFactory.createGrPostal("dl"), "postBoxNumber"));

        final Thoroughfare dlThoroughfare = xalFactory.createThoroughfare();
        dlThoroughfare.setType("dlThoroughfare");

        final Premise dlPremise = xalFactory.createPremise();
        dlPremise.setType("DLPremise");

        final DependentLocality dependentLocality = xalFactory.createDependentLocality();
        dependentLocality.setType("dlType");
        dependentLocality.setUsageType("dlUsageType");
        dependentLocality.setIndicator("dlIndicator");
        dependentLocality.setConnector("dlConnector");
        dependentLocality.setAddressLines(Arrays.asList(dlAddressLine));
        dependentLocality.setDependentLocalityNames(Arrays.asList(dlName));
        dependentLocality.setDependentLocalityNumber(dlNumber);
        dependentLocality.setPostBox(postBox1);
        dependentLocality.setThoroughfare(dlThoroughfare);
        dependentLocality.setPremise(dlPremise);
        dependentLocality.setPostalCode(dlPostalCode);
        dependentLocality.setDependentLocality(subDl);


        final PostalCode postalCode = xalFactory.createPostalCode();
        postalCode.setType("localityPostalCode");

        final Locality locality = xalFactory.createLocality();
        locality.setType("localityType");
        locality.setUsageType("localityUsageType");
        locality.setIndicator("localityIndicator");
        locality.setPostalCode(postalCode);
        locality.setDependentLocality(dependentLocality);
        locality.setPremise(localityPremise);
        locality.setThoroughfare(localityThoroughfare);
        locality.setLargeMailUser(largeMailUser);
        locality.setLocalityNames(Arrays.asList(localityName));
        locality.setAddressLines(Arrays.asList(localityAddressLine1, localityAddressLine2));

        final AddressDetails addressDetails = xalFactory.createAddressDetails();
            addressDetails.setLocality(locality);

        final Xal xal = xalFactory.createXal(Arrays.asList(addressDetails), null);

        final File temp = File.createTempFile("locality",".xml");
        //temp.deleteOnExit();

        final XalWriter writer = new XalWriter();
        writer.setOutput(temp);
        writer.write(xal);
        writer.dispose();

        DomCompare.compare(
                 new File(pathToTestFile), temp);

    }

}