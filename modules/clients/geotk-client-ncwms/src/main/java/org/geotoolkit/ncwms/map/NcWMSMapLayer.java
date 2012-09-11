/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2011, Geomatys
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
package org.geotoolkit.ncwms.map;

import java.net.MalformedURLException;
import java.net.URL;

import java.awt.Dimension;
import java.awt.geom.Point2D;

import org.geotoolkit.geometry.GeneralEnvelope;
import org.geotoolkit.ncwms.NcWMSCoverageReference;
import org.geotoolkit.ncwms.NcWebMapServer;
import org.geotoolkit.wms.GetMapRequest;
import org.geotoolkit.wms.map.WMSMapLayer;

import org.opengis.geometry.Envelope;
import org.opengis.referencing.operation.TransformException;
import org.opengis.util.FactoryException;


/**
 * Map representation of a ncWMS layer.
 *
 * @author Olivier Terral (Geomatys)
 * @module pending
 */
public class NcWMSMapLayer extends WMSMapLayer {
    
    private static NcWMSCoverageReference toReference(final NcWebMapServer server, final String... layers) {
        return new NcWMSCoverageReference(server, layers);
    }
    
    public NcWMSMapLayer(final NcWebMapServer server, final String... layers) {
        super(toReference(server, layers));
    }
    
    @Override
    public NcWMSCoverageReference getCoverageReference(){
        return (NcWMSCoverageReference) super.getCoverageReference();
    }
    
    /**
     * @deprecated use getCoverageReference() methods
     */
    public void setOpacity(final Integer opacity) {
        getCoverageReference().setOpacity(opacity);
    } 

    /**
     * @deprecated use getCoverageReference() methods
     */
    public Integer getNumColorBands() {
        return getCoverageReference().getNumColorBands();
    }

    /**
     * @deprecated use getCoverageReference() methods
     */
    public void setNumColorBands(Integer numColorBands) {
        getCoverageReference().setNumColorBands(numColorBands);
    }

    /**
     * @deprecated use getCoverageReference() methods
     */
    public Boolean isLogScale() {
        return getCoverageReference().isLogScale();
    }

    /**
     * @deprecated use getCoverageReference() methods
     */
    public void setLogScale(Boolean logScale) {
        getCoverageReference().setLogScale(logScale);
    }
    
    /**
     * @deprecated use getCoverageReference() methods
     */
    @Override
    public void prepareQuery(final GetMapRequest request, final GeneralEnvelope env,
            final Dimension dim, final Point2D pickCoord) throws TransformException,
            FactoryException{
        getCoverageReference().prepareQuery(request, env, dim, pickCoord);     
    }
        
    /**
     * @deprecated use getCoverageReference() methods
     */
    @Override
    public URL query(final Envelope env, final Dimension rect) throws MalformedURLException, 
    TransformException, FactoryException {
        return getCoverageReference().query(env, rect);
    }    

    /**
     * @deprecated use getCoverageReference() methods
     */
    @Override
    public URL queryFeatureInfo(final Envelope env, final Dimension rect, int x,
            int y, final String[] queryLayers, final String infoFormat, 
            final int featureCount) throws TransformException, FactoryException,
            MalformedURLException {
        return getCoverageReference().queryFeatureInfo(env, rect, x, y, queryLayers, infoFormat, featureCount);
    }
    
    /**
     * @deprecated use getCoverageReference() methods
     */
    @Override
    public URL queryLegend(final Dimension rect, final String format, final String rule, 
            final Double scale) throws MalformedURLException {
        return getCoverageReference().queryLegend(rect, format, rule, scale);
    }
        
    /**
     * @deprecated use getCoverageReference() methods
     */
    public URL queryMetadataLayerDetails() throws MalformedURLException {
        return getCoverageReference().queryMetadataLayerDetails();
    }
    
    /**
     * @deprecated use getCoverageReference() methods
     */
    public URL queryMetadataAnimationTimesteps(final String  start, final String end) throws MalformedURLException {
        return getCoverageReference().queryMetadataAnimationTimesteps(start, end);
    }
    
    /**
     * @deprecated use getCoverageReference() methods
     */
    public URL queryMetadataTimesteps() throws MalformedURLException {
        return getCoverageReference().queryMetadataTimesteps();
    }
    
    /**
     * @deprecated use getCoverageReference() methods
     */
    public URL queryMetadataMinmax(final String crsCode, final String bbox, final String width, final String height) throws MalformedURLException {
        return getCoverageReference().queryMetadataMinmax(crsCode, bbox, width, height);
    }
    
    /**
     * @deprecated use getCoverageReference() methods
     */
    public URL queryTransect(final String crsCode, final String lineString, 
            final String outputFormat) throws MalformedURLException {
        return getCoverageReference().queryTransect(crsCode, lineString, outputFormat);
    }
    
    /**
     * @deprecated use getCoverageReference() methods
     */
    public URL queryVerticalProfile(final String crsCode, float x, float y, 
            final String outputFormat) throws MalformedURLException {
        return getCoverageReference().queryVerticalProfile(crsCode, x, y, outputFormat);
    }
    
    /**
     * @deprecated use getCoverageReference() methods
     */
    public URL queryTimeseries(final Envelope env, final Dimension rect, int x,
            int y, final String infoFormat, 
            final String dateBegin, final String dateEnd) throws MalformedURLException, TransformException, FactoryException {
        return getCoverageReference().queryTimeseries(env, rect, x, y, infoFormat, dateBegin, dateEnd);
    }
}
