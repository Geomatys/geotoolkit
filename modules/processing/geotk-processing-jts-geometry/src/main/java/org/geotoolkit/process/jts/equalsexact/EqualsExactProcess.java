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
package org.geotoolkit.process.jts.equalsexact;

import java.util.logging.Level;
import java.util.logging.Logger;
import org.opengis.referencing.NoSuchAuthorityCodeException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import com.vividsolutions.jts.geom.Geometry;
import org.geotoolkit.process.AbstractProcess;
import org.opengis.parameter.ParameterValueGroup;

import static org.geotoolkit.process.jts.equalsexact.EqualsExactDescriptor.*;
import static org.geotoolkit.parameter.Parameters.*;
import org.geotoolkit.process.jts.JTSProcessingUtils;
import org.opengis.referencing.operation.TransformException;
import org.opengis.util.FactoryException;
/**
 * Compute if two input geometries are equalExact.
 * The process ensure that two geometries are into the same CoordinateReferenceSystem. * 
 * @author Quentin Boileau (Geomatys)
 * @module pending
 */
public class EqualsExactProcess extends AbstractProcess{
    
    public EqualsExactProcess(final ParameterValueGroup input){
        super(INSTANCE,input);
    }
    
    @Override
    public ParameterValueGroup call() {
        try {
            final Geometry geom1 = value(GEOM1, inputParameters); 
            Geometry geom2 = value(GEOM2, inputParameters);
            
             // ensure geometries are in the same CRS
            final CoordinateReferenceSystem resultCRS = JTSProcessingUtils.getCommonCRS(geom1, geom2);
            if(JTSProcessingUtils.isConversionNeeded(geom1, geom2)){
                geom2 = JTSProcessingUtils.convertToCRS(geom2, resultCRS);
            }
            
            Boolean result = false;
            
            if(value(TOLERANCE, inputParameters) != null){
                final Double tolerance = value(TOLERANCE, inputParameters);
                result = (Boolean) geom1.equalsExact(geom2,tolerance);
                
            }else{
                result = (Boolean) geom1.equalsExact(geom2);
            }
            
            getOrCreate(RESULT, outputParameters).setValue(result); 
            
        } catch (TransformException ex) {
            Logger.getLogger(EqualsExactProcess.class.getName()).log(Level.WARNING, null, ex);
        } catch (FactoryException ex) {
            Logger.getLogger(EqualsExactProcess.class.getName()).log(Level.WARNING, null, ex);
        }
        
        return outputParameters;
    }
    
}
