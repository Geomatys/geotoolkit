/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2009, Open Source Geospatial Foundation (OSGeo)
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

package org.geotoolkit.filter.capability;

import org.opengis.filter.capability.FilterCapabilities;
import org.opengis.filter.capability.IdCapabilities;
import org.opengis.filter.capability.ScalarCapabilities;
import org.opengis.filter.capability.SpatialCapabilities;

/**
 * Immutable filter capabilities.
 *
 * @author Johann Sorel (Geomatys)
 */
public class DefaultFilterCapabilities implements FilterCapabilities {

    private final String version;
    private final IdCapabilities id;
    private final SpatialCapabilities spatial;
    private final ScalarCapabilities scalar;

    public DefaultFilterCapabilities(String version, IdCapabilities id, SpatialCapabilities spatial, ScalarCapabilities scalar) {
        this.version = version;
        this.id = id;
        this.spatial = spatial;
        this.scalar = scalar;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public ScalarCapabilities getScalarCapabilities() {
        return scalar;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public SpatialCapabilities getSpatialCapabilities() {
        return spatial;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public IdCapabilities getIdCapabilities() {
        return id;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public String getVersion() {
        return version;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final DefaultFilterCapabilities other = (DefaultFilterCapabilities) obj;
        if ((this.version == null) ? (other.version != null) : !this.version.equals(other.version)) {
            return false;
        }
        if (this.id != other.id && (this.id == null || !this.id.equals(other.id))) {
            return false;
        }
        if (this.spatial != other.spatial && (this.spatial == null || !this.spatial.equals(other.spatial))) {
            return false;
        }
        if (this.scalar != other.scalar && (this.scalar == null || !this.scalar.equals(other.scalar))) {
            return false;
        }
        return true;
    }
    
    /**
     * {@inheritDoc }
     */
    @Override
    public int hashCode() {
        int hash = 7;
        hash = 71 * hash + (this.version != null ? this.version.hashCode() : 0);
        hash = 71 * hash + (this.id != null ? this.id.hashCode() : 0);
        hash = 71 * hash + (this.spatial != null ? this.spatial.hashCode() : 0);
        hash = 71 * hash + (this.scalar != null ? this.scalar.hashCode() : 0);
        return hash;
    }

}
