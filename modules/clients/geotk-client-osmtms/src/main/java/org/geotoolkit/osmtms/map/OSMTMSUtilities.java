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
package org.geotoolkit.osmtms.map;

import org.geotoolkit.geometry.DirectPosition2D;
import org.geotoolkit.geometry.GeneralEnvelope;
import org.geotoolkit.referencing.CRS;
import org.geotoolkit.referencing.crs.DefaultGeographicCRS;
import org.opengis.geometry.DirectPosition;
import org.opengis.geometry.Envelope;
import org.opengis.referencing.NoSuchAuthorityCodeException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.MathTransform;
import org.opengis.referencing.operation.TransformException;
import org.opengis.util.FactoryException;

/**
 *
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public final class OSMTMSUtilities {
    
    public static final double BASE_TILE_SIZE = 256d;
    
    public static final CoordinateReferenceSystem GOOGLE_MERCATOR;
    public static final Envelope MERCATOR_EXTEND;
        
    static {
        try {
            GOOGLE_MERCATOR = CRS.decode("EPSG:3857");
            
            //X goes from 0 (left edge is 180 °W) to 2^zoom -1 (right edge is 180 °E) 
            //Y goes from 0 (top edge is 85.0511 °N) to 2^zoom -1 (bottom edge is 85.0511 °S) in a Mercator projection
            MERCATOR_EXTEND = new GeneralEnvelope(GOOGLE_MERCATOR);
            ((GeneralEnvelope)MERCATOR_EXTEND).setRange(0, -20037508.342789244d, 20037508.342789244d);
            ((GeneralEnvelope)MERCATOR_EXTEND).setRange(1, -20037508.342789244d, 20037508.342789244d);
            
            
        } catch (NoSuchAuthorityCodeException ex) {
            throw new RuntimeException(ex);
        } catch (FactoryException ex) {
            throw new RuntimeException(ex);
        }       
    }
    
    private OSMTMSUtilities(){}
    
}
