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
package org.geotoolkit.process.math.substract;

import org.geotoolkit.process.AbstractProcess;
import org.opengis.parameter.ParameterValueGroup;

import static org.geotoolkit.process.math.substract.SubstractDescriptor.*;
import static org.geotoolkit.parameter.Parameters.*;

/**
 * @author Quentin Boileau (Geomatys)
 * @module pending
 */
public class SubstractProcess extends AbstractProcess{
    
    public SubstractProcess(){
        super(INSTANCE);
    }
    
    @Override
    public void run() {
        
        final double first = value(FIRST_NUMBER, inputParameters);  
        final double second = value(SECOND_NUMBER, inputParameters);      
        
        final Double result = first - second;
        
        final ParameterValueGroup output =  getOutput();
        getOrCreate(RESULT_NUMBER, output).setValue(result);  
        
    }
    
}
