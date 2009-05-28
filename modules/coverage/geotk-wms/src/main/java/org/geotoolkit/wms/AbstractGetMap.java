/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2009, Geomatys
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
import org.opengis.geometry.Envelope;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public abstract class AbstractGetMap extends AbstractRequest implements GetMapRequest{

    protected final String version;
    protected final HashMap<String,String> dims = new HashMap<String, String>();
    protected String format = "image/png";
    protected String exception = "application/vnd.ogc.se_inimage";
    protected String[] layers = null;
    protected String[] styles = null;
    protected Envelope enveloppe = null;
    protected Dimension dimension = null;
    protected String sld = null;
    protected String sldBody = null;
    protected Boolean transparent = true;

    protected AbstractGetMap(String serverURL,String version){
        super(serverURL);
        this.version = version;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public String[] getLayers() {
        return layers;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void setLayers(String... layers) {
        this.layers = layers;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public Envelope getEnvelope() {
        return enveloppe;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void setEnvelope(Envelope env) {
        this.enveloppe = env;
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
    public String[] getStyles() {
        return styles;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void setStyles(String... styles) {
        this.styles = styles;
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
    public boolean getTransparent(){
        return transparent;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void setTransparent(boolean transparent){
        this.transparent = transparent;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public Map<String,String> dimensions(){
        return dims;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public URL getURL() throws MalformedURLException {
        if(layers == null || layers.length == 0){
            throw new IllegalArgumentException("Layers are not defined");
        }
        if(dimension == null){
            throw new IllegalArgumentException("Dimension is not defined");
        }

        requestParameters.put("SERVICE",    "WMS");
        requestParameters.put("REQUEST",    "GetMap");
        requestParameters.put("VERSION",    version);
        requestParameters.put("EXCEPTIONS", exception);
        requestParameters.put("FORMAT",     format);
        requestParameters.put("WIDTH",      String.valueOf(dimension.width));
        requestParameters.put("HEIGHT",     String.valueOf(dimension.height));
        requestParameters.put("LAYERS",     toString(layers));
        requestParameters.put("STYLES",     toString(styles));
        requestParameters.put("TRANSPARENT", Boolean.toString(transparent).toUpperCase());

        if (sld != null) {
            requestParameters.put("SLD",sld);
        }
        if (sldBody != null) {
            requestParameters.put("SLD_BODY",sldBody);
        }

        requestParameters.putAll(dims);
        requestParameters.putAll(toString(enveloppe));

        return super.getURL();
    }

    private String toString(String[] vars){
        if(vars == null || vars.length == 0) return "";

        final StringBuilder sb = new StringBuilder();
        int i=0;
        for(;i<vars.length-1;i++){
            sb.append(vars[i]).append(',');
        }
        sb.append(vars[i]);

        return sb.toString();
    }

    protected abstract Map<String,String> toString(Envelope env);

}
