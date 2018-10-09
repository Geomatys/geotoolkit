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
package org.geotoolkit.data.multires;

import java.awt.Dimension;
import java.awt.Rectangle;
import java.util.Map;
import java.util.stream.Stream;
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
    private final Rectangle dataExtent;

    public DefiningMosaic(String identifier, DirectPosition upperLeft, double scale, Dimension tileSize, Dimension gridSize) {
        this(identifier,upperLeft, scale, tileSize, gridSize, (Rectangle) null);
    }

    public DefiningMosaic(String identifier, DirectPosition upperLeft, double scale, Dimension tileSize, Dimension gridSize, Dimension dataExtent) {
        this(identifier,upperLeft, scale, tileSize, gridSize, new Rectangle(dataExtent));
    }

    public DefiningMosaic(String identifier, DirectPosition upperLeft, double scale, Dimension tileSize, Dimension gridSize, Rectangle dataExtent) {
        this.identifier = identifier;
        this.upperLeft = upperLeft;
        this.scale = scale;
        this.tileSize = tileSize;
        this.gridSize = gridSize;
        this.dataExtent = dataExtent != null ? dataExtent :
                new Rectangle(0,0, gridSize.width*tileSize.width, gridSize.height * tileSize.height);
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
    public Rectangle getDataExtent() {
        return new Rectangle(dataExtent);
    }

    @Override
    public boolean isMissing(int col, int row) throws PointOutsideCoverageException {
        throw new UnsupportedOperationException("Not supported.");
    }

    @Override
    public Tile getTile(int col, int row, Map hints) throws DataStoreException {
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
