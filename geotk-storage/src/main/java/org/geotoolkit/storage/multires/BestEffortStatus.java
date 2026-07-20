/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2026, Geomatys
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
package org.geotoolkit.storage.multires;

import java.util.List;
import java.util.stream.Stream;
import org.apache.sis.coverage.grid.GridExtent;
import org.apache.sis.storage.DataStoreException;

/**
 * A tile matrix status which only define grid extent informations.
 *
 * @author Johann Sorel (Geomatys)
 */
public final class BestEffortStatus extends TileMatrixStatus {

    private final List<GridExtent> extents;

    public BestEffortStatus(TileMatrix matrix, GridExtent queryExtent, List<GridExtent> extents) {
        super(matrix, queryExtent);
        this.extents = extents;
    }

    @Override
    public Stream<GridExtent> toGridExtents() throws DataStoreException {
        return extents.stream();
    }

}
