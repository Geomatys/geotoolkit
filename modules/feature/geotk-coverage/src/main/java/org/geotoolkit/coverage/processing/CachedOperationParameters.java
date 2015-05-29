/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2001-2012, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2009-2012, Geomatys
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
package org.geotoolkit.coverage.processing;

import java.util.Map;
import java.util.Locale;
import java.util.Arrays;
import java.util.TreeMap;
import java.util.Objects;

import org.opengis.coverage.Coverage;
import org.opengis.coverage.processing.Operation;
import org.opengis.parameter.ParameterValueGroup;

import org.apache.sis.util.Utilities;
import org.geotoolkit.parameter.Parameters;
import org.geotoolkit.coverage.CoverageReferences;


/**
 * An {@link Operation}-{@link ParameterValueGroup} pair, used by
 * {@link DefaultOperation#doOperation} for caching the result of
 * operations.
 *
 * @author Martin Desruisseaux (IRD, Geomatys)
 * @version 3.00
 *
 * @since 2.1
 * @module
 */
final class CachedOperationParameters {
    /**
     * The operation to apply on grid coverages.
     */
    private final Operation operation;

    /**
     * The parameters names in alphabetical order, including source coverages.
     */
    private final String[] names;

    /**
     * The parameters values. {@link Coverage} objects will be retained as weak references.
     */
    private final Object[] values;

    /**
     * The hash code value for this key.
     */
    private final int hashCode;

    /**
     * Constructs a new key for the specified operation and parameters.
     *
     * @param operation  The operation to apply on grid coverages.
     * @param parameters The parameters, including source grid coverages.
     */
    CachedOperationParameters(final Operation operation, final ParameterValueGroup parameters) {
        final Map<String,Object> param = new TreeMap<>();
        Parameters.copy(parameters, param);
        this.operation = operation;
        this.names     = new String[param.size()];
        this.values    = new Object[names.length];
        int hashCode   = operation.hashCode();
        int index      = 0;
        for (final Map.Entry<String,Object> entry : param.entrySet()) {
            final String name = entry.getKey().trim().toLowerCase(Locale.US);
            Object value = entry.getValue();
            if (value != null) {
                hashCode = 31*hashCode + Utilities.deepHashCode(value);
                if (value instanceof Coverage) {
                    value = CoverageReferences.DEFAULT.reference((Coverage) value);
                }
            }
            hashCode = 7*hashCode + name.hashCode();
            names [index  ] = name;
            values[index++] = value;
        }
        this.hashCode = hashCode;
    }

    /**
     * Returns a hash code value for this key.
     */
    @Override
    public int hashCode() {
        return hashCode;
    }

    /**
     * Compares the specified object with this key for equality.
     */
    @Override
    public boolean equals(final Object object) {
        if (object instanceof CachedOperationParameters) {
            final CachedOperationParameters that = (CachedOperationParameters) object;
            if (Objects.equals(operation, that.operation) && Arrays.equals(names, that.names)) {
                /*
                 * Following arrays contain WeakReferences, but this is okay if they
                 * have been created with CoverageReferences because the later ensures
                 * that (refA == refB) implies (coverageA == coverageB).
                 */
                return Arrays.deepEquals(values, that.values);
            }
        }
        return false;
    }
}
