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
import java.awt.image.RenderedImage;
import java.awt.image.WritableRenderedImage;
import org.apache.sis.geometry.Envelope2D;
import org.apache.sis.geometry.GeneralEnvelope;
import org.apache.sis.util.ArgumentChecks;
import org.geotoolkit.geometry.Envelopes;
import org.geotoolkit.image.io.large.WritableLargeRenderedImage;
import org.geotoolkit.image.iterator.PixelIterator;
import org.geotoolkit.image.iterator.PixelIteratorFactory;
import org.geotoolkit.math.XMath;
import org.opengis.referencing.datum.PixelInCell;
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
   
    private static final double[] CLAMP_BYTE   = new double[]{0,                 255};
    private static final double[] CLAMP_SHORT  = new double[]{Short.MIN_VALUE,   Short.MAX_VALUE};
    private static final double[] CLAMP_USHORT = new double[]{0,                 0xFFFF};
    private static final double[] CLAMP_INT    = new double[]{Integer.MIN_VALUE, Integer.MAX_VALUE};
            
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
     * Table which contain x, y coordinates after {@link MathTransform} transformation.
     */
    private final double[] srcCoords;
    
    /**
     * Table which contain x, y coordinates after {@link MathTransform} transformation.
     */
    private final double[] destCoords;

    /**
     * Aray which represente destination pixel values for all bands.
     */
    private double[] pixelValue;

    /**
     * Iterator use to fill destination image from interpolation of source image pixel value.
     */
    final PixelIterator destIterator;

    /**
     * Minimum and maximum values authorized for pixels. All interpolated value outside this interval will be clamped.
     */
    protected final double[] clamp;
    
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
     * Source pixel coordinate is obtained from invert transformation of destination pixel coordinates.<br/>
     * The default border comportement is {@link ResampleBorderComportement#EXTRAPOLATION}.<br/><br/>
     * 
     * <strong>
     * Moreover : the specified MathTransform should be from CENTER of target image point to CENTER of source image point.<br/>
     * The used MathTransform is consider with {@link PixelInCell#CELL_CENTER} configuration.</strong></p>
     *
     * @param mathTransform Transformation use to transform target point to source point.
     * @param imageDest image will be fill by image source pixel interpolation.
     * @param interpol Interpolation use to interpolate source image pixels.
     * @param fillValue contains value use when pixel transformation is out of source image boundary.
     * @throws NoninvertibleTransformException if it is impossible to invert {@code MathTransform} parameter.
     */
    @Deprecated
    public Resample(MathTransform mathTransform, WritableRenderedImage imageDest, Interpolation interpol, double[] fillValue) throws NoninvertibleTransformException, TransformException {
        this(mathTransform, imageDest, null, interpol, fillValue);
    }

    /**
     * <p>Fill destination image area from interpolation of source pixels.<br/>
     * Source pixel coordinate is obtained from invert transformation of destination pixel coordinates.<br/>
     * The default border comportement is {@link ResampleBorderComportement#EXTRAPOLATION}.<br/><br/>
     * 
     * <strong>
     * Moreover : the specified MathTransform should be from CENTER of target image point to CENTER of source image point.<br/>
     * The used MathTransform is consider with {@link PixelInCell#CELL_CENTER} configuration.</strong></p>
     * 
     * @param mathTransform Transformation use to transform target point to source point.
     * @param imageDest image will be fill by image source pixel interpolation.
     * @param resampleArea destination image area within pixels are resample.
     * @param interpol Interpolation use to interpolate source image pixels.
     * @param fillValue contains value use when pixel transformation is out of source image boundary.
     * @throws NoninvertibleTransformException if it is impossible to invert {@code MathTransform} parameter.
     */
    @Deprecated
    public Resample(MathTransform mathTransform, WritableRenderedImage imageDest, Rectangle resampleArea, Interpolation interpol, double[] fillValue) throws NoninvertibleTransformException, TransformException {
        this(mathTransform, imageDest, resampleArea, interpol, fillValue, ResampleBorderComportement.EXTRAPOLATION);
    }

    /**
     * <p>Fill destination image area from interpolation of source pixels from imageSrc.<br/>
     * Source pixel coordinates is obtained from invert transformation of destination pixel coordinates.<br/><br/>
     * - In case where interpolation equals {@linkplain InterpolationCase#LANCZOS lanczos interpolation} the default 
     * choosen lanczos window is initialized by value 2.<br/>
     * - The default border comportement is define as {@link ResampleBorderComportement#FILL_VALUE}<br/>
     * - In case where pixel transformation is out of source image boundary the default choosen fill value is {@link Double#NaN}.<br/><br/>
     * 
     * <strong>
     * Moreover : the specified MathTransform should be from CENTER of target image point to CENTER of source image point.<br/>
     * The used MathTransform is consider as {@link PixelInCell#CELL_CENTER} configuration.</strong></p>
     * 
     * @param mathTransform Transformation use to transform target point to source point.
     * @param imageDest image will be fill by image source pixel interpolation.
     * @param imageSrc source image which contain pixel values which will be interpolate. 
     * @param interpolationCase case of interpolation.
     * @throws NoninvertibleTransformException if it is impossible to invert {@code MathTransform} parameter.
     * @see ResampleBorderComportement
     * @see LanczosInterpolation#LanczosInterpolation(org.geotoolkit.image.iterator.PixelIterator, int) 
     */
    public Resample(MathTransform mathTransform, WritableRenderedImage imageDest, 
            RenderedImage imageSrc, InterpolationCase interpolation) throws TransformException {
        this(mathTransform, imageDest, null, imageSrc, interpolation, 2, ResampleBorderComportement.FILL_VALUE, null);
    }
    
    /**
     * <p>Fill destination image area from interpolation of source pixels from imageSrc.<br/>
     * Source pixel coordinates is obtained from invert transformation of destination pixel coordinates.<br/><br/>
     * - In case where interpolation equals {@linkplain InterpolationCase#LANCZOS lanczos interpolation} the default 
     * choosen lanczos window is initialized by value 2.<br/><br/>
     * <strong>
     * Moreover : the specified MathTransform should be from CENTER of target image point to CENTER of source image point.<br/>
     * The used MathTransform is consider as {@link PixelInCell#CELL_CENTER} configuration.</strong></p>
     * 
     * @param mathTransform Transformation use to transform target point to source point.
     * @param imageDest image will be fill by image source pixel interpolation.
     * @param imageSrc source image which contain pixel values which will be interpolate. 
     * @param interpolationCase case of interpolation.
     * @param lanczosWindow only use about Lanczos interpolation.
     * @param rbc comportement of the destination image border. 
     * @param fillValue contains value use when pixel transformation is out of source image boundary.
     * @throws NoninvertibleTransformException if it is impossible to invert {@code MathTransform} parameter.
     * @see ResampleBorderComportement
     */
    public Resample(MathTransform mathTransform, WritableRenderedImage imageDest, RenderedImage imageSrc,
            InterpolationCase interpolation, ResampleBorderComportement rbc, double[] fillValue) throws TransformException {
        this(mathTransform, imageDest, null, imageSrc, interpolation, 2, rbc, fillValue);
    }
    
    /**
     * <p>Fill destination image area from interpolation of source pixels from imageSrc.<br/>
     * Source pixel coordinates is obtained from invert transformation of destination pixel coordinates.<br/><br/>
     * <strong>
     * Moreover : the specified MathTransform should be from CENTER of target image point to CENTER of source image point.<br/>
     * The used MathTransform is consider as {@link PixelInCell#CELL_CENTER} configuration.</strong></p>
     * 
     * @param mathTransform Transformation use to transform target point to source point.
     * @param imageDest image will be fill by image source pixel interpolation.
     * @param imageSrc source image which contain pixel values which will be interpolate. 
     * @param interpolationCase case of interpolation.
     * @param lanczosWindow only use about Lanczos interpolation.
     * @param rbc comportement of the destination image border. 
     * @param fillValue contains value use when pixel transformation is out of source image boundary.
     * @throws NoninvertibleTransformException if it is impossible to invert {@code MathTransform} parameter.
     * @see ResampleBorderComportement
     */
    public Resample(MathTransform mathTransform, WritableRenderedImage imageDest, RenderedImage imageSrc,
            InterpolationCase interpolation, int lanczosWindow, ResampleBorderComportement rbc, double[] fillValue) throws TransformException {
        this(mathTransform, imageDest, null, imageSrc, interpolation, lanczosWindow, rbc, fillValue);
    }
    
    /**
     * <p>Fill destination image area from interpolation of source pixels from imageSrc.<br/>
     * Source pixel coordinates is obtained from invert transformation of destination pixel coordinates.<br/><br/>
     * <strong>
     * Moreover : the specified MathTransform should be from CENTER of target image point to CENTER of source image point.<br/>
     * The used MathTransform is consider as {@link PixelInCell#CELL_CENTER} configuration.</strong></p>
     * 
     * @param mathTransform Transformation use to transform target point to source point.
     * @param imageDest image will be fill by image source pixel interpolation.
     * @param resampleArea destination image area within pixels are resample.
     * @param imageSrc source image which contain pixel values which will be interpolate. 
     * @param interpolationCase case of interpolation.
     * @param lanczosWindow only use about Lanczos interpolation.
     * @param rbc comportement of the destination image border. 
     * @param fillValue contains value use when pixel transformation is out of source image boundary.
     * @throws NoninvertibleTransformException if it is impossible to invert {@code MathTransform} parameter.
     * @see ResampleBorderComportement
     */
    public Resample(MathTransform mathTransform, WritableRenderedImage imageDest, Rectangle resampleArea, RenderedImage imageSrc,
            InterpolationCase interpolation, int lanczosWindow, ResampleBorderComportement rbc, double[] fillValue) throws TransformException {
        ArgumentChecks.ensureNonNull("mathTransform", mathTransform);
        ArgumentChecks.ensureNonNull("imageSrc", imageSrc);
        ArgumentChecks.ensureNonNull("ResampleBorderComportement", rbc);
        if (imageDest == null) {
            final Envelope2D srcGrid = new Envelope2D();
            srcGrid.setFrame(imageSrc.getMinX() + 0.5, imageSrc.getMinY() + 0.5, imageSrc.getWidth() - 1, imageSrc.getHeight() - 1);
            final GeneralEnvelope dest = Envelopes.transform(mathTransform.inverse(), srcGrid);
            final int minx = (int) dest.getLower(0);
            final int miny = (int) dest.getLower(1);
            final int w    = (int) dest.getSpan(0);
            final int h    = (int) dest.getSpan(1);
            this.imageDest = new WritableLargeRenderedImage(minx, miny, w, h, null, 0, 0, imageSrc.getColorModel());
        } else {
            /*
             * If a user give a destination image he should hope that his image boundary stay unchanged.
             */
            if (rbc == ResampleBorderComportement.CROP) 
                throw new IllegalArgumentException("It is impossible to define appropriate border comportement with a given image and crop request.");
            this.imageDest = imageDest;
        }
        this.numBands = imageSrc.getSampleModel().getNumBands();
        if (fillValue.length != numBands)
            throw new IllegalArgumentException("fillValue table length and numbands are different : "+fillValue.length+" numbands = "+this.numBands);
        assert(numBands == imageDest.getWritableTile(imageDest.getMinTileX(), imageDest.getMinTileY()).getNumBands())
                : "destination image numbands different from source image numbands";
        this.destIterator        = PixelIteratorFactory.createDefaultWriteableIterator(this.imageDest, this.imageDest, resampleArea);
        this.destToSourceMathTransform = mathTransform;
        
        srcCoords  = new double[2];
        destCoords = new double[2];
        //-- interpolation creation --//
        PixelIterator pix = PixelIteratorFactory.createDefaultIterator(imageSrc);
        interpol          = Interpolation.create(pix, interpolation, lanczosWindow, rbc, fillValue);
        
        this.rbc   = rbc;
        this.clamp = getClamp(imageDest.getSampleModel().getDataType());
    }
        
    /**
     * <p>Fill destination image area from interpolation of source pixels.<br/>
     * Source pixel coordinate is obtained from invert transformation of destination pixel coordinates.<br/><br/>
     * <strong>
     * Moreover : the specified MathTransform should be from CENTER of target image point to CENTER of source image point.<br/>
     * The used MathTransform is consider with {@link PixelInCell#CELL_CENTER} configuration.</strong></p>
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
    @Deprecated
    public Resample(MathTransform mathTransform, WritableRenderedImage imageDest, Rectangle resampleArea, 
            Interpolation interpol, double[] fillValue, ResampleBorderComportement rbc) throws NoninvertibleTransformException, TransformException {
        ArgumentChecks.ensureNonNull("mathTransform", mathTransform);
        ArgumentChecks.ensureNonNull("interpolation", interpol);
        final Rectangle bound    = interpol.getBoundary();
        if (imageDest == null) {
            final Envelope2D srcGrid = new Envelope2D();
            srcGrid.setFrame(bound.x + 0.5, bound.y + 0.5, bound.width - 1, bound.height - 1);
            final GeneralEnvelope dest = Envelopes.transform(mathTransform.inverse(), srcGrid);
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
        this.destToSourceMathTransform = mathTransform;
        this.interpol            = interpol;
        srcCoords  = new double[2];
        destCoords = new double[2];
        this.rbc   = rbc;
        this.clamp = getClamp(imageDest.getSampleModel().getDataType());
    }
    
    private static double[] getClamp(int dataType){
        switch (dataType) {
            /* Because DataBuffer.TYPE_BYTE is define as UByte. */
            case DataBuffer.TYPE_BYTE : return CLAMP_BYTE;
            case DataBuffer.TYPE_SHORT : return CLAMP_SHORT;
            case DataBuffer.TYPE_USHORT : return CLAMP_USHORT;
            case DataBuffer.TYPE_INT: return CLAMP_INT;
            default : return null;
        }
    }

    /**
     * Fill destination image from source image pixel interpolation.
     */
    public void fillImage() throws TransformException {
        int band;
        
        while (destIterator.next()) {
            band = 0;
            //-- Compute source coordinate from destination coordinate and mathtransform.
            destCoords[0] = destIterator.getX();
            destCoords[1] = destIterator.getY();
            destToSourceMathTransform.transform(destCoords, 0, srcCoords, 0, 1);
            
            //-- Compute interpolation from source image pixel value and computed source coordinates.
            double sample = interpol.interpolate(srcCoords[0], srcCoords[1], band);
            if(clamp!=null) sample = XMath.clamp(sample, clamp[0], clamp[1]);
            destIterator.setSampleDouble(sample);
            while (++band != numBands && destIterator.next()) {
                sample = interpol.interpolate(srcCoords[0], srcCoords[1], band);
                if(clamp!=null) sample = XMath.clamp(sample, clamp[0], clamp[1]);
                destIterator.setSampleDouble(sample);
            }
        }
    }

    public void fillImagePx() throws TransformException {
        int band;
        while (destIterator.next()) {
            band = 0;
            //-- Compute source coordinate from destination coordinate and mathtransform.
            destCoords[0] = destIterator.getX();
            destCoords[1] = destIterator.getY();
            destToSourceMathTransform.transform(destCoords, 0, srcCoords, 0, 1);
            
            //-- Compute interpolation from source image pixel value and computed source coordinates.
            pixelValue = interpol.interpolate(srcCoords[0], srcCoords[1]);
            if(clamp!=null) pixelValue[band] = XMath.clamp(pixelValue[band], clamp[0], clamp[1]);
            destIterator.setSampleDouble(pixelValue[band]);
            while (++band < numBands) {
                destIterator.next();
                if(clamp!=null) pixelValue[band] = XMath.clamp(pixelValue[band], clamp[0], clamp[1]);
                destIterator.setSampleDouble(pixelValue[band]);
            }
        }
    }

    public Interpolation getInterpol() {
        return interpol;
    }
}
