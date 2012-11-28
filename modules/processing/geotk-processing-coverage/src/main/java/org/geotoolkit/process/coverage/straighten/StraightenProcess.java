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
package org.geotoolkit.process.coverage.straighten;

import org.geotoolkit.coverage.grid.GridCoverage2D;
import org.geotoolkit.coverage.grid.GridEnvelope2D;
import org.geotoolkit.coverage.grid.GridGeometry2D;
import org.geotoolkit.coverage.processing.Operations;
import static org.geotoolkit.parameter.Parameters.*;
import org.geotoolkit.process.AbstractProcess;
import org.geotoolkit.process.ProcessException;
import static org.geotoolkit.process.coverage.straighten.StraightenDescriptor.*;
import org.geotoolkit.referencing.operation.transform.AffineTransform2D;
import org.opengis.geometry.Envelope;
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
        super(INSTANCE,input);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void execute() throws ProcessException {
        final GridCoverage2D candidate = (GridCoverage2D) value(COVERAGE_IN, inputParameters);

        //resample coverage, we want it to be 'straight', no rotation or different axe scale.
        final CoordinateReferenceSystem crs = candidate.getCoordinateReferenceSystem2D();
        final GridGeometry2D gridgeom = candidate.getGridGeometry();
        final MathTransform gridToCRS = gridgeom.getGridToCRS2D();
        final Envelope outEnv = candidate.getEnvelope2D();

        try{
            final double[] coords = new double[2 * 3];
            coords[2] = 1;
            coords[2*2+1] = 1;
            gridToCRS.transform(coords, 0, coords, 0, 3);
            final double scale = Math.min(
                    Math.abs(coords[0] - coords[2]),
                    Math.abs(coords[1] - coords[5]));
            
            final AffineTransform2D outGridToCRS = new AffineTransform2D(scale, 0, 0, -scale, outEnv.getMinimum(0), outEnv.getMaximum(1));
            final GridEnvelope2D gridEnv = new GridEnvelope2D(0, 0, (int)(outEnv.getSpan(0)/scale), (int)(outEnv.getSpan(1)/scale));
            final GridGeometry2D outgridGeom = new GridGeometry2D(gridEnv,outGridToCRS,crs);
            final GridCoverage2D outCoverage = (GridCoverage2D) Operations.DEFAULT.resample(candidate, crs, outgridGeom, null);
            getOrCreate(COVERAGE_OUT, outputParameters).setValue(outCoverage);
        }catch(TransformException ex){
            throw new ProcessException(ex.getMessage(), this, ex);
        }
    }

}
