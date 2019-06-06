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
package org.geotoolkit.processing.coverage.resample;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.*;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.apache.sis.coverage.SampleDimension;
import org.apache.sis.coverage.grid.GridCoverage;
import org.apache.sis.coverage.grid.GridExtent;
import org.apache.sis.coverage.grid.GridGeometry;
import org.apache.sis.coverage.grid.GridRoundingMode;
import org.apache.sis.geometry.Envelopes;
import org.apache.sis.geometry.GeneralDirectPosition;
import org.apache.sis.geometry.GeneralEnvelope;
import org.apache.sis.image.WritablePixelIterator;
import org.apache.sis.internal.metadata.AxisDirections;
import org.apache.sis.internal.system.DefaultFactories;
import org.apache.sis.metadata.iso.extent.DefaultGeographicBoundingBox;
import org.apache.sis.parameter.Parameters;
import org.apache.sis.referencing.CommonCRS;
import org.apache.sis.referencing.operation.transform.LinearTransform;
import org.apache.sis.referencing.operation.transform.MathTransforms;
import org.apache.sis.referencing.operation.transform.TransformSeparator;
import org.apache.sis.util.Utilities;
import org.geotoolkit.coverage.grid.GridGeometry2D;
import org.geotoolkit.factory.Hints;
import org.geotoolkit.image.BufferedImages;
import org.geotoolkit.image.interpolation.InterpolationCase;
import org.geotoolkit.image.interpolation.Resample;
import org.geotoolkit.image.interpolation.ResampleBorderComportement;
import org.geotoolkit.internal.coverage.CoverageUtilities;
import org.geotoolkit.process.ProcessException;
import org.geotoolkit.processing.AbstractProcess;

import static org.apache.sis.internal.coverage.BufferedGridCoverage.convert;
import static org.apache.sis.util.ArgumentChecks.ensureNonNull;
import static org.geotoolkit.processing.coverage.resample.ResampleDescriptor.*;

import org.apache.sis.referencing.CRS;
import org.geotoolkit.resources.Errors;
import org.opengis.coverage.CannotEvaluateException;
import org.opengis.geometry.Envelope;
import org.opengis.metadata.extent.GeographicBoundingBox;
import org.opengis.parameter.ParameterValueGroup;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.crs.SingleCRS;
import org.opengis.referencing.datum.PixelInCell;
import org.opengis.referencing.operation.*;
import org.opengis.util.FactoryException;

import javax.annotation.Nonnull;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public class ResampleProcess extends AbstractProcess {

    public ResampleProcess(GridCoverage coverage, CoordinateReferenceSystem targetCrs, double[] background) {
        super(INSTANCE, asParameters(coverage, targetCrs,  null, null, background));
    }

    public ResampleProcess(GridCoverage coverage, CoordinateReferenceSystem targetCrs,
                           GridGeometry gridGeom, InterpolationCase interpolation, double[] background) {
        super(INSTANCE, asParameters(coverage, targetCrs, gridGeom, interpolation, background));
    }

    public ResampleProcess(final ParameterValueGroup input) {
        super(INSTANCE, input);
    }

    private static ParameterValueGroup asParameters(GridCoverage coverage, CoordinateReferenceSystem targetCrs,
            GridGeometry gridGeom, InterpolationCase interpolation, double[] background){
        final Parameters params = Parameters.castOrWrap(ResampleDescriptor.INPUT_DESC.createValue());
        params.getOrCreate(IN_COVERAGE).setValue(coverage);
        if (targetCrs != null) {
            params.getOrCreate( IN_COORDINATE_REFERENCE_SYSTEM).setValue(targetCrs);
        }
        if (gridGeom != null) {
            params.getOrCreate(IN_GRID_GEOMETRY).setValue(gridGeom);
        }
        if (background != null) {
            params.getOrCreate(IN_BACKGROUND).setValue(background);
        }
        if (interpolation != null) {
            params.getOrCreate(IN_INTERPOLATION_TYPE).setValue(interpolation);
        }
        return params;
    }

    public GridCoverage executeNow() throws ProcessException {
        execute();
        return outputParameters.getValue(OUT_COVERAGE);
    }

    /**
     * Resamples a grid coverage.
     */
    @Override
    protected void execute() throws ProcessException {
        final GridCoverage source = inputParameters.getValue(IN_COVERAGE);
        final double[] background = inputParameters.getValue(IN_BACKGROUND);
        InterpolationCase interpolation = inputParameters.getValue(IN_INTERPOLATION_TYPE);
        final ResampleBorderComportement border = inputParameters.getValue(IN_BORDER_COMPORTEMENT_TYPE);

        CoordinateReferenceSystem targetCRS = (CoordinateReferenceSystem) inputParameters.parameter("CoordinateReferenceSystem").getValue();
        final GridGeometry targetGG = inputParameters.getValue(IN_GRID_GEOMETRY);
        final GridCoverage target;

        try {
            target = reproject(source, targetCRS, targetGG, interpolation, border, background, null);
        } catch (FactoryException exception) {
            throw new CannotReprojectException(Errors.format(
                    Errors.Keys.CantReprojectCoverage_1, CoverageUtilities.getName(source)), exception);
        } catch (TransformException exception) {
            throw new CannotReprojectException(Errors.format(
                    Errors.Keys.CantReprojectCoverage_1, CoverageUtilities.getName(source)), exception);
        }
        outputParameters.getOrCreate(OUT_COVERAGE).setValue(target);
    }

    /**
     * Creates a new coverage with a different coordinate reference reference system. If a
     * grid geometry is supplied, only its {@linkplain GridGeometry#getExtent()}  grid envelope}
     * and {@linkplain GridGeometry2D#getGridToCRS grid to CRS} transform are taken in account.
     *
     * @param sourceCoverage The source grid coverage.
     * @param targetCRS      Coordinate reference system for the new grid coverage, or {@code null}.
     * @param targetGG       The target grid geometry, or {@code null} for default.
     * @param background     The background values, or {@code null} for default.
     * @param interpolationType  The interpolation to use, or {@code null} if none.
     * @param hints
     *          The rendering hints. This is usually provided by {@link AbstractCoverageProcessor}.
     *          This method will looks for {@link Hints#COORDINATE_OPERATION_FACTORY} and
     *          {@link Hints#JAI_INSTANCE} keys.
     * @return  The new grid coverage, or {@code sourceCoverage} if no resampling was needed.
     * @throws  FactoryException If a transformation step can't be created.
     * @throws TransformException If a transformation failed.
     */
    public static GridCoverage reproject(GridCoverage              sourceCoverage,
                                           CoordinateReferenceSystem targetCRS,
                                           GridGeometry              targetGG,
                                           InterpolationCase         interpolationType,
                                           double[]                  background,
                                           final Hints               hints)
            throws FactoryException, TransformException
    {
        return reproject(sourceCoverage, targetCRS, targetGG, interpolationType,
                null, background, hints);
    }

    /**
     * Creates a new coverage with a different coordinate reference reference system. If a
     * grid geometry is supplied, only its {@linkplain GridGeometry#getExtent()}  grid envelope}
     * and {@linkplain GridGeometry#getGridToCRS grid to CRS} transform are taken in account.
     *
     * @param sourceCov      The source grid coverage.
     * @param targetCRS      Coordinate reference system for the new grid coverage, or {@code null}.
     * @param targetGG       The target grid geometry, or {@code null} for default.
     * @param background     The background values, or {@code null} for default.
     * @param borderComportement The comportement used when points are outside of the source coverage,
     *          or {@code null} for default. Default is EXTRAPOLATION.
     * @param interpolationType  The interpolation to use, or {@code null} if none.
     * @param hints
     *          The rendering hints. This is usually provided by {@link AbstractCoverageProcessor}.
     *          This method will looks for {@link Hints#COORDINATE_OPERATION_FACTORY} and
     *          {@link Hints#JAI_INSTANCE} keys.
     * @return  The new grid coverage, or {@code sourceCoverage} if no resampling was needed.
     * @throws  FactoryException If a transformation step can't be created.
     * @throws TransformException If a transformation failed.
     */
    public static GridCoverage reproject(GridCoverage              sourceCov,
                                           CoordinateReferenceSystem targetCRS,
                                           GridGeometry              targetGG,
                                           InterpolationCase         interpolationType,
                                           ResampleBorderComportement borderComportement,
                                           double[]                  background,
                                           final Hints               hints)
            throws FactoryException, TransformException
    {
        //set default values

        if (interpolationType == null) {
            interpolationType = InterpolationCase.NEIGHBOR;
        }
        if(borderComportement==null)
            borderComportement = ResampleBorderComportement.EXTRAPOLATION;
        // Temporary HACK because org.geotoolkit.image.interpolation.Resample does not support cropping.
        else if (ResampleBorderComportement.CROP.equals(borderComportement))
            borderComportement = ResampleBorderComportement.FILL_VALUE;


        ////////////////////////////////////////////////////////////////////////////////////////
        ////                                                                                ////
        //// =======>>  STEP 1: Extracts needed informations from the parameters   <<====== ////
        ////            STEP 2: Creates the "target to source" MathTransform                ////
        ////            STEP 3: Computes the target image layout                            ////
        ////            STEP 4: Applies the transform operation ("Affine","Warp")           ////
        ////                                                                                ////
        ////////////////////////////////////////////////////////////////////////////////////////

        /*
         * The projection are usually applied on floating-point values, in order to gets maximal precision and to handle
         * correctly the special case of NaN values. However, we can apply the projection on integer values if the
         * interpolation type is "nearest neighbor", since this is not really an interpolation. We can also keep integer
         * values if sample conversion from packed to geophysic is fully linear, and no "no-data" value is defined. This
         * last requirement is very important, in order to avoid mixing of aberrant values.
         *
         * If one of the two conditions above is meet, then we verify if an "integer version" of the image is available
         * as a source of the source coverage (i.e. the floating-point image is derived from the integer image, not the
         *  converse).
         */
        final List<SampleDimension> sds = sourceCov.getSampleDimensions();
        if (sds == null || sds.isEmpty())
            throw new IllegalArgumentException("Input coverage does not properly declare sample dimensions");
        final int nBands = sds.size();

        final boolean interpolationIsNearestNeighbor = InterpolationCase.NEIGHBOR.equals(interpolationType);
        final boolean geophysicRequired = !interpolationIsNearestNeighbor && isGeophysicRequired(sds);
        sourceCov = sourceCov.forConvertedValues(geophysicRequired);

        //extract fill value after the resampling view type has been chosen. Note that if no geophysic view is needed, no fill value can be correctly applied.
        final double[] fillValue;
        if (background != null) {
            if (nBands != background.length) {
                throw new TransformException("Invalid default values, expected size " + nBands + " but was " + background.length);
            }
            fillValue = background;
        } else if (geophysicRequired || interpolationIsNearestNeighbor) {
            fillValue = getFillValue(sourceCov);
        } else fillValue = null;

        ////////////////////////////////////////////////////////////////////////////////////////
        ////                                                                                ////
        ////            STEP 1: Extracts needed informations from the parameters            ////
        //// =======>>  STEP 2: Creates the "target to source" MathTransform       <<====== ////
        ////            STEP 3: Computes the target image layout                            ////
        ////            STEP 4: Applies the transform operation ("Affine","Warp")           ////
        ////                                                                                ////
        ////////////////////////////////////////////////////////////////////////////////////////

        final OutputGridBuilder builder = new OutputGridBuilder(sourceCov.getGridGeometry(), targetGG)
                .setTargetCrs(targetCRS);

        MathTransform targetToSource = builder.createBridge(PixelInCell.CELL_CENTER);

        ////////////////////////////////////////////////////////////////////////////////////////
        ////                                                                                ////
        ////            STEP 1: Extracts needed informations from the parameters            ////
        ////            STEP 2: Creates the "target to source" MathTransform                ////
        //// =======>>  STEP 3: Computes the target image layout                   <<====== ////
        ////            STEP 4: Applies the transform operation ("Affine","Warp")           ////
        ////                                                                                ////
        ////////////////////////////////////////////////////////////////////////////////////////

        RenderedImage sourceImage = sourceCov.render(null); // TODO : We should check if only a subset of source image is required
        final List<SampleDimension> outputSampleDims = geophysicRequired?
                sds.stream()
                        .map(sd -> sd.forConvertedValues(true))
                        .collect(Collectors.toList())
                : sds;

        final Dimension outputDim = builder.getTargetImageDimension();
        final BufferedImage targetImage = BufferedImages.createImage(outputDim.width, outputDim.height, sourceImage);
        final WritableRaster targetRaster = targetImage.getRaster();
        //fill target image with fill values
        if (fillValue != null) {
            //if fill values are all 0 do nothing
            //zero is the default value in created raster
            boolean allZero = true;
            for (double d : fillValue) {
                if (d != 0.0) {
                    allZero = false;
                    break;
                }
            }
            if (!allZero) {
                final WritablePixelIterator writer = WritablePixelIterator.create(targetImage);
                while (writer.next()) {
                    writer.setPixel(fillValue);
                }
                writer.close();
            }
        }

        ////////////////////////////////////////////////////////////////////////////////////////
        ////                                                                                ////
        ////            STEP 1: Extracts needed informations from the parameters            ////
        ////            STEP 2: Creates the "target to source" MathTransform                ////
        ////            STEP 3: Computes the target image layout                            ////
        //// =======>>  STEP 4: Applies the transform operation ("Affine","Warp")  <<====== ////
        ////                                                                                ////
        ////////////////////////////////////////////////////////////////////////////////////////

        if (targetToSource.isIdentity()) {
            // TODO: couldn't we return directly source coverage ?
            if (sourceImage.getWidth() == targetRaster.getWidth() && sourceImage.getHeight() == targetRaster.getHeight()) {
                //we can directly copy raster to raster
                targetRaster.setDataElements(0, 0, sourceImage.getData());
            } else {
                // the setRect method is more expensive but will make the appropriate clipping
                targetRaster.setRect(0, 0, sourceImage.getData());
            }

        } else {

            //try to optimize resample using java wrap operation
//        if(canUseJavaInterpolation(sourceImage, targetToSource, interpolationType)){
//            allSteps2D = PixelTranslation.translate(allSteps2D, PixelOrientation.CENTER,PixelOrientation.UPPER_LEFT,0,1);
//            try{
//                return resampleUsingJava(sourceCoverage, sourceImage, interpolationType,
//                                   allSteps2D, targetImage, targetGG, finalView, hints);
//            }catch(ImagingOpException ex){
//                LOGGER.log(Level.WARNING, "Resampling process : Failed to use java affine resampling.");
//            }
//        }

//        if(!(targetToSource instanceof AffineTransform)){
//            //try to use a grid transform to improve performances
//            try{
//                final TransformGrid tr = TransformGrid.create(
//                        (MathTransform2D)targetToSource,
//                        new Rectangle(targetImage.getWidth(),targetImage.getHeight()) );
//                if(tr.globalTransform!=null){
//                    //we can completly replace it by an affine transform
//                    targetToSource = new AffineTransform2D(tr.globalTransform);
//                    targetToSource = PixelTranslation.translate(targetToSource, PixelOrientation.UPPER_LEFT,PixelOrientation.UPPER_LEFT,0,1);
//
//                    // we should be able to use Java affine op here, but this produces plenty of artifact
//                    // since the transform is approximative, artifact = white pixels on tile borders
//                    //if(canUseJavaInterpolation(sourceImage, targetToSource, interpolationType)){
//                    //    MathTransform inv = targetToSource.inverse();
//                    //    return resampleUsingJava(sourceCoverage, sourceImage, interpolationType,
//                    //                       inv, targetImage, targetGG, finalView, hints);
//                    //}
//                }else{
//                    targetToSource = new GridMathTransform(tr);
//                }
//            }catch(ArithmeticException ex){
//                //could not be optimized
//                LOGGER.log(Level.FINE, ex.getMessage());
//            }
//        }

            final Resample resample = new Resample(targetToSource, targetImage, sourceImage,
                    interpolationType, borderComportement, fillValue);
            resample.fillImage(builder.isSameCrs());
        }

        return geophysicRequired?
                new NoConversionCoverage(builder.target, outputSampleDims, targetImage) :
                new PackedCoverage(builder.target, outputSampleDims, targetImage);
    }

    private static double[] getFillValue(GridCoverage source) {
        return source.getSampleDimensions().stream()
                .mapToDouble(sd -> getFillValue(sd, Double.NaN))
                .toArray();
    }

    private static double getFillValue(final SampleDimension sd, final double defaultValue) {
        final Optional<Number> bg = sd.getBackground();
        if (bg.isPresent())
            return bg.get().doubleValue();
        final Set<Number> noDataValues = sd.getNoDataValues();
        if (noDataValues.isEmpty())
            return defaultValue;
        return noDataValues.iterator().next().doubleValue();
    }

    /**
     * Check if conditions are met to use java affine interpolation.
     *
     * @param sourceImage
     * @param trs
     * @param interpolation
     * @return
     */
    private static boolean canUseJavaInterpolation(RenderedImage sourceImage,
            MathTransform trs, InterpolationCase interpolation){
        final int datatype = sourceImage.getSampleModel().getDataType();;
        if(!(datatype == DataBuffer.TYPE_BYTE || datatype == DataBuffer.TYPE_INT)){
            return false;
        }
        return interpolation != InterpolationCase.LANCZOS && trs instanceof AffineTransform &&
              ((sourceImage instanceof BufferedImage) || (sourceImage.getNumXTiles()==1 && sourceImage.getNumYTiles()==1));
    }

    /**
     * Find the Java interpolation equivalent value.
     *
     * @return RenderingHints or null if not found
     */
    private static Object fingJavaInterpolationHint(final InterpolationCase interpolationType){
        switch(interpolationType){
            case NEIGHBOR : return RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR;
            case BILINEAR : return RenderingHints.VALUE_INTERPOLATION_BILINEAR;
            case BICUBIC : return RenderingHints.VALUE_INTERPOLATION_BICUBIC;
            case BICUBIC2 : return RenderingHints.VALUE_INTERPOLATION_BICUBIC;
            default: return null;
        }
    }

    // TODO: try to re-activate, it would allow optimize a few cases.
    private static void resampleUsingJava(
            RenderedImage sourceImage,InterpolationCase interpolationType, AffineTransform trs,
            BufferedImage targetImage){

        final Object javaInterHint = fingJavaInterpolationHint(interpolationType);
        final RenderingHints ophints;
        if (javaInterHint != null) ophints = new RenderingHints(RenderingHints.KEY_INTERPOLATION, javaInterHint);
        else ophints = null;
        if(sourceImage instanceof BufferedImage){
            final AffineTransformOp op = new AffineTransformOp(trs, ophints);
            op.filter((BufferedImage)sourceImage, targetImage);
        }else{
            //only one tile
            final AffineTransformOp op = new AffineTransformOp(trs, ophints);
            // TODO : shouldn't we deduce a set of tiles to filter on ?
            op.filter(sourceImage.getTile(0, 0), targetImage.getRaster());
        }
    }

    /**
     * Returns a source CRS compatible with the given target CRS. This method try to returns
     * a CRS which would not thrown an {@link NoninvertibleTransformException} if attempting
     * to transform from "target" to "source" (reminder: Warp works on <strong>inverse</strong>
     * transforms).
     *
     * @param sourceCRS2D
     *          The two-dimensional source CRS. Actually, this method accepts arbitrary dimension
     *          provided that are not greater than {@code sourceCRS}, but in theory it is 2D.
     * @param sourceCRS
     *          The n-dimensional source CRS.
     * @param targetCRS
     *          The n-dimensional target CRS.
     */
    private static CoordinateReferenceSystem compatibleSourceCRS(
             final CoordinateReferenceSystem sourceCRS2D,
             final CoordinateReferenceSystem sourceCRS,
             final CoordinateReferenceSystem targetCRS)
    {
        final int dim2D = sourceCRS2D.getCoordinateSystem().getDimension();
        return (targetCRS.getCoordinateSystem().getDimension() == dim2D &&
                sourceCRS.getCoordinateSystem().getDimension()  > dim2D) ? sourceCRS2D : sourceCRS;
    }

    /**
     * Test given coverage sample dimension to determine if pixel interpolation requires geophysic values, or can be
     * processed upon packed values (which can be more efficient). The only cases where packed values are sufficient are
     * when no fill value exists, and transfer function is fully linear.
     *
     * @param source Coverage to check.
     * @return True if resample HAVE TO USE geophysic values. False otherwise.
     */
    private static boolean isGeophysicRequired(final List<SampleDimension> source) {
        for (SampleDimension sd : source) {
            // If there's missing data in the image, we cannot interpolate packed values, as it would possibly mix
            // an aberrant value (Ex: -32767 in a short packed image) with valid packed data.
            if (!sd.getNoDataValues().isEmpty())
                return true;

            final boolean isNotLinear = sd.getTransferFunction()
                    .map(tr -> !(tr instanceof LinearTransform))
                    .isPresent();
            if (isNotLinear)
                return true;
        }

        // If we get here, all sample dimensions have got a linear transfer function, geophysic interpolation is not required.
        return false;
    }

    private static class NoConversionCoverage extends GridCoverage {

        final RenderedImage buffer;
        /**
         * Constructs a grid coverage using the specified grid geometry and sample dimensions.
         *  @param grid  the grid extent, CRS and conversion from cell indices to CRS.
         * @param bands sample dimensions for each image band.
         * @param buffer
         */
        protected NoConversionCoverage(GridGeometry grid, Collection<? extends SampleDimension> bands, RenderedImage buffer) {
            super(grid, bands);
            this.buffer = buffer;
        }

        @Override
        public synchronized  GridCoverage forConvertedValues(boolean converted) {
            return this;
        }

        @Override
        public RenderedImage render(GridExtent sliceExtent) throws CannotEvaluateException {
            if (sliceExtent == null || sliceExtent.equals(getGridGeometry().getExtent()))
                return buffer;
            if (buffer instanceof BufferedImage) {
                final BufferedImage img = (BufferedImage) buffer;
                return CoverageUtilities.subgrid(img, sliceExtent);
            } else {
                throw new UnsupportedOperationException("TODO: generic case for cropped view.");
            }
        }
    }

    private static class PackedCoverage extends NoConversionCoverage {

        /**
         * Result of the call to {@link #forConvertedValues(boolean)}, created when first needed.
         */
        private GridCoverage converted;

        /**
         * Constructs a grid coverage using the specified grid geometry and sample dimensions.
         *
         * @param grid   the grid extent, CRS and conversion from cell indices to CRS.
         * @param bands  sample dimensions for each image band.
         * @param buffer
         */
        protected PackedCoverage(GridGeometry grid, Collection<? extends SampleDimension> bands, RenderedImage buffer) {
            super(grid, bands, buffer);
        }

        @Override
        public synchronized  GridCoverage forConvertedValues(boolean converted) {
            if (converted) {
                synchronized (this) {
                    if (this.converted == null) {
                        this.converted = convert(this);
                    }
                    return this.converted;
                }
            }
            return this;
        }
    }
}
