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
package org.geotoolkit.referencing.operation;

import org.geotoolkit.factory.Hints;
import org.geotoolkit.factory.FactoryFinder;


/**
 * Performs the same test than {@link COFactoryUsingMolodenskyTest}, but using the
 * {@code "AbridgedMolodensky"} method instead than {@code "Molodensky"}.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.16
 *
 * @since 3.16
 */
public final class COFactoryUsingAbridgedMolodenskyTest extends COFactoryUsingMolodenskyTest {
    /**
     * Creates a new test suite.
     */
    public COFactoryUsingAbridgedMolodenskyTest() {
        super(new Hints(FactoryFinder.FILTER_KEY, FILTER, Hints.DATUM_SHIFT_METHOD, "Abridged Molodensky"));
    }

    /**
     * Returns the datum shift method used by this test.
     */
    @Override
    protected String getDatumShiftMethod() {
        return "Abridged Molodensky";
    }

    /**
     * Relaxes the tolerance threshold provided by the super class.
     */
    @Override
    SamplePoints.Target getExpectedResult(final SamplePoints sample, final boolean withHeight) {
        tolerance = SamplePoints.MOLODENSKY_TOLERANCE * 10;
        zTolerance = Math.max(zTolerance, 2E-2);
        if (zDimension == null) {
            zDimension = new int[] {2};
        }
        return sample.tgt;
    }
}
