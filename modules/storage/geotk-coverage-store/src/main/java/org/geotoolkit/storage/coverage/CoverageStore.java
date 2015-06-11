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
import org.apache.sis.storage.DataStore;
import org.apache.sis.storage.DataStoreException;
import org.geotoolkit.storage.DataNode;
import org.geotoolkit.storage.StorageListener;
import org.geotoolkit.version.Version;
import org.geotoolkit.version.VersionControl;
import org.geotoolkit.version.VersioningException;
import org.geotoolkit.feature.type.Name;
import org.opengis.parameter.ParameterValueGroup;

/**
 *
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public abstract class CoverageStore extends DataStore {

    /**
     * Get the parameters used to initialize this source from it's factory.
     *
     * @return source configuration parameters
     */
    public abstract ParameterValueGroup getConfiguration();

    /**
     * Get the factory which created this source.
     *
     * @return this source original factory
     */
    public abstract CoverageStoreFactory getFactory();

    /**
     * Returns the root node of the coverage store.
     * This node is the main access point to the content of the store.
     *
     * TODO move this in Apache SIS DataStore class when ready
     *
     * @return DataNode never null.
     */
    public abstract DataNode getRootNode() throws DataStoreException;


    ////////////////////////////////////////////////////////////////////////////
    // OLD API /////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////

    /**
     * Get a collection of all available coverage names.
     * @return Set<Name> , never null, but can be empty.
     * @throws DataStoreException
     */
    public abstract Set<Name> getNames() throws DataStoreException;

    /**
     * Check if this coverage store support versioning.
     * @return true if versioning is supported.
     */
    public abstract boolean handleVersioning();

    /**
     * Get version history for given coverage.
     * @return VersionHistory for given name.
     */
    public abstract VersionControl getVersioning(Name typeName) throws VersioningException;

    /**
     * Get the coverage reference for the given name.
     * @param name reference name
     * @return CoverageReference
     * @throws DataStoreException
     */
    public abstract CoverageReference getCoverageReference(Name name) throws DataStoreException;

    /**
     * Get the coverage reference for the given name and version.
     * If the version do not exist it will be created.
     *
     * @param name reference name
     * @param version version
     * @return CoverageReference
     * @throws DataStoreException
     */
    public abstract CoverageReference getCoverageReference(Name name, Version version) throws DataStoreException;

    /**
     * Create a new coverage reference.
     * The returned coverage reference might have a different namespace.
     *
     * @param name
     * @return CoverageReference
     * @throws DataStoreException
     */
    public abstract CoverageReference create(Name name) throws DataStoreException;

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
    public abstract void delete(Name name) throws DataStoreException;

    /**
     * Add a storage listener which will be notified when structure changes or
     * when coverage data changes.
     * @param listener to add
     */
    public abstract void addStorageListener(StorageListener listener);

    /**
     * Remove a storage listener
     * @param listener to remove
     */
    public abstract void removeStorageListener(StorageListener listener);

}
