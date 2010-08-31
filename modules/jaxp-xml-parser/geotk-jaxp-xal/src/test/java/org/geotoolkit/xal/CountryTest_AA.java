/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.geotoolkit.xal;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.stream.XMLStreamException;
import org.geotoolkit.xal.model.AddressDetails;
import org.geotoolkit.xal.model.AddressLines;
import org.geotoolkit.xal.model.AdministrativeArea;
import org.geotoolkit.xal.model.Country;
import org.geotoolkit.xal.model.CountryNameCode;
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

        assertEquals(1, admininstrativeArea0.getAddressLines().size());
        assertEquals("Troisième ligne", admininstrativeArea0.getAddressLines().get(0).getContent());
        final GenericTypedGrPostal administrativeAreaName0 = admininstrativeArea0.getAdministrativeAreaNames().get(0);
        assertEquals("typeAAN",administrativeAreaName0.getType());
        assertEquals("codeAAN", administrativeAreaName0.getGrPostal().getCode());
        assertEquals("Administrative Area Name", administrativeAreaName0.getContent());
    }

    @Test
    public void country_AAWriteTest() throws IOException, XMLStreamException, ParserConfigurationException, SAXException{
//        final XalFactory xalFactory = DefaultXalFactory.getInstance();
//
//        final GenericTypedGrPostal administrativeAreaName = xalFactory.createGenericTypedGrPostal();
//        administrativeAreaName.setContent("Administrative Area Name");
//        administrativeAreaName.setGrPostal(xalFactory.createGrPostal("codeAAN"));
//        administrativeAreaName.setType("typeAAN");
//
//        final GenericTypedGrPostal addressLine3 = xalFactory.createGenericTypedGrPostal();
//        addressLine3.setContent("Troisième ligne");
//
//
//        //ADMINISTRATIVE AREA
//        final Country country = xalFactory.createCountry();
//        country.setAdministrativeArea(null);

//        final AddressLines addressLines = xalFactory.createAddressLines();
//
//        final GenericTypedGrPostal addressLine0 = xalFactory.createGenericTypedGrPostal();
//        addressLine0.setGrPostal(xalFactory.createGrPostal("code1"));
//        addressLine0.setType("type1");
//        addressLine0.setContent("Première ligne");
//
//        final GenericTypedGrPostal addressLine1 = xalFactory.createGenericTypedGrPostal();
//        addressLine1.setGrPostal(xalFactory.createGrPostal("code2"));
//        addressLine1.setType("type2");
//        addressLine1.setContent("Seconde ligne");
//        addressLines.setAddressLines(Arrays.asList(addressLine0, addressLine1));
//
//        final AddressDetails addressDetails = xalFactory.createAddressDetails();
//        addressDetails.setAddressLines(addressLines);
//
//        final Xal xal = xalFactory.createXal(Arrays.asList(addressDetails), null);
//
//        final File temp = File.createTempFile("addressLinesTest",".xal");
//        temp.deleteOnExit();
//
//        final XalWriter writer = new XalWriter();
//        writer.setOutput(temp);
//        writer.write(xal);
//        writer.dispose();
//
//        DomCompare.compare(
//                 new File(pathToTestFile), temp);

    }
}