/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2008-2009, Open Source Geospatial Foundation (OSGeo)
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
package org.geotoolkit.referencing.datum;

import java.util.Random;
import static org.geotoolkit.referencing.datum.DefaultEllipsoid.*;

import org.junit.*;
import org.opengis.test.Validators;
import static org.junit.Assert.*;


/**
 * Tests the {@link DefaultEllipsoid} class.
 *
 * @author Martin Desruisseaux (IRD)
 * @version 3.0
 *
 * @since 2.1
 */
public class EllipsoidTest {
    /**
     * Validates constant definitions.
     */
    @Test
    public void validate() {
        Validators.validate(WGS84);
        Validators.validate(GRS80);
        Validators.validate(INTERNATIONAL_1924);
        Validators.validate(CLARKE_1866);
        Validators.validate(SPHERE);
    }

    /**
     * Tests the orthodromic distance computed by {@link DefaultEllipsoid}. There is actually two
     * algorithms used: one for the ellipsoidal model, and a simpler one for spherical model.
     * We test the ellipsoidal model using know values of nautical mile at different latitude.
     * Then, we test the spherical model with random values. If JDK 1.4 assertion is enabled,
     * the spherical model will compare its result with the ellipsoidal one.
     *
     * Note about nautical mile:
     *
     *    "Le mille marin était, en principe, la longueur de la minute sexagésimale du méridien
     *     à la latitude de 45°. Cette longueur dépendait donc des valeurs adoptées pour le rayon
     *     équatorial de la terre et son aplatissement. En France, le décret du 3 mai 1961 sur les
     *     unités de mesure, fixe à 1852 mètres la longueur du mille marin qui est également la
     *     valeur adoptée pour le mille marin international."
     *
     *                                   Source: Office de la langue française, 1996
     *                                           http://www.granddictionnaire.com
     */
    @Test
    public void testOrthodromicDistance() {
        final Random random = new Random(799843215);
        final DefaultEllipsoid e = WGS84;
        final double hm = 0.5/60; // Half of a minute of angle, in degrees.
        double tolerance = 0.2;
        /*
         * Tests the ellipsoidal model.
         */
        assertEquals("Nautical mile at equator",    1842.78, e.orthodromicDistance(0, 00-hm, 0, 00+hm), tolerance);
        assertEquals("Nautical mile at North pole", 1861.67, e.orthodromicDistance(0, 90-2*hm, 0,  90), tolerance);
        assertEquals("Nautical mile at South pole", 1861.67, e.orthodromicDistance(0, 2*hm-90, 0, -90), tolerance);
        assertEquals("International nautical mile", 1852.00, e.orthodromicDistance(0, 45-hm, 0, 45+hm), tolerance);
        for (double i=0.01; i<180; i+=1) {
            final double base = 180*random.nextDouble()-90;
            assertEquals(i+"° rotation", e.getSemiMajorAxis() * Math.toRadians(i),
                                         e.orthodromicDistance(base, 0, base+i, 0), tolerance);
        }
        /*
         * Tests the spherical model. The factory method should create
         * a specialized class, which is not the usual Ellipsoid class.
         */
        final double radius = e.getSemiMajorAxis();
        final double circumference = (radius*1.00000001) * (2*Math.PI);
        final DefaultEllipsoid s = createEllipsoid("Sphere", radius, radius, e.getAxisUnit());
        assertTrue("Spheroid class", Spheroid.class.equals(s.getClass()));
        tolerance = 0.001;
        for (double i=0; i<=180; i+=1) {
            final double base = 360*random.nextDouble()-180;
            assertEquals(i+"° rotation", s.getSemiMajorAxis()*Math.toRadians(i),
                                         s.orthodromicDistance(base, 0, base+i, 0), tolerance);
        }
        for (double i=-90; i<=+90; i+=1) {
            final double meridian = 360*random.nextDouble()-180;
            assertEquals(i+"° rotation", s.getSemiMajorAxis()*Math.toRadians(Math.abs(i)),
                                         s.orthodromicDistance(meridian, 0, meridian, i), tolerance);
        }
        for (int i=0; i<100; i++) {
            final double y1 =  -90 + 180*random.nextDouble();
            final double y2 =  -90 + 180*random.nextDouble();
            final double x1 = -180 + 360*random.nextDouble();
            final double x2 = -180 + 360*random.nextDouble();
            final double distance = s.orthodromicDistance(x1, y1, x2, y2);
            assertTrue("Range of legal values", distance >= 0 && distance <= circumference);
        }
    }
}
