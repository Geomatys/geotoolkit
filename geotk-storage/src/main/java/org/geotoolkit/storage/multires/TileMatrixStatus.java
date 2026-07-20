/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2026, Geomatys
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

import java.awt.image.BufferedImage;
import java.awt.image.IndexColorModel;
import java.awt.image.RenderedImage;
import java.util.Hashtable;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Stream;
import org.apache.sis.coverage.grid.DisjointExtentException;
import org.apache.sis.coverage.grid.GridExtent;
import org.apache.sis.image.WritablePixelIterator;
import org.apache.sis.image.internal.shared.ReshapedImage;
import org.apache.sis.storage.DataStoreException;
import org.geotoolkit.image.BufferedImages;

/**
 * Data region description.
 *
 * Note : this class is in early stage and may evolve or be renamed.
 *
 * @author Johann Sorel (Geomatys)
 */
public abstract class TileMatrixStatus {

    /**
     * Color model of tile status coverage.
     * <ul>
     *   <li>UNKNOWN   BLACK</li>
     *   <li>EXISTS    GREEN</li>
     *   <li>MISSING   BLUE</li>
     *   <li>OUTSIDE_EXTENT  GRAY</li>
     *   <li>IN_ERROR   RED</li>
     * </ul>
     */
    public static final IndexColorModel STATUS_CM;

    protected final TileMatrix matrix;
    protected final GridExtent queryExtent;

    static {
        STATUS_CM = new IndexColorModel(3, 5,
                    new byte[]{  0,        0,        0,(byte)100,(byte)255},
                    new byte[]{  0,(byte)255,        0,(byte)100,        0},
                    new byte[]{  0,        0,(byte)255,(byte)100,        0});
    }

    public TileMatrixStatus(TileMatrix matrix, GridExtent queryExtent) {
        this.matrix = matrix;
        this.queryExtent = queryExtent;
    }

    /**
     * @return queryed extent
     */
    public GridExtent getQueryExtent() {
        return queryExtent;
    }

    /**
     * Query the matrix for a list of extents where tiles exist.
     * <ul>
     *   <li>All tiles in the matrix are guaranted to be included in those extents.</li>
     *   <li>The returned extents may contain missing tiles.</li>
     *   <li>An empty stream means the tilematrix is guaranted to be empty.</li>
     *   <li>An empty optional means the information is too expensive to compute or is not available.
     *      Therefor no assumption should be made and tiles may be anywhere on the TileMatrix.
     *   </li>
     * </ul>
     * @return stream of extent where tiles exists, empty optional if the information is too expensive to compute
     *        or is not available.
     */
    public abstract Stream<GridExtent> toGridExtents() throws DataStoreException;

    /**
     * View the tile status as an image, one pixel for each tile.
     * The image pixels are aligned with the tile matrix schema.
     *
     * The image pixel value is coded on 3bits :
     *  - 0 : UNKNOWN = BLACK
     *  - 1 : EXISTS  = GREEN
     *  - 2 : MISSING = BLUE
     *  - 3 : OUTSIDE_EXTENT = GRAY
     *  - 4 : IN_ERROR = RED
     *
     * @return image
     * @throws DataStoreException
     */
    public RenderedImage toImage() throws DataStoreException {

        final int width = (int) queryExtent.getSize(0);
        final int height = (int) queryExtent.getSize(1);
        final BufferedImage image = new BufferedImage(STATUS_CM,
            STATUS_CM.createCompatibleWritableRaster(width,height),false, new Hashtable<>());
        final WritablePixelIterator ite = WritablePixelIterator.create(image);
        BufferedImages.setAll(image, new double[]{2});
        final List<GridExtent> areas = toGridExtents().toList();
        for (GridExtent area : areas) {
            try {
                final GridExtent intersect = area.intersect(queryExtent);
                intersect.latticePointStream(true).forEach(new Consumer<long[]>() {
                    @Override
                    public void accept(long[] t) {
                        synchronized (ite) {
                            ite.moveTo((int)(t[0] - queryExtent.getLow(0)), (int)(t[1] - queryExtent.getLow(1)));
                            ite.setSample(0, 1);
                        }
                    }
                });
            } catch (DisjointExtentException ex) {
                //continue
            }
        }

        //place it
        return ReshapedImage.translate(image, queryExtent.getLow(0), queryExtent.getLow(1));
    }
}
