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

import org.geotoolkit.coverage.CoverageReference;
import org.geotoolkit.coverage.PyramidSet;
import org.geotoolkit.feature.DefaultName;
import org.geotoolkit.map.DefaultCoverageMapLayer;
import org.geotoolkit.osmtms.OSMTileMapServer;
import org.geotoolkit.storage.DataStoreException;
import org.geotoolkit.style.DefaultStyleFactory;
import org.geotoolkit.style.StyleConstants;
import org.geotoolkit.util.ArgumentChecks;


/**
 * Map representation of a TMS layer.
 *
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public class OSMTMSMapLayer extends DefaultCoverageMapLayer {    

    private static CoverageReference getReference(OSMTileMapServer server){
        try {
            return server.getCoverageReference(server.getNames().iterator().next());
        } catch (DataStoreException ex) {
            throw new RuntimeException(ex);
        }
    }
    /**
     * Query extension.
     */
    private static final String DEFAULT_FORMAT = ".png";
    
    
    public OSMTMSMapLayer(final OSMTileMapServer server) {
        super(getReference(server),
              new DefaultStyleFactory().style(StyleConstants.DEFAULT_RASTER_SYMBOLIZER),
              new DefaultName("osm"));
        setUserPropertie(PyramidSet.HINT_FORMAT, DEFAULT_FORMAT);
    }
       
    /**
     * Sets the extension for the output response. By default sets to {@code .png}.
     * @param format 
     */
    public void setFormat(final String format) {
        ArgumentChecks.ensureNonNull("format", format);
        setUserPropertie(PyramidSet.HINT_FORMAT, format);
    }
    
    /**
     * Gets the extension for the output response. By default {@code .png}.
     */
    public String getFormat() {
        Object val = getUserPropertie(PyramidSet.HINT_FORMAT);
        if(val != null){
            return val.toString();
        }
        return null;
    }
    
}
