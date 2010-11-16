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
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;
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
public abstract class AbstractGetMap extends AbstractRequest implements GetMapRequest {

    private static final SimpleDateFormat ISO_FORMAT = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss.SSS'Z'");

    static {
        ISO_FORMAT.setTimeZone(TimeZone.getTimeZone("GMT+0"));
    }
    /**
     * Default logger for all GetMap requests.
     */
    protected static final Logger LOGGER = Logging.getLogger(AbstractGetMap.class);
    /**
     * The version to use for this webservice request.
     */
    protected final String version;
    protected final Map<String, String> dims = new HashMap<String, String>();
    protected String format = "image/png";
    protected String exceptions = null;
    protected String[] layers = null;
    protected String[] styles = null;
    protected Envelope envelope = null;
    protected Dimension dimension = null;
    protected String sld = null;
    protected String sldVersion = null;
    protected String sldBody = null;
    protected Boolean transparent = null;

    /**
     * Defines the server url and the service version for this kind of request.
     *
     * @param serverURL The server url.
     * @param version The version of the request.
     */
    protected AbstractGetMap(String serverURL, String version) {
        super(serverURL);
        this.version = version;
    }

    /**
     * {@inheritDoc }
     * @return
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
        return envelope;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void setEnvelope(Envelope env) {
        this.envelope = env;
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
        return exceptions;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void setExceptions(String ex) {
        this.exceptions = ex;
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
    public String getSld() {
        return sld;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void setSld(String sld) {
        this.sld = sld;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public String getSldBody() {
        return sldBody;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void setSldBody(String sldBody) {
        this.sldBody = sldBody;
    }

    @Override
    public String getSldVersion() {
        return sldVersion;
    }

    @Override
    public void setSldVersion(String sldVersion) {
        this.sldVersion = sldVersion;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public Boolean isTransparent() {
        return transparent;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void setTransparent(Boolean transparent) {
        this.transparent = transparent;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public Map<String, String> dimensions() {
        return dims;
    }

    @Override
    protected void prepareParameters() {
        super.prepareParameters();

        // Tests if the mandatory parameters are available
        if (layers == null || layers.length == 0) {
            throw new IllegalArgumentException("LAYERS are not defined");
        }

        if (version == null) {
            throw new IllegalArgumentException("VERSION parameter is not defined");
        }

        if (format == null) {
            throw new IllegalArgumentException("FORMAT parameter is not defined");
        }

        if (dimension == null) {
            throw new IllegalArgumentException("WIDTH or HEIGHT parameter is not defined");
        }

        if (envelope == null) {
            throw new IllegalArgumentException("Envelope is not defined");
        }


        // Add mandatory parameters
        requestParameters.put("SERVICE", "WMS");
        requestParameters.put("REQUEST", "GetMap");
        requestParameters.put("VERSION", version);
        requestParameters.put("FORMAT", format);
        requestParameters.put("WIDTH", String.valueOf(dimension.width));
        requestParameters.put("HEIGHT", String.valueOf(dimension.height));
        requestParameters.put("LAYERS", StringUtilities.toCommaSeparatedValues(layers));
        requestParameters.putAll(toString(envelope));

        String stylesParam = "";

        if (styles != null && styles.length > 0 && styles[0] != null) {
            stylesParam = StringUtilities.toCommaSeparatedValues(styles);
        }
        
        requestParameters.put("STYLES", stylesParam);



        // Add optional parameters
        if (dims != null && !dims.isEmpty()) {
            requestParameters.putAll(dims);
        }

        if (exceptions != null) {
            requestParameters.put("EXCEPTIONS", exceptions);
        }

        if (transparent != null) {
            requestParameters.put("TRANSPARENT", Boolean.toString(transparent).toUpperCase());
        }

        if (sld != null) {
            requestParameters.put("SLD", sld);
        }

        if (sldVersion != null) {
            requestParameters.put("SLD_VERSION", sldVersion);
        }

        if (sldBody != null) {
            requestParameters.put("SLD_BODY", sldBody);
        }

    }

    /**
     * Return a map containing BBOX, SRS (or CRS), TIME and ELEVATION parameters.
     *
     * @param env
     * @return 
     */
    protected abstract Map<String, String> toString(Envelope env);

    /**
     * Encode TIME and ELEVATION parameters if they are defined in the envelope.
     *
     * @param env Current Envelope
     * @param map map containing GetMap parameters
     */
    protected void encodeTimeAndElevation(Envelope env, Map<String, String> map) {
        //append time and elevation parameter
        final CoordinateSystem cs = env.getCoordinateReferenceSystem().getCoordinateSystem();
        for (int i = 0, n = cs.getDimension(); i < n; i++) {
            final CoordinateSystemAxis axis = cs.getAxis(i);
            final AxisDirection ad = axis.getDirection();
            if (ad.equals(AxisDirection.FUTURE) || ad.equals(AxisDirection.PAST)) {
                //found a temporal axis
                final double minT = env.getMinimum(i);
                final double maxT = env.getMaximum(i);

                if (Double.isNaN(minT) || Double.isInfinite(minT)) {
                    if (Double.isNaN(maxT) || Double.isInfinite(maxT)) {
                        //both null, do nothing
                    } else {
                        //only max limit
                        map.put("TIME", toDateString(maxT));
                    }
                } else if (Double.isNaN(maxT) || Double.isInfinite(maxT)) {
                    if (Double.isNaN(minT) || Double.isInfinite(minT)) {
                        //both null, do nothing
                    } else {
                        //only min limit
                        map.put("TIME", toDateString(minT));
                    }
                } else {
                    //both ok, calculate median
                    final double median = (minT + maxT) / 2;
                    map.put("TIME", toDateString(median));
                }


            } else if (ad.equals(AxisDirection.UP) || ad.equals(AxisDirection.DOWN)) {
                //found a vertical axis
                final double minV = env.getMinimum(i);
                final double maxV = env.getMaximum(i);

                if (Double.isNaN(minV) || Double.isInfinite(minV)) {
                    if (Double.isNaN(maxV) || Double.isInfinite(maxV)) {
                        //both null, do nothing
                    } else {
                        //only max limit
                        map.put("ELEVATION", String.valueOf(maxV));
                    }
                } else if (Double.isNaN(maxV) || Double.isInfinite(maxV)) {
                    if (Double.isNaN(minV) || Double.isInfinite(minV)) {
                        //both null, do nothing
                    } else {
                        //only min limit
                        map.put("ELEVATION", String.valueOf(minV));
                    }
                } else {
                    //both ok, calculate median
                    final double median = (minV + maxV) / 2;
                    map.put("ELEVATION", String.valueOf(median));
                }

            }
        }
    }

    /**
     * Transform a double representing a Date to a String
     */
    private static String toDateString(double value) {
        return ISO_FORMAT.format(new Date((long) value));
    }
}
