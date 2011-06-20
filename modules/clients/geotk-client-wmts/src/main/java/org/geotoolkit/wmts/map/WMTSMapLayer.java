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
package org.geotoolkit.wmts.map;

import org.geotoolkit.geometry.Envelope2D;
import org.geotoolkit.map.AbstractMapLayer;
import org.geotoolkit.style.DefaultStyleFactory;
import org.geotoolkit.util.ArgumentChecks;
import org.geotoolkit.wmts.WebMapTileServer;

import org.opengis.geometry.Envelope;

import static org.geotoolkit.util.ArgumentChecks.*;
import static org.geotoolkit.referencing.crs.DefaultGeographicCRS.*;


/**
 * Map representation of a WMTS layer.
 *
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public class WMTSMapLayer extends AbstractMapLayer {    

    //full world extend
    private static final Envelope MAXEXTEND_ENV = new Envelope2D(WGS84, -180,
                                                                 -90, 360, 180);            
    
    /**
     * The server to request.
     */
    private final WebMapTileServer server;

    /**
     * The layer map .
     */
    private String mapType = null;
    
    /**
     * Wanted tile set, live null for automatic selection.
     */
    private String tileSet = null;
    
    /**
     * Query format.
     */
    private String format = "image/png";
    
    /**
     * Layer style.
     */
    private String tileSetStyle = null;

    
    public WMTSMapLayer(final WebMapTileServer server) {
        super(new DefaultStyleFactory().style());
        this.server = server;

        //register the default graphic builder for geotk 2D engine.
        graphicBuilders().add(WMTSGraphicBuilder.INSTANCE);
    }
    
    /**
     * Sets the layer to request.
     *
     * @param name layer name.
     */
    public void setLayer(final String name) {
        ArgumentChecks.ensureNonNull("layer name", name);
        this.mapType = name;
    }

    /**
     * Returns the map layer name.
     */
    public String getLayer() {
        return mapType;
    }

    /**
     * Sets the format for the output response. By default sets to {@code image/png}.
     * @param format 
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

    /**
     * Gets the tile set to use for the output response. 
     * null if tile set is choose automaticly.
     */
    public String getTileSet() {
        return tileSet;
    }

    /**
     * @param tileSet : tile set to use, null for automatic.
     */
    public void setTileSet(String tileSet) {
        this.tileSet = tileSet;
    }

    /**
     * @return named style use on server.
     */
    public String getTileSetStyle() {
        return tileSetStyle;
    }

    /**
     * @param tileSetStyle 
     */
    public void setTileSetStyle(String tileSetStyle) {
        this.tileSetStyle = tileSetStyle;
    }

    /**
     * Returns the {@link StaticGoogleMapsServer} to request. Can't be {@code null}.
     */
    public WebMapTileServer getServer() {
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
