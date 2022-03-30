/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2019, Geomatys
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

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;
import org.apache.sis.coverage.SampleDimension;
import org.apache.sis.coverage.grid.GridCoverage;
import org.apache.sis.coverage.grid.GridGeometry;
import org.apache.sis.storage.AbstractGridCoverageResource;
import org.apache.sis.internal.storage.ResourceOnFileSystem;
import org.apache.sis.parameter.Parameters;
import org.apache.sis.storage.DataStore;
import org.apache.sis.storage.DataStoreException;
import org.apache.sis.storage.DataStoreProvider;
import org.apache.sis.storage.GridCoverageResource;
import org.apache.sis.storage.Query;
import org.apache.sis.storage.UnsupportedQueryException;
import org.apache.sis.storage.WritableGridCoverageResource;
import org.geotoolkit.coverage.io.GridCoverageReadParam;
import org.geotoolkit.coverage.io.ImageCoverageReader;
import org.geotoolkit.coverage.io.ImageCoverageWriter;
import org.geotoolkit.image.io.plugin.TiffImageReader;
import org.geotoolkit.image.io.plugin.TiffImageWriteParam;
import org.geotoolkit.image.io.plugin.TiffImageWriter;
import org.geotoolkit.nio.IOUtilities;
import org.geotoolkit.storage.DataStores;
import org.geotoolkit.util.NamesExt;
import org.opengis.geometry.Envelope;
import org.opengis.metadata.Metadata;
import org.opengis.parameter.ParameterValueGroup;
import org.opengis.util.GenericName;

/**
 * This is a temporary provider until geotiff is moved to Apache SIS.
 *
 * @author Johann Sorel (Geomatys)
 */
final class TiffStore extends DataStore implements ResourceOnFileSystem, WritableGridCoverageResource {

    private final Path path;
    private final Resource resource = new Resource();

    TiffStore(Path path) {
        this.path = path;
    }

    @Override
    public DataStoreProvider getProvider() {
        return DataStores.getProviderById(TiffProvider.NAME);
    }

    @Override
    public Optional<ParameterValueGroup> getOpenParameters() {
        final Parameters parameters = Parameters.castOrWrap(TiffProvider.PARAMETERS_DESCRIPTOR.createValue());
        parameters.getOrCreate(TiffProvider.PATH).setValue(path.toUri());
        return Optional.of(parameters);
    }

    @Override
    public Optional<GenericName> getIdentifier() throws DataStoreException {
        return resource.getIdentifier();
    }

    @Override
    public Metadata getMetadata() throws DataStoreException {
        return resource.getMetadata();
    }

    @Override
    public Path[] getComponentFiles() throws DataStoreException {
        return new Path[]{path};
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
    public GridCoverageResource subset(Query query) throws UnsupportedQueryException, DataStoreException {
        return resource.subset(query);
    }

    @Override
    public Optional<Envelope> getEnvelope() throws DataStoreException {
        return resource.getEnvelope();
    }

    @Override
    public void write(GridCoverage coverage, Option... options) throws DataStoreException {
        resource.write(coverage, options);
    }


    private final class Resource extends AbstractGridCoverageResource implements WritableGridCoverageResource {

        //caches
        private List<SampleDimension> sampleDimensions;
        private GridGeometry gridGeometry;


        private Resource() {
            super(null);
        }

        @Override
        public Optional<GenericName> getIdentifier() throws DataStoreException {
            //create identifier with file name
            String name = IOUtilities.filenameWithoutExtension(path);
            return Optional.of(NamesExt.create(name));
        }

        @Override
        public synchronized GridGeometry getGridGeometry() throws DataStoreException {
            initMetas();
            return gridGeometry;
        }

        @Override
        public synchronized List<SampleDimension> getSampleDimensions() throws DataStoreException {
            initMetas();
            return sampleDimensions;
        }

        @Override
        public GridCoverage read(GridGeometry domain, int... range) throws DataStoreException {
            final GridCoverageReadParam param = new GridCoverageReadParam();
            if (range != null && range.length > 0) {
                param.setSourceBands(range);
                param.setDestinationBands(IntStream.range(0, range.length).toArray());
            }

            if (domain != null && domain.isDefined(org.apache.sis.coverage.grid.GridGeometry.ENVELOPE)) {
                param.setEnvelope(domain.getEnvelope());
            }
            if (domain != null && domain.isDefined(GridGeometry.RESOLUTION)) {
                param.setResolution(domain.getResolution(true));
            }
            ImageCoverageReader reader = acquireReader();
            try {
                return reader.read(param);
            } finally {
                reader.dispose();
            }
        }

        @Override
        public void write(GridCoverage coverage, Option... options) throws DataStoreException {
            final TiffImageWriter tiffwriter = new TiffImageWriter(new TiffImageWriter.Spi());
            tiffwriter.setOutput(path);

            // TODO pass parameters down to writer
            TiffImageWriteParam params = new TiffImageWriteParam(tiffwriter);

            final ImageCoverageWriter writer = new ImageCoverageWriter();
            writer.setOutput( tiffwriter );
            try {
                writer.write(coverage, null);
            } finally {
                sampleDimensions = null;
                gridGeometry = null;
                writer.dispose();
            }
        }

        private synchronized void initMetas() throws DataStoreException {
            if (sampleDimensions != null) return;

            if (Files.exists(path)) {
                ImageCoverageReader reader = acquireReader();
                try {
                    sampleDimensions = reader.getSampleDimensions();
                    gridGeometry = reader.getGridGeometry();
                } finally {
                    reader.dispose();
                }
            } else {
                throw new DataStoreException("File "+path.toUri().toString()+" does not exist yet, write a coverage first");
            }
        }

        private ImageCoverageReader acquireReader() throws DataStoreException {
            final TiffImageReader tiffreader = new TiffImageReader(new TiffImageReader.Spi());
            tiffreader.setInput(path);
            final ImageCoverageReader reader = new ImageCoverageReader();
            reader.setInput(tiffreader);
            return reader;
        }

    }

}
