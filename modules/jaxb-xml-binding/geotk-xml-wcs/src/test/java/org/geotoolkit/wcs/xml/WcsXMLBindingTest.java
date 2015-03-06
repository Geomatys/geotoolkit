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

import java.io.IOException;
import java.io.StringReader;
import java.util.Arrays;
import org.geotoolkit.wcs.xml.v100.GetCoverageType;
import java.io.StringWriter;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.parsers.ParserConfigurationException;

//Junit dependencies
import org.geotoolkit.ows.xml.v110.CodeType;
import org.geotoolkit.wcs.xml.v100.OutputType;
import org.apache.sis.xml.MarshallerPool;
import org.junit.*;
import org.xml.sax.SAXException;

import static org.apache.sis.test.Assert.*;
import org.geotoolkit.gml.xml.v321.AssociationRoleType;
import org.geotoolkit.gml.xml.v321.FileType;
import org.geotoolkit.gmlcov.geotiff.xml.v100.CompressionType;
import org.geotoolkit.gmlcov.geotiff.xml.v100.InterleaveType;
import org.geotoolkit.gmlcov.geotiff.xml.v100.ObjectFactory;
import org.geotoolkit.gmlcov.geotiff.xml.v100.ParametersType;
import org.geotoolkit.gmlcov.geotiff.xml.v100.PredictorType;
import org.geotoolkit.gmlcov.xml.v100.AbstractDiscreteCoverageType;
import org.geotoolkit.wcs.xml.v200.CoverageDescriptionType;
import org.geotoolkit.wcs.xml.v200.ExtensionType;


/**
 *
 * @author Guilhem Legal (Geomatys)
 * @module pending
 */
public class WcsXMLBindingTest {

    private static final MarshallerPool pool = WCSMarshallerPool.getInstance();
    private Marshaller   marshaller;
    private Unmarshaller unmarshaller;

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
        if (marshaller != null) {
            pool.recycle(marshaller);
        }
        if (unmarshaller != null) {
            pool.recycle(unmarshaller);
        }
    }

    /**
     * Test simple Record Marshalling.
     *
     * @throws JAXBException
     */
    @Test
    public void marshallingTest111() throws JAXBException, IOException, ParserConfigurationException, SAXException {

        org.geotoolkit.wcs.xml.v111.RangeSubsetType.FieldSubset field = new org.geotoolkit.wcs.xml.v111.RangeSubsetType.FieldSubset("id1", "NEAREST");
        org.geotoolkit.wcs.xml.v111.RangeSubsetType dsub = new org.geotoolkit.wcs.xml.v111.RangeSubsetType(Arrays.asList(field));
        org.geotoolkit.wcs.xml.v111.GetCoverageType getCoverage
                = new org.geotoolkit.wcs.xml.v111.GetCoverageType(new CodeType("source1"), null, dsub, new org.geotoolkit.wcs.xml.v111.OutputType(null, "EPSG:4326"));

        StringWriter sw = new StringWriter();
        marshaller.marshal(getCoverage, sw);

        String result = sw.toString();

        String expResult = "<ns5:GetCoverage version=\"1.1.1\" service=\"WCS\"" +
                                    " xmlns:ns5=\"http://www.opengis.net/wcs/1.1.1\"" +
                                    " xmlns:ows=\"http://www.opengis.net/ows/1.1\">" + '\n'
                         + "    <ows:Identifier>source1</ows:Identifier>" + '\n'
                         + "    <ns5:RangeSubset>" + '\n'
                         + "        <ns5:FieldSubset>" + '\n'
                         + "            <ows:Identifier>id1</ows:Identifier>" + '\n'
                         + "            <ns5:InterpolationType>NEAREST</ns5:InterpolationType>" + '\n'
                         + "        </ns5:FieldSubset>" + '\n'
                         + "    </ns5:RangeSubset>" + '\n'
                         + "    <ns5:Output store=\"false\" format=\"EPSG:4326\"/>" + '\n'
                         + "</ns5:GetCoverage>" + '\n';
        assertXmlEquals(expResult, result, "xmlns:*");

    }

    /**
     * Test simple Record Marshalling.
     *
     * @throws JAXBException
     */
    @Test
    public void marshallingTest100() throws JAXBException, IOException, ParserConfigurationException, SAXException {

        GetCoverageType getCoverage = new GetCoverageType("source1", null, null, "nearest neighbor", new OutputType("image/png", "EPSG:4326"));

        StringWriter sw = new StringWriter();
        marshaller.marshal(getCoverage, sw);

        String result = sw.toString();

        String expResult = "<wcs:GetCoverage version=\"1.0.0\" service=\"WCS\" " +
                                    "xmlns:wcs=\"http://www.opengis.net/wcs\">" + '\n' +
                           "    <wcs:sourceCoverage>source1</wcs:sourceCoverage>" + '\n' +
                           "    <wcs:interpolationMethod>nearest neighbor</wcs:interpolationMethod>" + '\n' +
                           "    <wcs:output>" + '\n' +
                           "        <wcs:crs>EPSG:4326</wcs:crs>" + '\n' +
                           "        <wcs:format>image/png</wcs:format>" + '\n' +
                           "    </wcs:output>" + '\n' +
                           "</wcs:GetCoverage>" + '\n' ;
        assertXmlEquals(expResult, result, "xmlns:*");
    }
    
    @Test
    public void marshallingTest200() throws JAXBException, IOException, ParserConfigurationException, SAXException {

        org.geotoolkit.wcs.xml.v200.GetCoverageType getCoverage = new org.geotoolkit.wcs.xml.v200.GetCoverageType("test", "image/geotiff", null);

        ParametersType param = new ParametersType();
        param.setCompression(CompressionType.NONE);
        param.setInterleave(InterleaveType.PIXEL);
        param.setJpegQuality(10);
        param.setPredictor(PredictorType.NONE);
        param.setTileheight(12);
        param.setTilewidth(15);
        param.setTiling(true);
        ObjectFactory facto = new ObjectFactory();
        getCoverage.setExtension(new ExtensionType(facto.createParameters(param)));
        
        StringWriter sw = new StringWriter();
        marshaller.marshal(getCoverage, sw);

        String result = sw.toString();

        String expResult = "<wcs:GetCoverage version=\"2.0.0\" service=\"WCS\" " +
                           "xmlns:wcs=\"http://www.opengis.net/wcs/2.0\" xmlns:geotiff=\"http://www.opengis.net/gmlcov/geotiff/1.0\">" + '\n' +
                           "    <wcs:Extension>\n" +
                           "        <geotiff:parameters>\n" +
                           "            <geotiff:compression>None</geotiff:compression>\n" +
                           "            <geotiff:jpeg_quality>10</geotiff:jpeg_quality>\n" +
                           "            <geotiff:predictor>None</geotiff:predictor>\n" +
                           "            <geotiff:interleave>Pixel</geotiff:interleave>\n" +
                           "            <geotiff:tiling>true</geotiff:tiling>\n" +
                           "            <geotiff:tileheight>12</geotiff:tileheight>\n" +
                           "            <geotiff:tilewidth>15</geotiff:tilewidth>\n" +
                           "        </geotiff:parameters>\n" +
                           "    </wcs:Extension>\n" +         
                           "    <wcs:CoverageId>test</wcs:CoverageId>" + '\n' +
                           "    <wcs:format>image/geotiff</wcs:format>" + '\n' +
                           "</wcs:GetCoverage>" + '\n' ;
        assertXmlEquals(expResult, result, "xmlns:*");
        
        final org.geotoolkit.wcs.xml.v200.GetCoverageType unmarshalled = (org.geotoolkit.wcs.xml.v200.GetCoverageType)((JAXBElement) unmarshaller.unmarshal(new StringReader(expResult))).getValue();
        assertEquals(getCoverage, unmarshalled);
        
        
        final org.geotoolkit.gml.xml.v321.RangeSetType rangeSet = new org.geotoolkit.gml.xml.v321.RangeSetType();
        final FileType ft = new FileType();
        ft.setMimeType("image/tiff");
        final String ext = ".tif";
        ft.setRangeParameters(new AssociationRoleType("cid:grey" + ext, 
                                                      "http://www.opengis.net/spec/GMLCOV_geotiff-coverages/1.0/conf/geotiff-coverage",
                                                      "fileReference"));
        ft.setFileReference("cid:grey" + ext);
        rangeSet.setFile(ft);
        final AbstractDiscreteCoverageType cov = new AbstractDiscreteCoverageType(new CoverageDescriptionType(), rangeSet);
        final org.geotoolkit.gmlcov.xml.v100.ObjectFactory factory = new org.geotoolkit.gmlcov.xml.v100.ObjectFactory();
        JAXBElement jb = factory.createGridCoverage(cov);
        
        sw = new StringWriter();
        marshaller.marshal(jb, sw);

        result = sw.toString();
        System.out.println(result);
    }
}
