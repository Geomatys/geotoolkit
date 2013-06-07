/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2013, Geomatys
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

    private final MPTileReference[][] mpTileReference;

    public MPGridMosaic(final long id, Pyramid pyramid, DirectPosition upperLeft, Dimension gridSize, Dimension tileSize, double scale) {
        super(String.valueOf(id),pyramid, upperLeft, gridSize, tileSize, scale);
        mpTileReference = new MPTileReference[gridSize.width][gridSize.height];
    }

    @Override
    public boolean isMissing(int col, int row) {
        return mpTileReference[col][row] == null;
    }

    @Override
    public MPTileReference getTile(int col, int row, Map hints) throws DataStoreException {
        return mpTileReference[col][row];
    }    

    public void setTile(int col, int row, MPTileReference tile){
        mpTileReference[col][row] = tile;
    }

}
