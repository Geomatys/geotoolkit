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
package org.geotoolkit.coverage.processing;

import java.awt.image.Raster;
import java.awt.image.RenderedImage;
import javax.media.jai.OperationNode;

import org.opengis.coverage.grid.GridCoverage;
import org.geotoolkit.coverage.grid.SampleCoverage;
import static org.geotoolkit.coverage.grid.ViewType.*;

import org.junit.*;
import static org.junit.Assert.*;


/**
 * Tests JAI operation wrapped as {@link OperatorJAI}.
 *
 * @author Martin Desruisseaux (IRD)
 * @version 3.02
 *
 * @since 2.1
 */
public final strictfp class OperationsTest extends GridProcessingTestBase {
    /**
     * The grid coverage processor.
     */
    private Operations processor;

    /**
     * Creates a new test suite.
     */
    public OperationsTest() {
        super(Operations.class);
    }

    /**
     * Fetches the processor before each test.
     */
    @Before
    public void setUp() {
        processor = Operations.DEFAULT;
        loadSampleCoverage(SampleCoverage.SST);
    }

    /**
     * Tests {@link Operations#subtract}.
     *
     * @todo Investigate why the color palette is lost.
     */
    @Test
    public void testSubtract() {
        double[]      constants      = new double[] {18.75};
        GridCoverage  sourceCoverage = coverage.view(GEOPHYSICS);
        GridCoverage  targetCoverage = (GridCoverage) processor.subtract(sourceCoverage, constants);
        RenderedImage sourceImage    = sourceCoverage.getRenderableImage(0,1).createDefaultRendering();
        RenderedImage targetImage    = targetCoverage.getRenderableImage(0,1).createDefaultRendering();
        Raster        sourceRaster   = sourceImage.getData();
        Raster        targetRaster   = targetImage.getData();
        assertNotSame(sourceCoverage,                                targetCoverage);
        assertNotSame(sourceImage,                                   targetImage);
        assertNotSame(sourceRaster,                                  targetRaster);
        assertSame   (sourceCoverage.getCoordinateReferenceSystem(), targetCoverage.getCoordinateReferenceSystem());
        assertEquals (sourceCoverage.getEnvelope(),                  targetCoverage.getEnvelope());
        assertEquals (sourceCoverage.getGridGeometry(),              targetCoverage.getGridGeometry());
        assertEquals (sourceRaster  .getMinX(),                      targetRaster  .getMinX());
        assertEquals (sourceRaster  .getMinY(),                      targetRaster  .getMinY());
        assertEquals (sourceRaster  .getWidth(),                     targetRaster  .getWidth());
        assertEquals (sourceRaster  .getHeight(),                    targetRaster  .getHeight());
        assertEquals (0, sourceRaster.getMinX());
        assertEquals (0, sourceRaster.getMinY());
        assertEquals ("SubtractConst", ((OperationNode) targetImage).getOperationName());

        for (int y=sourceRaster.getHeight(); --y>=0;) {
            for (int x=sourceRaster.getWidth(); --x>=0;) {
                final float s = sourceRaster.getSampleFloat(x, y, 0);
                final float t = targetRaster.getSampleFloat(x, y, 0);
                if (Float.isNaN(s)) {
                    /*
                     * For a mysterious reason (JAI bug?), the following test seems to fail when
                     * JAI is running in pure Java mode. If you get an assertion failure on this
                     * line, then make sure that "<your_jdk_path>/jre/bin/mlib_jai.dll" (Windows)
                     * or "lib/i386/libmlib_jai.so" (Linux) is presents in your JDK installation.
                     */
                    if (false) // Test disabled, because we are now planing to get ride of JAI.
                    assertTrue("This assertion is know to fail when JAI is running in pure Java mode.\n" +
                               "Please make sure that \"<your_jdk_path>/jre/bin/mlib_jai.dll\" (Windows)\n" +
                               "or \"lib/i386/libmlib_jai.so\" (Linux) is presents in your JDK installation.", Float.isNaN(t));
                } else {
                    assertEquals(s - constants[0], t, 1E-3f);
                }
            }
        }
        show(targetCoverage);
    }

    /**
     * Tests {@link Operations#nodataFilter}.
     */
    @Test
    public void testNodataFilter() {
        GridCoverage  sourceCoverage = coverage.view(GEOPHYSICS);
        GridCoverage  targetCoverage = processor.nodataFilter(sourceCoverage);
        RenderedImage sourceImage    = sourceCoverage.getRenderableImage(0,1).createDefaultRendering();
        RenderedImage targetImage    = targetCoverage.getRenderableImage(0,1).createDefaultRendering();
        Raster        sourceRaster   = sourceImage.getData();
        Raster        targetRaster   = targetImage.getData();
        assertNotSame(sourceCoverage,                                targetCoverage);
        assertNotSame(sourceImage,                                   targetImage);
        assertNotSame(sourceRaster,                                  targetRaster);
        assertSame   (sourceCoverage.getCoordinateReferenceSystem(), targetCoverage.getCoordinateReferenceSystem());
        assertEquals (sourceCoverage.getEnvelope(),                  targetCoverage.getEnvelope());
        assertEquals (sourceCoverage.getGridGeometry(),              targetCoverage.getGridGeometry());
        assertEquals (sourceRaster  .getMinX(),                      targetRaster  .getMinX());
        assertEquals (sourceRaster  .getMinY(),                      targetRaster  .getMinY());
        assertEquals (sourceRaster  .getWidth(),                     targetRaster  .getWidth());
        assertEquals (sourceRaster  .getHeight(),                    targetRaster  .getHeight());
        assertEquals (0, sourceRaster.getMinX());
        assertEquals (0, sourceRaster.getMinY());
        assertEquals ("org.geotoolkit.NodataFilter", ((OperationNode) targetImage).getOperationName());

        for (int y=sourceRaster.getHeight(); --y>=0;) {
            for (int x=sourceRaster.getWidth(); --x>=0;) {
                final float s = sourceRaster.getSampleFloat(x, y, 0);
                final float t = targetRaster.getSampleFloat(x, y, 0);
                if (Float.isNaN(s)) {
                    if (!Float.isNaN(t)) {
                        // TODO: put some test here.
                    }
                } else {
                    assertEquals(s, t, 1E-5f);
                }
            }
        }
        show(targetCoverage);
    }

    /**
     * Tests {@link Operations#gradientMagnitude}.
     *
     * @todo Investigate why the geophysics view is much more visible than the non-geophysics one.
     */
    @Test
    public void testGradientMagnitude() {
        GridCoverage  sourceCoverage = coverage.view(GEOPHYSICS);
        GridCoverage  targetCoverage = (GridCoverage) processor.gradientMagnitude(sourceCoverage);
        RenderedImage sourceImage    = sourceCoverage.getRenderableImage(0,1).createDefaultRendering();
        RenderedImage targetImage    = targetCoverage.getRenderableImage(0,1).createDefaultRendering();
        Raster        sourceRaster   = sourceImage.getData();
        Raster        targetRaster   = targetImage.getData();
        assertNotSame(sourceCoverage,                                targetCoverage);
        assertNotSame(sourceImage,                                   targetImage);
        assertNotSame(sourceRaster,                                  targetRaster);
        assertSame   (sourceCoverage.getCoordinateReferenceSystem(), targetCoverage.getCoordinateReferenceSystem());
        assertEquals (sourceCoverage.getEnvelope(),                  targetCoverage.getEnvelope());
        assertEquals (sourceCoverage.getGridGeometry(),              targetCoverage.getGridGeometry());
        assertEquals (sourceRaster  .getMinX(),                      targetRaster  .getMinX());
        assertEquals (sourceRaster  .getMinY(),                      targetRaster  .getMinY());
        assertEquals (sourceRaster  .getWidth(),                     targetRaster  .getWidth());
        assertEquals (sourceRaster  .getHeight(),                    targetRaster  .getHeight());
        assertEquals (0, sourceRaster.getMinX());
        assertEquals (0, sourceRaster.getMinY());
        assertEquals ("GradientMagnitude", ((OperationNode) targetImage).getOperationName());

        assertEquals(3.95f, targetRaster.getSampleFloat(304, 310, 0), 1E-2f);
        assertEquals(1.88f, targetRaster.getSampleFloat(262, 357, 0), 1E-2f);

        show(targetCoverage);
    }
}
