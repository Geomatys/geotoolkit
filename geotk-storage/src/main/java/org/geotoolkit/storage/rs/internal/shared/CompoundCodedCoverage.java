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

import java.util.List;
import org.apache.sis.coverage.SampleDimension;
import org.geotoolkit.storage.rs.CodeIterator;
import org.geotoolkit.storage.rs.CodedCoverage;
import org.geotoolkit.storage.rs.WritableCodeIterator;
import org.opengis.feature.FeatureType;
import org.opengis.util.FactoryException;
import org.opengis.util.GenericName;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public final class CompoundCodedCoverage extends AbstractCodedCoverage{

    private final CodedCoverage[] coverages;
    private final int[] bandToCoverage;
    private final int[] bandToCoverageBand;

    public CompoundCodedCoverage(GenericName name, CodedCoverage[] coverages, FeatureType sampleType) throws FactoryException {
        super(name, coverages[0].getGeometry(), sampleType);
        this.coverages = coverages;
        this.bandToCoverage = new int[sampleType.getProperties(true).size()];
        this.bandToCoverageBand = new int[bandToCoverage.length];

        int idx = 0;
        for (int i = 0; i < coverages.length; i++) {
            final List<SampleDimension> sds = coverages[i].getSampleDimensions();
            int si = 0;
            for (SampleDimension sd : sds) {
                bandToCoverage[idx] = i;
                bandToCoverageBand[idx] = si;
                idx++;
                si++;
            }
        }
    }

    @Override
    public CodeIterator createIterator() {
        return new BandedIterator(type, mapping);
    }

    @Override
    public WritableCodeIterator createWritableIterator() {
        throw new UnsupportedOperationException("Not supported.");
    }

    private final class BandedIterator extends BandedCodeIterator {

        private final BandedCodeIterator[] iterators;

        BandedIterator(FeatureType type, String[] mapping) {
            super(type, mapping);

            iterators = new BandedCodeIterator[coverages.length];
            for (int i = 0; i < iterators.length; i++) {
                iterators[i] = (BandedCodeIterator) coverages[i].createIterator();
            }
        }

        @Override
        public double getSampleDouble(int band) {
            final BandedCodeIterator ci = iterators[bandToCoverage[band]];
            return ci.getSampleDouble(bandToCoverageBand[band]);
        }

        @Override
        public void setPropertyValue(String name, Object value) throws IllegalArgumentException {
            throw new UnsupportedOperationException("Not supported.");
        }

        @Override
        public int[] getPosition() {
            return iterators[0].getPosition();
        }

        @Override
        public void moveTo(int[] zid) {
            for (int i = 0; i < iterators.length; i++) {
                iterators[i].moveTo(zid);
            }
        }

        @Override
        public boolean next() {
            boolean hasNext = false;
            for (int i = 0; i < iterators.length; i++) {
                hasNext = iterators[i].next();
            }
            return hasNext;
        }

        @Override
        public void rewind() {
            for (int i = 0; i < iterators.length; i++) {
                iterators[i].rewind();
            }
        }

    }

}
