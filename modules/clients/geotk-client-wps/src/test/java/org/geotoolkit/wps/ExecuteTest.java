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
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import org.geotoolkit.ows.xml.v110.CodeType;
import org.geotoolkit.wps.v100.DescribeProcess100;
import org.geotoolkit.wps.v100.Execute100;
import org.geotoolkit.wps.xml.WPSMarshallerPool;
import org.geotoolkit.wps.xml.v100.DescribeProcess;
import org.geotoolkit.wps.xml.v100.Execute;
import org.junit.Test;
import static org.junit.Assert.*;


/**
 * Testing class for GetCapabilities requests of WPS client, in version 1.0.0.
 *
 * @author Quentin Boileau
 */
public class ExecuteTest {
    public ExecuteTest() {}

   
    @Test 
   public void testRequestAndMarshall(){
        try {
            List<Double> corner = new ArrayList<Double>();
            corner.add(10.0);
            corner.add(10.0);
            
            List<AbstractWPSInput> inputs = new ArrayList<AbstractWPSInput>();
            inputs.add(new WPSInputLiteral("literal", "10"));
            inputs.add(new WPSInputBoundingBox("bbox", corner, corner, "EPSG:4326", 2));
            inputs.add(new WPSInputComplex("complex", null));
            inputs.add(new WPSInputReference("reference", "http://link.to/reference/"));
            
            List<WPSOutput> outputs = new ArrayList<WPSOutput>();
            outputs.add(new WPSOutput("output"));
            
            final Execute100 exec100 = new Execute100("http://test.com",null);
            exec100.setIdentifier("identifier");
            exec100.setInputs(inputs);
            exec100.setOutputs(outputs);
            
            final Execute request = exec100.makeRequest();
            assertEquals("WPS", request.getService());
            assertEquals("1.0.0", request.getVersion());
            assertEquals(request.getIdentifier().getValue(),"identifier");
            
            final StringWriter stringWriter = new StringWriter();
            final Marshaller marshaller = WPSMarshallerPool.getInstance().acquireMarshaller();
            marshaller.marshal(request,stringWriter);
           
            String expected = expectedRequest();
            
            assertEquals(expected, expected);
        } catch (JAXBException ex) {
            fail(ex.getLocalizedMessage());
            return;
        }
   }
    
   private static String expectedRequest(){
       
       String str = 
               "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n"
                +"<wps:Execute version=\"1.0.0\" service=\"WPS\" xmlns:wps=\"http://www.opengis.net/wps/1.0.0\" xmlns:ows=\"http://www.opengis.net/ows/1.1\" xmlns:xlink=\"http://www.w3.org/1999/xlink\" xmlns:gml=\"http://www.opengis.net/gml\">\n"
                +"    <ows:Identifier>identifier</ows:Identifier>\n"
                +"    <wps:DataInputs>\n"
                +"        <wps:Input>\n"
                +"            <ows:Identifier>literal</ows:Identifier>\n"
                +"            <wps:Data>\n"
                +"                <wps:LiteralData>10</wps:LiteralData>\n"
                +"            </wps:Data>\n"
                +"        </wps:Input>\n"
                +"        <wps:Input>\n"
                +"            <ows:Identifier>bbox</ows:Identifier>\n"
                +"            <wps:Data>\n"
                +"                <wps:BoundingBoxData dimensions=\"2\" crs=\"EPSG:4326\">\n"
                +"                    <ows:LowerCorner>10.0 10.0</ows:LowerCorner>\n"
                +"                    <ows:UpperCorner>10.0 10.0</ows:UpperCorner>\n"
                +"                </wps:BoundingBoxData>\n"
                +"            </wps:Data>\n"
                +"        </wps:Input>\n"
                +"        <wps:Input>\n"
                +"            <ows:Identifier>complex</ows:Identifier>\n"
                +"            <wps:Data>\n"
                +"                <wps:ComplexData/>\n"
                +"            </wps:Data>\n"
                +"        </wps:Input>\n"
                +"        <wps:Input>\n"
                +"            <ows:Identifier>reference</ows:Identifier>\n"
                +"            <wps:Reference xlink:href=\"http://link.to/reference/\"/>\n"
                +"        </wps:Input>\n"
                +"    </wps:DataInputs>\n"
                +"    <wps:ResponseForm>\n"
                +"        <wps:ResponseDocument status=\"false\" lineage=\"false\" storeExecuteResponse=\"false\">\n"
                +"            <wps:Output asReference=\"false\">\n"
                +"                <ows:Identifier>output</ows:Identifier>\n"
                +"            </wps:Output>\n"
                +"        </wps:ResponseDocument>\n"
                +"    </wps:ResponseForm>\n"
                +"</wps:Execute>\n";
      
      return str;  
   }
}
