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
import java.net.URI;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.apache.sis.internal.storage.ResourceOnFileSystem;
import org.apache.sis.parameter.Parameters;
import org.apache.sis.storage.Aggregate;
import org.apache.sis.storage.DataStore;
import org.apache.sis.storage.DataStoreException;
import org.apache.sis.storage.DataStoreProvider;
import org.apache.sis.storage.event.ChangeEvent;
import org.apache.sis.storage.event.ChangeListener;
import static org.geotoolkit.coverage.landsat.LandsatConstants.*;
import org.geotoolkit.coverage.landsat.LandsatConstants.CoverageGroup;
import org.geotoolkit.storage.DataStores;
import org.geotoolkit.storage.Resource;
import org.opengis.metadata.Metadata;
import org.opengis.parameter.ParameterValueGroup;
import org.opengis.util.FactoryException;
import org.opengis.util.GenericName;

/**
 * Store adapted to Landsat 8 files structure.
 *
 * @author Remi Marechal (Geomatys)
 * @author Johann Sorel (Geomatys)
 * @version 1.0
 * @since   1.0
 */
public class LandsatCoverageStore extends DataStore implements Aggregate, ResourceOnFileSystem {

    private final ParameterValueGroup params;
    private final List<Resource> resources = new ArrayList<>();

    /**
     * The current parent landsat8 directory.
     */
    private final Path origin;

    /**
     * Parset to convert file metadata into {@link Metadata}.
     *
     * @see LandsatMetadataParser
     */
    private final LandsatMetadataParser metadataParser;

    /**
    * DirectoryStream.Filter on metadata files.
    */
    public static final DirectoryStream.Filter<Path> METADATA_FILTER = new DirectoryStream.Filter<Path>() {
        @Override
        public boolean accept(Path entry) throws IOException {
            return entry.getFileName().toString().toLowerCase().endsWith("mtl.txt");
        }
    };

    /**
     *
     * @param path
     * @throws DataStoreException
     */
    public LandsatCoverageStore(Path path) throws DataStoreException {
        this(toParameters(path.toUri()));
    }

    /**
     *
     * @param uri
     * @throws DataStoreException
     */
    public LandsatCoverageStore(URI uri) throws DataStoreException {
        this(toParameters(uri));
    }

    /**
     * Build Landsat Coverage store from params.<br>
     *
     * Params must contain path to find data and metadata path.
     *
     * @param params
     * @throws DataStoreException
     */
    public LandsatCoverageStore(ParameterValueGroup params)
            throws DataStoreException {
        this.params = params;

        final Object uri = Parameters.castOrWrap(params).getValue(LandsatProvider.PATH);
        final Path path;
        if (uri != null) {
            path = Paths.get((URI) uri);
        } else {
            throw new DataStoreException("Landsat8 store : path must be setted.");
        }

        //-- add 3 Coverage References : REFLECTIVE, PANCHROMATIQUE, THERMIC.
        metadataParser          = getMetadataParser(path);
        origin                  = metadataParser.getPath().getParent();

        final LandsatCoverageResource reflectiveRef = new LandsatCoverageResource(this, origin, metadataParser, CoverageGroup.REFLECTIVE);
        resources.add(reflectiveRef);
        final LandsatCoverageResource panchroRef    = new LandsatCoverageResource(this, origin, metadataParser, CoverageGroup.PANCHROMATIC);
        resources.add(panchroRef);
        final LandsatCoverageResource thermicRef    = new LandsatCoverageResource(this, origin, metadataParser, CoverageGroup.THERMAL);
        resources.add(thermicRef);
    }

    /**
     * Set current URL into {@link ParameterValueGroup}.
     *
     * @param url Landsat 8 path.
     * @return
     */
    private static ParameterValueGroup toParameters(URI uri) {
        final Parameters params = Parameters.castOrWrap(LandsatProvider.PARAMETERS_DESCRIPTOR.createValue());
        params.getOrCreate(LandsatProvider.PATH).setValue(uri);
        return params;
    }

    @Override
    public ParameterValueGroup getOpenParameters() {
        return params;
    }

    @Override
    public Collection<org.apache.sis.storage.Resource> components() throws DataStoreException {
        return Collections.unmodifiableList(resources);
    }

    /**
     * {@inheritDoc }.
     */
    @Override
    public DataStoreProvider getProvider() {
        return DataStores.getProviderById(LandsatProvider.NAME);
    }

    @Override
    public GenericName getIdentifier() {
        return null;
    }

    /**
     * {@inheritDoc }.
     */
    @Override
    public void close() throws DataStoreException {
        //-- close nothing
    }

    /**
     * {@inheritDoc }
     * Moreover, the returned metadata are the globales metadatas for Landsat.
     * {@link #getMetadata(java.lang.String) } with the groupname {@link LandsatConstants#GENERAL_LABEL}.
     *
     * @return Metadata for all Landsat8 coverage types.
     * @throws DataStoreException
     */
    @Override
    public Metadata getMetadata() throws DataStoreException {
        try {
            return metadataParser.getMetadata(CoverageGroup.ALL);
        } catch (FactoryException | ParseException ex) {
            throw new DataStoreException(ex);
        }
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
    private LandsatMetadataParser getMetadataParser(final Path landsatPath)
            throws DataStoreException {
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
    private LandsatMetadataParser createMetadataParser(final Path file) throws IOException, DataStoreException {
        if (METADATA_FILTER.accept(file)) {
            //-- return terminate if the file is valid.
            final LandsatMetadataParser parser = new LandsatMetadataParser(file);
            if (parser.isValid())
                return parser;
        }
        throw new DataStoreException("Invalid metadata file :"+file.toString());
    }

    @Override
    public <T extends ChangeEvent> void addListener(ChangeListener<? super T> listener, Class<T> eventType) {
    }

    @Override
    public <T extends ChangeEvent> void removeListener(ChangeListener<? super T> listener, Class<T> eventType) {
    }
}
