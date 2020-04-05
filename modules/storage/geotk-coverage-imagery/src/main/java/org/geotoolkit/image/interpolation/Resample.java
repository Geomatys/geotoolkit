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

import java.awt.Point;
import java.awt.Rectangle;
import java.awt.image.DataBuffer;
import java.awt.image.RenderedImage;
import java.awt.image.WritableRenderedImage;
import org.apache.sis.image.PixelIterator;
import org.apache.sis.image.WritablePixelIterator;
import org.apache.sis.referencing.operation.projection.ProjectionException;
import org.apache.sis.util.ArgumentChecks;
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
     * TODO : require a 2D transform, so we can directly convert positions from image iterator instead of requiring
     * an array buffer.
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
     * Iterator use to fill destination image from interpolation of source image pixel value.
     */
    final WritablePixelIterator destIterator;

    /**
     * Minimum and maximum values authorized for pixels. All interpolated value outside this interval will be clamped.
     */
    protected final double[] clamp;

    /**
     * Contains value use when destination pixel coordinates transformation
     * are out of source image boundary.
     */
    private double[] fillValue;

    /**
     * On the border of the destination image, projection of destination border
     * pixel coordinates should be out of source image boundary.
     * This enum explain the expected comportement define by user.
     * By default is {@link ResampleBorderComportement#EXTRAPOLATION},
     * which allow interpolation outside of source pixel validity area.
     */
    ResampleBorderComportement rbc;

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
     * @param interpolation case of interpolation.
     * @throws NoninvertibleTransformException if it is impossible to invert {@code MathTransform} parameter.
     * @see ResampleBorderComportement
     * @see LanczosInterpolation#LanczosInterpolation(org.geotoolkit.image.iterator.PixelIterator, int)
     */
    public Resample(MathTransform mathTransform, WritableRenderedImage imageDest,
            RenderedImage imageSrc, InterpolationCase interpolation) throws TransformException {
        this(mathTransform, imageDest, null, imageSrc, interpolation, 2, ResampleBorderComportement.FILL_VALUE, new double[imageDest.getSampleModel().getNumBands()]);
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
     * @param interpolation case of interpolation.
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
     * The used MathTransform is consider as {@link PixelInCell#CELL_CENTER} configuration.</strong>
     * <strong>
     * fillValue parameter should be {@code null}, in case where fillValue is null, when destination
     * coordinates pixel transformation is out of source boundary the destination pixel has no setted sample values.<br/>
     * In other words resampling has no impact on destination pixel samples values when transformation is outside source image boundary.<br/></strong></p>
     *
     * @param mathTransform Transformation use to transform target point to source point.
     * @param imageDest image will be fill by image source pixel interpolation.
     * @param resampleArea destination image area within pixels are resample.
     * @param imageSrc source image which contain pixel values which will be interpolate.
     * @param interpolation case of interpolation.
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
        ArgumentChecks.ensureNonNull("imageDest", imageDest);
        /*
        * If a user give a destination image he should hope that his image boundary stay unchanged.
        */
        if (rbc == ResampleBorderComportement.CROP)
            throw new IllegalArgumentException("It is impossible to define appropriate border comportement with a given image and crop request.");
        this.imageDest = imageDest;
        this.numBands = imageSrc.getSampleModel().getNumBands();
        this.fillValue = fillValue;
        if (fillValue != null)
            if (fillValue.length != numBands)
                throw new IllegalArgumentException("fillValue table length and numbands are different : "+fillValue.length+" numbands = "+this.numBands);
        assert(numBands == imageDest.getWritableTile(imageDest.getMinTileX(), imageDest.getMinTileY()).getNumBands())
                : "destination image numbands different from source image numbands";
        this.destIterator              = new WritablePixelIterator.Builder().setRegionOfInterest(resampleArea).createWritable(this.imageDest);
        this.destToSourceMathTransform = mathTransform;

        srcCoords  = new double[2];
        destCoords = new double[2];
        //-- interpolation creation --//
        PixelIterator pix = PixelIterator.create(imageSrc);

        Rectangle boundary = pix.getDomain();
        if (lanczosWindow > boundary.width || lanczosWindow > boundary.height) {
            //image is too small for interpolation, switch to neareast neighor
            lanczosWindow = 1;
            interpolation = InterpolationCase.NEIGHBOR;
        }
        interpol = Interpolation.create(pix, interpolation, lanczosWindow, rbc, fillValue);

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
     * @param resampleArea destination image area within pixels are resample, can be null.
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
        ArgumentChecks.ensureNonNull("imageDest", imageDest);
        final Rectangle bound    = interpol.getBoundary();
        /*
         * If a user give a destination image he should hope that his image boundary stay unchanged.
         */
        if (rbc == ResampleBorderComportement.CROP)
            throw new IllegalArgumentException("It is impossible to define appropriate border comportement with a given image and crop request.");
        this.imageDest = imageDest;
        this.numBands  = interpol.getNumBands();
        this.fillValue = fillValue;
        if (fillValue != null)
            if (fillValue.length != numBands)
                throw new IllegalArgumentException("fillValue table length and numbands are different : "+fillValue.length+" numbands = "+this.numBands);
        assert(numBands == imageDest.getWritableTile(imageDest.getMinTileX(), imageDest.getMinTileY()).getNumBands())
                : "destination image numbands different from source image numbands";
        this.destIterator              = new WritablePixelIterator.Builder().setRegionOfInterest(resampleArea).createWritable(this.imageDest);
        this.destToSourceMathTransform = mathTransform;
        this.interpol                  = interpol;
        srcCoords  = new double[2];
        destCoords = new double[2];
        this.rbc   = rbc;
        this.clamp = getClamp(imageDest.getSampleModel().getDataType());
    }

    private static double[] getClamp(int dataType) {
        switch (dataType) {
            /* Because DataBuffer.TYPE_BYTE is define as UByte. */
            case DataBuffer.TYPE_BYTE   : return CLAMP_BYTE;
            case DataBuffer.TYPE_SHORT  : return CLAMP_SHORT;
            case DataBuffer.TYPE_USHORT : return CLAMP_USHORT;
            case DataBuffer.TYPE_INT    : return CLAMP_INT;
            default                     : return null;
        }
    }

    /**
     * Fill image without any grid, all pixels coordinates are transform by given {@link MathTransform}.
     */
    public void fillImage() throws TransformException {
        while (destIterator.next()) {
            //-- Compute source coordinate from destination coordinate and mathtransform.
            final Point position = destIterator.getPosition();
            destCoords[0] = position.x;
            destCoords[1] = position.y;
            try {
                destToSourceMathTransform.transform(destCoords, 0, srcCoords, 0, 1);
            } catch (ProjectionException ex) {
                //coordinate can not be computed in source crs
                srcCoords[0] = Double.NaN;
                srcCoords[1] = Double.NaN;
            }

            //-- if destination coordinate transformation is out of source boundary.
            if (!interpol.checkInterpolate(srcCoords[0], srcCoords[1])) {
                if (fillValue != null) destIterator.setPixel(fillValue);
            } else {
                double[] pixel = interpol.interpolate(srcCoords[0], srcCoords[1]);
                if (clamp != null) XMath.applyClamp(pixel, clamp[0], clamp[1]);
                destIterator.setPixel(pixel);
            }
        }
    }

    /**
     * Returns {@link Interpolation} object use to resample.
     *
     * @return {@link Interpolation} object use to resample.
     */
    public Interpolation getInterpol() {
        return interpol;
    }
}
