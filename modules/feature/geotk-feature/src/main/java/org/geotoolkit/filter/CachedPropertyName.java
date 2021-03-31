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

package org.geotoolkit.filter;

import java.util.Collections;
import java.util.List;
import org.opengis.filter.ValueReference;

import static org.apache.sis.util.ArgumentChecks.*;
import org.geotoolkit.filter.binding.Binding;
import org.geotoolkit.filter.binding.Bindings;
import org.opengis.feature.FeatureType;
import org.opengis.util.ScopedName;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
class CachedPropertyName extends AbstractExpression implements ValueReference<Object,Object> {

    private final String property;

    private final Binding accessor;

    CachedPropertyName(final String property, final Class clazz, final FeatureType expectedType) {
        ensureNonNull("property name", property);
        this.property = property;

        final Binding fallacc = Bindings.getBinding(clazz,property);
        this.accessor = fallacc;
    }

    @Override
    public ScopedName getFunctionName() {
        return createName("PropertyName");
    }

    @Override
    public List getParameters() {
        return Collections.EMPTY_LIST;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public String getXPath() {
        return property;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public Object apply(final Object candidate) {
        return accessor.get(candidate, property, null);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public String toString() {
        return property;
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
        final CachedPropertyName other = (CachedPropertyName) obj;
        if ((this.property == null) ? (other.property != null) : !this.property.equals(other.property)) {
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
        hash = 73 * hash + (this.property != null ? this.property.hashCode() : 0);
        return hash;
    }
}
