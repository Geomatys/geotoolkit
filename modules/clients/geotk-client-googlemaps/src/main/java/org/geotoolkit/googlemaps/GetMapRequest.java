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
package org.geotoolkit.googlemaps;

import java.awt.Dimension;
import org.geotoolkit.client.Request;
import org.opengis.geometry.DirectPosition;

/**
 * Map request for static google maps api.
 * 
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public interface GetMapRequest extends Request{
    
    public static final String TYPE_ROADMAP     = "roadmap";
    public static final String TYPE_SATELLITE   = "satellite";
    public static final String TYPE_TERRAIN     = "terrain";
    public static final String TYPE_HYBRID      = "hybrid";
        
    /** png8 or png (default) specifies the 8-bit PNG format.*/
    public static final String FORMAT_PNG8 = "png";
    /** png32 specifies the 32-bit PNG format. */
    public static final String FORMAT_PNG32 = "png32";
    /** gif specifies the GIF format. */
    public static final String FORMAT_GIF = "gif";
    /** jpg specifies the JPEG compression format. */
    public static final String FORMAT_JPG = "jpg";
    /** jpg-baseline specifies a non-progressive JPEG compression format. */
    public static final String FORMAT_JPG_BASELINE = "jpg-baseline";
    
    
    /**
     * Returns the map type of the request.
     * The map type define the datas which will be displayed on the map.
     * Use one of the TYPE_* constants.
     */
    String getMapType();
    
    /**
     * Sets the maptype of the request. Must be set.
     */
    void setMapType(String maptype);
    
    /**
     * Returns the zoom level of the request.
     */
    int getZoom();
    
    /**
     * Sets the zoom level of the request. Must be set.
     */
    void setZoom(int zoom);
    
    /**
     * Returns the center of the request.
     */
    DirectPosition getCenter();
    
    /**
     * Sets the center of the request. Must be set.
     * The coordinate will be translated in lat/lon later for the query.
     */
    void setCenter(DirectPosition position);
    
    /**
     * Returns the output dimension to request, never {@code null}.
     */
    Dimension getDimension();

    /**
     * Sets the output dimension to request. Must be set.
     */
    void setDimension(Dimension dim);
    
    /**
     * Returns the output request format.
     */
    String getFormat();
    
    /**
     * Sets the output request format. Must be set.
     */
    void setFormat(String format);
    
}
