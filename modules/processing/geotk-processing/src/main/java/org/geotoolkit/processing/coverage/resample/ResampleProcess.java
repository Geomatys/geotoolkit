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

import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.awt.image.DataBuffer;
import java.awt.image.IndexColorModel;
import java.awt.image.RenderedImage;
import java.awt.image.WritableRaster;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.logging.LogRecord;
import java.util.logging.Logger;
import org.apache.sis.geometry.GeneralEnvelope;
import org.apache.sis.util.logging.Logging;
import org.geotoolkit.coverage.GridSampleDimension;
import org.geotoolkit.coverage.grid.GeneralGridEnvelope;
import org.geotoolkit.coverage.grid.GridCoverage2D;
import org.geotoolkit.coverage.grid.GridGeometry2D;
import org.geotoolkit.coverage.grid.ViewType;
import org.geotoolkit.coverage.processing.AbstractCoverageProcessor;
import org.geotoolkit.coverage.processing.CannotReprojectException;
import org.geotoolkit.factory.FactoryFinder;
import org.geotoolkit.factory.Hints;
import org.apache.sis.geometry.Envelopes;
import org.geotoolkit.image.interpolation.Interpolation;
import org.geotoolkit.image.interpolation.InterpolationCase;
import org.geotoolkit.image.interpolation.Resample;
import org.geotoolkit.internal.coverage.CoverageUtilities;
import static org.geotoolkit.internal.coverage.CoverageUtilities.hasRenderingCategories;
import org.geotoolkit.parameter.Parameters;
import org.geotoolkit.utility.parameter.ParametersExt;
import org.geotoolkit.processing.AbstractProcess;
import org.geotoolkit.process.ProcessException;

import static org.geotoolkit.processing.coverage.resample.ResampleDescriptor.*;

import org.geotoolkit.referencing.CRS;
import org.geotoolkit.referencing.operation.AbstractCoordinateOperationFactory;
import org.apache.sis.referencing.operation.transform.MathTransforms;
import org.geotoolkit.image.interpolation.ResampleBorderComportement;
import org.geotoolkit.referencing.operation.transform.DimensionFilter;
import org.geotoolkit.resources.Errors;
import org.geotoolkit.image.BufferedImages;
import org.opengis.coverage.grid.GridCoverage;
import org.opengis.coverage.grid.GridEnvelope;
import org.opengis.coverage.grid.GridGeometry;
import org.opengis.geometry.Envelope;
import org.opengis.metadata.spatial.PixelOrientation;
import org.opengis.parameter.ParameterValueGroup;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.datum.PixelInCell;
import org.opengis.referencing.operation.CoordinateOperation;
import org.opengis.referencing.operation.CoordinateOperationFactory;
import org.opengis.referencing.operation.MathTransform;
import org.opengis.referencing.operation.MathTransform2D;
import org.opengis.referencing.operation.MathTransformFactory;
import org.opengis.referencing.operation.TransformException;
import org.opengis.util.FactoryException;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public class ResampleProcess extends AbstractProcess {

    private static final Logger LOGGER = Logging.getLogger("org.geotoolkit.processing.coverage.resample");

    /**
     * The corner to use for performing calculation. By default {@link GridGeometry#getGridToCRS()}
     * maps to pixel center (as of OGC specification). In JAI, the transforms rather map to the
     * upper left corner.
     */
    private static final PixelOrientation CORNER = PixelOrientation.UPPER_LEFT;


    public ResampleProcess(GridCoverage2D coverage, CoordinateReferenceSystem targetCrs, double[] background) {
        super(INSTANCE, asParameters(coverage, targetCrs,  null, null, background));
    }

    public ResampleProcess(GridCoverage2D coverage, CoordinateReferenceSystem targetCrs,
                           GridGeometry gridGeom, InterpolationCase interpolation, double[] background) {
        super(INSTANCE, asParameters(coverage, targetCrs, gridGeom, interpolation, background));
    }

    public ResampleProcess(final ParameterValueGroup input) {
        super(INSTANCE, input);
    }

    private static ParameterValueGroup asParameters(GridCoverage2D coverage, CoordinateReferenceSystem targetCrs,
            GridGeometry gridGeom, InterpolationCase interpolation, double[] background){
        final ParameterValueGroup params = ResampleDescriptor.INPUT_DESC.createValue();
        ParametersExt.getOrCreateValue(params, IN_COVERAGE.getName().getCode()).setValue(coverage);
        if(targetCrs!=null){
            ParametersExt.getOrCreateValue(params, IN_COORDINATE_REFERENCE_SYSTEM.getName().getCode()).setValue(targetCrs);
        }
        if(gridGeom!=null){
            ParametersExt.getOrCreateValue(params, IN_GRID_GEOMETRY.getName().getCode()).setValue(gridGeom);
        }
        if(background!=null){
            ParametersExt.getOrCreateValue(params, IN_BACKGROUND.getName().getCode()).setValue(background);
        }
        if(interpolation!=null){
            ParametersExt.getOrCreateValue(params, IN_INTERPOLATION_TYPE.getName().getCode()).setValue(interpolation);
        }
        return params;
    }

    public GridCoverage2D executeNow() throws ProcessException {
        execute();
        return (GridCoverage2D) outputParameters.parameter(OUT_COVERAGE.getName().getCode()).getValue();
    }

    /**
     * Resamples a grid coverage.
     */
    @Override
    protected void execute() throws ProcessException {


        final GridCoverage2D source = (GridCoverage2D) Parameters.getOrCreate(IN_COVERAGE, inputParameters).getValue();
        final double[] background = (double[]) Parameters.getOrCreate(IN_BACKGROUND, inputParameters).getValue();
        InterpolationCase interpolation = (InterpolationCase) Parameters.getOrCreate(IN_INTERPOLATION_TYPE, inputParameters).getValue();
        if(interpolation == null){
            interpolation = InterpolationCase.NEIGHBOR;
        }
        CoordinateReferenceSystem targetCRS = (CoordinateReferenceSystem) inputParameters.parameter("CoordinateReferenceSystem").getValue();
        if (targetCRS == null) {
            targetCRS = source.getCoordinateReferenceSystem();
        }
        final GridGeometry2D targetGG = GridGeometry2D.castOrCopy(
                (GridGeometry) inputParameters.parameter("GridGeometry").getValue());
        final GridCoverage2D target;

        try {
            target = reproject(source, targetCRS, targetGG, interpolation, background, null);
        } catch (FactoryException exception) {
            throw new CannotReprojectException(Errors.format(
                    Errors.Keys.CantReprojectCoverage_1, source.getName()), exception);
        } catch (TransformException exception) {
            throw new CannotReprojectException(Errors.format(
                    Errors.Keys.CantReprojectCoverage_1, source.getName()), exception);
        }

        Parameters.getOrCreate(OUT_COVERAGE, outputParameters).setValue(target);
    }

    /**
     * Constructs a new grid coverage for the specified grid geometry.
     *
     * @param source    The source for this grid coverage.
     * @param image     The image.
     * @param geometry  The grid geometry (including the new CRS).
     * @param finalView The view for the target coverage.
     */
    private static GridCoverage2D create(final GridCoverage2D source,
                                         final BufferedImage  image,
                                         final GridGeometry2D geometry,
                                         final ViewType       finalView,
                                         final Hints          hints)
    {
        final GridSampleDimension[] sampleDimensions;
        switch (finalView) {
            case PHOTOGRAPHIC: {
                sampleDimensions = null;
                break;
            }
            default: {
                sampleDimensions = source.getSampleDimensions();
                break;
            }
        }
        /*
         * The resampling may have been performed on the geophysics view.
         * Try to restore the original view.
         */
        GridCoverage2D coverage = new GridCoverage2D(source.getName(), image, geometry, sampleDimensions,
              new GridCoverage2D[] {source}, null, hints);
        coverage = coverage.view(finalView);
        return coverage;
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
    public static GridCoverage2D reproject(GridCoverage2D            sourceCoverage,
                                           CoordinateReferenceSystem targetCRS,
                                           GridGeometry2D            targetGG,
                                           InterpolationCase         interpolationType,
                                           double[]                  background,
                                           final Hints               hints)
            throws FactoryException, TransformException
    {
        ////////////////////////////////////////////////////////////////////////////////////////
        ////                                                                                ////
        //// =======>>  STEP 1: Extracts needed informations from the parameters   <<====== ////
        ////            STEP 2: Creates the "target to source" MathTransform                ////
        ////            STEP 3: Computes the target image layout                            ////
        ////            STEP 4: Applies the transform operation ("Affine","Warp")           ////
        ////                                                                                ////
        ////////////////////////////////////////////////////////////////////////////////////////

        final CoordinateReferenceSystem sourceCRS = sourceCoverage.getCoordinateReferenceSystem();
        if (targetCRS == null) {
            targetCRS = sourceCRS;
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
            automaticGR = !targetGG.isDefined(GridGeometry2D.EXTENT);
            if (!automaticGR || targetGG.isDefined(GridGeometry2D.GRID_TO_CRS)) {
                automaticGG = false;
            } else {
                /*
                 * Before to abandon the grid geometry, checks if it contains an envelope (note:
                 * we really want it in sourceCRS, not targetCRS - the reprojection will be done
                 * later in this method). If so, we will recreate a new grid geometry from the
                 * envelope using the same "grid to CRS" transform than the original coverage.
                 * The result may be an image with a different size.
                 */
                if (targetGG.isDefined(GridGeometry2D.ENVELOPE)) {
                    final Envelope       envelope = targetGG.getEnvelope();
                    final GridGeometry2D sourceGG = sourceCoverage.getGridGeometry();
                    final MathTransform  gridToCRS;
                    switch (envelope.getDimension()) {
                        case 2:  gridToCRS = sourceGG.getGridToCRS2D(CORNER); break;
                        default: gridToCRS = sourceGG.getGridToCRS(CORNER);   break;
                    }
                    targetGG = new GridGeometry2D(PixelInCell.CELL_CENTER, gridToCRS, envelope, null);
                    automaticGG = false;
                } else {
                    targetGG = null;
                    automaticGG = true;
                }
            }
        }

        final GridGeometry2D sourceGG = sourceCoverage.getGridGeometry();
        final CoordinateReferenceSystem compatibleSourceCRS = compatibleSourceCRS(
                sourceCoverage.getCoordinateReferenceSystem2D(), sourceCRS, targetCRS);
        /*
         * The projection are usually applied on floating-point values, in order
         * to gets maximal precision and to handle correctly the special case of
         * NaN values. However, we can apply the projection on integer values if
         * the interpolation type is "nearest neighbor", since this is not really
         * an interpolation.
         *
         * If this condition is meets, then we verify if an "integer version" of the image
         * is available as a source of the source coverage (i.e. the floating-point image
         * is derived from the integer image, not the converse).
         */
        final ViewType processingView = preferredViewForOperation(
                                        sourceCoverage, interpolationType, false, hints);
        final ViewType finalView = CoverageUtilities.preferredViewAfterOperation(sourceCoverage);
        sourceCoverage = sourceCoverage.view(processingView);
        RenderedImage sourceImage = sourceCoverage.getRenderedImage();
        assert sourceCoverage.getCoordinateReferenceSystem() == sourceCRS : sourceCoverage;
        // From this point, consider 'sourceCoverage' as final.

        ////////////////////////////////////////////////////////////////////////////////////////
        ////                                                                                ////
        ////            STEP 1: Extracts needed informations from the parameters            ////
        //// =======>>  STEP 2: Creates the "target to source" MathTransform       <<====== ////
        ////            STEP 3: Computes the target image layout                            ////
        ////            STEP 4: Applies the transform operation ("Affine","Warp")           ////
        ////                                                                                ////
        ////////////////////////////////////////////////////////////////////////////////////////

        final CoordinateOperationFactory factory = FactoryFinder.getCoordinateOperationFactory(hints);
        final MathTransformFactory mtFactory;
        if (factory instanceof AbstractCoordinateOperationFactory) {
            mtFactory = ((AbstractCoordinateOperationFactory) factory).getMathTransformFactory();
        } else {
            mtFactory = FactoryFinder.getMathTransformFactory(hints);
        }
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
        MathTransform allSteps, allSteps2D;
        final MathTransform step1, step2, step3 ;
        if (CRS.equalsIgnoreMetadata(sourceCRS, targetCRS)) {
            /*
             * Note: targetGG should not be null, otherwise 'existingCoverage(...)' should
             *       have already detected that this resample is not doing anything.
             */
            if (!targetGG.isDefined(GridGeometry2D.GRID_TO_CRS)) {
                step1    = sourceGG.getGridToCRS(PixelOrientation.CENTER); // Really sourceGG, not targetGG
                step2    = MathTransforms.identity(step1.getTargetDimensions());
                step3    = step1.inverse();
                allSteps = MathTransforms.identity(step1.getSourceDimensions());
                targetGG = new GridGeometry2D(targetGG.getExtent(), step1, targetCRS);
            } else {
                step1    = targetGG.getGridToCRS(PixelOrientation.CENTER);
                step2    = MathTransforms.identity(step1.getTargetDimensions());
                step3    = sourceGG.getGridToCRS(PixelOrientation.CENTER).inverse();
                allSteps = mtFactory.createConcatenatedTransform(step1, step3);
                if (!targetGG.isDefined(GridGeometry2D.EXTENT)) {
                    /*
                     * If the target grid envelope was not explicitly specified, a grid envelope
                     * will be automatically computed in such a way that it will maps to the same
                     * georeferenced envelope (at least approximatively).
                     */
                    Envelope gridEnvelope;
                    gridEnvelope = toEnvelope(sourceGG.getExtent());
                    gridEnvelope = Envelopes.transform(allSteps.inverse(), gridEnvelope);
                    targetGG  = new GridGeometry2D(new GeneralGridEnvelope(gridEnvelope,
                            PixelInCell.CELL_CORNER, false), step1, targetCRS);
                }
            }
        } else {
            if (sourceCRS == null) {
                throw new CannotReprojectException(Errors.format(Errors.Keys.UnspecifiedCrs));
            }
            final Envelope        sourceEnvelope;
            final GeneralEnvelope targetEnvelope;
            final CoordinateOperation operation = factory.createOperation(sourceCRS, targetCRS);
            final boolean force2D = (sourceCRS != compatibleSourceCRS);
            step2          = factory.createOperation(targetCRS, compatibleSourceCRS).getMathTransform();
            step3          = (force2D ? sourceGG.getGridToCRS2D(PixelOrientation.CENTER) : sourceGG.getGridToCRS(PixelOrientation.CENTER)).inverse();
            sourceEnvelope = sourceCoverage.getEnvelope(); // Don't force this one to 2D.
            targetEnvelope = Envelopes.transform(operation, sourceEnvelope);
            targetEnvelope.setCoordinateReferenceSystem(targetCRS);
            // 'targetCRS' may be different than the one set by CRS.transform(...).
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
            if (targetGG == null) {
                final GridEnvelope targetGR;
                targetGR = force2D ? new GeneralGridEnvelope(sourceGG.getExtent2D()) : sourceGG.getExtent();
                targetGG = new GridGeometry2D(targetGR, targetEnvelope);
                step1    = targetGG.getGridToCRS(PixelOrientation.CENTER);
            } else if (!targetGG.isDefined(GridGeometry2D.GRID_TO_CRS)) {
                targetGG = new GridGeometry2D(targetGG.getExtent(), targetEnvelope);
                step1    = targetGG.getGridToCRS(PixelOrientation.CENTER);
            } else {
                step1 = targetGG.getGridToCRS(PixelOrientation.CENTER);
                if (!targetGG.isDefined(GridGeometry2D.EXTENT)) {
                    final GeneralEnvelope gridEnvelope = Envelopes.transform(step1.inverse(), targetEnvelope);
                    // According OpenGIS specification, GridGeometry maps pixel's center.
                    targetGG = new GridGeometry2D(new GeneralGridEnvelope(gridEnvelope,
                            PixelInCell.CELL_CENTER, false), step1, targetCRS);
                }
            }
            /*
             * Computes the final transform.
             */
            allSteps = mtFactory.createConcatenatedTransform(
                       mtFactory.createConcatenatedTransform(step1, step2), step3);
        }
        allSteps2D = toMathTransform2D(allSteps, mtFactory, targetGG);
        if (!(allSteps2D instanceof MathTransform2D)) {
            // Should not happen with Geotk implementations. May happen
            // with some external implementations, but should stay unusual.
            throw new TransformException(Errors.format(Errors.Keys.NoTransform2dAvailable));
        }

        ////////////////////////////////////////////////////////////////////////////////////////
        ////                                                                                ////
        ////            STEP 1: Extracts needed informations from the parameters            ////
        ////            STEP 2: Creates the "target to source" MathTransform                ////
        //// =======>>  STEP 3: Computes the target image layout                   <<====== ////
        ////            STEP 4: Applies the transform operation ("Affine","Warp")           ////
        ////                                                                                ////
        ////////////////////////////////////////////////////////////////////////////////////////


        final Rectangle sourceBB = sourceGG.getExtent2D();
        final Rectangle targetBB = targetGG.getExtent2D();
        final BufferedImage targetImage = BufferedImages.createImage(targetBB.width,targetBB.height, sourceImage);
        final WritableRaster targetRaster = targetImage.getRaster();

        ////////////////////////////////////////////////////////////////////////////////////////
        ////                                                                                ////
        ////            STEP 1: Extracts needed informations from the parameters            ////
        ////            STEP 2: Creates the "target to source" MathTransform                ////
        ////            STEP 3: Computes the target image layout                            ////
        //// =======>>  STEP 4: Applies the transform operation ("Affine","Warp")  <<====== ////
        ////                                                                                ////
        ////////////////////////////////////////////////////////////////////////////////////////

        if(allSteps2D.isIdentity()){
            //we can directly copy raster to raster
            targetRaster.setDataElements(0, 0, sourceImage.getData());
            return create(sourceCoverage, targetImage, targetGG, finalView, hints);
        }

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

        final double[] fillValue = getFillValue(sourceCoverage);

//        final Interpolation interpolator = Interpolation.create(
//                PixelIteratorFactory.createDefaultIterator(sourceImage,sourceBB), interpolationType, 2);
         final Resample resample = new Resample(targetToSource, targetImage, sourceImage,
                interpolationType, ResampleBorderComportement.EXTRAPOLATION, fillValue);
        resample.fillImage();

        return create(sourceCoverage, targetImage, targetGG, finalView, hints);
    }

    private static double[] getFillValue(GridCoverage2D gridCoverage2D){
        final GridSampleDimension[] dimensions = gridCoverage2D.getSampleDimensions();
        final int nbBand = dimensions.length;
        final double[] fillValue = new double[nbBand];
        Arrays.fill(fillValue, Double.NaN);
        for(int i=0;i<nbBand;i++){
            final double[] nodata = dimensions[i].geophysics(true).getNoDataValues();
            if(nodata!=null && nodata.length>0){
                fillValue[i] = nodata[0];
            }
        }
        return fillValue;
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

    private static GridCoverage2D resampleUsingJava(GridCoverage2D sourceCoverage,
            RenderedImage sourceImage,InterpolationCase interpolationType, MathTransform trs,
            BufferedImage targetImage, GridGeometry2D targetGG, ViewType finalView, Hints hints){

        final Object javaInterHint = fingJavaInterpolationHint(interpolationType);
        final RenderingHints ophints = new RenderingHints(new HashMap());
        ophints.put(RenderingHints.KEY_INTERPOLATION, javaInterHint);
        if(sourceImage instanceof BufferedImage){
            final AffineTransformOp op = new AffineTransformOp((AffineTransform)trs, ophints);
            op.filter((BufferedImage)sourceImage, targetImage);
            return create(sourceCoverage, targetImage, targetGG, finalView, hints);
        }else{
            //only one tile
            final AffineTransformOp op = new AffineTransformOp((AffineTransform)trs, ophints);
            op.filter(sourceImage.getTile(0, 0), targetImage.getRaster());
            return create(sourceCoverage, targetImage, targetGG, finalView, hints);
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
                                                     final GridGeometry2D       sourceGG)
            throws FactoryException
    {
        final DimensionFilter filter = new DimensionFilter(mtFactory);
        filter.addSourceDimension(sourceGG.axisDimensionX);
        filter.addSourceDimension(sourceGG.axisDimensionY);
        MathTransform candidate = filter.separate(transform);
        if (candidate instanceof MathTransform2D) {
            return (MathTransform2D) candidate;
        }
        filter.addTargetDimension(sourceGG.axisDimensionX);
        filter.addTargetDimension(sourceGG.axisDimensionY);
        candidate = filter.separate(transform);
        if (candidate instanceof MathTransform2D) {
            return (MathTransform2D) candidate;
        }
        throw new FactoryException(Errors.format(Errors.Keys.NoTransform2dAvailable));
    }

    /**
     * Casts the specified grid envelope into a georeferenced envelope. This is used before to
     * transform the envelope using {@link CRSUtilities#transform(MathTransform, Envelope)}.
     */
    private static Envelope toEnvelope(final GridEnvelope gridEnvelope) {
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
     * Logs a message.
     */
    private static void log(final LogRecord record) {
        record.setSourceClassName("Resample");
        record.setSourceMethodName("doOperation");
        final Logger logger = AbstractCoverageProcessor.LOGGER;
        record.setLoggerName(logger.getName());
        logger.log(record);
    }

    /**
     * General purpose method used in various operations for {@link GridCoverage2D} to help
     * with taking decisions on how to treat coverages with respect to their {@link ColorModel}.
     * <p>
     * The need for this method arose in consideration of the fact that applying most operations
     * on coverage whose {@link ColorModel} is an instance of {@link IndexColorModel} may lead to
     * unpredictable results depending on the applied {@link Interpolation} (think about applying
     * "Scale" with {@link InterpolationBilinear} on a non-geophysics {@link GridCoverage2D} with an
     * {@link IndexColorModel}) or more simply on the operation itself ("SubsampleAverage" cannot
     * be applied at all on a {@link GridCoverage2D} backed by an {@link IndexColorModel}).
     * <p>
     * This method suggests the actions to take depending on the structure of the provided
     * {@link GridCoverage2D}, the provided {@link Interpolation} and if the operation uses
     * a filter or not (this is useful for operations like SubsampleAverage or FilteredSubsample).
     * <p>
     * In general the idea is as follows: If the original coverage is backed by a
     * {@link RenderedImage} with an {@link IndexColorModel}, we have the following cases:
     * <p>
     * <ul>
     *  <li>if the interpolation is {@link InterpolationNearest} and there is no filter involved
     *      we can apply the operation on the {@link IndexColorModel}-backed coverage with nor
     *      problems.</li>
     *  <li>If the interpolations in of higher order or there is a filter to apply we have to
     *      options:
     *      <ul>
     *        <li>If the coverage has a twin geophysics view we need to go back to it and apply
     *            the operation there.</li>
     *        <li>If the coverage has no geophysics view (an orthophoto with an intrisic
     *            {@link IndexColorModel} view) we need to perform an RGB(A) color expansion
     *            before applying the operation.</li>
     *      </ul>
     *  </li>
     * </ul>
     * <p>
     * A special case is when we want to apply an operation on the geophysics view of a coverage
     * that does not involve high order interpolation or filters. In this case we suggest to apply
     * the operation on the non-geophysics view, which is usually much faster. Users may ignore
     * this advice.
     *
     * @param coverage The coverage to check for the action to take.
     * @param interpolation The interpolation to use for the action to take, or {@code null} if none.
     * @param hasFilter {@code true} if the operation we will apply is going to use a filter.
     * @param hints The hints to use when applying a certain operation.
     * @return {@link ViewType#SAME} if nothing has to be done on the provided coverage,
     *         {@link ViewType#PHOTOGRAPHIC} if a color expansion has to be provided,
     *         {@link ViewType#GEOPHYSICS} if we need to employ the geophysics view of
     *         the provided coverage,
     *         {@link ViewType#NATIVE} if we suggest to employ the native (usually packed) view
     *         of the provided coverage.
     *
     * @since 2.5
     *
     * @todo Move this method in {@link org.geotoolkit.coverage.processing.Operation2D}.
     */
    public static ViewType preferredViewForOperation(final GridCoverage2D coverage,
            final InterpolationCase interpolationType, final boolean hasFilter, final RenderingHints hints)
    {
        /*
         * Checks if the user specified explicitly the view he wants to use for performing
         * the calculations.
         */
        if (hints != null) {
            final Object candidate = hints.get(Hints.COVERAGE_PROCESSING_VIEW);
            if (candidate instanceof ViewType) {
                return (ViewType) candidate;
            }
        }
        /*
         * Tries to infer automatically the view to use.  If there is no sample dimension with
         * a "sample to geophysics" transform, then we assume that the image has no geophysics
         * meaning and would better be handled as photographic.
         */
        final RenderedImage sourceImage = coverage.getRenderedImage();
        if (sourceImage.getColorModel() instanceof IndexColorModel) {
            if (!hasRenderingCategories(coverage)) {
                return ViewType.PHOTOGRAPHIC;
            }
            /*
             * If there is no filter and no interpolation, then we don't need to operate on
             * geophysics value. The packed view is usually faster. We could returns either
             * NATIVE, PACKED or SAME, which are equivalent in many cases:
             *
             *  - SAME is likely equivalent to PACKED because we checked that the color model is indexed.
             *  - NATIVE is likely equivalent to PACKED because data in NetCDF or HDF files are often packed.
             *
             * However those views differ in their behavior when the native data are geophysics
             * rather than packed (e.g. a NetCDF file with floating point values). In this case,
             * NATIVE is equivalent to GEOPHYSICS. The tradeoff of each views are:
             *
             *  - NATIVE is more accurate but slower when native data are geophysics
             *    (but as fast as other views when native data are packed).
             *
             *  - SAME is "as the user said" on the assumption that if he asked an operation on
             *    a packed view of a coverage rather than the geophysics view, he know what he
             *    is doing.
             */
            if (!hasFilter && (interpolationType == null || InterpolationCase.NEIGHBOR.equals(interpolationType))) {
                if (hints != null) {
                    final Object rendering = hints.get(RenderingHints.KEY_RENDERING);
                    if (RenderingHints.VALUE_RENDER_QUALITY.equals(rendering)) {
                        return ViewType.NATIVE;
                    }
                    if (RenderingHints.VALUE_RENDER_SPEED.equals(rendering)) {
                        return ViewType.SAME;
                    }
                }
                return ViewType.SAME; // Default value.
            }
            // In this case we need to go back the geophysics view of the source coverage.
            return ViewType.GEOPHYSICS;
        }
        /*
         * The operations are usually applied on floating-point values, in order
         * to gets maximal precision and to handle correctly the special case of
         * NaN values. However, we can apply some operation on integer values if
         * the interpolation type is "nearest neighbor", since this is not
         * really an interpolation.
         *
         * If this condition is met, then we verify if an "integer version" of
         * the image is available as a source of the source coverage (i.e. the
         * floating-point image is derived from the integer image, not the
         * converse).
         */
        if (!hasFilter && (interpolationType == null || InterpolationCase.NEIGHBOR.equals(interpolationType))) {
            final GridCoverage2D candidate = coverage.view(ViewType.NATIVE);
            if (candidate != coverage) {
                final List<RenderedImage> sources = coverage.getRenderedImage().getSources();
                if (sources != null && sources.contains(candidate.getRenderedImage())) {
                    return ViewType.NATIVE;
                }
            }
        }
        return ViewType.SAME;
    }

    /**
     * Computes a grid geometry from a source coverage and a target envelope. This is a convenience
     * method for computing the {@link #GRID_GEOMETRY} argument of a {@code "resample"} operation
     * from an envelope. The target envelope may contains a different coordinate reference system,
     * in which case a reprojection will be performed.
     *
     * @param source The source coverage.
     * @param target The target envelope, including a possibly different coordinate reference system.
     * @return A grid geometry inferred from the target envelope.
     * @throws TransformException If a transformation was required and failed.
     *
     * @since 2.5
     */
    public static GridGeometry computeGridGeometry(final GridCoverage source, final Envelope target)
            throws TransformException
    {
        final CoordinateReferenceSystem targetCRS = target.getCoordinateReferenceSystem();
        final CoordinateReferenceSystem sourceCRS = source.getCoordinateReferenceSystem();
        final CoordinateReferenceSystem reducedCRS;
        if (target.getDimension() == 2 && sourceCRS.getCoordinateSystem().getDimension() != 2) {
            reducedCRS = CoverageUtilities.getCRS2D(source);
        } else {
            reducedCRS = sourceCRS;
        }
        GridGeometry gridGeometry = source.getGridGeometry();
        if (targetCRS == null || CRS.equalsIgnoreMetadata(reducedCRS, targetCRS)) {
            /*
             * Same CRS (or unknown target CRS, which we treat as same), so we will keep the same
             * "gridToCRS" transform. Basically the result will be the same as if we did a crop,
             * except that we need to take in account a change from nD to 2D.
             */
            final MathTransform gridToCRS;
            if (reducedCRS == sourceCRS) {
                gridToCRS = gridGeometry.getGridToCRS();
            } else {
                gridToCRS = GridGeometry2D.castOrCopy(gridGeometry).getGridToCRS2D();
            }
            gridGeometry = new GridGeometry2D(PixelInCell.CELL_CENTER, gridToCRS, target, null);
        } else {
            /*
             * Different CRS. We need to infer an image size, which may be the same than the
             * original size or something smaller if the envelope is a subarea. We process by
             * transforming the target envelope to the source CRS and compute a new grid geometry
             * with that envelope. The grid envelope of that grid geometry is the new image size.
             * Note that failure to transform the envelope is non-fatal (we will assume that the
             * target image should have the same size). Then create again a new grid geometry,
             * this time with the target envelope.
             */
            GridEnvelope gridEnvelope;
            try {
                final GeneralEnvelope transformed;
                transformed = CRS.transform(CRS.getCoordinateOperationFactory(true)
                        .createOperation(targetCRS, reducedCRS), target);
                final Envelope reduced;
                final MathTransform gridToCRS;
                if (reducedCRS == sourceCRS) {
                    reduced   = source.getEnvelope();
                    gridToCRS = gridGeometry.getGridToCRS();
                } else {
                    reduced   = CoverageUtilities.getEnvelope2D(source);
                    gridToCRS = GridGeometry2D.castOrCopy(gridGeometry).getGridToCRS2D();
                }
                transformed.intersect(reduced);
                gridGeometry = new GridGeometry2D(PixelInCell.CELL_CENTER, gridToCRS, transformed, null);
            } catch (FactoryException exception) {
                recoverableException("resample", exception);
            } catch (TransformException exception) {
                recoverableException("resample", exception);
                // Will use the grid envelope from the original geometry,
                // which will result in keeping the same image size.
            }
            gridEnvelope = gridGeometry.getExtent();
            gridGeometry = new GridGeometry2D(gridEnvelope, target);
        }
        return gridGeometry;
    }

    /**
     * Invoked when an error occurred but the application can fallback on a reasonable default.
     *
     * @param method The method where the error occurred.
     * @param exception The error.
     */
    private static void recoverableException(final String method, final Exception exception) {
        Logging.recoverableException(null, ResampleProcess.class, method, exception);
    }


}
