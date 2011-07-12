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
package org.geotoolkit.process.jts.buffer;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import java.util.Collections;
import org.geotoolkit.process.AbstractProcess;
import org.opengis.parameter.ParameterValueGroup;

import static org.geotoolkit.process.jts.buffer.BufferDescriptor.*;
import static org.geotoolkit.parameter.Parameters.*;
/**
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public class BufferProcess extends AbstractProcess{
    
    public BufferProcess(){
        super(INSTANCE);
    }
    
    @Override
    public void run() {
        
        final Geometry geom = value(GEOM, inputParameters);  
        final double distance = value(DISTANCE, inputParameters);
        
        int segments = 0;
        if(value(SEGMENTS, inputParameters) != null){
            segments = value(SEGMENTS, inputParameters);  
        }
        
        int endStyle = 0;
        if(value(ENDSTYLE, inputParameters) != null){
             endStyle = value(ENDSTYLE, inputParameters);   
        }
        
        Geometry result = new GeometryFactory().buildGeometry(Collections.EMPTY_LIST);
        
        if(segments > 0){
            if(endStyle != 0){
                 result = geom.buffer(distance, segments, endStyle);
            }else{
                 result = geom.buffer(distance, segments);
            }
        }else{
            result = geom.buffer(distance);
        }
     
        final ParameterValueGroup output =  getOutput();
        getOrCreate(RESULT_GEOM, output).setValue(result); 
        
    }
    
}
