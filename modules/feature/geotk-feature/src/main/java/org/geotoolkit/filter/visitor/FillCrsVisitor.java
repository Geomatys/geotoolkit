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

import org.locationtech.jts.geom.Geometry;
import java.util.logging.Level;
import org.geotoolkit.geometry.BoundingBox;
import org.geotoolkit.geometry.jts.JTS;
import org.opengis.filter.expression.Literal;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.util.FactoryException;
import org.apache.sis.util.logging.Logging;

/**
 * Used to clean PropertyEqualsTo on identifiers.
 *
 * @author Johann Sorel (Geomatys)
 * @module
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
                obj = new BoundingBox(bbox,crs);
            }
        }else if(obj instanceof Geometry){
            try {
                Geometry geo = (Geometry) obj;
                geo = (Geometry) geo.clone();
                if(JTS.findCoordinateReferenceSystem(geo) == null){
                    JTS.setCRS(geo, crs);
                }
                obj = geo;
            } catch (FactoryException ex) {
                Logging.getLogger("org.geotoolkit.filter.visitor").log(Level.SEVERE, null, ex);
            }
        }

        return getFactory(extraData).literal(obj);
    }

}
