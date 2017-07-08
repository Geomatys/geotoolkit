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

import org.opengis.metadata.Metadata;
import org.opengis.parameter.ParameterValueGroup;
import org.opengis.util.FactoryException;

import org.apache.sis.storage.DataStoreException;
import org.geotoolkit.storage.DefaultDataSet;
import org.geotoolkit.storage.coverage.AbstractCoverageStore;
import org.geotoolkit.storage.coverage.CoverageStoreFactory;
import org.geotoolkit.storage.coverage.CoverageType;
import org.geotoolkit.util.NamesExt;
import org.geotoolkit.utility.parameter.ParametersExt;

import static org.geotoolkit.coverage.landsat.LandsatConstants.*;
import org.geotoolkit.storage.DataStores;
import org.geotoolkit.storage.Resource;

/**
 * Store adapted to Landsat 8 comportement.
 *
 * @author Remi Marechal (Geomatys)
 * @version 1.0
 * @since   1.0
 */
public class LandsatCoverageStore extends AbstractCoverageStore {

    private final DefaultDataSet root = new DefaultDataSet(NamesExt.create("root"));

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
     * @param path
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
        super(params);

        final Object uri = ParametersExt.getOrCreateValue(params, LandsatStoreFactory.PATH.getName().getCode()).getValue();
        final Path path;
        if (uri != null) {
            path = Paths.get((URI) uri);
        } else {
            throw new DataStoreException("Landsat8 store : path must be setted.");
        }

        //-- add 3 Coverage References : REFLECTIVE, PANCHROMATIQUE, THERMIC.
        metadataParser          = getMetadataParser(path);
        origin                  = metadataParser.getPath().getParent();
        final String sceneName  = getSceneName();

        final LandsatCoverageResource reflectiveRef = new LandsatCoverageResource(this, NamesExt.create(getDefaultNamespace(),
                                                                                    sceneName+"-"+REFLECTIVE_LABEL), origin, metadataParser);
        root.addResource(reflectiveRef);
        final LandsatCoverageResource panchroRef    = new LandsatCoverageResource(this, NamesExt.create(getDefaultNamespace(),
                                                                                    sceneName+"-"+PANCHROMATIC_LABEL), origin, metadataParser);
        root.addResource(panchroRef);

        final LandsatCoverageResource thermicRef    = new LandsatCoverageResource(this, NamesExt.create(getDefaultNamespace(),
                                                                                   sceneName+"-"+THERMAL_LABEL), origin, metadataParser);
        root.addResource(thermicRef);
    }

    /**
     * Set current URL into {@link ParameterValueGroup}.
     *
     * @param url Landsat 8 path.
     * @return
     */
    private static ParameterValueGroup toParameters(URI uri) {
        final ParameterValueGroup params = LandsatStoreFactory.PARAMETERS_DESCRIPTOR.createValue();
        ParametersExt.getOrCreateValue(params, LandsatStoreFactory.PATH.getName().getCode()).setValue(uri);
        return params;
    }

    /**
     * {@inheritDoc }.
     */
    @Override
    public Resource getRootResource() throws DataStoreException {
        return root;
    }

    /**
     * {@inheritDoc }.
     */
    @Override
    public CoverageStoreFactory getFactory() {
        return (CoverageStoreFactory) DataStores.getFactoryById(LandsatStoreFactory.NAME);
    }

    /**
     * {@inheritDoc }.
     */
    @Override
    public CoverageType getType() {
        return CoverageType.GRID;
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
            return metadataParser.getMetadata(GENERAL_LABEL);
        } catch (FactoryException | ParseException ex) {
            throw new DataStoreException(ex);
        }
    }


    //**************************************************************************//
    //********** added methods only effectives for Landsat utilisation *********//
    //**************************************************************************//

    /**
     * Returns part of {@linkplain #getMetadata() global Landsat 8 metadatas},
     * in relation with only Landsat 8 group name datas.
     * The valid group name are {@link LandsatConstants#GENERAL_LABEL},
     * {@link LandsatConstants#REFLECTIVE_LABEL},
     * {@link LandsatConstants#PANCHROMATIC_LABEL},
     * {@link LandsatConstants#THERMAL_LABEL}.
     *
     * @return Reflective Landsat 8 metadatas.
     * @throws DataStoreException
     */
    public Metadata getMetadata(String groupNameLabel) throws DataStoreException {
        try {
            return metadataParser.getMetadata(REFLECTIVE_LABEL);
        } catch (FactoryException | ParseException ex) {
            throw new DataStoreException(ex);
        }
    }

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
}
