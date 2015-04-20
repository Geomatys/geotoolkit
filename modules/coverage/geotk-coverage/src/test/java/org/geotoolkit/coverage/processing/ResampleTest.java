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
package org.geotoolkit.coverage.processing;

import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.AffineTransform;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.image.RenderedImage;
import java.awt.image.renderable.ParameterBlock;
import javax.media.jai.JAI;

import org.opengis.coverage.grid.GridGeometry;
import org.opengis.util.FactoryException;
import org.opengis.util.NoSuchIdentifierException;
import org.opengis.parameter.ParameterValueGroup;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.crs.GeographicCRS;
import org.opengis.referencing.datum.Ellipsoid;
import org.opengis.referencing.operation.MathTransform;
import org.opengis.referencing.operation.OperationNotFoundException;

import org.geotoolkit.factory.Hints;
import org.geotoolkit.referencing.CRS;
import org.geotoolkit.referencing.cs.PredefinedCS;
import org.geotoolkit.referencing.crs.DefaultProjectedCRS;
import org.apache.sis.referencing.operation.transform.DefaultMathTransformFactory;
import org.geotoolkit.referencing.operation.MathTransforms;
import org.geotoolkit.coverage.CoverageFactoryFinder;
import org.geotoolkit.coverage.grid.GridCoverage2D;
import org.geotoolkit.coverage.grid.GridGeometry2D;
import org.geotoolkit.coverage.grid.GeneralGridEnvelope;
import org.geotoolkit.coverage.grid.SampleCoverage;
import org.geotoolkit.coverage.grid.ViewType;

import org.junit.*;
import static org.geotoolkit.test.Assert.*;
import static org.geotoolkit.test.Commons.*;


/**
 * Visual test of the "Resample" operation. A remote sensing image is projected from a fitted
 * coordinate system to a geographic one.
 *
 * @author Rémi Eve (IRD)
 * @author Martin Desruisseaux (IRD)
 * @version 3.02
 *
 * @since 2.1
 */
public final strictfp class ResampleTest extends GridProcessingTestBase {
    /**
     * Creates a new test suite.
     */
    public ResampleTest() {
        super(Operations.class);
    }

    /**
     * Returns a projected CRS for test purpose.
     */
    private static CoordinateReferenceSystem getProjectedCRS(final GridCoverage2D coverage) {
        try {
            final GeographicCRS  base = (GeographicCRS) coverage.getCoordinateReferenceSystem();
            final Ellipsoid ellipsoid = base.getDatum().getEllipsoid();
            final DefaultMathTransformFactory factory = new DefaultMathTransformFactory();
            final ParameterValueGroup parameters = factory.getDefaultParameters("Oblique_Stereographic");
            parameters.parameter("semi_major").setValue(ellipsoid.getSemiMajorAxis());
            parameters.parameter("semi_minor").setValue(ellipsoid.getSemiMinorAxis());
            parameters.parameter("central_meridian").setValue(5);
            parameters.parameter("latitude_of_origin").setValue(-5);
            final MathTransform mt;
            try {
                mt = factory.createParameterizedTransform(parameters);
            } catch (FactoryException exception) {
                fail(exception.getLocalizedMessage());
                return null;
            }
            return new DefaultProjectedCRS("Stereographic", base, mt, PredefinedCS.PROJECTED);
        } catch (NoSuchIdentifierException exception) {
            fail(exception.getLocalizedMessage());
            return null;
        }
    }

    /**
     * Projects the specified coverage to the same CRS without hints.
     * The result will be displayed in a window if {@link #show} is set to {@code true}.
     *
     * @param coverage  The coverage to project.
     * @return The operation name which was applied on the image, or {@code null} if none.
     */
    private String showProjected() {
        return showResampled(coverage.getCoordinateReferenceSystem(), null, null, true);
    }

    /**
     * Tests the "Resample" operation with an identity transform.
     *
     * @todo Investigate why we get a Lookup operation on the first coverage.
     */
    @Test
    public void testIdentity() {
        loadSampleCoverage(SampleCoverage.SST);
        assertEquals("Lookup", showProjected());
        loadSampleCoverage(SampleCoverage.FLOAT);
        assertNull(showProjected());
    }

    /**
     * Tests the "Resample" operation with a "Crop" transform.
     */
    @Test
    public void testCrop() {
        final GridGeometry2D g1,g2;
        final MathTransform gridToCRS = null;
        g1 = new GridGeometry2D(new GeneralGridEnvelope(new Rectangle(50,50,100,100), 2), gridToCRS, null);
        g2 = new GridGeometry2D(new GeneralGridEnvelope(new Rectangle(50,50,200,200), 2), gridToCRS, null);
        loadSampleCoverage(SampleCoverage.SST);
        assertEquals("Crop",   showResampled(null, g2, null, false));
        assertEquals("Lookup", showResampled(null, g2, null, true ));
        loadSampleCoverage(SampleCoverage.FLOAT);
        assertEquals("Crop",   showResampled(null, g1,
                new Hints(Hints.COVERAGE_PROCESSING_VIEW, ViewType.PHOTOGRAPHIC), true));
    }

    /**
     * Tests the "Resample" operation with a stereographic coordinate system.
     */
    @Test
    public void testStereographic() {
        loadSampleCoverage(SampleCoverage.SST);
        assertEquals("Warp", showResampled(getProjectedCRS(coverage), null, null, true));
    }

    /**
     * Tests the "Resample" operation with a stereographic coordinate system.
     *
     * @throws FactoryException If the CRS can't not be created.
     */
    @Test
    public void testsNad83() throws FactoryException {
        final Hints photo = new Hints(Hints.COVERAGE_PROCESSING_VIEW, ViewType.PHOTOGRAPHIC);
        final CoordinateReferenceSystem crs = CRS.parseWKT(
                "GEOGCS[\"NAD83\"," +
                  "DATUM[\"North_American_Datum_1983\"," +
                    "SPHEROID[\"GRS 1980\",6378137,298.257222101,AUTHORITY[\"EPSG\",\"7019\"]]," +
                    "TOWGS84[0,0,0,0,0,0,0],AUTHORITY[\"EPSG\",\"6269\"]]," +
                  "PRIMEM[\"Greenwich\",0, AUTHORITY[\"EPSG\",\"8901\"]]," +
                  "UNIT[\"degree\",0.0174532925199433,AUTHORITY[\"EPSG\",\"9108\"]]," +
                  "AXIS[\"Lat\",NORTH]," +
                  "AXIS[\"Long\",EAST]," +
                  "AUTHORITY[\"EPSG\",\"4269\"]]");
        loadSampleCoverage(SampleCoverage.FLOAT);
        org.junit.Assume.assumeTrue(viewEnabled);
        // Following code work correctly when displayed in a component, but
        // cause an IndexOutOfBoundsException otherwide (TODO: investigate).
        assertEquals("Warp", showResampled(crs, null, photo, true));
    }

    /**
     * Tests the "Resample" operation with an "Affine" transform.
     */
    @Test
    public void testAffine() {
        final Hints photo = new Hints(Hints.COVERAGE_PROCESSING_VIEW, ViewType.PHOTOGRAPHIC);
        loadSampleCoverage(SampleCoverage.SST);
        showTranslated(null, true,  "Lookup", "Affine");
        loadSampleCoverage(SampleCoverage.FLOAT);
        // TODO : Re-activate when we've identified why GridCoverage2D.getViewTypes() modifications impact it.
        //showTranslated(photo, false, "org.geotoolkit.SampleTranscode", "org.geotoolkit.SampleTranscode");
    }

    /**
     * Tests <var>X</var>,<var>Y</var> translation in the {@link GridGeometry} after
     * a "Resample" operation.
     *
     * @throws NoninvertibleTransformException If a "grid to CRS" transform is not invertible.
     */
    @Test
    @Ignore("There is an issue with GridGeometry2D constructor with PixelInCell value.")
    public void testTranslation() throws NoninvertibleTransformException {
        loadSampleCoverage(SampleCoverage.SST);
        final int    transX =  -253;
        final int    transY =  -456;
        final double scaleX =  0.04;
        final double scaleY = -0.04;
        final ParameterBlock block = new ParameterBlock().
                addSource(coverage.getRenderedImage()).
                add((float) transX).
                add((float) transY);
        RenderedImage image = JAI.create("Translate", block);
        assertEquals("Incorrect X translation", transX, image.getMinX());
        assertEquals("Incorrect Y translation", transY, image.getMinY());
        /*
         * Create a grid coverage from the translated image but with the same envelope.
         * Consequently, the 'gridToCoordinateSystem' should be translated by the same
         * amount, with the opposite sign.
         */
        AffineTransform expected = getAffineTransform(coverage);
        assertNotNull(expected);
        expected = new AffineTransform(expected); // Get a mutable instance.
        coverage = CoverageFactoryFinder.getGridCoverageFactory(null).create("Translated",
                image, coverage.getEnvelope(), coverage.getSampleDimensions(),
                new GridCoverage2D[]{coverage}, coverage.getProperties());
        expected.translate(-transX, -transY);
        assertTransformEquals(expected, getAffineTransform(coverage));
        /*
         * Apply the "Resample" operation with a specific 'gridToCoordinateSystem' transform.
         * The envelope is left unchanged. The "Resample" operation should compute automatically
         * new image bounds.
         */
        final AffineTransform at = AffineTransform.getScaleInstance(scaleX, scaleY);
        final MathTransform   tr = MathTransforms.linear(at);
        final GridGeometry2D geometry = new GridGeometry2D(null, tr, null);
        coverage = (GridCoverage2D) Operations.DEFAULT.resample(coverage,
                coverage.getCoordinateReferenceSystem(), geometry, null);
        assertEquals(at, getAffineTransform(coverage));
        image = coverage.getRenderedImage();
        expected.preConcatenate(at.createInverse());
        final Point point = new Point(transX, transY);
        assertSame(point, expected.transform(point, point)); // Round toward neareast integer
        assertEquals("Incorrect X translation", point.x, image.getMinX());
        assertEquals("Incorrect Y translation", point.y, image.getMinY());
    }

    /**
     * Ensures that the resampling takes in account the system-wide "lenient datum shift" hint.
     * The actual data doesn't matter for this test. We are interested only in the exception.
     *
     * @throws FactoryException Should never happen.
     *
     * @since 3.02
     */
    @Test
    @Ignore("Usage of hints will be removed in Apache SIS.")
    public void testLenientDatumShift() throws FactoryException {
        final CoordinateReferenceSystem sourceCRS = CRS.parseWKT(
                "PROJCS[\"Bessel_1841_Hotine_Oblique_Mercator_Azimuth_Natural_Origin\"," +
                "GEOGCS[\"GCS_Bessel_1841\",DATUM[\"D_Bessel_1841\"," +
                "SPHEROID[\"Bessel_1841\",6377397.155,299.1528128]]," +
                "PRIMEM[\"Greenwich\",0.0],UNIT[\"Degree\",0.0174532925199433]]," +
                "PROJECTION[\"Hotine_Oblique_Mercator_Azimuth_Natural_Origin\"]," +
                "PARAMETER[\"False_Easting\",-9419820.5907]," +
                "PARAMETER[\"False_Northing\",200000.0]," +
                "PARAMETER[\"Scale_Factor\",1.0]," +
                "PARAMETER[\"Azimuth\",90.0]," +
                "PARAMETER[\"Longitude_Of_Center\",7.439583333333333]," +
                "PARAMETER[\"Latitude_Of_Center\",46.95240555555556]," +
                "UNIT[\"Meter\",1.0]]");
        final CoordinateReferenceSystem targetCRS = CRS.parseWKT(decodeQuotes(
                "PROJCS[“ETRS89 / ETRS-LAEA”,\n" +
                "  GEOGCS[“ETRS89”,\n" +
                "    DATUM[“European Terrestrial Reference System 1989”,\n" +
                "      SPHEROID[“GRS 1980”, 6378137.0, 298.257222101, AUTHORITY[“EPSG”,“7019”]],\n" +
                "      TOWGS84[0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0],\n" +
                "      AUTHORITY[“EPSG”,“6258”]],\n" +
                "    PRIMEM[“Greenwich”, 0.0, AUTHORITY[“EPSG”,“8901”]],\n" +
                "    UNIT[“degree”, 0.017453292519943295],\n" +
                "    AXIS[“Geodetic longitude”, EAST],\n" +
                "    AXIS[“Geodetic latitude”, NORTH],\n" +
                "    AUTHORITY[“EPSG”,“4258”]],\n" +
                "  PROJECTION[“Lambert Azimuthal Equal Area”, AUTHORITY[“EPSG”,“9820”]],\n" +
                "  PARAMETER[“latitude_of_center”, 52.0],\n" +
                "  PARAMETER[“longitude_of_center”, 10.0],\n" +
                "  PARAMETER[“false_easting”, 4321000.0],\n" +
                "  PARAMETER[“false_northing”, 3210000.0],\n" +
                "  UNIT[“metre”, 1.0],\n" +
                "  AXIS[“Easting”, EAST],\n" +
                "  AXIS[“Northing”, NORTH],\n" +
                "  AUTHORITY[“EPSG”,“3035”]]"));
        createRandomCoverage(sourceCRS);
        final GridCoverage2D sample = coverage;
        try {
            resample(targetCRS, null, null, true);
            fail("Projection without Bursa-Wolf parameters should fail.");
        } catch (CannotReprojectException e) {
            // This is the exepcted exception.
            // Cause should be: "Missing Bursa-Wolf parameters".
            assertTrue(e.getCause() instanceof OperationNotFoundException);
        }
        coverage = sample;
        Hints.putSystemDefault(Hints.LENIENT_DATUM_SHIFT, Boolean.TRUE);
        try {
            resample(targetCRS, null, null, true);
            // Should not throw exception anymore.
        } finally {
            Hints.removeSystemDefault(Hints.LENIENT_DATUM_SHIFT);
        }
    }
}
