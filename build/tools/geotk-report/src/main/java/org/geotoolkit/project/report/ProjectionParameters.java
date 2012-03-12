/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2012, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2012, Geomatys
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
package org.geotoolkit.project.report;

import java.util.Map;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.io.File;
import java.io.IOException;

import org.opengis.metadata.citation.Citation;
import org.opengis.referencing.IdentifiedObject;
import org.opengis.referencing.operation.Projection;
import org.opengis.referencing.operation.Conversion;
import org.opengis.referencing.operation.Transformation;
import org.opengis.referencing.operation.SingleOperation;
import org.opengis.test.report.ParameterNamesReport;

import org.geotoolkit.resources.Errors;
import org.geotoolkit.factory.FactoryFinder;
import org.geotoolkit.util.collection.XCollections;
import org.geotoolkit.referencing.operation.MathTransformProvider;

import static org.geotoolkit.metadata.iso.citation.Citations.*;


/**
 * Generates a list of projection parameters.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.20
 *
 * @since 3.20
 */
public final class ProjectionParameters extends ParameterNamesReport {
    /**
     * All authority names as {@link String} instances. Those names will be used as
     * column headers in the table of coordinate operation methods. Those headers will
     * typically be "EPSG", "OGC", "ESRI", "NetCDF", "GeoTIFF" and "PROJ4".
     */
    private final Map<String,String> columnHeaders;

    /**
     * The type of coordinate operation methods, in the order to be shown in the HTML report.
     * We will typically show map projections first, followed by coordinate conversions,
     * followed by coordinate transformations.
     */
    private final Class<? extends SingleOperation>[] categories;

    /**
     * Creates a new instance with the default set of authorities.
     */
    private ProjectionParameters() {
        this(EPSG, OGC, ESRI, NETCDF, GEOTIFF, PROJ4);
    }

    /**
     * Creates a new instance which will use the parameter names and aliases
     * of the given authorities.
     *
     * @param authorities The authorities for which to show parameter names and aliases.
     */
    @SuppressWarnings({"unchecked","rawtypes"})
    private ProjectionParameters(final Citation... authorities) {
        super(null);
        Reports.initialize(properties);
        properties.setProperty("TITLE", "Coordinate Operation parameters");
        properties.setProperty("DESCRIPTION",
                "All those <cite>Operation Methods</cite> and parameter names are supported "  +
                "by the <a href=\"http://www.geotoolkit.org/modules/referencing/index.html\">" +
                "Geotoolkit.org referencing module</a>.");

        final Map<String,String> columns = new LinkedHashMap<>(XCollections.hashMapCapacity(authorities.length));
        for (final Citation authority : authorities) {
            final String code = getIdentifier(authority);
            String name = code;
            if (name.equalsIgnoreCase("PROJ4")) {
                name = "Proj.4"; // This is the actual project name.
            }
            if (columns.put(code, name) != null) {
                throw new IllegalArgumentException(Errors.format(Errors.Keys.VALUE_ALREADY_DEFINED_$1, code));
            }
        }
        columnHeaders = Collections.unmodifiableMap(columns);
        categories = new Class[] {
            Projection.class,
            Conversion.class,
            Transformation.class
        };
    }

    /**
     * Generates the HTML report.
     *
     * @param args Ignored.
     * @throws IOException If an error occurred while writing the HTML file.
     */
    public static void main(final String[] args) throws IOException {
        final ProjectionParameters writer = new ProjectionParameters();
        writer.add(FactoryFinder.getMathTransformFactory(null));
        writer.write(new File("operation-parameters.html"));
    }

    /**
     * Returns the HTML text to use as a column header for each
     * {@linkplain ReferenceIdentifier#getCodeSpace() code spaces} or
     * {@linkplain GenericName#scope() scopes}. For each entry in the returned map, the
     * {@linkplain java.util.Map.Entry#getKey() key} is the code spaces or scope and the
     * {@linkplain java.util.Map.Entry#getValue() value} is the column header. The columns
     * will be shown in iteration order.
     *
     * @return The name of all code spaces or scopes. Some typical values are {@code "EPSG"},
     *         {@code "OGC"}, {@code "ESRI"}, {@code "GeoTIFF"} or {@code "NetCDF"}.
     */
    @Override
    public Map<String,String> getColumnHeaders() {
        return columnHeaders;
    }

    /**
     * Returns a user category for the given object, or {@code null} if none. If non-null,
     * this category will be formatted as a single row in the HTML table before all subsequent
     * objects of the same category.
     *
     * <p>In order to get good results, the {@link #compare(IdentifiedObject, IdentifiedObject)}
     * method needs to be defined in such a way that objects of the same category are grouped
     * together.</p>
     *
     * @param  object The object for which to get the category.
     * @return The category of the given object, or {@code null} if none.
     */
    @Override
    public String getCategory(final IdentifiedObject object) {
        if (object instanceof MathTransformProvider) {
            final Class<? extends SingleOperation> c = ((MathTransformProvider) object).getOperationType();
            if (c != null) {
                for (final Class<?> category : categories) {
                    if (category.isAssignableFrom(c)) {
                        if (category == Projection.class) {
                            return "Map projections";
                        }
                        return category.getSimpleName() + 's';
                    }
                }
            }
        }
        return super.getCategory(object);
    }

    /**
     * Compares the given operation methods for display order. This method is used for
     * sorting the operation methods in the order to be show on the HTML output page.
     * First, methods are sorted by categories according the order of elements in the
     * {@link #categories} array. For each operation of the same category, methods are
     * sorted by alphabetical order.
     *
     * @param o1 The first operation method to compare.
     * @param o2 The second operation method to compare.
     * @return -1 if {@code o1} should appears before {@code o2}, or -1 for the converse.
     */
    @Override
    public int compare(final IdentifiedObject o1, final IdentifiedObject o2) {
        boolean c1 = (o1 instanceof MathTransformProvider);
        boolean c2 = (o2 instanceof MathTransformProvider);
        if (c1 && c2) {
            final Class<? extends SingleOperation> op1 = ((MathTransformProvider) o1).getOperationType();
            final Class<? extends SingleOperation> op2 = ((MathTransformProvider) o2).getOperationType();
            for (final Class<?> category : categories) {
                c1 = category.isAssignableFrom(op1);
                c2 = category.isAssignableFrom(op2);
                if (c1 != c2) return c1 ? -1 : +1;
            }
        } else if (c1 != c2) {
            return c1 ? -1 : +1; // Sort Geotk implementations before non-Geotk ones.
        }
        return super.compare(o1, o2);
    }
}
