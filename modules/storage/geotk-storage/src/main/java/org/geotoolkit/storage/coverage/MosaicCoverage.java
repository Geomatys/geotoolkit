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
package org.geotoolkit.storage.coverage;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.image.RenderedImage;
import java.util.List;
import org.apache.sis.coverage.SampleDimension;
import org.apache.sis.coverage.grid.GridCoverage;
import org.apache.sis.coverage.grid.GridExtent;
import org.apache.sis.coverage.grid.GridGeometry;
import org.apache.sis.internal.coverage.GridCoverage2D;
import org.apache.sis.referencing.operation.transform.LinearTransform;
import org.apache.sis.storage.DataStoreException;
import org.apache.sis.storage.GridCoverageResource;
import org.geotoolkit.storage.multires.Mosaic;
import org.geotoolkit.storage.multires.Pyramids;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.datum.PixelInCell;
import org.opengis.util.FactoryException;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public final class MosaicCoverage {

    private MosaicCoverage(){}

    /**
     * Create a dynamic grid coverage 2d of the given mosaic.
     * Datas are not loaded in memory.
     *
     * @param mosaic
     * @return
     */
    public static GridCoverage create(GridCoverageResource ref, Mosaic mosaic) throws DataStoreException, FactoryException {
        final Dimension gridSize = mosaic.getGridSize();
        return create(ref, mosaic, new Rectangle(0, 0, gridSize.width, gridSize.height));
    }

    /**
     * Create a dynamic grid coverage 2d of the given mosaic.
     * Datas are not loaded in memory.
     *
     * @param mosaic
     * @return
     */
    public static GridCoverage create(GridCoverageResource ref, Mosaic mosaic, Rectangle gridRange) throws DataStoreException, FactoryException {

        Dimension maxgridSize = mosaic.getGridSize();
        if (gridRange.x < 0 || gridRange.y < 0 || (gridRange.x+gridRange.width) > maxgridSize.width || (gridRange.y+gridRange.height) > maxgridSize.height) {
            throw new DataStoreException("Invalid grid range " + gridRange+", grid limit is " + maxgridSize);
        }

        final LinearTransform gridToCrs = Pyramids.getTileGridToCRS(mosaic, new Point(gridRange.x, gridRange.y), PixelInCell.CELL_CENTER);
        final CoordinateReferenceSystem crs = mosaic.getUpperLeftCorner().getCoordinateReferenceSystem();
        final Dimension gridSize = gridRange.getSize();
        final Dimension tileSize = mosaic.getTileSize();
        final GridExtent extent = new GridExtent(gridSize.width * tileSize.width, gridSize.height * tileSize.height);
        final GridGeometry gridGeom = new GridGeometry(extent, PixelInCell.CELL_CENTER, gridToCrs, crs);
        final List<SampleDimension> dims = ref.getSampleDimensions();
        final RenderedImage image = new MosaicImage(mosaic, gridRange, dims);
        return new GridCoverage2D(gridGeom, dims, image);
    }

}
