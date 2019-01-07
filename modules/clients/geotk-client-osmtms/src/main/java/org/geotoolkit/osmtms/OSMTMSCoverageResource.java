/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2012, Geomatys
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
package org.geotoolkit.osmtms;

import java.util.Collection;
import org.apache.sis.coverage.grid.GridGeometry;
import org.apache.sis.storage.DataStoreException;
import org.geotoolkit.coverage.grid.ViewType;
import org.geotoolkit.coverage.io.GridCoverageReader;
import org.geotoolkit.data.multires.MultiResolutionModel;
import org.geotoolkit.data.multires.Pyramid;
import org.geotoolkit.osmtms.model.OSMTMSPyramidSet;
import org.geotoolkit.storage.coverage.AbstractPyramidalCoverageResource;
import org.opengis.util.GenericName;

/**
 * Open Street Map Tile Map Server.
 *
 * @author Johann Sorel (Geomatys)
 * @module
 */
public class OSMTMSCoverageResource extends AbstractPyramidalCoverageResource {

    OSMTMSCoverageResource(OSMTileMapClient server, GenericName name){
        super(server,name);
    }

    public OSMTMSPyramidSet getPyramidSet() {
        return ((OSMTileMapClient)store).getPyramidSet();
    }

    @Override
    public GridGeometry getGridGeometry() throws DataStoreException {
        final GridCoverageReader reader = acquireReader();
        try {
            return reader.getGridGeometry();
        } finally {
            recycle(reader);
        }
    }

    @Override
    public Collection<Pyramid> getModels() throws DataStoreException {
        return ((OSMTileMapClient)store).getPyramidSet().getPyramids();
    }

    /**
     * Returns adapted {@link ViewType} for OSM TMS reference.
     * The default associated view is {@link ViewType#PHOTOGRAPHIC}.
     *
     * @return
     * @throws DataStoreException
     */
    @Override
    public ViewType getPackMode() throws DataStoreException {
        return ViewType.PHOTOGRAPHIC;
    }

    @Override
    public MultiResolutionModel createModel(MultiResolutionModel template) throws DataStoreException {
        throw new DataStoreException("Not supported.");
    }

    @Override
    public void removeModel(String identifier) throws DataStoreException {
        throw new DataStoreException("Not supported.");
    }

}
