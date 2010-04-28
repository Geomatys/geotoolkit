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
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;
import org.geotoolkit.client.AbstractRequest;
import org.geotoolkit.util.StringUtilities;
import org.geotoolkit.util.logging.Logging;
import org.opengis.geometry.Envelope;
import org.opengis.referencing.cs.AxisDirection;
import org.opengis.referencing.cs.CoordinateSystem;
import org.opengis.referencing.cs.CoordinateSystemAxis;


/**
 * Abstract implementation of {@link GetMapRequest}, which defines the parameters for
 * a GetMap request.
 *
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public abstract class AbstractGetMap extends AbstractRequest implements GetMapRequest{
    /**
     * Default logger for all GetMap requests.
     */
    protected static final Logger LOGGER = Logging.getLogger(AbstractGetMap.class);

    /**
     * The version to use for this webservice request.
     */
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

    /**
     * Defines the server url and the service version for this kind of request.
     *
     * @param serverURL The server url.
     * @param version The version of the request.
     */
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
        requestParameters.put("LAYERS",     StringUtilities.toCommaSeparatedValues(layers));
        if(styles != null && styles.length>0 && styles[0] != null){
            requestParameters.put("STYLES",StringUtilities.toCommaSeparatedValues(styles));
        }else{
            requestParameters.put("STYLES","");
        }
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

    protected abstract Map<String,String> toString(Envelope env);

    protected void encodeTimeAndElevation(Envelope env, Map<String,String> map){
        //append time and elevation parameter
        final CoordinateSystem cs = env.getCoordinateReferenceSystem().getCoordinateSystem();
        for(int i=0, n=cs.getDimension(); i<n; i++){
            final CoordinateSystemAxis axis = cs.getAxis(i);
            final AxisDirection ad = axis.getDirection();
            if(ad.equals(AxisDirection.FUTURE) || ad.equals(AxisDirection.PAST)){
                //found a temporal axis
                final double minT = env.getMinimum(i);
                final double maxT = env.getMaximum(i);

                if(Double.isNaN(minT) || Double.isInfinite(minT)){
                    if(Double.isNaN(maxT) || Double.isInfinite(maxT)){
                        //both null, do nothing
                    }else{
                        //only max limit
                        map.put("TIME", String.valueOf(maxT));
                    }
                }else if(Double.isNaN(maxT) || Double.isInfinite(maxT)){
                    if(Double.isNaN(minT) || Double.isInfinite(minT)){
                        //both null, do nothing
                    }else{
                        //only min limit
                        map.put("TIME", String.valueOf(minT));
                    }
                }else{
                    //both ok, calculate median
                    final double median = (minT+maxT)/2;
                    map.put("TIME", String.valueOf(median));
                }


            } else if(ad.equals(AxisDirection.UP) || ad.equals(AxisDirection.DOWN)){
                //found a vertical axis
                final double minV = env.getMinimum(i);
                final double maxV = env.getMaximum(i);

                if(Double.isNaN(minV) || Double.isInfinite(minV)){
                    if(Double.isNaN(maxV) || Double.isInfinite(maxV)){
                        //both null, do nothing
                    }else{
                        //only max limit
                        map.put("ELEVATION", String.valueOf(maxV));
                    }
                }else if(Double.isNaN(maxV) || Double.isInfinite(maxV)){
                    if(Double.isNaN(minV) || Double.isInfinite(minV)){
                        //both null, do nothing
                    }else{
                        //only min limit
                        map.put("ELEVATION", String.valueOf(minV));
                    }
                }else{
                    //both ok, calculate median
                    final double median = (minV+maxV)/2;
                    map.put("ELEVATION", String.valueOf(median));
                }

            }
        }
    }

    @Override
    public InputStream getSOAPResponse() throws IOException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

}
