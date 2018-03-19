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

import java.awt.Image;
import java.io.IOException;
import java.nio.file.Path;
import java.text.ParseException;
import java.util.HashSet;
import java.util.Set;
import org.apache.sis.internal.storage.ResourceOnFileSystem;
import org.apache.sis.metadata.iso.DefaultMetadata;
import org.apache.sis.storage.DataStoreException;
import org.geotoolkit.coverage.io.CoverageStoreException;
import org.geotoolkit.coverage.io.GridCoverageReader;
import org.geotoolkit.coverage.io.GridCoverageWriter;
import org.geotoolkit.storage.coverage.AbstractCoverageResource;
import org.opengis.util.FactoryException;
import org.opengis.util.GenericName;
import static org.geotoolkit.coverage.landsat.LandsatConstants.*;

/**
 * Reader adapted to read and aggregate directly needed bands to build appropriate
 * REFLECTIVE, THERMIC, or PANCHROMATIC Landsat part.
 *
 * @author Remi Marechal (Geomatys)
 * @version 1.0
 * @since   1.0
 */
public class LandsatCoverageResource extends AbstractCoverageResource implements ResourceOnFileSystem {

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
    public LandsatCoverageResource(final LandsatCoverageStore store, final Path parentDirectory,
                final LandsatMetadataParser metadataParser, final CoverageGroup group) {
        super(store, group.createName(store.getSceneName()));
        this.parentDirectory = parentDirectory;
        this.metadataParser = metadataParser;
        this.group = group;
    }

    /**
     * {@inheritDoc }
     *
     * @return 0.
     */
    @Override
    public int getImageIndex() {
        return 0;
    }

    @Override
    protected DefaultMetadata createMetadata() throws DataStoreException {
        try {
            return metadataParser.getMetadata(group);
        } catch (FactoryException ex) {
            throw new DataStoreException(ex.getMessage(), ex);
        } catch (ParseException ex) {
            throw new DataStoreException(ex.getMessage(), ex);
        }
    }

    /**
     * {@inheritDoc }
     *
     * @return always return false, no Landsat 8 writer.
     * @throws DataStoreException
     */
    @Override
    public boolean isWritable() throws DataStoreException {
        return false;
    }

    @Override
    public GridCoverageReader acquireReader() throws CoverageStoreException {
        try {
            return new LandsatReader(parentDirectory, metadataParser, group);
        } catch (IOException ex) {
            throw new CoverageStoreException(ex);
        }
    }

    /**
     * {@inheritDoc }
     *
     * Throw an exception.<br>
     * Landsat 8 writer does not supported.
     *
     * @return
     * @throws CoverageStoreException
     */
    @Override
    public GridCoverageWriter acquireWriter() throws CoverageStoreException {
        throw new CoverageStoreException("Not supported.");
    }

    /**
     * {@inheritDoc }
     * @return
     * @throws org.apache.sis.storage.DataStoreException
     */
    @Override
    public Image getLegend() throws DataStoreException {
        return null;
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
