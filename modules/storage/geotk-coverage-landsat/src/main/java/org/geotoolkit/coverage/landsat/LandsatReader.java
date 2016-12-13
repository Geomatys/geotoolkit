/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2016, Geomatys
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
package org.geotoolkit.coverage.landsat;

import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.IOException;
import java.nio.file.Path;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CancellationException;

import javax.imageio.ImageReadParam;
import javax.imageio.ImageReader;

import org.opengis.coverage.grid.GridCoverage;
import org.opengis.coverage.grid.GridEnvelope;
import org.opengis.geometry.Envelope;
import org.opengis.metadata.Metadata;
import org.opengis.parameter.ParameterValueGroup;
import org.opengis.util.FactoryException;
import org.opengis.util.GenericName;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.datum.PixelInCell;
import org.opengis.referencing.operation.MathTransform;

import org.apache.sis.referencing.operation.matrix.Matrices;
import org.apache.sis.referencing.operation.matrix.MatrixSIS;
import org.apache.sis.util.ArgumentChecks;

import org.geotoolkit.coverage.GridSampleDimension;
import org.geotoolkit.coverage.grid.GeneralGridEnvelope;
import org.geotoolkit.coverage.grid.GeneralGridGeometry;
import org.geotoolkit.coverage.grid.GridCoverageBuilder;
import org.geotoolkit.coverage.grid.GridGeometry2D;
import org.geotoolkit.coverage.io.CoverageStoreException;
import org.geotoolkit.coverage.io.GridCoverageReadParam;
import org.geotoolkit.coverage.io.GridCoverageReader;
import org.geotoolkit.coverage.io.ImageCoverageReader;
import org.geotoolkit.image.io.plugin.TiffImageReader;
import org.geotoolkit.internal.referencing.CRSUtilities;
import org.geotoolkit.process.ProcessDescriptor;
import org.geotoolkit.process.ProcessFinder;

import static org.geotoolkit.coverage.landsat.LandsatConstants.*;
import org.geotoolkit.storage.coverage.CoverageReaderHelper;

/**
 * Reader to read Landsat datas.
 *
 * @author Remi Marechal (Geomatys)
 * @version 1.0
 * @since   1.0
 */
public class LandsatReader extends GridCoverageReader {

    static {
        /**
         * Index arrays which contain index to retrieve appropriates band index to
         * build different coverage from Landsat datas.
         * Values came from Landsat8 documentation.
         */
       final int[] REFLECTIVE_BAND_ID   = new int[]{1, 2, 3, 4, 5, 6, 7, 9};
       final int[] PANCHROMATIC_BAND_ID = new int[]{8};
       final int[] THERMIC_BAND_ID      = new int[]{10, 11};

       BANDS_INDEX = new int[][]{REFLECTIVE_BAND_ID,
                                 PANCHROMATIC_BAND_ID,
                                 THERMIC_BAND_ID};
    }

    /**
     * Index arrays which contain index to retrieve appropriates band index to
     * build different coverage from Landsat datas.
     */
    private final static int[][] BANDS_INDEX;

    /**
     * TiffImageReader SPI used to read images
     */
    private static final TiffImageReader.Spi TIFF_SPI = new TiffImageReader.Spi();

    /**
     * {@link Path} of the directory which contain all band images.
     */
    private final Path parenPath;

    /**
     * Parser to read metadata.
     */
    private final LandsatMetadataParser metaParse;

    /**
     * Array which contains all sample dimension for each read index.
     */
    private final List[] gsdLandsat = new List[3];

    /**
     * Build a Landsat reader from Parent directory path and metadata file path.
     *
     * @param parentDirectory directory path which contain all Landsat bands images.
     * @param metadata path to metadata file.
     * @throws IOException if problem during metadatas parser building.
     */
    LandsatReader(final Path parentDirectory, final Path metadata) throws IOException {
        ArgumentChecks.ensureNonNull("parent directory path", parentDirectory);
        ArgumentChecks.ensureNonNull("metadata path", metadata);
        this.parenPath = parentDirectory;
        this.metaParse  = new LandsatMetadataParser(metadata);
    }

    /**
     * Build a Landsat reader from Parent directory path and metadata file path.
     *
     * @param parentDirectory directory path which contain all Landsat bands images.
     * @param metadataParser metadata parser for Landsat8.
     * @throws IOException if problem during metadatas parser building.
     */
    LandsatReader(final Path parentDirectory, final LandsatMetadataParser metadataParser) throws IOException {
        ArgumentChecks.ensureNonNull("parent directory path", parentDirectory);
        ArgumentChecks.ensureNonNull("metadata parser", metadataParser);
        this.parenPath  = parentDirectory;
        this.metaParse  = metadataParser;
    }

    /**
     * {@inheritDoc }
     * Returns ISO19115 metadata filled by Landsat metadatas.
     *
     * @return ISO19115 metadata filled by Landsat metadatas.
     * @throws CoverageStoreException if problem during metadata parsing.
     */
    @Override
    public Metadata getMetadata() throws CoverageStoreException {
        try {
            return metaParse.getMetadata(GENERAL_LABEL);
        } catch (FactoryException | ParseException ex) {
            throw new CoverageStoreException("Landsat coverage reader metadata parsing : ", ex);
        }
    }

    /**
     * {@inheritDoc }
     *
     * @return
     * @throws CoverageStoreException
     * @throws CancellationException
     */
    @Override
    public List<? extends GenericName> getCoverageNames() throws CoverageStoreException, CancellationException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    /**
     * {@inheritDoc }
     *
     * @param index 0, 1 or 2 for respectively ({@link LandsatConstants#REFLECTIVE_LABEL},
     * {@link LandsatConstants#PANCHROMATIC_LABEL}, {@link LandsatConstants#THERMAL_LABEL}).
     * @return
     * @throws CoverageStoreException
     * @throws CancellationException
     */
    @Override
    public GeneralGridGeometry getGridGeometry(int index) throws CoverageStoreException, CancellationException {
        final String coverageName;
        switch (index) {
            case 0 : {
                //-- Reflective case RGB band 1, 2, 3, 4, 5, 6, 7, 9
                coverageName = REFLECTIVE_LABEL;
                break;
            }
            case 1 : {
                //-- Panchromatic case one sample band 8
                coverageName = PANCHROMATIC_LABEL;
                break;
            }
            case 2 : {
                //-- Thermic case band 10 and 11
                coverageName = THERMAL_LABEL;
                break;
            }
            default : throw new CoverageStoreException("Current index "+index+" is not known, available index are : "
                    + "0 for reflective, 1 for panchromatic and 2 for Thermic");
        }

        final GridEnvelope gridExtent;
        final MathTransform gridToCRS;
        final CoordinateReferenceSystem crs;
        try {
            gridExtent        = metaParse.getGridExtent(coverageName);
            gridToCRS         = metaParse.getGridToCRS(coverageName);
            crs               = metaParse.getCRS();
        } catch (Exception ex) {
            throw new CoverageStoreException(ex);
        }
        return new GridGeometry2D(gridExtent, PixelInCell.CELL_CORNER, gridToCRS, crs, null);
    }

    /**
     * {@inheritDoc }
     *
     * @param index 0, 1 or 2 for respectively ({@link LandsatConstants#REFLECTIVE_LABEL},
     * {@link LandsatConstants#PANCHROMATIC_LABEL}, {@link LandsatConstants#THERMAL_LABEL}).
     * @return
     * @throws CoverageStoreException
     * @throws CancellationException
     */
    @Override
    public List<GridSampleDimension> getSampleDimensions(int index) throws CoverageStoreException, CancellationException {
        if (gsdLandsat[index] != null)
            return gsdLandsat[index];

        final int[] bandId = getBandsIndex(index);

        final List<GridSampleDimension> gList = new ArrayList<>();
        for (int i : bandId) {
            final String bandName = metaParse.getValue(true, BAND_NAME_LABEL + i);
            final Path resolve = parenPath.resolve(bandName);
            final ImageCoverageReader imageCoverageReader = new ImageCoverageReader();
            try {
                final ImageReader tiffReader = TIFF_SPI.createReaderInstance();
                tiffReader.setInput(resolve);
                imageCoverageReader.setInput(tiffReader);
                gList.addAll(imageCoverageReader.getSampleDimensions(0));
            } catch (IOException ex) {
                throw new CoverageStoreException(ex);
            } finally {
                imageCoverageReader.dispose();
            }
        }

        gsdLandsat[index] = gList;
        return gsdLandsat[index];
    }

    /**
     * {@inheritDoc }
     *
     * A Landsat 8 data may contain many internal coverage.<br>
     *
     * Index parameter differenciates this kind of read : <br>
     * - 0 to read REFLECTIVE part of Landsat 8 Coverage (8 bands).<br>
     * - 1 to read PANCHROMATIC part of Landsat 8 Coverage (1 bands).<br>
     * - 2 to read THERMIC part of Landsat 8 Coverage (2 bands).
     *
     * @param index 0, 1 or 2 for respectively ({@link LandsatConstants#REFLECTIVE_LABEL},
     * {@link LandsatConstants#PANCHROMATIC_LABEL}, {@link LandsatConstants#THERMAL_LABEL}).
     * @param param
     * @return
     * @throws CoverageStoreException
     * @throws CancellationException
     */
    @Override
    public GridCoverage read(int index, GridCoverageReadParam param)
            throws CoverageStoreException, CancellationException {

        //-- get all needed band to build coverage (see Landsat spec)
        final int[] bandId = getBandsIndex(index);

        //-- create an adapted coverage name
        StringBuilder strBuild = new StringBuilder("Landsat8 : ");
        switch (index) {
            case 0 : {
                //-- Reflective case RGB band 1, 2, 3, 4, 5, 6, 7, 9
                strBuild.append(REFLECTIVE_LABEL);
                break;
            }
            case 1 : {
                //-- Panchromatic case one sample band 8
                strBuild.append(PANCHROMATIC_LABEL);
                break;
            }
            case 2 : {
                //-- Thermic case band 10 and 11
                strBuild.append(THERMAL_LABEL);
                break;
            }
            default : strBuild.append("Unknow type");
        }

        strBuild.append("-");
        strBuild.append(parenPath.getName(parenPath.getNameCount()-1));

        try {

            final CoverageReaderHelper crh        = new CoverageReaderHelper((GridGeometry2D) getGridGeometry(index), param);
            final Dimension outImgSize            = crh.getOutImgSize();
            final Rectangle srcImgBoundary        = crh.getSrcImgBoundary();
            final GridGeometry2D destGridGeometry = crh.getDestGridGeometry();

            final RenderedImage[] bands = new RenderedImage[bandId.length];

            int currentCov = 0;
            for (int i : bandId) {
                //-- get band name
                final String bandName = metaParse.getValue(true, BAND_NAME_LABEL + i);
                //-- add to coverage name
                final Path band = parenPath.resolve(bandName);

                //-- reader config
                final TiffImageReader tiffReader = (TiffImageReader) TIFF_SPI.createReaderInstance();
                tiffReader.setInput(band);
                final ImageReadParam readParam = tiffReader.getDefaultReadParam();
                readParam.setSourceRegion(srcImgBoundary);
                readParam.setSourceSubsampling(Math.max((int) Math.round(srcImgBoundary.getWidth()  / outImgSize.getWidth()), 1),
                                               Math.max((int) Math.round(srcImgBoundary.getHeight() / outImgSize.getHeight()), 1), 0, 0);

                try {
                    BufferedImage read = tiffReader.read(0, readParam);
                    bands[currentCov++] = read;
                } finally {
                    tiffReader.dispose();
                }
            }

        //-- test avec ca
//        GridCoverage2D coverageRGBIR = new BandCombineProcess(
//                    coverageR,coverageG,coverageB,coverageIR).executeNow();

            final ProcessDescriptor desc = ProcessFinder.getProcessDescriptor("image", "bandcombine");
            assert (desc != null);

            final ParameterValueGroup params = desc.getInputDescriptor().createValue();
            params.parameter("images").setValue(bands);


            final org.geotoolkit.process.Process process = desc.createProcess(params);
            final ParameterValueGroup result = process.call();

            //check result coverage
            final RenderedImage outImage =  (RenderedImage) result.parameter("result").getValue();

            /*
             * After image reading with integer subsampling scales image haven't got expected size.
             * Re-build coverage from image and requested envelope
             */
            final int originFHA = CRSUtilities.firstHorizontalAxis(destGridGeometry.getCoordinateReferenceSystem());

            //-- gridToCRS
            final Envelope envelope       = destGridGeometry.getEnvelope();
            final MathTransform gridToCRS = destGridGeometry.getGridToCRS(PixelInCell.CELL_CORNER);
            final int srcDim              = gridToCRS.getSourceDimensions();
            final int destDim             = gridToCRS.getTargetDimensions();
            int[] upper = new int[destDim];
            Arrays.fill(upper, 1);
            upper[originFHA]     = outImage.getWidth();
            upper[originFHA + 1] = outImage.getHeight();

            final GeneralGridEnvelope ggE = new GeneralGridEnvelope(new int[srcDim], upper, false);
            final MatrixSIS sisMatrix     = Matrices.createDiagonal(destDim + 1, srcDim + 1);
            final double scaleX = envelope.getSpan(originFHA) / outImage.getWidth();
            final double scaleY = envelope.getSpan(originFHA + 1) / outImage.getHeight();
            final double transX = envelope.getMinimum(originFHA);
            final double transY = envelope.getMaximum(originFHA + 1);

            sisMatrix.setElement(originFHA, originFHA, scaleX);
            sisMatrix.setElement(originFHA + 1, originFHA + 1, - scaleY);
            sisMatrix.setElement(originFHA, srcDim, transX);
            sisMatrix.setElement(originFHA + 1, srcDim, transY);

            final GridGeometry2D gridGeometry2D = new GridGeometry2D(ggE, PixelInCell.CELL_CORNER, gridToCRS,
                                                        destGridGeometry.getCoordinateReferenceSystem(), null);
            //-- new built grid to CRS

            //-- build new coverage
            final GridCoverageBuilder gridCoverageBuilder = new GridCoverageBuilder();
            gridCoverageBuilder.setName(strBuild.toString());
            gridCoverageBuilder.setGridGeometry(gridGeometry2D);
            gridCoverageBuilder.setRenderedImage(outImage);
            List<GridSampleDimension> sampleDimensions = getSampleDimensions(index);
            gridCoverageBuilder.setSampleDimensions(sampleDimensions.toArray(new GridSampleDimension[sampleDimensions.size()]));
            
            return gridCoverageBuilder.build();

        } catch (Exception ex) {
            throw new CoverageStoreException(ex);
        }
    }

    /**
     * Returns array index of future aggregated bands from read index.<br>
     * Supported values are 0 for REFLECTIVE, 1 for PANCHROMATIC and 2 for THERMIC.
     *
     * @param readIndex Coverage read index.
     * @return array index of future aggregated bands
     * @throws CoverageStoreException if index is out of [0; 3] boundary.
     */
    private static int[] getBandsIndex(final int readIndex) throws CoverageStoreException {
        if (readIndex < 0 || readIndex > 2)
            throw new CoverageStoreException("Current index "+readIndex+" is not known, available index are : "
                    + "0 for reflective, 1 for panchromatic and 2 for Thermic");
        return BANDS_INDEX[readIndex];
    }


/**
 * Following method in commentary in wainting for tiffImageReader update about ImageReadParam.setSrcRenderSize().
 */
////    /**
////     * {@inheritDoc }
////     *
////     * A Landsat 8 data may contain many internal coverage.<br>
////     *
////     * Index parameter differenciates this kind of read : <br>
////     * - 0 to read REFLECTIVE part of Landsat 8 Coverage (8 bands).<br>
////     * - 1 to read PANCHROMATIC part of Landsat 8 Coverage (1 bands).<br>
////     * - 2 to read THERMIC part of Landsat 8 Coverage (2 bands).
////     *
////     * @param index 0, 1 or 2.
////     * @param param
////     * @return
////     * @throws CoverageStoreException
////     * @throws CancellationException
////     */
////    @Override
////    public GridCoverage read(int index, GridCoverageReadParam param) throws CoverageStoreException, CancellationException {
////
////        final int[] bandId;
////        switch (index) {
////            case 0 : {
////                //-- Reflective case RGB band 1, 2, 3, 4, 5, 6, 7, 9
////                bandId = REFLECTIVE_BAND_ID;
////                break;
////            }
////            case 1 : {
////                //-- Panchromatic case one sample band 8
////                bandId = PANCHROMATIC_BAND_ID;
////                break;
////            }
////            case 2 : {
////                //-- Thermic case band 10 and 11
////                bandId = THERMIC_BAND_ID;
////                break;
////            }
////            default : throw new CoverageStoreException("Current index "+index+" is not known, available index are : "
////                    + "0 for reflective, 1 for panchromatic and 2 for Thermic");
////        }
////
////        try {
////
////            final Path[] bands = new Path[bandId.length];
////            int b = 0;
////            //-- build all bands path
////            for (int i : bandId) {
////                final String bandName = metaParse.getValue(true, BAND_NAME_LABEL+i);
////                bands[b++] = parenPath.resolve(bandName);
////            }
////
////            final GridGeometry2D originGridGeometry = (GridGeometry2D) getGridGeometry(index);
////
////            final CoverageReaderHelper crh = new CoverageReaderHelper(originGridGeometry, param);
////
////            //-- srcImgBoundary
////            final Rectangle srcImgBoundary  = crh.getSrcImgBoundary();
////
////            //-- out img boundary
////            final Dimension outimgDimension = crh.getOutImgSize();
////
////            //-- destination grid geometry
////            GridGeometry2D destGridGeometry = crh.getDestGridGeometry();
////
////            //-- build appropriate ColorModel and SampleModel
////            final ImageReader reader = XImageIO.getReader(bands[0].toFile(), false, true);
////            SampleType sampTip = SampleType.valueOf(reader.getRawImageType(0).getSampleModel().getDataType());
////            reader.dispose();
////
////            final ColorModel colorModel   = ImageUtils.createColorModel(sampTip, bands.length, PhotometricInterpretation.GrayScale, null);
////            final SampleModel sampleModel = ImageUtils.createSampleModel(PlanarConfiguration.Banded, sampTip, outimgDimension.width, outimgDimension.height, bands.length);
////
////            //-- build specifical sentinel Large Image
////            final Landsat8RenderedImage landsatRenderedImage = new Landsat8RenderedImage(srcImgBoundary, outimgDimension,
////                                                                                         sampleModel, colorModel, bands);
////
////            final List<GridSampleDimension> sampleDimensions = getSampleDimensions(index);
////            final GridCoverageBuilder gcb = new GridCoverageBuilder();
////            gcb.setRenderedImage(landsatRenderedImage);
////            gcb.setGridGeometry(destGridGeometry);
////            gcb.setSampleDimensions(sampleDimensions.toArray(new GridSampleDimension[sampleDimensions.size()]));
////
////            return gcb.getGridCoverage2D();
////        } catch (TransformException |IOException ex) {
////            throw new CoverageStoreException(ex);
////        }
////    }
}
