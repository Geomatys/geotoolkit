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
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.RenderedImage;
import java.awt.image.SampleModel;
import org.apache.sis.image.PixelIterator;
import org.apache.sis.image.WritablePixelIterator;
import org.apache.sis.util.ArgumentChecks;
import org.geotoolkit.image.BufferedImages;
import org.geotoolkit.image.internal.ImageUtils;
import org.geotoolkit.process.ProcessException;
import org.geotoolkit.processing.AbstractProcess;
import org.opengis.parameter.ParameterValueGroup;

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
 * Moreover, new adapted {@link ColorModel} and {@link SampleModel} are computed.
 *
 * @author Remi Marechal (Geomatys)
 * @author Johann Sorel (Geomatys)
 *
 * @see BufferedImages#createGrayScaleColorModel(int, int, int, double, double)
 * @see ImageUtils#createSampleModel(org.geotoolkit.image.internal.PlanarConfiguration, org.geotoolkit.image.internal.SampleType, int, int, int)
 */
public class BandCombineProcess extends AbstractProcess {

    private RenderedImage[] inputImages;
    private int sampleType   = -1;
    private int nbtotalbands = 0;
    private int width        = 0;
    private int height       = 0;
    private int[] nbBands;
    private int[] nbBandIndex;
    private PixelIterator[] readItes;
    private int[][] minXYs;
    private Dimension[] tilesSize;

    public BandCombineProcess(final ParameterValueGroup input) {
        super(BandCombineDescriptor.INSTANCE, input);
    }

    @Override
    protected void execute() throws ProcessException {
        ArgumentChecks.ensureNonNull("inputParameter", inputParameters);

        inputImages = inputParameters.getValue(BandCombineDescriptor.IN_IMAGES);

        if (inputImages.length == 0)
            throw new ProcessException("No image to combine", this, null);

        if (inputImages.length == 1) {
            //nothing to do
            outputParameters.getOrCreate(BandCombineDescriptor.OUT_IMAGE).setValue(inputImages[0]);
            return;
        }

        //check and extract informations, all images should have the same size and sample type.
        nbBands     = new int[inputImages.length];
        nbBandIndex = new int[inputImages.length];

        //-- attribut use only during same tile size
        readItes = new PixelIterator[inputImages.length];
        //-- minimum image coordinates only use during assert
        minXYs = new int[inputImages.length][2];

        tilesSize = new Dimension[inputImages.length];

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
            readItes[i]    = new PixelIterator.Builder().create(image);
            nbBands[i]     = sm.getNumBands();
            nbBandIndex[i] = nbtotalbands;
            nbtotalbands  += sm.getNumBands();
        }

        //TODO : use a subclass of computed image.
        final BufferedImage img = BufferedImages.createImage(width, height, nbtotalbands, sampleType);

        final org.apache.sis.image.PixelIterator[] ins = new org.apache.sis.image.PixelIterator[inputImages.length];
        for (int i=0;i<inputImages.length;i++) {
            ins[i] = org.apache.sis.image.PixelIterator.create(inputImages[i]);
        }

        final org.apache.sis.image.WritablePixelIterator out = WritablePixelIterator.create(img);

        final double[] sample = new double[nbtotalbands];
        int y,x,i,b;
        for (y=0;y<height;y++) {
            for (x=0;x<width;x++) {
                out.moveTo(x, y);
                for (i=0;i<inputImages.length;i++) {
                    ins[i].moveTo(x, y);
                    if (nbBands[i]==1) {
                        sample[nbBandIndex[i]] = ins[i].getSampleDouble(0);
                    } else {
                        for (b=0;b<nbBands[i];b++) {
                            sample[nbBandIndex[i]+b] = ins[i].getSampleDouble(b);
                        }
                    }
                }
                out.setPixel(sample);
            }
        }

        outputParameters.getOrCreate(BandCombineDescriptor.OUT_IMAGE).setValue(img);
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
        assert pix.getPosition().x == expectedPosX : message+" expected position X : "+expectedPosX+", found : "+pix.getPosition().x;
        assert pix.getPosition().y == expectedPosY : message+" expected position Y : "+expectedPosY+", found : "+pix.getPosition().y;
        return true;
    }
}
