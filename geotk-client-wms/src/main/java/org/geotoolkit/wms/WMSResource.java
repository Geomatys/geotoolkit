/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2012-2019, Geomatys
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
import java.awt.Image;
import java.awt.geom.AffineTransform;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import org.apache.sis.coverage.SampleDimension;
import org.apache.sis.coverage.grid.GridCoverage;
import org.apache.sis.coverage.grid.GridCoverage2D;
import org.apache.sis.coverage.grid.GridExtent;
import org.apache.sis.coverage.grid.GridGeometry;
import org.apache.sis.coverage.grid.GridRoundingMode;
import org.apache.sis.geometry.Envelope2D;
import org.apache.sis.geometry.Envelopes;
import org.apache.sis.geometry.GeneralDirectPosition;
import org.apache.sis.geometry.GeneralEnvelope;
import org.apache.sis.referencing.util.AxisDirections;
import org.apache.sis.referencing.util.j2d.AffineTransform2D;
import org.apache.sis.storage.AbstractGridCoverageResource;
import org.apache.sis.storage.base.StoreResource;
import org.apache.sis.referencing.CRS;
import org.apache.sis.referencing.CommonCRS;
import org.apache.sis.storage.DataStore;
import org.apache.sis.storage.DataStoreException;
import static org.apache.sis.util.ArgumentChecks.*;
import org.apache.sis.util.Utilities;
import org.geotoolkit.client.CapabilitiesException;
import org.geotoolkit.client.Request;
import org.geotoolkit.internal.referencing.CRSUtilities;
import org.geotoolkit.referencing.ReferencingUtilities;
import org.geotoolkit.util.NamesExt;
import org.geotoolkit.util.StringUtilities;
import org.opengis.geometry.DirectPosition;
import org.opengis.geometry.Envelope;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.cs.AxisDirection;
import org.opengis.referencing.cs.CoordinateSystem;
import org.opengis.referencing.cs.CoordinateSystemAxis;
import org.opengis.referencing.datum.PixelInCell;
import org.opengis.referencing.operation.MathTransform;
import org.opengis.referencing.operation.TransformException;
import org.opengis.util.FactoryException;
import org.opengis.util.GenericName;

/**
 * Coverage Reference for a WMS layer.
 *
 * @author Johann Sorel (Geomatys)
 * @author Cédric Briançon (Geomatys)
 * @module
 */
public class WMSResource extends AbstractGridCoverageResource implements StoreResource {

    static final Dimension DEFAULT_SIZE = new Dimension(256, 256);
    protected static final Logger LOGGER = Logger.getLogger("org.geotoolkit.wms");

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
    private static final Envelope MAXEXTEND_ENV = new Envelope2D(
            CommonCRS.WGS84.normalizedGeographic(), -180, -90, 360, 180);

    /**
     * The web map server to request.
     */
    private final WebMapClient server;

    /**
     * Map for optional dimensions specified for the GetMap request.
     */
    private final Map<String, String> dims = new HashMap<String, String>();

    /**
     * The layers to request.
     */
    private GenericName[] layers;

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
     * WARNING: if we strictly respect the spec this value should be false.
     */
    private Boolean transparent = true;

    /**
     * Output format of exceptions
     */
    private String exceptionsFormat = null;

    /**
     * Use local reprojection of the image.
     */
    private boolean useLocalReprojection = true;

    /**
     * If a daterange is defined in the envelope, move the temporal value
     * on an existing value.
     */
    private boolean matchCapabilitiesDates = false;

    // hacks to fix some server not returning proper images
    private CRS84Politic    crs84Politic = CRS84Politic.STRICT;
    private EPSG4326Politic epsg4326Politic = EPSG4326Politic.STRICT;

    private final GenericName identifier;
    private Envelope env;


    public WMSResource(final WebMapClient server, String ... layers) {
        this(server,toNames(layers));
    }

    public WMSResource(final WebMapClient server, final GenericName ... names) {
        super(null, false);
        this.server = server;
        this.identifier = names[0];

        if(names == null || names.length == 0){
            throw new IllegalArgumentException("No layer name defined");
        }

        this.layers = names;
    }

    private static GenericName[] toNames(String ... names){
        if(names == null || names.length == 0){
            return new GenericName[0];
        }

        final GenericName[] ns = new GenericName[names.length];
        for(int i=0;i<names.length;i++){
            final String str = names[i];
            if(str != null && str.contains(",")){
                throw new IllegalArgumentException("invalid layer, name must not contain ',' caractere : " + str);
            }
            ns[i] = NamesExt.valueOf(str);
        }
        return ns;
    }

    @Override
    public Optional<GenericName> getIdentifier() throws DataStoreException {
        return Optional.of(identifier);
    }

    @Override
    public DataStore getOriginator() {
        return server;
    }

    /**
     * @return array of all layer names
     */
    public GenericName[] getNames() throws DataStoreException{
        return layers.clone();
    }

    /**
     * Sets the layer names to requests.
     *
     * @param names Array of layer names.
     */
    public void setLayerNames(final String... names) {
        this.layers = toNames(names);
    }

    /**
     * Returns the layer names.
     */
    public String[] getLayerNames() {
        final String[] ns = new String[layers.length];
        for(int i=0;i<layers.length;i++){
            ns[i] = NamesExt.toExtendedForm(layers[i]);
        }
        return ns;
    }

    /**
     * Returns a concatenated string of all layer names, separated by comma.
     */
    public String getCombinedLayerNames() {
        return StringUtilities.toCommaSeparatedValues((Object[])getLayerNames());
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
     * Sets the sldBody parameter.
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
     */
    public void setSldVersion(final String sldVersion) {
        this.sldVersion = sldVersion;
    }

    /**
     * Sets the format for the output response. By default sets to {@code image/png}
     * if none.
     *
     * @param format The mime type of an output format.
     */
    public void setFormat(final String format) {
        ensureNonNull("format", format);
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
    public void setExceptionsFormat(final String exceptionsFormat) {
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
    public void setTransparent(final Boolean transparent) {
        this.transparent = transparent;
    }

    public void setCrs84Politic(final CRS84Politic crs84Politic) {
        ensureNonNull("CRS84 politic", crs84Politic);
        this.crs84Politic = crs84Politic;
    }

    public CRS84Politic getCrs84Politic() {
        return crs84Politic;
    }

    public void setEpsg4326Politic(final EPSG4326Politic epsg4326Politic) {
        ensureNonNull("EPSG4326 politic", epsg4326Politic);
        this.epsg4326Politic = epsg4326Politic;
    }

    public EPSG4326Politic getEpsg4326Politic() {
        return epsg4326Politic;
    }

    /**
     * Define if the map layer must rely on the geotoolkit reprojection capabilities
     * if the distant server can not handle the canvas crs.
     * The result image might not be pretty, but still better than no image.
     */
    public void setUseLocalReprojection(final boolean useLocalReprojection) {
        this.useLocalReprojection = useLocalReprojection;
    }

    public boolean isUseLocalReprojection() {
        return useLocalReprojection;
    }

    /**
     * Set to true if the time parameter must be adjusted to match the closest
     * date provided in the layer getCapabilities.
     */
    public void setMatchCapabilitiesDates(final boolean matchCapabilitiesDates) {
        this.matchCapabilitiesDates = matchCapabilitiesDates;
    }

    public boolean isMatchCapabilitiesDates() {
        return matchCapabilitiesDates;
    }

    public Envelope getBounds() {
        if(env == null){
            try {
                env = WMSUtilities.getGridGeometry(server, getLayerNames()[0]).getEnvelope();
            } catch (CapabilitiesException ex) {
                LOGGER.log(Level.WARNING, ex.getMessage(), ex);
            }
            if(env == null){
                env = MAXEXTEND_ENV;
            }
        }
        return env;
    }

    @Override
    public GridGeometry getGridGeometry() throws DataStoreException {
        try {
            //we only know the envelope,
            return WMSUtilities.getGridGeometry(server, getLayerNames()[0]);
        } catch (CapabilitiesException ex) {
            return new GridGeometry(PixelInCell.CELL_CENTER, null, getBounds(), GridRoundingMode.ENCLOSING);
        }
    }

    @Override
    public List<SampleDimension> getSampleDimensions() throws DataStoreException {
        final List<SampleDimension> sd = new ArrayList<>();
        switch (format) {
            case "image/png" :
                //4 bands
                sd.add(new SampleDimension.Builder().setName("1").build());
                sd.add(new SampleDimension.Builder().setName("2").build());
                sd.add(new SampleDimension.Builder().setName("3").build());
                sd.add(new SampleDimension.Builder().setName("4").build());
                break;
            default :
                //3 bands
                sd.add(new SampleDimension.Builder().setName("1").build());
                sd.add(new SampleDimension.Builder().setName("2").build());
                sd.add(new SampleDimension.Builder().setName("3").build());

        }
        return sd;
    }

    @Override
    public GridCoverage read(GridGeometry domain, int... range) throws DataStoreException {
        if (domain == null) {
            domain = getGridGeometry();
        }

        if (range != null && range.length != 0) {
            throw new DataStoreException("Source or destination bands can not be used on WMS coverages.");
        }

        GeneralEnvelope env;
        if (domain.isDefined(GridGeometry.ENVELOPE)) {
            env = new GeneralEnvelope(domain.getEnvelope());
        } else {
            env = new GeneralEnvelope(getGridGeometry().getEnvelope());
        }

        CoordinateReferenceSystem crs = env.getCoordinateReferenceSystem();

        final CoordinateReferenceSystem crs2d;
        try {
            crs2d = CRSUtilities.getCRS2D(crs);
        } catch (TransformException ex) {
            throw new DataStoreException("WMS reading expect a CRS whose first component is 2D.", ex);
        }

        final CoordinateReferenceSystem candidateCRS = env.getDimension() > 2? crs : crs2d;
        if (env.getCoordinateReferenceSystem() == null) {
            env.setCoordinateReferenceSystem(candidateCRS);
        } else if (!Utilities.equalsIgnoreMetadata(env.getCoordinateReferenceSystem(), candidateCRS)) {
            try {
                env = GeneralEnvelope.castOrCopy(Envelopes.transform(env, candidateCRS));
            } catch (TransformException ex) {
                throw new DataStoreException("Could not transform coverage envelope to given crs.", ex);
            }
        }

        final Dimension dim;
        if (domain.isDefined(GridGeometry.EXTENT)) {
            GridExtent extent = domain.getExtent();
            dim = new Dimension(
                    (int) extent.getSize(0),
                    (int) extent.getSize(1));
        } else {
            dim = DEFAULT_SIZE;
        }

        final GetMapRequest request = server.createGetMap();

        //Filling the request header map from the map of the layer's server
        final Map<String, String> headerMap = server.getRequestHeaderMap();
        if (headerMap != null) {
            request.getHeaderMap().putAll(headerMap);
        }

        try {
            prepareQuery(request, env, dim, null);
            LOGGER.fine(request.getURL().toExternalForm());
        } catch (Exception ex) {
            throw new DataStoreException(ex.getMessage(), ex);
        }

        //read image
        try (final InputStream stream = request.getResponseStream()) {
            RenderedImage image = ImageIO.read(stream);

            //the envelope CRS may have been changed by prepareQuery method
            final CoordinateReferenceSystem resultCrs = CRS.getHorizontalComponent(env.getCoordinateReferenceSystem());
            final Envelope env2D = Envelopes.transform(env, resultCrs);
            //grid to crs returned is in corner
            final AffineTransform gridToCRS = ReferencingUtilities.toAffine(dim, env2D);

            //we must honor the number of sample dimensions we declared
            //some WMS services returned mixed sample and color model for better compression
            final List<SampleDimension> sampleDimensions = getSampleDimensions();
            if (sampleDimensions.size() != image.getSampleModel().getNumBands()) {
                BufferedImage cp;
                if (sampleDimensions.size() == 3) {
                    cp = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_RGB);
                } else if (sampleDimensions.size() == 4) {
                    cp = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_ARGB);
                } else {
                    throw new DataStoreException();
                }
                cp.createGraphics().drawRenderedImage(image, new AffineTransform());
                image = cp;
            }

            final GridExtent extent = new GridExtent(image.getWidth(), image.getHeight());
            final GridGeometry grid = new GridGeometry(extent, PixelInCell.CELL_CORNER, new AffineTransform2D(gridToCRS), resultCrs);
            return new GridCoverage2D(grid, sampleDimensions, image);

        } catch (IOException | TransformException ex) {
            throw new DataStoreException(ex.getMessage(), ex);
        }

    }

    /*************************  Queries functions *****************************/
    protected CoordinateReferenceSystem findOriginalCRS() throws FactoryException,CapabilitiesException {
        return WMSUtilities.findOriginalCRS(server,getLayerNames()[0]);
    }

    protected boolean supportCRS(CoordinateReferenceSystem crs2D) throws FactoryException, CapabilitiesException {
        return WMSUtilities.supportCRS(server,getLayerNames()[0],crs2D);
    }

    protected Long findClosestDate(long l) throws CapabilitiesException {
        return WMSUtilities.findClosestDate(server,getLayerNames()[0],(long)l);
    }

    /**
     * Gives a {@linkplain GetMapRequest GetMap request} for the given envelope and
     * output dimension. The default format will be {@code image/png} if the
     * {@link #setFormat(java.lang.String)} has not been called.
     *
     * @param env A valid envlope to request.
     * @param rect The dimension for the output response.
     * @return A {@linkplain GetMapRequest get map request}.
     * @throws MalformedURLException if the generated url is invalid.
     * @throws TransformException if the tranformation between 2 CRS failed.
     */
    public URL query(final Envelope env, final Dimension rect)
            throws MalformedURLException, TransformException, FactoryException {
        final GetMapRequest request = server.createGetMap();
        prepareQuery(request, new GeneralEnvelope(env), rect, null);
        return request.getURL();
    }

    /**
     * Prepare parameters for a getMap query.
     * The given parameters will be modified !
     */
    public void prepareQuery(final GetMapRequest request, final GeneralEnvelope env,
            final Dimension dim, final Point2D pickCoord) throws TransformException,
            FactoryException{

        //envelope before any modification
        GeneralEnvelope beforeEnv = new GeneralEnvelope(env);

        final CoordinateReferenceSystem crs = env.getCoordinateReferenceSystem();
        CoordinateReferenceSystem crs2D = CRSUtilities.getCRS2D(crs);
        GeneralEnvelope fakeEnv = new GeneralEnvelope(env);

        //check if we must make the  coverage reprojection ourself--------------
        boolean supportCRS = false;
        try {
            supportCRS = supportCRS(crs2D);
        } catch (CapabilitiesException ex) {
            LOGGER.log(Level.WARNING, ex.toString(), ex);
        }

        final boolean longitudeFirst = server.getVersion().longitudeFirst;
        if (isUseLocalReprojection() && !supportCRS) {
            try {
                crs2D = findOriginalCRS();
            } catch (CapabilitiesException ex) {
                //we tryed
                crs2D = null;
            }
            if(crs2D == null){
                //last chance use : EPSG:4326
                crs2D = CommonCRS.WGS84.geographic();
            }

            if (longitudeFirst && (Utilities.equalsIgnoreMetadata(crs2D, CommonCRS.WGS84.normalizedGeographic()))) {
                //in case we are asking for a WMS in 1.1.0 and CRS:84
                //we must change the crs to 4326 but with CRS:84 coordinate
                final GeneralEnvelope trsEnv = new GeneralEnvelope(ReferencingUtilities.transform2DCRS(env, CommonCRS.WGS84.normalizedGeographic()));
                env.setEnvelope(trsEnv);
                final CoordinateReferenceSystem fakeCrs = ReferencingUtilities.change2DComponent(crs, CommonCRS.WGS84.geographic());
                trsEnv.setCoordinateReferenceSystem(fakeCrs);
                fakeEnv.setEnvelope(trsEnv);
            }else if (longitudeFirst) {
                //in case we are asking for a WMS in 1.1.0 and a geographic crs
                //we must set longitude coordinates first but preserve the crs
                final CoordinateReferenceSystem lfcrs = ReferencingUtilities.setLongitudeFirst(crs2D);
                final GeneralEnvelope trsEnv = new GeneralEnvelope(ReferencingUtilities.transform2DCRS(env, lfcrs));
                env.setEnvelope(trsEnv);
                trsEnv.setCoordinateReferenceSystem(ReferencingUtilities.change2DComponent(crs, crs2D));
                fakeEnv.setEnvelope(trsEnv);
            } else {
                final GeneralEnvelope  trsEnv = new GeneralEnvelope(ReferencingUtilities.transform2DCRS(env, crs2D));
                env.setEnvelope(trsEnv);
                fakeEnv.setEnvelope(trsEnv);
            }

        }else{

            if (longitudeFirst && (Utilities.equalsIgnoreMetadata(crs2D, CommonCRS.WGS84.normalizedGeographic()))) {
                //in case we are asking for a WMS in 1.1.0 and CRS:84
                //we must change the crs to 4326 but with CRS:84 coordinate
                final GeneralEnvelope trsEnv = new GeneralEnvelope(env);
                final CoordinateReferenceSystem fakeCrs = ReferencingUtilities.change2DComponent(crs, CommonCRS.WGS84.geographic());
                trsEnv.setCoordinateReferenceSystem(fakeCrs);
                fakeEnv.setEnvelope(trsEnv);
            } else if (longitudeFirst) {
                //in case we are asking for a WMS in 1.1.0 and a geographic crs
                //we must set longitude coordinates first but preserve the crs
                final GeneralEnvelope trsEnv = new GeneralEnvelope(ReferencingUtilities.setLongitudeFirst(env));
                trsEnv.setCoordinateReferenceSystem(crs);
                fakeEnv.setEnvelope(trsEnv);
            }
        }

        //WMS returns images with EAST-WEST axis first, so we ensure we modify the crs as expected
        final Envelope longFirstEnvelope = ReferencingUtilities.setLongitudeFirst(env);
        env.setEnvelope(longFirstEnvelope);


        //Recalculate pick coordinate according to reverse transformation
        if(pickCoord != null){
            beforeEnv = (GeneralEnvelope) ReferencingUtilities.setLongitudeFirst(beforeEnv);

            //calculate new coordinate in the reprojected query
            final AffineTransform beforeTrs = ReferencingUtilities.toAffine(dim,beforeEnv);
            final AffineTransform afterTrs = ReferencingUtilities.toAffine(dim,env);
            try {
                afterTrs.invert();
            } catch (NoninvertibleTransformException ex) {
                throw new TransformException("Failed to invert transform.",ex);
            }

            beforeTrs.transform(pickCoord, pickCoord);

            final DirectPosition pos = new GeneralDirectPosition(env.getCoordinateReferenceSystem());
            pos.setOrdinate(0, pickCoord.getX());
            pos.setOrdinate(1, pickCoord.getY());

            final MathTransform trs = CRS.findOperation(beforeEnv.getCoordinateReferenceSystem(), env.getCoordinateReferenceSystem(), null).getMathTransform();
            trs.transform(pos, pos);

            pickCoord.setLocation(pos.getOrdinate(0), pos.getOrdinate(1));
            afterTrs.transform(pickCoord, pickCoord);
        }

        prepareGetMapRequest(request, fakeEnv, dim);
    }

    /**
     * Prepare parameters for a GetMap request.
     *
     * @param request the GetMap request
     * @param env A valid envelope to request.
     * @param rect the output dimension
     * @throws TransformException
     */
    private void prepareGetMapRequest(final GetMapRequest request, Envelope env,
            final Dimension rect) throws TransformException{

        //check the politics, the distant wms server might not be strict on axis orders
        // nor in it's crs definitions between CRS:84 and EPSG:4326
        final CoordinateReferenceSystem crs2D = CRSUtilities.getCRS2D(env.getCoordinateReferenceSystem());


        //we loose the vertical and temporale crs in the process, must be fixed
        //check CRS84 politic---------------------------------------------------
        if (crs84Politic != CRS84Politic.STRICT) {
            if (Utilities.equalsIgnoreMetadata(crs2D, CommonCRS.WGS84.normalizedGeographic())) {

                switch (crs84Politic) {
                    case CONVERT_TO_EPSG4326:
                        env = Envelopes.transform(env, crs2D);
                        env = new GeneralEnvelope(env);
                        ((GeneralEnvelope) env).setCoordinateReferenceSystem(CommonCRS.WGS84.geographic());
                        break;
                }
            }
        }

        //check EPSG4326 politic------------------------------------------------
        if (epsg4326Politic != EPSG4326Politic.STRICT) {
            if (Utilities.equalsIgnoreMetadata(crs2D, CommonCRS.WGS84.geographic())) {
                switch (epsg4326Politic) {
                    case CONVERT_TO_CRS84:
                        env = Envelopes.transform(env, crs2D);
                        env = new GeneralEnvelope(env);
                        ((GeneralEnvelope) env).setCoordinateReferenceSystem(CommonCRS.WGS84.normalizedGeographic());
                        break;
                }
            }
        }

        if(matchCapabilitiesDates){
            final CoordinateReferenceSystem crs = env.getCoordinateReferenceSystem();
            final int index = dimensionColinearWith(crs.getCoordinateSystem(), CommonCRS.Temporal.JULIAN.crs().getCoordinateSystem().getAxis(0));
            if(index >= 0){
                //there is a temporal axis
                final double median = env.getMedian(index);
                Long closest = null;
                try {
                    closest = findClosestDate((long)median);
                } catch (CapabilitiesException ex) {
                    //at least we tryed
                }
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
        request.setLayers(getLayerNames());
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
        request.dimensions().putAll(dimensions());
    }

    public Image getLegend() throws DataStoreException {
        final BufferedImage image;
        try {
            final Request getLegend =queryLegend(null, "image/png", null, null);
            image = ImageIO.read(getLegend.getResponseStream());
        } catch (IOException e) {
            throw new DataStoreException(e);
        }
        return image;
    }

    /**
     * Gives a {@linkplain GetLegendRequest GetLegendGraphic request} for
     * the given dimension. The default format will be {@code image/png} if the
     * {@link #setFormat(java.lang.String)} has not been called.
     *
     * @param rect     the dimension of the image drawn
     * @param format   the format of the image drawn
     * @param rule     the SLD rule to draw
     * @param scale    the scale level of the SLD rule to draw
     * @return A {@linkplain GetLegendRequest GetLegendGraphic request}.
     * @throws MalformedURLException if the generated url is invalid.
     */
    public GetLegendRequest queryLegend(final Dimension rect, final String format,
            final String rule, final Double scale) throws MalformedURLException {
        final GetLegendRequest request = server.createGetLegend();
        prepareGetLegendRequest(request, rect, format, rule, scale);
        return request;
    }

    /**
     * Prepare parameters for a GetLegendGraphic request.
     *
     * @param request  the GetLegend request
     * @param rect     the dimension of the image drawn
     * @param format   the format of the image drawn
     * @param rule     the SLD rule to draw
     * @param scale    the scale level of the SLD rule to draw
     */
    protected void prepareGetLegendRequest(final GetLegendRequest request,
            final Dimension rect, final String format, final String rule,
            final Double scale) {

        request.setDimension(rect);
        request.setFormat(format);
        request.setExceptions(exceptionsFormat);
        request.setLayer(getLayerNames()[0]);

        if (styles.length > 0) {
            request.setStyle(styles[0]);
        }

        request.setSld(sld);
        request.setSldBody(sldBody);
        request.setRule(rule);
        request.setScale(scale);
        request.setSldVersion(sldVersion);
        request.dimensions().putAll(dimensions());
    }

    /**
     * Gives a {@linkplain GetFeatureInfoRequest GetFeatureInfo request} for the
     * given envelope and output dimension. The default format will be
     * {@code image/png} if the {@link #setFormat(java.lang.String)} has not
     * been called.
     *
     * @param env          the current envelope of the map
     * @param rect         the dimension of the map
     * @param x            X coordinate of the point
     * @param y            Y coordinate of the point
     * @param queryLayers   layers to query
     * @param infoFormat    output format of the GetFeatureInfo response
     * @param featureCount  max number of features to retrieve
     * @return A {@linkplain GGetFeatureInfoRequest GetFeatureInfo request}.
     * @throws TransformException
     * @throws FactoryException
     * @throws MalformedURLException if the generated url is invalid.
     */
    public GetFeatureInfoRequest queryFeatureInfo(final Envelope env, final Dimension rect, int x,
            int y, final String[] queryLayers, final String infoFormat,
            final int featureCount) throws TransformException, FactoryException {
        final GetFeatureInfoRequest request = server.createGetFeatureInfo();
        prepareGetFeatureInfoRequest(request, env, rect, x, y, queryLayers,
                                     infoFormat, featureCount);
        return request;
    }

    /**
     * Prepare parameters for a GetFeatureInfo request.
     *
     * @param request      the GetFeatureInfo request
     * @param env          the current envelope of the map
     * @param rect         the dimension of the map
     * @param x            X coordinate of the point
     * @param y            Y coordinate of the point
     * @param queryLayers   layers to query
     * @param infoFormat    output format of the GetFeatureInfo response
     * @param featureCount  max number of features to retrieve
     * @throws TransformException
     * @throws FactoryException
     * @throws MalformedURLException
     */
    protected void prepareGetFeatureInfoRequest(final GetFeatureInfoRequest request,
            final Envelope env, final Dimension rect, int x, int y,
            final String[] queryLayers, final String infoFormat, final int featureCount)
            throws TransformException, FactoryException {
        request.setQueryLayers(queryLayers);
        request.setInfoFormat(infoFormat);
        request.setFeatureCount(featureCount);

        final GeneralEnvelope cenv = new GeneralEnvelope(env);
        final Dimension crect = new Dimension(rect);
        final Point2D pickCoord = new Point2D.Double(x, y);

        // Add the GetMap parameters
        prepareQuery(request, cenv, crect, pickCoord);

        request.setColumnIndex( (int)Math.round(pickCoord.getX()) );
        request.setRawIndex( (int)Math.round(pickCoord.getY()) );
    }

    /**
     * Returns the dimension within the coordinate system of the first occurrence of an axis
     * colinear with the specified axis. If an axis with the same
     * {@linkplain CoordinateSystemAxis#getDirection direction} or an
     * {@linkplain AxisDirections#opposite opposite} direction than {@code axis}
     * occurs in the coordinate system, then the dimension of the first such occurrence
     * is returned. That is, the value <var>k</var> such that:
     *
     * {@preformat java
     *     absolute(cs.getAxis(k).getDirection()) == absolute(axis.getDirection())
     * }
     *
     * is {@code true}. If no such axis occurs in this coordinate system,
     * then {@code -1} is returned.
     * <p>
     * For example, {@code dimensionColinearWith(DefaultCoordinateSystemAxis.TIME)}
     * returns the dimension number of time axis.
     *
     * @param  cs   The coordinate system to examine.
     * @param  axis The axis to look for.
     * @return The dimension number of the specified axis, or {@code -1} if none.
     */
    public static int dimensionColinearWith(final CoordinateSystem     cs,
                                            final CoordinateSystemAxis axis)
    {
        int candidate = -1;
        final int dimension = cs.getDimension();
        final AxisDirection direction = AxisDirections.absolute(axis.getDirection());
        for (int i=0; i<dimension; i++) {
            final CoordinateSystemAxis xi = cs.getAxis(i);
            if (direction.equals(AxisDirections.absolute(xi.getDirection()))) {
                candidate = i;
                if (axis.equals(xi)) {
                    break;
                }
            }
        }
        return candidate;
    }


}
