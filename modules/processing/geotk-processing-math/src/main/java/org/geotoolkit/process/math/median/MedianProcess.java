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
package org.geotoolkit.process.math.median;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.geotoolkit.process.AbstractProcess;
import org.opengis.parameter.ParameterValueGroup;

/**
 * @author Quentin Boileau (Geomatys)
 * @module pending
 */
public class MedianProcess extends AbstractProcess{
    
    public MedianProcess(){
        super(MedianDescriptor.INSTANCE);
    }
    
    @Override
    public void run() {
        
        final Double[] set = (Double[])inputParameters.parameter("set").getValue();   
        List<Double> list = Arrays.asList(set);
        
        //Sort the set of double
        Collections.sort(list);
        
        //index of the median
        int indexMedian = ((list.size()+1)/2)-1;
        Double median = 0.0;
        
        if(list.size()%2 == 0){
            median = (list.get(indexMedian) + list.get(indexMedian+1))/2;//avg of index and index +1
        }else{
            median = list.get(indexMedian);
        }
       
        final ParameterValueGroup res =  super.getOutput();
        res.parameter("result").setValue(median);
        
    }
    
}
