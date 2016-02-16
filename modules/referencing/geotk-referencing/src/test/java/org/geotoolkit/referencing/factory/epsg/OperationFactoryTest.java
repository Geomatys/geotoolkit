/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2005-2012, Open Source Geospatial Foundation (OSGeo)
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
package org.geotoolkit.referencing.factory.epsg;

import org.opengis.util.FactoryException;
import org.opengis.referencing.operation.Conversion;
import org.opengis.referencing.operation.Transformation;
import org.opengis.referencing.operation.CoordinateOperation;
import org.opengis.referencing.operation.CoordinateOperationFactory;
import org.opengis.referencing.operation.ConcatenatedOperation;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

import org.geotoolkit.factory.Hints;
import org.geotoolkit.factory.AuthorityFactoryFinder;
import org.geotoolkit.referencing.operation.AuthorityBackedFactory;
import org.geotoolkit.referencing.operation.AbstractCoordinateOperation;
import org.geotoolkit.referencing.operation.CachingCoordinateOperationFactory;

import org.apache.sis.util.Classes;
import org.junit.*;

import static org.junit.Assume.*;
import static org.geotoolkit.test.Assert.*;
import static org.geotoolkit.test.Commons.decodeQuotes;


/**
 * Tests the usage of {@link CoordinateOperationFactory} with the help of the EPSG database.
 *
 * @author Martin Desruisseaux (IRD)
 * @version 4.0-M2
 *
 * @since 2.4
 */
public final strictfp class OperationFactoryTest extends EpsgFactoryTestBase {
    /**
     * The operation factory being tested.
     */
    private final CoordinateOperationFactory opFactory;

    /**
     * Creates a test suite.
     */
    public OperationFactoryTest() {
        opFactory = AuthorityFactoryFinder.getCoordinateOperationFactory(null);
    }

    /**
     * Tests the creation of an operation from a geographic CRS to WGS84 which is expected
     * to be explicitly described in the database. The transformation involves a datum shift.
     *
     * @throws FactoryException Should not happen.
     */
    @Test
    @Ignore("CachingCoordinateOperationFactory is (for now) hidden by SIS factory.")
    public final void testGeographicBacked() throws FactoryException {
        assumeNotNull(factory);

        assertTrue("Expected a caching factory but got " + opFactory.getClass().getCanonicalName(),
                opFactory instanceof CachingCoordinateOperationFactory);
        assertTrue("EPSG authority factory not found.",
                ((CachingCoordinateOperationFactory) opFactory).getImplementationHints().
                get(Hints.COORDINATE_OPERATION_FACTORY) instanceof AuthorityBackedFactory);

        final CoordinateReferenceSystem  sourceCRS;
        final CoordinateReferenceSystem  targetCRS;
        final CoordinateOperation        operation;
        sourceCRS = factory.createCoordinateReferenceSystem("4230");
        targetCRS = factory.createCoordinateReferenceSystem("4326");
        operation = opFactory.createOperation(sourceCRS, targetCRS);

        assertSame(sourceCRS, operation.getSourceCRS());
        assertSame(targetCRS, operation.getTargetCRS());
        assertSame(operation, opFactory.createOperation(sourceCRS, targetCRS));
        assertEquals("1133", getIdentifier(operation)); // See comment in DefaultDataSourceTest.
        assertEquals(10.0, org.apache.sis.referencing.operation.AbstractCoordinateOperation.castOrCopy(operation).getLinearAccuracy(), 1E-6);
        assertTrue(operation instanceof Transformation);
    }

    /**
     * Tests the creation of an operation from a geographic CRS to WGS84 <strong>not</strong>
     * backed directly by an authority factory. However, the inverse transform may exist in
     * the authority factory.
     *
     * @throws FactoryException Should not happen.
     */
    @Test
    public final void testGeographicUnbacked() throws FactoryException {
        assumeNotNull(factory);

        final CoordinateReferenceSystem  sourceCRS;
        final CoordinateReferenceSystem  targetCRS;
        final CoordinateOperation        operation;
        sourceCRS  = factory.createCoordinateReferenceSystem("4326");
        targetCRS  = factory.createCoordinateReferenceSystem("2995");
        operation  = opFactory.createOperation(sourceCRS, targetCRS);
        assertTrue("This test needs an operation not backed by the EPSG factory.",
                operation.getIdentifiers().isEmpty());
        /*
         * Should contains exactly one transformations and an arbitrary number of conversions.
         */
        assertTrue(operation instanceof ConcatenatedOperation);
        int count = 0;
        for (final CoordinateOperation op : ((ConcatenatedOperation) operation).getOperations()) {
            if (op instanceof Transformation) {
                count++;
            } else {
                assertTrue("Expected Conversion but got " +
                        Classes.getShortName(AbstractCoordinateOperation.getType(op)) + ". ",
                        (op instanceof Conversion));
            }
        }
        assertEquals("The coordinate operation should contains exactly 1 transformation", 1, count);
        assertTrue(org.apache.sis.referencing.operation.AbstractCoordinateOperation.castOrCopy(operation).getLinearAccuracy() <= 25);
    }

    /**
     * Tests the creation of an operation from EPSG:27572 to WGS84.
     * We use the WKT format as a way to check the math transform.
     * The geocentric translation is specified in the EPSG database.
     *
     * @throws FactoryException Should not happen.
     */
    @Test
    @Ignore
    public final void testProjected() throws FactoryException {
        assumeNotNull(factory);

        final CoordinateReferenceSystem  sourceCRS;
        final CoordinateReferenceSystem  targetCRS;
        final CoordinateOperation        operation;
        sourceCRS = factory.createCoordinateReferenceSystem("27572");
        targetCRS = factory.createCoordinateReferenceSystem("4326");
        operation = opFactory.createOperation(sourceCRS, targetCRS);

        assertSame(sourceCRS, operation.getSourceCRS());
        assertSame(targetCRS, operation.getTargetCRS());
        assertSame(operation, opFactory.createOperation(sourceCRS, targetCRS));

        String wkt = operation.getMathTransform().toString();
        assertMultilinesEquals(decodeQuotes(
                "CONCAT_MT[INVERSE_MT[PARAM_MT[“Lambert_Conformal_Conic_1SP”,\n" +
                "      PARAMETER[“semi_major”, 6378249.2],\n" +
                "      PARAMETER[“semi_minor”, 6356515.0],\n" +
                "      PARAMETER[“central_meridian”, 0.0],\n" +
                "      PARAMETER[“latitude_of_origin”, 46.8],\n" +
                "      PARAMETER[“scale_factor”, 0.99987742],\n" +
                "      PARAMETER[“false_easting”, 600000.0],\n" +
                "      PARAMETER[“false_northing”, 2200000.0]]],\n" +
                "  PARAM_MT[“Affine”,\n" +
                "    PARAMETER[“num_row”, 3],\n" +
                "    PARAMETER[“num_col”, 3],\n" +
                "    PARAMETER[“elt_0_2”, 2.33722917]],\n" +
                "  PARAM_MT[“Ellipsoid_To_Geocentric”,\n" +
                "    PARAMETER[“dim”, 2],\n" +
                "    PARAMETER[“semi_major”, 6378249.2],\n" +
                "    PARAMETER[“semi_minor”, 6356515.0]],\n" +
                "  PARAM_MT[“Geocentric translations (geog2D domain)”,\n" +
                "    PARAMETER[“dx”, -168.0],\n" +
                "    PARAMETER[“dy”, -60.0],\n" +
                "    PARAMETER[“dz”, 320.0]],\n" +
                "  PARAM_MT[“Geocentric_To_Ellipsoid”,\n" +
                "    PARAMETER[“dim”, 2],\n" +
                "    PARAMETER[“semi_major”, 6378137.0],\n" +
                "    PARAMETER[“semi_minor”, 6356752.314245179]],\n" +
                "  PARAM_MT[“Affine”,\n" +
                "    PARAMETER[“num_row”, 3],\n" +
                "    PARAMETER[“num_col”, 3],\n" +
                "    PARAMETER[“elt_0_0”, 0.0],\n" +
                "    PARAMETER[“elt_0_1”, 1.0],\n" +
                "    PARAMETER[“elt_1_0”, 1.0],\n" +
                "    PARAMETER[“elt_1_1”, 0.0]]]"), wkt);
    }

    /**
     * Tests the selection of operations from NAD27 (EPSG:4267) or NAD83 (EPSG:4269)
     * to WGS84 (EPSG:4326) in different geographic areas.
     *
     * <p>Example of source CRS that we should test (not yet implemented, to do in Apache SIS):</p>
     * <table>
     *   <tr><td>NAD27</td><td>NAD83</td> <td>Area</td></tr>
     *   <tr><td>15851</td><td>1188</td><td>CONUS</td></tr>
     *   <tr> <td>8609</td><td>1723</td><td>United States (USA) - Mississipi</td></tr>
     *   <tr> <td>8630</td><td>1740</td><td>United States (USA) - Wyoming</td></tr>
     *   <tr> <td>8624</td><td>1734</td><td>United States (USA) - Texas east of 100°W</td></tr>
     *   <tr> <td>8625</td><td>1735</td><td>United States (USA) - Texas west of 100°W</td></tr>
     * </table>
     *
     * @throws FactoryException Should not happen.
     */
    @Test
    @Ignore("Will be re-enabled after the port to Apache SIS.")
    public final void testAreaDependant() throws FactoryException {
        assumeNotNull(factory);
        final int[] codes = {
            /*
             * Columns: [source CRS], [target CRS], [expected coordinate operation]
             */
             4267, 4269, 1241,   // NAD27 to NAD83 (1):  United States (USA) - CONUS including EEZ
             3814, 4326, 1723,   // NAD83 to WGS84 (27): United States (USA) - Mississipi
            32156, 4326, 1740,   // NAD83 to WGS84 (44): United States (USA) - Wyoming
            32139, 4326, 1188    // NAD83 to WGS84 (1):  North America (source CRS was Texas central).
        };
        for (int i=0; i<codes.length;) {
            final String sourceCode = Integer.toString(codes[i++]);
            final String targetCode = Integer.toString(codes[i++]);
            final String expectedOp = Integer.toString(codes[i++]);
            final CoordinateReferenceSystem  sourceCRS = factory.createCoordinateReferenceSystem(sourceCode);
            final CoordinateReferenceSystem  targetCRS = factory.createCoordinateReferenceSystem(targetCode);
            final CoordinateOperation        operation = opFactory.createOperation(sourceCRS, targetCRS);
            final Transformation            datumShift = getTransformation(operation);
            assertNotNull(expectedOp, datumShift);
            assertEquals(expectedOp, getIdentifier(datumShift));
        }
    }

    /**
     * Returns the first coordinate operation which is a transformation.
     * The skipped part are the map projections, which is not the purpose of this test.
     */
    private static Transformation getTransformation(final CoordinateOperation operation) {
        if (operation instanceof Transformation) {
            return (Transformation) operation;
        }
        if (operation instanceof ConcatenatedOperation) {
            for (final CoordinateOperation op : ((ConcatenatedOperation) operation).getOperations()) {
                final Transformation tr = getTransformation(op);
                if (tr != null) {
                    return tr;
                }
            }
        }
        return null;
    }
}
