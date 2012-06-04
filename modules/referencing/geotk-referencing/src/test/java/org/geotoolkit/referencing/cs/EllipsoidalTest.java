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
package org.geotoolkit.referencing.cs;

import javax.measure.unit.NonSI;
import org.opengis.test.Validators;
import org.geotoolkit.test.referencing.ReferencingTestBase;
import org.junit.*;

import static org.geotoolkit.referencing.Assert.*;


/**
 * Tests the {@link DefaultEllipsoidalCS} class.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.20
 *
 * @since 3.20
 */
public final strictfp class EllipsoidalTest extends ReferencingTestBase {
    /**
     * Tests the {@link DefaultEllipsoidalCS#shiftLongitudeRange(boolean)} method.
     */
    @Test
    public void testShiftLongitudeRange() {
        final DefaultEllipsoidalCS cs = DefaultEllipsoidalCS.GEODETIC_3D;
        assertEquals("longitude.minimumValue", -180.0, cs.getAxis(0).getMinimumValue(), 0.0);
        assertEquals("longitude.maximumValue", +180.0, cs.getAxis(0).getMaximumValue(), 0.0);

        assertSame("Expected a no-op.", cs,  cs.shiftLongitudeRange(false));
        final DefaultEllipsoidalCS shifted = cs.shiftLongitudeRange(true);
        assertNotSame("Expected a new CS.", cs, shifted);
        Validators.validate(shifted);

        assertEquals("longitude.minimumValue",      0.0, shifted.getAxis(0).getMinimumValue(), 0.0);
        assertEquals("longitude.maximumValue",    360.0, shifted.getAxis(0).getMaximumValue(), 0.0);
        assertSame("Expected a no-op.",         shifted, shifted.shiftLongitudeRange(true));
        assertSame("Expected the original CS.", cs,      shifted.shiftLongitudeRange(false));
        assertSame("Expected cached instance.", shifted, cs     .shiftLongitudeRange(true));
    }

    /**
     * Tests the {@link DefaultEllipsoidalCS#shiftLongitudeRange(boolean)} method
     * with grad units.
     */
    @Test
    public void testShiftLongitudeRangeGrad() {
        final DefaultEllipsoidalCS cs = DefaultEllipsoidalCS.GEODETIC_2D.usingUnit(NonSI.GRADE);
        assertEquals("longitude.minimumValue", -200.0, cs.getAxis(0).getMinimumValue(), 0.0);
        assertEquals("longitude.maximumValue", +200.0, cs.getAxis(0).getMaximumValue(), 0.0);

        assertSame("Expected a no-op.", cs,  cs.shiftLongitudeRange(false));
        final DefaultEllipsoidalCS shifted = cs.shiftLongitudeRange(true);
        assertNotSame("Expected a new CS.", cs, shifted);
        Validators.validate(shifted);

        assertEquals("longitude.minimumValue",      0.0, shifted.getAxis(0).getMinimumValue(), 0.0);
        assertEquals("longitude.maximumValue",    400.0, shifted.getAxis(0).getMaximumValue(), 0.0);
        assertSame("Expected a no-op.",         shifted, shifted.shiftLongitudeRange(true));
        assertSame("Expected the original CS.", cs,      shifted.shiftLongitudeRange(false));
        assertSame("Expected cached instance.", shifted, cs     .shiftLongitudeRange(true));
    }
}
