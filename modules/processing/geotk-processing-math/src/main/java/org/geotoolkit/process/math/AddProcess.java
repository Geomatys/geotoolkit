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
package org.geotoolkit.process.math;

import org.geotoolkit.process.AbstractProcess;
import org.opengis.parameter.ParameterValueGroup;

/**
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public class AddProcess extends AbstractProcess{

    private Double result = null;
    
    public AddProcess(){
        super(AddDescriptor.INSTANCE);
    }
    
    @Override
    public void run() {
        
        final double first = (Double)inputParameters.parameter("first").getValue();   
        final double second = (Double)inputParameters.parameter("second").getValue();       
        
        result = first + second;        
    }

    @Override
    public ParameterValueGroup getOutput() {
        final ParameterValueGroup res =  super.getOutput();
        res.parameter("result").setValue(result);
        return res;
    }
    
}
