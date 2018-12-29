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

import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;
import java.util.Arrays;
import java.util.Collection;
import javax.measure.IncommensurableException;
import org.apache.sis.geometry.Envelopes;
import org.apache.sis.internal.referencing.j2d.AffineTransform2D;
import org.apache.sis.referencing.CRS;
import org.apache.sis.referencing.operation.transform.MathTransforms;
import org.apache.sis.storage.DataStoreException;
import org.apache.sis.util.ArgumentChecks;
import org.geotoolkit.coverage.GridSampleDimension;
import org.geotoolkit.data.multires.Mosaic;
import org.geotoolkit.data.multires.Pyramid;
import org.geotoolkit.data.multires.Pyramids;
import org.geotoolkit.display.PortrayalException;
import org.geotoolkit.display3d.utils.TextureUtils;
import org.geotoolkit.image.internal.ImageUtilities;
import org.geotoolkit.image.interpolation.Interpolation;
import org.geotoolkit.image.interpolation.InterpolationCase;
import org.geotoolkit.image.interpolation.Resample;
import org.geotoolkit.image.iterator.PixelIterator;
import org.geotoolkit.image.iterator.PixelIteratorFactory;
import org.geotoolkit.storage.coverage.GridMosaicRenderedImage;
import org.geotoolkit.storage.coverage.PyramidalCoverageResource;
import org.opengis.geometry.Envelope;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.MathTransform;
import org.opengis.referencing.operation.NoninvertibleTransformException;
import org.opengis.referencing.operation.TransformException;
import org.opengis.util.FactoryException;

/**
 *
 * @author Thomas Rouby (Geomatys))
 */
public class PyramidElevationLoader extends AbstractElevationLoader {

    private final PyramidalCoverageResource coverageRef;
    private Pyramid dataSource;
    private final double minElevation;
    private final double maxElevation;
    private GridMosaicRenderedImage dataRenderedImage = null;

    private CoordinateReferenceSystem outputCrs;
    private MathTransform transformToOutput, transformFromOutput;

    public PyramidElevationLoader(final PyramidalCoverageResource ref) throws FactoryException, IncommensurableException, DataStoreException {
        ArgumentChecks.ensureNonNull("pyramid reference", ref);
        this.coverageRef = ref;

        final Collection<Pyramid> pyramidsMNT = Pyramids.getPyramids(ref);
        if (!pyramidsMNT.isEmpty()){
            dataSource = pyramidsMNT.iterator().next();
        }

        ArgumentChecks.ensureNonNull("pyramid", dataSource);

        final GridSampleDimension elevationDim = ref.getGridSampleDimensions().get(0).geophysics(true);
        this.minElevation = elevationDim.getMinimumValue();
        this.maxElevation = elevationDim.getMaximumValue();
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

    public void setOutputCRS(CoordinateReferenceSystem outputCrs) throws PortrayalException {
        this.outputCrs = outputCrs;
        try {
            createTransformOutput();
        } catch (FactoryException | IncommensurableException ex) {
            throw new PortrayalException(ex);
        }
    }

    /**
     * Internal only, use setOutputCRS to recalculate output transform
     */
    private void createTransformOutput() throws FactoryException, IncommensurableException {
        if (outputCrs != null){
            final CoordinateReferenceSystem crsImg = this.dataSource.getCoordinateReferenceSystem();
            transformToOutput = CRS.findOperation(crsImg, outputCrs, null).getMathTransform();
            try {
                transformFromOutput = transformToOutput.inverse();
            } catch (NoninvertibleTransformException ex) {
                transformFromOutput = CRS.findOperation(outputCrs, crsImg, null).getMathTransform();
            }
        }
    }

    public BufferedImage getBufferedImageOf(final Envelope outputEnv, final Dimension outputDimension) throws PortrayalException {

        if(outputCrs == null){
            throw new PortrayalException("Output crs has not been set");
        }

        if (!org.geotoolkit.referencing.CRS.equalsApproximatively(outputEnv.getCoordinateReferenceSystem(), outputCrs)){
            this.setOutputCRS(outputEnv.getCoordinateReferenceSystem());
        }

        final Envelope env;
        try {
            env = Envelopes.transform(transformFromOutput, outputEnv);
        } catch (TransformException ex) {
            throw new PortrayalException(ex);
        }

        final double scale = env.getSpan(0)/outputDimension.width;
        final int indexImg = TextureUtils.getNearestScaleIndex(dataSource.getScales(), scale);

        if (dataRenderedImage != null) {
            final Mosaic gridMosaic = dataRenderedImage.getGridMosaic();
            final double mosaicScale = gridMosaic.getScale();
            final double mosaicIndex = TextureUtils.getNearestScaleIndex(dataSource.getScales(), mosaicScale);
            if (!dataSource.getMosaics().contains(gridMosaic) || mosaicIndex != indexImg) {
                final Collection<? extends Mosaic> mosaics = dataSource.getMosaics(indexImg);
                if (!mosaics.isEmpty()) {
                    dataRenderedImage = new GridMosaicRenderedImage(mosaics.iterator().next());
                } else {
                    dataRenderedImage = null;
                    return null;
                }
            }
        } else {
            final Collection<? extends Mosaic> mosaics = dataSource.getMosaics(indexImg);
            if (!mosaics.isEmpty()) {
                dataRenderedImage = new GridMosaicRenderedImage(mosaics.iterator().next());
            } else {
                dataRenderedImage = null;
                return null;
            }
        }
        try {
            return extractTileImage(outputEnv, dataRenderedImage, transformFromOutput, outputDimension);
        } catch (TransformException ex) {
            throw new PortrayalException(ex);
        }
    }

    private static BufferedImage extractTileImage(final Envelope tileEnvelope,
            final GridMosaicRenderedImage dataRenderedImage, final MathTransform transformFromOutput,
            final Dimension tileSize) throws TransformException {
        if (dataRenderedImage == null) {
            return null;
        }

        final double targetTileWidth = tileSize.width;
        final double targetTileHeight = tileSize.height;

        final Mosaic gridmosaic = dataRenderedImage.getGridMosaic();
        final MathTransform mosaicCrsToMosaicGrid = Pyramids.getTileGridToCRS(gridmosaic, new Point(0, 0)).inverse();

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
