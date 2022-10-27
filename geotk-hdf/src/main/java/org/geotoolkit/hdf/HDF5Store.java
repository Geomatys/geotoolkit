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
import java.util.Map;
import java.util.Optional;
import org.apache.sis.internal.storage.ResourceOnFileSystem;
import org.apache.sis.metadata.iso.DefaultMetadata;
import org.apache.sis.parameter.Parameters;
import org.apache.sis.storage.Aggregate;
import org.apache.sis.storage.DataStore;
import org.apache.sis.storage.DataStoreException;
import org.apache.sis.storage.Resource;
import org.geotoolkit.hdf.api.DefaultGroup;
import org.geotoolkit.hdf.api.Group;
import org.geotoolkit.hdf.api.Node;
import org.geotoolkit.hdf.io.Connector;
import org.opengis.metadata.Metadata;
import org.opengis.parameter.ParameterValueGroup;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public final class HDF5Store extends DataStore implements Group, Aggregate, ResourceOnFileSystem {

    private final Connector cnx;
    private final DefaultGroup root;

    public HDF5Store(Path path) throws IllegalArgumentException, DataStoreException, IOException {
        cnx = new Connector(path);
        root = new DefaultGroup(null, cnx, cnx.getSuperblock().rootGroupSymbolTableEntry, path.getFileName().toString());
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

    @Override
    public String getName() {
        return root.getName();
    }

    @Override
    public Group getParent() {
        return root.getParent();
    }

    @Override
    public Map<String, Object> getAttributes() {
        return root.getAttributes();
    }

    @Override
    public Collection<? extends Resource> components() throws DataStoreException {
        return root.components();
    }

    @Override
    public Node getComponent(String name) {
        return root.getComponent(name);
    }

    @Override
    public Path[] getComponentFiles() throws DataStoreException {
        return new Path[]{cnx.getPath()};
    }

    @Override
    public String toString() {
        return root.toString();
    }

}
