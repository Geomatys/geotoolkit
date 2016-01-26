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
import java.awt.geom.AffineTransform;
import java.awt.image.DataBuffer;
import java.awt.image.RenderedImage;
import java.awt.image.WritableRenderedImage;
import org.apache.sis.geometry.Envelope2D;
import org.apache.sis.geometry.GeneralEnvelope;
import org.apache.sis.util.ArgumentChecks;
import org.apache.sis.geometry.Envelopes;
import org.geotoolkit.image.io.large.WritableLargeRenderedImage;
import org.geotoolkit.image.iterator.PixelIterator;
import org.geotoolkit.image.iterator.PixelIteratorFactory;
import org.geotoolkit.math.XMath;
import org.opengis.referencing.datum.PixelInCell;
import org.opengis.referencing.operation.MathTransform;
import org.opengis.referencing.operation.MathTransform2D;
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
     * Iterator use to fill destination image from interpolation of source image pixel value.
     */
    final PixelIterator destIterator;

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
     * Grid use to resample image if exist.
     * @see GridFactory#create(org.opengis.referencing.operation.MathTransform2D, java.awt.Rectangle)
     */
    ResampleGrid theGrid;

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
     * The used MathTransform is consider as {@link PixelInCell#CELL_CENTER} configuration.</strong></p>
     *
     * @param mathTransform Transformation use to transform target point to source point.
     * @param imageDest image will be fill by image source pixel interpolation.
     * @param imageSrc source image which contain pixel values which will be interpolate.
     * @param interpolation case of interpolation.
     * @param lanczosWindow only use about Lanczos interpolation.
     * @param rbc comportement of the destination image border.
     * @param fillValue contains value use when pixel transformation is out of source image boundary, or {@code null}.
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
        if (imageDest == null) {
            final Envelope2D srcGrid = new Envelope2D();
            srcGrid.setFrame(imageSrc.getMinX() + 0.5, imageSrc.getMinY() + 0.5, imageSrc.getWidth() - 1, imageSrc.getHeight() - 1);
            final GeneralEnvelope dest = Envelopes.transform(mathTransform.inverse(), srcGrid);
            final int minx = (int) dest.getLower(0);
            final int miny = (int) dest.getLower(1);
            final int w    = (int) dest.getSpan(0);
            final int h    = (int) dest.getSpan(1);
            this.imageDest = new WritableLargeRenderedImage(minx, miny, w, h, null, 0, 0, imageSrc.getColorModel(), imageSrc.getSampleModel());
        } else {
            /*
             * If a user give a destination image he should hope that his image boundary stay unchanged.
             */
            if (rbc == ResampleBorderComportement.CROP)
                throw new IllegalArgumentException("It is impossible to define appropriate border comportement with a given image and crop request.");
            this.imageDest = imageDest;
        }
        this.numBands = imageSrc.getSampleModel().getNumBands();
        this.fillValue = fillValue;
        if (fillValue != null)
            if (fillValue.length != numBands)
                throw new IllegalArgumentException("fillValue table length and numbands are different : "+fillValue.length+" numbands = "+this.numBands);
        assert(numBands == imageDest.getWritableTile(imageDest.getMinTileX(), imageDest.getMinTileY()).getNumBands())
                : "destination image numbands different from source image numbands";
        this.destIterator              = PixelIteratorFactory.createDefaultWriteableIterator(this.imageDest, this.imageDest, resampleArea);
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
            this.imageDest = new WritableLargeRenderedImage(minx, miny, w, h, null, 0, 0, interpol.pixelIterator.getRenderedImage().getColorModel(), interpol.pixelIterator.getRenderedImage().getSampleModel());
        } else {
            /*
             * If a user give a destination image he should hope that his image boundary stay unchanged.
             */
            if (rbc == ResampleBorderComportement.CROP)
                throw new IllegalArgumentException("It is impossible to define appropriate border comportement with a given image and crop request.");
            this.imageDest = imageDest;
        }
        this.numBands  = interpol.getNumBands();
        this.fillValue = fillValue;
        if (fillValue != null)
            if (fillValue.length != numBands)
                throw new IllegalArgumentException("fillValue table length and numbands are different : "+fillValue.length+" numbands = "+this.numBands);
        assert(numBands == imageDest.getWritableTile(imageDest.getMinTileX(), imageDest.getMinTileY()).getNumBands())
                : "destination image numbands different from source image numbands";
        this.destIterator              = PixelIteratorFactory.createDefaultWriteableIterator(this.imageDest, this.imageDest, resampleArea);
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
     * Fill destination image from pre-computed grid.
     *
     * @throws TransformException
     */
    private void fillImageByGrid() throws TransformException {

        final double[] theGridArray  = theGrid.getGrid();
        final int stepX              = theGrid.getStepX();
        final int stepY              = theGrid.getStepY();
        final int minGridX           = theGrid.getMinGridX();
        final int minGridY           = theGrid.getMinGridY();
        final int minGridXIndex      = theGrid.getMinGridXIndex();
        final int minGridYIndex      = theGrid.getMinGridYIndex();

        //-- destination area traveled by destination iterator.
        Rectangle rectBound  = destIterator.getBoundary(true);

        //-- destination raster dimensions.
        final int tileWidth  = imageDest.getTileWidth();
        final int tileHeight = imageDest.getTileHeight();

        //-- destination raster index which will be traveled.
        final int destMinRastXIndex = imageDest.getMinTileX() + (rectBound.x - imageDest.getMinX()) / tileWidth;
        final int destMinRastYIndex = imageDest.getMinTileY() + (rectBound.y - imageDest.getMinY()) / tileHeight;
        final int destMaxRastX      = imageDest.getMinTileX() + (rectBound.x + rectBound.width + tileWidth - 1) / tileWidth;
        final int destMaxRastY      = imageDest.getMinTileY() + (rectBound.y + rectBound.height + tileHeight - 1) / tileHeight;

        //-- grid dimensions.
        final int gridWidth         = theGrid.getGridWidth();
        final int gridHeight        = theGrid.getGridHeight();
        final int gridLineStride    = gridWidth << 1;

        //-- current raster coordinate in Y direction.
        int rY    = destMinRastYIndex * tileHeight;
        int rMaxY = rY + tileHeight;

        for (int idRy = destMinRastYIndex; idRy < destMaxRastY; idRy++) {

            //-- define intersection between current raster and area traveled by destination iterator in Y direction.
            final int interMinRastY = StrictMath.max(rY, rectBound.y);
            final int interMaxRastY = StrictMath.min(rMaxY, rectBound.y + rectBound.height);

            //-- define minimum and maximum needed grid index in Y direction.
            final int gCMinY = (int) ((interMinRastY - minGridY) / stepY) + minGridYIndex;
            /*
             * Max grid index in Y direction equal Math.ceil(intersectionY / stepY) + 1.
             * With + 1 because gridHeight = sub-division on Y axis + 1;
             */
            final int gCMaxY = (int) ((interMaxRastY - minGridY + stepY - 1) / stepY) + minGridYIndex + 1;
            assert gCMaxY <= gridHeight : "Computed max grid index should be lesser or equal than grid height. Expected max grid index : "+gridHeight+", found : "+gCMaxY;

            //-- current raster coordinate in X direction.
            int rX    = destMinRastXIndex * tileWidth;
            int rMaxX = rX + tileWidth;

            for (int idRx = destMinRastXIndex; idRx < destMaxRastX; idRx++) {

                //-- define intersection between current raster and area traveled by destination iterator in X direction.
                final int interMinRastX = StrictMath.max(rX, rectBound.x);
                final int interMaxRastX = StrictMath.min(rMaxX, rectBound.x + rectBound.width);

                //-- define minimum and maximum needed grid index in X direction.
                final int gCMinX = (int) (interMinRastX - minGridY / stepX) + minGridXIndex;
                /*
                 * Max grid index in X direction equal Math.ceil(intersectionX / stepX) + 1.
                 * With + 1 because gridWidth = sub-division on X axis + 1;
                 */
                final int gCMaxX = (int) ((interMaxRastX - minGridX + stepX - 1) / stepX) + minGridXIndex + 1;
                assert gCMaxX <= gridWidth : "Computed max grid index in X direction should be lesser or equal than grid width. Expected max grid index : "+gridWidth+", found : "+gCMaxX;

                //------------------------- grid working -------------------------//
                //-- index des points a interpoler dans la grille
                int id10, id11;
                double v00X, v10X, v01X, v11X;
                double v00Y, v10Y, v01Y, v11Y;

                //-- travel destination pixel coordinates.
                //-- intersection between area iterated on raster and current grid cell projected into raster space.
                final int interMinY = StrictMath.max(interMinRastY, gCMinY * stepY);
                final int interMinX = StrictMath.max(interMinRastX, gCMinX * stepX);

                final int interMaxY = StrictMath.min(interMaxRastY, gCMaxY * stepY);
                final int interMaxX = StrictMath.min(interMaxRastX, gCMaxX * stepX);

                //-- define grid array index.
                int rowId0 = gCMinY * gridLineStride + (gCMinX << 1);
                int maxRowId0 = (gCMaxY - 2) * gridLineStride + (gCMinX << 1);
                int rowId1 = rowId0 + gridLineStride;

                int py = interMinY;

                //-- Define pixel coordinate in Y direction to pass at next grid cell.
                int nextGIdY = (gCMinY + 1) * stepY;// + (stepY >>> 1);

                //-- current grid index in X direction.
                int gx = gCMinX;
                //-- current grid index in Y direction.
                int gy = gCMinY;

                while (py < interMaxY) {
                    if (py == nextGIdY) {
                        rowId0 += gridLineStride;
                        rowId0 = StrictMath.min(rowId0, maxRowId0);
                        rowId1 = rowId0 + gridLineStride;
                        gy = StrictMath.min(++gy, gridHeight - 2);
                        nextGIdY += stepY;
                    }
                    id10 = rowId0 + 2;
                    id11 = rowId1 + 2;
                    gx   = gCMinX;
                    int px = interMinX;

                    //-- Define pixel coordinate in X direction to pass at next grid cell.
                    int nextGIdX = (gCMinX + 1) * stepX;// + (stepX >>> 1);

                    /*
                     * To define source image coordinate we use bilinear interpolation
                     * from precedently computed values from grid.
                     * Bilinear interpolation is computing like follow in 3 steps.
                     * First we compute "A" value from v00 and v01 (grid values),
                     * t0y which is v00 position in grid and ty the destination
                     * pixel coordinate projected into grid space by followed formula : A = (ty - t0y) * (v10 - v00) + v00.
                     *
                     * Secondly, with the same formula we compute B.
                     *
                     * And finally, we compute coordinates by same precedently formula
                     * with precedently computing results A and B but on X axis.
                     *
                     * P = (tX - t0X) * (B - A) + A
                     *
                     *              t0X           tX
                     *               |            |            |
                     *             ____           |          ____
                     *   t0y ------|v00|--------- C ---------|v10|----
                     *               |            |            |
                     *               |            |            |
                     *               |            |            |
                     *               |            |            |
                     *      ty ----  A ---------- P ---------- B-----
                     *               |            |            |
                     *               |            |            |
                     *               |            |            |
                     *               |            |            |
                     *             ____           |          ____
                     *       ------|v01|--------- D ---------|v11|----
                     *               |            |            |
                     *
                     * During iteration in X direction into the grid we note that v00 become v10 and v01 -> v11, and thereby A = B.
                     * To avoid some of unneccessary computing we affect B to A and
                     * we just re-compute B value and only interpolation in X direction (C and D values).
                     */
                    //-- vX
                    v00X = theGridArray[rowId0];     v10X = theGridArray[id10];
                    v01X = theGridArray[rowId1];     v11X = theGridArray[id11];

                    //-- vY
                    v00Y = theGridArray[rowId0 | 1]; v10Y = theGridArray[id10 | 1];
                    v01Y = theGridArray[rowId1 | 1]; v11Y = theGridArray[id11 | 1];


                    //-- destination image pixel coordinate into grid space in Y direction.
                    final double destY = (py + imageDest.getMinY()) / ((double) stepY);

                    final double ty_t0y = (destY - gy); //-- ty - toy

                    //-- constant value on X source coordinate.
                    double coeff0X = ty_t0y * (v01X - v00X);
                    double coeff1X = ty_t0y * (v11X - v10X);

                    //-- constant value on Y source coordinate.
                    double coeff0Y = ty_t0y * (v01Y - v00Y);
                    double coeff1Y = ty_t0y * (v11Y - v10Y);

                    while (px < interMaxX) {
                        if (px == nextGIdX) {
                            nextGIdX += stepX;
                            if (++gx <= gridWidth - 2) {

                                id10 += 2;
                                id11 += 2;

                                v00X = v10X;
                                v01X = v11X;
                                v10X = theGridArray[id10];
                                v11X = theGridArray[id11];

                                v00Y = v10Y;
                                v01Y = v11Y;
                                v10Y = theGridArray[id10 | 1];
                                v11Y = theGridArray[id11 | 1];

                                //-- coefficient exchange
                                //-- on destination X axis coordinate
                                coeff0X = coeff1X;
                                coeff1X = ty_t0y * (v11X - v10X);
                                //-- on destination Y axis coordinate
                                coeff0Y = coeff1Y;
                                coeff1Y = ty_t0y * (v11Y - v10Y);
                            } else {
                                gx--;
                            }
                        }

                        //-- coordinate interpolation
                        final double destX = (px + imageDest.getMinX()) / ((double) stepX); //-- remonter cette addition pour eviter n *

                        final double tx_t0x = (destX - gx);//-- tx - t0x

                        //-- Compute interpolation from destination image pixel coordinate and
                        //-- computed source coordinates from grid.
                        //-- interpolation on X coordinates
                        final double srcX = tx_t0x * (coeff1X + v10X - v00X) + (1 - tx_t0x) * coeff0X + v00X;

                        //-- interpolation on Y coordinates
                        final double srcY = tx_t0x * (coeff1Y + v10Y - v00Y) + (1 - tx_t0x) * coeff0Y + v00Y;

                        int band = 0;
                        //-- pixel value interpolation
                        //-- if destination coordinate transformation is out of source boundary.
                        if (!interpol.checkInterpolate(srcX, srcY)) {
                            while (band < numBands && destIterator.next()) {
                                if (fillValue != null) destIterator.setSampleDouble(fillValue[band]);
                                band++;
                            }
                        } else {
                            while (band < numBands && destIterator.next()) {
                                double sample = interpol.interpolate(srcX, srcY, band++);
                                if (clamp != null) sample = XMath.clamp(sample, clamp[0], clamp[1]);
                                destIterator.setSampleDouble(sample);
                            }
                        }
                        px++;
                    }
                    py++;
                }

                //------------------------- grid working -------------------------//
                rX += tileWidth;
                rMaxX += tileWidth;
            }
            rY    += tileHeight;
            rMaxY += tileHeight;
        }
    }

    /**
     * Fill image without any grid, all pixels coordinates are transform by given {@link MathTransform}.
     *
     * @throws TransformException
     */
    private void fillImageByAffineTransform(AffineTransform destCoordToSource) throws TransformException {
        int band;
        while (destIterator.next()) {
            band = 0;
            //-- Compute source coordinate from destination coordinate and mathtransform.
            destCoords[0] = destIterator.getX();
            destCoords[1] = destIterator.getY();
            destCoordToSource.transform(destCoords, 0, srcCoords, 0, 1);

            //-- if destination coordinate transformation is out of source boundary.
            if (!interpol.checkInterpolate(srcCoords[0], srcCoords[1])) {

                if (fillValue != null) destIterator.setSampleDouble(fillValue[band]); //Todo : find a way to avoid code duplication
                while (++band < numBands) {
                    destIterator.next();
                    if (fillValue != null) destIterator.setSampleDouble(fillValue[band]);
                }
            } else {
                double sample = interpol.interpolate(srcCoords[0], srcCoords[1], band);//Todo : find a way to avoid code duplication
                if (clamp != null) sample = XMath.clamp(sample, clamp[0], clamp[1]);
                destIterator.setSampleDouble(sample);
                while (++band < numBands) {
                    destIterator.next();
                    sample = interpol.interpolate(srcCoords[0], srcCoords[1], band);
                    if (clamp != null) sample = XMath.clamp(sample, clamp[0], clamp[1]);
                    destIterator.setSampleDouble(sample);
                }
            }
        }
    }

    /**
     * Fill image without any grid, all pixels coordinates are transform by given {@link MathTransform}.
     *
     * @throws TransformException
     */
    private void fillImageByTransform() throws TransformException {
        int band;
        while (destIterator.next()) {
            band = 0;
            //-- Compute source coordinate from destination coordinate and mathtransform.
            destCoords[0] = destIterator.getX();
            destCoords[1] = destIterator.getY();
            destToSourceMathTransform.transform(destCoords, 0, srcCoords, 0, 1);

            //-- if destination coordinate transformation is out of source boundary.
            if (!interpol.checkInterpolate(srcCoords[0], srcCoords[1])) {
                if (fillValue != null) destIterator.setSampleDouble(fillValue[band]);  //Todo : find a way to avoid code duplication
                while (++band < numBands) {
                    destIterator.next();
                    if (fillValue != null) destIterator.setSampleDouble(fillValue[band]);
                }
            } else {
                double sample = interpol.interpolate(srcCoords[0], srcCoords[1], band);  //Todo : find a way to avoid code duplication
                if (clamp != null) sample = XMath.clamp(sample, clamp[0], clamp[1]);
                destIterator.setSampleDouble(sample);
                while (++band < numBands) {
                    destIterator.next();
                    sample = interpol.interpolate(srcCoords[0], srcCoords[1], band);
                    if (clamp != null) sample = XMath.clamp(sample, clamp[0], clamp[1]);
                    destIterator.setSampleDouble(sample);
                }
            }
        }
    }

    /**
     * Fill destination image from source image pixel interpolation.
     */
    public void fillImage() throws TransformException {
        if (destToSourceMathTransform instanceof MathTransform2D) {
            try {
                final GridFactory gridFact = new GridFactory(0.125);
                final Object object = gridFact.create((MathTransform2D) destToSourceMathTransform, destIterator.getBoundary(false));
                if (object instanceof AffineTransform) {
                    fillImageByAffineTransform((AffineTransform) object);
                } else {
                    theGrid = (ResampleGrid) object;
                    fillImageByGrid();
                }
                return;
            } catch (TransformException ex) {
                //-- leave to fall back
            } catch (ArithmeticException e) {
                //-- leave to fall back
            }
        }
        fillImageByTransform();
    }

    /**
     * Please use {@link #fillImageByTransform() } method.
     *
     * @throws TransformException
     * @deprecated replace by {@link #fillImageByTransform() }.
     */
    @Deprecated
    public void fillImagePx() throws TransformException {
        int band;
        while (destIterator.next()) {
            band = 0;
            //-- Compute source coordinate from destination coordinate and mathtransform.
            destCoords[0] = destIterator.getX();
            destCoords[1] = destIterator.getY();
            destToSourceMathTransform.transform(destCoords, 0, srcCoords, 0, 1);

            //-- if destination coordinate transformation is out of source boundary.
            if (!interpol.checkInterpolate(srcCoords[0], srcCoords[1])) {
                if (fillValue != null) destIterator.setSampleDouble(fillValue[band]);
                while (++band < numBands) {
                    destIterator.next();
                    if (fillValue != null) destIterator.setSampleDouble(fillValue[band]);
                }
            } else {
                double sample = interpol.interpolate(srcCoords[0], srcCoords[1], band);
                if (clamp != null) sample = XMath.clamp(sample, clamp[0], clamp[1]);
                destIterator.setSampleDouble(sample);
                while (++band < numBands) {
                    destIterator.next();
                    sample = interpol.interpolate(srcCoords[0], srcCoords[1], band);
                    if (clamp != null) sample = XMath.clamp(sample, clamp[0], clamp[1]);
                    destIterator.setSampleDouble(sample);
                }
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

    /**
     * Returns the needed grid use to resample if exist or {@code null} if none.
     *
     * @return needed grid use to resample if exist or {@code null} if none.
     */
    ResampleGrid getGrid() {
        return theGrid;
    }
}
