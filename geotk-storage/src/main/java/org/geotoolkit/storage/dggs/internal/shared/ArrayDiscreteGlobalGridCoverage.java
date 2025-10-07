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
package org.geotoolkit.storage.dggs.internal.shared;

import java.util.ArrayList;
import java.util.List;
import org.apache.sis.coverage.SampleDimension;
import org.apache.sis.feature.builder.AttributeTypeBuilder;
import org.apache.sis.feature.builder.FeatureTypeBuilder;
import org.apache.sis.geometries.math.TupleArray;
import org.apache.sis.geometries.math.TupleArrayCursor;
import org.apache.sis.storage.DataStoreException;
import org.geotoolkit.storage.dggs.DiscreteGlobalGridGeometry;
import org.geotoolkit.storage.rs.internal.shared.BandedCodeIterator;
import org.geotoolkit.storage.rs.internal.shared.CodedCoverageAsFeatureSet;
import org.geotoolkit.storage.rs.internal.shared.WritableBandedCodeIterator;
import org.opengis.feature.FeatureType;
import org.opengis.util.GenericName;

/**
 * DGGS Coverage backed by a list of samples stored in TupleArrays.
 *
 * @author Johann Sorel (Geomatys)
 */
public final class ArrayDiscreteGlobalGridCoverage extends AbstractDiscreteGlobalGridCoverage{

    private final GenericName name;
    private final List<TupleArray> samples;

    //cached
    private FeatureType type;
    private String[] mapping;

    public ArrayDiscreteGlobalGridCoverage(GenericName name, DiscreteGlobalGridGeometry gridGeometry, List<TupleArray> samples) {
        super(gridGeometry);
        this.name = name;
        this.samples = samples;

        final int nbCell = zones.size();
        for (TupleArray ta : samples) {
            if (ta.getLength() != nbCell) {
                throw new IllegalArgumentException("Number of samples do not match number of cells");
            }
            if (ta.getDimension() != 1) {
                throw new IllegalArgumentException("Samples tuple arrays must have a dimension of 1");
            }
        }
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
    public List<SampleDimension> getSampleDimensions() {
        final List<SampleDimension> lst = new ArrayList<>();
        for (TupleArray ta : samples) {
            lst.addAll(ta.getSampleSystem().getSampleDimensions());
        }
        return lst;
    }

    @Override
    public synchronized FeatureType getSampleType() {
        if (type != null) return type;

        final FeatureTypeBuilder ftb = new FeatureTypeBuilder();
        ftb.setName(name);
        final AttributeTypeBuilder<?>[] created = CodedCoverageAsFeatureSet.toFeatureType(ftb, getSampleDimensions());
        mapping = new String[created.length];
        for (int i = 0; i < created.length; i++) {
            mapping[i] = created[i].getName().toString();
        }
        return ftb.build();
    }

    private final class Iterator extends WritableBandedCodeIterator {

        private int position = -1;

        private final TupleArrayCursor[] cursors;

        public Iterator(FeatureType type, String[] mapping) {
            super(type, mapping);
            cursors = new TupleArrayCursor[samples.size()];
            for (int i = 0; i < cursors.length; i++) {
                cursors[i] = samples.get(i).cursor();
            }
        }

        @Override
        public void setSample(int band, double value) {
            cursors[band].moveTo(position);
            cursors[band].samples().set(0, value);
        }

        @Override
        public int getNumBands() {
            return cursors.length;
        }

        @Override
        public int[] getPosition() {
            return new int[]{position};
        }

        @Override
        public void moveTo(int[] zid) {
            if (zid[0] >= 0 && zid[0] < zones.size()) {
                position = zid[0];
            } else {
                throw new IllegalArgumentException("Zone " + zid[0] +" is not part of this coverage");
            }
        }

        public void moveTo(Object zone) {
            Integer idx = index.get(zone);
            if (idx == null) {
                throw new IllegalArgumentException("Zone " + zone +" is not part of this coverage");
            }
            position = idx;
        }

        @Override
        public boolean next() {
            if (position < zones.size()-1) {
                position ++;
                return true;
            } else {
                return false;
            }
        }

        @Override
        public double getSampleDouble(int band) {
            cursors[band].moveTo(position);
            return cursors[band].samples().get(0);
        }

        @Override
        public void rewind() {
            position = -1;
        }

        @Override
        public void close() throws DataStoreException {
        }

    }

}
