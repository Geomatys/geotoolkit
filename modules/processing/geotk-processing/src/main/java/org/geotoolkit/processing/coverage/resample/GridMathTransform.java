/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2013, Geomatys
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
package org.geotoolkit.processing.coverage.resample;

import org.apache.sis.referencing.operation.transform.AbstractMathTransform2D;
import org.opengis.referencing.operation.Matrix;
import org.opengis.referencing.operation.TransformException;


/**
 * MathTransform wrapping a TransformGrid.
 *
 * @author Johann Sorel (Geomatys)
 */
public class GridMathTransform extends AbstractMathTransform2D {

    private final TransformGrid grid;
    private final int lineLength;

    public GridMathTransform(TransformGrid grid) {
        this.grid = grid;
        lineLength = (grid.xNumCells+1)*2;
    }

    @Override
    public Matrix transform(double[] srcPts, int srcOff, double[] dstPts, int dstOff, boolean derivate) throws TransformException {
        Matrix derivative = null;
        if (derivate) {
            //TODO
        }

        final int x = (int)srcPts[srcOff];
        final int y = (int)srcPts[srcOff+1];
        final int index = ((y/grid.yStep) * (grid.xNumCells+1) + (x/grid.xStep))*2;

        // Avoid an exception if we reach outside grid.
        if (index < 0 || index + 2 + lineLength >= grid.warpPositions.length) {
            dstPts[dstOff] = Double.NaN;
            dstPts[dstOff+1] = Double.NaN;
        } else {
            final double xtl = grid.warpPositions[index               ];
            final double xtr = grid.warpPositions[index+2             ];
            final double xbl = grid.warpPositions[index   + lineLength];
            final double xbr = grid.warpPositions[index+2 + lineLength];

            final double ytl = grid.warpPositions[index+1             ];
            final double ytr = grid.warpPositions[index+3             ];
            final double ybl = grid.warpPositions[index+1 + lineLength];
            final double ybr = grid.warpPositions[index+3 + lineLength];

            double deltax = srcPts[srcOff] / grid.xStep;
            deltax -= Math.floor(deltax);
            double deltay = srcPts[srcOff+1] / grid.yStep;
            deltay -= Math.floor(deltay);

            final double xti = (xtr - xtl) * deltax + xtl;
            final double xbi = (xbr - xbl) * deltax + xbl;
            final double dx = (xbi - xti) * deltay + xti;

            final double yti = (ytr - ytl) * deltax + ytl;
            final double ybi = (ybr - ybl) * deltax + ybl;
            final double dy = (ybi - yti) * deltay + yti;

            dstPts[dstOff] = dx;
            dstPts[dstOff+1] = dy;
        }

        return derivative;
    }

}
