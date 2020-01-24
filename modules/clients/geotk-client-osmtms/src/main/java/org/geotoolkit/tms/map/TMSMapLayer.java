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
package org.geotoolkit.tms.map;

import org.apache.sis.storage.DataStoreException;
import org.apache.sis.storage.GridCoverageResource;
import org.apache.sis.util.ArgumentChecks;
import org.geotoolkit.map.DefaultCoverageMapLayer;
import org.geotoolkit.storage.DataStores;
import org.geotoolkit.storage.multires.Pyramids;
import org.geotoolkit.style.DefaultStyleFactory;
import org.geotoolkit.style.StyleConstants;
import org.geotoolkit.tms.TileMapClient;


/**
 * Map representation of a TMS layer.
 *
 * @author Johann Sorel (Geomatys)
 * @module
 */
public class TMSMapLayer extends DefaultCoverageMapLayer {

    private static GridCoverageResource getReference(TileMapClient server){
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


    public TMSMapLayer(final TileMapClient server) {
        super(getReference(server),
              new DefaultStyleFactory().style(StyleConstants.DEFAULT_RASTER_SYMBOLIZER));
        getUserProperties().put(Pyramids.HINT_FORMAT, DEFAULT_FORMAT);
    }

    /**
     * Sets the extension for the output response. By default sets to {@code .png}.
     */
    public void setFormat(final String format) {
        ArgumentChecks.ensureNonNull("format", format);
        getUserProperties().put(Pyramids.HINT_FORMAT, format);
    }

    /**
     * Gets the extension for the output response. By default {@code .png}.
     */
    public String getFormat() {
        Object val = getUserProperties().get(Pyramids.HINT_FORMAT);
        if(val != null){
            return val.toString();
        }
        return null;
    }

}
