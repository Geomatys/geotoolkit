/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2002-2008, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2009, Geomatys
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

import java.util.Optional;
import org.geotoolkit.filter.visitor.IsSupportedFilterVisitor;
import org.opengis.filter.Filter;
import org.opengis.filter.capability.Conformance;
import org.opengis.filter.capability.FilterCapabilities;
import org.opengis.filter.capability.IdCapabilities;
import org.opengis.filter.capability.ScalarCapabilities;
import org.opengis.filter.capability.SpatialCapabilities;
import org.opengis.filter.capability.TemporalCapabilities;

/**
 * Immutable filter capabilities.
 *
 * @author Johann Sorel (Geomatys)
 * @module
 */
public class DefaultFilterCapabilities implements FilterCapabilities {

    private final String version;
    private final IdCapabilities id;
    private final SpatialCapabilities spatial;
    private final ScalarCapabilities scalar;
    private final TemporalCapabilities temporal;

    public DefaultFilterCapabilities(final String version, final IdCapabilities id,
            final SpatialCapabilities spatial,
            final ScalarCapabilities scalar,
            final TemporalCapabilities temporal) {
        this.version = version;
        this.id = id;
        this.spatial = spatial;
        this.scalar = scalar;
        this.temporal = temporal;
    }

    @Override
    public Conformance getConformance() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Optional<ScalarCapabilities> getScalarCapabilities() {
        return Optional.ofNullable(scalar);
    }

    @Override
    public Optional<SpatialCapabilities> getSpatialCapabilities() {
        return Optional.ofNullable(spatial);
    }

    @Override
    public Optional<TemporalCapabilities> getTemporalCapabilities() {
        return Optional.ofNullable(temporal);
    }

    @Override
    public Optional<IdCapabilities> getIdCapabilities() {
        return Optional.ofNullable(id);
    }

    public String getVersion() {
        return version;
    }

    public boolean supports(final Filter filter) {
        if (filter == null) {
            return false;
        }
        final IsSupportedFilterVisitor supportedVisitor = new IsSupportedFilterVisitor(this);
        return supportedVisitor.visit(filter);
    }

    @Override
    public boolean equals(final Object obj) {
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
