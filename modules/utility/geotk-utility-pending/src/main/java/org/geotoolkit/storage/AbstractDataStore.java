/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2013, Geomatys
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

import java.util.logging.Level;
import org.apache.sis.storage.DataStoreException;
import org.apache.sis.util.Classes;
import org.apache.sis.util.logging.Logging;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public abstract class AbstractDataStore extends AbstractStorage{

    /**
     * Returns the root node of the data store.
     * This node is the main access point to the content of the store.
     *
     * TODO move this in Apache SIS DataStore class when ready
     *
     * @return DataNode never null.
     */
    public abstract Resource getRootNode() throws DataStoreException;

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append(Classes.getShortClassName(this));
        try {
            final Resource node = getRootNode();
            sb.append(' ');
            sb.append(node.toString());
        } catch (DataStoreException ex) {
            Logging.getLogger("org.geotoolkit.storage").log(Level.WARNING, null, ex);
        }

        return sb.toString();
    }
}
