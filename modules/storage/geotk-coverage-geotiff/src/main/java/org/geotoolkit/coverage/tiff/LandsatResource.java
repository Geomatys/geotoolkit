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
package org.geotoolkit.coverage.tiff;

import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.IOException;
import java.nio.file.Path;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CancellationException;
import javax.imageio.ImageReadParam;
import javax.imageio.ImageReader;
import org.apache.sis.coverage.SampleDimension;
import org.apache.sis.coverage.grid.GridCoverage;
import org.apache.sis.coverage.grid.GridCoverageBuilder;
import org.apache.sis.coverage.grid.GridExtent;
import org.apache.sis.coverage.grid.GridGeometry;
import org.apache.sis.internal.storage.MetadataBuilder;
import org.apache.sis.internal.storage.ResourceOnFileSystem;
import org.apache.sis.internal.storage.StoreResource;
import org.apache.sis.storage.DataStore;
import org.apache.sis.storage.DataStoreException;
import org.apache.sis.storage.GridCoverageResource;
import org.geotoolkit.coverage.io.ImageCoverageReader;
import static org.geotoolkit.coverage.tiff.LandsatConstants.*;
import org.geotoolkit.coverage.tiff.LandsatConstants.CoverageGroup;
import org.geotoolkit.image.io.plugin.TiffImageReader;
import org.geotoolkit.process.ProcessDescriptor;
import org.geotoolkit.process.ProcessException;
import org.geotoolkit.process.ProcessFinder;
import org.geotoolkit.storage.coverage.GeoreferencedGridCoverageResource;
import org.opengis.geometry.Envelope;
import org.opengis.parameter.ParameterValueGroup;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.datum.PixelInCell;
import org.opengis.referencing.operation.MathTransform;
import org.opengis.util.FactoryException;
import org.opengis.util.GenericName;
import org.opengis.util.NoSuchIdentifierException;

/**
 * Reader adapted to read and aggregate directly needed bands to build appropriate
 * REFLECTIVE, THERMIC, or PANCHROMATIC Landsat part.
 *
 * @author Remi Marechal (Geomatys)
 * @version 1.0
 * @since   1.0
 */
final class LandsatResource extends GeoreferencedGridCoverageResource implements GridCoverageResource, ResourceOnFileSystem, StoreResource {

    /**
     * TiffImageReader SPI used to read images
     */
    private static final TiffImageReader.Spi TIFF_SPI = new TiffImageReader.Spi();

    private final LandsatStore store;
    private final GenericName name;

    /**
     * {@link Path} of the parent directory which contain all
     * Landsat 8 images.
     */
    private final Path parentDirectory;

    /**
     * {@link Path} to the metadata landsat 8 file.
     */
    private final LandsatMetadataParser metadataParser;

    /**
     * Array which contains all sample dimension for each read index.
     */
    private List gsdLandsat = null;

    private final CoverageGroup group;

    /**
     * Build an appripriate {@link CoverageReference} to read Landsat 8 datas.<br><br>
     *
     * Note : a Landsat 8 product may contains 3 kind of coverages.<br>
     * To make difference between them we use the {@linkplain GenericName name} given in parameter.<br>
     *
     * the expected names are : REFLECTIVE, THERMIC, or PANCHROMATIC.
     *
     * @param store normally Landsat store.
     * @param group REFLECTIVE, THERMIC, or PANCHROMATIC.
     * @param parentDirectory path metadata file parent folder.
     * @param metadataParser Landsat 8 parent directory.
     */
    public LandsatResource(final LandsatStore store, final Path parentDirectory,
                final LandsatMetadataParser metadataParser, final CoverageGroup group) {
        super(null);
        this.store = store;
        this.name = group.createName(store.getSceneName());
        this.parentDirectory = parentDirectory;
        this.metadataParser = metadataParser;
        this.group = group;
    }

    @Override
    public Optional<GenericName> getIdentifier() throws DataStoreException {
        return Optional.of(name);
    }

    @Override
    public Optional<Envelope> getEnvelope() throws DataStoreException {
        GridGeometry gg = getGridGeometry();
        if (gg.isDefined(GridGeometry.ENVELOPE)) {
            return Optional.ofNullable(gg.getEnvelope());
        } else {
            return Optional.empty();
        }
    }

    @Override
    public DataStore getOriginator() {
        return store;
    }

    @Override
    protected void createMetadata(MetadataBuilder metadata) throws DataStoreException {
        try {
            //TODO merge this metadata in default one
            metadataParser.getMetadata(group);
        } catch (FactoryException ex) {
            throw new DataStoreException(ex.getMessage(), ex);
        } catch (ParseException ex) {
            throw new DataStoreException(ex.getMessage(), ex);
        }
        super.createMetadata(metadata);
    }

    @Override
    public GridGeometry getGridGeometry() throws DataStoreException, CancellationException {

        final GridExtent gridExtent;
        final MathTransform gridToCRS;
        final CoordinateReferenceSystem crs;
        try {
            gridExtent = metadataParser.getGridExtent(group);
            gridToCRS  = metadataParser.getGridToCRS(group);
            crs        = metadataParser.getCRS();
        } catch (Exception ex) {
            throw new DataStoreException(ex);
        }
        return new GridGeometry(gridExtent, PixelInCell.CELL_CORNER, gridToCRS, crs);
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
            final String bandName = metadataParser.getValue(true, BAND_NAME_LABEL + i);
            final Path resolve = parentDirectory.resolve(bandName);
            final ImageCoverageReader imageCoverageReader = new ImageCoverageReader();
            try {
                final ImageReader tiffReader = TIFF_SPI.createReaderInstance();
                tiffReader.setInput(resolve);
                imageCoverageReader.setInput(tiffReader);
                final List<SampleDimension> candidates = imageCoverageReader.getSampleDimensions();
                if (candidates != null) gList.addAll(candidates);
            } catch (IOException ex) {
                throw new DataStoreException(ex);
            } finally {
                imageCoverageReader.dispose();
            }
        }
        gsdLandsat = gList;
        return gsdLandsat;
    }

    @Override
    public Path[] getComponentFiles() throws DataStoreException {
        final Set<Path> paths = new HashSet<>();
        for (int idx : group.bands) {
            final String bandName = metadataParser.getValue(true, BAND_NAME_LABEL + idx);
            paths.add(parentDirectory.resolve(bandName));
        }
        return paths.toArray(new Path[paths.size()]);
    }

    private GenericName getCoverageName() throws DataStoreException, CancellationException {
        final String sceneName  = metadataParser.getValue(false, SCENE_ID);
        return group.createName(sceneName);
    }

    @Override
    protected GridCoverage readGridSlice(int[] areaLower, int[] areaUpper, int[] subsampling, int... range) throws DataStoreException {
        GridGeometry geometry = getGridGeometry(getGridGeometry(), areaLower, areaUpper, subsampling);

        //-- get all needed band to build coverage (see Landsat spec)
        final int[] bandId = group.bands;
        final RenderedImage[] bands = new RenderedImage[bandId.length];

        try {
            int currentCov = 0;
            for (int i : bandId) {
                //-- get band name
                final String bandName = metadataParser.getValue(true, BAND_NAME_LABEL + i);
                //-- add to coverage name
                final Path band = parentDirectory.resolve(bandName);

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
            gcb.setValues(outImage);
            gcb.setDomain(geometry);
            return gcb.build();

        } catch (IOException | NoSuchIdentifierException | ProcessException ex) {
            throw new DataStoreException(ex.getMessage(), ex);
        }
    }

}
