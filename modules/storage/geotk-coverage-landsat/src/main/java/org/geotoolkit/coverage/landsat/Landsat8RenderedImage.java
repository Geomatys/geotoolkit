/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
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
package org.geotoolkit.coverage.landsat;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferByte;
import java.awt.image.DataBufferDouble;
import java.awt.image.DataBufferFloat;
import java.awt.image.DataBufferInt;
import java.awt.image.DataBufferShort;
import java.awt.image.DataBufferUShort;
import java.awt.image.Raster;
import java.awt.image.SampleModel;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

import javax.imageio.ImageReadParam;
import javax.imageio.ImageReader;

import org.opengis.referencing.datum.PixelInCell;
import org.opengis.referencing.operation.MathTransform;
import org.opengis.referencing.operation.TransformException;

import org.apache.sis.internal.referencing.j2d.AffineTransform2D;
import org.apache.sis.util.ArgumentChecks;
import org.apache.sis.util.logging.Logging;

import org.geotoolkit.image.internal.ImageUtils;
import org.geotoolkit.image.internal.PhotometricInterpretation;
import org.geotoolkit.image.internal.PlanarConfiguration;
import org.geotoolkit.image.internal.SampleType;
import org.geotoolkit.image.interpolation.InterpolationCase;
import org.geotoolkit.image.interpolation.Resample;
import org.geotoolkit.image.io.XImageIO;
import org.geotoolkit.image.io.large.AbstractLargeRenderedImage;
import org.geotoolkit.metadata.iso.spatial.PixelTranslation;

/**
 * Special {@link AbstractLargeRenderedImage} implementation for Landsat 8 image reading.<br>
 * Aim of this class is to aggregate on the fly all band during tile reading.<br>
 * Now, internal reader need to be updated for this use.<br>
 * For an efficient way reader must know use ImageReadParam.setSrcRenderSize()
 * to avoid unneccessary resampling for each tile.
 *
 * @author Remi Marechal (Geomatys).
 * @version 1.0
 * @since   1.0
 */
public class Landsat8RenderedImage extends AbstractLargeRenderedImage {

    /*
    * TODO :
    * Maybe performance should be improve with just a limited cache
    * of 9 tiles to avoid requesting tile during interpolation or other ....
    */

    private final static Logger LOGGER = Logging.getLogger("org.geotoolkit.coverage.landsat");

    /**
     * Different aggregated image bands which compose image.
     * Moreother image numBands equals size of this array.
     */
    private final Path[] bands;

    /**
     * Subsampling offset.
     * Offset of the view in the real image space.
     */
    private final double trsx;
    private final double trsy;

    /**
     * Subsampling scale.
     * Scale of the view in the real image space.
     */
    private final double scaleX;
    private final double scaleY;

    /**
     * Define if the rasters at extremum image boundary,
     * should be clipped or not to to the image boundary size.
     */
    private final boolean clipped;

    private SampleType sampleType;

    private final ColorModel oneBandColorModel;

    /**
     * Define an image which has outImgDimension as its boundary, which represent
     * a view of the srcImgBoundary into original image space.<br>
     * Moreover a subsampling is directly effectuate and aggregate all band on the fly during read tile.
     *
     * @param srcImgBoundary original source read image region.
     * @param bands array of {@link Path} to read all bands.
     * @param sampleModel {@link SampleModel} of this image.
     * @param outImgDimension size of this image.
     * @param colorModel {@link ColorModel} of this image.
     */
    public Landsat8RenderedImage(final Rectangle srcImgBoundary, final Dimension outImgDimension, final SampleModel sampleModel,
            final ColorModel colorModel, final Path ...bands) {
        super((int)outImgDimension.getWidth(), (int)outImgDimension.getHeight(), sampleModel, colorModel);
        ArgumentChecks.ensureNonNull("bands", bands);
        if (bands.length == 0)
            throw new IllegalArgumentException("Impossible to define appropriate bands with empty bands paths array.");

        clipped    = true;
        this.bands = bands;
        scaleX     = srcImgBoundary.getWidth() / outImgDimension.getWidth() ;
        scaleY     = srcImgBoundary.getHeight() / outImgDimension.getHeight(); //-- verif scale > 1
        trsx       = srcImgBoundary.getMinX();
        trsy       = srcImgBoundary.getMinY();
        sampleType = SampleType.valueOf(sampleModel.getDataType());

        oneBandColorModel = ImageUtils.createColorModel(sampleType, 1, PhotometricInterpretation.GrayScale, null);
    }

    @Override
    public Raster getTile(int tileX, int tileY) {
        final int tileWidth  = getTileWidth();
        final int tileHeight = getTileHeight();

        //-- clip raster maximum boundary values with the current image size
        int brx = Math.min(getWidth(), (tileX + 1) * tileWidth);
        int bry = Math.min(getHeight(), (tileY + 1) * tileHeight);

        //-- size of the current requested tile.
        final int tlx = tileX * tileWidth;
        final int tly = tileY * tileHeight;
        final int rasterWidth  = (brx - tlx);
        final int rasterHeight = (bry - tly);

        final int srcRegionMinX = (int) Math.floor(tlx * scaleX + trsx);
        final int srcRegionMinY = (int) Math.floor(tly * scaleY + trsy);
        final int srcRegionMaxX = (int) Math.ceil(brx * scaleX + trsx);
        final int srcRegionMaxY = (int) Math.ceil(bry * scaleY + trsy);

        final Dimension srcRenderSize = new Dimension(rasterWidth, rasterHeight);
        final Rectangle srcRegion = new Rectangle(srcRegionMinX, srcRegionMinY,
                                                  srcRegionMaxX - srcRegionMinX,
                                                  srcRegionMaxY - srcRegionMinY);

        if (!clipped
          && (rasterWidth < tileWidth
           || rasterHeight < tileHeight)) {

            //-- will do re-copy
            return null;
        }

        final SampleModel oneBandSampleModel = ImageUtils.createSampleModel(PlanarConfiguration.Banded, sampleType,
                rasterWidth, rasterHeight, 1);

        //-- create array bank container
        final Object bankData = createBankData(sampleType, bands.length);

        /*
         * Build Callable to multi thread reading band.
         */
         final ExecutorService poule = Executors.newFixedThreadPool(bands.length);

        try {
            int b = 0;
            for (final Path band : bands) {

                if (!Files.exists(band))
                    throw new IllegalStateException("The data at current path : "+band+" doesn't exist.");

                //-- submit reading into thread
                poule.submit(new BandReader(sampleType, band, oneBandSampleModel, b++, bankData, srcRegion, srcRenderSize));
            }


            //-- waiting end of reading
            poule.shutdown();
            poule.awaitTermination(2, TimeUnit.MINUTES);

            //-- all bands are read
            final DataBuffer dataBuffer = createDatabuffer(sampleType, bankData, rasterWidth * rasterHeight);
            final SampleModel createSampleModel = ImageUtils.createSampleModel(PlanarConfiguration.Banded, sampleType,
                                                                               rasterWidth, rasterHeight, bands.length);

            return Raster.createWritableRaster(createSampleModel, dataBuffer, new Point((int) tlx, (int) tly));
        } catch (UnsupportedOperationException | InterruptedException ex) {
            throw new IllegalStateException(ex);
        }
    }

    /**
     * If read tile is not at the requested size, effectuate resampling to ajust it.
     *
     * @param tile
     * @param destImg
     * @throws TransformException
     */
    private void resampleTile(final BufferedImage tile, final BufferedImage destImg)
            throws TransformException {

        MathTransform affineTransform2D = new AffineTransform2D(tile.getWidth() / (double) destImg.getWidth(), 0, 0, destImg.getHeight() / (double) tile.getHeight(), 0, 0);
        affineTransform2D = PixelTranslation.translate(affineTransform2D, PixelInCell.CELL_CORNER, PixelInCell.CELL_CENTER);

        final Resample resample = new Resample(affineTransform2D, destImg, tile, InterpolationCase.NEIGHBOR);
        resample.fillImage();
    }

    /**
     * Build a 2D array, initialized at the asked {@link SampleType}.
     *
     * @param sampleType
     * @param bandNumber
     * @return
     */
    private Object createBankData(final SampleType sampleType, final int bandNumber) {
        switch (sampleType) {
            case Byte : {
                return new byte[bandNumber][];
            }
            case Short  :
            case UShort : {
                return new short[bandNumber][];
            }
            case Integer : {
                return new int[bandNumber][];
            }
            case Float : {
                return new float[bandNumber][];
            }
            case Double : {
                return new double[bandNumber][];
            }
            default : throw new IllegalStateException("Current SampleType : "+sampleType+" is not known."); //-- should never append
        }
    }

    /**
     * Create a {@link DataBuffer} initialized at correct sample type and filled by bankData array.
     *
     * @param sampleType
     * @param bankDatas
     * @param buffersize
     * @return
     */
    private DataBuffer createDatabuffer(final SampleType sampleType, final Object bankDatas, final int buffersize) {
        switch (sampleType) {
            case Byte : {
                return new DataBufferByte((byte[][]) bankDatas, buffersize);
            }
            case Short : {
                return new DataBufferShort((short[][]) bankDatas, buffersize);
            }
            case UShort : {
                return new DataBufferUShort((short[][]) bankDatas, buffersize);
            }
            case Integer : {
                return new DataBufferInt((int[][]) bankDatas, buffersize);
            }
            case Float : {
                return new DataBufferFloat((float[][]) bankDatas, buffersize);
            }
            case Double : {
                return new DataBufferDouble((double[][]) bankDatas, buffersize);
            }
            default : throw new IllegalStateException("Current SampleType : "+sampleType+" is not known."); //-- should never append
        }
    }

    /**
     *
     * @param sampleType
     * @param bandID
     * @param destinationArray
     * @param datas
     */
    private void mergeRasters(final SampleType sampleType, final int bandID, final Object destinationArray, final BufferedImage datas) {

        switch (sampleType) {
            case Byte : {
                final byte[][] da  = (byte[][]) destinationArray;
                final DataBufferByte dbb = (DataBufferByte) datas.getData().getDataBuffer();
                da[bandID] = dbb.getData();
                break;
            }
            case Short : {
                final short[][] da  = (short[][]) destinationArray;
                final DataBufferShort dbb = (DataBufferShort) datas.getData().getDataBuffer();
                da[bandID] = dbb.getData();
                break;
            }

            case UShort : {
                final short[][] da  = (short[][]) destinationArray;
                final DataBufferUShort dbb = (DataBufferUShort) datas.getData().getDataBuffer();
                da[bandID] = dbb.getData();
                break;
            }
            case Integer : {
                final int[][] da  = (int[][]) destinationArray;
                final DataBufferInt dbb = (DataBufferInt) datas.getData().getDataBuffer();
                da[bandID] = dbb.getData();
                break;
            }
            case Float : {
                final float[][] da  = (float[][]) destinationArray;
                final DataBufferFloat dbb = (DataBufferFloat) datas.getData().getDataBuffer();
                da[bandID] = dbb.getData();
                break;
            }
            case Double : {
                final double[][] da  = (double[][]) destinationArray;
                final DataBufferDouble dbb = (DataBufferDouble) datas.getData().getDataBuffer();
                da[bandID] = dbb.getData();
                break;
            }
        }

    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        //close decoder : not needed, the jni binding has a fallback on finalize.
        // calling a close explicitly here provokes a JVM crash
    }

    /**
     * Private class to multi-Thread band reading.
     */
    private class BandReader implements Runnable {

        private final Object bankDatas;
        private final Path bandPath;
        private final Rectangle sourceRegion;
        private final Dimension renderSize;
        private final int bandIndex;
        private final SampleModel oneBandSampleModel;

        public BandReader(final SampleType sampleType, final Path bandPath,
                final SampleModel oneBandSampleModel, final int bandIndex, final Object bankDatas,
                final Rectangle sourceRegion, final Dimension renderSize) {
            this.bankDatas    = bankDatas;
            this.bandPath     = bandPath;
            this.sourceRegion = sourceRegion;
            this.renderSize   = renderSize;
            this.bandIndex    = bandIndex;
            this.oneBandSampleModel = oneBandSampleModel;
        }


        @Override
        public void run() {
            final ImageReader reader;
            try {
                reader = XImageIO.getReader(bandPath, false, true);

                if (!sampleType.equals(SampleType.valueOf(reader.getRawImageType(0).getSampleModel().getDataType()))) {
                    throw new IllegalArgumentException("Expected datatype : "+sampleType+", found : "
                            +SampleType.valueOf(reader.getRawImageType(0).getSampleModel().getDataType()));
                }
                final ImageReadParam defaultReadParam = reader.getDefaultReadParam();
                defaultReadParam.setSourceRegion(sourceRegion);

                if (defaultReadParam.canSetSourceRenderSize()) {
                    defaultReadParam.setSourceRenderSize(renderSize);
                } else {
                    //-- cast scale into int to increase out image size compared to math.ceil()
                    defaultReadParam.setSourceSubsampling((int) Math.max(1, sourceRegion.width / renderSize.width),
                                                          (int) Math.max(1, sourceRegion.height / renderSize.height), 0, 0);
                }

                BufferedImage read = reader.read(0, defaultReadParam);
                reader.dispose();

                //-- do resampling if necessary
                if (read.getWidth()  != renderSize.width
                 || read.getHeight() != renderSize.height) {
                    final BufferedImage destImg = new BufferedImage(oneBandColorModel,
                            Raster.createWritableRaster(oneBandSampleModel, new Point()),
                            false, null);
                    resampleTile(read, destImg);
                    read = destImg;
                }
                mergeRasters(sampleType, bandIndex, bankDatas, read);
            } catch (IOException | TransformException ex) {
                throw new IllegalStateException(ex);
            }
        }
    }
}
