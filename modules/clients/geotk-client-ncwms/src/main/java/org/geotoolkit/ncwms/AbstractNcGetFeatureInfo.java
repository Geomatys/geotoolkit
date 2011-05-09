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
import org.geotoolkit.wms.AbstractGetFeatureInfo;


/**
 * Abstract implementation of {@link NcGetFeatureInfoRequest}, which defines the parameters for
 * a GetFeatureInfo request.
 * 
 * @author Olivier Terral (Geomatys)
 * @module pending
 */
public abstract class AbstractNcGetFeatureInfo extends AbstractGetFeatureInfo implements  NcGetFeatureInfoRequest {
    
    /**
     * Default logger for all GetFeatureInfo requests.
     */
    protected static final Logger LOGGER = Logging.getLogger(AbstractNcGetFeatureInfo.class);
    
    protected Integer opacity = null;
    
    protected String colorScaleRange = null;
    
    protected Integer numColorBands = null;
    
    protected Boolean logScale = null;

    /**
     * {@inheritDoc}
     */
    protected AbstractNcGetFeatureInfo(final String serverURL, final String version) {
        super(serverURL, version);
    }    
    
    /**
     * {@inheritDoc }
     */
    @Override
    public Integer getOpacity() {
        return opacity;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void setOpacity(final Integer opacity) {
        this.opacity = opacity;
    }
    
    /**
     * {@inheritDoc }
     */
    @Override
    public String getColorScaleRange() {
        return colorScaleRange;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void setColorScaleRange(final String colorScaleRange) {
        this.colorScaleRange = colorScaleRange;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public Integer getNumColorBands() {
        return numColorBands;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void setNumColorBands(final Integer numColorBands) {
        this.numColorBands = numColorBands;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public Boolean isLogScale() {
        return logScale;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void setLogScale(final Boolean logScale) {
        this.logScale = logScale;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    protected void prepareParameters() {
        super.prepareParameters();
        
        if (opacity != null)
            requestParameters.put("OPACITY", String.valueOf(opacity));
        
        if (colorScaleRange != null)
            requestParameters.put("COLORSCALERANGE", colorScaleRange);
        
        if (numColorBands != null)
            requestParameters.put("NUMCOLORBANDS", String.valueOf(numColorBands));
        
        if (logScale != null)
            requestParameters.put("LOGSCALE", String.valueOf(logScale));

    }
}
