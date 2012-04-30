/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2011, Geomatys
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation;
 *    version 2.1 of the License.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */
package org.geotoolkit.wps;

import java.io.StringWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import org.geotoolkit.ows.xml.v110.CodeType;
import org.geotoolkit.wps.v100.DescribeProcess100;
import org.geotoolkit.wps.xml.WPSMarshallerPool;
import org.geotoolkit.wps.xml.v100.DescribeProcess;
import org.junit.Test;
import static org.junit.Assert.*;


/**
 * Testing class for GetCapabilities requests of WPS client, in version 1.0.0.
 *
 * @author Quentin Boileau
 */
public class DescribeProcessTest {
    public DescribeProcessTest() {}

    /**
     * Ensures the {@link DescribeProcess#getURL()} method returns a well-built url,
     * with the parameters given.
     */
    @Test
    public void testDescribeProcess110() {
        List<String> identifiers = new ArrayList<String>();
        identifiers.add("identifier1");
        identifiers.add("identifier2");
        identifiers.add("identifier3");
        
        final DescribeProcess100 desc100 = new DescribeProcess100("http://test.com",null);
        desc100.setIdentifiers(identifiers);
        final URL url;
        try {
            url = desc100.getURL();
        } catch (MalformedURLException ex) {
            fail(ex.getLocalizedMessage());
            return;
        }
        
        final String strUrl = url.toString();
        //final String expectedURL = "http://test.com?VERSION=1.0.0&SERVICE=WPS&REQUEST=DescribeProcess&IDENTIFIER=identifier1,identifier2,identifier3";
        assertTrue(strUrl.contains("VERSION=1.0.0"));
        assertTrue(strUrl.contains("SERVICE=WPS"));
        assertTrue(strUrl.contains("REQUEST=DescribeProcess"));
        assertTrue(strUrl.contains("IDENTIFIER=identifier1,identifier2,identifier3"));
    }
    
   @Test 
   public void testRequestAndMarshall(){
        try {
            final List<String> identifiers = new ArrayList<String>();
            identifiers.add("identifier1");
            identifiers.add("identifier2");
            identifiers.add("identifier3");
            
            final List<CodeType> identifierList = new ArrayList<CodeType>();
            identifierList.add(new CodeType("identifier1"));
            identifierList.add(new CodeType("identifier2"));
            identifierList.add(new CodeType("identifier3"));
            
            final DescribeProcess100 desc100 = new DescribeProcess100("http://test.com",null);
            desc100.setIdentifiers(identifiers);
            final DescribeProcess request = desc100.makeRequest();
            assertEquals("WPS", request.getService());
            assertEquals("1.0.0", request.getVersion());
            assertEquals(request.getIdentifier(),identifierList);
            
            final StringWriter stringWriter = new StringWriter();
            final Marshaller marshaller = WPSMarshallerPool.getInstance().acquireMarshaller();
            marshaller.marshal(request,stringWriter);
            
            final String expectedMarshalledRequest = 
                    "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n"
                    + "<wps:DescribeProcess version=\"1.0.0\" service=\"WPS\" xmlns:wps=\"http://www.opengis.net/wps/1.0.0\" xmlns:ows=\"http://www.opengis.net/ows/1.1\" xmlns:xlink=\"http://www.w3.org/1999/xlink\" xmlns:gml=\"http://www.opengis.net/gml\">\n"
                    + "    <ows:Identifier>identifier1</ows:Identifier>\n"
                    + "    <ows:Identifier>identifier2</ows:Identifier>\n"
                    + "    <ows:Identifier>identifier3</ows:Identifier>\n"
                    + "</wps:DescribeProcess>\n";
            
            assertEquals(expectedMarshalledRequest, stringWriter.toString()); 
        } catch (JAXBException ex) {
            fail(ex.getLocalizedMessage());
            return;
        }
   }
   
}
