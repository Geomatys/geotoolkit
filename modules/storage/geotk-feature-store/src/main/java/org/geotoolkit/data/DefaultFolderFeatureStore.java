/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2011, Geomatys
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
package org.geotoolkit.data;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.*;
import java.util.logging.Level;
import org.apache.sis.storage.DataStoreException;
import static org.geotoolkit.data.AbstractFileFeatureStoreFactory.*;
import static org.geotoolkit.data.AbstractFolderFeatureStoreFactory.*;
import org.geotoolkit.data.query.DefaultQueryCapabilities;
import org.geotoolkit.data.query.Query;
import org.geotoolkit.data.query.QueryCapabilities;
import org.geotoolkit.factory.Hints;
import org.geotoolkit.parameter.Parameters;
import org.geotoolkit.storage.DataFileStore;
import org.geotoolkit.version.VersionControl;
import org.geotoolkit.version.VersioningException;
import org.geotoolkit.feature.Feature;
import org.geotoolkit.feature.type.FeatureType;
import org.opengis.util.GenericName;
import org.geotoolkit.feature.type.PropertyDescriptor;
import org.opengis.filter.Filter;
import org.opengis.filter.identity.FeatureId;
import org.opengis.parameter.GeneralParameterDescriptor;
import org.opengis.parameter.ParameterDescriptor;
import org.opengis.parameter.ParameterDescriptorGroup;
import org.opengis.parameter.ParameterValueGroup;

/**
 * Handle a folder of single file FeatureStore.
 *
 * @author Johann Sorel (Geomatys)
 * @author Cédric Briançon (Geomatys)
 * @module pending
 */
public class DefaultFolderFeatureStore extends AbstractFeatureStore implements DataFileStore {

    /**
     * Listen to changes in sub stores and propagate them.
     */
    private final FeatureStoreListener subListener = new FeatureStoreListener() {

        @Override
        public void structureChanged(FeatureStoreManagementEvent event) {
            event = event.copy(DefaultFolderFeatureStore.this);
            sendStructureEvent(event);
        }

        @Override
        public void contentChanged(FeatureStoreContentEvent event) {
            event = event.copy(DefaultFolderFeatureStore.this);
            sendContentEvent(event);
        }
    };

    private final ParameterValueGroup folderParameters;
    private final AbstractFolderFeatureStoreFactory folderFactory;
    private final FileFeatureStoreFactory singleFileFactory;
    private final ParameterValueGroup singleFileDefaultParameters;
    private Map<GenericName,FeatureStore> stores = null;

    public DefaultFolderFeatureStore(final ParameterValueGroup params, final AbstractFolderFeatureStoreFactory factory){
        super(params);
        this.folderParameters = params;
        this.folderFactory = factory;
        this.singleFileFactory = this.folderFactory.getSingleFileFactory();

        final ParameterDescriptorGroup desc = singleFileFactory.getParametersDescriptor();
        singleFileDefaultParameters = desc.createValue();
        for(GeneralParameterDescriptor pdesc : desc.descriptors()){
            if(pdesc == PATH || pdesc.getName().getCode().equals(IDENTIFIER.getName().getCode())) {
                continue;
            }
            Parameters.getOrCreate((ParameterDescriptor)pdesc, singleFileDefaultParameters)
                    .setValue(folderParameters.parameter(pdesc.getName().getCode()).getValue());
        }

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public FeatureStoreFactory getFactory() {
        return folderFactory;
    }

    /**
     * Fallthrought to sub feature stores.
     */
    @Override
    public VersionControl getVersioning(GenericName typeName) throws VersioningException {
        try {
            typeCheck(typeName);
        } catch (DataStoreException ex) {
            throw new VersioningException(ex);
        }
        final FeatureStore store = stores.get(typeName);
        return store.getVersioning(typeName);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public synchronized Set<GenericName> getNames() throws DataStoreException {

        if(stores == null){
            this.stores = new HashMap<>();
            final Path folder = getFolder(folderParameters);

            if(!Files.exists(folder)){
                try{
                    Files.createDirectory(folder);
                }catch(IOException | SecurityException ex){
                    throw new DataStoreException(ex.getMessage(), ex);
                }
            }

            Boolean recursive = Parameters.value(RECURSIVE, folderParameters);
            if (recursive == null) {
                recursive = RECURSIVE.getDefaultValue();
            }

            try {
                if (recursive) {
                    Files.walkFileTree(folder, new SimpleFileVisitor<Path>() {
                        @Override
                        public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                            testFile(file);
                            return FileVisitResult.CONTINUE;
                        }

                        @Override
                        public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
                            if (Files.isHidden(dir) || Files.isSymbolicLink(dir)) {
                                return FileVisitResult.SKIP_SUBTREE;
                            }
                            return FileVisitResult.CONTINUE;
                        }
                    });
                } else {
                    try (DirectoryStream<Path> directoryStream = Files.newDirectoryStream(folder)) {
                        for (Path path : directoryStream) {
                            testFile(path);
                        }
                    }
                }
            } catch (IOException ex) {
                getLogger().log(Level.WARNING, ex.getLocalizedMessage(), ex);
            }
        }

        return stores.keySet();
    }

    private void testFile(Path file) throws IOException {
        if (Files.isHidden(file) || Files.isSymbolicLink(file)) {
            return;//skip hidden and sym link files
        }

        final ParameterValueGroup params = singleFileDefaultParameters.clone();
        try {
            Parameters.getOrCreate(PATH, params).setValue(file.toUri().toURL());
        } catch (MalformedURLException ex) {
            getLogger().log(Level.FINE, ex.getLocalizedMessage(), ex);
        }
        if (singleFileFactory.canProcess(params)) {
            try {
                final FeatureStore fileDS = (FeatureStore) singleFileFactory.open(params);
                fileDS.addStorageListener(subListener);
                stores.put(fileDS.getNames().iterator().next(), fileDS);
            } catch (DataStoreException ex) {
                getLogger().log(Level.WARNING, ex.getLocalizedMessage(), ex);
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void createFeatureType(final GenericName typeName, final FeatureType featureType) throws DataStoreException {

        if(typeName == null){
            throw new DataStoreException("Type name can not be null.");
        }

        if(!featureType.getName().equals(typeName)){
            throw new DataStoreException("Type name must match feature type name.");
        }

        if(getNames().contains(typeName)){
            throw new DataStoreException("Type name "+ typeName + " already exists.");
        }

        final ParameterValueGroup params = singleFileDefaultParameters.clone();
        try {
            final Path folder = getFolder(folderParameters);
            final String fileName = typeName.tip().toString() + singleFileFactory.getFileExtensions()[0];
            final Path newFile = folder.resolve(fileName);
            Parameters.getOrCreate(PATH, params).setValue(newFile.toUri().toURL());
        } catch (MalformedURLException ex) {
            throw new DataStoreException(ex);
        }

        final FeatureStore store = (FeatureStore) singleFileFactory.create(params);
        store.addStorageListener(subListener);
        store.createFeatureType(typeName, featureType);
        stores.put(typeName, store);
    }

    /**
     * Unsupported, throws a {@link DataStoreException}.
     */
    @Override
    public void updateFeatureType(final GenericName typeName, final FeatureType featureType) throws DataStoreException {
        throw new DataStoreException("Not supported.");
    }

    /**
     * Unsupported, throws a {@link DataStoreException}.
     */
    @Override
    public synchronized void deleteFeatureType(final GenericName typeName) throws DataStoreException {
        final FeatureStore store = stores.get(typeName);
        if (store == null) {
            throw new DataStoreException("There's no data with the following type name : "+typeName);
        }
        // We should get a file feature store.
        final Path[] sourceFiles;
        try {
            if (store instanceof DataFileStore) {
                sourceFiles = ((DataFileStore) store).getDataFiles();
            } else {
                // Not a file store ? We try to find an url parameter and see if it's a file one.
                final URI fileURI = Parameters.value(PATH, store.getConfiguration());
                if (fileURI == null) {
                    throw new DataStoreException("Source data cannot be reached for type name : " + typeName);
                }
                sourceFiles = new Path[]{Paths.get(fileURI)};
            }

            for (Path path : sourceFiles) {
                Files.deleteIfExists(path);
            }
        } catch (IOException e) {
            throw new DataStoreException("Source data cannot be deleted for type name : " + typeName, e);
        }

        stores.remove(typeName);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public FeatureType getFeatureType(final GenericName typeName) throws DataStoreException {
        typeCheck(typeName);
        final FeatureStore store = stores.get(typeName);
        return store.getFeatureType(typeName);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isWritable(final GenericName typeName) throws DataStoreException {
        typeCheck(typeName);
        final FeatureStore store = stores.get(typeName);
        return store.isWritable(typeName);
    }

    /**
     * Unsupported, throws a {@link DataStoreException}.
     */
    @Override
    public QueryCapabilities getQueryCapabilities() {
        return new DefaultQueryCapabilities(false, false);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<FeatureId> addFeatures(final GenericName groupName, final Collection<? extends Feature> newFeatures,
            final Hints hints) throws DataStoreException {
        typeCheck(groupName);
        final FeatureStore store = stores.get(groupName);
        return store.addFeatures(groupName, newFeatures);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void updateFeatures(final GenericName groupName, final Filter filter,
            final Map<? extends PropertyDescriptor, ? extends Object> values) throws DataStoreException {
        typeCheck(groupName);
        final FeatureStore store = stores.get(groupName);
        store.updateFeatures(groupName, filter, values);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void removeFeatures(final GenericName groupName, final Filter filter) throws DataStoreException {
        typeCheck(groupName);
        final FeatureStore store = stores.get(groupName);
        store.removeFeatures(groupName, filter);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public FeatureReader getFeatureReader(final Query query) throws DataStoreException {
        final GenericName name = query.getTypeName();
        typeCheck(name);
        final FeatureStore store = stores.get(name);
        return store.getFeatureReader(query);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public FeatureWriter getFeatureWriter(final GenericName typeName, final Filter filter, final Hints hints) throws DataStoreException {
        typeCheck(typeName);
        final FeatureStore store = stores.get(typeName);
        return store.getFeatureWriter(typeName, filter, hints);
    }

    private Path getFolder(final ParameterValueGroup params) throws DataStoreException{
        final URI uri = Parameters.value(FOLDER_PATH, params);

        try {
            return Paths.get(uri);
        } catch (FileSystemNotFoundException | IllegalArgumentException e) {
            throw new DataStoreException(e);
        }
    }

    /**
     * {@inheritDoc}
     */
    public Path[] getDataFiles() throws DataStoreException {
        final Path folder = getFolder(folderParameters);
        return new Path[]{ folder };
    }

    @Override
    public void refreshMetaModel() {
        stores=null;
    }

}
