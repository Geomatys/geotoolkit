/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2010-2011, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2010-2011, Geomatys
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
package org.geotoolkit.referencing.adapters;

import java.util.Random;
import ucar.unidata.geoloc.projection.Mercator;

import org.opengis.metadata.extent.GeographicBoundingBox;
import org.opengis.referencing.operation.TransformException;
import org.opengis.test.referencing.TransformTestCase;

import org.junit.Test;

import static org.opengis.test.Assert.*;
import static org.opengis.test.Validators.*;


/**
 * Tests the {@link NetcdfProjection} class. The projected values correctness
 * (external consistency) is not verified - only internal consistency is verified.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.20
 *
 * @since 3.20
 */
public final strictfp class NetcdfProjectionTest extends TransformTestCase {
    /**
     * The NetCDF projection wrapper to test.
     */
    private final NetcdfProjection projection;

    /**
     * Creates a new test case initialized with a default NetCDF projection.
     */
    public NetcdfProjectionTest() {
        final Mercator p = new Mercator();
        p.setName("Default Mercator projection");
        projection = new NetcdfProjection(p, null, null);
        transform = projection.transform;
        tolerance = 1E-10;
        isDerivativeSupported = false;
    }

    /**
     * Tests the consistency of various {@code transform} methods. This method runs the
     * {@link #verifyInDomain(double[], double[], int[], Random)} test method using a
     * trivial {@link SimpleTransform2D} implementation.
     *
     * @throws TransformException Should never happen.
     */
    @Test
    public void testConsistency() throws TransformException {
        validate(transform);
        verifyInDomain(new double[] {-180, -80}, // Minimal ordinate values to test.
                       new double[] {+180, +80}, // Maximal ordinate values to test.
                       new int[]    { 180,  80}, // Number of points to test.
                       new Random(216919106));
    }

    /**
     * Tests projection name and classname.
     */
    @Test
    public void testNames() {
        assertEquals("Default Mercator projection", projection.getName().getCode());
        assertEquals("Mercator", projection.getMethod().getName().getCode());
    }

    /**
     * Tests the {@link NetcdfProjection#getDomainOfValidity()} method.
     * In NetCDF 4.2, the declared bounding box was approximatively
     * west=-152.85, east=-57.15, south=-43.1, north=43.1. However
     * we presume that this bounding box may change in the future.
     */
    @Test
    public void testDomainOfValidity() {
        final GeographicBoundingBox box = (GeographicBoundingBox)
                projection.getDomainOfValidity().getGeographicElements().iterator().next();
        assertBetween("westBoundLongitude", -180, -152, box.getWestBoundLongitude());
        assertBetween("eastBoundLongitude",  -58, +180, box.getEastBoundLongitude());
        assertBetween("southBoundLatitude",  -90,  -43, box.getSouthBoundLatitude());
        assertBetween("northBoundLatitude",   43,  +90, box.getNorthBoundLatitude());
    }
}
