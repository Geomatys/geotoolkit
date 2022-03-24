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

import org.apache.sis.geometry.GeneralEnvelope;
import org.locationtech.jts.geom.Geometry;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.sis.internal.filter.FunctionNames;
import org.geotoolkit.geometry.jts.JTS;
import org.opengis.filter.Literal;
import org.opengis.geometry.Envelope;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.util.FactoryException;

/**
 * Used to clean PropertyEqualsTo on identifiers.
 *
 * @author Johann Sorel (Geomatys)
 *
 * @deprecated Not used anymore.
 */
@Deprecated
public class FillCrsVisitor extends DuplicatingFilterVisitor {

    public FillCrsVisitor(final CoordinateReferenceSystem crs) {
        setExpressionHandler(FunctionNames.Literal, (e) -> {
            final Literal<Object,?> expression = (Literal<Object,?>) e;
            Object obj = expression.getValue();
            if (obj instanceof Envelope bbox) {
                if (bbox.getCoordinateReferenceSystem() == null) {
                    obj = new GeneralEnvelope(bbox);
                    ((GeneralEnvelope) obj).setCoordinateReferenceSystem(crs);
                }
            } else if (obj instanceof Geometry) {
                try {
                    Geometry geo = (Geometry) obj;
                    geo = (Geometry) geo.clone();
                    if (JTS.findCoordinateReferenceSystem(geo) == null) {
                        JTS.setCRS(geo, crs);
                    }
                    obj = geo;
                } catch (FactoryException ex) {
                    Logger.getLogger("org.geotoolkit.filter.visitor").log(Level.SEVERE, null, ex);
                }
            }
            return ff.literal(obj);
        });
    }
}
