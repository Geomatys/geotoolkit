/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2012, Geomatys
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
package org.geotoolkit.filter.visitor;

import com.vividsolutions.jts.geom.Geometry;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.geotoolkit.geometry.DefaultBoundingBox;
import org.geotoolkit.geometry.jts.JTS;
import org.opengis.filter.expression.Literal;
import org.opengis.geometry.BoundingBox;
import org.opengis.referencing.NoSuchAuthorityCodeException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.util.FactoryException;

/**
 * Used to clean PropertyEqualsTo on identifiers.
 * 
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public class FillCrsVisitor extends DuplicatingFilterVisitor{
    
    public static final FillCrsVisitor VISITOR = new FillCrsVisitor();

    @Override
    public Object visit(Literal expression, Object extraData) {
        final CoordinateReferenceSystem crs = (CoordinateReferenceSystem) extraData;
        Object obj = expression.getValue();
        if(obj instanceof BoundingBox){
            BoundingBox bbox = (BoundingBox) obj;
            if(bbox.getCoordinateReferenceSystem() == null){
                obj = new DefaultBoundingBox(bbox,crs);
            }
        }else if(obj instanceof Geometry){
            try {
                Geometry geo = (Geometry) obj;
                geo = (Geometry) geo.clone();
                if(JTS.findCoordinateReferenceSystem(geo) == null){
                    JTS.setCRS(geo, crs);
                }
                obj = geo;
            } catch (NoSuchAuthorityCodeException ex) {
                Logger.getLogger(FillCrsVisitor.class.getName()).log(Level.SEVERE, null, ex);
            } catch (FactoryException ex) {
                Logger.getLogger(FillCrsVisitor.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
        return getFactory(extraData).literal(obj);
    }
    
}
