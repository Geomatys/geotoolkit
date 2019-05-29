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
import org.apache.sis.geometry.GeneralEnvelope;
import org.apache.sis.image.WritablePixelIterator;
import org.apache.sis.internal.coverage.BufferedGridCoverage;
import org.apache.sis.internal.metadata.AxisDirections;
import org.apache.sis.internal.raster.RasterFactory;
import org.apache.sis.internal.system.DefaultFactories;
import org.apache.sis.metadata.iso.extent.DefaultGeographicBoundingBox;
import org.apache.sis.parameter.Parameters;
import org.apache.sis.referencing.CommonCRS;
import org.apache.sis.referencing.operation.transform.LinearTransform;
import org.apache.sis.referencing.operation.transform.MathTransforms;
import org.apache.sis.referencing.operation.transform.TransformSeparator;
import org.apache.sis.util.ArgumentChecks;
import org.apache.sis.util.Utilities;
import org.apache.sis.util.logging.Logging;
import org.geotoolkit.coverage.grid.GridGeometry2D;
import org.geotoolkit.factory.Hints;
import org.geotoolkit.image.BufferedImages;
import org.geotoolkit.image.interpolation.InterpolationCase;
import org.geotoolkit.image.interpolation.Resample;
import org.geotoolkit.image.interpolation.ResampleBorderComportement;
import org.geotoolkit.internal.coverage.CoverageUtilities;
import org.geotoolkit.process.ProcessException;
import org.geotoolkit.processing.AbstractProcess;

import static org.apache.sis.util.ArgumentChecks.ensureNonNull;
import static org.geotoolkit.processing.coverage.resample.ResampleDescriptor.*;

import org.apache.sis.referencing.CRS;
import org.geotoolkit.resources.Errors;
import org.opengis.geometry.Envelope;
import org.opengis.metadata.extent.GeographicBoundingBox;
import org.opengis.parameter.ParameterValueGroup;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.crs.SingleCRS;
import org.opengis.referencing.datum.PixelInCell;
import org.opengis.referencing.operation.*;
import org.opengis.util.FactoryException;

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
     * grid geometry is supplied, only its {@linkplain GridGeometry2D#getRange grid envelope}
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
        final GridGeometry sourceGG = sourceCov.getGridGeometry();

        //set default values

        if (interpolationType == null) {
            interpolationType = InterpolationCase.NEIGHBOR;
        }
        if(borderComportement==null) borderComportement = ResampleBorderComportement.EXTRAPOLATION;

        ////////////////////////////////////////////////////////////////////////////////////////
        ////                                                                                ////
        //// =======>>  STEP 1: Extracts needed informations from the parameters   <<====== ////
        ////            STEP 2: Creates the "target to source" MathTransform                ////
        ////            STEP 3: Computes the target image layout                            ////
        ////            STEP 4: Applies the transform operation ("Affine","Warp")           ////
        ////                                                                                ////
        ////////////////////////////////////////////////////////////////////////////////////////

        final CoordinateReferenceSystem sourceCRS = sourceGG.getCoordinateReferenceSystem();
        if (targetCRS == null) {
            if (targetGG != null && targetGG.isDefined(GridGeometry.CRS))
                targetCRS = targetGG.getCoordinateReferenceSystem();
            else targetCRS = sourceCRS;
            // From this point, consider "targetCRS" as final.
        }
        /*
         * The following will tell us if the target GridRange (GR) and GridGeometry (GG) should
         * be computed automatically, or if we should follow strictly what the user said. Note
         * that "automaticGG" implies "automaticGR" but the converse is not necessarily true.
         */
        final boolean automaticGG, automaticGR;
        /*
         * Grid envelope and "grid to CRS" transform are the only grid geometry informations used
         * by this method. If they are not available, this is equivalent to not providing grid
         * geometry at all. In such case set to 'targetGG' reference to null, since null value
         * is what the remaining code will check for.
         */
        if (targetGG == null) {
            automaticGG = true;
            automaticGR = true;
        } else {
            automaticGR = !targetGG.isDefined(GridGeometry.EXTENT);
            if (!automaticGR || targetGG.isDefined(GridGeometry.GRID_TO_CRS)) {
                automaticGG = false;
            } else {
                /*
                 * Before to abandon the grid geometry, checks if it contains an envelope (note:
                 * we really want it in sourceCRS, not targetCRS - the reprojection will be done
                 * later in this method). If so, we will recreate a new grid geometry from the
                 * envelope using the same "grid to CRS" transform than the original coverage.
                 * The result may be an image with a different size.
                 */
                if (targetGG.isDefined(GridGeometry.ENVELOPE)) {
                    final Envelope       envelope = targetGG.getEnvelope();
                    final MathTransform  gridToCRS = sourceGG.getGridToCRS(PixelInCell.CELL_CENTER);
                    targetGG = new GridGeometry(PixelInCell.CELL_CENTER, gridToCRS, envelope, GridRoundingMode.ENCLOSING);
                    automaticGG = false;
                } else {
                    targetGG = null;
                    automaticGG = true;
                }
            }
        }

        // TODO : Is it really necessary ? Is it the best strategy ?
        final CoordinateReferenceSystem compatibleSourceCRS = compatibleSourceCRS(
                CRS.getHorizontalComponent(sourceCRS), sourceCRS, targetCRS);
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
        final boolean geophysicRequired = !interpolationIsNearestNeighbor || isGeophysicRequired(sds);
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

        final MathTransformFactory mtFactory = DefaultFactories.forBuildin(MathTransformFactory.class);
        /*
         * Computes the INVERSE of the math transform from [Source Grid] to [Target Grid].
         * The transform will be computed using the following path:
         *
         *      Target Grid --> Target CRS --> Source CRS --> Source Grid
         *                   ^              ^              ^
         *                 step 1         step 2         step 3
         *
         * If source and target CRS are equal, a shorter path is used. This special
         * case is needed because 'sourceCRS' and 'targetCRS' may be null.
         *
         *      Target Grid --> Common CRS --> Source Grid
         *                   ^              ^
         *                 step 1         step 3
         */
        MathTransform allSteps;
        MathTransform2D allSteps2D;
        final MathTransform step1, step2, step3;
        final boolean canUseGrid;
        if (Utilities.equalsIgnoreMetadata(sourceCRS, targetCRS)) {
            /*
             * Note: targetGG should not be null, otherwise 'existingCoverage(...)' should
             *       have already detected that this resample is not doing anything.
             */
            canUseGrid = true;
            if (!targetGG.isDefined(GridGeometry.GRID_TO_CRS)) {
                step1    = sourceGG.getGridToCRS(PixelInCell.CELL_CENTER); // Really sourceGG, not targetGG
                step2    = MathTransforms.identity(step1.getTargetDimensions());
                step3    = step1.inverse();
                allSteps = MathTransforms.identity(step1.getSourceDimensions());
                targetGG = new GridGeometry(targetGG.getExtent(), PixelInCell.CELL_CENTER, step1, targetCRS);
            } else {
                step1    = targetGG.getGridToCRS(PixelInCell.CELL_CENTER);
                step2    = MathTransforms.identity(step1.getTargetDimensions());
                step3    = sourceGG.getGridToCRS(PixelInCell.CELL_CENTER).inverse();
                allSteps = mtFactory.createConcatenatedTransform(step1, step3);
                if (!targetGG.isDefined(GridGeometry.EXTENT)) {
                    /*
                     * If the target grid envelope was not explicitly specified, a grid envelope
                     * will be automatically computed in such a way that it will maps to the same
                     * georeferenced envelope (at least approximatively).
                     *
                     * TODO: verify this with Martin. It's very suspicious.
                     */
                    Envelope gridEnvelope;
                    gridEnvelope = toEnvelope(sourceGG.getExtent());
                    gridEnvelope = Envelopes.transform(allSteps.inverse(), gridEnvelope);
                    targetGG = new org.apache.sis.coverage.grid.GridGeometry(
                            PixelInCell.CELL_CORNER, MathTransforms.identity(gridEnvelope.getDimension()),
                            gridEnvelope, GridRoundingMode.ENCLOSING);
                }
            }
        } else {
            if (sourceCRS == null) {
                throw new CannotReprojectException(Errors.format(Errors.Keys.UnspecifiedCrs));
            }
            final Envelope        sourceEnvelope;
            final GeographicBoundingBox roi = tryGetRoi(targetGG, sourceGG);
            final CoordinateOperation operation = CRS.findOperation(sourceCRS, targetCRS, roi);
            final CoordinateOperation inverseOp = CRS.findOperation(targetCRS, compatibleSourceCRS, roi);
            final boolean force2D = (sourceCRS != compatibleSourceCRS);
            final GridGeometry sourceGGCopy;
            if (force2D) {
                final int xAxis = AxisDirections.indexOfColinear(sourceCRS.getCoordinateSystem(), compatibleSourceCRS.getCoordinateSystem());
                sourceGGCopy = sourceGG.reduce(xAxis, xAxis+1);
            } else sourceGGCopy = sourceGG;

            step2          = WraparoundTransform.create(mtFactory, inverseOp);
            step3          = sourceGGCopy.getGridToCRS(PixelInCell.CELL_CENTER).inverse();
            canUseGrid     = step2 == inverseOp.getMathTransform();
            sourceEnvelope = sourceGG.getEnvelope(); // Don't force this one to 2D.
            // 'targetCRS' may be different than the one set by Envelopes.transform(...).
            /*
             * If the target GridGeometry is incomplete, provides default
             * values for the missing fields. Three cases may occurs:
             *
             * - User provided no GridGeometry at all. Then, constructs an image of the same size
             *   than the source image and set an envelope big enough to contains the projected
             *   coordinates. The transform will derive from the grid and georeferenced envelopes.
             *
             * - User provided only a grid envelope. Then, set an envelope big enough to contains
             *   the projected coordinates. The transform will derive from the grid and georeferenced
             *   envelopes.
             *
             * - User provided only a "grid to CRS" transform. Then, transform the projected
             *   envelope to "grid units" using the specified transform and create a grid envelope
             *   big enough to hold the result.
             */
            if (targetGG == null || !targetGG.isDefined(GridGeometry.GRID_TO_CRS)) {
                final GeneralEnvelope targetEnvelope = Envelopes.transform(operation, sourceEnvelope);
                targetEnvelope.setCoordinateReferenceSystem(targetCRS);
                final MathTransform targetGrid2Crs = MathTransforms.concatenate(
                        sourceGGCopy.getGridToCRS(PixelInCell.CELL_CENTER),
                        operation.getMathTransform()
                );
                targetGG = new GridGeometry(sourceGGCopy.getExtent(), PixelInCell.CELL_CENTER, targetGrid2Crs, targetCRS);
            } else if (!targetGG.isDefined(GridGeometry.EXTENT)) {
                    final GeneralEnvelope targetEnvelope = Envelopes.transform(operation, sourceEnvelope);
                    targetEnvelope.setCoordinateReferenceSystem(targetCRS);
                    // TODO: check rounding mode
                    targetGG = new GridGeometry(PixelInCell.CELL_CENTER, targetGG.getGridToCRS(PixelInCell.CELL_CENTER), targetEnvelope, GridRoundingMode.ENCLOSING);
            }
            /*
             * Computes the final transform.
             */
            step1    = targetGG.getGridToCRS(PixelInCell.CELL_CENTER);
            allSteps = mtFactory.createConcatenatedTransform(
                       mtFactory.createConcatenatedTransform(step1, step2), step3);
        }

        final Point sourceGridAxes = getImageAxis(sourceGG);
        final Point targetGridAxes = getImageAxis(targetGG);

        allSteps2D = toMathTransform2D(allSteps, mtFactory, new int[]{targetGridAxes.x, targetGridAxes.y}, new int[]{sourceGridAxes.x, sourceGridAxes.y});

        ////////////////////////////////////////////////////////////////////////////////////////
        ////                                                                                ////
        ////            STEP 1: Extracts needed informations from the parameters            ////
        ////            STEP 2: Creates the "target to source" MathTransform                ////
        //// =======>>  STEP 3: Computes the target image layout                   <<====== ////
        ////            STEP 4: Applies the transform operation ("Affine","Warp")           ////
        ////                                                                                ////
        ////////////////////////////////////////////////////////////////////////////////////////

        RenderedImage sourceImage = sourceCov.render(null); // TODO : We should check if only a subset of source image is required
        final int sourceDataType = sourceImage.getTile(0, 0).getDataBuffer().getDataType();
        final GridExtent targetBB = targetGG.getExtent();

        final List<SampleDimension> outputSampleDims = geophysicRequired?
                sds.stream()
                        .map(sd -> sd.forConvertedValues(true))
                        .collect(Collectors.toList())
                : sds;
        final AccessibleBufferedCoverage targetCov = new AccessibleBufferedCoverage(targetGG, outputSampleDims, sourceDataType);
        final int targetWidth = Math.toIntExact(targetBB.getSize(targetGridAxes.x));
        final WritableRaster targetRaster = RasterFactory.createRaster(
                targetCov.getBuffer(),
                targetWidth,
                Math.toIntExact(targetBB.getSize(targetGridAxes.y)),
                nBands,
                Math.multiplyExact(nBands, targetWidth),
                new int[]{0},
                IntStream.range(0, nBands).toArray(),
                new Point(0, 0)
        );

        // TODO: verify, I don't know what I'm doing.
        final ColorModel tmpCM = BufferedImages.createGrayScaleColorModel(sourceDataType, nBands, 0, 0, 1);
        final BufferedImage targetImage = new BufferedImage(tmpCM, targetRaster, false, null);
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

        if (allSteps2D.isIdentity()) {
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
//        if(canUseJavaInterpolation(sourceImage, allSteps2D, interpolationType)){
//            allSteps2D = PixelTranslation.translate(allSteps2D, PixelOrientation.CENTER,PixelOrientation.UPPER_LEFT,0,1);
//            try{
//                return resampleUsingJava(sourceCoverage, sourceImage, interpolationType,
//                                   allSteps2D, targetImage, targetGG, finalView, hints);
//            }catch(ImagingOpException ex){
//                LOGGER.log(Level.WARNING, "Resampling process : Failed to use java affine resampling.");
//            }
//        }

            MathTransform targetToSource = allSteps2D;
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
            resample.fillImage(canUseGrid);
        }

        return targetCov;
    }

    /**
     * Build a geographic extent by analyzing input geometries. It is a pseudo-safe operation returning null if no area
     * of interest can be deduced, in order to avoid failure of entire resample.
     * @param target Primary grid geometry to extract geographic bbox from.
     * @param fallback If we cannot deduce a bbox from first parameter, we'll try to get one from this one.
     * @return null if we cannot compute any geographic extent.
     */
    private static GeographicBoundingBox tryGetRoi(GridGeometry target, GridGeometry fallback) {
        GeographicBoundingBox geoBbox = tryExtractGeoBbox(target);
        if (geoBbox == null)
            geoBbox = tryExtractGeoBbox(fallback);

        return geoBbox;
    }

    private static GeographicBoundingBox tryExtractGeoBbox(GridGeometry target) {
        try {
            if (target != null && target.isDefined(GridGeometry.ENVELOPE)) {
                return toGeoBbox(target.getEnvelope());
            }
        } catch (RuntimeException | TransformException e) {
            // TODO: log or raise warning
        }

        return null;
    }

    private static GeographicBoundingBox toGeoBbox(Envelope baseEnvelope) throws TransformException {
        final Envelope geoEnv = Envelopes.transform(baseEnvelope, CommonCRS.defaultGeographic());
        return new DefaultGeographicBoundingBox(
                geoEnv.getMinimum(0), geoEnv.getMaximum(0),
                geoEnv.getMinimum(1), geoEnv.getMaximum(1)
        );
    }

    private static Point getImageAxis(GridGeometry sourceGG) {
        if (sourceGG.isDefined(GridGeometry.EXTENT)) {
            final GridExtent extent = sourceGG.getExtent();
            final int[] dims = IntStream.range(0, sourceGG.getDimension())
                    .filter(i -> extent.getSize(i) > 1)
                    .limit(2)
                    .toArray();
            if (dims.length == 2) {
                return new Point(dims[0], dims[1]);
            }
        }

        // TODO: better strategy:
        // Cannot safely deduce grid dimensions. Check horizontal axis in source geometry. It's not very safe, cause
        // we don't care about possible axis shifts in grid to CRS transform.
        if (sourceGG.isDefined(GridGeometry.CRS)) {
            final CoordinateReferenceSystem crs = sourceGG.getCoordinateReferenceSystem();
            final SingleCRS hCrs = CRS.getHorizontalComponent(crs);
            if (hCrs != null) {
                final int xAxis = AxisDirections.indexOfColinear(crs.getCoordinateSystem(), hCrs.getCoordinateSystem());
                if (xAxis >= 0) return new Point(xAxis, xAxis+1);
            }
        }

        // TODO: Warn user
        return new Point(0, 1);
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
     * Returns the math transform for the two specified dimensions of the specified transform.
     *
     * @param  transform The transform.
     * @param  mtFactory The factory to use for extracting the sub-transform.
     * @param  sourceGG  The grid geometry which is the source of the <strong>transform</strong>.
     *                   This is {@code targetGG} in the {@link #reproject} method, because the
     *                   later computes a transform from target to source grid geometry.
     * @return The {@link MathTransform2D} part of {@code transform}.
     * @throws FactoryException If {@code transform} is not separable.
     */
    private static MathTransform2D toMathTransform2D(final MathTransform        transform,
                                                     final MathTransformFactory mtFactory,
                                                     final int[]                sourceDims,
                                                     final int[]                targetDims)
            throws FactoryException
    {
        final TransformSeparator filter = new TransformSeparator(transform, mtFactory);
        filter.addSourceDimensions(sourceDims);
        filter.addTargetDimensions(targetDims);
        MathTransform candidate = filter.separate();
        if (candidate instanceof MathTransform2D) {
            return (MathTransform2D) candidate;
        }
//        filter.addTargetDimensions(sourceGG.axisDimensionX,
//                                   sourceGG.axisDimensionY);
//        candidate = filter.separate();
//        if (candidate instanceof MathTransform2D) {
//            return (MathTransform2D) candidate;
//        }
        throw new FactoryException(Errors.format(Errors.Keys.NoTransform2dAvailable));
    }

    /**
     * Casts the specified grid envelope into a georeferenced envelope. This is used before to
     * transform the envelope using {@link CRSUtilities#transform(MathTransform, Envelope)}.
     */
    private static Envelope toEnvelope(final GridExtent gridEnvelope) {
        final int dimension = gridEnvelope.getDimension();
        final double[] lower = new double[dimension];
        final double[] upper = new double[dimension];
        for (int i=0; i<dimension; i++) {
            lower[i] = gridEnvelope.getLow(i);
            upper[i] = gridEnvelope.getHigh(i) + 1;
        }
        return new GeneralEnvelope(lower, upper);
    }

    /**
     * Test given coverage sample dimension to determine if pixel interpolation requires geophysic values, or can be
     * processed upon packed values (which can be more efficient). Note that if any incertitude is left (missing sample
     * dimension or transfer functions), we force geophysic mode for safety.
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

            final boolean isLinear = sd.getTransferFunction()
                    .map(LinearTransform.class::isInstance)
                    .isPresent();
            if (!isLinear)
                return true;
        }

        // If we get here, all sample dimensions have got a linear transfer function, geophysic interpolation is not required.
        return false;
    }

    /**
     * Override of {@link BufferedGridCoverage Apache SIS in-memory coverage} which allow access to its internal buffer.
     */
    private static class AccessibleBufferedCoverage extends BufferedGridCoverage {

        public AccessibleBufferedCoverage(GridGeometry grid, Collection<? extends SampleDimension> bands, int dataType) {
            super(grid, bands, dataType);
        }

        private DataBuffer getBuffer() {
            return data;
        }
    }

    private static GridGeometry fixGrid(final GridGeometry currentTarget, final CoordinateReferenceSystem targetCrs) {
        if (currentTarget == null) {

        } else {

        }

        throw new UnsupportedOperationException();
    }

    private static class ResampleContext {
        final GridGeometry source;
        final CoordinateReferenceSystem targetCrs;

        public ResampleContext(GridGeometry source, CoordinateReferenceSystem targetCrs) {
            ensureNonNull("Source geometry", source);
            ensureNonNull("Target CRS", targetCrs);
            this.source = source;
            this.targetCrs = targetCrs;
        }
    }
}
