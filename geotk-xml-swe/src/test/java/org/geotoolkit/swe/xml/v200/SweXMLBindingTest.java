/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2022, Geomatys
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
package org.geotoolkit.swe.xml.v200;

import java.io.StringReader;
import java.io.StringWriter;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBElement;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Marshaller;
import jakarta.xml.bind.Unmarshaller;
import org.apache.sis.xml.MarshallerPool;
import org.geotoolkit.util.StringUtilities;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.*;
import static org.apache.sis.metadata.Assertions.assertXmlEquals;


/**
 *
 * @author Guilhem Legal (Geomatys)
 */
public class SweXMLBindingTest extends org.geotoolkit.test.TestBase {

    private static MarshallerPool pool;
    private Marshaller   marshaller;
    private Unmarshaller   unmarshaller;

    @BeforeClass
    public static void setUpClass() throws Exception {
        pool = new MarshallerPool(JAXBContext.newInstance("org.geotoolkit.swe.xml.v101:org.geotoolkit.swe.xml.v200:org.apache.sis.internal.jaxb.geometry"), null);
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
        if (marshaller != null) {
            pool.recycle(marshaller);
        }
        if (unmarshaller != null) {
            pool.recycle(unmarshaller);
        }
    }

    /**
     * Test simple Record Marshalling.
     */
    @Test
    public void marshallingTest() throws Exception {
        TextType text = new TextType("id-001", "urn:ogc:id-001", "some description", "urn:ogc:def:id-001", "some value", null);

        ObjectFactory factory = new ObjectFactory();

        StringWriter sw = new StringWriter();
        marshaller.marshal(factory.createText(text), sw);

        String result = sw.toString();
        //we remove the first line
        result = result.substring(result.indexOf("?>") + 2).trim();
        //we remove the xmlmns
        result = StringUtilities.removeXmlns(result);

        String expResult = """
                           <ns7:Text definition="urn:ogc:def:id-001" id="id-001" >
                             <ns7:identifier>urn:ogc:id-001</ns7:identifier>
                             <ns7:description>some description</ns7:description>
                             <ns7:value>some value</ns7:value>
                           </ns7:Text>
                           """;
        assertEquals(expResult.trim(), result.trim());

        DataRecordType elementType = new DataRecordType();
        TextEncodingType encoding = new TextEncodingType("encoding-1", ",", "@@", ".");
        elementType.addField(new Field("any name", text));
        DataArrayType array = new DataArrayType("array-id-1", 0, encoding, "array-id-1", "somes values",  elementType, null);


        sw = new StringWriter();
        marshaller.marshal(factory.createDataArray(array), sw);
        result = sw.toString();

        //we remove the first line
        result = result.substring(result.indexOf("?>") + 2).trim();

        expResult = """
                    <?xml version="1.0" encoding="UTF-8" standalone="yes"?>
                    <ns7:DataArray id="array-id-1" xmlns:swe="http://www.opengis.net/swe/1.0.1" xmlns:ns6="http://www.opengis.net/gml" xmlns:ns7="http://www.opengis.net/swe/2.0" xmlns:gml="http://www.opengis.net/gml/3.2" xmlns:om="http://www.opengis.net/om/1.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xmlns:xlink="http://www.w3.org/1999/xlink">
                      <ns7:elementCount>
                        <ns7:Count>
                          <ns7:value>0</ns7:value>
                        </ns7:Count>
                      </ns7:elementCount>
                      <ns7:elementType name="somes values">
                        <ns7:DataRecord>
                          <ns7:field name="any name">
                            <ns7:Text definition="urn:ogc:def:id-001" id="id-001">
                              <ns7:identifier>urn:ogc:id-001</ns7:identifier>
                              <ns7:description>some description</ns7:description>
                              <ns7:value>some value</ns7:value>
                            </ns7:Text>
                          </ns7:field>
                        </ns7:DataRecord>
                      </ns7:elementType>
                      <ns7:encoding>
                        <ns7:TextEncoding decimalSeparator="," tokenSeparator="@@" blockSeparator="." id="encoding-1"/>
                      </ns7:encoding>
                      <ns7:values>array-id-1</ns7:values>
                    </ns7:DataArray>
                    """;

        assertXmlEquals(expResult, result, "xmlns:*");
    }

    @Test
    public void unmarshallingTest() throws Exception {
        String s =
        "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" + '\n' +
        "<swe:DataArray xmlns:swe=\"http://www.opengis.net/swe/2.0\">" + '\n' +
        "        <swe:elementCount>" + '\n' +
        "                <swe:Count>" + '\n' +
        "                        <swe:value>5</swe:value>" + '\n' +
        "                </swe:Count>" + '\n' +
        "        </swe:elementCount>" + '\n' +
        "        <swe:elementType name=\"point\">" + '\n' +
        "                <swe:DataRecord>" + '\n' +
        "                        <swe:field name=\"depth\">" + '\n' +
        "                                <swe:Quantity definition=\"http://mmisw.org/ont/cf/parameter/depth\">" + '\n' +
        "                                        <swe:label>Sampling Point Vertical Location</swe:label>" + '\n' +
        "                                        <swe:uom code=\"m\"/>" + '\n' +
        "                                </swe:Quantity>" + '\n' +
        "                        </swe:field>" + '\n' +
        "                        <swe:field name=\"temperature\">" + '\n' +
        "                                <swe:Quantity definition=\"http://mmisw.org/ont/cf/parameter/sea_water_temperature\">" + '\n' +
        "                                        <swe:label>Temperature</swe:label>" + '\n' +
        "                                        <swe:uom code=\"Cel\"/>" + '\n' +
        "                                </swe:Quantity>" + '\n' +
        "                        </swe:field>" + '\n' +
        "                        <swe:field name=\"salinity\">" + '\n' +
        "                          <swe:Quantity definition=\"http://mmisw.org/ont/cf/parameter/sea_water_salinity\">" + '\n' +
        "                                        <swe:label>Salinity</swe:label>" + '\n' +
        "                                        <swe:uom code=\"[ppth]\"/>" + '\n' +
        "                                </swe:Quantity>" + '\n' +
        "                        </swe:field>" + '\n' +
        "                </swe:DataRecord>" + '\n' +
        "        </swe:elementType>" + '\n' +
        "        <swe:encoding>" + '\n' +
        "                <swe:TextEncoding blockSeparator=\" \" tokenSeparator=\",\"/>" + '\n' +
        "        </swe:encoding>" + '\n' +
        "        <swe:values>00,12,45 10,13,20 20,14,30 30,13,35 40,13,40</swe:values>" + '\n' +
        "</swe:DataArray>";

        Object obj = unmarshaller.unmarshal(new StringReader(s));
        if (obj instanceof JAXBElement) {
            obj = ((JAXBElement)obj).getValue();
        }
        assertTrue(obj instanceof DataArrayType);

        DataArrayType result = (DataArrayType) obj;


        String values = "00,12,45 10,13,20 20,14,30 30,13,35 40,13,40";

        DataRecordType elementType = new DataRecordType();
        TextEncodingType encoding = new TextEncodingType(null, null, ",", " ");

        QuantityType q1 = new QuantityType("http://mmisw.org/ont/cf/parameter/depth", "m", null);
        q1.setLabel("Sampling Point Vertical Location");
        elementType.addField(new Field("depth", q1));

        QuantityType q2 = new QuantityType("http://mmisw.org/ont/cf/parameter/sea_water_temperature", "Cel", null);
        q2.setLabel("Temperature");
        elementType.addField(new Field("temperature", q2));

        QuantityType q3 = new QuantityType("http://mmisw.org/ont/cf/parameter/sea_water_salinity", "[ppth]", null);
        q3.setLabel("Salinity");
        elementType.addField(new Field("salinity", q3));

        DataArrayType expected = new DataArrayType(null, 5, encoding, values, "point", elementType, null);

        assertEquals(expected, result);
    }
}
