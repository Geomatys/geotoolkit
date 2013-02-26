/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2010, Geomatys
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
package org.geotoolkit.coverage.memory;

import java.awt.Dimension;
import java.util.Map;
import org.geotoolkit.coverage.AbstractGridMosaic;
import org.geotoolkit.coverage.Pyramid;
import org.geotoolkit.coverage.TileReference;
import org.geotoolkit.storage.DataStoreException;
import org.opengis.geometry.DirectPosition;

/**
 *
 * @author rmarechal
 */
public class MPGridMosaic extends AbstractGridMosaic {

    
    private final MPCoverageReference ref;
    private final long id;
    private final MPTileReference[][] mpTileReference;
    
    
    public MPGridMosaic(final MPCoverageReference ref, final long id,
            Pyramid pyramid, DirectPosition upperLeft, Dimension gridSize, Dimension tileSize, double scale) {
        super(String.valueOf(id),pyramid, upperLeft, gridSize, tileSize, scale);
        this.ref = ref;
        this.id = id;
        mpTileReference = new MPTileReference[gridSize.height][gridSize.width];
    }

    public long getDatabaseId() {
        return id;
    }

    public MPCoverageReference getCoverageReference() {
        return ref;
    }
    
    @Override
    public MPTileReference getTile(int col, int row, Map hints) throws DataStoreException {
        return mpTileReference[row][col];
    }    
    
    MPTileReference[][] getTiles(){
        return mpTileReference;
    }
    
}
