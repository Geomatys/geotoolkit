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

import java.awt.Image;
import java.net.MalformedURLException;
import java.util.logging.Level;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.IOException;
import java.net.URL;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import javax.imageio.ImageIO;

import org.geotoolkit.coverage.grid.GridCoverage2D;
import org.geotoolkit.coverage.grid.GridCoverageFactory;
import org.geotoolkit.coverage.grid.ViewType;
import org.geotoolkit.coverage.processing.CoverageProcessingException;
import org.geotoolkit.coverage.processing.Operations;
import org.geotoolkit.referencing.crs.DefaultGeographicCRS;
import org.geotoolkit.display.canvas.RenderingContext;
import org.geotoolkit.display.canvas.control.CanvasMonitor;
import org.geotoolkit.display.exception.PortrayalException;
import org.geotoolkit.display2d.GO2Utilities;
import org.geotoolkit.display2d.canvas.RenderingContext2D;
import org.geotoolkit.geometry.Envelope2D;
import org.geotoolkit.geometry.GeneralEnvelope;
import org.geotoolkit.internal.referencing.CRSUtilities;
import org.geotoolkit.map.AbstractMapLayer;
import org.geotoolkit.map.DynamicMapLayer;
import org.geotoolkit.referencing.CRS;
import org.geotoolkit.referencing.cs.DefaultCoordinateSystemAxis;
import org.geotoolkit.referencing.operation.transform.LinearTransform;
import org.geotoolkit.style.DefaultStyleFactory;
import org.geotoolkit.temporal.object.FastDateParser;
import org.geotoolkit.util.NullArgumentException;
import org.geotoolkit.util.StringUtilities;
import org.geotoolkit.wms.GetLegendRequest;
import org.geotoolkit.wms.GetMapRequest;
import org.geotoolkit.wms.WebMapServer;
import org.geotoolkit.wms.xml.AbstractDimension;
import org.geotoolkit.wms.xml.AbstractLayer;

import org.opengis.geometry.Envelope;
import org.opengis.metadata.spatial.PixelOrientation;
import org.opengis.util.FactoryException;
import org.opengis.referencing.NoSuchAuthorityCodeException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.crs.TemporalCRS;
import org.opengis.referencing.operation.MathTransform2D;
import org.opengis.referencing.operation.TransformException;


/**
 * Map representation of a WMS layer.
 *
 * @author Johann Sorel (Geomatys)
 * @author Cédric Briançon (Geomatys)
 * @module pending
 */
public class WMSMapLayer extends AbstractMapLayer implements DynamicMapLayer {
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
     * Optional SLD body directly in the request.
     */
    private String sldBody = null;

    /**
     * Output format of the response.
     */
    private String format = "image/png";

    private CRS84Politic crs84Politic = CRS84Politic.STRICT;
    private EPSG4326Politic epsg4326Politic = EPSG4326Politic.STRICT;
    private boolean useLocalReprojection = false;
    private boolean matchCapabilitiesDates = false;

    public WMSMapLayer(final WebMapServer server, final String... layers) {
        super(new DefaultStyleFactory().style());
        this.server = server;
        this.layers = layers;
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
        return MAXEXTEND_ENV;
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
     * {@inheritDoc }
     */
    @Override
    public URL query(final RenderingContext context) throws PortrayalException {
        return query(context, null);
    }

    /**
     * {@inheritDoc }
     */
    private URL query(final RenderingContext context, CoordinateReferenceSystem replaceCRS) throws PortrayalException {

        if (!(context instanceof RenderingContext2D)) {
            throw new PortrayalException("WMSLayer only support rendering for RenderingContext2D");
        }

        final RenderingContext2D context2D = (RenderingContext2D) context;
        Envelope env = context2D.getCanvasObjectiveBounds();

        //looks like the reprojection will be handle by geotoolkit,
        //the distant server might not be very friendly or projection capabilities
        if (replaceCRS != null) {
            try {
                env = CRS.transform(env, replaceCRS);
            } catch (TransformException ex) {
                LOGGER.log(Level.WARNING, ex.getLocalizedMessage(), ex);
            }
        }

        Shape rect = context2D.getCanvasDisplayBounds();

        final double rotation = context2D.getCanvas().getController().getRotation();
        final AffineTransform trs = new AffineTransform();
        trs.rotate(rotation);
        rect = trs.createTransformedShape(rect);

        try {
            return query(env, rect.getBounds().getSize());
        } catch (MalformedURLException ex) {
            LOGGER.log(Level.WARNING, ex.getLocalizedMessage(), ex);
        }

        return null;
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
     */
    public URL query(Envelope env, final Dimension rect) throws MalformedURLException {

        //check the politics, the distant wms server might not be strict on axis orders
        // nor in it's crs definitions between CRS:84 and EPSG:4326

        //check CRS84 politic---------------------------------------------------
        if (crs84Politic != CRS84Politic.STRICT) {
            if (CRS.equalsIgnoreMetadata(env.getCoordinateReferenceSystem(), DefaultGeographicCRS.WGS84)) {
                switch (crs84Politic) {
                    case CONVERT_TO_EPSG4326:
                        env = new GeneralEnvelope(env);
                        ((GeneralEnvelope) env).setCoordinateReferenceSystem(EPSG_4326);
                        break;
                }
            }
        }

        //check EPSG4326 politic------------------------------------------------
        if (epsg4326Politic != EPSG4326Politic.STRICT) {
            if (CRS.equalsIgnoreMetadata(env.getCoordinateReferenceSystem(), DefaultGeographicCRS.WGS84)) {
                switch (epsg4326Politic) {
                    case CONVERT_TO_CRS84:
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
                System.out.println("here");
                //there is a temporal axis
                double median = env.getMedian(index);
                Long closest = findClosestDate((long)median);
                if(closest != null){
                    GeneralEnvelope adjusted = new GeneralEnvelope(env);
                    adjusted.setRange(index, closest, closest);
                    env = adjusted;
                    System.out.println("adjusted : " + new Date(closest));
                }
            }
        }

        final GetMapRequest request = server.createGetMap();
        request.setEnvelope(env);
        request.setDimension(rect);
        request.setLayers(layers);
        if (styles == null) {
            request.setStyles("");
        } else {
            request.setStyles(styles);
        }
        request.setSld(sld);
        request.setSldBody(sldBody);
        request.setFormat(format);
        request.dimensions().putAll(dims);
        return request.getURL();
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void portray(RenderingContext context) throws PortrayalException {
        if (!(context instanceof RenderingContext2D)) {
            throw new PortrayalException("WMSLayer only support rendering for RenderingContext2D");
        }

        final RenderingContext2D context2D = (RenderingContext2D) context;

        CoordinateReferenceSystem queryCrs = context.getObjectiveCRS2D();
        Envelope env = context2D.getCanvasObjectiveBounds();
        //check if we must make the  coverage reprojection ourself--------------
        if (useLocalReprojection) {
            try {
                if (!supportCRS(context2D.getCanvasObjectiveBounds().getCoordinateReferenceSystem())) {
                    queryCrs = findOriginalCRS();
                    if(queryCrs == null){
                        //last chance use : CRS:84
                        queryCrs = DefaultGeographicCRS.WGS84;
                    }
                    env = CRS.transform(env, queryCrs);
                }
            } catch (FactoryException ex) {
                context.getMonitor().exceptionOccured(ex, Level.WARNING);
            } catch (TransformException ex) {
                context.getMonitor().exceptionOccured(ex, Level.WARNING);
            }
        }

        final Dimension dim = context2D.getCanvasDisplayBounds().getSize();
        final URL url;
        try {
            url = query(env, dim);
        } catch (MalformedURLException ex) {
            throw new PortrayalException(ex);
        }

        LOGGER.log(Level.WARNING, "[WMSMapLayer] : GETMAP request : {0}", url);

        final BufferedImage image;
        try {
            image = ImageIO.read(url);
        } catch (IOException io) {
            throw new PortrayalException(io);
        }

        if (image == null) {
            throw new PortrayalException("WMS server didn't return an image.");
        }

        final GridCoverageFactory gc = new GridCoverageFactory();
        final GridCoverage2D coverage = gc.create("wms", image, env);
        portray(context2D, coverage);
        
    }

    private static void portray(RenderingContext2D renderingContext, GridCoverage2D dataCoverage) throws PortrayalException{
        final CanvasMonitor monitor = renderingContext.getMonitor();
        final Graphics2D g2d = renderingContext.getGraphics();

        final CoordinateReferenceSystem coverageCRS = dataCoverage.getCoordinateReferenceSystem();
        try{
            final CoordinateReferenceSystem candidate2D = CRSUtilities.getCRS2D(coverageCRS);
            if(!CRS.equalsIgnoreMetadata(candidate2D,renderingContext.getObjectiveCRS2D()) ){

                dataCoverage = (GridCoverage2D) Operations.DEFAULT.resample(dataCoverage.view(ViewType.NATIVE), renderingContext.getObjectiveCRS2D());

                if(dataCoverage != null){
                    dataCoverage = dataCoverage.view(ViewType.RENDERED);
                }
            }
        } catch (CoverageProcessingException ex) {
            monitor.exceptionOccured(ex, Level.WARNING);
            return;
        } catch(Exception ex){
            //several kind of errors can happen here, we catch anything to avoid blocking the map component.
            monitor.exceptionOccured(
                new IllegalStateException("Coverage is not in the requested CRS, found : " +
                "\n"+ coverageCRS +
                " was expecting : \n" +
                renderingContext.getObjectiveCRS() +
                "\nOriginal Cause:"+ ex.getMessage(), ex), Level.WARNING);
            return;
        }

        if(dataCoverage == null){
            LOGGER.log(Level.WARNING, "Reprojected coverage is null.");
            return;
        }

        //we must switch to objectiveCRS for grid coverage
        renderingContext.switchToObjectiveCRS();

        final RenderedImage img = dataCoverage.getRenderedImage();
        final MathTransform2D trs2D = dataCoverage.getGridGeometry().getGridToCRS2D(PixelOrientation.UPPER_LEFT);
        if(trs2D instanceof AffineTransform){
            g2d.setComposite(GO2Utilities.ALPHA_COMPOSITE_1F);
            g2d.drawRenderedImage(img, (AffineTransform)trs2D);
        }else if (trs2D instanceof LinearTransform) {
            final LinearTransform lt = (LinearTransform) trs2D;
            final int col = lt.getMatrix().getNumCol();
            final int row = lt.getMatrix().getNumRow();
            //TODO using only the first parameters of the linear transform
            throw new PortrayalException("Could not render image, GridToCRS is a not an AffineTransform, found a " + trs2D.getClass());
        }else{
            throw new PortrayalException("Could not render image, GridToCRS is a not an AffineTransform, found a " + trs2D.getClass() );
        }

    }

    /**
     * {@inheritDoc }
     */
    @Override
    public Image getLegend() throws PortrayalException {
        final GetLegendRequest request = server.creategetLegend();
        request.setLayer(layers[0]);

        final BufferedImage buffer;
        try {
            buffer = ImageIO.read(request.getURL());
        } catch (MalformedURLException ex) {
            throw new PortrayalException(ex);
        } catch (IOException ex) {
            throw new PortrayalException(ex);
        }

        return buffer;
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
     * Verify if the server supports the given {@linkplain CoordinateReferenceSystem crs}.
     *
     * @param crs The {@linkplain CoordinateReferenceSystem crs} to test.
     * @return {@code True} if the given {@linkplain CoordinateReferenceSystem crs} is present
     *         in the list of supported crs in the GetCapabilities response. {@code False} otherwise.
     * @throws FactoryException
     */
    private boolean supportCRS(final CoordinateReferenceSystem crs) throws FactoryException {
        final AbstractLayer layer = server.getCapabilities().getLayerFromName(layers[0]);

        final String srid = CRS.lookupIdentifier(crs, true);

        if(layer != null){
            for (String str : layer.getCRS()) {
                if (srid.equalsIgnoreCase(str)) {
                    return true;
                }
            }
        }else{
            LOGGER.log(Level.WARNING, "Layer : " + layers[0] + " could not be found in the getCapabilities. "
                    + "This can be caused by an incorrect layer name (check case-sensitivity) or a non-compliant wms serveur.");
        }

        return false;
    }

    /**
     * Find the best original crs of the data in the capabilities.
     */
    private CoordinateReferenceSystem findOriginalCRS() throws FactoryException {
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
            LOGGER.log(Level.WARNING, "Layer : " + layers[0] + " could not be found in the getCapabilities. "
                    + "This can be caused by an incorrect layer name (check case-sensitivity) or a non-compliant wms serveur.");
        }

        return null;
    }

    /**
     * Search in the getCapabilities the closest date.
     */
    private Long findClosestDate(long date) {
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
            LOGGER.log(Level.WARNING, "Layer : " + layers[0] + " could not be found in the getCapabilities. "
                    + "This can be caused by an incorrect layer name (check case-sensitivity) or a non-compliant wms serveur.");
        }

        return null;
    }

}
