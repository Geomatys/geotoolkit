/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2016, Geomatys
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
package org.geotoolkit.storage;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import org.apache.sis.internal.metadata.NameToIdentifier;
import org.apache.sis.metadata.MetadataCopier;
import org.apache.sis.metadata.MetadataStandard;
import org.apache.sis.storage.DataStoreException;
import org.geotoolkit.util.NamesExt;
import org.opengis.metadata.Identifier;
import org.opengis.metadata.Metadata;
import org.opengis.parameter.ParameterValueGroup;
import org.opengis.util.GenericName;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public abstract class DataStore extends org.apache.sis.storage.DataStore {

    /**
     * Cached value for the store metadata. Initialized when first queried. See
     * {@link #getMetadata() } for more information.
     */
    private Metadata metadata;

    /**
     * A lock to synchronize metadata initialization. See {@link #getMetadata() }
     * for further information.
     */
    private final Object mdLock = new Object();

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
    public abstract DataStoreFactory getFactory();

    @Override
    public Metadata getMetadata() throws DataStoreException {
        final Metadata mdRef;
        synchronized (mdLock) {
            if (metadata == null)
                metadata = createMetadata();
            mdRef = metadata;
        }
        return mdRef == null? null : new MetadataCopier(MetadataStandard.ISO_19115).copy(Metadata.class, mdRef);
    }

    /**
     * Create a new metadata containing information about this datastore and the
     * data it contains.
     *
     * Note : Analysis should be restricted to report only information currently
     * available in this dataset. Further computing should be performed externally.
     *
     * @return Created metadata. Can be null if there's no information available.
     * @throws DataStoreException If an error occurs while analyzing underlying
     * data.
     */
    protected Metadata createMetadata() throws DataStoreException {
        return null;
    }

//    public abstract Resource getRootResource() throws DataStoreException;

//    /**
//     * Convenient method to list identifiers of all resources in the store.
//     *
//     * @return Set, never null, can be empty.
//     */
//    public Set<GenericName> getNames() throws DataStoreException {
//        final Set<GenericName> names = new HashSet<>();
//        fillNames(getRootResource(), names);
//        return names;
//    }
//
//    private void fillNames(Resource candidate, Set<GenericName> names) {
//        final Identifier identifier = candidate.getIdentifier();
//        if (identifier instanceof GenericName) {
//            names.add((GenericName) identifier);
//        } else {
//            names.add(NamesExt.create(identifier.getCode()));
//        }
//
//        if (candidate instanceof DataSet) {
//            final DataSet ds = (DataSet) candidate;
//            for (Resource rs : ds.getResources()) {
//                fillNames(rs, names);
//            }
//        }
//
//    }
//
//    public Resource findResource(String name) throws DataStoreException {
//        final Resource res = findResource(name, getRootResource());
//        if (res==null) {
//            throw new DataStoreException("No resource for name : "+name);
//        }
//        return res;
//    }
//
//    private Resource findResource(String name, Resource candidate) throws DataStoreException {
//        boolean match = NameToIdentifier.isHeuristicMatchForIdentifier(Collections.singleton(candidate.getIdentifier()), name);
//        Resource result = match ? candidate : null;
//
//        if (candidate instanceof DataSet) {
//            final DataSet ds = (DataSet) candidate;
//            for (Resource rs : ds.getResources()) {
//                rs = findResource(name, rs);
//                if (rs!=null) {
//                    if (result!=null) {
//                        throw new DataStoreException("Multiple resources match the name : "+name);
//                    }
//                    result = rs;
//                }
//            }
//        }
//        return result;
//    }
}
