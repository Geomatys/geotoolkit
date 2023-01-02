/*
 *     (C) 2019, Geomatys
 */
package org.geotoolkit.processing.science.drift.v2;

import java.awt.geom.Point2D;
import java.nio.DoubleBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.apache.sis.util.ArgumentChecks;
import org.apache.sis.util.collection.IntegerList;
import org.opengis.referencing.operation.NoninvertibleTransformException;
import org.opengis.referencing.operation.TransformException;

/**
 * /!\ NOT THREAD SAFE !
 *
 * @author Alexis Manin (Geomatys)
 */
class PointBucket {

    // TODO : allow parameterization.
    private static final float COMPACTION_RATIO = .9f;

    final GridModel targetGrid;
    final int nbDims;

    private double[] positions;
    private DoubleBuffer buffer;
    final List<PointReference> references;

    private final IntegerList freeRooms;

    final int maxPts;

    private double[] gridOrdinates;
    private boolean dirtyGrid = true;

    public PointBucket(GridModel targetGrid, int maxPts) throws NoninvertibleTransformException {
        ArgumentChecks.ensureStrictlyPositive("Maximum number of points", maxPts);
        this.targetGrid = targetGrid;
        this.nbDims = targetGrid.dimension;

        final int initialPts = Math.min(maxPts, 256);
        final int initialSize = Math.multiplyExact(initialPts, nbDims);
        final int maxSize = Math.multiplyExact(maxPts, nbDims);
        this.positions = new double[initialSize];
        buffer = DoubleBuffer.wrap(positions);
        buffer.limit(0);
        this.references = new ArrayList<>(initialPts);
        freeRooms = new IntegerList(initialPts, maxSize - nbDims);

        this.maxPts = maxPts;
    }

    public void read(PointReference ref, final Point2D.Double target) {
        target.x = positions[ref.pointIdx];
        target.y = positions[ref.pointIdx+1];
    }

    public void readInGrid(PointReference ref, final Point2D.Double target) {
        assert !dirtyGrid;
        target.x = gridOrdinates[ref.pointIdx];
        target.y = gridOrdinates[ref.pointIdx+1];
    }

    public void refreshGrid() throws TransformException {
        if (dirtyGrid) {
            final int newSize = buffer.limit();
            assert newSize % 2 == 0;
            gridOrdinates = new double[newSize];
            targetGrid.crs2grid.transform(positions, 0, gridOrdinates, 0, newSize/nbDims);
            dirtyGrid = false;
        }
    }

    PointReference add(final double[] point, final double weight) {
        final int limit = buffer.limit();
        final int idx = freeRooms.isEmpty() ? limit : freeRooms.removeLast();
        if (idx > limit) {
            throw new IllegalStateException("An index in free room space is greater than current cursor !");
        }

        final boolean incrementCursor = idx == limit;
        if (incrementCursor) {
            expand();
        }

        try {
            buffer.position(idx);
            buffer.put(point);
            final PointReference ref = new PointReference(idx, weight);
            references.add(ref);
            dirtyGrid = true;
            return ref;
        } catch (RuntimeException | Error e) {
            // In case of error, try to rollback state.
            try {
                if (incrementCursor) {
                    buffer.limit(limit);
                } else {
                    freeRooms.addInt(idx);
                }
            } catch (Exception bis) {
                e.addSuppressed(bis);
            }

            throw e;
        }
    }

    private void expand() {
        dirtyGrid = true;
        final int newLimit = buffer.limit() + nbDims;
        if (newLimit > Math.multiplyExact(maxPts, nbDims))
            throw new IllegalStateException("Cannot expand point bucket, because it would exceed expected maximum: "+maxPts);
        if (buffer.capacity() <= newLimit) {
            final int newLength = Math.multiplyExact(positions.length, 2);

            final int position = buffer.position();
            positions = Arrays.copyOf(positions, newLength);
            buffer = DoubleBuffer.wrap(positions);
            buffer.limit(newLimit);
            /* HACK: cast for jdk8 support */ ((java.nio.Buffer) buffer).position(position);
        } else {
            buffer.limit(newLimit);
        }
    }

    void remove(final PointReference point) {
        // TODO : compact thing.
        if (references.remove(point)) {
            freeRooms.addInt(point.pointIdx);
        }
    }

    void removeLeastProbable(final int maxAllowedPoints) {
        final int nbToRemove = references.size() - maxAllowedPoints;
        if (nbToRemove == 1) {
            remove(Collections.min(references));
        } else if (nbToRemove > 1) {
            final int newPtCount = references.size() - nbToRemove;
            final int newSize = newPtCount * nbDims;
            final double[] tmpPositions = new double[newSize];
            final List<PointReference> tmpRefs = new  ArrayList<>(references);
            Collections.sort(tmpRefs, (r1, r2) -> -r1.compareTo(r2));
            references.clear();
            for (int i = 0 ; i < newPtCount ; i++) {
                final PointReference ref = tmpRefs.get(i);
                final int newIdx = i*nbDims;
                System.arraycopy(positions, ref.pointIdx, tmpPositions, newIdx, nbDims);
                references.add(new PointReference(newIdx, ref.weight));
            }

            System.arraycopy(tmpPositions, 0, positions, 0, newSize);
            /* HACK: cast for jdk8 support */ ((java.nio.Buffer) buffer).position(0);
            buffer.limit(newSize);
            freeRooms.clear();
            dirtyGrid = true;
/* TODO: we should use another approach : retain most probable references, and completely recreate positions to compact storage.
            references.stream()
                    .sorted()
                    .limit(nbToRemove)
                    .forEach(this::remove);
*/
        }
    }

    public class PointReference implements Comparable<PointReference> {

        private double weight;

        private final int pointIdx;

        private PointReference(final int pointIdx, final double weight) {
            this.pointIdx = pointIdx;
            this.weight = weight;
        }

        public double getWeight() {
            return weight;
        }

        public void setWeight(double weight) {
            this.weight = weight;
        }

        public int getPointIdx() {
            return pointIdx;
        }

        public void read(final Point2D.Double target) {
            PointBucket.this.read(this, target);
        }

        public void readInGrid(final Point2D.Double target) {
            PointBucket.this.readInGrid(this, target);
        }

        @Override
        public int compareTo(PointReference o) {
            final int probaCompare = Double.compare(weight, o.weight);
            return probaCompare != 0 ? probaCompare : Integer.compare(pointIdx, o.pointIdx);
        }

        @Override
        public int hashCode() {
            return pointIdx;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            } else if (obj == null) {
                return false;
            } else if (getClass() != obj.getClass()) {
                return false;
            }

            final PointReference other = (PointReference) obj;
            if (Double.doubleToLongBits(this.weight) != Double.doubleToLongBits(other.weight)) {
                return false;
            }

            return this.pointIdx == other.pointIdx;
        }

        @Override
        public String toString() {
            return "PointReference{" + "weight=" + weight + ", pointIdx=" + pointIdx + '}';
        }
    }
}
