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
 * Implementation of {@link NcGetTransectRequest}, which defines the parameters for
 * a GetTransect request.
 *
 * @author Olivier Terral (Geomatys)
 * @module pending
 */
public class NcGetTransect extends AbstractRequest implements NcGetTransectRequest {
    
    /**
     * Default logger for all GetVerticalProfile requests.
     */
    protected static final Logger LOGGER = Logging.getLogger(NcGetTransect.class);
    
    private String layer = null;
    
    private String crs = null;
    
    private String lineString = null;
    
    private String format = null;
    
    private String time = null;
    
    private String elevation = null;

    /**
     * {@inheritDoc}
     */
    protected NcGetTransect(final String serverURL) {
        super(serverURL);
    }    

    /**
     * {@inheritDoc}
     */
    @Override
    public String getLayer() {
        return layer;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setLayer(final String name) {
        this.layer = name;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getCrs() {
        return crs;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setCrs(final String crsCode) {
        this.crs = crsCode;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getLineString() {
        return lineString;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setLineString(final String lineString) {
        this.lineString = lineString;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getFormat() {
        return format;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setFormat(final String format) {
        this.format = format;
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
    public String getElevation() {
        return time;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setElevation(final String elevation) {
        this.elevation = elevation;
    }
    
    
    /**
     * {@inheritDoc}
     */
    @Override
    protected void prepareParameters() {
        super.prepareParameters();

        // Tests if the mandatory parameters are available
        if (layer == null)
            throw new IllegalArgumentException("Must provide a LAYER parameter");    
        
        if (crs == null)
            throw new IllegalArgumentException("Must provide an CRS parameter");
        
        if (lineString == null)
            throw new IllegalArgumentException("Must provide an LINESTRING parameter");
        
        if (format == null)
            throw new IllegalArgumentException("Must provide an FORMAT parameter");
        
        requestParameters.put("REQUEST", "GetTransect");
        requestParameters.put("LAYER", layer);
        requestParameters.put("CRS", crs);
        requestParameters.put("LINESTRING", lineString);
        requestParameters.put("FORMAT", format);
        
        if (time != null)
            requestParameters.put("TIME", time);
        
        if (elevation != null)
            requestParameters.put("ELEVATION", elevation);
        
    }
}
