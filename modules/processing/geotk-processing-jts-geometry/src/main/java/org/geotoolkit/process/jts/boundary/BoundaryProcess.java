/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2011, Geomatys
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
package org.geotoolkit.process.jts.boundary;

import com.vividsolutions.jts.geom.Geometry;
import org.geotoolkit.process.AbstractProcess;
import org.opengis.parameter.ParameterValueGroup;

import static org.geotoolkit.process.jts.boundary.BoundaryDescriptor.*;
import static org.geotoolkit.parameter.Parameters.*;
/**
 * @author Quentin Boileau (Geomatys)
 * @module pending
 */
public class BoundaryProcess extends AbstractProcess{
    
    public BoundaryProcess(final ParameterValueGroup input){
        super(INSTANCE,input);
    }
    
    @Override
    public ParameterValueGroup call() {
        
        final Geometry geom = value(GEOM, inputParameters);  
        
        final Geometry result = geom.getBoundary();
        
        getOrCreate(RESULT_GEOM, outputParameters).setValue(result); 
        return outputParameters;
    }
    
}
