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

import org.apache.sis.geometry.GeneralEnvelope;
import org.apache.sis.referencing.CommonCRS;
import org.geotoolkit.coverage.grid.GridCoverage2D;
import org.geotoolkit.coverage.grid.GridCoverageBuilder;
import org.geotoolkit.process.ProcessDescriptor;
import org.geotoolkit.process.ProcessException;
import org.geotoolkit.process.ProcessFinder;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.opengis.parameter.ParameterValueGroup;
import org.opengis.util.NoSuchIdentifierException;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

/**
 * @author bgarcia
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

        Assert.assertTrue(1l == statistics.getBands()[0].getFullDistribution().get(200d));
        Assert.assertTrue(8l == statistics.getBands()[0].getFullDistribution().get(100d));
    }

    @Test
    public void executionTestMin() throws NoSuchIdentifierException, ProcessException {
        final ProcessDescriptor desc = ProcessFinder.getProcessDescriptor("coverage", "statistic");
        final ParameterValueGroup procparams = desc.getInputDescriptor().createValue();
        procparams.parameter("inCoverage").setValue(coverage);
        final org.geotoolkit.process.Process process = desc.createProcess(procparams);
        final ParameterValueGroup result = process.call();
        ImageStatistics statistics = (ImageStatistics) result.parameter("outStatistic").getValue();

        Assert.assertEquals(100d, statistics.getBands()[0].getMin(), 0d);
    }

    @Test
    public void executionTestMax() throws NoSuchIdentifierException, ProcessException {
        final ProcessDescriptor desc = ProcessFinder.getProcessDescriptor("coverage", "statistic");
        final ParameterValueGroup procparams = desc.getInputDescriptor().createValue();
        procparams.parameter("inCoverage").setValue(coverage);
        final org.geotoolkit.process.Process process = desc.createProcess(procparams);
        final ParameterValueGroup result = process.call();
        ImageStatistics statistics = (ImageStatistics) result.parameter("outStatistic").getValue();

        Assert.assertEquals(200d, statistics.getBands()[0].getMax(), 0d);
    }
    
    @Test
    public void testTightenDistribution() {
        int fullDistribSize = 223;
        int tightenDistribSize = 101;

        Map<Double, Long> distribution = new HashMap<>();
        for (int i = 0; i < fullDistribSize; i++) {
            distribution.put((double)i, (long)i);
        }

        ImageStatistics stat = new ImageStatistics(1);
        ImageStatistics.Band band0 = stat.getBand(0);
        band0.setFullDistribution(distribution);

        Long[] values = band0.tightenDistribution(tightenDistribSize);
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
