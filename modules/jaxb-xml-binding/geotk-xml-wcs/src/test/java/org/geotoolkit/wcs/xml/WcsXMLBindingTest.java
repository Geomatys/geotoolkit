/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2008 - 2010, Geomatys
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
package org.geotoolkit.wcs.xml;

import java.util.Arrays;
import org.geotoolkit.wcs.xml.v100.GetCoverageType;
import java.io.StringWriter;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

//Junit dependencies
import org.geotoolkit.gml.xml.v311.ObjectFactory;
import org.geotoolkit.ows.xml.v110.CodeType;
import org.geotoolkit.wcs.xml.v100.OutputType;
import org.geotoolkit.xml.MarshallerPool;
import org.junit.*;
import static org.junit.Assert.*;

/**
 *
 * @author Guilhem Legal (Geomatys)
 * @module pending
 */
public class WcsXMLBindingTest {

    private MarshallerPool pool;
    private Marshaller   marshaller;
    private static ObjectFactory FACTORY = new ObjectFactory();

    @Before
    public void setUp() throws JAXBException {
        pool = WCSMarshallerPool.getInstance();
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
    public void marshallingTest111() throws JAXBException {

        org.geotoolkit.wcs.xml.v111.RangeSubsetType.FieldSubset field = new org.geotoolkit.wcs.xml.v111.RangeSubsetType.FieldSubset("id1", "NEAREST");
        org.geotoolkit.wcs.xml.v111.RangeSubsetType dsub = new org.geotoolkit.wcs.xml.v111.RangeSubsetType(Arrays.asList(field));
        org.geotoolkit.wcs.xml.v111.GetCoverageType getCoverage
                = new org.geotoolkit.wcs.xml.v111.GetCoverageType(new CodeType("source1"), null, dsub, new org.geotoolkit.wcs.xml.v111.OutputType(null, "EPSG:4326"));

        StringWriter sw = new StringWriter();
        marshaller.marshal(getCoverage, sw);

        String result = sw.toString();
        //we remove the first line
        result = result.substring(result.indexOf("?>") + 3);
        //we remove the xmlmns
        result = result.replace(" xmlns:gml=\"http://www.opengis.net/gml\"", "");
        result = result.replace(" xmlns:xlink=\"http://www.w3.org/1999/xlink\"", "");
        result = result.replace(" xmlns:ows=\"http://www.opengis.net/ows/1.1\"", "");
        result = result.replace(" xmlns:ns5=\"http://www.opengis.net/wcs/1.1.1\"", "");
        result = result.replace(" xmlns:wcs=\"http://www.opengis.net/wcs\"", "");


        String expResult = "<ns5:GetCoverage version=\"1.1.1\" service=\"WCS\">" + '\n'
                         + "    <ows:Identifier>source1</ows:Identifier>" + '\n'
                         + "    <ns5:RangeSubset>" + '\n'
                         + "        <ns5:FieldSubset>" + '\n'
                         + "            <ows:Identifier>id1</ows:Identifier>" + '\n'
                         + "            <ns5:InterpolationType>NEAREST</ns5:InterpolationType>" + '\n'
                         + "        </ns5:FieldSubset>" + '\n'
                         + "    </ns5:RangeSubset>" + '\n'
                         + "    <ns5:Output store=\"false\" format=\"EPSG:4326\"/>" + '\n'
                         + "</ns5:GetCoverage>" + '\n';
        assertEquals(expResult, result);

    }

    /**
     * Test simple Record Marshalling.
     *
     * @throws JAXBException
     */
    @Test
    public void marshallingTest100() throws JAXBException {

        GetCoverageType getCoverage = new GetCoverageType("source1", null, null, "nearest neighbor", new OutputType("image/png", "EPSG:4326"));

        StringWriter sw = new StringWriter();
        marshaller.marshal(getCoverage, sw);

        String result = sw.toString();
        //we remove the first line
        result = result.substring(result.indexOf("?>") + 3);
        //we remove the xmlmns
        result = result.replace(" xmlns:gml=\"http://www.opengis.net/gml\"", "");
        result = result.replace(" xmlns:xlink=\"http://www.w3.org/1999/xlink\"", "");
        result = result.replace(" xmlns:ows=\"http://www.opengis.net/ows/1.1\"", "");
        result = result.replace(" xmlns:ns5=\"http://www.opengis.net/wcs/1.1.1\"", "");
        result = result.replace(" xmlns:wcs=\"http://www.opengis.net/wcs\"", "");


        String expResult = "<wcs:GetCoverage version=\"1.0.0\" service=\"WCS\">" + '\n' +
                           "    <wcs:sourceCoverage>source1</wcs:sourceCoverage>" + '\n' +
                           "    <wcs:interpolationMethod>nearest neighbor</wcs:interpolationMethod>" + '\n' +
                           "    <wcs:output>" + '\n' +
                           "        <wcs:crs>EPSG:4326</wcs:crs>" + '\n' +
                           "        <wcs:format>image/png</wcs:format>" + '\n' +
                           "    </wcs:output>" + '\n' +
                           "</wcs:GetCoverage>" + '\n' ;
        assertEquals(expResult, result);

    }
}
