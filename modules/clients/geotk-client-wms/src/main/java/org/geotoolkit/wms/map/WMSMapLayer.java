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
import org.geotoolkit.client.Request;
import org.geotoolkit.coverage.CoverageReference;
import org.apache.sis.geometry.GeneralEnvelope;
import org.geotoolkit.map.DefaultCoverageMapLayer;
import org.geotoolkit.style.DefaultStyleFactory;
import org.geotoolkit.style.StyleConstants;
import org.geotoolkit.wms.*;
import org.geotoolkit.wms.WMSCoverageReference.CRS84Politic;
import org.geotoolkit.wms.WMSCoverageReference.EPSG4326Politic;
import org.opengis.geometry.Envelope;
import org.opengis.referencing.operation.TransformException;
import org.opengis.util.FactoryException;


/**
 * Map representation of a WMS layer.
 *
 * @author Johann Sorel (Geomatys)
 * @author Cédric Briançon (Geomatys)
 * @module pending
 */
public class WMSMapLayer extends DefaultCoverageMapLayer {

    private static WMSCoverageReference toReference(final WebMapClient server, final String... layers){
        return new WMSCoverageReference(server, layers);
    }

    public WMSMapLayer(final WebMapClient server, final String... layers) {
        this(toReference(server, layers));
        graphicBuilders().add(WMSGraphicBuilder.INSTANCE);
    }

    protected WMSMapLayer(final CoverageReference ref){
        super(ref,new DefaultStyleFactory().style(StyleConstants.DEFAULT_RASTER_SYMBOLIZER));
    }

    /**
     * Configuration of the requested coverage.
     * @return WMSCoverageReference , never null
     */
    @Override
    public WMSCoverageReference getCoverageReference() {
        return (WMSCoverageReference) super.getCoverageReference();
    }

    /**
     * @deprecated use getCoverageReference() methods
     */
    public void setLayerNames(final String... names) {
        getCoverageReference().setLayerNames(names);
    }

    /**
     * @deprecated use getCoverageReference() methods
     */
    public String[] getLayerNames() {
        return getCoverageReference().getLayerNames();
    }

    /**
     * @deprecated use getCoverageReference() methods
     */
    public String getCombinedLayerNames() {
        return getCoverageReference().getCombinedLayerNames();
    }

    /**
     * @deprecated use getCoverageReference() methods
     */
    public void setStyles(final String... styles) {
        getCoverageReference().setStyles(styles);
    }

    /**
     * @deprecated use getCoverageReference() methods
     */
    public String[] getStyles() {
        return getCoverageReference().getStyles();
    }

    /**
     * @deprecated use getCoverageReference() methods
     */
    public void setSld(final String sld) {
        getCoverageReference().setSld(sld);
    }

    /**
     * @deprecated use getCoverageReference() methods
     */
    public String getSld() {
        return getCoverageReference().getSld();
    }

    /**
     * @deprecated use getCoverageReference() methods
     */
    public void setSldBody(final String sldBody) {
        getCoverageReference().setSldBody(sldBody);
    }

    /**
     * @deprecated use getCoverageReference() methods
     */
    public String getSldBody() {
        return getCoverageReference().getSldBody();
    }

    /**
     * @deprecated use getCoverageReference() methods
     */
    public String getSldVersion() {
        return getCoverageReference().getSldVersion();
    }

    /**
     * @deprecated use getCoverageReference() methods
     */
    public void setSldVersion(final String sldVersion) {
        getCoverageReference().setSldVersion(sldVersion);
    }

    /**
     * @deprecated use getCoverageReference() methods
     */
    public void setFormat(final String format) {
        getCoverageReference().setFormat(format);
    }

    /**
     * @deprecated use getCoverageReference() methods
     */
    public String getFormat() {
        return getCoverageReference().getFormat();
    }

    /**
     * @deprecated use getCoverageReference() methods
     */
    public Map<String, String> dimensions() {
        return getCoverageReference().dimensions();
    }

    /**
     * @deprecated use getCoverageReference() methods
     */
    public String getExceptionsFormat() {
        return getCoverageReference().getExceptionsFormat();
    }

    /**
     * @deprecated use getCoverageReference() methods
     */
    public void setExceptionsFormat(final String exceptionsFormat) {
        getCoverageReference().setExceptionsFormat(exceptionsFormat);
    }

    /**
     * @deprecated use getCoverageReference() methods
     */
    public Boolean isTransparent() {
        return getCoverageReference().isTransparent();
    }

    /**
     * @deprecated use getCoverageReference() methods
     */
    public void setTransparent(final Boolean transparent) {
        getCoverageReference().setTransparent(transparent);
    }

    /**
     * @deprecated use getCoverageReference() methods
     */
    public WebMapClient getServer() {
        return (WebMapClient)getCoverageReference().getStore();
    }

    /**
     * @deprecated use getCoverageReference() methods
     */
    public void setCrs84Politic(final CRS84Politic crs84Politic) {
        getCoverageReference().setCrs84Politic(crs84Politic);
    }

    /**
     * @deprecated use getCoverageReference() methods
     */
    public CRS84Politic getCrs84Politic() {
        return getCoverageReference().getCrs84Politic();
    }

    /**
     * @deprecated use getCoverageReference() methods
     */
    public void setEpsg4326Politic(final EPSG4326Politic epsg4326Politic) {
        getCoverageReference().setEpsg4326Politic(epsg4326Politic);
    }

    /**
     * @deprecated use getCoverageReference() methods
     */
    public EPSG4326Politic getEpsg4326Politic() {
        return getCoverageReference().getEpsg4326Politic();
    }

    /**
     * @deprecated use getCoverageReference() methods
     */
    public void setUseLocalReprojection(final boolean useLocalReprojection) {
        getCoverageReference().setUseLocalReprojection(useLocalReprojection);
    }

    /**
     * @deprecated use getCoverageReference() methods
     */
    public boolean isUseLocalReprojection() {
        return getCoverageReference().isUseLocalReprojection();
    }

    /**
     * @deprecated use getCoverageReference() methods
     */
    public void setMatchCapabilitiesDates(final boolean matchCapabilitiesDates) {
        getCoverageReference().setMatchCapabilitiesDates(matchCapabilitiesDates);
    }

    /**
     * @deprecated use getCoverageReference() methods
     */
    public boolean isMatchCapabilitiesDates() {
        return getCoverageReference().isMatchCapabilitiesDates();
    }

    /**
     * @deprecated use getCoverageReference() methods
     */
    public URL query(final Envelope env, final Dimension rect)
            throws MalformedURLException, TransformException, FactoryException {
        return getCoverageReference().query(env, rect);
    }

    /**
     * @deprecated use getCoverageReference() methods
     */
    public void prepareQuery(final GetMapRequest request, final GeneralEnvelope env,
            final Dimension dim, final Point2D pickCoord) throws TransformException,
            FactoryException{
        getCoverageReference().prepareQuery(request, env, dim, pickCoord);
    }

    /**
     * @deprecated use getCoverageReference() methods
     */
    public URL queryLegend(final Dimension rect, final String format,
            final String rule, final Double scale) throws MalformedURLException {
        return getCoverageReference().queryLegend(rect, format, rule, scale).getURL();
    }

    /**
     * @deprecated use getCoverageReference() methods
     */
    public URL queryFeatureInfo(final Envelope env, final Dimension rect, int x,
            int y, final String[] queryLayers, final String infoFormat,
            final int featureCount) throws TransformException, FactoryException,
            MalformedURLException {
        return getCoverageReference().queryFeatureInfo(env, rect, x, y, queryLayers, infoFormat, featureCount).getURL();
    }

}
