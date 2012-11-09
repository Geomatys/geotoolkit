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
package org.geotoolkit.coverage.postgresql;

import java.awt.Dimension;
import java.awt.Point;
import java.util.Map;
import org.geotoolkit.coverage.AbstractGridMosaic;
import org.geotoolkit.coverage.Pyramid;
import org.geotoolkit.coverage.TileReference;
import org.geotoolkit.storage.DataStoreException;
import org.opengis.geometry.DirectPosition;

/**
 *
 * @author Johann Sorel
 */
public class PGGridMosaic extends AbstractGridMosaic{

    private final PGCoverageReference ref;
    private final long id;
    
    public PGGridMosaic(final PGCoverageReference ref, final long id,
            Pyramid pyramid, DirectPosition upperLeft, Dimension gridSize, Dimension tileSize, double scale) {
        super(String.valueOf(id),pyramid, upperLeft, gridSize, tileSize, scale);
        this.ref = ref;
        this.id = id;
    }

    public long getDatabaseId() {
        return id;
    }

    public PGCoverageReference getCoverageReference() {
        return ref;
    }
    
    @Override
    public TileReference getTile(int col, int row, Map hints) throws DataStoreException {
        final PGTileReference tile = new PGTileReference(this, new Point(col, row));
        return tile;
    }
    
}
