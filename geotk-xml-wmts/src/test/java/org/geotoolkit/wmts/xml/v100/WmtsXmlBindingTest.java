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
package org.geotoolkit.wmts.xml.v100;

import java.io.StringReader;
import java.io.StringWriter;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import org.geotoolkit.gml.xml.v311.DirectPositionType;
import org.geotoolkit.gml.xml.v311.PointType;
import org.geotoolkit.ows.xml.v110.CodeType;
import org.geotoolkit.wmts.xml.WMTSMarshallerPool;

import org.apache.sis.xml.MarshallerPool;
import org.junit.*;

import static org.apache.sis.test.MetadataAssert.*;


/**
 *
 * @author Guilhem Legal (Geomatys)
 * @module
 */
public class WmtsXmlBindingTest extends org.geotoolkit.test.TestBase {

    private MarshallerPool pool;
    private Unmarshaller unmarshaller;
    private Marshaller   marshaller;

    @Before
    public void setUp() throws JAXBException {
        pool = WMTSMarshallerPool.getInstance();
        unmarshaller = pool.acquireUnmarshaller();
        marshaller   = pool.acquireMarshaller();
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
     */
    @Test
    public void unmarshallingTest() throws JAXBException {

        String xml = "<TileMatrix xmlns=\"http://www.opengis.net/wmts/1.0\" xmlns:ows=\"http://www.opengis.net/ows/1.1\" xmlns:gml=\"http://www.opengis.net/gml\" >" + '\n' +
                     "  <ows:Identifier>16d</ows:Identifier>" + '\n' +
                     "  <ScaleDenominator>55218.001386</ScaleDenominator>" + '\n' +
                     "  <TopLeftPoint>" + '\n' +
                     "      <gml:Point gml:id=\"p1\">" + '\n' +
                     "          <gml:pos>-90.080000 29.982000</gml:pos>" + '\n' +
                     "      </gml:Point>" + '\n' +
                     "  </TopLeftPoint>" + '\n' +
                     "  <TileWidth>256</TileWidth>" + '\n' +
                     "  <TileHeight>256</TileHeight>" + '\n' +
                     "  <MatrixWidth>3</MatrixWidth>" + '\n' +
                     "  <MatrixHeight>3</MatrixHeight>" + '\n' +
                     " </TileMatrix>";

        StringReader sr = new StringReader(xml);
        TileMatrix result = (TileMatrix) unmarshaller.unmarshal(sr);

        PointType pt = new PointType("p1", new DirectPositionType(-90.080000, 29.982000));
        TileMatrix expResult = new TileMatrix(new CodeType("16d"),
                                              55218.001386,
                                              256,
                                              256,
                                              3,
                                              3);

        assertEquals(expResult, result);

    }

    /**
     * Test simple Record Marshalling.
     *
     * @throws java.lang.Exception
     */
    @Test
    public void marshallingTest() throws Exception {

        PointType pt = new PointType("p1", new DirectPositionType(-90.080000, 29.982000));
        TileMatrix matrix = new TileMatrix(new CodeType("16d"),
                                              55218.001386,
                                              256,
                                              256,
                                              3,
                                              3);


        StringWriter sw = new StringWriter();
        marshaller.marshal(matrix, sw);
        String result = sw.toString();

        //System.out.println("RESULT:" + result);


        String expResult =
                     "<TileMatrix xmlns=\"http://www.opengis.net/wmts/1.0\" xmlns:ows=\"http://www.opengis.net/ows/1.1\">" + '\n' +
                     "    <ows:Identifier>16d</ows:Identifier>" + '\n' +
                     "    <ScaleDenominator>55218.001386</ScaleDenominator>" + '\n' +
                     "    <TileWidth>256</TileWidth>" + '\n' +
                     "    <TileHeight>256</TileHeight>" + '\n' +
                     "    <MatrixWidth>3</MatrixWidth>" + '\n' +
                     "    <MatrixHeight>3</MatrixHeight>" + '\n' +
                     "</TileMatrix>" + '\n';

        assertXmlEquals(expResult, result, "xmlns:*");
    }
}
