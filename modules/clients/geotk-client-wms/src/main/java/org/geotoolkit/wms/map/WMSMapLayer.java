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
package org.geotoolkit.wms.map;

import java.awt.Dimension;
import java.awt.geom.Point2D;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;
import org.apache.sis.geometry.GeneralEnvelope;
import org.apache.sis.storage.GridCoverageResource;
import org.geotoolkit.map.DefaultCoverageMapLayer;
import org.geotoolkit.style.DefaultStyleFactory;
import org.geotoolkit.style.StyleConstants;
import org.geotoolkit.wms.*;
import org.geotoolkit.wms.WMSCoverageResource.CRS84Politic;
import org.geotoolkit.wms.WMSCoverageResource.EPSG4326Politic;
import org.opengis.geometry.Envelope;
import org.opengis.referencing.operation.TransformException;
import org.opengis.util.FactoryException;


/**
 * Map representation of a WMS layer.
 *
 * @author Johann Sorel (Geomatys)
 * @author Cédric Briançon (Geomatys)
 * @module
 */
public class WMSMapLayer extends DefaultCoverageMapLayer {

    private static WMSCoverageResource toReference(final WebMapClient server, final String... layers){
        return new WMSCoverageResource(server, layers);
    }

    public WMSMapLayer(final WebMapClient server, final String... layers) {
        this(toReference(server, layers));
        graphicBuilders().add(WMSGraphicBuilder.INSTANCE);
    }

    protected WMSMapLayer(final GridCoverageResource ref){
        super(ref, new DefaultStyleFactory().style(StyleConstants.DEFAULT_RASTER_SYMBOLIZER));
    }

    /**
     * Configuration of the requested coverage.
     * @return WMSCoverageResource , never null
     */
    @Override
    public WMSCoverageResource getResource() {
        return (WMSCoverageResource) super.getResource();
    }

    /**
     * @deprecated use getCoverageReference() methods
     */
    public void setLayerNames(final String... names) {
        getResource().setLayerNames(names);
    }

    /**
     * @deprecated use getCoverageReference() methods
     */
    public String[] getLayerNames() {
        return getResource().getLayerNames();
    }

    /**
     * @deprecated use getCoverageReference() methods
     */
    public String getCombinedLayerNames() {
        return getResource().getCombinedLayerNames();
    }

    /**
     * @deprecated use getCoverageReference() methods
     */
    public void setStyles(final String... styles) {
        getResource().setStyles(styles);
    }

    /**
     * @deprecated use getCoverageReference() methods
     */
    public String[] getStyles() {
        return getResource().getStyles();
    }

    /**
     * @deprecated use getCoverageReference() methods
     */
    public void setSld(final String sld) {
        getResource().setSld(sld);
    }

    /**
     * @deprecated use getCoverageReference() methods
     */
    public String getSld() {
        return getResource().getSld();
    }

    /**
     * @deprecated use getCoverageReference() methods
     */
    public void setSldBody(final String sldBody) {
        getResource().setSldBody(sldBody);
    }

    /**
     * @deprecated use getCoverageReference() methods
     */
    public String getSldBody() {
        return getResource().getSldBody();
    }

    /**
     * @deprecated use getCoverageReference() methods
     */
    public String getSldVersion() {
        return getResource().getSldVersion();
    }

    /**
     * @deprecated use getCoverageReference() methods
     */
    public void setSldVersion(final String sldVersion) {
        getResource().setSldVersion(sldVersion);
    }

    /**
     * @deprecated use getCoverageReference() methods
     */
    public void setFormat(final String format) {
        getResource().setFormat(format);
    }

    /**
     * @deprecated use getCoverageReference() methods
     */
    public String getFormat() {
        return getResource().getFormat();
    }

    /**
     * @deprecated use getCoverageReference() methods
     */
    public Map<String, String> dimensions() {
        return getResource().dimensions();
    }

    /**
     * @deprecated use getCoverageReference() methods
     */
    public String getExceptionsFormat() {
        return getResource().getExceptionsFormat();
    }

    /**
     * @deprecated use getCoverageReference() methods
     */
    public void setExceptionsFormat(final String exceptionsFormat) {
        getResource().setExceptionsFormat(exceptionsFormat);
    }

    /**
     * @deprecated use getCoverageReference() methods
     */
    public Boolean isTransparent() {
        return getResource().isTransparent();
    }

    /**
     * @deprecated use getCoverageReference() methods
     */
    public void setTransparent(final Boolean transparent) {
        getResource().setTransparent(transparent);
    }

    /**
     * @deprecated use getCoverageReference() methods
     */
    public WebMapClient getServer() {
        return (WebMapClient)getResource().getOriginator();
    }

    /**
     * @deprecated use getCoverageReference() methods
     */
    public void setCrs84Politic(final CRS84Politic crs84Politic) {
        getResource().setCrs84Politic(crs84Politic);
    }

    /**
     * @deprecated use getCoverageReference() methods
     */
    public CRS84Politic getCrs84Politic() {
        return getResource().getCrs84Politic();
    }

    /**
     * @deprecated use getCoverageReference() methods
     */
    public void setEpsg4326Politic(final EPSG4326Politic epsg4326Politic) {
        getResource().setEpsg4326Politic(epsg4326Politic);
    }

    /**
     * @deprecated use getCoverageReference() methods
     */
    public EPSG4326Politic getEpsg4326Politic() {
        return getResource().getEpsg4326Politic();
    }

    /**
     * @deprecated use getCoverageReference() methods
     */
    public void setUseLocalReprojection(final boolean useLocalReprojection) {
        getResource().setUseLocalReprojection(useLocalReprojection);
    }

    /**
     * @deprecated use getCoverageReference() methods
     */
    public boolean isUseLocalReprojection() {
        return getResource().isUseLocalReprojection();
    }

    /**
     * @deprecated use getCoverageReference() methods
     */
    public void setMatchCapabilitiesDates(final boolean matchCapabilitiesDates) {
        getResource().setMatchCapabilitiesDates(matchCapabilitiesDates);
    }

    /**
     * @deprecated use getCoverageReference() methods
     */
    public boolean isMatchCapabilitiesDates() {
        return getResource().isMatchCapabilitiesDates();
    }

    /**
     * @deprecated use getCoverageReference() methods
     */
    public URL query(final Envelope env, final Dimension rect)
            throws MalformedURLException, TransformException, FactoryException {
        return getResource().query(env, rect);
    }

    /**
     * @deprecated use getCoverageReference() methods
     */
    public void prepareQuery(final GetMapRequest request, final GeneralEnvelope env,
            final Dimension dim, final Point2D pickCoord) throws TransformException,
            FactoryException{
        getResource().prepareQuery(request, env, dim, pickCoord);
    }

    /**
     * @deprecated use getCoverageReference() methods
     */
    public URL queryLegend(final Dimension rect, final String format,
            final String rule, final Double scale) throws MalformedURLException {
        return getResource().queryLegend(rect, format, rule, scale).getURL();
    }

    /**
     * @deprecated use getCoverageReference() methods
     */
    public URL queryFeatureInfo(final Envelope env, final Dimension rect, int x,
            int y, final String[] queryLayers, final String infoFormat,
            final int featureCount) throws TransformException, FactoryException,
            MalformedURLException {
        return getResource().queryFeatureInfo(env, rect, x, y, queryLayers, infoFormat, featureCount).getURL();
    }

}
