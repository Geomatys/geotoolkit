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
import org.opengis.geometry.Envelope;

/**
 * Request to expand a changeset.
 *
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public interface ExpandChangeSetRequest extends Request{

    /**
     * @param id of the requested changeset to download
     */
    void setChangeSetID(int id);

    /**
     * @return id of the requested changeset
     */
    int getChangeSetID();

    /**
     * @param env to expand changeSet.
     */
    void setEnvelope(Envelope env);

    /**
     * @return env to expand changeSet.
     */
    Envelope getEnvelope();

}
