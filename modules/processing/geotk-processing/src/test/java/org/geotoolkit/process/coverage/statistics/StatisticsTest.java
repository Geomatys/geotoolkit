/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2014, Geomatys
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
package org.geotoolkit.process.coverage.statistics;

import org.geotoolkit.metadata.ImageStatistics;
import org.apache.sis.geometry.GeneralEnvelope;
import org.apache.sis.referencing.CommonCRS;
import org.geotoolkit.coverage.grid.GridCoverage2D;
import org.geotoolkit.coverage.grid.GridCoverageBuilder;
import org.geotoolkit.image.internal.SampleType;
import org.geotoolkit.process.ProcessDescriptor;
import org.geotoolkit.process.ProcessException;
import org.geotoolkit.process.ProcessFinder;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.opengis.parameter.ParameterValueGroup;
import org.opengis.util.NoSuchIdentifierException;

/**
 * @author bgarcia (Geomatys)
 * @author Quentin Boileau (Geomatys)
 */
public class StatisticsTest {

    private GeneralEnvelope env = new GeneralEnvelope(CommonCRS.WGS84.normalizedGeographic());
    private GridCoverageBuilder gcb = new GridCoverageBuilder();
    private GridCoverage2D coverage;

    @Before
    public void initTest(){
        env.setRange(0, 0, 3);
        env.setRange(1, 0, 3);

        gcb.setEnvelope(env);
        gcb.setRenderedImage(new float[][]{
                {100,100,100},
                {100,200,100},
                {100,100,100}
        });
        coverage = gcb.getGridCoverage2D();
    }

    @Test
    public void executionTestRepartition() throws NoSuchIdentifierException, ProcessException {
        final ProcessDescriptor desc = ProcessFinder.getProcessDescriptor("coverage", "statistic");
        final ParameterValueGroup procparams = desc.getInputDescriptor().createValue();
        procparams.parameter("inCoverage").setValue(coverage);
        final org.geotoolkit.process.Process process = desc.createProcess(procparams);
        final ParameterValueGroup result = process.call();
        ImageStatistics statistics = (ImageStatistics) result.parameter("outStatistic").getValue();

        ImageStatistics.Band band0 = statistics.getBands()[0];

        //test min / max values
        Assert.assertEquals(100d, band0.getMin(), 0d);
        Assert.assertEquals(200d, band0.getMax(), 0d);

        // test distribution
        Assert.assertTrue(1l == band0.getDistribution().get(200d));
        Assert.assertTrue(8l == band0.getDistribution().get(100d));
    }

    @Test
    public void performanceTest() {
        double max = 100000.0;
        NumericHistogram histogram = new NumericHistogram(1000, 0.0, 100000.0);

        long start = System.currentTimeMillis();
        for (long i = 1; i <= 100000000l; i++) {
            histogram.addValue(Math.random()*max);
        }
        long end = System.currentTimeMillis();
        System.out.println("Histogram computed for 100.000.000 values finished in "+(end-start)+" ms");
    }

    @Test
    public void mergeHistogramTest() {

        long[] expectHisto = new long[] {1, 1, 1, 1, 1, 1, 1, 1, 1, 1};
        long[] expectMerge = new long[] {3, 2, 2, 2, 2, 2, 2, 2, 2, 1};

        NumericHistogram histo1 = new NumericHistogram(10, 0.0, 10.0);
        NumericHistogram histo2 = new NumericHistogram(10, 1.0, 11.0);

        for (long i = 0; i < 10; i++) {
            histo1.addValue((double)i, 1);
            histo2.addValue((double)i+histo2.getMin(), 1);
        }

        Assert.assertArrayEquals(expectHisto, histo1.getHist());
        Assert.assertArrayEquals(expectHisto, histo2.getHist());

        NumericHistogram merged = Statistics.mergeHistograms(histo1, histo2);
        //System.out.println(Arrays.toString(merged.getHist()));
        Assert.assertArrayEquals(expectMerge, merged.getHist());

        // test 2
        expectMerge = new long[] {2, 2, 2, 2, 2, 2, 2, 2, 2, 2};

        histo1 = new NumericHistogram(10, 0.0, 10.0);
        histo2 = new NumericHistogram(10, 0.0, 10.0);

        for (long i = 0; i < 10; i++) {
            histo1.addValue((double)i, 1);
            histo2.addValue((double)i+histo2.getMin(), 1);
        }

        Assert.assertArrayEquals(expectHisto, histo1.getHist());
        Assert.assertArrayEquals(expectHisto, histo2.getHist());

        merged = Statistics.mergeHistograms(histo1, histo2);
        //System.out.println(Arrays.toString(merged.getHist()));
        Assert.assertArrayEquals(expectMerge, merged.getHist());

        // test 3
        expectMerge = new long[] {3, 2, 3, 2, 0, 0, 3, 2, 3, 2};

        histo1 = new NumericHistogram(10, 0.0, 10.0);
        histo2 = new NumericHistogram(10, 15.0, 25.0);

        for (long i = 0; i < 10; i++) {
            histo1.addValue((double)i, 1);
            histo2.addValue((double)i+histo2.getMin(), 1);
        }

        Assert.assertArrayEquals(expectHisto, histo1.getHist());
        Assert.assertArrayEquals(expectHisto, histo2.getHist());

        merged = Statistics.mergeHistograms(histo1, histo2);
        //System.out.println(Arrays.toString(merged.getHist()));
        Assert.assertArrayEquals(expectMerge, merged.getHist());
    }

    @Test
    public void testTightenDistribution() {
        int fullDistribSize = 223;
        int tightenDistribSize = 101;

        long[] histo = new long[fullDistribSize];
        for (int i = 0; i < fullDistribSize; i++) {
            histo[i] = (long)i;
        }

        ImageStatistics stat = new ImageStatistics(1, SampleType.Byte);
        ImageStatistics.Band band0 = stat.getBand(0);
        band0.setHistogram(histo);

        long[] values = band0.tightenHistogram(tightenDistribSize);
        Assert.assertEquals(tightenDistribSize, values.length);

        long resultSum = 0;
        for (int i = 0; i < values.length; i++) {
            resultSum += values[i];
        }

        long expectSum = 0;
        for (int i = 0; i < fullDistribSize; i++) {
            expectSum += i;
        }

        Assert.assertEquals(expectSum, resultSum);
    }

}
