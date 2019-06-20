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

import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.IOException;
import java.nio.file.Path;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CancellationException;
import javax.imageio.ImageReadParam;
import javax.imageio.ImageReader;
import org.apache.sis.coverage.SampleDimension;
import org.apache.sis.coverage.grid.GridCoverage;
import org.apache.sis.coverage.grid.GridExtent;
import org.apache.sis.coverage.grid.GridGeometry;
import org.apache.sis.storage.DataStoreException;
import org.apache.sis.util.ArgumentChecks;
import org.geotoolkit.coverage.grid.GridCoverageBuilder;
import org.geotoolkit.coverage.grid.GridGeometry2D;
import org.geotoolkit.coverage.io.CoverageStoreException;
import org.geotoolkit.coverage.io.GridCoverageReadParam;
import org.geotoolkit.coverage.io.ImageCoverageReader;
import static org.geotoolkit.coverage.landsat.LandsatConstants.*;
import org.geotoolkit.coverage.landsat.LandsatConstants.CoverageGroup;
import org.geotoolkit.image.io.plugin.TiffImageReader;
import org.geotoolkit.process.ProcessDescriptor;
import org.geotoolkit.process.ProcessException;
import org.geotoolkit.process.ProcessFinder;
import org.geotoolkit.storage.coverage.GeoReferencedGridCoverageReader;
import org.geotoolkit.storage.coverage.GridCoverageResource;
import org.opengis.metadata.Metadata;
import org.opengis.parameter.ParameterValueGroup;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.datum.PixelInCell;
import org.opengis.referencing.operation.MathTransform;
import org.opengis.referencing.operation.TransformException;
import org.opengis.util.FactoryException;
import org.opengis.util.GenericName;
import org.opengis.util.NoSuchIdentifierException;

/**
 * Reader to read Landsat datas.
 *
 * @author Remi Marechal (Geomatys)
 * @version 1.0
 * @since   1.0
 */
final class LandsatReader extends GeoReferencedGridCoverageReader {
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
    private List gsdLandsat = null;

    private final CoverageGroup group;

    /**
     * Build a Landsat reader from Parent directory path and metadata file path.
     *
     * @param parentDirectory directory path which contain all Landsat bands images.
     * @param metadata path to metadata file.
     * @throws IOException if problem during metadatas parser building.
     */
    LandsatReader(GridCoverageResource res, final Path parentDirectory, final Path metadata, LandsatConstants.CoverageGroup group) throws IOException {
        super(res);
        ArgumentChecks.ensureNonNull("parent directory path", parentDirectory);
        ArgumentChecks.ensureNonNull("metadata path", metadata);
        this.parenPath = parentDirectory;
        this.metaParse  = new LandsatMetadataParser(metadata);
        this.group = group;
    }

    /**
     * Build a Landsat reader from Parent directory path and metadata file path.
     *
     * @param parentDirectory directory path which contain all Landsat bands images.
     * @param metadataParser metadata parser for Landsat8.
     * @throws IOException if problem during metadatas parser building.
     */
    LandsatReader(GridCoverageResource res, final Path parentDirectory, final LandsatMetadataParser metadataParser, LandsatConstants.CoverageGroup group) throws IOException {
        super(res);
        ArgumentChecks.ensureNonNull("parent directory path", parentDirectory);
        ArgumentChecks.ensureNonNull("metadata parser", metadataParser);
        this.parenPath  = parentDirectory;
        this.metaParse  = metadataParser;
        this.group = group;
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
            return metaParse.getMetadata(group);
        } catch (FactoryException | ParseException ex) {
            throw new CoverageStoreException("Landsat coverage reader metadata parsing : ", ex);
        }
    }

    @Override
    public GenericName getCoverageName() throws CoverageStoreException, CancellationException {
        final String sceneName  = metaParse.getValue(false, SCENE_ID);
        return group.createName(sceneName);
    }

    @Override
    public GridGeometry getGridGeometry() throws CoverageStoreException, CancellationException {

        final GridExtent gridExtent;
        final MathTransform gridToCRS;
        final CoordinateReferenceSystem crs;
        try {
            gridExtent = metaParse.getGridExtent(group);
            gridToCRS  = metaParse.getGridToCRS(group);
            crs        = metaParse.getCRS();
        } catch (Exception ex) {
            throw new CoverageStoreException(ex);
        }
        return new GridGeometry2D(gridExtent, PixelInCell.CELL_CORNER, gridToCRS, crs);
    }

    /**
     * {@inheritDoc }
     *
     * @param index 0, 1 or 2 for respectively ({@link LandsatConstants#REFLECTIVE_LABEL},
     * {@link LandsatConstants#PANCHROMATIC_LABEL}, {@link LandsatConstants#THERMAL_LABEL}).
     */
    @Override
    public List<SampleDimension> getSampleDimensions() throws DataStoreException, CancellationException {
        if (gsdLandsat != null) {
            return gsdLandsat;
        }
        final int[] bandId = group.bands;
        final List<SampleDimension> gList = new ArrayList<>();
        for (int i : bandId) {
            final String bandName = metaParse.getValue(true, BAND_NAME_LABEL + i);
            final Path resolve = parenPath.resolve(bandName);
            final ImageCoverageReader imageCoverageReader = new ImageCoverageReader();
            try {
                final ImageReader tiffReader = TIFF_SPI.createReaderInstance();
                tiffReader.setInput(resolve);
                imageCoverageReader.setInput(tiffReader);
                final List<SampleDimension> candidates = imageCoverageReader.getSampleDimensions();
                if (candidates != null) gList.addAll(candidates);
            } catch (IOException ex) {
                throw new CoverageStoreException(ex);
            } finally {
                imageCoverageReader.dispose();
            }
        }
        gsdLandsat = gList;
        return gsdLandsat;
    }

    @Override
    protected GridCoverage readGridSlice(int[] areaLower, int[] areaUpper, int[] subsampling, GridCoverageReadParam param)
            throws DataStoreException, TransformException, CancellationException
    {
        GridGeometry geometry = GeoReferencedGridCoverageReader.getGridGeometry(getGridGeometry(), areaLower, areaUpper, subsampling);

        //-- get all needed band to build coverage (see Landsat spec)
        final int[] bandId = group.bands;
        final RenderedImage[] bands = new RenderedImage[bandId.length];

        try {
            int currentCov = 0;
            for (int i : bandId) {
                //-- get band name
                final String bandName = metaParse.getValue(true, BAND_NAME_LABEL + i);
                //-- add to coverage name
                final Path band = parenPath.resolve(bandName);

                //-- reader config
                final TiffImageReader tiffReader = (TiffImageReader) TIFF_SPI.createReaderInstance();
                tiffReader.setInput(band);

                Rectangle rec = new Rectangle(
                        areaLower[0],
                        areaLower[1],
                        areaUpper[0] - areaLower[0],
                        areaUpper[1] - areaLower[1]);

                final ImageReadParam readParam = tiffReader.getDefaultReadParam();
                readParam.setSourceRegion(rec);
                readParam.setSourceSubsampling(subsampling[0], subsampling[1], 0, 0);

                try {
                    BufferedImage read = tiffReader.read(0, readParam);
                    bands[currentCov++] = read;
                } finally {
                    tiffReader.dispose();
                }
            }
            final ProcessDescriptor desc = ProcessFinder.getProcessDescriptor("geotoolkit", "image:bandcombine");
            final ParameterValueGroup params = desc.getInputDescriptor().createValue();
            params.parameter("images").setValue(bands);
            final org.geotoolkit.process.Process process = desc.createProcess(params);
            final ParameterValueGroup result = process.call();
            final RenderedImage outImage =  (RenderedImage) result.parameter("result").getValue();

            final GridCoverageBuilder gcb = new GridCoverageBuilder();
            gcb.setRenderedImage(outImage);
            gcb.setGridGeometry(geometry);
            gcb.setName(getCoverageName().toString());

            return gcb.build();

        } catch(IOException | NoSuchIdentifierException | ProcessException ex) {
            throw new CoverageStoreException(ex.getMessage(), ex);
        }
    }
}
