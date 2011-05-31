/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2011, Geomatys
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
package org.geotoolkit.process.coverage;

import java.util.AbstractCollection;

import org.geotoolkit.coverage.io.GridCoverageReader;
import org.geotoolkit.data.DataStoreRuntimeException;
import org.geotoolkit.data.FeatureIterator;

import org.opengis.coverage.grid.GridEnvelope;
import org.opengis.feature.Feature;

/**
 *  FeatureCollection for raster process
 * @author Quentin Boileau
 * @module pending
 */
public abstract class RasterFeatureCollection extends AbstractCollection<Feature> {

    private final GridCoverageReader reader;
    private final int minX;
    private final int minY;
    private final int maxX;
    private final int maxY;

    /**
     * Constructor
     * @param reader GridCoverageReader
     * @param range GridEnvelope
     */
    public RasterFeatureCollection(final GridCoverageReader reader, final GridEnvelope range) {
        this.reader = reader;

        this.minX = range.getLow(0);
        this.minY = range.getLow(1);
        this.maxX = range.getHigh(0) + 1;
        this.maxY = range.getHigh(1) + 1;

    }

    /**
     * Return the feature create by the process in 
     * cell (x,y)
     *
     * @param x
     * @param y
     * @return Feature in cell (x,y)
     */
    protected abstract Feature create(final int x, final int y);

    /**
     * Return the reader
     * @return GridCoverageReader
     */
    protected GridCoverageReader getReader() {
        return reader;
    }

    /**
     * @return the minX in the grid
     */
    protected int getMinX() {
        return minX;
    }

    /**
     * @return the minY in the grid
     */
    protected int getMinY() {
        return minY;
    }

    /**
     * @return the maxX in the grid
     */
    protected int getMaxX() {
        return maxX;
    }

    /**
     * @return the maxY in the grid
     */
    protected int getMaxY() {
        return maxY;
    }

    /**
     * Return Iterator connecting to GridCoverage
     * @return Iterator
     */
    @Override
    public FeatureIterator<Feature> iterator() {
        return new RasterFeatureIterator();
    }

    /**
     *  {@inheritDoc }
     */
    @Override
    public int size() {
        RasterFeatureIterator iter = new RasterFeatureIterator();
        int i = 0;
        while (iter.hasNext()) {
            i++;
            iter.next();
        }
        return i;
    }

    /**
     * Implementation ofIterator for RasterFeatureCollection
     * @author Quentin Boileau
     * @module pending
     */
    protected class RasterFeatureIterator implements FeatureIterator<Feature> {

        private int iter;

        /**
         * Initialize iterator
         */
        public RasterFeatureIterator() {
            iter = 0;
        }

        /**
         * Return the next Feature create by the process
         * @return Feature
         */
        @Override
        public Feature next() {

            int x = iter % (maxX - minX);
            int y = (int) iter / (maxX - minX);
            iter++;
            return create(x, y);
        }

        /**
         * Close iterator
         */
        @Override
        public void close() {
        }

        /**
         * Return hasNext() result from the the GridCoverage
         * hasNext() function is based on the GridEnvelope range
         */
        @Override
        public boolean hasNext() {
            int buff = iter;
            boolean test = false;
            if (buff++ <= ((maxX - minX) * (maxY - minY)) - 1) {
                test = true;
            }
            return test;
        }

        /**
         * Useless because current Collection can't be modified
         */
        @Override
        public void remove() {
            throw new DataStoreRuntimeException("Unmodifiable collection");
        }
    }
}
