/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2010-2012, Geomatys
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

import org.apache.sis.storage.event.StoreEvent;
import org.apache.sis.storage.Resource;

/**
 * Storage event.
 *
 * @author Johann Sorel (Geomatys)
 */
public abstract class StorageEvent extends StoreEvent {

    public StorageEvent(final Resource source){
        super(source);
    }

    /**
     * Copy this event changing it's source.
     * @param source new source
     * @return StorageEvent
     */
    public abstract StorageEvent copy(Resource source);
}
