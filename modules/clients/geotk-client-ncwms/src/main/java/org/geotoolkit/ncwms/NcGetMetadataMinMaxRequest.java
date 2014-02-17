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
package org.geotoolkit.ncwms;

import org.geotoolkit.client.Request;


/**
 * Interface for GetMetadata requests
 * 
 * @author Olivier Terral (Geomatys)
 * @module pending
 */
public interface NcGetMetadataMinMaxRequest extends NcGetMetadataRequest {    
    
    /**
     * Returns the crs code name.
     */
    String getCrs();
    
    /**
     * Sets the crs code .
     */
    void setCrs(final String crsCode);
    
    /**
     * Returns the current bbox: minx,miny,max,maxy
     */
    String getBbox();

    /**
     * Sets the bbox .   
     */
    void setBbox(final String bbox);
    
    /**
     * Returns the width.
     */
    String getWidth();
    
    /**
     * Sets the width.
     */
    void setWidth(final String width);
    
    
    /**
     * Returns the height.
     */
    String getHeight();
    
    /**
     * Sets the height.
     */
    void setHeight(final String height);

}
