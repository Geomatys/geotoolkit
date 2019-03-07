/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
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
package org.geotoolkit.processing.coverage.straighten;

import org.apache.sis.coverage.grid.GridExtent;
import org.apache.sis.internal.referencing.j2d.AffineTransform2D;
import org.apache.sis.parameter.Parameters;
import org.geotoolkit.coverage.grid.GridCoverage2D;
import org.geotoolkit.coverage.grid.GridGeometry2D;
import org.geotoolkit.process.ProcessException;
import org.geotoolkit.processing.AbstractProcess;
import org.geotoolkit.coverage.grid.Coverage;
import org.geotoolkit.image.interpolation.InterpolationCase;
import org.geotoolkit.processing.coverage.resample.ResampleProcess;
import org.opengis.geometry.Envelope;
import org.opengis.metadata.spatial.PixelOrientation;
import org.opengis.parameter.ParameterValueGroup;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.MathTransform;
import org.opengis.referencing.operation.TransformException;

/**
 * Remove rotation or any special case from the coverage GridToCRS.
 *
 * @author Johann Sorel (Geomatys)
 * @module
 */
public class StraightenProcess extends AbstractProcess {

    /**
     * Default constructor
     */
    public StraightenProcess(final ParameterValueGroup input) {
        super(StraightenDescriptor.INSTANCE,input);
    }

    /**
     *
     * @param coverage coverage to process
     */
    public StraightenProcess(Coverage coverage){
        super(StraightenDescriptor.INSTANCE, asParameters(coverage));
    }

    private static ParameterValueGroup asParameters(Coverage coverage){
        final Parameters params = Parameters.castOrWrap(StraightenDescriptor.INPUT_DESC.createValue());
        params.getOrCreate(StraightenDescriptor.COVERAGE_IN).setValue(coverage);
        return params;
    }

    /**
     * Execute process now.
     *
     * @return straighten coverage
     * @throws ProcessException
     */
    public Coverage executeNow() throws ProcessException {
        execute();
        return (Coverage) outputParameters.parameter(StraightenDescriptor.COVERAGE_OUT.getName().getCode()).getValue();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void execute() throws ProcessException {
        final GridCoverage2D candidate = (GridCoverage2D) inputParameters.getValue(StraightenDescriptor.COVERAGE_IN);

        //resample coverage, we want it to be 'straight', no rotation or different axe scale.
        final CoordinateReferenceSystem crs = candidate.getCoordinateReferenceSystem2D();
        final GridGeometry2D gridgeom = candidate.getGridGeometry();
        final GridExtent gridenv = gridgeom.getExtent2D();
        final MathTransform gridToCRS = gridgeom.getGridToCRS2D(PixelOrientation.UPPER_LEFT);
        final Envelope outEnv = candidate.getEnvelope2D();



        try{
            final double[] coords = new double[2 * 5];
            coords[0] = gridenv.getLow(0);      coords[1] = gridenv.getLow(1);
            coords[2] = gridenv.getLow(0);      coords[3] = gridenv.getHigh(1)+1;
            coords[4] = gridenv.getHigh(0)+1;      coords[5] = gridenv.getHigh(1)+1;
            coords[6] = gridenv.getHigh(0)+1;      coords[7] = gridenv.getLow(1);
            coords[8] = gridenv.getHigh(0)+2;    coords[9] = gridenv.getHigh(1)+2;
            gridToCRS.transform(coords, 0, coords, 0, 5);
            double minX = Math.min(Math.min(coords[0], coords[2]), Math.min(coords[4], coords[6]));
            double maxX = Math.max(Math.max(coords[0], coords[2]), Math.max(coords[4], coords[6]));
            double minY = Math.min(Math.min(coords[1], coords[3]), Math.min(coords[5], coords[7]));
            double maxY = Math.max(Math.max(coords[1], coords[3]), Math.max(coords[5], coords[7]));
            double spanX = maxX-minX;
            double spanY = maxY-minY;
            double scaleX = spanX / gridenv.getSize(0);
            double scaleY = spanY / gridenv.getSize(1);
            double scale = Math.min(scaleX, scaleY);

            if(coords[0] > coords[6]){
                // x axe flip
                minX = Math.min(minX, coords[8]);
            }
            if(coords[1] < coords[3]){
                // y axe flip
                maxY = Math.max(maxY, coords[9]);
            }

            final AffineTransform2D outGridToCRS = new AffineTransform2D(scale, 0, 0, -scale, minX, maxY);
            final GridExtent gridEnv = new GridExtent((long)(spanX/scale), (long)(spanY/scale));
            final GridGeometry2D outgridGeom = new GridGeometry2D(gridEnv, PixelOrientation.UPPER_LEFT, outGridToCRS, crs);
            final GridCoverage2D outCoverage = new ResampleProcess(candidate, outgridGeom.getCoordinateReferenceSystem(), outgridGeom, InterpolationCase.NEIGHBOR, null).executeNow();
            outputParameters.getOrCreate(StraightenDescriptor.COVERAGE_OUT).setValue(outCoverage);
        }catch(TransformException ex){
            throw new ProcessException(ex.getMessage(), this, ex);
        }
    }

}
