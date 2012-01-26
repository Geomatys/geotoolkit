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

import org.geotoolkit.coverage.CoverageReference;
import org.geotoolkit.coverage.PyramidSet;
import org.geotoolkit.feature.DefaultName;
import org.geotoolkit.map.DefaultCoverageMapLayer;
import org.geotoolkit.storage.DataStoreException;
import org.geotoolkit.style.DefaultStyleFactory;
import org.geotoolkit.style.StyleConstants;
import org.geotoolkit.util.ArgumentChecks;
import org.geotoolkit.wmts.WebMapTileServer;
import org.geotoolkit.wmts.model.WMTSPyramidSet;
import org.opengis.feature.type.Name;

/**
 * Map representation of a WMTS layer.
 *
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public class WMTSMapLayer extends DefaultCoverageMapLayer {
    
    /**
     * Query default format.
     */
    private static final String DEFAULT_FORMAT = "image/png";
    
    /**
     * The server to request.
     */
    private final WebMapTileServer server;
    
    private static CoverageReference getReference(WebMapTileServer server, String mapType){
        try {
            for(Name n : server.getNames()){
                if(n.getLocalPart().equalsIgnoreCase(mapType)){
                    return server.getCoverageReference(n);
                }
            }
            throw new RuntimeException("Not layer for name : " + mapType);
            
        } catch (DataStoreException ex) {
            throw new RuntimeException(ex);
        }
    }
    
    public WMTSMapLayer(final WebMapTileServer server, String layerName) {
        super(getReference(server, layerName),
              new DefaultStyleFactory().style(StyleConstants.DEFAULT_RASTER_SYMBOLIZER),
              new DefaultName("osm"));
        setUserPropertie(PyramidSet.HINT_FORMAT, DEFAULT_FORMAT);
        this.server = server;
    }
    
    /**
     * Sets the format for the output response. By default sets to {@code image/png}.
     * @param format 
     */
    public void setFormat(final String format) {
        ArgumentChecks.ensureNonNull("format", format);
        setUserPropertie(PyramidSet.HINT_FORMAT, format);
    }
    
    /**
     * Gets the extension for the output response. By default {@code image/png}.
     * @return 
     */
    public String getFormat() {
        Object val = getUserPropertie(PyramidSet.HINT_FORMAT);
        if(val != null){
            return val.toString();
        }
        return null;
    }

    /**
     * @return named style use on server.
     */
    public String getTileSetStyle() {
        Object val = getUserPropertie(WMTSPyramidSet.HINT_STYLE);
        if(val != null){
            return val.toString();
        }
        return null;
    }

    /**
     * @param tileSetStyle 
     */
    public void setTileSetStyle(String tileSetStyle) {
        ArgumentChecks.ensureNonNull("tileSetStyle", tileSetStyle);
        setUserPropertie(WMTSPyramidSet.HINT_STYLE, tileSetStyle);
    }

    /**
     * Returns the {@link WebMapTileServer} to request. Can't be {@code null}.
     * @return 
     */
    public WebMapTileServer getServer() {
        return server;
    }
   
}
