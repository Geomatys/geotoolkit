/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2012, Geomatys
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
package org.geotoolkit.coverage.xmlstore;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import javax.xml.bind.JAXBException;

import org.geotoolkit.storage.coverage.AbstractCoverageStore;
import org.geotoolkit.storage.coverage.CoverageReference;
import org.geotoolkit.storage.coverage.CoverageStoreFactory;
import org.geotoolkit.storage.coverage.CoverageType;
import org.geotoolkit.coverage.grid.ViewType;
import org.geotoolkit.util.NamesExt;
import org.apache.sis.storage.DataStoreException;
import org.geotoolkit.parameter.Parameters;
import org.geotoolkit.storage.DataNode;
import org.geotoolkit.storage.DataStores;
import org.geotoolkit.storage.DefaultDataNode;
import org.opengis.util.GenericName;
import org.opengis.parameter.ParameterValueGroup;

/**
 * Coverage store relying on an xml file.
 *
 * @author Johann Sorel (Geomatys)
 * @module
 */
public class XMLCoverageStore extends AbstractCoverageStore {

    private final Path root;
    private final DataNode rootNode = new DefaultDataNode();

    final boolean cacheTileState;

    @Deprecated
    public XMLCoverageStore(File root) throws URISyntaxException, IOException {
        this(root.toPath(),true);
    }

    public XMLCoverageStore(Path root) throws URISyntaxException, IOException {
        this(root,true);
    }
    
    public XMLCoverageStore(URL rootPath) throws URISyntaxException, IOException {
        this(rootPath, true);
    }

    @Deprecated
    public XMLCoverageStore(File root, boolean cacheTileStateInMemory) throws URISyntaxException, IOException {
        this(toParameters(root.toURI(),cacheTileStateInMemory));
    }

    public XMLCoverageStore(Path root, boolean cacheTileStateInMemory) throws URISyntaxException, IOException {
        this(toParameters(root.toUri(),cacheTileStateInMemory));
    }

    public XMLCoverageStore(URL rootPath, boolean cacheTileStateInMemory) throws URISyntaxException, IOException {
        this(toParameters(rootPath.toURI(), cacheTileStateInMemory));
    }
        
    public XMLCoverageStore(ParameterValueGroup params) throws URISyntaxException, IOException {
        super(params);
        final URI rootPath = Parameters.value(XMLCoverageStoreFactory.PATH, params);
        root = Paths.get(rootPath);
        Boolean tmpCacheState = Parameters.value(XMLCoverageStoreFactory.CACHE_TILE_STATE, params);
        cacheTileState = (tmpCacheState == null)? true : tmpCacheState;
        explore();
    }

    private static ParameterValueGroup toParameters(URI rootPath, boolean cacheState) {
        final ParameterValueGroup params = XMLCoverageStoreFactory.PARAMETERS_DESCRIPTOR.createValue();
        Parameters.getOrCreate(XMLCoverageStoreFactory.PATH, params).setValue(rootPath);
        Parameters.getOrCreate(XMLCoverageStoreFactory.CACHE_TILE_STATE, params).setValue(cacheState);
        return params;
    }
    
    @Override
    public CoverageStoreFactory getFactory() {
        return (CoverageStoreFactory) DataStores.getFactoryById(XMLCoverageStoreFactory.NAME);
    }

    @Override
    public DataNode getRootNode() {
        return rootNode;
    }

    /**
     * Search all xml files in the folder which define a pyramid model.
     */
    private void explore() throws IOException {

        if (!Files.exists(root)) {
            Files.createDirectories(root);
        }

        if (Files.isRegularFile(root)) {
            createReference(root);
        } else {
            List<Path> paths = new ArrayList<>();
            try (DirectoryStream<Path> stream = Files.newDirectoryStream(root)) {
                for (Path path : stream) {
                    if (Files.isRegularFile(path) && path.getFileName().toString().toLowerCase().endsWith(".xml")) {
                        paths.add(path);
                    }
                }
            }
            if (!paths.isEmpty()) {
                for (Path candidate : paths) {
                    //try to parse the file
                    createReference(candidate);
                }
            }
        }
    }

    private void createReference(Path refDescriptor) {
        try {
            //TODO useless copy here
            final XMLCoverageReference set = XMLCoverageReference.read(refDescriptor);
            final GenericName name = NamesExt.create(getDefaultNamespace(), set.getId());
            final XMLCoverageReference ref = new XMLCoverageReference(this,name,set.getPyramidSet());
            ref.copy(set);
            rootNode.getChildren().add(ref);
        } catch (JAXBException ex) {
            getLogger().log(Level.INFO, "file is not a pyramid : {0}", refDescriptor.toString());
        } catch (DataStoreException ex) {
            getLogger().log(Level.WARNING, "Pyramid descriptor contains an invalid CRS : "+refDescriptor.toAbsolutePath().toString(), ex);
        } catch (IOException e) {
            getLogger().log(Level.WARNING, "Unable to read pyramid file description : {0}", refDescriptor.toString());
        }
    }

    @Override
    public void close() {
    }

    @Override
    public CoverageReference create(GenericName name) throws DataStoreException {
        return create(name, null, null);
    }

    /**
     * Create a CoverageReference with a specific data type and preferred image tile format.
     * Default is ViewType.RENDERED and PNG tile format.
     *
     * @param name name of the new CoverageReference.
     * @param packMode data type (Geophysic or Rendered). Can be null.
     * @param preferredFormat pyramid tile format. Can be null.
     * @return new CoverageReference.
     * @throws DataStoreException
     */
    public CoverageReference create(GenericName name, ViewType packMode, String preferredFormat) throws DataStoreException {
        if (Files.isRegularFile(root)) {
            throw new DataStoreException("Store root is a file, not a directory, no reference creation allowed.");
        }
        name = NamesExt.create(getDefaultNamespace(), name.tip().toString());
        final Set<GenericName> names = getNames();
        if(names.contains(name)){
            throw new DataStoreException("Name already used in store : " + name.tip().toString());
        }

        final XMLPyramidSet set = new XMLPyramidSet();
        final XMLCoverageReference ref = new XMLCoverageReference(this,name,set);
        ref.initialize(root.resolve(name.tip().toString()+".xml"));

        if (packMode != null) {
            ref.setPackMode(packMode);
        }

        if (preferredFormat != null) {
            ref.setPreferredFormat(preferredFormat);
        }

        rootNode.getChildren().add(ref);
        ref.save();
        return ref;
    }

    @Override
    public CoverageType getType() {
        return CoverageType.PYRAMID;
    }
}
