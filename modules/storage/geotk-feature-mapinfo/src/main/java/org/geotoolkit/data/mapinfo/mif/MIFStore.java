/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2013-2019, Geomatys
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
package org.geotoolkit.data.mapinfo.mif;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.apache.sis.internal.storage.ResourceOnFileSystem;
import org.apache.sis.metadata.iso.DefaultMetadata;
import org.apache.sis.parameter.Parameters;
import org.apache.sis.storage.DataStore;
import org.apache.sis.storage.DataStoreException;
import org.apache.sis.storage.FeatureSet;
import org.apache.sis.storage.Resource;
import org.apache.sis.storage.StorageConnector;
import org.apache.sis.storage.WritableAggregate;
import org.geotoolkit.data.DefiningFeatureSet;
import org.geotoolkit.data.mapinfo.ProjectionUtils;
import org.geotoolkit.storage.DataStores;
import org.opengis.feature.FeatureType;
import org.opengis.filter.Filter;
import org.opengis.metadata.Metadata;
import org.opengis.parameter.ParameterValueGroup;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.util.GenericName;

/**
 * A featureStore for MapInfo exchange format MIF-MID.
 *
 * @author Alexis Manin (Geomatys)
 *         Date : 21/02/13
 */
public class MIFStore extends DataStore implements WritableAggregate, ResourceOnFileSystem {

    private final Parameters parameters;
    final MIFManager manager;
    private List<Resource> components;

    /**
     * Creates a new instance of MIFFeatureStore.
     *
     * @param uri The URL of the MIF file to use for this DataStore.
     *
     * @throws DataStoreException If we got a problem getting needed files.
     */
    public MIFStore(final URI uri) throws DataStoreException {
        this(toParameter(uri));
    }

    public MIFStore(final ParameterValueGroup params) throws DataStoreException {
        super(DataStores.getProviderById(MIFProvider.NAME), new StorageConnector(Parameters.castOrWrap(params).getMandatoryValue(MIFProvider.PATH)));

        this.parameters = Parameters.unmodifiable(params);

        final URI filePath = this.parameters.getMandatoryValue(MIFProvider.PATH);
        try {
            manager = new MIFManager(filePath);
        } catch (Exception e) {
            throw new DataStoreException("Datastore can't reach target data.", e);
        }
    }

    @Override
    public Optional<ParameterValueGroup> getOpenParameters() {
        return Optional.of(parameters);
    }

    @Override
    public Metadata getMetadata() throws DataStoreException {
        return new DefaultMetadata();
    }

    private static ParameterValueGroup toParameter(final URI uri) {
        final Parameters params = Parameters.castOrWrap(MIFProvider.PARAMETERS_DESCRIPTOR.createValue());
        params.getOrCreate(MIFProvider.PATH).setValue(uri);
        return params;
    }

    @Override
    public synchronized Resource add(Resource resource) throws DataStoreException {

        final FeatureType type;
        if (resource instanceof DefiningFeatureSet) {
            type = ((DefiningFeatureSet) resource).getType();
        } else if (resource instanceof FeatureSet) {
            type = ((FeatureSet) resource).getType();
        } else {
            throw new DataStoreException("Unexpected resource type");
        }

        try {
            manager.addSchema(type.getName(), type);
        } catch (URISyntaxException | IOException e) {
            throw new DataStoreException("We're unable to add a schema because we can't access source files.", e);
        }
        //clear cache
        components = null;

        return findResource(type.getName().toString());
    }

    @Override
    public synchronized void remove(Resource resource) throws DataStoreException {
        if (resource instanceof MIFFeatureSet) {
            final MIFFeatureSet fs = (MIFFeatureSet) resource;
            final GenericName name = fs.getType().getName();
            manager.deleteSchema(name.toString());
            fs.removeIf(Filter.INCLUDE::evaluate);
        } else {
            throw new DataStoreException("Unexpected resource type");
        }

        //clear cache
        components = null;
    }

    @Override
    public synchronized Collection<? extends Resource> components() throws DataStoreException {
        if (components == null) {
            components = new ArrayList<>();
            for (GenericName name : manager.getTypeNames()) {
                components.add(new MIFFeatureSet(this, name));
            }
            components = Collections.unmodifiableList(components);
        }
        return components;
    }

    /**
     * MIF file defines a delimiter character to separate values into the MID file. This function allows user to redefine it.
     * @param newDelimiter The new delimiter to use for MID value separation.
     */
    public void setDelimiter(char newDelimiter) {
        manager.setDelimiter(newDelimiter);
    }

    public static boolean isCompatibleCRS(CoordinateReferenceSystem source) {
        boolean isCompatible = false;
        try {
            final String mifCRS = ProjectionUtils.crsToMIFSyntax(source);
            if (mifCRS != null && !mifCRS.isEmpty()) {
                isCompatible = true;
            }
        } catch (Exception e) {
            // Nothing to do here, if we get an exception, we just get an incompatible CRS.
        }
        return isCompatible;
    }

    @Override
    public Path[] getComponentFiles() throws DataStoreException {
        List<Path> results = new ArrayList<>();
        if (manager.getMIFPath() != null) {
            results.add(Paths.get(manager.getMIFPath()));
        }
        if (manager.getMIDPath() != null) {
            results.add(Paths.get(manager.getMIDPath()));
        }
        return results.toArray(new Path[results.size()]);
    }

    @Override
    public void close() throws DataStoreException {
    }
}
