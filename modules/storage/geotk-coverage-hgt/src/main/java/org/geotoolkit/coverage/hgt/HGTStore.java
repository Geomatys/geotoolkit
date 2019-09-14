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
package org.geotoolkit.coverage.hgt;

import java.awt.image.BufferedImage;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferShort;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.sis.coverage.SampleDimension;
import org.apache.sis.coverage.grid.GridCoverage;
import org.apache.sis.coverage.grid.GridExtent;
import org.apache.sis.coverage.grid.GridGeometry;
import org.apache.sis.geometry.GeneralEnvelope;
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
import org.geotoolkit.coverage.grid.GridGeometry2D;
import org.geotoolkit.image.BufferedImages;
import org.geotoolkit.storage.DataStores;
import org.geotoolkit.storage.coverage.GeoReferencedGridCoverageReader;
import org.geotoolkit.storage.coverage.GeoreferencedGridCoverageResource;
import org.geotoolkit.util.NamesExt;
import org.opengis.geometry.Envelope;
import org.opengis.metadata.Metadata;
import org.opengis.parameter.ParameterValueGroup;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.util.GenericName;

/**
 * Store adapted to HGT files.
 *
 * @author Johann Sorel (Geomatys)
 * @author Alexis Manin (Geomatys)
 */
public class HGTStore extends DataStore implements GridCoverageResource, ResourceOnFileSystem {

    /**
     * HGT file name pattern. Give lower-left geographic position (CRS:84) of the current tile.
     */
    private static final Pattern FILENAME_PATTERN = Pattern.compile("(?i)(N|S)(\\d+)(E|W)(\\d+)");

    private static final SampleDimension SAMPLE_DIMENSION;
    static {
        SAMPLE_DIMENSION = new SampleDimension.Builder()
                .addQuantitative("data", Short.MIN_VALUE + 1, Short.MAX_VALUE, Units.METRE)
                .addQualitative(null, Short.MIN_VALUE)
                .build();
    }

    static final int SAMPLE_SIZE = Short.SIZE / Byte.SIZE;

    private final Parameters parameters;
    private final Path fileInput;
    private final Res resource;

    public HGTStore(final StorageConnector connector) throws DataStoreException {
        super(DataStores.getProviderById(HGTProvider.NAME), connector);
        this.fileInput = connector.getStorageAs(Path.class);
        this.parameters = Parameters.castOrWrap(HGTProvider.PARAMETERS_DESCRIPTOR.createValue());
        this.parameters.getOrCreate(HGTProvider.PATH).setValue(fileInput.toUri());
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
        return DataStores.getProviderById(HGTProvider.NAME);
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
            super(HGTStore.this);
            name = NamesExt.create(fileInput.getFileName().toString());
        }

        @Override
        public Optional<GenericName> getIdentifier() throws DataStoreException {
            return Optional.of(name);
        }

        @Override
        public synchronized GridGeometry getGridGeometry() throws DataStoreException {
            if (grid == null) {
                try {
                    final Matcher matcher = FILENAME_PATTERN.matcher(org.geotoolkit.nio.IOUtilities.filenameWithoutExtension(fileInput));
                    matcher.find();

                    final int size = (int) Math.round(Math.sqrt(Files.size(fileInput) / (Short.SIZE / Byte.SIZE)));
                    final GridExtent extent = new GridExtent(size, size);
                    final CoordinateReferenceSystem crs = CommonCRS.WGS84.normalizedGeographic();
                    final GeneralEnvelope envelope = new GeneralEnvelope(crs);
                    final int latitude = matcher.group(1).toLowerCase().startsWith("n")?
                            Integer.parseInt(matcher.group(2)) : -Integer.parseInt(matcher.group(2));
                    final int longitude = matcher.group(3).toLowerCase().startsWith("e")?
                            Integer.parseInt(matcher.group(4)) : -Integer.parseInt(matcher.group(4));
                    envelope.setRange(0, longitude, longitude+1);
                    envelope.setRange(1, latitude, latitude+1);

                    grid = new GridGeometry2D(extent, envelope);
                } catch (IOException ex) {
                    throw new DataStoreException(ex.getMessage(), ex);
                }
            }
            return grid;
        }

        @Override
        public List<SampleDimension> getSampleDimensions() throws DataStoreException {
            return Collections.singletonList(SAMPLE_DIMENSION);
        }

        @Override
        protected GridCoverage readGridSlice(int[] areaLower, int[] areaUpper, int[] subsampling) throws DataStoreException {

            final GridGeometry allGridGeom = getGridGeometry();
            final GridGeometry gridGeometry = GeoReferencedGridCoverageReader.getGridGeometry(getGridGeometry(), areaLower, areaUpper, subsampling);
            final GridExtent extent = allGridGeom.getExtent();

            short[] allData;
            try (final ChannelImageInputStream imageStream = new ChannelImageInputStream(
                    null, Files.newByteChannel(fileInput), ByteBuffer.allocateDirect(8192), false)) {

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
