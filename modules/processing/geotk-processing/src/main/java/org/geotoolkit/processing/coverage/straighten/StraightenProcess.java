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

import org.geotoolkit.coverage.grid.GridCoverage2D;
import org.geotoolkit.coverage.grid.GridEnvelope2D;
import org.geotoolkit.coverage.grid.GridGeometry2D;
import org.geotoolkit.coverage.processing.Operations;
import static org.geotoolkit.parameter.Parameters.*;
import org.geotoolkit.processing.AbstractProcess;
import org.geotoolkit.process.ProcessException;
import org.apache.sis.internal.referencing.j2d.AffineTransform2D;
import org.geotoolkit.utility.parameter.ParametersExt;
import org.opengis.coverage.Coverage;
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
 * @module pending
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
        final ParameterValueGroup params = StraightenDescriptor.INPUT_DESC.createValue();
        ParametersExt.getOrCreateValue(params, StraightenDescriptor.COVERAGE_IN.getName().getCode()).setValue(coverage);
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
        final GridCoverage2D candidate = (GridCoverage2D) value(StraightenDescriptor.COVERAGE_IN, inputParameters);

        //resample coverage, we want it to be 'straight', no rotation or different axe scale.
        final CoordinateReferenceSystem crs = candidate.getCoordinateReferenceSystem2D();
        final GridGeometry2D gridgeom = candidate.getGridGeometry();
        final GridEnvelope2D gridenv = gridgeom.getExtent2D();
        final MathTransform gridToCRS = gridgeom.getGridToCRS2D(PixelOrientation.UPPER_LEFT);
        final Envelope outEnv = candidate.getEnvelope2D();

        
        
        try{
            final double[] coords = new double[2 * 5];
            coords[0] = gridenv.getMinX();      coords[1] = gridenv.getMinY();
            coords[2] = gridenv.getMinX();      coords[3] = gridenv.getMaxY();
            coords[4] = gridenv.getMaxX();      coords[5] = gridenv.getMaxY();
            coords[6] = gridenv.getMaxX();      coords[7] = gridenv.getMinY();  
            coords[8] = gridenv.getMaxX()+1;    coords[9] = gridenv.getMaxY()+1;  
            gridToCRS.transform(coords, 0, coords, 0, 5);
            double minX = Math.min(Math.min(coords[0], coords[2]), Math.min(coords[4], coords[6]));
            double maxX = Math.max(Math.max(coords[0], coords[2]), Math.max(coords[4], coords[6]));
            double minY = Math.min(Math.min(coords[1], coords[3]), Math.min(coords[5], coords[7]));
            double maxY = Math.max(Math.max(coords[1], coords[3]), Math.max(coords[5], coords[7]));
            double spanX = maxX-minX;
            double spanY = maxY-minY;
            double scaleX = spanX / gridenv.getWidth();
            double scaleY = spanY / gridenv.getHeight();
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
            final GridEnvelope2D gridEnv = new GridEnvelope2D(0, 0, (int)(spanX/scale), (int)(spanY/scale));
            final GridGeometry2D outgridGeom = new GridGeometry2D(gridEnv, PixelOrientation.UPPER_LEFT, outGridToCRS,crs,null);
            final GridCoverage2D outCoverage = (GridCoverage2D) Operations.DEFAULT.resample(candidate, crs, outgridGeom, null);
            getOrCreate(StraightenDescriptor.COVERAGE_OUT, outputParameters).setValue(outCoverage);
        }catch(TransformException ex){
            throw new ProcessException(ex.getMessage(), this, ex);
        }
    }

}
