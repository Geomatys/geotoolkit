/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2012, Open Source Geospatial Foundation (OSGeo)
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
package org.geotoolkit.referencing.crs;

import org.opengis.test.Validators;
import org.opengis.referencing.cs.CoordinateSystemAxis;
import org.geotoolkit.test.referencing.ReferencingTestBase;
import org.junit.*;

import static org.geotoolkit.referencing.Assert.*;
import static org.geotoolkit.referencing.cs.AxisRangeType.*;


/**
 * Tests the {@link DefaultCompoundCRS} class.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.20
 *
 * @since 3.20
 */
public final strictfp class CompoundTest extends ReferencingTestBase {
    /**
     * Tests the {@link DefaultCompoundCRS#shiftAxisRange(AxisRangeType)} method.
     */
    @Test
    public void testShiftLongitudeRange() {
        final DefaultCompoundCRS crs = new DefaultCompoundCRS("Spatio-temporal",
                DefaultGeographicCRS.WGS84_3D, DefaultTemporalCRS.JULIAN);
        CoordinateSystemAxis axis = crs.getCoordinateSystem().getAxis(0);
        assertEquals("longitude.minimumValue", -180.0, axis.getMinimumValue(), 0.0);
        assertEquals("longitude.maximumValue", +180.0, axis.getMaximumValue(), 0.0);

        assertSame("Expected a no-op.", crs, crs.shiftAxisRange(SPANNING_ZERO_LONGITUDE));
        final DefaultCompoundCRS shifted =   crs.shiftAxisRange(POSITIVE_LONGITUDE);
        assertNotSame("Expected a new CRS.", crs, shifted);
        Validators.validate(shifted);

        axis = shifted.getCoordinateSystem().getAxis(0);
        assertEquals("longitude.minimumValue",      0.0, axis.getMinimumValue(), 0.0);
        assertEquals("longitude.maximumValue",    360.0, axis.getMaximumValue(), 0.0);
        assertSame("Expected a no-op.",         shifted, shifted.shiftAxisRange(POSITIVE_LONGITUDE));
        assertSame("Expected the original CS.", crs,     shifted.shiftAxisRange(SPANNING_ZERO_LONGITUDE));
        assertSame("Expected cached instance.", shifted, crs    .shiftAxisRange(POSITIVE_LONGITUDE));
    }
}
