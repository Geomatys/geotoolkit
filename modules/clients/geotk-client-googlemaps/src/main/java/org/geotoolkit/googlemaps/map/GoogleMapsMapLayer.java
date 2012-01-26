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

import org.geotoolkit.coverage.CoverageReference;
import org.geotoolkit.coverage.PyramidSet;
import org.geotoolkit.feature.DefaultName;
import org.geotoolkit.googlemaps.GetMapRequest;
import org.geotoolkit.googlemaps.StaticGoogleMapsServer;
import org.geotoolkit.map.DefaultCoverageMapLayer;
import org.geotoolkit.storage.DataStoreException;
import org.geotoolkit.style.DefaultStyleFactory;
import org.geotoolkit.style.StyleConstants;
import org.geotoolkit.util.ArgumentChecks;
import org.opengis.feature.type.Name;


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
    private final StaticGoogleMapsServer server;

    /**
     * Output format of the response.
     */
    private static final String DEFAULT_FORMAT = GetMapRequest.FORMAT_PNG8;
    

    private static CoverageReference getReference(StaticGoogleMapsServer server, String mapType){
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
    
    public GoogleMapsMapLayer(final StaticGoogleMapsServer server, String maptype) {
        super(getReference(server,maptype),
              new DefaultStyleFactory().style(StyleConstants.DEFAULT_RASTER_SYMBOLIZER),
              new DefaultName("google"));
        this.server = server;
        setUserPropertie(PyramidSet.HINT_FORMAT, DEFAULT_FORMAT);
    }
    
    /**
     * Sets the format for the output response. By default sets to {@code png}.     *
     * @param format 
     */
    public void setFormat(final String format) {
        ArgumentChecks.ensureNonNull("format", format);
        setUserPropertie(PyramidSet.HINT_FORMAT, format);
    }

    /**
     * Gets the format for the output response. By default {@code png}.
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
     * Returns the {@link StaticGoogleMapsServer} to request. Can't be {@code null}.
     * @return 
     */
    public StaticGoogleMapsServer getServer() {
        return server;
    }
   
}
