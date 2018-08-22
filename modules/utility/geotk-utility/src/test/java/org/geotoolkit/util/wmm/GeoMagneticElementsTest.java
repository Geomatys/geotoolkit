/*
 * Geotoolkit.org - An Open Source Java GIS Toolkit
 * http://www.geotoolkit.org
 *
 * (C) 2018, Geomatys.
 *
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation;
 * version 2.1 of the License.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 */
package org.geotoolkit.util.wmm;

import java.io.IOException;
import java.net.URISyntaxException;
import org.junit.Assert;
import org.junit.Test;

/**
 *
 * @author Hasdenteufel Eric (Geomatys)
 */
public class GeoMagneticElementsTest {

    @Test
    public void testMagneticFieldAtPoints() throws IOException, URISyntaxException {

        MagneticModel magneticModel = WorldMagneticModel.readMagModel();
        Assert.assertNotNull(magneticModel);

        MagneticModel timedMagneticModel = magneticModel.timelyModify(new MagneticDate(2015.0));
        Assert.assertNotNull(timedMagneticModel);


        GeoMagneticElements geoMagneticElements = WorldMagneticModel.computeGeoMagneticElements(timedMagneticModel, new CoordGeodetic( 80.0, 0.0, 0.0));
        Assert.assertNotNull(geoMagneticElements);
        //Main fields
        Assert.assertEquals(6627.1, geoMagneticElements.X, 0.1);
        Assert.assertEquals(-445.9, geoMagneticElements.Y, 0.1);
        Assert.assertEquals(54432.3, geoMagneticElements.Z, 0.1);
        Assert.assertEquals(6642.1, geoMagneticElements.H, 0.1);
        Assert.assertEquals(54836.0, geoMagneticElements.F, 0.1);
        Assert.assertEquals(83.04, geoMagneticElements.Incl, 0.01);
        Assert.assertEquals(-3.85, geoMagneticElements.Decl, 0.01);
        Assert.assertEquals(-3.85, geoMagneticElements.GV, 0.01);
        //Secular fields
        Assert.assertEquals(-11.1, geoMagneticElements.Xdot, 0.1);
        Assert.assertEquals(51.5, geoMagneticElements.Ydot, 0.1);
        Assert.assertEquals(10.8, geoMagneticElements.Zdot, 0.1);
        Assert.assertEquals(-14.5, geoMagneticElements.Hdot, 0.1);
        Assert.assertEquals(8.9, geoMagneticElements.Fdot, 0.1);
        Assert.assertEquals(0.02, geoMagneticElements.Incldot, 0.01);
        Assert.assertEquals(0.44, geoMagneticElements.Decldot, 0.01);


        geoMagneticElements = WorldMagneticModel.computeGeoMagneticElements(timedMagneticModel, new CoordGeodetic( 0.0, 120.0, 0.0));
        Assert.assertNotNull(geoMagneticElements);
        //Main fields
        Assert.assertEquals(39518.2, geoMagneticElements.X, 0.1);
        Assert.assertEquals(392.9, geoMagneticElements.Y, 0.1);
        Assert.assertEquals(-11252.4, geoMagneticElements.Z, 0.1);
        Assert.assertEquals(39520.2, geoMagneticElements.H, 0.1);
        Assert.assertEquals(41090.9, geoMagneticElements.F, 0.1);
        Assert.assertEquals(-15.89, geoMagneticElements.Incl, 0.01);
        Assert.assertEquals(0.57, geoMagneticElements.Decl, 0.01);
        Assert.assertEquals(0.57, geoMagneticElements.GV, 0.01);
        //Secular fields
        Assert.assertEquals(21.3, geoMagneticElements.Xdot, 0.1);
        Assert.assertEquals(-68.2, geoMagneticElements.Ydot, 0.1);
        Assert.assertEquals(88.9, geoMagneticElements.Zdot, 0.1);
        Assert.assertEquals(20.6, geoMagneticElements.Hdot, 0.1);
        Assert.assertEquals(-4.5, geoMagneticElements.Fdot, 0.1);
        Assert.assertEquals(0.13, geoMagneticElements.Incldot, 0.01);
        Assert.assertEquals(-0.10, geoMagneticElements.Decldot, 0.01);


        geoMagneticElements = WorldMagneticModel.computeGeoMagneticElements(timedMagneticModel, new CoordGeodetic(-80.0, 240.0, 0.0));
        Assert.assertNotNull(geoMagneticElements);
        //Main fields
        Assert.assertEquals(5797.3, geoMagneticElements.X, 0.1);
        Assert.assertEquals(15761.1, geoMagneticElements.Y, 0.1);
        Assert.assertEquals(-52919.1, geoMagneticElements.Z, 0.1);
        Assert.assertEquals(16793.5, geoMagneticElements.H, 0.1);
        Assert.assertEquals(55519.8, geoMagneticElements.F, 0.1);
        Assert.assertEquals(-72.39, geoMagneticElements.Incl, 0.01);
        Assert.assertEquals(69.81, geoMagneticElements.Decl, 0.01);
        Assert.assertEquals(309.81, geoMagneticElements.GV, 0.01);
        //Secular fields
        Assert.assertEquals(30.6, geoMagneticElements.Xdot, 0.1);
        Assert.assertEquals(8.1, geoMagneticElements.Ydot, 0.1);
        Assert.assertEquals(92.4, geoMagneticElements.Zdot, 0.1);
        Assert.assertEquals(18.2, geoMagneticElements.Hdot, 0.1);
        Assert.assertEquals(-82.6, geoMagneticElements.Fdot, 0.1);
        Assert.assertEquals(0.05, geoMagneticElements.Incldot, 0.01);
        Assert.assertEquals(-0.09, geoMagneticElements.Decldot, 0.01);

        geoMagneticElements = WorldMagneticModel.computeGeoMagneticElements(timedMagneticModel, new CoordGeodetic( 80.0, 0.0, 100.0));
        Assert.assertNotNull(geoMagneticElements);
        //Main fields
        Assert.assertEquals(6314.3, geoMagneticElements.X, 0.1);
        Assert.assertEquals(-471.6, geoMagneticElements.Y, 0.1);
        Assert.assertEquals(52269.8, geoMagneticElements.Z, 0.1);
        Assert.assertEquals(6331.9, geoMagneticElements.H, 0.1);
        Assert.assertEquals(52652.0, geoMagneticElements.F, 0.1);
        Assert.assertEquals(83.09, geoMagneticElements.Incl, 0.01);
        Assert.assertEquals(-4.27, geoMagneticElements.Decl, 0.01);
        Assert.assertEquals(-4.27, geoMagneticElements.GV, 0.01);
        //Secular fields
        Assert.assertEquals(-9.5, geoMagneticElements.Xdot, 0.1);
        Assert.assertEquals(49.2, geoMagneticElements.Ydot, 0.1);
        Assert.assertEquals(9.1, geoMagneticElements.Zdot, 0.1);
        Assert.assertEquals(-13.2, geoMagneticElements.Hdot, 0.1);
        Assert.assertEquals(7.5, geoMagneticElements.Fdot, 0.1);
        Assert.assertEquals(0.02, geoMagneticElements.Incldot, 0.01);
        Assert.assertEquals(0.44, geoMagneticElements.Decldot, 0.01);

        geoMagneticElements = WorldMagneticModel.computeGeoMagneticElements(timedMagneticModel, new CoordGeodetic( 0.0, 120.0, 100.0));
        Assert.assertNotNull(geoMagneticElements);
        //Main fields
        Assert.assertEquals(37535.6, geoMagneticElements.X, 0.1);
        Assert.assertEquals(364.4, geoMagneticElements.Y, 0.1);
        Assert.assertEquals(-10773.4, geoMagneticElements.Z, 0.1);
        Assert.assertEquals(37537.3, geoMagneticElements.H, 0.1);
        Assert.assertEquals(39052.7, geoMagneticElements.F, 0.1);
        Assert.assertEquals(-16.01, geoMagneticElements.Incl, 0.01);
        Assert.assertEquals(0.56, geoMagneticElements.Decl, 0.01);
        Assert.assertEquals(0.56, geoMagneticElements.GV, 0.01);
        //Secular fields
        Assert.assertEquals(20.0, geoMagneticElements.Xdot, 0.1);
        Assert.assertEquals(-61.9, geoMagneticElements.Ydot, 0.1);
        Assert.assertEquals(83.7, geoMagneticElements.Zdot, 0.1);
        Assert.assertEquals(19.4, geoMagneticElements.Hdot, 0.1);
        Assert.assertEquals(-4.4, geoMagneticElements.Fdot, 0.1);
        Assert.assertEquals(0.13, geoMagneticElements.Incldot, 0.01);
        Assert.assertEquals(-0.09, geoMagneticElements.Decldot, 0.01);


        geoMagneticElements = WorldMagneticModel.computeGeoMagneticElements(timedMagneticModel, new CoordGeodetic(-80.0, 240.0, 100.0));
        Assert.assertNotNull(geoMagneticElements);
        //Main fields
        Assert.assertEquals(5613.1, geoMagneticElements.X, 0.1);
        Assert.assertEquals(14791.5, geoMagneticElements.Y, 0.1);
        Assert.assertEquals(-50378.6, geoMagneticElements.Z, 0.1);
        Assert.assertEquals(15820.7, geoMagneticElements.H, 0.1);
        Assert.assertEquals(52804.4, geoMagneticElements.F, 0.1);
        Assert.assertEquals(-72.57, geoMagneticElements.Incl, 0.01);
        Assert.assertEquals(69.22, geoMagneticElements.Decl, 0.01);
        Assert.assertEquals(309.22, geoMagneticElements.GV, 0.01);
        //Secular fields
        Assert.assertEquals(28.2, geoMagneticElements.Xdot, 0.1);
        Assert.assertEquals(6.9, geoMagneticElements.Ydot, 0.1);
        Assert.assertEquals(86.2, geoMagneticElements.Zdot, 0.1);
        Assert.assertEquals(16.5, geoMagneticElements.Hdot, 0.1);
        Assert.assertEquals(-77.3, geoMagneticElements.Fdot, 0.1);
        Assert.assertEquals(0.05, geoMagneticElements.Incldot, 0.01);
        Assert.assertEquals(-0.09, geoMagneticElements.Decldot, 0.01);

        timedMagneticModel = magneticModel.timelyModify(new MagneticDate(2017.5));
        Assert.assertNotNull(timedMagneticModel);

        geoMagneticElements = WorldMagneticModel.computeGeoMagneticElements(timedMagneticModel, new CoordGeodetic( 80.0, 0.0, 0.0));
        Assert.assertNotNull(geoMagneticElements);
        //Main fields
        Assert.assertEquals(6599.4, geoMagneticElements.X, 0.1);
        Assert.assertEquals(-317.1, geoMagneticElements.Y, 0.1);
        Assert.assertEquals(54459.2, geoMagneticElements.Z, 0.1);
        Assert.assertEquals(6607.0, geoMagneticElements.H, 0.1);
        Assert.assertEquals(54858.5, geoMagneticElements.F, 0.1);
        Assert.assertEquals(83.08, geoMagneticElements.Incl, 0.01);
        Assert.assertEquals(-2.75, geoMagneticElements.Decl, 0.01);
        Assert.assertEquals(-2.75, geoMagneticElements.GV, 0.01);
        //Secular fields
        Assert.assertEquals(-11.1, geoMagneticElements.Xdot, 0.1);
        Assert.assertEquals(51.5, geoMagneticElements.Ydot, 0.1);
        Assert.assertEquals(10.8, geoMagneticElements.Zdot, 0.1);
        Assert.assertEquals(-13.5, geoMagneticElements.Hdot, 0.1);
        Assert.assertEquals(9.1, geoMagneticElements.Fdot, 0.1);
        Assert.assertEquals(0.02, geoMagneticElements.Incldot, 0.01);
        Assert.assertEquals(0.44, geoMagneticElements.Decldot, 0.01);


    }
}
