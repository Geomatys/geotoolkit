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
package org.geotoolkit.process.jts.union;

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
 * JUnit test of Union process
 * @author Quentin Boileau
 * @module pending
 */
public class UnionTest extends AbstractProcessTest{

   
    public UnionTest() {
        super("union");
    }

    @Test
    public void testUnion() {
        
        GeometryFactory fact = new GeometryFactory();
        
        // Inputs first
        final LinearRing  ring = fact.createLinearRing(new Coordinate[]{
           new Coordinate(0.0, 0.0),
           new Coordinate(0.0, 10.0),
           new Coordinate(5.0, 10.0),
           new Coordinate(5.0, 0.0),
           new Coordinate(0.0, 0.0)
        });
        
        final Geometry geom1 = fact.createPolygon(ring, null) ;
        
        
        final LinearRing  ring2 = fact.createLinearRing(new Coordinate[]{
           new Coordinate(-5.0, 0.0),
           new Coordinate(-5.0, 10.0),
           new Coordinate(2.0, 10.0),
           new Coordinate(2.0, 0.0),
           new Coordinate(-5.0, 0.0)
        });
      
        final Geometry geom2 = fact.createPolygon(ring2, null) ;
        // Process
        final ProcessDescriptor desc = ProcessFinder.getProcessDescriptor("jts", "union");
        final org.geotoolkit.process.Process proc = desc.createProcess();

        final ParameterValueGroup in = desc.getInputDescriptor().createValue();
        in.parameter("geom1").setValue(geom1);
        in.parameter("geom2").setValue(geom2);
        proc.setInput(in);
        proc.run();

        //result
        final Geometry result = (Geometry) proc.getOutput().parameter("result_geom").getValue();
       
        
        final Geometry expected = geom1.union(geom2);
        
        assertTrue(expected.equals(result));
    }
    
}
