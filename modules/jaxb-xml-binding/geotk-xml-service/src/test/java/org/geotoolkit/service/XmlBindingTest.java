/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2011, Geomatys
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
package org.geotoolkit.service;

import java.io.StringWriter;
import java.io.StringReader;
import java.util.Arrays;
import org.opengis.util.LocalName;
import org.opengis.util.TypeName;
import org.opengis.util.MemberName;
import org.geotoolkit.metadata.iso.DefaultMetadata;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import org.geotoolkit.naming.DefaultNameFactory;
import org.geotoolkit.xml.MarshallerPool;

import org.junit.*;
import static org.junit.Assert.*;
/**
 *
 * @author guilhem
 */
public class XmlBindingTest {
 
    private MarshallerPool pool;
    private Unmarshaller unmarshaller;
    private Marshaller   marshaller;

    @Before
    public void setUp() throws JAXBException {
        pool =   new MarshallerPool(DefaultMetadata.class, org.geotoolkit.service.ServiceIdentificationImpl.class);
        unmarshaller = pool.acquireUnmarshaller();
        marshaller   = pool.acquireMarshaller();
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
     * @throws java.lang.Exception
     */
    @Test
    public void unmarshallingTest() throws JAXBException {
        String xml = 
        "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>" + '\n' +
        "<srv:SV_ServiceIdentification xmlns:gco=\"http://www.isotc211.org/2005/gco\" xmlns:srv=\"http://www.isotc211.org/2005/srv\">" + '\n' +
        "    <srv:serviceType>" + '\n' +
        "        <gco:LocalName>test service Type</gco:LocalName>" + '\n' +
        "    </srv:serviceType>" + '\n' +
        "    <srv:containsOperations>" + '\n' +
        "        <srv:SV_OperationMetadata>" + '\n' +
        "            <srv:parameters>" + '\n' +
        "                <srv:SV_Parameter>" + '\n' +
        "                    <srv:name>" + '\n' +
        "                        <gco:aName>" + '\n' +
        "                            <gco:CharacterString>VERSION</gco:CharacterString>" + '\n' +
        "                        </gco:aName>" + '\n' +
        "                        <gco:attributeType>" + '\n' +
        "                            <gco:TypeName>" + '\n' +
        "                                <gco:aName>" + '\n' +
        "                                   <gco:CharacterString>CharacterString</gco:CharacterString>" + '\n' +
        "                                </gco:aName>" + '\n' +
        "                           </gco:TypeName>" + '\n' +
        "                       </gco:attributeType>" + '\n' +
        "                    </srv:name>" + '\n' +
        "                </srv:SV_Parameter>" + '\n' +
        "            </srv:parameters>" + '\n' +
        "        </srv:SV_OperationMetadata>" + '\n' +
        "    </srv:containsOperations>" + '\n' +
        "</srv:SV_ServiceIdentification>" + '\n';
        
        Object obj = unmarshaller.unmarshal(new StringReader(xml));
        assertTrue(obj instanceof ServiceIdentificationImpl);
        ServiceIdentificationImpl result = (ServiceIdentificationImpl) obj;

        ServiceIdentificationImpl expResult = new ServiceIdentificationImpl();
        
        ParameterImpl params = new ParameterImpl();
        DefaultNameFactory factory = new DefaultNameFactory();
        TypeName tname = factory.createTypeName(null, "CharacterString");
        MemberName name = factory.createMemberName(null, "VERSION", tname);
        params.setName(name);
        
        OperationMetadataImpl meta = new OperationMetadataImpl();
        meta.setParameters(params);
        expResult.setContainsOperations(Arrays.asList(meta));
        
        
        LocalName loc = factory.createLocalName(null, "test service Type");
        expResult.setServiceType(loc);
        
        assertEquals(expResult.getContainsOperations().iterator().next().getParameters(), result.getContainsOperations().iterator().next().getParameters());
        assertEquals(expResult.getContainsOperations().iterator().next().getConnectPoint(), result.getContainsOperations().iterator().next().getConnectPoint());
        assertEquals(expResult.getContainsOperations().iterator().next().getDCP(), result.getContainsOperations().iterator().next().getDCP());
        assertEquals(expResult.getContainsOperations().iterator().next().getDependsOn(), result.getContainsOperations().iterator().next().getDependsOn());
        assertEquals(expResult.getContainsOperations().iterator().next(), result.getContainsOperations().iterator().next());
        assertEquals(expResult.getContainsOperations(), result.getContainsOperations());
        assertEquals(expResult, result);
    }

    /**
     * Test simple Record Marshalling.
     *
     * @throws java.lang.Exception
     */
    @Test
    public void marshallingTest() throws Exception {
        ServiceIdentificationImpl servIdent = new ServiceIdentificationImpl();
        
        ParameterImpl params = new ParameterImpl();
        DefaultNameFactory factory = new DefaultNameFactory();
        TypeName tname = factory.createTypeName(null, "CharacterString");
        MemberName name = factory.createMemberName(null, "VERSION", tname);
        params.setName(name);
        
        OperationMetadataImpl meta = new OperationMetadataImpl();
        meta.setParameters(params);
        servIdent.setContainsOperations(Arrays.asList(meta));
        
        
        LocalName loc = factory.createLocalName(null, "test service Type");
        servIdent.setServiceType(loc);
        
        StringWriter sw = new StringWriter();
        marshaller.marshal(servIdent, sw);
        
        String expResult = 
        "<srv:SV_ServiceIdentification >" + '\n' +
        "    <srv:serviceType>" + '\n' +
        "        <gco:LocalName>test service Type</gco:LocalName>" + '\n' +
        "    </srv:serviceType>" + '\n' +
        "    <srv:containsOperations>" + '\n' +
        "        <srv:SV_OperationMetadata>" + '\n' +
        "            <srv:parameters>" + '\n' +
        "                <srv:SV_Parameter>" + '\n' +
        "                    <srv:name>" + '\n' +
        "                        <gco:aName>" + '\n' +
        "                            <gco:CharacterString>VERSION</gco:CharacterString>" + '\n' +
        "                        </gco:aName>" + '\n' +
        "                        <gco:attributeType>" + '\n' +
        "                            <gco:TypeName>" + '\n' +
        "                                <gco:aName>" + '\n' +
        "                                    <gco:CharacterString>CharacterString</gco:CharacterString>" + '\n' +
        "                                </gco:aName>" + '\n' +
        "                            </gco:TypeName>" + '\n' +
        "                        </gco:attributeType>" + '\n' +
        "                    </srv:name>" + '\n' +
        "                </srv:SV_Parameter>" + '\n' +
        "            </srv:parameters>" + '\n' +
        "        </srv:SV_OperationMetadata>" + '\n' +
        "    </srv:containsOperations>" + '\n' +
        "</srv:SV_ServiceIdentification>" + '\n';
        
        String result = sw.toString();
         //we remove the first line
        result = result.substring(result.indexOf("?>") + 3);
        //we remove the xmlmns
        result = removeXmlns(result);
        
        assertEquals(expResult, result);
    }
    
    /**
     * Remove all the XML namespace declaration.
     * @param xml
     * @return
     */
    public static String removeXmlns(final String xml) {
        String s = xml;
        s = s.replaceAll("xmlns=\"[^\"]*\" ", "");
        s = s.replaceAll("xmlns=\"[^\"]*\"", "");
        s = s.replaceAll("xmlns:[^=]*=\"[^\"]*\" ", "");
        s = s.replaceAll("xmlns:[^=]*=\"[^\"]*\"", "");
        return s;
    }
}
