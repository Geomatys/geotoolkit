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
package org.geotoolkit.process.jts.equalsexact;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LinearRing;
import org.geotoolkit.process.ProcessDescriptor;
import org.geotoolkit.process.ProcessFinder;
import org.geotoolkit.process.jts.AbstractProcessTest;

import org.opengis.parameter.ParameterValueGroup;

import org.junit.Test;
import static org.junit.Assert.*;

/**
 * JUnit test of Equals2d process
 * @author Quentin Boileau
 * @module pending
 */
public class EqualsExactTest extends AbstractProcessTest{

   
    public EqualsExactTest() {
        super("equals2d");
    }

    @Test
    public void testEquals2d() {
        
        GeometryFactory fact = new GeometryFactory();
        
        // Inputs first
        final LinearRing  ring = fact.createLinearRing(new Coordinate[]{
           new Coordinate(2.0, -5.0),
           new Coordinate(0.0, 10.0),
           new Coordinate(3.0, 8.0),
           new Coordinate(5.0, 0.0),
           new Coordinate(2.0, -5.0)
        });
        
        final Geometry geom = fact.createPolygon(ring, null) ;
        final Geometry geom2 = fact.createPolygon(ring, null) ;
        
        final double tolerance = 0.00001;
      
        // Process
        final ProcessDescriptor desc = ProcessFinder.getProcessDescriptor("jts", "equals2d");
        final org.geotoolkit.process.Process proc = desc.createProcess();

        final ParameterValueGroup in = desc.getInputDescriptor().createValue();
        in.parameter("geom1").setValue(geom);
        in.parameter("geom2").setValue(geom);
        in.parameter("tolerance").setValue(tolerance);
        proc.setInput(in);
        proc.run();

        //result
        final Boolean result = (Boolean) proc.getOutput().parameter("result").getValue();
       
        final Boolean expected = geom.equalsExact(geom2, tolerance);
        
        assertTrue(expected.equals(result));
    }
    
}
