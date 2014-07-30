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
import org.opengis.util.LocalName;
import org.opengis.util.TypeName;
import org.opengis.util.MemberName;
import javax.xml.bind.JAXBContext;
import org.apache.sis.metadata.iso.DefaultMetadata;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import org.apache.sis.metadata.iso.service.DefaultOperationMetadata;
import org.apache.sis.util.iso.DefaultNameFactory;
import org.apache.sis.metadata.iso.service.DefaultParameter;
import org.apache.sis.metadata.iso.service.DefaultServiceIdentification;
import org.apache.sis.xml.MarshallerPool;

import org.junit.*;
import static java.util.Collections.singleton;
import static org.apache.sis.test.Assert.*;


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
        pool = new MarshallerPool(JAXBContext.newInstance(DefaultMetadata.class), null);
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
        assertTrue(obj instanceof DefaultServiceIdentification);
        DefaultServiceIdentification result = (DefaultServiceIdentification) obj;

        DefaultServiceIdentification expResult = new DefaultServiceIdentification();

        DefaultParameter params = new DefaultParameter();
        DefaultNameFactory factory = new DefaultNameFactory();
        TypeName tname = factory.createTypeName(null, "CharacterString");
        MemberName name = factory.createMemberName(null, "VERSION", tname);
        params.setName(name);

        DefaultOperationMetadata meta = new DefaultOperationMetadata();
        meta.setParameters(singleton(params));
        expResult.setContainsOperations(singleton(meta));


        LocalName loc = factory.createLocalName(null, "test service Type");
        expResult.setServiceType(loc);

        assertEquals(expResult.getContainsOperations().iterator().next().getParameters(), result.getContainsOperations().iterator().next().getParameters());
        assertEquals(expResult.getContainsOperations().iterator().next().getConnectPoints(), result.getContainsOperations().iterator().next().getConnectPoints());
        assertEquals(expResult.getContainsOperations().iterator().next().getDistributedComputingPlatforms(), result.getContainsOperations().iterator().next().getDistributedComputingPlatforms());
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
        DefaultServiceIdentification servIdent = new DefaultServiceIdentification();

        DefaultParameter params = new DefaultParameter();
        DefaultNameFactory factory = new DefaultNameFactory();
        TypeName tname = factory.createTypeName(null, "CharacterString");
        MemberName name = factory.createMemberName(null, "VERSION", tname);
        params.setName(name);

        DefaultOperationMetadata meta = new DefaultOperationMetadata();
        meta.setParameters(singleton(params));
        servIdent.setContainsOperations(singleton(meta));


        LocalName loc = factory.createLocalName(null, "test service Type");
        servIdent.setServiceType(loc);

        StringWriter sw = new StringWriter();
        marshaller.marshal(servIdent, sw);

        String expResult =
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
        "                                    <gco:CharacterString>CharacterString</gco:CharacterString>" + '\n' +
        "                                </gco:aName>" + '\n' +
        "                            </gco:TypeName>" + '\n' +
        "                        </gco:attributeType>" + '\n' +
        "                    </srv:name>" + '\n' +
        "                    <srv:valueType>" + '\n' +
        "                        <gco:TypeName>" + '\n' +
        "                            <gco:aName>" + '\n' +
        "                                <gco:CharacterString>CharacterString</gco:CharacterString>" + '\n' +
        "                            </gco:aName>" + '\n' +
        "                        </gco:TypeName>" + '\n' +
        "                    </srv:valueType>" + '\n' +
        "                </srv:SV_Parameter>" + '\n' +
        "            </srv:parameters>" + '\n' +
        "        </srv:SV_OperationMetadata>" + '\n' +
        "    </srv:containsOperations>" + '\n' +
        "</srv:SV_ServiceIdentification>" + '\n';

        String result = sw.toString();
         //we remove the first line
        result = result.substring(result.indexOf("?>") + 3);

        assertXmlEquals(expResult, result, "xmlns:*");
    }
}
