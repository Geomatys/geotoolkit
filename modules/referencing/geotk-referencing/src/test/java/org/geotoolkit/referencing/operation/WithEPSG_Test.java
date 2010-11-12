/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2009-2010, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2009-2010, Geomatys
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

import org.opengis.util.FactoryException;
import org.opengis.referencing.operation.MathTransform;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.crs.GeographicCRS;
import org.opengis.referencing.crs.ProjectedCRS;
import org.opengis.referencing.datum.GeodeticDatum;
import org.opengis.referencing.operation.OperationNotFoundException;

import org.geotoolkit.referencing.CRS;
import org.geotoolkit.referencing.ReferencingTestCase;

import org.geotoolkit.referencing.crs.DefaultCompoundCRS;
import org.geotoolkit.referencing.datum.BursaWolfParameters;
import org.geotoolkit.referencing.datum.DefaultGeodeticDatum;
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
     * @see <a href="http://jira.geotoolkit.org/browse/GEOTK-65">GEOTK-65</a>
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

    /**
     * Tests the conversion from {@code CompoundCRS[EPSG:3035 + Sigma-level]} to {@code EPSG:4326}.
     * The interesting part in this test is that the height is not a standard height, and the
     * referencing module is not supposed to known how to build a 3D Geographic CRS (needed as
     * an intermediate step for the datum shift) with that height.
     *
     * @throws FactoryException Should never happen.
     *
     * @see <a href="http://jira.geotoolkit.org/browse/GEOTK-71">GEOTK-71</a>
     */
    @Test
    public void testProjected3D_to_2D() throws FactoryException {
        assumeTrue(isEpsgFactoryAvailable());

        CoordinateReferenceSystem targetCRS = CRS.decode("EPSG:4326");
        CoordinateReferenceSystem sourceCRS = CRS.decode("EPSG:3035");
        GeodeticDatum targetDatum = ((GeographicCRS) targetCRS).getDatum();
        GeodeticDatum sourceDatum =  ((ProjectedCRS) sourceCRS).getDatum();
        final BursaWolfParameters param = ((DefaultGeodeticDatum) sourceDatum).getBursaWolfParameters(targetDatum);
        assertNotNull("This test requires that an explicit BursaWolf parameter exists.", param);
        assertTrue("This test requires that the BursaWolf parameter is set to identity.", param.isIdentity());

        CoordinateReferenceSystem vertCRS = CRS.parseWKT(
                "VERT_CS[\"Sigma Level\",VERT_DATUM[\"Sigma Level\",2000],UNIT[\"level\",1.0],AXIS[\"Sigma Level\",DOWN]]");
        sourceCRS = new DefaultCompoundCRS("ETRS89 + Sigma level", sourceCRS, vertCRS);
        final MathTransform tr = CRS.findMathTransform(sourceCRS, targetCRS);
        assertSame(tr, CRS.findMathTransform(sourceCRS, targetCRS, false));
        assertSame(tr, CRS.findMathTransform(sourceCRS, targetCRS, true));
        assertEquals(3, tr.getSourceDimensions());
        assertEquals(2, tr.getTargetDimensions());
    }
}
