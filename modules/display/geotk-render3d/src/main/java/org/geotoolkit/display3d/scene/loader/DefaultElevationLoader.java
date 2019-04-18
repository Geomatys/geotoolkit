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
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.DataBuffer;
import java.awt.image.Raster;
import java.awt.image.RenderedImage;
import java.awt.image.WritableRaster;
import java.util.Arrays;
import java.util.List;
import javax.measure.IncommensurableException;
import org.apache.sis.coverage.SampleDimension;
import org.apache.sis.coverage.grid.GridCoverage;
import org.apache.sis.coverage.grid.GridGeometry;
import org.apache.sis.image.PixelIterator;
import org.apache.sis.internal.referencing.j2d.AffineTransform2D;
import org.apache.sis.referencing.CRS;
import org.apache.sis.referencing.operation.transform.MathTransforms;
import org.apache.sis.storage.DataStoreException;
import org.geotoolkit.coverage.SampleDimensionUtils;
import org.geotoolkit.coverage.io.DisjointCoverageDomainException;
import org.geotoolkit.coverage.io.GridCoverageReadParam;
import org.geotoolkit.coverage.io.GridCoverageReader;
import org.geotoolkit.display.PortrayalException;
import org.geotoolkit.image.BufferedImages;
import org.geotoolkit.image.internal.ImageUtilities;
import org.geotoolkit.image.interpolation.Interpolation;
import org.geotoolkit.image.interpolation.InterpolationCase;
import org.geotoolkit.image.interpolation.Resample;
import org.geotoolkit.storage.coverage.GridCoverageResource;
import org.opengis.coverage.grid.SequenceType;
import org.opengis.geometry.Envelope;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.datum.PixelInCell;
import org.opengis.referencing.operation.MathTransform;
import org.opengis.referencing.operation.NoninvertibleTransformException;
import org.opengis.referencing.operation.TransformException;
import org.opengis.util.FactoryException;

/**
 *
 * @author Thomas Rouby (Geomatys)
 * @author Johann Sorel (Geomatys)
 */
public class DefaultElevationLoader extends AbstractElevationLoader {

    private final GridCoverageResource coverageRef;
    private final GridGeometry gridGeom;
    private final double minElevation;
    private final double maxElevation;

    private CoordinateReferenceSystem outputCrs;
    private MathTransform coverageToOutput, outputToCoverage;

    public DefaultElevationLoader(GridCoverageResource ref) throws FactoryException, IncommensurableException, DataStoreException {
        this.coverageRef = ref;

        this.gridGeom = coverageRef.getGridGeometry();
        final List<SampleDimension> dimensions = coverageRef.getSampleDimensions();
        if(dimensions!=null && !dimensions.isEmpty()){
            final SampleDimension elevationDim = dimensions.get(0).forConvertedValues(true);
            this.minElevation = SampleDimensionUtils.getMinimumValue(elevationDim);
            this.maxElevation = SampleDimensionUtils.getMaximumValue(elevationDim);
        }else{
            this.minElevation = 0;
            this.maxElevation = 1000;
        }

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

    @Override
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
            final CoordinateReferenceSystem crsImg = gridGeom.getCoordinateReferenceSystem();
            coverageToOutput = CRS.findOperation(crsImg, outputCrs, null).getMathTransform();
            try {
                outputToCoverage = coverageToOutput.inverse();
            } catch (NoninvertibleTransformException ex) {
                outputToCoverage = CRS.findOperation(outputCrs, crsImg, null).getMathTransform();
            }
        }
    }

    @Override
    public BufferedImage getBufferedImageOf(final Envelope outputEnv, final Dimension outputDimension) throws PortrayalException {

        if(outputCrs == null){
            throw new PortrayalException("Output crs has not been set");
        }

        if (!org.geotoolkit.referencing.CRS.equalsApproximatively(outputEnv.getCoordinateReferenceSystem(), outputCrs)){
            this.setOutputCRS(outputEnv.getCoordinateReferenceSystem());
        }

        try{
            final GridCoverageReader reader = coverageRef.acquireReader();
            final GridCoverageReadParam params = new GridCoverageReadParam();
            params.setEnvelope(outputEnv);
            try{
                final GridCoverage coverage = reader.read(params);
                return extractTileImage(outputEnv, coverage, outputToCoverage, outputDimension);
            }catch(DisjointCoverageDomainException de){
                //tile outside of the coverage, it's possible
                //create a fake tile at minimum elevation
                final BufferedImage img = BufferedImages.createImage(
                        outputDimension.width, outputDimension.height, 1, DataBuffer.TYPE_DOUBLE);
                ImageUtilities.fill(img, minElevation);
                return img;
            }

        }catch(Exception ex){
            throw new PortrayalException(ex);
        }
    }

    private static BufferedImage extractTileImage(final Envelope tileEnvelope,
            final GridCoverage coverage, final MathTransform outputCRSToCoverageCRS,
            final Dimension tileSize) throws TransformException {

        final RenderedImage dataRenderedImage = coverage.render(null);

        if (dataRenderedImage == null) {
            return null;
        }

        final double targetTileWidth = tileSize.width;
        final double targetTileHeight = tileSize.height;

        final MathTransform coverageCRSToImageGrid = coverage.getGridGeometry().getGridToCRS(PixelInCell.CELL_CORNER).inverse();

        final AffineTransform2D tileGridToOutputCRS = new AffineTransform2D(
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
                tileGridToOutputCRS, outputCRSToCoverageCRS, coverageCRSToImageGrid);

        //resample image
        final double[] fillValue = new double[targetImage.getData().getNumBands()];
        Arrays.fill(fillValue, Double.NaN);
        final PixelIterator it = new PixelIterator.Builder().setIteratorOrder(SequenceType.LINEAR).create(dataRenderedImage);
        final Interpolation interpol = Interpolation.create(it, InterpolationCase.NEIGHBOR, 2);
        final Resample resampler = new Resample(sourceToTarget, targetImage, interpol, fillValue);
        resampler.fillImage();

        return targetImage;
    }

}
