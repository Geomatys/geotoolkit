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
package org.geotoolkit.process.jts.area;

import com.vividsolutions.jts.geom.Geometry;
import org.geotoolkit.process.AbstractProcess;
import org.opengis.parameter.ParameterValueGroup;

import static org.geotoolkit.process.jts.area.AreaDescriptor.*;
import static org.geotoolkit.parameter.Parameters.*;
/**
 * @author Quentin Boileau (Geomatys)
 * @module pending
 */
public class AreaProcess extends AbstractProcess{
    
    public AreaProcess(final ParameterValueGroup input){
        super(INSTANCE,input);
    }
    
    @Override
    public ParameterValueGroup call() {
        
        final Geometry geom = value(GEOM, inputParameters);  
               
        final Double result = geom.getArea();
               
        getOrCreate(RESULT, outputParameters).setValue(result); 
        return outputParameters;
    }
    
}
