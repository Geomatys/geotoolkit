/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2014, Geomatys
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

package org.geotoolkit.sml.xml;

import java.util.Arrays;
import jakarta.xml.bind.Unmarshaller;
import org.apache.sis.xml.MarshallerPool;
import org.geotoolkit.gml.xml.AbstractGeometry;
import org.geotoolkit.gml.xml.v311.DirectPositionType;
import org.geotoolkit.gml.xml.v311.PointType;
import static org.junit.Assert.assertEquals;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author Guilhem Legal (Geomatys)
 */
public class SensorMLUtilitiesTest {

    private static MarshallerPool marshallerPool;

    @BeforeClass
    public static void setUpClass() throws Exception {
        marshallerPool = SensorMLMarshallerPool.getInstance();
    }
    
    /**
     *
     * @throws java.lang.Exception
     */
    @Test
    public void getPhysicalIDTest() throws Exception {
        Unmarshaller unmarshaller = marshallerPool.acquireUnmarshaller();
        AbstractSensorML sensor = (AbstractSensorML) unmarshaller.unmarshal(SensorMLUtilitiesTest.class.getResourceAsStream("/org/geotoolkit/sml/component.xml"));
        String phyID = SensorMLUtilities.getPhysicalID(sensor).orElse(null);
        assertEquals("00ARGLELES_2000", phyID);

        sensor = (AbstractSensorML) unmarshaller.unmarshal(SensorMLUtilitiesTest.class.getResourceAsStream("/org/geotoolkit/sml/component2.xml"));
        phyID  = SensorMLUtilities.getPhysicalID(sensor).orElse(null);
        assertEquals("00ARGLELES", phyID);

        sensor = (AbstractSensorML) unmarshaller.unmarshal(SensorMLUtilitiesTest.class.getResourceAsStream("/org/geotoolkit/sml/component3.xml"));
        phyID  = SensorMLUtilities.getPhysicalID(sensor).orElse(null);
        assertEquals(null, phyID);

        marshallerPool.recycle(unmarshaller);
    }

    /**
     *
     * @throws java.lang.Exception
     */
    @Test
    public void getSensorPositionTest() throws Exception {
        Unmarshaller unmarshaller = marshallerPool.acquireUnmarshaller();
        AbstractSensorML sensor = (AbstractSensorML) unmarshaller.unmarshal(SensorMLUtilitiesTest.class.getResourceAsStream("/org/geotoolkit/sml/component2.xml"));
        AbstractGeometry result = SensorMLUtilities.getSensorPosition(sensor).orElse(null);
        DirectPositionType posExpResult = new DirectPositionType("urn:ogc:crs:EPSG:27582", 2, Arrays.asList(65400.0,1731368.0));
        PointType expResult = new PointType(posExpResult);

        assertEquals(expResult, result);

        sensor    = (AbstractSensorML) unmarshaller.unmarshal(SensorMLUtilitiesTest.class.getResourceAsStream("/org/geotoolkit/sml/component.xml"));
        result    = SensorMLUtilities.getSensorPosition(sensor).orElse(null);
        expResult = null;

        assertEquals(expResult, result);

        marshallerPool.recycle(unmarshaller);
    }

}
