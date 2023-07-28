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
package org.geotoolkit.processing.coverage.statistics;

import org.apache.sis.coverage.SampleDimension;
import org.apache.sis.coverage.grid.GridCoverage;
import org.apache.sis.coverage.grid.GridCoverageBuilder;
import org.apache.sis.coverage.grid.GridExtent;
import org.apache.sis.coverage.grid.GridGeometry;
import org.apache.sis.geometry.GeneralEnvelope;
import org.apache.sis.referencing.CommonCRS;
import org.geotoolkit.image.BufferedImages;
import org.geotoolkit.image.internal.SampleType;
import org.geotoolkit.process.ProcessDescriptor;
import org.geotoolkit.process.ProcessException;
import org.geotoolkit.process.ProcessFinder;
import org.geotoolkit.processing.GeotkProcessingRegistry;
import org.geotoolkit.storage.coverage.ImageStatistics;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.opengis.parameter.ParameterValueGroup;
import org.opengis.util.NoSuchIdentifierException;

import static java.lang.Float.NaN;
import org.apache.sis.coverage.grid.GridOrientation;

/**
 * @author bgarcia (Geomatys)
 * @author Quentin Boileau (Geomatys)
 */
public class StatisticsTest {

    private GeneralEnvelope env = new GeneralEnvelope(CommonCRS.WGS84.normalizedGeographic());
    private GridCoverage coverage;

    @Before
    public void initTest(){
        env.setRange(0, 0, 3);
        env.setRange(1, 0, 4);

        GridCoverageBuilder gcb = new GridCoverageBuilder();
        gcb.setDomain(new GridGeometry(new GridExtent(3, 4), env, GridOrientation.HOMOTHETY));
        gcb.setValues(BufferedImages.toDataBuffer1D(new float[][]{
                {NaN,100,100},
                {100,NaN,100},
                {100,100,NaN},
                {100,200,100}
        }), null);
        gcb.setRanges(new SampleDimension.Builder().setName(0).build());
        coverage = gcb.build();
    }

    @Test
    public void executionTestRepartition() throws NoSuchIdentifierException, ProcessException {
        final ProcessDescriptor desc = ProcessFinder.getProcessDescriptor(GeotkProcessingRegistry.NAME, StatisticsDescriptor.NAME);
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

        ImageStatistics stat = new ImageStatistics(1, SampleType.BYTE);
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
