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
package org.geotoolkit.process.jts.union;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import java.util.Collections;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.geotoolkit.geometry.jts.JTS;
import org.geotoolkit.process.AbstractProcess;
import org.geotoolkit.process.jts.JTSProcessingUtils;
import org.opengis.geometry.MismatchedDimensionException;
import org.opengis.parameter.ParameterValueGroup;

import org.opengis.referencing.NoSuchAuthorityCodeException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import static org.geotoolkit.process.jts.union.UnionDescriptor.*;
import static org.geotoolkit.parameter.Parameters.*;
import org.opengis.referencing.operation.TransformException;
import org.opengis.util.FactoryException;
/**
 * Compute the union geometry of the two inputs geometries.
 * The process ensure that two geometries are into the same CoordinateReferenceSystem.
 * The returned point keep the common geometry CRS.
 * @author Quentin Boileau (Geomatys)
 * @module pending
 */
public class UnionProcess extends AbstractProcess{
    
    public UnionProcess(final ParameterValueGroup input){
        super(INSTANCE,input);
    }
    
    @Override
    public ParameterValueGroup call() {
       
        try {
            
            Geometry geom1 = value(GEOM1, inputParameters); 
            Geometry geom2 = value(GEOM2, inputParameters); 
            
            Geometry result = new GeometryFactory().buildGeometry(Collections.emptyList());
            
            // ensure geometries are in the same CRS
            final CoordinateReferenceSystem resultCRS = JTSProcessingUtils.getCommonCRS(geom1, geom2);
            if(JTSProcessingUtils.isConversionNeeded(geom1, geom2)){
                geom2 = JTSProcessingUtils.convertToCRS(geom2, resultCRS);
            }
            
            result = (Geometry) geom1.union(geom2);
            if(resultCRS != null){
                JTS.setCRS(result, resultCRS);
            }
            
            
            getOrCreate(RESULT_GEOM, outputParameters).setValue(result); 
            
        } catch (MismatchedDimensionException ex) {
            Logger.getLogger(UnionProcess.class.getName()).log(Level.WARNING, null, ex);
        } catch (TransformException ex) {
            Logger.getLogger(UnionProcess.class.getName()).log(Level.WARNING, null, ex);
        } catch (NoSuchAuthorityCodeException ex) {
            Logger.getLogger(UnionProcess.class.getName()).log(Level.WARNING, null, ex);
        } catch (FactoryException ex) {
            Logger.getLogger(UnionProcess.class.getName()).log(Level.WARNING, null, ex);
        }
        
         return outputParameters;
    }
    
}
