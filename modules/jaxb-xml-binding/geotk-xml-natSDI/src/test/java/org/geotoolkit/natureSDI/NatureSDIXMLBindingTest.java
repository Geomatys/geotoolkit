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
package org.geotoolkit.natureSDI;

import java.io.StringReader;
import java.io.StringWriter;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

//Junit dependencies
import javax.xml.bind.Unmarshaller;
import org.geotoolkit.naturesdi.NATSDI_DataIdentification;
import org.geotoolkit.naturesdi.NATSDI_SpeciesInformation;
import org.geotoolkit.xml.MarshallerPool;
import org.junit.*;
import static org.junit.Assert.*;

/**
 *
 * @author Guilhem Legal (Geomatys)
 * @module pending
 */
public class NatureSDIXMLBindingTest {

    private MarshallerPool pool;

    @Before
    public void setUp() throws JAXBException {
        pool = new MarshallerPool(NATSDI_DataIdentification.class);
        
    }

    @After
    public void tearDown() {
        
    }

    /**
     * Test simple Record Marshalling.
     *
     * @throws JAXBException
     */
    @Ignore
    public void marshallingTest() throws JAXBException {

        /*Marshaller marshaller = pool.acquireMarshaller();
        NATSDI_DataIdentification identInfo = new NATSDI_DataIdentification();

        Map<String, Object> metamap = identInfo.asMap();


        NATSDI_SpeciesInformation inf = new NATSDI_SpeciesInformation();

        inf.setSpeciesVernacularName("poissons");

        final List<NATSDI_SpeciesInformation> infs = Collections.singletonList(inf);
        metamap.put("speciesInformation", infs);
        
        identInfo.setSpeciesInformation(infs);

        StringWriter sw = new StringWriter();
        marshaller.marshal(identInfo, sw);

        String result = sw.toString();

        System.out.println(result);
        
        we remove the first line
        result = result.substring(result.indexOf("?>") + 3);
        //we remove the xmlmns
        result = result.replace(" xmlns:gml=\"http://www.opengis.net/gml\"", "");
        result = result.replace(" xmlns:xlink=\"http://www.w3.org/1999/xlink\"", "");
       

        String expResult = "<gml:Envelope srsName=\"urn:ogc:def:crs:EPSG:6.8:4283\" gml:id=\"bound-1\">" + '\n' +
                           "    <gml:lowerCorner>-30.711 134.196</gml:lowerCorner>" + '\n' +
                           "    <gml:upperCorner>-30.702 134.205</gml:upperCorner>" + '\n' +
                           "</gml:Envelope>" + '\n' ;
        assertEquals(expResult, result);

        pool.release(marshaller);*/

    }

    /**
     * Test simple Record Marshalling.
     *
     * @throws JAXBException
     */
    @Test
    public void unmarshallingTest() throws JAXBException {

        Unmarshaller unmarshaller = pool.acquireUnmarshaller();
        String xml = 
        "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>" + '\n' +
        "<ns6:NATSDI_DataIdentification xmlns:gco=\"http://www.isotc211.org/2005/gco\" xmlns:ns6=\"http://www.mdweb-project.org/files/xsd/\" >" + '\n' +
        "<ns6:speciesInformation>" + '\n' +
        "    <ns6:speciesVernacularName>" + '\n' +
        "        <gco:CharacterString>poissons</gco:CharacterString>" + '\n' +
        "    </ns6:speciesVernacularName>" + '\n' +
        "</ns6:speciesInformation>" + '\n' +
        "</ns6:NATSDI_DataIdentification>";

        Object obj = unmarshaller.unmarshal(new StringReader(xml));

        //System.out.println(obj);
        pool.release(unmarshaller);
    }
}
