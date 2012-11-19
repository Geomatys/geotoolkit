/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2012, Geomatys
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


package org.geotoolkit.gml.v321;


//Junit dependencies
import org.geotoolkit.gml.xml.v321.TimeInstantPropertyType;
import org.geotoolkit.gml.xml.v321.TimeInstantType;
import org.geotoolkit.gml.xml.v321.TimePeriodType;
import org.geotoolkit.gml.xml.v321.TimePositionType;
import org.junit.*;
import static org.junit.Assert.*;

/**
 *
 * @author Guilhem Legal (Geomatys)
 */
public class TimePeriodTypeTest {

    @BeforeClass
    public static void setUpClass() throws Exception {

    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @Test
    public void getTimeTest() throws Exception {
        TimePeriodType tp = new TimePeriodType("2008-11-01T02:00:00", "2008-11-01T02:01:00");
        assertEquals(60000, tp.getTime());
        
        tp = new TimePeriodType("2008-11-01T01:00:00", "2008-11-01T02:00:00");
        assertEquals(3600000, tp.getTime());
        
        tp = new TimePeriodType("2008-11-01T01:00:00", null);
        assertEquals(-1, tp.getTime());
        
        tp = new TimePeriodType(null, "2008-11-01T02:00:00");
        assertEquals(-1, tp.getTime());
        
        String s1 = null;
        tp = new TimePeriodType(s1, null);
        assertEquals(-1, tp.getTime());
        
    }
    
    @Test
    public void getTime2Test() throws Exception {
        TimePositionType tb = new TimePositionType("2008-11-01T02:00:00");
        TimePositionType te = new TimePositionType("2008-11-01T02:01:00");
        TimePeriodType tp = new TimePeriodType(tb, te);
        assertEquals(60000, tp.getTime());
        
        tb = new TimePositionType("2008-11-01T01:00:00");
        te = new TimePositionType("2008-11-01T02:00:00");
        tp = new TimePeriodType(tb, te);
        assertEquals(3600000, tp.getTime());
        
        tp = new TimePeriodType(tb, null);
        assertEquals(-1, tp.getTime());
        
        tb = null;
        tp = new TimePeriodType(tb, te);
        assertEquals(-1, tp.getTime());
        
        tb = null;
        te = null;
        tp = new TimePeriodType(tb, te);
        assertEquals(-1, tp.getTime());
        
    }
    
    @Test
    public void getTime3Test() throws Exception {
        String snull = null;
        TimeInstantType tb = new TimeInstantType(new TimePositionType("2008-11-01T02:00:00"));
        TimeInstantType te = new TimeInstantType(new TimePositionType("2008-11-01T02:01:00"));
        TimePeriodType tp = new TimePeriodType(snull);
        tp.setBegin(new TimeInstantPropertyType(tb));
        tp.setEnd(new TimeInstantPropertyType(te));
        assertEquals(60000, tp.getTime());
        
        tb = new TimeInstantType(new TimePositionType("2008-11-01T01:00:00"));
        te = new TimeInstantType(new TimePositionType("2008-11-01T02:00:00"));
        tp = new TimePeriodType(snull);
        tp.setBegin(new TimeInstantPropertyType(tb));
        tp.setEnd(new TimeInstantPropertyType(te));
        assertEquals(3600000, tp.getTime());
        
        tp = new TimePeriodType(snull);
        tp.setBegin(new TimeInstantPropertyType(tb));
        assertEquals(-1, tp.getTime());
        
        tp = new TimePeriodType(snull);
        tp.setEnd(new TimeInstantPropertyType(te));
        assertEquals(-1, tp.getTime());
        
        tp = new TimePeriodType(snull);
        assertEquals(-1, tp.getTime());
        
    }
}
