/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2011-2012, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2011-2012, Geomatys
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
package org.geotoolkit.display.shape;

import java.awt.Shape;
import java.awt.geom.Path2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.GeneralPath;
import java.awt.geom.PathIterator;
import java.awt.geom.AffineTransform;
import java.awt.geom.IllegalPathStateException;

import org.opengis.util.FactoryException;
import org.opengis.referencing.crs.ProjectedCRS;
import org.opengis.referencing.operation.MathTransform2D;
import org.opengis.referencing.operation.TransformException;

import org.apache.sis.referencing.CRS;
import org.apache.sis.referencing.operation.transform.MathTransforms;
import org.geotoolkit.test.gui.ShapeTestBase;
import org.geotoolkit.test.referencing.WKT;

import org.junit.*;


/**
 * Tests the {@link ProjectedShape} class.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.20
 *
 * @since 3.20
 */
public final strictfp class ProjectedShapeTest extends ShapeTestBase {
    /**
     * The projection to use for testing purpose.
     * This field is computed by {@link #createReferenceShape(Shape, MathTransform2D)}.
     */
    private MathTransform2D projection;

    /**
     * Transforms the given shape using the given projection, then apply a correction for fitting
     * the shape in the test viewer. This method also set the {@link #projection} to the resulting
     * concatenated transform.
     */
    private Shape createReferenceShape(final Shape shape, MathTransform2D tr) throws TransformException {
        final Path2D path = transform(shape, tr);
        Rectangle2D bounds = path.getBounds2D();
        final AffineTransform adjust = AffineTransform.getTranslateInstance(SHAPE_X, SHAPE_Y);
        adjust.scale(2*SHAPE_WIDTH / bounds.getWidth(), 2*SHAPE_HEIGHT / bounds.getHeight());
        adjust.translate(-bounds.getX(), -bounds.getY());
        path.transform(adjust);
        tr = MathTransforms.concatenate(tr, (MathTransform2D) org.geotoolkit.referencing.operation.MathTransforms.linear(adjust));
        projection = tr;
        return path;
    }

    /**
     * Projects a shape and compare with a shape projected by other means.
     * Current version is only a visual test.
     *
     * @throws FactoryException If the transform can not be created.
     * @throws TransformException If an error occurred while transforming some points.
     */
    @Test
    public void testWithLambert() throws FactoryException, TransformException {
        final ProjectedCRS crs = (ProjectedCRS) CRS.fromWKT(WKT.PROJCS_LAMBERT_CONIC_NTF);
        final GeneralPath path = new GeneralPath();
        path.moveTo(10.000000f,  10.0000000f);
        path.lineTo(56.666667f,  -6.6666667f);
        path.lineTo(56.666667f, -40.000000f);
        path.lineTo(80.000000f,  10.0000000f);
        path.lineTo(56.666667f,  60.0000000f);
        path.lineTo(56.666667f,  26.6666667f);
        path.closePath();
        final Shape reference = createReferenceShape(path, (MathTransform2D) crs.getConversionFromBase().getMathTransform());
        final Shape target = ProjectedShape.wrap(path, projection);
        show(target, reference, false);
    }

    /**
     * Creates a transformed shape by sampling an arbitrary amount of point on each line segment.
     * This shape is used as a reference for comparison with clever shapes, computed from cubic
     * approximation or other formulas. This method is not intended to be efficient.
     *
     * @param  shape The shape to transform.
     * @param  mt The math transform to apply on the shape.
     * @return The transformed shape.
     */
    public static Path2D transform(final Shape shape, final MathTransform2D mt) {
        final double flatness = ShapeUtilities.getFlatness(shape);
        final Path2D.Double path = new Path2D.Double();
        final PathIterator it = shape.getPathIterator(null, flatness);
        final double[] coords = new double[6];
        double x0=Double.NaN, y0=Double.NaN;
        try {
            while (!it.isDone()) {
                final int code = it.currentSegment(coords);
                switch (code) {
                    case PathIterator.SEG_CLOSE: {
                        path.closePath();
                        break;
                    }
                    case PathIterator.SEG_MOVETO: {
                        x0 = coords[0];
                        y0 = coords[1];
                        mt.transform(coords, 0, coords, 0, 1);
                        path.moveTo(coords[0], coords[1]);
                        break;
                    }
                    case PathIterator.SEG_LINETO: {
                        final double x = coords[0];
                        final double y = coords[1];
                        double dx = x - x0;
                        double dy = y - y0;
                        final int n = Math.max(1, (int) Math.round(Math.hypot(dx, dy) / flatness));
                        dx /= n;
                        dy /= n;
                        for (int i=n; --i>=0;) {
                            coords[0] = x0 = x - dx*i;
                            coords[1] = y0 = y - dy*i;
                            mt.transform(coords, 0, coords, 0, 1);
                            path.lineTo(coords[0], coords[1]);
                        }
                        break;
                    }
                    default: throw new AssertionError(code);
                }
                it.next();
            }
        } catch (TransformException cause) {
            IllegalPathStateException e = new IllegalPathStateException(cause.toString());
            e.initCause(cause);
            throw e;
        }
        return path;
    }
}
