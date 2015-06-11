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

import java.awt.Point;
import java.util.List;
import org.apache.sis.storage.DataStoreException;
import org.geotoolkit.coverage.GridSampleDimension;
import org.geotoolkit.coverage.grid.GridCoverage2D;
import org.geotoolkit.coverage.grid.GridCoverageBuilder;
import org.opengis.coverage.SampleDimension;
import org.opengis.referencing.datum.PixelInCell;
import org.opengis.referencing.operation.MathTransform;

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
    public static GridCoverage2D create(PyramidalCoverageReference ref, GridMosaic mosaic) throws DataStoreException{
        final GridCoverageBuilder gcb = new GridCoverageBuilder();
        gcb.setCoordinateReferenceSystem(mosaic.getPyramid().getCoordinateReferenceSystem());
        gcb.setGridToCRS((MathTransform)AbstractGridMosaic.getTileGridToCRS(mosaic, new Point(0, 0)));
        gcb.setPixelAnchor(PixelInCell.CELL_CORNER);
        gcb.setRenderedImage(new GridMosaicRenderedImage(mosaic));
        final List<GridSampleDimension> dims = ref.getSampleDimensions();
        gcb.setSampleDimensions(dims.toArray(new SampleDimension[0]));
        return gcb.getGridCoverage2D();
    }

}
