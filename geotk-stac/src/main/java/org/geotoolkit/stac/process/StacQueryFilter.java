/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2026, Geomatys
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
package org.geotoolkit.stac.process;

import java.util.Map;
import java.util.Objects;

/**
 * Client-side evaluation of the STAC API <a href="https://github.com/stac-api-extensions/query">
 * Query Extension</a> shape: a map of item property name to a small map of
 * {@code {operator: value}}, e.g. {@code {"eo:cloud_cover": {"lte": 50}}}. All entries are
 * AND-combined.
 *
 * <p>Used when a STAC endpoint does not itself support the Query Extension (or for a static
 * catalog with no search endpoint at all), by filtering already-fetched {@link org.geotoolkit.stac.dto.Item}
 * properties instead of pushing the filter server-side.
 *
 * @author Quentin BIALOTA (Geomatys)
 */
public final class StacQueryFilter {

    private StacQueryFilter() {
    }

    /**
     * @return true if the item properties satisfy every condition in {@code query}
     * (AND-combined), or if {@code query} is null/empty.
     */
    public static boolean matches(Map<String, Object> itemProperties, Map<String, Map<String, Object>> query) {
        if (query == null || query.isEmpty()) {
            return true;
        }
        for (Map.Entry<String, Map<String, Object>> entry : query.entrySet()) {
            Object propertyValue = itemProperties == null ? null : itemProperties.get(entry.getKey());
            if (!evaluate(propertyValue, entry.getValue())) {
                return false;
            }
        }
        return true;
    }

    /**
     * @return true if {@code propertyValue} satisfies every {@code {operator: value}} entry
     * in {@code operators} (AND-combined), or if {@code operators} is null/empty.
     */
    private static boolean evaluate(Object propertyValue, Map<String, Object> operators) {
        if (operators == null || operators.isEmpty()) {
            return true;
        }
        for (Map.Entry<String, Object> op : operators.entrySet()) {
            if (!evaluateOperator(op.getKey(), propertyValue, op.getValue())) {
                return false;
            }
        }
        return true;
    }

    /**
     * @return the result of applying the Query Extension {@code operator}
     * (one of {@code eq}/{@code neq}/{@code lt}/{@code lte}/{@code gt}/{@code gte})
     * between {@code x} (the item property value) and {@code y} (the filter value).
     * @throws UnsupportedOperationException if {@code operator} is not one of the above
     */
    private static boolean evaluateOperator(String operator, Object x, Object y) {
        if (x == null || y == null) {
            return switch (operator) {
                case "eq" -> Objects.equals(x, y);
                case "neq" -> !Objects.equals(x, y);
                default -> false;
            };
        }
        int cmp = compare(x, y);
        return switch (operator) {
            case "eq" -> cmp == 0;
            case "neq" -> cmp != 0;
            case "lt" -> cmp < 0;
            case "lte" -> cmp <= 0;
            case "gt" -> cmp > 0;
            case "gte" -> cmp >= 0;
            default -> throw new UnsupportedOperationException("Unsupported query operator: " + operator);
        };
    }

    /**
     * @return the comparison result between {@code a} and {@code b}: numeric comparison if
     * both are {@link Number}s, otherwise a string comparison of their {@code toString()}.
     */
    private static int compare(Object a, Object b) {
        if (a instanceof Number an && b instanceof Number bn) {
            return Double.compare(an.doubleValue(), bn.doubleValue());
        }
        return String.valueOf(a).compareTo(String.valueOf(b));
    }
}
