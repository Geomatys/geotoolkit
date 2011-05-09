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
public interface NcGetMetadataRequest extends Request {    
    
    /**
     * Returns the layer name.
     */
    String getLayerName();
    
    /**
     * Sets the layer name.
     */
    void setLayerName(final String name);
    
    /**
     * Returns an id representing which informations you want.
     */
    String getItem();

    /**
     * Sets the type of the GetMetadata request.    
     * 
     * @param item The type of the GetMetadata request. Possible values
     * are 'menu', 'layerDetails', 'timesteps', 'minmax', 'animationTimesteps'
     */
    void setItem(final String item);
    
    /**
     * Returns the day in ISO8601 format.
     */
    String getDay();
    
    /**
     * Sets the day in ISO8601 format.
     */
    void setDay(final String day);
    
    
    /**
     * Returns the start date of an animation in ISO8601 format.
     */
    String getStart();
    
    /**
     * Sets the start date of an animation in ISO8601 format.
     */
    void setStart(final String start);
    
    /**
     * Returns the end date of an animation in ISO8601 format.
     */
    String getEnd();
    
    /**
     * Sets the end date of an animation in ISO8601 format.
     */
    void setEnd(final String end);
    
    /**
     * Returns the Time in ISO8601 format.
     */
    String getTime();
    
    /**
     * Sets the Time in ISO8601 format.
     */
    void setTime(final String time);
    

}
