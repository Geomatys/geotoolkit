/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2010, Geomatys
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

package org.geotoolkit.data;

import java.util.EventListener;

/**
 * Listener for datastore, session and feature collection.
 *
 * @author Johann Sorel (Geomatys)
 */
public interface StorageListener extends EventListener{

    /**
     * Fired when a feature type has been created, modified or deleted.
     * @param event
     */
    void structureChanged(StorageManagementEvent event);

    /**
     * Fired when some features has been added, modified or deleted.
     * @param event
     */
    void contentChanged(StorageContentEvent event);

}
