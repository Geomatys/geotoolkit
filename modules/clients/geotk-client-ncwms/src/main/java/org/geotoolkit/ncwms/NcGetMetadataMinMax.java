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
import org.geotoolkit.util.logging.Logging;


/**
 * Implementation of {@link NcGetMetadataMinMaxRequest}, which defines the parameters for
 * a GetMetadata?item=minmax request.
 *
 * @author Olivier Terral (Geomatys)
 * @module pending
 */
public class NcGetMetadataMinMax extends NcGetMetadata implements NcGetMetadataMinMaxRequest {
    
    /**
     * Default logger for all GetMetadata requests.
     */
    protected static final Logger LOGGER = Logging.getLogger(NcGetMetadataMinMax.class);
    
    private String crs = null;
    
    private String bbox = null;
    
    private String width = null;
    
    private String height = null;

    /**
     * {@inheritDoc}
     */
    protected NcGetMetadataMinMax(final String serverURL) {
        super(serverURL);
    }    

    
    /**
     * {@inheritDoc}
     */
    @Override
    protected void prepareParameters() {
        super.prepareParameters();        
        requestParameters.put("crs", getCrs());
        requestParameters.put("bbox", getBbox());
        requestParameters.put("width", getWidth());
        requestParameters.put("height", getHeight());
    }

    /**
     * @return the crs
     */
    @Override
    public String getCrs() {
        return crs;
    }

    /**
     * @param crs the crs to set
     */
    @Override
    public void setCrs(String crs) {
        this.crs = crs;
    }

    /**
     * @return the bbox
     */
    @Override
    public String getBbox() {
        return bbox;
    }

    /**
     * @param bbox the bbox to set
     */
    @Override
    public void setBbox(String bbox) {
        this.bbox = bbox;
    }

    /**
     * @return the width
     */
    @Override
    public String getWidth() {
        return width;
    }

    /**
     * @param width the width to set
     */
    @Override
    public void setWidth(String width) {
        this.width = width;
    }

    /**
     * @return the height
     */
    @Override
    public String getHeight() {
        return height;
    }

    /**
     * @param height the height to set
     */
    @Override
    public void setHeight(String height) {
        this.height = height;
    }

}
