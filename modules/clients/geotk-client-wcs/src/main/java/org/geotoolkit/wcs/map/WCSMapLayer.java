/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2010, Johann Sorel
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
package org.geotoolkit.wcs.map;

import java.awt.Dimension;
import java.net.MalformedURLException;
import java.net.URL;

import org.apache.sis.referencing.CommonCRS;
import org.apache.sis.geometry.Envelope2D;
import org.apache.sis.storage.Resource;
import org.geotoolkit.map.AbstractMapLayer;
import org.geotoolkit.style.DefaultStyleFactory;
import org.geotoolkit.wcs.GetCoverageRequest;
import org.geotoolkit.wcs.WebCoverageClient;

import org.opengis.geometry.Envelope;


/**
 * Map representation of a WCS layer.
 *
 * @author Johann Sorel
 * @module
 */
public class WCSMapLayer extends AbstractMapLayer {

    //TODO : we should use the envelope provided by the wms capabilities
    private static final Envelope MAXEXTEND_ENV = new Envelope2D(CommonCRS.WGS84.normalizedGeographic(), -180, -90, 360, 180);

    /**
     * The web coverage server to request.
     */
    private final WebCoverageClient server;

    /**
     * The layer to request.
     */
    private String layer;

    /**
     * Output format of the response.
     */
    private String format = "image/png";

    public WCSMapLayer(final WebCoverageClient server, final String layer) {
        super(new DefaultStyleFactory().style());
        this.server = server;
        this.layer = layer;

        //register the default graphic builder for geotk 2D engine.
        graphicBuilders().add(WCSGraphicBuilder.INSTANCE);
    }

    @Override
    public Resource getResource() {
        return null;
    }

    /**
     * Returns the {@link WebCoverageClient} to request. Can't be {@code null}.
     */
    public WebCoverageClient getServer() {
        return server;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public Envelope getBounds() {
        return MAXEXTEND_ENV;
    }

    /**
     * Creates the {@linkplain GetCoverageRequest get coverage request} object.
     *
     * @return A {@linkplain GetCoverageRequest get coverage request} object containing the
     *         predefined parameters.
     */
    public GetCoverageRequest createGetMapRequest() {
        final GetCoverageRequest request = server.createGetCoverage();
        request.setCoverage(layer);
        return request;
    }

    URL query(final Envelope env, final Dimension dim) throws MalformedURLException {

        final GetCoverageRequest request = server.createGetCoverage();
        request.setEnvelope(env);
        request.setDimension(dim);
        request.setCoverage(layer);
        request.setResponseCRS(env.getCoordinateReferenceSystem());
        request.setFormat(format);
        return request.getURL();
    }

    /**
     * Sets the layer names to requests.
     *
     * @param names Array of layer names.
     */
    public void setLayerName(final String name) {
        this.layer = name;
    }

    /**
     * Returns the layer names.
     */
    public String getLayerName() {
        return layer;
    }

    /**
     * Sets the format for the output response. By default sets to {@code image/png}
     * if none.
     *
     * @param format The mime type of an output format.
     */
    public void setFormat(String format) {
        this.format = format;
        if (this.format == null) {
            format = "image/png";
        }
    }

    /**
     * Gets the format for the output response. By default {@code image/png}.
     */
    public String getFormat() {
        return format;
    }

}
