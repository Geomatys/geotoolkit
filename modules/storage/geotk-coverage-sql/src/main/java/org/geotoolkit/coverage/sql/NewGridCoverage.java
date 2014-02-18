/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2007-2012, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2007-2012, Geomatys
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
package org.geotoolkit.coverage.sql;

import java.io.File;
import java.awt.geom.AffineTransform;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import javax.imageio.spi.ImageReaderSpi;
import org.geotoolkit.image.io.mosaic.Tile;
import org.geotoolkit.coverage.grid.GridGeometry2D;


/**
 * Information about a grid coverage to be added in the database.
 * Even if the image is not really a tile, this is a convenient way
 * to carry those information.
 *
 * @author Martin Desruisseaux (IRD, Geomatys)
 * @version 3.21
 *
 * @since 3.21
 * @module
 *
 * @todo This is an ugly hack. Needs to do something better.
 */
final class NewGridCoverage extends Tile {
    /**
     * For cross-version compatibility.
     */
    private static final long serialVersionUID = -9161647875115463421L;

    /**
     * The <cite>grid to CRS</cite> transform.
     */
    private final AffineTransform gridToCRS;

    /**
     * The coordinate reference system.
     */
    final CoordinateReferenceSystem crs;

    /**
     * Creates a new "tile" for the given image.
     */
    NewGridCoverage(final ImageReaderSpi readerSpi, final File file,
            final GridGeometry2D gridGeometry, final AffineTransform gridToCRS)
    {
        super(readerSpi, file, 0, gridGeometry.getExtent2D());
        this.gridToCRS = gridToCRS;
        this.crs = gridGeometry.getCoordinateReferenceSystem2D();
    }

    /**
     * Returns the transform specified at construction time.
     */
    @Override
    public AffineTransform getGridToCRS() {
        return gridToCRS;
    }
}
