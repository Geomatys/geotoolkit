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
package org.geotoolkit.processing.jts.lenght;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.LinearRing;
import org.geotoolkit.process.ProcessDescriptor;
import org.geotoolkit.process.ProcessException;
import org.geotoolkit.process.ProcessFinder;
import org.geotoolkit.processing.GeotkProcessingRegistry;
import org.geotoolkit.processing.AbstractProcessTest;
import static org.junit.Assert.assertTrue;
import org.junit.Test;
import org.opengis.parameter.ParameterValueGroup;
import org.opengis.util.NoSuchIdentifierException;

/**
 * JUnit test of length process
 * @author Quentin Boileau
 * @module
 */
public class LenghtTest extends AbstractProcessTest {


    public LenghtTest() {
        super("jts:lenght");
    }

    @Test
    public void testLenght() throws NoSuchIdentifierException, ProcessException {

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

        // Process
        final ProcessDescriptor desc = ProcessFinder.getProcessDescriptor(GeotkProcessingRegistry.NAME,"jts:lenght");

        final ParameterValueGroup in = desc.getInputDescriptor().createValue();
        in.parameter("geom").setValue(geom1);
        final org.geotoolkit.process.Process proc = desc.createProcess(in);

        //result
        final Double result = (Double) proc.call().parameter("result").getValue();

        final Double expected = geom1.getLength();

        assertTrue(expected.equals(result));
    }

}
