/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2021, Geomatys
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

import java.util.List;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import org.apache.sis.util.ArgumentChecks;
import org.apache.sis.internal.filter.Node;
import org.apache.sis.internal.feature.AttributeConvention;
import org.opengis.feature.Feature;
import org.opengis.filter.Expression;
import org.opengis.filter.ResourceId;


/**
 * Filter features using a set of predefined identifiers and discarding features
 * whose identifier is not in the set.
 *
 * @author  Johann Sorel (Geomatys)
 * @author  Martin Desruisseaux (Geomatys)
 */
final class FilterByIdentifier extends Node implements ResourceId<Object> {
    /**
     * The identifier of features to retain.
     */
    private final String identifier;

    /**
     * Creates a new filter using the given identifier.
     */
    FilterByIdentifier(final String identifier) {
        // Identifier should be non-empty, but some test in downstream project uses empty identifiers.
        ArgumentChecks.ensureNonNull("identifier", identifier);
        this.identifier = identifier;
    }

    /**
     * Returns the identifiers of feature instances to accept.
     */
    @Override
    public String getIdentifier() {
        return identifier;
    }

    /**
     * Returns the parameters of this filter.
     */
    @Override
    public List<Expression<? super Object, ?>> getExpressions() {
        return Collections.singletonList(FilterUtilities.FF.literal(identifier));
    }

    /**
     * Returns the identifiers specified at construction time. This is used for {@link #toString()},
     * {@link #hashCode()} and {@link #equals(Object)} implementations.
     */
    @Override
    protected Collection<?> getChildren() {
        return Collections.singleton(identifier);
    }

    /**
     * Returns {@code true} if the given object is a {@link Feature} instance and its identifier
     * is one of the identifier specified at {@code FilterByIdentifier} construction time.
     */
    @Override
    public boolean test(Object object) {
        final Object id;
        if (object instanceof Feature) {
            id = ((Feature) object).getValueOrFallback(AttributeConvention.IDENTIFIER, null);
        } else if (object instanceof Map) {
            id = ((Map) object).get(AttributeConvention.IDENTIFIER);
        } else {
            return false;
        }
        return (id != null) && identifier.equals(id.toString());
    }
}
