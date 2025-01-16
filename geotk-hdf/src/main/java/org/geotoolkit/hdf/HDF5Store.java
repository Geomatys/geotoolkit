/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2022, Geomatys
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation; either
 *    version 2.1 of the License, or (at your option) any later version.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */
package org.geotoolkit.hdf;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Collection;
import java.util.Collections;
import java.util.Optional;
import java.util.logging.Level;
import org.apache.sis.metadata.iso.DefaultMetadata;
import org.apache.sis.parameter.Parameters;
import org.apache.sis.storage.Aggregate;
import org.apache.sis.storage.DataStore;
import org.apache.sis.storage.DataStoreException;
import org.apache.sis.storage.Resource;
import org.apache.sis.storage.StorageConnector;
import org.geotoolkit.hdf.api.Dataset;
import org.geotoolkit.hdf.api.Group;
import org.geotoolkit.hdf.api.Node;
import org.geotoolkit.hdf.convention.CFCoverageResource;
import org.geotoolkit.hdf.convention.DatasetAsFeatureSet;
import org.geotoolkit.hdf.convention.GroupAsAggregate;
import org.geotoolkit.hdf.io.Connector;
import org.opengis.metadata.Metadata;
import org.opengis.parameter.ParameterValueGroup;
import org.opengis.util.FactoryException;
import org.opengis.util.GenericName;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public final class HDF5Store extends DataStore implements Aggregate {

    private final Connector cnx;
    private final Group root;
    private final Resource decorate;

    public HDF5Store(HDF5Provider provider, Path path) throws IllegalArgumentException, DataStoreException, IOException {
        super(provider, new StorageConnector(path));
        cnx = new Connector(path);
        root = new Group(null, cnx, cnx.getSuperblock().rootGroupSymbolTableEntry, path.getFileName().toString());
        decorate = decorate(root);
    }

    @Override
    public Optional<ParameterValueGroup> getOpenParameters() {
        final Parameters parameters = Parameters.castOrWrap(HDF5Provider.PARAMETERS_DESCRIPTOR.createValue());
        parameters.getOrCreate(HDF5Provider.PATH).setValue(cnx.getPath().toUri());
        return Optional.of(parameters);
    }

    @Override
    public Metadata getMetadata() throws DataStoreException {
        return new DefaultMetadata();
    }

    @Override
    public void close() throws DataStoreException {
        cnx.close();
    }

    /**
     * Get root HDF-5 Group.
     * @return root HDF-5 Group, never null.
     */
    public Group getRootGroup() {
        return root;
    }

    @Override
    public Optional<GenericName> getIdentifier() throws DataStoreException {
        return Optional.empty();
    }

    @Override
    public Collection<? extends Resource> components() throws DataStoreException {
        return Collections.singletonList(decorate);
    }

    @Override
    public Optional<FileSet> getFileSet() throws DataStoreException {
        return Optional.of(new FileSet(cnx.getPath()));
    }

    @Override
    public String toString() {
        return getRootGroup().toString();
    }

    /**
     * Decorate HDF nodes to Resources.
     */
    private Resource decorate(Node node) {
        if (node instanceof Group group) {
            final String conventions = String.valueOf(group.getAttributes().get("Conventions"));
            if (conventions.startsWith("CF")) {
                try {
                    return new CFCoverageResource(this, group);
                } catch (DataStoreException | FactoryException ex) {
                    HDF5Provider.LOGGER.log(Level.WARNING, "Failed to intepret " + group.getName() + " as a CF grid coverage.\n" + ex.getMessage(), ex);
                }
            }
            final GroupAsAggregate gaa = new GroupAsAggregate(this, group);
            for (Node n : group.components()) {
                gaa.resources().add(decorate(n));
            }
            return gaa;
        } else if (node instanceof Dataset ds) {
            return new DatasetAsFeatureSet(this, ds);
        } else {
            throw new IllegalArgumentException("Unexpected node type " + node);
        }
    }

}
