/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2007-2012, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2009-2012, Geomatys
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
package org.geotoolkit.metadata.iso.quality;

import java.util.Collection;

import org.opengis.metadata.quality.Result;
import org.opengis.metadata.quality.ConformanceResult;

import org.apache.sis.test.DependsOn;
import org.geotoolkit.metadata.iso.citation.CitationsTest;

import org.junit.*;
import static org.geotoolkit.test.Assert.*;


/**
 * Tests {@link AbstractPositionalAccuracy} constants.
 *
 * @author Martin Desruisseaux (IRD, Geomatys)
 * @version 3.19
 *
 * @since 3.19 (derived from 2.2)
 */
@DependsOn(CitationsTest.class)
public final strictfp class AbstractPositionalAccuracyTest {
    /**
     * Tests {@link AbstractPositionalAccuracy} constants.
     */
    @Test
    public void testPositionalAccuracy() {
        assertEquals("Identity comparison",
                     AbstractPositionalAccuracy.DATUM_SHIFT_APPLIED,
                     AbstractPositionalAccuracy.DATUM_SHIFT_APPLIED);

        assertEquals("Identity comparison",
                     AbstractPositionalAccuracy.DATUM_SHIFT_OMITTED,
                     AbstractPositionalAccuracy.DATUM_SHIFT_OMITTED);

        assertNotSame(AbstractPositionalAccuracy.DATUM_SHIFT_APPLIED,
                      AbstractPositionalAccuracy.DATUM_SHIFT_OMITTED);

        final Collection<? extends Result> appliedResults = AbstractPositionalAccuracy.DATUM_SHIFT_APPLIED.getResults();
        final Collection<? extends Result> omittedResults = AbstractPositionalAccuracy.DATUM_SHIFT_OMITTED.getResults();
        final ConformanceResult applied = (ConformanceResult) appliedResults.iterator().next();
        final ConformanceResult omitted = (ConformanceResult) omittedResults.iterator().next();
        assertNotSame(applied, omitted);
        assertTrue (applied.pass());
        assertFalse(omitted.pass());
        assertFalse(applied.equals(omitted));
        assertFalse(appliedResults.equals(omittedResults));
        assertFalse(AbstractPositionalAccuracy.DATUM_SHIFT_APPLIED.equals(
                    AbstractPositionalAccuracy.DATUM_SHIFT_OMITTED));
    }
}
