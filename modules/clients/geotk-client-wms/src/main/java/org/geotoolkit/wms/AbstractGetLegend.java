/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2009-2010, Geomatys
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
package org.geotoolkit.wms;

import java.awt.Dimension;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;
import org.geotoolkit.client.AbstractRequest;
import org.geotoolkit.util.StringUtilities;
import org.geotoolkit.util.logging.Logging;


/**
 * Abstract implementation of {@link GetLegendRequest}, which defines the
 * parameters for a GetLegendGraphic request.
 *
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public abstract class AbstractGetLegend extends AbstractRequest implements GetLegendRequest{
    /**
     * Default logger for all GetLegendGraphic requests.
     */
    protected static final Logger LOGGER = Logging.getLogger(AbstractGetLegend.class);

    /**
     * The version to use for this webservice request.
     */
    protected final String version;
    protected String format = "image/png";
    protected String exception = "application/vnd.ogc.se_inimage";
    protected String layer = null;
    protected String style = null;
    protected Dimension dimension = null;
    protected String sld = null;
    protected String sldBody = null;
    protected String rule = null;
    protected Double scale = null;
    protected String sldVersion = null;
    protected final Map<String, String> dims = new HashMap<String, String>();

    /**
     * Defines the server url and the service version for this kind of request.
     *
     * @param serverURL The server url.
     * @param version The version of the request.
     */
    protected AbstractGetLegend(final String serverURL,final String version){
        super(serverURL);
        this.version = version;
    }    
    
    /**
     * {@inheritDoc }
     */
    @Override
    public Map<String, String> dimensions() {
        return dims;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public String getLayer() {
        return layer;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void setLayer(final String layer) {
        this.layer = layer;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public Dimension getDimension() {
        return dimension;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void setDimension(final Dimension dim) {
        this.dimension = dim;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public String getFormat() {
        return format;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void setFormat(final String format) {
        this.format = format;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public String getExceptions() {
        return exception;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void setExceptions(final String ex) {
        this.exception = ex;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public String getRule() {
        return rule;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void setRule(final String rule) {
        this.rule = rule;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public Double getScale() {
        return scale;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void setScale(final Double scale) {
        this.scale = scale;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public String getStyle() {
        return style;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void setStyle(final String style) {
        this.style = style;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public String getSld(){
        return sld;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void setSld(final String sld){
        this.sld = sld;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public String getSldVersion() {
        return sldVersion;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void setSldVersion(final String sldVersion) {
        this.sldVersion = sldVersion;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public String getSldBody(){
        return sldBody;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void setSldBody(final String sldBody){
        this.sldBody = sldBody;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public URL getURL() throws MalformedURLException {
        
        if (layer == null)
            throw new IllegalArgumentException("Layer is not defined");
        
        
        if (format == null)
            throw new IllegalArgumentException("Format is not defined");
        
        
        if (version == null)
            throw new IllegalArgumentException("WMS version is not defined");
        
        
        // Mandatory parameters
        requestParameters.put("SERVICE",    "WMS");
        requestParameters.put("VERSION",    version);
        requestParameters.put("REQUEST",    "GetLegendGraphic");
        requestParameters.put("LAYER",      layer);
        
        
        // Add optional parameters 
        if (format != null)
            requestParameters.put("FORMAT",     format);
        
        if (exception != null)
            requestParameters.put("EXCEPTIONS", exception);        

        if (dimension != null) {
            requestParameters.put("WIDTH",      String.valueOf(dimension.width));
            requestParameters.put("HEIGHT",     String.valueOf(dimension.height));
        }
        
        
        // Add one style parameter       
        String styleParam = null; 
        String styleValue = null;
        
        if (sldBody != null) {
            styleParam = "SLD_BODY";
            styleValue = sldBody;
            
        } else if (sld != null) {
            styleParam = "SLD";
            styleValue = sld;        
        
        } else if (style != null ) {
            styleParam = "STYLE";
            styleValue = style;                 
        }
        
        if (styleParam != null && styleValue != null)
            requestParameters.put(styleParam, styleValue);
        
        if (sldVersion != null)
            requestParameters.put("SLD_VERSION", sldVersion);        
        
        if (rule != null) {
            requestParameters.put("RULE", rule);
        }
        if (scale != null) {
            requestParameters.put("SCALE", scale.toString());
        }

        // Add optional dimensions parameters
        if (dims != null && !dims.isEmpty()) {
            requestParameters.putAll(dims);
        }
        
        return super.getURL();
    }

}
