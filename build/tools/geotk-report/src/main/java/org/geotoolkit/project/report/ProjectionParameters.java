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

import java.io.File;
import java.io.Writer;
import java.io.IOException;
import java.util.Comparator;
import java.util.Arrays;

import org.opengis.metadata.citation.Citation;
import org.opengis.parameter.GeneralParameterDescriptor;
import org.opengis.referencing.IdentifiedObject;
import org.opengis.referencing.operation.Projection;
import org.opengis.referencing.operation.Conversion;
import org.opengis.referencing.operation.Transformation;
import org.opengis.referencing.operation.SingleOperation;
import org.opengis.referencing.operation.OperationMethod;
import org.opengis.referencing.operation.MathTransformFactory;

import org.geotoolkit.factory.FactoryFinder;
import org.geotoolkit.referencing.IdentifiedObjects;
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
public final class ProjectionParameters extends ReportGenerator implements Comparator<OperationMethod> {
    /**
     * The authorities for which to get the names or aliases.
     */
    private final Citation[] authorities;

    /**
     * All authority names as {@link String} instances. Those names will be used as
     * column headers in the table of coordinate operation methods. Those headers will
     * typically be "EPSG", "OGC", "ESRI", "NetCDF", "GeoTIFF" and "PROJ4".
     */
    private final String[] authorityLabels;

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
        this.authorities = authorities;
        authorityLabels = new String[authorities.length];
        for (int i=0; i<authorities.length; i++) {
            String name = getIdentifier(authorities[i]);
            if (name.equalsIgnoreCase("PROJ4")) {
                name = "Proj.4"; // This is the actual project name.
            }
            authorityLabels[i] = name;
        }
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
        writer.write(FactoryFinder.getMathTransformFactory(null));
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
    public int compare(final OperationMethod o1, final OperationMethod o2) {
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
        } else {
            if (c1 != c2) return c1 ? -1 : +1; // Sort Geotk implementations before non-Geotk ones.
        }
        return IdentifiedObjects.getName(o1, null).compareTo(IdentifiedObjects.getName(o2, null));
    }

    /**
     * Returns the category label for the given operation method.
     *
     * @param  op The operation method, ignored if {@code c} is non-null.
     * @param  c  The operation type, or {@code null} to infer from {@code op}.
     * @param  anchor {@code true} for returning a HTML anchor instead than the human-readable string.
     * @return A label to write in the HTML report for the category of the operation method.
     */
    private String getCategory(final OperationMethod op, Class<? extends SingleOperation> c, final boolean anchor) {
        if (c == null && op instanceof MathTransformProvider) {
            c = ((MathTransformProvider) op).getOperationType();
        }
        if (c != null) {
            for (final Class<?> category : categories) {
                if (category.isAssignableFrom(c)) {
                    if (anchor) {
                        return category.getSimpleName().toLowerCase(LOCALE) + 's';
                    }
                    if (category == Projection.class) {
                        return "Map projections";
                    }
                    return category.getSimpleName() + 's';
                }
            }
        }
        return anchor ? "others" : "Others";
    }

    /**
     * Invoked from the {@link #main(String[])} method for generating the list of coordinate
     * operation methods for the given factory.
     */
    private void write(final MathTransformFactory factory) throws IOException {
        try (final Writer out = openHTML(new File("operation-parameters.html"), "Coordinate Operation parameters")) {
            out.write("<p>This list is generated from Geotoolkit.org version ");
            out.write(getGeotkVersion());
            out.write(". All those <cite>Operation Methods</cite> and parameter names are supported " +
                      "by the <a href=\"http://www.geotoolkit.org/modules/referencing/index.html\">" +
                      "Geotoolkit.org referencing module</a>.</p>\n");
            out.write("<p>Content:</p>\n<ul>\n");
            for (final Class<? extends SingleOperation> category : categories) {
                out.write("  <li><a href=\"#");
                out.write(getCategory(null, category, true));
                out.write("\">");
                out.write(getCategory(null, category, false));
                out.write("</a></li>\n");
            }
            out.write("</ul>\n");
            openTable(out);
            String previousCategory = null;
            final OperationMethod[] methods = factory.getAvailableMethods(null).toArray(new OperationMethod[0]);
            Arrays.sort(methods, this);
            for (final OperationMethod method : methods) {
                final String category = getCategory(method, null, false);
                final boolean categoryChange = !category.equals(previousCategory);
                if (previousCategory != null) {
                    writeLine(out, categoryChange ? "<hr>" : "&nbsp;", false);
                }
                if (categoryChange) {
                    previousCategory = category;
                    writeLine(out, "<a name=\"" + getCategory(method, null, true) + "\">" + category + "</a>", true);
                    writeTableHeader(out, authorityLabels);
                }
                writeRow(out, method, true);
                for (final GeneralParameterDescriptor param : method.getParameters().descriptors()) {
                    writeRow(out, param, false);
                }
            }
            closeHTML(out);
        }
    }

    /**
     * Writes the given text on a single cell spanning the whole table.
     */
    private void writeLine(final Writer out, final String content, final boolean isHeader) throws IOException {
        out.write("<tr>");
        out.write(isHeader ? "<th bgcolor=\"" + TABLE_HEADER_BACKGROUND + "\" colspan=\"" : "<td colspan=\"");
        out.write(Integer.toString(authorities.length));
        out.write("\">");
        out.write(content);
        out.write(isHeader ? "</th>" : "</td>");
        out.write("</tr>\n");
    }

    /**
     * Writes a single row with the names of the given objects.
     */
    private void writeRow(final Writer out, final IdentifiedObject object, final boolean isHeader) throws IOException {
        out.write("<tr>");
        for (int i=0; i<authorities.length;) {
            final Citation authority = authorities[i];
            int colspan = 1;
            while (++i < authorities.length) {
                if (IdentifiedObjects.getName(object, authorities[i]) != null) {
                    break;
                }
                colspan++;
            }
            out.write("<td nowrap");
            if (colspan != 1) {
                out.write(" colspan=\"");
                out.write(Integer.toString(colspan));
                out.write('"');
            }
            if (isHeader) {
                out.write(" bgcolor=\"" + TABLE_HIGHLIGHT + "\" height=\"32\"");
            }
            out.write('>');
            if (isHeader) {
                out.write("<b>");
            }
            boolean hasMore = false;
            for (final String name : IdentifiedObjects.getNames(object, authority)) {
                if (hasMore) {
                    out.write("<br>");
                }
                if (!isHeader) {
                    out.write("&nbsp;&bull;&nbsp;");
                }
                out.write(name);
                hasMore = true;
            }
            if (isHeader) {
                out.write("</b>");
            }
            out.write("</td>");
        }
        out.write("</tr>\n");
    }
}
