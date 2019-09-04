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

import org.apache.sis.storage.DataStoreException;
import org.apache.sis.storage.GridCoverageResource;
import org.apache.sis.util.ArgumentChecks;
import org.geotoolkit.data.multires.Pyramids;
import org.geotoolkit.map.DefaultCoverageMapLayer;
import org.geotoolkit.storage.DataStores;
import org.geotoolkit.style.DefaultStyleFactory;
import org.geotoolkit.style.StyleConstants;
import org.geotoolkit.wmts.WebMapTileClient;
import org.geotoolkit.wmts.model.WMTSPyramidSet;
import org.opengis.util.GenericName;

/**
 * Map representation of a WMTS layer.
 *
 * @author Johann Sorel (Geomatys)
 * @module
 */
public class WMTSMapLayer extends DefaultCoverageMapLayer {

    /**
     * Query default format.
     */
    private static final String DEFAULT_FORMAT = "image/png";

    /**
     * The server to request.
     */
    private final WebMapTileClient server;

    private static GridCoverageResource getReference(WebMapTileClient server, String mapType){
        try {
            for(GenericName n : DataStores.getNames(server, true, GridCoverageResource.class)){
                if(n.tip().toString().equalsIgnoreCase(mapType)){
                    return (GridCoverageResource) server.findResource(n.toString());
                }
            }
            throw new RuntimeException("Not layer for name : " + mapType);

        } catch (DataStoreException ex) {
            throw new RuntimeException(ex);
        }
    }

    public WMTSMapLayer(final WebMapTileClient server, String layerName) {
        super(getReference(server, layerName),
              new DefaultStyleFactory().style(StyleConstants.DEFAULT_RASTER_SYMBOLIZER));
        setUserProperty(Pyramids.HINT_FORMAT, DEFAULT_FORMAT);
        this.server = server;
    }

    /**
     * Sets the format for the output response. By default sets to {@code image/png}.
     */
    public void setFormat(final String format) {
        ArgumentChecks.ensureNonNull("format", format);
        setUserProperty(Pyramids.HINT_FORMAT, format);
    }

    /**
     * Gets the extension for the output response. By default {@code image/png}.
     */
    public String getFormat() {
        Object val = getUserProperty(Pyramids.HINT_FORMAT);
        if(val != null){
            return val.toString();
        }
        return null;
    }

    /**
     * @return named style use on server.
     */
    public String getTileSetStyle() {
        Object val = getUserProperty(WMTSPyramidSet.HINT_STYLE);
        if(val != null){
            return val.toString();
        }
        return null;
    }

    public void setTileSetStyle(String tileSetStyle) {
        ArgumentChecks.ensureNonNull("tileSetStyle", tileSetStyle);
        setUserProperty(WMTSPyramidSet.HINT_STYLE, tileSetStyle);
    }

    /**
     * Returns the {@link WebMapTileClient} to request. Can't be {@code null}.
     */
    public WebMapTileClient getServer() {
        return server;
    }

}
