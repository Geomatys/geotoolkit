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

import org.geotoolkit.storage.coverage.CoverageReference;
import org.geotoolkit.storage.coverage.PyramidSet;
import org.geotoolkit.googlemaps.GetMapRequest;
import org.geotoolkit.googlemaps.StaticGoogleMapsClient;
import org.geotoolkit.map.DefaultCoverageMapLayer;
import org.apache.sis.storage.DataStoreException;
import org.geotoolkit.style.DefaultStyleFactory;
import org.geotoolkit.style.StyleConstants;
import org.apache.sis.util.ArgumentChecks;
import org.opengis.util.GenericName;


/**
 * Map representation of a Static GoogleMaps layer.
 *
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public class GoogleMapsMapLayer extends DefaultCoverageMapLayer {

    /**
     * The server to request.
     */
    private final StaticGoogleMapsClient server;

    /**
     * Output format of the response.
     */
    private static final String DEFAULT_FORMAT = GetMapRequest.FORMAT_PNG8;


    private static CoverageReference getReference(StaticGoogleMapsClient server, String mapType){
        try {
            for(GenericName n : server.getNames()){
                if(n.tip().toString().equalsIgnoreCase(mapType)){
                    return server.getCoverageReference(n);
                }
            }
            throw new RuntimeException("Not layer for name : " + mapType);

        } catch (DataStoreException ex) {
            throw new RuntimeException(ex);
        }
    }

    public GoogleMapsMapLayer(final StaticGoogleMapsClient server, String maptype) {
        super(getReference(server,maptype),
              new DefaultStyleFactory().style(StyleConstants.DEFAULT_RASTER_SYMBOLIZER));
        this.server = server;
        setUserProperty(PyramidSet.HINT_FORMAT, DEFAULT_FORMAT);
    }

    /**
     * Sets the format for the output response. By default sets to {@code png}.     *
     * @param format
     */
    public void setFormat(final String format) {
        ArgumentChecks.ensureNonNull("format", format);
        setUserProperty(PyramidSet.HINT_FORMAT, format);
    }

    /**
     * Gets the format for the output response. By default {@code png}.
     * @return
     */
    public String getFormat() {
        Object val = getUserProperty(PyramidSet.HINT_FORMAT);
        if(val != null){
            return val.toString();
        }
        return null;
    }

    /**
     * Returns the {@link StaticGoogleMapsClient} to request. Can't be {@code null}.
     * @return
     */
    public StaticGoogleMapsClient getServer() {
        return server;
    }

}
