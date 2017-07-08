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
package org.geotoolkit.storage.coverage;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.UnaryOperator;
import java.util.stream.Stream;
import org.apache.sis.internal.metadata.NameToIdentifier;
import org.apache.sis.storage.DataStoreException;
import org.apache.sis.util.iso.DefaultScopedName;
import org.geotoolkit.storage.DataSet;
import org.geotoolkit.storage.DataStoreFactory;
import org.geotoolkit.storage.Resource;
import org.geotoolkit.storage.StorageListener;
import org.geotoolkit.util.NamesExt;
import org.geotoolkit.version.Version;
import org.geotoolkit.version.VersionControl;
import org.geotoolkit.version.VersioningException;
import org.opengis.metadata.Identifier;
import org.opengis.metadata.Metadata;
import org.opengis.util.GenericName;
import org.opengis.parameter.ParameterValueGroup;

/**
 *
 * @author Johann Sorel (Geomatys)
 * @module
 */
public interface CoverageStore extends AutoCloseable {

    /**
     * Get the parameters used to initialize this source from it's factory.
     *
     * @return source configuration parameters
     */
    ParameterValueGroup getConfiguration();

    /**
     * Get the factory which created this source.
     *
     * @return this source original factory
     */
    DataStoreFactory getFactory();

    Metadata getMetadata() throws DataStoreException;

    /**
     * Returns the root resource of the coverage store.
     * This node is the main access point to the content of the store.
     *
     * @return DataNode never null.
     * @throws org.apache.sis.storage.DataStoreException
     */
    public abstract Resource getRootResource() throws DataStoreException;

    /**
     * Get a collection of all available coverage names.
     *
     * @return Set<Name> , never null, but can be empty.
     * @throws DataStoreException
     */
    default Set<GenericName> getNames() throws DataStoreException {
        final Set<GenericName> names = new HashSet<>();

        //recursively fill names
        new Consumer<Resource>() {
            @Override
            public void accept(final Resource candidate) {
                final Identifier identifier = candidate.getIdentifier();
                if (identifier instanceof GenericName) {
                    names.add((GenericName) identifier);
                } else {
                    names.add(NamesExt.create(identifier.getCode()));
                }
                if (candidate instanceof DataSet) {
                    final DataSet ds = (DataSet) candidate;
                    for (Resource rs : ds.getResources()) {
                        accept(rs);
                    }
                }
            }
        }.accept(getRootResource());

        return names;
    }

    default Resource findResource(final String name) throws DataStoreException {

        //recursive search
        Object res = new Function<Resource,Object>() {
            @Override
            public Object apply(final Resource candidate) {
                boolean match = NameToIdentifier.isHeuristicMatchForIdentifier(Collections.singleton(candidate.getIdentifier()), name);
                Object result = match ? candidate : null;

                if (candidate instanceof DataSet) {
                    final DataSet ds = (DataSet) candidate;
                    for (Resource rs : ds.getResources()) {
                        Object rr = apply(rs);
                        if (rr instanceof DataStoreException) {
                            return rr;
                        } else if (rr instanceof Resource) {
                            if (result!=null) {
                                return new DataStoreException("Multiple resources match the name : "+name);
                            }
                            result = rr;
                        }
                    }
                }
                return result;
            }
        }.apply(getRootResource());

        if (res==null) {
            throw new DataStoreException("No resource for name : "+name);
        } else if (res instanceof DataStoreException) {
            throw (DataStoreException)res;
        }
        return (Resource) res;
    }

    ////////////////////////////////////////////////////////////////////////////
    // OLD API /////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////

    /**
     * Check if this coverage store support versioning.
     *
     * @return true if versioning is supported.
     */
    public abstract boolean handleVersioning();

    /**
     * Get version history for given coverage.
     *
     * @return VersionHistory for given name.
     */
    public abstract VersionControl getVersioning(GenericName typeName) throws VersioningException;

    /**
     * Get the coverage reference for the given name.
     *
     * @param name reference name
     * @return CoverageResource
     * @throws DataStoreException
     */
    public abstract CoverageResource findResource(GenericName name) throws DataStoreException;

    /**
     * Get the coverage reference for the given name and version.
     * If the version do not exist it will be created.
     *
     * @param name reference name
     * @param version version
     * @return CoverageResource
     * @throws DataStoreException
     */
    public abstract CoverageResource findResource(GenericName name, Version version) throws DataStoreException;

    /**
     * Create a new coverage reference.
     * The returned coverage reference might have a different namespace.
     *
     * @param name
     * @return CoverageResource
     * @throws DataStoreException
     */
    public abstract CoverageResource create(GenericName name) throws DataStoreException;

    /**
     * Check coverage type define in {@link CoverageType}
     * @return a {@link CoverageType}
     */
    public abstract CoverageType getType();

    /**
     * Delete an existing coverage reference.
     *
     * @param name
     * @throws DataStoreException
     */
    public abstract void delete(GenericName name) throws DataStoreException;

    /**
     * Add a storage listener which will be notified when structure changes or
     * when coverage data changes.
     *
     * @param listener to add
     */
    public abstract void addStorageListener(StorageListener listener);

    /**
     * Remove a storage listener.
     *
     * @param listener to remove
     */
    public abstract void removeStorageListener(StorageListener listener);

    void close() throws DataStoreException;

}
