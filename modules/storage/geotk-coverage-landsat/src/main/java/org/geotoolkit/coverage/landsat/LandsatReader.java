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

import java.io.IOException;
import java.nio.file.Path;
import java.text.ParseException;
import java.util.List;
import java.util.concurrent.CancellationException;
import org.apache.sis.geometry.GeneralEnvelope;
import org.apache.sis.util.ArgumentChecks;
import org.geotoolkit.coverage.GridSampleDimension;
import org.geotoolkit.coverage.grid.GeneralGridEnvelope;
import org.geotoolkit.coverage.grid.GeneralGridGeometry;
import org.geotoolkit.coverage.grid.GridCoverage2D;
import org.geotoolkit.coverage.grid.GridEnvelope2D;
import org.geotoolkit.coverage.grid.GridGeometry2D;
import org.geotoolkit.coverage.io.CoverageStoreException;
import org.geotoolkit.coverage.io.GridCoverageReadParam;
import org.geotoolkit.coverage.io.GridCoverageReader;
import org.geotoolkit.coverage.io.ImageCoverageReader;
import static org.geotoolkit.coverage.landsat.LandsatConstants.*;
import org.geotoolkit.process.ProcessDescriptor;
import org.geotoolkit.process.ProcessFinder;
import org.opengis.coverage.grid.GridCoverage;
import org.opengis.coverage.grid.GridEnvelope;
import org.opengis.geometry.Envelope;
import org.opengis.metadata.Metadata;
import org.opengis.parameter.ParameterValueGroup;
import org.opengis.util.FactoryException;
import org.opengis.util.GenericName;

/**
 * Reader to read Landsat datas.
 *
 * @author Remi Marechal (Geomatys)
 * @version 1.0
 * @since   1.0
 */
public class LandsatReader extends GridCoverageReader {

    /**
     * {@link Path} of the directory which contain all band images.
     */
    private final Path parenPath;

    /**
     * Parser to read metadata.
     */
    private final LandsatMetadataParser metaParse;

    /**
     * Index arrays which contain index to retrieve appropriates band index to
     * build different coverage from Landsat datas.
     */
    private static final int[] REFLECTIVE_BAND_ID   = new int[]{1, 2, 3, 4, 5, 6, 7, 9};
    private static final int[] PANCHROMATIC_BAND_ID = new int[]{8};
    private static final int[] THERMIC_BAND_ID      = new int[]{10, 11};

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
     * @param index 0, 1 or 2
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
        final Envelope projectedEnvelope;
        try {
            gridExtent        = metaParse.getGridExtent(coverageName);
            projectedEnvelope = metaParse.getProjectedEnvelope();
        } catch (Exception ex) {
            throw new CoverageStoreException(ex);
        }

        return new GridGeometry2D(gridExtent, projectedEnvelope);
    }

    /**
     * {@inheritDoc }
     *
     * @param index
     * @return
     * @throws CoverageStoreException
     * @throws CancellationException
     */
    @Override
    public List<GridSampleDimension> getSampleDimensions(int index) throws CoverageStoreException, CancellationException {
        //-- a voir si elles sont pas dans les metadatas.
        switch (index) {
            case 0 : {
                //-- Reflective case RGB band 1, 2, 3, 4, 5, 6, 7, 9
                break;
            }
            case 1 : {
                //-- Panchromatic case one sample band 8
                break;
            }
            case 2 : {
                //-- Thermic case band 10 and 11
                break;
            }
            default : throw new CoverageStoreException("Current index "+index+" is not known, available index are : "
                    + "0 for reflective, 1 for panchromatic and 2 for Thermic");
        }
        return null;
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
     * @param index 0, 1 or 2.
     * @param param
     * @return
     * @throws CoverageStoreException
     * @throws CancellationException
     */
    @Override
    public GridCoverage read(int index, GridCoverageReadParam param) throws CoverageStoreException, CancellationException {

        final int[] bandId;
        switch (index) {
            case 0 : {
                //-- Reflective case RGB band 1, 2, 3, 4, 5, 6, 7, 9
                bandId = REFLECTIVE_BAND_ID;
                break;
            }
            case 1 : {
                //-- Panchromatic case one sample band 8
                bandId = PANCHROMATIC_BAND_ID;
                break;
            }
            case 2 : {
                //-- Thermic case band 10 and 11
                bandId = THERMIC_BAND_ID;
                break;
            }
            default : throw new CoverageStoreException("Current index "+index+" is not known, available index are : "
                    + "0 for reflective, 1 for panchromatic and 2 for Thermic");
        }

        try {

            final GridCoverage2D[] bands = new GridCoverage2D[bandId.length];
            //-- TODO multi threader cette boucle
            int currentCov = 0;
            for (int i : bandId) {
                final String bandName = metaParse.getValue(true, BAND_NAME_LABEL+i);
                final Path band = parenPath.resolve(bandName);
                final ImageCoverageReader bandReader = new ImageCoverageReader();
                param.setDeferred(true);
                bandReader.setInput(band.toFile());
                final GridCoverage2D read = bandReader.read(0, param);
                bands[currentCov++] = read;
                bandReader.dispose();
            }


        //-- test avec ca
//        GridCoverage2D coverageRGBIR = new BandCombineProcess(
//                    coverageR,coverageG,coverageB,coverageIR).executeNow();


            final ProcessDescriptor desc = ProcessFinder.getProcessDescriptor("coverage", "bandcombine");
            assert (desc != null);

            final ParameterValueGroup params = desc.getInputDescriptor().createValue();
            params.parameter("coverages").setValue(bands);


            final org.geotoolkit.process.Process process = desc.createProcess(params);
            final ParameterValueGroup result = process.call();

            //check result coverage
            final GridCoverage2D outCoverage = (GridCoverage2D) result.parameter("result").getValue();

            final Envelope projectedEnvelope = metaParse.getProjectedEnvelope();
            final int projDim = projectedEnvelope.getDimension();

            if (projDim > 2) {

                final GridEnvelope2D extent2D = outCoverage.getGridGeometry().getExtent2D();
                final int[] high = new int[projDim];
                high[0] = extent2D.width;
                high[1] = extent2D.height;
                high[2] = 1;

                final GridEnvelope grEnv3D = new GeneralGridEnvelope(new int[projDim], high, false);

                final Envelope env2D = outCoverage.getGridGeometry().getEnvelope2D();

                final GeneralEnvelope gE = new GeneralEnvelope(projectedEnvelope);
                gE.setRange(0, env2D.getMinimum(0), env2D.getMaximum(0));
                gE.setRange(1, env2D.getMinimum(1), env2D.getMaximum(1));

                final GridGeometry2D gridGeometry2D = new GridGeometry2D(grEnv3D, gE);

                return new GridCoverage2D(outCoverage.getName().toString(), outCoverage.getRenderedImage(),
                    gridGeometry2D, outCoverage.getSampleDimensions(),
                    null, null, null);
            } else {
                return outCoverage;
            }

        } catch (Exception ex) {
            throw new CoverageStoreException(ex);
        }
    }

}
