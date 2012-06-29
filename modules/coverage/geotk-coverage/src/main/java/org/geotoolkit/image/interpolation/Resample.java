/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2012, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2012, Geomatys
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
package org.geotoolkit.image.interpolation;

import java.awt.Rectangle;
import java.awt.image.WritableRenderedImage;
import org.geotoolkit.image.iterator.PixelIterator;
import org.geotoolkit.image.iterator.PixelIteratorFactory;
import org.opengis.referencing.operation.MathTransform;
import org.opengis.referencing.operation.NoninvertibleTransformException;
import org.opengis.referencing.operation.TransformException;

/**
 * Fill target image from source image pixels interpolation at coordinate define
 * by transformation of target pixel coordinate by {@code MathTransform}.
 *
 * @author Rémi Marechal       (Geomatys).
 * @author Martin Desruisseaux (Geomatys).
 */
public class Resample {

    /**
     * Transform multi-dimensional point (in our case pixel coordinate) from target image
     * {@code CoordinateReferenceSystem} to source image {@code CoordinateReferenceSystem}.
     */
    private final MathTransform invertMathTransform;

    /**
     * Image in which image source pixel interpolation result is push.
     */
    private final WritableRenderedImage imageDest;

    /**
     * Sort of interpolation use to interpolate image source pixels.
     */
    private final Interpolation interpol;

    /**
     * Image number bands.<br/>
     * Note : source and target image have same bands number.
     */
    private final int numBands;

    /**
     * Minimum inclusive pixel index from source iterate source object in X direction.
     */
    private final int minSourceX;

    /**
     * Maximum inclusive pixel index from source iterate source object in X direction.
     */
    private final int maxSourceX;

    /**
     * Minimum inclusive pixel index from source iterate source object in Y direction.
     */
    private final int minSourceY;

    /**
     * Maximum inclusive pixel index from source iterate source object in Y direction.
     */
    private final int maxSourceY;


    /**
     * Fill destination image from interpolation of source pixels.<br/>
     * Source pixel is obtained from invert transformation of destination pixel coordinates.
     *
     * @param mathTransform Transformation use to transform source point to target point.
     * @param imageDest image will be fill by image source pixel interpolation.
     * @param interpol Interpolation use to interpolate source image pixels.
     * @throws NoninvertibleTransformException if it is impossible to invert {@code MathTransform} parameter.
     */
    public Resample(MathTransform mathTransform, WritableRenderedImage imageDest, Interpolation interpol) throws NoninvertibleTransformException {
        this.numBands = interpol.getNumBands();
        assert(numBands == imageDest.getWritableTile(imageDest.getMinTileX(), imageDest.getMinTileY()).getNumBands())
                : "destination image numbands different from source image numbands";
        this.invertMathTransform = mathTransform.inverse();
        this.imageDest = imageDest;
        this.interpol = interpol;
        Rectangle bound = interpol.getBoundary();
        minSourceX = bound.x;
        minSourceY = bound.y;
        maxSourceX = minSourceX + bound.width  - 1;
        maxSourceY = minSourceY + bound.height - 1;
    }

    /**
     * Compute interpolation value from source image.
     *
     * @param x destination pixel coordinate.
     * @param y destination pixel coordinate.
     * @return interpolation value from source image.
     * @throws TransformException
     */
    private double[] getSourcePixelValue(double x, double y) throws TransformException {
        double[] srcCoords = new double[2];
        invertMathTransform.transform(new double[]{x, y, 1}, 0, srcCoords, 0, 1);
        if (srcCoords[0]<minSourceX||srcCoords[0]>maxSourceX
          ||srcCoords[1]<minSourceY||srcCoords[1]>maxSourceY) return null;
        return interpol.interpolate(srcCoords[0], srcCoords[1]);
    }

    /**
     * Fill destination image from source image pixel interpolation.
     */
    public void fillImage() throws TransformException {
        final PixelIterator destIterator = PixelIteratorFactory.createDefaultWriteableIterator(imageDest, imageDest);
        int band;
        double[] destPixValue;
        int compDebug = 0;
        while (destIterator.next()) {
            band = 0;
            int getx = destIterator.getX();
            int gety = destIterator.getY();
            if(compDebug==15){
                System.out.println("");
            }
            destPixValue = getSourcePixelValue(destIterator.getX(), destIterator.getY());
            if (destPixValue == null) {
                while (++band != numBands) destIterator.next();
            } else {
                destIterator.setSampleDouble(destPixValue[0]);
                while (++band != numBands) {
                    destIterator.next();
                    destIterator.setSampleDouble(destPixValue[band]);
                }
            }
            compDebug++;
        }
    }
}
