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

package org.geotoolkit.data.osm.model;

import java.io.Serializable;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public abstract class IdentifiedElement implements Serializable {

    private final long id;
    private final int version;
    private final int changeset;
    private final User user;
    private final long timestamp;
    private final Map<String,String> tags;

    public IdentifiedElement(long id, int version, int changeset, User user,
            long timestamp, Map<String,String> tags) {
        this.id = id;
        this.version = version;
        this.changeset = changeset;
        this.user = user;
        this.timestamp = timestamp;

        if(tags == null || tags.isEmpty()){
            this.tags = Collections.EMPTY_MAP;
        }else{
            if(tags instanceof HashMap){
                this.tags = (Map<String, String>) ((HashMap)tags).clone();
            }else{
                this.tags = new HashMap(tags);
            }
        }
    }

    public long getId() {
        return id;
    }

    public int getVersion() {
        return version;
    }

    public int getChangeset() {
        return changeset;
    }

    public User getUser() {
        return user;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public Map<String, String> getTags() {
        return tags;
    }

}
