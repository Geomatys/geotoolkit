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

import org.apache.sis.coverage.grid.GridCoverage;
import org.apache.sis.coverage.grid.GridCoverageProcessor;
import org.apache.sis.coverage.grid.GridGeometry;
import org.apache.sis.image.ImageProcessor;
import org.apache.sis.image.Interpolation;
import org.apache.sis.parameter.Parameters;
import org.geotoolkit.image.interpolation.InterpolationCase;
import org.geotoolkit.image.interpolation.ResampleBorderComportement;
import org.geotoolkit.process.ProcessException;
import org.geotoolkit.processing.AbstractProcess;
import static org.geotoolkit.processing.coverage.resample.ResampleDescriptor.*;
import org.geotoolkit.resources.Errors;
import org.opengis.parameter.ParameterValueGroup;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.datum.PixelInCell;
import org.opengis.referencing.operation.TransformException;
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
            target = reproject(source, targetCRS, targetGG, interpolation, border, background);
        } catch (FactoryException exception) {
            throw new CannotReprojectException(Errors.format(
                    Errors.Keys.CantReprojectCoverage_1, "source"), exception);
        } catch (TransformException exception) {
            throw new CannotReprojectException(Errors.format(
                    Errors.Keys.CantReprojectCoverage_1, "source"), exception);
        }
        outputParameters.getOrCreate(OUT_COVERAGE).setValue(target);
    }

    /**
     * Creates a new coverage with a different coordinate reference reference system. If a
     * grid geometry is supplied, only its {@linkplain GridGeometry#getExtent()}  grid envelope}
     * and {@linkplain GridGeometry#getGridToCRS grid to CRS} transform are taken in account.
     *
     * @param sourceCoverage The source grid coverage.
     * @param targetCRS      Coordinate reference system for the new grid coverage, or {@code null}.
     * @param targetGG       The target grid geometry, or {@code null} for default.
     * @param background     The background values, or {@code null} for default.
     * @param interpolationType  The interpolation to use, or {@code null} if none.
     * @return  The new grid coverage, or {@code sourceCoverage} if no resampling was needed.
     * @throws  FactoryException If a transformation step can't be created.
     * @throws TransformException If a transformation failed.
     */
    public static GridCoverage reproject(GridCoverage              sourceCoverage,
                                           CoordinateReferenceSystem targetCRS,
                                           GridGeometry              targetGG,
                                           InterpolationCase         interpolationType,
                                           double[]                  background)
            throws FactoryException, TransformException
    {
        return reproject(sourceCoverage, targetCRS, targetGG, interpolationType, null, background);
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
     * @return  The new grid coverage, or {@code sourceCoverage} if no resampling was needed.
     * @throws  FactoryException If a transformation step can't be created.
     * @throws TransformException If a transformation failed.
     */
    public static GridCoverage reproject(GridCoverage              sourceCov,
                                           CoordinateReferenceSystem targetCRS,
                                           GridGeometry              targetGG,
                                           InterpolationCase         interpolationType,
                                           ResampleBorderComportement borderComportement,
                                           double[]                  background)
            throws FactoryException, TransformException
    {
        //set default values

        if (interpolationType == null) {
            interpolationType = InterpolationCase.NEIGHBOR;
        }


        final ImageProcessor imgProcessor = new ImageProcessor();
        if (background != null) {
            final Number[] result = new Number[background.length];
            for (int i=0; i<background.length; i++) {
                result[i] = background[i];
            }
            imgProcessor.setFillValues(result);
        }
        final Interpolation interpolation;
        switch (interpolationType) {
            case BICUBIC :
            case BICUBIC2 :
            case BILINEAR : interpolation = Interpolation.BILINEAR; break;
            case LANCZOS : interpolation = Interpolation.LANCZOS; break;
            default : interpolation = Interpolation.NEAREST; break;
        }

        if (targetGG == null) {
            if (targetCRS == null) {
                return sourceCov;
            }
            targetGG = new GridGeometry(null, PixelInCell.CELL_CENTER, null, targetCRS);
        }

        final GridCoverageProcessor processor = new GridCoverageProcessor(imgProcessor);
        processor.setInterpolation(interpolation);
        final GridCoverage resampled = processor.resample(sourceCov, targetGG);
        return resampled;
    }

}
