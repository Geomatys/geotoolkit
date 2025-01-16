/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2023, Geomatys
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
package org.geotoolkit.display2d.canvas;

import java.awt.Rectangle;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import org.apache.sis.coverage.grid.GridExtent;
import org.apache.sis.coverage.grid.GridGeometry;
import org.apache.sis.geometry.GeneralEnvelope;
import org.apache.sis.referencing.operation.builder.LinearTransformBuilder;
import org.apache.sis.referencing.operation.transform.MathTransforms;
import org.opengis.geometry.DirectPosition;
import org.opengis.coordinate.MismatchedDimensionException;
import org.opengis.referencing.operation.MathTransform;
import org.opengis.referencing.operation.MathTransform2D;
import org.opengis.referencing.operation.Matrix;
import org.opengis.referencing.operation.NoninvertibleTransformException;
import org.opengis.referencing.operation.TransformException;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public final class GridOptimizedTransform implements MathTransform{

    private final MathTransform crsToGrid;
    private final GridGeometry gridGeometry2d;

    private final MathTransform gridApproximation;
    private MathTransform linearApproximation;

    public GridOptimizedTransform(MathTransform crsToGrid, GridGeometry gridGeometry2d) throws ClassNotFoundException, NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        this.crsToGrid = crsToGrid;
        this.gridGeometry2d = gridGeometry2d;

        final GridExtent extent = gridGeometry2d.getExtent();
        final Rectangle rect = new Rectangle((int)extent.getLow(0), (int)extent.getLow(1), (int)extent.getSize(0), (int)extent.getSize(1));

        final Class<?> clazz = Class.forName("org.apache.sis.image.ResamplingGrid");
        final Method m = clazz.getDeclaredMethod("getOrCreate", MathTransform2D.class, Rectangle.class);
        m.setAccessible(true);
        gridApproximation = (MathTransform) m.invoke(null, crsToGrid, rect);
    }

    @Override
    public int getSourceDimensions() {
        return crsToGrid.getSourceDimensions();
    }

    @Override
    public int getTargetDimensions() {
        return crsToGrid.getTargetDimensions();
    }

    private MathTransform getFallback() throws TransformException {
        if (linearApproximation == null) {
            try {
                final GeneralEnvelope env = gridGeometry2d.getExtent().toEnvelope(MathTransforms.translation(0.0,0.0));
                linearApproximation = LinearTransformBuilder.approximate(crsToGrid.inverse(), env).inverse();
            } catch (Exception ex) {
                linearApproximation = crsToGrid;
            }
        }
        return linearApproximation;
    }

    @Override
    public DirectPosition transform(DirectPosition ptSrc, DirectPosition ptDst) throws MismatchedDimensionException, TransformException {
        try {
            return gridApproximation.transform(ptSrc, ptDst);
        } catch (TransformException ex) {
            return getFallback().transform(ptSrc, ptDst);
        }
    }

    @Override
    public void transform(double[] srcPts, int srcOff, double[] dstPts, int dstOff, int numPts) throws TransformException {
        try {
            gridApproximation.transform(srcPts, srcOff, dstPts, dstOff, numPts);
        } catch (TransformException ex) {
            getFallback().transform(srcPts, srcOff, dstPts, dstOff, numPts);
        }
    }

    @Override
    public void transform(float[] srcPts, int srcOff, float[] dstPts, int dstOff, int numPts) throws TransformException {
        try {
            gridApproximation.transform(srcPts, srcOff, dstPts, dstOff, numPts);
        } catch (TransformException ex) {
            getFallback().transform(srcPts, srcOff, dstPts, dstOff, numPts);
        }
    }

    @Override
    public void transform(float[] srcPts, int srcOff, double[] dstPts, int dstOff, int numPts) throws TransformException {
        try {
            gridApproximation.transform(srcPts, srcOff, dstPts, dstOff, numPts);
        } catch (TransformException ex) {
            getFallback().transform(srcPts, srcOff, dstPts, dstOff, numPts);
        }
    }

    @Override
    public void transform(double[] srcPts, int srcOff, float[] dstPts, int dstOff, int numPts) throws TransformException {
        try {
            gridApproximation.transform(srcPts, srcOff, dstPts, dstOff, numPts);
        } catch (TransformException ex) {
            getFallback().transform(srcPts, srcOff, dstPts, dstOff, numPts);;
        }
    }

    @Override
    public Matrix derivative(DirectPosition point) throws MismatchedDimensionException, TransformException {
        return crsToGrid.derivative(point);
    }

    @Override
    public MathTransform inverse() throws NoninvertibleTransformException {
        return crsToGrid.inverse();
    }

    @Override
    public boolean isIdentity() {
        return crsToGrid.isIdentity();
    }

    @Override
    public String toWKT() {
        return crsToGrid.toWKT();
    }

}
