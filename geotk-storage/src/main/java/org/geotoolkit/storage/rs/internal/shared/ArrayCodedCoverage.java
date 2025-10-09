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

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import org.apache.sis.feature.builder.FeatureTypeBuilder;
import org.apache.sis.geometries.math.TupleArray;
import org.apache.sis.geometries.math.TupleArrayCursor;
import org.apache.sis.storage.DataStoreException;
import org.geotoolkit.storage.multires.TileMatrices;
import org.geotoolkit.storage.rs.CodedGeometry;
import org.opengis.feature.FeatureType;
import org.opengis.util.FactoryException;
import org.opengis.util.GenericName;

/**
 * Referenced Coverage backed by a list of samples stored in TupleArrays.
 *
 * @author Johann Sorel (Geomatys)
 */
public final class ArrayCodedCoverage extends AbstractCodedCoverage{

    private final List<TupleArray> samples;

    public ArrayCodedCoverage(final GenericName name, CodedGeometry gridGeometry, List<TupleArray> samples) throws FactoryException {
        super(name, gridGeometry, createType(name, samples));

        this.samples = samples;
        final long nbCell = TileMatrices.countCells(extent);
        for (TupleArray ta : samples) {
            if (ta.getLength() != nbCell) {
                throw new IllegalArgumentException("Number of samples do not match number of cells");
            }
            if (ta.getDimension() != 1) {
                throw new IllegalArgumentException("Samples tuple arrays must have a dimension of 1");
            }
        }
    }

    private static FeatureType createType(GenericName name, List<TupleArray> sampleDimensions) {
        final FeatureTypeBuilder ftb = new FeatureTypeBuilder();
        ftb.setName(name);
        for (TupleArray ta : sampleDimensions) {
            CodedCoverageAsFeatureSet.toFeatureType(ftb, ta.getSampleSystem().getSampleDimensions());
        }
        return ftb.build();
    }

    public List<TupleArray> getSamples() {
        return samples;
    }

    @Override
    public BandedCodeIterator createIterator() {
        return createWritableIterator();
    }

    @Override
    public WritableBandedCodeIterator createWritableIterator() {
        return new Iterator(getSampleType(), mapping);
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 79 * super.hashCode() + Objects.hashCode(this.samples);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final ArrayCodedCoverage other = (ArrayCodedCoverage) obj;
        return super.equals(obj) && Objects.equals(this.samples, other.samples);
    }

    private final class Iterator extends WritableBandedCodeIterator {

        private long linearPosition = -1;
        private final long nbCell;

        private final TupleArrayCursor[] cursors;

        public Iterator(FeatureType type, String[] mapping) {
            super(type, mapping);
            nbCell = TileMatrices.countCells(gridGeometry.getExtent());

            cursors = new TupleArrayCursor[samples.size()];
            for (int i = 0; i < cursors.length; i++) {
                cursors[i] = samples.get(i).cursor();
            }
        }

        private long toLinearPosition(int[] pos) {
            long p = 0;
            for (int i = 0; i < dimension; i++) {
                p += pos[i] * dimStep[i];
            }
            return p;
        }

        @Override
        public void setSample(int band, double value) {
            cursors[band].moveTo(Math.toIntExact(linearPosition));
            cursors[band].samples().set(0, value);
        }

        @Override
        public int getNumBands() {
            return cursors.length;
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
        public double getSampleDouble(int band) {
            cursors[band].moveTo(Math.toIntExact(linearPosition));
            return cursors[band].samples().get(0);
        }

        @Override
        public void rewind() {
            linearPosition = -1;
        }

        @Override
        public void close() throws DataStoreException {
        }

    }

}
