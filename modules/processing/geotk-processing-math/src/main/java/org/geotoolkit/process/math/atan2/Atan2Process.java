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
package org.geotoolkit.process.math.atan2;

import org.geotoolkit.process.AbstractProcess;
import org.geotoolkit.process.ProcessEvent;
import org.geotoolkit.util.DefaultInternationalString;
import org.opengis.parameter.ParameterValueGroup;

import static org.geotoolkit.process.math.atan2.Atan2Descriptor.*;
import static org.geotoolkit.parameter.Parameters.*;
/**
 * @author Quentin Boileau (Geomatys)
 * @module pending
 */
public class Atan2Process extends AbstractProcess{
    
    public Atan2Process(final ParameterValueGroup input){
        super(INSTANCE,input);
    }
    
    @Override
    public ParameterValueGroup call() {
        
        final double first = value(FIRST_NUMBER, inputParameters); 
        final double second = value(SECOND_NUMBER, inputParameters);
        
        Double result = 0.0;
        try{
            result = Math.atan2(second, first);
        }catch(Exception e){
            fireFailEvent(new ProcessEvent(this, e.getMessage(),0, e));
        }
        
        getOrCreate(RESULT_NUMBER, outputParameters).setValue(result);
        return outputParameters;
    }
    
}
