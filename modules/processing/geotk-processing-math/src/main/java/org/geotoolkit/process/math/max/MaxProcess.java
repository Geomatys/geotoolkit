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
package org.geotoolkit.process.math.max;

import org.geotoolkit.process.AbstractProcess;
import org.opengis.parameter.ParameterValueGroup;

/**
 * @author Quentin Boileau (Geomatys)
 * @module pending
 */
public class MaxProcess extends AbstractProcess{
    
    public MaxProcess(){
        super(MaxDescriptor.INSTANCE);
    }
    
    @Override
    public void run() {
        
        final Double[] set = (Double[])inputParameters.parameter("set").getValue();   
        
        Double max = Math.max(set[0], set[1]);
        for(int i=1; i<set.length; i++){
            max = Math.max(max.doubleValue(), set[i].doubleValue());
        }
       
        final ParameterValueGroup res =  super.getOutput();
        res.parameter("result").setValue(max);
        
    }
    
}
