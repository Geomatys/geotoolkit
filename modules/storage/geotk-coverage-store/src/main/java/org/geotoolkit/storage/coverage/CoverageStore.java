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

import java.util.Set;
import org.apache.sis.storage.DataStoreException;
import org.apache.sis.storage.Resource;
import org.geotoolkit.storage.DataStoreFactory;
import org.geotoolkit.storage.StorageListener;
import org.geotoolkit.version.Version;
import org.geotoolkit.version.VersionControl;
import org.geotoolkit.version.VersioningException;
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
    ParameterValueGroup getOpenParameters();

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
    Resource getRootResource() throws DataStoreException;

    /**
     * Get a collection of all available coverage names.
     *
     * @return Set<GenericName> , never null, but can be empty.
     * @throws DataStoreException
     */
    Set<GenericName> getNames() throws DataStoreException;

    Resource findResource(final String name) throws DataStoreException;

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
