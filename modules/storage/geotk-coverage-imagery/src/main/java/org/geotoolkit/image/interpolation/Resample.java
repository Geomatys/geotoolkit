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
import java.awt.image.DataBuffer;
import java.awt.image.WritableRenderedImage;
import org.apache.sis.geometry.Envelope2D;
import org.apache.sis.geometry.GeneralEnvelope;
import org.apache.sis.util.ArgumentChecks;
import org.geotoolkit.geometry.Envelopes;
import org.geotoolkit.image.io.large.WritableLargeRenderedImage;
import org.geotoolkit.image.iterator.PixelIterator;
import org.geotoolkit.image.iterator.PixelIteratorFactory;
import org.geotoolkit.math.XMath;
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
    private MathTransform destToSourceMathTransform;

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

    private double[] pixelValue;

    /**
     * Iterator use to fill destination image from interpolation of source image pixel value.
     */
    final PixelIterator destIterator;

    /**
     * Minimum and maximum values authorized for pixels. All interpolated value outside this interval will be clamped.
     */
    protected final double[] clamk = new double[2];
    
    /**
     * On the border of the destination image, projection of destination border 
     * pixel coordinates should be out of source image boundary.
     * This enum explain the expected comportement define by user.
     * By default is {@link ResampleBorderComportement#EXTRAPOLATION}, 
     * which allow interpolation outside of source pixel validity area. 
     */
    ResampleBorderComportement rbc;

    /**
     * <p>Fill destination image from interpolation of source pixels.<br/>
     * Source pixel coordinate is obtained from invert transformation of destination pixel coordinates.</p>
     * The default border comportement is {@link ResampleBorderComportement#EXTRAPOLATION}.
     *
     * @param mathTransform Transformation use to transform target point to source point.
     * @param imageDest image will be fill by image source pixel interpolation.
     * @param interpol Interpolation use to interpolate source image pixels.
     * @param fillValue contains value use when pixel transformation is out of source image boundary.
     * @throws NoninvertibleTransformException if it is impossible to invert {@code MathTransform} parameter.
     */
    public Resample(MathTransform mathTransform, WritableRenderedImage imageDest, Interpolation interpol, double[] fillValue) throws NoninvertibleTransformException, TransformException {
        this(mathTransform, imageDest, null, interpol, fillValue);
    }

    /**
     * <p>Fill destination image area from interpolation of source pixels.<br/>
     * Source pixel coordinate is obtained from invert transformation of destination pixel coordinates.</p>
     * The default border comportement is {@link ResampleBorderComportement#EXTRAPOLATION}.
     * 
     * @param mathTransform Transformation use to transform target point to source point.
     * @param imageDest image will be fill by image source pixel interpolation.
     * @param resampleArea destination image area within pixels are resample.
     * @param interpol Interpolation use to interpolate source image pixels.
     * @param fillValue contains value use when pixel transformation is out of source image boundary.
     * @throws NoninvertibleTransformException if it is impossible to invert {@code MathTransform} parameter.
     */
    public Resample(MathTransform mathTransform, WritableRenderedImage imageDest, Rectangle resampleArea, Interpolation interpol, double[] fillValue) throws NoninvertibleTransformException, TransformException {
        this(mathTransform, imageDest, resampleArea, interpol, fillValue, ResampleBorderComportement.EXTRAPOLATION);
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
     * @param rbc comportement of the destination image border. 
     * @throws NoninvertibleTransformException if it is impossible to invert {@code MathTransform} parameter.
     * @see ResampleBorderComportement
     */
    public Resample(MathTransform mathTransform, WritableRenderedImage imageDest, Rectangle resampleArea, 
            Interpolation interpol, double[] fillValue, ResampleBorderComportement rbc) throws NoninvertibleTransformException, TransformException {
        ArgumentChecks.ensureNonNull("mathTransform", mathTransform);
        ArgumentChecks.ensureNonNull("interpolation", interpol);
        final Rectangle bound    = interpol.getBoundary();
        if (imageDest == null) {
            final Envelope2D srcGrid = new Envelope2D();
            srcGrid.setFrame(bound.x + 0.5, bound.y + 0.5, bound.width - 1, bound.height - 1);
            final GeneralEnvelope dest = Envelopes.transform(mathTransform, srcGrid);
            final int minx = (int) dest.getLower(0);
            final int miny = (int) dest.getLower(1);
            final int w    = (int) dest.getSpan(0);
            final int h    = (int) dest.getSpan(1);
            this.imageDest = new WritableLargeRenderedImage(minx, miny, w, h, null, 0, 0, interpol.pixelIterator.getRenderedImage().getColorModel());
        } else {
            /*
             * If a user give a destination image he should hope that his image boundary stay unchanged.
             */
            if (rbc == ResampleBorderComportement.CROP) 
                throw new IllegalArgumentException("It is impossible to define appropriate border comportement with a given image and crop request.");
            this.imageDest = imageDest;
        }
        this.numBands = interpol.getNumBands();
        if (fillValue.length != numBands)
            throw new IllegalArgumentException("fillValue table length and numbands are different : "+fillValue.length+" numbands = "+this.numBands);
        assert(numBands == imageDest.getWritableTile(imageDest.getMinTileX(), imageDest.getMinTileY()).getNumBands())
                : "destination image numbands different from source image numbands";
        this.destIterator        = PixelIteratorFactory.createDefaultWriteableIterator(this.imageDest, this.imageDest, resampleArea);
        this.fillValue           = fillValue;
        this.destToSourceMathTransform = mathTransform;
        this.interpol            = interpol;
        minSourceX = bound.x;
        minSourceY = bound.y;
        maxSourceX = minSourceX + bound.width;
        maxSourceY = minSourceY + bound.height;
        srcCoords  = new double[2];
        destCoords = new double[2];
        this.rbc   = rbc;
        final int datatype = imageDest.getSampleModel().getDataType();
        switch (datatype) {
            case DataBuffer.TYPE_BYTE : {
                clamk[0] = 0;
                clamk[1] = 255;
                break;
            }
            case DataBuffer.TYPE_SHORT :
            case DataBuffer.TYPE_USHORT : {
                clamk[0] = 0;
                clamk[1] = 65535;
                break;
            }
            case DataBuffer.TYPE_FLOAT : {
                clamk[0] = Float.MIN_VALUE;
                clamk[1] = Float.MAX_VALUE;
                break;
            }
            case DataBuffer.TYPE_INT: {
                clamk[0] = Integer.MIN_VALUE;
                clamk[1] = Integer.MAX_VALUE;
                break;
            }
            default : {
                clamk[0] = Double.MIN_VALUE;
                clamk[0] = Double.MAX_VALUE;
            }
        }
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
            destCoords[0] = destIterator.getX() + 0.5;
            destCoords[1] = destIterator.getY() + 0.5;
            destToSourceMathTransform.transform(destCoords, 0, srcCoords, 0, 1);
            src0 = (int) srcCoords[0];
            src1 = (int) srcCoords[1];
            
            //check out of range
            if (src0 < minSourceX || src0 >= maxSourceX
             || src1 < minSourceY || src1 >= maxSourceY) {
                destIterator.setSampleDouble(fillValue[band]);
                while (++band != numBands) {
                    destIterator.next();
                    destIterator.setSampleDouble(fillValue[band]);
                }
            } else {
                //-- check border comportement --//
                if (rbc == ResampleBorderComportement.FILL_VALUE && interpol instanceof SeparableInterpolation &&
                        (srcCoords[0] < minSourceX + 0.5 || srcCoords[0] > maxSourceX - 0.5
                      || srcCoords[1] < minSourceY + 0.5 || srcCoords[1] > maxSourceY - 0.5)) {
                    destIterator.setSampleDouble(fillValue[band]);
                    while (++band != numBands) {
                        destIterator.next();
                        destIterator.setSampleDouble(fillValue[band]);
                    }
                } else {
                    srcCoords[0] = XMath.clamp(srcCoords[0], minSourceX, maxSourceX);
                    srcCoords[1] = XMath.clamp(srcCoords[1], minSourceY, maxSourceY);
                    destIterator.setSampleDouble(
                            XMath.clamp(interpol.interpolate(srcCoords[0], srcCoords[1], band), clamk[0], clamk[1]));
                    while (++band != numBands) {
                        destIterator.next();
                        destIterator.setSampleDouble(
                                XMath.clamp(interpol.interpolate(srcCoords[0], srcCoords[1], band), clamk[0], clamk[1]));
                    }
                }
            }
        }
    }

    public void fillImagePx() throws TransformException {
        int band;
        int src0;
        int src1;
        while (destIterator.next()) {
            band = 0;
            //Compute interpolation value from source image.
            destCoords[0] = destIterator.getX() + 0.5;
            destCoords[1] = destIterator.getY() + 0.5;
            destToSourceMathTransform.transform(destCoords, 0, srcCoords, 0, 1);
            src0 = (int) srcCoords[0];
            src1 = (int) srcCoords[1];

            //check out of range
            if (src0 < minSourceX || src0 >= maxSourceX
                    || src1 < minSourceY || src1 >= maxSourceY) {
                destIterator.setSampleDouble(fillValue[band]);
                while (++band != numBands) {
                    destIterator.next();
                    destIterator.setSampleDouble(fillValue[band]);
                }
            } else {
                srcCoords[0] = XMath.clamp(srcCoords[0], minSourceX, maxSourceX);
                srcCoords[1] = XMath.clamp(srcCoords[1], minSourceY, maxSourceY);
                pixelValue = interpol.interpolate(srcCoords[0], srcCoords[1]);
                destIterator.setSampleDouble(XMath.clamp(pixelValue[band], clamk[0], clamk[1]));
                while (++band < numBands) {
                    destIterator.next();
                    destIterator.setSampleDouble(XMath.clamp(pixelValue[band], clamk[0], clamk[1]));
                }
            }
        }
    }

    public Interpolation getInterpol() {
        return interpol;
    }
}
