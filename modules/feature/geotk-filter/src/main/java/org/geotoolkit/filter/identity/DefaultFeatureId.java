/*
 *    GeoTools - The Open Source Java GIS Toolkit
 *    http://geotools.org
 * 
 *    (C) 2002-2008, Open Source Geospatial Foundation (OSGeo)
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
package org.geotoolkit.filter.identity;

import org.opengis.feature.Feature;
import org.opengis.filter.identity.FeatureId;

public class DefaultFeatureId implements FeatureId {

    private final String id;

    public DefaultFeatureId(String id) {
        if (id == null) {
            throw new NullPointerException("Feature id can not be null");
        }
        this.id = id;
    }

    @Override
    public String getID() {
        return id;
    }

    @Override
    public boolean matches(Object object) {
        if (object instanceof Feature) {
            return id.equals(((Feature)object).getIdentifier().getID());
        }
        return false;
    }

    @Override
    public String toString() {
        return id;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final DefaultFeatureId other = (DefaultFeatureId) obj;
        if ((this.id == null) ? (other.id != null) : !this.id.equals(other.id)) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 97 * hash + (this.id != null ? this.id.hashCode() : 0);
        return hash;
    }

}
