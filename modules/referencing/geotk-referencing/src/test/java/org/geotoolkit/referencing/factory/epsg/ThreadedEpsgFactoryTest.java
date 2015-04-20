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
import javax.measure.unit.Unit;

import org.opengis.referencing.*;
import org.opengis.referencing.cs.*;
import org.opengis.referencing.crs.*;
import org.opengis.referencing.datum.*;
import org.opengis.referencing.operation.*;
import org.opengis.util.FactoryException;
import org.opengis.metadata.Identifier;
import org.opengis.metadata.extent.GeographicBoundingBox;
import org.opengis.parameter.ParameterValueGroup;
import org.opengis.geometry.Envelope;

import org.geotoolkit.test.referencing.WKT;
import org.apache.sis.referencing.CRS;
import org.apache.sis.referencing.IdentifiedObjects;
import org.apache.sis.referencing.crs.AbstractCRS;
import org.apache.sis.referencing.datum.DefaultGeodeticDatum;
import org.geotoolkit.referencing.operation.AbstractCoordinateOperation;
import org.geotoolkit.referencing.factory.IdentifiedObjectFinder;
import org.geotoolkit.referencing.factory.AbstractAuthorityFactory;
import org.geotoolkit.referencing.factory.ThreadedAuthorityFactory;
import org.apache.sis.metadata.iso.extent.DefaultGeographicBoundingBox;
import org.geotoolkit.factory.AuthorityFactoryFinder;
import org.geotoolkit.internal.InternalUtilities;
import org.apache.sis.referencing.CommonCRS;
import org.apache.sis.util.ComparisonMode;

import org.junit.*;
import static org.junit.Assume.assumeNotNull;
import static org.geotoolkit.referencing.Assert.*;
import static org.geotoolkit.referencing.Commons.*;


/**
 * Tests transformations from CRS and/or operations created from the EPSG factory.
 *
 * @author Martin Desruisseaux (IRD, Geomatys)
 * @author Vadim Semenov
 * @version 3.18
 *
 * @since 2.1
 */
public final strictfp class ThreadedEpsgFactoryTest extends EpsgFactoryTestBase {
    /**
     * Small value for parameter value comparisons.
     */
    private static final double EPS = 1E-6;

    /**
     * Creates a test suite for the MS-Access database.
     */
    public ThreadedEpsgFactoryTest() {
        super(ThreadedEpsgFactory.class);
    }

    /**
     * Creates a test suite for the given factory type.
     * This is used for the test suite in other modules.
     *
     * @param type The class of the factory being tested.
     */
    protected ThreadedEpsgFactoryTest(final Class<? extends AbstractAuthorityFactory> type) {
        super(type);
    }

    /**
     * Tests creations of CRS objects.
     *
     * @throws FactoryException if an error occurred while querying the factory.
     */
    @Test
    public final void testCreationCRS() throws FactoryException {
        assumeNotNull(factory);

        CoordinateReferenceSystem crs;
        ParameterValueGroup parameters;

        crs = factory.createCoordinateReferenceSystem("4274");
        assertEquals("4274", getIdentifier(crs));
        assertTrue(crs instanceof GeographicCRS);
        assertEquals(2, crs.getCoordinateSystem().getDimension());

        crs = factory.createCoordinateReferenceSystem("EPSG:4140");
        assertEquals("4140", getIdentifier(crs));
        assertTrue(crs instanceof GeographicCRS);
        assertEquals(2, crs.getCoordinateSystem().getDimension());

        crs = factory.createCoordinateReferenceSystem("2027");
        assertEquals("2027", getIdentifier(crs));
        assertTrue(crs instanceof ProjectedCRS);
        assertEquals(2, crs.getCoordinateSystem().getDimension());
        parameters = ((ProjectedCRS) crs).getConversionFromBase().getParameterValues();
        assertEquals(   -93, parameters.parameter("central_meridian"  ).doubleValue(), EPS);
        assertEquals(     0, parameters.parameter("latitude_of_origin").doubleValue(), EPS);
        assertEquals(0.9996, parameters.parameter("scale_factor"      ).doubleValue(), EPS);
        assertEquals(500000, parameters.parameter("false_easting"     ).doubleValue(), EPS);
        assertEquals(     0, parameters.parameter("false_northing"    ).doubleValue(), EPS);

        crs = factory.createCoordinateReferenceSystem(" EPSG : 2442 ");
        assertEquals("2442", getIdentifier(crs));
        assertTrue(crs instanceof ProjectedCRS);
        assertEquals(2, crs.getCoordinateSystem().getDimension());
        parameters = ((ProjectedCRS) crs).getConversionFromBase().getParameterValues();
        assertEquals(   135, parameters.parameter("central_meridian"  ).doubleValue(), EPS);
        assertEquals(     0, parameters.parameter("latitude_of_origin").doubleValue(), EPS);
        assertEquals(     1, parameters.parameter("scale_factor"      ).doubleValue(), EPS);
        assertEquals(500000, parameters.parameter("false_easting"     ).doubleValue(), EPS);
        assertEquals(     0, parameters.parameter("false_northing"    ).doubleValue(), EPS);

        crs = factory.createCoordinateReferenceSystem("EPSG:4915");
        assertEquals("4915", getIdentifier(crs));
        assertTrue(crs instanceof GeocentricCRS);
        assertEquals(3, crs.getCoordinateSystem().getDimension());

        crs = factory.createCoordinateReferenceSystem("EPSG:4993");
        assertEquals("4993", getIdentifier(crs));
        assertTrue(crs instanceof GeographicCRS);
        assertEquals(3, crs.getCoordinateSystem().getDimension());

        crs = factory.createCoordinateReferenceSystem("EPSG:5735");
        assertEquals("5735", getIdentifier(crs));
        assertTrue(crs instanceof VerticalCRS);
        assertEquals(1, crs.getCoordinateSystem().getDimension());

        crs = factory.createCoordinateReferenceSystem("EPSG:5801");
        assertEquals("5801", getIdentifier(crs));
        assertTrue(crs instanceof EngineeringCRS);
        assertEquals(2, crs.getCoordinateSystem().getDimension());

        crs = factory.createCoordinateReferenceSystem("EPSG:7400");
        assertEquals("7400", getIdentifier(crs));
        assertTrue(crs instanceof CompoundCRS);
        assertEquals(3, crs.getCoordinateSystem().getDimension());

        // GeographicCRS without datum
        crs = factory.createCoordinateReferenceSystem("63266405");
        assertTrue(crs instanceof GeographicCRS);
        assertEquals("WGS 84 (deg)", crs.getName().getCode());
        assertEquals(2, crs.getCoordinateSystem().getDimension());

        // Google projection
        crs = factory.createCoordinateReferenceSystem("3857");
        assertTrue(crs instanceof ProjectedCRS);
        assertEquals("WGS 84 / Pseudo-Mercator", crs.getName().getCode());
        assertEquals(2, crs.getCoordinateSystem().getDimension());
        assertEquals("EPSG:1024", getOperationMethod(crs));

        // A deprecated EPSG code.
        crs = factory.createCoordinateReferenceSystem("3786");
        assertTrue(crs instanceof ProjectedCRS);
        assertEquals("World Equidistant Cylindrical (Sphere)", crs.getName().getCode());
        assertEquals(2, crs.getCoordinateSystem().getDimension());
        assertEquals("EPSG:9823", getOperationMethod(crs));

        // The undeprecated replacement of the above.
        crs = factory.createCoordinateReferenceSystem("4088");
        assertTrue(crs instanceof ProjectedCRS);
        assertEquals("World Equidistant Cylindrical (Sphere)", crs.getName().getCode());
        assertEquals(2, crs.getCoordinateSystem().getDimension());
        assertEquals("EPSG:1029", getOperationMethod(crs));

        // Lambert Azimuthal Equal Area (Spherical).
        crs = factory.createCoordinateReferenceSystem("3408");
        assertTrue(crs instanceof ProjectedCRS);
        assertEquals("NSIDC EASE-Grid North", crs.getName().getCode());
        assertEquals(2, crs.getCoordinateSystem().getDimension());
        assertEquals("EPSG:1027", getOperationMethod(crs));
    }

    /**
     * Returns the EPSG code of the operation method for the given projected CRS.
     */
    private static String getOperationMethod(final CoordinateReferenceSystem crs) {
        final Identifier id = ((ProjectedCRS) crs).getConversionFromBase().getMethod().getIdentifiers().iterator().next();
        return id.getCodeSpace() + ':' + id.getCode();
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
     * Tests the creation of CRS using name instead of primary keys.
     *
     * @throws FactoryException if an error occurred while querying the factory.
     */
    @Test
    public final void testNameUsage() throws FactoryException {
        assumeNotNull(factory);
        /*
         * Tests unit
         */
        assertSame   (factory.createUnit("9002"), factory.createUnit("foot"));
        assertNotSame(factory.createUnit("9001"), factory.createUnit("foot"));
        /*
         * Tests CRS
         */
        final CoordinateReferenceSystem primary, byName;
        primary = factory.createCoordinateReferenceSystem("27581");
        assertEquals("27581", getIdentifier(primary));
        assertTrue(primary instanceof ProjectedCRS);
        assertEquals(2, primary.getCoordinateSystem().getDimension());
        /*
         * Gets the CRS by name. It should be the same.
         */
        byName = factory.createCoordinateReferenceSystem("NTF (Paris) / France I");
        assertEquals(primary, byName);
        /*
         * Gets the CRS using 'createObject'. It will require more SQL
         * statement internally in order to determines the object type.
         */
        assertEquals(primary, factory.createObject("27581"));
        assertEquals(byName,  factory.createObject("NTF (Paris) / France I"));
        /*
         * Tests descriptions.
         */
        assertEquals("NTF (Paris) / France II", factory.getDescriptionText("27582").toString());
        /*
         * Tests fetching an object with name containing semi-colon.
         */
        final CoordinateSystem cs = factory.createCoordinateSystem(
                "Ellipsoidal 2D CS. Axes: latitude, longitude. Orientations: north, east. UoM: DMS");
        assertEquals("6411", getIdentifier(cs));
        /*
         * Tests with a unknown name. The exception should be NoSuchAuthorityCodeException
         * (some previous version wrongly threw a SQLException when using HSQL database).
         */
        try {
            factory.createGeographicCRS("WGS84");
            fail();
        } catch (NoSuchAuthorityCodeException e) {
            // This is the expected exception.
            assertEquals("WGS84", e.getAuthorityCode());
        }
    }

    /**
     * Tests the {@link AuthorityFactory#getDescriptionText} method.
     *
     * @throws FactoryException if an error occurred while querying the factory.
     */
    @Test
    public final void testDescriptionText() throws FactoryException {
        assumeNotNull(factory);

        assertEquals("World Geodetic System 1984", factory.getDescriptionText( "6326").toString(Locale.ENGLISH));
        assertEquals("Mean Sea Level",             factory.getDescriptionText( "5100").toString(Locale.ENGLISH));
        assertEquals("NTF (Paris) / Nord France",  factory.getDescriptionText("27591").toString(Locale.ENGLISH));
        assertEquals("Ellipsoidal height",         factory.getDescriptionText(   "84").toString(Locale.ENGLISH));
    }

    /**
     * Tests the {@code getAuthorityCodes()} method.
     * This test is very slow - the most costly parts are identified in the source code.
     *
     * @throws FactoryException if an error occurred while querying the factory.
     */
    @Test
    public final void testAuthorityCodes() throws FactoryException {
        assumeNotNull(factory);

        final Set<String> crs = factory.getAuthorityCodes(CoordinateReferenceSystem.class);
        assertFalse(crs.isEmpty());
        assertEquals("Check size() consistency", crs.size(), crs.size());
        assertTrue(crs.size() > 0); // Must be after the 'assertEquals' above.

        final Set<String> geographicCRS = factory.getAuthorityCodes(GeographicCRS.class);
        assertTrue (geographicCRS instanceof AuthorityCodes);
        assertFalse(geographicCRS.isEmpty());
        assertTrue (geographicCRS.size() > 0);
        assertTrue (geographicCRS.size() < crs.size());
        assertFalse(geographicCRS.containsAll(crs));
        assertTrue (crs.containsAll(geographicCRS));

        // BEGIN COSTLY TEST (1)
        // ----------------------------------------------------------------------------
        final Set<String> projectedCRS = factory.getAuthorityCodes(ProjectedCRS.class);
        assertTrue (projectedCRS instanceof AuthorityCodes);
        assertFalse(projectedCRS.isEmpty());
        assertTrue (projectedCRS.size() > 0);
        assertTrue (projectedCRS.size() < crs.size());
        assertFalse(projectedCRS.containsAll(crs));
        assertTrue (crs.containsAll(projectedCRS));
        assertTrue(Collections.disjoint(geographicCRS, projectedCRS));
        // ----------------------------------------------------------------------------
        // END COSTLY TEST (1)

        final Set<String> datum = factory.getAuthorityCodes(Datum.class);
        assertTrue (datum instanceof AuthorityCodes);
        assertFalse(datum.isEmpty());
        assertTrue (datum.size() > 0);
        if (false) {
            // The two sets were distincts prior EPSG version 7.06.
            // Starting from 7.06, there is some overlaps.
            assertTrue(Collections.disjoint(datum, crs));
        } else {
            final Set<String> intersect = new HashSet<>(crs);
            intersect.retainAll(datum);
            final int size = intersect.size();
            assertTrue("CRS set size",   size <= crs.size());
            assertTrue("Datum set size", size <= datum.size());
            assertTrue("Number of overlaps: " + size, size <= 51);
            assertTrue("CRS set sanity",   crs  .containsAll(intersect));
            assertTrue("Datum set sanity", datum.containsAll(intersect));
        }

        final Set<String> geodeticDatum = factory.getAuthorityCodes(GeodeticDatum.class);
        assertTrue (geodeticDatum instanceof AuthorityCodes);
        assertFalse(geodeticDatum.isEmpty());
        assertTrue (geodeticDatum.size() > 0);
        assertFalse(geodeticDatum.containsAll(datum));
        assertTrue (datum.containsAll(geodeticDatum));

        if (!((ThreadedAuthorityFactory) factory).isActive()) {
            ThreadedAuthorityFactory.LOGGER.warning("SKIPPED THREADED TEST\n" +
                "A test has been skipped because the backing store (DirectEpsgFactory) has been\n" +
                "disposed after its timeout. The cache content is lost, causing 'assertSame' to\n" +
                "fail.  Testing with 'assertEquals' and a new backing store would be slow since\n" +
                "it would trig a new scan of the database. If you are testing a database through\n" +
                "the network, try to run the test when there is no network latency.");
            // Note: the above check is not always sufficient for preventing test failure since
            //       there is a window of vulnerability between that check and the execution of
            //       factory.getAuthorityCodes(...) method.
        } else {
            // Ensures that the factory keept the set in its cache.
            assertSame(crs,           factory.getAuthorityCodes(CoordinateReferenceSystem.class));
            assertSame(geographicCRS, factory.getAuthorityCodes(            GeographicCRS.class));
            assertSame(projectedCRS,  factory.getAuthorityCodes(             ProjectedCRS.class));
            assertSame(datum,         factory.getAuthorityCodes(                    Datum.class));
            assertSame(geodeticDatum, factory.getAuthorityCodes(            GeodeticDatum.class));
            assertSame(geodeticDatum, factory.getAuthorityCodes(     DefaultGeodeticDatum.class));
        }

        // Try a dummy type.
        @SuppressWarnings({"unchecked","rawtypes"})
        final Class<? extends IdentifiedObject> wrong = (Class) String.class;
        assertTrue("Dummy type", factory.getAuthorityCodes(wrong).isEmpty());

        // Tests projections, which are handle in a special way.
        final Set<String> operations      = factory.getAuthorityCodes(SingleOperation.class);
        final Set<String> conversions     = factory.getAuthorityCodes(Conversion     .class);
        final Set<String> projections     = factory.getAuthorityCodes(Projection     .class);
        final Set<String> transformations = factory.getAuthorityCodes(Transformation .class);

        assertTrue (operations      instanceof AuthorityCodes);
        assertTrue (conversions     instanceof AuthorityCodes);
        assertTrue (projections     instanceof AuthorityCodes);
        assertTrue (transformations instanceof AuthorityCodes);

        // BEGIN COSTLY TEST (2)
        // ----------------------------------------------------------------------------
        assertTrue (conversions    .size() < operations .size());
        assertTrue (projections    .size() < operations .size());
        assertTrue (transformations.size() < operations .size());
        assertTrue (projections    .size() < conversions.size());

        assertFalse(projections.containsAll(conversions));
        assertTrue (conversions.containsAll(projections));
        assertTrue (operations .containsAll(conversions));
        assertTrue (operations .containsAll(transformations));

        assertTrue (Collections.disjoint(conversions, transformations));
        assertFalse(Collections.disjoint(conversions, projections));
        // ----------------------------------------------------------------------------
        // END COSTLY TEST (2)

        assertFalse(operations     .isEmpty());
        assertFalse(conversions    .isEmpty());
        assertFalse(projections    .isEmpty());
        assertFalse(transformations.isEmpty());

        assertTrue (conversions.contains("101"));
        assertFalse(projections.contains("101"));
        assertTrue (projections.contains("16001"));

        // We are cheating here since we are breaking generic type check.
        // However in the particular case of our EPSG factory, it works.
        @SuppressWarnings({"unchecked","rawtypes"})
        final Set<?> units = factory.getAuthorityCodes((Class) Unit.class);
        assertTrue (units instanceof AuthorityCodes);
        assertFalse(units.isEmpty());
        assertTrue (units.size() > 0);

        // Tests the fusion of all types
        final Set<String> all = factory.getAuthorityCodes(IdentifiedObject.class);
        assertFalse(all instanceof AuthorityCodes); // Usually a HashSet.
        assertTrue (all.containsAll(crs));
        assertTrue (all.containsAll(datum));
        assertTrue (all.containsAll(operations));
        assertFalse(all.containsAll(units));  // They are not IdentifiedObjects.
    }

    /**
     * Tests {@link CRS#getEnvelope} and {@link CRS#getGeographicBoundingBox}.
     *
     * @throws FactoryException if an error occurred while querying the factory.
     * @throws TransformException if a coordinate can't be transformed.
     */
    @Test
    public final void testValidArea() throws FactoryException, TransformException {
        assumeNotNull(factory);

        final CoordinateReferenceSystem crs = factory.createCoordinateReferenceSystem("7400");
        final GeographicBoundingBox bbox = CRS.getGeographicBoundingBox(crs);
        assertNotNull("No bounding box. Maybe an older EPSG database is running?", bbox);
        assertEquals(42.25, bbox.getSouthBoundLatitude(), EPS);
        assertEquals(51.10, bbox.getNorthBoundLatitude(), EPS);
        assertEquals(-5.20, bbox.getWestBoundLongitude(), EPS);
        assertEquals( 8.23, bbox.getEastBoundLongitude(), EPS);

        final Envelope envelope = org.geotoolkit.referencing.CRS.getEnvelope(crs);
        assertEquals(46.944, envelope.getMinimum(0), 1E-3);
        assertEquals(56.777, envelope.getMaximum(0), 1E-3);
        assertEquals(-8.375, envelope.getMinimum(1), 1E-3);
        assertEquals( 6.548, envelope.getMaximum(1), 1E-3);
        assertNull(org.geotoolkit.referencing.CRS.getEnvelope(null));

        final DefaultGeographicBoundingBox rep = new DefaultGeographicBoundingBox();
        rep.setBounds(envelope);
        assertEquals(42.25, rep.getSouthBoundLatitude(), 1E-3);
        assertEquals(51.10, rep.getNorthBoundLatitude(), 1E-3);
        assertEquals(-5.20, rep.getWestBoundLongitude(), 1E-3);
        assertEquals( 8.23, rep.getEastBoundLongitude(), 1E-3);
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
     * Tests the creation of {@link Conversion} objects.
     *
     * @throws FactoryException if an error occurred while querying the factory.
     */
    @Test
    public final void testConversions() throws FactoryException {
        assumeNotNull(factory);
        /*
         * Fetch directly the "UTM zone 10N" operation. Because this
         * operation was not obtained in the context of a projected CRS,
         * the source and target CRS shall be unspecified (i.e. null).
         */
        final CoordinateOperation operation = factory.createCoordinateOperation("16010");
        assertEquals("16010", getIdentifier(operation));
        assertTrue(operation instanceof Conversion);
        assertNull(operation.getSourceCRS());
        assertNull(operation.getTargetCRS());
        assertNull(operation.getMathTransform());
        /*
         * Fetch the "WGS 72 / UTM zone 10N" projected CRS.
         * The operation associated to this CRS should now
         * define the source and target CRS.
         */
        final ProjectedCRS crs = factory.createProjectedCRS("32210");
        final CoordinateOperation projection = crs.getConversionFromBase();
        assertEquals("32210", getIdentifier(crs));
        assertEquals("16010", getIdentifier(projection));
        assertTrue   (projection instanceof Projection);
        assertNotNull(projection.getSourceCRS());
        assertNotNull(projection.getTargetCRS());
        assertNotNull(projection.getMathTransform());
        assertNotSame(projection, operation);
        /*
         * Compare the conversion obtained directly with the conversion obtained
         * indirectly through a projected CRS. Both should use the same method.
         */
        final OperationMethod copMethod = ((Conversion) operation) .getMethod();
        final OperationMethod crsMethod = ((Conversion) projection).getMethod();
        assertEquals("9807", getIdentifier(copMethod));
        assertEquals("9807", getIdentifier(crsMethod));
        assertEquals(copMethod.hashCode(), crsMethod.hashCode());
        assertEquals(copMethod, crsMethod);
        try {
            assertSame(copMethod, crsMethod);
            assertSame(copMethod, factory.createOperationMethod("9807"));
        } catch (AssertionError error) {
            System.out.println(
                    "The following contains more information about a JUnit test failure.\n" +
                    "See the JUnit report for the stack trace. Below is a cache dump.\n" +
                    "See the operation method EPSG:9807 and compare with:\n" +
                    "  - Method obtained directly:   " + InternalUtilities.identity(copMethod) + "\n" +
                    "  - Method obtained indirectly: " + InternalUtilities.identity(crsMethod));
            ((ThreadedEpsgFactory) factory).printCacheContent(null);
            throw error;
        }
        /*
         * WGS 72BE / UTM zone 10N
         */
        assertNotDeepEquals(crs, factory.createProjectedCRS("32410"));
        /*
         * Creates a projected CRS from base and projected CRS codes.
         */
        final Set<CoordinateOperation> all = factory.createFromCoordinateReferenceSystemCodes("4322", "32210");
        assertEquals(1, all.size());
        assertTrue(all.contains(projection));
    }

    /**
     * Tests the creation of {@link Transformation} objects.
     *
     * @throws FactoryException if an error occurred while querying the factory.
     */
    @Test
    public final void testTransformations() throws FactoryException {
        assumeNotNull(factory);
        /*
         * Longitude rotation
         */
        assertTrue(factory.createCoordinateOperation("1764") instanceof Transformation);
        /*
         * ED50 (4230)  -->  WGS 84 (4326)  using
         * Geocentric translations (9603).
         * Accuracy = 2.5
         */
        final CoordinateOperation      operation1 = factory.createCoordinateOperation("1087");
        final CoordinateReferenceSystem sourceCRS = operation1.getSourceCRS();
        final CoordinateReferenceSystem targetCRS = operation1.getTargetCRS();
        final MathTransform             transform = operation1.getMathTransform();
        assertEquals("1087", getIdentifier(operation1));
        assertEquals("4230", getIdentifier(sourceCRS));
        assertEquals("4326", getIdentifier(targetCRS));
        assertTrue   (operation1 instanceof Transformation);
        assertNotSame(sourceCRS, targetCRS);
        assertFalse  (operation1.getMathTransform().isIdentity());
        if (isEpsgDatabaseUpToDate()) {
            // EPSG databases before version 7.6 declared a precision of 999.
            assertEquals(2.5, AbstractCoordinateOperation.getAccuracy(operation1), 1E-6);
        }
        /*
         * ED50 (4230)  -->  WGS 84 (4326)  using
         * Position Vector 7-param. transformation (9606).
         * Accuracy = 1.5
         */
        final CoordinateOperation operation2 = factory.createCoordinateOperation("1631");
        assertEquals("1631", getIdentifier(operation2));
        assertTrue (operation2 instanceof Transformation);
        assertSame (sourceCRS, operation2.getSourceCRS());
        assertSame (targetCRS, operation2.getTargetCRS());
        assertFalse(operation2.getMathTransform().isIdentity());
        assertFalse(transform.equals(operation2.getMathTransform()));
        assertEquals(1.5, AbstractCoordinateOperation.getAccuracy(operation2), 1E-6);
        /*
         * ED50 (4230)  -->  WGS 84 (4326)  using
         * Coordinate Frame rotation (9607).
         * Accuracy = 1.0
         */
        final CoordinateOperation operation3 = factory.createCoordinateOperation("1989");
        assertEquals("1989", getIdentifier(operation3));
        assertTrue (operation3 instanceof Transformation);
        assertSame (sourceCRS, operation3.getSourceCRS());
        assertSame (targetCRS, operation3.getTargetCRS());
        assertFalse(operation3.getMathTransform().isIdentity());
        assertFalse(transform.equals(operation3.getMathTransform()));
        assertEquals(1.0, AbstractCoordinateOperation.getAccuracy(operation3), 1E-6);
        if (false) {
            System.out.println(operation3);
            System.out.println(operation3.getSourceCRS());
            System.out.println(operation3.getTargetCRS());
            System.out.println(operation3.getMathTransform());
        }
        /*
         * Tests "BD72 to WGS 84 (1)" (EPSG:1609) creation. This one has an unusual unit for the
         * "Scale difference" parameter (EPSG:8611). The value is 0.999999 and the unit is "unity"
         * (EPSG:9201) instead of the usual "parts per million" (EPSG:9202). It was used to thrown
         * an exception in older EPSG factory implementations.
         */
        assertEquals(1.0, AbstractCoordinateOperation.getAccuracy(factory.createCoordinateOperation("1609")), 1E-6);
        /*
         * Creates from CRS codes. There is 40 such operations in EPSG version 6.7.
         * The preferred one (according the "supersession" table) is EPSG:1612.
         *
         * Note: the above assertion fails on PostgreSQL because its "ORDER BY" clause put null
         * values last, while Access and HSQL put them first. The PostgreSQL behavior is better
         * for what we want (operations with unknown accuracy last). Unfortunately, I don't know
         * yet how to instructs Access to put null values last using standard SQL ("IIF" is not
         * standard, and Access doesn't seem to understand "CASE ... THEN" clauses).
         */
        final Set<CoordinateOperation> all = factory.createFromCoordinateReferenceSystemCodes("4230", "4326");
        assertTrue(all.size() >= 3);
        assertTrue(all.contains(operation1));
        assertTrue(all.contains(operation2));
        assertTrue(all.contains(operation3));
        int count=0;
        for (final CoordinateOperation check : all) {
            assertSame(sourceCRS, check.getSourceCRS());
            assertSame(targetCRS, check.getTargetCRS());
            if (count++ == 0) {
                assertEquals("1612", getIdentifier(check)); // see comment above.
            }
        }
        assertEquals(all.size(), count);
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
            final double accuracy = AbstractCoordinateOperation.getAccuracy(operation);
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
     * Compares a WKT found in the field with the EPSG equivalent.
     *
     * @throws FactoryException if an error occurred while querying the factory.
     *
     * @see <a href="http://jira.codehaus.org/browse/GEOT-1268">GEOT-1268</a>
     */
    @Test
    @Ignore
    public final void testEqualsApproximatively() throws FactoryException {
        assumeNotNull(factory);
        final CoordinateReferenceSystem crs1 = org.geotoolkit.referencing.CRS.parseWKT(WKT.PROJCS_LAMBERT_CONIC_NAD83);
        final CoordinateReferenceSystem crs2 = org.geotoolkit.referencing.CRS.decode("EPSG:26986");
        assertEqualsApproximatively(crs1, crs2, true);
    }

    /**
     * Tests {@link ThreadedEpsgFactory#find} method with a geographic CRS.
     *
     * @throws FactoryException if an error occurred while querying the factory.
     */
    @Test
    public final void testFind() throws FactoryException {
        assumeNotNull(factory);
        /*
         * Test initial conditions.
         */
        final IdentifiedObjectFinder finder = factory.getIdentifiedObjectFinder(CoordinateReferenceSystem.class);
        assertTrue("Full scan should be enabled by default.", finder.isFullScanAllowed());
        assertNull("Should not find WGS84 because the axis order is not the same.",
                   finder.find(CommonCRS.WGS84.normalizedGeographic()));
        /*
         * Tests that the cache is empty.
         */
        final CoordinateReferenceSystem crs = org.geotoolkit.referencing.CRS.parseWKT(WKT.GEOGCS_WGS84_YX);
        finder.setFullScanAllowed(false);
        assertNull("Should not find without a full scan, because the WKT contains no identifier " +
                   "and the CRS name is ambiguous (more than one EPSG object have this name).",
                   finder.find(crs));
        /*
         * Scan the database for searching the CRS.
         */
        finder.setFullScanAllowed(true);
        final IdentifiedObject find = finder.find(crs);
        assertNotNull("With full scan allowed, the CRS should be found.", find);
        assertEqualsIgnoreMetadata(crs, find, false);
        assertEquals("Not the expected CRS.", "4326",
                IdentifiedObjects.getIdentifier(find, factory.getAuthority()).getCode());
        /*
         * Should find the CRS without the need of a full scan, because of the cache.
         */
        finder.setFullScanAllowed(false);
        assertSame("The CRS should still in the cache.", find, finder.find(crs));
    }

    /**
     * Tests {@link ThreadedEpsgFactory#find} method with a projected CRS.
     *
     * @throws FactoryException if an error occurred while querying the factory.
     */
    @Test
    @Ignore
    public final void testFindProjectedCRS() throws FactoryException {
        assumeNotNull(factory);
        final IdentifiedObjectFinder finder = factory.getIdentifiedObjectFinder(CoordinateReferenceSystem.class);
        /*
         * The PROJCS below intentionally uses a name different from the one found in the
         * EPSG database, in order to force a full scan (otherwise the EPSG database would
         * find it by name, but we want to test the scan).
         */
        final String wkt = decodeQuotes(
                "PROJCS[“Beijing 1954”,\n" +
                "   GEOGCS[“Beijing 1954”,\n" +
                "     DATUM[“Beijing 1954”,\n" +
                "       SPHEROID[“Krassowsky 1940”, 6378245.0, 298.3]],\n" +
                "     PRIMEM[“Greenwich”, 0.0],\n" +
                "     UNIT[“degree”, 0.017453292519943295],\n" +
                "     AXIS[“Geodetic latitude”, NORTH],\n" +
                "     AXIS[“Geodetic longitude”, EAST]],\n" +
                "   PROJECTION[“Transverse Mercator\"],\n" +
                "   PARAMETER[“central_meridian”, 135.0],\n" +
                "   PARAMETER[“latitude_of_origin”, 0.0],\n" +
                "   PARAMETER[“scale_factor”, 1.0],\n" +
                "   PARAMETER[“false_easting”, 500000.0],\n" +
                "   PARAMETER[“false_northing”, 0.0],\n" +
                "   UNIT[“m”, 1.0],\n" +
                "   AXIS[“Northing”, NORTH],\n" +
                "   AXIS[“Easting”, EAST]]");
        final CoordinateReferenceSystem crs = org.geotoolkit.referencing.CRS.parseWKT(wkt);

        finder.setFullScanAllowed(false);
        assertNull("Should not find the CRS without a full scan.", finder.find(crs));

        finder.setFullScanAllowed(true);
        final IdentifiedObject find = finder.find(crs);
        assertNotNull("With full scan allowed, the CRS should be found.", find);
        assertEqualsIgnoreMetadata(crs, find, false);
        /*
         * Both EPSG:2442 and EPSG:21463 defines the same projection with the same parameters
         * and the same base GeographicCRS (EPSG:4214). The only difference I found was the
         * area of validity...
         *
         * Note that there is also a EPSG:21483 code, but that one is deprecated and should
         * not be selected in this test.
         */
        final String code = IdentifiedObjects.getIdentifier(find, factory.getAuthority()).getCode();
        assertEquals("2442", code); // Finder order should be determinist, so 2442 is the selected CRS.

        finder.setFullScanAllowed(false);
        assertEquals("The CRS should still in the cache.", "EPSG:"+code, finder.findIdentifier(crs));
    }

    /**
     * Tests {@link ThreadedEpsgFactory#find} method with a CRS which is slightly different than
     * the one to look for in the database.
     *
     * @throws FactoryException if an error occurred while querying the factory.
     *
     * @see <a href="http://jira.geotoolkit.org/browse/GEOTK-62">GEOTK-62</a>
     *
     * @since 3.18
     */
    @Test
    @Ignore
    public final void testFindApproximative() throws FactoryException {
        assumeNotNull(factory);

        final String wkt = decodeQuotes(
                "PROJCS[“Search approximative”,\n" +
                "   GEOGCS[“Search approximative”,\n" +
                "     DATUM[“Beijing 1954”,\n" + // Datum name matter.
                "       SPHEROID[“Search approximative”, 6378245.00000006, 298.299999999998]],\n" +
                "     PRIMEM[“Greenwich”, 0.0],\n" +
                "     UNIT[“degree”, 0.017453292519943295],\n" +
                "     AXIS[“Geodetic latitude”, NORTH],\n" +
                "     AXIS[“Geodetic longitude”, EAST]],\n" +
                "   PROJECTION[“Transverse Mercator\"],\n" +
                "   PARAMETER[“central_meridian”, 135.0000000000013],\n" +
                "   PARAMETER[“latitude_of_origin”, 0.0],\n" +
                "   PARAMETER[“scale_factor”, 1.0],\n" +
                "   PARAMETER[“false_easting”, 500000.000000004],\n" +
                "   PARAMETER[“false_northing”, 0.0],\n" +
                "   UNIT[“m”, 1.0],\n" +
                "   AXIS[“Northing”, NORTH],\n" +
                "   AXIS[“Easting”, EAST]]");
        final CoordinateReferenceSystem crs = org.geotoolkit.referencing.CRS.parseWKT(wkt);
        final IdentifiedObjectFinder finder = factory.getIdentifiedObjectFinder(CoordinateReferenceSystem.class);
        IdentifiedObject find = finder.find(crs);
        assertNull("Should not find the CRS without approximative mode.", find);

        finder.setComparisonMode(ComparisonMode.APPROXIMATIVE);
        find = finder.find(crs);
        assertNotNull("In approximative mode, the CRS should be found.", find);
        assertEqualsApproximatively(crs, find, true);
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
    public final void testUnique() throws FactoryException {
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
