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
package org.geotoolkit.build.project.report;

import java.util.Set;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.io.File;
import java.io.IOException;

import org.opengis.metadata.citation.Citation;
import org.opengis.parameter.ParameterDescriptorGroup;
import org.opengis.referencing.IdentifiedObject;
import org.opengis.referencing.operation.Projection;
import org.opengis.referencing.operation.Conversion;
import org.opengis.referencing.operation.Transformation;
import org.opengis.referencing.operation.SingleOperation;
import org.opengis.test.report.OperationParametersReport;

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
public final class ProjectionParameters extends OperationParametersReport {
    /**
     * All authority names as {@link String} instances. Those names will be used as
     * column headers in the table of coordinate operation methods. Those headers will
     * typically be "EPSG", "OGC", "ESRI", "NetCDF", "GeoTIFF" and "PROJ4".
     */
    private final Set<String> columnHeaders;

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
        final Set<String> columns = new LinkedHashSet<String>(XCollections.hashMapCapacity(authorities.length));
        for (final Citation authority : authorities) {
            columns.add(getIdentifier(authority));
        }
        columnHeaders = Collections.unmodifiableSet(columns);
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
     * Creates a new row for the given operation and parameters. The given code spaces will
     * be ignored; we will use our own code spaces derived from the citations given at
     * construction time instead.
     */
    @Override
    protected Row createRow(final IdentifiedObject operation, final ParameterDescriptorGroup parameters, final Set<String> codeSpaces) {
        final Row row = super.createRow(operation, parameters, columnHeaders);
        /*
         * Find a user category for the given object. If a category is found, it will be formatted
         * as a single row in the HTML table before all subsequent objects of the same category.
         * Note that in order to get good results, the Row.compare(...) method needs to be defined
         * in such a way that objects of the same category are grouped together.
         */
        int categoryIndex = categories.length;
        if (operation instanceof MathTransformProvider) {
            final Class<? extends SingleOperation> c = ((MathTransformProvider) operation).getOperationType();
            if (c != null) {
                for (int i=0; i<categoryIndex; i++) {
                    final Class<?> category = categories[i];
                    if (category.isAssignableFrom(c)) {
                        if (category == Projection.class) {
                            row.category = "Map projections";
                        } else {
                            row.category = category.getSimpleName() + 's';
                        }
                        categoryIndex = i;
                        break;
                    }
                }
            }
        }
        return new OrderedRow(row, categoryIndex);
    }

    /**
     * A row implementation sorted by category before to be sorted by name. This implementation
     * is used for sorting the operation methods in the order to be show on the HTML output page.
     * First, the operations are sorted by categories according the order of elements in the
     * {@link #categories} array. For each operation of the same category, methods are
     * sorted by alphabetical order.
     */
    private static final class OrderedRow extends Row {
        /** The category index to use for sorting rows. */
        private final int categoryIndex;

        /** Creates a new row as a copy of the given row.*/
        OrderedRow(final Row toCopy, final int categoryIndex) {
            super(toCopy);
            this.categoryIndex = categoryIndex;
        }

        /** Compares by category, then compares by name. */
        @Override public int compareTo(final Row o) {
            final int c = categoryIndex - ((OrderedRow) o).categoryIndex;
            return (c != 0) ? c : super.compareTo(o);
        }
    }
}
