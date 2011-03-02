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
package org.geotoolkit.filter.identity;

import java.io.Serializable;
import org.opengis.feature.Attribute;
import org.opengis.filter.identity.FeatureId;
import org.opengis.filter.identity.Identifier;

import static org.geotoolkit.util.ArgumentChecks.*;

/**
 * Immutable feature id.
 *
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public class DefaultFeatureId implements FeatureId,Serializable {

    private final String id;

    public DefaultFeatureId(final String id) {
        ensureNonNull("feature id", id);
        this.id = id;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public String getID() {
        return id;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public boolean matches(final Object object) {
        if (object instanceof Attribute) {
            final Identifier identifier = ((Attribute)object).getIdentifier();
            return identifier != null && id.equals(identifier.getID());
        }
        return false;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public String toString() {
        return id;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public boolean equals(final Object obj) {
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

    /**
     * {@inheritDoc }
     */
    @Override
    public int hashCode() {
        int hash = 7;
        hash = 97 * hash + (this.id != null ? this.id.hashCode() : 0);
        return hash;
    }

}
