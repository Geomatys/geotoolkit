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
package org.geotoolkit.processing.jts.envelope;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LinearRing;
import org.geotoolkit.geometry.jts.JTS;
import org.geotoolkit.process.ProcessDescriptor;
import org.geotoolkit.process.ProcessException;
import org.geotoolkit.process.ProcessFinder;
import org.geotoolkit.processing.jts.AbstractProcessTest;
import static org.junit.Assert.assertTrue;
import org.junit.Test;
import org.opengis.parameter.ParameterValueGroup;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.util.FactoryException;
import org.opengis.util.NoSuchIdentifierException;
import org.apache.sis.referencing.CommonCRS;

/**
 * JUnit test of ConvexHull process
 *
 * @author Quentin Boileau @module pending
 */
public class EnvelopeTest extends AbstractProcessTest {

    public EnvelopeTest() {
        super("envelope");
    }

    @Test
    public void testEnvelope() throws NoSuchIdentifierException, ProcessException, FactoryException {

        GeometryFactory fact = new GeometryFactory();

        // Inputs first
        final LinearRing ring = fact.createLinearRing(new Coordinate[]{
                    new Coordinate(2.0, -5.0),
                    new Coordinate(0.0, 10.0),
                    new Coordinate(3.0, 8.0),
                    new Coordinate(5.0, 0.0),
                    new Coordinate(2.0, -5.0)
                });

        final Geometry geom = fact.createPolygon(ring, null);
        final CoordinateReferenceSystem crs1 = CommonCRS.WGS84.geographic();
        JTS.setCRS(geom, crs1);

        // Process
        final ProcessDescriptor desc = ProcessFinder.getProcessDescriptor("jts", "envelope");

        final ParameterValueGroup in = desc.getInputDescriptor().createValue();
        in.parameter("geom").setValue(geom);
        final org.geotoolkit.process.Process proc = desc.createProcess(in);

        //result
        final Geometry result = (Geometry) proc.call().parameter("result_geom").getValue();

        final Geometry expected = geom.getEnvelope();

        assertTrue(expected.equals(result));
        assertTrue(crs1.equals(JTS.findCoordinateReferenceSystem(result)));
    }
}
