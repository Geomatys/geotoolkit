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
package org.geotoolkit.ogc.xml;

// J2SE dependencies
import java.io.IOException;
import org.opengis.filter.sort.SortOrder;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;

// JAXB dependencies
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.parsers.ParserConfigurationException;

// Geotoolkit dependencies
import org.geotoolkit.gml.xml.v311.DirectPositionType;
import org.geotoolkit.gml.xml.v311.EnvelopeType;
import org.geotoolkit.ogc.xml.v110.FilterType;
import org.geotoolkit.ogc.xml.v110.ObjectFactory;
import org.geotoolkit.ogc.xml.v110.OverlapsType;
import org.geotoolkit.ogc.xml.v110.PropertyNameType;
import org.geotoolkit.ogc.xml.v110.SortByType;
import org.geotoolkit.ogc.xml.v110.SortOrderType;
import org.geotoolkit.ogc.xml.v110.SortPropertyType;
import org.geotoolkit.ogc.xml.v200.BBOXType;
import org.geotoolkit.ogc.xml.v200.ContainsType;
import org.geotoolkit.ogc.xml.v200.TimeAfterType;
import org.geotoolkit.ogc.xml.v200.LowerBoundaryType;
import org.geotoolkit.ogc.xml.v200.PropertyIsBetweenType;
import org.geotoolkit.ogc.xml.v200.UpperBoundaryType;
import org.geotoolkit.ogc.xml.v200.LiteralType;
import org.geotoolkit.gml.xml.v321.TimeInstantType;
import org.apache.sis.util.logging.Logging;
import org.apache.sis.xml.MarshallerPool;

//Junit dependencies
import org.junit.*;
import org.xml.sax.SAXException;

import static org.apache.sis.test.Assert.*;


/**
 * A Test suite verifying that the Record are correctly marshalled/unmarshalled
 *
 * @author Guilhem Legal (Geomatys)
 * @module
 */
public class FilterXMLBindingTest extends org.geotoolkit.test.TestBase {

    private static final Logger LOGGER = Logging.getLogger("org.geotoolkit.filter");

    private static final MarshallerPool pool = FilterMarshallerPool.getInstance();
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
    public void filterMarshalingTest() throws JAXBException, IOException, ParserConfigurationException, SAXException {

        /*
         * Test marshalling spatial filter
         */
        DirectPositionType lowerCorner = new DirectPositionType(10.0, 11.0);
        DirectPositionType upperCorner = new DirectPositionType(10.0, 11.0);
        EnvelopeType envelope         = new EnvelopeType(lowerCorner, upperCorner, "EPSG:4326");

        OverlapsType filterElement     = new OverlapsType(new PropertyNameType("boundingBox"), envelope);
        FilterType filter              = new FilterType(filterElement);

        StringWriter sw = new StringWriter();
        marshaller.marshal(filter, sw);

        String result = sw.toString();

        String expResult =
        "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>"     + '\n' +
        "<ogc:Filter xmlns:ogc=\"http://www.opengis.net/ogc\" xmlns:gml=\"http://www.opengis.net/gml\">" + '\n' +
        "    <ogc:Overlaps>"                                                + '\n' +
        "        <ogc:PropertyName>boundingBox</ogc:PropertyName>"          + '\n' +
        "        <gml:Envelope srsName=\"EPSG:4326\">"                      + '\n' +
        "            <gml:lowerCorner>10.0 11.0</gml:lowerCorner>"          + '\n' +
        "            <gml:upperCorner>10.0 11.0</gml:upperCorner>"          + '\n' +
        "        </gml:Envelope>"                                           + '\n' +
        "    </ogc:Overlaps>"                                               + '\n' +
        "</ogc:Filter>"                                                     + '\n';

        LOGGER.log(Level.FINER, "result: {0}", result);
        LOGGER.log(Level.FINER, "expected: {0}", expResult);
        assertXmlEquals(expResult, result, "xmlns:*");



        ObjectFactory factory = new ObjectFactory();

        final BBOXType bbox = new BBOXType("propName", envelope);

        org.geotoolkit.ogc.xml.v200.FilterType filter2   = new org.geotoolkit.ogc.xml.v200.FilterType(bbox);

        sw = new StringWriter();
        marshaller.marshal(filter2, sw);
        result = sw.toString();

        expResult =
                "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n" +
                "<fes:Filter xmlns:fes=\"http://www.opengis.net/fes/2.0\" xmlns:ns8=\"http://www.opengis.net/gml\">\n" +
                "  <fes:BBOX>\n" +
                "    <fes:ValueReference>propName</fes:ValueReference>\n" +
                "    <ns8:Envelope srsName=\"EPSG:4326\">\n" +
                "      <ns8:lowerCorner>10.0 11.0</ns8:lowerCorner>\n" +
                "      <ns8:upperCorner>10.0 11.0</ns8:upperCorner>\n" +
                "    </ns8:Envelope>\n" +
                "  </fes:BBOX>\n" +
                "</fes:Filter>";

        assertXmlEquals(expResult, result, "xmlns:*");

        /*--------------------------------------------*/
        /*- --------------- DEBUG --------------------*/
        /*--------------------------------------------*/

        String[] arr = new String[2];
        arr[0] = "boby";
        arr[1] = "DESC";

        SortPropertyType sp = new SortPropertyType(arr[0], SortOrderType.valueOf(arr[1]));
        SortByType sort = new SortByType(Arrays.asList(sp));

        JAXBElement<SortByType> jbSort = factory.createSortBy(sort);

        //marshaller.marshal(jbSort, System.out);

        sp = new SortPropertyType(arr[0], SortOrder.valueOf(arr[1]));
        sort = new SortByType(Arrays.asList(sp));

        jbSort = factory.createSortBy(sort);

        //marshaller.marshal(jbSort, System.out);

        BBOXType filterBox = new BBOXType("boundingBox", "$test");
        org.geotoolkit.ogc.xml.v200.FilterType filter3 = new org.geotoolkit.ogc.xml.v200.FilterType(filterBox);
        sw = new StringWriter();
        marshaller.marshal(filter3, sw);
        result = sw.toString();

        expResult =
                "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n" +
                "<fes:Filter xmlns:fes=\"http://www.opengis.net/fes/2.0\">\n" +
                "  <fes:BBOX>\n" +
                "    <fes:ValueReference>boundingBox</fes:ValueReference>$test</fes:BBOX>\n" +
                "</fes:Filter>";

        assertXmlEquals(expResult, result, "xmlns:*");


        TimeAfterType filterAfter = new TimeAfterType("boundingBox", "$test");
        org.geotoolkit.ogc.xml.v200.FilterType filter4 = new org.geotoolkit.ogc.xml.v200.FilterType(filterAfter);

        sw = new StringWriter();
        marshaller.marshal(filter4, sw);
        result = sw.toString();

        expResult =
                "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n" +
                "<fes:Filter xmlns:fes=\"http://www.opengis.net/fes/2.0\">\n" +
                "  <fes:After>\n" +
                "    <fes:ValueReference>boundingBox</fes:ValueReference>$test</fes:After>\n" +
                "</fes:Filter>";

        assertXmlEquals(expResult, result, "xmlns:*");


        final org.geotoolkit.gml.xml.v321.ObjectFactory gmlFactory = new org.geotoolkit.gml.xml.v321.ObjectFactory();
        final org.geotoolkit.ogc.xml.v200.ObjectFactory fesFactory = new org.geotoolkit.ogc.xml.v200.ObjectFactory();
        PropertyIsBetweenType pes = new PropertyIsBetweenType();
        pes.setExpression(fesFactory.createValueReference((String)"prop"));
        final LowerBoundaryType lower = new LowerBoundaryType();
        final TimeInstantType ti = new TimeInstantType("2002");
        final LiteralType lowlit = new LiteralType(gmlFactory.createTimeInstant(ti));
        lower.setExpression(fesFactory.createLiteral(lowlit));
        pes.setLowerBoundary(lower);
        final UpperBoundaryType upper = new UpperBoundaryType();
        final TimeInstantType ti2 = new TimeInstantType("2004");
        final LiteralType upplit = new LiteralType(gmlFactory.createTimeInstant(ti2));
        upper.setExpression(fesFactory.createLiteral(upplit));
        pes.setUpperBoundary(upper);
        org.geotoolkit.ogc.xml.v200.FilterType filter5 = new org.geotoolkit.ogc.xml.v200.FilterType(pes);

        sw = new StringWriter();
        marshaller.marshal(filter5, sw);
        result = sw.toString();

        expResult =
                "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n" +
                "<fes:Filter xmlns:gml=\"http://www.opengis.net/gml/3.2\" xmlns:fes=\"http://www.opengis.net/fes/2.0\">\n" +
                "  <fes:PropertyIsBetween>\n" +
                "    <fes:ValueReference>prop</fes:ValueReference>\n" +
                "    <fes:LowerBoundary>\n" +
                "      <fes:Literal>\n" +
                "        <gml:TimeInstant>\n" +
                "          <gml:timePosition>2002</gml:timePosition>\n" +
                "        </gml:TimeInstant>\n" +
                "      </fes:Literal>\n" +
                "    </fes:LowerBoundary>\n" +
                "    <fes:UpperBoundary>\n" +
                "      <fes:Literal>\n" +
                "        <gml:TimeInstant>\n" +
                "          <gml:timePosition>2004</gml:timePosition>\n" +
                "        </gml:TimeInstant>\n" +
                "      </fes:Literal>\n" +
                "    </fes:UpperBoundary>\n" +
                "  </fes:PropertyIsBetween>\n" +
                "</fes:Filter>";

        assertXmlEquals(expResult, result, "xmlns:*");
    }

    /**
     * Test simple Record Marshalling.
     *
     * @throws JAXBException
     */
    @Test
    public void filterUnmarshalingTest() throws JAXBException {

        /*
         * Test Unmarshalling spatial filter.
         */

        String xml =
       "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>" + '\n' +
        "<ogc:Filter xmlns:ogc=\"http://www.opengis.net/ogc\" xmlns:xlink=\"http://www.w3.org/1999/xlink\" xmlns:gml=\"http://www.opengis.net/gml\">" + '\n' +
        "    <ogc:Overlaps>"                                                                                                                           + '\n' +
        "        <ogc:PropertyName>boundingBox</ogc:PropertyName>"                                                                                     + '\n' +
        "        <gml:Envelope srsName=\"EPSG:4326\">"                                                                                                 + '\n' +
        "            <gml:lowerCorner>10.0 11.0</gml:lowerCorner>"                                                                                     + '\n' +
        "            <gml:upperCorner>10.0 11.0</gml:upperCorner>"                                                                                     + '\n' +
        "        </gml:Envelope>"                                                                                                                      + '\n' +
        "    </ogc:Overlaps>"                                                                                                                          + '\n' +
        "</ogc:Filter>" + '\n';

        StringReader sr = new StringReader(xml);

        JAXBElement jb =  (JAXBElement) unmarshaller.unmarshal(sr);
        FilterType result = (FilterType) jb.getValue();

        DirectPositionType lowerCorner = new DirectPositionType(10.0, 11.0);
        DirectPositionType upperCorner = new DirectPositionType(10.0, 11.0);
        EnvelopeType envelope         = new EnvelopeType(lowerCorner, upperCorner, "EPSG:4326");

        OverlapsType filterElement     = new OverlapsType(new PropertyNameType("boundingBox"), envelope);
        FilterType expResult           = new FilterType(filterElement);


        assertEquals(expResult.getSpatialOps().getValue(), result.getSpatialOps().getValue());
        assertEquals(expResult, result);

        xml =
       "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>" + '\n' +
        "<ogc:Filter xmlns:ogc=\"http://www.opengis.net/fes/2.0\" xmlns:gml=\"http://www.opengis.net/gml\">"  + '\n' +
        "    <ogc:BBOX>"                                                                                                                               + '\n' +
        "        <ogc:ValueReference>boundingBox</ogc:ValueReference>"                                                                                 + '\n' +
        "        <gml:Envelope srsName=\"EPSG:4326\">"                                                                                                 + '\n' +
        "            <gml:lowerCorner>10.0 11.0</gml:lowerCorner>"                                                                                     + '\n' +
        "            <gml:upperCorner>10.0 11.0</gml:upperCorner>"                                                                                     + '\n' +
        "        </gml:Envelope>"                                                                                                                      + '\n' +
        "    </ogc:BBOX>"                                                                                                                              + '\n' +
        "</ogc:Filter>" + '\n';

         sr = new StringReader(xml);

        jb =  (JAXBElement) unmarshaller.unmarshal(sr);
        org.geotoolkit.ogc.xml.v200.FilterType result2 = (org.geotoolkit.ogc.xml.v200.FilterType) jb.getValue();
        final org.geotoolkit.gml.xml.v311.ObjectFactory gmlFactory = new org.geotoolkit.gml.xml.v311.ObjectFactory();
        BBOXType filterBox = new BBOXType("boundingBox", gmlFactory.createEnvelope(envelope));
        org.geotoolkit.ogc.xml.v200.FilterType expResult2 = new org.geotoolkit.ogc.xml.v200.FilterType(filterBox);


        assertEquals(((JAXBElement)((BBOXType)expResult2.getSpatialOps().getValue()).getAny()).getValue(), ((JAXBElement)((BBOXType)result2.getSpatialOps().getValue()).getAny()).getValue());
        assertEquals(expResult2.getSpatialOps().getValue(), result2.getSpatialOps().getValue());
        assertEquals(expResult2, result2);

         xml =
       "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>" + '\n' +
        "<ogc:Filter xmlns:ogc=\"http://www.opengis.net/fes/2.0\">"  + '\n' +
        "    <ogc:BBOX>"                                                                                                                               + '\n' +
        "        <ogc:ValueReference>boundingBox</ogc:ValueReference>"                                                                                     + '\n' +
        "        $test"                                                                                                                                + '\n' +
        "    </ogc:BBOX>"                                                                                                                              + '\n' +
        "</ogc:Filter>" + '\n';

         sr = new StringReader(xml);

        jb =  (JAXBElement) unmarshaller.unmarshal(sr);
        result2 = (org.geotoolkit.ogc.xml.v200.FilterType) jb.getValue();

        filterBox = new BBOXType("boundingBox", "$test");
        expResult2 = new org.geotoolkit.ogc.xml.v200.FilterType(filterBox);


        assertEquals(expResult2.getSpatialOps().getValue(), result2.getSpatialOps().getValue());
        assertEquals(expResult2, result2);

        xml =
       "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>" + '\n' +
        "<ogc:Filter xmlns:ogc=\"http://www.opengis.net/fes/2.0\">"  + '\n' +
        "    <ogc:Contains>"                                                                                                                               + '\n' +
        "        <ogc:ValueReference>boundingBox</ogc:ValueReference>"                                                                                     + '\n' +
        "        $test"                                                                                                                                + '\n' +
        "    </ogc:Contains>"                                                                                                                              + '\n' +
        "</ogc:Filter>" + '\n';

         sr = new StringReader(xml);

        jb =  (JAXBElement) unmarshaller.unmarshal(sr);
        result2 = (org.geotoolkit.ogc.xml.v200.FilterType) jb.getValue();

        ContainsType filterContains = new ContainsType("boundingBox", "$test");
        expResult2 = new org.geotoolkit.ogc.xml.v200.FilterType(filterContains);


        assertEquals(expResult2.getSpatialOps().getValue(), result2.getSpatialOps().getValue());
        assertEquals(expResult2, result2);

        xml =
       "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>" + '\n' +
        "<ogc:Filter xmlns:ogc=\"http://www.opengis.net/fes/2.0\">"  + '\n' +
        "    <ogc:After>"                                                                                                                               + '\n' +
        "        <ogc:ValueReference>boundingBox</ogc:ValueReference>"                                                                                     + '\n' +
        "        $test"                                                                                                                                + '\n' +
        "    </ogc:After>"                                                                                                                              + '\n' +
        "</ogc:Filter>" + '\n';

         sr = new StringReader(xml);

        jb =  (JAXBElement) unmarshaller.unmarshal(sr);
        result2 = (org.geotoolkit.ogc.xml.v200.FilterType) jb.getValue();

        TimeAfterType filterAfter = new TimeAfterType("boundingBox", "$test");
        expResult2 = new org.geotoolkit.ogc.xml.v200.FilterType(filterAfter);


        assertEquals(expResult2.getTemporalOps().getValue(), result2.getTemporalOps().getValue());
        assertEquals(expResult2, result2);


        xml =
            "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n" +
            "<fes:Filter xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:gco=\"http://www.isotc211.org/2005/gco\" xmlns:gmx=\"http://www.isotc211.org/2005/gmx\" xmlns:gmi=\"http://www.isotc211.org/2005/gmi\" xmlns:gmd=\"http://www.isotc211.org/2005/gmd\" xmlns:gml=\"http://www.opengis.net/gml/3.2\" xmlns:ogc=\"http://www.opengis.net/ogc\" xmlns:ns8=\"http://www.opengis.net/gml\" xmlns:xlink=\"http://www.w3.org/1999/xlink\" xmlns:fes=\"http://www.opengis.net/fes/2.0\" xmlns:ows=\"http://www.opengis.net/ows/1.1\">\n" +
            "  <fes:PropertyIsBetween>\n" +
            "    <fes:ValueReference>prop</fes:ValueReference>\n" +
            "    <fes:LowerBoundary>\n" +
            "      <fes:Literal><gml:TimeInstant>\n" +
            "          <gml:timePosition>2002</gml:timePosition>\n" +
            "        </gml:TimeInstant></fes:Literal>\n" +
            "    </fes:LowerBoundary>\n" +
            "    <fes:UpperBoundary>\n" +
            "      <fes:Literal><gml:TimeInstant>\n" +
            "          <gml:timePosition>2004</gml:timePosition>\n" +
            "        </gml:TimeInstant></fes:Literal>\n" +
            "    </fes:UpperBoundary>\n" +
            "  </fes:PropertyIsBetween>\n" +
            "</fes:Filter>";

        sr = new StringReader(xml);

        jb =  (JAXBElement) unmarshaller.unmarshal(sr);
        org.geotoolkit.ogc.xml.v200.FilterType result3 = (org.geotoolkit.ogc.xml.v200.FilterType) jb.getValue();

        final org.geotoolkit.gml.xml.v321.ObjectFactory gml32Factory = new org.geotoolkit.gml.xml.v321.ObjectFactory();
        final org.geotoolkit.ogc.xml.v200.ObjectFactory fesFactory = new org.geotoolkit.ogc.xml.v200.ObjectFactory();
        PropertyIsBetweenType expPes = new PropertyIsBetweenType();
        expPes.setExpression(fesFactory.createValueReference((String)"prop"));
        final LowerBoundaryType lower = new LowerBoundaryType();
        final TimeInstantType ti = new TimeInstantType("2002");
        final LiteralType lowlit = new LiteralType(gml32Factory.createTimeInstant(ti));
        lower.setExpression(fesFactory.createLiteral(lowlit));
        expPes.setLowerBoundary(lower);
        final UpperBoundaryType upper = new UpperBoundaryType();
        final TimeInstantType ti2 = new TimeInstantType("2004");
        final LiteralType upplit = new LiteralType(gml32Factory.createTimeInstant(ti2));
        upper.setExpression(fesFactory.createLiteral(upplit));
        expPes.setUpperBoundary(upper);
        org.geotoolkit.ogc.xml.v200.FilterType expResult3 = new org.geotoolkit.ogc.xml.v200.FilterType(expPes);


        assertTrue(result3.getComparisonOps().getValue() instanceof PropertyIsBetweenType);
        PropertyIsBetweenType resPes = (PropertyIsBetweenType) result3.getComparisonOps().getValue();

        assertEquals(expPes.getUpperBoundary(), resPes.getUpperBoundary());
        assertEquals(expPes.getLowerBoundary(), resPes.getLowerBoundary());
        assertEquals(expPes, resPes);
        assertEquals(expResult3.getComparisonOps().getValue(), result3.getComparisonOps().getValue());
        assertEquals(expResult3, result3);
    }
}

