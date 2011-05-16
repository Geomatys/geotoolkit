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
        
        marshaller.marshal(servIdent, System.out);
    }
}
