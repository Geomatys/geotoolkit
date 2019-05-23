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

import org.apache.sis.storage.DataStoreException;
import org.apache.sis.util.ArgumentChecks;
import org.geotoolkit.data.multires.Pyramids;
import org.geotoolkit.map.DefaultCoverageMapLayer;
import org.geotoolkit.osmtms.OSMTileMapClient;
import org.geotoolkit.storage.DataStores;
import org.geotoolkit.storage.coverage.GridCoverageResource;
import org.geotoolkit.style.DefaultStyleFactory;
import org.geotoolkit.style.StyleConstants;


/**
 * Map representation of a TMS layer.
 *
 * @author Johann Sorel (Geomatys)
 * @module
 */
public class OSMTMSMapLayer extends DefaultCoverageMapLayer {

    private static GridCoverageResource getReference(OSMTileMapClient server){
        try {
            return (GridCoverageResource) DataStores.flatten(server, true, GridCoverageResource.class).iterator().next();
        } catch (DataStoreException ex) {
            throw new RuntimeException(ex);
        }
    }
    /**
     * Query extension.
     */
    private static final String DEFAULT_FORMAT = ".png";


    public OSMTMSMapLayer(final OSMTileMapClient server) {
        super(getReference(server),
              new DefaultStyleFactory().style(StyleConstants.DEFAULT_RASTER_SYMBOLIZER));
        setUserProperty(Pyramids.HINT_FORMAT, DEFAULT_FORMAT);
    }

    /**
     * Sets the extension for the output response. By default sets to {@code .png}.
     * @param format
     */
    public void setFormat(final String format) {
        ArgumentChecks.ensureNonNull("format", format);
        setUserProperty(Pyramids.HINT_FORMAT, format);
    }

    /**
     * Gets the extension for the output response. By default {@code .png}.
     */
    public String getFormat() {
        Object val = getUserProperty(Pyramids.HINT_FORMAT);
        if(val != null){
            return val.toString();
        }
        return null;
    }

}
