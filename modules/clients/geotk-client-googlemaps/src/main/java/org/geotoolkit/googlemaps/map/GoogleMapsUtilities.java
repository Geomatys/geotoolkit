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
package org.geotoolkit.googlemaps.map;

import java.awt.image.BufferedImage;
import java.io.IOException;
import javax.imageio.ImageIO;

import org.geotoolkit.geometry.GeneralDirectPosition;
import org.geotoolkit.geometry.GeneralEnvelope;
import org.geotoolkit.referencing.CRS;

import org.opengis.geometry.DirectPosition;
import org.opengis.geometry.Envelope;
import org.opengis.referencing.NoSuchAuthorityCodeException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.util.FactoryException;

/**
 *
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public final class GoogleMapsUtilities {

    public static final int BASE_TILE_SIZE = 256;
    
    public static final CoordinateReferenceSystem GOOGLE_MERCATOR;
    public static final Envelope MERCATOR_EXTEND;
    public static final BufferedImage OVERLOAD;
    

    /**
     * Resolution at zoom 0 level. in meter by pixel.
     */
    public static final double ZOOM_ZERO_RESOLUTION;
    
    static {
        try {
            GOOGLE_MERCATOR = CRS.decode("EPSG:3857");
        } catch (NoSuchAuthorityCodeException ex) {
            throw new RuntimeException(ex);
        } catch (FactoryException ex) {
            throw new RuntimeException(ex);
        }
        
        
        //this is not an exact extent, google once again do not respect anything
        //MERCATOR_EXTEND = CRS.getEnvelope(GoogleMapsUtilities.GOOGLE_MERCATOR);
        MERCATOR_EXTEND = new GeneralEnvelope(GOOGLE_MERCATOR);
        ((GeneralEnvelope)MERCATOR_EXTEND).setRange(0, -2e7, 2e7);
        ((GeneralEnvelope)MERCATOR_EXTEND).setRange(1, -2e7, 2e7);
        
        ZOOM_ZERO_RESOLUTION = MERCATOR_EXTEND.getSpan(0) / BASE_TILE_SIZE;
        try {
            OVERLOAD = ImageIO.read(GoogleMapsUtilities.class.getResourceAsStream("/org/geotoolkit/googlemaps/staticmap.png"));
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }
    
    private GoogleMapsUtilities() {
    }
        
    /**
     * Returns resolution at zoom given level. in meter by pixel.
     */
    public static double getZoomResolution(final int zoom){
        return ZOOM_ZERO_RESOLUTION * Math.pow(0.5d, zoom);
    }
            
    public static DirectPosition getCenter(final int zoom, final int col, final int row){        
        final GeneralDirectPosition position = new GeneralDirectPosition(GOOGLE_MERCATOR);
        
        //we look for the center, like searching for a corner if we had two times more cells
        final double zoomRes = getZoomResolution(zoom + 1);
        position.setOrdinate(0, MERCATOR_EXTEND.getMinimum(0) + zoomRes* BASE_TILE_SIZE * ( (col)*2 + 1) );
        position.setOrdinate(1, MERCATOR_EXTEND.getMaximum(1) - zoomRes* BASE_TILE_SIZE * ( (row)*2 + 1) );
        
        return position;
    }
    
}
