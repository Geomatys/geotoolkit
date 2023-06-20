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

import java.awt.image.Raster;
import java.awt.image.RenderedImage;
import java.awt.image.SampleModel;
import java.util.Arrays;
import java.util.function.DoublePredicate;
import java.util.stream.DoubleStream;
import javax.annotation.Nullable;
import org.apache.sis.coverage.SampleDimension;
import org.apache.sis.coverage.grid.GridCoverage;
import org.apache.sis.image.PixelIterator;
import org.apache.sis.parameter.Parameters;
import org.apache.sis.storage.DataStoreException;
import org.apache.sis.storage.GridCoverageResource;
import org.geotoolkit.coverage.SampleDimensionUtils;
import org.geotoolkit.image.internal.SampleType;
import org.geotoolkit.process.ProcessException;
import org.geotoolkit.processing.AbstractProcess;

import static org.geotoolkit.processing.coverage.statistics.NumericHistogram.AddResult.SUCCESS;
import static org.geotoolkit.processing.coverage.statistics.StatisticsDescriptor.*;
import org.geotoolkit.storage.coverage.ImageStatistics;
import org.opengis.parameter.ParameterValueGroup;

/**
 * Process to compute {@link ImageStatistics image statistics}.
 * from a {@link GridCoverage} or {@link GridCoverageResource}.
 *
 * Can be directly use using analyse() static methods: <br/>
 * Eg. : <br/>
 * <code>GridCoverage2D myCoverage = ...;</code><br/>
 * <code>ImageStatistics stats = Statistics.analyse(myCoverage, true);</code><br/>
 * <code>Long[] distribution = stats.getBand(0).tightenHistogram(50);</code><br/>s
 *
 * @author Benjamin Garcia (Geomatys)
 * @author Quentin Boileau (Geomatys)
 * @author Johann Sorel (Geomatys)
 */
public class Statistics extends AbstractProcess {

    public Statistics(final RenderedImage image, boolean excludeNoData){
        this(toParameters(image, null, null, excludeNoData));
    }

    public Statistics(final GridCoverage coverage, boolean excludeNoData){
        this(toParameters(null, coverage, null, excludeNoData));
    }

    public Statistics(final GridCoverageResource ref, boolean excludeNoData){
        this(toParameters(null, null, ref, excludeNoData));
    }

    public Statistics(final ParameterValueGroup input) {
        super(StatisticsDescriptor.INSTANCE, input);
    }

    private static ParameterValueGroup toParameters(final RenderedImage image, final GridCoverage coverage, final GridCoverageResource ref,
                                                    boolean excludeNoData) {
        final Parameters params = Parameters.castOrWrap(StatisticsDescriptor.INSTANCE.getInputDescriptor().createValue());
        params.getOrCreate(IMAGE).setValue(image);
        params.getOrCreate(REF).setValue(ref);
        params.getOrCreate(COVERAGE).setValue(coverage);
        params.getOrCreate(EXCLUDE_NO_DATA).setValue(excludeNoData);
        return params;
    }

    /**
     * Run Statistics process with a RenderedImage and return ImageStatistics
     * @param image RenderedImage to analyse
     * @param excludeNoData exclude no-data flag (NaN values)
     * @return ImageStatistics
     * @throws ProcessException
     */
    public static ImageStatistics analyse(RenderedImage image, boolean excludeNoData) throws ProcessException {
        org.geotoolkit.process.Process process = new Statistics(image, excludeNoData);
        Parameters out = Parameters.castOrWrap(process.call());
        return out.getValue(OUTCOVERAGE);
    }

    @Override
    protected void execute() throws ProcessException {

        final RenderedImage inImage = inputParameters.getValue(IMAGE);
        final boolean excludeNoData = inputParameters.getValue(EXCLUDE_NO_DATA);

        fireProgressing("Pre-analysing", 0f, false);
        final RenderedImage image;
        final ImageStatistics sc;
        if (inImage != null) {
            image = inImage;

            final SampleModel sm = image.getSampleModel();
            final SampleType sampleType = SampleType.valueOf(sm.getDataType());
            final int nbBands = sm.getNumBands();
            //create empty statistic object
            sc = new ImageStatistics(nbBands, sampleType);

        } else {

            final GridCoverage inCoverage = inputParameters.getValue(COVERAGE);
            GridCoverage candidate = null;
            if (inCoverage != null) {
                candidate = inCoverage;
            } else {
                final GridCoverageResource ref = inputParameters.getValue(REF);
                if (ref != null) {
                    candidate = getCoverage(ref);
                }
            }

            //we want the statistics on the real data values
            candidate = candidate.forConvertedValues(true);

            if (candidate == null) {
                throw new ProcessException("Null Coverage.", this, null);
            }

            //TODO extract view as process input parameter.
            image = candidate.render(null);

            final SampleModel sm = image.getSampleModel();
            final SampleType sampleType = SampleType.valueOf(sm.getDataType());
            final int nbBands = sm.getNumBands();
            sc = new ImageStatistics(nbBands, sampleType);

            final SampleDimension[] sampleDimensions = candidate.getSampleDimensions().toArray(new SampleDimension[0]);
            //add no data values and name on bands
            for (int i = 0; i < sampleDimensions.length; i++) {
                sc.getBand(i).setNoData(SampleDimensionUtils.getNoDataValues(sampleDimensions[i]));
                sc.getBand(i).setName(sampleDimensions[i].getName().toString());
            }
        }

        final DoublePredicate[] validityTests =initValueValidityTests(sc, excludeNoData);

        outputParameters.getOrCreate(OUTCOVERAGE).setValue(sc);
        fireProgressing("Pre-analysing finished", 10f, true);
        fireProgressing("Start range/histogram computing", 10f, true);

        final ImageStatistics.Band[] bands = sc.getBands();
        final org.apache.sis.math.Statistics[] stats = new org.apache.sis.math.Statistics[bands.length];
        for(int i=0;i<bands.length;i++) stats[i] = new org.apache.sis.math.Statistics("stats");
        int nbBands = bands.length;

        //optimization for GridMosaicRenderedImage impl
        NumericHistogram[] histo = new NumericHistogram[nbBands];

        final int startX = image.getMinTileX();
        final int startY = image.getMinTileY();
        final int endX = startX + image.getNumXTiles();
        final int endY = startY + image.getNumYTiles();

        long totalTiles = image.getNumXTiles() * image.getNumYTiles();

        //analyse each tiles
        //todo make this parallel
        Raster tile;
        PixelIterator pix;
        int step = 1;
        for (long y = startY; y < endY; y++) {
            for (long x = startX; x < endX; x++) {
                tile = image.getTile(Math.toIntExact(x), Math.toIntExact(y));
                pix = new PixelIterator.Builder().create(tile);

                analyseRange(pix, stats, bands, excludeNoData);
                pix.rewind();

                mergeHistograms(histo, analyseHistogram(pix, bands, stats, validityTests));

                updateBands(bands, histo);
                fireProgressing("Histogram progressing", (step/totalTiles)*0.9f, true);
                step++;
            }
        }

        //copy statistics in band container
        for(int i=0;i<bands.length;i++){
            bands[i].setMin(stats[i].minimum());
            bands[i].setMax(stats[i].maximum());
            bands[i].setMean(stats[i].mean());
            bands[i].setStd(stats[i].standardDeviation(true));
        }

    }

    private void updateBands(ImageStatistics.Band[] bands, NumericHistogram[] histo) {
        for (int i = 0; i < bands.length; i++) {
            bands[i].setHistogram(histo[i].getHist());
        }
    }

    private void mergeHistograms(NumericHistogram[] histo, NumericHistogram[] tileHisto) {

        for (int i = 0; i < histo.length; i++) {
            NumericHistogram histo1 = histo[i];
            NumericHistogram histo2 = tileHisto[i];
            histo[i] = mergeHistograms(histo1, histo2);
        }
    }

    static NumericHistogram mergeHistograms(NumericHistogram histo1, NumericHistogram histo2) {

        if (histo1 == null) {
            return histo2;
        }

        int nbBins = histo1.getNbBins();
        double min = Math.min(histo1.getMin(), histo2.getMin());
        double max = Math.max(histo1.getMax(), histo2.getMax());
        NumericHistogram resultHisto = new NumericHistogram(nbBins, min, max);

        //add first histogram values
        long[] hist1 = histo1.getHist();
        double histo1BinSize = (histo1.getMax() - histo1.getMin()) / (double)nbBins;
        for (int j = 0; j <nbBins; j++) {
            double value = histo1.getMin()+histo1BinSize*j;
            long occurs = hist1[j];
            resultHisto.addValue(value, occurs);
        }

        //add second histogram values
        long[] hist2 = histo2.getHist();
        int nbBins2 = histo2.getNbBins();
        double histo2BinSize = (histo2.getMax() - histo2.getMin()) / (double)nbBins2;
        for (int j = 0; j <nbBins; j++) {
            double value = histo2.getMin()+histo2BinSize*j;
            long occurs = hist2[j];
            resultHisto.addValue(value, occurs);
        }

        return resultHisto;
    }

    private void analyseRange(final PixelIterator pix, final org.apache.sis.math.Statistics[] stats,
                              final ImageStatistics.Band[] bands, final boolean excludeNoData) {
        //first pass to compute min/max values
        double [][] noDatas = null;
        if (excludeNoData) {
            noDatas = new double[bands.length][];
            for (int i = 0; i < bands.length; i++) {
                noDatas[i] = bands[i].getNoData();
            }
        }

        while (pix.next()) {
            for (int b = 0; b < stats.length; b++) {
                final double d = pix.getSampleDouble(b);
                if (Double.isNaN(d) || Double.isInfinite(d)) {
                    continue;
                }

                //remove noData from stats
                if (noDatas != null && noDatas[b] != null && Arrays.binarySearch(noDatas[b], d) >= 0) {
                    continue;
                }

                stats[b].accept(d);
            }
        }
    }

    /**
     * Analyse each pixels using a PixelIterator
     * @param pix PixelIterator
     * @param bands
     * @param validityTests Predicates to filter values extracted from pixel iterator. The predicates must accept
     *                      (return true) values that are valid, and reject (return false) for no-data / not finite
     *                      values.
     */
    private NumericHistogram[] analyseHistogram(final PixelIterator pix, final ImageStatistics.Band[] bands,
                                                org.apache.sis.math.Statistics[] stats, final DoublePredicate[] validityTests) {

        int nbBands = bands.length;
        final NumericHistogram[] histograms = new NumericHistogram[nbBands];
        for (int i = 0; i < nbBands; i++) {
            int nbBins = getNbBins(bands[i].getDataType());
            histograms[i] = new NumericHistogram(nbBins, stats[i].minimum(), stats[i].maximum());
        }

        //reset iterator
        pix.rewind();

        //second pass to compute histogram
        // this int permit to loop on images band.
        while (pix.next()) {
            for (int b = 0; b < nbBands; b++) {
                final double value = pix.getSampleDouble(b);
                if (validityTests[b].test(value)) {
                    NumericHistogram.AddResult state = histograms[b].addValue(value);
                    /* As non-finite values should have already been filtered, and min/max values are well identified,
                     * the result of above operation should always be a success. However, in case of error, we will be
                     * notified quickly, and avoid producing incorrect results.
                     */
                    if (!SUCCESS.equals(state)) throw new IllegalStateException("A value cannot be added to histogram. Reason: "+state);
                }
            }
        }

        return histograms;
    }

    private int getNbBins(SampleType dataType) {
        if (dataType != null && dataType.equals(SampleType.BYTE)) {
            return 255;
        }
        return 1000;
    }

    private static DoublePredicate[] initValueValidityTests(final ImageStatistics incompleteStats, final boolean excludeNoData) {
        final DoublePredicate[] validityTests;
        if (excludeNoData) {
            validityTests = Arrays.stream(incompleteStats.getBands())
                    .map(ImageStatistics.Band::getNoData)
                    .map(Statistics::initValueValidityTest)
                    .toArray(size -> new DoublePredicate[size]);
        } else {
            // Only check given value is a finite one. No-data categories will be counted as long as they relate to
            // finite values.
            validityTests = new DoublePredicate[incompleteStats.getBands().length];
            Arrays.fill(validityTests, (DoublePredicate) Double::isFinite);
        }
        return validityTests;
    }

    private static DoublePredicate initValueValidityTest(@Nullable double[] nonAllowedValues) {
        if (nonAllowedValues == null || nonAllowedValues.length < 1 || Arrays.stream(nonAllowedValues).allMatch(Double::isNaN)) {
            return Double::isFinite;
        }
        final double[] defCopyWithoutNaN = DoubleStream.of(nonAllowedValues)
                .filter(Double::isFinite)
                .sorted()
                .toArray();
        return value -> Double.isFinite(value) && Arrays.binarySearch(defCopyWithoutNaN, value) < 0;
    }

    /**
     * Read coverage from CoverageResource
     * @param ref
     * @return
     * @throws ProcessException
     */
    private GridCoverage getCoverage(GridCoverageResource ref) throws ProcessException {
        try {
            return ref.read(null, null);
        } catch (DataStoreException e) {
            throw new ProcessException(e.getMessage(), this, e);
        }
    }

}
