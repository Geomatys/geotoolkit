/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2010-2012, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2010-2012, Geomatys
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
package org.geotoolkit.referencing.operation.transform;

import org.apache.sis.internal.referencing.j2d.AffineTransform2D;
import java.util.Arrays;
import java.awt.Rectangle;
import java.awt.geom.Rectangle2D;

import javax.media.jai.Warp;
import javax.media.jai.WarpGrid;
import javax.media.jai.WarpAffine;

import org.opengis.util.FactoryException;
import org.opengis.parameter.ParameterValueGroup;
import org.opengis.referencing.operation.MathTransform2D;
import org.opengis.referencing.operation.TransformException;
import org.opengis.referencing.operation.MathTransformFactory;

import org.apache.sis.math.Statistics;
import org.apache.sis.referencing.operation.transform.MathTransforms;
import org.geotoolkit.referencing.operation.builder.GridToEnvelopeMapper;
import org.geotoolkit.test.TestBase;

import org.apache.sis.referencing.CommonCRS;
import org.apache.sis.test.DependsOn;
import org.junit.*;
import static org.junit.Assert.*;
import static java.lang.StrictMath.*;
import org.apache.sis.geometry.Shapes2D;
import org.apache.sis.internal.system.DefaultFactories;
import org.apache.sis.io.TableAppender;


/**
 * Tests the {@link WarpFactory} class.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.16
 *
 * @since 3.16
 */
@DependsOn(WarpAdapterTest.class)
public final strictfp class WarpFactoryTest extends TestBase {
    /**
     * The tolerance threshold (in pixels) used by the {@link WarpFactory} being tested.
     */
    private static final double TOLERANCE = 0.25;

    /**
     * The location and dimension (in pixels) of a pseudo-image to be projected.
     */
    private final Rectangle imageBounds = new Rectangle(10, 20, 800, 600);

    /**
     * Tests the creation of an approximatively affine transform.
     *
     * @throws TransformException Should not happen.
     */
    @Test
    public void testAffine() throws TransformException {
        final Rectangle bounds = new Rectangle(100, 200, 300, 400);
        /*
         * Trivial case: should be detected easily by the factory.
         */
        MathTransform2D tr = new AffineTransform2D(2, 0, 0, 3, -10, -20);
        final Warp warp1 = WarpFactory.DEFAULT.create(null, tr, bounds);
        assertTrue("Expected a WarpAffine.", warp1 instanceof WarpAffine);
        final float[][] coefficients = ((WarpAffine) warp1).getCoeffs();
        assertTrue(Arrays.deepEquals(coefficients, new float[][] {
            {-10, 2, 0},
            {-20, 0, 3}
        }));
        /*
         * Hide the fact that the transform is affine. It will require
         * more work from the factory, but it should still detect that
         * the transform is affine.
         */
        tr = new PrivateTransform2D(tr);
        final Warp warp2 = WarpFactory.DEFAULT.create(null, tr, bounds);
        assertTrue("Expected a WarpAffine.", warp2 instanceof WarpAffine);
        assertTrue(Arrays.deepEquals(coefficients, ((WarpAffine) warp2).getCoeffs()));
    }

    /**
     * Creates a projection for the WGS84 ellipsoid using the default parameters.
     *
     * @param  name The projection classification.
     * @return The math transform implementing the given projection.
     */
    private static MathTransform2D createProjection(final String name) throws FactoryException {
        final MathTransformFactory factory = DefaultFactories.forBuildin(MathTransformFactory.class);
        final ParameterValueGroup param = factory.getDefaultParameters(name);
        param.parameter("semi_major").setValue(CommonCRS.WGS84.ellipsoid().getSemiMajorAxis());
        param.parameter("semi_minor").setValue(CommonCRS.WGS84.ellipsoid().getSemiMinorAxis());
        param.parameter("latitude_of_origin").setValue(20);
        return (MathTransform2D) factory.createParameterizedTransform(param);
    }

    /**
     * Wraps the given projection in a transform from the source grid to the target grid.
     *
     * @param  projection The projection to wrap.
     * @param  domain The domain in source coordinates (will be converted from geodetic to grid).
     * @return The transform from source grid to target grid.
     * @throws TransformException If an transform can not be created.
     */
    private MathTransform2D createResampling(final MathTransform2D projection, final Rectangle2D domain)
            throws TransformException
    {
        final GridToEnvelopeMapper mapper = new GridToEnvelopeMapper();
        mapper.setGridExtent(imageBounds);
        mapper.setEnvelope(domain);
        final MathTransform2D pre = (MathTransform2D)
                org.geotoolkit.referencing.operation.MathTransforms.linear(mapper.createAffineTransform());
        mapper.setEnvelope(Shapes2D.transform(projection, domain, null));
        final MathTransform2D post = (MathTransform2D)
                org.geotoolkit.referencing.operation.MathTransforms.linear(mapper.createAffineTransform()).inverse();
        return MathTransforms.concatenate(pre, projection, post);
    }

    /**
     * Compares the result of a warp with the result of a "reference" warp.
     *
     * @param  expected The "reference" warp.
     * @param  tested   The warp to test.
     */
    private void compare(final String name, final MathTransform2D transform, final Warp tested) {
        final Warp expected = new WarpAdapter(name, transform);
        final Statistics sx = new Statistics("sx");
        final Statistics sy = new Statistics("sy");
        float[] expPt = null;
        float[] tstPt = null;
        final int xmin = imageBounds.x;
        final int ymin = imageBounds.y;
        final int xmax = imageBounds.width  + xmin;
        final int ymax = imageBounds.height + ymin;
        for (int y=ymin; y<ymax; y++) {
            for (int x=xmin; x<xmax; x++) {
                expPt = expected.warpPoint(x, y, expPt);
                tstPt =   tested.warpPoint(x, y, tstPt);
                assertEquals("Expected a two-dimensional point.", 2, expPt.length);
                assertEquals("Expected a two-dimensional point.", 2, tstPt.length);
                final double dx = abs(expPt[0] - tstPt[0]);
                final double dy = abs(expPt[1] - tstPt[1]);
                if (!(dx <= TOLERANCE && dy <= TOLERANCE)) {
                    fail("Error at (" + x + ',' + y + "): expected " +
                            Arrays.toString(expPt) + " but got " +
                            Arrays.toString(tstPt) + ". Error is (" + dx + ", " + dy + ')');
                }
                sx.accept(dx);
                sy.accept(dy);
            }
        }
        if (out != null) {
            final TableAppender table = new TableAppender();
            table.setMultiLinesCells(true);
            table.appendHorizontalSeparator();
            table.append(sx.toString());
            table.nextColumn();
            table.append(sy.toString());
            table.nextLine();
            table.appendHorizontalSeparator();
            out.println(name);
            out.println(table);
            out.println();
        }
    }

    /**
     * Tests using the Mercator projection.
     *
     * @throws FactoryException Should not happen.
     * @throws TransformException Should not happen.
     */
    @Test
    @Ignore("MathTransformFactory not found for unknown reason. Will be revisited in Apache SIS.")
    public void testMercator() throws FactoryException, TransformException {
        final MathTransform2D projection = createProjection("Mercator_1SP");
        final Rectangle2D.Double domain = new Rectangle2D.Double(-20, -40, 40, 80);
        /*
         * Try on a relatively large region, crossing the equator.
         */
        MathTransform2D tr = createResampling(projection, domain);
        Warp warp = WarpFactory.DEFAULT.create(null, tr, imageBounds);
        assertTrue("Expected a WarpGrid.", warp instanceof WarpGrid);
        assertEquals("The x dimension should be affine.",   1, ((WarpGrid) warp).getXNumCells());
        assertEquals("The y dimension can not be affine.", 18, ((WarpGrid) warp).getYStep());
        compare("Mercator using WarpGrid", tr, warp);
        assertSame("Warp should be cached", warp, WarpFactory.DEFAULT.create(null, tr, imageBounds));
        /*
         * Try on a smaller region. Should be optimized to the affine transform case.
         */
        domain.width = domain.height = 0.25; domain.y = 20;
        tr = createResampling(projection, domain);
        warp = WarpFactory.DEFAULT.create(null, tr, imageBounds);
        assertTrue("Expected a WarpAffine.", warp instanceof WarpAffine);
        compare("Mercator using WarpAffine", tr, warp);
        assertSame("Warp should be cached", warp, WarpFactory.DEFAULT.create(null, tr, imageBounds));
    }

    /**
     * Tests using the Lambert projection.
     *
     * @throws FactoryException Should not happen.
     * @throws TransformException Should not happen.
     */
    @Test
    @Ignore("MathTransformFactory not found for unknown reason. Will be revisited in Apache SIS.")
    public void testLambert() throws FactoryException, TransformException {
        final MathTransform2D projection = createProjection("Lambert_Conformal_Conic_1SP");
        final Rectangle2D.Double domain = new Rectangle2D.Double(-20, 40, 40, 20);
        /*
         * Try on a relatively large region, crossing the equator.
         */
        MathTransform2D tr = createResampling(projection, domain);
        Warp warp = WarpFactory.DEFAULT.create(null, tr, imageBounds);
        assertTrue("Expected a WarpGrid.", warp instanceof WarpGrid);
        assertEquals("The x dimension can not be affine.", 16, ((WarpGrid) warp).getXNumCells());
        assertEquals("The y dimension can not be affine.", 37, ((WarpGrid) warp).getYStep());
        compare("Lambert using WarpGrid", tr, warp);
        assertSame("Warp should be cached", warp, WarpFactory.DEFAULT.create(null, tr, imageBounds));
        /*
         * Try on a smaller region. Should be optimized to the affine transform case.
         */
        domain.width = domain.height = 0.125; domain.y = 50;
        tr = createResampling(projection, domain);
        warp = WarpFactory.DEFAULT.create(null, tr, imageBounds);
        assertTrue("Expected a WarpAffine.", warp instanceof WarpAffine);
        compare("Lambert using WarpAffine", tr, warp);
        assertSame("Warp should be cached", warp, WarpFactory.DEFAULT.create(null, tr, imageBounds));
    }
}
