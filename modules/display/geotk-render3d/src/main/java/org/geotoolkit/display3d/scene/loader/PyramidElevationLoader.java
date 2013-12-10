/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
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
package org.geotoolkit.display3d.scene.loader;

import org.apache.sis.geometry.GeneralDirectPosition;
import org.apache.sis.storage.DataStoreException;
import org.geotoolkit.coverage.*;
import org.geotoolkit.image.interpolation.Interpolation;
import org.geotoolkit.image.interpolation.InterpolationCase;
import org.geotoolkit.image.interpolation.Resample;
import org.geotoolkit.image.iterator.PixelIterator;
import org.geotoolkit.image.iterator.PixelIteratorFactory;
import org.geotoolkit.math.XMath;
import org.geotoolkit.referencing.CRS;
import org.geotoolkit.referencing.operation.MathTransforms;
import org.geotoolkit.referencing.operation.matrix.XAffineTransform;
import org.geotoolkit.referencing.operation.transform.AffineTransform2D;
import org.opengis.geometry.DirectPosition;
import org.opengis.geometry.Envelope;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.MathTransform;
import org.opengis.referencing.operation.NoninvertibleTransformException;
import org.opengis.referencing.operation.TransformException;
import org.opengis.util.FactoryException;

import javax.measure.converter.ConversionException;
import java.awt.*;
import java.awt.image.*;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import org.geotoolkit.display3d.utils.TextureUtils;
import org.geotoolkit.internal.image.ImageUtilities;

/**
 * @author Thomas Rouby (Geomatys))
 */
public class PyramidElevationLoader implements ElevationLoader {

    private final static double[][] mask = new double[5][5];
    static {
        mask[0] = new double[]{ 0, 1, 2, 1, 0};
        mask[1] = new double[]{ 1, 2, 3, 2, 1};
        mask[2] = new double[]{ 2, 3, 4, 3, 2};
        mask[3] = new double[]{ 1, 2, 3, 2, 1};
        mask[4] = new double[]{ 0, 1, 2, 1, 0};
    }

//    private final CoverageReference dataCoverage;
    private final PyramidalCoverageReference coverageRef;
    private Pyramid dataSource;
    private final double minElevation;
    private final double maxElevation;
    private GridMosaicRenderedImage dataRenderedImage = null;

    private CoordinateReferenceSystem outputCrs;
    private MathTransform transformToOutput, transformFromOutput;

    public PyramidElevationLoader(PyramidalCoverageReference ref) throws FactoryException, ConversionException, DataStoreException {
        this.coverageRef = ref;

        final Collection<Pyramid> pyramidsMNT = (Collection<Pyramid>) ref.getPyramidSet().getPyramids();
        if (!pyramidsMNT.isEmpty()){
            dataSource = pyramidsMNT.iterator().next();
        }


        final GridSampleDimension elevationDim = ref.getSampleDimensions(0).get(0).geophysics(true);
        this.minElevation = elevationDim.getMinimumValue();
        this.maxElevation = elevationDim.getMaximumValue();
        //Get the Layer image

//        this.dataCoverage = dataCoverage;
//        if (dataCoverage instanceof PyramidalCoverageReference){
//            final PyramidalCoverageReference dataPyramidal = (PyramidalCoverageReference) dataCoverage;
//            final PyramidSet pyramidSet = dataPyramidal.getPyramidSet();
//            if (pyramidSet.getPyramids().size() > 0){
//                final Pyramid pyramid = pyramidSet.getPyramids().iterator().next();
//                if (pyramid != null) {
//                    this.dataSource = pyramid;
//                } else {
//                    this.dataSource = null;
//                }
//            } else {
//                this.dataSource = null;
//            }
//        } else {
//            throw new UnsupportedOperationException("CoverageReference " + dataCoverage.getClass().getName() + " is not supported by this loader");
//        }
//
//        if (this.dataSource == null) {
//            throw new DataStoreException("DataSource cannot be null");
//        }

    }

    @Override
    public double getMinimumElevation() {
        return minElevation;
    }

    @Override
    public double getMaximumElevation() {
        return maxElevation;
    }

    /**
     * Return the current outputCRS if set, else null
     * @return
     */
    public CoordinateReferenceSystem getOutputCRS() {
        return outputCrs;
    }

    public void setOutputCRS(CoordinateReferenceSystem outputCrs) throws FactoryException, ConversionException {
        this.outputCrs = outputCrs;
        createTransformOutput();
    }

    /**
     * Internal only, use setOutputCRS to recalculate output transform
     * @throws FactoryException
     * @throws ConversionException
     */
    private void createTransformOutput() throws FactoryException, ConversionException {
        if (outputCrs != null){
            final CoordinateReferenceSystem crsImg = this.dataSource.getCoordinateReferenceSystem();
            transformToOutput = CRS.findMathTransform(crsImg, outputCrs, true);
            try {
                transformFromOutput = transformToOutput.inverse();
            } catch (NoninvertibleTransformException ex) {
                transformFromOutput = CRS.findMathTransform(outputCrs, crsImg, true);
            }
        }
    }

    public BufferedImage getBufferedImageOf(Envelope outputEnv, Dimension outputDimension) throws TransformException, FactoryException, ConversionException {

        if(outputCrs == null){
            throw new TransformException("Output crs has not been set");
        }

        if (!CRS.equalsApproximatively(outputEnv.getCoordinateReferenceSystem(), outputCrs)){
            this.setOutputCRS(outputEnv.getCoordinateReferenceSystem());
        }

        if (dataSource != null) {
            final Envelope env = CRS.transform(transformFromOutput, outputEnv);

            final double scale = env.getSpan(0)/outputDimension.width;
            final int indexImg = TextureUtils.getNearestScaleIndex(dataSource.getScales(), scale);

            if (dataRenderedImage != null) {
                final GridMosaic gridMosaic = dataRenderedImage.getGridMosaic();
                final double mosaicScale = gridMosaic.getScale();
                final double mosaicIndex = TextureUtils.getNearestScaleIndex(dataSource.getScales(), mosaicScale);
                if (gridMosaic.getPyramid() != dataSource || mosaicIndex != indexImg) {
                    final Collection<GridMosaic> mosaics = dataSource.getMosaics(indexImg);
                    if (!mosaics.isEmpty()) {
                        dataRenderedImage = new GridMosaicRenderedImage(mosaics.iterator().next());
                    } else {
                        dataRenderedImage = null;
                        return null;
                    }
                }
            } else {
                final Collection<GridMosaic> mosaics = dataSource.getMosaics(indexImg);
                if (!mosaics.isEmpty()) {
                    dataRenderedImage = new GridMosaicRenderedImage(mosaics.iterator().next());
                } else {
                    dataRenderedImage = null;
                    return null;
                }
            }
        }

        return extractTileImage(outputEnv, dataRenderedImage, transformFromOutput, outputDimension);
    }

    public double getSmoothValueOf(DirectPosition position, double scale) throws FactoryException, ConversionException, TransformException {
        final DirectPosition pos = new GeneralDirectPosition(this.dataSource.getCoordinateReferenceSystem());
        if (CRS.equalsApproximatively(this.dataSource.getCoordinateReferenceSystem(), position.getCoordinateReferenceSystem())) {
            pos.setOrdinate(0, position.getOrdinate(0));
            pos.setOrdinate(1, position.getOrdinate(1));
        } else {
            if (!CRS.equalsApproximatively(position.getCoordinateReferenceSystem(), outputCrs)){
                this.setOutputCRS(position.getCoordinateReferenceSystem());
            }
            transformFromOutput.transform(position, pos);
            scale *= XAffineTransform.getScale((AffineTransform2D)transformFromOutput);
        }

        final int indexImg = TextureUtils.getNearestScaleIndex(dataSource.getScales(), scale);
        if (dataRenderedImage != null) {
            final GridMosaic gridMosaic = dataRenderedImage.getGridMosaic();
            final double mosaicScale = gridMosaic.getScale();
            final double mosaicIndex = TextureUtils.getNearestScaleIndex(dataSource.getScales(), mosaicScale);
            if (gridMosaic.getPyramid() != dataSource || mosaicIndex != indexImg) {
                final Collection<GridMosaic> mosaics = dataSource.getMosaics(indexImg);
                if (!mosaics.isEmpty()) {
                    dataRenderedImage = new GridMosaicRenderedImage(mosaics.iterator().next());
                } else {
                    dataRenderedImage = null;
                    return minElevation;
                }
            }
        } else {
            final Collection<GridMosaic> mosaics = dataSource.getMosaics(indexImg);
            if (!mosaics.isEmpty()) {
                dataRenderedImage = new GridMosaicRenderedImage(mosaics.iterator().next());
            } else {
                dataRenderedImage = null;
                return minElevation;
            }
        }

        final Envelope env = dataRenderedImage.getGridMosaic().getEnvelope();

        boolean contains = true;
        final CoordinateReferenceSystem crs = env.getCoordinateReferenceSystem();
        for (int i=0; i<crs.getCoordinateSystem().getDimension() && contains; i++) {
            final double ordinate = pos.getOrdinate(i);
            contains = env.getMinimum(i) < ordinate && ordinate < env.getMaximum(i);
        }

        if (contains) {

            final int x = (int)Math.floor(((pos.getOrdinate(0)-env.getMinimum(0))/env.getSpan(0)) * dataRenderedImage.getWidth());
            final int startX = XMath.clamp(x-2, 0, dataRenderedImage.getWidth()-1);
            final int y = dataRenderedImage.getHeight() - (int)Math.floor(((pos.getOrdinate(1) - env.getMinimum(1)) / env.getSpan(1)) * dataRenderedImage.getHeight());
            final int startY = XMath.clamp(y-2, 0, dataRenderedImage.getHeight()-1);

            final int width = Math.min(startX+5, dataRenderedImage.getWidth()-1) - startX;
            final int height = Math.min(startY+5, dataRenderedImage.getHeight()-1) - startY;

            if (width > 0 && height > 0){
                final Raster pixels = dataRenderedImage.getData(
                        new Rectangle(startX, startY, width, height));

                final double[] values = pixels.getSamples(pixels.getMinX(), pixels.getMinY(), width, height, 0, (double[])null);
                double value = 1.0;
                double divider = 0.0;

                for (int u=0; u<width; u++) {
                    for (int v=0; v<height; v++) {
                        int i = 2-(x-(startX+u));
                        int j = 2-(y-(startY+v));

                        divider += mask[i][j];
                        value += mask[i][j] * values[u+v*width];
                    }
                }

                if(Double.isNaN(value)){
                    return minElevation;
                }

                if (divider > 0.0) {
                    return value/divider;
                }
            }
        }

        return minElevation;
    }

    public double getValueOf(DirectPosition position, double scale) throws DataStoreException, TransformException, IOException, ConversionException, FactoryException {

        final DirectPosition pos = new GeneralDirectPosition(this.dataSource.getCoordinateReferenceSystem());
        if (CRS.equalsApproximatively(this.dataSource.getCoordinateReferenceSystem(), position.getCoordinateReferenceSystem())) {
            pos.setOrdinate(0, position.getOrdinate(0));
            pos.setOrdinate(1, position.getOrdinate(1));
        } else {
            if (!CRS.equalsApproximatively(position.getCoordinateReferenceSystem(), outputCrs)){
                this.setOutputCRS(position.getCoordinateReferenceSystem());
            }
            transformFromOutput.transform(position, pos);
            scale *= XAffineTransform.getScale((AffineTransform2D)transformFromOutput);
        }

        final int indexImg = TextureUtils.getNearestScaleIndex(dataSource.getScales(), scale);
        double val = getValueOf(pos, indexImg);
        if(Double.isNaN(val)){
            val = minElevation;
        }
        return val;
    }

    private double getValueOf(DirectPosition pos, int indexImg) throws FactoryException, ConversionException, TransformException, DataStoreException, IOException {

        final Collection<GridMosaic> mosaics = this.dataSource.getMosaics(indexImg);
        Iterator<GridMosaic> iterator = mosaics.iterator();
        if (iterator.hasNext()) {
            final GridMosaic mosaic = iterator.next();

            final Envelope mosaicEnv = mosaic.getEnvelope();

            boolean contains = true;
            final CoordinateReferenceSystem crs = mosaicEnv.getCoordinateReferenceSystem();
            for (int i=0; i<crs.getCoordinateSystem().getDimension() && contains; i++) {
                final double ordinate = pos.getOrdinate(i);
                contains = mosaicEnv.getMinimum(i) < ordinate && ordinate < mosaicEnv.getMaximum(i);
            }

            if (contains) {
                final Dimension tileSize = mosaic.getTileSize();
                final Dimension gridSize = mosaic.getGridSize();

                final double tileLon = mosaicEnv.getSpan(0) / gridSize.getWidth();
                final double tileLat = mosaicEnv.getSpan(1) / gridSize.getHeight();

                final int posX = (int)Math.floor((pos.getOrdinate(0)-mosaicEnv.getMinimum(0))/tileLon);
                final int posY = gridSize.height - (int)Math.ceil((pos.getOrdinate(1)-mosaicEnv.getMinimum(1))/tileLat);

                final TileReference tile = mosaic.getTile(posX, posY, null);
                final Envelope tileEnv = mosaic.getEnvelope(posX, posY);

                final RenderedImage tileImage;
                if (tile.getInput() instanceof RenderedImage){
                    tileImage = (RenderedImage) tile.getInput();
                } else {
                    tileImage = tile.getImageReader().read(tile.getImageIndex());
                }

                final int x = (int)Math.floor(((pos.getOrdinate(0)-tileEnv.getMinimum(0))/tileEnv.getSpan(0))*tileSize.getWidth());
                final int y = tileSize.height - (int)Math.floor(((pos.getOrdinate(1) - tileEnv.getMinimum(1)) / tileEnv.getSpan(1)) * tileSize.getHeight());

                final Raster pixel = tileImage.getData(new Rectangle(XMath.clamp(x, 0, tileSize.width-1), XMath.clamp(y, 0, tileSize.height-1), 1, 1));

                return pixel.getPixel(pixel.getMinX(),pixel.getMinY(), (double[])null)[0];
            }
        }

        return 0.0;
    }

    private static BufferedImage extractTileImage(final Envelope tileEnvelope, final GridMosaicRenderedImage dataRenderedImage, final MathTransform transformFromOutput, final Dimension tileSize) throws TransformException {
        if (dataRenderedImage == null) {
            return null;
        }

        final double targetTileWidth = tileSize.width;
        final double targetTileHeight = tileSize.height;

        final GridMosaic gridmosaic = dataRenderedImage.getGridMosaic();
        final MathTransform mosaicCrsToMosaicGrid = AbstractGridMosaic.getTileGridToCRS(gridmosaic, new Point(0, 0)).inverse();

        final AffineTransform2D targetGridToTargetCrs = new AffineTransform2D(
                tileEnvelope.getSpan(0)/targetTileWidth,
                0, 0,
                -tileEnvelope.getSpan(1)/targetTileHeight,
                tileEnvelope.getMinimum(0),
                tileEnvelope.getMaximum(1));

        final ColorModel sourceColorModel = dataRenderedImage.getColorModel();
        final Raster prototype = dataRenderedImage.getData(new Rectangle(1, 1));
        //prepare the output image
        final WritableRaster targetRaster = prototype.createCompatibleWritableRaster(tileSize.width, tileSize.height);
        final BufferedImage targetImage = new BufferedImage(sourceColorModel, targetRaster, sourceColorModel.isAlphaPremultiplied(), null);
        ImageUtilities.fill(targetImage, Double.NaN);

        final MathTransform sourceToTarget = MathTransforms.concatenate(
                targetGridToTargetCrs, transformFromOutput, mosaicCrsToMosaicGrid);

        //resample image
        final double[] fillValue = new double[targetImage.getData().getNumBands()];
        Arrays.fill(fillValue, Double.NaN);
        final PixelIterator it = PixelIteratorFactory.createRowMajorIterator(dataRenderedImage);
        final Interpolation interpol = Interpolation.create(it, InterpolationCase.NEIGHBOR, 2);
        final Resample resampler = new Resample(sourceToTarget, targetImage, interpol, fillValue);
        resampler.fillImage();

        return targetImage;
    }

}
