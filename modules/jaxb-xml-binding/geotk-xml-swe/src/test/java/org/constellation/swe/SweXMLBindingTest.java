/*
 *    Constellation - An open source and standard compliant SDI
 *    http://www.constellation-sdi.org
 *
 *    (C) 2007 - 2009, Geomatys
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation; either
 *    version 3 of the License, or (at your option) any later version.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */

package org.constellation.swe;

import java.io.StringWriter;
import java.util.Arrays;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import org.constellation.swe.v101.AnyScalarPropertyType;
import org.constellation.swe.v101.DataArrayEntry;
import org.constellation.swe.v101.SimpleDataRecordEntry;
import org.constellation.swe.v101.Text;
import org.constellation.swe.v101.TextBlockEntry;

//Junit dependencies
import org.geotoolkit.xml.MarshallerPool;
import org.junit.*;
import static org.junit.Assert.*;

/**
 *
 * @author Guilhem Legal (Geomatys)
 */
public class SweXMLBindingTest {

    private MarshallerPool pool;
    private Marshaller   marshaller;

    @Before
    public void setUp() throws JAXBException {
        pool = new MarshallerPool("org.constellation.swe.v101");
        marshaller = pool.acquireMarshaller();
    }

    @After
    public void tearDown() {
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
    public void marshallingTest() throws JAXBException {

        Text text = new Text("definition", "some value");

        StringWriter sw = new StringWriter();
        marshaller.marshal(text, sw);

        String result = sw.toString();
        //we remove the first line
        result = result.substring(result.indexOf("?>") + 3);
        //we remove the xmlmns
        result = result.replace(" xmlns:xlink=\"http://www.w3.org/1999/xlink\"", "");
        result = result.replace(" xmlns:gml=\"http://www.opengis.net/gml\"", "");
        result = result.replace(" xmlns:swe=\"http://www.opengis.net/swe/1.0.1\"", "");
        result = result.replace(" xmlns:sa=\"http://www.opengis.net/sa/1.0\"", "");
       

        String expResult = "<swe:Text definition=\"definition\">" + '\n' +
                           "    <swe:value>some value</swe:value>" + '\n' +
                           "</swe:Text>" + '\n' ;
        assertEquals(expResult, result);

        SimpleDataRecordEntry elementType = new SimpleDataRecordEntry();
        AnyScalarPropertyType any = new AnyScalarPropertyType("id-1", "any name", text);
        TextBlockEntry encoding = new TextBlockEntry("encoding-1", ",", "@@", ".");
        elementType.setField(Arrays.asList(any));
        DataArrayEntry array = new DataArrayEntry("array-id-1", 0, elementType, encoding, null);


        sw = new StringWriter();
        marshaller.marshal(array, sw);
        result = sw.toString();

        //we remove the first line
        result = result.substring(result.indexOf("?>") + 3);
        //we remove the xmlmns
        result = result.replace(" xmlns:xlink=\"http://www.w3.org/1999/xlink\"", "");
        result = result.replace(" xmlns:gml=\"http://www.opengis.net/gml\"", "");
        result = result.replace(" xmlns:swe=\"http://www.opengis.net/swe/1.0.1\"", "");
        result = result.replace(" xmlns:sa=\"http://www.opengis.net/sa/1.0\"", "");
        

        expResult = "<swe:DataArray gml:id=\"array-id-1\">" + '\n' +
                    "    <swe:elementCount>" + '\n' +
                    "        <swe:Count>" + '\n' +
                    "            <swe:value>0</swe:value>" + '\n' +
                    "        </swe:Count>" + '\n' +
                    "    </swe:elementCount>" + '\n' +
                    "    <swe:elementType name=\"array-id-1\">" + '\n' +
                    "        <swe:SimpleDataRecord>" + '\n' +
                    "            <swe:field name=\"any name\">" + '\n' +
                    "                <swe:Text definition=\"definition\">" + '\n' +
                    "                    <swe:value>some value</swe:value>" + '\n' +
                    "                </swe:Text>" + '\n' +
                    "            </swe:field>" + '\n' +
                    "        </swe:SimpleDataRecord>" + '\n' +
                    "    </swe:elementType>" + '\n' +
                    "    <swe:encoding>" + '\n' +
                    "        <swe:TextBlock blockSeparator=\"@@\" decimalSeparator=\".\" tokenSeparator=\",\" id=\"encoding-1\"/>" + '\n' +
                    "    </swe:encoding>" + '\n' +
                    "</swe:DataArray>" + '\n';

        assertEquals(expResult, result);
    }
}
