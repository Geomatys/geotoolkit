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
import java.awt.image.RenderedImage;
import java.util.List;
import org.apache.sis.coverage.SampleDimension;
import org.apache.sis.coverage.grid.GridCoverage;
import org.apache.sis.coverage.grid.GridExtent;
import org.apache.sis.coverage.grid.GridGeometry;
import org.apache.sis.internal.coverage.GridCoverage2D;
import org.apache.sis.referencing.operation.transform.LinearTransform;
import org.apache.sis.storage.DataStoreException;
import org.geotoolkit.storage.multires.Mosaic;
import org.geotoolkit.storage.multires.Pyramids;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.datum.PixelInCell;
import org.opengis.util.FactoryException;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public final class GridMosaicCoverage2D {

    private GridMosaicCoverage2D(){}

    /**
     * Create a dynamic grid coverage 2d of the given mosaic.
     * Datas are not loaded in memory.
     *
     * @param mosaic
     * @return
     */
    public static GridCoverage create(org.apache.sis.storage.GridCoverageResource ref, Mosaic mosaic) throws DataStoreException, FactoryException {

        final LinearTransform gridToCrs = Pyramids.getTileGridToCRS(mosaic, new Point(0, 0), PixelInCell.CELL_CENTER);
        final CoordinateReferenceSystem crs = mosaic.getUpperLeftCorner().getCoordinateReferenceSystem();
        final Dimension gridSize = mosaic.getGridSize();
        final Dimension tileSize = mosaic.getTileSize();
        final GridExtent extent = new GridExtent(gridSize.width * tileSize.width, gridSize.height * tileSize.height);
        final GridGeometry gridGeom = new GridGeometry(extent, PixelInCell.CELL_CENTER, gridToCrs, crs);
        final RenderedImage image = new GridMosaicRenderedImage(mosaic);
        final List<SampleDimension> dims = ref.getSampleDimensions();

        return new GridCoverage2D(gridGeom, dims, image);
    }

}
