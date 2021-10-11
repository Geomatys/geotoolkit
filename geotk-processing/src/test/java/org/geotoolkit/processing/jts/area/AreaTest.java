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
package org.geotoolkit.processing.jts.area;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.LinearRing;
import org.geotoolkit.process.ProcessDescriptor;
import org.geotoolkit.process.ProcessException;
import org.geotoolkit.process.ProcessFinder;
import org.geotoolkit.processing.GeotkProcessingRegistry;
import org.geotoolkit.processing.AbstractProcessTest;

import org.opengis.parameter.ParameterValueGroup;

import org.junit.Test;
import static org.junit.Assert.*;
import org.opengis.util.NoSuchIdentifierException;

/**
 * JUnit test of Area process
 * @author Quentin Boileau
 * @module
 */
public class AreaTest extends AbstractProcessTest {


    public AreaTest() {
        super("jts:area");
    }

    @Test
    public void testArea() throws NoSuchIdentifierException, ProcessException {

        GeometryFactory fact = new GeometryFactory();

        // Inputs first
        final LinearRing  ring = fact.createLinearRing(new Coordinate[]{
           new Coordinate(0.0, 0.0),
           new Coordinate(0.0, 10.0),
           new Coordinate(5.0, 10.0),
           new Coordinate(5.0, 0.0),
           new Coordinate(0.0, 0.0)
        });

        final Geometry geom = fact.createPolygon(ring, null) ;



        // Process
        final ProcessDescriptor desc = ProcessFinder.getProcessDescriptor(GeotkProcessingRegistry.NAME, "jts:area");

        final ParameterValueGroup in = desc.getInputDescriptor().createValue();
        in.parameter("geom").setValue(geom);
        final org.geotoolkit.process.Process proc = desc.createProcess(in);

        //result
        final Double result = (Double) proc.call().parameter("result").getValue();


        final Double expected = (Double) geom.getArea();

        assertTrue(expected.equals(result));
    }

}
