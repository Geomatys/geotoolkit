/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2012-2014, Geomatys
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
package org.geotoolkit.wmts;

import java.util.Collection;
import org.apache.sis.storage.DataStoreException;
import org.geotoolkit.data.multires.MultiResolutionModel;
import org.geotoolkit.data.multires.Pyramid;
import org.geotoolkit.storage.coverage.AbstractPyramidalCoverageResource;
import org.geotoolkit.wmts.model.WMTSPyramidSet;
import org.opengis.util.GenericName;

/**
 * WMTS Coverage Reference.
 *
 * @author Johann Sorel (Geomatys)
 * @module
 */
public class WMTSCoverageResource extends AbstractPyramidalCoverageResource {

    private final WMTSPyramidSet set;

    WMTSCoverageResource(WebMapTileClient server, GenericName name, boolean cacheImage){
        super(server,name);
        set = new WMTSPyramidSet(server, name.tip().toString(), cacheImage);
    }

    public WMTSPyramidSet getPyramidSet() {
        return set;
    }

    @Override
    public Collection<Pyramid> getModels() throws DataStoreException {
        return set.getPyramids();
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
