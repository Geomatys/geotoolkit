/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2010-2012, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2010-2012, Geomatys
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
 *
 *    NOTE: permission has been given to the JScience project (http://www.jscience.org)
 *          to distribute this file under BSD-like license.
 */
package org.geotoolkit.nature;

import org.junit.*;

import static org.junit.Assert.*;


/**
 * Tests the {@link SeaWater} class. Values are compares against the values computes by the
 * <a href="http://www2.sese.uwa.edu.au/~hollings/pilot/denscalc.html">calculator there</a>,
 * except sound velocity.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.16
 *
 * @since 3.16
 */
public final strictfp class SeaWaterTest {
    /**
     * The values to tests in the following order:
     * <p>
     * <ol>
     *   <li>Salinity (PSU)</li>
     *   <li>Temperature (°C)</li>
     *   <li>Pressure (dbar)</li>
     *   <li>Expected conductivity (S/m)<li>
     *   <li>Expected density (kg/m³)</li>
     *   <li>Expected specific heat (J/(kg °C))</li>
     *   <li>Expected fusion temperature (°C)</li>
     *   <li>Expected adiabetic temperature gradient (mdeg/dbar)</li>
     *   <li>Expected sound velocity (m/s)</li>
     *   <li>Expected depth (m)</li>
     * </ol>
     */
    private static final double[][] VALUES = {
        new double[] {35, 15,    0, 4.2914,   1025.97,  3989.8, -1.922, 0.150544, 1506.7,    0},
        new double[] {25, 20,  500, 3.559139, 1019.356, 4032.3, -1.735, 0.178186, 1593.6,  496},
        new double[] {30,  5, 2500, 3.005365, 1035.084, 3941.7, -3.52,  0.106214, 1891.6, 2468}
    };

    /**
     * Tests the methods using Salinity (or conductivity), Temperature, Pressure as input parameters.
     */
    @Test
    public void testSTP() {
        for (final double[] values : VALUES) {
            final double salinity      = values[0];
            final double temperature   = values[1];
            final double pressure      = values[2];
            final double conductivity  = values[3] * 10; // Convert to mS/cm.
            final double density       = values[4];
            final double specificHeat  = values[5];
            final double freezingPoint = values[6];
            final double adiabeticTG   = values[7] / 1000; // Convert approximatively to °C/m
            final double soundVelocity = values[8];
            final double depth         = values[9];
            assertEquals("salinity",       salinity,       SeaWater.salinity                    (conductivity, temperature, pressure), 5E-4);
            assertEquals("conductivity",   conductivity,   SeaWater.conductivity                (salinity,     temperature, pressure), 5E-4);
            assertEquals("density",        density,        SeaWater.density                     (salinity,     temperature, pressure), 5E-3);
            assertEquals("sigmaT",         density - 1000, SeaWater.densitySigmaT               (salinity,     temperature, pressure), 5E-3);
            assertEquals("volume",         1 / density,    SeaWater.volume                      (salinity,     temperature, pressure), 5E-3);
            assertEquals("specific heat",  specificHeat,   SeaWater.specificHeat                (salinity,     temperature, pressure), 5E-2);
            assertEquals("freezing point", freezingPoint,  SeaWater.fusionTemperature           (salinity,                  pressure), 5E-4);
            assertEquals("adiabetic T.G.", adiabeticTG,    SeaWater.adiabeticTemperatureGradient(salinity,     temperature, pressure), 5E-7);
            assertEquals("sound velocity", soundVelocity,  SeaWater.soundVelocity               (salinity,     temperature, pressure), 5E-2);
            assertEquals("depth",          depth,          SeaWater.depth                       (pressure,     45),                    10);
        }
    }
}
