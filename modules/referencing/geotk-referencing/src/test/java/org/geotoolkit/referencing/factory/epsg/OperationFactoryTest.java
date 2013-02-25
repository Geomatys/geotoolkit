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
import org.geotoolkit.referencing.factory.AbstractAuthorityFactory;
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
 * @version 3.00
 *
 * @since 2.4
 */
public final strictfp class OperationFactoryTest extends EpsgFactoryTestBase {
    /**
     * The operation factory being tested.
     */
    private final CoordinateOperationFactory opFactory;

    /**
     * Creates a test suite for the MS-Access database.
     */
    public OperationFactoryTest() {
        this(ThreadedEpsgFactory.class);
    }

    /**
     * Creates a test suite for the given factory type.
     * This is used for the test suite in other modules.
     *
     * @param type The class of the factory being tested.
     */
    protected OperationFactoryTest(final Class<? extends AbstractAuthorityFactory> type) {
        super(type);
        opFactory = AuthorityFactoryFinder.getCoordinateOperationFactory(null);
    }

    /**
     * Tests the creation of an operation from a geographic CRS to WGS84 which is expected
     * to be explicitly described in the database. The transformation involve a datum shift.
     *
     * @throws FactoryException Should not happen.
     */
    @Test
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
        assertEquals("1612", getIdentifier(operation)); // See comment in DefaultDataSourceTest.
        assertEquals(1.0, AbstractCoordinateOperation.getAccuracy(operation), 1E-6);
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
        assertTrue(AbstractCoordinateOperation.getAccuracy(operation) <= 25);
    }

    /**
     * Tests the creation of an operation from EPSG:27572 to WGS84.
     * We use the WKT format as a way to check the math transform.
     * The geocentric translation is specified in the EPSG database.
     *
     * @throws FactoryException Should not happen.
     */
    @Test
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
}
