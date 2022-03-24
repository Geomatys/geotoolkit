/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2009-2011, Geomatys
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
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.sis.referencing.CRS;
import org.geotoolkit.client.AbstractRequest;
import org.geotoolkit.client.CapabilitiesException;
import org.geotoolkit.referencing.ReferencingUtilities;
import org.geotoolkit.security.ClientSecurity;
import org.geotoolkit.util.StringUtilities;
import org.geotoolkit.wms.xml.AbstractDimension;
import org.geotoolkit.wms.xml.AbstractLayer;
import org.geotoolkit.wms.xml.AbstractWMSCapabilities;
import org.geotoolkit.wms.xml.Style;
import org.opengis.geometry.Envelope;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.cs.AxisDirection;
import org.opengis.referencing.cs.CoordinateSystem;
import org.opengis.referencing.cs.CoordinateSystemAxis;
import org.opengis.util.FactoryException;

/**
 * Abstract implementation of {@link GetMapRequest}, which defines the parameters for
 * a GetMap request.
 *
 * @author Johann Sorel (Geomatys)
 * @module
 */
public abstract class AbstractGetMap extends AbstractRequest implements GetMapRequest {

    private static final SimpleDateFormat ISO_FORMAT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
    static {
        ISO_FORMAT.setTimeZone(TimeZone.getTimeZone("GMT+0"));
    }

    /**
     * Default logger for all GetMap requests.
     */
    protected static final Logger LOGGER = Logger.getLogger("org.geotoolkit.wms");
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
    protected WebMapClient server = null;

    /**
     * Defines the server url and the service version for this kind of request.
     *
     * @param serverURL The server url.
     * @param version The version of the request.
     */
    protected AbstractGetMap(final String serverURL, final String version, final ClientSecurity security) {
        super(serverURL, security, null);
        this.version = version;
    }

    protected AbstractGetMap(final WebMapClient server, final String version, final ClientSecurity security) {
        super(server.getURL().toString(),security,null, server.getTimeOutValue());
        this.server = server;
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
    public void setLayers(final String... layers) {
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
    public void setEnvelope(final Envelope env) {
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
        return exceptions;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void setExceptions(final String ex) {
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
    public void setStyles(final String... styles) {
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
    public void setSld(final String sld) {
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
    public void setSldBody(final String sldBody) {
        this.sldBody = sldBody;
    }

    @Override
    public String getSldVersion() {
        return sldVersion;
    }

    @Override
    public void setSldVersion(final String sldVersion) {
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
    public void setTransparent(final Boolean transparent) {
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
        requestParameters.put("LAYERS", StringUtilities.toCommaSeparatedValues((Object[])layers));
        try {
            requestParameters.putAll(toString(envelope));
        } catch (FactoryException|NullPointerException ex) {
            throw new IllegalArgumentException("Cannot define coordinate system from envelope CRS");
        }


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

        if (sldVersion != null) {
            requestParameters.put("SLD_VERSION", sldVersion);
        }


        // Add one style parameter
        String styleParam = "STYLES";
        String styleValue = "";

        if (sldBody != null) {
            styleParam = "SLD_BODY";
            styleValue = sldBody;

        } else if (sld != null) {
            styleParam = "SLD";
            styleValue = sld;

        } else if (styles != null && styles.length > 0 && styles[0] != null) {
            styleValue = StringUtilities.toCommaSeparatedValues((Object[])styles);
        } else {
            //try to found the default style name in the capabilities
            //some server implementation do not like when the style is left empty
            if(server != null && layers != null){
                try{
                    final StringBuilder sb = new StringBuilder();
                    for(int i=0;i<layers.length;i++){
                        final String ln = layers[i];
                        if(i!=0){
                            sb.append(',');
                        }
                        final List<? extends Style> styles = WMSUtilities.findStyleCandidates(server, ln);
                        if(styles != null && !styles.isEmpty()){
                            final String name = styles.get(0).getName();
                            final String title = styles.get(0).getTitle();
                            if(name!=null){
                                sb.append(name);
                            }else if(title!=null){
                                sb.append(title);
                            }
                        }
                    }
                    styleValue = sb.toString();
                }catch(CapabilitiesException ex){
                    LOGGER.log(Level.FINE, ex.getMessage(),ex);
                }
            }

        }

        requestParameters.put(styleParam, styleValue);

    }

    /**
     * Return a map containing BBOX, SRS (or CRS), TIME and ELEVATION parameters.
     */
    protected Map<String,String> toString(final Envelope env) throws FactoryException {
        final Map<String,String> map = new HashMap<>();
        final StringBuilder sb = new StringBuilder();
        final double minx = env.getMinimum(0);
        final double maxx = env.getMaximum(0);
        final double miny = env.getMinimum(1);
        final double maxy = env.getMaximum(1);
        sb.append(minx).append(',').append(miny).append(',').append(maxx).append(',').append(maxy);

        map.put("BBOX", sb.toString());

        CoordinateReferenceSystem crs2d = CRS.getHorizontalComponent(env.getCoordinateReferenceSystem());
        map.put(getCRSParameterName(), ReferencingUtilities.lookupIdentifier(crs2d, true));

        encodeNDParameters(env, map);

        return map;
    }

    protected abstract String getCRSParameterName();

    /**
     * Encode other additional parameters, like TIME, ELEVATION or others which will be put
     * in a filter, if they are defined in the envelope.
     *
     * @param env Current Envelope
     * @param map map containing GetMap parameters
     */
    protected void encodeNDParameters(final Envelope env, final Map<String, String> map) {
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

            } else if ((!ad.equals(AxisDirection.EAST)) && (!ad.equals(AxisDirection.WEST)) &&
                       (!ad.equals(AxisDirection.SOUTH)) && (!ad.equals(AxisDirection.NORTH))) {

                /*
                 * If other dimension is present in requested CRS, check if current layer capabilities
                 * support this dimension before add CQL filter on request.
                 */
                if (server != null && layers.length == 1) {
                    try {
                        final AbstractWMSCapabilities capa = server.getServiceCapabilities();
                        final AbstractLayer layer = capa.getLayerFromName(layers[0]);
                        final List capaDims  = layer.getDimension();
                        boolean dimensionSupported = false;

                        for (Object capaDim : capaDims) {
                            if (capaDim instanceof AbstractDimension) {
                                AbstractDimension absDim = (AbstractDimension) capaDim;
                                if (absDim.getName().equals(axis.getName().getCode())) {
                                    dimensionSupported = true;
                                }
                            }
                        }
                        if(dimensionSupported) {
                            final String newFilter = axis.getName().getCode() +"="+ env.getMedian(i);
                            if (map.containsKey("cql_filter")) {
                                final String previousFilter = map.get("cql_filter");
                                map.put("cql_filter", previousFilter + " AND " + newFilter);
                            } else {
                                map.put("cql_filter", newFilter);
                            }
                        }

                    } catch (CapabilitiesException ex) {
                        // no nothing
                    }
                }
            }
        }
    }

    /**
     * Encode ELEVATION parameters if defined in the envelope.
     *
     * @param env Current Envelope
     * @param map map containing GetMap parameters
     */
    protected void encodeElevation(final Envelope env, final Map<String, String> map) {
        //append time and elevation parameter
        final CoordinateSystem cs = env.getCoordinateReferenceSystem().getCoordinateSystem();
        for (int i = 0, n = cs.getDimension(); i < n; i++) {
            final CoordinateSystemAxis axis = cs.getAxis(i);
            final AxisDirection ad = axis.getDirection();
            if (ad.equals(AxisDirection.UP) || ad.equals(AxisDirection.DOWN)) {
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
    private static String toDateString(final double value) {
        return ISO_FORMAT.format(new Date((long) value));
    }
}
