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
 * Experimental event used to notify the content of the resource has change.
 * For FeatureSet this implies features have been added, deleted or updated.
 * For GridCoverageResource this implies the coverage samples have change.
 *
 * @author Johann Sorel (Geomatys)
 */
public class ContentEvent extends StoreEvent {

    public ContentEvent(Resource resource) {
        super(resource);
    }

}
