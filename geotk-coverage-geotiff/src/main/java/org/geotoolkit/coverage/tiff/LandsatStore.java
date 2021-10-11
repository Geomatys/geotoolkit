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
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.apache.sis.internal.storage.ResourceOnFileSystem;
import org.apache.sis.storage.Aggregate;
import org.apache.sis.storage.DataStoreException;
import org.apache.sis.storage.DataStoreProvider;

import static org.geotoolkit.coverage.tiff.LandsatConstants.*;
import org.geotoolkit.coverage.tiff.LandsatConstants.CoverageGroup;
import org.geotoolkit.storage.DataStores;
import org.apache.sis.storage.Resource;
import org.apache.sis.storage.StorageConnector;

/**
 * Store adapted to Landsat 8 files structure.
 *
 * @author Remi Marechal (Geomatys)
 * @author Johann Sorel (Geomatys)
 */
public class LandsatStore extends org.apache.sis.storage.earthobservation.LandsatStore implements Aggregate, ResourceOnFileSystem {

    private final List<Resource> resources = new ArrayList<>();

    /**
     * Parser to convert file metadata.
     */
    private final LandsatMetadataParser metadataParser;

    /**
     * DirectoryStream.Filter on metadata files.
     */
    private static final DirectoryStream.Filter<Path> METADATA_FILTER = new DirectoryStream.Filter<Path>() {
        @Override
        public boolean accept(Path entry) throws IOException {
            return entry.getFileName().toString().toLowerCase().endsWith("mtl.txt");
        }
    };

    public LandsatStore(final StorageConnector connector) throws DataStoreException {
        this(connector, connector.getStorageAs(Path.class));
    }
    /**
     * Build Landsat Coverage store.
     */
    private LandsatStore(final StorageConnector connector, Path path) throws DataStoreException {
        super(null, connector);
        metadataParser = getMetadataParser(path);
        final Path origin = metadataParser.getPath().getParent();

        final LandsatResource reflectiveRef = new LandsatResource(this, origin, metadataParser, CoverageGroup.REFLECTIVE);
        resources.add(reflectiveRef);
        final LandsatResource panchroRef    = new LandsatResource(this, origin, metadataParser, CoverageGroup.PANCHROMATIC);
        resources.add(panchroRef);
        final LandsatResource thermicRef    = new LandsatResource(this, origin, metadataParser, CoverageGroup.THERMAL);
        resources.add(thermicRef);
    }

    @Override
    public List components() throws DataStoreException {
        return Collections.unmodifiableList(resources);
    }

    @Override
    public Path[] getComponentFiles() throws DataStoreException {
        final Set<Path> paths = new HashSet<>();
        paths.add(metadataParser.getPath());
        for (org.apache.sis.storage.Resource r : DataStores.flatten(this, false)) {
            if (r instanceof ResourceOnFileSystem) {
                paths.addAll(Arrays.asList(((ResourceOnFileSystem) r).getComponentFiles()));
            }
        }
        return paths.toArray(new Path[paths.size()]);
    }

    //**************************************************************************//
    //********** added methods only effectives for Landsat utilisation *********//
    //**************************************************************************//

    /**
     * Returns Landsat datas name from theirs metadatas.
     * May return null if the Landsat scene doesn't own name.
     *
     * @return Landsat metadata name if exist, else return {@code null}.
     */
    public String getSceneName() {
        return metadataParser.getValue(false, SCENE_ID);
    }

    /**
     * Search into current directory or file stipulate by path and return {@link LandsatMetadataParser}.
     *
     * @param landsatPath
     * @return the found Landsat metadata path.
     * @throws DataStoreException if impossible to found metadata file.
     */
    private static LandsatMetadataParser getMetadataParser(final Path landsatPath) throws DataStoreException {
        try {
            if (Files.isDirectory(landsatPath)) {
                //search metadata file
                try (DirectoryStream<Path> stream = Files.newDirectoryStream(landsatPath, METADATA_FILTER)) {
                    for (Path candidate : stream) {
                        //will throw IOException if metadata not valid
                        return createMetadataParser(candidate);
                    }
                }
            } else {
                return createMetadataParser(landsatPath);
            }
        } catch (IOException ex) {
            throw new DataStoreException(ex);
        }
        throw new DataStoreException("Impossible to find Metadata file for Landsat from Path : "+landsatPath);
    }

    /**
     * Returns {@link LandsatMetadataParser} if given path is conform to be a Landsat metadata path (should finish by "_MTL.txt")
     * and also check if the metadata file {@linkplain LandsatMetadataParser#isValid() is valid}.
     *
     * @param file studied metadata file
     * @param isMandatory if {@link Path} is not conform and {@code true} then throw Exception whereas {@code false} will return {@code null}.
     * @return {@link LandsatMetadataParser} if possible.
     * @throws IOException if problem during parsing metadata.
     * @throws DataStoreException if appropriate Landsat metadata file
     */
    private static LandsatMetadataParser createMetadataParser(final Path file) throws IOException, DataStoreException {
        if (METADATA_FILTER.accept(file)) {
            //-- return terminate if the file is valid.
            final LandsatMetadataParser parser = new LandsatMetadataParser(file);
            if (parser.isValid())
                return parser;
        }
        throw new DataStoreException("Invalid metadata file :"+file.toString());
    }

    @Override
    public DataStoreProvider getProvider() {
        return DataStores.getProviderById(LandsatProvider.NAME);
    }
}
