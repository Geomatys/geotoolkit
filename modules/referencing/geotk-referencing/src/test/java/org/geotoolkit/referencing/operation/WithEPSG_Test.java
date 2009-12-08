/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2009, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2009, Geomatys
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

import javax.measure.converter.ConversionException;

import org.opengis.referencing.FactoryException;
import org.opengis.referencing.operation.MathTransform;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.OperationNotFoundException;

import org.geotoolkit.referencing.CRS;
import org.geotoolkit.referencing.ReferencingTestCase;

import org.geotoolkit.referencing.crs.DefaultCompoundCRS;
import org.junit.*;

import static org.junit.Assume.*;


/**
 * Tests the {@link CoordinateOperationFactory} with an EPSG database.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.07
 *
 * @since 3.07
 */
public final class WithEPSG_Test extends ReferencingTestCase {
    /**
     * Tests the conversion from {@code EPSG:4979} to {@code EPSG:4326}.
     * Note that {@code EPSG:4979} is the replacement of {@code EPSG:4327}
     * with degrees units instead of DMS.
     *
     * @throws FactoryException Should never happen.
     *
     * @see http://jira.geotoolkit.org/browse/GEOTK-65
     */
    @Test
    public void testGeographic3D_to_2D() throws FactoryException {
        assumeTrue(isEpsgFactoryAvailable());

        CoordinateReferenceSystem sourceCRS = CRS.decode("EPSG:4327");
        CoordinateReferenceSystem targetCRS = CRS.decode("EPSG:4326");
        MathTransform tr;
        try {
            tr = CRS.findMathTransform(sourceCRS, targetCRS);
            fail("No conversion from EPSG:4327 to EPSG:4326 should be allowed because the units " +
                 "conversion from DMS to degrees is not linear. Note that this exception may be " +
                 "removed in a future version if we implement non-linear unit conversions.");
        } catch (OperationNotFoundException e) {
            assertTrue("The operation should have failed because of a unit conversion error.",
                    e.getCause() instanceof ConversionException);
        }
        sourceCRS = CRS.decode("EPSG:4979");
        tr = CRS.findMathTransform(sourceCRS, targetCRS);
        assertEquals(3, tr.getSourceDimensions());
        assertEquals(2, tr.getTargetDimensions());
        assertDiagonalMatrix(tr, true, 1, 1, 0);
    }
}
