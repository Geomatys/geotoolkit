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

import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.image.Raster;
import java.awt.image.RenderedImage;
import java.awt.image.SampleModel;
import java.util.Arrays;
import org.apache.sis.coverage.SampleDimension;
import org.apache.sis.coverage.grid.GridCoverage;
import org.apache.sis.coverage.grid.GridExtent;
import org.apache.sis.coverage.grid.GridGeometry;
import org.apache.sis.image.PixelIterator;
import org.apache.sis.parameter.Parameters;
import org.apache.sis.storage.DataStoreException;
import org.apache.sis.storage.GridCoverageResource;
import org.geotoolkit.coverage.SampleDimensionUtils;
import org.geotoolkit.image.internal.SampleType;
import org.geotoolkit.process.ProcessException;
import org.geotoolkit.processing.AbstractProcess;
import static org.geotoolkit.processing.coverage.statistics.StatisticsDescriptor.*;
import org.geotoolkit.storage.coverage.ImageStatistics;
import org.geotoolkit.storage.coverage.MosaicImage;
import org.geotoolkit.storage.multires.Mosaic;
import org.opengis.geometry.Envelope;
import org.opengis.parameter.ParameterValueGroup;

/**
 * Process to create a {@link org.geotoolkit.process.coverage.statistics.ImageStatistics}
 * from a {@link org.geotoolkit.coverage.grid.GridCoverage} or {@link org.geotoolkit.coverage.io.GridCoverageResource.
 *
 * Can be directly use using analyse() static methods: <br/>
 * Eg. : <br/>
 * <code>GridCoverage2D myCoverage = ...;</code><br/>
 * <code>ImageStatistics stats = Statistics.analyse(myCoverage, true);</code><br/>
 * <code>Long[] distribution = stats.getBand(0).tightenHistogram(50);</code><br/>s
 *
 * @author bgarcia
 * @author Quentin Boileau (Geomatys)
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

    /**
     * Run Statistics process with a GridCoverage2D and return ImageStatistics
     *
     * @param coverage GridCoverage2D
     * @param excludeNoData exclude no-data flag
     * @return ImageStatistics
     * @throws ProcessException
     */
    public static ImageStatistics analyse(GridCoverage coverage, boolean excludeNoData) throws ProcessException {
        org.geotoolkit.process.Process process = new Statistics(coverage, excludeNoData);
        Parameters out = Parameters.castOrWrap(process.call());
        return out.getValue(OUTCOVERAGE);
    }

    /**
     * Run Statistics process with a CoverageResource and return ImageStatistics
     *
     * @param ref CoverageResource
     * @param excludeNoData exclude no-data flag
     * @return ImageStatistics
     * @throws ProcessException
     */
    public static ImageStatistics analyse(GridCoverageResource ref, boolean excludeNoData) throws ProcessException {
        org.geotoolkit.process.Process process = new Statistics(ref, excludeNoData);
        Parameters out = Parameters.castOrWrap(process.call());
        return out.getValue(OUTCOVERAGE);
    }

    /**
     * Run Statistics process with a CoverageResource and return ImageStatistics
     * the process is run on a reduced version of the data to avoid consuming to much resources.
     *
     * @param ref CoverageResource
     * @param excludeNoData exclude no-data flag
     * @param imageSize sampled image size
     * @return ImageStatistics
     * @throws ProcessException
     */
    public static ImageStatistics analyse(GridCoverageResource ref, boolean excludeNoData, int imageSize)
            throws ProcessException, DataStoreException {
        final GridGeometry gridGeom = ref.getGridGeometry();
        final Envelope env = gridGeom.getEnvelope();
        final GridExtent ext = gridGeom.getExtent();

        final double[] res = new double[ext.getDimension()];
        double max = 0;
        for(int i=0;i<res.length;i++){
            res[i] = (env.getSpan(i) / imageSize);
            max = Math.max(max,res[i]);
        }
        Arrays.fill(res, max);


        final GridGeometry query = gridGeom.derive().subgrid(env, res).sliceByRatio(0.5, 0, 1).build();
        GridCoverage coverage = ref.read(query);
        //we want the statistics on the real data values
        coverage = coverage.forConvertedValues(true);
        org.geotoolkit.process.Process process = new Statistics(coverage, excludeNoData);
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
            outputParameters.getOrCreate(OUTCOVERAGE).setValue(sc);

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

            outputParameters.getOrCreate(OUTCOVERAGE).setValue(sc);
            fireProgressing("Pre-analysing finished", 10f, true);
            fireProgressing("Start range/histogram computing", 10f, true);
        }

        final ImageStatistics.Band[] bands = sc.getBands();
        final org.apache.sis.math.Statistics[] stats = new org.apache.sis.math.Statistics[bands.length];
        for(int i=0;i<bands.length;i++) stats[i] = new org.apache.sis.math.Statistics("stats");
        int nbBands = bands.length;

        //optimization for GridMosaicRenderedImage impl
        NumericHistogram[] histo = new NumericHistogram[nbBands];
        if (image instanceof MosaicImage) {
            final MosaicImage mosaicImage = (MosaicImage) image;
            final Mosaic gridMosaic = mosaicImage.getGridMosaic();
            final Rectangle gridRange = mosaicImage.getGridRange();
            final Dimension gridSize = gridMosaic.getGridSize();

            long startX = gridRange.x;
            long startY = gridRange.y;
            long endX = gridRange.x + gridRange.width;
            long endY = gridRange.y + gridRange.height;
            long totalTiles = gridSize.width * gridSize.height;

            //analyse each tiles of GridMosaicRenderedImage
            Raster tile;
            PixelIterator pix;
            int step = 1;
            for (long y = startY; y < endY; y++) {
                for (long x = startX; x < endX; x++) {
                    if (!gridMosaic.isMissing(x,y)) {
                        tile = mosaicImage.getTile(Math.toIntExact(x-startX), Math.toIntExact(y-startY));
                        pix = new PixelIterator.Builder().create(tile);

                        analyseRange(pix, stats, bands, excludeNoData);
                        pix.rewind();

                        mergeHistograms(histo, analyseHistogram(pix, bands, stats, excludeNoData));

                        updateBands(bands, histo);
                        fireProgressing("Histogram progressing", (step/totalTiles)*0.9f, true);
                    }
                    step++;
                }
            }

        } else {
            //-- this code replace more global case define after this code block.
            //-- an error from JAI is occured when PixelIterator request getTile of JAI RenderedOP.
            //-- To avoid exception we perform statistic after tile request and tile per tile.
            //-- Moreover if tile request fail pass to the next tile.

            {
                final int tileGridXOffset = image.getTileGridXOffset();
                final int tileGridYOffset = image.getTileGridYOffset();
                final int numXTiles       = image.getNumXTiles();
                final int numYTiles       = image.getNumYTiles();
                final int indexXmax       = tileGridXOffset + numXTiles;
                final int indexYmax       = tileGridYOffset + numYTiles;
                int step = 0;
                final int totalTiles = numXTiles * numYTiles;
                for (int y = tileGridYOffset; y < indexYmax; y++) {
                    for (int x = tileGridXOffset; x < indexXmax; x++) {
                        ++step;
                        try {
                            final Raster tile       = image.getTile(x, y);
                            final PixelIterator pix = new PixelIterator.Builder().create(tile);
                            analyseRange(pix, stats, bands, excludeNoData);
                            pix.rewind();

                            mergeHistograms(histo, analyseHistogram(pix, bands, stats, excludeNoData));

                            updateBands(bands, histo);
                            fireProgressing("Start histogram computing", (step/totalTiles)*0.9f, true);
                        } catch (ArrayIndexOutOfBoundsException ex) {
                            // when error is occured from JAI RenderedOP pass to the next tile
                            continue;
                        }
                    }
                }
            }


            //-- code in comment to avoid JAI tiles problems.
            //-- when JAI problems will be resolved or JAI not used
            //-- this code will be decommented and precedently code block should be deleted.
            {
    //            //standard image
    //            final PixelIterator pix = PixelIteratorFactory.createDefaultIterator(image);
    //
    //            //get min/max
    //            analyseRange(pix, stats, bands, excludeNoData);
    //            fireProgressing("Start histogram computing", 55f, true);
    //
    //            //reset iterator
    //            pix.rewind();

                //compute histogram
    //            histo = analyseHistogram(pix, bands, stats, excludeNoData);
    //            updateBands(bands, histo);
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
     * @param excludeNoData
     */
    private NumericHistogram[] analyseHistogram(final PixelIterator pix, final ImageStatistics.Band[] bands,
                                                org.apache.sis.math.Statistics[] stats, final boolean excludeNoData) {

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
        if (excludeNoData) {
            while (pix.next()) {
                for (int b = 0; b < nbBands; b++) {
                    final double d = pix.getSampleDouble(b);

                    //add value if not NaN or is flag as no-data
                    if (!Double.isNaN(d) &&
                            (bands[b].getNoData() == null || !(Arrays.binarySearch(bands[b].getNoData(), d) >= 0))) {
                        histograms[b].addValue(d);
                    }
                }
            }
        } else {
            //iter on each pixel band by band to add values on each band.
            while (pix.next()) {
                for (int b = 0; b < nbBands; b++) {
                    final double d = pix.getSampleDouble(b);
                    histograms[b].addValue(d);
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
