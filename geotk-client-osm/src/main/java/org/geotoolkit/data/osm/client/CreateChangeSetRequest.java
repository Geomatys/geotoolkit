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

package org.geotoolkit.data.osm.client;

import org.geotoolkit.client.Request;
import org.geotoolkit.data.osm.model.ChangeSet;

/**
 * Request to open a new changeset.
 *
 * @author Johann Sorel (Geomatys)
 * @module
 */
public interface CreateChangeSetRequest extends Request{

    /**
     * Base parameters of the newly created chageset.
     * @param cs : ChangeSet
     */
    void setChangeSet(ChangeSet cs);

    /**
     * Base parameters of the newly created chageset.
     * @return ChangeSet
     */
    ChangeSet getChangeSet();

}
