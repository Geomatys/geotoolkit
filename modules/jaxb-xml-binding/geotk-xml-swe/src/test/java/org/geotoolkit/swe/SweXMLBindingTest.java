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
package org.geotoolkit.swe;

import java.io.StringReader;
import org.geotoolkit.swe.xml.v101.AbstractDataComponentType;
import java.util.List;
import java.util.ArrayList;
import java.io.StringWriter;
import java.util.Arrays;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import org.geotoolkit.swe.xml.v101.AnyScalarPropertyType;
import org.geotoolkit.swe.xml.v101.DataArrayType;
import org.geotoolkit.swe.xml.v101.SimpleDataRecordType;
import org.geotoolkit.swe.xml.v101.Text;
import org.geotoolkit.swe.xml.v101.TextBlockType;
import org.geotoolkit.swe.xml.v101.DataBlockDefinitionType;
import org.geotoolkit.swe.xml.v101.DataRecordType;
import org.geotoolkit.swe.xml.v101.DataComponentPropertyType;
import org.geotoolkit.swe.xml.v101.ObjectFactory;

//Junit dependencies
import org.geotoolkit.util.StringUtilities;
import org.geotoolkit.xml.MarshallerPool;
import org.junit.*;
import static org.junit.Assert.*;

/**
 *
 * @author Guilhem Legal (Geomatys)
 * @module pending
 */
public class SweXMLBindingTest {

    private static MarshallerPool pool;
    private Marshaller   marshaller;
    private Unmarshaller   unmarshaller;

    @BeforeClass
    public static void setUpClass() throws Exception {
        pool = new MarshallerPool("org.geotoolkit.swe.xml.v101:org.geotoolkit.swe.xml.v200:org.geotoolkit.internal.jaxb.geometry");
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
            pool.release(marshaller);
        }
        if (unmarshaller != null) {
            pool.release(unmarshaller);
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
        result = StringUtilities.removeXmlns(result);
       
        String expResult = "<swe:Text definition=\"definition\" >" + '\n' +
                           "    <swe:value>some value</swe:value>" + '\n' +
                           "</swe:Text>" + '\n' ;
        assertEquals(expResult, result);

        SimpleDataRecordType elementType = new SimpleDataRecordType();
        AnyScalarPropertyType any = new AnyScalarPropertyType("id-1", "any name", text);
        TextBlockType encoding = new TextBlockType("encoding-1", ",", "@@", ".");
        elementType.setField(Arrays.asList(any));
        DataArrayType array = new DataArrayType("array-id-1", 0, elementType, encoding, null);


        sw = new StringWriter();
        marshaller.marshal(array, sw);
        result = sw.toString();

        //we remove the first line
        result = result.substring(result.indexOf("?>") + 3);
        //we remove the xmlmns
        result = StringUtilities.removeXmlns(result);

        expResult = "<swe:DataArray gml:id=\"array-id-1\" >" + '\n' +
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
        
    
        ObjectFactory factory = new ObjectFactory();
        
        final List<DataComponentPropertyType> fields = new ArrayList<DataComponentPropertyType>();
        fields.add(DataComponentPropertyType.LATITUDE_FIELD);
        fields.add(DataComponentPropertyType.LONGITUDE_FIELD);
        fields.add(DataComponentPropertyType.TIME_FIELD);
        final DataRecordType posRecord = new DataRecordType(null, fields);
        final DataBlockDefinitionType definition = new DataBlockDefinitionType(null, Arrays.asList((AbstractDataComponentType)posRecord), TextBlockType.DEFAULT_ENCODING);
        
        marshaller.marshal(factory.createDataBlockDefinition(definition), System.out);
        
        org.geotoolkit.swe.xml.v200.ObjectFactory factoryV200 = new org.geotoolkit.swe.xml.v200.ObjectFactory();
        org.geotoolkit.swe.xml.v200.DataArrayType arrayV200 = new org.geotoolkit.swe.xml.v200.DataArrayType("test-id", 2, null, "balbbla", null);
        marshaller.marshal(factoryV200.createDataArray(arrayV200), System.out);
    }
    
    @Test
    public void cloneDataBlockDefinitionTest() throws Exception {
        final List<DataComponentPropertyType> fields = new ArrayList<DataComponentPropertyType>();
        fields.add(DataComponentPropertyType.LATITUDE_FIELD);
        fields.add(DataComponentPropertyType.LONGITUDE_FIELD);
        fields.add(DataComponentPropertyType.TIME_FIELD);
        final DataRecordType posRecord = new DataRecordType(null, fields);
        final DataBlockDefinitionType expResult = new DataBlockDefinitionType(null, Arrays.asList((AbstractDataComponentType)posRecord), TextBlockType.DEFAULT_ENCODING);
        
        final DataBlockDefinitionType result = new DataBlockDefinitionType(expResult);
        
        assertEquals(expResult.getEncoding(), result.getEncoding());
        assertEquals(expResult, result);
        
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
        System.out.println(obj);
    }
}
