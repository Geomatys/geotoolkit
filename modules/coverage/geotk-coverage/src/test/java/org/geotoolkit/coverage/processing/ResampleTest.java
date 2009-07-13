/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2003-2009, Open Source Geospatial Foundation (OSGeo)
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
package org.geotoolkit.coverage.processing;

import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.AffineTransform;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.image.RenderedImage;
import java.awt.image.renderable.ParameterBlock;
import javax.media.jai.JAI;

import org.opengis.parameter.ParameterValueGroup;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.NoSuchIdentifierException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.crs.GeographicCRS;
import org.opengis.referencing.datum.Ellipsoid;
import org.opengis.referencing.operation.MathTransform;

import org.geotoolkit.factory.Hints;
import org.geotoolkit.referencing.CRS;
import org.geotoolkit.referencing.cs.DefaultCartesianCS;
import org.geotoolkit.referencing.crs.DefaultProjectedCRS;
import org.geotoolkit.referencing.operation.DefaultMathTransformFactory;
import org.geotoolkit.referencing.operation.transform.ProjectiveTransform;
import org.geotoolkit.coverage.CoverageFactoryFinder;
import org.geotoolkit.coverage.grid.GridCoverage2D;
import org.geotoolkit.coverage.grid.GridGeometry2D;
import org.geotoolkit.coverage.grid.GeneralGridEnvelope;
import org.geotoolkit.coverage.grid.SampleCoverage;
import org.geotoolkit.coverage.grid.ViewType;

import org.junit.*;
import static org.junit.Assert.*;


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
public final class ResampleTest extends GridProcessingTestCase {
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
            return new DefaultProjectedCRS("Stereographic", base, mt, DefaultCartesianCS.PROJECTED);
        } catch (NoSuchIdentifierException exception) {
            fail(exception.getLocalizedMessage());
            return null;
        }
    }

    /**
     * Projects the specified coverage to the same CRS without hints.
     * The result will be displayed in a window if {@link #SHOW} is set to {@code true}.
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
        showTranslated(photo, false, "org.geotoolkit.SampleTranscode", "org.geotoolkit.SampleTranscode");
    }

    /**
     * Tests <var>X</var>,<var>Y</var> translation in the {@link GridGeometry} after
     * a "Resample" operation.
     *
     * @throws NoninvertibleTransformException If a "grid to CRS" transform is not invertible.
     */
    @Test
    public void testTranslation() throws NoninvertibleTransformException {
        loadSampleCoverage(SampleCoverage.SST);
        doTranslation(coverage);
    }

    /**
     * Performs a translation using the "Resample" operation.
     *
     * @param grid the {@link GridCoverage2D} to apply the translation on.
     * @throws NoninvertibleTransformException If a "grid to CRS" transform is not invertible.
     */
    private void doTranslation(GridCoverage2D grid) throws NoninvertibleTransformException {
        final int    transX =  -253;
        final int    transY =  -456;
        final double scaleX =  0.04;
        final double scaleY = -0.04;
        final ParameterBlock block = new ParameterBlock().
                addSource(grid.getRenderedImage()).
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
        AffineTransform expected = getAffineTransform(grid);
        assertNotNull(expected);
        expected = new AffineTransform(expected); // Get a mutable instance.
        grid = CoverageFactoryFinder.getGridCoverageFactory(null).create("Translated",
                image, grid.getEnvelope(), grid.getSampleDimensions(),
                new GridCoverage2D[]{grid}, grid.getProperties());
        expected.translate(-transX, -transY);
        assertTransformEquals(expected, getAffineTransform(grid));
        /*
         * Apply the "Resample" operation with a specific 'gridToCoordinateSystem' transform.
         * The envelope is left unchanged. The "Resample" operation should compute automatically
         * new image bounds.
         */
        final AffineTransform at = AffineTransform.getScaleInstance(scaleX, scaleY);
        final MathTransform   tr = ProjectiveTransform.create(at);
        final GridGeometry2D geometry = new GridGeometry2D(null, tr, null);
        grid = (GridCoverage2D) Operations.DEFAULT.resample(grid,
                grid.getCoordinateReferenceSystem(), geometry, null);
        assertEquals(at, getAffineTransform(grid));
        image = grid.getRenderedImage();
        expected.preConcatenate(at.createInverse());
        final Point point = new Point(transX, transY);
        assertSame(point, expected.transform(point, point)); // Round toward neareast integer
        assertEquals("Incorrect X translation", point.x, image.getMinX());
        assertEquals("Incorrect Y translation", point.y, image.getMinY());
    }
}
