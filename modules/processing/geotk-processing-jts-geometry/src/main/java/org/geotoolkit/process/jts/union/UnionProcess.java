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
import org.geotoolkit.process.jts.JTSProcessingUtils;
import java.util.Collections;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.geotoolkit.geometry.jts.JTS;
import org.geotoolkit.process.AbstractProcess;
import org.geotoolkit.referencing.CRS;
import org.opengis.geometry.MismatchedDimensionException;
import org.opengis.parameter.ParameterValueGroup;

import org.opengis.referencing.NoSuchAuthorityCodeException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.MathTransform;
import static org.geotoolkit.process.jts.union.UnionDescriptor.*;
import static org.geotoolkit.parameter.Parameters.*;
import org.opengis.referencing.operation.TransformException;
import org.opengis.util.FactoryException;
/**
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
            
            final CoordinateReferenceSystem crs1 = JTS.findCoordinateReferenceSystem(geom1);
            final CoordinateReferenceSystem crs2 = JTS.findCoordinateReferenceSystem(geom2);
            
            CoordinateReferenceSystem resultCRS = null;
            
            if(crs1 != null){
                resultCRS = crs1;
                if(crs2 != null){
                   if(!(crs1.equals(crs2))){
                        //Conversion
                        final MathTransform mt = CRS.findMathTransform(crs2, crs1);
                        geom2 = JTS.transform(geom2, mt);
                        resultCRS = crs1;
                   }
                }
            }else{
                if(crs2 != null){
                    resultCRS = crs2;
                }
            }
            
            
            result = (Geometry) geom1.union(geom2);
            if(resultCRS != null){
                JTS.setCRS(result, resultCRS);
            }
            
            
            getOrCreate(RESULT_GEOM, outputParameters).setValue(result); 
            
        } catch (MismatchedDimensionException ex) {
            Logger.getLogger(UnionProcess.class.getName()).log(Level.SEVERE, null, ex);
        } catch (TransformException ex) {
            Logger.getLogger(UnionProcess.class.getName()).log(Level.SEVERE, null, ex);
        } catch (NoSuchAuthorityCodeException ex) {
            Logger.getLogger(UnionProcess.class.getName()).log(Level.SEVERE, null, ex);
        } catch (FactoryException ex) {
            Logger.getLogger(UnionProcess.class.getName()).log(Level.SEVERE, null, ex);
        }
        
         return outputParameters;
    }
    
}
