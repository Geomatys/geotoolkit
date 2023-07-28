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
package org.geotoolkit.swe.xml.v101;

import java.util.List;
import java.util.ArrayList;
import java.io.StringWriter;
import java.util.Arrays;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Marshaller;
import jakarta.xml.bind.Unmarshaller;

import org.geotoolkit.util.StringUtilities;
import jakarta.xml.bind.JAXBContext;
import org.apache.sis.xml.MarshallerPool;
import org.junit.*;

import static org.junit.Assert.*;
import static org.geotoolkit.test.Assertions.assertXmlEquals;


/**
 *
 * @author Guilhem Legal (Geomatys)
 */
public class SweXMLBindingTest {

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
        Text text = new Text("id-001", "urn:ogc:id-001", "some description", "urn:ogc:def:id-001", "some value");

        StringWriter sw = new StringWriter();
        marshaller.marshal(text, sw);

        String result = sw.toString();
        //we remove the first line
        result = result.substring(result.indexOf("?>") + 2).trim();
        //we remove the xmlmns
        result = StringUtilities.removeXmlns(result);

        String expResult = """
                           <swe:Text definition="urn:ogc:def:id-001" ns6:id="id-001" >
                             <ns6:description>some description</ns6:description>
                             <ns6:name>urn:ogc:id-001</ns6:name>
                             <swe:value>some value</swe:value>
                           </swe:Text>
                           """;
        assertEquals(expResult.trim(), result.trim());

        SimpleDataRecordType elementType = new SimpleDataRecordType();
        AnyScalarPropertyType any = new AnyScalarPropertyType("id-1", "any name", text);
        TextBlockType encoding = new TextBlockType("encoding-1", ",", "@@", ".");
        elementType.setField(Arrays.asList(any));
        DataArrayType array = new DataArrayType("array-id-1", 0, "array-id-1", elementType, encoding, null, null);


        sw = new StringWriter();
        marshaller.marshal(array, sw);
        result = sw.toString();

        //we remove the first line
        result = result.substring(result.indexOf("?>") + 2).trim();

        expResult = "<swe:DataArray gml:id=\"array-id-1\"" +
                        " xmlns:gml=\"http://www.opengis.net/gml\"" +
                        " xmlns:swe=\"http://www.opengis.net/swe/1.0.1\">" + '\n' +
                    "    <swe:elementCount>" + '\n' +
                    "        <swe:Count>" + '\n' +
                    "            <swe:value>0</swe:value>" + '\n' +
                    "        </swe:Count>" + '\n' +
                    "    </swe:elementCount>" + '\n' +
                    "    <swe:elementType name=\"array-id-1\">" + '\n' +
                    "        <swe:SimpleDataRecord>" + '\n' +
                    "            <swe:field name=\"any name\">" + '\n' +
                    "                <swe:Text definition=\"urn:ogc:def:id-001\" gml:id=\"id-001\">\n" +
                    "                   <gml:description>some description</gml:description>\n" +
                    "                   <gml:name>urn:ogc:id-001</gml:name>\n" +
                    "                   <swe:value>some value</swe:value>\n" +
                    "                </swe:Text>" + '\n' +
                    "            </swe:field>" + '\n' +
                    "        </swe:SimpleDataRecord>" + '\n' +
                    "    </swe:elementType>" + '\n' +
                    "    <swe:encoding>" + '\n' +
                    "        <swe:TextBlock blockSeparator=\"@@\" decimalSeparator=\".\" tokenSeparator=\",\" id=\"encoding-1\"/>" + '\n' +
                    "    </swe:encoding>" + '\n' +
                    "</swe:DataArray>" + '\n';

        assertXmlEquals(expResult, result, "xmlns:*");


        ObjectFactory factory = new ObjectFactory();

        final List<DataComponentPropertyType> fields = new ArrayList<>();
        fields.add(DataComponentPropertyType.LATITUDE_FIELD);
        fields.add(DataComponentPropertyType.LONGITUDE_FIELD);
        fields.add(DataComponentPropertyType.TIME_FIELD);
        final DataRecordType posRecord = new DataRecordType(null, fields);
        final DataBlockDefinitionType definition = new DataBlockDefinitionType(null, Arrays.asList((AbstractDataComponentType)posRecord), TextBlockType.DEFAULT_ENCODING);

        sw = new StringWriter();
        marshaller.marshal(factory.createDataBlockDefinition(definition), sw);
        result = sw.toString();

        String expected = """
                          <?xml version="1.0" encoding="UTF-8" standalone="yes"?>
                          <swe:DataBlockDefinition xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xmlns:xlink="http://www.w3.org/1999/xlink" xmlns:swe="http://www.opengis.net/swe/1.0.1" xmlns:gml="http://www.opengis.net/gml/3.2" xmlns:om="http://www.opengis.net/om/1.0" xmlns:ns6="http://www.opengis.net/gml" xmlns:ns7="http://www.opengis.net/swe/2.0">
                            <swe:DataRecord>
                              <swe:field name="Latitude">
                                <swe:Quantity definition="urn:ogc:phenomenon:latitude:wgs84">
                                  <swe:uom code="degree"/>
                                </swe:Quantity>
                              </swe:field>
                              <swe:field name="Longitude">
                                <swe:Quantity definition="urn:ogc:phenomenon:longitude:wgs84">
                                  <swe:uom code="degree"/>
                                </swe:Quantity>
                              </swe:field>
                              <swe:field name="Time">
                                <swe:Time definition="urn:ogc:data:time:iso8601">
                                  <swe:value></swe:value>
                                </swe:Time>
                              </swe:field>
                            </swe:DataRecord>
                            <swe:encoding>
                              <swe:TextBlock tokenSeparator="," decimalSeparator="." blockSeparator="@@" id="encoding-1"/>
                            </swe:encoding>
                          </swe:DataBlockDefinition>
                          """;

        assertXmlEquals(expected, result, "xmlns:*");

        expected = """
                   <?xml version="1.0" encoding="UTF-8" standalone="yes"?>
                   <ns7:DataArray xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xmlns:xlink="http://www.w3.org/1999/xlink" xmlns:swe="http://www.opengis.net/swe/1.0.1" xmlns:gml="http://www.opengis.net/gml/3.2" xmlns:om="http://www.opengis.net/om/1.0" xmlns:ns6="http://www.opengis.net/gml" xmlns:ns7="http://www.opengis.net/swe/2.0" id="test-id">
                     <ns7:elementCount>
                       <ns7:Count>
                         <ns7:value>2</ns7:value>
                       </ns7:Count>
                     </ns7:elementCount>
                     <ns7:values>balbbla</ns7:values>
                   </ns7:DataArray>
                   """;
        org.geotoolkit.swe.xml.v200.ObjectFactory factoryV200 = new org.geotoolkit.swe.xml.v200.ObjectFactory();
        org.geotoolkit.swe.xml.v200.DataArrayType arrayV200 = new org.geotoolkit.swe.xml.v200.DataArrayType("test-id", 2, null, "balbbla", "test-id", null, null);

        sw = new StringWriter();
        marshaller.marshal(factoryV200.createDataArray(arrayV200), sw);
        result = sw.toString();

        assertXmlEquals(expected, result, "xmlns:*");
    }

    @Test
    public void cloneDataBlockDefinitionTest() throws Exception {
        final List<DataComponentPropertyType> fields = new ArrayList<>();
        fields.add(DataComponentPropertyType.LATITUDE_FIELD);
        fields.add(DataComponentPropertyType.LONGITUDE_FIELD);
        fields.add(DataComponentPropertyType.TIME_FIELD);
        final DataRecordType posRecord = new DataRecordType(null, fields);
        final DataBlockDefinitionType expResult = new DataBlockDefinitionType(null, Arrays.asList((AbstractDataComponentType)posRecord), TextBlockType.DEFAULT_ENCODING);

        final DataBlockDefinitionType result = new DataBlockDefinitionType(expResult);

        assertEquals(expResult.getEncoding(), result.getEncoding());
        assertEquals(expResult, result);
    }
}
