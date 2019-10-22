/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2018, Geomatys
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
import java.awt.Point;
import java.util.Map;
import java.util.stream.Stream;
import org.apache.sis.coverage.grid.GridExtent;
import org.apache.sis.storage.DataStoreException;
import org.geotoolkit.process.Monitor;
import org.opengis.coverage.PointOutsideCoverageException;
import org.opengis.geometry.DirectPosition;
import org.opengis.geometry.Envelope;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
final class ProgressiveMosaic implements Mosaic {

    private final ProgressivePyramid pyramid;
    private final Mosaic base;

    ProgressiveMosaic(ProgressivePyramid pyramid, Mosaic base) {
        this.pyramid = pyramid;
        this.base = base;
    }

    @Override
    public String getIdentifier() {
        return base.getIdentifier();
    }

    @Override
    public DirectPosition getUpperLeftCorner() {
        return base.getUpperLeftCorner();
    }

    @Override
    public Dimension getGridSize() {
        return base.getGridSize();
    }

    @Override
    public double getScale() {
        return base.getScale();
    }

    @Override
    public Dimension getTileSize() {
        return base.getTileSize();
    }

    @Override
    public Envelope getEnvelope() {
        return base.getEnvelope();
    }

    @Override
    public boolean isMissing(long col, long row) throws PointOutsideCoverageException {
        if (pyramid.res.generator == null) {
            return base.isMissing(col, row);
        }
        //tile will be generated
        return false;
    }

    @Override
    public Tile getTile(long col, long row, Map hints) throws DataStoreException {
        Tile tile = base.getTile(col, row);
        if (tile == null && pyramid.res.generator != null) {
            //generate tile
            tile = pyramid.res.generator.generateTile(pyramid, base, new Point(Math.toIntExact(col), Math.toIntExact(row)));
            base.writeTiles(Stream.of(tile), null);
            tile = base.getTile(col, row);
        }
        return tile;
    }

    /**
     * Return the full extent, tiles will be generated.
     * @return
     */
    @Override
    public GridExtent getDataExtent() {
        final Dimension tileSize = getTileSize();
        final Dimension gridSize = getGridSize();
        return new GridExtent(
                ((long) gridSize.width) * tileSize.width,
                ((long) gridSize.height) * tileSize.height);
    }

    @Override
    public void writeTiles(Stream<Tile> tiles, Monitor monitor) throws DataStoreException {
        base.writeTiles(tiles, monitor);
    }

    @Override
    public void deleteTile(int tileX, int tileY) throws DataStoreException {
        base.deleteTile(tileX, tileY);
    }

}
