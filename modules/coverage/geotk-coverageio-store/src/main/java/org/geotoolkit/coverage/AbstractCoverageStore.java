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

package org.geotoolkit.coverage;

import java.util.Collection;
import java.util.logging.Logger;
import org.geotoolkit.storage.DataStoreException;
import org.geotoolkit.util.logging.Logging;
import org.opengis.feature.type.Name;

/**
 * Uncomplete implementation of a coveragestore.
 *
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public abstract class AbstractCoverageStore implements CoverageStore{


    protected static final String NO_NAMESPACE = "no namespace";

    private final Logger Logger = Logging.getLogger(getClass().getPackage().getName());

    private final String defaultNamespace;

    protected AbstractCoverageStore(final String namespace) {
        if (namespace == null) {
            defaultNamespace = "http://geotoolkit.org";
        } else if (namespace.equals(NO_NAMESPACE)) {
            defaultNamespace = null;
        } else {
            defaultNamespace = namespace;
        }
    }

    protected String getDefaultNamespace() {
        return defaultNamespace;
    }

    protected Logger getLogger(){
        return Logger;
    }

    @Override
    public CoverageReference create(Name name) throws DataStoreException {
        throw new DataStoreException("Creation of new coverage not supported.");
    }

    ////////////////////////////////////////////////////////////////////////////
    // useful methods for datastore that doesn't implement all query parameters/
    ////////////////////////////////////////////////////////////////////////////

    /**
     * Convinient method to check that the given type name exist.
     * Will raise a datastore exception if the name do not exist in this datastore.
     * @param candidate Name to test.
     * @throws DataStoreException if name do not exist.
     */
    protected void typeCheck(final Name candidate) throws DataStoreException{

        final Collection<Name> names = getNames();
        if(!names.contains(candidate)){
            final StringBuilder sb = new StringBuilder("Type name : ");
            sb.append(candidate);
            sb.append(" do not exist in this datastore, available names are : ");
            for(final Name n : names){
                sb.append(n).append(", ");
            }
            throw new DataStoreException(sb.toString());
        }
    }

}
