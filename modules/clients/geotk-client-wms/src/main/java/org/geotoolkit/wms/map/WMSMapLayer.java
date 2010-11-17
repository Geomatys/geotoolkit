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
package org.geotoolkit.wms.map;

import java.awt.geom.NoninvertibleTransformException;
import java.net.MalformedURLException;
import java.util.logging.Level;
import java.awt.Dimension;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.net.URL;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.geotoolkit.display2d.GO2Utilities;
import org.geotoolkit.referencing.crs.DefaultGeographicCRS;
import org.geotoolkit.geometry.Envelope2D;
import org.geotoolkit.geometry.GeneralDirectPosition;
import org.geotoolkit.geometry.GeneralEnvelope;
import org.geotoolkit.internal.referencing.CRSUtilities;
import org.geotoolkit.map.AbstractMapLayer;
import org.geotoolkit.referencing.CRS;
import org.geotoolkit.referencing.cs.DefaultCoordinateSystemAxis;
import org.geotoolkit.style.DefaultStyleFactory;
import org.geotoolkit.temporal.object.FastDateParser;
import org.geotoolkit.util.NullArgumentException;
import org.geotoolkit.util.StringUtilities;
import org.geotoolkit.wms.GetFeatureInfoRequest;
import org.geotoolkit.wms.GetMapRequest;
import org.geotoolkit.wms.WebMapServer;
import org.geotoolkit.wms.xml.AbstractDimension;
import org.geotoolkit.wms.xml.AbstractLayer;
import org.geotoolkit.wms.xml.Style;

import org.opengis.geometry.DirectPosition;
import org.opengis.geometry.Envelope;
import org.opengis.referencing.operation.TransformException;
import org.opengis.util.FactoryException;
import org.opengis.referencing.NoSuchAuthorityCodeException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.MathTransform;


/**
 * Map representation of a WMS layer.
 *
 * @author Johann Sorel (Geomatys)
 * @author Cédric Briançon (Geomatys)
 * @module pending
 */
public class WMSMapLayer extends AbstractMapLayer {
    /**
     * EPSG:4326 object.
     */
    private static final CoordinateReferenceSystem EPSG_4326;
    static {
        CoordinateReferenceSystem crs = null;
        try {
            crs = CRS.decode("EPSG:4326");
        } catch (NoSuchAuthorityCodeException ex) {
            LOGGER.log(Level.WARNING, ex.getLocalizedMessage(), ex);
        } catch (FactoryException ex) {
            LOGGER.log(Level.WARNING, ex.getLocalizedMessage(), ex);
        }
        EPSG_4326 = crs;
    }

    /**
     * Configure the politic when the requested envelope is in CRS:84.
     * Some servers are not strict on axis order or crs definitions.
     * that's why we need this.
     */
    public static enum CRS84Politic {
        STRICT,
        CONVERT_TO_EPSG4326
    }

    /**
     * Configure the politic when the requested envelope is in EPSG:4326.
     * Some servers are not strict on axis order or crs definitions.
     * that's why we need this.
     */
    public static enum EPSG4326Politic {
        STRICT,
        CONVERT_TO_CRS84
    }

    //TODO : we should use the envelope provided by the wms capabilities
    private static final Envelope MAXEXTEND_ENV = new Envelope2D(DefaultGeographicCRS.WGS84, -180, -90, 360, 180);

    /**
     * The web map server to request.
     */
    private final WebMapServer server;

    /**
     * Map for optional dimensions specified for the GetMap request.
     */
    private final Map<String, String> dims = new HashMap<String, String>();

    /**
     * The layers to request.
     */
    private String[] layers;

    /**
     * The styles associated to the {@link #layers}.
     */
    private String[] styles = new String[0];

    /**
     * Optional SLD file for the layer to request.
     */
    private String sld = null;

    /**
     * Optional SLD version, if a SLD file have been given it is mandatory.
     */
    private String sldVersion = null;

    /**
     * Optional SLD body directly in the request.
     */
    private String sldBody = null;

    /**
     * Output format of the response.
     */
    private String format = "image/png";

    /**
     * Transparence of the layer.
     * WARNING: if we stictly respect the spec this value should be false.
     */
    private Boolean transparent = true;

    /**
     * Output format of exceptions
     */
    private String exceptionsFormat = null;

    private CRS84Politic crs84Politic = CRS84Politic.STRICT;
    private EPSG4326Politic epsg4326Politic = EPSG4326Politic.STRICT;
    private boolean useLocalReprojection = false;
    private boolean matchCapabilitiesDates = false;

    private Envelope env = null;

    public WMSMapLayer(final WebMapServer server, final String... layers) {
        super(new DefaultStyleFactory().style());
        this.server = server;
        this.layers = layers;

        //register the default graphic builder for geotk 2D engine.
        graphicBuilders().add(WMSGraphicBuilder.INSTANCE);
    }

    /**
     * Returns the {@link WebMapServer} to request. Can't be {@code null}.
     */
    public WebMapServer getServer() {
        return server;
    }

    public void setCrs84Politic(final CRS84Politic crs84Politic) {
        if (crs84Politic == null) {
            throw new NullPointerException("CRS84 politic can not be null.");
        }
        this.crs84Politic = crs84Politic;
    }

    public CRS84Politic getCrs84Politic() {
        return crs84Politic;
    }

    public void setEpsg4326Politic(final EPSG4326Politic epsg4326Politic) {
        if (epsg4326Politic == null) {
            throw new NullPointerException("EPSG4326 politic can not be null.");
        }
        this.epsg4326Politic = epsg4326Politic;
    }

    public EPSG4326Politic getEpsg4326Politic() {
        return epsg4326Politic;
    }

    /**
     * Define if the map layer must rely on the geotoolkit reprojection capabilities
     * if the distant server can not handle the canvas crs.
     * The result image might not be pretty, but still better than no image.
     * @param useLocalReprojection
     */
    public void setUseLocalReprojection(boolean useLocalReprojection) {
        this.useLocalReprojection = useLocalReprojection;
    }

    public boolean isUseLocalReprojection() {
        return useLocalReprojection;
    }

    /**
     * Set to true if the time parameter must be adjusted to match the closest
     * date provided in the layer getCapabilities.
     * @param matchCapabilitiesDates
     */
    public void setMatchCapabilitiesDates(boolean matchCapabilitiesDates) {
        this.matchCapabilitiesDates = matchCapabilitiesDates;
    }

    public boolean isMatchCapabilitiesDates() {
        return matchCapabilitiesDates;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public Envelope getBounds() {
        if(env == null){
            env = findEnvelope();
            if(env == null){
                env = MAXEXTEND_ENV;
            }
        }
        return env;
    }

    /**
     * Creates the {@linkplain GetMapRequest get map request} object.
     *
     * @return A {@linkplain GetMapRequest get map request} object containing the
     *         predefined parameters.
     */
    public GetMapRequest createGetMapRequest() {
        final GetMapRequest request = server.createGetMap();
        request.setLayers(layers);
        return request;
    }

    /**
     * Gives a {@linkplain GetMapRequest get map request} for the given envelope and
     * output dimension. The default format will be {@code image/png} if the
     * {@link #setFormat(java.lang.String)} has not been called.
     *
     * @param env A valid envlope to request.
     * @param rect The dimension for the output response.
     * @return A {@linkplain GetMapRequest get map request}.
     * @throws MalformedURLException if the generated url is invalid.
     * @throws TransformException if the tranformation between 2 CRS failed.
     */
    public URL query(Envelope env, final Dimension rect) throws MalformedURLException, TransformException {
        final GetMapRequest request = server.createGetMap();
        prepareGetMapRequest(request, env, rect);
        return request.getURL();
    }

    public URL queryFeatureInfo(Envelope env, final Dimension rect, int x, int y,
            final String[] queryLayers, final String infoFormat, int featureCount)
            throws TransformException, FactoryException, MalformedURLException, NoninvertibleTransformException {
        
        final GetFeatureInfoRequest request = getServer().createGetFeatureInfo();
        request.setQueryLayers(queryLayers);
        request.setInfoFormat(infoFormat);
        request.setFeatureCount(featureCount);

        final GeneralEnvelope cenv = new GeneralEnvelope(env);
        final Dimension crect = new Dimension(rect);

        prepareQuery(request, cenv, crect, new double[]{1,1});

        //recalculate x/y coordinates since there might be a local reprojection
        final CoordinateReferenceSystem beforeCRS = env.getCoordinateReferenceSystem();
        final CoordinateReferenceSystem afterCRS = env.getCoordinateReferenceSystem();

        if(!CRS.equalsIgnoreMetadata(beforeCRS, afterCRS)){
            //calculate new coordinate in the reprojected query
            final Point2D point = new Point2D.Double(x, y);
            final AffineTransform beforeTrs = GO2Utilities.toAffine(rect,env);
            final AffineTransform afterTrs = GO2Utilities.toAffine(crect,cenv);
            afterTrs.invert();

            beforeTrs.transform(point, point);

            final DirectPosition pos = new GeneralDirectPosition(env.getCoordinateReferenceSystem());
            pos.setOrdinate(0, point.getX());
            pos.setOrdinate(1, point.getY());

            final MathTransform trs = CRS.findMathTransform(beforeCRS, afterCRS);
            trs.transform(pos, pos);

            point.setLocation(pos.getOrdinate(0), pos.getOrdinate(1));
            afterTrs.transform(point, point);
            x = (int) point.getX();
            y = (int) point.getY();

        }
        request.setColumnIndex(x);
        request.setRawIndex(y);

        return request.getURL();
    }

    /**
     * Prepare parameters for a getMap query.
     * The given parameters will be modified !
     *
     * @param request
     * @param env
     * @param dim
     */
    void prepareQuery(GetMapRequest request, GeneralEnvelope env, Dimension dim, double[] resolution) throws TransformException, FactoryException{

        final CoordinateReferenceSystem crs = env.getCoordinateReferenceSystem();
        CoordinateReferenceSystem crs2D = CRSUtilities.getCRS2D(crs);

        //check if we must make the  coverage reprojection ourself--------------
        if (isUseLocalReprojection()) {
            if (!supportCRS(crs2D)) {
                crs2D = findOriginalCRS();
                if(crs2D == null){
                    //last chance use : CRS:84
                    crs2D = DefaultGeographicCRS.WGS84;
                }
                //change the 2D crs part of the envelope, preserve other axis
                final Envelope trsEnv = GO2Utilities.transform2DCRS(env, crs2D);
                env.setEnvelope(new GeneralEnvelope(trsEnv));
            }
        }

        //resolution contain dpi adjustments, to obtain an image of the correct dpi
        //we raise the request dimension so that when we reduce it it will have the
        //wanted dpi.
        dim.width /= resolution[0];
        dim.height /= resolution[1];

        prepareGetMapRequest(request, env, dim);
    }

    private void prepareGetMapRequest(GetMapRequest request, Envelope env, final Dimension rect) throws TransformException{
        //check the politics, the distant wms server might not be strict on axis orders
        // nor in it's crs definitions between CRS:84 and EPSG:4326
        final CoordinateReferenceSystem crs2D = CRSUtilities.getCRS2D(env.getCoordinateReferenceSystem());


        //we loose the vertical and temporale crs in the process, must be fixed
        //check CRS84 politic---------------------------------------------------
        if (crs84Politic != CRS84Politic.STRICT) {
            if (CRS.equalsIgnoreMetadata(crs2D, DefaultGeographicCRS.WGS84)) {

                switch (crs84Politic) {
                    case CONVERT_TO_EPSG4326:
                        env = CRS.transform(env, crs2D);
                        env = new GeneralEnvelope(env);
                        ((GeneralEnvelope) env).setCoordinateReferenceSystem(EPSG_4326);
                        break;
                }
            }
        }

        //check EPSG4326 politic------------------------------------------------
        if (epsg4326Politic != EPSG4326Politic.STRICT) {
            if (CRS.equalsIgnoreMetadata(crs2D, EPSG_4326)) {
                switch (epsg4326Politic) {
                    case CONVERT_TO_CRS84:
                        env = CRS.transform(env, crs2D);
                        env = new GeneralEnvelope(env);
                        ((GeneralEnvelope) env).setCoordinateReferenceSystem(DefaultGeographicCRS.WGS84);
                        break;
                }
            }
        }

        if(matchCapabilitiesDates){
            final CoordinateReferenceSystem crs = env.getCoordinateReferenceSystem();
            final int index = CRSUtilities.dimensionColinearWith(crs.getCoordinateSystem(), DefaultCoordinateSystemAxis.TIME);
            if(index >= 0){
                //there is a temporal axis
                final double median = env.getMedian(index);
                final Long closest = findClosestDate((long)median);
                if(closest != null){
                    final GeneralEnvelope adjusted = new GeneralEnvelope(env);
                    adjusted.setRange(index, closest, closest);
                    env = adjusted;
                    LOGGER.log(Level.FINE, "adjusted : {0}", new Date(closest));
                }
            }
        }

        request.setEnvelope(env);
        request.setDimension(rect);
        request.setLayers(layers);
        if (styles == null) {
            request.setStyles("");
        } else {
            request.setStyles(styles);
        }
        request.setSld(sld);
        request.setSldVersion(sldVersion);
        request.setSldBody(sldBody);
        request.setFormat(format);
        request.setExceptions(exceptionsFormat);
        request.setTransparent(transparent);
        request.dimensions().putAll(dims);
    }

    /**
     * Sets the layer names to requests.
     *
     * @param names Array of layer names.
     */
    public void setLayerNames(final String... names) {
        this.layers = names;
    }

    /**
     * Returns the layer names.
     */
    public String[] getLayerNames() {
        return layers.clone();
    }

    /**
     * Returns a concatenated string of all layer names, separated by comma.
     */
    public String getCombinedLayerNames() {
        return StringUtilities.toCommaSeparatedValues(layers);
    }

    /**
     * Sets the styles for the layers.
     *
     * @param styles Array of style names.
     */
    public void setStyles(final String... styles) {
        this.styles = styles;
    }

    /**
     * Returns the style names.
     */
    public String[] getStyles() {
        return styles.clone();
    }

    /**
     * Sets the sld value.
     *
     * @param sld A sld string.
     */
    public void setSld(final String sld) {
        this.sld = sld;
    }

    /**
     * Gets the sld parameters. Can return {@code null}.
     */
    public String getSld() {
        return sld;
    }

    /**
     * Sets the slBody parameter.
     *
     * @param sldBody A sld body.
     */
    public void setSldBody(final String sldBody) {
        this.sldBody = sldBody;
    }

    /**
     * Gets the sld body parameter of this request. Can return {@code null}.
     */
    public String getSldBody() {
        return sldBody;
    }

    /**
     * Get the SLD specification version for SLD defines with SLD or SLD_BODY parameter
     *
     * @return the sldVersion
     */
    public String getSldVersion() {
        return sldVersion;
    }

    /**
     * Set the SLD specification version for SLD defines with SLD or SLD_BODY parameter
     *
     * @param sldVersion 
     */
    public void setSldVersion(String sldVersion) {
        this.sldVersion = sldVersion;
    }

    /**
     * Sets the format for the output response. By default sets to {@code image/png}
     * if none.
     *
     * @param format The mime type of an output format.
     */
    public void setFormat(String format) {
        if (format == null) {
            throw new NullArgumentException("format  = "+format);
        }
        this.format = format;
        
    }

    /**
     * Gets the format for the output response. By default {@code image/png}.
     */
    public String getFormat() {
        return format;
    }

    public Map<String, String> dimensions() {
        return dims;
    }

    /**
     * @return the exceptionsFormat
     */
    public String getExceptionsFormat() {
        return exceptionsFormat;
    }

    /**
     * @param exceptionsFormat the exceptionsFormat to set
     */
    public void setExceptionsFormat(String exceptionsFormat) {
        this.exceptionsFormat = exceptionsFormat;
    }    

    /**
     * @return the transparent
     */
    public Boolean isTransparent() {
        return transparent;
    }

    /**
     * @param transparent the transparent to set
     */
    public void setTransparent(Boolean transparent) {
        this.transparent = transparent;
    }

    /**
     * Verify if the server supports the given {@linkplain CoordinateReferenceSystem crs}.
     *
     * @param crs The {@linkplain CoordinateReferenceSystem crs} to test.
     * @return {@code True} if the given {@linkplain CoordinateReferenceSystem crs} is present
     *         in the list of supported crs in the GetCapabilities response. {@code False} otherwise.
     * @throws FactoryException
     */
    boolean supportCRS(final CoordinateReferenceSystem crs) throws FactoryException {
        final AbstractLayer layer = server.getCapabilities().getLayerFromName(layers[0]);

        final String srid = CRS.lookupIdentifier(crs, true);

        if(layer != null){
            for (String str : layer.getCRS()) {
                if (srid.equalsIgnoreCase(str)) {
                    return true;
                }
            }
        }else{
            LOGGER.log(Level.WARNING, "Layer : {0} could not be found in the getCapabilities. "
                    + "This can be caused by an incorrect layer name (check case-sensitivity) or a non-compliant wms serveur.", layers[0]);
        }

        return false;
    }

    /**
     * Find the best original crs of the data in the capabilities.
     */
    CoordinateReferenceSystem findOriginalCRS() throws FactoryException {
        final AbstractLayer layer = server.getCapabilities().getLayerFromName(layers[0]);

        if(layer != null){
            for (String str : layer.getCRS()) {
                //search and return the first crs that we succesfuly parsed.
                try{
                    CoordinateReferenceSystem crs = CRS.decode(str);
                    if(crs != null){
                        return crs;
                    }
                }catch(FactoryException ex){
                    LOGGER.log(Level.FINE, "Could not parse crs code : {0}", str);
                }
            }
        }else{
            LOGGER.log(Level.WARNING, "Layer : {0} could not be found in the getCapabilities. "
                    + "This can be caused by an incorrect layer name (check case-sensitivity) or a non-compliant wms serveur.", layers[0]);
        }

        return null;
    }

    /**
     * Search in the getCapabilities the closest date.
     */
    Long findClosestDate(long date) {
        final AbstractLayer layer = server.getCapabilities().getLayerFromName(layers[0]);

        if(layer != null){
            for(AbstractDimension dim : layer.getAbstractDimension()){
                if("time".equalsIgnoreCase(dim.getName())){
                    //we found the temporal dimension
                    final FastDateParser parser = new FastDateParser();
                    final String[] dates = dim.getValue().split(",");

                    final long d = date;
                    Long closest = null;
                    for(String str : dates){
                        str = str.replaceAll("\n", "");
                        str = str.trim();
                        long candidate = parser.parseToMillis(str);
                        if(closest == null){
                            closest = candidate;
                        }else if( Math.abs(d-candidate) < Math.abs(d-closest)){
                            closest = candidate;
                        }
                    }

                    return closest;
                }
            }
        }else{
            LOGGER.log(Level.WARNING, "Layer : {0} could not be found in the getCapabilities. "
                    + "This can be caused by an incorrect layer name (check case-sensitivity) or a non-compliant wms serveur.", layers[0]);
        }

        return null;
    }

    Envelope findEnvelope(){
        final AbstractLayer layer = server.getCapabilities().getLayerFromName(layers[0]);

        if(layer != null){
            return layer.getEnvelope();
        }
        return null;
    }

    public List<? extends Style> findStyleCandidates(){
        final AbstractLayer layer = server.getCapabilities().getLayerFromName(layers[0]);
         if(layer != null){
            return layer.getStyle();
        }
        return Collections.emptyList();
    }

}
