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
package org.geotoolkit.googlemaps.map;

import org.geotoolkit.geometry.Envelope2D;
import org.geotoolkit.googlemaps.GetMapRequest;
import org.geotoolkit.googlemaps.StaticGoogleMapsServer;
import org.geotoolkit.map.AbstractMapLayer;
import org.geotoolkit.style.DefaultStyleFactory;
import org.geotoolkit.util.ArgumentChecks;

import org.opengis.geometry.Envelope;

import static org.geotoolkit.referencing.crs.DefaultGeographicCRS.*;
import static org.geotoolkit.util.ArgumentChecks.*;


/**
 * Map representation of a Static GoogleMaps layer.
 *
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public class GoogleMapsMapLayer extends AbstractMapLayer {    

    //google maps are full world extend
    private static final Envelope MAXEXTEND_ENV = new Envelope2D(WGS84, -180,
                                                                 -90, 360, 180);             

    /**
     * The server to request.
     */
    private final StaticGoogleMapsServer server;

    /**
     * Output format of the response.
     */
    private String format = GetMapRequest.FORMAT_PNG8;
    
    /**
     * The layer map type.
     */
    private String mapType = GetMapRequest.TYPE_ROADMAP;

    
    public GoogleMapsMapLayer(final StaticGoogleMapsServer server) {
        super(new DefaultStyleFactory().style());
        this.server = server;

        //register the default graphic builder for geotk 2D engine.
        graphicBuilders().add(GoogleMapsGraphicBuilder.INSTANCE);
    }
    
    /**
     * Sets the map type to request.
     *
     * @param names Array of layer names.
     */
    public void setMapType(final String type) {
        ArgumentChecks.ensureNonNull("map type", type);
        this.mapType = type;
    }

    /**
     * Returns the map type.
     */
    public String getMapType() {
        return mapType;
    }

    /**
     * Sets the format for the output response. By default sets to {@code png}.     *
     * @param format 
     */
    public void setFormat(final String format) {
        ensureNonNull("format", format);
        this.format = format;
    }

    /**
     * Gets the format for the output response. By default {@code png}.
     */
    public String getFormat() {
        return format;
    }

    /**
     * Returns the {@link StaticGoogleMapsServer} to request. Can't be {@code null}.
     */
    public StaticGoogleMapsServer getServer() {
        return server;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public Envelope getBounds() {
        return MAXEXTEND_ENV;
    }
   
}
