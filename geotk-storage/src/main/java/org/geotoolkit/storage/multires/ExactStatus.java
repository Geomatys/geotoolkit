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
import org.apache.sis.storage.tiling.TileStatus;
import org.geotoolkit.image.BufferedImages;
import static org.geotoolkit.storage.multires.TileMatrixStatus.STATUS_CM;

/**
 * A tile matrix status which compute information tile by tile.
 *
 * @author Johann Sorel (Geomatys)
 */
public final class ExactStatus extends TileMatrixStatus {

    private final List<GridExtent> extents;

    public ExactStatus(TileMatrix matrix, GridExtent queryExtent, List<GridExtent> extents) {
        super(matrix, queryExtent);
        this.extents = extents;
    }

    @Override
    public Stream<GridExtent> toGridExtents() throws DataStoreException {
        return extents.stream();
    }

    @Override
    public RenderedImage toImage() throws DataStoreException {

        final int width = Math.toIntExact(queryExtent.getSize(0));
        final int height = Math.toIntExact(queryExtent.getSize(1));
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
                        TileStatus status;
                        try {
                            status = matrix.getTileStatus(t[0], t[1]);
                        } catch (DataStoreException ex) {
                            status = TileStatus.IN_ERROR;
                        }
                        synchronized (ite) {
                            ite.moveTo((int)(t[0] - queryExtent.getLow(0)), (int)(t[1] - queryExtent.getLow(1)));
                            switch (status) {
                                case EXISTS : ite.setSample(0, 1); break;
                                case IN_ERROR : ite.setSample(0, 4); break;
                                case MISSING : ite.setSample(0, 2); break;
                                case OUTSIDE_EXTENT : ite.setSample(0, 3); break;
                                case UNKNOWN : ite.setSample(0, 0); break;
                            }
                        }
                    }
                });
            } catch (DisjointExtentException ex) {
                //continue
            }
        }

        //place it at correct offset
        return ReshapedImage.translate(image, queryExtent.getLow(0), queryExtent.getLow(1));
    }

}
