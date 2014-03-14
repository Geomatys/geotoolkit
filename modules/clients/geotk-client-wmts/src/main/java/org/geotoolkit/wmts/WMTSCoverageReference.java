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

import org.geotoolkit.coverage.*;
import org.apache.sis.storage.DataStoreException;
import org.geotoolkit.wmts.model.WMTSPyramidSet;
import org.opengis.feature.type.Name;

/**
 * WMTS Coverage Reference.
 * 
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public class WMTSCoverageReference extends AbstractPyramidalCoverageReference {

    private final PyramidSet set;

    WMTSCoverageReference(WebMapTileClient server, Name name, boolean cacheImage){
        super(server,name);
        set = new WMTSPyramidSet(server, name.getLocalPart(), cacheImage);
    }

    @Override
    public int getImageIndex() {
        return 0;
    }

    @Override
    public PyramidSet getPyramidSet() throws DataStoreException {
        return set;
    }

}
