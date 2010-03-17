/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2010, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2010, Geomatys
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
package org.geotoolkit.internal.referencing;

import org.geotoolkit.referencing.crs.DefaultGeographicCRS;
import org.geotoolkit.referencing.crs.DefaultVerticalCRS;
import org.geotoolkit.referencing.cs.DefaultCoordinateSystemAxis;

import org.junit.*;
import static org.junit.Assert.*;


/**
 * Tests the {@link CRSUtilities} class.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.10
 *
 * @since 3.10
 */
public final class CRSUtilitiesTest {
    /**
     * Tests {@link CRSUtilities#dimensionColinearWith}.
     */
    @Test
    public void testDimensionColinearWith() {
        assertEquals(1, CRSUtilities.dimensionColinearWith(
                DefaultGeographicCRS.WGS84_3D.getCoordinateSystem(),
                DefaultCoordinateSystemAxis.LATITUDE));

        assertEquals(0, CRSUtilities.dimensionColinearWith(
                DefaultGeographicCRS.WGS84_3D.getCoordinateSystem(),
                DefaultGeographicCRS.WGS84.getCoordinateSystem()));

        assertEquals(2, CRSUtilities.dimensionColinearWith(
                DefaultGeographicCRS.WGS84_3D.getCoordinateSystem(),
                DefaultVerticalCRS.ELLIPSOIDAL_HEIGHT.getCoordinateSystem()));
    }
}
