/*
 * Copyright 2016-2021, 2024 Uber Technologies, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.geotoolkit.dggs.h3.internal.shared;

import java.util.function.LongSupplier;
import java.util.stream.LongStream;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public final class Iterators {

    private Iterators(){}

    /**
     * Initialize a IterCellsChildren struct representing the sequence giving
     * the children of cell `h` at resolution `childRes`.
     *
     * At any point in the iteration, starting once
     * the struct is initialized, IterCellsChildren.h gives the current child.
     *
     * Also, IterCellsChildren.h == H3_NULL when all the children have been iterated
     * through, or if the input to `iterInitParent` was invalid.
     */
    public static LongStream iterInitParent(long h, int childRes) {
        final int parentRes = H3Index.getResolution(h);

        if (childRes == parentRes+1) {
            //direct children, provide and optimized case
            long idx = H3Index.setResolution(h, childRes);
            if (H3Index.isPentagon(h)) {
                return LongStream.of(
                    H3Index.setIndexDigit(idx, childRes, 0),
                    //H3Index.setIndexDigit(idx, childRes, 1), //skipped in the pentagon case
                    H3Index.setIndexDigit(idx, childRes, 2),
                    H3Index.setIndexDigit(idx, childRes, 3),
                    H3Index.setIndexDigit(idx, childRes, 4),
                    H3Index.setIndexDigit(idx, childRes, 5),
                    H3Index.setIndexDigit(idx, childRes, 6)
                );
            } else {
                return LongStream.of(
                    H3Index.setIndexDigit(idx, childRes, 0),
                    H3Index.setIndexDigit(idx, childRes, 1),
                    H3Index.setIndexDigit(idx, childRes, 2),
                    H3Index.setIndexDigit(idx, childRes, 3),
                    H3Index.setIndexDigit(idx, childRes, 4),
                    H3Index.setIndexDigit(idx, childRes, 5),
                    H3Index.setIndexDigit(idx, childRes, 6)
                );
            }
        }

        final IterCellsChildren iter = new IterCellsChildren();
        iter._parentRes = H3Index.getResolution(h);

        if (childRes < iter._parentRes || childRes > Constants.MAX_H3_RES) {
            throw new IllegalArgumentException();
        }

        iter.h = H3Index.zeroIndexDigits(h, iter._parentRes + 1, childRes);
        iter.h = H3Index.setResolution(iter.h, childRes);

        if (H3Index.isPentagon(iter.h)) {
            // The skip digit skips `1` for pentagons.
            // The "_skipDigit" moves to the left as we count up from the
            // child resolution to the parent resolution.
            iter._skipDigit = childRes;
        } else {
            // if not a pentagon, we can ignore "skip digit" logic
            iter._skipDigit = -1;
        }

        final long size = H3Index.cellToChildrenSize(h, childRes);
        return LongStream.generate(iter).limit(size);
    }


    private static class IterCellsChildren implements LongSupplier {
        private int _parentRes;
        private int _skipDigit;
        private boolean firstValue = true;
        private long h;

        /**
         * Step a IterCellsChildren to the next child cell.
         * When the iteration is over, IterCellsChildren.h will be H3_NULL.
         * Handles iterating through hexagon and pentagon cells.
         */
        @Override
        public long getAsLong() {
            if (firstValue) {
                firstValue = false;
                return h;
            }

            final int childRes = H3Index.getResolution(h);
            h = _incrementResDigit(h, childRes);

            for (int i = childRes; i >= _parentRes; i--) {
                if (i == _parentRes) {
                    // if we're modifying the parent resolution digit, then we're done
                    throw new IllegalArgumentException("Reached iterator end");
                }

                // PENTAGON_SKIPPED_DIGIT == 1
                if (i == _skipDigit && H3Index.getIndexDigit(h, i) == H3Index.PENTAGON_SKIPPED_DIGIT) {
                    // Then we are iterating through the children of a pentagon cell.
                    // All children of a pentagon have the property that the first
                    // nonzero digit between the parent and child resolutions is
                    // not 1.
                    // I.e., we never see a sequence like 00001.
                    // Thus, we skip the `1` in this digit.
                    h = _incrementResDigit(h, i);
                    _skipDigit -= 1;
                    break;
                }

                // INVALID_DIGIT == 7
                if (H3Index.getIndexDigit(h, i) == H3Index.INVALID_DIGIT) {
                    h = _incrementResDigit(h, i);  // zeros out it[i] and increments it[i-1] by 1
                } else {
                    break;
                }
            }

            return h;
        }

        // increment the digit (0--7) at location `res`
        // H3_PER_DIGIT_OFFSET == 3
        private static long _incrementResDigit(long h, int res) {
            long val = 1;
            val <<= H3Index.H3_PER_DIGIT_OFFSET * (Constants.MAX_H3_RES - res);
            return h + val;
        }
    }

}
