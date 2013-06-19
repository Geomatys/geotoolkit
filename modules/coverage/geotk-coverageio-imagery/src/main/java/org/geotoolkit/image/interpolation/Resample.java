/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
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
    private MathTransform invertMathTransform;

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
     * Table use which target image pixel transformation is out of source image boundary.
     */
    private final double[] fillValue;

    /**
     * Table which contain x, y coordinates after {@link MathTransform} transformation.
     */
    private final double[] srcCoords;
    
    /**
     * Table which contain x, y coordinates after {@link MathTransform} transformation.
     */
    private final double[] destCoords;

    /**
     * Iterator use to fill destination image from interpolation of source image pixel value.
     */
    final PixelIterator destIterator;

    /**
     * <p>Fill destination image from interpolation of source pixels.<br/>
     * Source pixel coordinate is obtained from invert transformation of destination pixel coordinates.</p>
     *
     * @param mathTransform Transformation use to transform target point to source point.
     * @param imageDest image will be fill by image source pixel interpolation.
     * @param interpol Interpolation use to interpolate source image pixels.
     * @param fillValue contains value use when pixel transformation is out of source image boundary.
     * @throws NoninvertibleTransformException if it is impossible to invert {@code MathTransform} parameter.
     */
    public Resample(MathTransform mathTransform, WritableRenderedImage imageDest, Interpolation interpol, double[] fillValue) throws NoninvertibleTransformException, TransformException {
        this(mathTransform,imageDest,null,interpol,fillValue);
        
    }

    /**
     * <p>Fill destination image area from interpolation of source pixels.<br/>
     * Source pixel coordinate is obtained from invert transformation of destination pixel coordinates.</p>
     *
     * @param mathTransform Transformation use to transform target point to source point.
     * @param imageDest image will be fill by image source pixel interpolation.
     * @param resampleArea destination image area within pixels are resample.
     * @param interpol Interpolation use to interpolate source image pixels.
     * @param fillValue contains value use when pixel transformation is out of source image boundary.
     * @throws NoninvertibleTransformException if it is impossible to invert {@code MathTransform} parameter.
     */
    public Resample(MathTransform mathTransform, WritableRenderedImage imageDest, Rectangle resampleArea, Interpolation interpol, double[] fillValue) throws NoninvertibleTransformException, TransformException {
        this.numBands = interpol.getNumBands();
        if (fillValue.length != numBands)
            throw new IllegalArgumentException("fillValue table length and numbands are different : "+fillValue.length+" numbands = "+this.numBands);
        assert(numBands == imageDest.getWritableTile(imageDest.getMinTileX(), imageDest.getMinTileY()).getNumBands())
                : "destination image numbands different from source image numbands";
        this.destIterator        = PixelIteratorFactory.createDefaultWriteableIterator(imageDest, imageDest, resampleArea);
        this.fillValue           = fillValue;
        this.invertMathTransform = mathTransform;
        this.imageDest           = imageDest;
        this.interpol            = interpol;
        final Rectangle bound    = interpol.getBoundary();
        minSourceX = bound.x;
        minSourceY = bound.y;
        maxSourceX = minSourceX + bound.width  - 1;
        maxSourceY = minSourceY + bound.height - 1;
        srcCoords  = new double[2];
        destCoords = new double[2];
        
        
    }

    /**
     * Fill destination image from source image pixel interpolation.
     */
    public void fillImage() throws TransformException {
        int band;
        int src0;
        int src1;
        
        while (destIterator.next()) {
            band = 0;
            //Compute interpolation value from source image.
            destCoords[0] = destIterator.getX();
            destCoords[1] = destIterator.getY();
            invertMathTransform.transform(destCoords, 0, srcCoords, 0, 1);
            src0 = (int) srcCoords[0];
            src1 = (int) srcCoords[1];
            
            //check out of range
            if (src0 < minSourceX || src0 > maxSourceX
                || src1 < minSourceY || src1 > maxSourceY) {
                destIterator.setSampleDouble(fillValue[band]);
                while (++band != numBands) {
                    destIterator.next();
                    destIterator.setSampleDouble(fillValue[band]);
                }
            }else{
                destIterator.setSampleDouble(interpol.interpolate(src0, src1, band));
                while (++band != numBands) {
                    destIterator.next();
                    destIterator.setSampleDouble(interpol.interpolate(src0, src1, band));
                }
            }
            
        }
    }
}
