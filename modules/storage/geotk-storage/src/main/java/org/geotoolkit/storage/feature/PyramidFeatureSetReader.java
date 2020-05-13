/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2020, Geomatys
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
package org.geotoolkit.storage.feature;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Spliterators;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import org.apache.sis.internal.storage.query.SimpleQuery;
import org.apache.sis.storage.DataStoreException;
import org.apache.sis.storage.FeatureSet;
import org.apache.sis.storage.Resource;
import org.apache.sis.util.collection.BackingStoreException;
import org.geotoolkit.image.BufferedImages;
import org.geotoolkit.storage.multires.DeferredTile;
import org.geotoolkit.storage.multires.Mosaic;
import org.geotoolkit.storage.multires.MultiResolutionResource;
import org.geotoolkit.storage.multires.Pyramid;
import org.geotoolkit.storage.multires.Pyramids;
import org.geotoolkit.storage.multires.Tile;
import org.opengis.feature.Feature;
import org.opengis.feature.FeatureType;

/**
 * A utility class which is capable of reading features from a pyramid.
 *
 * @author Johann Sorel (Geomatys)
 */
public class PyramidFeatureSetReader {

    private final MultiResolutionResource resource;
    private final FeatureType type;

    public PyramidFeatureSetReader(MultiResolutionResource resource, FeatureType type) {
        this.resource = resource;
        this.type = type;
    }

    public Stream<Feature> features(SimpleQuery query, boolean bln) throws DataStoreException {
        if (query != null) {
            throw new DataStoreException("Queries not supported yet.");
        }

        final List<Pyramid> pyramids = Pyramids.getPyramids(resource);
        if (pyramids.isEmpty()) {
            return Stream.empty();
        }

        final Pyramid pyramid = pyramids.get(0);
        final Collection<Mosaic> mosaics = pyramid.getMosaics(pyramid.getScales()[0]);
        if (mosaics.isEmpty()) {
            return Stream.empty();
        } else if (mosaics.size() != 1) {
            throw new DataStoreException("Only one mosaic for a given scale should exist.");
        }

        final Mosaic mosaic = mosaics.iterator().next();
        final Dimension gridSize = mosaic.getGridSize();
        final Rectangle area = new Rectangle(gridSize);

        final TileIterator iterator = new TileIterator(mosaic, area);

        final Stream<Feature> stream = StreamSupport.stream(Spliterators.spliteratorUnknownSize(iterator, 0), false);
        stream.onClose(iterator::close);
        return stream;
    }

    private static class TileIterator implements Iterator<Feature> {

        private final Mosaic mosaic;
        private final Iterator<Point> pointIte;
        private Stream<Feature> subStream;
        private Iterator<Feature> subIte;
        private Feature next;


        public TileIterator(Mosaic mosaic, Rectangle rectangle) {
            this.mosaic = mosaic;
            pointIte = BufferedImages.pointStream(rectangle).iterator();
        }

        @Override
        public boolean hasNext() throws BackingStoreException {
            findNext();
            return next != null;
        }

        @Override
        public Feature next() throws BackingStoreException {
            findNext();
            if (next == null) {
                throw new NoSuchElementException();
            }
            Feature c = next;
            next = null;
            return c;
        }

        private void findNext() throws BackingStoreException {
            if (next != null) return;

            if (subStream != null) {
                if (subIte.hasNext()) {
                    next = subIte.next();
                    return;
                } else {
                    subStream.close();
                    subIte = null;
                    subStream = null;
                }
            }

            try {
                while (pointIte.hasNext()) {
                    final Point pt = pointIte.next();
                    Tile tile = mosaic.getTile(pt.x, pt.y);

                    Resource resource = tile;
                    if (tile instanceof DeferredTile) {
                        DeferredTile dt = (DeferredTile) tile;
                        resource = dt.open();
                    }
                    if (resource instanceof FeatureSet) {
                        subStream = ((FeatureSet) resource).features(false);
                        subIte = subStream.iterator();
                    }

                    if (subStream != null) {
                        if (subIte.hasNext()) {
                            next = subIte.next();
                            return;
                        } else {
                            subStream.close();
                            subIte = null;
                            subStream = null;
                        }
                    }
                }
            } catch (DataStoreException ex) {
                throw new BackingStoreException(ex.getMessage(), ex);
            }
        }

        public void close() {
            if (subStream != null) {
                subStream.close();
                subIte = null;
                subStream = null;
            }
        }
    }
}
