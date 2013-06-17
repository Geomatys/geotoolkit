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
package org.geotoolkit.process.coverage.resample;

import org.geotoolkit.coverage.grid.GridCoverage2D;
import org.geotoolkit.coverage.grid.GridGeometry2D;
import org.geotoolkit.coverage.processing.CannotReprojectException;
import org.geotoolkit.coverage.processing.operation.Resample;
import org.geotoolkit.geometry.GeneralEnvelope;
import org.geotoolkit.image.interpolation.InterpolationCase;
import org.geotoolkit.internal.coverage.CoverageUtilities;
import org.geotoolkit.parameter.Parameters;
import org.geotoolkit.process.AbstractProcess;
import org.geotoolkit.process.ProcessException;
import org.geotoolkit.referencing.CRS;
import org.geotoolkit.resources.Errors;
import org.geotoolkit.util.logging.Logging;

import static org.geotoolkit.process.coverage.resample.Resample2Descriptor.*;


import org.opengis.coverage.grid.GridCoverage;
import org.opengis.coverage.grid.GridEnvelope;
import org.opengis.coverage.grid.GridGeometry;
import org.opengis.geometry.Envelope;
import org.opengis.parameter.ParameterValueGroup;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.datum.PixelInCell;
import org.opengis.referencing.operation.MathTransform;
import org.opengis.referencing.operation.TransformException;
import org.opengis.util.FactoryException;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public class Resample2Process extends AbstractProcess {

    Resample2Process(final ParameterValueGroup input) {
        super(INSTANCE, input);
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
            target = Resampler2D.reproject(source, targetCRS, targetGG, interpolation, background, null);
        } catch (FactoryException exception) {
            throw new CannotReprojectException(Errors.format(
                    Errors.Keys.CANT_REPROJECT_COVERAGE_1, source.getName()), exception);
        } catch (TransformException exception) {
            throw new CannotReprojectException(Errors.format(
                    Errors.Keys.CANT_REPROJECT_COVERAGE_1, source.getName()), exception);
        }
        
        Parameters.getOrCreate(OUT_COVERAGE, outputParameters).setValue(target);
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
        Logging.recoverableException(Resample.class, method, exception);
    }
    
    
}
