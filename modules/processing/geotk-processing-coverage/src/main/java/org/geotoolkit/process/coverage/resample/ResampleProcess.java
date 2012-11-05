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

import org.geotoolkit.coverage.processing.Operations;
import org.geotoolkit.parameter.Parameters;
import org.geotoolkit.process.AbstractProcess;
import org.geotoolkit.process.ProcessException;
import static org.geotoolkit.process.coverage.resample.ResampleDescriptor.*;
import org.opengis.coverage.Coverage;
import org.opengis.coverage.grid.GridCoverage;
import org.opengis.geometry.Envelope;
import org.opengis.parameter.ParameterValueGroup;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public class ResampleProcess extends AbstractProcess {

    ResampleProcess(final ParameterValueGroup input) {
        super(INSTANCE, input);
    }

    @Override
    protected void execute() throws ProcessException {
        
        final GridCoverage inputCoverage = (GridCoverage) Parameters.getOrCreate(IN_COVERAGE, inputParameters).getValue();
        final CoordinateReferenceSystem inputCRS = (CoordinateReferenceSystem) Parameters.getOrCreate(IN_CRS, inputParameters).getValue();
        final Envelope inputEnv = (Envelope) Parameters.getOrCreate(IN_ENVELOPE, inputParameters).getValue();
        
        if(inputCRS == null && inputEnv == null ){
            throw new ProcessException("One of input crs or envelope parameter must be set.", this, new IllegalArgumentException());
        }
        
        
        final Coverage result;
        if(inputEnv != null){
            result = Operations.DEFAULT.resample(inputCoverage, inputEnv, null);
        }else{
            result = Operations.DEFAULT.resample(inputCoverage, inputCRS);
        }
        
        Parameters.getOrCreate(OUT_COVERAGE, outputParameters).setValue(result);
    }
    
}
