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

import com.vividsolutions.jts.geom.Point;
import java.util.Date;

/**
 * GPX trace file metadatas.
 *
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public class GPXFileMetadata {
    
    private final long id;
    private final String name;
    private final String user;
    private final boolean pub;
    private final boolean pending;
    private final Date time;
    private final double lat;
    private final double lon;

    public GPXFileMetadata(long id, String name, String user, boolean pub, boolean pending, Date time, double lat, double lon) {
        this.id = id;
        this.name = name;
        this.user = user;
        this.pub = pub;
        this.pending = pending;
        this.time = time;
        this.lat = lat;
        this.lon = lon;
    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getUser() {
        return user;
    }

    public Date getTime() {
        return time;
    }

    public boolean isPending() {
        return pending;
    }

    public boolean isPublic() {
        return pub;
    }

    public double getLat() {
        return lat;
    }

    public double getLon() {
        return lon;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final GPXFileMetadata other = (GPXFileMetadata) obj;
        if (this.id != other.id) {
            return false;
        }
        if ((this.name == null) ? (other.name != null) : !this.name.equals(other.name)) {
            return false;
        }
        if ((this.user == null) ? (other.user != null) : !this.user.equals(other.user)) {
            return false;
        }
        if (this.pub != other.pub) {
            return false;
        }
        if (this.pending != other.pending) {
            return false;
        }
        if (this.time != other.time && (this.time == null || !this.time.equals(other.time))) {
            return false;
        }
        return true;
    }
    
    @Override
    public int hashCode() {
        int hash = 3;
        hash = 89 * hash + (int) (this.id ^ (this.id >>> 32));
        hash = 89 * hash + (this.name != null ? this.name.hashCode() : 0);
        hash = 89 * hash + (this.user != null ? this.user.hashCode() : 0);
        hash = 89 * hash + (this.pub ? 1 : 0);
        hash = 89 * hash + (this.pending ? 1 : 0);
        hash = 89 * hash + (this.time != null ? this.time.hashCode() : 0);
        return hash;
    }
    
}
