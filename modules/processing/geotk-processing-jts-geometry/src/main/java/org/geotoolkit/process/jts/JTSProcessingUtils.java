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
package org.geotoolkit.process.jts;

import com.vividsolutions.jts.geom.Geometry;
import org.geotoolkit.geometry.jts.JTS;
import org.geotoolkit.referencing.CRS;
import org.geotoolkit.util.ArgumentChecks;
import org.opengis.geometry.MismatchedDimensionException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.MathTransform;
import org.opengis.referencing.operation.TransformException;
import org.opengis.util.FactoryException;

/**
 *
 * @author Quentin Boileau (Geomatys)
 */
public class JTSProcessingUtils {


    private JTSProcessingUtils() {
    }
    
    /**
     * This method return the common CoordinateReferenceSystem of two geometries.
     * If first geometry has a CRS, it'll be returned.
     * If second geometry has a CRS AND the first one CRS is null,  it's the second geometry CRS that will be returned.
     * If first and second geometries CRS are null, null CRS will be returned.
     * @param geom1
     * @param geom2 
     * @return the CRS keeped for the geometries.
     * @throws FactoryException
     * @throws TransformException 
     */
    public static CoordinateReferenceSystem getCommonCRS(final Geometry geom1, final Geometry geom2) throws FactoryException, TransformException{
        
        CoordinateReferenceSystem resultCRS = null;
        
        //get geometies CRS
        final CoordinateReferenceSystem crs1 = JTS.findCoordinateReferenceSystem(geom1);
        final CoordinateReferenceSystem crs2 = JTS.findCoordinateReferenceSystem(geom2);
        
        //crs1 exist
        if(crs1 != null){
            resultCRS = crs1;
            
        }else{
            
            //crs1 == null and crs2 exist
            if(crs2 != null){
                resultCRS = crs2;
            }
        }
        
        return resultCRS;
    }
    
    /**
     * This utility method convert a geometry into a different CoordinateReferenceSystem.
     * If the geometry crs is not defined, the geometry will be returned without transformation.
     * @param geom a geometry to convert. (Not null)
     * @param crsTarget the target CoordinateReferenceSystem (Not null)
     * @return the geometry converted with targetCRS as geometry CRS.
     * @throws MismatchedDimensionException
     * @throws TransformException
     * @throws FactoryException 
     */
    public static Geometry convertToCRS(final Geometry geom, final CoordinateReferenceSystem crsTarget) 
            throws MismatchedDimensionException, TransformException, FactoryException{
        ArgumentChecks.ensureNonNull("geometry", geom);
        ArgumentChecks.ensureNonNull("crsTarget", crsTarget);
        
        //get geometry CRS
        final CoordinateReferenceSystem crsGeom = JTS.findCoordinateReferenceSystem(geom);
        if(crsGeom == null){
            return geom;
        }
        
        //convert geometry
        Geometry result = null;
        final MathTransform mt = CRS.findMathTransform(crsGeom, crsTarget);
        result = JTS.transform(geom, mt);
        JTS.setCRS(result, crsTarget);
        
        return result;
    }
    
    /**
     * This method check if two geometries have a different CRS.
     * @param geom1
     * @param geom2
     * @return true if geom1 and geom2 have different CRS.
     * @throws FactoryException 
     */
    public static boolean isConversionNeeded(final Geometry geom1, final Geometry geom2) throws FactoryException{
        
        final CoordinateReferenceSystem crs1 = JTS.findCoordinateReferenceSystem(geom1);
        final CoordinateReferenceSystem crs2 = JTS.findCoordinateReferenceSystem(geom2);
        
        //if crs1 and crs2 are different and not null
        if(crs1 != null && crs2 != null && (!crs1.equals(crs2))){
            return true;
        }
        return false;
        
    }
    
}
