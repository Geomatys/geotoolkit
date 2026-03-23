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
import java.util.Arrays;
import java.util.List;
import org.apache.sis.coverage.SampleDimension;
import org.apache.sis.geometries.math.Array;
import org.apache.sis.geometries.math.Cursor;
import org.apache.sis.geometries.math.DataType;
import org.apache.sis.geometries.math.NDArrays;
import org.apache.sis.geometries.math.SampleSystem;
import org.apache.sis.storage.DataStoreException;
import org.geotoolkit.storage.dggs.DiscreteGlobalGridGeometry;
import org.geotoolkit.storage.rs.CodeIterator;
import org.geotoolkit.storage.rs.WritableCodeIterator;
import org.opengis.util.GenericName;

/**
 * DGGS Coverage backed by a list of samples stored in TupleArrays.
 *
 * @author Johann Sorel (Geomatys)
 */
public final class ArrayDiscreteGlobalGridCoverage extends IndexedDiscreteGlobalGridCoverage{

    private final GenericName name;
    private final List<Array> samples;

    public ArrayDiscreteGlobalGridCoverage(GenericName name, DiscreteGlobalGridGeometry gridGeometry, List<Array> samples) {
        super(gridGeometry);
        this.name = name;
        this.samples = samples;

        final int nbCell = zones.size();
        for (Array ta : samples) {
            if (ta.getLength() != nbCell) {
                throw new IllegalArgumentException("Number of samples do not match number of cells");
            }
            if (ta.getDimension() != 1) {
                throw new IllegalArgumentException("Samples tuple arrays must have a dimension of 1");
            }
        }
    }

    public ArrayDiscreteGlobalGridCoverage(GenericName name, DiscreteGlobalGridGeometry gridGeometry, SampleDimension ... sampleDimensions) {
        super(gridGeometry);
        this.name = name;

        final int nbZones = Math.toIntExact(gridGeometry.getExtent().getSize(0));
        final List<Array> samples = new ArrayList<>();
        for (int i = 0; i < sampleDimensions.length; i++) {
            final SampleSystem ss = new SampleSystem(DataType.DOUBLE, sampleDimensions[i]);
            double[] arr = new double[nbZones];
            Arrays.fill(arr, Double.NaN);
            samples.add(NDArrays.of(ss, arr));
        }
        this.samples = samples;
    }

    public List<Array> getSamples() {
        return samples;
    }

    @Override
    public CodeIterator createIterator() {
        return createWritableIterator();
    }

    @Override
    public WritableCodeIterator createWritableIterator() {
        return new Iterator();
    }

    @Override
    public List<SampleDimension> getSampleDimensions() {
        final List<SampleDimension> lst = new ArrayList<>();
        for (Array ta : samples) {
            lst.addAll(ta.getSampleSystem().getSampleDimensions());
        }
        return lst;
    }

    private final class Iterator implements WritableCodeIterator {

        private int position = -1;

        private final Cursor[] cursors;

        public Iterator() {
            cursors = new Cursor[samples.size()];
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
