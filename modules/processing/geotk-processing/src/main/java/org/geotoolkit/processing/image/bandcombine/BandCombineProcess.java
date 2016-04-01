/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2016, Geomatys
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
package org.geotoolkit.processing.image.bandcombine;

import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.image.ColorModel;
import java.awt.image.RenderedImage;
import java.awt.image.SampleModel;
import java.awt.image.WritableRenderedImage;

import org.opengis.parameter.ParameterValueGroup;

import org.apache.sis.util.ArgumentChecks;

import org.geotoolkit.image.iterator.PixelIterator;
import org.geotoolkit.image.iterator.PixelIteratorFactory;
import org.geotoolkit.image.BufferedImages;
import org.geotoolkit.image.internal.ImageUtils;
import org.geotoolkit.image.internal.PlanarConfiguration;
import org.geotoolkit.image.internal.SampleType;
import org.geotoolkit.image.io.large.WritableLargeRenderedImage;
import org.geotoolkit.parameter.Parameters;
import org.geotoolkit.processing.AbstractProcess;
import org.geotoolkit.process.ProcessException;

/**
 * Process to combine some source image into same result image.<br><br>
 *
 * Result image will own same band number as sum of all source image bands.<br>
 * Bands order writing is the same as image order given in parameters.<br>
 * To combine images each source images must answer 3 criterions as follow : <br>
 * - same width <br>
 * - same height<br>
 * - same dataType.<br><br>
 *
 * On the other side source images may have different upper left corner coordinates,
 * and also internal differents tiles size.<br><br>
 *
 * To improve performance ouput image is {@link WritableLargeRenderedImage} type.<br><br>
 *
 * Moreover, new adapted {@link ColorModel} and {@link SampleModel} are computed.
 *
 * @author Remi Marechal (Geomatys)
 * @author Johann Sorel (Geomatys)
 *
 * @see BufferedImages#createGrayScaleColorModel(int, int, int, double, double)
 * @see ImageUtils#createSampleModel(org.geotoolkit.image.internal.PlanarConfiguration, org.geotoolkit.image.internal.SampleType, int, int, int)
 */
public class BandCombineProcess extends AbstractProcess {

    public BandCombineProcess(final ParameterValueGroup input) {
        super(BandCombineDescriptor.INSTANCE, input);
    }

    @Override
    protected void execute() throws ProcessException {
        ArgumentChecks.ensureNonNull("inputParameter", inputParameters);

        final RenderedImage[] inputImages = (RenderedImage[]) Parameters.getOrCreate(BandCombineDescriptor.IN_IMAGES, inputParameters).getValue();

        if (inputImages.length == 0)
            throw new ProcessException("No image to combine", this, null);

        if (inputImages.length == 1) {
            //nothing to do
            Parameters.getOrCreate(BandCombineDescriptor.OUT_IMAGE, outputParameters).setValue(inputImages[0]);
            return;
        }

        //check and extract informations, all images should have the same size and sample type.
        int sampleType   = -1;
        int nbtotalbands = 0;
        int width        = 0;
        int height       = 0;
        final int[] nbBands     = new int[inputImages.length];
        final int[] nbBandIndex = new int[inputImages.length];

        //-- attribut use only during same tile size
        final PixelIterator[] readItes = new PixelIterator[inputImages.length];
        //-- minimum image coordinates only use during assert
        final int[][] minXYs = new int[inputImages.length][2];

        final Dimension[] tilesSize = new Dimension[inputImages.length];

        for(int i = 0; i < inputImages.length; i++) {
            final RenderedImage image = inputImages[i];
            final SampleModel sm = image.getSampleModel();
            if (sampleType == -1) {
                //first image
                sampleType = sm.getDataType();
                width      = image.getWidth();
                height     = image.getHeight();
            } else {
                //check same model
                if (sampleType != sm.getDataType())
                    throw new ProcessException("Images do not have the same sample type", this, null);
                if (width != image.getWidth() || height != image.getHeight())
                    throw new ProcessException("Images do not have the same size", this, null);

            }

            minXYs[i][0]   = image.getMinX();
            minXYs[i][1]   = image.getMinY();
            tilesSize[i]   = new Dimension(image.getTileWidth(), image.getTileHeight());
            readItes[i]    = PixelIteratorFactory.createDefaultIterator(image);
            nbBands[i]     = sm.getNumBands();
            nbBandIndex[i] = nbtotalbands;
            nbtotalbands  += sm.getNumBands();
        }

        //-- try to reuse a java color model for better performances
        final ColorModel cm  = BufferedImages.createGrayScaleColorModel(sampleType,nbtotalbands, 0, 0, 10);
        final SampleModel sm = ImageUtils.createSampleModel(PlanarConfiguration.INTERLEAVED, SampleType.valueOf(sampleType), width, height, nbtotalbands);


        //-- destination Images Dimensions
        boolean sameTileSize = sameTileSize(tilesSize);
        final Dimension destTileSize;
        //-- if all srcTiles dimensions are equals keep same tiles sizes.
        if (sameTileSize) {
            if (tilesSize[0].width >= 64
                && tilesSize[0].width <= 256
                && tilesSize[0].height >= 64
                && tilesSize[0].height <= 256 ) {
                //-- keep control to destination tile size to avoid giant destination tile size.
                destTileSize = new Dimension(tilesSize[0]);
            } else {
                sameTileSize = false;
                destTileSize = new Dimension(256, 256);
            }

        } else {
        //-- else destination tile size (256 * 256).
            destTileSize = new Dimension(256, 256);
        }

        final WritableRenderedImage resultImage = new WritableLargeRenderedImage(0, 0, width, height, destTileSize, 0, 0, cm, sm);

        //-- 2 cases
        //-- SrcTiles dimensions and src dest Dimension are equals
        if (sameTileSize) {

            final PixelIterator destPix = PixelIteratorFactory.createDefaultWriteableIterator(resultImage, resultImage);

            int itId = -1;
            while (destPix.next()) {

                //-- when a dest sample is completely filled
                //-- rewind to first destination sample
                if (++itId >= inputImages.length)
                    itId = 0;

                final PixelIterator srcPix = readItes[itId];
                srcPix.next();

                //-- current src iterator coordinates
                final int srcPX = srcPix.getX();
                final int srcPY = srcPix.getY();

                //-- current destination iterator coordinates
                final int dstPX = srcPX - minXYs[itId][0];
                final int dstPY = srcPY - minXYs[itId][1];

                destPix.setSampleDouble(srcPix.getSampleDouble());

                int b = 0;
                while (++b < nbBands[itId]) {
                    assert checkPositions("Src Pix Iterator for image "+itId+" at band index : "+nbBandIndex[itId] + b, srcPix,  srcPX, srcPY);
                    assert checkPositions("Dest Pix at Band index : "+nbBandIndex[itId] + b, destPix, dstPX, dstPY);
                    destPix.next();
                    srcPix.next();
                    destPix.setSampleDouble(srcPix.getSampleDouble());
                }
                assert checkPositions("Src Pix Iterator for image "+itId+" at band index : "+nbBandIndex[itId] + b, srcPix,  srcPX, srcPY);
                assert checkPositions("Dest Pix at Band index : "+nbBandIndex[itId] + b, destPix, dstPX, dstPY);

            }

        } else {
            for (int i = 0; i < inputImages.length; i++) {
                final RenderedImage srcImage = inputImages[i];
                final int srcMinX = srcImage.getMinX();
                final int srcMinY = srcImage.getMinY();
                //-- travel all source image tile to read tile once a time
                for (int ty = 0; ty < srcImage.getNumYTiles(); ty++) {
                    for (int tx = 0; tx < srcImage.getNumXTiles(); tx++) {
                        final Rectangle srcArea = new Rectangle(srcMinX + tx * tilesSize[i].width, srcMinY + ty * tilesSize[i].height,
                                                                               tilesSize[i].width, tilesSize[i].height);

                        final PixelIterator srcPix = PixelIteratorFactory.createDefaultIterator(srcImage, srcArea);

                        //-- dest image begin (0, 0)
                        final Rectangle destArea = new Rectangle(tx * tilesSize[i].width, ty * tilesSize[i].height,
                                                                      tilesSize[i].width, tilesSize[i].height);

                        final PixelIterator destPix = PixelIteratorFactory.createDefaultWriteableIterator(resultImage, resultImage, destArea);

                        while (srcPix.next()) {
                            //-- current src iterator coordinates
                            final int srcPX = srcPix.getX();
                            final int srcPY = srcPix.getY();

                            //-- current destination iterator coordinates
                            final int dstPX = srcPix.getX() - srcMinX;
                            final int dstPY = srcPix.getY() - srcMinY;

                            destPix.moveTo(dstPX, dstPY, nbBandIndex[i]);
                            destPix.setSampleDouble(srcPix.getSampleDouble());

                            //-- fork other bands writing like follow
                            //-- to avoid unnecessary multiple pixeliterator.move() call
                            int b = 0;
                            while (++b < nbBands[i]) {
                                assert checkPositions("Src Pix Iterator for image "+i+" at band index : "+nbBandIndex[i] + b, srcPix,  srcPX, srcPY);
                                assert checkPositions("Dest Pix at Band index : "+nbBandIndex[i] + b, destPix, dstPX, dstPY);
                                srcPix.next();
                                destPix.next();
                                destPix.setSampleDouble(srcPix.getSampleDouble());
                            }

                            assert checkPositions("Src Pix Iterator for image "+i+" at band index : "+nbBandIndex[i] + b, srcPix,  srcPX, srcPY);
                            assert checkPositions("Dest Pix at Band index : "+nbBandIndex[i] + b, destPix, dstPX, dstPY);
                        }
                    }
                }
            }
        }
        Parameters.getOrCreate(BandCombineDescriptor.OUT_IMAGE, outputParameters).setValue(resultImage);
    }

    /**
     * Returns {@code true} if all {@link Dimension} are equals.<br>
     * Moreover it is use to define if all source image have the same tile size.
     *
     * @param tileSizes tile size of future aggregated src images.
     * @return
     */
    private static boolean sameTileSize(final Dimension[] tileSizes) {
        ArgumentChecks.ensureNonNull("tileSize", tileSizes);
        if (tileSizes.length == 0)
            return false;

        final Dimension dimRef = tileSizes[0];
        for (int d = 1; d < tileSizes.length; d++) {
            if (!(dimRef.equals(tileSizes[d])))
                return false;
        }
        return true;
    }

    /**
     * Throw {@link AssertionError} if {@link PixelIterator} position doesn't
     * match with expected position given in parameters.
     *
     * @param pix current tested iterator.
     * @param expectedPosX expected raster X position.
     * @param expectedPosY expected raster X position.
     * @return true if positions match.
     */
    private static boolean checkPositions(final String message, final PixelIterator pix, final int expectedPosX, final int expectedPosY) {
        ArgumentChecks.ensureNonNull("PixelIterator", pix);
        assert pix.getX() == expectedPosX : message+" expected position X : "+expectedPosX+", found : "+pix.getX();
        assert pix.getY() == expectedPosY : message+" expected position Y : "+expectedPosY+", found : "+pix.getY();
        return true;
    }
}
