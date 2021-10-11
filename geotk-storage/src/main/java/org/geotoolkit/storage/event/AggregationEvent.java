/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2019, Geomatys
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
package org.geotoolkit.storage.event;

import org.apache.sis.storage.Resource;
import org.apache.sis.storage.event.StoreEvent;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public class AggregationEvent extends StoreEvent {

    public static final int TYPE_ADD = 1;
    public static final int TYPE_REMOVE = 2;

    private final int type;
    private final Resource[] changes;

    public AggregationEvent(Resource source, int type, Resource ... changes) {
        super(source);
        this.type = type;
        this.changes = changes;
    }

    public int getType() {
        return type;
    }

    public Resource[] getChanges() {
        return changes;
    }

}
