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
import org.geotoolkit.coverage.*;
import org.geotoolkit.coverage.grid.GeneralGridGeometry;
import org.geotoolkit.coverage.grid.GridCoverage2D;
import org.geotoolkit.coverage.grid.ViewType;
import org.geotoolkit.coverage.io.CoverageStoreException;
import org.geotoolkit.coverage.io.GridCoverageReadParam;
import org.geotoolkit.coverage.io.GridCoverageReader;
import org.geotoolkit.image.iterator.PixelIterator;
import org.geotoolkit.image.iterator.PixelIteratorFactory;
import org.geotoolkit.parameter.ParametersExt;
import org.geotoolkit.process.AbstractProcess;
import org.geotoolkit.process.ProcessException;
import org.geotoolkit.referencing.CRS;
import org.opengis.coverage.grid.GridCoverage;
import org.opengis.coverage.grid.GridEnvelope;
import org.opengis.parameter.ParameterValueGroup;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.MathTransform;
import org.opengis.referencing.operation.TransformException;

import java.awt.*;
import java.awt.image.Raster;
import java.awt.image.RenderedImage;
import java.awt.image.SampleModel;
import java.util.Arrays;

import static org.geotoolkit.parameter.Parameters.getOrCreate;
import static org.geotoolkit.parameter.Parameters.value;
import static org.geotoolkit.process.coverage.statistics.StatisticsDescriptor.*;

/**
 * Process to create a {@link org.geotoolkit.process.coverage.statistics.ImageStatistics}
 * from a {@link org.geotoolkit.coverage.grid.GridCoverage2D} or {@link org.geotoolkit.coverage.io.GridCoverageReader}.
 *
 * Can be directly use using analyse() static methods: <br/>
 * Eg. : <br/>
 * <code>GridCoverage2D myCoverage = ...;</code><br/>
 * <code>ImageStatistics stats = Statistics.analyse(myCoverage, true);</code><br/>
 * <code>Long[] distribution = stats.getBand(0).tightenDistribution(50);</code><br/>s
 *
 * @author bgarcia
 * @author Quentin Boileau (Geomatys)
 */
public class Statistics extends AbstractProcess{

    public Statistics(final RenderedImage image, boolean excludeNoData){
        this(toParameters(image, null, null, null, 0, excludeNoData));
    }

    public Statistics(final GridCoverage2D coverage, boolean excludeNoData){
        this(toParameters(null, coverage, null, null, 0, excludeNoData));
    }

    public Statistics(final CoverageReference ref, boolean excludeNoData){
        this(toParameters(null, null, ref, null, 0, excludeNoData));
    }

    public Statistics(final GridCoverageReader reader, final int imageIdx, boolean excludeNoData){
        this(toParameters(null, null, null, reader, imageIdx, excludeNoData));
    }

    public Statistics(final ParameterValueGroup input) {
        super(StatisticsDescriptor.INSTANCE, input);
    }

    private static ParameterValueGroup toParameters(final RenderedImage image, final GridCoverage2D coverage, final CoverageReference ref,
                                                    final GridCoverageReader reader,  final int imageIdx, boolean excludeNoData) {
        final ParameterValueGroup params = StatisticsDescriptor.INSTANCE.getInputDescriptor().createValue();
        ParametersExt.getOrCreateValue(params, IMAGE.getName().getCode()).setValue(image);
        ParametersExt.getOrCreateValue(params, REF.getName().getCode()).setValue(ref);
        ParametersExt.getOrCreateValue(params, COVERAGE.getName().getCode()).setValue(coverage);
        ParametersExt.getOrCreateValue(params, READER.getName().getCode()).setValue(reader);
        ParametersExt.getOrCreateValue(params, IMAGE_IDX.getName().getCode()).setValue(imageIdx);
        ParametersExt.getOrCreateValue(params, EXCLUDE_NO_DATA.getName().getCode()).setValue(excludeNoData);
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
        ParameterValueGroup out = process.call();
        return value(OUTCOVERAGE, out);
    }

    /**
     * Run Statistics process with a GridCoverage2D and return ImageStatistics
     *
     * @param coverage GridCoverage2D
     * @param excludeNoData exclude no-data flag
     * @return ImageStatistics
     * @throws ProcessException
     */
    public static ImageStatistics analyse(GridCoverage2D coverage, boolean excludeNoData) throws ProcessException {
        org.geotoolkit.process.Process process = new Statistics(coverage, excludeNoData);
        ParameterValueGroup out = process.call();
        return value(OUTCOVERAGE, out);
    }

    /**
     * Run Statistics process with a CoverageReference and return ImageStatistics
     *
     * @param ref CoverageReference
     * @param excludeNoData exclude no-data flag
     * @return ImageStatistics
     * @throws ProcessException
     */
    public static ImageStatistics analyse(CoverageReference ref, boolean excludeNoData) throws ProcessException {
        org.geotoolkit.process.Process process = new Statistics(ref, excludeNoData);
        ParameterValueGroup out = process.call();
        return value(OUTCOVERAGE, out);
    }


    /**
     * Run Statistics process with a GridCoverageReader and return ImageStatistics
     *
     * @param reader GridCoverageReader
     * @param imageIdx image index to read
     * @param excludeNoData exclude no-data flag
     * @return ImageStatistics
     * @throws ProcessException
     */
    public static ImageStatistics analyse(GridCoverageReader reader, int imageIdx, boolean excludeNoData)
            throws ProcessException {
        org.geotoolkit.process.Process process = new Statistics(reader, imageIdx, excludeNoData);
        ParameterValueGroup out = process.call();
        return value(OUTCOVERAGE, out);
    }

    @Override
    protected void execute() throws ProcessException {

        final RenderedImage inImage = value(IMAGE, inputParameters);
        final boolean excludeNoData = value(EXCLUDE_NO_DATA, inputParameters);

        final RenderedImage image;
        final ImageStatistics sc;
        if (inImage != null) {
            image = inImage;

            final SampleModel sm = image.getSampleModel();
            final int nbBands = sm.getNumBands();

            //create empty statistic object
            sc = new ImageStatistics(nbBands);

        } else {

            final GridCoverage2D inCoverage = value(COVERAGE, inputParameters);
            GridCoverage2D candidate = null;
            if (inCoverage != null) {
                candidate = inCoverage;
            } else {
                final GridCoverageReader reader = value(READER, inputParameters);
                final Integer imageIdx = value(IMAGE_IDX, inputParameters);

                if (reader != null && imageIdx != null) {
                    candidate = getCoverage(reader, imageIdx);
                } else {
                    final CoverageReference ref = value(REF, inputParameters);
                    if (ref != null) {
                        candidate = getCoverage(ref);
                    }
                }
            }

            if (candidate == null) {
                throw new ProcessException("Null Coverage.", this, null);
            }

            //TODO extract view as process input parameter.
            candidate = candidate.view(ViewType.GEOPHYSICS);
            image = candidate.getRenderedImage();

            final SampleModel sm = image.getSampleModel();
            final int nbBands = sm.getNumBands();
            sc = new ImageStatistics(nbBands);

            final GridSampleDimension[] sampleDimensions = candidate.getSampleDimensions();
            //add no data values and name on bands
            for (int i = 0; i < sampleDimensions.length; i++) {
                sc.getBand(i).setNoData(sampleDimensions[i].getNoDataValues());
                sc.getBand(i).setName(sampleDimensions[i].getDescription().toString());
            }
        }

        final ImageStatistics.Band[] bands = sc.getBands();
        int nbBands = bands.length;

        //optimization for GridMosaicRenderedImage impl
        if (image instanceof GridMosaicRenderedImage) {
            final GridMosaicRenderedImage mosaicImage = (GridMosaicRenderedImage) image;
            final GridMosaic gridMosaic = mosaicImage.getGridMosaic();
            final Dimension gridSize = gridMosaic.getGridSize();

            int startX = 0;
            int startY = 0;
            int endX = gridSize.width;
            int endY = gridSize.height;

            final Rectangle dataArea = gridMosaic.getDataArea();
            if (dataArea != null) {
                startX = dataArea.x;
                startY = dataArea.y;
                endX = dataArea.x + dataArea.width;
                endY = dataArea.y + dataArea.height;
            }

            //analyse each tiles of GridMosaicRenderedImage
            Raster tile;
            PixelIterator pix;
            for (int y = startY; y < endY; y++) {
                for (int x = startX; x < endX; x++) {
                    if (!gridMosaic.isMissing(x,y)) {
                        tile = mosaicImage.getTile(x, y);
                        pix = PixelIteratorFactory.createDefaultIterator(tile);
                        analyseRaster(pix, nbBands, bands, excludeNoData);
                    }
                }
            }

        } else {
            //standard image
            final PixelIterator pix = PixelIteratorFactory.createDefaultIterator(image);
            analyseRaster(pix, nbBands, bands, excludeNoData);
        }

        // finalize analysis
        sc.finish();

        //return ImageStatistics
        getOrCreate(OUTCOVERAGE, outputParameters).setValue(sc);
    }

    /**
     * Analyse each pixels using a PixelIterator
     * @param pix PixelIterator
     * @param nbBands
     * @param bands
     */
    private void analyseRaster(final PixelIterator pix, final int nbBands, final ImageStatistics.Band[] bands,
                               final boolean excludeNoData) {
        // this int permit to loop on images band.
        int b = 0;
        if (excludeNoData) {
            while (pix.next()) {
                final double d = pix.getSampleDouble();

                //add value if not NaN or is flag as no-data
                if (!Double.isNaN(d) &&
                        (bands[b].getNoData() == null || !(Arrays.binarySearch(bands[b].getNoData(), d) >= 0))) {
                    bands[b].addValue(d);
                }

                //reset b to loop on first band
                if (++b == nbBands) b = 0;
            }
        } else {
            //iter on each pixel band by band to add values on each band.
            while (pix.next()) {
                final double d = pix.getSampleDouble();
                bands[b].addValue(d);

                //reset b to loop on first band
                if (++b == nbBands) b = 0;
            }
        }
    }

    /**
     * Read coverage from CoverageReference
     * @param ref
     * @return
     * @throws ProcessException
     */
    private GridCoverage2D getCoverage(CoverageReference ref) throws ProcessException {
        try {
            final GridCoverageReader reader = ref.acquireReader();
            GridCoverage2D coverage = getCoverage(reader, ref.getImageIndex());
            ref.recycle(reader);
            return coverage;

        } catch (CoverageStoreException e) {
            throw new ProcessException(e.getMessage(), this, e);
        }
    }

    /**
     * Read coverage from a GridCoverageReader.
     * @param reader
     * @param imageIdx
     * @return
     * @throws ProcessException
     */
    private GridCoverage2D getCoverage(GridCoverageReader reader, int imageIdx) throws ProcessException {
        try {
            final GeneralGridGeometry gridGeometry = reader.getGridGeometry(imageIdx);
            CoordinateReferenceSystem crs = gridGeometry.getCoordinateReferenceSystem();
            final MathTransform gridToCRS = gridGeometry.getGridToCRS();
            final GridEnvelope extent = gridGeometry.getExtent();
            final int dim = extent.getDimension();

            //TODO analyse CRS to find lat/lon dimension position in extent envelope.
            final double[] low  = new double[dim];
            final double[] high = new double[dim];
            low[0]  = extent.getLow(0);
            high[0] = extent.getHigh(0);
            low[1]  = extent.getLow(1);
            high[1] = extent.getHigh(1);

            final GeneralEnvelope sliceExtent = new GeneralEnvelope(crs);
            for (int i = 0; i < dim; i++) {
                sliceExtent.setRange(i, low[i], high[i]);
            }

            final GridCoverageReadParam readParam = new GridCoverageReadParam();
            readParam.setEnvelope(CRS.transform(gridToCRS, sliceExtent));
            readParam.setDeferred(true);
            readParam.setCoordinateReferenceSystem(crs);

            final GridCoverage coverage = reader.read(imageIdx, readParam);
            return  CoverageUtilities.firstSlice(coverage);
        } catch (CoverageStoreException | TransformException e) {
            throw new ProcessException(e.getMessage(), this, e);
        }
    }
}
