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

import java.util.logging.Logger;
import org.geotoolkit.client.AbstractRequest;

import org.geotoolkit.util.logging.Logging;


/**
 * Implementation of {@link NcGetMetadataRequest}, which defines the parameters for
 * a GetMetadata request.
 *
 * @author Olivier Terral (Geomatys)
 * @module pending
 */
public class NcGetMetadata extends AbstractRequest implements NcGetMetadataRequest {
    
    /**
     * Default logger for all GetMetadata requests.
     */
    protected static final Logger LOGGER = Logging.getLogger(NcGetMetadata.class);
    
    private String layerName = null;
    
    private String item = null;
    
    private String day = null;
    
    private String start = null;
    
    private String end = null;
    
    private String time = null;

    /**
     * {@inheritDoc}
     */
    protected NcGetMetadata(final String serverURL) {
        super(serverURL);
    }    

    /**
     * {@inheritDoc}
     */
    @Override
    public String getLayerName() {
        return layerName;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setLayerName(final String name) {
        layerName = name;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public String getItem() {
        return item;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setItem(final String item) {
        this.item = item;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getDay() {
        return day;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void setDay(final String day) {
        this.day = day;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getStart() {
        return start;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setStart(final String start) {
        this.start = start;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getEnd() {
        return end;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setEnd(final String end) {
        this.end = end;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public String getTime() {
        return time;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setTime(final String time) {
        this.time = time;
    }
    
    
    /**
     * {@inheritDoc}
     */
    @Override
    protected void prepareParameters() {
        super.prepareParameters();

        // Tests if the mandatory parameters are available          
        if (item == null) {
            throw new IllegalArgumentException("Must provide an item parameter");
            
        } else if (item.equals("layerDetails")) {
            
            if (time != null)            
                requestParameters.put("time", time);
            
        } else if (item.equals("animationTimesteps")) {
            
            if (start == null)
                throw new IllegalArgumentException("Must provide a start parameter");   
            
            if (end == null)
                throw new IllegalArgumentException("Must provide a end parameter"); 
            
            requestParameters.put("start", start);
            requestParameters.put("end", end);
            
        } else if (item.equals("timesteps")) {
            
            if (day == null)
                throw new IllegalArgumentException("Must provide a day parameter");   
            
            requestParameters.put("day", day);
            
        } else if (!item.equals("menu") && !item.equals("minmax")) {
            throw new IllegalArgumentException("Invalid value for item parameter");             
        }       
        
        requestParameters.put("request", "GetMetadata");
        requestParameters.put("item", item);
        
        if (!item.equals("menu")) {
            
            if (layerName == null)
                throw new IllegalArgumentException("Must provide a layerName parameter"); 
            
            requestParameters.put("layerName", layerName);
        }
    }
}
