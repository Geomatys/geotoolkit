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
import org.geotoolkit.util.logging.Logging;
import org.geotoolkit.xml.MarshallerPool;

//Junit dependencies
import org.junit.*;
import static org.junit.Assert.*;


/**
 * A Test suite verifying that the Record are correctly marshalled/unmarshalled
 *
 * @author Guilhem Legal (Geomatys)
 * @module pending
 */
public class FilterXMLBindingTest {

    private static final Logger LOGGER = Logging.getLogger("org.geotoolkit.filter");

    private static final MarshallerPool pool = FilterMarshallerPool.getInstance();;
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
    public void filterMarshalingTest() throws JAXBException {

        /*
         * Test marshalling spatial filter
         */
        DirectPositionType lowerCorner = new DirectPositionType(10.0, 11.0);
        DirectPositionType upperCorner = new DirectPositionType(10.0, 11.0);
        EnvelopeType envelope         = new EnvelopeType("env-id", lowerCorner, upperCorner, "EPSG:4326");

        OverlapsType filterElement     = new OverlapsType(new PropertyNameType("boundingBox"), envelope);
        FilterType filter              = new FilterType(filterElement);

        StringWriter sw = new StringWriter();
        marshaller.marshal(filter, sw);

        String result = sw.toString();
        //we remove the xmlmns
        result = result.replace(" xmlns:xlink=\"http://www.w3.org/1999/xlink\"", "");
        result = result.replace(" xmlns:gml=\"http://www.opengis.net/gml\"", "");
        result = result.replace(" xmlns:ogc=\"http://www.opengis.net/ogc\"", "");

        String expResult =
        "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>"     + '\n' +
        "<ogc:Filter>"                                                      + '\n' +
        "    <ogc:Overlaps>"                                                + '\n' +
        "        <ogc:PropertyName>boundingBox</ogc:PropertyName>"          + '\n' +
        "        <gml:Envelope srsName=\"EPSG:4326\" gml:id=\"env-id\">"    + '\n' +
        "            <gml:lowerCorner>10.0 11.0</gml:lowerCorner>"          + '\n' +
        "            <gml:upperCorner>10.0 11.0</gml:upperCorner>"          + '\n' +
        "        </gml:Envelope>"                                           + '\n' +
        "    </ogc:Overlaps>"                                               + '\n' +
        "</ogc:Filter>"                                                     + '\n';

        LOGGER.log(Level.FINER, "result: {0}", result);
        LOGGER.log(Level.FINER, "expected: {0}", expResult);
        assertEquals(expResult, result);

        
        
        ObjectFactory factory = new ObjectFactory();
        
        
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
        "        <gml:Envelope srsName=\"EPSG:4326\" gml:id=\"env-id\">"                                                                                                 + '\n' +
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
        EnvelopeType envelope         = new EnvelopeType("env-id", lowerCorner, upperCorner, "EPSG:4326");

        OverlapsType filterElement     = new OverlapsType(new PropertyNameType("boundingBox"), envelope);
        FilterType expResult           = new FilterType(filterElement);


        assertEquals(expResult.getSpatialOps().getValue(), result.getSpatialOps().getValue());
        assertEquals(expResult, result);

    }
}

