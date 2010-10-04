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
import java.util.logging.Logger;
import org.geotoolkit.client.AbstractRequest;
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
    protected Boolean transparent = true;
    protected String rule = null;
    protected String sldVersion = null;

    /**
     * Defines the server url and the service version for this kind of request.
     *
     * @param serverURL The server url.
     * @param version The version of the request.
     */
    protected AbstractGetLegend(String serverURL,String version){
        super(serverURL);
        this.version = version;
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
    public void setLayer(String layer) {
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
    public void setDimension(Dimension dim) {
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
    public void setFormat(String format) {
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
    public void setExceptions(String ex) {
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
    public void setRule(String rule) {
        this.rule = rule;
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
    public void setStyle(String style) {
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
    public void setSld(String sld){
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
    public void setSldVersion(String sldVersion) {
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
    public void setSldBody(String sldBody){
        this.sldBody = sldBody;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public URL getURL() throws MalformedURLException {
        if (layer == null) {
            throw new IllegalArgumentException("Layer is not defined");
        }
        if (format == null) {
            throw new IllegalArgumentException("Format is not defined");
        }
        requestParameters.put("LAYER",      layer);
        requestParameters.put("SERVICE",    "WMS");
        requestParameters.put("REQUEST",    "GetLegendGraphic");
        requestParameters.put("VERSION",    version);
        requestParameters.put("FORMAT",     format);
        if (exception != null) {
            requestParameters.put("EXCEPTIONS", exception);
        }

        if (dimension != null) {
            requestParameters.put("WIDTH",      String.valueOf(dimension.width));
            requestParameters.put("HEIGHT",     String.valueOf(dimension.height));
        }
        if (style != null) {
            requestParameters.put("STYLE", style);
        }
        // SLD_VERSION is mandatory when SLD is given.
        if (sld != null && sldVersion != null) {
            requestParameters.put("SLD", sld);
            requestParameters.put("SLD_VERSION", sldVersion);
        }
        if (rule != null) {
            requestParameters.put("RULE", rule);
        }
        if (sldBody != null) {
            requestParameters.put("SLD_BODY", sldBody);
        }

        return super.getURL();
    }

    private String toString(String[] vars){
        if(vars == null || vars.length == 0) return "";

        if(vars.length == 1 && vars[0] == null){
            return "";
        }

        final StringBuilder sb = new StringBuilder();
        int i=0;
        for(;i<vars.length-1;i++){
            sb.append(vars[i]).append(',');
        }
        sb.append(vars[i]);

        return sb.toString();
    }

}
