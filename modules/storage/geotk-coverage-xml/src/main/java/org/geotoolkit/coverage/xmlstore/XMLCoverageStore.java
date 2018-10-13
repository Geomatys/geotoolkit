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
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import javax.xml.bind.JAXBException;
import org.apache.sis.parameter.Parameters;
import org.apache.sis.referencing.NamedIdentifier;
import org.apache.sis.storage.DataStoreException;
import org.apache.sis.storage.WritableAggregate;
import org.geotoolkit.coverage.grid.ViewType;
import org.geotoolkit.storage.DataStoreFactory;
import org.geotoolkit.storage.DataStores;
import org.apache.sis.storage.Resource;
import org.geotoolkit.storage.coverage.AbstractCoverageStore;
import org.geotoolkit.storage.coverage.DefiningCoverageResource;
import org.geotoolkit.storage.coverage.GridCoverageResource;
import org.geotoolkit.util.NamesExt;
import org.opengis.parameter.ParameterValueGroup;
import org.opengis.util.GenericName;

/**
 * Coverage store relying on an xml file.
 *
 * @author Johann Sorel (Geomatys)
 * @module
 */
public class XMLCoverageStore extends AbstractCoverageStore implements WritableAggregate {

    private final Path root;
    private final List<Resource> resources = new ArrayList<>();

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
        final URI rootPath = Parameters.castOrWrap(params).getValue(XMLCoverageStoreFactory.PATH);
        root = Paths.get(rootPath);
        Boolean tmpCacheState = Parameters.castOrWrap(params).getValue(XMLCoverageStoreFactory.CACHE_TILE_STATE);
        cacheTileState = (tmpCacheState == null)? true : tmpCacheState;
        explore();
    }

    private static ParameterValueGroup toParameters(URI rootPath, boolean cacheState) {
        final Parameters params = Parameters.castOrWrap(XMLCoverageStoreFactory.PARAMETERS_DESCRIPTOR.createValue());
        params.getOrCreate(XMLCoverageStoreFactory.PATH).setValue(rootPath);
        params.getOrCreate(XMLCoverageStoreFactory.CACHE_TILE_STATE).setValue(cacheState);
        return params;
    }

    @Override
    public GenericName getIdentifier() {
        return null;
    }

    @Override
    public DataStoreFactory getProvider() {
        return DataStores.getFactoryById(XMLCoverageStoreFactory.NAME);
    }

    @Override
    public Collection<org.apache.sis.storage.Resource> components() throws DataStoreException {
        return Collections.unmodifiableList(resources);
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
            resources.add(XMLCoverageResource.read(refDescriptor));
        } catch (JAXBException ex) {
            getLogger().log(Level.INFO, "file is not a pyramid : {0}", refDescriptor.toString());
        } catch (DataStoreException ex) {
            getLogger().log(Level.WARNING, "Pyramid descriptor contains an invalid CRS : "+refDescriptor.toAbsolutePath().toString(), ex);
        } catch (IOException e) {
            getLogger().log(Level.WARNING, "Unable to read pyramid file description : {0}", refDescriptor.toString());
        }
    }

    @Override
    public GridCoverageResource add(org.apache.sis.storage.Resource resource) throws DataStoreException {
        if (!(resource instanceof DefiningCoverageResource)) {
            throw new DataStoreException("Unsupported resource "+resource);
        }
        final DefiningCoverageResource cr = (DefiningCoverageResource) resource;
        final GenericName name = cr.getName();

        return create(name, null, null);
    }

    @Override
    public void remove(org.apache.sis.storage.Resource resource) throws DataStoreException {
        if (!(resource instanceof GridCoverageResource)) {
            throw new DataStoreException("Unknown resource "+resource);
        }
        final GridCoverageResource cr = (GridCoverageResource) resource;
        final NamedIdentifier name = cr.getIdentifier();

        //TODO
        throw new DataStoreException("Remove operation not supported.");
    }

    /**
     * Create a CoverageResource with a specific data type and preferred image tile format.
     * Default is ViewType.RENDERED and PNG tile format.
     *
     * @param name name of the new CoverageResource.
     * @param packMode data type (Geophysic or Rendered). Can be null.
     * @param preferredFormat pyramid tile format. Can be null.
     * @return new CoverageResource.
     * @throws DataStoreException
     */
    public GridCoverageResource create(GenericName name, ViewType packMode, String preferredFormat) throws DataStoreException {
        if (Files.isRegularFile(root)) {
            throw new DataStoreException("Store root is a file, not a directory, no reference creation allowed.");
        }
        name = NamesExt.create(name.tip().toString());
        final Set<GenericName> names = getNames();
        if(names.contains(name)){
            throw new DataStoreException("Name already used in store: " + name.tip());
        }

        final XMLPyramidSet set = new XMLPyramidSet();
        final XMLCoverageResource ref = new XMLCoverageResource(this,name,set);
        ref.initialize(root.resolve(name.tip() + ".xml"));

        if (packMode != null) {
            ref.setPackMode(packMode);
        }

        if (preferredFormat != null) {
            ref.setPreferredFormat(preferredFormat);
        }

        resources.add(ref);
        ref.save();
        return ref;
    }

    @Override
    public void close() {
    }

}
