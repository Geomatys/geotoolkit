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
package org.geotoolkit.process.math.avg;

import org.geotoolkit.process.AbstractProcess;
import org.opengis.parameter.ParameterValueGroup;

/**
 * @author Quentin Boileau (Geomatys)
 * @module pending
 */
public class AvgProcess extends AbstractProcess{
    
    public AvgProcess(){
        super(AvgDescriptor.INSTANCE);
    }
    
    @Override
    public void run() {
        
        final Double[] set = (Double[])inputParameters.parameter("set").getValue();   
        
        Double sum = 0.0;
        for(int i=0; i<set.length; i++){
            sum += set[i].doubleValue();
        }
        
        Double result = sum / set.length;
        
        final ParameterValueGroup res =  super.getOutput();
        res.parameter("result").setValue(result);
        
    }
    
}
