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
package org.geotoolkit.wmsc.model;

import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import org.apache.sis.storage.DataStoreException;
import org.geotoolkit.client.CapabilitiesException;
import org.geotoolkit.client.Request;
import org.geotoolkit.client.map.CachedTileMatrixSets;
import org.geotoolkit.storage.multires.TileMatrices;
import org.geotoolkit.wms.GetMapRequest;
import org.geotoolkit.wms.xml.v111.Capability;
import org.geotoolkit.wms.xml.v111.VendorSpecificCapabilities;
import org.geotoolkit.wmsc.WebMapClientCached;
import org.geotoolkit.wmsc.xml.v111.TileSet;
import org.opengis.referencing.NoSuchAuthorityCodeException;
import org.opengis.util.FactoryException;
import org.geotoolkit.storage.multires.TileMatrixSet;
import org.geotoolkit.storage.multires.TileMatrix;

/**
 *
 * @author Johann Sorel (Geomatys)
 * @module
 */
public class WMSCTileMatrixSets extends CachedTileMatrixSets {

    private final String layer;

    public WMSCTileMatrixSets(final WebMapClientCached server, final String layer) throws CapabilitiesException {
        super(server,true,server.isCacheImage());
        this.layer = layer;

        //WMSC is a WMS 1.1.1
        final Capability capas = (Capability) server.getServiceCapabilities().getCapability();
        final VendorSpecificCapabilities vendor = capas.getVendorSpecificCapabilities();

        if(vendor == null){
            return;
        }

        final List<TileSet> sets = vendor.getTileSet();

        if(sets == null){
            return;
        }

        //find tileset definition for this layer
        for(final TileSet set : sets){
            for(String layerName : set.getLayers()){
                if(!layer.equals(layerName)){
                    continue;
                }

                try {
                    final WMSCTileMatrixSet pyramid = new WMSCTileMatrixSet(this, set);
                    getTileMatrixSets().add(pyramid);
                } catch (NoSuchAuthorityCodeException ex) {
                    LOGGER.log(Level.INFO, ex.getMessage(),ex);
                } catch (FactoryException ex) {
                    LOGGER.log(Level.INFO, ex.getMessage(),ex);
                }

            }
        }
    }

    @Override
    protected WebMapClientCached getServer() {
        return (WebMapClientCached)super.getServer();
    }

    public String getLayer() {
        return layer;
    }

    @Override
    public Request getTileRequest(TileMatrixSet pyramid, TileMatrix mosaic, long col, long row, Map hints) throws DataStoreException {
        final GetMapRequest request = getServer().createGetMap();
        request.setLayers(layer);
        request.setEnvelope(TileMatrices.computeTileEnvelope(mosaic, col, row));
        request.setDimension(mosaic.getTileSize());
        request.setFormat(((WMSCTileMatrixSet)pyramid).getTileset().getFormat());
        return request;
    }

}
