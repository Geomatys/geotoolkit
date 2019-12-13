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
package org.geotoolkit.coverage.vi3g;

import java.awt.image.BufferedImage;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferShort;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.apache.sis.coverage.SampleDimension;
import org.apache.sis.coverage.grid.GridCoverage;
import org.apache.sis.coverage.grid.GridExtent;
import org.apache.sis.coverage.grid.GridGeometry;
import org.apache.sis.internal.referencing.j2d.AffineTransform2D;
import org.apache.sis.internal.storage.ResourceOnFileSystem;
import org.apache.sis.internal.storage.io.ChannelImageInputStream;
import org.apache.sis.internal.storage.io.HyperRectangleReader;
import org.apache.sis.internal.storage.io.Region;
import org.apache.sis.measure.Units;
import org.apache.sis.metadata.iso.DefaultMetadata;
import org.apache.sis.parameter.Parameters;
import org.apache.sis.referencing.CommonCRS;
import org.apache.sis.storage.DataStore;
import org.apache.sis.storage.DataStoreException;
import org.apache.sis.storage.DataStoreProvider;
import org.apache.sis.storage.GridCoverageResource;
import org.apache.sis.storage.StorageConnector;
import org.apache.sis.util.Numbers;
import org.geotoolkit.coverage.grid.GridCoverageBuilder;
import org.geotoolkit.image.BufferedImages;
import org.geotoolkit.storage.DataStores;
import org.geotoolkit.storage.coverage.GeoreferencedGridCoverageResource;
import org.geotoolkit.util.NamesExt;
import org.opengis.geometry.Envelope;
import org.opengis.metadata.Metadata;
import org.opengis.parameter.ParameterValueGroup;
import org.opengis.referencing.crs.GeographicCRS;
import org.opengis.referencing.datum.PixelInCell;
import org.opengis.util.GenericName;

/**
 * GIMMS AVHRR Global NDVI 1/12-degree Geographic Lat/Lon.
 *
 * 1. DESCRIPTION
 *
 * VI3G dataset is an inverse cartographic transformation and mosaicing of the
 *   GIMMS AVHRR 8-km Albers Conical Equal Area continentals AF, AZ, EA, NA, and
 *   SA to a global 1/12-degree Lat/Lon grid.
 *
 *   Continent demarcation and pixel selection is predetermined with an ancillary
 *   NDVI-3G based land-water mask.
 *
 * 2. FILE NAMING CONVENTION
 *
 *  geo[year][month][period].n[sat][-[VI][version]g
 *
 *  where
 *        year     2-int   2 digit year
 *        month    3-char  abbr. lower case month name
 *        period   3-char  alphanum period: bimonthly 15[ab]
 *        sat      2-int   satellite number
 *        version  n-int   version number (3)
 *
 *  For example,
 *
 *  geo09jan15a.n17-VI3g
 *
 * 3. GRID PARAMETERS
 *
 *    grid-name: Geographic Lat/Lon
 *    pixel-size: 1/12=0.833 degrees
 *
 *    size-x: 4320
 *    size-y: 2160
 *
 *    upper-left-lat: 90.0-1/24
 *    upper-left-lon: -180.0+1/24
 *    lower-right-lat: -90.0+1/24
 *    lower-right-lon: 180.0-1/24
 *
 *    *coordinates located UL corner of pixel
 *
 * 4. DATA FORMAT - VI3g
 *
 *    datatype: 16-bit signed integer
 *    byte-order: big endian
 *
 *    scale-factor: 10000
 *    min-valid: -10000
 *    max-valid: 10000
 *    mask-water: -10000
 *    mask-nodata: -5000
 *
 *    *values include embedded flags (see full NDVI-3G documentation - in preparation)
 *
 * 5. FLAG VALUES
 *    Each NDVI data set (ndvi3g) is an INT16 file saved with ieee-big_endian
 *    it ranges from -10000->(10000->10004)
 *    with the flagW file added to the ndvi values as follows:
 *  ndvi3g = round(ndvi*10000) + flagW - 1;
 *  flagW ranges from 1->7
 *    to retrieve the original ndvi  and flagW values
 *  flagW = ndvi3g-floor(ndvi3g/10)*10 + 1;
 *  ndvi = floor(ndvi3g/10)/1000
 *    The meaning of the FLAG:
 *  FLAG = 7 (missing data)
 *  FLAG = 6 (NDVI retrieved from average seasonal profile, possibly snow)
 *  FLAG = 5 (NDVI retrieved from average seasonal profile)
 *  FLAG = 4 (NDVI retrieved from spline interpolation, possibly snow)
 *  FLAG = 3 (NDVI retrieved from spline interpolation)
 *  FLAG = 2 (Good value)
 *  FLAG = 1 (Good value)
 * END
 *
 *
 * @author Alexis Manin (Geomatys)
 * @author Johann Sorel (Geomatys)
 */
public class VI3GStore extends DataStore implements GridCoverageResource, ResourceOnFileSystem {

    private static final SampleDimension SAMPLE_DIMENSION;
    static {
        SAMPLE_DIMENSION = new SampleDimension.Builder()
                .addQuantitative("data", -0, 10006, Units.UNITY.multiply(10000))
                .addQualitative("water", -10000)
                .addQualitative(null, -5000)
                .build();
    }

    public static final int SAMPLE_SIZE = Short.SIZE / Byte.SIZE;
    public static final int WIDTH = 2160;
    public static final int HEIGHT = 4320;

    private final Parameters parameters;
    private final Path fileInput;
    private final Res resource;

    public VI3GStore(final StorageConnector connector) throws DataStoreException {
        super(DataStores.getProviderById(VI3GProvider.NAME), connector);
        this.fileInput = connector.getStorageAs(Path.class);
        this.parameters = Parameters.castOrWrap(VI3GProvider.PARAMETERS_DESCRIPTOR.createValue());
        this.parameters.getOrCreate(VI3GProvider.PATH).setValue(fileInput.toUri());
        this.resource = new Res();
    }

    @Override
    public Optional<ParameterValueGroup> getOpenParameters() {
        return Optional.of(parameters);
    }

    @Override
    public Path[] getComponentFiles() throws DataStoreException {
        return new Path[]{fileInput};
    }

    @Override
    public DataStoreProvider getProvider() {
        return DataStores.getProviderById(VI3GProvider.NAME);
    }

    @Override
    public Metadata getMetadata() throws DataStoreException {
        return new DefaultMetadata();
    }

    @Override
    public void close() throws DataStoreException {
    }

    @Override
    public GridGeometry getGridGeometry() throws DataStoreException {
        return resource.getGridGeometry();
    }

    @Override
    public List<SampleDimension> getSampleDimensions() throws DataStoreException {
        return resource.getSampleDimensions();
    }

    @Override
    public GridCoverage read(GridGeometry domain, int... range) throws DataStoreException {
        return resource.read(domain, range);
    }

    @Override
    public Optional<Envelope> getEnvelope() throws DataStoreException {
        return resource.getEnvelope();
    }


    private final class Res extends GeoreferencedGridCoverageResource {

        private final GenericName name;
        private GridGeometry grid;

        private Res() {
            super(VI3GStore.this);
            name = NamesExt.create(fileInput.getFileName().toString());
        }

        @Override
        public Optional<GenericName> getIdentifier() throws DataStoreException {
            return Optional.of(name);
        }

        @Override
        public synchronized GridGeometry getGridGeometry() throws DataStoreException {
            if (grid == null) {
                GeographicCRS crs = CommonCRS.WGS84.geographic();

                // HACK : a problem has been detected with Geotk rendering of latitude first data.
                // As a warkaround, we define the image as longitude first, and then roll it
                // using sheer.
                final GridExtent gridEnv = new GridExtent(WIDTH, HEIGHT);
                AffineTransform2D tr = new AffineTransform2D(-180.0 / WIDTH, 0, 0, 360.0 / HEIGHT, 90.0, -180.0);

                grid = new GridGeometry(gridEnv, PixelInCell.CELL_CORNER, tr, crs);
            }
            return grid;
        }

        @Override
        public List<SampleDimension> getSampleDimensions() throws DataStoreException {
            return Collections.singletonList(SAMPLE_DIMENSION);
        }

        @Override
        protected GridCoverage readGridSlice(int[] areaLower, int[] areaUpper, int[] subsampling, int ... range) throws DataStoreException {

            final GridGeometry allGridGeom = getGridGeometry();
            final GridGeometry gridGeometry = getGridGeometry(getGridGeometry(), areaLower, areaUpper, subsampling);
            final GridExtent extent = allGridGeom.getExtent();

            short[] allData;
            try (final ChannelImageInputStream imageStream = new ChannelImageInputStream(
                    null, Files.newByteChannel(fileInput), ByteBuffer.allocateDirect(8192), false)) {
                imageStream.setByteOrder(ByteOrder.BIG_ENDIAN);

                final HyperRectangleReader reader = new HyperRectangleReader(Numbers.SHORT, imageStream, 0);
                allData = (short[]) reader.read(new Region(
                        new long[]{extent.getSize(0),extent.getSize(1)},
                        new long[]{areaLower[0], areaLower[1]},
                        new long[]{areaUpper[0], areaUpper[1]},
                        subsampling));


            } catch (IOException ex) {
                throw new DataStoreException(ex.getMessage(), ex);
            }

            final int width = (int) gridGeometry.getExtent().getSize(0);
            final int height = (int) gridGeometry.getExtent().getSize(1);
            final BufferedImage image = BufferedImages.createImage(width, height, 1, DataBuffer.TYPE_SHORT);
            DataBuffer dataBuffer = image.getRaster().getDataBuffer();
            System.arraycopy(allData, 0, ((DataBufferShort) dataBuffer).getData(), 0, allData.length);

            final GridCoverageBuilder gcb = new GridCoverageBuilder();
            gcb.setName(name.toString());
            gcb.setRenderedImage(image);
            gcb.setSampleDimensions(getSampleDimensions());
            gcb.setGridGeometry(gridGeometry);
            return gcb.build();
        }

    }

}
