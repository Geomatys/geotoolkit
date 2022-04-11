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
package org.geotoolkit.filter.coverage;

import java.io.Serializable;
import org.apache.sis.util.ArgumentChecks;
import org.opengis.coverage.GeometryValuePair;
import org.opengis.filter.Expression;
import org.opengis.filter.ValueReference;


/**
 * Represents the selection of a band in a coverage where the band is represented by an XPath.
 *
 * <h2>Limitations</h2>
 * Current implementation supports only the "*" path, i.e. the selection of all bands.
 *
 * @author Martin Desruisseaux (Geomatys)
 */
final class BandReference implements ValueReference<GeometryValuePair,GeometryValuePair>, Serializable {
    /**
     * The XPath of all bands.
     */
    static final String WILDCARD = "*";

    /**
     * The reference for all bands.
     */
    private static final BandReference ALL = new BandReference();

    /**
     * Creates a new reference to a band.
     */
    private BandReference() {
    }

    /**
     * Creates a reference to the bands identified by the given XPath.
     *
     * @param  <V>    the type of the values to be fetched (compile-time value of {@code type}).
     * @param  xpath  the path to the band(s) whose value will be returned by the {@code apply(R)} method.
     * @param  type   the type of the values to be fetched (run-time value of {@code <V>}).
     * @return an expression evaluating the referenced coverage value.
     */
    @SuppressWarnings("unchecked")
    static <V> ValueReference<GeometryValuePair, V> create(final String xpath, final Class<V> type) {
        if (!xpath.equals(WILDCARD)) {
            throw new IllegalArgumentException("Currently only the " + WILDCARD + " XPath is supported.");
        }
        if (!type.isAssignableFrom(GeometryValuePair.class)) {
            // TODO: if the type is a geometry or a record, extract the corresponding component.
            throw new ClassCastException();
        }
        return (ValueReference<GeometryValuePair, V>) ALL;
    }

    /**
     * Returns the XPath of selected bands. This is either a band name or {@code "*"} for all bands.
     */
    @Override
    public String getXPath() {
        return WILDCARD;
    }

    /**
     * Returns the given cell geometry-value pair with only the bands selected by this reference.
     */
    @Override
    public GeometryValuePair apply(final GeometryValuePair input) {
        return input;
    }

    /**
     * Returns an expression returning geometry-value pairs as instance of the given type.
     */
    @Override
    public <N> Expression<GeometryValuePair, N> toValueType(final Class<N> type) {
        ArgumentChecks.ensureNonNull("type", type);
        return create(WILDCARD, type);
    }
}
