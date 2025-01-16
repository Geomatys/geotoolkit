/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2007-2012, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2009-2012, Geomatys
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
package org.geotoolkit.storage.coverage.mosaic;

import java.awt.Dimension;
import java.awt.geom.AffineTransform;
import java.io.*;


import org.apache.sis.coverage.grid.GridExtent;
import org.apache.sis.storage.DataStoreException;
import org.apache.sis.storage.GridCoverageResource;

import static org.apache.sis.util.ArgumentChecks.*;

import org.apache.sis.util.collection.BackingStoreException;
import org.apache.sis.coverage.grid.PixelInCell;
import org.opengis.referencing.operation.MathTransform;
import org.apache.sis.referencing.operation.gridded.Tile;


/**
 * A tile to be read by {@link MosaicImageReader}.
 */
final class ResourceTile extends Tile {
    private final GridCoverageResource resource;

    ResourceTile(GridCoverageResource resource) throws DataStoreException {
        super(null, toAffine(resource.getGridGeometry().getGridToCRS(PixelInCell.CELL_CENTER)));
        ensureNonNull("resource", resource);
        this.resource = resource;
    }

    private static AffineTransform toAffine(MathTransform trs) throws DataStoreException {
        if (trs instanceof AffineTransform) {
            return (AffineTransform) trs;
        } else {
            throw new DataStoreException("Coverage Grid to CRS is not affine");
        }
    }

    public GridCoverageResource getResource() {
        return resource;
    }

    /**
     * Invoked when the tile size need to be read.
     *
     * @return the tile size.
     * @throws IOException if an I/O operation was required for fetching the tile size and that operation failed.
     */
    protected Dimension fetchSize() throws IOException {
        try {
            final GridExtent extent = resource.getGridGeometry().getExtent();
            return new Dimension((int) extent.getSize(0), (int) extent.getSize(1));
        } catch (DataStoreException e) {
            Throwable cause = e.getCause();
            if (cause instanceof IOException) {
                throw (IOException) cause;
            }
            throw new BackingStoreException(e);
        }
    }

    /**
     * Compares this tile with the specified one for equality.
     */
    @Override
    public boolean equals(final Object object) {
        return super.equals(object) && ((ResourceTile) object).resource == resource;
    }

    /**
     * Returns a hash code value for this tile.
     */
    @Override
    public int hashCode() {
        return super.hashCode() + resource.hashCode();
    }
}
