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
import org.geotoolkit.referencing.rs.Code;
import org.geotoolkit.referencing.rs.CodeOperation;
import org.geotoolkit.referencing.rs.ReferenceSystems;
import org.geotoolkit.storage.multires.TileMatrices;
import org.geotoolkit.storage.rs.CodeIterator;
import org.geotoolkit.storage.rs.CodeTransform;
import org.geotoolkit.storage.rs.CodedCoverage;
import org.geotoolkit.storage.rs.CodedGeometry;
import org.geotoolkit.storage.rs.WritableCodeIterator;
import org.opengis.referencing.operation.TransformException;
import org.opengis.util.FactoryException;
import org.opengis.util.GenericName;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public final class ResampledCodedCoverage extends AbstractCodedCoverage {

    private final CodedCoverage base;

    private final CodeTransform sourceGridTrs;
    private final CodeTransform targetGridTrs;
    private final CodeOperation operation;


    public ResampledCodedCoverage(GenericName name, CodedCoverage base, CodedGeometry target) throws FactoryException {
        super(name, target, base.getSampleDimensions());
        this.base = base;

        final CodedGeometry baseGeometry = base.getGeometry();
        sourceGridTrs = baseGeometry.getGridToRS();
        targetGridTrs = target.getGridToRS();
        operation = ReferenceSystems.findOperation(target.getReferenceSystem(), baseGeometry.getReferenceSystem(), null);
    }

    @Override
    public CodeIterator createIterator() {
        return new BandedIterator();
    }

    @Override
    public WritableCodeIterator createWritableIterator() {
        throw new UnsupportedOperationException("Not supported.");
    }

    private class BandedIterator implements CodeIterator {

        private long linearPosition = -1;
        private final CodeIterator sourceIterator;
        private final long nbCell;

        private boolean sourceBanded;
        private boolean sourceMoved = false;
        private boolean sourceExist = false;

        BandedIterator() {
            sourceIterator = base.createIterator();
            nbCell = TileMatrices.countCells(extent);
            sourceBanded = sourceIterator instanceof CodeIterator;
        }

        @Override
        public int getNumBands() {
            return base.getSampleDimensions().size();
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
            sourceMoved = false;
        }

        @Override
        public boolean next() {
            sourceMoved = false;
            if (linearPosition < nbCell-1) {
                linearPosition ++;
                return true;
            } else {
                return false;
            }
        }

        private void moveSource() {
            if (sourceMoved) return;
            sourceMoved = true;
            try {
                final int[] targetPos = getPosition();
                final Code targetCode = targetGridTrs.toCode(targetPos);
                final Code sourceCode = operation.transform(targetCode, null);
                int[] sourcePos = sourceGridTrs.toGrid(sourceCode);
                sourceIterator.moveTo(sourcePos);
                sourceExist = true;
            } catch (TransformException | IllegalArgumentException e) {
                sourceExist = false;
            }
        }

        @Override
        public void rewind() {
            linearPosition = -1;
            sourceMoved = false;
        }

        @Override
        public double getSampleDouble(int band) {
            moveSource();
            if (sourceExist) {
                return sourceIterator.getSampleDouble(band);
            }
            return Double.NaN;
        }

    }

}
