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

import java.io.IOException;
import java.nio.file.Path;
import java.text.ParseException;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.apache.sis.coverage.SampleDimension;
import org.apache.sis.coverage.grid.GridCoverage;
import org.apache.sis.coverage.grid.GridGeometry;
import org.apache.sis.internal.storage.ResourceOnFileSystem;
import org.apache.sis.internal.storage.StoreResource;
import org.apache.sis.metadata.iso.DefaultMetadata;
import org.apache.sis.storage.DataStore;
import org.apache.sis.storage.DataStoreException;
import org.apache.sis.storage.GridCoverageResource;
import org.apache.sis.storage.event.StoreListeners;
import org.geotoolkit.coverage.io.CoverageStoreException;
import org.geotoolkit.coverage.io.GridCoverageReadParam;
import org.geotoolkit.coverage.io.GridCoverageReader;
import static org.geotoolkit.coverage.tiff.LandsatConstants.*;
import org.geotoolkit.coverage.tiff.LandsatConstants.CoverageGroup;
import org.opengis.geometry.Envelope;
import org.opengis.util.FactoryException;
import org.opengis.util.GenericName;

/**
 * Reader adapted to read and aggregate directly needed bands to build appropriate
 * REFLECTIVE, THERMIC, or PANCHROMATIC Landsat part.
 *
 * @author Remi Marechal (Geomatys)
 * @version 1.0
 * @since   1.0
 */
final class LandsatResource extends StoreListeners implements GridCoverageResource, ResourceOnFileSystem, StoreResource {

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
        super(null, null);
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
    public GridGeometry getGridGeometry() throws DataStoreException {
        final GridCoverageReader reader = acquireReader();
        try {
            return reader.getGridGeometry();
        } finally {
            reader.dispose();
        }
    }

    @Override
    public List<SampleDimension> getSampleDimensions() throws DataStoreException {
        final GridCoverageReader reader = acquireReader();
        try {
            return reader.getSampleDimensions();
        } finally {
            reader.dispose();
        }
    }

    @Override
    public DefaultMetadata getMetadata() throws DataStoreException {
        try {
            return metadataParser.getMetadata(group);
        } catch (FactoryException ex) {
            throw new DataStoreException(ex.getMessage(), ex);
        } catch (ParseException ex) {
            throw new DataStoreException(ex.getMessage(), ex);
        }
    }

    public GridCoverageReader acquireReader() throws CoverageStoreException {
        try {
            return new LandsatReader(this, parentDirectory, metadataParser, group);
        } catch (IOException ex) {
            throw new CoverageStoreException(ex);
        }
    }

    @Override
    public GridCoverage read(GridGeometry domain, int... range) throws DataStoreException {
        final GridCoverageReadParam param = new GridCoverageReadParam();
        if (range != null && range.length > 0) {
            param.setSourceBands(range);
            param.setDestinationBands(range);
        }

        if (domain != null && domain.isDefined(org.apache.sis.coverage.grid.GridGeometry.ENVELOPE)) {
            param.setEnvelope(domain.getEnvelope());
        }
        if (domain != null && domain.isDefined(GridGeometry.RESOLUTION)) {
            param.setResolution(domain.getResolution(true));
        }
        GridCoverageReader reader = acquireReader();
        try {
            return reader.read(param);
        } finally {
            reader.dispose();
        }
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
}
