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
import java.util.Map;
import java.util.stream.Stream;
import org.apache.sis.coverage.grid.GridExtent;
import org.apache.sis.geometry.GeneralDirectPosition;
import org.apache.sis.storage.DataStoreException;
import org.geotoolkit.process.Monitor;
import org.opengis.coverage.PointOutsideCoverageException;
import org.opengis.geometry.DirectPosition;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public class DefiningMosaic implements Mosaic {

    private final String identifier;
    private final DirectPosition upperLeft;
    private final double scale;
    private final Dimension tileSize;
    private final Dimension gridSize;
    private final GridExtent dataExtent;

    public DefiningMosaic(String identifier, DirectPosition upperLeft, double scale, Dimension tileSize, Dimension gridSize) {
        this(identifier,upperLeft, scale, tileSize, gridSize, (GridExtent) null);
    }

    public DefiningMosaic(String identifier, DirectPosition upperLeft, double scale, Dimension tileSize, Dimension gridSize, Dimension dataExtent) {
        this(identifier,upperLeft, scale, tileSize, gridSize, new GridExtent(dataExtent.width, dataExtent.height));
    }

    public DefiningMosaic(String identifier, DirectPosition upperLeft, double scale, Dimension tileSize, Dimension gridSize, GridExtent dataExtent) {
        this.identifier = identifier;
        this.upperLeft = upperLeft;
        this.scale = scale;
        this.tileSize = tileSize;
        this.gridSize = gridSize;
        this.dataExtent = dataExtent != null ? dataExtent :
                new GridExtent(
                        ((long) gridSize.width) * tileSize.width,
                        ((long) gridSize.height) * tileSize.height);
    }

    @Override
    public String getIdentifier() {
        return identifier;
    }

    @Override
    public DirectPosition getUpperLeftCorner() {
        return new GeneralDirectPosition(upperLeft); //defensive copy
    }

    @Override
    public Dimension getGridSize() {
        return (Dimension) gridSize.clone(); //defensive copy
    }

    @Override
    public double getScale() {
        return scale;
    }

    @Override
    public Dimension getTileSize() {
        return (Dimension) tileSize.clone(); //defensive copy
    }

    @Override
    public GridExtent getDataExtent() {
        return dataExtent;
    }

    @Override
    public boolean isMissing(long col, long row) throws PointOutsideCoverageException {
        throw new UnsupportedOperationException("Not supported.");
    }

    @Override
    public Tile getTile(long col, long row, Map hints) throws DataStoreException {
        throw new DataStoreException("Not supported.");
    }

    @Override
    public void writeTiles(Stream<Tile> tiles, Monitor monitor) throws DataStoreException {
        throw new UnsupportedOperationException("Not supported.");
    }

    @Override
    public void deleteTile(int tileX, int tileY) throws DataStoreException {
        throw new UnsupportedOperationException("Not supported.");
    }
}
