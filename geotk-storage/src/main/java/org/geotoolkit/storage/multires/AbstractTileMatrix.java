/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2011-2018, Geomatys
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

import java.awt.Dimension;
import java.util.Arrays;
import java.util.UUID;
import org.apache.sis.coverage.grid.GridGeometry;
import org.apache.sis.geometry.GeneralEnvelope;
import org.apache.sis.storage.DataStoreException;
import org.apache.sis.storage.tiling.TileMatrixSet;
import org.apache.sis.storage.tiling.TileStatus;
import org.apache.sis.util.Classes;
import org.apache.sis.util.iso.Names;
import org.opengis.geometry.DirectPosition;
import org.opengis.util.GenericName;

/**
 * Abstract TileMatrix.
 *
 * @author Johann Sorel (Geomatys)
 * @module
 */
public abstract class AbstractTileMatrix implements TileMatrix {

    private final GenericName id;
    private final TileMatrixSet tileMatrixSet;
    private final GridGeometry tilingScheme;
    private final int[] tileSize;

    public AbstractTileMatrix(GenericName id, TileMatrixSet tileMatrixSet, DirectPosition upperLeft, Dimension gridSize,
            int[] tileSize, double scale) {
        this(id, tileMatrixSet, TileMatrices.toTilingScheme(upperLeft, gridSize, scale, tileSize), tileSize);
    }

    public AbstractTileMatrix(GenericName id, TileMatrixSet tileMatrixSet, GridGeometry tilingScheme, int[] tileSize) {
        this.tileMatrixSet = tileMatrixSet;
        this.tilingScheme = tilingScheme;
        this.tileSize = tileSize.clone();
        if (tileSize.length != tilingScheme.getExtent().getDimension()) {
            throw new IllegalArgumentException("Tile size (" + tileSize.length + ") must match tiling scheme extent dimension (" + tilingScheme.getExtent().getDimension() + ")");
        }

        if (id == null) {
            this.id = Names.createLocalName(null, null, UUID.randomUUID().toString());
        } else {
            this.id = id;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public GenericName getIdentifier() {
        return id;
    }

    @Override
    public GridGeometry getTilingScheme() {
        return tilingScheme;
    }

    /**
     * {@inheritDoc}
     */
    public TileMatrixSet getTileMatrixSet() {
        return tileMatrixSet;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int[] getTileSize() {
        return tileSize.clone(); //defensive copy
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public TileStatus getTileStatus(long... indices) throws DataStoreException {
        return TileStatus.UNKNOWN;
    }

    @Override
    public String toString() {
        return toString(this);
    }

    /**
     * Pretty print outut of given mosaic.
     * @param matrix not null
     */
    public static String toString(TileMatrix matrix) {
        final StringBuilder sb = new StringBuilder(Classes.getShortClassName(matrix));
        sb.append("   id = ").append(matrix.getIdentifier());
        sb.append("   resolution = ").append(Arrays.toString(matrix.getTilingScheme().getResolution(true)));
        sb.append("   gridSize = ").append(matrix.getTilingScheme().getExtent());
        sb.append("   tileSize = ").append(Arrays.toString(matrix.getTileSize()));
        sb.append("   bbox = ").append(new GeneralEnvelope(matrix.getTilingScheme().getEnvelope()).toString());
        return sb.toString();
    }

}
