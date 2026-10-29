/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2011, Geomatys
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
package org.geotoolkit.tms.model;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.apache.sis.coverage.grid.GridExtent;
import org.apache.sis.storage.DataStoreException;
import org.geotoolkit.storage.coverage.DefaultImageTile;
import org.geotoolkit.storage.multires.AbstractTileMatrix;
import org.apache.sis.storage.tiling.Tile;
import org.apache.sis.storage.tiling.TileMatrixSet;
import org.opengis.geometry.DirectPosition;

/**
 *
 * @author Johann Sorel (Geomatys)
 * @module
 */
public class TMSTileMatrix extends AbstractTileMatrix {

    private final TMSTileMatrixSets set;
    private final int scaleLevel;
    private Tile anyTile = null;

    public TMSTileMatrix(TMSTileMatrixSets set, TileMatrixSet pyramid, DirectPosition upperLeft, Dimension gridSize,
            int[] tileSize, double scale, int scaleLevel) {
        super(null, pyramid,upperLeft,gridSize,tileSize,scale);
        this.scaleLevel = scaleLevel;
        this.set = set;
    }

    public int getScaleLevel() {
        return scaleLevel;
    }

    /**
     * Returns the tile, its image converted to ARGB.
     * Tile servers often send palette images, with a different palette in each tile : the mosaic image
     * built from the tiles has a single color model, the tiles must share it.
     */
    @Override
    public Optional<Tile> getTile(long... indices) throws DataStoreException {
        final Tile tile = set.getTile(getTileMatrixSet(), this, indices, null).orElse(null);
        if (tile instanceof DefaultImageTile imageTile) {
            final RenderedImage image;
            try {
                image = imageTile.getImage();
            } catch (IOException ex) {
                throw new DataStoreException(ex.getMessage(), ex);
            }
            if (image == null) {
                return Optional.empty();
            }
            return Optional.of(new DefaultImageTile(this, toARGB(image), indices));
        }
        return Optional.ofNullable(tile);
    }

    private static RenderedImage toARGB(RenderedImage image) {
        if (image instanceof BufferedImage bi && bi.getType() == BufferedImage.TYPE_INT_ARGB) {
            return image;
        }
        final BufferedImage argb = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_ARGB);
        final Graphics2D g = argb.createGraphics();
        try {
            g.drawRenderedImage(image, AffineTransform.getTranslateInstance(-image.getMinX(), -image.getMinY()));
        } finally {
            g.dispose();
        }
        return argb;
    }

    @Override
    public Stream<Tile> getTiles(GridExtent indicesRanges, boolean parallel) throws DataStoreException {
        if (indicesRanges == null) indicesRanges = getTilingScheme().getExtent();
        final List<long[]> points = indicesRanges.latticePointStream(parallel).collect(Collectors.toList());
        return set.getTiles(getTileMatrixSet(), this, points, null);
    }

    @Override
    public synchronized Tile anyTile() throws DataStoreException {
        if (anyTile == null) {
            anyTile = getTile(0, 0).orElse(null);
        }
        return anyTile;
    }

}
