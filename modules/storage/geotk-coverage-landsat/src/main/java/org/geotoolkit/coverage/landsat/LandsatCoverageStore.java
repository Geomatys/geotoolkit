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
import java.net.URL;
import java.nio.file.FileSystems;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.text.ParseException;
import org.apache.sis.storage.DataStoreException;
import org.geotoolkit.storage.DataNode;
import org.geotoolkit.storage.DefaultDataNode;
import org.geotoolkit.storage.coverage.AbstractCoverageStore;
import org.geotoolkit.storage.coverage.CoverageStoreFactory;
import org.geotoolkit.storage.coverage.CoverageStoreFinder;
import org.geotoolkit.storage.coverage.CoverageType;
import org.geotoolkit.util.NamesExt;
import org.geotoolkit.utility.parameter.ParametersExt;
import org.opengis.metadata.Metadata;
import org.opengis.parameter.ParameterValueGroup;
import org.opengis.util.FactoryException;
import static org.geotoolkit.coverage.landsat.LandsatConstants.*;

/**
 * Store adapted to Landsat 8 comportement.
 *
 * @author Remi Marechal (Geomatys)
 * @version 1.0
 * @since 1.0
 */
public class LandsatCoverageStore extends AbstractCoverageStore {

    private final DataNode root = new DefaultDataNode();

    /**
     * The current parent landsat8 directory.
     */
    private final Path origin;

    /**
     * landsat8 matadata file.
     */
    private Path metadataPath;

    /**
     * Parset to convert file metadata into {@link Metadata}.
     *
     * @see LandsatMetadataParser
     */
    private final LandsatMetadataParser metadataParser;

    /**
     *
     * @param path
     * @throws DataStoreException
     */
    public LandsatCoverageStore(URL path) throws DataStoreException {
        this(toParameters(path));
    }

    /**
     * Build Landsat Coverage store from params.<br>
     *
     * Params must contain path to find data and metadata path.
     *
     * @param params
     * @throws DataStoreException
     */
    public LandsatCoverageStore(ParameterValueGroup params) throws DataStoreException {
        super(params);

        final URL url = (URL) ParametersExt.getOrCreateValue(params, LandsatStoreFactory.PATH.getName().getCode()).getValue();

        final Path path = FileSystems.getDefault().getPath(url.getFile());
        if (Files.isDirectory(path)) {
            origin = path;
        } else {
            origin = path.getParent();
        }

        try {
            Files.walkFileTree(origin, new LandsatVisitor());
        } catch (IOException ex) {
            throw new DataStoreException(ex);
        }

        if (metadataPath == null){
            throw new DataStoreException("No Landsat Metadata file found.");
        }

        //-- add 3 Coverage References : REFLECTIVE, PANCHROMATIQUE, THERMIC.
        try {
            metadataParser = new LandsatMetadataParser(metadataPath);
            final LandsatCoverageReference reflectiveRef = new LandsatCoverageReference(this, NamesExt.create(getDefaultNamespace(),
                                                                                        REFLECTIVE_LABEL), origin, metadataPath);
            root.getChildren().add(reflectiveRef);
            final LandsatCoverageReference panchroRef    = new LandsatCoverageReference(this, NamesExt.create(getDefaultNamespace(),
                                                                                        PANCHROMATIC_LABEL), origin, metadataPath);
            root.getChildren().add(panchroRef);

            final LandsatCoverageReference thermicRef    = new LandsatCoverageReference(this, NamesExt.create(getDefaultNamespace(),
                                                                                        THERMAL_LABEL), origin, metadataPath);
            root.getChildren().add(thermicRef);

        } catch (IOException ex) {
            throw new DataStoreException(ex);
        }
    }

    /**
     * Set current URL into {@link ParameterValueGroup}.
     *
     * @param url Landsat 8 path.
     * @return
     */
    private static ParameterValueGroup toParameters(URL url) {
        final ParameterValueGroup params = LandsatStoreFactory.PARAMETERS_DESCRIPTOR.createValue();
        ParametersExt.getOrCreateValue(params, LandsatStoreFactory.PATH.getName().getCode()).setValue(url);
        return params;
    }

    /**
     * {@inheritDoc }.
     */
    @Override
    public DataNode getRootNode() throws DataStoreException {
        return root;
    }

    /**
     * {@inheritDoc }.
     */
    @Override
    public CoverageStoreFactory getFactory() {
        return CoverageStoreFinder.getFactoryById(LandsatStoreFactory.NAME);
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
     * Define criterion to select the current Landsat visited metadata file.
     *
     */
    private class LandsatVisitor extends SimpleFileVisitor<Path>{

        /**
         * Returns {@link FileVisitResult.TERMINATE} if the current traveled file is an appropriate Landsat metadata file.
         * Also assigne the correct {@link Path} to {@link #metadataPath}.
         *
         * @param file
         * @param attrs
         * @return
         * @throws IOException
         */
        @Override
        public FileVisitResult visitFile(final Path file, final BasicFileAttributes attrs) throws IOException {
            final String name = file.getFileName().toString();
            final int index = name.lastIndexOf('.');
            if (index >= 0) {
                final String suffix = name.substring(index+1).toLowerCase();
                if ("txt".equals(suffix)) {
                    metadataPath = file;
                    //-- return terminate if the file is valid.
                    if (new LandsatMetadataParser(metadataPath).isValid())
                        return FileVisitResult.TERMINATE;
                    else
                        metadataPath = null;
                }
            }
            return super.visitFile(file, attrs);
        }
    }
}
