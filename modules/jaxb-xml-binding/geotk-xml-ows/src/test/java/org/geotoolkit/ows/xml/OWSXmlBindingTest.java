/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2008 - 2009, Geomatys
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
package org.geotoolkit.ows.xml;

// J2SE dependencies
import java.io.IOException;
import org.geotoolkit.ows.xml.v110.ExceptionReport;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;

// JAXB dependencies
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.parsers.ParserConfigurationException;

// Geotoolkit dependencies
import org.apache.sis.util.logging.Logging;
import org.apache.sis.xml.MarshallerPool;

//Junit dependencies
import org.junit.*;
import org.xml.sax.SAXException;

import static org.apache.sis.test.Assert.*;
import org.geotoolkit.ows.xml.v200.AdditionalParameter;
import org.geotoolkit.ows.xml.v200.AdditionalParametersType;
import org.geotoolkit.ows.xml.v200.CodeType;
import org.geotoolkit.ows.xml.v200.MetadataType;
import org.geotoolkit.ows.xml.v200.ObjectFactory;


/**
 * A Test suite verifying that the Record are correctly marshalled/unmarshalled
 *
 * @author Guilhem Legal
 * @module
 */
public class OWSXmlBindingTest extends org.geotoolkit.test.TestBase {

    private static final Logger LOGGER = Logging.getLogger("org.geotoolkit.filter");

    private static final MarshallerPool pool = ExceptionReportMarshallerPool.getInstance();
    private static final MarshallerPool owsPool;
    static {
        try {
            owsPool = new MarshallerPool(JAXBContext.newInstance(
                    org.geotoolkit.ows.xml.v100.ObjectFactory.class,
                    org.geotoolkit.ows.xml.v200.ObjectFactory.class), null);
        } catch (JAXBException ex) {
            throw new AssertionError(ex); // Should never happen, unless we have a build configuration problem.
        }
    }
    private Unmarshaller unmarshaller;
    private Marshaller   marshaller;

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }


    @Before
    public void setUp() throws JAXBException {
        marshaller = pool.acquireMarshaller();
        unmarshaller = pool.acquireUnmarshaller();
    }

    @After
    public void tearDown() {
        if (unmarshaller != null) {
            pool.recycle(unmarshaller);
        }
        if (marshaller != null) {
            pool.recycle(marshaller);
        }
    }

    /**
     * Test simple Record Marshalling.
     *
     * @throws JAXBException
     */
    @Test
    public void exceptionMarshalingTest() throws JAXBException, IOException, ParserConfigurationException, SAXException {

        /*
         * Test marshalling exception report 110
         */
        ExceptionReport report = new ExceptionReport("some error", OWSExceptionCode.INVALID_CRS.name(), "parameter1", "1.1.0");
        StringWriter sw = new StringWriter();
        marshaller.marshal(report, sw);

        String result = sw.toString();

        String expResult =
        "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>"           + '\n' +
        "<ns2:ExceptionReport version=\"1.1.0\" xmlns:ns2=\"http://www.opengis.net/ows/1.1\">" + '\n' +
        "    <ns2:Exception locator=\"parameter1\" exceptionCode=\"InvalidCRS\">" + '\n' +
        "        <ns2:ExceptionText>some error</ns2:ExceptionText>"               + '\n' +
        "    </ns2:Exception>"                                                    + '\n' +
        "</ns2:ExceptionReport>"                                                  + '\n';
        assertXmlEquals(expResult, result, "xmlns:*");

    }

    /**
     * Test simple Record Marshalling.
     *
     * @throws JAXBException
     */
    @Test
    public void exceptionUnmarshalingTest() throws JAXBException {

        /*
         * Test Unmarshalling exceptionReport 1.1
         */

        String xml =
       "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>"           + '\n' +
        "<ns2:ExceptionReport version=\"1.1.0\" xmlns:ns2=\"http://www.opengis.net/ows/1.1\">"  + '\n' +
        "    <ns2:Exception locator=\"parameter1\" exceptionCode=\"InvalidCRS\">" + '\n' +
        "        <ns2:ExceptionText>some error</ns2:ExceptionText>"               + '\n' +
        "    </ns2:Exception>"                                                    + '\n' +
        "</ns2:ExceptionReport>"                                                  + '\n';

        StringReader sr = new StringReader(xml);

        ExceptionReport result = (ExceptionReport) unmarshaller.unmarshal(sr);

        ExceptionReport expResult = new ExceptionReport("some error", OWSExceptionCode.INVALID_CRS.name(), "parameter1", "1.1.0");


        assertEquals(expResult, result);

    }

    /**
     * Test simple Record Marshalling.
     *
     * @throws JAXBException
     */
    @Test
    public void additionalParametersTypeMarshallingTest() throws JAXBException, IOException, ParserConfigurationException, SAXException {
        Marshaller marshaller = owsPool.acquireMarshaller();
        final ObjectFactory factory = new ObjectFactory();
        
        AdditionalParametersType metadata = new AdditionalParametersType();
        List<AdditionalParameter> params = new ArrayList<>();
        params.add(new AdditionalParameter(new CodeType("param-1"), Arrays.asList("value 1")));
        params.add(new AdditionalParameter(new CodeType("param-2"), Arrays.asList("value 2")));
        metadata.setAdditionalParameter(params);
        
        StringWriter sw = new StringWriter();
        marshaller.marshal(factory.createAdditionalParameters(metadata), sw);

        String result = sw.toString();

        System.out.println(result);

        String expResult =
        "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n" +
        "<ns4:AdditionalParameters xmlns:ows=\"http://www.opengis.net/ows\" xmlns:ns4=\"http://www.opengis.net/ows/2.0\" xmlns:xlink=\"http://www.w3.org/1999/xlink\" xmlns:ins=\"http://www.inspire.org\">\n" +
        "  <ns4:AdditionalParameter>\n" +
        "    <ns4:Name>param-1</ns4:Name>\n" +
        "    <ns4:Value xsi:type=\"xsd:string\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">value 1</ns4:Value>\n" +
        "  </ns4:AdditionalParameter>\n" +
        "  <ns4:AdditionalParameter>\n" +
        "    <ns4:Name>param-2</ns4:Name>\n" +
        "    <ns4:Value xsi:type=\"xsd:string\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">value 2</ns4:Value>\n" +
        "  </ns4:AdditionalParameter>\n" +
        "</ns4:AdditionalParameters>"                                                  + '\n';
        assertXmlEquals(expResult, result, "xmlns:*");
    }

    @Test
    public void additionalParametersTypeUnmarshalingTest() throws JAXBException {

        Unmarshaller unmarshaller = owsPool.acquireUnmarshaller();

        String xml =
       "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n" +
        "<ns4:AdditionalParameters xmlns:ows=\"http://www.opengis.net/ows\" xmlns:ns4=\"http://www.opengis.net/ows/2.0\" xmlns:xlink=\"http://www.w3.org/1999/xlink\" xmlns:ins=\"http://www.inspire.org\">\n" +
        "  <ns4:AdditionalParameter>\n" +
        "    <ns4:Name>param-1</ns4:Name>\n" +
        "    <ns4:Value xsi:type=\"xsd:string\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">value 1</ns4:Value>\n" +
        "  </ns4:AdditionalParameter>\n" +
        "  <ns4:AdditionalParameter>\n" +
        "    <ns4:Name>param-2</ns4:Name>\n" +
        "    <ns4:Value xsi:type=\"xsd:string\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">value 2</ns4:Value>\n" +
        "  </ns4:AdditionalParameter>\n" +
        "</ns4:AdditionalParameters>"+ '\n';

        StringReader sr = new StringReader(xml);

        AdditionalParametersType result = ((JAXBElement<AdditionalParametersType>) unmarshaller.unmarshal(sr)).getValue();

        AdditionalParametersType expResult = new AdditionalParametersType();
        List<AdditionalParameter> params = new ArrayList<>();
        params.add(new AdditionalParameter(new CodeType("param-1"), Arrays.asList("value 1")));
        params.add(new AdditionalParameter(new CodeType("param-2"), Arrays.asList("value 2")));
        expResult.setAdditionalParameter(params);


        assertEquals(expResult.getAdditionalParameter(), result.getAdditionalParameter());

    }

    @Test
    public void additionalParametersNoTypeUnmarshalingTest() throws JAXBException {

        Unmarshaller unmarshaller = owsPool.acquireUnmarshaller();

        String xml =
       "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n" +
        "<ns4:AdditionalParameters xmlns:ows=\"http://www.opengis.net/ows\" xmlns:ns4=\"http://www.opengis.net/ows/2.0\" xmlns:xlink=\"http://www.w3.org/1999/xlink\" xmlns:ins=\"http://www.inspire.org\">\n" +
        "  <ns4:AdditionalParameter>\n" +
        "    <ns4:Name>param-1</ns4:Name>\n" +
        "    <ns4:Value>value 1</ns4:Value>\n" +
        "  </ns4:AdditionalParameter>\n" +
        "  <ns4:AdditionalParameter>\n" +
        "    <ns4:Name>param-2</ns4:Name>\n" +
        "    <ns4:Value>value 2</ns4:Value>\n" +
        "  </ns4:AdditionalParameter>\n" +
        "</ns4:AdditionalParameters>"+ '\n';

        StringReader sr = new StringReader(xml);

        AdditionalParametersType result = ((JAXBElement<AdditionalParametersType>) unmarshaller.unmarshal(sr)).getValue();

        AdditionalParametersType expResult = new AdditionalParametersType();
        List<AdditionalParameter> params = new ArrayList<>();
        params.add(new AdditionalParameter(new CodeType("param-1"), Arrays.asList("value 1")));
        params.add(new AdditionalParameter(new CodeType("param-2"), Arrays.asList("value 2")));
        expResult.setAdditionalParameter(params);


        assertEquals(expResult.getAdditionalParameter(), result.getAdditionalParameter());

    }
}

