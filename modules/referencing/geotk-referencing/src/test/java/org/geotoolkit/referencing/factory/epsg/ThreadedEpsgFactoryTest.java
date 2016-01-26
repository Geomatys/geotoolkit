/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2003-2012, Open Source Geospatial Foundation (OSGeo)
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

import java.io.*;
import java.util.*;
import java.sql.SQLException;
import org.opengis.referencing.cs.*;
import org.opengis.referencing.crs.*;
import org.opengis.referencing.operation.*;
import org.opengis.util.FactoryException;

import org.apache.sis.referencing.crs.AbstractCRS;
import org.geotoolkit.factory.AuthorityFactoryFinder;
import org.apache.sis.referencing.operation.AbstractCoordinateOperation;
import org.apache.sis.util.ComparisonMode;

import org.junit.*;
import static org.junit.Assume.assumeNotNull;
import static org.geotoolkit.referencing.Assert.*;


/**
 * Tests transformations from CRS and/or operations created from the EPSG factory.
 *
 * @author Martin Desruisseaux (IRD, Geomatys)
 * @author Vadim Semenov
 * @version 3.18
 */
public final strictfp class ThreadedEpsgFactoryTest extends EpsgFactoryTestBase {
    /**
     * Creates a test suite for the MS-Access database.
     */
    public ThreadedEpsgFactoryTest() {
        super();
    }

    /**
     * Tests creations of operation objects.
     *
     * @throws FactoryException if an error occurred while querying the factory.
     */
    @Test
    public final void testCreationOperations() throws FactoryException {
        assumeNotNull(factory);

        final CoordinateOperationFactory opf = AuthorityFactoryFinder.getCoordinateOperationFactory(null);
        CoordinateReferenceSystem sourceCRS, targetCRS;
        CoordinateOperation operation;

        sourceCRS = factory.createCoordinateReferenceSystem("4273");
        targetCRS = factory.createCoordinateReferenceSystem("4979");
        operation = opf.createOperation(sourceCRS, targetCRS);
        assertNotSame(sourceCRS, targetCRS);
        assertFalse(operation.getMathTransform().isIdentity());

        assertSame(sourceCRS, factory.createCoordinateReferenceSystem("EPSG:4273"));
        assertSame(targetCRS, factory.createCoordinateReferenceSystem("EPSG:4979"));

        assertSame(sourceCRS, factory.createCoordinateReferenceSystem(" EPSG : 4273 "));
        assertSame(targetCRS, factory.createCoordinateReferenceSystem(" EPSG : 4979 "));
        /*
         * CRS with "South along 180°" and "South along 90°E" axis.
         */
        sourceCRS = factory.createCoordinateReferenceSystem("EPSG:32661");
        targetCRS = factory.createCoordinateReferenceSystem("4326");
        operation = opf.createOperation(sourceCRS, targetCRS);
        final MathTransform    transform = operation.getMathTransform();
        final CoordinateSystem  sourceCS = sourceCRS.getCoordinateSystem();
        final CoordinateSystemAxis axis0 = sourceCS.getAxis(0);
        final CoordinateSystemAxis axis1 = sourceCS.getAxis(1);
        assertEquals("Northing",         axis0.getName().getCode());
        assertEquals("Easting",          axis1.getName().getCode());
        assertEquals("South along 180°", axis0.getDirection().name());
        assertEquals("South along 90°E", axis1.getDirection().name());
        assertFalse(transform.isIdentity());
//        assertTrue(transform instanceof ConcatenatedTransform);
//        ConcatenatedTransform ct = (ConcatenatedTransform) transform;
//        /*
//         * An affine transform for swapping axis should be performed after the map projection.
//         */
//        final int before = AffineTransform.TYPE_TRANSLATION       |
//                           AffineTransform.TYPE_QUADRANT_ROTATION |
//                           AffineTransform.TYPE_UNIFORM_SCALE;
//        final int after  = AffineTransform.TYPE_FLIP              |
//                           AffineTransform.TYPE_QUADRANT_ROTATION |
//                           AffineTransform.TYPE_UNIFORM_SCALE;
//        assertTrue(ct.transform1 instanceof AffineTransform);
//        assertEquals(before, ((AffineTransform) ct.transform1).getType());
//        assertTrue(ct.transform2 instanceof ConcatenatedTransform);
//        ct = (ConcatenatedTransform) ct.transform2;
//        assertTrue(ct.transform1 instanceof AbstractMathTransform);
//        assertTrue(ct.transform2 instanceof AffineTransform);
//        assertEquals(after, ((AffineTransform) ct.transform2).getType());
    }

    /**
     * Tests the serialization of many {@link CoordinateOperation} objects.
     *
     * @throws FactoryException if an error occurred while querying the factory.
     * @throws IOException If an error occurred during the serialization process.
     * @throws ClassNotFoundException Should never occur.
     */
    @Test
    @Ignore
    public final void testSerialization() throws FactoryException, IOException, ClassNotFoundException {
        assumeNotNull(factory);

        CoordinateReferenceSystem crs1 = factory.createCoordinateReferenceSystem("4326");
        CoordinateReferenceSystem crs2 = factory.createCoordinateReferenceSystem("4322");
        CoordinateOperationFactory opf = AuthorityFactoryFinder.getCoordinateOperationFactory(null);
        CoordinateOperation cop = opf.createOperation(crs1, crs2);
        serialize(cop);

        crs1 = crs2 = null;
        final String crs1_name  = "4326";
        final int crs2_ranges[] = {4326,  4326,
                       /* [ 2] */  4322,  4322,
                       /* [ 4] */  4269,  4269,
                       /* [ 6] */  4267,  4267,
                       /* [ 8] */  4230,  4230,
                       /* [10] */ 32601, 32660,
                       /* [12] */ 32701, 32760,
                       /* [14] */  2759,  2930};

        for (int irange=0; irange<crs2_ranges.length; irange+=2) {
            int range_start = crs2_ranges[irange  ];
            int range_end   = crs2_ranges[irange+1];
            for (int isystem2=range_start; isystem2<=range_end; isystem2++) {
                if (crs1 == null) {
                    crs1 = factory.createCoordinateReferenceSystem(crs1_name);
                }
                String crs2_name = Integer.toString(isystem2);
                crs2 = factory.createCoordinateReferenceSystem(crs2_name);
                cop = opf.createOperation(crs1, crs2);
                serialize(cop);
            }
        }
    }

    /**
     * Tests the serialization of the specified object.
     */
    private static void serialize(final Object object) throws IOException, ClassNotFoundException {
        final ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        try (ObjectOutputStream out = new ObjectOutputStream(buffer)) {
            out.writeObject(object);
        }
        final Object read;
        try (ObjectInputStream in = new ObjectInputStream(new ByteArrayInputStream(buffer.toByteArray()))) {
            read = in.readObject();
        }
        assertEquals(object, read);
        assertEquals(object.hashCode(), read.hashCode());
    }

    /**
     * Fetches the accuracy declared in all coordinate operations found in the database.
     *
     * @throws FactoryException if an error occurred while querying the factory.
     */
    @Test
    @Ignore
    public final void testAccuracy() throws FactoryException {
        assumeNotNull(factory);

        final Set<String> identifiers = factory.getAuthorityCodes(CoordinateOperation.class);
        double min     = Double.POSITIVE_INFINITY;
        double max     = Double.NEGATIVE_INFINITY;
        double sum     = 0;
        int    count   = 0; // Number of coordinate operations (minus the skipped ones).
        int    created = 0; // Number of coordinate operations recognized by the factory.
        int    valid   = 0; // Number of non-NaN accuracies.
        for (final String code : identifiers) {
            final CoordinateOperation operation;
            count++;
            try {
                operation = factory.createCoordinateOperation(code);
            } catch (FactoryException exception) {
                // Skip unsupported coordinate operations, except if the cause is a SQL exception.
                if (exception.getCause() instanceof SQLException) {
                    throw exception;
                }
                continue;
            } catch (IllegalArgumentException exception) {
                // TODO: we apparently have a bug with operation method 9633 (and others...).
                if (!exception.getMessage().contains("Ordnance Survey National Transformation")) {
                    throw exception;
                }
                continue;
            }
            created++;
            assertNotNull(operation);
            final double accuracy = AbstractCoordinateOperation.castOrCopy(operation).getLinearAccuracy();
            assertFalse(accuracy < 0);
            if (!Double.isNaN(accuracy)) {
                if (accuracy < min) min=accuracy;
                if (accuracy > max) max=accuracy;
                sum += accuracy;
                valid++;
            }
        }
        final PrintWriter out = ThreadedEpsgFactoryTest.out;
        if (out != null) {
            out.print("Number of coordinate operations:    "); out.println(identifiers.size());
            out.print("Number of tested operations:        "); out.println(count);
            out.print("Number of recognized operations:    "); out.println(created);
            out.print("Number of operations with accuracy: "); out.println(valid);
            out.print("Minimal accuracy value (meters):    "); out.println(min);
            out.print("Maximal accuracy value (meters):    "); out.println(max);
            out.print("Average accuracy value (meters):    "); out.println(sum / valid);
            out.flush();
        }
    }

    /**
     * We are supposed to be able to get back identical {@link CoordinateReferenceSystem}
     * objects when we create using the same definition. This test case ensures that we do
     * get back identical objects when using an EPSG code and when using WKT.
     * <p>
     * The same definition is used in each case - will it work?
     * Answer is no because we lost metadata information in WKT formatting;
     * however two instances created with the same WKT work out okay.
     *
     * @throws FactoryException if an error occurred while querying the factory.
     */
    @Test
    @Ignore
    public final void testUnique() throws FactoryException {        // LGPL
        assumeNotNull(factory);

        final AbstractCRS epsgCrs = (AbstractCRS) org.geotoolkit.referencing.CRS.decode("EPSG:4326");
        final String      wkt     = epsgCrs.toWKT();
        final AbstractCRS wktCrs  = (AbstractCRS) org.geotoolkit.referencing.CRS.parseWKT(wkt);

        assertTrue   ("equals ignore metadata",  epsgCrs.equals(wktCrs, ComparisonMode.APPROXIMATIVE));
        assertTrue   ("equals ignore metadata",  epsgCrs.equals(wktCrs, ComparisonMode.IGNORE_METADATA));
        assertFalse  ("equals compare metadata", epsgCrs.equals(wktCrs, ComparisonMode.BY_CONTRACT));
        assertFalse  ("equals compare metadata", epsgCrs.equals(wktCrs, ComparisonMode.STRICT));
        assertFalse  ("equals",   epsgCrs.equals(wktCrs));
        assertNotSame("identity", epsgCrs, wktCrs);

        // Parsing the same thing twice?
        final AbstractCRS wktCrs2 = (AbstractCRS) org.geotoolkit.referencing.CRS.parseWKT(wkt);
        assertTrue  ("equals ignore metadata",  wktCrs.equals(wktCrs2, ComparisonMode.APPROXIMATIVE));
        assertTrue  ("equals ignore metadata",  wktCrs.equals(wktCrs2, ComparisonMode.IGNORE_METADATA));
        assertTrue  ("equals compare metadata", wktCrs.equals(wktCrs2, ComparisonMode.BY_CONTRACT));
        assertTrue  ("equals compare metadata", wktCrs.equals(wktCrs2, ComparisonMode.STRICT));
        assertEquals("equals",   wktCrs, wktCrs2);
        assertSame  ("identity", wktCrs, wktCrs2);
    }
}
