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
package org.geotoolkit.storage.feature;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.*;
import java.util.logging.Level;
import org.apache.sis.internal.storage.ResourceOnFileSystem;
import org.apache.sis.parameter.Parameters;
import org.apache.sis.storage.DataStoreException;
import org.apache.sis.storage.IllegalNameException;
import org.apache.sis.storage.Query;
import org.apache.sis.storage.UnsupportedQueryException;
import org.apache.sis.storage.event.StoreEvent;
import org.apache.sis.storage.event.StoreListener;
import static org.geotoolkit.storage.feature.AbstractFileFeatureStoreFactory.*;
import static org.geotoolkit.storage.feature.AbstractFolderFeatureStoreFactory.*;
import org.geotoolkit.storage.feature.query.DefaultQueryCapabilities;
import org.geotoolkit.storage.feature.query.QueryCapabilities;
import org.geotoolkit.factory.Hints;
import org.geotoolkit.storage.DataStoreFactory;
import org.geotoolkit.storage.event.StorageEvent;
import org.geotoolkit.version.VersionControl;
import org.geotoolkit.version.VersioningException;
import org.opengis.feature.Feature;
import org.opengis.feature.FeatureType;
import org.opengis.filter.Filter;
import org.opengis.filter.identity.FeatureId;
import org.opengis.parameter.GeneralParameterDescriptor;
import org.opengis.parameter.ParameterDescriptor;
import org.opengis.parameter.ParameterDescriptorGroup;
import org.opengis.parameter.ParameterValueGroup;
import org.opengis.util.GenericName;

/**
 * Handle a folder of single file FeatureStore.
 *
 * @author Johann Sorel (Geomatys)
 * @author Cédric Briançon (Geomatys)
 * @module
 */
public class DefaultFolderFeatureStore<T extends DataStoreFactory & FileFeatureStoreFactory> extends AbstractFeatureStore implements ResourceOnFileSystem {

    /**
     * Listen to changes in sub stores and propagate them.
     */
    private final StoreListener subListener = new StoreListener() {

        @Override
        public void eventOccured(StoreEvent event) {
            if (event instanceof StorageEvent) {
                event = ((StorageEvent)event).copy(DefaultFolderFeatureStore.this);
            }
            sendEvent(event);
        }
    };

    private final Parameters folderParameters;
    private final AbstractFolderFeatureStoreFactory folderFactory;
    private final T singleFileFactory;
    private final Parameters singleFileDefaultParameters;
    private GenericNameIndex<FeatureStore> stores = null;

    public DefaultFolderFeatureStore(final ParameterValueGroup params, final AbstractFolderFeatureStoreFactory factory){
        super(params);
        this.folderParameters = Parameters.castOrWrap(params);
        this.folderFactory = factory;
        this.singleFileFactory = this.folderFactory.getSingleFileFactory();

        final ParameterDescriptorGroup desc = singleFileFactory.getOpenParameters();
        singleFileDefaultParameters = Parameters.castOrWrap(desc.createValue());
        for(GeneralParameterDescriptor pdesc : desc.descriptors()){
            if(pdesc == PATH || pdesc.getName().getCode().equals(IDENTIFIER.getName().getCode())) {
                continue;
            }
            singleFileDefaultParameters.getOrCreate((ParameterDescriptor)pdesc)
                    .setValue(folderParameters.getValue((ParameterDescriptor) pdesc));
        }

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public AbstractFolderFeatureStoreFactory getProvider() {
        return folderFactory;
    }

    /**
     * Fallthrought to sub feature stores.
     */
    @Override
    public VersionControl getVersioning(String typeName) throws VersioningException {
        try {
            typeCheck(typeName);
        } catch (DataStoreException ex) {
            throw new VersioningException(ex);
        }
        final FeatureStore store;
        try {
            store = stores.get(this, typeName);
        } catch (IllegalNameException ex) {
            throw new VersioningException(ex);
        }
        return store.getVersioning(typeName);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public synchronized Set<GenericName> getNames() throws DataStoreException {

        if(stores == null){
            this.stores = new GenericNameIndex<>();
            final Path folder = getFolder(folderParameters);

            if(!Files.exists(folder)){
                try{
                    Files.createDirectory(folder);
                }catch(IOException | SecurityException ex){
                    throw new DataStoreException(ex.getMessage(), ex);
                }
            }

            Boolean recursive = folderParameters.getValue(RECURSIVE);
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

        return stores.getNames();
    }

    private void testFile(Path file) throws IOException {
        if (Files.isHidden(file) || Files.isSymbolicLink(file)) {
            return;//skip hidden and sym link files
        }

        final Parameters params = singleFileDefaultParameters.clone();
        params.getOrCreate(PATH).setValue(file.toUri());

        if (singleFileFactory.canProcess(params)) {
            try {
                final FeatureStore fileDS = (FeatureStore) singleFileFactory.open(params);
                fileDS.addListener(StoreEvent.class, subListener);
                stores.add(this, fileDS.getNames().iterator().next(), fileDS);
            } catch (DataStoreException ex) {
                getLogger().log(Level.WARNING, ex.getLocalizedMessage(), ex);
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void createFeatureType(final FeatureType featureType) throws DataStoreException {
        final GenericName typeName = featureType.getName();

        if(getNames().contains(typeName)){
            throw new DataStoreException("Type name "+ typeName + " already exists.");
        }

        final Parameters params = singleFileDefaultParameters.clone();
        try {
            final Path folder = getFolder(folderParameters);
            final String fileName = typeName.tip().toString() +"."+ singleFileFactory.getSuffix().iterator().next();
            final Path newFile = folder.resolve(fileName);
            params.getOrCreate(PATH).setValue(newFile.toUri().toURL());
        } catch (MalformedURLException ex) {
            throw new DataStoreException(ex);
        }

        final FeatureStore store = (FeatureStore) singleFileFactory.create(params);
        store.addListener(StoreEvent.class, subListener);
        store.createFeatureType(featureType);
        stores.add(this, typeName, store);
    }

    /**
     * Unsupported, throws a {@link DataStoreException}.
     */
    @Override
    public void updateFeatureType(final FeatureType featureType) throws DataStoreException {
        throw new DataStoreException("Not supported.");
    }

    /**
     * Unsupported, throws a {@link DataStoreException}.
     */
    @Override
    public synchronized void deleteFeatureType(final String typeName) throws DataStoreException {
        final FeatureStore store = stores.get(this, typeName);
        if (store == null) {
            throw new DataStoreException("There's no data with the following type name : "+typeName);
        }
        // We should get a file feature store.
        final Path[] sourceFiles;
        try {
            if (store instanceof ResourceOnFileSystem) {
                sourceFiles = ((ResourceOnFileSystem) store).getComponentFiles();
            } else {
                // Not a file store ? We try to find an url parameter and see if it's a file one.
                final URI fileURI = Parameters.castOrWrap(store.getOpenParameters().get()).getValue(PATH);
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

        stores.remove(this, store.getNames().iterator().next());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public FeatureType getFeatureType(final String typeName) throws DataStoreException {
        typeCheck(typeName);
        final FeatureStore store = stores.get(this, typeName);
        return store.getFeatureType(typeName);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isWritable(final String typeName) throws DataStoreException {
        typeCheck(typeName);
        final FeatureStore store = stores.get(this, typeName);
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
    public List<FeatureId> addFeatures(final String groupName, final Collection<? extends Feature> newFeatures,
            final Hints hints) throws DataStoreException {
        typeCheck(groupName);
        final FeatureStore store = stores.get(this, groupName);
        return store.addFeatures(groupName, newFeatures);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void updateFeatures(final String groupName, final Filter filter,
            final Map<String, ?> values) throws DataStoreException {
        typeCheck(groupName);
        final FeatureStore store = stores.get(this, groupName);
        store.updateFeatures(groupName, filter, values);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void removeFeatures(final String groupName, final Filter filter) throws DataStoreException {
        typeCheck(groupName);
        final FeatureStore store = stores.get(this, groupName);
        store.removeFeatures(groupName, filter);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public FeatureReader getFeatureReader(final Query query) throws DataStoreException {
        if (!(query instanceof org.geotoolkit.storage.feature.query.Query))  throw new UnsupportedQueryException();

        final org.geotoolkit.storage.feature.query.Query gquery = (org.geotoolkit.storage.feature.query.Query) query;
        final String name = gquery.getTypeName();
        typeCheck(name);
        final FeatureStore store = stores.get(this, name);
        return store.getFeatureReader(query);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public FeatureWriter getFeatureWriter(Query query) throws DataStoreException {
        if (!(query instanceof org.geotoolkit.storage.feature.query.Query))  throw new UnsupportedQueryException();

        final org.geotoolkit.storage.feature.query.Query gquery = (org.geotoolkit.storage.feature.query.Query) query;
        final String typeName = gquery.getTypeName();
        typeCheck(typeName);
        final FeatureStore store = stores.get(this, typeName);
        return store.getFeatureWriter(query);
    }

    private Path getFolder(final Parameters params) throws DataStoreException{
        final URI uri = params.getValue(FOLDER_PATH);

        try {
            return Paths.get(uri);
        } catch (FileSystemNotFoundException | IllegalArgumentException e) {
            throw new DataStoreException(e);
        }
    }

    /**
     * {@inheritDoc}
     */
    public Path[] getComponentFiles() throws DataStoreException {
        final Path folder = getFolder(folderParameters);
        return new Path[]{ folder };
    }

    @Override
    public void refreshMetaModel() {
        stores=null;
    }
}
