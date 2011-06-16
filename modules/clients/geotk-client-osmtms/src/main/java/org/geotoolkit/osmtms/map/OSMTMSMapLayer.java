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
package org.geotoolkit.osmtms.map;

import org.geotoolkit.geometry.Envelope2D;
import org.geotoolkit.map.AbstractMapLayer;
import org.geotoolkit.style.DefaultStyleFactory;
import org.geotoolkit.osmtms.OSMTileMapServer;

import org.opengis.geometry.Envelope;

import static org.geotoolkit.util.ArgumentChecks.*;
import static org.geotoolkit.referencing.crs.DefaultGeographicCRS.*;


/**
 * Map representation of a TMS layer.
 *
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public class OSMTMSMapLayer extends AbstractMapLayer {    

    //full world extend
    private static final Envelope MAXEXTEND_ENV = new Envelope2D(WGS84, -180,
                                                                 -90, 360, 180);            
    
    /**
     * The server to request.
     */
    private final OSMTileMapServer server;
    
    /**
     * Query extension.
     */
    private String format = ".png";
    
    
    public OSMTMSMapLayer(final OSMTileMapServer server) {
        super(new DefaultStyleFactory().style());
        this.server = server;

        //register the default graphic builder for geotk 2D engine.
        graphicBuilders().add(OSMTMSGraphicBuilder.INSTANCE);
    }
    
    /**
     * Sets the extension for the output response. By default sets to {@code .png}.
     * @param format 
     */
    public void setFormat(final String format) {
        ensureNonNull("format", format);
        this.format = format;
    }

    /**
     * Gets the extension for the output response. By default {@code .png}.
     */
    public String getFormat() {
        return format;
    }

    /**
     * Returns the {@link StaticGoogleMapsServer} to request. Can't be {@code null}.
     */
    public OSMTileMapServer getServer() {
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
