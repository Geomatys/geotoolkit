/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2025, Geomatys
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
package org.geotoolkit.storage.rs.internal.shared;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.apache.sis.coverage.grid.GridExtent;
import org.apache.sis.storage.DataStoreException;
import org.geotoolkit.referencing.rs.ReferenceSystems;
import org.geotoolkit.storage.multires.TileMatrices;
import org.geotoolkit.storage.rs.CodeIterator;
import org.geotoolkit.storage.rs.CodedGeometry;
import org.geotoolkit.storage.rs.WritableCodeIterator;
import org.opengis.feature.Feature;
import org.opengis.feature.FeatureType;
import org.opengis.util.FactoryException;
import org.opengis.util.GenericName;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public final class FeatureCodedCoverage extends AbstractCodedCoverage{

    private final List<Feature> features;

    public FeatureCodedCoverage(GenericName name, CodedGeometry gridGeometry, FeatureType type) throws FactoryException {
        super(name, gridGeometry, type);
        this.features = new ArrayList<>();
        final GridExtent extent = gridGeometry.slice(ReferenceSystems.getHorizontalComponent(gridGeometry.getReferenceSystem()).get()).get().getExtent();
        for (int i = 0, n = Math.toIntExact(extent.getSize(0)); i < n; i++) {
            features.add(type.newInstance());
        }
    }
    public FeatureCodedCoverage(GenericName name, CodedGeometry gridGeometry, List<Feature> features) throws FactoryException {
        super(name, gridGeometry, features.get(0).getType());
        this.features = features;
    }

    @Override
    public CodeIterator createIterator() {
        return createWritableIterator();
    }

    @Override
    public WritableCodeIterator createWritableIterator() {
        return new Iterator();
    }

    private class Iterator implements WritableCodeIterator {

        private long linearPosition = -1;
        private final long nbCell;

        public Iterator() {
            nbCell = TileMatrices.countCells(gridGeometry.getExtent());
        }

        private long toLinearPosition(int[] pos) {
            long p = 0;
            for (int i = 0; i < dimension; i++) {
                p += pos[i] * dimStep[i];
            }
            return p;
        }

        @Override
        public int[] getPosition() {
            long remain = linearPosition;
            final int[] pos = new int[dimension];
            for (int i = 0; i < pos.length; i++) {
                long k = remain / dimStep[i];
                pos[i] = Math.toIntExact(dimOffsets[i] + k);
                remain -= k * dimStep[i];
            }
            return pos;
        }

        @Override
        public void moveTo(int[] pos) {
            final long lp = toLinearPosition(pos);
            if (lp < 0 || lp >= nbCell) {
                throw new IllegalArgumentException("Position " + Arrays.toString(pos) +" is not part of this coverage");
            }
            linearPosition = lp;
        }

        @Override
        public boolean next() {
            if (linearPosition < nbCell-1) {
                linearPosition ++;
                return true;
            } else {
                return false;
            }
        }

        @Override
        public void rewind() {
            linearPosition = -1;
        }

        @Override
        public void close() throws DataStoreException {
        }

        @Override
        public Feature getSample() {
            return features.get(Math.toIntExact(linearPosition));
        }
    }
}
