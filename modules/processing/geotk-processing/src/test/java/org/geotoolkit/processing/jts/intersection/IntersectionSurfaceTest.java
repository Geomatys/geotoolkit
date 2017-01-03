/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2012, Geomatys
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
package org.geotoolkit.processing.jts.intersection;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LinearRing;
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
 * JUnit test of intersection surface process
 *
 * @author Quentin Boileau
 * @author Cédric Briançon (Geomatys)
 * @module
 */
public class IntersectionSurfaceTest extends AbstractProcessTest {

    public IntersectionSurfaceTest() {
        super("jts:intersectionSurface");
    }

    @Test
    public void testIntersection() throws NoSuchIdentifierException, ProcessException {

        GeometryFactory fact = new GeometryFactory();

        // Inputs first
        final LinearRing ring = fact.createLinearRing(new Coordinate[]{
                    new Coordinate(0.0, 0.0),
                    new Coordinate(0.0, 10.0),
                    new Coordinate(5.0, 10.0),
                    new Coordinate(5.0, 0.0),
                    new Coordinate(0.0, 0.0)
                });

        final Geometry geom1 = fact.createPolygon(ring, null);


        final LinearRing ring2 = fact.createLinearRing(new Coordinate[]{
                    new Coordinate(-5.0, 0.0),
                    new Coordinate(-5.0, 10.0),
                    new Coordinate(2.0, 10.0),
                    new Coordinate(2.0, 0.0),
                    new Coordinate(-5.0, 0.0)
                });

        final Geometry geom2 = fact.createPolygon(ring2, null);
        // Process
        final ProcessDescriptor desc = ProcessFinder.getProcessDescriptor(GeotkProcessingRegistry.NAME,"jts:intersectionSurface");

        final ParameterValueGroup in = desc.getInputDescriptor().createValue();
        in.parameter("geom1").setValue(geom1);
        in.parameter("geom2").setValue(geom2);
        final org.geotoolkit.process.Process proc = desc.createProcess(in);

        //result
        final Double result = (Double) proc.call().parameter("result_surface").getValue();


        final Geometry expectedGeom = geom1.intersection(geom2);
        final double expected = expectedGeom.getArea();

        assertTrue(expected == result);
    }
}
