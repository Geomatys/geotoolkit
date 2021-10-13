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
import javax.measure.Quantity;
import javax.measure.Unit;
import javax.measure.quantity.Length;
import org.apache.sis.geometry.Envelopes;
import org.apache.sis.internal.storage.AbstractFeatureSet;
import org.apache.sis.internal.storage.query.FeatureQuery;
import org.apache.sis.measure.Quantities;
import org.apache.sis.measure.Units;
import org.apache.sis.referencing.CRS;
import org.apache.sis.storage.DataStoreException;
import org.apache.sis.storage.FeatureSet;
import org.apache.sis.storage.NoSuchDataException;
import org.apache.sis.storage.Resource;
import org.apache.sis.util.collection.BackingStoreException;
import org.geotoolkit.filter.visitor.ExtractBoundsFilterVisitor;
import org.geotoolkit.geometry.jts.JTSEnvelope2D;
import org.geotoolkit.image.BufferedImages;
import org.geotoolkit.referencing.ReferencingUtilities;
import org.geotoolkit.storage.multires.DeferredTile;
import org.geotoolkit.storage.multires.Tile;
import org.geotoolkit.storage.multires.TileMatrices;
import org.geotoolkit.storage.multires.TileMatrix;
import org.geotoolkit.storage.multires.TileMatrixSet;
import org.opengis.feature.Feature;
import org.opengis.feature.FeatureType;
import org.opengis.filter.Filter;
import org.opengis.geometry.Envelope;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.TransformException;
import org.opengis.util.FactoryException;
import org.geotoolkit.storage.multires.TiledResource;

/**
 * A utility class which is capable of reading features from a pyramid.
 *
 * @author Johann Sorel (Geomatys)
 */
public class TileMatrixSetFeatureReader {

    private final TiledResource resource;
    private final FeatureType type;

    public TileMatrixSetFeatureReader(TiledResource resource, FeatureType type) {
        this.resource = resource;
        this.type = type;
    }

    public Stream<Feature> features(FeatureQuery query, boolean bln) throws DataStoreException {

        final List<TileMatrixSet> pyramids = TileMatrices.getTileMatrixSets(resource);
        if (pyramids.isEmpty()) {
            return Stream.empty();
        }

        final TileMatrixSet pyramid = pyramids.get(0);
        if (pyramid.getTileMatrices().isEmpty()) {
            return Stream.empty();
        }

        TileMatrix mosaic;
        final Collection<TileMatrix> mosaics = pyramid.getTileMatrices(pyramid.getScales()[0]);
        if (mosaics.isEmpty()) {
            return Stream.empty();
        } else if (mosaics.size() != 1) {
            throw new DataStoreException("Only one mosaic for a given scale should exist.");
        }
        mosaic = mosaics.iterator().next();

        Envelope env = null;
        if (query != null && query.getLinearResolution() != null) {
            final Quantity<Length> linearResolution = query.getLinearResolution();
            try {
                double mosaicLinearRes = getLinearResolution(mosaic, linearResolution.getUnit()).getValue().doubleValue();

                for (TileMatrix m : pyramid.getTileMatrices()) {
                    Quantity<Length> mr = getLinearResolution(m, linearResolution.getUnit());
                    if (mr.getValue().doubleValue() <= linearResolution.getValue().doubleValue()
                        && mr.getValue().doubleValue() > mosaicLinearRes) {
                        mosaic = m;
                        mosaicLinearRes = mr.getValue().doubleValue();
                    }
                }
            } catch (TransformException | FactoryException ex) {
                throw new DataStoreException(ex.getMessage(), ex);
            }

            Filter filter = query.getSelection();
            if (filter != null) {
                JTSEnvelope2D e = new JTSEnvelope2D(pyramid.getCoordinateReferenceSystem());
                e = (JTSEnvelope2D) ExtractBoundsFilterVisitor.bbox(filter, e);
                if (!e.isEmpty() && !e.isNull()) {
                    env = e;
                }
            }
        }

        final Dimension gridSize = mosaic.getGridSize();
        final Rectangle area;
        if (env != null) {
            try {
                env = Envelopes.transform(env, mosaic.getUpperLeftCorner().getCoordinateReferenceSystem());
            } catch (TransformException ex) {
                throw new DataStoreException(ex.getMessage(), ex);
            }
            try {
                area = TileMatrices.getTilesInEnvelope(mosaic, env);
            } catch (NoSuchDataException ex) {
                return Stream.empty();
            }
        } else {
            area = new Rectangle(gridSize);
        }

        final TileIterator iterator = new TileIterator(mosaic, area);

        if (query == null) {
            return StreamSupport.stream(Spliterators.spliteratorUnknownSize(iterator, 0), false)
                    .onClose(iterator::close);
        } else {
            //create a fake subset
            final FeatureSet subfs = new AbstractFeatureSet(null) {
                @Override
                public FeatureType getType() throws DataStoreException {
                    return type;
                }
                @Override
                public Stream<Feature> features(boolean parallel) throws DataStoreException {
                    final Stream<Feature> stream = StreamSupport.stream(Spliterators.spliteratorUnknownSize(iterator, 0), false);
                    stream.onClose(iterator::close);
                    return stream;
                }
            };

            return subfs.subset(query).features(bln);
        }
    }

    private Quantity<Length> getLinearResolution(TileMatrix mosaic, Unit<Length> unit) throws TransformException, FactoryException {
        final double[] res = new double[]{mosaic.getScale(), mosaic.getScale()};
        final double[] newRes = new double[2];
        final CoordinateReferenceSystem target2DCRS = CRS.forCode("EPSG:3395");
        ReferencingUtilities.convertResolution(mosaic.getEnvelope(), res, target2DCRS, newRes);
        return Quantities.create(newRes[0], Units.METRE).to(unit);
    }

    private static class TileIterator implements Iterator<Feature> {

        private final TileMatrix mosaic;
        private final Iterator<Point> pointIte;
        private Stream<Feature> subStream;
        private Iterator<Feature> subIte;
        private Feature next;


        public TileIterator(TileMatrix mosaic, Rectangle rectangle) {
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
