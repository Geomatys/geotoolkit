/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.geotoolkit.test.storage;

import java.util.Arrays;
import java.util.Random;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.image.RenderedImage;
import org.apache.sis.coverage.grid.GridCoverage;
import org.apache.sis.coverage.grid.GridDerivation;
import org.apache.sis.coverage.grid.GridGeometry;
import org.apache.sis.coverage.grid.GridExtent;
import org.apache.sis.storage.DataStoreException;
import org.apache.sis.storage.GridCoverageResource;
import org.apache.sis.storage.RasterLoadingStrategy;
import org.apache.sis.util.privy.Numerics;
import org.apache.sis.image.PixelIterator;
import org.apache.sis.util.ArraysExt;
import org.junit.Test;

import static org.junit.Assert.*;


/**
 * Copy of a test class from Apache SIS.
 */
public class CoverageReadConsistency {
    private static final int BIDIMENSIONAL = 2;
    private final GridCoverageResource resource;
    private final GridCoverage full;
    private boolean allowOffsets;
    private boolean allowSubsampling;
    private boolean allowBandSubset;
    private final Random random;
    private final int numIterations;
    private final boolean failOnMismatch;

    public CoverageReadConsistency(final GridCoverageResource tested) throws DataStoreException {
        resource       = tested;
        full           = tested.read(null, null);
        random         = new Random();
        numIterations  = 100;
        failOnMismatch = true;
    }

    public CoverageReadConsistency(final GridCoverageResource tested, final GridCoverage reference,
                                   final long seed, final int readCount)
    {
        resource       = tested;
        full           = reference;
        random         = new Random();
        numIterations  = readCount;
        failOnMismatch = false;
    }

    @Test
    public void testSubRegionAtOrigin() throws DataStoreException {
        readAndCompareRandomRegions("Subregions at (0,0)");
    }

    @Test
    public void testSubRegionsAnywhere() throws DataStoreException {
        allowOffsets = true;
        readAndCompareRandomRegions("Subregions");
    }

    @Test
    public void testSubsamplingAtOrigin() throws DataStoreException {
        allowSubsampling = true;
        readAndCompareRandomRegions("Subsampling at (0,0)");
    }

    @Test
    public void testSubsamplingAnywhere() throws DataStoreException {
        allowOffsets     = true;
        allowSubsampling = true;
        readAndCompareRandomRegions("Subsampling");
    }

    @Test
    public void testBandSubsetAtOrigin() throws DataStoreException {
        allowBandSubset = true;
        readAndCompareRandomRegions("Bands at (0,0)");
    }

    @Test
    public void testBandSubsetAnywhere() throws DataStoreException {
        allowOffsets    = true;
        allowBandSubset = true;
        readAndCompareRandomRegions("Bands");
    }

    @Test
    public void testAllAnywhere() throws DataStoreException {
        allowOffsets     = true;
        allowBandSubset  = true;
        allowSubsampling = true;
        readAndCompareRandomRegions("All");
    }

    private void randomConfigureResource() throws DataStoreException {
        final RasterLoadingStrategy[] choices = RasterLoadingStrategy.values();
        resource.setLoadingStrategy(choices[random.nextInt(choices.length)]);
    }

    private GridGeometry randomDomain(final GridGeometry gg, final long[] low, final long[] high, final int[] subsampling) {
        final GridExtent fullExtent = gg.getExtent();
        final int dimension = fullExtent.getDimension();
        for (int d=0; d<dimension; d++) {
            final int span = StrictMath.toIntExact(fullExtent.getSize(d));
            final int rs = random.nextInt(span);                            // Span of the sub-region - 1.
            if (allowOffsets) {
                low[d] = random.nextInt(span - rs);                         // Note: (span - rs) > 0.
            }
            high[d] = low[d] + rs;
            subsampling[d] = 1;
            if (allowSubsampling) {
                subsampling[d] += random.nextInt(StrictMath.max(rs / 16, 1));
            }
        }
        return gg.derive().subgrid(new GridExtent(null, low, high, true), subsampling).build();
    }

    private int[] randomRange(final int numBands) {
        if (!allowBandSubset) {
            return ArraysExt.range(0, numBands);
        }
        final int[] selectedBands = new int[numBands];
        for (int i=0; i<numBands; i++) {
            selectedBands[i] = random.nextInt(numBands);
        }
        // Remove duplicated elements.
        long included = 0;
        int count = 0;
        for (final int b : selectedBands) {
            if (included != (included |= Numerics.bitmask(b))) {
                selectedBands[count++] = b;
            }
        }
        return ArraysExt.resize(selectedBands, count);
    }

    private void readAndCompareRandomRegions(final String label) throws DataStoreException {
        randomConfigureResource();
        final GridGeometry gg = resource.getGridGeometry();
        final int    dimension   = gg.getDimension();
        final long[] low         = new long[dimension];
        final long[] high        = new long[dimension];
        final int [] subsampling = new int [dimension];
        final int [] subOffsets  = new int [dimension];
        final int    numBands    = resource.getSampleDimensions().size();
        int failuresCount = 0;
        for (int it=0; it < numIterations; it++) {
            final GridGeometry domain = randomDomain(gg, low, high, subsampling);
            final int[] selectedBands = randomRange(numBands);
            /*
             * Read a coverage containing the requested sub-domain. Note that the reader is free to read
             * more data than requested. The extent actually read is `actualReadExtent`. It shall contain
             * fully the requested `domain`.
             */
            final long startTime = System.nanoTime();
            final GridCoverage subset = resource.read(domain, selectedBands);
            final GridExtent actualReadExtent = subset.getGridGeometry().getExtent();
            if (failOnMismatch) {
                assertEquals("Unexpected number of dimensions.", dimension, actualReadExtent.getDimension());
                for (int d=0; d<dimension; d++) {
                    if (subsampling[d] == 1) {
                        assertTrue("Actual extent is too small.", actualReadExtent.getSize(d) > high[d] - low[d]);
                        assertTrue("Actual extent is too small.", actualReadExtent.getLow (d) <= low [d]);
                        assertTrue("Actual extent is too small.", actualReadExtent.getHigh(d) >= high[d]);
                    }
                }
            }
            if (allowSubsampling && full != null) {
                final GridDerivation change = full.getGridGeometry().derive().subgrid(subset.getGridGeometry());
                cast(change.getSubsampling(),        subsampling, dimension);
                cast(change.getSubsamplingOffsets(), subOffsets,  dimension);
            }
            final long[] sliceMin = actualReadExtent.getLow() .getCoordinateValues();
            final long[] sliceMax = actualReadExtent.getHigh().getCoordinateValues();
nextSlice:  for (;;) {
                System.arraycopy(sliceMin, BIDIMENSIONAL, sliceMax, BIDIMENSIONAL, dimension - BIDIMENSIONAL);
                final PixelIterator itr = iterator(full,   sliceMin, sliceMax, subsampling, subOffsets, allowSubsampling);
                final PixelIterator itc = iterator(subset, sliceMin, sliceMax, subsampling, subOffsets, false);
                if (itr != null) {
                    assertEquals(itr.getDomain().getSize(), itc.getDomain().getSize());
                    final double[] expected = new double[selectedBands.length];
                    double[] reference = null, actual = null;
                    while (itr.next()) {
                        assertTrue(itc.next());
                        reference = itr.getPixel(reference);
                        actual    = itc.getPixel(actual);
                        for (int i=0; i<selectedBands.length; i++) {
                            expected[i] = reference[selectedBands[i]];
                        }
                        if (!Arrays.equals(expected, actual)) {
                            failuresCount++;
                            if (!failOnMismatch) break;
                            final Point pr = itr.getPosition();
                            final Point pc = itc.getPosition();
                            final StringBuilder message = new StringBuilder(100).append("Mismatch at position (")
                                    .append(pr.x).append(", ").append(pr.y).append(") in full image and (")
                                    .append(pc.x).append(", ").append(pc.y).append(") in tested sub-image");
                            findMatchPosition(itr, pr, selectedBands, actual, message);
                            assertArrayEquals(message.toString(), expected, actual, 0);
                        }
                    }
                    assertFalse(itc.next());
                } else {
                    double[] actual = null;
                    while (itc.next()) {
                        actual = itc.getPixel(actual);
                    }
                }
                for (int d=dimension; --d >= BIDIMENSIONAL;) {
                    if (sliceMin[d]++ <= actualReadExtent.getHigh(d)) continue nextSlice;
                    sliceMin[d] = actualReadExtent.getLow(d);
                }
                break;
            }
        }
    }

    private static void cast(long[] source, int[] dest, int n) {
        for (int i=0; i<n; i++) {
            dest[i] = Math.toIntExact(source[i]);
        }
    }

    private static PixelIterator iterator(final GridCoverage coverage, long[] sliceMin, long[] sliceMax,
            final int[] subsampling, final int[] subOffsets, final boolean allowSubsampling)
    {
        if (coverage == null) {
            return null;
        }
        final Rectangle sliceAOI = new Rectangle(StrictMath.toIntExact(sliceMax[0] - sliceMin[0] + 1),
                                                 StrictMath.toIntExact(sliceMax[1] - sliceMin[1] + 1));
        if (allowSubsampling) {
            sliceMin = sliceMin.clone();
            sliceMax = sliceMax.clone();
            for (int i=0; i<sliceMin.length; i++) {
                sliceMin[i] = sliceMin[i] * subsampling[i] + subOffsets[i];
                sliceMax[i] = sliceMax[i] * subsampling[i] + subOffsets[i];
            }
        }
        RenderedImage image = coverage.render(new GridExtent(null, sliceMin, sliceMax, true));
        if (allowSubsampling) {
            final int subX = subsampling[0];
            final int subY = subsampling[1];
            if (subX > image.getTileWidth() || subY > image.getTileHeight()) {
                return null;        // `SubsampledImage` does not support this case.
            }
            int offX = StrictMath.floorMod(image.getMinX(), subX);
            int offY = StrictMath.floorMod(image.getMinY(), subY);
            if (offX != 0) {sliceAOI.x--; offX = subX - offX;}
            if (offY != 0) {sliceAOI.y--; offY = subY - offY;}
            image = SubsampledImage.create(image, subX, subY, offX, offY);
            if (image == null) {
                return null;
            }
        }
        return new PixelIterator.Builder().setRegionOfInterest(sliceAOI).create(image);
    }

    private static void findMatchPosition(final PixelIterator ir, final Point pr, final int[] selectedBands,
                                          final double[] actual, final StringBuilder message)
    {
        final double[] expected = new double[actual.length];
        double[] reference = null;
        for (int dy=0; dy<10; dy++) {
            for (int dx=0; dx<10; dx++) {
                if ((dx | dy) != 0) {
                    for (int c=0; c<4; c++) {
                        final int x = (c & 1) == 0 ? -dx : dx;
                        final int y = (c & 2) == 0 ? -dy : dy;
                        try {
                            ir.moveTo(pr.x + x, pr.y + y);
                        } catch (IndexOutOfBoundsException e) {
                            continue;
                        }
                        reference = ir.getPixel(reference);
                        for (int i=0; i<selectedBands.length; i++) {
                            expected[i] = reference[selectedBands[i]];
                        }
                        if (Arrays.equals(expected, actual)) {
                            message.append(" (note: found a match at offset (").append(x).append(", ").append(y)
                                   .append(") in full image)");
                            return;
                        }
                    }
                }
            }
        }
    }
}
