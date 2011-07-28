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
package org.geotoolkit.process.jts.covers;

import com.vividsolutions.jts.geom.Geometry;
import org.geotoolkit.process.AbstractProcess;
import org.opengis.parameter.ParameterValueGroup;

import static org.geotoolkit.process.jts.covers.CoversDescriptor.*;
import static org.geotoolkit.parameter.Parameters.*;
/**
 * @author Quentin Boileau (Geomatys)
 * @module pending
 */
public class CoversProcess extends AbstractProcess{
    
    public CoversProcess(){
        super(INSTANCE);
    }
    
    @Override
    public void run() {
        
        final Geometry geom1 = value(GEOM1, inputParameters); 
        final Geometry geom2 = value(GEOM2, inputParameters); 
        
        final Boolean result = (Boolean) geom1.covers(geom2);
        
        final ParameterValueGroup output =  getOutput();
        getOrCreate(RESULT, output).setValue(result); 
        
    }
    
}
