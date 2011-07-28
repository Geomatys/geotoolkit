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
package org.geotoolkit.process.jts.isempty;

import com.vividsolutions.jts.geom.Geometry;
import org.geotoolkit.process.AbstractProcess;
import org.opengis.parameter.ParameterValueGroup;

import static org.geotoolkit.process.jts.isempty.IsEmptyDescriptor.*;
import static org.geotoolkit.parameter.Parameters.*;
/**
 * @author Quentin Boileau (Geomatys)
 * @module pending
 */
public class IsEmptyProcess extends AbstractProcess{
    
    public IsEmptyProcess(){
        super(INSTANCE);
    }
    
    @Override
    public void run() {
        
        final Geometry geom1 = value(GEOM, inputParameters); 
        
        final Boolean result = (Boolean) geom1.isEmpty();
        
        final ParameterValueGroup output =  getOutput();
        getOrCreate(RESULT, output).setValue(result); 
        
    }
    
}
