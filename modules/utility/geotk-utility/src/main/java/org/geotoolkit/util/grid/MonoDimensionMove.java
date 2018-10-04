/*
 * Geotoolkit.org - An Open Source Java GIS Toolkit
 * http://www.geotoolkit.org
 *
 * (C) 2018, Geomatys.
 *
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation;
 * version 2.1 of the License.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 */
package org.geotoolkit.util.grid;

import java.util.Arrays;
import java.util.Spliterator;
import java.util.function.Consumer;
import java.util.function.DoubleUnaryOperator;

/**
 *
 * @author Alexis Manin (Geomatys)
 */
class MonoDimensionMove implements Spliterator<double[]> {

        private final int dimension;

        /**
         * Point to consider as origin for the next move.
         */
        private double[] startPoint;
        private final DoubleUnaryOperator moveDir;
        private double distanceLeft;
        boolean hasNext = true;

        public MonoDimensionMove(int dimension, double[] startPoint, final double distanceLeft) {
            this.dimension = dimension;
            this.startPoint = Arrays.copyOf(startPoint, startPoint.length);
            this.distanceLeft = distanceLeft;

            moveDir = (distanceLeft < 0)? GridTraversal::floorOrDecrement : GridTraversal::ceilOrIncrement;
        }

        @Override
        public boolean tryAdvance(final Consumer<? super double[]> consumer) {
            if (!hasNext || GridTraversal.isNearZero(distanceLeft)) return false;

            final double start = startPoint[dimension];
            double nextPos = moveDir.applyAsDouble(start);
            final double dist = nextPos - start;
            if (dist > distanceLeft) {
                nextPos = start + distanceLeft;
                hasNext = false;
            }

            distanceLeft -= dist;

            final double[] nextPoint = new double[startPoint.length];
            System.arraycopy(startPoint, 0, nextPoint, 0, startPoint.length);
            nextPoint[dimension] = nextPos;

            consumer.accept(nextPoint);
            startPoint = nextPoint;
            return true;
        }

        @Override
        public Spliterator<double[]> trySplit() {
            if (distanceLeft < 3.0) {
                return null;
            } else {
                // For consistency, we must cut on an integer value. Otherwise, we would introduce a point in an
                // arbitrary position in the grid.
                double cutDistance = distanceLeft/2.0;
                double cutNearestInt = Math.floor(startPoint[dimension] + cutDistance);
                cutDistance = distanceLeft - (cutNearestInt - startPoint[dimension]);
                distanceLeft -= cutDistance;

                final double[] splitStart = Arrays.copyOf(startPoint, startPoint.length);
                splitStart[dimension] = cutNearestInt;
                final MonoDimensionMove prefix = new MonoDimensionMove(dimension, startPoint, distanceLeft);
                startPoint = splitStart;
                distanceLeft = cutDistance;
                return prefix;
            }
        }

        @Override
        public long estimateSize() {
            /* TODO : Not exact: we could have one more element left initially.
             * Example : if distanceleft=2.3, and our current position is 1.9, we must browse values 2.0, 3.0, 4.0 and
             * 4.2. But, with below computation, we will return 3 moves left instead of 4.
             */
            return (long) Math.ceil(Math.abs(distanceLeft));
        }

        @Override
        public int characteristics() {
            return ORDERED | DISTINCT | NONNULL;
        }
    }
