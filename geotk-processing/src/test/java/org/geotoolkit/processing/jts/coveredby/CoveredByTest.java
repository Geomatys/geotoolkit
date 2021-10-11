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
package org.geotoolkit.processing.jts.coveredby;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.LinearRing;
import org.geotoolkit.geometry.jts.JTS;
import org.geotoolkit.process.ProcessDescriptor;
import org.geotoolkit.process.ProcessException;
import org.geotoolkit.process.ProcessFinder;
import org.geotoolkit.processing.AbstractProcessTest;
import org.apache.sis.referencing.CRS;
import static org.junit.Assert.assertTrue;
import org.junit.Test;
import org.opengis.parameter.ParameterValueGroup;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.MathTransform;
import org.opengis.referencing.operation.TransformException;
import org.opengis.util.FactoryException;
import org.opengis.util.NoSuchIdentifierException;
import org.apache.sis.referencing.CommonCRS;
import org.geotoolkit.processing.GeotkProcessingRegistry;

/**
 * JUnit test of CoveredBy process
 * @author Quentin Boileau
 * @module
 */
public class CoveredByTest extends AbstractProcessTest {


    public CoveredByTest() {
        super("jts:coveredBy");
    }

    @Test
    public void testCoveredBy() throws NoSuchIdentifierException, ProcessException {

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

        Geometry geom2 = fact.createPoint(new Coordinate(5, 5)) ;

        // Process
        final ProcessDescriptor desc = ProcessFinder.getProcessDescriptor(GeotkProcessingRegistry.NAME,"jts:coveredBy");

        final ParameterValueGroup in = desc.getInputDescriptor().createValue();
        in.parameter("geom1").setValue(geom1);
        in.parameter("geom2").setValue(geom2);
        final org.geotoolkit.process.Process proc = desc.createProcess(in);

        //result
        final Boolean result = (Boolean) proc.call().parameter("result").getValue();

        final Boolean expected = geom1.contains(geom2);

        assertTrue(expected.equals(result));
    }

     @Test
    public void testCoveredByCRS() throws NoSuchIdentifierException, ProcessException, FactoryException, TransformException {

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

         CoordinateReferenceSystem crs1 = CommonCRS.WGS84.geographic();
         JTS.setCRS(geom1, crs1);

        Geometry geom2 = fact.createPoint(new Coordinate(5, 5)) ;

        CoordinateReferenceSystem crs2 = CRS.forCode("EPSG:2154");
        JTS.setCRS(geom2, crs2);

        // Process
        final ProcessDescriptor desc = ProcessFinder.getProcessDescriptor(GeotkProcessingRegistry.NAME,"jts:coveredBy");

        final ParameterValueGroup in = desc.getInputDescriptor().createValue();
        in.parameter("geom1").setValue(geom1);
        in.parameter("geom2").setValue(geom2);
        final org.geotoolkit.process.Process proc = desc.createProcess(in);

        //result
        final Boolean result = (Boolean) proc.call().parameter("result").getValue();

        final MathTransform mt = CRS.findOperation(crs2, crs1, null).getMathTransform();
        geom2 = JTS.transform(geom2, mt);
        final Boolean expected = geom1.contains(geom2);

        assertTrue(expected.equals(result));
    }

}
