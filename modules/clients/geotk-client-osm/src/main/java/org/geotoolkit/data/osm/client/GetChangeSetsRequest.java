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
import org.geotoolkit.util.DateRange;
import org.opengis.geometry.Envelope;

/**
 * Request to get changesets.
 *
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public interface GetChangeSetsRequest extends Request{

    Envelope getEnvelope();

    /**
     * Find changesets within the given bounding box
     */
    void setEnvelope(Envelope env);

    long getUserId();
    
    /**
     * Find changesets by the user with the given user id or display name. 
     * Providing both is an error. 
     */
    void setUserId(long id);
    
    String getUserName();
    
    /**
     * Find changesets by the user with the given user id or display name. 
     * Providing both is an error. 
     */
    void setUserName(String name);
    
    DateRange getTimeRange();
    
    /**
     * start date is mandatory, close date is optional.
     * 
     * Find changesets closed after T1 
     * Find changesets that were closed after T1 and created before T2 
     */
    void setTimeRange(DateRange range);
    
    boolean isOnlyOpenChangeSets();

    /**
     * Only finds changesets that are still open but excludes changesets that are
     * closed or have reached the element limit for a changeset (50.000 at the moment)
     */
    void setOnlyOpenChangeSets(boolean open);

    boolean isOnlyClosedChangeSets();

    /**
     * Only finds changesets that are closed or have reached the element limit
     */
    void setOnlyClosedChangeSets(boolean closed);

}
