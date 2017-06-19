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

import org.apache.sis.storage.DataStoreException;
import org.geotoolkit.storage.coverage.AbstractPyramidalCoverageResource;
import org.geotoolkit.storage.coverage.PyramidSet;
import org.geotoolkit.wmts.model.WMTSPyramidSet;
import org.opengis.util.GenericName;

/**
 * WMTS Coverage Reference.
 *
 * @author Johann Sorel (Geomatys)
 * @module
 */
public class WMTSCoverageResource extends AbstractPyramidalCoverageResource {

    private final PyramidSet set;

    WMTSCoverageResource(WebMapTileClient server, GenericName name, boolean cacheImage){
        super(server,name,0);
        set = new WMTSPyramidSet(server, name.tip().toString(), cacheImage);
    }

    @Override
    public PyramidSet getPyramidSet() throws DataStoreException {
        return set;
    }

}
