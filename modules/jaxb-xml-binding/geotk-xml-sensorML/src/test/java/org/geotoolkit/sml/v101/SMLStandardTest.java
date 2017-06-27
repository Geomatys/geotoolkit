/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2017, Geomatys
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
package org.geotoolkit.sml.v101;

import java.io.InputStream;
import java.util.Map;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.Unmarshaller;
import org.apache.sis.metadata.KeyNamePolicy;
import org.apache.sis.metadata.ValueExistencePolicy;
import org.geotoolkit.sml.xml.SensorMLMarshallerPool;
import org.geotoolkit.sml.xml.v101.SensorML;
import org.geotoolkit.sml.xml.v101.SensorMLStandard;
import org.junit.*;
import static org.junit.Assert.assertTrue;

/**
 *
 * @author Guilhem Legal (Geomatys)
 */
public class SMLStandardTest extends org.geotoolkit.test.TestBase {

    @Test
    public void systemAsMapTest() throws Exception {
        
        Unmarshaller unmarshaller = SensorMLMarshallerPool.getInstance().acquireUnmarshaller();
        
        InputStream is = SmlXMLBindingTest.class.getResourceAsStream("/org/geotoolkit/sml/system101.xml");
        Object unmarshalled = unmarshaller.unmarshal(is);
        if (unmarshalled instanceof JAXBElement) {
            unmarshalled = ((JAXBElement)unmarshalled).getValue();
        }

        assertTrue(unmarshalled instanceof SensorML);

        SensorML system = (SensorML) unmarshalled;
        Map<String,Object> result = system.asMap();

        result = SensorMLStandard.SYSTEM.asValueMap(system, KeyNamePolicy.UML_IDENTIFIER, ValueExistencePolicy.NON_EMPTY);

        is = SmlXMLBindingTest.class.getResourceAsStream("/org/geotoolkit/sml/component101.xml");

        unmarshalled = unmarshaller.unmarshal(is);
        if (unmarshalled instanceof JAXBElement) {
            unmarshalled = ((JAXBElement)unmarshalled).getValue();
        }

        assertTrue(unmarshalled instanceof SensorML);

        SensorML component = (SensorML) unmarshalled;
        result = component.asMap();

        result = SensorMLStandard.COMPONENT.asValueMap(component, KeyNamePolicy.UML_IDENTIFIER, ValueExistencePolicy.NON_EMPTY);
    }

    
}
