/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2011, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2011, Geomatys
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
import java.awt.geom.Rectangle2D;
import java.awt.geom.AffineTransform;

import org.opengis.util.FactoryException;
import org.opengis.referencing.crs.ProjectedCRS;
import org.opengis.referencing.operation.MathTransform2D;
import org.opengis.referencing.operation.TransformException;

import org.geotoolkit.referencing.CRS;
import org.geotoolkit.referencing.operation.transform.ConcatenatedTransform;
import org.geotoolkit.referencing.operation.transform.ProjectiveTransform;
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
    private Shape createReferenceShape(Shape shape, MathTransform2D tr) throws TransformException {
        shape = tr.createTransformedShape(shape);
        Rectangle2D bounds = shape.getBounds2D();
        final AffineTransform adjust = AffineTransform.getTranslateInstance(SHAPE_X, SHAPE_Y);
        adjust.scale(2*SHAPE_WIDTH / bounds.getWidth(), 2*SHAPE_HEIGHT / bounds.getHeight());
        adjust.translate(-bounds.getX(), -bounds.getY());
        shape = adjust.createTransformedShape(shape);
        tr = ConcatenatedTransform.create(tr, (MathTransform2D) ProjectiveTransform.create(adjust));
        projection = tr;
        return shape;
    }

    /**
     * Projects a shape and compare with a shape projected by other means.
     * Current version is only a visual test.
     *
     * @throws FactoryException If the transform can not be created.
     * @throws TransformException If an error occurred while transforming some points.
     */
    @Test
    public void testLambert() throws FactoryException, TransformException {
        final ProjectedCRS crs = (ProjectedCRS) CRS.parseWKT(WKT.PROJCS_LAMBERT_CONIC_NTF);
        final Shape source = new Arrow2D(10, -40, 70, 100);
        final Shape reference = createReferenceShape(source, (MathTransform2D) crs.getConversionFromBase().getMathTransform());
        final Shape target = ProjectedShape.wrap(source, projection);
        show(target, reference, false);
    }
}
