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
import java.util.List;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.stream.XMLStreamException;
import org.geotoolkit.xal.model.AddressDetails;
import org.geotoolkit.xal.model.AdministrativeArea;
import org.geotoolkit.xal.model.AfterBeforeEnum;
import org.geotoolkit.xal.model.Country;
import org.geotoolkit.xal.model.CountryNameCode;
import org.geotoolkit.xal.model.Department;
import org.geotoolkit.xal.model.Firm;
import org.geotoolkit.xal.model.GenericTypedGrPostal;
import org.geotoolkit.xal.model.MailStop;
import org.geotoolkit.xal.model.MailStopNumber;
import org.geotoolkit.xal.model.PostBox;
import org.geotoolkit.xal.model.PostBoxNumber;
import org.geotoolkit.xal.model.PostBoxNumberExtension;
import org.geotoolkit.xal.model.PostBoxNumberPrefix;
import org.geotoolkit.xal.model.PostBoxNumberSuffix;
import org.geotoolkit.xal.model.PostOffice;
import org.geotoolkit.xal.model.PostOfficeNumber;
import org.geotoolkit.xal.model.PostTown;
import org.geotoolkit.xal.model.PostTownSuffix;
import org.geotoolkit.xal.model.PostalCode;
import org.geotoolkit.xal.model.PostalCodeNumberExtension;
import org.geotoolkit.xal.model.PostalRoute;
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
public class CountryTest_AA {

    private static final double DELTA = 0.000000000001;
    private static final String pathToTestFile = "src/test/resources/org/geotoolkit/xal/country_AA.xml";

    public CountryTest_AA() {
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
    public void country_AAReadTest() throws IOException, XMLStreamException {

        final XalReader reader = new XalReader();
        reader.setInput(new File(pathToTestFile));
        final Xal xalObjects = reader.read();
        reader.dispose();

        assertEquals(1, xalObjects.getAddressDetails().size());
        final AddressDetails addressDetails0 = xalObjects.getAddressDetails().get(0);

        final Country country = addressDetails0.getCountry();

        final List<GenericTypedGrPostal> addressLines = country.getAddressLines();
        assertEquals(2, addressLines.size());

        final GenericTypedGrPostal addressLine0 = addressLines.get(0);
        assertEquals("code1", addressLine0.getGrPostal().getCode());
        assertEquals("type1", addressLine0.getType());
        assertEquals("Première ligne", addressLine0.getContent());

        final GenericTypedGrPostal addressLine1 = addressLines.get(1);
        assertEquals("code2", addressLine1.getGrPostal().getCode());
        assertEquals("type2", addressLine1.getType());
        assertEquals("Seconde ligne", addressLine1.getContent());

        assertEquals(2,country.getCountryNameCodes().size());
        
        final CountryNameCode countryNameCode0 = country.getCountryNameCodes().get(0);
        assertEquals("scheme1", countryNameCode0.getScheme());
        assertEquals("code1", countryNameCode0.getGrPostal().getCode());
        assertEquals("countryNameCode1", countryNameCode0.getContent());

        final CountryNameCode countryNameCode1 = country.getCountryNameCodes().get(1);
        assertEquals("scheme2", countryNameCode1.getScheme());
        assertEquals("code2", countryNameCode1.getGrPostal().getCode());
        assertEquals("countryNameCode2", countryNameCode1.getContent());

        assertEquals(1, country.getCountryNames().size());
        final GenericTypedGrPostal countryName0 = country.getCountryNames().get(0);
        assertEquals("typeCN", countryName0.getType());
        assertEquals("codeCN", countryName0.getGrPostal().getCode());
        assertEquals("Country Name", countryName0.getContent());

        assertTrue(country.getAdministrativeArea() != null);
        final AdministrativeArea admininstrativeArea0 = country.getAdministrativeArea();

        assertEquals("typeAA", admininstrativeArea0.getType());
        assertEquals("usageTypeAA", admininstrativeArea0.getUsageType());
        assertEquals("indicatorAA", admininstrativeArea0.getIndicator());
        assertEquals(1, admininstrativeArea0.getAddressLines().size());
        assertEquals("Troisième ligne", admininstrativeArea0.getAddressLines().get(0).getContent());

        assertEquals(1, admininstrativeArea0.getAdministrativeAreaNames().size());
        final GenericTypedGrPostal administrativeAreaName0 = admininstrativeArea0.getAdministrativeAreaNames().get(0);
        assertEquals("typeAAN",administrativeAreaName0.getType());
        assertEquals("codeAAN", administrativeAreaName0.getGrPostal().getCode());
        assertEquals("Administrative Area Name", administrativeAreaName0.getContent());

        final SubAdministrativeArea subAdministrativeArea = admininstrativeArea0.getSubAdministrativeArea();

        assertEquals("typeSAA", subAdministrativeArea.getType());
        assertEquals("usageTypeSAA", subAdministrativeArea.getUsageType());
        assertEquals("indicatorSAA", subAdministrativeArea.getIndicator());
        assertEquals(1, subAdministrativeArea.getAddressLines().size());
        assertEquals("Quatrième Ligne", subAdministrativeArea.getAddressLines().get(0).getContent());

        assertEquals(1, subAdministrativeArea.getSubAdministrativeAreaNames().size());
        final GenericTypedGrPostal subAdministrativeAreaName = subAdministrativeArea.getSubAdministrativeAreaNames().get(0);
        assertEquals("typeSAAN",subAdministrativeAreaName.getType());
        assertEquals("codeSAAN", subAdministrativeAreaName.getGrPostal().getCode());
        assertEquals("Sub Administrative Area Name", subAdministrativeAreaName.getContent());

        final PostOffice subAdministrativePostOffice = subAdministrativeArea.getPostOffice();
        assertEquals(1, subAdministrativePostOffice.getAddressLines().size());
        assertEquals("PostOfficeAddressLine", subAdministrativePostOffice.getAddressLines().get(0).getContent());
        assertEquals(2, subAdministrativePostOffice.getPostOfficeNames().size());

        final GenericTypedGrPostal subAdminPostOfficeName0 = subAdministrativePostOffice.getPostOfficeNames().get(0);
        assertEquals("postOfficeNameType", subAdminPostOfficeName0.getType());
        assertEquals("postOfficeNameCode", subAdminPostOfficeName0.getGrPostal().getCode());
        assertEquals("post office name", subAdminPostOfficeName0.getContent());

        final GenericTypedGrPostal subAdminPostOfficeName1 = subAdministrativePostOffice.getPostOfficeNames().get(1);
        assertEquals("post office name 2", subAdminPostOfficeName1.getContent());

        final PostalRoute subAdminPostOfficePostalRoute = subAdministrativePostOffice.getPostalRoute();
        assertEquals(1, subAdminPostOfficePostalRoute.getAddressLines().size());
        assertEquals("postal route address line", subAdminPostOfficePostalRoute.getAddressLines().get(0).getContent());
        assertEquals(1, subAdminPostOfficePostalRoute.getPostalRouteNames().size());
        final GenericTypedGrPostal subAdminPostOfficePostalRouteName0 = subAdminPostOfficePostalRoute.getPostalRouteNames().get(0);
        assertEquals("postal route name", subAdminPostOfficePostalRouteName0.getContent());
        assertEquals("postalRouteNameType", subAdminPostOfficePostalRouteName0.getType());
        assertEquals("postalRouteNameCode", subAdminPostOfficePostalRouteName0.getGrPostal().getCode());

        final PostBox subAdminPostOfficePostalRoutePostBox = subAdminPostOfficePostalRoute.getPostBox();
        assertEquals("postal route post box number", subAdminPostOfficePostalRoutePostBox.getPostBoxNumber().getContent());

        final PostBox subAdminPostOfficePostBox = subAdministrativePostOffice.getPostBox();
        assertEquals("postBoxType", subAdminPostOfficePostBox.getType());
        assertEquals("postBoxIndicator", subAdminPostOfficePostBox.getIndicator());
        assertEquals(1, subAdminPostOfficePostBox.getAddressLines().size());
        assertEquals("post box address line", subAdminPostOfficePostBox.getAddressLines().get(0).getContent());

        final PostBoxNumber subAdminPostOfficePostBoxNumber = subAdminPostOfficePostBox.getPostBoxNumber();
        assertEquals("postBoxNumberCode", subAdminPostOfficePostBoxNumber.getGrPostal().getCode());
        assertEquals("post box number", subAdminPostOfficePostBoxNumber.getContent());

        final PostBoxNumberPrefix subAdminPostOfficePostBoxNumberPrefix = subAdminPostOfficePostBox.getPostBoxNumberPrefix();
        assertEquals("prefixSeparator", subAdminPostOfficePostBoxNumberPrefix.getNumberPrefixSeparator());
        assertEquals("prefixCode", subAdminPostOfficePostBoxNumberPrefix.getGrPostal().getCode());
        assertEquals("post box number prefix", subAdminPostOfficePostBoxNumberPrefix.getContent());

        final PostBoxNumberSuffix subAdminPostOfficePostBoxNumberSuffix = subAdminPostOfficePostBox.getPostBoxNumberSuffix();
        assertEquals("suffixSeparator", subAdminPostOfficePostBoxNumberSuffix.getNumberSuffixSeparator());
        assertEquals("suffixCode", subAdminPostOfficePostBoxNumberSuffix.getGrPostal().getCode());
        assertEquals("post box number suffix", subAdminPostOfficePostBoxNumberSuffix.getContent());

        final PostBoxNumberExtension subAdminPostOfficePostBoxNumberExtension = subAdminPostOfficePostBox.getPostBoxNumberExtension();
        assertEquals("extensionSeparator", subAdminPostOfficePostBoxNumberExtension.getNumberExtensionSeparator());
        assertEquals("post box number extension", subAdminPostOfficePostBoxNumberExtension.getContent());

        final Firm subAdminPostOfficePostBoxFirm = subAdminPostOfficePostBox.getFirm();
        assertEquals("firmType", subAdminPostOfficePostBoxFirm.getType());
        assertEquals(1, subAdminPostOfficePostBoxFirm.getAddressLines().size());
        assertEquals("firm address line", subAdminPostOfficePostBoxFirm.getAddressLines().get(0).getContent());

        assertEquals(1, subAdminPostOfficePostBoxFirm.getFirmNames().size());
        final GenericTypedGrPostal subAdminPostOfficePostBoxFirmName = subAdminPostOfficePostBoxFirm.getFirmNames().get(0);
        assertEquals("firmNameType", subAdminPostOfficePostBoxFirmName.getType());
        assertEquals("firmNameCode", subAdminPostOfficePostBoxFirmName.getGrPostal().getCode());
        assertEquals("firm name", subAdminPostOfficePostBoxFirmName.getContent());

        assertEquals(1, subAdminPostOfficePostBoxFirm.getDepartments().size());
        final Department subAdminPostOfficePostBoxFirmDepartment = subAdminPostOfficePostBoxFirm.getDepartments().get(0);
        assertEquals("departmentType", subAdminPostOfficePostBoxFirmDepartment.getType());

        assertEquals(1, subAdminPostOfficePostBoxFirmDepartment.getAddressLines().size());
        assertEquals("department address line", subAdminPostOfficePostBoxFirmDepartment.getAddressLines().get(0).getContent());

        assertEquals(1, subAdminPostOfficePostBoxFirmDepartment.getDepartmentNames().size());
        final GenericTypedGrPostal subAdminPostOfficePostBoxFirmDepartmentName = subAdminPostOfficePostBoxFirmDepartment.getDepartmentNames().get(0);
        assertEquals("departmentNameType", subAdminPostOfficePostBoxFirmDepartmentName.getType());
        assertEquals("departmentNameCode", subAdminPostOfficePostBoxFirmDepartmentName.getGrPostal().getCode());
        assertEquals("department name", subAdminPostOfficePostBoxFirmDepartmentName.getContent());

        final MailStop subAdminPostOfficePostBoxFirmDepartmentMailStop = subAdminPostOfficePostBoxFirmDepartment.getMailStop();
        assertEquals("mailStopType", subAdminPostOfficePostBoxFirmDepartmentMailStop.getType());
        assertEquals(1, subAdminPostOfficePostBoxFirmDepartmentMailStop.getAddressLines().size());
        assertEquals("mail stop address line", subAdminPostOfficePostBoxFirmDepartmentMailStop.getAddressLines().get(0).getContent());

        assertEquals(1, subAdminPostOfficePostBoxFirmDepartmentMailStop.getMailStopNames().size());
        final GenericTypedGrPostal subAdminPostOfficePostBoxFirmDepartmentMailStopName = subAdminPostOfficePostBoxFirmDepartmentMailStop.getMailStopNames().get(0);
        assertEquals("mailStopNameType", subAdminPostOfficePostBoxFirmDepartmentMailStopName.getType());
        assertEquals("mailStopNameCode", subAdminPostOfficePostBoxFirmDepartmentMailStopName.getGrPostal().getCode());
        assertEquals("mail stop name", subAdminPostOfficePostBoxFirmDepartmentMailStopName.getContent());


        final MailStopNumber subAdminPostOfficePostBoxFirmDepartmentMailStopNumber = subAdminPostOfficePostBoxFirmDepartmentMailStop.getMailStopNumber();
        assertEquals("numberSeparator", subAdminPostOfficePostBoxFirmDepartmentMailStopNumber.getNameNumberSeparator());
        assertEquals("mailStopNumberCode", subAdminPostOfficePostBoxFirmDepartmentMailStopNumber.getGrPostal().getCode());
        assertEquals("mail stop number", subAdminPostOfficePostBoxFirmDepartmentMailStopNumber.getContent());

        
        final PostalCode subAdminPostOfficePostBoxFirmDepartmentPostalCode = subAdminPostOfficePostBoxFirmDepartment.getPostalCode();
        assertEquals("postalCodeType", subAdminPostOfficePostBoxFirmDepartmentPostalCode.getType());

        assertEquals(1, subAdminPostOfficePostBoxFirmDepartmentPostalCode.getAddressLines().size());
        assertEquals("postal code address line", subAdminPostOfficePostBoxFirmDepartmentPostalCode.getAddressLines().get(0).getContent());

        assertEquals(1, subAdminPostOfficePostBoxFirmDepartmentPostalCode.getPostalCodeNumbers().size());
        final GenericTypedGrPostal subAdminPostOfficePostBoxFirmDepartmentPostalCodeNumber = subAdminPostOfficePostBoxFirmDepartmentPostalCode.getPostalCodeNumbers().get(0);
        assertEquals("postalCodeNumberType", subAdminPostOfficePostBoxFirmDepartmentPostalCodeNumber.getType());
        assertEquals("postalCodeNumberCode", subAdminPostOfficePostBoxFirmDepartmentPostalCodeNumber.getGrPostal().getCode());
        assertEquals("postal code number", subAdminPostOfficePostBoxFirmDepartmentPostalCodeNumber.getContent());


        assertEquals(1, subAdminPostOfficePostBoxFirmDepartmentPostalCode.getPostalCodeNumberExtensions().size());
        final PostalCodeNumberExtension subAdminPostOfficePostBoxFirmDepartmentPostalCodeNumberExtension = subAdminPostOfficePostBoxFirmDepartmentPostalCode.getPostalCodeNumberExtensions().get(0);
        assertEquals("extensionType", subAdminPostOfficePostBoxFirmDepartmentPostalCodeNumberExtension.getType());
        assertEquals("separator", subAdminPostOfficePostBoxFirmDepartmentPostalCodeNumberExtension.getNumberExtensionSeparator());
        assertEquals("extensionCode", subAdminPostOfficePostBoxFirmDepartmentPostalCodeNumberExtension.getGrPostal().getCode());
        assertEquals("postal code number extension", subAdminPostOfficePostBoxFirmDepartmentPostalCodeNumberExtension.getContent());


        final PostTown subAdminPostOfficePostBoxFirmDepartmentPostalCodePostTown = subAdminPostOfficePostBoxFirmDepartmentPostalCode.getPostTown();
        assertEquals("postTownType", subAdminPostOfficePostBoxFirmDepartmentPostalCodePostTown.getType());

        assertEquals(1, subAdminPostOfficePostBoxFirmDepartmentPostalCodePostTown.getAddressLines().size());
        assertEquals("post town address line", subAdminPostOfficePostBoxFirmDepartmentPostalCodePostTown.getAddressLines().get(0).getContent());

        assertEquals(1, subAdminPostOfficePostBoxFirmDepartmentPostalCodePostTown.getPostTownNames().size());
        final GenericTypedGrPostal subAdminPostOfficePostBoxFirmDepartmentPostalCodePostTownName = subAdminPostOfficePostBoxFirmDepartmentPostalCodePostTown.getPostTownNames().get(0);
        assertEquals("nameType", subAdminPostOfficePostBoxFirmDepartmentPostalCodePostTownName.getType());
        assertEquals("nameCode", subAdminPostOfficePostBoxFirmDepartmentPostalCodePostTownName.getGrPostal().getCode());
        assertEquals("name", subAdminPostOfficePostBoxFirmDepartmentPostalCodePostTownName.getContent());

        final PostTownSuffix subAdminPostOfficePostBoxFirmDepartmentPostalCodePostTownSuffix = subAdminPostOfficePostBoxFirmDepartmentPostalCodePostTown.getPostTownSuffix();
        assertEquals("suffixCode", subAdminPostOfficePostBoxFirmDepartmentPostalCodePostTownSuffix.getGrPostal().getCode());
        assertEquals("suffix", subAdminPostOfficePostBoxFirmDepartmentPostalCodePostTownSuffix.getContent());

        final MailStop subAdminPostOfficePostBoxFirmMailStop = subAdminPostOfficePostBoxFirm.getMailStop();
        assertEquals(2, subAdminPostOfficePostBoxFirmMailStop.getAddressLines().size());
        assertEquals("mail stop 1", subAdminPostOfficePostBoxFirmMailStop.getAddressLines().get(0).getContent());
        assertEquals("mail stop 2", subAdminPostOfficePostBoxFirmMailStop.getAddressLines().get(1).getContent());

        final PostalCode subAdminPostOfficePostBoxFirmPostalCode = subAdminPostOfficePostBoxFirm.getPostalCode();
        assertEquals(2, subAdminPostOfficePostBoxFirmPostalCode.getAddressLines().size());
        assertEquals("postal code line 1", subAdminPostOfficePostBoxFirmPostalCode.getAddressLines().get(0).getContent());
        assertEquals("postal code line 2", subAdminPostOfficePostBoxFirmPostalCode.getAddressLines().get(1).getContent());


        final PostalCode subAdminPostOfficePostalCode = subAdministrativePostOffice.getPostalCode();
        assertEquals(2, subAdminPostOfficePostalCode.getPostalCodeNumbers().size());
        assertEquals("postalCodeNumber1", subAdminPostOfficePostalCode.getPostalCodeNumbers().get(0).getContent());
        assertEquals("postalCodeNumber2", subAdminPostOfficePostalCode.getPostalCodeNumbers().get(1).getContent());

        assertEquals(2, subAdminPostOfficePostalCode.getPostalCodeNumberExtensions().size());
        assertEquals("extension1", subAdminPostOfficePostalCode.getPostalCodeNumberExtensions().get(0).getContent());
        assertEquals("extension2", subAdminPostOfficePostalCode.getPostalCodeNumberExtensions().get(1).getContent());

        final PostOffice adminAreaPostOffice = admininstrativeArea0.getPostOffice();
        final PostOfficeNumber adminAreaPostOfficeNumber = adminAreaPostOffice.getPostOfficeNumber();
        assertEquals("indicator", adminAreaPostOfficeNumber.getIndicator());
        assertEquals(AfterBeforeEnum.BEFORE, adminAreaPostOfficeNumber.getIndicatorOccurrence());
        assertEquals("code", adminAreaPostOfficeNumber.getGrPostal().getCode());
        assertEquals("post office number", adminAreaPostOfficeNumber.getContent());

    }

    @Test
    public void country_AAWriteTest() throws IOException, XMLStreamException, ParserConfigurationException, SAXException{
        final XalFactory xalFactory = DefaultXalFactory.getInstance();

        final GenericTypedGrPostal departmentMailStopAddressLine0 = xalFactory.createGenericTypedGrPostal(null, null, "mail stop address line");
        final GenericTypedGrPostal departmentMailStopName0 = xalFactory.createGenericTypedGrPostal();
        departmentMailStopName0.setType("mailStopNameType");
        departmentMailStopName0.setGrPostal(xalFactory.createGrPostal("mailStopNameCode"));
        departmentMailStopName0.setContent("mail stop name");

        final MailStopNumber departmentMailStopNumber = xalFactory.createMailStopNumber(
                "numberSeparator", xalFactory.createGrPostal("mailStopNumberCode"), "mail stop number");
        final MailStop departmentMailStop = xalFactory.createMailStop(
                Arrays.asList(departmentMailStopAddressLine0),
                Arrays.asList(departmentMailStopName0),
                departmentMailStopNumber, "mailStopType");

        final GenericTypedGrPostal departmentPostalCodeAddressLine0 = xalFactory.createGenericTypedGrPostal();
        departmentPostalCodeAddressLine0.setContent("postal code address line");

        final GenericTypedGrPostal departmentPostalCodeNumber = xalFactory.createGenericTypedGrPostal(
                "postalCodeNumberType", xalFactory.createGrPostal("postalCodeNumberCode"), "postal code number");

        final PostalCodeNumberExtension departmentPostalCodeNumberExtension = xalFactory.createPostalCodeNumberExtension();
        departmentPostalCodeNumberExtension.setType("extensionType");
        departmentPostalCodeNumberExtension.setGrPostal(xalFactory.createGrPostal("extensionCode"));
        departmentPostalCodeNumberExtension.setNumberExtensionSeparator("separator");
        departmentPostalCodeNumberExtension.setContent("postal code number extension");


        final GenericTypedGrPostal departmentPostalCodePostTownAddressLine0 = xalFactory.createGenericTypedGrPostal();
        departmentPostalCodePostTownAddressLine0.setContent("post town address line");

        final GenericTypedGrPostal departmentPostalCodePostTownName0 = xalFactory.createGenericTypedGrPostal();
        departmentPostalCodePostTownName0.setContent("name");
        departmentPostalCodePostTownName0.setType("nameType");
        departmentPostalCodePostTownName0.setGrPostal(xalFactory.createGrPostal("nameCode"));

        final PostTown departmentPostalCodePostTown = xalFactory.createPostTown();
        departmentPostalCodePostTown.setType("postTownType");
        departmentPostalCodePostTown.setAddressLines(Arrays.asList(departmentPostalCodePostTownAddressLine0));
        departmentPostalCodePostTown.setPostTownNames(Arrays.asList(departmentPostalCodePostTownName0));

        final PostTownSuffix departmentPostalCodePostTownSuffix = xalFactory.createPostTownSuffix(xalFactory.createGrPostal("suffixCode"), "suffix");
        departmentPostalCodePostTown.setPostTownSuffix(departmentPostalCodePostTownSuffix);

        final PostalCode departmentPostalCode = xalFactory.createPostalCode(
                Arrays.asList(departmentPostalCodeAddressLine0),
                Arrays.asList(departmentPostalCodeNumber),
                Arrays.asList(departmentPostalCodeNumberExtension),
                departmentPostalCodePostTown, "postalCodeType");

        final Department department = xalFactory.createDepartment();
        department.setAddressLines(Arrays.asList(xalFactory.createGenericTypedGrPostal(null, null, "department address line")));
        department.setDepartmentNames(Arrays.asList(xalFactory.createGenericTypedGrPostal(
                "departmentNameType", xalFactory.createGrPostal("departmentNameCode"), "department name")));
        department.setType("departmentType");
        department.setMailStop(departmentMailStop);
        department.setPostalCode(departmentPostalCode);

        final GenericTypedGrPostal firmMailStopAddressLine0 = xalFactory.createGenericTypedGrPostal();
        firmMailStopAddressLine0.setContent("mail stop 1");
        final GenericTypedGrPostal firmMailStopAddressLine1 = xalFactory.createGenericTypedGrPostal();
        firmMailStopAddressLine1.setContent("mail stop 2");

        final MailStop firmMailStop = xalFactory.createMailStop();
        firmMailStop.setAddressLines(Arrays.asList(firmMailStopAddressLine0, firmMailStopAddressLine1));

        final GenericTypedGrPostal firmPostalCodeAddressLine0 = xalFactory.createGenericTypedGrPostal();
        firmPostalCodeAddressLine0.setContent("postal code line 1");
        final GenericTypedGrPostal firmPostalCodeAddressLine1 = xalFactory.createGenericTypedGrPostal();
        firmPostalCodeAddressLine1.setContent("postal code line 2");
        final PostalCode firmPostalCode = xalFactory.createPostalCode();
        firmPostalCode.setAddressLines(Arrays.asList(firmPostalCodeAddressLine0, firmPostalCodeAddressLine1));


        final GenericTypedGrPostal firmName0 = xalFactory.createGenericTypedGrPostal(
                "firmNameType", xalFactory.createGrPostal("firmNameCode"), "firm name");
        final GenericTypedGrPostal firmAddressLine0 = xalFactory.createGenericTypedGrPostal();
            firmAddressLine0.setContent("firm address line");

        final Firm firm = xalFactory.createFirm();
        firm.setType("firmType");
        firm.setAddressLines(Arrays.asList(firmAddressLine0));
        firm.setFirmNames(Arrays.asList(firmName0));
        firm.setDepartments(Arrays.asList(department));
        firm.setMailStop(firmMailStop);
        firm.setPostalCode(firmPostalCode);


        final GenericTypedGrPostal postOfficePostBoxAddressLine0 = xalFactory.createGenericTypedGrPostal();
            postOfficePostBoxAddressLine0.setContent("post box address line");

        final PostBoxNumber postOfficePostBoxNumber = xalFactory.createPostBoxNumber(
                xalFactory.createGrPostal("postBoxNumberCode"), "post box number");

        final PostBoxNumberPrefix postOfficePostBoxNumberPrefix = xalFactory.createPostBoxNumberPrefix();
            postOfficePostBoxNumberPrefix.setNumberPrefixSeparator("prefixSeparator");
            postOfficePostBoxNumberPrefix.setGrPostal(xalFactory.createGrPostal("prefixCode"));
            postOfficePostBoxNumberPrefix.setContent("post box number prefix");
        final PostBoxNumberSuffix postOfficePostBoxNumberSuffix = xalFactory.createPostBoxNumberSuffix();
            postOfficePostBoxNumberSuffix.setNumberSuffixSeparator("suffixSeparator");
            postOfficePostBoxNumberSuffix.setGrPostal(xalFactory.createGrPostal("suffixCode"));
            postOfficePostBoxNumberSuffix.setContent("post box number suffix");
        final PostBoxNumberExtension postOfficePostBoxNumberExtension = xalFactory.createPostBoxNumberExtension("extensionSeparator", "post box number extension");

        final PostBox postOfficePostBox = xalFactory.createPostBox();
            postOfficePostBox.setType("postBoxType");
            postOfficePostBox.setIndicator("postBoxIndicator");postOfficePostBox.setAddressLines(Arrays.asList(postOfficePostBoxAddressLine0));
            postOfficePostBox.setPostBoxNumber(postOfficePostBoxNumber);
            postOfficePostBox.setPostBoxNumberPrefix(postOfficePostBoxNumberPrefix);
            postOfficePostBox.setPostBoxNumberSuffix(postOfficePostBoxNumberSuffix);
            postOfficePostBox.setPostBoxNumberExtension(postOfficePostBoxNumberExtension);
            postOfficePostBox.setFirm(firm);

        final PostalCodeNumberExtension postOfficePostalCodeNumberExtension1 = xalFactory.createPostalCodeNumberExtension();
            postOfficePostalCodeNumberExtension1.setContent("extension1");
        final PostalCodeNumberExtension postOfficePostalCodeNumberExtension2 = xalFactory.createPostalCodeNumberExtension();
         postOfficePostalCodeNumberExtension2.setContent("extension2");
        final GenericTypedGrPostal postOfficePostalCodeNumber1 = xalFactory.createGenericTypedGrPostal();
            postOfficePostalCodeNumber1.setContent("postalCodeNumber1");
        final GenericTypedGrPostal postOfficePostalCodeNumber2 = xalFactory.createGenericTypedGrPostal();
            postOfficePostalCodeNumber2.setContent("postalCodeNumber2");
        final PostalCode postOfficePostalCode = xalFactory.createPostalCode();
            postOfficePostalCode.setPostalCodeNumbers(Arrays.asList(postOfficePostalCodeNumber1, postOfficePostalCodeNumber2));
            postOfficePostalCode.setPostalCodeNumberExtensions(Arrays.asList(postOfficePostalCodeNumberExtension1, postOfficePostalCodeNumberExtension2));

        final GenericTypedGrPostal postalRouteAddressLine0 = xalFactory.createGenericTypedGrPostal();
            postalRouteAddressLine0.setContent("postal route address line");
        final GenericTypedGrPostal postalRouteName0 = xalFactory.createGenericTypedGrPostal(
                "postalRouteNameType", xalFactory.createGrPostal("postalRouteNameCode"), "postal route name");
        final PostBoxNumber postOfficePostalRoutePostBoxNumber = xalFactory.createPostBoxNumber(null, "postal route post box number");
        final PostBox postOfficePostalRoutePostBox = xalFactory.createPostBox();
            postOfficePostalRoutePostBox.setPostBoxNumber(postOfficePostalRoutePostBoxNumber);

        final PostalRoute postOfficePostalRoute = xalFactory.createPostalRoute();
        postOfficePostalRoute.setAddressLines(Arrays.asList(postalRouteAddressLine0));
        postOfficePostalRoute.setPostalRouteNames(Arrays.asList(postalRouteName0));
        postOfficePostalRoute.setPostBox(postOfficePostalRoutePostBox);

        final GenericTypedGrPostal postOfficeAddressLine0 = xalFactory.createGenericTypedGrPostal();
            postOfficeAddressLine0.setContent("PostOfficeAddressLine");
        final GenericTypedGrPostal postOfficeName0 = xalFactory.createGenericTypedGrPostal(
                "postOfficeNameType", xalFactory.createGrPostal("postOfficeNameCode"), "post office name");
        final GenericTypedGrPostal postOfficeName1 = xalFactory.createGenericTypedGrPostal();
            postOfficeName1.setContent("post office name 2");
        final PostOffice subAdminPostOffice = xalFactory.createPostOffice();
            subAdminPostOffice.setAddressLines(Arrays.asList(postOfficeAddressLine0));
            subAdminPostOffice.setPostOfficeNames(Arrays.asList(postOfficeName0, postOfficeName1));
            subAdminPostOffice.setPostalRoute(postOfficePostalRoute);
            subAdminPostOffice.setPostBox(postOfficePostBox);
            subAdminPostOffice.setPostalCode(postOfficePostalCode);

        final GenericTypedGrPostal subAdministrativeAreaAddressLine0 = xalFactory.createGenericTypedGrPostal();
        subAdministrativeAreaAddressLine0.setContent("Quatrième Ligne");
        final GenericTypedGrPostal subAdministrativeAreaName0 = xalFactory.createGenericTypedGrPostal(
                "typeSAAN", xalFactory.createGrPostal("codeSAAN"), "Sub Administrative Area Name");

        final SubAdministrativeArea subAdministrativeArea = xalFactory.createSubAdministrativeArea();
        subAdministrativeArea.setAddressLines(Arrays.asList(subAdministrativeAreaAddressLine0));
        subAdministrativeArea.setSubAdministrativeAreaNames(Arrays.asList(subAdministrativeAreaName0));
        subAdministrativeArea.setPostOffice(subAdminPostOffice);
        subAdministrativeArea.setIndicator("indicatorSAA");
        subAdministrativeArea.setUsageType("usageTypeSAA");
        subAdministrativeArea.setType("typeSAA");

        final PostOfficeNumber administrativeAreaPostOfficeNumber = xalFactory.createPostOfficeNumber(
                "indicator", AfterBeforeEnum.BEFORE, xalFactory.createGrPostal("code"), "post office number");
        final PostOffice administrativeAreaPostOffice = xalFactory.createPostOffice();
            administrativeAreaPostOffice.setPostOfficeNumber(administrativeAreaPostOfficeNumber);

        final GenericTypedGrPostal administrativeAreaName = xalFactory.createGenericTypedGrPostal();
            administrativeAreaName.setContent("Administrative Area Name");
            administrativeAreaName.setGrPostal(xalFactory.createGrPostal("codeAAN"));
            administrativeAreaName.setType("typeAAN");

        final GenericTypedGrPostal addressLine3 = xalFactory.createGenericTypedGrPostal();
            addressLine3.setContent("Troisième ligne");

        final AdministrativeArea administrativeArea = xalFactory.createAdministrativeArea();
            administrativeArea.setAddressLines(Arrays.asList(addressLine3));
            administrativeArea.setAdministrativeAreaNames(Arrays.asList(administrativeAreaName));
            administrativeArea.setIndicator("indicatorAA");
            administrativeArea.setUsageType("usageTypeAA");
            administrativeArea.setType("typeAA");
            administrativeArea.setPostOffice(administrativeAreaPostOffice);
            administrativeArea.setSubAdministrativeArea(subAdministrativeArea);

        final CountryNameCode countryNameCode1 = xalFactory.createCountryNameCode(
                "scheme1", xalFactory.createGrPostal("code1"), "countryNameCode1");

        final CountryNameCode countryNameCode2 = xalFactory.createCountryNameCode(
                "scheme2", xalFactory.createGrPostal("code2"), "countryNameCode2");

        final GenericTypedGrPostal countryName0 = xalFactory.createGenericTypedGrPostal();
            countryName0.setType("typeCN");
            countryName0.setGrPostal(xalFactory.createGrPostal("codeCN"));
            countryName0.setContent("Country Name");

        final GenericTypedGrPostal addressLine0 = xalFactory.createGenericTypedGrPostal();
            addressLine0.setGrPostal(xalFactory.createGrPostal("code1"));
            addressLine0.setType("type1");
            addressLine0.setContent("Première ligne");

        final GenericTypedGrPostal addressLine1 = xalFactory.createGenericTypedGrPostal();
            addressLine1.setGrPostal(xalFactory.createGrPostal("code2"));
            addressLine1.setType("type2");
            addressLine1.setContent("Seconde ligne");

        final Country country = xalFactory.createCountry();
            country.setAdministrativeArea(administrativeArea);
            country.setCountryNames(Arrays.asList(countryName0));
            country.setCountryNamesCodes(Arrays.asList(countryNameCode1, countryNameCode2));
            country.setAddressLines(Arrays.asList(addressLine0, addressLine1));

        final AddressDetails addressDetails = xalFactory.createAddressDetails();
            addressDetails.setCountry(country);

        final Xal xal = xalFactory.createXal(Arrays.asList(addressDetails), null);

        final File temp = File.createTempFile("country_AA",".xml");
        temp.deleteOnExit();

        final XalWriter writer = new XalWriter();
        writer.setOutput(temp);
        writer.write(xal);
        writer.dispose();

        DomCompare.compare(
                 new File(pathToTestFile), temp);

    }
}