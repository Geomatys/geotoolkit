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
import org.geotoolkit.ows.xml.v110.ExceptionReport;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.logging.Level;
import java.util.logging.Logger;

// JAXB dependencies
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

// Geotoolkit dependencies
import org.geotoolkit.util.logging.Logging;
import org.geotoolkit.xml.MarshallerPool;

//Junit dependencies
import org.junit.*;
import static org.junit.Assert.*;


/**
 * A Test suite verifying that the Record are correctly marshalled/unmarshalled
 *
 * @author Guilhem Legal
 * @module pending
 */
public class OWSXmlBindingTest {

    private static final Logger LOGGER = Logging.getLogger("org.geotoolkit.filter");

    private MarshallerPool pool;
    private Unmarshaller unmarshaller;
    private Marshaller   marshaller;

    @Before
    public void setUp() throws JAXBException {
        pool = ExceptionReportMarshallerPool.getInstance();
        marshaller = pool.acquireMarshaller();
        unmarshaller = pool.acquireUnmarshaller();
    }

    @After
    public void tearDown() {
        if (unmarshaller != null) {
            pool.release(unmarshaller);
        }
        if (marshaller != null) {
            pool.release(marshaller);
        }
    }

    /**
     * Test simple Record Marshalling.
     *
     * @throws JAXBException
     */
    @Test
    public void exceptionMarshalingTest() throws JAXBException {

        /*
         * Test marshalling exception report 110
         */
        ExceptionReport report = new ExceptionReport("some error", OWSExceptionCode.INVALID_CRS.name(), "parameter1", "1.1.0");
        StringWriter sw = new StringWriter();
        marshaller.marshal(report, sw);

        String result = sw.toString();
        //we remove the xmlmns
        result = result.replace(" xmlns:ows=\"http://www.opengis.net/ows\"", "");
        result = result.replace(" xmlns:ns2=\"http://www.opengis.net/ows/1.1\"", "");
        result = result.replace(" xmlns:ogc=\"http://www.opengis.net/ogc\"", "");

        String expResult =
        "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>"           + '\n' +
        "<ns2:ExceptionReport version=\"1.1.0\">"                                 + '\n' +
        "    <ns2:Exception locator=\"parameter1\" exceptionCode=\"InvalidCRS\">" + '\n' +
        "        <ns2:ExceptionText>some error</ns2:ExceptionText>"               + '\n' +
        "    </ns2:Exception>"                                                    + '\n' +
        "</ns2:ExceptionReport>"                                                  + '\n';

        LOGGER.log(Level.FINER, "result: {0}", result);
        LOGGER.log(Level.FINER, "expected: {0}", expResult);
        assertEquals(expResult, result);


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
}

